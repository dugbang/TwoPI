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

    private ArrayList<Integer> userId;

    private List<HashMap<Integer, String>> list_map;
    private List<ArrayList<Integer>> list_key;
    private List<ContentsData> actionStep;
    private SimpleDateFormat actionTimeFormat;
    private List<ActionLogData> actionLogList;
    private String contentsName;
    private int actionIndex;
    private int matchBaseIndex;


    public StateRule() {

        actionTimeFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
        //Date to = transFormat.parse(from);
        actionLogList = new ArrayList<ActionLogData>();

        loadBaseBlockId();

        // TODO; 시스템에 최초 설치시 등록되어야 하는 부분 > SQLite
        // 테스트를 위해 하드코딩함. > SQLite에 저장 가능
        userId = new ArrayList<Integer>();
        userId.add(USER_ID_MAX_LIMIT);
        userId.add(USER_ID_MAX_LIMIT - 1);
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
                    loadBaseContents();
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
                // TODO; 지금까지의 내용을 저장하여 엑셀파일을 생성 > 나중에 서버 업로드.
                actionLogList.clear();
                actionStep.clear();
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
                // TODO; 내부 DB 에 관련 콘텐츠가 있으면 로딩 없으면
                // 서버에 접속하여 해당 콘텐츠를 다운로드 하여야 함.
                // > 사용하는 경로 지정 필요, 클래스 시작 시점에 정보저장 필요.
                return "DOWNLOAD";
            }
        } else if (isStoryActive()) {
            saveBlcokId(blockId);
            if (isBlockIdBack(blockId)) {
                if (actionIndex > 0)
                    actionIndex--;
            } else {
                actionIndex++;
            }
            ContentsDisplay();
        }

        return "OK";
    }

    private boolean isBlockIdError(int blockId) {
        return blockId == 0;
    }

    private boolean isBlockIdBack(int blockId) {
        return blockId == 3;
    }

    private void saveBlcokId(int blockId) {
        ActionLogData actionInfo = new ActionLogData();
        actionInfo.blockId = blockId;
        actionInfo.actionTime = actionTimeFormat.format(new Date());
        actionLogList.add(actionInfo);

        //System.out.println(actionInfo.actionTime + " > " + list_map.get(matchBaseIndex).get(actionInfo.blockId));
    }

    private void ContentsDisplay() {
        System.out.println(actionIndex + " > " + actionStep.get(actionIndex).desc);
    }

    private void loadBaseContents() {
        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile(ROOT_DIR + STRING_BASE_INDEX[matchBaseIndex]);
        actionStep = readExcel.readContents();
        actionStep.get(0).contentsId = matchBaseIndex;
        actionIndex = 0;
        contentsName = STRING_BASE_INDEX[matchBaseIndex];
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


    private void dbg_OutputBlockId(List<Integer> key,
                                   HashMap<Integer, String> map) {

        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), map.get(key.get(i)));
            System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }

}
