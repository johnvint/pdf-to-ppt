package com.vint.pdf2ppt.core;

import java.io.File;

public interface ConversionProcessor {

    public void execute();

    public int getPageCount();

    public void setConversionCallback(ConversionCallback cb);

    public void setOutputFile(File file);
}
