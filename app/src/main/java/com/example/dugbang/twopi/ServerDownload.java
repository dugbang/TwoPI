package com.example.dugbang.twopi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by shbae on 2017-11-14.
 * 출처; http://forum.falinux.com/zbxe/index.php?document_srl=565194&mid=lecture_tip
 * 주의; 파일명내에 공백문자를 지원하지 않는다. => 안드로이드에서는 동작하지 않느다.
 */

class ServerDownload {

    DownloadThread dThread;

    public String download(String urlStr, String destDir) {
        int slashIndex = urlStr.lastIndexOf('/');
        int periodIndex = urlStr.lastIndexOf('.');

        if (periodIndex < 1 || slashIndex < 0 || slashIndex >= urlStr.length() - 1) {
            System.err.println("path or file name NG.");
            return null;
        }

        // 파일 어드레스에서 마지막에 있는 파일이름을 취득
        String fileName = urlStr.substring(slashIndex + 1);
        dThread = new DownloadThread(urlStr, fileName, destDir);
        dThread.start();
        try {
            dThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    class DownloadThread extends Thread {

        private final String urlStr;
        private final String fileName;
        private final String destDir;

        DownloadThread(String urlStr, String fileName, String destDir) {
            this.urlStr = urlStr;
            this.fileName = fileName;
            this.destDir = destDir;
        }

        @Override
        public void run() {
            int count;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {
                URL url = new URL(urlStr);
                connection = url.openConnection();
                connection.connect();

                input = new BufferedInputStream(url.openStream(), 8192);
                output = new FileOutputStream(new File(destDir, fileName));

                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();

                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Error; " + e.getMessage());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
