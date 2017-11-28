package com.example.dugbang.twopi;

/**
 * Created by shbae on 2017-11-10.
 */

// TODO; 요구사항을 반영하여야 함.
class ContentsData {
    //public static int contentsId = 0;
    public static String fileName;
    public String desc;
    public int sceneId;
    public int questId;
    public int actionNumber;
    public String nextPos;

    public String getBackgroundImage() {
        return String.format("..._%02d_%02d", sceneId, questId);
    }
}
