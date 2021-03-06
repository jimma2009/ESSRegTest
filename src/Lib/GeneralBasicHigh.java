package Lib;

import PageObject.PageObj_ApplyForLeave;
import PageObject.PageObj_TeamsNRoles;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import static Lib.SystemLibrary.*;
import static Lib.SystemLibraryHigh.validateTextValueInWebElementInUse_Main;

public class GeneralBasicHigh {

    public static boolean logonESSMain(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.logonESS(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], payrollDBName, testSerialNo, emailDomainName, driver )) {
                        errorCounter++;
                        logError("First Sign in ESS Failed. Try 2nd time");
                        driver.navigate().to(url_ESS+"/#/signout/");
                        Thread.sleep(5000);
                        driver.navigate().to(url_ESS);
                        Thread.sleep(5000);
                        logMessage("Web page: \""+url_ESS+"\" is opened.");
                        Thread.sleep(3000);
                        if (!GeneralBasic.logonESS(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], payrollDBName, testSerialNo, emailDomainName, driver )){
                            errorCounter++;
                            logError("The 2nd Sign in ESS Failed.");
                        }
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }

    public static boolean logonESSMain(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.logonESS(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], payrollDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }

    public static boolean logonESSMain_Backup_26052021(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.logonESS(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], payrollDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }

    public static boolean validateDashBoard_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Dashboard from row " + startSerialNo + " to " + endSerialNo + " in \"Dashboard\" sheet.");
        GeneralBasic.displayDashboard(driver);
        boolean isPassed=false;
        int errorCounter=0;
        boolean isCurrentPassed;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"Dashboard");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateDashboard(driver, cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][19], testSerialNo, emailDomainName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Multi Dashboard in \"Dashboard_Debug\" sheet.");
        return isPassed;
    }

    //Override below
    public static boolean addNewWebAPIConfiguration_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Adding multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    if(Strings.isNullOrEmpty(cellData[i][12])) {
                        if (!GeneralBasic.addNewWebAPIConfiguration(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], testSerialNo, driver))
                            errorCounter++;
                    } else {
                        if (!GeneralBasic.addNewWebAPIConfiguration(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], cellData[i][12], testSerialNo, driver)) errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi New Web API Configuration.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

