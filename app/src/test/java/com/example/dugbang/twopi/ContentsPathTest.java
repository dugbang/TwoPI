package com.example.dugbang.twopi;

import org.junit.Test;

import java.util.Iterator;

/**
 * Created by shbae on 2017-11-21.
 */
public class ContentsPathTest {
    @Test
    public void contentsFileList() throws Exception {
        ContentsPath contentsPath = new PcContentsPath();

        Iterator<String> keySetIterator = contentsPath.getFileList().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            System.out.println("file: " + key);
        }
    }
}