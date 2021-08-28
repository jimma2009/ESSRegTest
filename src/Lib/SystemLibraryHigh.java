package Lib;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import java.io.IOException;

import static Lib.SystemLibrary.*;

/**
 * Created by j3m on 17/05/2017.
 */
public class SystemLibraryHigh {
    public static void getSingleValue() throws InterruptedException {
        SystemLibrary.logMessage("*** Get Value TestNG.");

        ExcelDataDriver dd;
        int totalRow=0;
        int currentRow = 1;
        String value=null;
        try {
            dd = new ExcelDataDriver("C:\\SeleniumWebTest\\TestNGProject\\DataSource\\DataSource.xls", "EmployeePersonalDetail");
            totalRow=dd.excel_get_rows();
            System.out.println("Total Row is " + totalRow);

            value=dd.getCellDataasstring(1,1);
            if (value == null) {
                value = "No Value";
            }
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SystemLibrary.logMessage("*** End of Get Value TestNG.");
    }

    public static boolean getMultiValue(int startSerialNo, int endSerialNo) throws InterruptedException {
        boolean isPassed=false;
        SystemLibrary.logMessage("--- Start reading data from row "+startSerialNo+" to "+endSerialNo);
        ExcelDataDriver dd = null;

        try {
            dd = new ExcelDataDriver("C:\\SelTestProject\\ESSRegTest\\DataSource\\DataSource.xls", "ESSTest");
            //System.out.print(value.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }


        int totalRowCount=50;
        System.out.println("Total Row is " + totalRowCount);

        //Define parameters
        int serialNo = -999999;
        Double rate=-999999.00;

        String flag, employeeCode, surname, name;


        int currentRow = 0;
        while (currentRow <=totalRowCount) {
            try {
                serialNo = (int) dd.getCellDataasnumber(currentRow, 0);
                System.out.println("serilaNo value in currentRow:"+currentRow+" is "+serialNo+".");
                flag = dd.getCellDataasstring(currentRow, 1);
                System.out.println("flag value in currentRow:"+currentRow+" is "+flag+".");
                employeeCode = dd.getCellDataasstring(currentRow, 2);
                System.out.println("employeeCode value in currentRow:"+currentRow+" is "+employeeCode+".");
                if ((serialNo >= startSerialNo) && (serialNo <= endSerialNo)) {
                    if ((Strings.isNullOrEmpty(flag)) && (!Strings.isNullOrEmpty(employeeCode))) {
                        surname = dd.getCellDataasstring(currentRow, 3);
                        name = dd.getCellDataasstring(currentRow, 4);
                        if (!Strings.isNullOrEmpty(dd.getCellDataasstring(currentRow, 5))) {
                            rate = Double.parseDouble(dd.getCellDataasstring(currentRow, 5));
                        }
                        else {
                            rate=-999999.99;
                        }

                        System.out.println("serialNo=" + serialNo);
                        System.out.println("flag=" + flag);
                        System.out.println("employeeCode=" + employeeCode);
                        System.out.println("surname=" + surname);
                        System.out.println("name=" + name);
                        System.out.println("rate=" + rate);

                    }
                    else {
                        SystemLibrary.logWarning("--- Row " + serialNo + " in \"ESSTest\" sheet is ignored with flag or not data.");
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            currentRow++;
            rate=-999999.99;
        }

        SystemLibrary.logMessage("*** End of Reading Data from ESSTest sheet.");

        return isPassed;
    }

    public static String[][] readDataFromDatasheet(int startSerialNo, int endSerialNo, int columnCount, String sheetName) throws InterruptedException {

        //SystemLibrary.logMessage("--- Start reading data from row "+startSerialNo+" to "+endSerialNo+" in sheet: "+sheetName+".");
        //This is total row check in the datasheet.
        int totalRowCount=1500;
        int serialNo = -999999;
        String strSerialNo=null;
        String cellData[][]=new String[endSerialNo-startSerialNo+1][columnCount];
        String flag;
        int currentRow = 0;
        int arrayRow=0;

        ExcelDataDriver dd = null;
        try {
            dd = new ExcelDataDriver(SystemLibrary.dataSourcePath+ "DataSource.xls", sheetName);
            //System.out.print(value.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (currentRow <=totalRowCount) {
            try {
                serialNo = (int)(dd.getCellDataasnumber(currentRow, 0));
                //System.out.println("serialNo="+serialNo);
                flag = dd.getCellDataasstring(currentRow, 1);

                if ((serialNo >= startSerialNo) && (serialNo <= endSerialNo)) {
                    if ((Strings.isNullOrEmpty(flag))||(serialNo!=-999999)){

                        strSerialNo=String.valueOf(serialNo);
                        //System.out.println(strSerialNo);
                        cellData[arrayRow][0]=strSerialNo;
                        for (int i=1;i<columnCount;i++){
                            //System.out.println(dd.getCellDataasstring(currentRow, i));
                            cellData[arrayRow][i]=dd.getCellDataasstring(currentRow, i);
                        }
                        arrayRow++;
                    }
                    else {
                        SystemLibrary.logWarning("--- Row " + serialNo + " in "+sheetName+"  sheet is ignored with flag or not data.");
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            currentRow++;
        }

        SystemLibrary.logMessage("--- Reading Data from sheet \""+sheetName+"\" is completed.");
        return cellData;

    }

    public static boolean saveMultiFileIntoStore(int startSerialNo, int endSerialNo) throws InterruptedException {
        SystemLibrary.logMessage("--- Start Saving Multi File into Store from row "+startSerialNo+" to "+endSerialNo+" in UpdateStore sheet.");
        boolean isFound=true;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,5,"UpdateStore");

        System.out.println("cellData.length="+cellData.length);
        for (int i = 0; i < cellData.length; i++) {

            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if ((Strings.isNullOrEmpty(cellData[i][1]))&&(!Strings.isNullOrEmpty(cellData[i][3]))) {

                    //SystemLibrary.logMessage("itemName1="+cellData[i][2]);
                    try {
                        saveFileToStore(cellData[i][2], cellData[i][3]);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        isFound=false;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("--- End of Saving multi files into store.");
        return isFound;
    }

    public static boolean UpdateStore() throws InterruptedException {
        logMessage("--- Start update store files.");
        boolean isUpdated=saveMultiFileIntoStore(1, 100);
        logMessage("--- End of update store files.");
        return isUpdated;
    }

    public static boolean validateTextInElement_Main(int startSerialNo, int endSerialNo, WebElement element, String testSerialNo, String emailDomainName) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi text in Element from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (cellData[i][9]==null){
                        if (!SystemLibrary.validateTextValueInElement(element, cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName)) errorCounter++;
                    }
                    else{
                        if (!SystemLibrary.validateTextValueInElement(element, cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName)) errorCounter++;

                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating multi text in Element.");
        return isDone;
    }

    public static boolean validateTextValueInWebElementInUse_Main(int startSerialNo, int endSerialNo, WebElement element, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Web Element from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!validateTextValueInWebElementInUse(element, cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating multi Element.");
        return isDone;
    }

    public static boolean validateMultiStringFile(int startSerialNo, int endSerialNo, String stringToBeValidate, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi File from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (cellData[i][9]!=null){
                        if (!SystemLibrary.validateStringContainInFile(stringToBeValidate, cellData[i][6],cellData[i][7],cellData[i][8],cellData[i][9], emailDomainName, testSerialNo)) errorCounter++;
                    }
                    else{
                        if (!SystemLibrary.validateStringFile(stringToBeValidate, cellData[i][6],cellData[i][7],cellData[i][8], emailDomainName, testSerialNo)) errorCounter++;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating multi File.");
        return isDone;
    }

    public static boolean validateMultiPDFFile(int startSerialNo, int endSerialNo, String fileFullPathName) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi PDF File from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!SystemLibrary.updateAndValidateStorePDFFile(fileFullPathName, cellData[i][6],cellData[i][7],cellData[i][8],cellData[i][9])) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating multi File.");
        return isDone;
    }

    public static boolean validateScreenshotInElement_Main(int startSerialNo, int endSerialNo, WebElement element, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Web Element from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!validateScreenshotInElement(element, cellData[i][6], cellData[i][7], cellData[i][8], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating multi Element.");
        return isDone;
    }

    public static boolean modifyEmailStoreFile(int startSerialNo, int endSerialNo, String oldString, String newString) throws Exception {
        SystemLibrary.logMessage("--- Start Modifying Multi Email Store File from row " + startSerialNo + " to " + endSerialNo + " in \"MailServer\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"MailServer");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (cellData[i][8]!=null){
                        String storeFileFullPathName= storeFilePath+cellData[i][8];
                        if (!modifyTextFile(storeFileFullPathName, oldString, newString)) errorCounter++;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of  Modifying Multi Email Store.");
        return isDone;
    }

}
