package com.vint.mso2ppt.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDCcitt;
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
		PDPage page = (PDPage) iter.next();
		PDResources resources = page.getResources();
		Map pageImages = resources.getImages();

		XMLSlideShow ppt = new XMLSlideShow();
		XSLFSlide slide = ppt.createSlide();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PDCcitt originalImage = (PDCcitt) pageImages.values().iterator().next();
		ImageIO.write(originalImage.getRGBImage(), "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		System.out.println(imageInByte.length);

		int idx = ppt.addPicture(imageInByte, XSLFPictureData.PICTURE_TYPE_PNG);
		XSLFPictureShape pic = slide.createPicture(idx);

		FileOutputStream out = new FileOutputStream("C:/Users/Brad/merged.ppt");
		ppt.write(out);
		out.close();
	}
}
