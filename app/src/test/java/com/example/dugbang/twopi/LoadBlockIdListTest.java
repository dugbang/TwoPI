package com.example.dugbang.twopi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by shbae on 2017-11-20.
 */
public class LoadBlockIdListTest {

    private LoadBlockIdList loadBlockIdList;

    @Before
    public void setUp() throws Exception {
        loadBlockIdList = new LoadBlockIdList();
        loadBlockIdList.init(new PcContentsPath());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadExContents() throws Exception {
        System.out.println("LoadContents: " + loadBlockIdList.LoadContents(102));
        System.out.println("fileName: " + ContentsData.fileName);

        TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>(loadBlockIdList.getBlockIdDesc());
        Iterator<Integer> treeMapIter = treeMap.keySet().iterator();
        while (treeMapIter.hasNext()) {
            Integer key = treeMapIter.next();
            System.out.println("key: " + key + " value: " + treeMap.get(key));
        }
    }

    @Test
    public void loadBaseContents() throws Exception {
        loadBlockIdList.loadBaseContents();
    }

    @Test
    public void loadContents() throws Exception {
        System.out.println("len: " + loadBlockIdList.STRING_BASE_INDEX.length);
        System.out.println("LoadContents: " + loadBlockIdList.LoadContents(2));
    }

    @Test
    public void matchBlockId() throws Exception {
        int blockId = 30;
        System.out.println("key: " + loadBlockIdList.matchBlockId(blockId));
    }

    @Test
    public void loadBaseBlockIdEx() throws Exception {
        HashMap<String, ArrayList<Integer>> baseBlockIdMap = loadBlockIdList.getBaseBlockIdMap();

        Iterator<String> keySetIterator = loadBlockIdList.getBaseBlockIdMap().keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            System.out.println("key: " + key + " value: " + baseBlockIdMap.get(key));
        }

        Iterator<Integer> keySetIterator2 = loadBlockIdList.getBaseBlockIdDesc().keySet().iterator();
        while (keySetIterator2.hasNext()) {
            Integer key = keySetIterator2.next();
            System.out.println("key: " + key + " value: " + loadBlockIdList.getBaseBlockIdDesc().get(key));
        }
    }
}