package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by shbae on 2017-11-24.
 */

public class WriteExcelTest {

    private WriteExcel writeExcel;

    @Before
    public void setUp() throws Exception {
        writeExcel = new WriteExcel();
    }

    @Test
    public void createFile() throws Exception {
        String name = "a1aaa.xlsx";
        writeExcel.createFile(name);
        writeExcel.close();
    }
}
