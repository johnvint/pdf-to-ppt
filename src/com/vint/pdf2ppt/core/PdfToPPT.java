package com.vint.pdf2ppt.core;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class PdfToPpt implements Processor {

    @Override
    @SuppressWarnings("unchecked")
    public void process(File input, File output) throws FileNotFoundException, IOException {
        PDDocument doc = PDDocument.load(input);
        List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
        File file = new File("merged.ppt");
        XMLSlideShow ppt = new XMLSlideShow();
        java.awt.Dimension pgsize = ppt.getPageSize();

        for (PDPage page : pages) {
            XSLFSlide slide = ppt.createSlide();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage bi = page.convertToImage(BufferedImage.TYPE_INT_RGB, 400);
            BufferedImage image = resize(bi, pgsize.width * 1.15, pgsize.height * 1.10);
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();

            int idx = ppt.addPicture(imageInByte, XSLFPictureData.PICTURE_TYPE_JPEG);
            XSLFPictureShape pic = slide.createPicture(idx);
            pic.setAnchor(new Rectangle2D.Double(-70, 0, pgsize.width, pgsize.height));
            
            // this is necessary
            // the BufferedImage leverages a native byte array that may not get
            // automatically gc'd before the next one is created.  
            // for now we will brute force it to work
            System.gc();
        }

        FileOutputStream fOut = new FileOutputStream(file);
        ppt.write(fOut);
        fOut.close();
    }

    public BufferedImage resize(BufferedImage img, double newW, double newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage((int) newW, (int) newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, (int) newW, (int) newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

}