/*    //Override above
    public static boolean addNewWebAPIConfiguration_Main(int startSerialNo, int endSerialNo, String testSerialNo, String dbName, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Adding multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNewWebAPIConfiguration(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], cellData[i][12], testSerialNo, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi New Web API Configuration.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }*/


    public static boolean removeIntegrationAPIKey_Main_OLD(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start remove multi API Key from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isRemoved=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,8,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.removeGeneralAPIKey(cellData[i][4], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isRemoved=true;
        SystemLibrary.logMessage("*** End of Remove multi API Key.");
        return isRemoved;
    }

    public static void applyMultiLeaveViaDashboard_DONOTUSE(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start applying multi Leave Via Dashboard from row " + startSerialNo + " to " + endSerialNo + " in \"ApplyLeave\" sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;

        GeneralBasic.displayDashboard(driver);
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,10,"ApplyLeave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    GeneralBasic.applyLeaveViaDashboard_DONOTUSE(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],  cellData[i][8], cellData[i][9], driver);
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of applying multi Leave Via Dashboard.");

    }

    public static boolean applyMultiLeaveViaLeavePage_OLD(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start applying multi Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.applyLeaveViaLeavePage_OLD(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of applying multi Leave Via Leave Page.");
        return isDone;
    }

    public static boolean editMultiContactDetails(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, SQLException, ClassNotFoundException{
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Contact Details from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ContactDetails");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editContactDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], cellData[i][30], cellData[i][31], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi Contact Details.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean approveMultiLeave_OLD_DONOTUSE(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException, ParseException{
        //displayLeavesForApprovalViaPendingLeaveApprovalPanel (or) displayLeavesForApprovalViaNavigationBar first.
        boolean isApproved=false;
        SystemLibrary.logMessage("--- Start approving multi Leave from row " + startSerialNo + " to " + endSerialNo + " in \"LeaveApproval\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,6,"LeaveApproval");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    isApproved=GeneralBasic.approveLeave(cellData[i][2], cellData[i][3], cellData[i][4], driver);
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of approving multi Leave.");
        return isApproved;
    }

    public static boolean downloadMultiAuditReportViaDashboard(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        //Dispaly Dashboard first
        GeneralBasic.displayDashboard(driver);
        SystemLibrary.scrollToBottom(driver);
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Audit Report via Dashboard from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadAuditReportsViaDashboard(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][7], emailDomainName, testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Audit Report via Dashboard.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean editWebAPIConfiguration_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Editing multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isEdited=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editWebAPIConfiguration(cellData[i][2], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi New Web API Configuration.");
        if (errorCounter==0) isEdited=true;
        return isEdited;
    }

    public static boolean syncAllData_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start multi Sync All Data from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.syncAllData(driver, cellData[i][8])) {
                        errorCounter++ ;
                        logMessage("Try Sync All the 2nd time.");
                        if (!GeneralBasic.syncAllData(driver, cellData[i][8])){
                            errorCounter++;
                        }
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync All Data.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean syncAllDataSimultaneously_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start multi Sync All Data from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.syncAllDataSimultaneously(driver, cellData[i][8])) {
                        errorCounter++ ;
                        logMessage("Try Sync All the 2nd time.");
                        if (!GeneralBasic.syncAllDataSimultaneously(driver, cellData[i][8])){
                            errorCounter++;
                        }
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync All Data.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean approveMultiProfileChanges_DONOTUSE(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        //displayProfileApprovalViaPendingLeaveApprovalPanel (or) displayProfileApprovalViaNavigationBar first.
        boolean isApproved=false;
        SystemLibrary.logMessage("--- Start approving multi Leave from row " + startSerialNo + " to " + endSerialNo + " in \"ChangesApproval\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,6,"ChangesApproval");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    isApproved=GeneralBasic.approveProfileChanges(cellData[i][2], cellData[i][3], cellData[i][4], driver);
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of approving multi Profile Changes.");
        return isApproved;
    }


    public static boolean getContentFromRolesTable_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validate List in Roles Table from row " + startSerialNo + " to " + endSerialNo + " in \"GridItemList\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,7,"GridItemList");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.getContentFromRolesTable(cellData[i][2], cellData[i][4],cellData[i][5],cellData[i][6], driver)) errorCounter++ ;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync All Data.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean validatePermissionPanelScreenshot_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Permissions from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePermissionPanelScreenshot(cellData[i][2], cellData[i][6], cellData[i][7], cellData[i][8], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Permissions in \"RolesPermissions\" sheet.");
        return isPassed;
    }

    public static boolean configPermissionStatus_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Config Multi Permissions Status from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.configPermissionStatus(cellData[i][2], cellData[i][3], cellData[i][4], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Config Multi Permissions in \"RolesPermissions\" sheet.");
        return isPassed;
    }

    public static boolean validateTeamMembers_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Team Members from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateTeamMembers(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Team Members in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateTeamTable_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateTeamTable(cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Teams in \"Teams\" sheet.");
        return isPassed;
    }

    /*public static boolean getEllipsisInTeamTable_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Config Multi Teams Status from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,31,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!PageObj_Teams.getEllipsisInTeamTable(cellData[i][2], cellData[i][3], cellData[i][4], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Config Multi Teams in \"Teams\" sheet.");
        return isPassed;
    }*/

    public static boolean renameTeam_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start reaname Multi Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,11,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.renameTeam(cellData[i][2], cellData[i][5], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of rename Multi Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean addTeamMember_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start adding Multi Team Members from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addTeamMember(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], cellData[i][17], cellData[i][18], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of adding multi Team Members in \"Teams\" sheet.");
        return isPassed;
    }


    public static boolean deleteMultiTeam(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Delete Multi Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.deleteTeam(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Deleting Multi Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean removeMultiTeamMembers(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Remove Multi Members from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.removeMemberFromTeam( cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Removing Multi Members in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean changeMultiMemberRole_ViaTeamsPage(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Remove Multi Members from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.changeMemberRole_ViaTeamsPage(cellData[i][2], cellData[i][4], cellData[i][25], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Removing Multi Members in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean moveMemberToTeam_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Moving Multi Members to Team from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.moveMemberToTeam(cellData[i][2], cellData[i][5], cellData[i][4], cellData[i][23], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], cellData[i][17], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of adding multi Team Members in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean addMultiTeam(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isAdded=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addTeam(cellData[i][2], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isAdded=false;
        SystemLibrary.logMessage("*** End of Adding Multi Teams in \"Teams\" sheet.");
        return isAdded;
    }



    public static boolean editMultiPersonalInformation(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        //display Personal Info Details page first
        SystemLibrary.logMessage("--- Start Editing multi Personal Information Detail Page from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"UserPersonalInformation");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editPersonalInformation(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][20], cellData[i][21],testSerialNo, driver)) errorCounter++;;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Editing multi Personal Information Detail Page.");
        return isPassed;
    }

    public static boolean validateUserSideNavigationMenu_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName,  WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validate Multi User Side Navigation Menu from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    if (!GeneralBasic.validateUserSideNavigationMenu(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validate Multi User Side Navigation Menu in \"UserPersonalInformation\" sheet.");
        return isPassed;
    }


    public static boolean uploadUserPhoto_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Uploading multi user photo from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserPersonalInformation");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.uploadUserPhoto(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][19], testSerialNo, driver)) errorCounter++;;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");

                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Uploading multi user photo.");
        return isPassed;
    }

    public static boolean validateMultiAccountActivationStatus_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi User Account Activation Status from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateAccountActivationStatu_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], testSerialNo, driver)) errorCounter++;;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi User Account Activation Status in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean activateMultiUserAccount_ViaAdmin(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateUserAccount_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][16], cellData[i][17], payrollDBName, testSerialNo, emailDomainName, url_ESS, driver)){
                        errorCounter++;
                        SystemLibrary.logError("Failed in the first activation attempt.");
                        JavaMailLib.deleteAllMail(emailDomainName);
                        if (!GeneralBasic.activateUserAccount_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][16], cellData[i][17], payrollDBName, testSerialNo, emailDomainName, url_ESS, driver)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the second activation attempt.");
                        }

                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }
        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isDone;
    }

    public static boolean searchUserAndDisplayPersonalInformationPage_ViaAdmin_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start searching Mulit User and Displaying Personal Informaton Page via Admin from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.searchUserAndDisplayPersonalInformationPage_ViaAdmin(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of searching Mulit User and Displaying Personal Informaton Page via Admin in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateUserBanner_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validate multi User Banner details from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateUserBanner(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of validate multi User Banner details in \"UserPersonalInformation\" sheet.");
        return isPassed;
    }

    public static boolean searchUserAndDisplayContactDetailsPage_ViaAdmin_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start searching Mulit User and Displaying Contact Details Page via Admin from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.searchUserAndDisplayAccountSettingsPage_ViaAdmin(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of searching Mulit User and Displaying Personal Informaton Page via Admin in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateTeamMembersLeaveTab_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Tab in Team Member table from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateTeamMembersLeaveTab(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Leave Tab in Team Member table in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateBankAccounts_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Bank Accounts from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateBankAccounts(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of  Validating Bank Details.");
        return isPassed;
    }

    public static boolean validateSuperannuation_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Superannuation from row " + startSerialNo + " to " + endSerialNo + " in \"Superannuation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Superannuation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateSuperannuation(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Superannuation.");
        return isPassed;
    }

    public static boolean validate_ContactDetails_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Contact Details from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ContactDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateContactDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][14], cellData[i][15], cellData[i][16], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Contact Details.");
        return isPassed;
    }

    public static boolean validateLeavePage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeavePage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][24], cellData[i][25], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave.");
        return isPassed;
    }

    public static boolean validatePersonalInformation_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi user Personal Information from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePersonalInformation(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi user Personal Information.");
        return isPassed;
    }

    public static boolean validateEmployment_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi user Employment Information from row " + startSerialNo + " to " + endSerialNo + " in \"Employment\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Employment");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateEmployment(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi user Employment Information.");
        return isPassed;
    }

    public static boolean validateMultiMyApprovals_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validating multi My Approvals via Admin from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateMyApprovals_ViaAdmin(cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi My Approvals via Admin in \"Approvals\" sheet.");
        return isPassed;
    }

    public static boolean validateMultiOtherApprovals_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validating multi Other Approvals via Admin from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateOtherApprovals_ViaAdmin(cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Other Approvals via Admin in \"Approvals\" sheet.");
        return isPassed;
    }

    public static boolean downloadMultiPayAdviceReport_ViaDashboard(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        //Dispaly Dashboard first
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Pay Advice Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadPayAdvice_ViaDashboard(cellData[i][6], cellData[i][3], cellData[i][4], cellData[i][5], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Pay Advice Report via Dashboard.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean forgetPassword_Main(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        SystemLibrary.logMessage("--- Start reset Multi User Password from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.forgetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        /*SystemLibrary.logError("Failed in the first email attempt. Try the second time ...");
                        if (!GeneralBasic.forgetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the Second email attempt. ");
                        }*/
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Reset Multi User Password in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean forgetPassword_Main_OLD(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        SystemLibrary.logMessage("--- Start reset Multi User Password from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.forgetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        SystemLibrary.logError("Failed in the first email attempt. Try the second time ...");
                        if (!GeneralBasic.forgetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the Second email attempt. ");
                        }
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Reset Multi User Password in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean validateMultiUserBusinessCard(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception{
        SystemLibrary.logMessage("--- Start Validating Multi Business Card from row " + startSerialNo + " to " + endSerialNo + " in \"Dashboard\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Dashboard");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateUserBusinessCard(cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][11], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Multi User Business Card in \"Dashboard\" sheet.");
        return isPassed;
    }

    public static boolean disableMultiUserAccount(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start disable Multi user account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.disableUserAccount(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi User Business Card in \"UserAccountSettings\" sheet.");
        return isPassed;
    }

    public static boolean validateLeaveSettingInLeaveSettingPage_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Leave Settings from row " + startSerialNo + " to " + endSerialNo + " in \"SettingsLeave\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"SettingsLeave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeaveSettingInLeaveSettingPage(cellData[i][8], cellData[i][11], cellData[i][12], cellData[i][13], emailDomainName, testSerialNo, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Leave Settings in \"SettingsLeave\" sheet.");
        return isPassed;
    }

    public static boolean editSettingsLeave_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Editing Multi Leave Settings from row " + startSerialNo + " to " + endSerialNo + " in \"SettingsLeave\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"SettingsLeave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editSettingsLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][9], cellData[i][7], driver)){
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Leave Settings in \"SettingsLeave\" sheet.");
        return isDone;
    }

    public static boolean validateEditLeaveFormInLeaveSettingPage_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Edit Leave Form in Leave Setting Page from row " + startSerialNo + " to " + endSerialNo + " in \"SettingsLeave\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"SettingsLeave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (GeneralBasic.validateEditLeaveFormInLeaveSettingPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], driver)){
                        isDone=true;
                    }
                    else{
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isDone=false;
        SystemLibrary.logMessage("*** End of Validating Multi Edit Leave Form in Leave Setting Page from row " + startSerialNo + " to " + endSerialNo + " in \"SettingsLeave\" sheet.");
        return isDone;
    }

    public static boolean downloadMultiLeaveBalancesReport(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        //Dispaly Dashboard first
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Leave Balance Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,6,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadLeaveBalancesReport(cellData[i][3], cellData[i][4], cellData[i][5], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Leave Balance Report via Dashboard.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean editMultiLeave(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        //displayLeavePage(driver) first
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Leave from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!PageObj_ApplyForLeave.editApplyForLeave(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][26], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Edit multi Leave.");
        return isDone;
    }

    public static boolean validateApplyForLeaveForm_Main_OLD(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Validate multi Apply Leave Form from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateApplyForLeaveForm(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], cellData[i][26], emailDomainName, testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validate multi Apply For Leave Form.");
        return isDone;
    }

    public static boolean validate_AccountSettingsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Account Settings from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"UserAccountSettings");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_AccountSettings_Page(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Account Settings Details.");
        return isPassed;
    }


    public static boolean validate_TeamsRolesDetailsScreen_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Teams & Roles from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (GeneralBasic.validate_TeamsAndRoles(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][17], testSerialNo, emailDomainName, driver)==false) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Teams And Roles.");
        return isPassed;
    }

    public static boolean validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Approval Process vis Teams and Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_ApprovalProcess_ViaTeamsAndRolesPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][17], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("Validating multi Approval Process vis Teams and Roles page");
        return isPassed;
    }

    public static boolean validate_TeamMembers_ViaTeamsRolesDetailsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating muliti Team Members Via Teams and Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_TeamsAndRoles(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][17], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Mulit Team Members via Teams and Roles Page.");
        return isPassed;
    }

    public static boolean approveMultiItem(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Approving muliti Items from row " + startSerialNo + " to " + endSerialNo + " in \"ApproveItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,60,"ApproveItems");
        logMessage("The length of data is "+String.valueOf(cellData.length));

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.approveItem(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Approve Mulit Item via Teams and Roles Page.");
        return isDone;
    }


    public static boolean editMultiWorkflow(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start change muliti Workflows from row " + startSerialNo + " to " + endSerialNo + " in \"Workflows\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Workflows");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editWorkFlow(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][10], cellData[i][6], cellData[i][7], cellData[i][8], emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of changing Mulit Worklows.");
        return isDone;
    }

    public static boolean validateWorkflowsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validating muliti Workflows page from row " + startSerialNo + " to " + endSerialNo + " in \"Workflows\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Workflows");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateWorkflowsPage(cellData[i][2], cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of validating Mulit Worklows page.");
        return isDone;
    }

    public static boolean addMultiBankAccount(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding muliti Bank Accounts from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addBankAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][13], cellData[i][14], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Adding Mulit Bank Accounts.");
        return isDone;
    }

    public static boolean changeOrderOfBankAccount_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start changing the order of Bank Accounts from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.changeOrderOfBankAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][9], cellData[i][15], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of chaging the order of Bank Account.");
        return isDone;
    }

    public static boolean validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is for non admin user
        SystemLibrary.logMessage("--- Start validating multi My Approvals via Dashboard from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateMyApprovals_ProfileChange_ViaDashboard_NonAdmin(cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi My Approvals via Dashboard in \"Approvals\" sheet.");
        return isPassed;
    }


    public static boolean validateApprovals_All_NONAdminUser_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is for non admin user
        SystemLibrary.logMessage("--- Start validating multi My Approvals All from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateApprovals_All_NONAdminUser(cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi My Approvals All in \"Approvals\" sheet.");
        return isPassed;
    }

    public static boolean approveMultiPersonalInformationChanges(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        //display Personal Info Details page first
        SystemLibrary.logMessage("--- Start Approving multi Personal Information changes from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"UserPersonalInformation");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.approvePersonalInformationChanges(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][22], cellData[i][20], cellData[i][23], testSerialNo, driver)) errorCounter++;;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Approving multi Personal Information changese.");
        return isPassed;
    }

    public static boolean validate_EllipsisMenInTeamsRolesDetailsPage_Main_OLD(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Ellipsis Menu in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!PageObj_TeamsNRoles.validateElipsisMenuInTeamsAndRolesPage_OLD(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][17], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Ellipsis Menu in Teams & Roles page.");
        return isPassed;
    }

    public static boolean addRedirectApprovals_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Performing Mulit Redirect Approvals in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addRedirectApprovals(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][16], cellData[i][17], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Performing Mulit Redirect Approvals in Teams & Roles page.");
        return isDone;
    }

    public static boolean removeRedirectApprovals_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Removing Mulit Redirect Approvals in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.removeRedirectApprovals(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][16], cellData[i][17], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Removing Mulit Redirect Approvals in Teams & Roles page.");
        return isDone;
    }

    public static boolean validateMultiRolesPage(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateRolesPage(cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi Roles page.");
        return isPassed;
    }

    public static boolean validateEllipsisMenuInBankAccountsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Ellipsis Menu In Bank Accounts page from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateEllipsisMenuInBankAccountsPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of  Validating multi Ellipsis Menu In Bank Accounts Page.");
        return isPassed;
    }

    public static boolean editMultiBankAccount(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Editing muliti Bank Accounts from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editBankAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][13], cellData[i][14], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Mulit Bank Accounts.");
        return isDone;
    }

    public static boolean deleteMultiContactDetails(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, SQLException, ClassNotFoundException{
        boolean isPassed=true;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Deleting multi Contact Details from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ContactDetails");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.deleteContactDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], cellData[i][30], cellData[i][31], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Deleting multi Contact Details.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }


    public static boolean validate_RedirectedApprovers_Main_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=true;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Validating multi Redirected Approvers via Admin from row " + startSerialNo + " to " + endSerialNo + " in \"Redirected Approvers\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Redirected Approvers");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateRedirectedApprovers_ViaAdmin(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Validating multi Redirected Approvers via Admin.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean validateSearchResult_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, SQLException, ClassNotFoundException, Exception{
        boolean isPassed=true;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Validating multi Search Result from row " + startSerialNo + " to " + endSerialNo + " in \"SearchResult\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"SearchResult");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateSearchResult(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Validating multi Search Result.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }


    public static boolean validateDirectoryTable_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This is for non-admin user
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Validating multi Directory Table from row " + startSerialNo + " to " + endSerialNo + " in \"Directory\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Directory");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateDirectoryTable(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Validating multi Directory Table.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean editRedirectApprovals_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Editing Redirect Approver in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editRedirectApprovals(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][16], cellData[i][17], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Redirect Approver in Teams & Roles page.");
        return isDone;
    }

    public static boolean applyMultiLeave(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start applying multi Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.applyLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of applying multi Leave Via Leave Page.");
        return isDone;
    }

    public static boolean validateLeaveForecastInApplyLeaveDialogue_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Forecast in Apply Leave Dialogue from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeaveForecastInApplyLeaveDialogue(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave Forecast in Apply Leave Dialogue.");
        return isPassed;
    }

    public static boolean editMultiPendingLeave(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start edit multi Pending Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editPendingLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of edit multi Pending Leave Via Leave Page.");
        return isDone;
    }

    public static boolean validateLeavePage_ViaAdminApprovals_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This funciton is used for Admin user only
        SystemLibrary.logMessage("--- Start Validating Leave pages via Admin Approvals Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeavePage_ViaAdminApprovals(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave pages via Admin Approvals Page.");
        return isPassed;
    }


    public static boolean changeDefaultApprovalTeam_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Changing Default Approval Team from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.changeDefaultApprovalTeam(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][19], testSerialNo,  driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave pages via Admin Approvals Page.");
        return isPassed;
    }

    public static boolean validateApplyForLeaveForm_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Apply For Leave Dialogue from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateApplyForLeaveForm(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], cellData[i][26], emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave pages via Admin Approvals Page.");
        return isPassed;
    }

    public static boolean downloadMultiRolesReport(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Download multi Roles Report from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadRolesReport(cellData[i][2], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Download multi Roles Report.");
        return isPassed;
    }

    public static boolean duplicateMultiRole(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Duplicating multi role multi Roles from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.duplicateRole(cellData[i][2], cellData[i][10], cellData[i][11], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Duplicating multi role multi Roles");
        return isPassed;
    }

    public static boolean validatePermissionPanel_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Permissions Panel from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePermissionPanel(cellData[i][2], cellData[i][6], cellData[i][7], cellData[i][8], emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Permissions Panel in \"RolesPermissions\" sheet.");
        return isPassed;
    }

    public static boolean configPermissionStatusNew_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Config Multi Permissions Status from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.configPermissionStatusNew(cellData[i][2], cellData[i][3], cellData[i][4], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Config Multi Permissions in \"RolesPermissions\" sheet.");
        return isPassed;
    }

    public static boolean addMultiAdministratorRole(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding Mulit Administrator Roles in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addAdministratorRole(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][20], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Adding Multi Administrator Roles in Teams & Roles page.");
        return isDone;
    }

    public static boolean changeMultiMemberRole(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start changing Mulit Member Roles in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.changeMemberRole(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][20], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of changing Mulit Member Roles.");
        return isDone;
    }

    public static boolean validateIntegrationPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Integration Page from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateIntegrationPage(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating Integration page.");
        return isDone;
    }

    public static boolean validateIntegrationPage_WebAPIConfigForm_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Integration Page from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateIntegrationPage_WebAPIConfigForm(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating Integration page.");
        return isDone;
    }

    public static boolean activateMultiUserAccount_WithClickActionButtonOnly_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateUserAccount_WithClickActionButtonOnly_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], testSerialNo,  driver)){
                        errorCounter++;
                        SystemLibrary.logError("Failed in the first email attempt.Try the second attempt... ");
                        if (!GeneralBasic.activateUserAccount_WithClickActionButtonOnly_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], testSerialNo, driver)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the Second email attempt.");
                        }

                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean cancelActivateMultiUserAccount_ViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Cancelling Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.cancleActivateUserAccount_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, driver)){
                        errorCounter++;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean activateMultiUserAccount_ViaEmail(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateUserAccountViaEmail(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], cellData[i][7], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        logMessage("Failed activate user via email.");
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean activateMultiUserAccount_PasswordValidation_ViaAdmin(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, String url_ESS, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account - Password Validation from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateUserAccount_PasswordValidation_ViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][9], emailDomainName, testSerialNo, url_ESS, driver)){
                        errorCounter++;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean validateEmailNotificationIconInContactDetailPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Contact Details from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ContactDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateEmailNotificationIconInContactDetailPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][32], cellData[i][14], cellData[i][15], cellData[i][16], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Contact Details.");
        return isPassed;
    }

    public static boolean searchUserAndDisplayPersonalInformationPageViaDirecotry_ViaAdmin_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Search And Display User Personal Information Page Via Directory from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.searchAndDisplayUserPersonalInformationPageViaDirecotry_ViaAdmin(cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Search And Display User Personal Information Page Via Directory in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean resetPassword_Main(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws Exception {
        //Logon ESS first.
        SystemLibrary.logMessage("--- Start reset Multi User Password from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.resetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS, driver)){
                        errorCounter++;
                        SystemLibrary.logError("Failed in the first email attempt. Try the second time ...");
                        if (!GeneralBasic.resetPassword(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][15], payrollDBName, testSerialNo, emailDomainName, url_ESS, driver)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the Second email attempt. ");
                        }
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Reset Multi User Password in UserAccountSettings sheet.");
        return isPassed;
    }


    public static boolean editAccountSettings_NotificationEmail_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start editing multi notification eamil in Account Settings page from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"UserAccountSettings");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editAccountSettings_NotificationEmail(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][18], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of editing multi notification eamil in Account Settings page.");
        return isPassed;
    }

    public static boolean editAccountSettings_UserName_Main(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start editing multi Username in Account Settings page from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"UserAccountSettings");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editAccountSettings_Username(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][19], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of editing multi Username in Account Settings page.");
        return isPassed;
    }

    public static boolean validateLeaveReasonListInApplyForLeaveForm_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        //Apply For Leave Form is shown first.
        driver.findElement(By.xpath("//div[@id='leaveReason']")).click();
        Thread.sleep(2000);
        logMessage("Leave Reason dropdown list is clicked.");
        logScreenshot(driver);

        WebElement dropdownList=SystemLibrary.waitChild("//div[@id='leaveReason-select-list']", 60, 1, driver);
        if (dropdownList!=null){
            if (!validateTextValueInWebElementInUse_Main(startSerialNo, endSerialNo, dropdownList, testSerialNo, emailDomainName,  driver)) errorCounter++;
        }
        else{
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;

    }

    public static boolean validateLeavePage_Log_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeavePage_Log(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][9], cellData[i][14], cellData[i][23], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave.");
        return isPassed;
    }

    public static boolean validateLeavePage_Attachment_Man(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeavePage_Attachment(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][12], cellData[i][19], cellData[i][6], cellData[i][23], cellData[i][9], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave.");
        return isPassed;
    }

    public static boolean validatePageAdvicePaySummaryPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Pay Advice Page from row " + startSerialNo + " to " + endSerialNo + " in \"PayAdviceAndPaymentSummary\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"PayAdviceAndPaymentSummary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePayAdvicePaySummaryPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Pay Advice page.");
        return isPassed;
    }

    public static boolean validateLeaveBalance_ViaLeavePage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String serverName, String payrollDBName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Page - Leave Balace from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateLeaveBalance\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ValidateLeaveBalance");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeaveBalance_ViaLeavePage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][17], cellData[i][20], testSerialNo, serverName, payrollDBName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave Page - Leave Balace.");
        return isPassed;
    }


    public static boolean validateLeaveBalance_ViaDashboard_Main(int startSerialNo, int endSerialNo, String serverName, String payrollDBName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Page - Leave Balace from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateLeaveBalance\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ValidateLeaveBalance");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeaveBalance_ViaDashboard(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][17], cellData[i][20], serverName, payrollDBName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave Page - Leave Balace.");
        return isPassed;
    }

    public static boolean validateMultiApprovalsPage(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is for non admin user
        SystemLibrary.logMessage("--- Start validating multi Approvals Page from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateApprovalsPage(cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi My Approvals All in \"Approvals\" sheet.");
        return isPassed;
    }

    public static boolean valdiateMultiRedirectApprovalsForm(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Mulit Redirect Approvals Form in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateRedirectApprovalsForm(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][17], cellData[i][21], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating Mulit Redirect Approvals Form in Teams & Roles page.");
        return isDone;
    }

    public static boolean validateRedirectApprovalsMenu_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Redirect Approval Menu in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateRedirectApprovalsMenu(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][17], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Redirect Approval Menu in Teams & Roles page.");
        return isPassed;
    }

    public static boolean validate_EllipsisMenInTeamsRolesDetailsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Ellipsis Menu in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!PageObj_TeamsNRoles.validateElipsisMenuInTeamsAndRolesPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Ellipsis Menu in Teams & Roles page.");
        return isPassed;
    }

    public static boolean validateLeaveBalance_ViaApplyLeaveDialogue_Main(int startSerialNo, int endSerialNo, String testSerialNo, String serverName, String payrollDBName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Page - Leave Balace from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateLeaveBalance\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ValidateLeaveBalance");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateLeaveBalance_ViaApplyLeaveDialogue(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][19], cellData[i][20], testSerialNo, serverName, payrollDBName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave Page - Leave Balace.");
        return isPassed;
    }

    public static boolean validateMemberRoleListInChangeMemberRoleDialogue_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validate Multi Member Role List In Change Member Role Dialogue from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateMemberRoleListInChangeMemberRoleDialogue(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of changing Mulit Member Roles.");
        return isDone;
    }

    public static boolean validateTopNavigationMenu_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Top Navigation Menu from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        GeneralBasic.displayDashboard(driver);
        boolean isPassed=false;
        int errorCounter=0;
        boolean isCurrentPassed;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    WebElement topMenu=SystemLibrary.waitChild("//ul[@id='left-nav']", 10,  1, driver);
                    if (topMenu!=null){
                        if (!SystemLibrary.validateTextValueInElement(topMenu, cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName)) errorCounter++;
                    }else{
                        logError("Top Navigation Menu is NOT shonw.");
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }
        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Multi Top Navigation Menu in \"ValidateItems\" sheet.");
        return isPassed;
    }

    public static boolean removeMultiAdministratorRole(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Removing Mulit Administrator Roles in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.removeAdministratorRole(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][20], testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Removing Multi Administrator Roles in Teams & Roles page.");
        return isDone;
    }

    public static boolean editMultiWorkFlow_ValidateSaveWarning(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start change muliti Workflows from row " + startSerialNo + " to " + endSerialNo + " in \"Workflows\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Workflows");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editWorkFlow_ValidateSaveWarning(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][10], cellData[i][6], cellData[i][7], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of changing Mulit Worklows.");
        return isDone;
    }

    public static boolean approveMultiContactDetailsChanges(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        //display Personal Info Details page first
        SystemLibrary.logMessage("--- Start Approving multi Contact Details changes from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"ContactDetails");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.approveContactDetailsChanges(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][33], cellData[i][13], cellData[i][34], testSerialNo, driver)) errorCounter++;;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Approving multi Contact Details changese.");
        return isPassed;
    }

    public static boolean validatePersonalInformation_WithoutSearch_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi user Personal Information from row " + startSerialNo + " to " + endSerialNo + " in \"UserPersonalInformation\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePersonalInformation_WithoutSearch(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi user Personal Information.");
        return isPassed;
    }

    public static boolean validate_AccountSettingsPage_WithoutSearch_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Account Settings from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"UserAccountSettings");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_AccountSettings_Page_WtihoutSearch(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Account Settings Details.");
        return isPassed;
    }

    public static boolean validate_TeamsRolesDetailsScreen_WithoutSearch_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Teams & Roles from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (GeneralBasic.validate_TeamsAndRoles_WithoutSearch(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][17], testSerialNo, emailDomainName, driver)==false) errorCounter++;

                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Teams And Roles.");
        return isPassed;
    }

    public static boolean validatePageAdvicePaySummaryPage_WithoutSearch_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Pay Advice Page from row " + startSerialNo + " to " + endSerialNo + " in \"PayAdviceAndPaymentSummary\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"PayAdviceAndPaymentSummary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePayAdvicePaySummaryPage_WithoutSearch(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Pay Advice page.");
        return isPassed;
    }

    public static boolean cancelMultiPendingLeave(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start cancel multi Pending Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.cancelPendingLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of cancle multi Pending Leave Via Leave Page.");
        return isDone;
    }

    public static boolean cancelMultiLeave(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start cancle multi Pending Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.cancelLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], cellData[i][23], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of cancle multi Pending Leave Via Leave Page.");
        return isDone;
    }

    public static boolean addNewWebAPIConfiguration_Main_Backup(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Adding multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNewWebAPIConfiguration(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], testSerialNo, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi New Web API Configuration.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean removeGeneralAPIKey_Main(String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;
        String dataSourceFilePathName="C:\\TestAutomationProject\\ESSRegTest\\DataSource\\GUIKey_"+testSerialNo+".txt";
        String apiKey=SystemLibrary.getValueFromListFile("GUI Key", ": ", dataSourceFilePathName);
        if (!GeneralBasic.removeGeneralAPIKey(apiKey, driver)) errorCounter++;
        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean generateNewTestKeys(String moduleName, String moduleNumber, String testSerialNo, String emailDomainName, String payrollDBName, String comDBName, String appType) throws InterruptedException, IOException, Exception {
        boolean isDone=false;
        int intModuleNumber=0;

        logMessage("*** Start Generate New Test Keys.");
        ESSSoftAssert myAssert=new ESSSoftAssert();

        if (testSerialNo.length()==2){
            intModuleNumber=Integer.valueOf(moduleNumber);
            testSerialNo=getCurrentTestSerialNumber(intModuleNumber, moduleName);
        }

        /////////////// Disable Generate Tenant Key for B2C Testing temporarily by Jim on 20/04/2021 ////////////
        logMessage("Item "+moduleName+"1.1", testSerialNo);
        myAssert.assertEquals(WiniumLib.generateESSTenant(testSerialNo), true, "Failed in Item "+moduleName+"1.1: Generate ESS Tenant Key.");
        //////

        logMessage("Item "+moduleName+"1.2: Generate Web API Key.");
        WebAPIKeyManagement.generateWebAPIKey(testSerialNo, appType);
        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"1.2", "pass", testSerialNo);

        logMessage("*** End of Generate New Keys.");

        isDone=true;
        return isDone;
    }

    public static boolean generateNewTestKeys_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating multi Test Keys from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasicHigh.generateNewTestKeys(cellData[i][2], cellData[i][0], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][15])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Generating multi Test Keys.");
        if (errorCounter>0) isDone=false;
        return isDone;
    }


    public static String getTestKeyConfigureFromDatasheet_Main(int moduleNo, String itemName) throws Exception {
        String outputValue=null;
        int endSerialNo=moduleNo;
        SystemLibrary.logMessage("--- Start getting Test Key configure  from row " + moduleNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(moduleNo, endSerialNo,40,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    //if (!GeneralBasicHigh.generateNewTestKeys(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6])) errorCounter++;
                    switch (itemName){
                        case "moduleName":
                            outputValue=cellData[i][2];
                            break;
                        case "testSerialNo":
                            outputValue=cellData[i][3];
                            break;
                        case "emailDomainName":
                            outputValue=cellData[i][4];
                            break;
                        case "payrollDBName":
                            outputValue=cellData[i][5];
                            break;
                        case "payrollDB2Name":
                            outputValue=cellData[i][16];
                            break;
                        case "comDBName":
                            outputValue=cellData[i][6];
                            break;
                        case "comDB2Name":
                            outputValue=cellData[i][17];
                            break;
                        case "payrollDBOrderNo":
                            outputValue=cellData[i][10];
                            break;
                        case "payrollDB2OrderNo":
                            outputValue=cellData[i][18];
                            break;
                        case "url_ESS":
                            outputValue=cellData[i][12];
                            break;
                        case "moduleFunctionName":
                            outputValue=cellData[i][7];
                            break;
                        case "testRoundCode":
                            outputValue=cellData[i][8];
                            break;
                        case "emailAddressForTestNotification":
                            outputValue=cellData[i][20];
                            break;
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (itemName!=null){
            SystemLibrary.logMessage("The value of '"+itemName+"' is '"+outputValue+"'.");
        }else{
            logError("Item '"+itemName+"' is NOT found.");
        }

        return outputValue;
    }

    public static boolean configureMCS(String moduleName, String testSerialNo, String payrollDBName, String comDBName) throws Exception{
        boolean isDone=false;

        logMessage("Item "+moduleName+"1.3: Configure MCS.");
        if (payrollDBName.equals("ESS_Auto_Payroll1")&&(comDBName.equals("ESS_Auto_COM1"))){
            MCSLib.editMCS_Main(10011, 10011, testSerialNo);
        }
        else if (payrollDBName.equals("ESS_Auto_Payroll2")&&(comDBName.equals("ESS_Auto_COM2"))){
            MCSLib.editMCS_Main(10021, 10021, testSerialNo);
        }
        else if (payrollDBName.equals("ESS_Auto_Payroll3")&&(comDBName.equals("ESS_Auto_COM3"))){
            MCSLib.editMCS_Main(10031, 10031, testSerialNo);
        }


        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"1.3", "pass", testSerialNo);
        isDone=true;
        return isDone;
    }

    /*public static boolean configureMCS_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Configuring multi Test Keys from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,9,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasicHigh.configureMCS(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Configuring multi MCS.");
        if (errorCounter>0) isDone=false;
        return isDone;
    }*/


    public static boolean validateMultiEditWorkflowForm(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start change muliti Workflows from row " + startSerialNo + " to " + endSerialNo + " in \"Workflows\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Workflows");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.valdiateEditWorkFlowForm(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][10], cellData[i][6], cellData[i][7], cellData[i][8], emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of changing Mulit Worklows.");
        return isPassed;
    }

    public static boolean addMultiNewStarters_Main(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding Multi New Starters from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarter\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarter");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNewStarter(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Adding Multi New Starters");
        return isPassed;
    }

    public static boolean activateMultiNewStarter_ViaEmail(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateNewStarterViaEmail(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], cellData[i][7], payrollDBName, testSerialNo, emailDomainName)){
                        errorCounter++;
                        logMessage("Failed activate user via email.");
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean fillMultiNewStarterForm(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        SystemLibrary.logMessage("--- Start Filling Multi New Starter Form from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarterForm\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterForm");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.fillNewStarterForm(cellData[i][3], cellData[i][4], cellData[i][3], cellData[i][4], cellData[i][3], cellData[i][4], cellData[i][3], cellData[i][4], cellData[i][3], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        logMessage("Failed activate user via email.");
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Filling Multi New Starter Form.");
        return isPassed;
    }
    //////////////////////

    public static boolean logonESSAsNewStarter_Main(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"NewStarterForm");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.logonESSAsNewStarter(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], payrolDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_PersonalInformation(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi New Starter - Personal Infomation from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_PersonalInformation(cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Starter - Personal Infomation.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_ContactDetail(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password or Logon as Admin then select this new starter first by call selectUserViaTeamsPage_Main() function first.
        SystemLibrary.logMessage("--- Start Edit Multi New Starter  - Contact Details from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_ContactDetials sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_ContactDetials");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_ContactDetails(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Editing multi New Starter - Contact Details.");
        return isPassed;
    }

    public static boolean validateMultiNewStarter_Employment(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiate Multi New Starter  - Employment from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_Employment sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Employment");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_Employment(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter  - Employment from.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_TaxDetails(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi New Starter  - Tax Details from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_TaxDetails sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_TaxDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_TaxDetails(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19],  cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Starter  - Tax Details.");
        return isLogon;
    }

    public static boolean validateMultiNewStarter_TaxDetailsForm(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiating Multi New Starter  - Tax Details from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_TaxDetails sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_TaxDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_TaxDetailsForm(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter  - Tax Details from.");
        return isLogon;
    }

    public static boolean validateMultiNewStarter_SuperAnnuationForm(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiating Multi New Starter  - Supperannuation from row " + startSerialNo + " to " + endSerialNo+" in NewStarterFrom_Superannuation sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Superannuation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_SuperAnnuationForm(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter - Superannualtion from.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_SuperAnnuation(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi New Starter  - Supperannuation row " + startSerialNo + " to " + endSerialNo+" in NewStarterFrom_Superannuation sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Superannuation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_SuperAnnuationForm(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], cellData[i][30], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Starter - Superannualtion.");
        return isLogon;
    }


    public static boolean validateAPI_MicrOpay_EmployeeDetails_Terminated_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Display Sage MicrOpay Web API port() first
        SystemLibrary.logMessage("--- Start validating MicrOpay Web API - Employee Details - Include Terminated from row " + startSerialNo + " to " + endSerialNo+" in API_MicrOpay sheet.");
        boolean isPass=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"API_MicrOpay");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateAPI_MicrOpay_EmployeeDetails_Terminated(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isPass=true;
        SystemLibrary.logMessage("*** End of Validating MicrOpay Web API.");
        return isPass;
    }

    public static boolean validateAPI_Lookups_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Display Sage MicrOpay Web API port() first
        SystemLibrary.logMessage("--- Start validating MicrOpay Web API - Lookups from row " + startSerialNo + " to " + endSerialNo+" in API_MicrOpay sheet.");
        boolean isPass=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"API_MicrOpay");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateAPI_Lookups(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isPass=true;
        SystemLibrary.logMessage("*** End of Validating MicrOpay Web API.");
        return isPass;
    }

    public static boolean validateMultiNewStarter_BankAccount(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiating Multi New Starter  - BankAccount from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_BankAccount sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_BankAccount");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_BankAccount(cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter - Superannualtion from.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_BankAccount(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi New Starter  - Bank Account from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_BankAccount sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_BankAccount");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_BankAccount(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi NewStarter Bank Account.");
        return isLogon;
    }



    public static boolean validateMultiNewStarter_Summary(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start validating Multi New Starter  - Summary from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_Summary sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Summary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_Summary(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of validting Multi NewStarter Summary.");
        return isLogon;
    }


    public static boolean completeMultiNewStarter_Summary(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Completing Multi New Starter  - Summary from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_Summary sheet.");
        boolean isComplete=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Summary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.completeNewStarter_Summary(cellData[i][12], cellData[i][14], cellData[i][13], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isComplete=true;
        SystemLibrary.logMessage("*** End of Completing Multi NewStarter Summary.");
        return isComplete;
    }

    public static boolean validateNewStarterDetails_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi New Starter Details from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarterDetails\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStaterDetials(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi New Starter Details in \"NewStarterDetails\" sheet.");
        return isPassed;
    }


    public static boolean sendMultiNewStarterToPayroll(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start sending Multi New Starter to Payroll from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarterDetails\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.sendNewStaterToPayroll(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Sending New Starter to payroll.");
        return isPassed;
    }

    public static boolean finaliseMultiNewStarterInESS(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Finalising Multi New Starter from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarterDetails\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.finaliseNewStaterInESS(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], payrollDBName, testSerialNo, emailDomainName, driver)) {
                        errorCounter++ ;
                        logMessage("Try New starter Finalyse 2nd time.");
                        if (!GeneralBasic.finaliseNewStaterInESS(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], payrollDBName, testSerialNo, emailDomainName, driver))
                            errorCounter++ ;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Finalising New Starter to payroll.");
        return isPassed;
    }

    public static boolean validateIntegrationPage_SuperFund_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Integration Page from row " + startSerialNo + " to " + endSerialNo + " in \"Integration\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Integration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateIntegrationPage_SuperFund(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating Integration page.");
        return isDone;
    }

    public static boolean addMultiSuperPDSFileInIntegrationPage(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Super Fund PDS from row " + startSerialNo + " to " + endSerialNo + " in \"Integration\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Integration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addSuperPDSFileInIntegrationPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Validating Integration page.");
        return isDone;
    }

    public static boolean removeMultiNewStartersViaAdmin(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Remove Multi New Starters from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarter\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,60, "NewStarter");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.removeNewStarterViaAdmin( cellData[i][2], null, cellData[i][3], null, emailDomainName, testSerialNo, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Removing Multi New Starters in \"NewStarter\" sheet.");
        return isPassed;
    }

    public static boolean validateMultiNewStarter_PersonalInformation(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Validating Multi New Starter - Personal Infomation from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_PersonalInformation(cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][21], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Starter - Personal Infomation.");
        return isLogon;
    }


    public static boolean validateMultiNewStarter_ContactDetail(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Validating Multi New Starter  - Contact Details from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_ContactDetials sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_ContactDetials");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_ContactDetailsForm(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], cellData[i][30], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi New Starter - Contact Details.");
        return isPassed;
    }

    public static boolean syncChanges_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start multi Sync Changes from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.syncChanges(driver, cellData[i][8])) {
                        errorCounter++ ;
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync Changes.");

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean syncChangesSimultaneously_Main(int startSerialNo, int endSerialNo, WebDriver driver, WebDriver driver2) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start multi Sync Changes from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.syncChangesSimultaneously(driver, driver2, cellData[i][8])) {
                        errorCounter++ ;
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync Changes.");

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean selectUserViaTeamsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Team Members from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.selectUserViaTeamsPage(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][11], cellData[i][13], cellData[i][18], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Team Members in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateMultiNewStarter_Employment_ViaAdmin(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiate Multi New Starter  - Employment from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_Employment sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Employment");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateNewStarter_Employment_ViaAdmin(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter  - Employment from.");
        return isLogon;
    }

    public static String getCurrentTestSerialNumber(int moduleNo, String moduleName) throws Exception {
        String testSerialNo = getTestKeyConfigureFromDatasheet_Main(moduleNo, "testSerialNo");
        String snPrefix=null;
        if (testSerialNo.length()==2){
            snPrefix=testSerialNo;
            testSerialNo=DBManage.getLatestSerialNumberFromReportDB(snPrefix, moduleName);
        }
        return testSerialNo;
    }

    public static boolean downloadMultiLeaveHistoryReport(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        //Dispaly Dashboard first
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Leave History Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadLeaveHistoryReport(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][7], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Leave History Report via Dashboard.");
        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean downloadMultiLeaveBalancesReport_New(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        //Dispaly Dashboard first
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Leave Balance Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadLeaveBalanceReport_New(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][7], driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Leave Balance Report via Dashboard.");
        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean downloadMultiAppliedLeaveByDateReport(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        //Dispaly Dashboard first
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Leave By Date Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadAppliedLeaveByDateReport(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Leave By Date Report via Dashboard.");
        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean validateESSNewStarter_IntroductionPage_ViaAdim_Main(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterForm");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateESSNewStarter_IntroductionPage_ViaAdim(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11] , cellData[i][22], payrolDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }


    public static boolean validateESSNewStarter_WelcomePage_Main(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterForm");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateESSNewStarter_WelcomePage (cellData[i][8], cellData[i][9], cellData[i][10], payrolDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }

    public static boolean editMultiNewStarter_Employment_ViaAdmin(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Valiate Multi New Starter  - Employment from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_Employment sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_Employment");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_Employment_ViaAdmin(cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Valiate Multi New Starter  - Employment from.");
        return isLogon;
    }

    public static boolean editMultiNewStater_AfterReadyToApproveForms(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start sending Multi New Starter to Payroll from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarterDetails\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarterDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStater_AfterReadyToApproveForms(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][15], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Sending New Starter to payroll.");
        return isPassed;
    }

    public static boolean editMultiNewStarter_TaxDetails_nochangeDeclaration(int startSerialNo, int endSerialNo, String payrolDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi New Starter  - Tax Details from row " + startSerialNo + " to " + endSerialNo+" in NewStarterForm_TaxDetails sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"NewStarterForm_TaxDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editNewStarter_TaxDetails_nochangeDeclaration(cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19],  cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Starter  - Tax Details.");
        return isLogon;
    }

    public static boolean activateMultiNewstarter_afterFinalise_ViaEmail(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        SystemLibrary.logMessage("--- Start Activating Multi User Account from row " + startSerialNo + " to " + endSerialNo + " in \"UserAccountSettings\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;
        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"UserAccountSettings");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.activateNewstarter_afterFinaliseViaEmail(cellData[i][10], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][8], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][16], cellData[i][7], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        logMessage("Failed activate user via email.");
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Activating Multi User Account in UserAccountSettings sheet.");
        return isPassed;
    }

    public static boolean logonESSMain_WithWhatNEW(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.logonESS_WithWhatNew(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][10], payrollDBName, testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }


    public static boolean downloadMultiTeamsReportViaAdmin(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomain, WebDriver driver) throws Exception {
        //Super user only
        SystemLibrary.logMessage("--- Start download multi Team Report from row " + startSerialNo + " to " + endSerialNo + " in \"DownloadFile\" sheet.");

        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,6,"DownloadFile");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadTeamsReportViaAdmin(cellData[i][3], cellData[i][4], cellData[i][5], testSerialNo, emailDomain, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Team Report via Dashboard.");
        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean validateMultiApprovalsPage_Attachment(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is for non admin user
        SystemLibrary.logMessage("--- Start validating multi Approvals Page - Attachment from row " + startSerialNo + " to " + endSerialNo + " in \"Approvals\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Approvals");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateApprovalsPage_Attachment(cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating multi My Approvals All - Attachment in \"Approvals\" sheet.");
        return isPassed;
    }

    public static boolean downloadMultiPayAdviceReport(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start download multi Pay Advice Report from row " + startSerialNo + " to " + endSerialNo + " in \"PayAdviceAndPaymentSummary\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"PayAdviceAndPaymentSummary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadPayAdviceReport(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][12], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Pay Advice report.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean downloadMultiPaymentSummaryReport(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start download multi Payment Summary Report from row " + startSerialNo + " to " + endSerialNo + " in \"PayAdviceAndPaymentSummary\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"PayAdviceAndPaymentSummary");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.downloadPaymentSummaryReport(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][12], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Download Multi Payment Summary report.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }


    public static boolean addMultiNonPayrollUser(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Non Payroll User from row " + startSerialNo + " to " + endSerialNo + " in \"NewStarter\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"NewStarter");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNonPayrollUser(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Adding Multi Non Payroll User.");
        return isPassed;
    }

    public static boolean validateBalanceDetails_ViaLeavePage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String serverName, String payrollDBName, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Leave Page - Leave Balace from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateLeaveBalance\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ValidateLeaveBalance");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateBalanceDetails_ViaLeavePage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], testSerialNo, serverName, payrollDBName, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating Leave Page - Leave Balace.");
        return isPassed;
    }


    public static boolean editMultiApprovedLeave(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        boolean isDone=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start edit multi Pending Leave Via Leave Page from row " + startSerialNo + " to " + endSerialNo + " in \"Leave\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Leave");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editApprovedLeave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11],  cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][26], cellData[i][27], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of edit multi Pending Leave Via Leave Page.");
        return isDone;
    }

    public static boolean delteMultiBankAccount(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Editing muliti Bank Accounts from row " + startSerialNo + " to " + endSerialNo + " in \"BankAccounts\" sheet.");
        boolean isDone=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"BankAccounts");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.deleteBankAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Mulit Bank Accounts.");
        return isDone;
    }


    public static boolean editMultiContactDetails_Address_WebAPIOFF(int startSerialNo, int endSerialNo, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, SQLException, ClassNotFoundException{
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Contact Details from row " + startSerialNo + " to " + endSerialNo + " in \"ContactDetails\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"ContactDetails");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.editContactDetails_Address_WebAPIOFF(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], payrollDBName, testSerialNo, emailDomainName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi Contact Details.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean Validatesyncmessage_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start multi Sync All Data from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.Validatesyncmessage(driver, cellData[i][8])) {
                        errorCounter++ ;
                    }
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of multi Sync All Data.");

        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    public static boolean generateNewTestKeysViaCustomer_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating multi Test Keys from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasicHigh.generateNewTestKeysViaCustomer(cellData[i][2], cellData[i][0], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][15])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Generating multi Test Keys.");
        if (errorCounter>0) isDone=false;
        return isDone;
    }

    public static boolean generateNewTestKeysViaCustomer(String moduleName, String moduleNumber, String testSerialNo, String emailDomainName, String payrollDBName, String comDBName, String appType) throws InterruptedException, IOException, Exception {
        boolean isDone=false;
        int intModuleNumber=0;

        logMessage("*** Start Generate New Test Keys.");
        ESSSoftAssert myAssert=new ESSSoftAssert();

        if (testSerialNo.length()==2){
            intModuleNumber=Integer.valueOf(moduleNumber);
            testSerialNo=getCurrentTestSerialNumber(intModuleNumber, moduleName);
        }

        //////////// Disable generate Tenant Key automatically for B2C test temporarily by Jim on 20/04/2020 ////////////
        logMessage("Item "+moduleName+"1.1", testSerialNo);
        myAssert.assertEquals(WiniumLib.generateESSTenant(testSerialNo), true, "Failed in Item "+moduleName+"1.1: Generate ESS Tenant Key.");
        //////

        logMessage("Item "+moduleName+"1.2: Generate Web API Key Via Customer.");
        WebAPIKeyManagement.generateWebAPIKeyViaCustomer(testSerialNo, emailDomainName, appType);
        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"1.2", "pass", testSerialNo);

        logMessage("*** End of Generate New Keys Via Customer.");

        isDone=true;
        return isDone;
    }

    public static boolean validateSelectedTeams_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateSelectedTeams(cellData[i][2], cellData[i][19], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }


    public static boolean validateAllSelectedTeams_UncheckAnyTeam_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateAllSelectedTeams_UncheckAnyTeam(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }


    public static boolean validateCheckboxes_SelectActivateMembers_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateCheckboxes_SelectActivateMembers(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateSelectActivateMembersPopup_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateSelectActivateMembersPopup(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean massActivateMembers_Teams_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                boolean isEmpty=Strings.isNullOrEmpty(cellData[i][1]);
                if (isEmpty){
                    //if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.massActivateMembers_Teams(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateSelectedTeams_AfterMassActivateMembers_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi Selected Teams from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateSelectedTeams_AfterMassActivateMembers(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], cellData[i][24], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi Selected Teams in \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validateViewSelectedTeamsOnLeave_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating Multi View Selected Teams on Leave from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateViewSelectedTeamsOnLeave(cellData[i][2], cellData[i][19], cellData[i][20], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][18], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validating Multi View Selected Teams on Leave in \"Teams\" sheet.");
        return isPassed;
    }


    public static boolean validatePopupDuplicateRole_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validate Popup Duplicate multi Roles from row " + startSerialNo + " to " + endSerialNo + " in \"RolesPermissions\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"RolesPermissions");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatePopupDuplicateRole(cellData[i][2], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of Validate Popup Duplicate multi Roles");
        return isPassed;
    }

    public static boolean validatechangeMemberRoleDialogueViaTeamsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validate change Member Role Dialogue from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatechangeMemberRoleDialogue_ViaTeamsPage(cellData[i][2], cellData[i][4], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of validate change Member Role Dialogue \"Teams\" sheet.");
        return isPassed;
    }

    public static boolean validatechangeRoleAfterClickManagerRole_ViaTeamsPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start change Role After Click Manager Role from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,21,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validatechangeRoleAfterClickManagerRole_ViaTeamsPage(cellData[i][2], cellData[i][4], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        SystemLibrary.logMessage("*** End of change Role After Click Manager Role in \"Teams\" sheet.");
        return isPassed;
    }
    public static boolean validateChangeRoleTeamsElipsisMenuInTeamsAndRolesPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start validate Change Role Teams Elipsis Menu in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validateChangeRoleTeamsElipsisMenuInTeamsAndRolesPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8],  cellData[i][22], cellData[i][23], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isDone=false;
        SystemLibrary.logMessage("*** End of validate Change Role Teams Elipsis Menu in \"Teams\" sheet.");
        return isDone;
    }

    public static boolean changeMultiRole_ViaTeamsNRolesPage (int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Change Role Teams Elipsis Menu in Teams & Roles page from row " + startSerialNo + " to " + endSerialNo + " in \"TeamsAndRoles\" sheet.");
        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TeamsAndRoles");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.changeRole_ViaTeamsNRolesPage(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][20], cellData[i][22], cellData[i][23], testSerialNo, emailDomainName, driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isDone=false;
        SystemLibrary.logMessage("*** End of Change Role Teams Elipsis Menu in \"Teams\" sheet.");
        return isDone;
    }

    public static boolean validate_EllipsisMenuInTeamPage_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Ellipsis Menu in Team page from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_EllipsisMenuInTeamPage(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName,  driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Ellipsis Menu In Teams Page");
        return isPassed;
    }

    public static boolean validate_EllipsisMenuWithinSingleTeamTable_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        SystemLibrary.logMessage("--- Start Validating multi Ellipsis Menu within Team Table from row " + startSerialNo + " to " + endSerialNo + " in \"Teams\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"Teams");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.validate_EllipsisMenuWithinSingleTeamTable(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], testSerialNo, emailDomainName,  driver)) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        SystemLibrary.logMessage("*** End of Validating multi Ellipsis Menu within Team table.");
        return isPassed;
    }

    public static boolean generateWebAPIKey_SecondDatabase(String moduleName, String moduleNumber, String testSerialNo, String emailDomainName, String payrollDBName, String comDBName, String appType, String dbName) throws InterruptedException, IOException, Exception {
        boolean isDone=false;
        int intModuleNumber=0;

        logMessage("*** Start Generate Web API Key for Second Database.");
        ESSSoftAssert myAssert=new ESSSoftAssert();

        if (testSerialNo.length()==2){
            intModuleNumber=Integer.valueOf(moduleNumber);
            testSerialNo=getCurrentTestSerialNumber(intModuleNumber, moduleName);
        }

        logMessage("Item "+moduleName+"15.1: Generate Web API Key Second Database.");
        WebAPIKeyManagement.generateWebAPIKey2(testSerialNo, appType, dbName);
        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"15.1", "pass", testSerialNo);

        logMessage("*** End of Generate New Keys.");

        isDone=true;
        return isDone;
    }

    public static boolean generateWebAPIKey_SecondDatabase_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating multi Test Keys from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasicHigh.generateWebAPIKey_SecondDatabase(cellData[i][2], cellData[i][0], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][15], cellData[i][21])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Generating multi Test Keys.");
        if (errorCounter>0) isDone=false;
        return isDone;
    }

    public static boolean configureMCS2(String moduleName, String testSerialNo, String payrollDB2Name, String comDB2Name) throws Exception{
        boolean isDone=false;

        logMessage("Item "+moduleName+"15.2: Configure MCS.");
        if (payrollDB2Name.equals("ESS2_Auto_Payroll")&&(comDB2Name.equals("ESS_Auto_COM1"))){
            MCSLib.editMCS2_Main(20001, 20001, testSerialNo);
        }
        else if (payrollDB2Name.equals("ESS2_Auto_Payroll2")&&(comDB2Name.equals("ESS2_Auto_COM2"))){
            MCSLib.editMCS2_Main(20011, 20011, testSerialNo);
        }

        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"15.2", "pass", testSerialNo);
        isDone=true;
        return isDone;
    }


