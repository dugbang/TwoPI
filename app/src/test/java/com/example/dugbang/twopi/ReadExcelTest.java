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
    }

    private void outPutMap(HashMap<Integer, String> map) {
        readExcel.setExcelFile(PcContentsPath.PC_ROOT + "BaseBlockId.xlsx");

        List<Integer> key = new ArrayList<Integer>(map.keySet());
        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), map.get(key.get(i)));
            System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }

    @Test
    public void listMap() throws Exception {
        readExcel.setExcelFile(PcContentsPath.PC_ROOT + "BaseBlockId.xlsx");

        List<HashMap<Integer, String>> list = new ArrayList<HashMap<Integer, String>>();

        for(int i = 0; i < 5; i++) {
            HashMap<Integer, String> map = readExcel.readSheetIndex(i);
            list.add(map);
        }

        for(int i = 0; i < list.size(); i++)
            outPutMap(list.get(i));

    }

    @Test
    public void openExcel() throws Exception {
        readExcel.setExcelFile(PcContentsPath.PC_ROOT + "BaseBlockId.xlsx");

        HashMap<Integer, String> map = readExcel.readSheetIndex(0);
        HashMap<Integer, String> map2 = readExcel.readSheetIndex(1);
        HashMap<Integer, String> map3 = readExcel.readSheetIndex(2);

        outPutMap(map);
        outPutMap(map2);
        outPutMap(map3);
    }

    @Test
    public void readContents() throws Exception {
        readExcel.setExcelFile(PcContentsPath.PC_ROOT + "ContentsShape.xlsx");
        List<ContentsData> list = readExcel.readContents();

        for(int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).desc + ", " + list.get(i).sceneId + ", " +
                    list.get(i).questId + ", " + list.get(i).actionNumber);
        }

    }

}
