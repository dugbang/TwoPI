package com.example.dugbang.twopi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by dugbang on 2017-10-29.
 */

public class LoadCsv {
    private static HashMap<String, String> smartBlock;
    private static ArrayList<String> smartBlock_key;

    //private HashMap<String, String> smartBlock = new HashMap<String, String>();

    public static void main(String[] args) {
        String fname = "D:\\smartblock.csv";
        try {
            load_csv(fname);
        } catch (IOException e) {
            //
        }
    }

    private static void load_csv(String fname) throws IOException {
        smartBlock = new HashMap<String, String>();
        smartBlock_key = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname),"UTF8"));
        while(true) {
            String line = br.readLine();
            if (line==null) break;

            int pos = line.indexOf(",");
            smartBlock_key.add(line.substring(0, pos));
            smartBlock.put(line.substring(0, pos), line.substring(pos+1));
            //System.out.println(line.substring(0, pos) + " > " + line.substring(pos+1));
        }
        br.close();

        System.out.println("output... map size; " + smartBlock.size());
        Iterator<String> keySetIterator = smartBlock.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            System.out.println("key: " + key + " value: " + smartBlock.get(key));
        }

        for(int i=0; i<smartBlock_key.size(); i++) {
            System.out.println("key: " + smartBlock_key.get(i) + " value: " + smartBlock.get(smartBlock_key.get(i)));
        }
    }
}
