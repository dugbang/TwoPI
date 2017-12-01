package com.example.dugbang.twopi;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by shbae on 2017-12-01.
 */

public class CsvFileTest {
    @Test
    public void readCsvFile() throws Exception {
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
}
