package com.example.dugbang.twopi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shbae on 2017-10-30.
 */

public class SmartBlock {

    private ArrayList<String> key;
    private HashMap<String, String> map;

    public void load_csv(String csv_name) throws Exception {
        key = new ArrayList<String>();
        map = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv_name),"UTF8"));
        while(true) {
            String line = br.readLine();
            if (line==null) break;

            int pos = line.indexOf(",");
            key.add(line.substring(0, pos));
            map.put(line.substring(0, pos), line.substring(pos+1));
            //System.out.println(line.substring(0, pos) + " > " + line.substring(pos+1));
        }
        br.close();
    }

    public ArrayList<String> getKey() {
        return key;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
