package com.example.dugbang.twopi;

import org.junit.Test;

import java.util.List;

/**
 * Created by shbae on 2017-12-01.
 */

public class ContentsFileListTest {
    @Test
    public void getFileList() throws Exception {
        ContentsFileList contentsFileList = new ContentsFileList(new PcContentsPath());
//        contentsFileList.dbg_output();

        List<ContentsData> actionStep = contentsFileList.LoadContents(50);
        System.out.println("actionStep size; " + actionStep.size());

    }
}
