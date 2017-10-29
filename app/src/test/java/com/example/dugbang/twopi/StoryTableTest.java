package com.example.dugbang.twopi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dugbang on 2017-10-29.
 */
public class StoryTableTest {
    private StoryTable storyTable;

    @Before
    public void setUp() throws Exception {
        storyTable = new StoryTable();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected=Exception.class)
    public void 함수의_호출_순서() throws Exception {
        storyTable.getSceneIndex();
    }

    @Test
    public void getSceneIndex() throws Exception {
        storyTable.setStoryID(100);
        storyTable.setSceneID(3);
        assertThat(storyTable.getSceneIndex(), is(100<<8 | 3));
    }

    @Test
    public void getSceneID() throws Exception {
        storyTable.setSceneID(1);
        assertThat(storyTable.getSceneID(), is(1));

    }

    @Test
    public void getStoryID() throws Exception {
        storyTable.setStoryID(100);
        assertThat(storyTable.getStoryID(), is(100));
    }
}