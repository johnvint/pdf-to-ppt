package com.vint.pdf2ppt.core;

public interface ConversionCallback {

	public void onStart();

	public void onPageProcessed(int page);

	public void onPagesProcessed();

	public void onCompletion();
}
