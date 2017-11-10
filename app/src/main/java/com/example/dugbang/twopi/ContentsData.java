package com.example.dugbang.twopi;

/**
 * Created by shbae on 2017-11-10.
 */

class ContentsData {
    public static int contentsId = 0;
    public String desc;
    public int sceneId;
    public int questId;
    public int blockId;

    public String getBackgroundImage() {
        return String.format("Contents_%03d_%02d_%02d", contentsId, sceneId, questId);
    }
}
