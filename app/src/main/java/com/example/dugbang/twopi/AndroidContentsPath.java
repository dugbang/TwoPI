package com.example.dugbang.twopi;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shbae on 2017-11-29.
 */

public class AndroidContentsPath implements ContentsPath {

    private final String appRoot;

    public AndroidContentsPath(Context context, boolean deleteFlag) {
        appRoot = context.getFilesDir().getAbsolutePath();
        File file = new File(appRoot);
        if (file.exists())
            file.mkdirs();

        if (deleteFlag) {
            deleteFiles(file);
        }
    }

    private void deleteFiles(File file) {
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList) {
            childFile.delete();
        }
    }

    @Override
    public String getRoot() {
        return appRoot;
    }

    @Override
    public boolean validFileName(String fileName) {
        List<String> fileList = getFileList();
        return fileList.contains(fileName);
    }

    @Override
    public List<String> getFileList() {
        File path = new File(appRoot);

        String fileList[] = path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
//                return name.startsWith("Contents_");
                return name.endsWith(".xlsx");
            }
        });
        return Arrays.asList(fileList);
    }
}
