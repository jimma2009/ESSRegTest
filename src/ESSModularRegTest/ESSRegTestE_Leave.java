package ESSModularRegTest;

import Lib.*;
import PageObject.PageObj_ApplyForLeave;
import PageObject.PageObj_Leave;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.*;

public class ESSRegTestE_Leave {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=105;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName="E";

    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

            //////////////// New Field /////////////
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");
            testEmailNotification =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailAddressForTestNotification");
            ///////
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 10011) //a
    public void testE10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item E10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item E1.4: Delete and Restore Payroll and Common DB.");
        DBManage.deleteAndRestoreSageMicrOpayDB(payrollDBName, comDBName, emailDomainName);

        //Assing the rights to DB after restoring.
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(201, 201);
            DBManage.sqlExecutor_Main(202, 202);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(401, 401);
            DBManage.sqlExecutor_Main(402, 402);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(411, 411);
            DBManage.sqlExecutor_Main(412, 412);
        }
        DBManage.logTestResultIntoDB_Main("Item E1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item E1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item E1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item E1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item E1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item E1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item E1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item E1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item E10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testE10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test E10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item E1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item E1.8: Add API configuration.");

        logMessage("Item E1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item E1.9: Sync All.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testE10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test E10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item E2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item E2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item E2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item E2.2: Validate Team Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10031.");
        myAssert.assertAll();
    }


    @Test (priority=10041)
    public static void testE10041_ImportTeamViaTPU() throws Exception {
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("*** Start Test E10041: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();

        logMessage("Item E3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item E3: Import Team via TPU.");

        logMessage("*** End of test E10041");
        myAssert.assertAll();
    }

    /*
    @Test(priority = 10042)
    public void testE10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31168, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10042: Add Teams and Members.");
        myAssert.assertAll();
    }


    @Test(priority = 10042)
    public void testE10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31140, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10042: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10043)
    public void testE10043_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10043: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31141, 31150, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10043: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10044)
    public void testE10044_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10044: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31151, 31160, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10044: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10045)
    public void testE10045_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10045: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31161, 31168, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10045: Add Teams and Members.");
        myAssert.assertAll();
    }
     */

    @Test(priority = 10051) //a
    public void testE10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test E10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item E4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item E4: Validate Team after import team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10051.");
        myAssert.assertAll();
    }


    ////////////////////////
    // Start User Detail function test

    @Test(priority = 10061)
    public static void testE10061_EditAndValidateLeaveSettings() throws InterruptedException, SQLException, IOException, ClassNotFoundException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test E10061.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item E5.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(11, 11, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.1: Step 189_1: Validate Annual Leave Settings.");

        SystemLibrary.logMessage("Item E5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(12, 12, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.2: Step 189_2: Validate Long Service Leave Settings.");

        SystemLibrary.logMessage("Item E5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(13, 13, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.3: Step 189_3: Validate Other Leave Settings.");

        SystemLibrary.logMessage("Item E5.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(14, 14, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.4: Step 189_4: Validate Personal Leave Settings.");

        SystemLibrary.logMessage("Item E5.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(15, 15, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.5: Step 189_5: Validate Time In Lieu #2 Settings.");

        SystemLibrary.logMessage("Item E5.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(16, 16, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.6: Step 189_6: Validate Time In Liue Settings.");

        ////////////////////////
        SystemLibrary.logMessage("Item E5.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main(21, 21, driver), true, "Failed in Item E5.7: Step 190 - Step 192: Edit Annual Leave Settings.");

        SystemLibrary.logMessage("Item E5.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main(22, 22, driver), true, "Failed in Item E5.8: Step 183 - Step 193 : Edit Other Leave Settings.");

        SystemLibrary.logMessage("Item E5.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main(23, 23, driver), true, "Failed in Item E5.9: Step 194: Edit Leave Settings.");

        ///////////////////////////////////////////////

        SystemLibrary.logMessage("Item E5.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10001, 10001, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.10: Step 194_1: Validate Annual Leave Settings.");

        SystemLibrary.logMessage("Item E5.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10002, 10002, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.11: Step 194_2: Validate Long Service Leave Settings.");

        SystemLibrary.logMessage("Item E5.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10003, 10003, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.12: Step 194_3: Validate Other Leave Settings.");

        SystemLibrary.logMessage("Item E5.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10004, 10004, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.13: Step 194_4: Validate Personal Leave Settings.");

        SystemLibrary.logMessage("Item E5.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10005, 10005, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.14: Step 194_5: Validate Time In Lieu #2 Settings.");

        SystemLibrary.logMessage("Item E5.15", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main(10006, 10006, emailDomainName, testSerialNo, driver), true, "Failed in Item E5.15: Step 194_6: Validate Time In Liue Settings.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10061: Edit and Validate Leave Settings.");
        myAssert.assertAll();

    }

    //Must run without headless
    @Test(priority = 10071)
    public static void testE10071_DownloadAndValidateLeaveBalanceReport() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10071.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item E6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport(41, 41, driver), true, "Failed in Item E6: Step 195: Download and validate Leave Balance Report.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10071: Download and Validate Leave Balances Report.");
        myAssert.assertAll();
    }

    @Test(priority = 10081)
    public static void testE10081_ValidateLeave() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10081");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete all data in _iptblFinaliseLeaveApplication table");
        DBManage.sqlExecutor_Main(141, 141);

        logMessage("Log on as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Activate Sharon A");
        logMessage("Item E7.1", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(112, 112, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.1: Activate Sharon A.");

        logMessage("Activate Jack F");
        logMessage("Item E7.2", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(103, 103, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.2: Activate Jack F.");

        logMessage("Activate Sue A.");
        logMessage("Item E7.3", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(104, 104, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.3: Activate Sue A.");

        //////////// Log on as Sharon A ////////////
        logMessage("Item E7.4", testSerialNo);
        signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.4: Log on as Sharon A.");


        //////////// Log on as Jack F ////////////
        logMessage("Item E7.5", testSerialNo);
        signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.5: Log on as Jack F.");
        //////

        //////////// Log on as Sue A ////////////
        logMessage("Item E7.6", testSerialNo);
        signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.6: Log on as Sue A.");
        //////

        signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //////////////////////
        SystemLibrary.logMessage("Item E7.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E7.7: Logon as Sharon A the 2nd time.");

        SystemLibrary.logMessage("Item E7.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(91, 91, testSerialNo, emailDomainName, driver), true, "Failed in Item E7.8: Step 196: Validate Sharon A Dashboard.");

        SystemLibrary.logMessage("Step 197: Display Leave page after clickng View More button in Dashboard.");
        logMessage("Item E7.9", testSerialNo);
        GeneralBasic.displayDashboard(driver);
        driver.findElement(By.xpath("//div[@id='leave-balances-widget']//div[@class='ps-content show-content']//a[contains(text(),'View more')]")).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        SystemLibrary.logMessage("View more button is clicked.");
        WebElement lable_Leave=waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver);
        boolean isLeavePageShown=false;
        if (lable_Leave!=null) {
            isLeavePageShown=true;
            logMessage("Leave Page is shown.");
        }
        else{
            logError("Leave Page is NOT shonw.");
        }
        myAssert.assertEquals(isLeavePageShown, true, "Falied in Item E7.9: Step 197: Display Leave page after clickng View More button in Dashboard.");

        logMessage("Item E7.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item E7.10: Step 197: Validate Leave Balance via Dashboard.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10081.");
        myAssert.assertAll();
    }

    /////////////////////////// With DB Pay Advice issue below ///////////////////
    //Must run without Headless
    @Test(priority = 10091)
    public static void testE10091_ApplyLeave() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10091.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Log on as Sharson ANDREWS.");
        GeneralBasicHigh.logonESSMain(341, 341, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Step 198 - Step 200: Display and Validate Apply Leave form.");
        logMessage("Item E8.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApplyForLeaveForm_Main(10011, 10011, emailDomainName, testSerialNo, driver), true, "Failed in Item E8.1: Step 198: Validate Apply Leave Form.");

        SystemLibrary.logMessage("Step 200: Validate Leave Reason List in Apply Leave form.");
        logMessage("Item E8.2", testSerialNo);
        GeneralBasic.displayLeavePage(driver);
        PageObj_Leave.button_AddLeave(driver).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Apply for Leave button is clicked.");
        logScreenshot(driver);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveReasonListInApplyForLeaveForm_Main(10051, 10051, testSerialNo, emailDomainName, driver), true, "Failed in Item E8.2: Step 200: Validate Leave Reason List in Apply Leave form.");
        PageObj_ApplyForLeave.button_Close(driver).click();
        logMessage("Close Apply for Leave button is clicked.");

        SystemLibrary.logMessage("Step 199 - Step 205: Apply User Defined Leave for Sharon A.");
        logMessage("Item E8.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(71, 71, testSerialNo, driver), true, "Failed in Item E8.3: Step 199 - Step 205: Apply User Defined Leave for Sharon A.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10091.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10101)
    public static void testE10101_ValidateLeaveLogAndAttachmentAfterApplyLeave() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10101.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Log on as Sharson ANDREWS.");
        logMessage("Item E9.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(341, 341, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E9.1: Logon as Sharon A.");

        logMessage("Step 205: Validate Leave Page after applying leave.");
        logMessage("Item E9.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(10031, 10031, testSerialNo, emailDomainName, driver), true, "Failed in Item E9.2: Step 205: Validate Leave Page after applying leave.");

        logMessage("Step 206_2: Validate Leave Attachment.");
        logMessage("Item E9.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Attachment_Man(10051, 10051, testSerialNo, driver), true, "Failed in Item E9.4: Step 206_2: Validate Leave Attachment.");

        logMessage("Step 206_1: Validate log in Leave Page.");
        logMessage("Item E9.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Log_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Failed in Item E9.3: Step 206_1: Validate log in Leave Page.");


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10101.");
        myAssert.assertAll();
    }

    //Must Run without Headless
    @Test(priority = 10102)
    public static void testE10102_ValidateLeaveReportAfterApplyUserDefinedLeave() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10102.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Log on as Admin.");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item E.X9.4.1: Download and Validate Leave History Report in Team B after apply UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20041, 20041, testSerialNo, driver), true, "Failed Item E.X9.4.1: Validate Leave History Report in Team B after apply UDL.");

        logMessage("Item E.X9.4.2: Download and Validate Leave Balances Report in Team B after apply UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20042, 20042, driver), true, "Failed Item E.X9.4.2: Download and Validate Leave Balance Report in Team B after apply UDL");

        logMessage("Item E.X9.4.3: Download and Validate Applied Leave By Date Report in Team B after apply UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20043, 20043, testSerialNo, driver), true, "Failed Item E.X9.4.3: Download and Validate Applied Leave By Date Report in Team B after apply UDL");

        logMessage("Item E9.4.X1: Downlood and Validate Audit Report after member apply leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(251, 251, emailDomainName, testSerialNo, driver), true, "Failed in Item E9.4.X1: Downlood and Validate Audit Report after member apply leave.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10102.");
        myAssert.assertAll();
    }

    @Test (priority = 10111)
    public static void testE10111_ValidateEmailNotifications() throws Exception {
        logMessage("*** Start Test E10111.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("step 212_1: Validate Jack FINGLE's Leave Application Submission email content.");
        logMessage("Item E10.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10031, 10031, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E10.1: step 212_1: Validate Jack FINGLE's Leave Application Submission email content.");

        SystemLibrary.logMessage("step 212_3: Validate Sue APPLEBY's Leave Application Submission email content.");
        logMessage("Item E10.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10041, 10041, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E10.2: step 212_3: Validate Sue APPLEBY's Leave Application Submission email content.");

        SystemLibrary.logMessage("step 213_1: Validate Sharon ANDREWS's Leave Application email content.");
        logMessage("Item E10.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10051, 10051,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E10.3: step 213_1: Validate Sharon ANDREWS's Leave Application email content.");

        JavaMailLib.deleteAllMail(emailDomainName);
        logMessage("End of Test E10111.");
        myAssert.assertAll();
    }

    @Test(priority = 10121)
    public void testE10121_ApproveTheLeaveViaAdminAndValidateEmail() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test E10121.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Step 216: Approve the leave via Admin.");
        logMessage("Item E11.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(21, 21, testSerialNo, emailDomainName, driver), true, "Failed in Item E11.1: Step 216: Approve the leave via Admin.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("Step 217_1: Validate Jack FINGLE's Leave Application approval email content.");
        logMessage("Item E11.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10061, 10061, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item 11.2: Step 217_1: Validate Jack FINGLE's Leave Application approval email content.");

        SystemLibrary.logMessage("step 217_3: Validate Sue APPLEBY's Leave Application approval email content.");
        logMessage("Item E11.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10071, 10071, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item 11.3: step 217_3: Validate Sue APPLEBY's Leave Application approval email content.");

        SystemLibrary.logMessage("step 218_1: Validate Sharon ANDREWS's Leave request email content.");
        logMessage("Item E11.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10081, 10081, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item 11.4: step 218_1: Validate Sharon ANDREWS's Leave Application request email content.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);
        SystemLibrary.logMessage("*** End of Test E10121.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10122)
    public static void testE10122_ValdiateLeaveReportAfterApplyUDL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10122.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X11.4.1: Download and Validate Leave History Report in Team Directory after Approve UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20051, 20051, testSerialNo, driver), true, "Failed Item E.X11.4.1: Validate Leave History Report in Team B after approve UDL.");

        logMessage("Item E.X11.4.2: Download and Validate Leave Balances Report in Team B after Approve UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20052, 20052, driver), true, "Failed Item E.X11.4.2: Download and Validate Leave Balance Report in Team B after approved UDL.");

        logMessage("Item E.X11.4.3: Download and Validate Applied Leave By Date Report in Team B after Approve UDL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20053, 20053, testSerialNo, driver), true, "Failed Item E.X11.4.3: Download and Validate Applied Leave By Date Report in Team B after approved UDL.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10122.");
        myAssert.assertAll();
    }


    //Under Developing...
    @Test(priority = 10131)
    public static void testE10131_ValdiateSueALeaveBalanceInDashboardAndLeavePage() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10131.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Step 246: Log in as Sue APPLEBY");
        GeneralBasicHigh.logonESSMain(351, 351, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E12.1: Step 246_1: Validate Sue A dashboard.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item E12.1: Step 246_1: Validate Sue A's dashboard.");

        logMessage("Item E12.2: Step 246_2: Validate Annual Leave balance in Sue A Dashboard Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10001, 10001, serverName, payrollDBName, driver), true, "Failed in Item E12.2: Step 246_2 Validate Annual Leave balance in Sue A's Dashboard Page.");

        logMessage("Item E12.3: Step 246_3: Validate Personal Leave balance in Sue A Dashboard Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10002, 10002, serverName, payrollDBName,  driver), true, "Failed in Item E12.3: Step 246_3: Validate Personal Leave balance in Sue A's Dashboard Page.");

        logMessage("Item E12.4: Step 246_4: Validate Long Service Leave balance in Sue A Dashboard Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10003, 10003, serverName, payrollDBName, driver), true, "Failed in Item E12.4: Step 246_4: Validate Long Service Leave balance in Sue A's Dashboard Page.");

        ////////////////////

        logMessage("Item E12.5: Step 246_5: Validate Annual Leave balance in Sue A Leave Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10011, 10011, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item E12.5: Step 246_5: Validate Annual Leave balance in Sue A's Dashboard Page.");

        logMessage("Item E12.6: Step 246_6: Validate Personal Leave balance in Sue A Leave Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10012, 10012, testSerialNo,  serverName, payrollDBName, driver), true, "Failed in Item E12.6: Step 246_6: Validate Personal Leave balance in Sue A's Dashboard Page.");

        logMessage("Item E12.7: Step 246_7: Validate Long Service Leave balance in Sue A Leave Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10013, 10013, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item E12.7: Step 246_7: Validate Long Service Leave balance in Sue A's Dashboard Page.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("End of Test E10131.");
        myAssert.assertAll();
    }

    //Under Developing ...
    @Test (priority=10141)
    public static void testE10141_ValidateChristineLeaveBalance() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test E10141.");

        //Activate Christine R firest.
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on as Admin user.");
        logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Activate Christine R.");
        logMessage("Item E13.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(106, 106, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Faield in Item E13.1: Activate Christine R");

        signoutESS(driver);
        driver.close();

        //////////////
        logMessage("Step 259: Log on as Christine RAMPLING");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item E13.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20001, 20001, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E13.2: log on as Christine R.");

        logMessage("Step 259: Validate Christine RAMPLING's Dashboard.");
        logMessage("Item E13.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20031, 20031,  testSerialNo, emailDomainName, driver), true, "Failed in Item E13.3: Step 259: Validate Christine RAMPLING's Dashboard.");

        logMessage("Step 260_1: Validate Annual Leave balance in Christine R's Dashboard Page.");
        logMessage("Item E13.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10021, 10021, serverName, payrollDBName, driver), true, "Failed in Item E13.4: Step 260_1: Validate Annual Leave balance in Christine R's Dashboard Page.");

        logMessage("Step 260_2: Validate Personal Leave balance in Christine R's Dashboard Page.");
        logMessage("Item E13.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10022, 10022, serverName, payrollDBName,  driver), true, "Failed in Item E13.5: Step 260_2: Validate Personal Leave balance in Chrinstine R's Dashboard Page.");

        logMessage("Step 260_3: Validate Long Service Leave balance in Chrinstine R's Dashboard Page.");
        logMessage("Item E13.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(10023, 10023, serverName, payrollDBName, driver), true, "Failed in Item E13.6: Step 260_3: Validate Long Service Leave balance in Christine R's Dashboard Page.");

        logMessage("Step 260_4: Validate Time in Lieu #1 balance in Chrinstine R's Leave Page.");
        logMessage("Item E13.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20001, 20001, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E13.7: Step 260_4: Validate Time in Lieu #1 balance in Chrinstine R's Leave Page.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("End of Test E10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public static void testE10151_CheckTerminatedEmployee_1() throws Exception {
        logMessage("*** Start Test E10151.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Step: Logon as Admin. validate Search result of Ryan MAIN");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 400: Validate Search result of Ryan MAIN");
        logMessage("Item E14.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Faield in Item E14.1: Step 400: Validate Search result of Ryan MAIN");

        logMessage("Step 401: Validate Ryan MAIN's banner");
        logMessage("Item E14.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(291, 291, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.2: Step 401: Validate Ryan MAIN's banner");

        logMessage("Change Admin Role Permission - Employeement - From Deny to View.");
        logMessage("ITem E14.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(51, 51, driver), true, "Failed in Item E14.3: Change Admin Role Permission - Employeement from Deny to View.");

        logMessage("Step 402: Validate Rayn MAIN's Employment page");
        logMessage("Item E14.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.4: Step 402: Validate Rayn MAIN's Employment page");

        logMessage("Step 403: Valiate Team B.");
        logMessage("Item E14.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.5: Step 403: Valiate Team B.");

        signoutESS(driver);
        driver.close();
        logMessage("End of Test E10151.");
        myAssert.assertAll();
    }

    @Test(priority = 10161)
    public static void testE10161_CheckTerminatedEmployee_2() throws Exception {
        logMessage("*** Start Test E10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Step 404: Logon as Jack FINGLE.");
        logMessage("Item E14.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(461, 461, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E14.6: Step 404: Logon as Jack FINGLE.");

        logMessage("Step 404: Search for Ryan MAIN");
        logMessage("Item E14.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(21, 21, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.7: Step 404: Search for Ryan MAIN");

        logMessage("Step 405_1: Validate Teams and Roles page.");
        logMessage("Item E14.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.8: Step 405_1: Validate Teams and Roles page.");

        logMessage("Step 405_2: Validate Team B.");
        logMessage("Item E14.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.9: Step 405_2: Valiate Team B.");

        signoutESS(driver);
        driver.close();
        logMessage("End of Test E10161.");
        myAssert.assertAll();
    }

    //Under Developing...
    @Test(priority = 10171)
    public static void testE10171_CheckTerminatedEmployee_3() throws Exception {
        logMessage("*** Start Test E10171.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Step 406_1: Logon as Sharon ANDREWS.");
        logMessage("Item E14.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(481, 481, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item E14.10: Step 406_1: Logon as Sharon ANDREWS.");

        logMessage("Step 406_2: Search for Ryan MAIN");
        logMessage("Item E14.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(31, 31, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.11: Step 406_2: Search for Ryan MAIN");

        logMessage("Step 407: Validate Directory All");
        logMessage("Item E14.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item E14.12: Step 407: Validate Directory All");

        signoutESS(driver);
        driver.close();
        logMessage("End of Test E10171.");
        myAssert.assertAll();
    }

    @Test(priority = 10181)
    public static void testE10181_RemoveTerminationAndSync() throws Exception {
        logMessage("*** Start Test 10181.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Step 408_1: Remove Ryan MAIN's Termination statu in Sage MicrOpay.");
        logMessage("Item E15.1", testSerialNo);
        switch (payrollDBName){
            case "ESS_Auto_Payroll1":
                DBManage.sqlExecutor_Main(121, 121);
                break;
            case "ESS_Auto_Payroll2":
                DBManage.sqlExecutor_Main(404, 404);
                break;
            case "ESS_Auto_Payroll3":
                DBManage.sqlExecutor_Main(414, 414);
                break;


        }

        myAssert.assertEquals(true, true, "Failed in Item E15.1: Remove Ryan M Termination status in Sage MicrOpay.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Step 408_2: Logon as Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 408_3: Sydne all data.");
        logMessage("Item E15.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(121, 121, driver), true, "Failed in Item E15.2: Step 408_3: Sydne all data.");

        signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Step 409_1: Logon as Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 409_2: Validate Ryan's termination status in search result.");
        logMessage("Item E15.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item E15.3: Step 409_2: Validate Ryan's termination status in search result.");

        logMessage("Step 409_3: Validate Ryan's termination status in Banner.");
        logMessage("Item E15.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(301, 301, testSerialNo, emailDomainName, driver), true, "Failed in Item E15.4: Step 409_3: Validate Ryan's termination status in Banner.");

        signoutESS(driver);
        driver.close();

        /////////////////
        logMessage("Step 410: Logon as Sharon.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(511, 511, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 410_2: Log on as Sharon and Search User Ryan MAIN.");
        logMessage("Item E15.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(51, 51, testSerialNo, emailDomainName, driver), true, "Failed in Item E15.5: Step 400: Validate Ryan's termination status in search result.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test 10181.");
        myAssert.assertAll();
    }

    @Test(priority = 10191)
    public static void testE10191_ApplyLeaveForRyanM() throws Exception {
        logMessage("*** Start Test E10191...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Activate Ryan M.");
        logMessage("Item E16.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(108, 108, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E16.1: Activate Ryn M.");

        signoutESS(driver);
        driver.close();

        logMessage("Step 417_1: Log on As Ryan MAIN");
        logMessage("Item E16.2", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(521, 521, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item E16.2: Step 417_1: Log on As Ryan MAIN.");

        logMessage("Step 417_2: Validate Ryan M's Dashboard.");
        logMessage("Item E16.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.3: Step 471_2: Validate Ryan M's Dashboard.");

        logMessage("Step 418 - Step 423: Apply Leave.");
        logMessage("Item E16.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(81, 81, testSerialNo, driver), true, "Failed in Item E16.4: Step 418 - Step 423: Apply Leave.");

        logMessage("Step 424: Click Next Leave in Dashbarod screen.");
        logMessage("Item E16.5", testSerialNo);
        GeneralBasic.displayDashboard(driver);
        WebElement button_NextLeave=SystemLibrary.waitChild("//div[@class='normal-header'][contains(text(),'Next Leave')]", 10, 1, driver);

        if (button_NextLeave!=null){
            button_NextLeave.click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Leave Page should shown.");
            logScreenshot(driver);

            WebElement lable_Leave=SystemLibrary.waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver);
            if (lable_Leave!=null){
                logMessage("Leave Page is shown after click Next Leave button on Dashboard page.");
                DBManage.logTestResultIntoDB_Main("Item E16.5", "pass", testSerialNo);
            }else{
                logError("Leave Page is NOT shown after click Next Leave button on Dashboard page.");
                myAssert.fail("Failed in Item E16.5: Step 424_1: Click Next Leave in Dashboard screen.");
                DBManage.logTestResultIntoDB_Main("Item E16.5", "fail", testSerialNo);
            }
        }else{
            DBManage.logTestResultIntoDB_Main("Item E16.5", "fail", testSerialNo);
        }

        logMessage("Step 424_2: Validate Ryan MAIN's Leave Page.");
        logMessage("Item E16.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(91, 91, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.6: Step 424_2: Validate Ryan MAIN's Leave Page.");

        logMessage("Step 426: Validate Leave Page - Apply Filter 1 - Untick Other Leave");
        logMessage("Item E16.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(10071, 10071, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.8: Step 426: Validate Leave Page - Apply Filter - Untick Other Leave");

        logMessage("Step 427: Validate Leave Page - Apply Filter 2 ");
        logMessage("Item E16.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(10072, 10072, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.9: Step 427: Validate Leave Page - Apply Filter 2 ");

        logMessage("Step 428: Reset Filter.");
        logMessage("Item E16.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(10073, 10073, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.10: Step 428: Reset fileter.");

        logMessage("Step 429: Validate Leave Upcoming Tab.");
        logMessage("Item E16.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(101, 101, testSerialNo, emailDomainName, driver), true, "Failed in Item E10.11: Step 429: Validate Leave Page - Upcoming.");

        logMessage("Step 430: Validate Leave Page - Pending.");
        logMessage("Item E16.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(102, 102, testSerialNo, emailDomainName, driver), true, "Failed in Item E10.12: Step 430: Validate Leave Page - Pending.");

        logMessage("Step 431: Validate Ryan M's Emails.");
        logMessage("Item E16.13", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(221, 221, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E10.13: Step 431: Validate Ryan MAIN's Email.");

        logMessage("Step 432: Validate Sue A's Emails.");
        logMessage("Item E16.14", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20001, 20001, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E10.14: Step 432: Validate Sue APPLEBY's Email.");

        logMessage("Step 425: Validate Log in Leave Page.");
        logMessage("Item E16.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Log_Main(10061, 10061, testSerialNo, emailDomainName, driver), true, "Failed in Item E16.7: Step 425: Validate Log in Leave Page.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10191.");
        myAssert.assertAll();
    }

    @Test(priority = 10201)
    public static void testE10201_ValidateLeaveAfterApplyLeaveForRyanMViaAdmin() throws Exception {
        logMessage("*** Start Test E10201...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Admin - My Approvals.");
        logMessage("Item E17.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item E17.1: Validate My Approvals.");

        logMessage("Validate Ryan M Leave Page via Admin. ");
        logMessage("Item E17.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item E17.2: Validate Ryan M Leave Page via Admin.");

        logMessage("Step 598: Validate Team - Directory - Leave - Ryan MAIN.");
        logMessage("Item E17.3: Step 598: Validate Team - Directory - Leave - Ryan MAIN.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item E17.3: Step 598: Validate Team - Directory - Leave - Ryan MAIN.");

        signoutESS(driver);
        driver.close();

        JavaMailLib.deleteAllMail(emailDomainName);
        logMessage("*** End of Test E10201.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10202)
    public static void testE10202_ValdiateLeaveReportAfterApplyVolunteerLeave() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10202.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X17.3.1: Download and Validate Leave History Report in Team B after apply Volunteer Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20091, 20091, testSerialNo, driver), true, "Failed in Item E.X17.3.1: Download and Validate Leave History Report in Team B after apply Volunteer Leave.");

        logMessage("Item E.X17.3.2: Download and Validate Leave Balances Report in Team B after apply Volunteer Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20092, 20092, driver), true, "Failed in Item E.X17.3.2: Download and Validate Leave Balances Report in Team B after apply Volunteer Leave");

        logMessage("Item E.X17.3.3: Download and Validate Applied Leave By Date Report in Team B after apply Volunteer Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20093, 20093, testSerialNo, driver), true, "Failed in Item E.X17.3.3: Download and Validate Applied Leave By Date Report in Team B after apply Volunteer Leave");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10202.");
        myAssert.assertAll();
    }


    @Test(priority = 10211)
    public static void testE10211_ValidateJenniferHLeaveDetailBeforeApplyLeave() throws Exception {
        logMessage("*** Start Test E10211...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Log on as Admin and activate Jennifer H.");
        logMessage("Item E18.1", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E18.1: Activate Jennifer H.");

        signoutESS(driver);
        driver.close();

        logMessage("Step 433_1: Log on As Jeniffer HOWE");
        logMessage("Item E18.2", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E18.2: Step 433_1: Log on As Jeniffer HOWE");

        logMessage("Step 433_2: Validate Jennifer H's Dashboard.");
        logMessage("Item E18.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item E18.3: Step 433_2: Validate Jennifer H's Dashboard.");

        logMessage("Step 433 and Step 437: Validate Leave Balance as date on 31/05/2017.");
        logMessage("Item E18.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(111, 111, testSerialNo, emailDomainName, driver), true, "Failed in Item E18.4: Step 433 and Step 437: Validate Leave Page.");

        logMessage("Step 434: Validate All Team Members.");
        logMessage("Item E18.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item E18.5: Step 434: Validate All Team Members.");

        logMessage("Step 435: Validate All Team Members - Leave Tab.");
        logMessage("Item E18.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(372, 372, testSerialNo, emailDomainName, driver), true, "Failed in Item E18.6: Step 435: Validate All Team Members - Leave Tab.");

        logMessage("Step 436: Validate Team - Leave Tab - Calendar June 2017.");
        logMessage("Item E18.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembersLeaveTab_Main(381, 381, testSerialNo, emailDomainName, driver), true, "Failed in Item E18.7: Step 436: Validate Team - Leave Tav - Calendar June 2017.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test E10211.");

        myAssert.assertAll();
    }

    @Test(priority = 10221)
    public static void testE10221_ValidateJenniferHAllLeaveBalanceForcase() throws Exception {
        logMessage("*** Start Test E10221...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Step 437: Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 437_1: Validate Jennifer H Annual Leave balance today via Dashboard.");
        logMessage("Item E19.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(20011, 20011, serverName, payrollDBName,  driver), true, "Failed in Item E19.1: Step 437_1: Validate Jennifer H's Leave balance today via Dashboard.");

        logMessage("Validate Jennifer H Personal Leave balance today via Dashboard.");
        logMessage("Item E19.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(20012, 20012, serverName, payrollDBName,  driver), true, "Failed in Item E19.2: Validate Jennifer H Personal Leave balance today via Dashboard.");

        logMessage("Validate Jennifer H Long Service Leave balance today via Dashboard.");
        logMessage("Item E19.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaDashboard_Main(20013, 20013, serverName, payrollDBName,  driver), true, "Failed in Item E19.3: Validate Jennifer H Long Service Leave balance today via Dashboard.");

        ////////////////////////////// Today /////////////////
        logMessage("Validate Jennifer H Annual Leave balance today via Leave Page.");
        logMessage("Item E19.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20011, 20011, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.4: Step 437_1: Validate Jennifer H's Leave balance today via Leave Page.");

        logMessage("Validate Jennifer H Personal Leave balance today via Leave Page.");
        logMessage("Item E19.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20012, 20012, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.5: Validate Jennifer H Personal Leave balance today via Leave Page.");

        logMessage("Validate Jennifer H Long Service Leave balance today via Leave Page.");
        logMessage("Item E19.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20013, 20013, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.6: Validate Jennifer H Long Service Leave balance today via Leave Page.");

        ////////////////////////// Past ////////////////////

        logMessage("Validate Jennifer H Annual Leave balance on 5th June 2017 via Leave Page.");
        logMessage("Item E19.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20021, 20021, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.7: Step 437_1: Validate Jennifer H's Leave balance on 5th June 2017 via Dashboard.");

        logMessage("Validate Jennifer H Personal Leave balance on 5th June 2017 via Leave Page.");
        logMessage("Item E19.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20022, 20022, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.8: Validate Jennifer H Personal Leave balance on 5th June 2017 via Dashboard.");

        logMessage("Validate Jennifer H Long Service Leave balance on 5th June via Leave Page.");
        logMessage("Item E19.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20023, 20023, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.9: Validate Jennifer H Long Service Leave balance on 5th June 2017 via Dashboard.");

        /////////////////////// Future ///////////////////

        logMessage("Validate Jennifer H Annual Leave balance in 2 month and the 2nd Friday via Leave Page.");
        logMessage("Item E19.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20031, 20031, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.10: Step 437_1: Validate Jennifer H's Leave balance in 2 month and the 2nd Friday via Dashboard.");

        logMessage("Validate Jennifer H Personal Leave balance in 2 month and the 2nd Friday  via Leave Page.");
        logMessage("Item E19.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20032, 20032, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.8: Validate Jennifer H Personal Leave balance in 2 month and the 2nd Friday via Dashboard.");

        logMessage("Validate Jennifer H Long Service Leave balance in 2 month and the 2nd Friday via Leave Page.");
        logMessage("Item E19.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20033, 20033, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E19.9: Validate Jennifer H Long Service Leave balance in 2 month and the 2nd Friday via Dashboard.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test E10221.");

        myAssert.assertAll();
    }

    @Test(priority = 10231)
    public static void testE10231_ApplyLeaveAndValidateLeaveViaJenniferH() throws Exception {
        logMessage("*** Start Test E10231...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Step 438 - Step 442: Apply Annual Leave for Jennifer H");
        logMessage("Item E20.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(121, 121, testSerialNo, driver), true, "Failed in Item E20.1: Step 438 - Step 442: Apply Annual Leave for Jennifer H");

        //////////////////////////////////////
        logMessage("Step 444_1: Validate Jennifer H today's Annual Leave balance via Leave Page after apply leave.");
        logMessage("Item E20.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10041, 10041, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.2: Step 444_1: Validate Jennifer H's Annual Leave balance today via Leave Page.");

        logMessage("Step 444_2: Validate Jennifer H today's Personal balance via Leave Page after apply leave.");
        logMessage("Item E20.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10042, 10042, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.3: Step 444_2: Validate Jennifer H's Personal Leave balance today via Leave Page.");

        logMessage("Step 444_3: Validate Jennifer H today's Long Service Leave balance via Leave Page after apply leave.");
        logMessage("Item E20.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10043, 10043, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.4: Step 444_3: Validate Jennifer H's Long Service Leave balance today via Leave Page.");
        //////

        ////////////////////////////
        logMessage("Step 445_1: forecast Jennifer H Annual Leave balance via Leave Page after apply leave.");
        logMessage("Item E20.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10051, 10051, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.5: Step 445_1: forecast Jennifer H Annual Leave balance via Leave Page after apply leave.");

        logMessage("Step 445_2: forecast Jennifer H Personal balance via Leave Page after apply leave.");
        logMessage("Item E20.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10052, 10052, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.6: Step 445_2: forecast Jennifer H Personal balance via Leave Page after apply leave.");

        logMessage("Step 445_3: forecast Jennifer H Long Service Leave balance via Leave Page after apply leave.");
        logMessage("Item E20.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(10053, 10053, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E20.7: Step Step 445_3: forecast Jennifer H Long Service Leave balance via Leave Page after apply leave.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test E10231.");

        myAssert.assertAll();
    }

    @Test (priority = 10241)
    public static void testE10241_ValidateEmail() throws Exception {
        logMessage("*** Start Test E10241.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Validate Jennifer H email");
        logMessage("Item E21.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E21.1: Validate Jennifer H email.");

        logMessage("Validate Sue A email");
        logMessage("Item E21.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20012, 20012, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E21.2: Validate Sue A email.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("*** End of test E10241");
        myAssert.assertAll();
    }

    @Test (priority = 10251)
    public static void testE10251_ValidateJenniferHLeaveViajanniferHAfterApplyLeave() throws Exception {
        logMessage("*** Start Test E10251.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H Dashoard.");
        logMessage("Item E22.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20061,20061, testSerialNo, emailDomainName, driver), true, "Failed in Item E22.1: Validate Jennifer H Dashboard via Jennifer H.");

        logMessage("Validate Jennifer H Leave Calendar via Jennifer H.");
        logMessage("Item E22.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item E22.2: Validate Jennifer H Leave Calendar via Jennifer H.");

        signoutESS(driver);
        driver.close();

        myAssert.assertAll();
        logMessage("*** End of Test E10251.");
    }

    @Test (priority = 10261)
    public static void testE10261_ValidateJenniferHLeaveViaSueAManagerAfterApplyLeave() throws Exception {
        logMessage("*** Start Test E10261.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Sue A Dashoard via Sue A after Jennifer H Apply leave.");
        logMessage("Item E23.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20071,20071, testSerialNo, emailDomainName, driver), true, "Failed in Item E23.1: Validate Sue A Dashoard via Sue A after Jennifer H Apply leave.");

        logMessage("Validate Jennifer H Leave Calendar via Manager Sue A.");
        logMessage("Item E23.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20022, 20022, testSerialNo, emailDomainName, driver), true, "Failed in Item E23.2: Validate Jennifer H Leave Calendar via Manager Sue A.");


        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10261.");
        myAssert.assertAll();
    }

    @Test (priority = 10271)
    public static void testE10271_ValidateLeaveInCalendarViaChristineRMember() throws Exception {
        logMessage("*** Start Test E10271.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Team Member Chrinstine R");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20001, 20001, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Christine R Dashoard via Christine R after Jennifer H Apply leave.");
        logMessage("Item E24.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20081,20081, testSerialNo, emailDomainName, driver), true, "Failed in Item E24.1: Validate Christine R Dashoard via Christine R after Jennifer H Apply leave.");

        logMessage("Validate Jennifer H Leave Calendar via Chrinstine R Team Member.");
        logMessage("Item E24.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20023, 20023, testSerialNo, emailDomainName, driver), true, "Failed in Item E24.2: Validate Jennifer H Leave Calendar via Team Member Christine R.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10271.");
    }

    //Must run without headless
    @Test (priority = 10272)
    public static void testE10272_ValdiateLeaveReportAfterApplyAL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10272.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X24.2.1: Download and Validate Leave History Report in Team C after Apply AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20061, 20061, testSerialNo, driver), true, "Failed Item E.X24.2.1: Validate Leave History Report in Team C after Apply AL.");

        logMessage("Item E.X24.2.2: Download and Validate Leave Balances Report in Team C after Apply AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20062, 20062, driver), true, "Failed Item E.X24.2.2: Download and Validate Leave Balance Report in Team C after Apply AL.");

        logMessage("Item E.X24.2.3: Download and Validate Applied Leave By Date Report in Team C after Apply AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20063, 20063, testSerialNo, driver), true, "Failed Item E.X24.2.3: Download and Validate Applied Leave By Date Report in Team C after Apply AL.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10272.");
        myAssert.assertAll();
    }

    @Test (priority = 10281)
    public static void testE10281_EditJenniferHPendingLeaveAndValidateLeaveViaJanniferH() throws Exception {
        logMessage("*** Start Test E10281.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Edit Jennifer H Pending AL via Jennifer H.");
        logMessage("Item E25.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPendingLeave(20021,20021, testSerialNo, driver), true, "Failed in Item E25.1:Edit Jennifer H Pending AL via Jennifer H.");

        logMessage("Validate Jennifer H Dashoard after edit pending AL Leave.");
        logMessage("Item E25.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20091,20091, testSerialNo, emailDomainName, driver), true, "Failed in Item E25.2: Validate Jennifer H Dashboard via Jennifer H after editing pending AL leave.");

        logMessage("Validate Jennifer H Leave Calendar via Jennifer H.");
        logMessage("Item E25.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item E25.3: Validate Jennifer H Leave Calendar via Jennifer H.");

        logMessage("Valiate Jennifer H AL balance via Jennifer H after editing pending leave.");
        logMessage("Item E25.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20041, 20041, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E25.4: Valiate Jennifer H AL balance via Jennifer H after editing pending leave");
        signoutESS(driver);
        driver.close();

        myAssert.assertAll();
        logMessage("*** End of Test E10281.");
    }


    @Test (priority = 10291)
    public static void testE10291_ValidateJenniferHLeaveViaSueA() throws Exception {
        logMessage("*** Start Test E10291.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Valdiate jennife H Leave Page via Sue A after Editing Pending Leave.");
        logMessage("Item E26.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item E26.1: Validate Jennifer H Leave Page via Sue A after Edit Pending Leave.");

        logMessage("Valiate Jennifer H AL balance via Sue after editing pending leave.");
        logMessage("Item E26.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20051, 20051, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E26.2: Valiate Jennifer H AL balance via Jennifer H after editing pending leave");
        signoutESS(driver);
        driver.close();


        logMessage("*** End of Test E10291.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10292)
    public static void testE10292_ValdiateLeaveReportAfterEditAL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10292.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X26.2.1: Download and Validate Leave History Report in Team C after edit pending AL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20071, 20071, testSerialNo, driver), true, "Failed Item E.X26.2.1: Validate Leave History Report in Team C after Edit Pending AL.");

        logMessage("Item E.X26.2.2: Download and Validate Leave Balances Report in Team C after edit pending AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20072, 20072, driver), true, "Failed Item E.X26.2.2: Download and Validate Leave Balance Report in Team C after Edit Pending AL.");

        logMessage("Item E.X26.2.3: Download and Validate Applied Leave By Date Report in Team C after edit pending AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20073, 20073, testSerialNo, driver), true, "Failed Item E.X26.2.3: Download and Validate Applied Leave By Date Report in Team C after Edit Pending AL");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10292.");
        myAssert.assertAll();
    }

    @Test (priority = 10301)
    public static void testE10301_CancelPendingALByJenniferHAndValidateLeaveViaJanniferH() throws Exception {
        logMessage("*** Start Test E10301.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Cancel AL by via Jennifer H.");
        logMessage("Item E27.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.cancelMultiPendingLeave(20041,20041, testSerialNo, driver), true, "Failed in Item E27.1:Cancel Pending Leave by via Jennifer H.");

        logMessage("Validate Jennifer H Leave Calendar after cancel leave via Jennifer H.");
        logMessage("Item E27.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item E27.2:Validate Jennifer H Leave Calendar after cancel leave via Jennifer H.");

        logMessage("Valdiate jennife H Leave Page via Jennifer H after cancel leave.");
        logMessage("Item E27.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item E27.3: Valdiate jennife H Leave Page via Jennifer H after cancel leave");

        logMessage("Valiate Jennifer H AL balance after cancel Leave via Jennifer H.");
        logMessage("Item E27.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20061, 20061, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E27.4: Valiate Jennifer H AL balance after cancel Leave via Jennifer H.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10301.");
        myAssert.assertAll();

    }

    @Test (priority = 10311)
    public static void testE10311_ValidateEmailAfterCancelEmail() throws Exception {
        logMessage("*** Start Test E10311.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item E28.1: Validate Jennifer H email after cancel leave", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20021, 20021, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E28.1: Validate Jennifer H Email after cancel leave.");

        logMessage("Item E28.2: Validate Sue A email after cancel leave.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20022, 20022, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E28.2: Validate Sue A Email after cancel leave.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("End of Test E10311.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10312)
    public static void testE10312_ValdiateLeaveReportAfterCancelAL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10312.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X28.2.1: Download and Validate Leave History Report in Team C after cancel pending AL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20081, 20081, testSerialNo, driver), true, "Failed in Item E.X28.2.1: Validate Leave History Report in Team C after Cancel Pending AL.");

        logMessage("Item E.X28.2.2: Download and Validate Leave Balances Report in Team C after cancel pending AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20082, 20082, driver), true, "Failed in Item E.X28.2.2: Download and Validate Leave Balance Report in Team C after Cancel Pending AL.");

        logMessage("Item E.X28.2.3: Download and Validate Applied Leave By Date Report in Team C after cancel pending AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20083, 20083, testSerialNo, driver), true, "Failed Item E.X28.2.3: Download and Validate Applied Leave By Date Report in Team C after Cancel Pending AL");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10312.");
        myAssert.assertAll();
    }

    @Test (priority = 10321)
    public static void testE10321_ApplyLeaveViaJennifer2ndTime() throws Exception {
        logMessage("*** Start Test E10321.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Apply Annual Leave via Jennifer H the second time.");
        logMessage("Item E29.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20061, 20061, testSerialNo, driver), true, "Failed in Item E29.1:Apply Annual Leave via Jennifer H the second time.");

        logMessage("Validate Jennifer H email after apply leave the 2nd time.");
        logMessage("Item E29.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20031, 20031, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E29.1: Validate Jennifer H email after apply leave the 2nd time.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Apply one day AL within the AL day above and Validate Apply Leave Form.");
        logMessage("Item E29.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApplyForLeaveForm_Main(20071, 20071, emailDomainName, testSerialNo, driver), true, "Failed in Item E29.3: Apply one day AL within the AL day above and Validate Apply Leave Form.");

        logMessage("Validate AL balance via leave page after Apply AL via Jennifer H the 2nd time.");
        logMessage("Item E29.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20071, 20071, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E29.4: Validate AL balance via leave page after Apply AL via Jennifer H the 2nd time.");

        logMessage("Valdiate Leave Page via Jennifer after apply leave the 2nd time");
        logMessage("Item E29.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20081, 20081, emailDomainName, testSerialNo, driver), true, "Failed in Item E29.5: Valdiate Leave Page via Jennifer after apply leave the 2nd time.");

        logMessage("Valdiate Leave Message in Jennifer Dashboard");
        logMessage("Item E29.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20101,20101, testSerialNo, emailDomainName, driver), true, "Failed in Item E29.6: Validate Jennifer H dashboard via Jennifer H after apply AL the 2nd time");

        signoutESS(driver);
        driver.close();

        logMessage("Log on As Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Leave message in Admin Other Approval page.");
        logMessage("Item E29.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item E29.7: Validate Leave message in Admin Other Approval page.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10321.");
        myAssert.assertAll();

    }

    @Test (priority = 10331)
    public static void testE10331_DeclineALViaManagerSueA() throws Exception {
        logMessage("*** Start Test E10331.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Valdiate Leave Message in Sue A Dashboard");
        logMessage("Item E30.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20111,20111, testSerialNo, emailDomainName, driver), true, "Failed in Item E30.1: Valdiate Leave Message in Sue A Dashboard");

        logMessage("Validate Jennifer H Leave Page via Sue A");
        logMessage("Item E30.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item E30.2: Validate Jennifer H Leave Page via Sue A");

        logMessage("Declidne Jennifer H AL via Sue A.");
        logMessage("Item E30.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20001, 20001, testSerialNo, emailDomainName, driver), true, "Falied in Item E30.3: Declidne Jennifer H AL via Sue A.");

        logMessage("Validate Jennifer H email after Sue A decline Jennifer H AL.");
        logMessage("Item E30.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20041, 20041, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E30.4: Validate Jennifer H email after Sue A decline Jennifer H AL leave.");

        logMessage("Validate Sue A email after Sue A decline Jennifer H AL.");
        logMessage("Item E30.5", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20042, 20042, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item E30.5: Validate Sue A email after Sue A decline Jennifer H AL.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Valdiate Leave Message in Sue A Dashboard");
        logMessage("Item E30.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20121,20121, testSerialNo, emailDomainName, driver), true, "Failed in Item E30.6: Valdiate Leave Message in Sue A Dashboard");

        logMessage("Validate Jennifer H Leave Page after Sue A decline Jennifer H AL via Sue A");
        logMessage("Item E30.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item E30.7: Validate Jennifer H Leave Page after Sue A decline Jennifer H AL via Sue A");

        logMessage("Validate Sue A approval page after Sue A decline Jennifer H AL via Sue A.");
        logMessage("Item E30.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovalsPage(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item E30.8: Validate Sue A approval page after Sue A decline Jennifer H AL via Sue A.");

        logMessage("Validate Jennifer H AL balance in leave page after Sue A Decline Jennifer H AL via Sue A.");
        logMessage("Item E30.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20081, 20081, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item 30.9: Validate Jennifer H AL balance in leave page after Sue A Decline Jennifer H AL via Sue A.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10331.");
        myAssert.assertAll();

    }

    //Must run without headless
    @Test (priority = 10332)
    public static void testE10332_ValdiateLeaveReportAfterDeclineAL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10332.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X30.9.1: Download and Validate Leave History Report in Team C after Decline AL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20101, 20101, testSerialNo, driver), true, "Failed in Item E.X30.9.1: Validate Leave History Report in Team C after Decline AL.");

        logMessage("Item E.X30.9.2: Download and Validate Leave Balances Report in Team C after Decline AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20102, 20102, driver), true, "Failed in Item E.X30.9.2: Download and Validate Leave Balance Report in Team C after Decline AL.");

        logMessage("Item E.X30.9.3: Download and Validate Applied Leave By Date Report in Team C after Decline AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20103, 20103, testSerialNo, driver), true, "Failed Item E.X30.9.3: Download and Validate Applied Leave By Date Report in Team C after Decline AL.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10332.");
        myAssert.assertAll();
    }

    @Test (priority = 10341)
    public static void testE10341_ApproveAppliedLeaveAndValidateLeave() throws Exception {
        logMessage("*** Start Test E10341.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Log on As Jennifer HOWE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Apply the AL via Jennifer H the 3rd time.");
        logMessage("Item E31.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20111, 20111, testSerialNo, driver), true, "Failed in Item E31.1:Apply Annual Leave via Jennifer H the second time.");

        logMessage("Validate Jennifer H email after apply leave the 3rd time.");
        logMessage("Item E31.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20051, 20051, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E31.2: Validate Jennifer H email after apply leave the 3rd time.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        signoutESS(driver);
        driver.close();

        logMessage("Log on As Sue A");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Approve Jennifer H AL via Sue A.");
        logMessage("Item E31.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20011, 20011, testSerialNo, emailDomainName, driver), true, "Falied in Item E31.3: Approve Jennifer H AL via Sue A.");

        logMessage("Validate Jennifer H email after apply leave the 3rd time.");
        logMessage("Item E31.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20061, 20061,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E31.4: Validate Jennifer H email after apply leave the 3rd time.");

        logMessage("Delet All Emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Valdiate Jennifer H Leave Page after Sue A  approve Jennnifer H AL via Sue A");
        logMessage("Item E31.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item E31.5: Valdiate Jennifer H Leave Page after Sue A  approve Jennnifer H AL via Sue A");

        logMessage("Validate Jennifer H AL balance after Sue A approve Jennifer H AL via Sue A");
        logMessage("Item E31.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20091, 20091, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item E31.6: Validate Jennifer H AL balance after Sue A approve Jennifer H AL via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("End of Test E10341.");
        myAssert.assertAll();
    }

    @Test (priority = 10342)
    public static void testE10342_ValidateJenniferHLeaveSateusAfterApprovedByManagerSueA() throws Exception {
        logMessage("*** Start Test E10341.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer Dashboard Leave message via Jennifer H");
        logMessage("Item E31.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item E31.7: Validate Jennifer Dashboard Leave message via Jennifer H");

        logMessage("Validate Jennifer Leave Page via Jennifer H");
        logMessage("Item E31.8",testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item E31.8: Validate Jennifer Leave Page via Jennifer H");

        logMessage("Validate Jennifer H AL balance vis Jennifer H");
        logMessage("Item E31.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20101, 20101, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E31.9: Validate Jennifer H AL balance vis Jennifer H");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10342.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10351)
    public static void testE10351_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10351.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E32: Download and validate Leave Application audit report after manager approve member's leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(20001, 20001, emailDomainName, testSerialNo, driver), true, "Failed in Item E32: Download and validate Leave Application audit report after manager approve member's leave.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test E10351.");
        myAssert.assertAll();
    }

    @Test (priority = 10361)
    public static void testE10361_ValidateJenniferHALInCalendar() throws Exception {
        logMessage("*** Start Test E10361");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H AL in Directory Calendar via Admin");
        logMessage("Item E33.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.1: Validate Jennifer H AL in Directory Calendar via Admin");

        logMessage("Validate Jennifer H AL in Team Calendar via Admin");
        logMessage("Item E33.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.2: Validate Jennifer H AL in Team Calendar via Admin");

        signoutESS(driver);
        driver.close();

        ////////////////////////
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Log on As Sue A");
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H AL in Directory Calendar via Sue A");
        logMessage("Item E33.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.3: Validate Jennifer H AL in Directory Calendar via Sue A");

        logMessage("Validate Jennifer H AL in Team Calendar via Sue A");
        logMessage("Item E33.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.4: Validate Jennifer H AL in Team Calendar via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("Log on As Jennifer H");
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H AL in Directory Calendar via Jennifer H");
        logMessage("Item E33.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.5: Validate Jennifer H AL in Directory Calendar via Jennifer H");

        logMessage("Validate Jennifer H AL in Team Calendar via Jennifer H");
        logMessage("Item E33.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.6: Validate Jennifer H AL in Team Calendar via Jennifer H.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10361");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10362)
    public static void testE10362_ValdiateLeaveReportAfterApproveAL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10362.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X33.6.1: Download and Validate Leave History Report in Team C after Approve AL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20121, 20121, testSerialNo, driver), true, "Failed in Item E.X33.6.1: Validate Leave History Report in Team C after Approve AL.");

        logMessage("Item E.X33.6.2: Download and Validate Leave Balances Report in Team C after Approve AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20122, 20122, driver), true, "Failed in Item E.X33.6.2: Download and Validate Leave Balance Report in Team C after Approve AL.");

        logMessage("Item E.X33.6.3: Download and Validate Applied Leave By Date Report in Team C after Approve AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20123, 20123, testSerialNo, driver), true, "Failed Item E.X33.6.3: Download and Validate Applied Leave By Date Report in Team C after Approve AL.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10362.");
        myAssert.assertAll();
    }

    @Test (priority = 10371)
    public static void testE10371_ValidateJenniferHALViaTeamMemberPhantomF() throws Exception {
        logMessage("*** Start Test E10371");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Log on As Admin.");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Activate Phantom F.");
        logMessage("Item E33.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(105, 105, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item E33.5: Validate Jennifer H AL in Directory Calendar via Jennifer H");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Phantom F.");
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10161, 10161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H AL on Calendar via team member Phanton F");
        logMessage("Item E33.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.8: Validate Jennifer H AL on Calendar via team member Phanton F");

        logMessage("Validate Jennifer H AL on Team - Leave Calendar via team member Phanton F");
        logMessage("Item E33.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item E33.9: Validate Jennifer H AL on Team - Leave Calendar via team member Phanton F");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10371");
        myAssert.assertAll();
    }

    @Test (priority = 10381)
    public static void testE10381_ApplyAndValidateJenniferHCarersLeave() throws Exception {
        logMessage("*** Start Test E10381");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Valdiate Leave Forecast when Apply Carers Leave the next month 2nd Month via Jennifer H.");
        logMessage("Item E34.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20111, 20111, testSerialNo, serverName, payrollDBName,  driver), true, "Falied in Item E34.1: Valdiate Leave Forecast when Apply Carers Leave the next month 2nd Month via Jennifer H");

        logMessage("Apply Carers Leave on the next month 1st Monday via Jennifer H");
        logMessage("Item E34.2", testSerialNo);
        myAssert.assertEquals(applyMultiLeave(20141, 20141, testSerialNo, driver), true, "Failed in Apply Carers Leave on the next month 1st Monday via Jennifer H");

        logMessage("Validate Jennifer H Email content..");
        logMessage("Item E34.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20071, 20071, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E34.3: Validate Jennifer H Email content.");

        logMessage("Validate Sue A Email content..");
        logMessage("Item E34.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20072, 20072, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E34.4: Validate Sue A Email content.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Valdiate Jennifer H Leave Page via Jennifer H");
        logMessage("Item E34.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item E34.5: Valdiate Jennifer H Leave Page via Jennifer H");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10381.");
        myAssert.assertAll();
    }

    @Test (priority = 10391)
    public static void testE10391_ApproveAndValidateJenniferHCarersLeaveViaSueA() throws Exception {
        logMessage("*** Start test E10391.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Approve Jennifer H Carers Leave via Sue A.");
        logMessage("Item E34.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20021, 20021, testSerialNo, emailDomainName, driver), true, "Falied in Item E34.6: Approve Jennifer H Carers Leave via Sue A.");

        logMessage("Item E34.7: Validate Jennifer H Email content via Sue A", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20081, 20081, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E34.7: Validate Jennifer H Email content.");

        logMessage("Validate Jennifer H Carers Leave in Leave Page via Sue A.");
        logMessage("Item E34.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item E34.8: Validate Jennifer H Carers Leave in Leave Page via Sue A.");

        logMessage("Validate Jennifer H PL balance in Leave Page via Sue A");
        logMessage("Item E34.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20121, 20121, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E34.9: Validate Jennifer H PL balance in Leave Page via Sue A");

        logMessage("Validate Jennifer H Carers Leave in Directory Calendar via Jennifer H");
        logMessage("Item E34.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item E34.10: Validate Jennifer H Carers Leave in Directory Calendar via Jennifer H");

        logMessage("Validate Jennifer H AL in Team Calendar via Jennifer H");
        logMessage("Item E34.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item E34.11: Validate Jennifer H AL in Team Calendar via Jennifer H");

        signoutESS(driver);
        driver.close();

        logMessage("Log on As Admin");
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H Carers Leave in Directory Calendar via Admin");
        logMessage("Item E34.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item E34.10: Validate Jennifer H Carers Leave in Directory Calendar via Jennifer H");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10391");
        myAssert.assertAll();
    }

    @Test (priority = 10401)
    public static void testE10401_ApplyAndValidateJenniferHSickLeave() throws Exception {
        logMessage("*** Start Test E10401");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Apply Sick Leave via Jennifer A");
        logMessage("Item E35.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20171, 20171, testSerialNo, driver), true, "Failed in Item E35.1: Apply Sick Leave via Jennifer A");

        logMessage("Validate Jennifer H Email content..");
        logMessage("Item E35.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20091, 20091, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E35.2: Validate Jennifer H Email content.");

        logMessage("Validate Sue A Email content..");
        logMessage("Item E35.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20092, 20092, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E35.3: Validate Sue A Email content.");

        logMessage("Valdiate Jennifer H Leave Page via Jennifer H after apply Sick Leave.");
        logMessage("Item E35.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item E35.4: Valdiate Jennifer H Leave Page via Jennifer H after apply Sick Leave.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010401");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10402)
    public static void testE10402_ValdiateLeaveReportAfterAppleSickLeave() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10402.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X35.4.1: Download and Validate Leave History Report in Team C after Apply Sick Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20131, 20131, testSerialNo, driver), true, "Failed in Item E.X35.4.1: Download and Validate Leave History Report in Team C after Apply Sick Leave");

        logMessage("Item E.X35.4.2: Download and Validate Leave Balances Report in Team C after Apply Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20132, 20132, driver), true, "Failed in Item E.X35.4.2: Download and Validate Leave Balances Report in Team C after Apply Sick Leave.");

        logMessage("Item E.X35.4.3: Download and Validate Applied Leave By Date Report in Team C after Apply Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20133, 20133, testSerialNo, driver), true, "Failed in Item E.X35.4.3: Download and Validate Applied Leave By Date Report in Team C after Apply Sick Leave.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10402.");
        myAssert.assertAll();
    }


    @Test (priority = 10411)
    public static void testE10411_ApproveAndValidateJenniferHCarersLeaveViaSueA() throws Exception {
        logMessage("*** Start test E10411.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Approve Jennifer H Sick Leave via Sue A.");
        logMessage("Item E35.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20031, 20031, testSerialNo, emailDomainName, driver), true, "Falied in Item E35.5: Approve Jennifer H Sick Leave via Sue A.");

        logMessage("Validate Jennifer H Carers Leave in Leave Page after approve Sick Leave via Sue A.");
        logMessage("Item E35.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item E35.6: Validate Jennifer H Carers Leave in Leave Page after approve Sick Leave via Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10411");
        myAssert.assertAll();
    }

    @Test (priority = 10421)
    public static void testE10421_ValidateLeaveInDirectoryCalendarViaJannifer() throws Exception {
        logMessage("*** Start test E10421.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Jennifer H Email content.");
        logMessage("Item E35.7", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20101, 20101, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item 35.8: Validate Jennifer H Email content.");

        logMessage("Validate Jennifer H Personal Leave balance.");
        logMessage("Item E35.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20131, 20131, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E35.8: Validate Jennifer H Personal Leave balance.");


        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10421");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10422)
    public static void testE10422_ValdiateLeaveReportAfterApproveSickLeave() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10422.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X35.8.1: Download and Validate Leave History Report in Team C after Approve Sick Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20141, 20141, testSerialNo, driver), true, "Failed in Item E.X35.8.1: Download and Validate Leave History Report in Team C after Approve Sick Leave");

        logMessage("Item E.X35.8.2: Download and Validate Leave Balances Report in Team C after Approve Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20142, 20142, driver), true, "Failed in Item E.X35.8.2: Download and Validate Leave Balances Report in Team C after Approve Sick Leave.");

        logMessage("Item E.X35.8.3: Download and Validate Applied Leave By Date Report in Team C after Approve Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20143, 20143, testSerialNo, driver), true, "Failed in Item E.X35.8.3: Download and Validate Applied Leave By Date Report in Team C after Approve Sick Leave.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10422.");
        myAssert.assertAll();
    }



    @Test (priority = 10431)
    public static void testE10431_CancelApprovedSickLeaveViaJenniferAndValidateLeave() throws Exception {
        logMessage("*** Start test E10431.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Cancel Approved Sick Leave via Jennifer H");
        logMessage("Item E36.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.cancelMultiLeave(20201, 20201, testSerialNo, driver), true, "Failed in Item 36.1: Cancel Approved Sick Leave via Jennifer H");

        logMessage("Validate Jennifer H Personal Leave balance after cancel .");
        logMessage("Item E36.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20141, 20141, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E36.2: Validate Jennifer H Personal Leave balance.");

        logMessage("Validate Jennifer H Carers Leave in Leave Page after cancel approved Sick Leave via Jennifer H.");
        logMessage("Item E36.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item E36.3: Validate Jennifer H Carers Leave in Leave Page after cancel approved Sick Leave via Jennifer H.");

        logMessage("Item E36.4: Validate Jennifer H Email content after cancel leave", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20111, 20111, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E36.4: Validate Jennifer H Email content.");

        logMessage("Item E36.5: Validate Sue A Email content after cancel leave.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20112, 20112,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E36.5: Validate Sue A Email content.");


        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10431");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10432)
    public static void testE10432_ValdiateLeaveReportAfterCancelApproveSickLeave() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10432.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X36.5.1: Download and Validate Leave History Report in Team C after Cancel Approve Sick Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20151, 20151, testSerialNo, driver), true, "Failed in Item E.X36.5.1: Download and Validate Leave History Report in Team C after Cancel Approve Sick Leave");

        logMessage("Item E.X36.5.2: Download and Validate Leave Balances Report in Team C after Cancel Approve Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20152, 20152, driver), true, "Failed in Item E.X36.5.2: Download and Validate Leave Balances Report in Team C after Cancel Approve Sick Leave.");

        logMessage("Item E.X36.5.3: Download and Validate Applied Leave By Date Report in Team C after Cancel Approve Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20153, 20153, testSerialNo, driver), true, "Failed in Item E.X36.5.3: Download and Validate Applied Leave By Date Report in Team C after Cancel Approve Sick Leave.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10432.");
        myAssert.assertAll();
    }


    @Test (priority = 10441)
    public static void testE10441_ApproveSickLeaveCancellationViaSueAAndValidateLeave() throws Exception {
        logMessage("*** Start test E10441.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Approve Jennifer H Sick Leave cancellation vis Sue A");
        logMessage("Item E36.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20041,  20041, testSerialNo, emailDomainName, driver), true, "Failed in Item 36.6: Approve Jennifer H Sick Leave cancellation vis Sue A");

        logMessage("Validate Jennifer H Email content..");
        logMessage("Item E36.7", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E36.74: Validate Jennifer H Email content.");

        logMessage("Item E36.8: Validate Jennifer H Personal Leave balance after approve Jennifer H Sick Leave Cancellation via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20151, 20151, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item E36.8: Validate Jennifer H Personal Leave balance after approve Jennifer H Sick Leave Cancellation via Sue A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10441");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10442)
    public static void testE10442_ValdiateLeaveReportAfterAprroveCancelledSickLeave() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10442.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X36.8.1: Download and Validate Leave History Report in Team C after approve Cancelled Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20161, 20161, testSerialNo, driver), true, "Failed in Item E.X36.8.1: Download and Validate Leave History Report in Team C after approve Cancelled Sick Leave.");

        logMessage("Item E.X36.8.2: Download and Validate Leave Balances Report in Team C after approve Cancelled Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20162, 20162, driver), true, "Failed in Item E.X36.8.2: Download and Validate Leave Balances Report in Team C after approve Cancelled Sick Leave.");

        logMessage("Item E.X36.8.3: Download and Validate Applied Leave By Date Report in Team C after approve Cancelled Sick Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20163, 20163, testSerialNo, driver), true, "Failed in Item E.X36.8.3: Download and Validate Applied Leave By Date Report in Team C after approve Cancelled Sick Leave.");
        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10442.");
        myAssert.assertAll();
    }

    @Test (priority = 10451)
    public static void testE10451_ApplyLSLViaJenniferH() throws Exception {
        logMessage("*** Start Test E10451");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Apply LSL via Jennifer A");
        logMessage("Item E37.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20221, 20221, testSerialNo, driver), true, "Failed in Item E35.1: Apply Sick Leave via Jennifer A");

        logMessage("Validate Jennifer H Email content..");
        logMessage("Item E37.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20131, 20131, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E37.2: Validate Jennifer H Email content.");

        logMessage("Validate Sue A Email content..");
        logMessage("Item E37.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20132, 20132, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E37.3: Validate Sue A Email content.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010451");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10452)
    public static void testE10452_ValdiateLeaveReportAfterApplyLSL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10452.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X37.3.1: Download and Validate Leave History Report in Team C after Apply LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20171, 20171, testSerialNo, driver), true, "Failed in Item E.X37.3.1: Download and Validate Leave History Report in Team C after Apply LSL.");

        logMessage("Item E.X37.3.2: Download and Validate Leave Balances Report in Team C after Apply LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20172, 20172, driver), true, "Failed in Item E.X37.3.2: Download and Validate Leave Balances Report in Team C after Apply LSL.");

        logMessage("Item E.X37.3.3: Download and Validate Applied Leave By Date Report in Team C after Apply LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20173, 20173, testSerialNo, driver), true, "Failed in Item E.X37.3.3: Download and Validate Applied Leave By Date Report in Team C after Apply LSL.");

        logMessage("Item E.X37.3.4: Download and Validate Applied Leave By Date Report in Team C Include Approved Leave after Apply LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20174, 20174, testSerialNo, driver), true, "Failed in Item E.X37.3.4: Download and Validate Applied Leave By Date Report in Team C Include Approved Leave after Apply LSL.");

        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10452.");
        myAssert.assertAll();
    }

    @Test (priority = 10461)
    public static void testE10461_ApproveLSLViaSueAAndValidateLeave() throws Exception {
        logMessage("*** Start test E10461.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Approve Jennifer H Sick Leave cancellation via Sue A");
        logMessage("Item E37.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20051,  20051, testSerialNo, emailDomainName, driver), true, "Failed in Item 37.4: Approve Jennifer H LSL via Sue A");

        logMessage("Validate Jennifer H Email content after Sue A approve Jennifer H LSL");
        logMessage("Item E37.5", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20141, 20141,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E37.5: Validate Jennifer H Email content after Sue A approve Jennifer H LSL");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10461");
        myAssert.assertAll();
    }

    @Test (priority = 10471)
    public static void testE10471_ValidateLeavePageViaJenniferH() throws Exception {
        logMessage("*** Start Test E10471");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validaet Jennifer H Leave page via Jennifer H");
        logMessage("Item E37.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item E37.6: Validaet Jennifer H Leave page via Jennifer H");

        logMessage("Validate Jennifer H LSL balance in Leave Page.");
        logMessage("Item E37.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20161, 20161, testSerialNo, serverName, payrollDBName,  driver), true, "Failed in Item E37.7: Validate Jennifer H LSL balance in Leave Page.");

        logMessage("Valdite Jennifer H LSL in directory calender via Jennifer H");
        logMessage("Item E37.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item E37.8: Valdite Jennifer H LSL in directory calender via Jennifer H");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010471");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10472)
    public static void testE10472_ValdiateLeaveReportAfterApplyLSL() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10472.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        //////////// Validate Leave Report ///////////////////
        logMessage("Item E.X37.8.1: Download and Validate Leave History Report in Team C after Approve LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20181, 20181, testSerialNo, driver), true, "Failed in Item Item E.X37.8.1: Download and Validate Leave History Report in Team C after Approve LSL.");

        logMessage("Item E.X37.8.2: Download and Validate Leave Balances Report in Team C after Approve LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20182, 20182, driver), true, "Failed in Item E.X37.8.2: Download and Validate Leave Balances Report in Team C after Approve LSL.");

        logMessage("Item E.X37.8.3: Download and Validate Applied Leave By Date Report in Team C after Approve LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20183, 20183, testSerialNo, driver), true, "Failed in Item E.X37.8.3: Download and Validate Applied Leave By Date Report in Team C after Approve LSL.");

        logMessage("Item E.X37.8.4: Download and Validate Applied Leave By Date Report in Team C Include Approved Leave after Approve LSL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20184, 20184, testSerialNo, driver), true, "Failed in Item E.X37.8.4: Download and Validate Applied Leave By Date Report in Team C Include Approved Leave after Approve LSL.");

        //////////////////////////

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10472.");
        myAssert.assertAll();
    }


    @Test (priority = 10481)
    public static void testE10481_ApplyUnpaidLeaveAndValidateLeave() throws Exception {
        logMessage("*** Start Test E10481");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E38.1: Validate Apply Unpaied Leave dialogue for Jennifer H via Shue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApplyForLeaveForm_Main(20241, 20241, emailDomainName, testSerialNo, driver), true, "Failed Item E38.1: Validate Apply Unpaied Leave dialogue for Jennifer H via Shue A");

        logMessage("Apply Unpaied Leave for Jennifer H via Sue A");
        logMessage("Item E38.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20251, 20251, testSerialNo, driver), true, "Failed in Item E38.2: Apply Unpaied Leave for Jennifer H via Sue A");

        logMessage("Validate Jennifer H Email content..");
        logMessage("Item E38.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20151, 20151, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item E38.3: Validate Jennifer H Email content.");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Jennifer H");
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Validate Jennifer H Leave page via Jennifer H.");
        logMessage("Item E38.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item E38.4: Validate Jennifer H Leave page via Jennifer H.");

        logMessage("Valdite Jennifer H Unpaied Leave in directory calender via Jennifer H");
        logMessage("Item E38.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDirectoryTable_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item E38.5: Valdite Jennifer H Unpaied Leave in directory calender via Jennifer H");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010481");
        myAssert.assertAll();
    }

    //Must Run without headless
    @Test (priority = 10491)
    public static void testE10491_ValidateLeaveReport() throws Exception {
        logMessage("*** Start Test E10491");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        ////////////// Team Directory ////////////
        logMessage("Item E39.1: Download and Validate Leave History Report in Team Directory.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20011, 20011, testSerialNo, driver), true, "Failed Item E39.1: Validate Leave History Report in Team Directory.");

        logMessage("Item E39.2: Download and Validate Leave Balances Report in Team Directory.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20012, 20012, driver), true, "Failed Item E39.2: Download and Validate Leave Balance Report in Team Directory.");

        logMessage("Item E39.3: Download and Validate Applied Leave By Date Report - Last 3 Years in Team Directory.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20013, 20013, testSerialNo, driver), true, "Failed Item E39.3: Download and Validate Applied Leave By Date Report - Last 3 years in Team Directory");

        //////////// Team B /////////////
        logMessage("Item E39.4: Download and Validate Leave History Report in Team B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(20021, 20021, testSerialNo, driver), true, "Failed Item E39.4: Validate Leave History Report in Team B.");

        logMessage("Item E39.5: Download and Validate Leave Balances Report in Team B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveBalancesReport_New(20022, 20022, driver), true, "Failed Item E39.5: Download and Validate Leave Balance Report in Team B.");

        logMessage("Item E39.6: Download and Validate Applied Leave By Date Report in Team B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20023, 20023, testSerialNo, driver), true, "Failed Item E39.6: Download and Validate Applied Leave By Date Report in Team B.");

        /////////// Within 3 months ////////////
        logMessage("Item E39.7: Download and Validate Applied Leave By Date Report - within 3 months", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20031, 20031, testSerialNo, driver), true, "Failed Item E39.7: Download and Validate Applied Leave By Date Report - within 3 Months.");

        logMessage("Item E39.8: Download and Validate Applied Leave By Date Report - within 3 months, Include Approved Leave", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20032, 20032, testSerialNo, driver), true, "Failed Item E39.8: Download and Validate Applied Leave By Date Report - within 3 Months, include Approved Leave.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010491");
        myAssert.assertAll();
    }

    @Test (priority = 10501)
    public static void testE10501_ValidateExtraLeaveByDateReport() throws Exception {
        logMessage("*** Start Test E10501");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E.X39.8.1: Download and Validate Applied Leave By Date Report - Team C - within one month After the next month.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20191, 20191, testSerialNo, driver), true, "Failed in Item E.X39.8.1: Download and Validate Applied Leave By Date Report - Team C - within one month After the next month.");

        logMessage("Item E.X39.8.2: Download and Validate Applied Leave By Date Report - Team C - within the first Week after the next 2 months.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20192, 20192, testSerialNo, driver), true, "Failed in Item E.X39.8.2: Download and Validate Applied Leave By Date Report - Team C - within the first Week after the next 2 months.");

        logMessage("Item E.X39.8.3: Download and Validate Applied Leave By Date Report - Team C - within the first Week after the next 2 months and include Approved Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20193, 20193, testSerialNo, driver), true, "Failed in Item E.X39.8.3: Download and Validate Applied Leave By Date Report - Team C - within the first Week after the next 2 months and include Approved Leave.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010501");
        myAssert.assertAll();
    }

    @Test (priority = 10511)
    public static void testE10511_ApplyJenniferHAnnualLeaveViaAdmin() throws Exception {
        logMessage("*** Start Test E10511");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E.X39.9.1: Admim apply a AL on be half of Jennifer H the 1st Thursday of last month.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20311, 20311, testSerialNo, driver), true, "Failed in Admim apply a AL on be half of Jennifer H the 1st Thursday of last month.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010511");
        myAssert.assertAll();
    }


    @Test (priority = 10521)
    public static void testE10521_ValidateExtraLeaveByDateReport() throws Exception {
        logMessage("*** Start Test E10521");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E.X39.9.2: Download and Validate Applied Leave By Date Report - Team C - within the the 1st Thursday of last month.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20201, 20201, testSerialNo, driver), true, "Failed in Item E.X39.9.2: Download and Validate Applied Leave By Date Report - Team C - within the the 1st Thursday of last month.");

        logMessage("Item E.X39.9.3: Download and Validate Applied Leave By Date Report - Team C - 4 days from today", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20202, 20202, testSerialNo, driver), true, "Failed in Item E.X39.9.3: Download and Validate Applied Leave By Date Report - Team C - 4 days from today");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010521");
        myAssert.assertAll();
    }


    @Test (priority = 10531)
    public static void testE10531_ApproveJenniferHAnnualLeaveViaAdminToday() throws Exception {
        logMessage("*** Start Test E10531");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E.X39.9.4: Approve Jennifer H AL via Admin Today", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20341, 20341, testSerialNo, emailDomainName, driver), true, "Failed in Item E.X39.9.4: Approve Jennifer H AL via Admin today");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010531");
        myAssert.assertAll();
    }

    @Test (priority = 10541)
    public static void testE10541_ValidateExtraLeaveByDateReportAfterApproveALToday() throws Exception {
        logMessage("*** Start Test E10541");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E.X39.9.5: Download and Validate Applied Leave By Date Report - Team C - 4 days from today after approve AL today.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20211, 20211, testSerialNo, driver), true, "Failed in Item E.X39.9.3: Download and Validate Applied Leave By Date Report - Team C - 4 days from today after approve AL today");

        logMessage("Item E.X39.9.6: Download and Validate Applied Leave By Date Report - Team C - 4 days from today after approve AL today including Approved Leave.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAppliedLeaveByDateReport(20212, 20212, testSerialNo, driver), true, "Failed in Item E.X39.9.3: Download and Validate Applied Leave By Date Report - Team C - 4 days from today after approve AL today including Approved Leave");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E1010541");
        myAssert.assertAll();
    }

    @Test (priority = 10551)
    public static void testE10551_ValidateSharonALeaveAttachment() throws Exception {
        logMessage("*** Start Test E10551");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.1: Validate Sharon A Leave Attachment via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Attachment_Man(20321, 20321, testSerialNo, driver), true, "Failed in Item E40.1: Validate Sharon A Leave Attachment via Admin.");
        signoutESS(driver);
        driver.close();

        ///////////////////////
        logMessage("Log on As Sharon Andrew");
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.2: Validate Sharon A Leave Attachment via Sharon Andrew.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Attachment_Man(20321, 20321, testSerialNo, driver), true, "Failed in Item E40.2: Validate Sharon A Leave Attachment via Sharon Andrew.");

        signoutESS(driver);
        driver.close();

        /////////////////////////
        logMessage("Log on As Sue A");
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.3: Validate Sharon A Leave Attachment via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Attachment_Man(20321, 20321, testSerialNo, driver), true, "Failed in Item E40.2: Validate Sharon A Leave Attachment via Manager Sue A.");

        signoutESS(driver);
        driver.close();

        /////////////////////////

        logMessage("Log on As Christine R");
        driver = GeneralBasic.launchESS(url_ESS, driverType);
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10061, 10061, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.4: Validate Sharon A Businss Card via Other member Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiUserBusinessCard(30011, 30011, testSerialNo, emailDomainName, driver), true, "Failed in Item E40.4: Validate Sharon A Businss Card via Other member Christine R.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E1010551");
        myAssert.assertAll();
    }


    @Test (priority = 10561)
    public static void testE10561_ValidateSharonALeaveAttachmentViaApprover() throws Exception {
        logMessage("*** Start Test E10561");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        /////////////////////////
        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.5: Add Redirect Approval Christine (Sue) RAMPLING via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addRedirectApprovals_Main (20451, 20451, testSerialNo, emailDomainName, driver), true, "Failed in Item E40.5: Add Redirect Approval Christine (Sue) RAMPLING via Manager Sue A");

        signoutESS(driver);
        driver.close();

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        ///////////////////////
        logMessage("Log on As Sharon Andrew");
        driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.6: Sharon A Apply AL with attachment.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20331, 20331, testSerialNo, driver), true, "Failed in Item E40.6: Sharon A Apply AL with attachment.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E1010561");
        myAssert.assertAll();
    }

    @Test (priority = 10571)
    public static void testE10571_ValidateSharonALeaveAttachmentViaSueA() throws Exception {
        logMessage("*** Test E10571.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E40.7: Validate Sharon A AL Attachment via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Attachment_Man(20341, 20341, testSerialNo, driver), true, "Failed in Item E40.7: Validate Sharon A AL Attachment via Manager Sue A.");

        //This function is for non admin user only
        logMessage("Item E40.8: Validate Sharon A AL Attachment in Approval page via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovalsPage_Attachment(20701, 20701, testSerialNo, emailDomainName, driver), true, "Failed in Item E40.8: Validate Sharon A AL Attachment in Approval page via Manager Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E1010571");
        myAssert.assertAll();
    }

    @Test (priority = 10581)
    public static void testE10581_ValidateSharonALeaveAttachmentViaRedirectApproverChristineR() throws Exception {
        logMessage("*** Test E10581.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Christine R");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10061, 10061, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //This function is for non admin user only
        logMessage("Item E40.9: Validate Sharon A AL Attachment Via Redirect Approver Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovalsPage_Attachment(20701, 20701, testSerialNo, emailDomainName, driver), false, "Failed in Item E40.9: Validate Sharon A AL Attachment Via Redirect Approver Christine R.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E1010581");
        myAssert.assertAll();
    }

    //////////////////// Add Leave Audit Report Validation Test ////////////////
    @Test (priority = 10591)
    public static void testE10591_ApplyJenniferHALViaSueA() throws Exception {
        logMessage("*** Test E10591.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.1: Apply Jennifer H AL via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(511, 511, testSerialNo, driver), true, "Failed in Item E41.1: Apply Jennifer H AL via Manager Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10591");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10601)
    public static void testE10601_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10601.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.2: Download and validate Leave Application audit report after manager apply leave on behalf of member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(261, 261, emailDomainName, testSerialNo, driver), true, "Failed in Item E41.2: Download and validate Leave Application audit report after manager apply leave on behalf of member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10601.");
        myAssert.assertAll();
    }

    ////////////////////// Work Flow To Do Nothing ////////////////
    @Test(priority = 10611)
    public static void testE10611_EditLeaveWorkFlowToDoNothing() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10611.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.3: Edit Leave Work flow to Do Nothing.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(181, 181, emailDomainName, testSerialNo, driver), true, "Failed in Item E41.3: Edit Leave Work flow to Do Nothing.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10611.");
        myAssert.assertAll();
    }

    @Test (priority = 10621)
    public static void testE10621_ApplySLViaMemberJenniferH() throws Exception {
        logMessage("*** Test E10621.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.4: Apply SL Via Team Member Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(521, 521, testSerialNo, driver), true, "Failed in Item E41.4: Apply SL Via Team Member Jennifer H.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10621");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10631)
    public static void testE10631_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10631.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.5: Download and validate Leave Application audit report after member apply leave with Do Nothing Leave Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(271, 271, emailDomainName, testSerialNo, driver), true, "Failed in E41.5: Download and validate Leave Application audit report after member apply leave with Do Nothing Leave Work flow.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10631.");
        myAssert.assertAll();
    }

    @Test (priority = 10641)
    public static void testE10641_ApplyJenniferHSLViaSueAWithDoNothingSetting() throws Exception {
        logMessage("*** Test E10641.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.6: Apply Jennifer H SL via Manager Sue A with Do Nothing Setting.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(531, 531, testSerialNo, driver), true, "Failed in Item E41.6: Apply Jennifer H SL via Manager Sue A with Do Nothing Setting.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10641");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10651)
    public static void testE10651_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10651.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.7: Download and validate Leave Application audit report after manager apply leave with Do Nothing Leave Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(281, 281, emailDomainName, testSerialNo, driver), true, "Failed in E41.7: Download and validate Leave Application audit report after manager apply leave with Do Nothing Leave Work flow.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10651.");
        myAssert.assertAll();
    }

    //////////////// Leave Work Flow set to Send Notification. ////////////////////
    @Test(priority = 10661)
    public static void testE10661_EditLeaveWorkFlowToSendANotification() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10611.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.8: Edit Leave Work flow to Send Notification.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(191, 191, emailDomainName, testSerialNo, driver), true, "Failed in Item E41.8: Edit Leave Work flow to Send Notification.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10661.");
        myAssert.assertAll();
    }

    @Test (priority = 10671)
    public static void testE10671_ApplyALViaMemberJenniferHWithSendToNotification() throws Exception {
        logMessage("*** Test E10671.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(531, 531, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.9: Apply AL Via Team Member Jennifer H with Send to Notification Settings.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(541, 541, testSerialNo, driver), true, "Failed in Item E41.9: Apply AL Via Team Member Jennifer H with Send to Notification Settings.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10671");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10681)
    public static void testE10681_DownloadAndValidateLeaveApplicationAuditReportAfterMemberApplyALWithSendToNotificationSettings() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10681.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.10: Download and validate Leave Application audit report after member apply leave with Send to Notification Settings.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(291, 291, emailDomainName, testSerialNo, driver), true, "Failed in Item E41.10: Download and validate Leave Application audit report after member apply leave with Send to Notification Settings.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10681.");
        myAssert.assertAll();
    }

    @Test (priority = 10691)
    public static void testE10691_ApplyJenniferHAKViaSueAWithSendToNotificationSettings() throws Exception {
        logMessage("*** Test E10641.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.11: Apply Jennifer H SL via Manager Sue A with Send to Notification Settings.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(531, 531, testSerialNo, driver), true, "Failed in Item E41.11: Apply Jennifer H SL via Manager Sue A with Send to Notification Settings.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10691");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10701)
    public static void testE10701_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10701.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E41.12: Download and validate Leave Application audit report after manager apply leave with Send to Notification Leave Work flow Settings.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(301, 301, emailDomainName, testSerialNo, driver), true, "Failed in Item E41.12: Download and validate Leave Application audit report after manager apply leave with Send to Notification Leave Work flow Settings.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10701.");
        myAssert.assertAll();
    }

    ////////////////////// Leave Work Flow To approval is required by all managers in the Team ////////////////
    @Test(priority = 10711)
    public static void testE10711_EditLeaveWorkFlowApprovalAllManagers() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10711.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.1: Edit Leave Work flow to approval is required by all managers in the Team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(201, 201, emailDomainName, testSerialNo, driver), true, "Failed in Item E42.1: Edit Leave Work flow to approval is required by all managers in the Team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10711.");
        myAssert.assertAll();
    }

    @Test (priority = 10721)
    public static void testE10721_ApplyALViaMemberSharonA() throws Exception {
        logMessage("*** Test E10721.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.2: Apply AL Via Team Member Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(561, 561, testSerialNo, driver), true, "Failed in Item E42.2: Apply AL Via Team Member Sharon A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10721");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10731)
    public static void testE10731_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10731.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.3: Download and validate Leave Application audit report after member apply leave with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(311, 311, emailDomainName, testSerialNo, driver), true, "Failed in E42.3: Download and validate Leave Application audit report after member apply leave with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10731.");
        myAssert.assertAll();
    }

    @Test (priority = 10741)
    public static void testE10741_ApproveAndValidateSharonAALViaSueA() throws Exception {
        logMessage("*** Start test E10741.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10012, 10012, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.4: Approve Sharon A Anuual Leave via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(161, 161, testSerialNo, emailDomainName, driver), true, "Falied in Item E42.4: Approve Sharon A Anuual Leave via Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10741");
        myAssert.assertAll();
    }


    //Must run without headless
    @Test(priority = 10751)
    public static void testE10751_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10751.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.5: Download and validate Leave Application audit report after Sue A Approve leave with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(321, 321, emailDomainName, testSerialNo, driver), true, "Failed in E42.5: Download and validate Leave Application audit report after Sue A Approve leave with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10751.");
        myAssert.assertAll();
    }


    @Test (priority = 10761)
    public static void testE10761_ApproveAndValidateSharonAALViaJackF() throws Exception {
        logMessage("*** Start test E10761.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack FINGLE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10011, 10011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.6: Approve Sharon A Anuual Leave via Jack F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(171, 171, testSerialNo, emailDomainName, driver), true, "Falied in Item E42.6: Approve Sharon A Anuual Leave via Jack F.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10761");
        myAssert.assertAll();
    }


    //Must run without headless
    @Test(priority = 10771)
    public static void testE10771_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10771.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.7: Download and validate Leave Application audit report after Jack F Approve leave with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(331, 331, emailDomainName, testSerialNo, driver), true, "Failed in E42.7: Download and validate Leave Application audit report after Jack F Approve leave with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10771.");
        myAssert.assertAll();
    }

    @Test (priority = 10781)
    public static void testE10781_ApplyAndValidateSharonAALViaSueA() throws Exception {
        logMessage("*** Start test E10781.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10012, 10012, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.8: Apply Sharon A AL Via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(571, 571, testSerialNo, driver), true, "Failed in Item E42.8: Apply Sharon A AL Via Manager Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10781");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10791)
    public static void testE10791_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10791.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.9: Download and validate Leave Application audit report after Sue A Submitted leave Behalf of Sharon A with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(341, 341, emailDomainName, testSerialNo, driver), true, "Failed in Item E42.9: Download and validate Leave Application audit report after Sue A Submitted leave Behalf of Sharon A with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10791.");
        myAssert.assertAll();
    }

    @Test (priority = 10801)
    public static void testE10801_ApproveAndValidateSharonAALViaJackF() throws Exception {
        logMessage("*** Start test E10801.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack FINGLE");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10011, 10011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.10: Approve Sharon A Anuual Leave via Jack F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(181, 181, testSerialNo, emailDomainName, driver), true, "Falied in Item E42.10: Approve Sharon A Anuual Leave via Jack F.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10801");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10811)
    public static void testE10811_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10811.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.11: Download and validate Leave Application audit report after Jack F Approve leave with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(351, 351, emailDomainName, testSerialNo, driver), true, "Failed in E42.11: Download and validate Leave Application audit report after Jack F Approve leave with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10811.");
        myAssert.assertAll();
    }

    @Test (priority = 10821)
    public static void testE10821_ApplySLViaMemberSharonA() throws Exception {
        logMessage("*** Test E10821.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.12: Apply SL Via Team Member Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(576, 576, testSerialNo, driver), true, "Failed in Item E42.12: Apply SL Via Team Member Sharon A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10821");
        myAssert.assertAll();
    }

    @Test (priority = 10831)
    public static void testE10831_ApproveAndValidateSharonAALViaAdmin() throws Exception {
        logMessage("*** Start test E10831.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.13: Approve Sharon A Sick Leave via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(186, 186, testSerialNo, emailDomainName, driver), true, "Falied in Item E42.13: Approve Sharon A Sick Leave via Admin.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10831");
        myAssert.assertAll();
    }

    @Test(priority = 10841)
    public static void testE10841_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10841.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E42.14: Download and validate Leave Application audit report after Admin Approve leave with required by all managers.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(356, 356, emailDomainName, testSerialNo, driver), true, "Failed in E42.14: Download and validate Leave Application audit report after Jack F Approve leave with required by all managers.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10841.");
        myAssert.assertAll();
    }

    ////////////////////// Leave Work Flow To approval is required by any managers in the Team and approval is required by ANY manager in another team ////////////////
    @Test(priority = 10851)
    public static void testE10851_EditLeaveWorkFlowApprovalAnyManagerTeamAnyManagerAnotherTeam() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10851.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.1: Edit Leave workflow - then:approval is required;by:any manager;in:the member's team;and then:approval is required;by:any manager;in:Team F via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(211, 211, emailDomainName, testSerialNo, driver), true, "Failed in Item E43.1: Edit Leave workflow - then:approval is required;by:any manager;in:the member's team;and then:approval is required;by:any manager;in:Team F via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10851.");
        myAssert.assertAll();
    }

    @Test (priority = 10861)
    public static void testE10861_ApplySLViaMemberSharonA() throws Exception {
        logMessage("*** Test E10861.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.2: Apply AL Via Team Member Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(581, 581, testSerialNo, driver), true, "Failed in Item E43.2: Apply AL Via Team Member Sharon A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10861");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10871)
    public static void testE10871_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10871.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.3: Download and validate Leave Application audit report after member apply leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(361, 361, emailDomainName, testSerialNo, driver), true, "Failed in E43.3: Download and validate Leave Application audit report after member apply leave with required by any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10871.");
        myAssert.assertAll();
    }

    @Test (priority = 10881)
    public static void testE10881_ApproveAndValidateSharonAALViaSueA() throws Exception {
        logMessage("*** Start test E10881.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10012, 10012, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.4: Approve Sharon A Anuual Leave via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(191, 191, testSerialNo, emailDomainName, driver), true, "Falied in Item E43.4: Approve Sharon A Anuual Leave via Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10881");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10891)
    public static void testE10891_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10891.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.5: Download and validate Leave Application audit report after Sue A Approve leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(371, 371, emailDomainName, testSerialNo, driver), true, "Failed in E43.5: Download and validate Leave Application audit report after Sue A Approve leave with required by  any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10891.");
        myAssert.assertAll();
    }

    @Test (priority = 10901)
    public static void testE10901_ApproveAndValidateSharonAALViaJackF() throws Exception {
        logMessage("*** Start test E10901.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Phantom F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.6: Approve Sharon A Anuual Leave via Phantom F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(201, 201, testSerialNo, emailDomainName, driver), true, "Falied in Item E43.6: Approve Sharon A Anuual Leave via Phantom F.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10901");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10911)
    public static void testE10911_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10911.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.7: Download and validate Leave Application audit report after Jack F Approve leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(381, 381, emailDomainName, testSerialNo, driver), true, "Failed in E43.7: Download and validate Leave Application audit report after Jack F Approve leave with required by any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10911.");
        myAssert.assertAll();
    }

    @Test (priority = 10921)
    public static void testE10921_ApplyAndValidateSharonAALViaSueA() throws Exception {
        logMessage("*** Start test E10921.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on As Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10012, 10012, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.8: Apply Sharon A AL Via Manager Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(591, 591, testSerialNo, driver), true, "Failed in Item E43.8: Apply Sharon A AL Via Manager Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10921");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10931)
    public static void testE10931_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10931.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.9: Download and validate Leave Application audit report after Sue A Submitted leave Behalf of Sharon A leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(391, 391, emailDomainName, testSerialNo, driver), true, "Failed in Item E43.9: Download and validate Leave Application audit report after Sue A Submitted leave Behalf of Sharon A leave with required by any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10931.");
        myAssert.assertAll();
    }

    @Test (priority = 10941)
    public static void testE10941_ApproveAndValidateSharonAALViaJackF() throws Exception {
        logMessage("*** Start test E10941.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Phantom F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.10: Approve Sharon A Anuual Leave via Phantom F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(211, 211, testSerialNo, emailDomainName, driver), true, "Falied in Item E43.10: Approve Sharon A Anuual Leave via Phantom F.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10941");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10951)
    public static void testE10951_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10951.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.11: Download and validate Leave Application audit report after Jack F Approve leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(401, 401, emailDomainName, testSerialNo, driver), true, "Failed in E43.11: Download and validate Leave Application audit report after Jack F Approve leave with required by any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10951.");
        myAssert.assertAll();
    }


    @Test (priority = 10961)
    public static void testE10961_ApplySLViaMemberSharonA() throws Exception {
        logMessage("*** Test E10961.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.12: Apply SL Via Team Member Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(601, 601, testSerialNo, driver), true, "Failed in Item E43.12: Apply SL Via Team Member Sharon A.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10961");
        myAssert.assertAll();
    }

    @Test (priority = 10971)
    public static void testE10971_ApproveAndValidateSharonAALViaAdmin() throws Exception {
        logMessage("*** Start test E10971.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.13: Approve Sharon A Sick Leave via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(221, 221, testSerialNo, emailDomainName, driver), true, "Falied in Item E43.13: Approve Sharon A Sick Leave via Admin.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of Test E10971");
        myAssert.assertAll();
    }

    @Test(priority = 10981)
    public static void testE10981_DownloadAndValidateLeaveApplicationAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test E10981.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on As Admin");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item E43.14: Download and validate Leave Application audit report after Admin Approve leave with required by any manager and any manager another team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(411, 411, emailDomainName, testSerialNo, driver), true, "Failed in E43.14: Download and validate Leave Application audit report after Admin Approve leave with required by any manager and any manager another team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10981.");
        myAssert.assertAll();
    }




    //////


    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item EEE", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module E - Leave Function test.");
    }

    /////////////////////// Debug here //////////////////////


}
