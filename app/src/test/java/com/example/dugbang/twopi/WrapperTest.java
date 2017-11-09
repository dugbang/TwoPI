package com.example.dugbang.twopi;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by shbae on 2017-11-09.
 */

public class WrapperTest {
    @Test
    public void should_wrap() throws Exception {
        assertWraps(null, 1, "");
        assertWraps("", 1, "");
        assertWraps("x", 1, "x");
        assertWraps("xx", 1, "x\nx");
        assertWraps("xxx", 1, "x\nx\nx");
        assertWraps("x x", 1, "x\nx");
        assertWraps("x xx", 3, "x\nxx");
        assertWraps("four score and seven years ago our fathers brought forth upon this continent", 7,
                "four\nscore\nand\nseven\nyears\nago our\nfathers\nbrought\nforth\nupon\nthis\ncontine\nnt");
        assertWraps("a dog with a bone", 6, "a dog\nwith a\nbone");
    }

    private void assertWraps(String s, int width, String expected) {
        assertThat(wrap(s, width), is(expected));
    }

    private String wrap(String s, int width) {
        if(s == null)
            return "";
        if(s.length() <= width)
            return s;
        else {
            int breakPoint = s.lastIndexOf(" ", width);
            if (breakPoint == -1)
                breakPoint = width;

            return s.substring(0, breakPoint) + "\n" + wrap(s.substring(breakPoint).trim(), width);
        }
    }
}
