package com.vint.pdf2ppt.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface Processor {

	public void process(File input, File output) throws FileNotFoundException, IOException;
}
