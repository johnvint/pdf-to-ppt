package com.vint.pdf2ppt.core;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	@SuppressWarnings("unchecked")
	public ConversionProcessor prepare(File input, File output) throws IOException {
		return new DefaultConversionProcessor(input, output);
	}

	private static final class DefaultConversionProcessor implements ConversionProcessor {
		private final File output;
		private PDDocument doc;
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private volatile ConversionCallback callback;

		private DefaultConversionProcessor(File input, File output) throws IOException {
			doc = PDDocument.load(input);
			this.output = output;
		}

		@Override
		public void execute() {
			executor.execute(new Runnable() {
				public void run() {
					try {
						asyncExecute();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}

		private void asyncExecute() throws IOException {
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
				BufferedImage bi = page.convertToImage(BufferedImage.TYPE_INT_RGB, 400);
				BufferedImage image = resize(bi, pgsize.width * 1.15, pgsize.height * 1.10);
				ImageIO.write(image, "jpg", baos);
				baos.flush();
				byte[] imageInByte = baos.toByteArray();

				int idx = ppt.addPicture(imageInByte, XSLFPictureData.PICTURE_TYPE_JPEG);
				XSLFPictureShape pic = slide.createPicture(idx);
				pic.setAnchor(new Rectangle2D.Double(-70, 0, pgsize.width, pgsize.height));

				// this is necessary
				// the BufferedImage leverages a native byte array that may not
				// get
				// automatically gc'd before the next one is created.
				// for now we will brute force it to work
				System.gc();

				if (callback != null) {
					callback.onPageProcessed(++count);
				}
			}

			callback.onPagesProcessed();

			try {
				FileOutputStream fOut = new FileOutputStream(output);
				ppt.write(fOut);
				fOut.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			callback.onCompletion();
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

		@Override
		public int getPageCount() {
			return doc.getNumberOfPages();
		}

		@Override
		public void setConversionCallback(ConversionCallback cb) {
			callback = cb;
		}

	}
}
