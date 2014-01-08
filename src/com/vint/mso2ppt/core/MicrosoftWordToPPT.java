package com.vint.mso2ppt.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;

public class MicrosoftWordToPPT implements Processor {

	@Override
	public void process(File input, File output) throws FileNotFoundException, IOException {
		HWPFDocument l = new HWPFDocument(new FileInputStream(input));
		int pagesNo = l.getSummaryInformation().getPageCount();
	}

}
