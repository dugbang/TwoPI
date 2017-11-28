package com.example.dugbang.twopi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * Created by shbae on 2017-11-02.
 */

class StateRule {

    public static final int USER_ID_MIN_LIMIT = 0xFF0000;
    public static final int USER_ID_MAX_LIMIT = 0xFFFFFF;

    public static final int STATE_STORY_ACTIVE = 2;
    public static final int STATE_READY = 1;
    public static final int STATE_SLEEP = 0;

    private int state = STATE_SLEEP;
    private int activeUserId = 0;
    private int actionIndex;
    private SimpleDateFormat actionTimeFormat;

    private List<ContentsData> actionStep;
    private List<ActionLogData> actionLogList;

    private final LoadBlockIdList loadBlockIdList;
    private final WriteExcel writeExcel;
    private final Stack actionStack;


    public StateRule() {
        actionTimeFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
        actionLogList = new ArrayList<ActionLogData>();

        loadBlockIdList = new LoadBlockIdList();
        writeExcel = new WriteExcel();

        actionStack = new Stack();
    }

    public String insertBlock(int blockId) {
        if (isBlockIdError(blockId)) return null;
        if (validUserIdBlock(blockId)) return "OK";
        if (isNonActive()) return "FAIL";

        if (isReady()) {
            loadContents(blockId);
        } else if (isContentsActive()) {
            saveBlcokId(blockId);

            if (isBlockIdBack(blockId)) {
                if (!actionStack.isEmpty())
                    // TODO; 직전의 Action Number; 2 일 경우 추가적인 처리 필요. => 논의 후 결정.
                    actionIndex = (int) actionStack.pop();
            } else {
                actionStack.push(actionIndex);
                setNextActionIndex();
            }
            if (actionStep.size() <= actionIndex)
                endOfContents();
            else
                ContentsDisplay();
        }
        return "OK";
    }

    private void setNextActionIndex() {
        String nextPos = actionStep.get(actionIndex).nextPos;
        if (nextPos.length() == 3) {
            if (nextPos.equals("END")) {
                actionIndex = actionStep.size();
            }
        } else if (nextPos.substring(0, 1).equals("F")) {
            actionIndex += Integer.parseInt(nextPos.substring(1));
        } else if (nextPos.substring(0, 1).equals("B")) {
            actionIndex -= Integer.parseInt(nextPos.substring(1));
            if (actionIndex < 0) {
                StateRuleException e = new StateRuleException();
                e.msg = "콘텐츠 파일의 이동범위를 벋어났습니다.";
                throw e;
            }
        } else {
            actionIndex = Integer.parseInt(nextPos) - 1;
        }
    }

    private void loadContents(int blockId) {
        if (loadBlockIdList.LoadContents(blockId)) {
            actionStep = loadBlockIdList.getActionStep();
            actionIndex = 0;
            state = STATE_STORY_ACTIVE;

            ContentsDisplay();
        }
    }

    private boolean validUserIdBlock(int blockId) {
        if (isUserIdBlock(blockId)) {
            if (isContentsActive()) {
                endOfContents();
            }
            activeUserId = blockId;
            state = STATE_READY;
            return true;
        }
        return false;
    }

    private void endOfContents() {
        // TODO; 지금까지의 내용을 저장하여 엑셀파일을 생성 > 나중에 서버 업로드.
        writeExcel.createFile(getFileName());
        writeExcel.logData(actionLogList);
        writeExcel.close();

        System.out.println("endOfContents()");
        actionLogList.clear();
        actionStep.clear();

        while (!actionStack.isEmpty()) {
            actionStack.pop();
        }
        state = STATE_READY;
    }

    private boolean isBlockIdError(int blockId) {
        return blockId == 0;
    }

    private boolean isBlockIdBack(int blockId) {
        return blockId == 3;
    }

    private void saveBlcokId(int blockId) {
        ActionLogData actionInfo = new ActionLogData();
        actionInfo.actionIndex = actionIndex;
        actionInfo.blockId = blockId;
        actionInfo.actionTime = actionTimeFormat.format(new Date());
        actionLogList.add(actionInfo);

        //System.out.println(actionInfo.actionTime + " > " + list_map.get(matchBaseIndex).get(actionInfo.actionNumber));
    }

    private void ContentsDisplay() {
        // TODO; 메인쪽에 이벤트로 발생하여야 함.
        int displayIndex = actionIndex + 1;
        String outStr = ContentsData.fileName + "; " + displayIndex + " > "
                + actionStep.get(actionIndex).desc
                + ", nextPos; " + actionStep.get(actionIndex).nextPos;
        System.out.println(outStr);
    }

    public void setTimeOut() {
        state = STATE_SLEEP;
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
        return state == STATE_SLEEP;
    }

    public int getActiveUserId() {
        return activeUserId;
    }

    public int getState() {
        return state;
    }

    public String getFileName() {
        String timeStr = actionTimeFormat.format(new Date());
        return String.format("0x%06X_%s_%s", activeUserId, timeStr, ContentsData.fileName);
    }
}
