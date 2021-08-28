package Lib;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import static Lib.SystemLibrary.*;



/**
 * Created by ess on 6/29/2017.
 */
public class DBManage {


    public static String sqlUserName="AutoTester";
    public static String sqlPassword="ESSTesting99";

    public static String dbBackupRetorePath="C:\\AutoTestDBBackup\\AutoTestDBRestoreBackup\\";

    public static void sqlExecuter(String strSQL, String serverName, String databaseName, String storeFileName, String updateStoreFileName, String validateFile, String exectueType) throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs;
        int returnResult=0;
        String strText = "";

        //String url="jdbc:sqlserver://ESSV1-0\\SQLEXPRESS:1433;user=ESSTest;password=WebTesting99;Database=ESS_Test";
        String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
        Connection conn = DriverManager.getConnection(url);

        //SystemLibrary.logMessage("Connecting " + serverName + " ...");
        Statement sta = conn.createStatement();

        if (exectueType==null) exectueType="1";

        if (exectueType.equals("2")){
            try{
                returnResult=sta.executeUpdate(strSQL);
                SystemLibrary.logMessage("The result message is "+returnResult);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (exectueType.equals("1")){
            ////////////////////
            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                //SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {

                // The column count starts from 1
                for (int j = 1; j <= columnCount; j++) {
                    strText = strText + "\"" + rsmd.getColumnName(j) + "\";";
                    // Do stuff with name
                }
                strText = strText + "\r\n";

                while (rs.next()) {
                    for (int k = 1; k <= columnCount; k++) {
                        strText = strText + "\"" + rs.getString(k) + "\";";
                    }
                    strText = strText + "\r\n";
                }

                String sqlResultFileName = "SQLResult_" + getCurrentTimeAndDate() + ".csv";
                String fileFullPathName = SystemLibrary.workingFilePath + sqlResultFileName;

                if (SystemLibrary.createTextFile(fileFullPathName, strText)) {
                    SystemLibrary.logMessage("SQL resrult is saved as \"" + fileFullPathName + "\".");
                    String urlAddress = SystemLibrary.serverUrlAddress + "TestLog/WorkingFile/" + sqlResultFileName;
                    System.out.println("Click here to access the SQL Result: " + urlAddress);
                }
                //SystemLibrary.logMessage(strSQL);
                //SystemLibrary.logDebug(strText);

                if (storeFileName != null) {
                    if (updateStoreFileName != null) {
                        if (updateStoreFileName.equals("1")) try {
                            SystemLibrary.saveFileToStore(fileFullPathName, storeFileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (validateFile != null) {
                        if (validateFile.equals("1")) {
                            SystemLibrary.compareTextFile(fileFullPathName, SystemLibrary.getStoreFileFullPathName(storeFileName));
                        }
                    }
                }
            }
            ///////
        }

    }


    public static void sqlExecutor_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start executing SQL from row " + startSerialNo + " to " + endSerialNo + " in \"SQLExecutor\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 10, "SQLExecutor");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        sqlExecuter(cellData[i][2], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of executing SQL from \"SQLExecutor\" sheet.");

    }


    /*
    public static void sqlServerDataDriver() throws SQLException, ClassNotFoundException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String url="jdbc:sqlserver://ESSV1-0\\SQLEXPRESS:1433;user=ESSTest;password=WebTesting99;Database=ESS_Test";
        Connection conn= DriverManager.getConnection(url);

        System.out.println("ESS_Test");
        Statement sta=conn.createStatement();
        String sql="Select * from _iptblEmployee";
        ResultSet rs=sta.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(5));
        }

    }
    */

    public static String backupDB(String serverName, String databaseName, String comment) throws SQLException, ClassNotFoundException, InterruptedException {
        int errorCounter=0;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = "";
        Integer returnResult=-999999;

        String backupFileFullName = "C:\\AutoTestDBBackup\\AutoTestDBLogBackup\\"+databaseName + "_" + getCurrentTimeAndDate() + ".bak";
        String strSQL = "BACKUP DATABASE [" + databaseName + "] TO DISK='" + backupFileFullName + "';";

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            returnResult=sta.executeUpdate(strSQL);
            //SystemLibrary.logDebug ("The return result is "+returnResult);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
            errorCounter++;
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////

        if (returnResult==-1){
            SystemLibrary.logMessage(strSQL);
            SystemLibrary.logMessage("The backup Database of '"+databaseName+"' is completed successfully.");
        }
        else{
            SystemLibrary.logError("The backup Database of '"+databaseName+"' is failed.");
            errorCounter++;
        }

        if (errorCounter>0) backupFileFullName=null;
        return backupFileFullName;
    }

    public static String backupDBToRestoreFolder(String serverName, String databaseName, String comment) throws SQLException, ClassNotFoundException, InterruptedException {
        int errorCounter=0;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = "";
        Integer returnResult=-999999;

        String backupFileFullName = "C:\\AutoTestDBBackup\\\\AutoTestDBRestoreBackup\\"+databaseName + "_" + getCurrentTimeAndDate() + ".bak";
        String strSQL = "BACKUP DATABASE [" + databaseName + "] TO DISK='" + backupFileFullName + "';";

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            returnResult=sta.executeUpdate(strSQL);
            //SystemLibrary.logDebug ("The return result is "+returnResult);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
            errorCounter++;
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////

        if (returnResult==-1){
            SystemLibrary.logMessage(strSQL);
            SystemLibrary.logMessage("The backup Database of '"+databaseName+"' is completed successfully.");
        }
        else{
            SystemLibrary.logError("The backup Database of '"+databaseName+"' is failed.");
            errorCounter++;
        }

        if (errorCounter>0) backupFileFullName=null;
        return backupFileFullName;
    }


    public static String backupDBSaveInFile(String serverName, String databaseName, String backUpNumber, String comment, String testSerialNo) throws SQLException, ClassNotFoundException, InterruptedException {
        String dbBackupFullPath = backupDBToRestoreFolder(serverName, databaseName, comment);
        String dbFileName = dbBackupFullPath.substring(dbBackupFullPath.lastIndexOf(File.separator) + 1);
        String logMessage = "DBBackup_" + backUpNumber + ": " + dbFileName+"\r\n";
        String fileName="DBBackup_"+testSerialNo+".txt";
        String fileFullName=dataSourcePath+fileName;
        createTextFile(fileFullName, logMessage+"\r\n");
        copyFile(fileFullName, testKeysPath+"DBBackup_"+testSerialNo+".txt");
        return dbFileName;
    }

    public static String getValueFromDBBackupFile(String backupNumber, String testSerialNo) throws IOException, InterruptedException {
        String value=null;
        List<String> list=getStringListFromFile(dataSourcePath+"DBBackup_"+testSerialNo+".txt");
        String itemName= "DBBackup_" + backupNumber;
        int totalLineNumber=list.size();
        for(int i=0;i<totalLineNumber;i++){
            String currentItem=list.get(i);
            if (currentItem.contains(itemName+": ")){
                value=currentItem.replace(itemName+": ", "");
                break;
            }
        }

        SystemLibrary.logMessage("The value of '"+itemName+"' is '" + value + "'.");
        return value;

    }

    public static boolean restoreDBFromFile(String serverName, String databaseName, String backUpNumber, String comment, String testSerialNo) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        String dbBackupFileName = getValueFromDBBackupFile(backUpNumber, testSerialNo);
        boolean restoreStatus =  restoreDB(serverName, databaseName, dbBackupFileName, null, null, comment);
        return restoreStatus;
    }