/*
    //Override below
    public static boolean addNewWebAPIConfigurationSecondDB_Main(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Adding multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNewWebAPIConfiguration_SecondDB(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], cellData[i][12], testSerialNo, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi New Web API Configuration.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }

    //Override above
    public static boolean addNewWebAPIConfigurationSecondDB_Main(int startSerialNo, int endSerialNo, String testSerialNo, String dbName, WebDriver driver) throws InterruptedException, IOException{
        SystemLibrary.logMessage("--- Start Adding multi New Web API Configuration from row " + startSerialNo + " to " + endSerialNo + " in \"WebAPIConfiguration\" sheet.");

        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"WebAPIConfiguration");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasic.addNewWebAPIConfiguration_SecondDB(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][11], testSerialNo, dbName, driver)) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi New Web API Configuration.");
        if (errorCounter>0) isPassed=false;
        return isPassed;
    }
*/

/*
    public static boolean generateNewTestKeysViaCustomer_SecondDatabase_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating multi Test Keys from row " + startSerialNo + " to " + endSerialNo + " in \"TestKeys\" sheet.");

        boolean isDone=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"TestKeys");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!GeneralBasicHigh.generateNewTestKeysViaCustomer_SecondDatabase(cellData[i][2], cellData[i][0], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][15])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Generating multi Test Keys.");
        if (errorCounter>0) isDone=false;
        return isDone;
    }

    public static boolean generateNewTestKeysViaCustomer_SecondDatabase(String moduleName, String moduleNumber, String testSerialNo, String emailDomainName, String payrollDBName, String comDBName, String appType) throws InterruptedException, IOException, Exception {
        boolean isDone=false;
        int intModuleNumber=0;

        logMessage("*** Start Generate Web API Key for Second Database.");
        ESSSoftAssert myAssert=new ESSSoftAssert();

        if (testSerialNo.length()==2){
            intModuleNumber=Integer.valueOf(moduleNumber);
            testSerialNo=getCurrentTestSerialNumber(intModuleNumber, moduleName);
        }

        logMessage("Item "+moduleName+"1.2: Generate Web API Key Via Customer for Second Database.");
        WebAPIKeyManagement.generateWebAPIKeyViaCustomer(testSerialNo, emailDomainName, appType);
        DBManage.logTestResultIntoDB_Main("Item "+moduleName+"1.2", "pass", testSerialNo);

        logMessage("*** End of Generate New Keys Via Customer for Second Database.");

        isDone=true;
        return isDone;
    }
*/







}
