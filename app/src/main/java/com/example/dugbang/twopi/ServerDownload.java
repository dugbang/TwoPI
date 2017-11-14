package com.example.dugbang.twopi;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by shbae on 2017-11-14.
 * 출처; http://forum.falinux.com/zbxe/index.php?document_srl=565194&mid=lecture_tip
 * 주의; 파일명내에 공백문자를 지원하지 않는다.
 */

class ServerDownload {

    public static final int size = 4 * 1024;

    public String download(String urlStr, String destDir) {
        int slashIndex = urlStr.lastIndexOf('/');
        int periodIndex = urlStr.lastIndexOf('.');

        if (periodIndex < 1 || slashIndex < 0 || slashIndex >= urlStr.length() - 1) {
            System.err.println("path or file name NG.");
            return null;
        }

        // 파일 어드레스에서 마지막에 있는 파일이름을 취득
        String fileName = urlStr.substring(slashIndex + 1);
        fileUrlReadAndDownload(urlStr, fileName, destDir);
        return fileName;
    }

    private void fileUrlReadAndDownload(String urlStr, String fileName, String destDir) {
        OutputStream outStream = null;
        URLConnection uCon = null;
        InputStream is = null;

        System.out.println("-------Download Start------");
        URL Url;
        byte[] buf;
        int byteRead;
        int byteWritten = 0;

        try {
            Url = new URL(urlStr);
            outStream = new BufferedOutputStream(new FileOutputStream(destDir + fileName));

            uCon = Url.openConnection();
            is = uCon.getInputStream();
            buf = new byte[size];
            while ((byteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, byteRead);
                byteWritten += byteRead;
            }

            System.out.println("Download Successfully.");
            System.out.println("File name : " + fileName);
            System.out.println("of bytes  : " + byteWritten);
            System.out.println("-------Download End--------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
