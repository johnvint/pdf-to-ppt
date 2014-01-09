### PDF to PPT
A simple application that will take a PDF and try to convert each page to an imagine and build a PowerPoint slide show in which each page represents another slide

### Build
To build this run

`./gradlew distZip `

or

`./gradlew distTar `

Find the zip/tar in build/distribution directory.  Unzip it and execute either the bat or shell script.

### Current State
Currently it takes a bit longer than I wanted to as attempting to resize the image prior to adding to the PDF reduces quality too much.  It is something that shouldn't be too hard to resolve just a matter of time.  As of now it works well enough.
