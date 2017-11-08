package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by shbae on 2017-11-02.
 */

public class ActionTest {

    private Action action;

    @Before
    public void setUp() throws Exception {
        action = new Action();
    }

    @Test
    public void TimeOut() throws Exception {
        assertThat(action.insertBlock(0xFFFFFF), is("OK"));
        assertThat(action.getActiveUserId(), is(0xFFFFFF));
        assertThat(action.getState(), is(1));

        action.setTimeOut();
        assertThat(action.getState(), is(0));
    }

    //@Test(expected=Exception.class)
    @Test
    public void insertBlock() throws Exception {
        assertThat(action.insertBlock(0), is(nullValue()));
        assertThat(action.insertBlock(1), is("FAIL"));
        assertThat(action.getState(), is(0));
        assertThat(action.insertBlock(0xFFFFFF), is("OK"));
        assertThat(action.getActiveUserId(), is(0xFFFFFF));
        assertThat(action.getState(), is(1));

        assertThat(action.insertBlock(1), is("OK"));
        assertThat(action.getState(), is(1));
        assertThat(action.insertBlock(7), is("OK"));
        assertThat(action.getState(), is(2));

        assertThat(action.insertBlock(0xFF0000), is("OK"));
        assertThat(action.getActiveUserId(), is(0xFF0000));
        assertThat(action.getState(), is(1));

        assertThat(action.insertBlock(20), is("OK"));
        assertThat(action.getState(), is(2));

        assertThat(action.insertBlock(130), is("MATCH"));
        assertThat(action.insertBlock(100), is("MISS MATCH"));

        assertThat(action.insertBlock(0xFF0000), is("OK"));
        assertThat(action.getState(), is(1));

        assertThat(action.insertBlock(130), is("DOWNLOAD"));

    }


    @Ignore
    @Test
    public void loadBlock() throws Exception {
        action.loadBlockFile("D:\\BaseBlock.csv");
        assertThat(action.getState(), is(1));
        action.loadBlockFile("D:\\Contents_100.csv");
        assertThat(action.getState(), is(2));
    }

}
