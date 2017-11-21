package com.example.dugbang.twopi;

/**
 * Created by shbae on 2017-11-14.
 */

public class MockServerBlockId implements ServerBlockId {
    /*
    리얼은 서버에 접속하여 실제 정보를 받아오도록 한다....
     */
    @Override
    public String getPathContentsFile(int blockId) {
        return "http://192.168.0.40/2pi/Contents_100.xlsx";
    }

    @Override
    public String getFileName(int blockId) {
        return "Contents_100.xlsx";
    }
}
