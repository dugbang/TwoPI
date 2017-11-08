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

class LoadExcel {

    private InputStream excelFileToRead;
    private XSSFWorkbook wb;
    // List<Integer> list = new ArrayList<Integer>(m.keySet());

    public void setExcelFile(String file) throws Exception {
        excelFileToRead = new FileInputStream(file);
        wb = new XSSFWorkbook(excelFileToRead);
    }

    public HashMap<Integer, String> readExcelSheet(int index) {
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

/*
        for(int rowIndex = 4; rowIndex < 100; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null)
                break;

            XSSFCell cell = row.getCell(1);
            String value = "";
            switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = (int)cell.getNumericCellValue() + ", " + row.getCell(2).getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue() + ", " + row.getCell(2).getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    value = cell.getBooleanCellValue() + ", " + row.getCell(2).getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_ERROR:
                    value = cell.getErrorCellValue() + ", " + row.getCell(2).getStringCellValue();
                    break;
            }
            System.out.println(value);
        }
*/

        return result;
    }

}
