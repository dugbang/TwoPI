package com.example.dugbang.twopi;

/**
 * Created by shbae on 2017-11-14.
 */

public interface ServerBlockId {
    public String getPathContentsFile(int blockId);
    public String getFileName(int blockId);
    public String getServerDownloadUrl();
}
