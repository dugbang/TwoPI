package com.example.dugbang.twopi;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by shbae on 2017-11-14.
 */

public class ServerDownloadTest {

    private ServerDownload serverDownload;

    @Before
    public void setUp() throws Exception {
        serverDownload = new ServerDownload();
    }

    @Test
    public void UrlAddressDownload() throws Exception {
        ContentsPath contentsPath = new PcContentsPath();
        String url = "http://192.168.0.40/2pi/Contents_100.xlsx";
        serverDownload.download(url, contentsPath.getRoot());
    }
}
