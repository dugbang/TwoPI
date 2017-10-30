package com.example.dugbang.twopi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by shbae on 2017-10-30.
 */
public class SmartBlockTest {
    private SmartBlock smartBlock;
    private ArrayList<String> storyKey;
    private HashMap<String, String> storyMap;

    @Before
    public void setUp() throws Exception {
        smartBlock = new SmartBlock();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void load_csv() throws Exception {
        String fname = "D:\\smartblock.csv";
        smartBlock.load_csv(fname);

        ArrayList<String> ctrlKey = smartBlock.getKey();
        HashMap<String, String> ctrlMap = smartBlock.getMap();

        SmartBlock story = new SmartBlock();
        story.load_csv("D:\\smartblock_100.csv");
        storyKey = story.getKey();
        storyMap = story.getMap();

        //assertThat(calc.sum(1, 2), is(3));
        assertThat(storyKey.size(), is(11));
        assertThat(ctrlKey.size(), is(50));
    }
}