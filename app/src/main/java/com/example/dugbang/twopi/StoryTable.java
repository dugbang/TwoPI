package com.example.dugbang.twopi;

/**
 * Created by dugbang on 2017-10-29.
 */

public class StoryTable {

    private int storyID = 0;
    private int sceneID = 0;
    private int sceneIndex = 0;

    public StoryTable() {
        setStoryID(100);
    }

    public int getStoryID() {
        return storyID;
    }

    public void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    public void setSceneID(int sceneID) {
        this.sceneID = sceneID;
        sceneIndex = storyID << 8 | sceneID;
    }

    public int getSceneID() {
        return sceneID;
    }

    public int getSceneIndex() throws Exception {
        if (sceneID == 0)
            throw new Exception();
        return sceneIndex;
    }


}
