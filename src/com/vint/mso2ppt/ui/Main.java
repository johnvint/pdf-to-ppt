package com.vint.mso2ppt.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.vint.mso2ppt.core.MicrosoftWordToPPT;

public class Main {

	public static void main(String args[]) throws FileNotFoundException, IOException {
		new MicrosoftWordToPPT().process(new File("C:/Users/Brad/Downloads/Physiology Lectures Pt. I(1).docx"), new File(""));
	}
}
