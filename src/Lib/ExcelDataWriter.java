package Lib;

import java.io.*;
import java.sql.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static Lib.SystemLibrary.logMessage;

public class ExcelDataWriter {

    public static void AddDataIntoExcelFile(String fileFullName, String sheetName, int rowNumber, int colNumber, String content) throws IOException {
        FileInputStream fileInputStream= new FileInputStream(new File(fileFullName)); //Read the spreadsheet that needs to be updated
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream); //Access the workbook

        XSSFSheet worksheet = workbook.getSheet(sheetName);//Access the worksheet, so that we can update / modify it.
        if (worksheet==null){
            worksheet=workbook.createSheet(sheetName);
        }

        XSSFRow row=worksheet.getRow(rowNumber);
        if (row==null){
            row=worksheet.createRow(rowNumber);
        }

        XSSFCell cell=row.getCell(colNumber);
        if (cell==null){
            cell=row.createCell(colNumber);
        }

        String currentCellString=cell.getStringCellValue();
        cell.setCellValue(content);  // Get current cell value value and overwrite the value
        //System.out.println("Value '"+content+"' is added into row '"+rowNumber+"' column '"+colNumber+" in file '"+fileFullName+"' sheet '"+sheetName+"'.");

        fileInputStream.close(); //Close the InputStream
        FileOutputStream fileOutputStream =new FileOutputStream(new File(fileFullName));  //Open FileOutputStream to write updates

        workbook.write(fileOutputStream); //write changes
        fileOutputStream.close();  //close the stream
    }

    public static boolean exportPerformanceTestResultIntoExcel(String taskName, int rowNumber,  int colNumber, int expectedTiming, int tolerance) throws InterruptedException, IOException, SQLException, ClassNotFoundException {
        boolean isDone=false;
        int errorCounter=0;
        String fileFullName="C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult_"+SystemLibrary.getCurrentTimeAndDate()+".xlsx";
        String sheetName="PerformanceResult";


        int actualTiming=0;
        int threadTimesCount=0;
        ResultSet resultSet=null;

        SystemLibrary.copyFile("C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", fileFullName);

        String[] taskItem=SystemLibrary.splitString(taskName, ";");

        for (int i=0;i<taskItem.length;i++){
            resultSet= getPerformanceResult(taskItem[i]);
            resultSet.next();
            actualTiming=resultSet.getInt(1);
            threadTimesCount=resultSet.getInt(2);
            long timeDifference=actualTiming-expectedTiming;

            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+1, taskName);
            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+2, String.valueOf(threadTimesCount));
            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+3, String.valueOf(actualTiming));
            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+4, String.valueOf(expectedTiming));
            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+5, String.valueOf(timeDifference));
            AddDataIntoExcelFile(fileFullName, sheetName, rowNumber+i, colNumber+6, SystemLibrary.getCurrentTimeAndDate());


            if (Math.abs(timeDifference)>tolerance){
                SystemLibrary.logWarning("The time difference is beyond the tolerance");
                SystemLibrary.logMessage("The expected Timing is "+ String.valueOf(expectedTiming)+". The actual Timinng is "+String.valueOf(actualTiming));
                errorCounter++;
                AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+7, "Fail");
            }
            else{
                AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+7, "Pass");
            }
        }




        SystemLibrary.logMessage("Performance Report is generated as below \n");
        SystemLibrary.logMessage(fileFullName);
        if (errorCounter==0) isDone=true;
        return isDone;
    }


    public static ResultSet getPerformanceResult(String taskName) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        String serverName="ess-prod.australiaeast.cloudapp.azure.com";
        String databaseName="ESS_TestResult";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;


        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        //String strSQL="select cEmpCode from "+databaseName+".dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        String strSQL="select avg(iTiming), count(iTiming) from perfTestResult where vTaskName='"+taskName+"'";

        logMessage(strSQL);

        try {
            String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();


        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////

        return rs;
    }

    public static void exportPerformanceResultIntoExcel(String taskName, int expectedTiming, int tolerance, int rowNumber, int colNumber, String fileFullName, String sheetName) throws InterruptedException, IOException, SQLException, ClassNotFoundException{

        ResultSet resultSet= ExcelDataWriter.getPerformanceResult(taskName);
        resultSet.next();
        int actualTiming=resultSet.getInt(1);
        int threadTimesCount=resultSet.getInt(2);
        long timeDifference=actualTiming-expectedTiming;

        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+1, taskName);
        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+2, String.valueOf(threadTimesCount));
        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+3, String.valueOf(actualTiming));
        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+4, String.valueOf(expectedTiming));
        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+5, String.valueOf(timeDifference));
        ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+6, SystemLibrary.getCurrentTimeAndDate());


        if (Math.abs(timeDifference)>tolerance){
            SystemLibrary.logWarning("The time difference is beyond the tolerance");
            SystemLibrary.logMessage("The expected Timing is "+ String.valueOf(expectedTiming)+". The actual Timinng is "+String.valueOf(actualTiming));

            ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+7, "Fail");
        }
        else{
            ExcelDataWriter.AddDataIntoExcelFile(fileFullName, sheetName, rowNumber, colNumber+7, "Pass");
        }
    }
}
