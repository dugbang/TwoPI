package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by shbae on 2017-11-02.
 */

public class StateRuleTest {

    private StateRule stateRule;

    @Before
    public void setUp() throws Exception {
        stateRule = new StateRule();
    }

    @Test
    public void TimeOut() throws Exception {
        assertAction(0xFFFFFF, "OK");
        assertUserId(0xFFFFFF);
        assertActionState(1);

        stateRule.setTimeOut();
        assertActionState(0);
    }

    @Test
    public void loadContents() throws Exception {
        assertActionState(stateRule.STATE_NONACTIVE);
        assertAction(0xFFFFFF, "OK");
        assertUserId(0xFFFFFF);
        assertActionState(stateRule.STATE_READY);

        loadContentsOfNumber();
        assertActionState(stateRule.STATE_STORY_ACTIVE);

        // play contents ==================
        assertAction(47, "OK");
        assertAction(3, "OK");
        assertAction(3, "OK");
        assertAction(53, "OK");
        assertAction(50, "OK");
        assertAction(50, "OK");
        assertAction(3, "OK");
    }

    private void loadContentsOfNumber() {
        assertAction(50, "OK");
    }

    //@Test(expected=Exception.class)
    @Test
    public void insertBlock() throws Exception {
        assertThat(stateRule.insertBlock(0), is(nullValue()));

        assertAction(1, "FAIL");
        assertActionState(0);
        assertAction(0xFFFFFF, "OK");
        assertUserId(0xFFFFFF);
        assertActionState(1);

        assertAction(1, "OK");
        assertActionState(1);
        assertAction(50, "OK");
        assertActionState(2);

        assertAction(0xFF0000, "OK");
        assertUserId(0xFF0000);
        assertActionState(1);

        assertAction(20, "OK");
        assertActionState(2);

        /*
        assertAction(0xFF0000, "OK");
        assertActionState(1);

        assertAction(130, "DOWNLOAD");
        */

    }

    private void assertUserId(int value) {
        assertThat(stateRule.getActiveUserId(), is(value));
    }

    private void assertActionState(int value) {
        assertThat(stateRule.getState(), is(value));
    }

    private void assertAction(int blockId, String value) {
        assertThat(stateRule.insertBlock(blockId), is(value));
    }



}
