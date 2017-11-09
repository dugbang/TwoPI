package com.example.dugbang.twopi;

import java.util.ArrayList;
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


    private int state = STATE_NONACTIVE;
    private int activeUserId = 0;

    private ArrayList<Integer> userId;

    private ArrayList<Integer> baseKey;
    private HashMap<Integer, String> baseMap;

    private ArrayList<Integer> storyKey;
    private HashMap<Integer, String> storyMap;


    /*
    private ArrayList<Integer> controlKey;
    private HashMap<Integer, String> controlMap;

    private ArrayList<Integer> shapeKey;
    private HashMap<Integer, String> shapeMap;

    private ArrayList<Integer> alphabetKey;
    private HashMap<Integer, String> alphabetMap;

    private ArrayList<Integer> emotionKey;
    private HashMap<Integer, String> emotionMap;

    private ArrayList<Integer> numberKey;
    private HashMap<Integer, String> numberMap;
    */

    private List<HashMap<Integer, String>> list_map;
    private List<ArrayList<Integer>> list_key;


    public StateRule() {

        userId = new ArrayList<Integer>();


        baseKey = new ArrayList<Integer>();
        baseMap = new HashMap<Integer, String>();

        storyKey = new ArrayList<Integer>();
        storyMap = new HashMap<Integer, String>();


        loadBaseBlockId();

        // TODO; 시스템에 최초 설치시 등록되어야 하는 부분 > SQLite
        // 테스트를 위해 하드코딩함. > SQLite에 저장 가능
        userId.add(USER_ID_MAX_LIMIT);
        userId.add(USER_ID_MAX_LIMIT - 1);
    }

    public void loadBaseBlockId() {

        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile("D:/BaseBlockId.xlsx");

        list_map = new ArrayList<HashMap<Integer, String>>();
        list_key = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < BASE_MAP_INDEX_SIZE; i++) {
            HashMap<Integer, String> map = readExcel.readBlockIdSheet(i);
            list_map.add(map);
            list_key.add(new ArrayList<Integer>(map.keySet()));
        }

