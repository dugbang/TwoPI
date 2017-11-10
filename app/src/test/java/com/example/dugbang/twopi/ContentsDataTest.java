package com.example.dugbang.twopi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shbae on 2017-11-10.
 */
public class ContentsDataTest {

    private ContentsData contentsData;

    @Before
    public void setUp() throws Exception {
        contentsData = new ContentsData();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getBackgroundImage() throws Exception {
        contentsData.contentsId = 4;
        contentsData.sceneId = 1;
        contentsData.questId = 2;
        assertEquals("Contents_004_01_02", contentsData.getBackgroundImage());
    }
}