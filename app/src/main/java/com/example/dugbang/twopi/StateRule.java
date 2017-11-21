package com.example.dugbang.twopi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private int state = STATE_NONACTIVE;
    private int activeUserId = 0;
    private int actionIndex;
    private SimpleDateFormat actionTimeFormat;

    private List<ContentsData> actionStep;
    private List<ActionLogData> actionLogList;

    private final LoadBlockIdList loadBlockIdList;


    public StateRule() {
        actionTimeFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
        actionLogList = new ArrayList<ActionLogData>();

        loadBlockIdList = new LoadBlockIdList();
    }

    public String insertBlock(int blockId) {
        if (isBlockIdError(blockId)) return null;
        if (validUserIdBlock(blockId)) return "OK";
        if (isNonActive()) return "FAIL";

        if (isReady()) {
            loadContents(blockId);
        } else if (isContentsActive()) {
            // TODO; 콘텐츠 파일형식이 변경되면 같이 수정.
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

    private void loadContents(int blockId) {
        if (loadBlockIdList.LoadContents(blockId)) {
            actionStep = loadBlockIdList.getActionStep();
            actionIndex = 0;
            state = STATE_STORY_ACTIVE;
        }
    }

    private boolean validUserIdBlock(int blockId) {
        if (isUserIdBlock(blockId)) {
            // TODO; 다른 상태에서 대기상태로 초기화 하는 부분 필요.???
            if (isContentsActive()) {
                endOfContents();
            }
            // TODO; 사용자 ID 사용에 따른 기능 추가 필요.???
            activeUserId = blockId;
            state = STATE_READY;

            return true;
        }
        return false;
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

        //System.out.println(actionInfo.actionTime + " > " + list_map.get(matchBaseIndex).get(actionInfo.correctId));
    }

    private void ContentsDisplay() {
        String outStr = actionIndex + " > " + actionStep.get(actionIndex).desc;
                        //+ ", bg; " + actionStep.get(actionIndex).getBackgroundImage();
        System.out.println(outStr);
    }

    public void setTimeOut() {
        state = STATE_NONACTIVE;
    }

    private boolean isUserIdBlock(int blockId) {
        return blockId >= USER_ID_MIN_LIMIT;
    }

    private boolean isContentsActive() {
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

}
