package com.example.dugbang.twopi;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shbae on 2017-11-20.
 */

public class PcContentsPath implements ContentsPath {

    public static final String PC_ROOT = "D:/Documents/휴먼케어/2PI_root/";

    @Override
    public List<String> getFileList() {
        File path = new File(PC_ROOT);

        String fileList[] = path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        });
        return Arrays.asList(fileList);
    }

    @Override
    public String getRoot() {
        return PC_ROOT;
    }

    @Override
    public boolean validFileName(String fileName) {
        List<String> fileList = getFileList();
        return fileList.contains(fileName);
    }

    @Override
    public List<String> getFileList(final String start_filter) {
        File path = new File(PC_ROOT);

        String fileList[] = path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(start_filter);
            }
        });
        return Arrays.asList(fileList);
    }
}
