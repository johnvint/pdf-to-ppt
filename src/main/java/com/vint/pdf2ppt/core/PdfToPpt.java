package com.vint.pdf2ppt.core;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class PdfToPpt implements ProcessorFactory {

    @Override
    public ConversionProcessor prepare(File input, File output) throws IOException {
        return new DefaultConversionProcessorDelegate(input, output);
    }

    private static final class DefaultConversionProcessorDelegate implements ConversionProcessor {
        private volatile File output;
        private final PDDocument doc;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private volatile ConversionCallback callback;

        private DefaultConversionProcessorDelegate(File input, File output) throws IOException {
            doc = PDDocument.load(input);
            this.output = output;
        }

        @Override
        public void execute() {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        doExecute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        @SuppressWarnings("unchecked")
        private void doExecute() throws IOException {
            if (callback != null) {
                callback.onStart();
            }

            List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
            XMLSlideShow ppt = new XMLSlideShow();
            java.awt.Dimension pgsize = ppt.getPageSize();

            int count = 0;
            for (PDPage page : pages) {
                XSLFSlide slide = ppt.createSlide();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage bi = page.convertToImage(BufferedImage.TYPE_INT_ARGB, 600);
                double width = pgsize.width * 1.15;
                double height = pgsize.height * 1.12;

                ImageIO.write(bi, "png", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();

                int idx = ppt.addPicture(imageInByte, XSLFPictureData.PICTURE_TYPE_JPEG);
                XSLFPictureShape pic = slide.createPicture(idx);
                pic.setAnchor(new Rectangle2D.Double(-40, -5, width, height));

                // This is necessary surprisingly.
                // The BufferedImage leverages a native byte array that may not
                // get automatically gc'd before the next one is created.
                // for now we will brute force it to work
                System.gc();

                if (callback != null) {
                    callback.onPageProcessed(++count);
                }
            }

            if (callback != null) {
                callback.onPagesProcessed();
            }

            writeFileAndEnd(ppt);

            if (callback != null) {
                callback.onCompletion();
            }
        }

        private final void writeFileAndEnd(XMLSlideShow ppt) throws IOException {
            FileOutputStream fOut = new FileOutputStream(output);
            try {
                ppt.write(fOut);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                fOut.flush();
                fOut.close();
                doc.close();
            }

            this.executor.shutdown();
        }


        @Override
        public int getPageCount() {
            return doc.getNumberOfPages();
        }

        @Override
        public void setConversionCallback(ConversionCallback cb) {
            callback = cb;
        }

        @Override
        public void setOutputFile(File file) {
            this.output = file;
        }

    }
}
