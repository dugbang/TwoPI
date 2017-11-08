package com.example.dugbang.twopi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by shbae on 2017-11-08.
 */

public class ExcelTest {

    @Test
    public void openExcel() throws Exception {

        InputStream ExcelFileToRead = new FileInputStream("D:/스마트블록_정의하기_01.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFSheet sheet = wb.getSheetAt(0);

        for(int rowIndex = 5; rowIndex < 100; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null)
                break;

            XSSFCell cell = row.getCell(1);
            String value = "";
            switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = (int)cell.getNumericCellValue() + "";
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue() + "";
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    value = cell.getBooleanCellValue() + "";
                    break;
                case HSSFCell.CELL_TYPE_ERROR:
                    value = cell.getErrorCellValue() + "";
                    break;
            }
            System.out.println(value);
        }
        //System.out.println(rowIndex);
    }
}
