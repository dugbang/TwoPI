package com.example.dugbang.twopi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LoadBlockIdList {

    public static final String[] STRING_BASE_INDEX = {
            "BaseBlockId.xlsx",
            "ContentsShape.xlsx",
            "ContentsAlphabat.xlsx",
            "ContentsEmotion.xlsx",
            "ContentsNumber.xlsx"
    };

    private ServerBlockId serverBlockId;
    private ServerDownload serverDownload;
    private ContentsPath contentsPath;

    private List<ContentsData> actionStep;
    private final HashMap<Integer, String> baseBlockIdDesc;
    private final HashMap<String, ArrayList<Integer>> baseBlockIdMap;

    private HashMap<Integer, String> blockIdDesc;

    public LoadBlockIdList() {
        serverBlockId = new MockServerBlockId();
        serverDownload = new ServerDownload();

        baseBlockIdMap = new HashMap<String, ArrayList<Integer>>();
        baseBlockIdDesc = new HashMap<Integer, String>();

        blockIdDesc = null;
        actionStep = null;
    }

    public void init(ContentsPath contentsPath) {

        // TODO; 안드로이드 실행시 해당 부분을 다시 초기화 할 필요가 있음.
        this.contentsPath = contentsPath;
        loadBaseContents();
        loadBaseBlockId();
    }

    public void loadBaseContents() {
        String fileName;
        for (int i = 0; i < STRING_BASE_INDEX.length; i++) {
            fileName = STRING_BASE_INDEX[i];
            if (!contentsPath.validFileName(fileName)) {
                fileName = serverDownload.download(serverBlockId.getServerDownloadUrl() + fileName, contentsPath.getRoot());
                System.out.println("fileName: " + fileName);
            }
        }
    }

    private void loadBaseBlockId() {
        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile(contentsPath.getRoot() + "BaseBlockId.xlsx");

        for (int i = 0; i < STRING_BASE_INDEX.length; i++) {
            HashMap<Integer, String> map = readExcel.readSheetIndex(i);
            ArrayList<Integer> blockIds = new ArrayList<Integer>();

            Iterator<Integer> keySetIterator = map.keySet().iterator();
            while (keySetIterator.hasNext()) {
                Integer key = keySetIterator.next();
                blockIds.add(key);
                baseBlockIdDesc.put(key, map.get(key));
                //System.out.println("key: " + key + " value: " + map.get(key));
            }
            baseBlockIdMap.put(STRING_BASE_INDEX[i], blockIds);
        }
    }

    public String matchBlockId(int blockId) {
        Iterator<String> keySetIterator = baseBlockIdMap.keySet().iterator();
        String fileName = "";
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
//            System.out.println("key: " + key + " value: " + baseBlockIdMap.get(key));
            if (baseBlockIdMap.get(key).contains(blockId)) {
                fileName = key;
                break;
            }
        }
        return fileName;
    }

    public boolean LoadContents(int blockId) {

        String fileName = matchBlockId(blockId);
        if (fileName.equals("BaseBlockId.xlsx"))
            return false;

//        System.out.println("fileName: " + fileName);
        if (fileName.equals("")) {
            fileName = serverBlockId.getFileName(blockId);
            if (!contentsPath.validFileName(fileName)) {
                fileName = serverDownload.download(serverBlockId.getPathContentsFile(blockId), contentsPath.getRoot());
            }
            System.out.println("fileName: " + fileName);
        }
        loadContentsFile(fileName);
        return true;
    }

    private void loadContentsFile(String fileName) {
        ReadExcel readExcel = new ReadExcel();
        readExcel.setExcelFile(contentsPath.getRoot() + fileName);

        actionStep = readExcel.readContents();
        blockIdDesc = readExcel.readBlockIdDesc();
        ContentsData.fileName = fileName;
        System.out.println("fileName: " + fileName + ", actionStep size; " + actionStep.size());
    }

    public List<ContentsData> getActionStep() {
        return actionStep;
    }

    public HashMap<Integer, String> getBlockIdDesc() {
        return blockIdDesc;
    }

    public HashMap<Integer, String> getBaseBlockIdDesc() {
        return baseBlockIdDesc;
    }

    public HashMap<String, ArrayList<Integer>> getBaseBlockIdMap() {
        return baseBlockIdMap;
    }
}
