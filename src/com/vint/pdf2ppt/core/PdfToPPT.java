package com.vint.pdf2ppt.core;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class PdfToPPT implements Processor {

	@Override
	public void process(File input, File output) throws FileNotFoundException, IOException {
		PDDocument doc = PDDocument.load(input);
		List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
		Iterator iter = pages.iterator();
		XMLSlideShow ppt = new XMLSlideShow();
		java.awt.Dimension pgsize = ppt.getPageSize();

		while (iter.hasNext()) {
			XSLFSlide slide = ppt.createSlide();
			PDPage page = (PDPage) iter.next();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 400), "jpg", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			System.out.println(imageInByte.length);

			int idx = ppt.addPicture(imageInByte, XSLFPictureData.PICTURE_TYPE_JPEG);
			XSLFPictureShape pic = slide.createPicture(idx);
			pic.setAnchor(new Rectangle2D.Double(0, 0, pgsize.width + 50, pgsize.height));
		}
		FileOutputStream out = new FileOutputStream("C:/Users/Brad/merged.ppt");
		ppt.write(out);
		out.close();
	}
}
