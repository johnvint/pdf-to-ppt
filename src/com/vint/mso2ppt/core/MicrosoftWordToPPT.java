package com.vint.mso2ppt.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.ranges.Range;

public class MicrosoftWordToPPT implements Processor {

	@Override
	public void process(File input, File output) throws FileNotFoundException, IOException {
		XWPFDocument doc = new XWPFDocument(new FileInputStream(input));
	}

}
