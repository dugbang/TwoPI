package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shbae on 2017-11-08.
 */

public class ReadExcelTest {

    private ReadExcel readExcel;
    // List<Integer> list = new ArrayList<Integer>(m.keySet());

    @Before
    public void setUp() throws Exception {
        readExcel = new ReadExcel();
        readExcel.setExcelFile("D:/BaseBlockId.xlsx");
    }

    private void outPutMap(HashMap<Integer, String> map) {
        List<Integer> key = new ArrayList<Integer>(map.keySet());
        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), map.get(key.get(i)));
            System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }

    @Test
    public void listMap() throws Exception {
        List<HashMap<Integer, String>> list = new ArrayList<HashMap<Integer, String>>();

        for(int i = 0; i < 3; i++) {
            HashMap<Integer, String> map = readExcel.readBlockIdSheet(i);
            list.add(map);
        }

        for(int i = 0; i < list.size(); i++)
            outPutMap(list.get(i));

    }

    @Test
    public void openExcel() throws Exception {
        HashMap<Integer, String> map = readExcel.readBlockIdSheet(0);
        HashMap<Integer, String> map2 = readExcel.readBlockIdSheet(1);
        HashMap<Integer, String> map3 = readExcel.readBlockIdSheet(2);

        outPutMap(map);
        outPutMap(map2);
        outPutMap(map3);
    }

    @Test
    public void readContents() throws Exception {

    }

}