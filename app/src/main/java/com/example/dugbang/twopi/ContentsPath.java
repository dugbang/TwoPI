package com.example.dugbang.twopi;

import java.util.List;

/**
 * Created by shbae on 2017-11-20.
 */

public interface ContentsPath {
    public String getRoot();
    public boolean validFileName(String fileName);
    public List<String> getFileList();
}
