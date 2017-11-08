package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shbae on 2017-11-08.
 */

public class LoadExcelTest {

    private LoadExcel loadExcel;
    // List<Integer> list = new ArrayList<Integer>(m.keySet());

    @Before
    public void setUp() throws Exception {
        loadExcel = new LoadExcel();
        loadExcel.setExcelFile("D:/BaseBlockId.xlsx");
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
            HashMap<Integer, String> map = loadExcel.readExcelSheet(i);
            list.add(map);
        }

        for(int i = 0; i < list.size(); i++)
            outPutMap(list.get(i));

    }

    @Test
    public void openExcel() throws Exception {
        HashMap<Integer, String> map = loadExcel.readExcelSheet(0);
        HashMap<Integer, String> map2 = loadExcel.readExcelSheet(1);
        HashMap<Integer, String> map3 = loadExcel.readExcelSheet(2);

        outPutMap(map);
        outPutMap(map2);
        outPutMap(map3);
    }
}
