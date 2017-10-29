package com.example.dugbang.twopi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dugbang on 2017-10-28.
 */
public class CalcTest {
    private Calc calc;

    @Before
    public void setUp() throws Exception {
        calc = new Calc();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void sum() throws Exception {
        assertThat(calc.sum(1, 2), is(3));
    }

    @Test
    public void sub() throws Exception {
        assertThat(calc.sub(1, 2), is(-1));
    }

}
