package com.vint.pdf2ppt.core;

public interface ConversionProcessor {

	public void execute();

	public int getPageCount();

	public void setConversionCallback(ConversionCallback cb);
}