    public static boolean backupMultiDB(int startSerialNo, int endSerialNo) throws InterruptedException {
        boolean isBackuped=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Backup DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBManagement\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 8, "DBManagement");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        if (backupDB(cellData[i][2], cellData[i][3], cellData[i][7])==null) errorCounter++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorCounter++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Backup DB from \"DBManagement\" sheet.");
        if (errorCounter==0) isBackuped=true;
        return isBackuped;
    }


    public static boolean restoreDB(String serverName, String databaseName, String backupFileName, String logicalDBFileName, String logicalDBLogFileName, String comment) throws SQLException, ClassNotFoundException, InterruptedException, UnknownHostException {
        boolean isRestored=false;
        int errorCounter=0;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = "";
        Integer returnResult=-999999;

        String backupFileFullName = dbBackupRetorePath+backupFileName;
        File backupFile=new File(backupFileFullName);
        if (!backupFile.exists()){
            String networkBackupFileFullName=backupFileFullName.replace("C:\\", "E:\\");
            if (SystemLibrary.copyFile(networkBackupFileFullName, backupFileFullName)){
                logMessage("Backpup file is copied from drive E: to C: successfully.");
            }else{
                logError("File is not found.");
                errorCounter++;
            }
        }



        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="use master; EXEC dbo.sp_restore_database @DatabaseName = ["+databaseName+"], @BackupFileName= N'"+backupFileFullName+"';";
        //String strSQL="USE master; ALTER DATABASE "+databaseName+" SET SINGLE_USER WITH ROLLBACK IMMEDIATE;RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE, MOVE '"+logicalDBFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL13.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".mdf', MOVE '"+logicalDBLogFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL13.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".ldf'";

        String hostName=getHostName();
        String strSQL;



        if (hostName.equals("MPTestVM29")||(hostName.equals("MPTestVM106"))){
            if ((logicalDBFileName!=null)&&(logicalDBLogFileName!=null)){
                strSQL="USE master; RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE, MOVE '"+logicalDBFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".mdf', MOVE '"+logicalDBLogFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".ldf'";
            }else{
                strSQL="USE master; RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE";
            }

            strSQL="USE master; RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE, MOVE '"+logicalDBFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".mdf', MOVE '"+logicalDBLogFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".ldf'";
        }else{
            if ((logicalDBFileName!=null)&&(logicalDBLogFileName!=null)){
                strSQL="USE master; RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE, MOVE '"+logicalDBFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".mdf', MOVE '"+logicalDBLogFileName+"' TO 'C:\\Program Files\\Microsoft SQL Server\\MSSQL14.MSSQLSERVER\\MSSQL\\DATA\\"+databaseName+".ldf'";
            }else{
                strSQL="USE master; RESTORE DATABASE "+databaseName+" FROM DISK='"+backupFileFullName+"' WITH REPLACE";
            }
        }



        logMessage("The SQL is below");
        System.out.println(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTesting99;Database=AutoLoginDBForRestore";
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=AutoLoginDBForRestore";
            logDebug(url);
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            returnResult=sta.executeUpdate(strSQL);
            SystemLibrary.logDebug ("The return result is "+returnResult);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
            errorCounter++;
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////

        if (returnResult==-1){
            SystemLibrary.logMessage("The restore Database of '"+databaseName+"' is completed successfully.");
        }
        else{
            SystemLibrary.logError("The restore Database of '"+databaseName+"' is failed.");
            errorCounter++;
        }
        if (errorCounter==0) isRestored=true;
        return isRestored;
    }

    public static boolean restoreDB_OLD(String serverName, String databaseName, String backupFileName, String comment) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = "";
        Integer returnResult=-999999;

        String backupFileFullName = dbBackupRetorePath+backupFileName;

        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        String strSQL="use master; EXEC dbo.sp_restore_database @DatabaseName = ["+databaseName+"], @BackupFileName= N'"+backupFileFullName+"';";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            returnResult=sta.executeUpdate(strSQL);
            logMessage("The SQL is: "+strSQL);
            SystemLibrary.logDebug ("The return result is "+returnResult);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////

        if (returnResult==-1){
            SystemLibrary.logMessage(strSQL);
            SystemLibrary.logMessage("The restore Database of '"+databaseName+"' is completed successfully.");
            isBackup=true;
        }
        else{
            SystemLibrary.logError("The restore Database of '"+databaseName+"' is failed.");
        }
        return isBackup;
    }

    public static boolean restoreMultiDB(int startSerialNo, int endSerialNo) throws InterruptedException, UnknownHostException {
        boolean isRestored=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start restoring DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBManagement\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 20, "DBManagement");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        if (!restoreDB(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][7], cellData[i][8], cellData[i][9])) errorCounter++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorCounter++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Restoring DB from \"DBManagement\" sheet.");
        if (errorCounter==0) isRestored=true;
        return isRestored;
    }


    public static void sqlExecutor_WithCustomizedSQL_Main(int startSerialNo, int endSerialNo, String strSQL) throws Exception {
        //SystemLibrary.logMessage("--- Start executing SQL from row " + startSerialNo + " to " + endSerialNo + " in \"SQLExecutor\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 10, "SQLExecutor");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        sqlExecuter(strSQL, cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of executing SQL from \"SQLExecutor\" sheet.");

    }

    public static String getEmployeeCodeFromPayrollDB(String serverName, String payrollDBName, String firstName, String lastName) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = null;

        //String strSQL = "RESTORE DATABASE " + payrollDBName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        String strSQL="select cEmpCode from "+payrollDBName+".dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + payrollDBName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + payrollDBName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {
                rs.next();
                strText=rs.getString(1);
            }
            logMessage("The SQL is: "+strSQL);
            SystemLibrary.logMessage ("The employee code is below:"+strText);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////


        return strText;
    }

    public static boolean deleteMultiDB(int startSerialNo, int endSerialNo) throws InterruptedException {
        boolean isAllDeleted=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Deleting multi DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBManagement\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 20, "DBManagement");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        if (!deleteDB(cellData[i][2], cellData[i][3])) errorCounter++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Deleting DB from \"DBManagement\" sheet.");
        if (errorCounter==0) isAllDeleted=true;
        return isAllDeleted;
    }

    private static boolean deleteDB(String serverName, String databaseName) throws ClassNotFoundException, InterruptedException {
        boolean isDeleted=false;
        int errorCounter=0;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = "";
        Integer returnResult=-999999;

        String strSQL="ALTER DATABASE "+databaseName+" SET SINGLE_USER WITH ROLLBACK IMMEDIATE;DROP DATABASE "+databaseName+";";
        logMessage("The SQL is below");
        System.out.println(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=AutoLoginDBForRestore";
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=AutoLoginDBForRestore";
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            returnResult=sta.executeUpdate(strSQL);
            SystemLibrary.logDebug ("The return result is "+returnResult);
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
            errorCounter++;
        }

        if (returnResult==0){
            SystemLibrary.logMessage("The database '"+databaseName+"' is deleted successfully.");
        }
        else{
            SystemLibrary.logError("Failed deleting the database '"+databaseName+"'.");
            errorCounter++;
        }

        if (errorCounter==0) isDeleted=true;
        return isDeleted;
    }

    public static void exportPerformanceResultIntoDB(String taskName, int threadID, int timing, String comment) throws Exception {
        String strSQL="insert into perfTestResult (vTaskName, iThreadID, iTiming, vComment) values ('"+taskName+"', "+threadID+", "+timing+", '"+comment+"');";
        sqlExecutor_WithCustomizedSQL_Main(1001, 1001, strSQL);
    }


    public static int getPerformanceTiming(String taskName, String serverName, String databaseName) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        int timingOut = 0;

        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        //String strSQL="select cEmpCode from "+databaseName+".dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        String strSQL="select avg(iTiming) from perfTestResult where vTaskName='"+taskName+"'";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {
                rs.next();
                timingOut=rs.getInt(1);
            }
            logMessage("The SQL is: "+strSQL);
            SystemLibrary.logMessage ("The Average timing of task '"+taskName+"' is "+String.valueOf(timingOut));
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////


        return timingOut;
    }

    public static boolean deleteAndRestoreSageMicrOpayDB(String payrollDBName, String comDBName, String emailDomainName) throws InterruptedException, UnknownHostException{
        boolean isDone=false;
        int errorCounter=0;
        SoftAssert myAssert=new SoftAssert();

        switch (emailDomainName){
            case "mptest1002.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(101, 102)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(101, 102)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(105, 106)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(105, 106)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(107, 108)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(107, 108)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(103, 104)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(103, 104)) errorCounter++;
                }
                break;

            case "mptest1001.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(151, 152)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(151, 152)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(155, 156)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(155, 156)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(157, 158)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(157, 158)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(153, 154)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(153, 154)) errorCounter++;
                }

                break;

            case "mptest1010.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(161, 162)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(161, 162)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(155, 156)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(155, 156)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(157, 158)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(157, 158)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(163, 164)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(163, 164)) errorCounter++;
                }
                break;

            ////////////////////////////
            case "mptest1020.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(171, 172)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(171, 172)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(175, 176)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(175, 176)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(177, 178)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(177, 178)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(173, 174)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(173, 174)) errorCounter++;
                }
                break;

            //////

            ////////////////////////////
            case "mptest1023.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(181, 182)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(181, 182)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(185, 186)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(185, 186)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(187, 188)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(187, 188)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(183, 184)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(183, 184)) errorCounter++;
                }
                break;

            //////

            case "mptest1030.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(131, 132)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(131, 132)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(135, 136)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(135, 136)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(137, 138)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(137, 138)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(133, 134)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(133, 134)) errorCounter++;
                }
                break;

                //////////////////

            case "mptest1060.dynu.net":
                if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(191, 192)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(191, 192)) errorCounter++;
                }
                else if ((payrollDBName.equals("ESS2_Auto_Payroll"))&&(comDBName.equals("ESS_Auto_COM1"))){
                    SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
                    if (!DBManage.deleteMultiDB(193, 194)) errorCounter++;

                    SystemLibrary.logMessage("Step P1: Start restoring Payroll and Common DB.");
                    if (!DBManage.restoreMultiDB(193, 194)) errorCounter++;
                }
                break;

                //////

        }
        if (errorCounter==0) isDone=true;
        return isDone;
    }

    private static void logTestResultIntoDB(String itemNo, String itemName, String description, String testResult, String comment, String testSerialNo) throws Exception {
        String strSQL="insert into TestResult_ESS (ItemNo, ItemName, Description, TestResult, Comment, TestSerialNo) values ('"+itemNo+"', '"+itemName+"', '"+description+"', '"+testResult+"', '"+comment+"', '"+testSerialNo+"');";
        sqlExecutor_WithCustomizedSQL_Main(2001, 2001, strSQL);
    }

    private static void updateTestResultIntoDB(String itemNo, String itemName, String description, String testResult, String comment, String testSerialNo) throws Exception {
        String strSQL="update TestResult_ESS set ItemName='"+itemName+"', Description='"+description+"', TestResult='"+testResult+"', Comment='"+comment+"', testSerialNo='"+testSerialNo+"') where ItemNo='"+itemNo+"' and testSerialNo='"+testSerialNo+"'";
        sqlExecutor_WithCustomizedSQL_Main(2001, 2001, strSQL);
    }


    public static void logTestResultIntoDB_Main(String itemNo, String testResult, String itemName, String testSerialNo) throws Exception {
        boolean isAllDeleted=false;
        int startSerialNo=1;
        int endSerialNo=20001;
        int errorCounter=0;
        //SystemLibrary.logMessage("--- Start logging multi DB from row " + startSerialNo + " to " + endSerialNo + " in \"ESSRegTestPlan\" sheet.");

        int serialNo = 0;
        String sheetName="";

        if (itemNo.contains("Item A")){
            sheetName="ESSRegTestPlanA";
        }
        else if (itemNo.contains("Item B")){
            sheetName="ESSRegTestPlanB";
        }
        else if (itemNo.contains("Item C")){
            sheetName="ESSRegTestPlanC";
        }
        else if (itemNo.contains("Item D")){
            sheetName="ESSRegTestPlanD";
        }
        else if (itemNo.contains("Item E")){
            sheetName="ESSRegTestPlanE";
        }
        else if (itemNo.contains("Item F")){
            sheetName="ESSRegTestPlanF";
        }
        else if (itemNo.contains("Item G")){
            sheetName="ESSRegTestPlanG";
        }
        else if (itemNo.contains("Item H")){
            sheetName="ESSRegTestPlanH";
        }
        else if (itemNo.contains("Item I")){
            sheetName="ESSRegTestPlanI";
        }
        else if (itemNo.contains("Item J")){
            sheetName="ESSRegTestPlanJ";
        }
        else if (itemNo.contains("Item K")){
            sheetName="ESSRegTestPlanK";
        }
        else if (itemNo.contains("Item L")){
            sheetName="ESSRegTestPlanL";
        }              
        else if (itemNo.contains("Item M")){
            sheetName="ESSRegTestPlanM";
        }
        else if (itemNo.contains("Item N")){
            sheetName="ESSRegTestPlanN";
        }
        else if (itemNo.contains("Item O")){
            sheetName="ESSRegTestPlanO";
        }


        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 30, sheetName);

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    if (cellData[i][2].equals(itemNo)){
                        if (itemName!=null){
                            logTestResultIntoDB(itemNo, itemName, cellData[i][6], testResult, cellData[i][10], testSerialNo);
                        }else{
                            logTestResultIntoDB(itemNo, cellData[i][4], cellData[i][6], testResult, cellData[i][10], testSerialNo);
                        }
                        break;
                    }

                    /*try {
                        if (!deleteDB(cellData[i][2], cellData[i][3])) errorCounter++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCounter++;
                    }*/

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of log result into DB.");
        if (errorCounter==0) isAllDeleted=true;

    }

    public static void logTestResultIntoDB_Main(String itemNo, String testResult, String testSerialNo) throws Exception {
        boolean isAllDeleted=false;
        int startSerialNo=1;
        int endSerialNo=20001;
        int errorCounter=0;
        //SystemLibrary.logMessage("--- Start logging multi DB from row " + startSerialNo + " to " + endSerialNo + " in \"ESSRegTestPlan\" sheet.");

        int serialNo = 0;
        String sheetName="";

        if (itemNo.contains("Item A")){
            sheetName="ESSRegTestPlanA";
        }
        else if (itemNo.contains("Item B")){
            sheetName="ESSRegTestPlanB";
        }
        else if (itemNo.contains("Item C")){
            sheetName="ESSRegTestPlanC";
        }
        else if (itemNo.contains("Item D")){
            sheetName="ESSRegTestPlanD";
        }
        else if (itemNo.contains("Item E")){
            sheetName="ESSRegTestPlanE";
        }
        else if (itemNo.contains("Item F")){
            sheetName="ESSRegTestPlanF";
        }
        else if (itemNo.contains("Item G")){
            sheetName="ESSRegTestPlanG";
        }
        else if (itemNo.contains("Item H")){
            sheetName="ESSRegTestPlanH";
        }
        else if (itemNo.contains("Item I")){
            sheetName="ESSRegTestPlanI";
        }
        else if (itemNo.contains("Item J")){
            sheetName="ESSRegTestPlanJ";
        }
        else if (itemNo.contains("Item K")){
            sheetName="ESSRegTestPlanK";
        }
        else if (itemNo.contains("Item L")){
            sheetName="ESSRegTestPlanL";
        }
        else if (itemNo.contains("Item M")){
            sheetName="ESSRegTestPlanM";
        }
        else if (itemNo.contains("Item N")){
            sheetName="ESSRegTestPlanN";
        }
        else if (itemNo.contains("Item O")){
            sheetName="ESSRegTestPlanO";
        }
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 30, sheetName);

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    if (cellData[i][2].equals(itemNo)){
                        logTestResultIntoDB(itemNo, cellData[i][4], cellData[i][6], testResult, cellData[i][10], testSerialNo);
                        break;
                    }

                    /*try {
                        if (!deleteDB(cellData[i][2], cellData[i][3])) errorCounter++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCounter++;
                    }*/

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of log result into DB.");
        if (errorCounter==0) isAllDeleted=true;

    }

    public static String getTestResultFromDB(String itemNo, String testSerailNo) throws SQLException, ClassNotFoundException, InterruptedException {
        String serverName="localhost";
        String databaseName="AutoLoginDBForRestore";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = null;

        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        String strSQL="select [TestResult] from "+databaseName+".dbo.TestResult_ESS where [ItemNo]='"+itemNo+"' and testSerialNo='"+testSerailNo+"'";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            //SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                //SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {
                rs.next();
                strText=rs.getString(1);
            }

            //SystemLibrary.logMessage ("The employee code is below:"+strText);
        }
        catch (Exception e){
            //SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////


        return strText;
    }


    ///////////////////////////////
    private static boolean exportESSConfigIntoDB(String moduleName, String testSerialNo, String emailDomainName, String payrollDBName, String comDBName, String moduleFunctionName, String testRoundNo, String testItemCount, String payrollDBOrderNo, String mcsVersion, String tpuVersion, String micrOpayVersion, String comment1) throws Exception {
        if (testRoundNo==null){
            testRoundNo="";
        }
        if (testItemCount ==null){
            testItemCount ="";
        }
        if (payrollDBOrderNo==null){
            payrollDBOrderNo="";
        }
        if (comment1==null){
            comment1="";
        }

        if (testSerialNo!=null){
            if (testSerialNo.length()==2){
                testSerialNo=generateNewTestSerialNo(testSerialNo);
            }
        }

        String strSQL="INSERT INTO dbo.TestRoundConfig (ModuleName, TestSerialNo, EmailDomainName, PayrollDBName, CommonDBName, ModuleFunctionName, TestRoundCode, TotalTestItem, PayrollDBOrder, MCSVersion, TPUVersion, MicrOpayVersion, Comment) VALUES ('"+moduleName+"', '"+testSerialNo+"', '"+emailDomainName+"', '"+payrollDBName+"', '"+comDBName+"', '"+moduleFunctionName+"', '"+testRoundNo+"', '"+testItemCount+"', '"+payrollDBOrderNo+"', '"+mcsVersion+"', '"+tpuVersion+"', '"+micrOpayVersion+"', '" +comment1+"');";
        sqlExecutor_WithCustomizedSQL_Main(2001, 2001, strSQL);
        logMessage("test serial number: "+testSerialNo+" is exported into DB.");
        return true;
    }

    public static boolean exportESSConfigIntoDB_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start importing muliti ESS Config into DB from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!exportESSConfigIntoDB(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][13], cellData[i][14], cellData[i][15])) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of importing muliti ESS Config into DB.");
        return isPassed;
    }

    public static String getLatestSerialNumberFromReportDB(String snPrefix, String moduleName) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = null;
        String serverName="mptest1002.dynu.net";
        //String serverName="localhost";

        String sqlUserName="AutoTester";
        String sqlPassword="ESSTesting99";
        String databaseName="AutoLoginDBForRestore";

        String strSQL=null;
        if (moduleName!=null){
            strSQL="select TOP 1 TestSerialNo from [AutoLoginDBForRestore].[dbo].[TestRoundConfig] where TestSerialNo like '"+snPrefix+"%' and ModuleName='"+moduleName+"' order by SerailNo desc";
        }else{
            strSQL="select TOP 1 TestSerialNo from [AutoLoginDBForRestore].[dbo].[TestRoundConfig] where TestSerialNo like '"+snPrefix+"%' order by SerailNo desc";
        }


        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + payrollDBName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {
                rs.next();
                strText=rs.getString(1);
            }
            logMessage("The SQL is: "+strSQL);
            SystemLibrary.logMessage ("The Lasted Serial Number  is '"+strText+"'");
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////
        return strText;
    }


    public static String getEmployeeEOPDate(String serverName, String payrollDBName, String employeeCode, int iLeaveType) throws SQLException, ClassNotFoundException, InterruptedException {
        boolean isBackup=false;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        String strText = null;

        //String strSQL = "RESTORE DATABASE " + payrollDBName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";

        String strSQL="use "+payrollDBName+"; select dPostProDate from [_iptblEmployeeLeave] inner join _iptblEmployee on _iptblEmployee.iEmploymentTypeID=[ESS_Auto_Payroll1].[dbo].[_iptblEmployeeLeave].iEmployeeID  where cEmpCode='"+employeeCode+"' and iLeaveType="+iLeaveType+";";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + payrollDBName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + payrollDBName;
            Connection conn = DriverManager.getConnection(url);

            SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
                strText="null";
            } else {
                rs.next();
                strText=rs.getString(1);
                strText=strText.substring(0, 10);
                strText=convertDateFormat_3(strText);
            }
            logMessage("The SQL is: "+strSQL);


            SystemLibrary.logMessage ("The EOP date is '"+strText+"'.");
        }
        catch (Exception e){
            SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////


        return strText;
    }


    public static boolean backupMultiDBSaveInFile(int startSerialNo, int endSerialNo, String testSerialNo) throws InterruptedException {
        boolean isBackuped=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Backup DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBBackupRestore\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 15, "DBBackupRestore");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        //    if (backupDB(cellData[i][2], cellData[i][3], cellData[i][7])==null) errorCounter++;
                        if (backupDBSaveInFile(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo)==null) errorCounter++;

                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorCounter++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Backup DB from \"DBBackupRestore\" sheet.");
        if (errorCounter==0) isBackuped=true;
        return isBackuped;
    }

    public static boolean restoreMultiDBFromFile(int startSerialNo, int endSerialNo, String testSerialNo) throws InterruptedException {
        boolean isBackuped=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Backup DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBBackupRestore\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 15, "DBBackupRestore");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        //    if (backupDB(cellData[i][2], cellData[i][3], cellData[i][7])==null) errorCounter++;
                        if (restoreDBFromFile(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo)==false) errorCounter++;

                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        errorCounter++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Backup DB from \"DBBackupRestore\" sheet.");
        if (errorCounter==0) isBackuped=true;
        return isBackuped;
    }

    public static boolean deleteMultiDBFromFile(int startSerialNo, int endSerialNo) throws InterruptedException {
        boolean isAllDeleted=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Deleting multi DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBBackupRestore\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 20, "DBBackupRestore");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        if (!deleteDB(cellData[i][2], cellData[i][3])) errorCounter++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Deleting DB from \"DBBackupRestore\" sheet.");
        if (errorCounter==0) isAllDeleted=true;
        return isAllDeleted;
    }


    public static boolean sqlExecuter_Compare_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start executing SQL from row " + startSerialNo + " to " + endSerialNo + " in \"SQLExecutor\" sheet.");

        int serialNo = 0;
        boolean isPassed = false;
        int errorCounter=0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 40, "SQLExecutor");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                      isPassed = sqlExecuter_Compare(cellData[i][2], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][13], emailDomainName, testSerialNo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorCounter ++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter ++;
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of executing SQL from \"SQLExecutor\" sheet.");
        if(errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean sqlExecuter_Compare(String strSQL, String serverName, String databaseName, String storeFileName, String isUpdateStore, String isCompare, String exectueType, String expectedContent, String emailDomainName, String testSerialNo) throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs;
        int returnResult=0;
        String strText = "";
        boolean isPassed = false;
        int errorCounter=0;

        //String url="jdbc:sqlserver://ESSV1-0\\SQLEXPRESS:1433;user=ESSTest;password=WebTesting99;Database=ESS_Test";
        String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
        Connection conn = DriverManager.getConnection(url);

        //SystemLibrary.logMessage("Connecting " + serverName + " ...");
        Statement sta = conn.createStatement();

        if (exectueType==null) exectueType="1";

        if (exectueType.equals("2")){
            try{
                returnResult=sta.executeUpdate(strSQL);
                SystemLibrary.logMessage("The result message is "+returnResult);
            }
            catch (SQLException e) {
                e.printStackTrace();
                errorCounter++;
            }
        }
        else if (exectueType.equals("1")){
            ////////////////////
            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                //SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {

                // The column count starts from 1
                for (int j = 1; j <= columnCount; j++) {
                    strText = strText + "\"" + rsmd.getColumnName(j) + "\",";
                    // Do stuff with name
                }
                strText = strText + "\r\n";

                while (rs.next()) {
                    for (int k = 1; k <= columnCount; k++) {
                        strText = strText + "\"" + rs.getString(k) + "\",";
                    }
                    strText = strText + "\r\n";
                }

                String sqlResultFileName = "SQLResult_" + getCurrentTimeAndDate() + ".csv";
                String fileFullPathName = SystemLibrary.workingFilePath + sqlResultFileName;

                if (!SystemLibrary.validateStringContainInFile(strText, storeFileName, isUpdateStore, isCompare, expectedContent, emailDomainName, testSerialNo)){
                    errorCounter++;
                }

              /*

                if (SystemLibrary.createTextFile(fileFullPathName, strText)) {
                    SystemLibrary.logMessage("SQL resrult is saved as \"" + fileFullPathName + "\".");
                    String urlAddress = SystemLibrary.serverUrlAddress + "TestLog/WorkingFile/" + sqlResultFileName;
                    System.out.println("Click here to access the SQL Result: " + urlAddress);
                }
                //SystemLibrary.logMessage(strSQL);
                //SystemLibrary.logDebug(strText);

                if (storeFileName != null) {
                    if (updateStoreFileName != null) {
                        if (updateStoreFileName.equals("1")) try {
                            SystemLibrary.saveFileToStore(fileFullPathName, storeFileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            errorCounter++;
                        }
                    }
                    if (validateFile != null) {
                        if (validateFile.equals("1")) {
                            isPassed = SystemLibrary.compareTextFile(fileFullPathName, SystemLibrary.getStoreFileFullPathName(storeFileName));
                        }
                    }
                }
                */

            }
            ///////
        }

        if(errorCounter ==0) isPassed = true;
        return isPassed;

    }

    public static int getSmokeTestStatus() throws SQLException, ClassNotFoundException, InterruptedException {
        String serverName="localhost";
        String databaseName="AutoLoginDBForRestore";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ResultSet rs=null;
        int testStatus = 0;

        //String strSQL = "RESTORE DATABASE " + databaseName + " FROM DISK='" + backupFileFullName + "' WITH REPLACE";
        //String strSQL="select cEmpCode from ESS_Auto_Payroll.dbo._iptblEmployee where cFirstName='"+firstName+"' and cSurname='"+lastName+"'";
        String strSQL="SELECT TOP 1 * FROM [AutoLoginDBForRestore].[dbo].[TestCommand] order by CommandTime desc;";

        logMessage(strSQL);

        try {
            //String url = "jdbc:sqlserver://" + serverName + ";user=AutoTester;password=ESSTest99;Database=" + databaseName;
            String url = "jdbc:sqlserver://" + serverName + ";user="+sqlUserName+";password="+sqlPassword+";Database=" + databaseName;
            Connection conn = DriverManager.getConnection(url);

            //SystemLibrary.logMessage("Connecting " + serverName + " ...");
            Statement sta = conn.createStatement();

            rs = sta.executeQuery(strSQL);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.wasNull()) {
                //SystemLibrary.logMessage(strSQL);
                SystemLibrary.logMessage("SQL Execution is completed without value return.");
            } else {
                rs.next();
                testStatus=rs.getInt(3);
            }

            //SystemLibrary.logMessage ("The employee code is below:"+strText);
        }
        catch (Exception e){
            //SystemLibrary.logError(e.getMessage());
        }
        //SystemLibrary.logMessage("The return result is "+returnResult);
        ///////////////


        return testStatus;
    }












    //////////////////  Debug here ////////////////



}
