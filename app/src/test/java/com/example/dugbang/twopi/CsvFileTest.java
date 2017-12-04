package com.example.dugbang.twopi;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by shbae on 2017-12-01.
 */

public class CsvFileTest {
    @Test
    public void readContentsFile() throws Exception {
        String filename = PcContentsPath.PC_ROOT + "ContentsNumber.csv";
        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            // UTF-8
            // CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"), ",", '"', 1);
            String[] s;
            while ((s = reader.readNext()) != null) {
                for (int i = 0; i < s.length; i++)
                    System.out.print(s[i].toString() + " ");
//                data.add(s);
                System.out.println("");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readBlockIdFile() throws Exception {
        String filename = PcContentsPath.PC_ROOT + "ContentsNumberBlockId.csv";
        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            // UTF-8
            // CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"), ",", '"', 1);
            String[] s;
            while ((s = reader.readNext()) != null) {
                for (int i = 0; i < s.length; i++)
                    System.out.print(s[i].toString() + " ");
//                data.add(s);
                System.out.println("");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeActionLog() throws Exception {
        String filename = PcContentsPath.PC_ROOT + "log.csv";
        List<String[]> data = new ArrayList<String[]>();

        data.add(new String[]{"1", "ddd", "kadjfl;kdaj"});
        data.add(new String[]{"2", "sss", "kldja;fldkjfldkjfa;"});
        data.add(new String[]{"4", "5555", "이라ㅓㅇㅁ;ㅣㅏㅓㄻ;ㅑㅓ;ㅣㅇㄴ머"});

        try {
            CSVWriter cw = new CSVWriter(new FileWriter(filename), ',', '"');
            Iterator<String[]> it = data.iterator();
            try {
                while (it.hasNext()) {
                    String[] s = (String[]) it.next();
                    cw.writeNext(s);
                }
            } finally {
                cw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
