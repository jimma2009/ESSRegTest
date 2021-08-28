package Lib;

import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcellDataWriter2 {

    public static void main(String[] args) throws Exception{

        FileInputStream fsIP= new FileInputStream(new File("C:\\Temp\\first.xls")); //Read the spreadsheet that needs to be updated

        HSSFWorkbook wb = new HSSFWorkbook(fsIP); //Access the workbook

        HSSFSheet worksheet = wb.getSheet("TableContent"); //Access the worksheet, so that we can update / modify it.

        HSSFCell cell= worksheet.getRow(1).getCell(1);
        String cellData=cell.getStringCellValue();
        System.out.println("Current Value before change is '"+cellData+"'.");
        cell.setCellValue("Where is the table.");
        System.out.println("New value is "+cell.getStringCellValue());

        fsIP.close(); //Close the InputStream

        FileOutputStream output_file =new FileOutputStream(new File("C:\\Temp\\first.xls"));  //Open FileOutputStream to write updates

        wb.write(output_file); //write changes

        output_file.close();  //close the stream
    }
}