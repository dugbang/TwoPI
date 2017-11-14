package com.example.dugbang.twopi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shbae on 2017-11-02.
 */

class StateRule {

    public static final int USER_ID_MIN_LIMIT = 0xFF0000;
    public static final int USER_ID_MAX_LIMIT = 0xFFFFFF;

    public static final int STATE_STORY_ACTIVE = 2;
    public static final int STATE_READY = 1;
    public static final int STATE_NONACTIVE = 0;

    public static final int BASE_MAP_INDEX_CONTROL = 0;
    public static final int BASE_MAP_INDEX_SHAPE = 1;
    public static final int BASE_MAP_INDEX_ALPHABAT = 2;
    public static final int BASE_MAP_INDEX_EMOTION = 3;
    public static final int BASE_MAP_INDEX_NUMBER = 4;
    public static final int BASE_MAP_INDEX_SIZE = 5;

    public static final String[] STRING_BASE_INDEX = {null,
            "ContentsShape.xlsx",
            "ContentsAlphabat.xlsx",
            "ContentsEmotion.xlsx",
            "ContentsNumber.xlsx"
    };
    public static final String ROOT_DIR = "D:/Documents/휴먼케어/2PI_root/";


    private int state = STATE_NONACTIVE;
    private int activeUserId = 0;

    private List<HashMap<Integer, String>> list_map;
    private List<ArrayList<Integer>> list_key;

    private List<ContentsData> actionStep;
    private SimpleDateFormat actionTimeFormat;
    private List<ActionLogData> actionLogList;
    private String contentsName;
    private int actionIndex;
    private int matchBaseIndex;

    private ServerBlockId mockServerBlockId;
    private ServerDownload serverDownload;


    public StateRule() {

        actionTimeFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
        actionLogList = new ArrayList<ActionLogData>();

        mockServerBlockId = new MockServerBlockId();
        serverDownload = new ServerDownload();

        loadBaseBlockId();
    }

    public void loadBaseBlockId() {

        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile(ROOT_DIR + "BaseBlockId.xlsx");

        list_map = new ArrayList<HashMap<Integer, String>>();
        list_key = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < BASE_MAP_INDEX_SIZE; i++) {
            HashMap<Integer, String> map = readExcel.readBlockIdSheet(i);
            list_map.add(map);
            list_key.add(new ArrayList<Integer>(map.keySet()));
        }
    }

    private void matchBaseMapIndex(int blockId) {
        for (matchBaseIndex = 0; matchBaseIndex < BASE_MAP_INDEX_SIZE; matchBaseIndex++) {
            if (list_key.get(matchBaseIndex).contains(blockId)) {
                if (matchBaseIndex != BASE_MAP_INDEX_CONTROL) {
                    state = STATE_STORY_ACTIVE;
                    contentsName = STRING_BASE_INDEX[matchBaseIndex];
                    loadBaseContents();
                    ContentsDisplay();
                }
                return;
            }
        }
    }

    public String insertBlock(int blockId) {
        if (isBlockIdError(blockId))
            return null;

        if (isUserIdBlock(blockId)) {
            // TODO; 다른 상태에서 대기상태로 초기화 하는 부분 필요.???
            if (isStoryActive()) {
                endOfContents();
            }
            // TODO; 사용자 ID 사용에 따른 기능 추가 필요.???
            activeUserId = blockId;
            state = STATE_READY;

            return "OK";
        }

        if (isNonActive())
            return "FAIL";
        else if (isReady()) {
            matchBaseMapIndex(blockId);
            if (matchBaseIndex == BASE_MAP_INDEX_SIZE) {
                contentsName = serverDownload.download(
                        mockServerBlockId.getPathContentsFile(blockId), StateRule.ROOT_DIR);
                loadBaseContents();
            }
        } else if (isStoryActive()) {
            saveBlcokId(actionIndex, blockId);

            if (isBlockIdBack(blockId)) {
                if (actionIndex > 0)
                    actionIndex--;
            } else {
                actionIndex++;
            }
            if (actionStep.size() == actionIndex)
                endOfContents();
            else
                ContentsDisplay();
        }

        return "OK";
    }

    private void endOfContents() {
        // TODO; 지금까지의 내용을 저장하여 엑셀파일을 생성 > 나중에 서버 업로드.
        System.out.println("endOfContents()");
        actionLogList.clear();
        actionStep.clear();

        state = STATE_READY;
    }

    private boolean isBlockIdError(int blockId) {
        return blockId == 0;
    }

    private boolean isBlockIdBack(int blockId) {
        return blockId == 3;
    }

    private void saveBlcokId(int actionIndex, int blockId) {
        ActionLogData actionInfo = new ActionLogData();
        actionInfo.actionIndex = actionIndex;
        actionInfo.blockId = blockId;
        actionInfo.actionTime = actionTimeFormat.format(new Date());
        actionLogList.add(actionInfo);

        //System.out.println(actionInfo.actionTime + " > " + list_map.get(matchBaseIndex).get(actionInfo.blockId));
    }

    private void ContentsDisplay() {
        String outStr = actionIndex + " > " + actionStep.get(actionIndex).desc
                        + ", bg; " + actionStep.get(actionIndex).getBackgroundImage();
        System.out.println(outStr);
    }

    private void loadBaseContents() {
        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile(ROOT_DIR + contentsName);
        actionStep = readExcel.readContents();

        actionIndex = 0;
        actionStep.get(actionIndex).contentsId = matchBaseIndex;
    }

    public void setTimeOut() {
        state = STATE_NONACTIVE;
    }

    private boolean isUserIdBlock(int blockId) {
        return blockId >= USER_ID_MIN_LIMIT;
    }

    private boolean isStoryActive() {
        return state == STATE_STORY_ACTIVE;
    }

    private boolean isReady() {
        return state == STATE_READY;
    }

    private boolean isNonActive() {
        return state == STATE_NONACTIVE;
    }

    public int getActiveUserId() {
        return activeUserId;
    }

    public int getState() {
        return state;
    }

    public List<HashMap<Integer, String>> getList_map() {
        return list_map;
    }

    public List<ArrayList<Integer>> getList_key() {
        return list_key;
    }


    private void dbg_OutputBlockId(List<Integer> key,
                                   HashMap<Integer, String> map) {

        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), map.get(key.get(i)));
            System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }

}
