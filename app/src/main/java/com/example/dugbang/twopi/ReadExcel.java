package com.example.dugbang.twopi;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by shbae on 2017-11-08.
 */

class ReadExcel {

    private InputStream excelFileToRead;
    private XSSFWorkbook wb;
    // List<Integer> list = new ArrayList<Integer>(m.keySet());

    public void setExcelFile(String file) {
        try {
            excelFileToRead = new FileInputStream(file);
            wb = new XSSFWorkbook(excelFileToRead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, String> readBlockIdSheet(int index) {
        HashMap<Integer, String> result = new HashMap<Integer, String>();

        XSSFSheet sheet = wb.getSheetAt(index);

        int rowIndex = 4;
        while (true) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null)
                break;

            XSSFCell cell = row.getCell(1);
            result.put((int)cell.getNumericCellValue(), row.getCell(2).getStringCellValue());

            rowIndex++;
        }

        return result;
    }

}
