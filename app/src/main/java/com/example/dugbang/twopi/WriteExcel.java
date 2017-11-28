package com.example.dugbang.twopi;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by shbae on 2017-11-24.
 */

class WriteExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String fileName;

    public void createFile(String fileName) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("저장");
        this.fileName = fileName;
    }

    public void close() {
        try {
            FileOutputStream fileoutputstream = new FileOutputStream(PcContentsPath.PC_ROOT + this.fileName);
            workbook.write(fileoutputstream);
            fileoutputstream.close();
//            System.out.println("엑셀파일생성성공");
        } catch (IOException e) {
            e.printStackTrace();
//            System.out.println("엑셀파일생성실패");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public void logData(List<ActionLogData> actionLogList) {
        ActionLogData log;
        XSSFRow row;
        int rowOffset = 4;
        for (int i = 0; i < actionLogList.size(); i++) {
            log = actionLogList.get(i);
            row = sheet.createRow(rowOffset + i);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(log.actionIndex);
            row.createCell(2).setCellValue(log.blockId);
            row.createCell(3).setCellValue(log.actionTime);
// 아래의 방식은 왜 동작하지 않는지 모르겠음...ㅠ
//            sheet.createRow(rowOffset + i).createCell(0).setCellValue(i + 1);
//            sheet.createRow(rowOffset + i).createCell(1).setCellValue(log.actionIndex);
//            sheet.createRow(rowOffset + i).createCell(2).setCellValue(log.blockId);
//            sheet.createRow(rowOffset + i).createCell(3).setCellValue(log.actionTime);
//            System.out.println(i + "," + log.actionIndex + ", " + log.blockId + ", " + log.actionTime);
        }
    }
}
