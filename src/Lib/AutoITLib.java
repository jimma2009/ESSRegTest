package Lib;

import autoitx4java.AutoItX;
import org.openqa.selenium.winium.WiniumDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static Lib.SystemLibrary.*;
import static autoitx4java.AutoItX.*;


public class AutoITLib {

    @Test
    public static void launchMeridain() throws InterruptedException {
        exitMeridian();

        AutoItX x = new AutoItX();
        x.run("C:\\Program Files (x86)\\Sage MicrOpay\\Evolution.exe");
        if (x.winWait("Sign in to Sage MicrOpay", "", 30) == true) {
            logMessage("Sage Micropay is launched.");
        } else {
            logError("Sage Micropay is NOT launched.");
        }
        logScreenshotX();
    }

    public static boolean logonMeridian(String userName, String password, String payrollDBName, String payrollDBOrderNo) throws InterruptedException {
        //launchMeridian first
        boolean isDone = false;
        AutoItX x = new AutoItX();
        x.winActive("Sign in to Sage Micropay");
        x.winWaitActive("Sign in to Sage MicrOpay", "", 60);

        switch (payrollDBOrderNo) {
            case "1":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 28);
                break;
            case "2":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 49);
                break;
            case "3":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 70);
                break;
        }

        /*switch (payrollDBName) {
            case "ESS_Auto_Payroll":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 28);
                break;
            case "ESS_Auto_Payroll2":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 49);
                break;
            case "ESS_Auto_Payroll3":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 70);
                break;
        }*/
        Thread.sleep(2000);
        logMessage("Payroll DB: '" + payrollDBName + "' is selected.");

        if (userName != null) {
            x.controlFocus("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]");
            x.controlClick("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "left", 2);
            x.controlSend("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "{DEL}", false);
            x.controlClick("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "left", 2);
            x.controlSend("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", userName);
            logMessage("User name is input.");
        }

        if (password != null) {
            x.controlFocus("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]");
            x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]");
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", "^a");
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", "{DEL}", false);
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", password);
            logMessage("Password is input.");
        }

        x.controlClick("Sign in to Sage MicrOpay", "Sign in", "[Class:TevColorButton; Instance:7]");
        Thread.sleep(9000);

        if (x.winWait("Sage MicrOpay", "", 120) == true) {
            logMessage("Sage Micropay is launched.");

            if (x.winWait("Notifications from Sage", "", 15)){
                x.winActivate("Notifications from Sage");
                logMessage("Notification Window pops up.");
                logScreenshotX();

                x.controlFocus("Notifications from Sage", "Mark the latest notifications as read (All notifications can still be viewed from the Main Menu)", "[CLASS:TevCheckBox; INSTANCE:1]");
                x.send("{SPACE}", false);
                x.sleep(2000);

                x.winClose("Notifications from Sage");
                x.sleep(2000);
                logMessage("Notification screen is closed.");
            }

            x.winSetState("Sage Micropay is launched.", "", SW_MAXIMIZE);
            Thread.sleep(8000);
            isDone = true;
        } else {
            logError("Sage Micropay is NOT launched.");
        }
        logScreenshotX();

        return isDone;
    }

    public static void logScreenshotX() throws InterruptedException {
        try {
            Robot robot = new Robot();

            String fileName = "Screenshot";
            String fileFormat = "png";
            String fileNameToBeGenerated = fileName + "_" + getCurrentTimeAndDate() + "." + fileFormat;
            String fileFullPathName = projectPath + "TestScreenshot\\" + fileNameToBeGenerated;

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, fileFormat, new File(fileFullPathName));

            SystemLibrary.logMessage(" Screenshot is saved as \"" + fileFullPathName + "\".");
            System.out.println("Click here to access the screenshot: " + serverUrlAddress + "TestScreenshot/" + fileNameToBeGenerated);

        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }

    public static boolean display_ImplementHRScreen() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();
        Thread.sleep(20000);
        x.winActivate("Sage MicrOpay");
        x.winWaitActive("Sage MicrOpay", "", 30);
        x.send("{ALT}m", false);
        Thread.sleep(2000);
        x.send("{DOWN}{RIGHT}{RIGHT}{DOWN}{DOWN}{DOWN}{ENTER}", false);
        Thread.sleep(5000);

        if (x.winWait("Implement HR Changes", "OK", 15) == true) {
            logMessage("Extra Dialogue is shown.");
            logScreenshotX();
            x.controlClick("Implement HR Changes", "OK", "[Class:TButton; Instance:1]");
            logMessage("OK button is clicked.");
            Thread.sleep(2000);
        }

        x.winSetState("Implement HR Changes", "", SW_MAXIMIZE);
        Thread.sleep(2000);
        logMessage("Implement HR screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean display_ImplementeHRScreen() throws InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();
        if (display_ImplementHRScreen()) {
            x.winActivate("Implement HR Changes", "Implement eHR");
            x.controlFocus("Implement HR Changes", "Implement eHR", "[Class:TGroupButton; Instance:1]");
            x.controlClick("Implement HR Changes", "Implement eHR", "[Class:TGroupButton; Instance:1]");
            Thread.sleep(20000);

            int counter = 0;
            while (!x.controlCommandIsVisible("Implement HR Changes", "eHR Report", "[CLASS:TButton; INSTANCE:2]")) {
                Thread.sleep(10000);
                counter++;
                if (counter >= 12) {
                    logError("Loading is over 2 mins.");
                    errorCounter++;
                    break;
                }
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) {
            logMessage("Implement eHR is shown.");
            x.winActivate("Implement HR Changes");
            x.controlFocus("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]");
            for (int i=0;i<400;i++){
                x.send("{RIGHT}", false);
            }

            logMessage("Screenshot after move to the right.");
            logScreenshotX();

            for (int i=0;i<400;i++){
                x.send("{LEFT}", false);
            }

            logMessage("Screenshot after move back to left");
            logScreenshotX();
        } else {
            logError("Failed display Implement eHR.");
            logScreenshotX();
        }

        if (errorCounter == 0) isShown = true;
        return isShown;
    }

    public static boolean saveGridInImplementEHRScreen(String storeFileName, String isUpdate, String isCompare, String expectedContent) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        AutoItX x = new AutoItX();

        if (display_ImplementeHRScreen()) {

            if (x.controlCommandIsEnabled("Implement HR Changes", "Save Grid", "[CLASS:TButton; INSTANCE:4]")) {
                x.controlClick("Implement HR Changes", "Save Grid", "[Class:TButton; Instance:4]");
                Thread.sleep(2000);
                logMessage("Save Grid button is clicked.");
                x.winWait("Save Grid to File", "", 20);
                x.winActivate("Save Grid to File", "");

                String fileName = "ImplementEHR_" + getCurrentTimeAndDate() + ".txt";
                String fileFullPathName = workingFilePath + fileName;

                x.controlClick("Save Grid to File", "", "[Class:ComboBox; Instance:2]");
                Thread.sleep(2000);
                x.controlSend("Save Grid to File", "", "[Class:ComboBox; Instance:2]", "{UP}{UP}{UP}{UP}{UP}{UP}", false);
                x.controlSend("Save Grid to File", "", "[Class:ComboBox; Instance:2]", "{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{ENTER}", false);
                Thread.sleep(2000);
                x.controlFocus("Save Grid to File", "", "[Class:ComboBox; Instance:1]");
                x.controlCommandSetCurrentSelection("Save Grid to File", "", "[Class:ComboBox; Instance:1]", "");
                Thread.sleep(2000);
                x.controlSend("Save Grid to File", "", "[Class:ComboBox; Instance:1]", fileFullPathName);
                Thread.sleep(5000);
                logMessage("File Path " + fileFullPathName + " is input.");
                logScreenshotX();

                x.controlClick("Save Grid to File", "&Save", "[Class:Button; Instance:2]");
                Thread.sleep(5000);
                logMessage("Save button is clicked.");

                if (storeFileName != null) {
                    if (!SystemLibrary.updateAndValidateStoreStringFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent))
                        errorCounter++;
                }

                x.winWait("Implement HR Changes", "", 10);
                x.winActivate("Implement HR Changes");
                x.controlClick("Implement HR Changes", "&No", "[CLASS:TButton; INSTANCE:1]");
                Thread.sleep(4000);
                logMessage("Review Now - No button is clicked.");
                logMessage("Grid Implement EHR is saved as " + fileFullPathName);

            } else {
                logError("Button is NOT enabled.");
                errorCounter++;
            }
            /////////////////

            close_ImplementHRScreen();


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean close_ImplementHRScreen() throws InterruptedException {
        AutoItX x = new AutoItX();
        x.winActivate("Implement HR Changes");
        x.controlClick("Implement HR Changes", "Close", "[Class:TButton; Instance:5]");
        Thread.sleep(2000);
        logMessage("Implement HR Changes screen is closed.");
        logScreenshotX();
        return true;
    }


    public static boolean saveGridInImplementEHRScreen_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Saving and Validate Grid in Implement eHR Screen from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.saveGridInImplementEHRScreen(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9]))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Saving and Validate Grid in Implement eHR Screen.");
        return isPassed;
    }

    public static boolean generateEHRPReImpReport_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating eHR Pre-Implementation Report in Implement eHR Screen from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.generateEHRPReImpReport(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], emailDomainName, testSerialNo))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Generating eHR Pre-Implementation Report in Implement eHR Screen.");
        return isPassed;
    }

    public static boolean exitMeridian() throws InterruptedException {
        boolean isClosed = false;
        AutoItX x = new AutoItX();
        int attempt = 0;

        if (x.winWait("Sage MicrOpay", "", 5)) {
            x.winActivate("Sage MicrOpay");
            x.winClose("Sage MicrOpay");
            //x.send("{ALT}x", false);
            Thread.sleep(2000);
            logMessage("Exit Sage MicrOpay is clicked.");
            x.controlFocus("Exit Sage MicrOpay", "Yes", "[Class:TButton; Instance:2]");
            x.controlClick("Exit Sage MicrOpay", "Yes", "[Class:TButton; Instance:2]");
            Thread.sleep(2000);
            attempt = 1;
        } else if (x.winWait("Sign in to Sage MicrOpay", "", 5)) {
            x.winActivate("Sign in to Sage MicrOpay");
            x.winClose("Sign in to Sage MicrOpay");
            //x.send("{ALT}x", false);
            Thread.sleep(2000);
            logMessage("Logon Micropay Screen is closed.");
            attempt = 1;
        } else {
            logMessage("MicrOpay is NOT started.");
        }

        if (attempt > 0) {
            if (x.processWaitClose("Evolution.exe", 15)) {
                logMessage("Sage Micropay is closed.");
                isClosed = true;
            } else {
                x.processClose("Evolution.exe");
                logWarning("Sage Micropay is terminated.");
                isClosed = true;
            }
        }

        return isClosed;
    }

    public static boolean generateEHRPReImpReport(String storeFileName, String isUpdate, String isCompare, String expectedContent, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        AutoItX x = new AutoItX();
        if (display_ImplementeHRScreen()) {
            x.controlClick("Implement HR Changes", "eHR Report", "[Class:TButton; Instance:2]");
            Thread.sleep(2000);
            logWarning("eHR button is clicked.");

            if (x.winWait("Print Preview", "", 300)) {
                String fileName = "HRPreImpReport_" + getCurrentTimeAndDate();
                String fileFullPathName = workingFilePath + fileName;

                x.winActivate("Print Preview", "");
                logMessage("eHR Print Preveiw screen is shown.");
                logScreenshotX();

                x.controlFocus("Print Preview", "", "[Class:TppToolbar; Instance:1]");
                Thread.sleep(2000);
                x.mouseClick("left", 20, 34, 1, 1);
                Thread.sleep(2000);
                logMessage("Print button on Toolbar is clicked.");
                logScreenshotX();

                x.winWait("Print", "", 20);
                x.winActivate("Print", "");
                x.controlClick("Print", "cbxPrintToFile", "[Class:TCheckBox; Instance:4]");
                Thread.sleep(2000);
                logMessage("Checkbox Print To File is checked.");
                logScreenshotX();

                x.controlFocus("Print", "", "[Class:TEdit; Instance:3]");
                //x.controlSend("Print", "", "[CLASS:TEdit; INSTANCE:3]", "{LCTRL}A", false);
                x.controlSend("Print", "", "[CLASS:TEdit; INSTANCE:3]", "{DEL}", false);
                Thread.sleep(2000);
                x.controlSend("Print", "", "[Class:TEdit; Instance:3]", fileFullPathName);
                Thread.sleep(2000);
                fileFullPathName = fileFullPathName + ".pdf";
                logMessage("File Path " + fileFullPathName + " is input.");
                logScreenshotX();

                x.controlClick("Print", "OK", "[Class:TButton; Instance:3]");
                Thread.sleep(5000);
                logMessage("OK button is clicked in Print Screen.");

                x.winWait("Print Preview", "", 70);
                x.winActivate("Print Preview", "");
                x.winClose("Print Preview");
                x.sleep(2000);
                logMessage("Print Preview screen is closed.");
                logMessage("eHR Pre Implement Report is saved as " + fileFullPathName);

                String sReportContent = SystemLibrary.getStringFromPDFFile(fileFullPathName);
                if (!SystemLibrary.validateStringContainInFile(sReportContent, storeFileName, isUpdate, isCompare, expectedContent, emailDomainName, testSerialNo))
                    errorCounter++;

            } else {
                x.winActivate("Implement HR Changes", "OK");
                logMessage("No Records in the eHR report.");
                logScreenshotX();
                x.controlClick("Implement HR Changes", "OK", "[CLASS:TButton; INSTANCE:1]");
                logMessage("OK button is clicked.");
                Thread.sleep(2000);
                logError("Print Preveiw screen is NOT shown.");
                errorCounter++;
            }

            close_ImplementHRScreen();
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean implementEHR() throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();
        if (display_ImplementeHRScreen()) {

            if (x.controlCommandIsEnabled("Implement HR Changes", "Implement", "[CLASS:TButton; INSTANCE:3]")) {
                x.mouseClick("left", 621, 77);
                Thread.sleep(2000);
                logMessage("Checkbox Implement ALl is ticked.");
                logScreenshotX();

                x.controlClick("Implement HR Changes", "Implement", "[CLASS:TButton; INSTANCE:3]");
                Thread.sleep(70000);
                logMessage("Butotn Implement is clicked.");
                logScreenshotX();

                x.winActivate("Implement HR Changes");
                x.controlFocus("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]");
                for (int i=0;i<400;i++){
                    x.send("{RIGHT}", false);
                }

                logMessage("Screenshot after move to the right.");
                logScreenshotX();

                for (int i=0;i<400;i++){
                    x.send("{LEFT}", false);
                }

                logMessage("Screenshot after move back to left");
                logScreenshotX();

            } else {
                logError("Implement Button is not enabled.");
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean display_PrintEmployeeDetailsReport() throws InterruptedException {
        boolean isShown = false;
        AutoItX x = new AutoItX();

        x.winActivate("Sage MicrOpay");
        x.winWaitActive("Sage MicrOpay", "", 30);
        x.send("!r", false);
        Thread.sleep(2000);
        x.send("{DOWN}{RIGHT}{DOWN}{DOWN}{DOWN}{RIGHT}{DOWN}{ENTER}", false);
        Thread.sleep(2000);

        if (x.winWait("Report -- Employee Details", "", 60)) {
            logMessage("Print Employee Details screen is shown.");
            isShown = true;
        } else {
            logError("Print Employee Details screen is NOT shown.");
        }
        logScreenshotX();
        return isShown;
    }

    public static boolean print_EmployeeDetailsReport(String fromEmployeeCode, String toEmployeeCode, String storeFileName, String isUpdate, String isCompare, String expectedContent, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (display_PrintEmployeeDetailsReport()) {
            AutoItX x = new AutoItX();

            x.winActivate("Report -- Employee Details");

            if (fromEmployeeCode!=null){
                x.controlFocus("Report -- Employee Details", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:2]");
                x.controlSend("Report -- Employee Details", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:2]", fromEmployeeCode+"{Tab}", false);
                x.sleep(2000);
                logMessage("From Employee '"+fromEmployeeCode+"' is input.");
            }

            if (toEmployeeCode!=null){
                x.controlFocus("Report -- Employee Details", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]");
                x.controlSend("Report -- Employee Details", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]", fromEmployeeCode+"{Tab}", false);
                x.sleep(2000);
                logMessage("To Employee '"+toEmployeeCode+"' is input.");
            }

            x.controlFocus("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]");
            x.controlClick("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]");
            x.controlSend("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]", "{SPACE}");
            logMessage("Select All option is checked.");

            logMessage("Screenshot before click Print butotn.");
            logScreenshotX();

            x.controlFocus("Report -- Employee Details", "Print", "[CLASS:TButton; INSTANCE:4]");
            x.controlClick("Report -- Employee Details", "Print", "[CLASS:TButton; INSTANCE:4]");
            logMessage("Pirnt button is clicked.");
            Thread.sleep(2000);

            String fileName = "EmployeeDetailReport_" + getCurrentTimeAndDate() + ".pdf";
            String fileFullPathName = workingFilePath + fileName;


            if (!print_File(fileFullPathName)) {
                errorCounter++;
            }

            String sReportContent = SystemLibrary.getStringFromPDFFile(fileFullPathName);
            if (!SystemLibrary.validateStringContainInFile(sReportContent, storeFileName, isUpdate, isCompare, expectedContent, emailDomainName, testSerialNo))
                errorCounter++;


            x.controlFocus("Report -- Employee Details", "Close", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Report -- Employee Details", "Close", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(2000);
            logMessage("Print Employee Detail Report screen is closed.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean print_File(String fileFullPathName) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        //Display System Print dialogue first.
        AutoItX x = new AutoItX();

        if (x.winWait("Print", "", 60)) {
            x.winActivate("Print", "");
            x.controlClick("Print", "cbxPrintToFile", "[CLASS:TCheckBox; INSTANCE:4]");
            Thread.sleep(2000);
            logMessage("Checkbox Print To File is checked.");
            logScreenshotX();

            x.controlFocus("Print", "", "[Class:TEdit; Instance:3]");
            x.controlSend("Print", "", "[Class:TEdit; Instance:3]", "^a");
            x.sleep(2000);
            //x.controlSend("Print", "", "[Class:TEdit; Instance:3]", "{LCTRL}a", false);
            x.controlSend("Print", "", "[Class:TEdit; Instance:3]", "{DEL}", false);
            Thread.sleep(2000);

            String inputFileFullPathName = fileFullPathName.replace(".pdf", "");
            x.controlSend("Print", "", "[Class:TEdit; Instance:3]", inputFileFullPathName);
            x.sleep(3000);
            logMessage("File Path '" + fileFullPathName + "' is input.");
            logScreenshotX();

            x.controlClick("Print", "OK", "[Class:TButton; Instance:3]");
            Thread.sleep(15000);
            logMessage("OK button is clicked in Print Screen.");

            int a=0;
            while (x.winWait("Printing", "", 10)){
                Thread.sleep(10000);
                a++;
                if (a>15);
            }
            x.winActivate("Printing", "");
            logScreenshotX();

            a = 0;
            while (x.controlCommandIsVisible("Printing", "Cancel", "[CLASS:TButton; INSTANCE:1]")) {
                Thread.sleep(60000);
                a++;
                if (a > 10) {
                    logError("Terminate printing report.");
                    errorCounter++;
                    break;
                }
            }

            logMessage("File: '" + fileFullPathName + "' is printed.");
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }



    public static void displayEmployeeListScreen() throws InterruptedException {
        searchMenu("Employees", 4);

        //Sort the employees by employee code
        AutoItX x = new AutoItX();

        x.winActivate("Sage MicrOpay");
        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.controlClick("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 689, 42);
        x.sleep(4000);

        x.controlClick("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 22, 42);
        x.sleep(4000);
        logMessage("Employee list is sorted by Employee Code.");

        x.controlClick("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 9, 830);
        x.sleep(3000);
        logMessage("Button go to top is clicked.");

        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.controlClick("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 18, 62);
        logMessage("The first row is clicked.");
        x.send("{DOWN}{DOWN}{UP}{UP}");
        logMessage("Screenshot of Employee List Screen.");
        logScreenshotX();

    }

    public static void displayPublicHolidayPlannerListScreen() throws InterruptedException {
        searchMenu("Public Holiday Planner", 1);
    }

    public static boolean searchMenu(String itemName, int order) throws InterruptedException {
        boolean isShown = false;
        AutoItX x = new AutoItX();
        x.controlFocus("Sage MicrOpay", "Search Functions", "[CLASS:TevColorButton; INSTANCE:1]");
        x.controlClick("Sage MicrOpay", "Search Functions", "[CLASS:TevColorButton; INSTANCE:1]");
        Thread.sleep(10000);
        logMessage("Search button is clicked.");

        if (x.winWait("Search Functions", "", 10)) {
            Thread.sleep(3000);
            logScreenshotX();

            x.controlFocus("Search Functions", "", "[CLASS:TcxTextEdit; INSTANCE:1]");
            String[] inputString = SystemLibrary.splitString(itemName, "");
            for (int i = 0; i < inputString.length; i++) {
                x.controlSend("Search Functions", "", "[CLASS:TcxTextEdit; INSTANCE:1]", inputString[i]);
                Thread.sleep(500);
            }

            Thread.sleep(2000);

            if (order > 0) {
                for (int i = 0; i < order; i++)
                    x.controlSend("Search Functions", "", "[CLASS:TcxInnerListBox; INSTANCE:1]", itemName + "{DOWN}", false);
                Thread.sleep(1000);
            }
            logMessage("Item '" + itemName + "' is input.");
            logScreenshotX();

            x.controlClick("Search Functions", "Open", "[CLASS:TevColorButton; INSTANCE:1]");
            Thread.sleep(5000);
            logMessage("Button Open is Clicked.");
            logScreenshotX();
            isShown = true;
        } else {
            logError("Search screen is NOT shown.");
        }

        return isShown;
    }


    public static boolean displayEmployee_LeaveDetails() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 19, 178);
        x.sleep(4000);

        logMessage("Employee Leave Detail screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean displayEmployee_PersonalDetail() throws InterruptedException {
        boolean isShown = false;
        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 28, 7);

        logMessage("Employee Personal Detail screen is shown.");
        logScreenshotX();

        isShown = true;
        return isShown;
    }

    public static boolean addLeaveInEmployeeLeaveDetailScreen(String leaveType, String leaveClass, String grantImmediately, String entitlementDate) throws InterruptedException {
        boolean isAdded = false;

        displayEmployee_LeaveDetails();

        AutoItX x = new AutoItX();
        x.controlFocus("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
        x.controlClick("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
        x.sleep(2000);
        logMessage("Add Leave button is clicked.");

        if (leaveType != null) {
            x.controlFocus("Add New Employee Leave", "", "[CLASS:TevComboBox; INSTANCE:1]");
            x.send(leaveType);
            x.send("{TAB}", false);
            x.sleep(4000);
            logMessage("Leave Type '" + leaveType + "' is input.");
        }

        if (leaveClass != null) {
            switch (leaveType) {
                case "Sick Leave":
                    x.controlFocus("Add New Employee Leave", "", "[CLASS:TevIPSickLeaveLookupCombo; INSTANCE:1]");
                    x.send(leaveClass);
                    x.send("{TAB}", false);
                    x.sleep(4000);

                    break;

                case "Long Service Leave":
                    x.controlFocus("Add New Employee Leave", "", "[CLASS:TevIPLongServiceLeaveLookupCombo; INSTANCE:1]");
                    x.send(leaveClass);
                    x.send("{TAB}", false);
                    x.sleep(4000);
            }
            logMessage("Leave Class '" + leaveClass + "' is input.");
        }

        if (grantImmediately != null) {
            if (grantImmediately.equals("1")) {
                x.controlFocus("Add New Employee Leave", "", "&Grant Immediately");
                x.send("{SPACE}", false);
                x.sleep(4000);
                logMessage("Grant Immediately is checked.");
            }
        }

        if (entitlementDate != null) {
            x.controlFocus("Add New Employee Leave", "", "[CLASS:TevDateEdit; INSTANCE:4]");
            x.send(entitlementDate);
            x.send("{TAB}", false);
            x.sleep(4000);
            logMessage("Entitlement Date '" + entitlementDate + "' is input.");
        }

        logMessage("Screenshot before click OK button.");
        logScreenshotX();

        x.controlFocus("Add New Employee Leave", "OK", "[CLASS:TButton; INSTANCE:3]");
        x.controlClick("Add New Employee Leave", "OK", "[CLASS:TButton; INSTANCE:3]");
        x.sleep(4000);

        logMessage("Leave Type '" + leaveType + "' is added.");
        logScreenshotX();
        isAdded = true;
        return isAdded;
    }

    public static void closeEmployeeDetailScreen() throws InterruptedException {
        boolean isClosed = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();
        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 28, 7);

        x.controlFocus("Edit Employee", "OK", "[CLASS:TButton; TEXT:OK]");
        x.controlClick("Edit Employee", "OK", "[CLASS:TButton; TEXT:OK]");

        x.sleep(15000);

        if (x.controlCommandIsVisible("Employee", "OK", "[CLASS:TButton; INSTANCE:1]")) {
            x.controlFocus("Employee", "OK", "[CLASS:TButton; INSTANCE:1]");
            logScreenshotX();
            logMessage("Extra message pops up.");
            x.controlClick("Employee", "OK", "[CLASS:TButton; INSTANCE:1]");
            logMessage("OK button is clicked.");
            x.controlFocus("Edit Employee", "Cancel", "[CLASS:TButton; TEXT:Cancel]");
            x.controlClick("Edit Employee", "Cancel", "[CLASS:TButton; TEXT:Cancel]");
            logMessage("Cancel button is click.");

            if (x.controlCommandIsVisible("Edit Employee", "&Yes", "[CLASS:TButton; TEXT:&Yes]")) {
                logMessage("Extra Message pops up.");
                logScreenshotX();
                x.controlFocus("Edit Employee", "&Yes", "[CLASS:TButton; TEXT:&Yes]");
                x.controlClick("Edit Employee", "&Yes", "[CLASS:TButton; TEXT:&Yes]");
                logMessage("Yes button is clicked.");
                logMessage("Employee Detail screen is closed without change made.");
            }

        } else {
            logMessage("Employee Detail screen is closed.");
        }

        logMessage("Employee Detail screen is closed.");
        logScreenshotX();
        isClosed = true;
        //return isClosed;
    }



    public static boolean editEmployee_RateDetail(String otherRate) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;
        displayEmployee_RateDetail();
        AutoItX x = new AutoItX();

        if (otherRate != null) {
            x.controlFocus("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:1]");
            x.controlClick("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:1]", "left", 2);
            x.send(otherRate);
            x.send("{TAB}", false);
            x.sleep(2000);
            logMessage("Other Rate '" + otherRate + "' is input.");
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean displayEmployee_RateDetail() throws InterruptedException {
        boolean isShown = false;
        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 16, 57);
        x.sleep(4000);

        logMessage("Employee Rate Detail screen is shown.");
        logScreenshotX();

        isShown = true;
        return isShown;
    }

    public static boolean addPublicHoliday(String code, String description, String location, String nameOfPublicHoliday, String dateOfPublicHoliday) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;
        displayPublicHolidayPlannerListScreen();
        AutoItX x = new AutoItX();

        x.controlFocus("Sage MicrOpay", "Maintenance", "[CLASS:TdxBarControl; TEXT:Maintenance]");
        x.controlClick("Sage MicrOpay", "Maintenance", "[CLASS:TdxBarControl; TEXT:Maintenance]", "left", 1, 26, 9);

        if (x.winWait("Add New Public Holiday Planner", "", 10)) {
            logMessage("Add New Public Holiday Planner dialogue is shown.");
            logScreenshotX();

            if (code != null) {
                x.controlFocus("Add New Public Holiday Planner", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                x.send(code);
                x.send("{TAB}", false);
                logMessage("Code '" + code + "' is input.");
            }

            if (description != null) {
                x.controlFocus("Add New Public Holiday Planner", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                x.send(description);
                x.send("{TAB}", false);
                logMessage("Description '" + description + "' is input.");
                x.sleep(3000);
            }

            if (location != null) {
                AutoITLib.selectMultiItemsInDropdownList("Add New Public Holiday Planner", "", "[CLASS:TCheckListPopup; INSTANCE:1]", location);
                logMessage("Location Items are added.");
            }

            if (nameOfPublicHoliday != null) {
                x.controlClick("Add New Public Holiday Planner", "Add", "[CLASS:TButton; TEXT:Add]");
                x.sleep(2000);
                logMessage("Add button is clicked.");

                x.send(nameOfPublicHoliday);
                x.send("{TAB}", false);

                if (dateOfPublicHoliday != null) {
                    if (dateOfPublicHoliday.contains(";")) {
                        dateOfPublicHoliday = SystemLibrary.getExpectedDate(dateOfPublicHoliday, null);
                    }

                    x.send(dateOfPublicHoliday);
                }
            }
            logMessage("Screenshot before click Add button.");
            logScreenshotX();

            x.controlFocus("Add New Public Holiday Planner", "OK", "[CLASS:TButton; TEXT:OK]");
            x.controlClick("Add New Public Holiday Planner", "OK", "[CLASS:TButton; TEXT:OK]");
            x.sleep(3000);
            logMessage("Add button is clicked.");
            logScreenshotX();

        } else {
            logError("Add Public Holiday Planner screen is NOT shown.");
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean click_Window(String title, int positionX, int positionY) throws InterruptedException {
        //Window must be activated first and on focus
        boolean isClicked = false;
        AutoItX x = new AutoItX();
        if (x.winWait(title, "", 5)) {
            int winPositionX = x.winGetPosX(title);
            int winPositionY = x.winGetPosY(title);
            int actualX = winPositionX + positionX;
            int actualY = winPositionY + positionY;

            x.mouseClick("left", actualX, actualY, 1, 0);
            Thread.sleep(2000);
            logMessage("Click windwow '" + title + "' in position " + String.valueOf(actualX) + ", " + String.valueOf(actualY));
            isClicked = true;
        } else {
            logError("Window is NOT found.");
        }
        return isClicked;
    }



    public static boolean selectMultiItemsInDropdownList_OLD(String title, String text, String controlID, String selectItem) throws InterruptedException {
        boolean isDone = false;
        AutoItX x = new AutoItX();

        x.winActivate(title);
        x.controlFocus(title, text, controlID);
        x.controlClick(title, text, controlID, "left", 1, 42, 8);
        x.sleep(2000);
        x.send("{SPACE}", false);
        int posX = x.controlGetPosX(title, text, controlID);
        System.out.println("posX=" + posX);
        int posY = x.controlGetPosY(title, text, controlID);
        System.out.println("posY=" + posY);
        int controlHeight = x.controlGetPosHeight(title, text, controlID);
        System.out.println("controlHeight=" + controlHeight);
        int controlWidth = x.controlGetPosWidth(title, text, controlID);
        System.out.println("controlWidth=" + controlWidth);

        int menu_Select_PosX = posX + controlWidth - 47;
        System.out.println(menu_Select_PosX);
        int menu_Select_PosY = posY + controlHeight + 35;
        System.out.println(menu_Select_PosY);

        click_Window(title, menu_Select_PosX, menu_Select_PosY);
        x.sleep(2000);

        int menu_SelectAll_PosX = 0;
        int menu_SelectAll_PosY = 0;

        if (selectItem != null) {
            switch (selectItem) {
                case "999999": //select all
                    menu_SelectAll_PosX = menu_Select_PosX;
                    menu_SelectAll_PosY = menu_Select_PosY + 30;
                    click_Window(title, menu_SelectAll_PosX, menu_SelectAll_PosY);
                    logMessage("Select All is Done.");
                    break;
                case "666666":
                    menu_SelectAll_PosX = menu_Select_PosX;
                    menu_SelectAll_PosY = menu_Select_PosY + 45;
                    click_Window(title, menu_SelectAll_PosX, menu_SelectAll_PosY);
                    logMessage("Unselect All is Done.");
                    break;
            }
        }

        isDone = true;
        return isDone;
    }

    public static void displayLeaveReasonListScreen() throws InterruptedException {
        searchMenu("Leave Reason", 1);
    }

    public static boolean addLeaveReason(String code, String description, String leaveType, String costAccount, String rateType, String limitType, String limitTypeDate, String maximumDay, String workersCompensation, String unpaidLeave) throws InterruptedException {
        boolean isAdded = false;
        int errorCounter = 0;
        displayLeaveReasonListScreen();
        AutoItX x = new AutoItX();

        x.controlFocus("Sage MicrOpay", "Maintenance", "[CLASS:TdxBarControl; TEXT:Maintenance]");
        x.controlClick("Sage MicrOpay", "Maintenance", "[CLASS:TdxBarControl; TEXT:Maintenance]", "left", 1, 26, 9);

        if (x.winWait("Add New Leave Reason", "", 5)) {
            x.winActivate("Add New Leave Reason", "");
            logMessage("Add Leave Reason screen is shown.");
            logScreenshotX();

            if (code != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevTextEdit; INSTANCE:3]");
                x.send(code);
                x.send("{TAB}", false);
                logMessage("Code '" + code + "' is input.");
            }

            if (description != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                x.send(description);
                x.send("{TAB}", false);
                logMessage("Description '" + description + "' is input.");
            }

            if (leaveType != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevComboBox; INSTANCE:1]");
                x.controlClick("Add New Leave Reason", "", "[CLASS:TevComboBox; INSTANCE:1]");
                x.send(leaveType);
                x.send("{TAB}", false);
                logMessage("Leave Type '" + leaveType + "' is input.");
            }

            if (costAccount != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                x.send(costAccount);
                x.send("{TAB}", false);
                logMessage("Cost Account '" + costAccount + "' is input.");
            }

            if (rateType != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevIPRateTypesLookupCombo; INSTANCE:1]");
                x.send(rateType);
                x.send("{TAB}", false);
                logMessage("Rate Type '" + rateType + "' is input.");
            }

            if (limitType != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevComboBox; INSTANCE:2]");
                x.send(limitType);
                x.send("{TAB}", false);
                logMessage("Limit Type '" + limitType + "' is input.");
            }

            if (limitTypeDate != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevDateEdit; INSTANCE:1]");
                x.send(limitTypeDate);
                x.send("{TAB}", false);
                logMessage("Limit Type Date '" + limitTypeDate + "' is input.");
            }

            if (maximumDay != null) {
                x.controlFocus("Add New Leave Reason", "", "[CLASS:TevIntEdit; INSTANCE:1]");
                x.controlClick("Add New Leave Reason", "", "[CLASS:TevIntEdit; INSTANCE:1]", "left", 2);
                x.send(maximumDay);
                x.send("{TAB}", false);
                logMessage("Maximum Day '" + maximumDay + "' is input.");
            }

            if (workersCompensation != null) {
                if (workersCompensation.equals("1")) {
                    x.controlFocus("Add New Leave Reason", "&Workers Compensation", "[CLASS:TevCheckBox; INSTANCE:2]");
                    x.send("{SPACE}", false);
                    logMessage("Workers Compensation is checked.");
                }

            }

            if (unpaidLeave != null) {
                if (unpaidLeave.equals("1")) {
                    x.controlFocus("Add New Leave Reason", "&Unpaid Leave", "[CLASS:TevCheckBox; INSTANCE:1]");
                    x.send("{SPACE}", false);
                    logMessage("Unpaid leave is checked.");
                }
            }

            logMessage("Screenshot before click Save button");
            logScreenshotX();

            x.controlFocus("Add New Leave Reason", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Add New Leave Reason", "OK", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(3000);
            logMessage("OK button is clicked.");
            logScreenshotX();


        } else {
            logError("Add New Leave Reason screen is NOT shown.");
            errorCounter++;
        }


        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }

    public static boolean display_UpdateLeaveEntitlementScreen() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();
        Thread.sleep(20000);
        x.winActivate("Sage MicrOpay");
        x.winWaitActive("Sage MicrOpay", "", 30);
        x.send("{ALT}t", false);
        Thread.sleep(2000);
        x.send("{RIGHT}{DOWN}{DOWN}{DOWN}{DOWN}{RIGHT}{ENTER}", false);
        Thread.sleep(5000);

        if (x.winWait("Update Leave Entitlements", "OK", 15) == true) {
            x.winActivate("Update Leave Entitlements", "");
            logMessage("Update Leave Entitlements window is shown.");
            logScreenshotX();

            Thread.sleep(2000);
            isShown = true;
        } else {
            logError("Update Leave Entitlements screen is NOT shown.");
        }


        isShown = true;
        return isShown;
    }


    public static boolean updateLeaveEntitlement(String payFrequency, String newEntitlementDate) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;
        AutoItX x = new AutoItX();

        display_UpdateLeaveEntitlementScreen();
        if (payFrequency != null) {
            if (!selectMultiItemsInDropdownList("Update Leave Entitlements", "", "[CLASS:TCheckListPopup; INSTANCE:1]", payFrequency))
                errorCounter++;
        }

        if (newEntitlementDate != null) {
            x.controlFocus("Update Leave Entitlements", "", "[CLASS:TcxGridSite; INSTANCE:1]");
            x.controlClick("Update Leave Entitlements", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 276, 27);
            x.send(newEntitlementDate);
            x.send("{TAB}", false);
            logMessage("New Entitlement Date '" + newEntitlementDate + "' is input.");
        }

        logMessage("Screenshot before click OK button.");
        logScreenshotX();

        x.controlFocus("Update Leave Entitlements", "OK", "[CLASS:TButton; INSTANCE:2]");
        x.controlClick("Update Leave Entitlements", "OK", "[CLASS:TButton; INSTANCE:2]");
        logMessage("OK button is clicked.");
        x.sleep(4000);
        String messageTest = x.controlGetText("Update Leave Entitlements", "", "[CLASS:TBrilliantRichEdit; INSTANCE:1]");
        logScreenshotX();
        logMessage("Proecess Log is below:");
        System.out.println(messageTest);

        x.controlFocus("Update Leave Entitlements", "Close", "[CLASS:TButton; INSTANCE:1]");
        x.controlClick("Update Leave Entitlements", "Close", "[CLASS:TButton; INSTANCE:1]");
        logMessage("Close button is clicked.");
        x.sleep(2000);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean clickControlInWindow_NEWInUse(String title, String text, String controlID, int controlPosX, int controlPosY) throws InterruptedException {
        boolean isClicked = false;
        int errorCounter = 0;

        AutoItX autoItX = new AutoItX();
        if (autoItX.winWait(title, "", 5)) {
            //autoItX.winActivate(title, text);
            int winX = autoItX.winGetPosX(title);
            logDebug("winX="+String.valueOf(winX));
            int winY = autoItX.winGetPosY(title);
            logDebug("winY="+String.valueOf(winY));

            int controlX=autoItX.controlGetPosX(title, text, controlID);
            logDebug("controlX="+String.valueOf(controlX));
            int controlY=autoItX.controlGetPosY(title, text, controlID);
            logDebug("controlY="+String.valueOf(controlY));

            int mouseX = winX + controlX+controlPosX;
            int mouseY = winY + controlY+controlPosY+23;
            autoItX.mouseClick("left", mouseX, mouseY);
            logMessage("Control '" + controlID + "' in window '" + title + "' is clicked at " + mouseX + " and " + mouseY + ".");
        } else {
            logError("Control '" + controlID + "' is in window '" + title + "' is NOT Found.");
            errorCounter++;
        }

        if (errorCounter == 0) isClicked = true;
        return isClicked;
    }

    public static boolean clickControlInWindow(String title, String text, String controlID, int controlPosX, int controlPosY) throws InterruptedException {
        boolean isClicked = false;
        int errorCounter = 0;

        AutoItX autoItX = new AutoItX();
        if (autoItX.winWait(title, "", 5)) {
            //autoItX.winActivate(title, text);
            int winX = autoItX.winGetPosX(title);
            int winY = autoItX.winGetPosY(title);

            int mouseX = winX + controlPosX;
            int mouseY = winY + controlPosY;
            autoItX.mouseClick("left", mouseX, mouseY);
            logMessage("Control '" + controlID + "' in window '" + title + "' is clicked at " + mouseX + " and " + mouseY + ".");
        } else {
            logError("Control '" + controlID + "' is in window '" + title + "' is NOT Found.");
            errorCounter++;
        }


        if (errorCounter == 0) isClicked = true;
        return isClicked;
    }

    public static boolean selectMultiItemsInDropdownList(String title, String text, String controlID, String items) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();

        x.winActivate(title);
        x.sleep(3000);
        x.controlFocus(title, text, controlID);
        x.sleep(3000);
        x.send("{SPACE}", false);
        logMessage("Dropdown list is shown.");
        logScreenshotX();

        int winX = x.winGetPosX(title);
        //logMessage("WinX="+winX);
        int winY = x.winGetPosY(title);
        //logMessage("WinY="+winY);

        int controlX = x.controlGetPosX(title, text, controlID);
        //logMessage("ControlX="+controlX);
        int controlY = x.controlGetPosY(title, text, controlID);
        //logMessage("ControlY="+controlY);
        int controlW = x.controlGetPosWidth(title, text, controlID);
        //logMessage("ControlWidth="+controlW);
        int controlH = x.controlGetPosHeight(title, text, controlID);
        //logMessage("ControlHeight="+controlH);

        int menu_Select_X = controlX + controlW - 55;
        int menu_Select_Y = 26 + controlY + controlH + controlH / 2;

        //logMessage("Menu_Select_X="+menu_Select_X);
        //logMessage("Menu_Select_Y="+menu_Select_Y);

        clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
        logMessage("Select menue is clicked.");
        logScreenshotX();

        if (items != null) {
            if (items.equals("999999")) {
                menu_Select_Y = menu_Select_Y + 21;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Select All is clicked.");
                logScreenshotX();
            } else if (items.equals("666666")) {
                menu_Select_Y = menu_Select_Y + 42;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Unselect All is clicked.");
                logScreenshotX();
            } else {
                String[] subItem = SystemLibrary.splitString(items, ";");
                menu_Select_Y = menu_Select_Y + 42;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Unselect All is clicked.");
                logScreenshotX();

                int totalItemsCount = subItem.length;
                logMessage("Total Item " + totalItemsCount + " to be selected.");
                String[] subSubItem = null;
                int currentOrderNumber = 0;
                String currentItem = null;
                for (int i = 0; i < totalItemsCount; i++) {
                    subSubItem = SystemLibrary.splitString(subItem[i], ":");
                    int orderNumber = Integer.parseInt(subSubItem[1]);
                    currentOrderNumber = orderNumber - currentOrderNumber;
                    logMessage("Order Number=" + orderNumber);
                    x.send("{Tab}", false);
                    x.sleep(1000);
                    x.send("{DOWN}", false);
                    x.sleep(1000);

                    for (int j = 1; j < currentOrderNumber; j++) {
                        x.send("{DOWN}", false);
                        x.sleep(1000);
                    }

                    x.send("{SPACE}", false);
                    x.sleep(1000);

                    x.send("{Tab}", false);

                    currentItem = x.controlGetText(title, text, controlID);
                    if (currentItem.contains(subSubItem[0])) {
                        logMessage("Item '" + subSubItem[0] + "' is selected.");
                    } else {
                        logError("Item '" + subSubItem[0] + "' is NOT selected.");
                        errorCounter++;
                    }
                }

                logScreenshotX();

            }
        } else {
            logError("Not item is selected.");
            errorCounter++;
        }

        x.controlClick(title, text, controlID);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean launchTPU() throws InterruptedException, IOException {
        boolean isLaunched = false;
        AutoItX x = new AutoItX();
        x.run("C:\\TestAutomationProject\\ESSRegTest\\TPU\\TPU.bat");

        if (x.winWait("Sage ESS Tenant Provisioning Utility", "", 15)) {
            logMessage("TPU Tool is launched.");
            isLaunched = true;
        } else {
            logError("TPU Tool is NOT launched.");
        }
        logScreenshotX();

        return isLaunched;
    }

    public static boolean exitTPU() throws InterruptedException {
        boolean isClosed = false;
        AutoItX x = new AutoItX();
        int attempt = 0;

        String tpuWinTitle = "Sage ESS Tenant Provisioning Utility";
        if (x.winWait(tpuWinTitle, "", 5)) {
            x.winActivate(tpuWinTitle);
            AutoITLib.click_Window(tpuWinTitle, 788, 15);
            x.sleep(2000);
            logMessage("Button - Close TPU is clicked.");
            attempt = 1;

            if (x.winWait(tpuWinTitle, "", 3)) {
                x.processClose("ESS TPU.exe");
                logWarning("Sage Micropay is terminated.");
                attempt = 2;
            } else {
                isClosed = true;
                logMessage("TPU Tool is closed.");
            }

        } else {
            logMessage("TPU tool is NOT launched.");
        }

        return isClosed;
    }


    public static String generateESSTenant(String testSerialNo) throws InterruptedException, IOException, UnsupportedFlavorException {
        String fileFullPathName = null;
        SoftAssert myAssert = new SoftAssert();
        AutoItX x = new AutoItX();
        AutoITLib.launchTPU();
        String tpuWinTitle = "Sage ESS Tenant Provisioning Utility";

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 223, 398);
        x.send("AutoTest");
        logMessage("Company Name 'AutoTest' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 219, 424);
        String customerID = "Test" + testSerialNo;
        x.send(customerID);
        logMessage("Customer ID '" + customerID + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 220, 503);
        x.send("Last");
        logMessage("Last Name 'Last' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 233, 532);
        String email = "testlast" + testSerialNo + "@sageautomation.com";
        x.send(email);
        logMessage("Email Address '" + email + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 233, 558);
        String lastNumber = testSerialNo.replace("sn", "");
        String phoneNumber = lastNumber + lastNumber + lastNumber;
        x.send(phoneNumber);
        logMessage("Phone Number '" + phoneNumber + "' is input.");

        logMessage("Screenshot before click Buton - Create ESS Customer.");
        logScreenshotX();

        SystemLibrary.clearClipboard();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 690, 589);
        x.sleep(25000);
        logMessage("Button - Create ESS Customer is clicked.");
        logScreenshotX();

        /////////////////////

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 676, 346);
        x.sleep(3000);
        logMessage("Button - Copy log to Sclipboard is clicked.");

        String currentClipboard = SystemLibrary.getClipboard();
        logMessage(currentClipboard);

        String fileName = "Test" + testSerialNo + "_" + getCurrentTimeAndDate() + ".txt";
        fileFullPathName = projectPath + "UserList\\" + fileName;
        createTextFile(fileFullPathName, currentClipboard);
        logMessage("Tenant detail is saved as '" + fileFullPathName + "'.");
        logMessage("Click here to access the Tenant Detail: '" + serverUrlAddress + "/userlist/" + fileName);

        /////////////// Backup old tenant file if existing ////////////
        moveFile(projectPath + "UserList\\TenantDetails.txt", projectPath + "UserList\\TenantDetails_Backup_" + getCurrentTimeAndDate() + ".txt");
        copyFile(fileFullPathName, projectPath + "UserList\\TenantDetails.txt");
        Thread.sleep(5000);

        exitTPU();
        return fileFullPathName;
    }

    public static boolean uploadCompanyLogoViaTPU(String testSerialNo) throws Exception {
        String logoFileFullPathName = "C:\\TestAutomationProject\\ESSRegTest\\DataSource\\TestAutoLogo.png";
        boolean isDone = false;
        int errorCounter = 0;

        String fileName="TenantDetails_"+testSerialNo+".txt";
        String tenantFileFullName=projectPath+"\\UserList\\"+fileName;
        String tenantKey=SystemLibrary.getValueFromListFile("Tenant Key", " = ", tenantFileFullName);
        String adminUsername=SystemLibrary.getValueFromListFile("Admin Login", " =", tenantFileFullName);
        String password=SystemLibrary.getValueFromListFile("Admin Password", " =", tenantFileFullName);

        launchTPU();

        AutoItX x = new AutoItX();

        String tpuWinTitle = "Sage ESS Tenant Provisioning Utility";

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 206, 64);
        x.sleep(3000);
        logMessage("Tab - Company Name and Log is clicked.");
        logScreenshotX();

        //////////////////
        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 272, 269);
        x.sleep(3000);
        x.send(tenantKey);
        logMessage("Tenant Key is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 272, 300);
        x.sleep(3000);
        x.send(adminUsername);
        logMessage("Admin Username is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 272, 328);
        x.sleep(3000);
        x.send(password);
        logMessage("Admin Password is input.");

        logScreenshotX();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 705, 362);
        x.sleep(3000);
        logMessage("Button - Validate is clicked.");

        String strPopupTilte = "Get Tenant Detail";
        if (x.winWait(strPopupTilte, "", 5)) {
            x.winActivate(strPopupTilte);
            if (x.winWait(strPopupTilte, "Validation is passed.", 15)) {
                logMessage("Validation is passed.");
            } else {
                logError("Invaldiate Tenanl informaiton.");
                errorCounter++;
            }
            logScreenshotX();

            x.controlFocus(strPopupTilte, "OK", "[CLASS:Button; INSTANCE:1]");
            x.controlClick(strPopupTilte, "OK", "[CLASS:Button; INSTANCE:1]");
            logMessage("OK button is cliicked.");
            x.sleep(3000);

            if (errorCounter == 0) {
                x.winActivate(tpuWinTitle);
                AutoITLib.click_Window(tpuWinTitle, 272, 397);
                x.sleep(3000);
                AutoITLib.doubleClick_Window(tpuWinTitle, 272, 397);
                x.send("AutoTest2");
                logMessage("AutoTest2 is input.");

                x.winActivate(tpuWinTitle);
                AutoITLib.click_Window(tpuWinTitle, 709, 431);
                x.sleep(3000);
                logMessage("Button - Update Company Name is clicked.");

                if (x.winWait("Company Name", "", 10)) {
                    if (x.winWait("Company Name", "Update company name is completed.", 5)) {
                        logMessage("Update company name is completed.");
                    } else {
                        logError("UPdate Company name is NOT completed.");
                        errorCounter++;
                    }
                    logScreenshotX();
                    x.controlFocus("Company Name", "OK", "[CLASS:Button; INSTANCE:1]");
                    x.controlClick("Company Name", "OK", "[CLASS:Button; INSTANCE:1]");
                    logMessage("OK button is clicked.");
                    x.sleep(2000);
                } else {
                    logError("Company Name is NOT updated.");
                    errorCounter++;
                }

                x.winActivate(tpuWinTitle);
                click_Window(tpuWinTitle, 704, 488);
                x.sleep(3000);
                logMessage("Button - Upload Company Logo is clicked.");

                if (x.winWait("Open", "", 10)) {
                    x.controlFocus("Open", "", "[CLASS:Edit; INSTANCE:1]");
                    x.controlSend("Open", "", "[CLASS:Edit; INSTANCE:1]", logoFileFullPathName);
                    logMessage("Company Logo file path '" + logoFileFullPathName + "' is input.");
                    logScreenshotX();
                    x.controlFocus("Open", "&Open", "[CLASS:Button; INSTANCE:1]");
                    x.controlClick("Open", "&Open", "[CLASS:Button; INSTANCE:1]");
                    logMessage("Button - Open is clicked.");
                    x.sleep(2000);

                    if (x.winWait("Company logo", "", 10)) {
                        if (x.winWait("Company logo", "Company logo has been uploaded successfully.", 10)) {
                            logMessage("Company logo has been uploaded successfully.");
                        } else {
                            logError("Company logo is NOT uploaded.");
                            errorCounter++;
                        }
                        logScreenshotX();

                        x.controlFocus("Company logo", "OK", "[CLASS:Button; INSTANCE:1]");
                        x.controlClick("Company logo", "OK", "[CLASS:Button; INSTANCE:1]");
                        x.sleep(3000);
                        logMessage("OK button is clicked.");
                        logScreenshotX();

                    } else {
                        logError("Failed Updating Company Logo.");
                    }
                } else {
                    logError("Open file screen is NOT shown.");
                    errorCounter++;
                }

                //if (x.winWait(""))
            }


        } else {
            errorCounter++;
            logError("Validation failed.");
        }

        if (errorCounter == 0) isDone = true;

        exitTPU();
        return isDone;
    }

    public static boolean doubleClick_Window(String title, int positionX, int positionY) throws InterruptedException {
        //Window must be activated first and on focus
        boolean isClicked = false;
        AutoItX x = new AutoItX();
        if (x.winWait(title, "", 5)) {
            int winPositionX = x.winGetPosX(title);
            int winPositionY = x.winGetPosY(title);
            int actualX = winPositionX + positionX;
            int actualY = winPositionY + positionY;

            x.mouseClick("left", actualX, actualY, 2, 0);

            Thread.sleep(2000);
            logMessage("Double Click windwow '" + title + "' in position " + String.valueOf(actualX) + ", " + String.valueOf(actualY));
            isClicked = true;
        } else {
            logError("Window is NOT found.");
        }
        return isClicked;
    }

    public static boolean tripleClick_Window(String title, int positionX, int positionY) throws InterruptedException {
        //Window must be activated first and on focus
        boolean isClicked = false;
        AutoItX x = new AutoItX();
        if (x.winWait(title, "", 5)) {
            int winPositionX = x.winGetPosX(title);
            int winPositionY = x.winGetPosY(title);
            int actualX = winPositionX + positionX;
            int actualY = winPositionY + positionY;

            x.mouseClick("left", actualX, actualY, 3, 0);

            Thread.sleep(2000);
            logMessage("Triple Click windwow '" + title + "' in position " + String.valueOf(actualX) + ", " + String.valueOf(actualY));
            isClicked = true;
        } else {
            logError("Window is NOT found.");
        }
        return isClicked;
    }

    public static boolean createAdminExternalUserViaTPU(WiniumDriver driver) throws Exception {

        boolean isDone = false;
        int errorCounter = 0;

        String tenantKey = GeneralBasic.getTenantKey_FromTenantDetails();
        String testSerialNumber = GeneralBasic.getTestSerailNumber_Main(10, 10);

        String username = "External_" + testSerialNumber;
        String firstName = "Harry_" + testSerialNumber;
        String lastName = "O'Brien_" + testSerialNumber;
        String email = "HarryO_" + testSerialNumber + "@sageautomation.com";

        launchTPU();

        AutoItX x = new AutoItX();

        String tpuWinTitle = "Sage ESS Tenant Provisioning Utility";

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 666, 57);
        x.sleep(3000);
        logMessage("Tab - Admin User Creatation tab is clicked.");
        logScreenshotX();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 369);
        x.send(tenantKey);
        logMessage("Tenant Key '" + tenantKey + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 400);
        x.send(username);
        logMessage("UserName '" + username + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 423);
        x.send(firstName);
        logMessage("First Name '" + firstName + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 453);
        x.send(lastName);
        logMessage("Last Name '" + lastName + "' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 484);
        x.send(email);
        logMessage("Email '" + email + "' is input.");

        logMessage("Screenshot before click Create button.");
        logScreenshotX();

        SystemLibrary.clearClipboard();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 731, 508);
        x.sleep(3000);
        logMessage("Button - Create is clicked.");
        logScreenshotX();

        if (x.winWait("ESS TPU", "Error occured. Please check the log", 5)) {
            x.controlFocus("ESS TPU", "OK", "[CLASS:Button; INSTANCE:1]");
            x.controlClick("ESS TPU", "OK", "[CLASS:Button; INSTANCE:1]");
            x.sleep(5000);
            logMessage("OK button is clicked.");

        } else {

        }

        Thread.sleep(5000);


        String currentClipboard = SystemLibrary.getClipboard();
        logMessage(currentClipboard);

        String fileName = "Test_ExtraAdminUser" + testSerialNumber + "_" + getCurrentTimeAndDate() + ".txt";
        String fileFullPathName = projectPath + "UserList\\" + fileName;
        createTextFile(fileFullPathName, currentClipboard);
        logMessage("Extra Admin User detail is saved as '" + fileFullPathName + "'.");
        logMessage("Click here to access the Tenant Detail: '" + serverUrlAddress + "/userlist/" + fileName);


        if (errorCounter == 0) isDone = true;

        //exitTPU();
        return isDone;
    }

    public static boolean drag_Window(String title, int positionX, int positionY, int toPositionX, int toPositionY) throws InterruptedException {
        //Window must be activated first and on focus
        boolean isClicked = false;
        AutoItX x = new AutoItX();
        if (x.winWait(title, "", 5)) {
            int winPositionX = x.winGetPosX(title);
            int winPositionY = x.winGetPosY(title);
            int actualX = winPositionX + positionX;
            int actualY = winPositionY + positionY;
            int toActualX = winPositionX + toPositionX;
            int toActualY = winPositionX + toPositionY;

            x.mouseClickDrag("left", actualX, actualY, toActualX, toActualY, 1);
            Thread.sleep(2000);
            isClicked = true;
        } else {
            logError("Window is NOT found.");
        }
        return isClicked;
    }

    public static boolean validateEmployeePersonalDetailsScreen(String firstName, String middleName, String lastName, String preferredName, String itemToBeValidated, String storeFileName, String isUpdate, String isCompare, String payrollDBName, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        displayEmployeeDetailsScreen(firstName, lastName, payrollDBName);
        displayEmployee_PersonalDetail();
        String[] itemList = SystemLibrary.splitString(itemToBeValidated, ";");

        int itemTotalCount = itemList.length;
        String currentItem = "";
        String stringToBeValidated = "";

        AutoItX x = new AutoItX();


        for (int i = 0; i < itemTotalCount; i++) {
            currentItem = itemList[i];
            if (currentItem.equals("Contact Details:Telephone")) {
                x.controlFocus("Edit Employee", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                stringToBeValidated = stringToBeValidated + currentItem + ": " + x.controlGetText("Edit Employee", "", "[CLASS:TevTextEdit; INSTANCE:2]") + "\n";
            }
            if (currentItem.equals("Contact Details:Mobile Phone")) {
                x.controlFocus("Edit Employee", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:2]");
                stringToBeValidated = stringToBeValidated + currentItem + ": " + x.controlGetText("Edit Employee", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:2]");
            }

        }
        if (!validateStringFile(stringToBeValidated, storeFileName, isUpdate, isCompare, emailDomainName, testSerialNo)) errorCounter++;

        x.controlClick("Edit Employee", "Cancel", "[CLASS:TButton; INSTANCE:3]");
        Thread.sleep(3000);
        logMessage("Cancel button is clicked.");

        logScreenshotX();


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean addEmployee_AddsDeds(String firstName, String middleName, String lastName, String preferredNamem, String addsDedsCode, String useDefault, String amountPerPayOrRate, String standardOrUnit, String addsDedsCostAccount, String membershipNumber, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        //String employeeCode = DBManage.getEmployeeCodeFromPayrollDB(serverName, payrollDBName,  firstName, lastName);
        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_AddsDeds();

            AutoItX x = new AutoItX();
            x.controlFocus("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
            x.controlClick("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
            Thread.sleep(4000);
            logMessage("Add button is clicked.");
            logScreenshotX();

            if (addsDedsCode != null) {
                x.controlFocus("Add New Employee Additions / Deductions", "", "[CLASS:TevIPAdditionsDeductionsLookupCombo; INSTANCE:1]");
                x.send(addsDedsCode);
                x.send("{Tab}", false);
                Thread.sleep(3000);
                logMessage("Adds Deds Code '" + addsDedsCode + "' is input.");
            }

            if (useDefault != null) {
                if (useDefault.equals("2")) {
                    x.controlFocus("Add New Employee Additions / Deductions", "&Use Default", "[CLASS:TevCheckBox; INSTANCE:1]");
                    AutoITLib.clickControlInWindow("Add New Employee Additions / Deductions", "&Use Default", "[CLASS:TevCheckBox; INSTANCE:1]", 140, 137);
                    Thread.sleep(3000);
                    logMessage("Use Default is unchecked.");
                }
            }

            if (amountPerPayOrRate != null) {
                x.controlFocus("Add New Employee Additions / Deductions", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:3]");
                x.send(amountPerPayOrRate);
                x.send("{Tab}", false);
                Thread.sleep(3000);
                logMessage("Amount '" + amountPerPayOrRate + "' is input.");
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            x.controlFocus("Add New Employee Additions / Deductions", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Add New Employee Additions / Deductions", "OK", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(4000);
            logMessage("OK button is clicked. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayEmployee_AddsDeds() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 55, 115);
        x.sleep(4000);

        logMessage("Employee Adds Deds screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean addCostAccount(String position, String state, String description, String department) throws InterruptedException {
        boolean isAdded = false;
        int errorCounter = 0;
        displayCostAccountListScreen();
        AutoItX x = new AutoItX();

        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.send("{Ins}", false);
        Thread.sleep(4000);
        logMessage("Insert key is press");
        x.winSetState("Add New Cost Account", "", SW_MAXIMIZE);
        logScreenshotX();

        if (position != null) {
            if (position.equals("1")) {
                x.controlFocus("Add New Cost Account", "", "[CLASS:TevIPCostAccountStructureLookupCombo; INSTANCE:1]");
                x.send(position);
                x.send("{Tab}", false);
                Thread.sleep(3000);
                logMessage("Position '" + position + "' is input.");

                if (state != null) {
                    x.controlFocus("Add New Cost Account", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:2]");
                    x.send(state);
                    x.send("{Tab}", false);
                    Thread.sleep(3000);
                    logMessage("State '" + state + "' is input.");
                }

                if (description != null) {
                    x.controlFocus("Add New Cost Account", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
                    x.send(description);
                    x.send("{Tab}", false);
                    Thread.sleep(3000);
                    logMessage("Description '" + description + "' is input.");
                }
            } else if (position.equals("2")) {
                x.controlFocus("Add New Cost Account", "", "[CLASS:TevIPCostAccountStructureLookupCombo; INSTANCE:1]");
                x.send(position);
                x.send("{Tab}", false);
                Thread.sleep(3000);
                logMessage("Position '" + position + "' is input.");

                if (state != null) {
                    x.controlFocus("Add New Cost Account", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]");
                    x.send(state);
                    x.send("{Tab}", false);
                    Thread.sleep(3000);
                    logMessage("State '" + state + "' is input.");
                }

                if (department != null) {
                    x.controlFocus("Add New Cost Account", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:2]");
                    x.send(department);
                    x.send("{Tab}", false);
                    Thread.sleep(3000);
                    logMessage("Department '" + department + "' is input.");
                }

                if (description != null) {
                    x.controlFocus("Add New Cost Account", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
                    x.send(description);
                    x.send("{Tab}", false);
                    Thread.sleep(3000);
                    logMessage("Descrition '" + description + "' is input.");
                }
            }

        }

        x.winSetState("Add New Cost Account", "", SW_SHOWDEFAULT);

        logMessage("Screenshot before click OK button");
        logScreenshotX();

        x.controlFocus("Add New Cost Account", "OK", "[CLASS:TButton; INSTANCE:2]");
        x.controlClick("Add New Cost Account", "OK", "[CLASS:TButton; INSTANCE:2]");
        Thread.sleep(3000);
        logMessage("OK button is clicked.");
        logScreenshotX();

        logMessage("Complete adding cost account.");
        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }

    public static void displayCostAccountListScreen() throws InterruptedException {
        searchMenu("Cost Account", 1);
    }

    public static boolean addEmployee_CostAccount(String firstName, String middleName, String lastName, String preferredNamem, String defaultCostAccount, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_CostAccount();

            AutoItX x = new AutoItX();
            x.controlFocus("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:1]");
            x.send(defaultCostAccount);
            x.send("{tab}", false);
            Thread.sleep(3000);
            logMessage("Defaut Cost Account '" + defaultCostAccount + "' is input.");
            logScreenshotX();

            x.controlFocus("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:10]");
            x.controlClick("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:10]");
            Thread.sleep(3000);
            logMessage("OK button is clicked.");

            x.controlFocus("Edit Employee", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Edit Employee", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(3000);
            logMessage("OK to Update Cost Account button is clicked.");
            logScreenshotX();


        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayEmployee_CostAccount() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 55, 100);
        x.sleep(4000);

        logMessage("Employee Cost Account screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean performLeaveProcessing(String payFrequency, String isPreview, String isProcessing, String storeFileName, String isUpdate, String isCompare, String expectedContent, String description, String itemNameOnHold, String existingTransactions, String bypassStandardAddsDeds, String bypassBankSplits, String addBalanceOfHours, String storeFileName_AuditReport) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;
        boolean isContinue=true;

        displayLeaveProcessingScreen();
        AutoItX x = new AutoItX();
        if (payFrequency != null) {
            x.controlFocus("Leave Processing", "", "[CLASS:TevComboInnerEdit; INSTANCE:1]");
            x.send(payFrequency);
            x.send("{tab}", false);
            Thread.sleep(3000);
            logMessage("Pay Frequency '" + payFrequency + "' is input.");
            logScreenshotX();
        }

        if (isPreview != null) {
            if (isPreview.equals("1")) {  //Preview Only

                x.controlFocus("Leave Processing", "&Preview", "[CLASS:TButton; INSTANCE:2]");
                x.controlClick("Leave Processing", "&Preview", "[CLASS:TButton; INSTANCE:2]");
                Thread.sleep(10000);
                logMessage("Preview button is clicked.");
                logScreenshotX();

                if (x.controlCommandIsEnabled("Leave Processing", "Save Grid", "[CLASS:TButton; INSTANCE:5]")){
                    //////////////////////////////
                    String fileName = "LeaveListBeforeProcess_" + getCurrentTimeAndDate() + ".txt";
                    String fileFullPathName = workingFilePath + fileName;

                    if (storeFileName != null) {
                        x.controlFocus("Leave Processing", "Save Grid", "[CLASS:TButton; INSTANCE:5]");
                        x.controlClick("Leave Processing", "Save Grid", "[CLASS:TButton; INSTANCE:5]");
                        Thread.sleep(3000);
                        logMessage("Save Grid button is clicked.");
                        logScreenshotX();

                        x.controlFocus("Save Grid to File", "", "[CLASS:ComboBox; INSTANCE:2]");
                        x.controlClick("Save Grid to File", "", "[CLASS:ComboBox; INSTANCE:2]");
                        Thread.sleep(3000);
                        x.send("A");
                        Thread.sleep(2000);
                        x.send("{Tab}", false);

                        x.controlFocus("Save Grid to File", "", "[CLASS:Edit; INSTANCE:1]");
                        x.send(fileFullPathName);
                        x.send("{Tab}", false);
                        logMessage("File path is input.");
                        logScreenshotX();

                        x.controlFocus("Save Grid to File", "&Save", "[CLASS:Button; INSTANCE:2]");
                        x.controlClick("Save Grid to File", "&Save", "[CLASS:Button; INSTANCE:2]");
                        Thread.sleep(3000);
                        logMessage("Save button is clicked.");
                        logScreenshotX();
                        logMessage("File is saved as '" + fileFullPathName + "'.");
                        logMessage("Click here to access the file: " + serverUrlAddress + "TestLog/WorkingFile/" + fileName);

                        x.controlFocus("Leave Processing", "&No", "[CLASS:TButton; INSTANCE:1]");
                        x.controlClick("Leave Processing", "&No", "[CLASS:TButton; INSTANCE:1]");
                        Thread.sleep(3000);
                        logMessage("Review - NO button is clicked.");
                        logScreenshotX();

                        if (!SystemLibrary.updateAndValidateStoreStringFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent))
                            errorCounter++;

                    }
                    //////
                }else{
                    logWarning("No Leave Process item is lised.");
                    errorCounter++;
                    isContinue=false;
                }
            }
        }


        if ((isProcessing!= null)&&(isContinue)) {
            if (itemNameOnHold != null) {
                if (!selectItemOnHoldFromLeaveProcessingScreen(itemNameOnHold)) errorCounter++;
            }
            if (description != null) {
                x.controlFocus("Leave Processing", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
                x.controlSend("Leave Processing", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]", description);
                x.send("{TAB}", false);
                Thread.sleep(3000);
                logMessage("Description '" + description + "' is input.");
                logScreenshotX();
            }
            if (existingTransactions != null) {
                x.controlFocus("Leave Processing", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]");
                x.controlSend("Leave Processing", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", existingTransactions);
                x.send("{TAB}", false);
                Thread.sleep(3000);
                logMessage("Existing Transaction '" + existingTransactions + "' is input.");
                logScreenshotX();
            }
            if (bypassStandardAddsDeds != null) {
                if (bypassStandardAddsDeds.equals("1")) {
                    x.controlFocus("Leave Processing", "Bypass Standard Additions and Deductions", "[CLASS:TevCheckBox; INSTANCE:4]");
                    x.controlSend("Leave Processing", "Bypass Standard Additions and Deductions", "[CLASS:TevCheckBox; INSTANCE:4]", "{SPACE}", false);
                    x.send("{TAB}", false);
                    Thread.sleep(3000);
                    logMessage("Bypass Standard Adds and Deds is checked.");
                    logScreenshotX();
                }
            }

            if (bypassBankSplits != null) {
                if (bypassBankSplits.equals("1")) {
                    x.controlFocus("Leave Processing", "Bypass Bank Splits", "[CLASS:TevCheckBox; INSTANCE:3]");
                    x.controlSend("Leave Processing", "Bypass Bank Splits", "[CLASS:TevCheckBox; INSTANCE:3]", "{SPACE}", false);
                    x.send("{TAB}", false);
                    Thread.sleep(3000);
                    logMessage("Bypass Bank Splits is checked.");
                    logScreenshotX();
                }
            }

            if (addBalanceOfHours != null) {
                if (addBalanceOfHours.equals("1")) {
                    x.controlFocus("Leave Processing", "Add Balance of Hours", "[CLASS:TevCheckBox; INSTANCE:2]");
                    x.controlSend("Leave Processing", "Add Balance of Hours", "[CLASS:TevCheckBox; INSTANCE:2]", "{SPACE}", false);
                    x.send("{TAB}", false);
                    Thread.sleep(3000);
                    logMessage("Add Balance of Hours is checked.");
                    logScreenshotX();
                }
            }

            x.controlFocus("Leave Processing", "Process", "[CLASS:TButton; INSTANCE:4]");
            x.controlClick("Leave Processing", "Process", "[CLASS:TButton; INSTANCE:4]");
            Thread.sleep(10000);
            logMessage("Process Button is clicked.");
            logScreenshotX();

            if (x.winWait("Print Preview", "", 10)) {
                logMessage("Print Preview screen is shown.");
                x.winSetState("Print Preview", "", SW_MAXIMIZE);
                Thread.sleep(3000);
                logScreenshotX();

                if (storeFileName_AuditReport != null) {
                    String fileName = "LeaveProcessingAuditReport_" + getCurrentTimeAndDate() + ".pdf";
                    String fileFullPathName = workingFilePath + fileName;

                    x.controlFocus("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]");
                    AutoITLib.clickControlInWindow("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]", 25, 40);
                    Thread.sleep(3000);
                    logMessage("Print button is clicked in Print Preview screen.");
                    logScreenshotX();

                    if (x.winWait("Print", "", 10)) {
                        x.controlFocus("Print", "cbxPrintToFile", "[CLASS:TCheckBox; INSTANCE:4]");
                        x.controlClick("Print", "cbxPrintToFile", "[CLASS:TCheckBox; INSTANCE:4]");
                        Thread.sleep(3000);
                        logMessage("PDF file is checked.");
                        logScreenshotX();

                        x.controlFocus("Print", "", "[CLASS:TPanel; INSTANCE:4]");
                        Thread.sleep(3000);
                        x.controlClick("Print", "", "[CLASS:TPanel; INSTANCE:4]", "left", 1, 461, 59);
                        logMessage("Select File button is clicked.");
                        logScreenshotX();

                        x.winWait("Save As", "", 10);
                        x.winActivate("Save As");
                        x.controlFocus("Save As", "", "[CLASS:Edit; INSTANCE:1]");
                        Thread.sleep(3000);
                        x.controlSend("Save As", "", "[CLASS:Edit; INSTANCE:1]", fileFullPathName);
                        x.send("{TAB}", false);
                        logMessage("File full path name is input.");
                        logScreenshotX();

                        x.controlFocus("Save As", "&Save", "[CLASS:Button; INSTANCE:2]");
                        x.controlClick("Save As", "&Save", "[CLASS:Button; INSTANCE:2]");
                        Thread.sleep(10000);
                        logMessage("Save button is clicked.");
                        logScreenshotX();
                        logMessage("File is save as '" + fileFullPathName + "'");


                        x.controlFocus("Print", "OK", "[CLASS:TButton; INSTANCE:3]");
                        x.controlClick("Print", "OK", "[CLASS:TButton; INSTANCE:3]");
                        Thread.sleep(10000);
                        logMessage("OK button is clicked.");
                        logScreenshotX();

                        x.controlFocus("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]");
                        Thread.sleep(3000);
                        AutoITLib.clickControlInWindow("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]", 470, 38);
                        Thread.sleep(3000);
                        logMessage("Print Preview close button is clicked.");
                        logScreenshotX();

                        if (!SystemLibrary.updateAndValidateStorePDFFile(isUpdate, isCompare, fileFullPathName, storeFileName_AuditReport, expectedContent))
                            errorCounter++;

                    } else {
                        logError("Printer screen is NOT shown.");
                        errorCounter++;
                    }

                }
            } else {
                logError("Print Preview screen is NOT shown.");
                errorCounter++;
            }

        }

        x.controlFocus("Leave Processing", "Close", "[CLASS:TButton; INSTANCE:1]");
        x.controlClick("Leave Processing", "Close", "[CLASS:TButton; INSTANCE:1]");
        Thread.sleep(3000);
        logMessage("Leave Processing Close button is clicked.");
        logScreenshotX();

        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayLeaveProcessingScreen() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("Leave Processing", 1);
        if (x.winWait("Leave Processing", "", 10)) {
            logMessage("Leave Processing screen is shown.");
            x.winSetState("Leave Processing", "", SW_MAXIMIZE);
            Thread.sleep(3000);
            isShown = true;
        } else {
            logError("Leave Processing screen is NOT shown.");
        }
        return isShown;
    }


    public static boolean selectItemOnHoldFromLeaveProcessingScreen(String itemNameOnHold) throws Exception {
        //Display Leave Processing screen first.
        //itemNameOnHold = "EMP17:Sharon:300.00;CUMMINGS:EMP12:100.00";
        //String itemNameOnHold = "EMP17:Sharon:300.00";
        boolean isDone = false;
        AutoItX x = new AutoItX();
        boolean isItemFound = false;
        int itemFoundCount = 0;
        String[] itemList = SystemLibrary.splitString(itemNameOnHold, ";");
        int totalRownCount = itemList.length;
        String subRow = null;

        for (int i = 0; i < totalRownCount; i++) {
            SystemLibrary.clearClipboard();
            int tableRowNumber = 1;
            isItemFound = false;
            x.controlFocus("Leave Processing", "", "[CLASS:TcxGridSite; INSTANCE:1]");
            x.controlClick("Leave Processing", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 71, 60);
            Thread.sleep(3000);
            logMessage("The first row of table is Highlighted.");
            x.controlSend("Leave Processing", "", "[CLASS:TcxGridSite; INSTANCE:1]", "^c");
            Thread.sleep(5000);
            logMessage("Control C is clicked.");
            String originalRow = null;
            String currentRow = SystemLibrary.getClipboard();
            originalRow = currentRow;

            subRow = itemList[i];
            String[] subList = SystemLibrary.splitString(subRow, ":");
            if ((currentRow.contains(subList[0]) && currentRow.contains(subList[1]) && currentRow.contains(subList[2]))) {
                x.send("{SPACE}", false);
                logMessage("Content is found in Row " + String.valueOf(tableRowNumber) + " and 'On Hold' is clicked.");
                logScreenshotX();
                itemFoundCount++;
                isItemFound = true;
                break;
            } else {
                while (!isItemFound) {
                    tableRowNumber++;
                    SystemLibrary.clearClipboard();
                    x.controlFocus("Leave Processing", "", "[CLASS:TcxGridSite; INSTANCE:1]");
                    x.send("{DOWN}", false);
                    Thread.sleep(3000);
                    x.controlSend("Leave Processing", "", "[CLASS:TcxGridSite; INSTANCE:1]", "^c");
                    Thread.sleep(5000);
                    logMessage("Ctrol C is clicked.");
                    currentRow = SystemLibrary.getClipboard();

                    if (originalRow.equals(currentRow)) {
                        logMessage("End of table. Item '" + subList[0] + " " + subList[1] + " " + subList[2] + "' is NOT found. ");
                        break;
                    } else {
                        originalRow = currentRow;

                        if ((currentRow.contains(subList[0]) && currentRow.contains(subList[1]) && currentRow.contains(subList[2]))) {
                            x.send("{SPACE}", false);
                            logMessage("Content is found in Row " + String.valueOf(tableRowNumber) + " and 'On Hold' is clicked.");
                            logScreenshotX();
                            isItemFound = true;
                            itemFoundCount++;
                            break;
                        }

                    }


                }
            }
        }
        logMessage("Totlal " + itemFoundCount + " items found.");

        if (totalRownCount == itemFoundCount) isDone = true;
        return isDone;
    }

    public static boolean printTransactionReport(String payrollCompany, String location, String paypoint, String fromEmployee, String toEmployee, String payFrequency, String periodEndDay, String transactionType, String includeStandardTransaction, String printJobCode, String printDateWorked, String storeFileName, String isUpdate, String isCompare, String expectedContent) throws InterruptedException, IOException {
        boolean isPassed = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();

        if (display_PrintTransactionReportScreen()) {


            if (fromEmployee != null) {
                x.controlFocus("Report -- Transaction Report", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]");
                x.controlSend("Report -- Transaction Report", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]", fromEmployee);
                x.send("{TAB}", false);
                logMessage("From Employee '" + fromEmployee + "' is input.");
                logScreenshotX();
            }

            if (toEmployee != null) {
                x.controlFocus("Report -- Transaction Report", "", "[CLASS:TevComboInnerEdit; INSTANCE:1]");
                x.controlSend("Report -- Transaction Report", "", "[CLASS:TevComboInnerEdit; INSTANCE:1]", toEmployee);
                x.send("{TAB}", false);
                logMessage("To Employee '" + toEmployee + "' is input.");
                logScreenshotX();
            }

            logMessage("Screenshot before click Print button.");
            logScreenshotX();
            x.controlClick("Report -- Transaction Report", "Print", "[CLASS:TButton; INSTANCE:4]");
            Thread.sleep(3000);
            logMessage("Print button is clicked.");
            logScreenshotX();


            String fileName = "TransactionReport_" + getCurrentTimeAndDate() + ".pdf";
            String fileFullPathName = workingFilePath + fileName;

            ////////////
            if (!print_File(fileFullPathName)) {
                errorCounter++;
            }else{
                String sReportContent = SystemLibrary.getStringFromPDFFile(fileFullPathName);
                SystemLibrary.updateAndValidateStorePDFFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent);

            }

            //////


            x.controlFocus("Report -- Transaction Report", "Close", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Report -- Transaction Report", "Close", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(2000);
            logMessage("Print Transaction Report screen is closed.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean display_PrintTransactionReportScreen() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("transaction report", 1);
        if (x.winWait("Report -- Transaction Report", "", 10)) {
            logMessage("Report -- Transaction Report screen is shown.");
            //x.winSetState("Leave Processing", "", SW_MAXIMIZE);
            Thread.sleep(3000);
            isShown = true;
        } else {
            logError("Report -- Transaction Report screen is NOT shown.");
        }
        return isShown;
    }

    public static boolean display_Screen(String searchText, String winTitle, int order) throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu(searchText, order);
        if (x.winWait(winTitle, "", 10)) {
            logMessage(winTitle + " screen is shown.");
            //x.winSetState("Leave Processing", "", SW_MAXIMIZE);
            Thread.sleep(4000);
            isShown = true;
        } else {
            logError(winTitle + " screen is NOT shown.");
        }
        return isShown;
    }


    public static boolean processPayAdvice(String payrollCompany, String location, String payPoint, String fromEmployee, String toEmployee, String deliveryMethod, String payFrequency, String periodEndDate, String adviceDate, String payFrequencyStartDate, String payFrequencyEndDate, String storeFileName, String isUpdate, String isCompare, String expectedContent, String isReprint) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (display_Screen("Transactions > Pay Advice", "Report -- Pay Advice", 1)) {
            Thread.sleep(3000);
            AutoItX x = new AutoItX();

            if (fromEmployee!=null) {
                x.controlFocus("Report -- Pay Advice", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:2]");
                x.controlClick("Report -- Pay Advice", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:2]");
                x.send(fromEmployee);
                Thread.sleep(3000);
                x.send("{TAB}", false);
                logMessage("From Employee '" + fromEmployee + "' is input.");
            }

            if (toEmployee!=null) {
                x.controlFocus("Report -- Pay Advice", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]");
                x.controlClick("Report -- Pay Advice", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]");
                x.send(toEmployee);
                Thread.sleep(3000);
                x.send("{TAB}", false);
                logMessage("To Employee '" + toEmployee + "' is input.");
            }

            if (deliveryMethod != null) {
                selectMultiItemsInDropdownList2("Report -- Pay Advice", "", "[CLASS:TcxCustomDropDownInnerEdit; INSTANCE:1]", deliveryMethod);
            }

            if (payFrequency != null) {
                selectMultiItemsInDropdownList2("Report -- Pay Advice", "", "[CLASS:TcxCustomDropDownInnerEdit; INSTANCE:2]", payFrequency);
            }

            if (isReprint != null) {
                if (isReprint.equals("1")) {
                    x.controlClick("Report -- Pay Advice", "Reprint Pay Advices", "[CLASS:TevCheckBox; INSTANCE:1]");
                    x.controlSend("Report -- Pay Advice", "Reprint Pay Advices", "[CLASS:TevCheckBox; INSTANCE:1]", "{SPACE}", false);
                    logMessage("Reprint is checked.");
                }
            }

            if ((payFrequencyStartDate != null) || (payFrequencyEndDate != null)) {
                x.controlClick("Report -- Pay Advice", "", "[CLASS:TevPageControl; INSTANCE:1]", "left", 1, 81, 12);
                Thread.sleep(3000);
                logMessage("Display Options tab is clicked.");

                if (payFrequencyStartDate != null) {
                    x.controlClick("Report -- Pay Advice", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 160, 27);
                    Thread.sleep(3000);
                    x.controlClick("Report -- Pay Advice", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 344, 27);
                    Thread.sleep(3000);
                    x.send("^a");
                    Thread.sleep(3000);

                    SystemLibrary.clearClipboard();
                    x.controlSend("Report -- Pay Advice", "", "[CLASS:TcxCustomDropDownInnerEdit; INSTANCE:1]", "^c");
                    x.sleep(5000);
                    String currentDateOnUI = SystemLibrary.getClipboard();
                    logMessage("The current Pay Frequency Start Date is '" + currentDateOnUI + "'.");

                    if (payFrequencyStartDate.contains(";")) {
                        payFrequencyStartDate = SystemLibrary.getExpectedDate(payFrequencyStartDate, currentDateOnUI);
                    }
                    x.send("{DEL}", false);
                    Thread.sleep(3000);
                    x.send(payFrequencyStartDate);
                    Thread.sleep(3000);
                    x.send("{TAB}", false);
                    Thread.sleep(3000);
                    logMessage("Pay Frequncy Start Date '" + payFrequencyStartDate + "' is input.");
                }

                if (payFrequencyEndDate != null) {
                    x.controlClick("Report -- Pay Advice", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 160, 27);
                    Thread.sleep(3000);
                    x.controlClick("Report -- Pay Advice", "", "[CLASS:TcxGridSite; INSTANCE:1]", "left", 1, 454, 27);
                    Thread.sleep(3000);
                    x.send("^a");
                    Thread.sleep(3000);

                    SystemLibrary.clearClipboard();
                    x.controlSend("Report -- Pay Advice", "", "[CLASS:TcxCustomDropDownInnerEdit; INSTANCE:1]", "^c");
                    x.sleep(5000);
                    String currentDateOnUI = SystemLibrary.getClipboard();
                    logMessage("The current Pay Frequency End Date is '" + currentDateOnUI + "'.");

                    if (payFrequencyEndDate.contains(";")) {
                        payFrequencyEndDate = SystemLibrary.getExpectedDate(payFrequencyEndDate, currentDateOnUI);
                    }
                    x.send("{DEL}", false);
                    Thread.sleep(3000);
                    x.send(payFrequencyEndDate);
                    Thread.sleep(3000);
                    x.send("{TAB}", false);
                    Thread.sleep(3000);
                    logMessage("Pay Frequncy End Date '" + payFrequencyEndDate + "' is input.");
                }

            }

            logMessage("Screenshot before click Produce button.");
            logScreenshotX();
            x.controlFocus("Report -- Pay Advice", "Produce", "[CLASS:TButton; INSTANCE:3]");
            x.controlClick("Report -- Pay Advice", "Produce", "[CLASS:TButton; INSTANCE:3]");
            Thread.sleep(3000);
            logMessage("Produce button is clicked.");
            logScreenshotX();


            x.controlFocus("Report -- Pay Advice", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Report -- Pay Advice", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            Thread.sleep(3000);
            logMessage("Produce Popup button is clicked.");
            logScreenshotX();


/*
            if(x.controlFocus("Report -- Pay Advice", "&Yes", "[CLASS:TButton; INSTANCE:2]") ) {
                x.controlClick("Report -- Pay Advice", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                Thread.sleep(3000);
                logMessage("Produce Popup button is clicked.");
                logScreenshotX();
            }
*/


            if (deliveryMethod.contains("Print")){
                ////////////
                String fileName = "PayAdviceReport_" + getCurrentTimeAndDate() + ".pdf";
                String fileFullPathName = workingFilePath + fileName;

                if (!print_File(fileFullPathName)) {
                    errorCounter++;
                }else{
                    String sReportContent = SystemLibrary.getStringFromPDFFile(fileFullPathName);
                    SystemLibrary.updateAndValidateStorePDFFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent);
                }
            }



            x.controlFocus("Report -- Pay Advice", "Close", "[CLASS:TButton; INSTANCE:1]");
            x.controlClick("Report -- Pay Advice", "Close", "[CLASS:TButton; INSTANCE:1]");
            Thread.sleep(3000);
            logMessage("Pay Advice Report screen is closed.");
            logScreenshotX();

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean selectMultiItemsInDropdownList2(String title, String text, String controlID, String items) throws InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();

        x.winActivate(title);
        x.sleep(3000);
        x.controlFocus(title, text, controlID);
        x.sleep(3000);
        x.send("{SPACE}", false);
        logMessage("Dropdown list is shown.");
        logScreenshotX();

        int winX = x.winGetPosX(title);
        //logMessage("WinX="+winX);
        int winY = x.winGetPosY(title);
        //logMessage("WinY="+winY);

        int controlX = x.controlGetPosX(title, text, controlID);
        //logMessage("ControlX="+controlX);
        int controlY = x.controlGetPosY(title, text, controlID);
        //logMessage("ControlY="+controlY);
        int controlW = x.controlGetPosWidth(title, text, controlID);
        //logMessage("ControlWidth="+controlW);
        int controlH = x.controlGetPosHeight(title, text, controlID);
        //logMessage("ControlHeight="+controlH);

        int menu_Select_X = controlX + controlW - 30;
        int menu_Select_Y = 38 + controlY + controlH + controlH / 2;

        //logMessage("Menu_Select_X="+menu_Select_X);
        //logMessage("Menu_Select_Y="+menu_Select_Y);

        clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
        logMessage("Select menue is clicked.");
        logScreenshotX();

        if (items != null) {
            if (items.equals("999999")) {
                menu_Select_Y = menu_Select_Y + 21;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Select All is clicked.");
                logScreenshotX();
            } else if (items.equals("666666")) {
                menu_Select_Y = menu_Select_Y + 42;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Unselect All is clicked.");
                logScreenshotX();
            } else {
                String[] subItem = SystemLibrary.splitString(items, ";");
                menu_Select_Y = menu_Select_Y + 42;
                clickControlInWindow(title, text, controlID, menu_Select_X, menu_Select_Y);
                logMessage("Unselect All is clicked.");
                logScreenshotX();

                int totalItemsCount = subItem.length;
                logMessage("Total Item " + totalItemsCount + " to be selected.");
                String[] subSubItem = null;
                int currentOrderNumber = 0;
                String currentItem = null;
                for (int i = 0; i < totalItemsCount; i++) {
                    subSubItem = SystemLibrary.splitString(subItem[i], ":");
                    int orderNumber = Integer.parseInt(subSubItem[1]);
                    currentOrderNumber = orderNumber - currentOrderNumber;
                    logMessage("Order Number=" + orderNumber);
                    x.send("{Tab}", false);
                    x.sleep(1000);
                    x.send("{DOWN}", false);
                    x.sleep(1000);

                    for (int j = 1; j < currentOrderNumber; j++) {
                        x.send("{DOWN}", false);
                        x.sleep(1000);
                    }

                    x.send("{SPACE}", false);
                    x.sleep(1000);

                    x.send("{Tab}", false);

                    currentItem = x.controlGetText(title, text, controlID);
                    if (currentItem.contains(subSubItem[0])) {
                        logMessage("Item '" + subSubItem[0] + "' is selected.");
                    } else {
                        logError("Item '" + subSubItem[0] + "' is NOT selected.");
                        errorCounter++;
                    }
                }

                logScreenshotX();

            }
        } else {
            logError("Not item is selected.");
            errorCounter++;
        }

        x.controlClick(title, text, controlID);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editEmployee_PayDetails(String firstName, String middleName, String lastName, String preferredNamem, String payrollCompany, String location, String payPoint, String payFrequency, String deliveryMethod, String paymentMethod, String awardBased, String autoPay, String recommenceDate, String employmentType, String jobClassification, String minPayLimit, String maxPayLimit, String serviceHoursToDate, String contractHours, String workersComp, String payrollTax, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_PayDetails();

            AutoItX x = new AutoItX();

            if (location != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:6]");
                x.controlSend("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:6]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(location);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Location '" + location + "' is input.");
            }

            if (contractHours != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]");
                x.controlSend("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(contractHours);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Contract Hours '" + contractHours + "' is input.");
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayEmployee_PayDetails() throws InterruptedException {
        //display Employee Personal Detail screen first.
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 24, 44);
        x.sleep(4000);

        logMessage("Employee Pay Details screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean editEmployee_Leave(String firstName, String middleName, String lastName, String preferredNamem, String actionMethod, String leaveType, String leaveClass, String leaveLoadingClass, String yearToDateLoading, String firstEntitlementPeriod, String grantImmediately, String nonEntitlementMonth, String suspendLeaveEntitlement, String suspendLeaveEntitlementFrom, String suspendLeaveEntitlementTo, String entitelmentDate, String entitlementHours, String prorataDate, String prorateHours, String leaveTakenDate, String leaveTakenHours, String totalEntitlement, String costAccount, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;
        AutoItX x = new AutoItX();

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_LeaveDetails();

            if (actionMethod.equals("1")) {
                //Add new leave
                x.controlFocus("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
                x.controlClick("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
                Thread.sleep(3000);
                logMessage("Add button is clicked in Employee Leave Details screen. ");
                logScreenshotX();

                if (x.winWait("Add New Employee Leave", "", 15)) {
                    logMessage("Add Leave screen is shown.");

                    if (leaveType != null) {
                        x.winActivate("Add New Employee Leave");
                        x.controlClick("Add New Employee Leave", "", "[CLASS:TevComboBox; INSTANCE:1]");
                        x.sleep(3000);
                        x.controlSend("Add New Employee Leave", "", "[CLASS:TevComboBox; INSTANCE:1]", leaveType);
                        x.send("{TAB}", false);
                        logMessage("Leave Type '"+leaveType+"' is input.");
                    }

                    if (leaveClass!=null){
                        String leaveClassControlID=null;
                        switch (leaveType) {
                            case "Annual Leave":
                                leaveClassControlID="[CLASS:TevComboInnerEdit; INSTANCE:6]";
                                break;
                            case "Long Service Leave":
                                leaveClassControlID="[CLASS:TevComboInnerEdit; INSTANCE:3]";
                                break;
                            case "Sick Leave":
                                leaveClassControlID="[CLASS:TevComboInnerEdit; INSTANCE:4]";
                                break;
                            case "User Defined Leave":
                                leaveClassControlID="[CLASS:TevComboInnerEdit; INSTANCE:2]";
                                break;
                        }

                        x.controlClick("Add New Employee Leave", "", leaveClassControlID);
                        x.controlSend("Add New Employee Leave", "", leaveClassControlID, "^a");
                        x.sleep(3000);
                        x.send("{DEL}", false);
                        x.sleep(2000);
                        x.send(leaveClass);
                        x.sleep(2000);
                        x.send("{TAB}", false);
                        x.sleep(3000);
                        logMessage("Leave Class '"+leaveClass+"' is input.");
                    }

                    if (grantImmediately!=null){
                        if (grantImmediately.equals("1")){
                            x.controlFocus("Add New Employee Leave", "&Grant Immediately", "[CLASS:TevCheckBox; INSTANCE:3]");
                            x.send("{SPACE}", false);
                            x.sleep(2000);
                            logMessage("Checkbox Grant Immediately is clicked.");
                        }
                    }

                    if (entitlementHours!=null){
                        x.controlClick("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:7]");
                        x.sleep(3000);
                        x.controlSend("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:7]", entitlementHours);
                        x.send("{TAB}", false);
                        logMessage("Leave Entitlement Hours '"+entitlementHours+"' is input.");
                    }

                    if (prorateHours!=null){
                        x.controlClick("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:6]");
                        x.sleep(3000);
                        x.controlSend("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:6]", prorateHours);
                        x.send("{TAB}", false);
                        logMessage("Prorata Hours '"+prorateHours+"' is input.");
                    }
                    if (leaveTakenHours!=null){
                        x.controlClick("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]");
                        x.sleep(3000);
                        x.controlSend("Add New Employee Leave", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]", leaveTakenHours);
                        x.send("{TAB}", false);
                        logMessage("Leave Taken Hours '"+leaveTakenHours+"' is input.");
                    }

                    logMessage("Screenshot before click OK button in Add New Employee Leave screen.");
                    logScreenshotX();

                    x.controlFocus("Add New Employee Leave", "OK", "[CLASS:TButton; INSTANCE:3]");
                    x.controlClick("Add New Employee Leave", "OK", "[CLASS:TButton; INSTANCE:3]");
                    Thread.sleep(4000);
                    logMessage("OK button is clicked in Add New Employee Leave screen.");
                    logScreenshotX();
                }
            } else {
                logError("Add Leave screen is NOT shown.");
                errorCounter++;
            }


            //////////////////////////
            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();

        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }


    public static boolean editEmployee_PersonalInformation(String firstName, String middleName, String lastName, String preferredNamem, String title, String gender, String maritialStatus, String dob, String dateHired, String aboriginalTorresStrait, String residentialStreet, String residentialSuburb, String residentialState, String residentialPostcode, String residentialCountry, String postalAddress, String postalSuburb, String postalState, String postalPostcode, String postalCountry, String telephone, String mobilePhone, String emailAddress, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            AutoItX x = new AutoItX();

            if (postalPostcode!=null){
                x.winActivate("Edit Employee");
                x.controlClick("Edit Employee", "","[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]");
                x.controlSend("Edit Employee", "","[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]", "^a");
                x.sleep(2000);
                x.send("{DEL}", false);
                x.send(postalPostcode);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Postal Postcode '"+postalPostcode+"' is input.");
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }


    public static boolean processEOP(String payFrequency, String storeFileName, String isUpdate, String isCompare, String expectedContent) throws InterruptedException, IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        if (display_Screen("End of Period > End of Pay", "End of Pay Process", 1)){
            if (payFrequency!=null){
                if (!selectMultiItemsInDropdownList2("End of Pay Process", "", "[CLASS:TcxCustomDropDownInnerEdit; INSTANCE:1]", payFrequency)) errorCounter++;
            }

            AutoItX x=new AutoItX();

            logMessage("Log screenshot before click OK button.");
            logScreenshotX();
            x.controlFocus("End of Pay Process", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(2000);
            x.controlClick("End of Pay Process", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(3000);
            logMessage("OK button is clicked in EOP screen.");
            logScreenshotX();

            if (x.winWait("End of Pay Process", "&No", 10)){
                logMessage("Would you like to continue screen is shown. ");
                logScreenshotX();

                x.controlFocus("End of Pay Process", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                x.controlClick("End of Pay Process", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                logMessage("Yes button is clicked.");
                logScreenshotX();
            }

            x.sleep(15000);
            logMessage("Wait 15 seconds for processing EOP.");

            int a=0;
            while (!x.controlCommandIsEnabled("End of Pay Process", "Close", "[CLASS:TButton; INSTANCE:1]")){
                x.sleep(5000);
                a++;
                if (a>24) break; //wait no more than 2 mins.
            }

            logScreenshotX();
            x.controlFocus("End of Pay Process", "", "[CLASS:TBrilliantRichEdit; INSTANCE:1]");
            String currentLog=x.controlGetText("End of Pay Process", "", "[CLASS:TBrilliantRichEdit; INSTANCE:1]");
            logMessage("The EOP log is below:");
            System.out.println(currentLog);

            String fileName = "EOPLog_" + getCurrentTimeAndDate() + ".pdf";
            String fileFullPathName = workingFilePath + fileName;
            SystemLibrary.createTextFile(fileFullPathName, currentLog);

            if (storeFileName!=null){
                if (!SystemLibrary.updateAndValidateStoreStringFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent)) errorCounter++;
            }

            x.controlFocus("End of Pay Process", "Close", "[CLASS:TButton; INSTANCE:1]");
            x.controlClick("End of Pay Process", "Close", "[CLASS:TButton; INSTANCE:1]");
            x.sleep(3000);
            logMessage("EOP close button is clicked.");
            logScreenshotX();

        }else{
            errorCounter++;
        }

        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }


    public static boolean exitMCS() throws InterruptedException {
        boolean isClosed = false;
        AutoItX x = new AutoItX();
        int attempt = 0;

        if (x.winWait("MicrOpay Client Service Config.  Version 3.0.1.3", "", 5)){
            x.controlClick("MicrOpay Client Service Config.  Version 3.0.1.3", "Close", "[NAME:CloseBtn]");
            Thread.sleep(3000);
            logMessage("MCS is closed.");
        }else{
            logMessage("MCS is not running.");
        }

        return isClosed;
    }

    public static boolean addDBInMCS(String databaseType, String databaseDescription, String dbServerName, String databaseName, String dbAuthenticationMethod, String databaseLoginName, String databasePassword, String comDBServerName, String comDBName, String comDBAuthenticationMethod, String comDatabaseLoginName, String comDatabasePassword, String integrationType, String apiKey, String serviceUsername, String servicePassword, String apiUrl, String hubUrl, String proxyUrl, String proxyUsername, String proxyPassword) throws Exception{
        boolean isAdded=false;
        int errorCounter=0;

        MCSLib.launchMCS();
        AutoItX x=new AutoItX();

        x.winActivate("MicrOpay Client Service Config.  Version");
        x.controlClick("MicrOpay Client Service Config.  Version", "Add", "[NAME:AddBtn]");
        x.sleep(3000);
        logMessage("Add button is clicked in MCS.");
        logScreenshotX();

        if (x.winWait("Add Database", "", 5)){
            logMessage("Add Database screen is shown.");

            if (databaseType!=null){
                x.controlFocus("Add Database", "", "[CLASS:Edit; INSTANCE:1]");
                x.controlSend("Add Database", "", "[CLASS:Edit; INSTANCE:1]", databaseType);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Type: "+databaseType+" is input.");
            }

            if (databaseDescription!=null){
                x.controlFocus("Add Database", "", "[NAME:DBDescriptionTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBDescriptionTextBox]", databaseDescription);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Description: "+databaseDescription+" is input.");
            }

            ///////////////////////
            if (dbServerName!=null){
                x.controlFocus("Add Database", "", "[NAME:DBServerNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBServerNameTextBox]", dbServerName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("DB Server Name: "+dbServerName+" is input.");
            }

            if (databaseName!=null){
                x.controlFocus("Add Database", "", "[NAME:DBNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBNameTextBox]", databaseName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Name: "+databaseName+" is input.");
            }

            if (dbAuthenticationMethod!=null){
                if (dbAuthenticationMethod.equals("1")){
                    x.controlFocus("Add Database", "Use Windows Authentication", "[NAME:DB1UseWindowsRB]");
                    x.controlClick("Add Database", "Use Windows Authentication", "[NAME:DB1UseWindowsRB]");
                    x.sleep(3000);
                    logMessage("Checkbox - User Windwo Authentication is clecked.");
                }else if (dbAuthenticationMethod.equals("2")){
                    x.controlFocus("Add Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    x.controlClick("Add Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    x.sleep(3000);
                    logMessage("Checkbox - Use SQL Server authentication is clecked.");
                }
            }

            if (databaseLoginName!=null){
                x.controlFocus("Add Database", "", "[NAME:DBUserNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBUserNameTextBox]", databaseLoginName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Login Name: "+databaseLoginName+" is input.");
            }

            if (databasePassword!=null){
                x.controlFocus("Add Database", "", "[NAME:DBPasswordTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBPasswordTextBox]", databasePassword);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Password: "+databasePassword+" is input.");
            }

            //////////////////////// Comm DB ////////////////////////

            if (comDBServerName!=null){
                x.controlFocus("Add Database", "", "[NAME:CommonDBServerNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:CommonDBServerNameTextBox]", comDBServerName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Common DB Server Name: "+comDBServerName+" is input.");
            }

            if (comDBName!=null){
                x.controlFocus("Add Database", "", "[NAME:CommonDBNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:CommonDBNameTextBox]", comDBName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Common DB Name: "+comDBName+" is input.");
            }

            if (comDBAuthenticationMethod!=null){
                if (comDBAuthenticationMethod.equals("1")){
                    x.controlFocus("Add Database", "Use Windows Authentication", "[NAME:DB2UseWindowsRB]");
                    x.controlClick("Add Database", "Use Windows Authentication", "[NAME:DB1UseWindowsRB]");
                    x.sleep(3000);
                    logMessage("Checkbox - User Windwo Authentication is clecked.");
                }else if (comDBAuthenticationMethod.equals("2")){
                    x.controlFocus("Add Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    x.controlClick("Add Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    x.sleep(3000);
                    logMessage("Checkbox - Use SQL Server authentication is clecked.");
                }
            }

            if (databaseLoginName!=null){
                x.controlFocus("Add Database", "", "[NAME:DBUserNameTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBUserNameTextBox]", databaseLoginName);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Login Name: "+databaseLoginName+" is input.");
            }

            if (databasePassword!=null){
                x.controlFocus("Add Database", "", "[NAME:DBPasswordTextBox]");
                x.controlSend("Add Database", "", "[NAME:DBPasswordTextBox]", databasePassword);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Database Password: "+databasePassword+" is input.");
            }
            //////
        }else{
            logError("Add database screen is NOT shown.");
        }

        if (errorCounter==0){
            isAdded=true;
        }
        return isAdded;
    }

    public static boolean display_PayrollCompanies() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("Payroll Companies", 1);
        logMessage("Payroll Company screen is shwon.");
        Thread.sleep(5000);
        return isShown;
    }

    public static boolean addPayrollCompany(String code, String name, String tradingAs, String EFTInstitution, String superFUND) throws InterruptedException {
        boolean isDone=false;
        int errorCounter=0;

        AutoItX x=new AutoItX();
        display_PayrollCompanies();
        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.send("{Ins}", false);
        Thread.sleep(4000);
        logMessage("Insert key is press");

        if (x.winWaitActive("Add New Payroll Company (Payer)", "", 10)){
            logMessage("Add Payroll Company Screen is shown.");

            if (code!=null){
                x.winActivate("Add New Payroll Company (Payer)");
                x.controlFocus("Add New Payroll Company (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:14]");
                x.send(code);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Code '"+code+"' is input.");
            }

            if (name!=null){
                x.controlFocus("Add New Payroll Company (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:16]");
                x.controlClick("Add New Payroll Company (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:16]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(name);
                logMessage("Name '"+name+"' is input.");
            }

            if (tradingAs!=null){
                x.controlFocus("Add New Payroll Company (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:15]");
                x.controlClick("Add New Payroll Company (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:15]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(tradingAs);
                x.send("{TAB}", false);
                logMessage("Trading as '"+tradingAs+"' is input.");
            }

            if (EFTInstitution!=null){
                x.controlFocus("Add New Payroll Company (Payer)", "", "[CLASS:TevIPEFTParametersLookupCombo; INSTANCE:1]");
                x.controlClick("Add New Payroll Company (Payer)", "", "[CLASS:TevIPEFTParametersLookupCombo; INSTANCE:1]");
                x.send(EFTInstitution);
                x.send("{TAB}", false);
                logMessage("EFT Institution '"+EFTInstitution+"' is input.");
            }

            if (superFUND!=null) {
                x.controlFocus("Add New Payroll Company (Payer)", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                x.controlClick("Add New Payroll Company (Payer)", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                x.send(superFUND);
                x.send("{TAB}", false);
                logMessage("Super Fund '" + superFUND + "' is input.");
            }
            logMessage("Screenshot before click Save button.");
            AutoITLib.logScreenshotX();

            x.controlFocus("Add New Payroll Company (Payer)", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Add New Payroll Company (Payer)", "OK", "[CLASS:TButton; INSTANCE:2]");
            logMessage("OK button is clicked.");
            x.sleep(3000);
            logScreenshotX();

        }else{
            logError("Add Payroll Company screen is NOT shwon.");
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean display_NewEmployees() throws InterruptedException {
        boolean isShown = false;

        if (display_Screen("New Employees", "New Employees Maintenance", 1)){
            isShown=true;
        }

        logScreenshotX();
        return isShown;
    }

    public static boolean closeNewEmployeeScreen() throws InterruptedException {
        boolean isClosed=false;
        AutoItX x=new AutoItX();
        x.winActivate("New Employees Maintenance");
        x.controlGetFocus("New Employees Maintenance", "");
        x.controlClick("New Employees Maintenance", "", "[CLASS:TdxBarControl; INSTANCE:1]", "left", 1, 26, 10);
        x.sleep(3000);
        logMessage("New Employee Maintenance screen is closed.");
        isClosed=true;
        logScreenshotX();
        return isClosed;
    }


    public static boolean editNewEmployee(String employeeCode, String location, String payPoint, String payFrequency, String defaultCostAccount, String superFund, String memberNumber, String employerContribution, String addNewEmployeeToPayroll, String saveNewEmployee) throws InterruptedException {
        //To be completed ....

        /*
        String employeeCode="JONES01";
        String location="Team F";
        String payPoint="NSW";
        String payFrequency="1W01";
        String defaultCostAccount="NSW-CONS";
        //String usePayrollCompanyDefaultSuperFund="1";
        //String superfundName="SUPER";
        String memberNumber="1234567";
        String employerContribution="SUPER";
        String addNewEmployeeToPayroll="1";
        */
        boolean isDone=false;
        int errorCounter=0;

        logMessage("Debug here.");

        AutoItX x=new AutoItX();
        boolean isShown=display_NewEmployees();
        if (isShown){

            String title="New Employees Maintenance";
            x.winActivate(title, "");
            x.controlGetFocus(title);
            AutoITLib.doubleClick_Window(title, 40, 206);

            if (x.winWait("Edit New Employee", "", 10)) {
                logMessage("Edit New Employee screen is shown.");
                logScreenshotX();

                /////////////////// Setup Screen //////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 1st Next button is clicked in New Emplyee - Setup screen.");
                logScreenshotX();

                //////////////// THF Declaration 1/2 Screen ///////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 2nd Next button is clicked in New Emplyee - TFN - Declaration 1/2 screen.");
                logScreenshotX();

                //////////////// THF Declaration 2/2 Screen ///////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 3rd Next button is clicked in New Emplyee - TFN - Declaration 2/2 screen.");
                logScreenshotX();

                //////////////// Personal Details Screen //////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 4th Next button is clicked in New Emplyee - Personal Details screen.");
                logScreenshotX();

                //////////////// Contact Details screen //////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 5th Next button is clicked in New Emplyee - Contact Details screen.");
                logScreenshotX();

                //////////// Employment screen /////////////
                if (employeeCode!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(employeeCode+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employee Code '"+employeeCode+"' is input.");
                }

                if (location!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPLocationLookupCombo; INSTANCE:1]");
                    x.send(location+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Location '"+location+"' is input.");
                }

                if (payPoint!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayPointLookupCombo; INSTANCE:1]");
                    x.send(payPoint+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("PayPoint '"+payPoint+"' is input.");
                }

                if (payFrequency!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayFrequencyLookupCombo; INSTANCE:1]");
                    x.send(payFrequency+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Pay Frequency '"+payFrequency+"' is input.");
                }

                if (defaultCostAccount!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                    x.send(defaultCostAccount+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Default Cost Account '"+defaultCostAccount+"' is input.");
                }

                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
                x.sleep(2000);
                logMessage("The 6th Next button is clicked in New Emplyee - Employeement screen.");
                logScreenshotX();

///////////// Superannuation Screen /////////////////

                if (superFund!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                    x.send(superFund+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Super Fund '"+superFund+"' is input.");
                }


                if (memberNumber!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(memberNumber+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Member Number '"+memberNumber+"' is input.");
                }

                if (employerContribution!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperSchemeLookupCombo; INSTANCE:1]");
                    x.send(employerContribution+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employer Contribution '"+employerContribution+"' is input.");
                }

                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
                x.sleep(2000);
                logMessage("The 6th Next button is clicked in New Emplyee - Superannuation screen.");
                logScreenshotX();

                ////////////// Bank Account Screen //////////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:9]");
                x.sleep(2000);
                logMessage("The 7th Next button is clicked in New Emplyee - Bank Account screen.");
                logScreenshotX();

                /////////////// Save Screen ////////////////
                if (saveNewEmployee!=null){
                    if (saveNewEmployee.equals("1")){
                        x.controlGetFocus("Edit New Employee", "Save");
                        x.controlClick("Edit New Employee", "Save", "[CLASS:TButton; INSTANCE:8]");
                        x.sleep(2000);
                        logMessage("Save button is clicked .");
                    }
                }
                logScreenshotX();

                /////////////// Confirmation Screen ////////////////
                if (addNewEmployeeToPayroll!=null){
                    if (addNewEmployeeToPayroll.equals("1")){
                        x.controlGetFocus("Edit New Employee", "Add New Employee to Payroll");
                        x.controlClick("Edit New Employee", "Add New Employee to Payroll", "[CLASS:TCheckBox; INSTANCE:1]");
                        logMessage("Butotn - Add New Employee to Payroll is clicked.");
                    }
                }
                logScreenshotX();

                x.controlGetFocus("Edit New Employee", "Save");
                x.controlClick("Edit New Employee", "Save", "[CLASS:TButton; INSTANCE:8]");
                x.sleep(2000);
                logMessage("Save button is clicked in the Confirmation page.");

                if (x.winWait("Edit Employee", "", 120)){
                    logMessage("Edit Employee Screen is shown.");

                    x.controlGetFocus("Edit Employee", "OK");
                    x.controlClick("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:4]");
                    x.sleep(10000);
                    logMessage("OK button is clicked in Edit Employee screen.");
                    logScreenshotX();

                    closeNewEmployeeScreen();

                }else{
                    logError("Edit Employee screen is NOT shown.");
                }

                logScreenshotX();


            }else{
                logError("Edit New Employee screen is NOT shown.");
            }

        }else{
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;

    }

    public static boolean editEmployee_Super(String firstName, String middleName, String lastName, String preferredNamem, String actionMethod, String fundCode, String fundDescription, String dateJointed, String membershipNo, String accountName, String category, String superSalary, String dateTerminated, String contributionType, String contributionName, String contributionValue, String contributionCostAccount, String applySGL, String applySalaryWages, String primaryContribution, String reportableSuper, String qualifications, String allocations, String newfundCode, String newfundDescription, String newdateJoined, String newmembershipNo, String newaccountName, String newcategory, String newsuperSalary, String newdateTerminated, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException, IOException, UnsupportedFlavorException {
        boolean isDone = false;
        int errorCounter = 0;

        //String employeeCode = DBManage.getEmployeeCodeFromPayrollDB(serverName, payrollDBName, firstName, lastName);
        AutoItX x = new AutoItX();
        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_Super();

            switch (actionMethod){
                case "1":
                    //Add new leave
                    x.controlFocus("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:6]");
                    x.controlClick("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:6]");
                    Thread.sleep(3000);
                    logMessage("Add button is clicked in Employee Super Details screen. ");
                    logScreenshotX();

                    if (x.winWait("Add New Employee Superannuation Fund", "", 20 )) {
                        logMessage("Add Employee Superannuation Fund screen is shown.");

                        x.winActivate("Add New Employee Superannuation Fund");
                        if (fundCode!=null){
                            x.controlFocus("Add New Employee Superannuation Fund", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                            x.send(fundCode+"{TAB}", false);
                            x.sleep(2000);
                            logMessage("Fund Name '"+fundCode+"' is input.");
                        }

                        if (dateJointed!=null){
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:3]");
                            x.send("^a", false);
                            x.sleep(1000);
                            x.send("{DEL}", false);
                            x.sleep(1000);
                            x.send(dateJointed+"{TAB}", false);
                            x.sleep(2000);
                            logMessage("Date Joined '"+dateJointed+"' is input.");
                        }

                        if (membershipNo!=null){
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                            x.send("^a", false);
                            x.sleep(1000);
                            x.send("{DEL}", false);
                            x.sleep(1000);
                            x.send(membershipNo+"{TAB}", false);
                            x.sleep(2000);
                            logMessage("Membership Number '"+membershipNo+"' is input.");
                        }

                        if (accountName!=null){
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                            x.send("^a", false);
                            x.sleep(1000);
                            x.send("{DEL}", false);
                            x.sleep(1000);
                            x.send(membershipNo+"{TAB}");
                            x.sleep(2000);
                            logMessage("Account Name '"+accountName+"' is input.");
                        }

                        if (category!=null){
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevIPSuperCategoryLookupCombo; INSTANCE:1]");
                            x.send("^a", false);
                            x.send("{DEL}", false);
                            x.send(category+"{TAB}");
                            x.sleep(2000);
                            logMessage("Category '"+category+"' is input.");
                        }

                        if (superSalary!=null){
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevFloatEdit; INSTANCE:2]");
                            x.send("^a", false);
                            x.sleep(1000);
                            x.send(superSalary+"{TAB}");
                            x.sleep(2000);
                            logMessage("Super Salary '"+superSalary+"' is input.");
                        }

                        if (dateTerminated!=null) {
                            x.controlFocus("Add New Employee Superannuation Fund", "", "[CLASS:TevCheckBox; INSTANCE:4]");
                            x.send("{SPACE}", false);
                            x.sleep((2000));
                            x.controlFocus("Add New Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:2]");
                            x.controlClick("Add New Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:2]");
                            x.send("^a", false);
                            x.send("{DEL}");
                            x.send(dateTerminated+"{TAB}");
                            x.sleep(2000);
                            logMessage("Date Terminated '"+dateTerminated+"' is input.");
                        }

                        logMessage("Screenshot before click OK button.");
                        logScreenshotX();

                        x.controlFocus("Add New Employee Superannuation Fund", "OK", "[CLASS:TButton; INSTANCE:2]");
                        x.controlClick("Add New Employee Superannuation Fund", "OK", "[CLASS:TButton; INSTANCE:2]");

                        Thread.sleep(4000);
                        logMessage("OK button is clicked in Add New Employee Leave screen.");
                        logScreenshotX();

                        if (selectSuperFundInEmployeeSuperDetailScreen(fundDescription, dateJointed, membershipNo)){
                            x.controlFocus("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
                            x.controlClick("Edit Employee", "&Add...", "[CLASS:TButton; INSTANCE:3]");
                            x.sleep(2000);
                            logMessage("Add Super Contribution button is clicked.");
                            logScreenshotX();

                            if (x.winWait("Add New Superannuation Contribution", "", 10)){
                                logMessage("Start adding contribution items....");
                                //////////////////////////////

                                if (contributionType!=null){
                                    if (contributionType.equals("1")){
                                        x.controlFocus("Add New Superannuation Contribution", "Employer", "[CLASS:TevRadioButton; INSTANCE:2]");
                                        x.send("{SPACE}", false);
                                        logMessage("Contribution Type 'Employer' is checked.");

                                        if (contributionName!=null){
                                            x.controlFocus("Add New Superannuation Contribution", "", "[CLASS:TevIPSuperSchemeLookupCombo; INSTANCE:1]");
                                            x.controlClick("Add New Superannuation Contribution", "", "[CLASS:TevIPSuperSchemeLookupCombo; INSTANCE:1]");
                                            x.send("^a", false);
                                            x.sleep(1000);
                                            x.send(contributionName+"{TAB}", false);
                                            x.sleep(1000);
                                            logMessage("Contribution Name '"+contributionName+"' is input.");
                                        }

                                    }else if (contributionType.equals("2")){
                                        x.controlFocus("Add New Superannuation Contribution", "Employee", "[CLASS:TevRadioButton; INSTANCE:1]");
                                        x.send("{SPACE}", false);
                                        logMessage("Contribution Type 'Employee' is checked.");

                                        if (contributionName!=null){
                                            x.controlFocus("Add New Superannuation Contribution", "", "[CLASS:TevIPAdditionsDeductionsLookupCombo; INSTANCE:1]");
                                            x.controlClick("Add New Superannuation Contribution", "", "[CLASS:TevIPAdditionsDeductionsLookupCombo; INSTANCE:1]");
                                            x.send("^a", false);
                                            x.sleep(1000);
                                            x.send(contributionName+"{TAB}", false);
                                            x.sleep(1000);
                                            logMessage("Contribution Name '"+contributionName+"' is input.");
                                        }
                                    }

                                    if (contributionValue!=null){
                                        x.controlFocus("Add New Superannuation Contribution", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]");
                                        x.controlClick("Add New Superannuation Contribution", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]");
                                        x.send("^a", false);
                                        x.sleep(1000);
                                        x.send(contributionValue+"{TAB}", false);
                                        logMessage("Contribution Value '"+contributionValue+"' is input.");

                                    }

                                    if (contributionCostAccount!=null){
                                        x.controlFocus("Add New Superannuation Contribution", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                                        x.controlClick("Add New Superannuation Contribution", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                                        x.send("^a", false);
                                        x.sleep(1000);
                                        x.send(contributionCostAccount+"{TAB}", false);
                                        logMessage("Contribution Cost Account '"+contributionCostAccount+"' is input.");
                                    }

                                    if (applySGL!=null){
                                        if (applySGL.equals("1")){
                                            x.controlFocus("Add New Superannuation Contribution", "Apply &SGL", "[CLASS:TevCheckBox; INSTANCE:3]");
                                            x.send("{SPACE}", false);
                                            logMessage("Apply SGL is checked.");
                                        }
                                    }

                                    if (applySalaryWages!=null){
                                        if (applySalaryWages.equals("1")){
                                            x.controlFocus("Add New Superannuation Contribution", "Apply Salary && Wages", "[CLASS:TevCheckBox; INSTANCE:2]");
                                            x.send("{SPACE}", false);
                                            logMessage("Apply Salary Wages is checked.");
                                        }
                                    }

                                    if (primaryContribution!=null){
                                        if (primaryContribution.equals("1")){
                                            x.controlFocus("Add New Superannuation Contribution", "&Primary Contribution", "[CLASS:TevCheckBox; INSTANCE:4]");
                                            x.send("{SPACE}", false);
                                            logMessage("Primary Contribution is checked.");
                                        }
                                    }

                                    if (reportableSuper!=null){
                                        if (reportableSuper.equals("1")){
                                            x.controlFocus("Add New Superannuation Contribution", "Reportable Super", "[CLASS:TevCheckBox; INSTANCE:1]");
                                            x.send("{SPACE}", false);
                                            logMessage("Reportable Super is checked.");
                                        }
                                    }

                                    if (qualifications!=null){
                                        if (qualifications.equals("1")){
                                            x.controlFocus("Add New Superannuation Contribution", "&Qualifications", "[CLASS:TevCheckBox; INSTANCE:5]");
                                            x.send("{SPACE}", false);
                                            logMessage("Qualification is checked.");
                                        }
                                    }

                                    if (allocations!=null){
                                        x.controlFocus("Add New Superannuation Contribution", "", "[CLASS:TevFloatEdit; INSTANCE:1]");
                                        x.controlClick("Add New Superannuation Contribution", "", "[CLASS:TevFloatEdit; INSTANCE:1]");
                                        x.send("^a", false);
                                        x.send(allocations+"{TAB}", false);
                                        logMessage("Allocation '"+allocations+"' is input.");
                                    }

                                    logMessage("Screenshot before click OK Button in Super Contribution screen.");
                                    logScreenshotX();

                                    x.controlFocus("Add New Superannuation Contribution", "OK", "[CLASS:TButton; INSTANCE:2]");
                                    x.controlClick("Add New Superannuation Contribution", "OK", "[CLASS:TButton; INSTANCE:2]");
                                    x.sleep(2000);
                                    logMessage("OK button is clicked in Super Controbution screen.");
                                    logScreenshotX();

                                }
                                //////
                            }else{
                                logError("The 'Add New Superannuation Contribution' screen is NOT shown.");
                                errorCounter++;
                            }
                        }else{
                            errorCounter++;
                        }
                    }else{
                        logError("Add New Employee Superannuation Fund screen is NOT shown.");
                        errorCounter++;
                    }
                    break;
                case "2":
                    if (selectSuperFundInEmployeeSuperDetailScreen(fundDescription, dateJointed, membershipNo)){
                        x.controlFocus("Edit Employee", "&Edit...", "[CLASS:TButton; INSTANCE:5]");
                        x.controlClick("Edit Employee", "&Edit...", "[CLASS:TButton; INSTANCE:5]");
                        x.sleep(2000);
                        logMessage("Edit super fund button is clicked.");
                        logScreenshotX();

                        if (x.winWait("Edit Employee Superannuation Fund", "", 20 )) {
                            x.winActivate("Edit Employee Superannuation Fund", "");

                            if (newfundCode!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                                x.send("^a", false);
                                x.sleep(1000);
                                x.send(newfundCode+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Fund Code '"+newfundCode+"' is input.");
                            }

                            if (newdateJoined!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:3]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:3]");
                                x.send("^a", false);
                                x.sleep(1000);
                                x.send(newdateJoined+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Date Joined '"+newdateJoined+"' is input.");
                            }

                            if (newmembershipNo!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:2]");
                                x.send("^a", false);
                                x.send(newmembershipNo+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Membershipt No '"+newmembershipNo+"' is input");
                            }

                            if (newaccountName!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                                x.send("^a", false);
                                x.send(newaccountName+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New AccountName '"+newaccountName+"' is input");
                            }

                            if (newcategory!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevIPSuperCategoryLookupCombo; INSTANCE:1]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevIPSuperCategoryLookupCombo; INSTANCE:1]");
                                x.send("^a", false);
                                x.send(newcategory+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Category '"+newcategory+"' is input.");
                            }

                            if (newsuperSalary!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevFloatEdit; INSTANCE:2]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevFloatEdit; INSTANCE:2]");
                                x.send("^a", false);
                                x.send(newsuperSalary+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Super Salary '"+newsuperSalary+"' is input.");
                            }

                            if (newdateTerminated!=null){
                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevCheckBox; INSTANCE:4]");
                                x.send("{SPACE}", false);
                                x.sleep(1000);
                                logMessage("Data Terminated is checked.");

                                x.controlFocus("Edit Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:2]");
                                x.controlClick("Edit Employee Superannuation Fund", "", "[CLASS:TevDateEdit; INSTANCE:2]");
                                x.send("^a", false);
                                x.send(newdateTerminated+"{TAB}", false);
                                x.sleep(1000);
                                logMessage("New Date Terminated '"+newdateTerminated+"' is input.");

                            }

                            logMessage("Screenshot before click OK button in Edit Employee Super fund screen.");
                            logScreenshotX();

                            x.controlFocus("Edit Employee Superannuation Fund", "OK", "[CLASS:TButton; INSTANCE:2]");
                            x.controlClick("Edit Employee Superannuation Fund", "OK", "[CLASS:TButton; INSTANCE:2]");
                            x.sleep(2000);
                            logMessage("OK button is clicked.");

                        }else{
                            logError("Edit Employee Superannuation Fund screen is NOT shown.");
                            errorCounter++;
                        }
                    }
                    break;

                case "3":
                    //Delete the Super Contribution first if has

                    if (contributionName!=null){
                        if (selectSuperContributionInEmployeeSuperDetailScreen(fundDescription, contributionType, contributionName)){
                            x.controlFocus("Edit Employee", "&Delete", "[CLASS:TButton; INSTANCE:1]");
                            x.controlClick("Edit Employee", "&Delete", "[CLASS:TButton; INSTANCE:1]");
                            x.sleep(2000);
                            logMessage("Button Delete is clicked in Super Contribution section.");

                            logMessage("Screenshot before click Yes button.");
                            logScreenshotX();

                            x.controlFocus("Superannuation Detail", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                            x.controlClick("Superannuation Detail", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                            x.sleep(2000);
                            logMessage("Button Yes is clicked to confirm deleting Super Contribution.");
                        }else{
                            logError("Contribution "+contributionName+"' is NOT found.");
                            errorCounter++;
                        }
                    }
                    if (selectSuperFundInEmployeeSuperDetailScreen(fundDescription, dateJointed, membershipNo)){

                        x.controlFocus("Edit Employee", "&Delete", "[CLASS:TButton; INSTANCE:4]");
                        x.controlClick("Edit Employee", "&Delete", "[CLASS:TButton; INSTANCE:4]");
                        x.sleep(4000);
                        logMessage("Delete button is clicked.");

                        if (x.winWait("Superannuation Detail", "", 10)){
                            logMessage("Delete Super Confirmation Dialogue is shwon.");
                            logScreenshotX();

                            x.controlFocus("Superannuation Detail", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                            x.controlClick("Superannuation Detail", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                            Thread.sleep(3000);
                            logMessage("Yes button is clicked.");
                        }
                    }else{
                        logError("Super "+fundDescription+"' is NOT found.");
                        errorCounter++;
                    }
                    break;
            }
            //////////////////////////
            logMessage("Screenshot before close Employee Deails screen. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();

        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayEmployee_Super() throws InterruptedException {
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 19, 127);
        x.sleep(4000);

        logMessage("Employee Super screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean selectSuperFundInEmployeeSuperDetailScreen(String fundDescription, String dateJoined, String membershipNo) throws InterruptedException, IOException, UnsupportedFlavorException {
        boolean isFound=false;
        String lastSelection=null;
        String currentSelection=null;

        AutoItX x=new AutoItX();
        x.winActivate("Edit Employee ");
        x.sleep(2000);
        x.controlFocus("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:2]");
        x.sleep(2000);
        AutoITLib.clickControlInWindow_NEWInUse("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:2]", 93, 27);
        x.sleep(2000);
        logMessage("Click the first row in Super funnd table.");
        clearClipboard();
        x.send("^c", false);
        x.sleep(2000);
        currentSelection=getClipboard();
        lastSelection=currentSelection;
        String expectedString=fundDescription+"\t"+dateJoined+"\t\t"+membershipNo;
        logMessage("The expected String is '"+expectedString+"'.");

        if (currentSelection.contains(expectedString)){
            logMessage("Super Fund '"+fundDescription+"' is found.");
            isFound=true;
        }else{
            while(isFound==false){
                x.controlFocus("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:2]");
                x.send("{DOWN}", false);
                x.sleep(2000);
                clearClipboard();
                x.send("^c", false);
                x.sleep(2000);
                currentSelection=getClipboard();
                if (currentSelection.contains(expectedString)){
                    logMessage("Super Fund '"+fundDescription+"' is found.");
                    isFound=true;
                    break;
                }
                if (currentSelection.equals(lastSelection)){
                    logWarning("End of fund list, Super Found '"+fundDescription+"' is NOT found.");
                    break;
                }
                lastSelection=currentSelection;
            }
        }

        return isFound;
    }

    public static boolean selectSuperContributionInEmployeeSuperDetailScreen(String fundDescription, String contributionType, String contributionName) throws InterruptedException, IOException, UnsupportedFlavorException {
        boolean isFound=false;
        String lastSelection=null;
        String currentSelection=null;

        AutoItX x=new AutoItX();
        x.winActivate("Edit Employee ");
        x.sleep(2000);
        x.controlFocus("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.sleep(2000);
        AutoITLib.clickControlInWindow_NEWInUse("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:1]", 83, 62);
        x.sleep(2000);
        logMessage("Click the first row in Super Contribution table.");
        clearClipboard();
        x.send("^c", false);
        x.sleep(2000);
        currentSelection=getClipboard();
        lastSelection=currentSelection;
        String strContributionType=null;
        switch (contributionType){
            case "1":
                strContributionType="Employer Contribution";
                break;
            case "2":
                strContributionType="Employee Contribution";
                break;
        }

        String expectedString=fundDescription+"\t"+strContributionType+"\t"+contributionName;
        logMessage("The expected String is '"+expectedString+"'.");

        if (currentSelection.contains(expectedString)){
            logMessage("Super Contribution '"+fundDescription+"' is found.");
            isFound=true;
        }else{
            while(isFound==false){
                x.controlFocus("Edit Employee ", "", "[CLASS:TcxGridSite; INSTANCE:1]");
                x.send("{DOWN}", false);
                x.sleep(2000);
                clearClipboard();
                x.send("^c", false);
                x.sleep(2000);
                currentSelection=getClipboard();
                if (currentSelection.contains(expectedString)){
                    logMessage("Super Contribution '"+fundDescription+"' is found.");
                    isFound=true;
                    break;
                }
                if (currentSelection.equals(lastSelection)){
                    logWarning("End of Super Contribution list, Super Contribution '"+fundDescription+"' is NOT found.");
                    break;
                }
                lastSelection=currentSelection;
            }
        }

        return isFound;
    }

    public static boolean editEmployee_TaxDetails(String firstName, String middleName, String lastName, String preferredNamem, String tfnType, String tfn, String tfnNameTitle, String tfnSurname, String tfnFirstname, String tfnMiddlename, String tfnHomeAddressStreet, String tfnHomeAddressSuburb, String tfgHomeAddressState, String tfnHomeAddressPostcode, String tfnHomeAddressCountry, String tfnPreviouseSurname, String tfnDOB, String tfnBasisOfPayment, String tfnTaxStatus, String tfnTaxfreeThreshold, String tfnHELP_SSL_TSLDebt, String tfnFinancialSupplementDebt, String tfnEmployeeSignaturePresent, String tfnDateDeclaration, String tfnForSuper, String tfnTaxType, String tfnTaxScaleCode, String applyDailyTaxing, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException, IOException, UnsupportedFlavorException {
        boolean isDone = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();
        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {

            displayEmployee_TaxDetails();

            if ((tfnType!=null)||(tfn!=null)||(tfnNameTitle!=null)||(tfnSurname!=null)||(tfnFirstname!=null)||(tfnMiddlename!=null)||(tfnHomeAddressStreet!=null)||(tfnHomeAddressSuburb!=null)||(tfgHomeAddressState!=null)||(tfnHomeAddressPostcode!=null)||(tfnHomeAddressCountry!=null)||(tfnPreviouseSurname!=null)||(tfnDOB!=null)||(tfnBasisOfPayment!=null)||(tfnTaxStatus!=null)||(tfnTaxfreeThreshold!=null)||(tfnHELP_SSL_TSLDebt!=null)||(tfnFinancialSupplementDebt!=null)||(tfnEmployeeSignaturePresent!=null)||(tfnDateDeclaration!=null)){
                x.controlFocus("Edit Employee", "TFN Declaration", "[CLASS:TButton; INSTANCE:1]");
                x.controlClick("Edit Employee", "TFN Declaration", "[CLASS:TButton; INSTANCE:1]");
                x.sleep(4000);
                logMessage("Button TFN Declaration is clicked.");
                x.winActivate("Employee TFN Declaration");
                AutoITLib.logScreenshotX();


                if (tfnType!=null){
                    x.controlFocus("Employee TFN Declaration", "", "[CLASS:TevComboBox; INSTANCE:2]");
                    x.controlClick("Employee TFN Declaration", "", "[CLASS:TevComboBox; INSTANCE:2]");
                    x.send("^a", false);
                    x.send(tfnType+"{TAB}", false);
                    x.sleep(1000);
                    logMessage("TFN Type '"+tfnType+"' is input.");
                }

                if (tfn!=null){
                    x.controlFocus("Employee TFN Declaration", "", "[CLASS:TevTextEdit; INSTANCE:5]");
                    x.controlClick("Employee TFN Declaration", "", "[CLASS:TevTextEdit; INSTANCE:5]");
                    x.send("^a", false);
                    x.send(tfn+"{TAB}", false);
                    x.sleep(1000);
                    logMessage("Tax File Number '"+tfn+"' is input.");
                }

                if (tfnTaxStatus!=null){
                    switch (tfnTaxStatus){
                        case "1":
                            x.controlFocus("Employee TFN Declaration", " An Australian           resident", "[CLASS:TevRadioButton; INSTANCE:3]");
                            x.controlClick("Employee TFN Declaration", " An Australian           resident", "[CLASS:TevRadioButton; INSTANCE:3]");
                            x.sleep(1000);
                            logMessage("Checkbox 'An Australian resident' is ticked.");
                            break;
                        case "2":
                            x.controlFocus("Employee TFN Declaration", "A foreign    resident", "[CLASS:TevRadioButton; INSTANCE:1]");
                            x.controlClick("Employee TFN Declaration", "A foreign    resident", "[CLASS:TevRadioButton; INSTANCE:1]");
                            x.sleep(1000);
                            logMessage("Checkbox 'A foreign resident' is ticked.");
                            break;
                        case "3":
                            x.controlFocus("Employee TFN Declaration", "      A working   holiday maker", "[CLASS:TevRadioButton; INSTANCE:2]");
                            x.controlClick("Employee TFN Declaration", "      A working   holiday maker", "[CLASS:TevRadioButton; INSTANCE:2]");
                            x.sleep(1000);
                            logMessage("Checkbox 'A working holiday maker' is ticked.");
                            break;
                    }
                }

                //////////////////////
                if (tfnHELP_SSL_TSLDebt!=null){
                    if (tfnHELP_SSL_TSLDebt.equals("1")){
                        x.controlFocus("Employee TFN Declaration", "", "[CLASS:TevCheckBox; INSTANCE:2]");
                        x.send("{SPACE}", false);
                        x.sleep(1000);
                        logMessage("TFN Have a HELP/SSL/TSL is checked.");
                    }
                }

                if (tfnFinancialSupplementDebt!=null){
                    if (tfnFinancialSupplementDebt.equals("1")){
                        x.controlFocus("Employee TFN Declaration", "", "[CLASS:TevCheckBox; INSTANCE:2]");
                        x.send("{SPACE}", false);
                        x.sleep(1000);
                        logMessage("TFN Financial Supplement Debt is checked.");
                    }
                }
                //////

                logMessage("Screenshot before click OK button in 'Employee TFN Declaration' form.");
                logScreenshotX();

                x.controlFocus("Employee TFN Declaration", "&OK", "[CLASS:TButton; INSTANCE:3]");
                x.controlClick("Employee TFN Declaration", "&OK", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(4000);
                logMessage("OK button is clicked in 'Employee TFN Declaration' form.");
            }

            if (tfnTaxScaleCode!=null) {
                x.controlFocus("Edit Employee", "", "[CLASS:TevPRTaxScalesLookupCombo; INSTANCE:1]");
                x.controlClick("Edit Employee", "", "[CLASS:TevPRTaxScalesLookupCombo; INSTANCE:1]");
                x.send("^a", false);
                x.send(tfnTaxScaleCode+"{TAB}", false);
                x.sleep(1000);
                logMessage("TFN Tax Scale Code '"+tfnTaxScaleCode+"' is input.");
            }

			x.sleep(4000);
            x.controlFocus("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:6]");
            x.controlClick("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:6]");
            x.sleep(4000);
            logMessage("Ok Button is clicked in Tax Detail Screen.");
            AutoITLib.logScreenshotX();


            //////////////////////////
            logMessage("Screenshot before close Employee Tax Deails screen. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();

        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean displayEmployee_TaxDetails() throws InterruptedException {
        //display employee detail screen first
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 27, 25);
        x.sleep(4000);

        logMessage("Employee Tax Details screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }

    public static boolean displayEmployeeDetailsScreen(String firstName, String lastName, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isShown = false;
        int errorCounter = 0;

        String employeeCode = DBManage.getEmployeeCodeFromPayrollDB(serverName, payrollDBName, firstName, lastName);
        AutoItX x = new AutoItX();
        OPT_WIN_TITLE_MATCH_MODE.equals(2);

        String windowTitle="Edit Employee \" "+employeeCode+" - "+lastName+" "+firstName+" \" ";
        if (!x.winWait(windowTitle, "", 10)){
            /////////////////////////
            displayEmployeeListScreen();
            x.winActivate("Sage MicrOpay");

            x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
            x.controlSend("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "{UP}", false);
            x.controlSend("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", "{DOWN}", false);

            x.controlSend("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", employeeCode, false);
            Thread.sleep(1000);
            logMessage("Employee Code '" + employeeCode + "' is input.");

            x.send("{ENTER}", false);
            logMessage("Enter Key is pressed.");
            Thread.sleep(2000);

            //Wait for exception pop up.
            if (x.winWait("Employee Maintenance", "", 4)){
                logMessage("Confirmation Screen is popping up.");
                logScreenshotX();
                x.winActivate("Employee Maintenance");
                x.controlFocus("Employee Maintenance", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                x.controlClick("Employee Maintenance", "&Yes", "[CLASS:TButton; INSTANCE:2]");
                x.sleep(5000);
                logMessage("Button YES is clicked.");
            }

            if (x.winWait(windowTitle, "", 30)) {
                x.winActivate(windowTitle);
                logMessage("Employee " + lastName + " " + firstName + "'s detail screen is shown.");
                Thread.sleep(1000);
            } else {
                logError("Employee " + lastName + " " + firstName + "'s detail screen is NOT shown.");
                errorCounter++;
                x.controlClick("Edit Employee", "Cancel", "[CLASS:TButton; INSTANCE:3]");
                logMessage("Wrong Employee Detail screen is closed.");
            }
            //////
        }else{
            logMessage("Employee Detail screen is being shown.");
            x.winActivate(windowTitle);
        }

        logScreenshotX();
        if (errorCounter == 0) isShown = true;
        return isShown;
    }

    public static boolean highlightItemInImplementEHRScreen(String employeeCode, String firstName, String lastName) throws InterruptedException, SQLException, ClassNotFoundException, IOException, UnsupportedFlavorException {
        boolean isPassed=false;
        int errorCounter=0;

        AutoItX x = new AutoItX();

        SystemLibrary.clearClipboard();
        x.controlFocus("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.controlSend("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]", "^c");
        x.sleep(3000);

        String initialItem="";
        String currentItem=SystemLibrary.getClipboard();
        initialItem=currentItem;
        if ((currentItem.contains(employeeCode))&&(currentItem.contains(firstName))&&(currentItem.contains(lastName))){
            logMessage("Employee '"+employeeCode+"' is found.");
        }else{
            for (int i=0;i<100;i++){
                x.controlSend("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]", "{DOWN}", false);
                x.sleep(2000);
                SystemLibrary.clearClipboard();
                x.controlSend("Implement HR Changes", "", "[CLASS:TcxGridSite; INSTANCE:1]", "^c");
                currentItem=SystemLibrary.getClipboard();

                if (currentItem.equals(initialItem)){
                    logMessage("End of list.");
                    logError("Employee '" + employeeCode + "' is NOT found.");
                    errorCounter++;
                    break;
                }

                if ((currentItem.contains(employeeCode))&&(currentItem.contains(firstName))&&(currentItem.contains(lastName))) {
                    logMessage("Employee '" + employeeCode + "' is found.");
                    break;
                }

                initialItem=currentItem;

            }

        }

        logScreenshotX();
        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean printEHRReport(String storeFileName, String isUpdate, String isCompare, String expectedContent, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;
        AutoItX x=new AutoItX();
        display_ImplementeHRScreen();

        x.controlFocus("Implement HR Changes", "eHR Report", "[CLASS:TButton; INSTANCE:2]");
        x.controlClick("Implement HR Changes", "eHR Report", "[CLASS:TButton; INSTANCE:2]");
        x.sleep(2000);
        logMessage("Button 'eHR Report' is clicked.");
        logScreenshotX();

        if (x.winWait("Print Preview", "", 10)){
            logMessage("eHR Report Privew screen is shown.");

            x.controlFocus("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]");
            //x.controlClick("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]", "1", 1, 19, 12);
            AutoITLib.clickControlInWindow_NEWInUse("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]", 19, 12);
            logMessage("Print button is clicked in eHR Report preview screen.");
            x.sleep(2000);
            logScreenshotX();

            if (x.winWait("Print", "", 10)){
                String fileName = "eHRReport" + getCurrentTimeAndDate() + ".pdf";
                String fileFullPathName = workingFilePath + fileName;

                if (!print_File(fileFullPathName)) {
                    errorCounter++;
                }

                x.controlFocus("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]");
                AutoITLib.clickControlInWindow_NEWInUse("Print Preview", "", "[CLASS:TppToolbar; INSTANCE:1]", 470, 12);
                x.sleep(2000);
                logMessage("eHR Report preview Screen is closed.");
                logScreenshotX();

                String sReportContent = SystemLibrary.getStringFromPDFFile(fileFullPathName);
                if (!SystemLibrary.validateStringContainInFile(sReportContent, storeFileName, isUpdate, isCompare, expectedContent, emailDomainName, testSerialNo))
                    errorCounter++;


            }else{
                logError("System print dialogue is NOT shown.");
                errorCounter++;
            }

        }else{
            logError("eHR Report Privew screen is NOT shown.");
            errorCounter++;
        }

        AutoITLib.close_ImplementHRScreen();

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean editEmployee_PersonalInformation1(String firstName, String middleName, String lastName, String preferredName, String title, String gender, String maritialStatus, String dob, String dateHired, String aboriginalTorresStrait, String residentialStreet, String residentialSuburb, String residentialState, String residentialPostcode, String residentialCountry, String postalAddress, String postalSuburb, String postalState, String postalPostcode, String postalCountry, String telephone, String mobilePhone, String emailAddress, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            AutoItX x = new AutoItX();

            if (gender!=null){
                x.winActivate("Edit Employee");
                x.controlClick("Edit Employee", "","[CLASS:TevIPGenderLookupCombo; INSTANCE:1]");
                x.controlSend("Edit Employee", "","[CLASS:TevIPGenderLookupCombo; INSTANCE:1]", "^a");
                x.sleep(2000);
                x.send("{DEL}", false);
                x.send(gender);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Gender '"+gender+"' is input.");
            }

            if (maritialStatus!=null){
                x.winActivate("Edit Employee");
                x.controlClick("Edit Employee", "","[CLASS:TevIPMaritalStatusLookupCombo; INSTANCE:1]");
                x.controlSend("Edit Employee", "","[CLASS:TevIPMaritalStatusLookupCombo; INSTANCE:1]", "^a");
                x.sleep(2000);
                x.send("{DEL}", false);
                x.send(maritialStatus);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Maritial Status '"+maritialStatus+"' is input.");
            }

            if (postalPostcode!=null){
                x.winActivate("Edit Employee");
                x.controlClick("Edit Employee", "","[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]");
                x.controlSend("Edit Employee", "","[CLASS:TcxCustomInnerTextEdit; INSTANCE:4]", "^a");
                x.sleep(2000);
                x.send("{DEL}", false);
                x.send(postalPostcode);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Postal Postcode '"+postalPostcode+"' is input.");
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }


    public static boolean editEmployee_PayDetails1(String firstName, String middleName, String lastName, String preferredNamem, String payrollCompany, String location, String payPoint, String payFrequency, String deliveryMethod, String paymentMethod, String awardBased, String autoPay, String recommenceDate, String employmentType, String jobClassification, String minPayLimit, String maxPayLimit, String serviceHoursToDate, String contractHours, String workersComp, String payrollTax, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_PayDetails();

            AutoItX x = new AutoItX();

            if (location != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:6]");
                x.controlSend("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:6]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(location);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Location '" + location + "' is input.");
            }

            if (employmentType != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevIPEmploymentTypeLookupCombo; INSTANCE:1]");
                x.controlSend("Edit Employee", "", "[CLASS:TevIPEmploymentTypeLookupCombo; INSTANCE:1]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(employmentType);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Employment Type '" + employmentType + "' is input.");
            }

            if (jobClassification != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevIPJobClassificationLookupCombo; INSTANCE:1]");
                x.controlSend("Edit Employee", "", "[CLASS:TevIPJobClassificationLookupCombo; INSTANCE:1]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(jobClassification);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Job Classification '" + jobClassification + "' is input.");
            }



            if (contractHours != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]");
                x.controlSend("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(contractHours);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Contract Hours '" + contractHours + "' is input.");
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

            //closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }


    public static boolean validateNewEmployeeMaintenanceScreen(String storeFileName, String isUpdate, String isCompare, String expectedContent) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;


        AutoItX x = new AutoItX();
        if (display_NewEmployees()) {

            String fileName="NewEmployeeList_"+getCurrentTimeAndDate()+".csv";
            String fileFullPathName= workingFilePath+fileName;

            x.controlFocus("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]");
            AutoITLib.clickControlInWindow_NEWInUse("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]", 267, 11);
            x.sleep(5000);
            x.controlFocus("Save Grid to File", "", "[CLASS:ComboBox; INSTANCE:1]");
            x.send(fileFullPathName);

            logMessage("Screenshot befor click Save button.");
            logScreenshotX();

            x.controlFocus("Save Grid to File", "&Save", "[CLASS:Button; INSTANCE:2]");
            x.controlClick("Save Grid to File", "&Save", "[CLASS:Button; INSTANCE:2]");
            x.sleep(5000);
            logMessage("Save button is clicked.");

            x.controlFocus("New Employees Maintenance", "&No", "[CLASS:TButton; INSTANCE:1]");
            x.controlClick("New Employees Maintenance", "&No", "[CLASS:TButton; INSTANCE:1]");
            x.sleep(2000);
            logMessage("No preview button is clicked.");


            logMessage("New employee list is saved as '"+fileFullPathName+"'.");

            if (!updateAndValidateStoreStringFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent)) errorCounter++;

            closeNewEmployeeScreen();
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;

    }

    public static boolean editPayrollCompany(String code, String name, String tradingAs, String EFTInstitution, String superFUND) throws InterruptedException {
        boolean isDone=false;
        int errorCounter=0;

        AutoItX x=new AutoItX();
        display_PayrollCompanies();


        AutoITLib.clickControlInWindow_NEWInUse("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", 60,60);
        x.sleep(2000);
        logMessage("Click the first row in Payroll Company table.");


        x.winActivate("Sage MicrOpay");

        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.controlSend("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", code, false);
        Thread.sleep(1000);
        logMessage("Employee Code '" + code + "' is input.");


        x.send("{ENTER}", false);
        logMessage("Enter Key is pressed.");
        Thread.sleep(2000);


        if (x.winWaitActive("Edit Payroll Company \""+name+"\" (Payer)", "", 10)){
            logMessage("Edit Payroll Company Screen is shown.");

            if (name!=null){
                x.controlFocus("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:16]");
                x.controlClick("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:16]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(name);
                logMessage("Name '"+name+"' is input.");
            }

            if (tradingAs!=null){
                x.controlFocus("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:15]");
                x.controlClick("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevTextEdit; INSTANCE:15]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(tradingAs);
                x.send("{TAB}", false);
                logMessage("Trading as '"+tradingAs+"' is input.");
            }

            if (EFTInstitution!=null){
                x.controlFocus("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevIPEFTParametersLookupCombo; INSTANCE:1]");
                x.controlClick("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevIPEFTParametersLookupCombo; INSTANCE:1]");
                x.send(EFTInstitution);
                x.send("{TAB}", false);
                logMessage("EFT Institution '"+EFTInstitution+"' is input.");
            }
            logScreenshotX();

            if (superFUND!=null) {
                x.controlFocus("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                x.controlClick("Edit Payroll Company \""+name+"\" (Payer)", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                x.send(superFUND);
                x.send("{TAB}", false);
                logMessage("Super Fund '" + superFUND + "' is input.");
            }
            logMessage("Screenshot before click Save button.");
            AutoITLib.logScreenshotX();

            x.controlFocus("Edit Payroll Company \""+name+"\" (Payer)", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Edit Payroll Company \""+name+"\" (Payer)", "OK", "[CLASS:TButton; INSTANCE:2]");
            logMessage("OK button is clicked.");
            x.sleep(3000);
            logScreenshotX();

        }else{
            logError("Edit Payroll Company screen is NOT shwon.");
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean editNewEmployee_New(String homeState, String postalState, String employeeCode, String firstName, String lastName, String location, String payPoint, String payFrequency, String defaultCostAccount, String superFund, String memberNumber, String employerContribution, String addNewEmployeeToPayroll, String saveNewEmployee) throws InterruptedException, IOException, UnsupportedFlavorException {
        //To be completed ....

        boolean isDone=false;
        int errorCounter=0;

        logMessage("Debug here.");

        AutoItX x=new AutoItX();

        if (display_NewEmployees()){

            SystemLibrary.clearClipboard();

            String initialContent="";
            String currentContent="";
            int rowNumber=1;

            x.winActivate("New Employees Maintenance");

            x.sleep(1000);
            AutoITLib.click_Window("New Employees Maintenance", 60, 196);
            x.sleep(1000);
            logMessage("The first row is highlighted.");
            x.send("^c", false);
            x.sleep(2000);
            initialContent=currentContent=SystemLibrary.getClipboard();

            if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                logMessage("Employee "+firstName+" "+lastName+" is found.");
                AutoITLib.doubleClick_Window("New Employees Maintenance", 60, 196);
                initialContent=currentContent;
            }else{
                while (rowNumber<20){
                    rowNumber++;
                    x.send("{DOWN}", false);
                    x.sleep(1000);
                    logMessage("The row "+rowNumber+" is highlighted.");
                    x.send("^c", false);
                    x.sleep(2000);
                    currentContent=SystemLibrary.getClipboard();
                    if (initialContent.equals(currentContent)){
                        logMessage("End of list.");
                        logError("Employeee "+firstName+" "+lastName+"is NOT found.");
                        errorCounter++;
                        break;
                    }

                    if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                        logMessage("Employee "+firstName+" "+lastName+" is found.");
                        AutoITLib.doubleClick_Window("New Employees Maintenance", 60, 196+18*(rowNumber-1));
                        break;
                    }
                    initialContent=currentContent;
                }
            }

            if (x.winWait("Edit New Employee", "", 10)) {
                logMessage("Edit New Employee screen is shown.");
                logScreenshotX();

                /////////////////// Setup Screen //////////////

                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 1st Next button is clicked in New Emplyee - Setup screen.");
                logScreenshotX();

                //////////////// THF Declaration 1/2 Screen ///////////////

                if (homeState!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevStateLookupCombo; INSTANCE:1]");
                    x.send(homeState+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Home State '"+homeState+"' is input.");
                }

                logScreenshotX();
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 2nd Next button is clicked in New Emplyee - TFN - Declaration 1/2 screen.");
                logScreenshotX();

                //////////////// THF Declaration 2/2 Screen ///////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 3rd Next button is clicked in New Emplyee - TFN - Declaration 2/2 screen.");
                logScreenshotX();

                //////////////// Personal Details Screen //////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 4th Next button is clicked in New Emplyee - Personal Details screen.");
                logScreenshotX();

                //////////////// Contact Details screen //////////////

                if (postalState!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevStateLookupCombo; INSTANCE:1]");
                    x.send(postalState+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Postal State '"+postalState+"' is input.");
                }

                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

             /* x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
               */
                x.sleep(2000);
                logMessage("The 5th Next button is clicked in New Emplyee - Contact Details screen.");
                logScreenshotX();

                //////////// Employment screen /////////////
                if (employeeCode!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(employeeCode+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employee Code '"+employeeCode+"' is input.");
                }

                if (location!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPLocationLookupCombo; INSTANCE:1]");
                    x.send(location+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Location '"+location+"' is input.");
                }

                if (payPoint!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayPointLookupCombo; INSTANCE:1]");
                    x.send(payPoint+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("PayPoint '"+payPoint+"' is input.");
                }

                if (payFrequency!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayFrequencyLookupCombo; INSTANCE:1]");
                    x.send(payFrequency+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Pay Frequency '"+payFrequency+"' is input.");
                }

                if (defaultCostAccount!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                    x.send(defaultCostAccount+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Default Cost Account '"+defaultCostAccount+"' is input.");
                }

                logMessage("Screenshot before click the 6th Next button is clicked in New Emplyee - Employeement screen");
                logScreenshotX();

                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

             /* x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
             */
                x.sleep(2000);
                logMessage("The 6th Next button is clicked in New Emplyee - Employeement screen.");
                logScreenshotX();

                ///////////// Superannuation Screen /////////////////

                if (superFund!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                    x.send(superFund+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Super Fund '"+superFund+"' is input.");
                }


                if (memberNumber!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(memberNumber+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Member Number '"+memberNumber+"' is input.");
                }

                if (employerContribution!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperSchemeLookupCombo; INSTANCE:1]");
                    x.send(employerContribution+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employer Contribution '"+employerContribution+"' is input.");
                }

                logMessage("Screenshot before click the 7th Next button in New Employee - Superannuation screen.");
                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

                /*x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
                */
                x.sleep(2000);
                logMessage("The 7th Next button is clicked in New Emplyee - Superannuation screen.");
                logScreenshotX();

                ////////////// Bank Account Screen //////////////////
                logMessage("Screenshot before click the 8th Next button in New Employee - Bank Account screen.");
                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

                /*x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:9]");*/
                x.sleep(2000);
                logMessage("The 8th Next button is clicked in New Emplyee - Bank Account screen.");
                logScreenshotX();

                /////////////// Confirmation Screen ////////////////
                if (addNewEmployeeToPayroll!=null){
                    if (addNewEmployeeToPayroll.equals("1")){
                        x.winActivate("Edit New Employee");
                        x.controlFocus("Edit New Employee", "Add New Employee to Payroll", "[CLASS:TCheckBox; INSTANCE:1]");
                        x.send("{SPACE}", false);
                        logMessage("Checkbox - Add New Employee to Payroll is clicked.");
                        logScreenshotX();
                    }
                }

                /////////////// Save Screen ////////////////
                if (saveNewEmployee!=null){
                    if (saveNewEmployee.equals("1")){
                        logMessage("Save button before clicked .");
                        x.winActivate("Edit New Employee");
                        AutoITLib.click_Window("Edit New Employee", 600,610);

                       /* x.controlGetFocus("Edit New Employee", "Save");
                        x.controlClick("Edit New Employee", "Save", "[CLASS:TButton; INSTANCE:8]");*/

                        x.sleep(2000);
                        logMessage("Save button is clicked .");
                    }
                }


                if (x.winWait("Edit Employee", "", 120)){
                    logMessage("Edit Employee Screen is shown.");

                    x.controlGetFocus("Edit Employee", "OK");
                    x.controlClick("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:4]");
                    x.sleep(10000);
                    logMessage("OK button is clicked in Edit Employee screen.");
                }else{
                    logMessage("Edit Employee screen is NOT shown.");
                }

                logMessage("Screenshot before close New Employee Screen.");
                logScreenshotX();
                closeNewEmployeeScreen();

            }else{
                logError("Edit New Employee screen is NOT shown.");
            }

        }else{
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;

    }


    public static boolean editNewEmployee_Voluntary(String employeeCode, String firstName, String lastName, String location, String payPoint, String payFrequency, String defaultCostAccount, String superFund, String memberNumber, String employerContribution, String addNewEmployeeToPayroll, String saveNewEmployee) throws InterruptedException, IOException, UnsupportedFlavorException {
        //To be completed ....

        boolean isDone=false;
        int errorCounter=0;

        logMessage("Debug here.");

        AutoItX x=new AutoItX();

        if (display_NewEmployees()){

            SystemLibrary.clearClipboard();

            String initialContent="";
            String currentContent="";
            int rowNumber=1;

            x.winActivate("New Employees Maintenance");

            x.sleep(1000);
            AutoITLib.click_Window("New Employees Maintenance", 60, 196);
            x.sleep(1000);
            logMessage("The first row is highlighted.");
            x.send("^c", false);
            x.sleep(2000);
            initialContent=currentContent=SystemLibrary.getClipboard();

            if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                logMessage("Employee "+firstName+" "+lastName+" is found.");
                AutoITLib.doubleClick_Window("New Employees Maintenance", 60, 196);
                initialContent=currentContent;
            }else{
                while (rowNumber<20){
                    rowNumber++;
                    x.send("{DOWN}", false);
                    x.sleep(1000);
                    logMessage("The row "+rowNumber+" is highlighted.");
                    x.send("^c", false);
                    x.sleep(2000);
                    currentContent=SystemLibrary.getClipboard();
                    if (initialContent.equals(currentContent)){
                        logMessage("End of list.");
                        logError("Employeee "+firstName+" "+lastName+"is NOT found.");
                        errorCounter++;
                        break;
                    }

                    if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                        logMessage("Employee "+firstName+" "+lastName+" is found.");
                        AutoITLib.doubleClick_Window("New Employees Maintenance", 60, 196+18*(rowNumber-1));
                        break;
                    }
                    initialContent=currentContent;
                }
            }

            if (x.winWait("Edit New Employee", "", 10)) {
                logMessage("Edit New Employee screen is shown.");
                logScreenshotX();

                /////////////////// Setup Screen //////////////

                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 1st Next button is clicked in New Emplyee - Setup screen.");
                logScreenshotX();

                //////////////// Voluntary  ///////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 2nd Next button is clicked in New Emplyee - TFN - Declaration 1/2 screen.");
                logScreenshotX();

                //////////////// Personal Details Screen //////////////
                x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
                x.sleep(2000);
                logMessage("The 4th Next button is clicked in New Emplyee - Personal Details screen.");
                logScreenshotX();

                //////////////// Contact Details screen //////////////
                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

             /* x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:3]");
               */
                x.sleep(2000);
                logMessage("The 5th Next button is clicked in New Emplyee - Contact Details screen.");
                logScreenshotX();

                //////////// Employment screen /////////////
                if (employeeCode!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(employeeCode+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employee Code '"+employeeCode+"' is input.");
                }

                if (location!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPLocationLookupCombo; INSTANCE:1]");
                    x.send(location+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Location '"+location+"' is input.");
                }

                if (payPoint!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayPointLookupCombo; INSTANCE:1]");
                    x.send(payPoint+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("PayPoint '"+payPoint+"' is input.");
                }

                if (payFrequency!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPPayFrequencyLookupCombo; INSTANCE:1]");
                    x.send(payFrequency+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Pay Frequency '"+payFrequency+"' is input.");
                }

                if (defaultCostAccount!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPCostAccountsLookupCombo; INSTANCE:1]");
                    x.send(defaultCostAccount+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Default Cost Account '"+defaultCostAccount+"' is input.");
                }

                logMessage("Screenshot before click the 6th Next button is clicked in New Emplyee - Employeement screen");
                logScreenshotX();

                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

             /* x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
             */
                x.sleep(2000);
                logMessage("The 6th Next button is clicked in New Emplyee - Employeement screen.");
                logScreenshotX();

                ///////////// Superannuation Screen /////////////////

                if (superFund!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperFundLookupCombo; INSTANCE:1]");
                    x.send(superFund+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Super Fund '"+superFund+"' is input.");
                }


                if (memberNumber!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.controlClick("Edit New Employee", "", "[CLASS:TevTextEdit; INSTANCE:1]");
                    x.send(memberNumber+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Member Number '"+memberNumber+"' is input.");
                }

                if (employerContribution!=null){
                    x.controlFocus("Edit New Employee", "", "[CLASS:TevIPSuperSchemeLookupCombo; INSTANCE:1]");
                    x.send(employerContribution+"{TAB}", false);
                    x.sleep(2000);
                    logMessage("Employer Contribution '"+employerContribution+"' is input.");
                }

                logMessage("Screenshot before click the 7th Next button in New Employee - Superannuation screen.");
                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

                /*x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
                */
                x.sleep(2000);
                logMessage("The 7th Next button is clicked in New Emplyee - Superannuation screen.");
                logScreenshotX();

                ////////////// Bank Account Screen //////////////////
                logMessage("Screenshot before click the 8th Next button in New Employee - Bank Account screen.");
                x.winActivate("Edit New Employee");
                AutoITLib.click_Window("Edit New Employee", 510,610);

                /*x.controlGetFocus("Edit New Employee", "Next >");
                x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:9]");*/
                x.sleep(2000);
                logMessage("The 8th Next button is clicked in New Emplyee - Bank Account screen.");
                logScreenshotX();

                /////////////// Confirmation Screen ////////////////
                if (addNewEmployeeToPayroll!=null){
                    if (addNewEmployeeToPayroll.equals("1")){
                        x.winActivate("Edit New Employee");
                        x.controlFocus("Edit New Employee", "Add New Employee to Payroll", "[CLASS:TCheckBox; INSTANCE:1]");
                        x.send("{SPACE}", false);
                        logMessage("Checkbox - Add New Employee to Payroll is clicked.");
                        logScreenshotX();
                    }
                }

                /////////////// Save Screen ////////////////
                if (saveNewEmployee!=null){
                    if (saveNewEmployee.equals("1")){
                        logMessage("Save button before clicked .");
                        x.winActivate("Edit New Employee");
                        AutoITLib.click_Window("Edit New Employee", 600,610);

                       /* x.controlGetFocus("Edit New Employee", "Save");
                        x.controlClick("Edit New Employee", "Save", "[CLASS:TButton; INSTANCE:8]");*/

                        x.sleep(2000);
                        logMessage("Save button is clicked .");
                    }
                }


                if (x.winWait("Edit Employee", "", 120)){
                    logMessage("Edit Employee Screen is shown.");

                    x.controlGetFocus("Edit Employee", "OK");
                    x.controlClick("Edit Employee", "OK", "[CLASS:TButton; INSTANCE:4]");
                    x.sleep(10000);
                    logMessage("OK button is clicked in Edit Employee screen.");
                }else{
                    logMessage("Edit Employee screen is NOT shown.");
                }

                logMessage("Screenshot before close New Employee Screen.");
                logScreenshotX();
                closeNewEmployeeScreen();

            }else{
                logError("Edit New Employee screen is NOT shown.");
            }

        }else{
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;

    }

    public static boolean display_LookupTables() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("Lookup Tables", 1);
        logMessage("Lookup Tables screen is shwon.");
        return isShown;
    }

    public static boolean addLookup(String employmentType, String gender, String jobClassification, String maritalStatus) throws InterruptedException {
        boolean isDone=false;
        int errorCounter=0;

        AutoItX x=new AutoItX();
        display_LookupTables();

        x.winActivate("Lookup Table Maintenance");
        x.sleep(1000);

        //////////////// Display Payroll Tab Screen //////////////
        x.winActivate("Lookup Table Maintenance");
        AutoITLib.click_Window("Lookup Table Maintenance", 25,72);
        x.sleep(2000);
        logMessage("Payroll Tab is clicked in Lookup Table Maintenance screen.");
        logScreenshotX();

        //////////// Add Employment Type /////////////
        if (employmentType!=null){
            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 45,115);
            x.sleep(2000);
            logMessage("Employment Type is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 100,45);
            x.sleep(2000);
            logMessage("Add Employment Type is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            x.controlFocus("Lookup Table Maintenance", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
            x.send(employmentType+"{TAB}", false);
            x.sleep(2000);
            logMessage("Employment Type '"+gender+"' is input.");
            logScreenshotX();
        }

        //////////// Add Gender /////////////
        if (gender!=null){
            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 45,135);
            x.sleep(2000);
            logMessage("Gender is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 100,45);
            x.sleep(2000);
            logMessage("Add Gender is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            x.controlFocus("Lookup Table Maintenance", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
            x.send(gender+"{TAB}", false);
            x.sleep(2000);
            logMessage("Gender '"+gender+"' is input.");
            logScreenshotX();
        }

        //////////// Job Classification /////////////
        if (jobClassification!=null){
            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 45,175);
            x.sleep(2000);
            logMessage("Job Classification is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 100,45);
            x.sleep(2000);
            logMessage("Add Job Classification is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            x.controlFocus("Lookup Table Maintenance", "", "[CLASS:TcxMaskEdit; INSTANCE:1]");
            x.send(jobClassification+"{TAB}", false);
            x.sleep(2000);
            logMessage("Job Classification '"+jobClassification+"' is input.");
            logScreenshotX();
        }

        //////////// Marital Status /////////////
        if (maritalStatus!=null){
            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 45,215);
            x.sleep(2000);
            logMessage("Marital Status is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            AutoITLib.click_Window("Lookup Table Maintenance", 100,45);
            x.sleep(2000);
            logMessage("Add Marital Status is clicked in Lookup Table Maintenance screen.");

            x.winActivate("Lookup Table Maintenance");
            x.controlFocus("Lookup Table Maintenance", "", "[CLASS:TcxCustomInnerTextEdit; INSTANCE:1]");
            x.send(maritalStatus+"{TAB}", false);
            x.sleep(2000);
            logMessage("Marital Status '"+maritalStatus+"' is input.");
            logScreenshotX();
        }

        logMessage("Screenshot before Lookup Table Maintenance Close");
        logScreenshotX();

        x.winActivate("Lookup Table Maintenance");
        AutoITLib.click_Window("Lookup Table Maintenance", 48,48);
        x.sleep(2000);
        logMessage("Screenshot After Lookup Table Maintenance Close.");
        logScreenshotX();

        if (errorCounter==0) isDone=true;
        return isDone;

    }

    public static boolean processbatch_NewEmployeeMaintenanceScreen(String selectall, String ProcessBatch) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        AutoItX x = new AutoItX();
        if (display_NewEmployees()) {

           if (selectall != null) {
                x.controlFocus("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]");
                AutoITLib.clickControlInWindow_NEWInUse("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]", 1745, 110);
                x.sleep(5000);
                logMessage("Screenshot before click Select All button.");
                logScreenshotX();
                x.controlClick("New Employees Maintenance", "Maintenance", "[CLASS:TevColorButton; INSTANCE:4]");
                logMessage("Screenshot After click Select All button.");
                logScreenshotX();
            }

            if (ProcessBatch != null) {
                x.controlFocus("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]");
                AutoITLib.clickControlInWindow_NEWInUse("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]", 1850, 110);
                x.sleep(5000);
                x.controlClick("New Employees Maintenance", "Maintenance", "[CLASS:TevColorButton; INSTANCE:3]");
                x.winActivate("New Employees Maintenance");
                AutoITLib.click_Window("New Employees Maintenance", 120, 105);
                logMessage("Screenshot After click Process Batch button.");
                logScreenshotX();
                x.sleep(5000);


            }


            closeNewEmployeeScreen();
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean deleteNewEmployeeMaintenanceScreen(String firstName, String lastName, String deleteNewStarter) throws InterruptedException, IOException, UnsupportedFlavorException {
        boolean isDone=false;
        int errorCounter=0;

        logMessage("Debug here.");

        AutoItX x=new AutoItX();

        if (display_NewEmployees()){

            SystemLibrary.clearClipboard();

            String initialContent="";
            String currentContent="";
            int rowNumber=1;

            x.winActivate("New Employees Maintenance");

            x.sleep(1000);
            AutoITLib.click_Window("New Employees Maintenance", 60, 196);
            x.sleep(1000);
            logMessage("The first row is highlighted.");
            x.send("^c", false);
            x.sleep(2000);
            initialContent=currentContent=SystemLibrary.getClipboard();

            if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                logMessage("Employee "+firstName+" "+lastName+" is found.");
                AutoITLib.click_Window("New Employees Maintenance", 60, 196);
                initialContent=currentContent;
            }else{
                while (rowNumber<20){
                    rowNumber++;
                    x.send("{DOWN}", false);
                    x.sleep(1000);
                    logMessage("The row "+rowNumber+" is highlighted.");
                    x.send("^c", false);
                    x.sleep(2000);
                    currentContent=SystemLibrary.getClipboard();
                    if (initialContent.equals(currentContent)){
                        logMessage("End of list.");
                        logError("Employeee "+firstName+" "+lastName+"is NOT found.");
                        errorCounter++;
                        break;
                    }

                    if ((currentContent.contains(firstName))&&(currentContent.contains(lastName))){
                        logMessage("Employee "+firstName+" "+lastName+" is found.");
                        AutoITLib.doubleClick_Window("New Employees Maintenance", 60, 196+18*(rowNumber-1));
                        break;
                    }
                    initialContent=currentContent;
                }
            }


            if (deleteNewStarter != null) {
                x.controlFocus("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]");
                AutoITLib.clickControlInWindow_NEWInUse("New Employees Maintenance", "Maintenance", "[CLASS:TdxBarControl; INSTANCE:1]", 190, 11);
                x.sleep(2000);
                x.winActivate("New Employees Maintenance");
                AutoITLib.click_Window("New Employees Maintenance", 140, 120);
                logMessage("Screenshot After click Dlete Employee button.");
                logScreenshotX();
                x.sleep(5000);
            }

        }else{
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;

    }

    public static boolean logonMeridianSecondDB(String userName, String password, String payrollDB2Name, String payrollDB2OrderNo) throws InterruptedException {
        //launchMeridian first
        boolean isDone = false;
        AutoItX x = new AutoItX();
        x.winActive("Sign in to Sage Micropay");
        x.winWaitActive("Sign in to Sage MicrOpay", "", 60);

        switch (payrollDB2OrderNo) {
            case "1":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 28);
                break;
            case "2":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 49);
                break;
            case "3":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 70);
                break;
        }

        /*switch (payrollDB2Name) {
            case "ESS_Auto_Payroll":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 28);
                break;
            case "ESS_Auto_Payroll2":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 49);
                break;
            case "ESS_Auto_Payroll3":
                x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 70);
                break;
        }*/
        Thread.sleep(2000);
        logMessage("Payroll DB: '" + payrollDB2Name + "' is selected.");

        if (userName != null) {
            x.controlFocus("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]");
            x.controlClick("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "left", 2);
            x.controlSend("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "{DEL}", false);
            x.controlClick("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", "left", 2);
            x.controlSend("Sign in to Sage MicrOpay", "", "[CLASS:TcxCustomComboBoxInnerEdit; INSTANCE:1]", userName);
            logMessage("User name is input.");
        }

        if (password != null) {
            x.controlFocus("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]");
            x.controlClick("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]");
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", "^a");
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", "{DEL}", false);
            Thread.sleep(1000);
            x.controlSend("Sign in to Sage MicrOpay", "", "[Class:TevTextEdit; Instance:1]", password);
            logMessage("Password is input.");
        }

        x.controlClick("Sign in to Sage MicrOpay", "Sign in", "[Class:TevColorButton; Instance:7]");
        Thread.sleep(4000);

        if (x.controlCommandIsVisible("", "&Yes", "[CLASS:TButton; INSTANCE:2]")) {
            x.controlFocus("", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            logScreenshotX();
            logMessage("Message displayed First time for Sharing Common DB.");
            x.controlClick("", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            logMessage("OK button is clicked.");

        } else {
            logMessage("Sharing Common DB Popup message is closed.");
        }
        Thread.sleep(9000);

        if (x.winWait("Sage MicrOpay", "", 120) == true) {
            logMessage("Sage Micropay is launched.");

            if (x.winWait("Notifications from Sage", "", 15)){
                x.winActivate("Notifications from Sage");
                logMessage("Notification Window pops up.");
                logScreenshotX();

                x.controlFocus("Notifications from Sage", "Mark the latest notifications as read (All notifications can still be viewed from the Main Menu)", "[CLASS:TevCheckBox; INSTANCE:1]");
                x.send("{SPACE}", false);
                x.sleep(2000);

                x.winClose("Notifications from Sage");
                x.sleep(2000);
                logMessage("Notification screen is closed.");
            }

            x.winSetState("Sage Micropay is launched.", "", SW_MAXIMIZE);
            Thread.sleep(8000);
            isDone = true;
        } else {
            logError("Sage Micropay is NOT launched.");
        }
        logScreenshotX();

        return isDone;
    }

    public static boolean display_PayFrequencies() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("Pay Frequencies", 1);
        logMessage("Pay Frequencies screen is shwon.");
        logScreenshotX();
        return isShown;
    }


    public static boolean editPayFrequencies(String code, String periodEndDate) throws InterruptedException, IOException, UnsupportedFlavorException {
        boolean isDone=false;
        int errorCounter=0;

        AutoItX x=new AutoItX();
        display_PayFrequencies();

        AutoITLib.clickControlInWindow_NEWInUse("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", 70,70);
        x.sleep(2000);
        logMessage("Click the first row in Payroll Company table.");

        x.winActivate("Sage MicrOpay");

        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.controlSend("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]", code, false);
        Thread.sleep(1000);
        logMessage("Employee Code '" + code + "' is input.");

        x.send("{ENTER}", false);
        logMessage("Enter Key is pressed.");
        Thread.sleep(2000);

        x.controlFocus("Sage MicrOpay", "", "[CLASS:TcxGridSite; INSTANCE:1]");
        x.send("{Ins}", false);
        Thread.sleep(4000);
        logMessage("Insert key is press");

        if (x.winWaitActive("Edit Pay Frequency \"Monthly\"", "", 10)){
            logMessage("Edit Pay Frequency Monthly Screen is shown.");

            if (periodEndDate!=null){

                x.winActivate("Edit Pay Frequency \"Monthly\"");
                AutoITLib.click_Window("Edit Pay Frequency \"Monthly\"", 50,200);

                if (periodEndDate.contains(";")) {
                    periodEndDate = SystemLibrary.getExpectedDate(periodEndDate, null);
                }
                x.send(periodEndDate);
                x.send("{TAB}", false);
                x.sleep(5000);
                logMessage("Period End Date '"+periodEndDate+"' is input.");
            }

            logMessage("Screenshot before click Save button.");
            AutoITLib.logScreenshotX();

            x.controlFocus("Edit Pay Frequency \"Monthly\"", "OK", "[CLASS:TButton; INSTANCE:2]");
            x.controlClick("Edit Pay Frequency \"Monthly\"", "OK", "[CLASS:TButton; INSTANCE:2]");
            logMessage("OK button is clicked.");
            x.sleep(3000);
            logScreenshotX();

        } else{
            logError("Edit Pay Frequency Monthly screen is NOT shwon.");
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;
    }



    public static boolean displayGenerateAutoPayTransactionScreen() throws InterruptedException {
        AutoItX x = new AutoItX();
        boolean isShown = false;
        searchMenu("Generate Auto Pay Transactions", 1);
        logMessage("Generate Auto Pay Transactions screen is shwon.");
        logScreenshotX();
        return isShown;
    }


    public static boolean generateAutoPayTransactions(String payFrequency) throws Exception {
        boolean isDone=false;
        int errorCounter=0;

        AutoItX x=new AutoItX();
        displayGenerateAutoPayTransactionScreen();

        x.winActive("Generate Auto Pay Transactions");
        x.winWaitActive("Generate Auto Pay Transactions", "", 60);
        logMessage("Generate Auto Pay Transactions is shown.");


        /*if (payFrequency != null) {
            selectMultiItemsInDropdownList2("Generate Auto Pay Transactions", "", "[CLASS:TCheckListPopup; INSTANCE:1]", payFrequency);
        }*/

        if (payFrequency!=null) {
            x.controlFocus("Generate Auto Pay Transactions", "", "[CLASS:TCheckListPopup; INSTANCE:1]");
            x.controlClick("Generate Auto Pay Transactions", "", "[CLASS:TCheckListPopup; INSTANCE:1]");
            Thread.sleep(3000);
            x.send(payFrequency);
            x.send("{tab}", false);


            if (payFrequency.equalsIgnoreCase("M01 - Monthly")) {
                AutoITLib.click_Window("Generate Auto Pay Transactions", 115,140);
                Thread.sleep(3000);
                AutoITLib.click_Window("Generate Auto Pay Transactions", 118,290);
                logMessage("Pay Frequency '" + payFrequency + "' is input.");
                logScreenshotX();
            }
            else if (payFrequency.equalsIgnoreCase("1W01 - Weekly")){
                AutoITLib.click_Window("Generate Auto Pay Transactions", 115, 125);
                Thread.sleep(3000);
                AutoITLib.click_Window("Generate Auto Pay Transactions", 118, 290);
                logMessage("Pay Frequency '" + payFrequency + "' is input.");
                logScreenshotX();
            }

            else if (payFrequency.equalsIgnoreCase("All")){

                AutoITLib.click_Window("Generate Auto Pay Transactions", 115,140);
                Thread.sleep(3000);
                AutoITLib.click_Window("Generate Auto Pay Transactions", 118,290);
                logMessage("Pay Frequency '" + payFrequency + "' is input.");
                logScreenshotX();

                AutoITLib.click_Window("Generate Auto Pay Transactions", 115, 125);
                Thread.sleep(3000);
                AutoITLib.click_Window("Generate Auto Pay Transactions", 118, 290);
                logMessage("Pay Frequency '" + payFrequency + "' is input.");
                logScreenshotX();
            }

            Thread.sleep(3000);
        }

        logMessage("Screenshot before click Save button.");
        AutoITLib.logScreenshotX();

        x.controlFocus("Generate Auto Pay Transactions", "OK", "[CLASS:TButton; INSTANCE:2]");
        x.controlClick("Generate Auto Pay Transactions", "OK", "[CLASS:TButton; INSTANCE:2]");
        logMessage("OK button is clicked.");
        x.sleep(10000);
        logScreenshotX();


        x.controlFocus("Generate Auto Pay Transactions", "Close", "[CLASS:TButton; INSTANCE:1]");
        x.controlClick("Generate Auto Pay Transactions", "Close", "[CLASS:TButton; INSTANCE:1]");
        Thread.sleep(3000);
        logMessage("Generate Auto Pay Transactions screen is closed.");
        logScreenshotX();


        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean displayEmployee_RateDetails() throws InterruptedException {
        //display Employee Personal Detail screen first.
        boolean isShown = false;

        AutoItX x = new AutoItX();

        x.controlFocus("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]");
        x.controlClick("Edit Employee", "", "[CLASS:TevTreeList; INSTANCE:1]", "left", 1, 25, 58);
        x.sleep(4000);

        logMessage("Employee Rate Details screen is shown.");
        logScreenshotX();
        isShown = true;
        return isShown;
    }


    public static boolean editEmployee_RateDetails(String firstName, String middleName, String lastName, String preferredNamem, String payClass, String normalHoursPaid, String yearlySalary, String autoPayAmount, String normalRate, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEmployeeDetailsScreen(firstName, lastName, payrollDBName)) {
            displayEmployee_RateDetails();

            AutoItX x = new AutoItX();

            if (payClass != null) {
                x.controlClick("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]");
                x.controlSend("Edit Employee", "", "[CLASS:TevComboInnerEdit; INSTANCE:2]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(payClass);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Pay Class '" + payClass + "' is input.");
            }

            if (normalHoursPaid!=null){
                x.controlFocus("Edit Employee", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:2]");
                x.controlClick("Edit Employee", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:2]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(normalHoursPaid);
                logMessage("Normal Hours Paid '"+ normalHoursPaid +"' is input.");
            }

            if (yearlySalary!=null){
                x.controlFocus("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:7]");
                x.controlClick("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:7]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(yearlySalary);
                logMessage("Yearly Salary '"+ yearlySalary +"' is input.");
                Thread.sleep(2000);
                x.controlFocus("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:3]");
                x.controlClick("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:3]");
                Thread.sleep(4000);
                logMessage("Calculate button is clicked.");
                logScreenshotX();
            }

            if (autoPayAmount!=null){
                x.controlFocus("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:5]");
                x.controlClick("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:5]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(autoPayAmount);
                logMessage("Auto Pay Amount '"+ autoPayAmount +"' is input.");
                Thread.sleep(2000);
                x.controlFocus("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:2]");
                x.controlClick("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:2]");
                Thread.sleep(4000);
                logMessage("Calculate button is clicked.");
                logScreenshotX();
            }

            if (normalRate!=null){
                x.controlFocus("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:4]");
                x.controlClick("Edit Employee", "", "[CLASS:TevFloatEdit; INSTANCE:4]");
                x.send("^a", false);
                x.send("{DEL}", false);
                x.send(normalRate);
                logMessage("Normal Rate'"+ normalRate +"' is input.");
                Thread.sleep(2000);
                x.controlFocus("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:1]");
                x.controlClick("Edit Employee", "Calculate", "[CLASS:TButton; INSTANCE:1]");
                Thread.sleep(4000);
                logMessage("Calculate button is clicked.");
                logScreenshotX();
            }

            logMessage("Screenshot before click OK button. ");
            logScreenshotX();

           closeEmployeeDetailScreen();
        } else {
            logError("Employee '" + firstName + " " + lastName + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) {
            isDone = true;
        }
        return isDone;
    }

    public static boolean process_ExtendTimeSheet() throws InterruptedException {
        boolean isProcess=false;
        AutoItX x=new AutoItX();
        x.winActivate("Extended Transaction");
        x.controlGetFocus("Extended Transaction", "");
        x.controlFocus("Extended Transaction", "Bar Menu", "[CLASS:TdxBarControl; INSTANCE:2]");
        AutoITLib.clickControlInWindow_NEWInUse("Extended Transaction", "Bar Menu", "[CLASS:TdxBarControl; INSTANCE:2]", 340, 14);
        Thread.sleep(3000);
        /*
        x.controlClick("Extended Transaction", "Bar Menu", "[CLASS:TdxBarControl; INSTANCE:2]");
        x.winActivate("Extended Transaction");
        AutoITLib.click_Window("Extended Transaction", 340, 12);
        logMessage("Log screenshot After Process button Clicked.");
        */

        isProcess=true;
        logScreenshotX();
        return isProcess;
    }

    public static boolean edit_ExtendTimeSheet(String employee, String hours, String storeFileName, String isUpdate, String isCompare, String expectedContent, String payrollDBName) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (display_Screen("Extended Timesheet", "Extended Transaction", 1)) {
            AutoItX x = new AutoItX();
            Thread.sleep(3000);

            if (employee!=null) {
                x.controlFocus("Extended Transaction", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]");
                x.controlClick("Extended Transaction", "", "[CLASS:TevIPEmployeeLookupCombo; INSTANCE:1]");
                x.send(employee);
                Thread.sleep(2000);
                x.send("{TAB}", false);
                logMessage("Employee '" + employee + "' is input.");
            }

            logMessage("Log screenshot before Auto Genaration Yes button.");
            logScreenshotX();
            x.controlFocus("Transaction", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(2000);
            x.controlClick("Transaction", "&Yes", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(2000);
            logMessage("Yes button is clicked in Auto Genaration Popup screen.");
            logScreenshotX();

            logMessage("Log screenshot before OverTime Hours Clicked.");
            x.winActivate("Extended Transaction");
            x.controlGetFocus("Extended Transaction", "");
            x.controlFocus("Extended Transaction", "", "[CLASS:TcxGridSite; INSTANCE:2]");
            AutoITLib.clickControlInWindow_NEWInUse("Extended Transaction", "", "[CLASS:TcxGridSite; INSTANCE:2]", 30, 61);

            /*
            x.controlClick("Extended Transaction", "", "[Class:TcxGridSite; Instance:2]");
            Thread.sleep(2000);
            x.controlSend("Extended Transaction", "", "[Class:TcxGridSite; Instance:2]", "{DOWN}{DOWN}{ENTER}", false);
            Thread.sleep(2000);
            logMessage("Log screenshot After OverTime Hours Clicked.");
            */
            logMessage("Log screenshot before Add OverTime Hours button Clicked.");
            logScreenshotX();
            x.controlFocus("Extended Transaction", "&Add", "[CLASS:TButton; INSTANCE:3]");
            x.sleep(1000);
            x.controlClick("Extended Transaction", "&Add", "[CLASS:TButton; INSTANCE:3]");
            x.sleep(1000);
            logMessage("Log screenshot after Add OverTime Hours button Clicked.");
            logScreenshotX();

            x.controlFocus("Extended Transaction", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]");
            x.sleep(2000);
            x.controlClick("Extended Transaction","", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]");
            x.sleep(1000);
            x.send("{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}", false);
            x.sleep(2000);

            if (hours !=null) {

                x.controlClick("Extended Transaction", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]");
                x.controlSend("Extended Transaction", "", "[CLASS:TevIPHoursDecimalHourEdit; INSTANCE:1]", "^a");
                x.sleep(3000);
                x.send("{DEL}", false);
                x.send(hours);
                x.send("{TAB}", false);
                x.sleep(3000);
                logMessage("Hours '" + hours + "' is input.");
                logScreenshotX();
            }
            /*
            x.controlFocus("Extended Transaction", "", "[CLASS:TcxGridSite; INSTANCE:3]");
            String currentLog=x.controlGetText("Extended Transaction", "", "[CLASS:TcxGridSite; INSTANCE:3]");
            logMessage("The Extended Transaction log is below:");
            System.out.println(currentLog);

            String fileName = "ExtendedTransaction_" + getCurrentTimeAndDate() + ".pdf";
            String fileFullPathName = workingFilePath + fileName;
            SystemLibrary.createTextFile(fileFullPathName, currentLog);

            if (storeFileName!=null){
                if (!SystemLibrary.updateAndValidateStoreStringFile(isUpdate, isCompare, fileFullPathName, storeFileName, expectedContent)) errorCounter++;
            }*/


            process_ExtendTimeSheet();

            logMessage("Log screenshot before Auto Genaration No button.");
            x.controlFocus("Transaction", "&No", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(2000);
            x.controlClick("Transaction", "&No", "[CLASS:TButton; INSTANCE:2]");
            x.sleep(2000);
            logMessage("No button is clicked in Auto Genaration Popup screen for Next Employee.");

            logMessage("Log screenshot before Close button.");
            x.controlGetFocus("Extended Transaction", "");
            x.controlFocus("Extended Transaction", "Bar Menu", "[CLASS:TdxBarControl; INSTANCE:2]");
            AutoITLib.clickControlInWindow_NEWInUse("Extended Transaction", "Bar Menu", "[CLASS:TdxBarControl; INSTANCE:2]", 340, 15);
            Thread.sleep(3000);
            logScreenshotX();


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }



        /////////////// Debug Test //////////////////
    @Test
    public static void test1() throws InterruptedException {
        AutoItX x=new AutoItX();
        x.winActivate("Edit New Employee");
        AutoITLib.click_Window("Edit New Employee", 510,610);


     /*   x.controlGetFocus("Edit New Employee", "Next >");
        x.controlClick("Edit New Employee", "Next >", "[CLASS:TButton; INSTANCE:6]");
        x.sleep(2000);*/


    }


}
