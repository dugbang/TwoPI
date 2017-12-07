package com.example.dugbang.twopi;

import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by shbae on 2017-12-01.
 */

class ContentsFileList {

    public static final String[] BASE_FILE_LIST = {
            "BlockIdBase.csv",
            "BlockIdAlphabat.csv",
            "BlockIdEmotion.csv",
            "BlockIdNumber.csv",
            "BlockIdShape.csv",
            "ContentsAlphabat.csv",
            "ContentsEmotion.csv",
            "ContentsNumber.csv",
            "ContentsShape.csv",
    };

    private final ContentsPath contentsPath;
    private final HashMap<String, ArrayList<Integer>> baseBlockIdMap;
    private final HashMap<Integer, String> baseBlockIdDesc;

    private HashMap<Integer, String> currentBlockIdDesc;

    private ServerBlockId serverBlockId;
    private ServerDownload serverDownload;

    public ContentsFileList(ContentsPath contentsPath) {
        this.contentsPath = contentsPath;
        serverDownload = new ServerDownload();
        serverBlockId = new MockServerBlockId();

        baseBlockIdMap = new HashMap<String, ArrayList<Integer>>();
        baseBlockIdDesc = new HashMap<Integer, String>();
        currentBlockIdDesc = new HashMap<Integer, String>();

        baseContentsDownload();
        loadBaseBlockIds();
    }

    public HashMap<Integer, String> getBlockIdDesc() {
        return baseBlockIdDesc;
    }

    public List<ContentsData> LoadContents(int blockId) {
        String fileName = matchBlockId(blockId);
        System.out.println("fileName: " + fileName);

        if (fileName != null)
            return loadContentsFile(fileName);

        fileName = serverBlockId.getFileName(blockId);
        if (fileName == null)
            return null;

        contentsDownload(fileName);
        return loadContentsFile(fileName);
    }

    private void baseContentsDownload() {
        for (int i = 0; i < BASE_FILE_LIST.length; i++) {
            String fileName = BASE_FILE_LIST[i];
            if (!contentsPath.validFileName(fileName)) {
                System.out.println("Download fileName: " + fileName);
                serverDownload.download(serverBlockId.getServerDownloadUrl() + fileName, contentsPath.getRoot());
            }
        }
    }

    private void loadBaseBlockIds() {
        Iterator<String> keySetIterator = contentsPath.getFileList("BlockId").iterator();
        while (keySetIterator.hasNext()) {
            String BlockIdFile = keySetIterator.next();
            if (BlockIdFile.equals("BlockIdBase.csv"))
                continue;
            readBlockIdCsvFile(BlockIdFile);
        }
    }

    private void readBlockIdCsvFile(String fileName) {
        try {
            CSVReader reader = new CSVReader(new FileReader(contentsPath.getRoot() + fileName));
            String[] s;
            currentBlockIdDesc.clear();
            ArrayList<Integer> blockIds = new ArrayList<Integer>();
            while ((s = reader.readNext()) != null) {
                blockIds.add(Integer.parseInt(s[0]));
                currentBlockIdDesc.put(Integer.parseInt(s[0]), s[1]);
            }
            baseBlockIdMap.put(getContentsCsvFile(fileName), blockIds);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baseBlockIdDesc.putAll(currentBlockIdDesc);
    }

    private String getContentsCsvFile(String fileName) {
        return fileName.replace("BlockId", "Contents");
    }

    private String matchBlockId(int blockId) {
        Iterator<String> keySetIterator = getBlockIdSortIterator();
        String fileName = null;
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            if (baseBlockIdMap.get(key).contains(blockId)) {
                fileName = key;
                break;
            }
        }
        return fileName;
    }

    @NonNull
    private Iterator<String> getBlockIdSortIterator() {
        TreeMap<String, ArrayList<Integer>> tm = new TreeMap<String, ArrayList<Integer>>(baseBlockIdMap);
        return tm.keySet().iterator();
    }

    private void contentsDownload(String fileName) {
        if (!contentsPath.validFileName(fileName)) {
            serverDownload.download(serverBlockId.getServerDownloadUrl() + fileName, contentsPath.getRoot());
            serverDownload.download(serverBlockId.getServerDownloadUrl() + getContentsCsvFile(fileName), contentsPath.getRoot());
        }
    }

    private List<ContentsData> loadContentsFile(String fileName) {
        readBlockIdCsvFile(fileName);
        return loadContentsCsvFile(getContentsCsvFile(fileName));
    }

    private List<ContentsData> loadContentsCsvFile(String fileName) {
        ContentsData.fileName = fileName;
        List<ContentsData> result = new ArrayList<ContentsData>();
        try {
            CSVReader reader = new CSVReader(new FileReader(contentsPath.getRoot() + fileName));
            String[] s;
            while ((s = reader.readNext()) != null) {
                ContentsData data = new ContentsData();
                data.desc = s[1];
                data.sceneId = Integer.parseInt(s[3]);
                data.questId = Integer.parseInt(s[4]);
                data.nextPos = s[5];
                data.actionNumber = Integer.parseInt(s[6]);
                result.add(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void dbg_output() {
//        Iterator<Integer> keySetIterator2 = baseBlockIdDesc.keySet().iterator();
//        while (keySetIterator2.hasNext()) {
//            Integer key = keySetIterator2.next();
//            System.out.println("key: " + key + " value: " + baseBlockIdDesc.get(key));
//        }
        Iterator<String> keySetIterator3 = getBlockIdSortIterator();
        while (keySetIterator3.hasNext()) {
            String key = keySetIterator3.next();
            System.out.println("key: " + key + " value: " + baseBlockIdMap.get(key));
        }
//        Iterator<Integer> keySetIterator = currentBlockIdDesc.keySet().iterator();
//        while (keySetIterator.hasNext()) {
//            Integer key = keySetIterator.next();
//            System.out.println("key: " + key + " value: " + currentBlockIdDesc.get(key));
//        }
    }
}
