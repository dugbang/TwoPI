package com.example.dugbang.twopi;

import android.content.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import au.com.bytecode.opencsv.CSVWriter;

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

    private final Stack actionStack;

    private ContentsPath contentsPath;
    private final ContentsFileList contentsFileList;
    private String outStr;
    private String outMsg;

    public StateRule(Context context) {
        actionTimeFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
        actionLogList = new ArrayList<ActionLogData>();
        actionStack = new Stack();

        if (context == null)
            contentsPath = new PcContentsPath();
        else
            contentsPath = new AndroidContentsPath(context, false);

        contentsFileList = new ContentsFileList(contentsPath);
    }


    public String insertBlock(int blockId) {
        if (isBlockIdError(blockId)) return null;
        if (validUserIdBlock(blockId)) return "OK";
        if (isNonActive()) return "FAIL";


        if (isReady()) {
            loadContents(blockId);
//            contentsFileList.dbg_output();
//            contentsFileList.dbg_output();
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
                throw new StateRuleException("콘텐츠 파일의 이동범위를 벋어났습니다.");
            }
        } else {
            actionIndex = Integer.parseInt(nextPos) - 1;
        }
    }

    private void loadContents(int blockId) {
        List<ContentsData> result = contentsFileList.LoadContents(blockId);
        if (result != null) {
            actionStep = result;
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
            outStr = "";
            return true;
        }
        return false;
    }

    private void endOfContents() {
        writeActionLog();

        System.out.println("endOfContents()");
        actionLogList.clear();
        actionStep.clear();

        while (!actionStack.isEmpty()) {
            actionStack.pop();
        }
        state = STATE_READY;
    }

    private void writeActionLog() {
        String timeStr = actionTimeFormat.format(new Date());
        String fileName = String.format("0x%06X_%s_%s.log", activeUserId, timeStr, ContentsData.fileName);

        try {
            CSVWriter cw = new CSVWriter(new FileWriter(contentsPath.getRoot() + fileName), ',', '"');
            try {
                ActionLogData log;
                for (int i = 0; i < actionLogList.size(); i++) {
                    log = actionLogList.get(i);
                    cw.writeNext(new String[]{i + 1 + "", log.actionIndex + "", log.blockId + "", log.actionTime + ""});
                }
            } finally {
                cw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getOutStr() {
        return outStr;
    }

    private void ContentsDisplay() {
        // TODO; 메인쪽에 이벤트로 발생되어야 함.
        int displayIndex = actionIndex + 1;
        outStr = ContentsData.fileName + "; " + displayIndex + " > "
                + actionStep.get(actionIndex).desc
                + ", nextPos; " + actionStep.get(actionIndex).nextPos;
        System.out.println(outStr);
        outMsg = "contents play files; " + displayIndex;
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

    public String getOutMsg() {
        return outMsg;
    }

    public String getFileName() {
        String timeStr = actionTimeFormat.format(new Date());
        return String.format("0x%06X_%s_%s", activeUserId, timeStr, ContentsData.fileName);
    }

    public String getRoot() {
        return contentsPath.getRoot();
    }
}
