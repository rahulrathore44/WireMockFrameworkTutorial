package org.learning.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class FileReaderUtils {

    public File readeFile(String fileNameWithExt) {
        var resource = this.getClass().getClassLoader().getResource(fileNameWithExt);
        if(resource == null)
            throw  new IllegalArgumentException("Cannot read file: " + fileNameWithExt + " from resources directory");
        return FileUtils.toFile(resource);
    }
}
