package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by shbae on 2017-11-21.
 */
public class ContentsPathTest {

    private ContentsPath contentsPath;

    @Before
    public void setUp() throws Exception {
        contentsPath = new PcContentsPath();
    }

    @Test
    public void contentsFileList() throws Exception {
        Iterator<String> keySetIterator = contentsPath.getFileList().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            System.out.println("file: " + key);
        }
    }

    @Test
    public void fileListOutput() throws Exception {
        Iterator<String> keySetIterator = contentsPath.getFileList("BlockId").iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            System.out.println("file: " + key);
        }
    }
}