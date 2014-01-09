package com.vint.pdf2ppt.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface ProcessorFactory {

	public ConversionProcessor prepare(File input, File output) throws IOException;
}
