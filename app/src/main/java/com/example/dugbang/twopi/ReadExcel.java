package com.example.dugbang.twopi;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public HashMap<Integer, String> readBlockIdSheet(int sheetIndex) {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);

        int rowIndex = 4;
        while (true) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null)
                break;

            //XSSFCell cell = row.getCell(1);
            result.put((int)row.getCell(1).getNumericCellValue(), row.getCell(2).getStringCellValue());
            rowIndex++;
        }
        return result;
    }

    public List<ContentsData> readContents() {
        List<ContentsData> result = new ArrayList<ContentsData>();
        XSSFSheet sheet = wb.getSheetAt(0);

        int rowIndex = 4;
        while (true) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null)
                break;

            ContentsData data = new ContentsData();
            try {
                data.desc = row.getCell(1).getStringCellValue();
                data.sceneId = (int)row.getCell(3).getNumericCellValue();
                data.questId = (int)row.getCell(4).getNumericCellValue();
                // TODO: action index > 어떤 방식으로 이동할 것인지는 좀더 검토해야함...
                //data.nextIndex = (int)row.getCell(5).getNumericCellValue();
                data.correctId = (int)row.getCell(7).getNumericCellValue();
            } catch (Exception e) {
                //e.printStackTrace();
                break;
            }

            result.add(data);
            rowIndex++;
        }
        return result;
    }
}