/*

        for (int i = 0; i < BASE_MAP_INDEX_SIZE; i++) {
            dbg_OutputBlockId(list_key.get(i), list_map.get(i));
        }

        controlMap = readExcel.readBlockIdSheet(BASE_MAP_INDEX_CONTROL);
        controlKey = new ArrayList<Integer>(controlMap.keySet());
        Collections.sort(controlKey);

        shapeMap = readExcel.readBlockIdSheet(BASE_MAP_INDEX_SHAPE);
        shapeKey = new ArrayList<Integer>(shapeMap.keySet());
        Collections.sort(shapeKey);

        alphabetMap = readExcel.readBlockIdSheet(BASE_MAP_INDEX_ALPHABAT);
        alphabetKey = new ArrayList<Integer>(alphabetMap.keySet());
        Collections.sort(alphabetKey);

        emotionMap = readExcel.readBlockIdSheet(BASE_MAP_INDEX_EMOTION);
        emotionKey = new ArrayList<Integer>(emotionMap.keySet());
        Collections.sort(emotionKey);

        numberMap = readExcel.readBlockIdSheet(BASE_MAP_INDEX_NUMBER);
        numberKey = new ArrayList<Integer>(numberMap.keySet());
        Collections.sort(numberKey);

        dbg_OutputBlockId(controlKey, controlMap);
        dbg_OutputBlockId(shapeKey, shapeMap);
        dbg_OutputBlockId(alphabetKey, alphabetMap);
        dbg_OutputBlockId(emotionKey, emotionMap);
        dbg_OutputBlockId(numberKey, numberMap);

        controlKey = new ArrayList<Integer>();
        controlMap = new HashMap<Integer, String>();
        loadFileToBlockId("D:\\Control.csv", controlKey, controlMap);

        shapeKey = new ArrayList<Integer>();
        shapeMap = new HashMap<Integer, String>();
        loadFileToBlockId("D:\\Shape.csv", shapeKey, shapeMap);

        alphabetKey = new ArrayList<Integer>();
        alphabetMap = new HashMap<Integer, String>();
        loadFileToBlockId("D:\\Alphabet.csv", alphabetKey, alphabetMap);

        emotionKey = new ArrayList<Integer>();
        emotionMap = new HashMap<Integer, String>();
        loadFileToBlockId("D:\\Emotion.csv", emotionKey, emotionMap);

*/
    }

    private int getMatchBaseMapIndex(int blockId) {
        for (int i = 0; i < BASE_MAP_INDEX_SIZE; i++) {
            if (list_key.get(i).contains(blockId))
                return i;
        }
        return BASE_MAP_INDEX_SIZE;
    }

    public String insertBlock(int blockId) {
        if (blockId == 0)
            return null;

        if (isUserIdBlock(blockId)) {
            // TODO; 다른 상태에서 대기상태로 초기화 하는 부분 필요.???
            if (isStoryActive()) {
                ;
            }
            // TODO; 사용자 ID 사용에 따른 기능 추가 필요.???
            activeUserId = blockId;
            state = STATE_READY;

            return "OK";
        }


        if (isNonActive())
            return "FAIL";
        else if (isReady()) {
            int matchIndex = getMatchBaseMapIndex(blockId);

            switch (matchIndex) {
                case BASE_MAP_INDEX_CONTROL:
                    break;
                case BASE_MAP_INDEX_SHAPE:
                    // TODO; 해당 놀이 로딩....
                    state = STATE_STORY_ACTIVE;
                    break;
                case BASE_MAP_INDEX_ALPHABAT:
                    state = STATE_STORY_ACTIVE;
                    break;
                case BASE_MAP_INDEX_EMOTION:
                    state = STATE_STORY_ACTIVE;
                    break;
                case BASE_MAP_INDEX_NUMBER:
                    state = STATE_STORY_ACTIVE;
                    break;
                case BASE_MAP_INDEX_SIZE:
                    // TODO; 내부 DB 에 관련 콘텐츠가 있으면 로딩 없으면
                    // 서버에 접속하여 해당 콘텐츠를 다운로드 하여야 함.
                    return "DOWNLOAD";
            }

            /*
            if (matchIndex == BASE_MAP_INDEX_CONTROL) {
                // NOP
            } else if (matchIndex == BASE_MAP_INDEX_SHAPE) {
                //loadFileToBlockId("D:\\Story_Alphabet.csv", storyKey, storyMap);
                state = STATE_STORY_ACTIVE;
            } else if (matchIndex == BASE_MAP_INDEX_ALPHABAT) {
                //loadFileToBlockId("D:\\Story_Shape.csv", storyKey, storyMap);
                state = STATE_STORY_ACTIVE;
            } else if (matchIndex == BASE_MAP_INDEX_EMOTION) {
                //loadFileToBlockId("D:\\Story_Emotion.csv", storyKey, storyMap);
                state = STATE_STORY_ACTIVE;
            } else if (matchIndex == BASE_MAP_INDEX_NUMBER) {
                //loadFileToBlockId("D:\\Story_Emotion.csv", storyKey, storyMap);
                state = STATE_STORY_ACTIVE;
            } else {
                // TODO; 내부 DB 에 관련 콘텐츠가 있으면 로딩 없으면
                // 서버에 접속하여 해당 콘텐츠를 다운로드 하여야 함.
                return "DOWNLOAD";
            }
            */

        } else if (isStoryActive()) {
            if (storyKey.contains(blockId)) {
                return "MATCH";
            }
            return "MISS MATCH";

            // TODO; 동작 > 모든 액션은 기록되어야 한다.
            // 뒤로 앞으로 취소 등의 동작에 대한 액션 기록(시간포함)

        }

        return "OK";
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

    private void loadFileToBlockId(String fileName,
                                   ArrayList<Integer> key,
                                   HashMap<Integer, String> map) {
        SmartBlock blockInfo = new SmartBlock();

        try {
            blockInfo.load_csv(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> file_key = blockInfo.getKey();
        HashMap<String, String> file_map = blockInfo.getMap();

        for (int i = 0; i < file_key.size(); i++) {
            key.add(Integer.parseInt(file_key.get(i)));
            map.put(key.get(i), file_map.get(file_key.get(i)));
            //System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }

    private void dbg_OutputBlockId(List<Integer> key,
                                   HashMap<Integer, String> map) {

        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), map.get(key.get(i)));
            System.out.println(key.get(i) + "," + map.get(key.get(i)));
        }
    }


/*

    public void loadBlockFile(String fileName) {
        SmartBlock blockInfo = new SmartBlock();

        try {
            blockInfo.load_csv(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> file_key = blockInfo.getKey();
        HashMap<String, String> file_map = blockInfo.getMap();

        if (baseKey.size() == 0) {
            for (int i = 0; i < file_key.size(); i++) {
                baseKey.add(Integer.parseInt(file_key.get(i)));
                baseMap.put(baseKey.get(i), file_map.get(file_key.get(i)));
                //System.out.println(key.get(i) + "," + map.get(key.get(i)));
            }
        } else {
            storyKey.clear();
            storyMap.clear();

            for (int i = 0; i < file_key.size(); i++) {
                storyKey.add(Integer.parseInt(file_key.get(i)));
                storyMap.put(storyKey.get(i), file_map.get(file_key.get(i)));
                //System.out.println(key.get(i) + "," + map.get(key.get(i)));
            }
        }

    }

    //public StateRuleException e = new StateRuleException();

    public void insertBlock(int blockID) throws StateRuleException {
        if (blockID == 0) {
            e.errorCode = 1;
            e.msg = "블록정보 인식 오류";
            throw e;
        } else if (blockID >= 0x800000) {
            e.errorCode = 2;
            e.msg = "사용자 ID 블록 입력";
            throw e;
        }
    }
*/


}