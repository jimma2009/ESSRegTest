package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.getCurrentTestSerialNumber;
import static Lib.GeneralBasicHigh.getTestKeyConfigureFromDatasheet_Main;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.logMessage;
import static Lib.SystemLibrary.serverName;

public class ESSRegTestN_EnvChangeMP {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=114;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String payrollDBOrderNumber;
    private static String moduleName="N";

    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");
            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");

            //////////////// New Field /////////////
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");
            testEmailNotification=getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailAddressForTestNotification");
            ///////

            if (payrollDBOrderNumber==null){
                payrollDBOrderNumber="1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 10011) //a
    public void testN10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item N10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        GeneralBasicHigh.configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item N1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item N1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item N1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item N1.5", "pass", testSerialNo);

        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item N1.6: Remove date from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item N1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item N1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item N1.7: Setup Admin user contact details.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item N1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Item N10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testN10021_WebAPIKeyAndSync_Step1_1() throws InterruptedException, IOException, Exception {
        SystemLibrary.logMessage("*** Start test N10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item N1.8: Add API configuration", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver),true, "Failed in Item N1.8: Add API configuration.");

        logMessage("Item N1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Failed in Item N1.9: Sync All");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test N10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testN10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test N10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item N2.1: Validate Team Initial Status - Unassigned member count", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20671, 20671, testSerialNo, emailDomainName, driver), true, "Failed in Item N2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item N2.2: Validate Tem Initial Status - Unassigned Team member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20681, 20681, testSerialNo, emailDomainName, driver), true, "Failed in Item N2.2: Validate Tem Initial Status - Unassigned Team member.");

        SystemLibrary.logMessage("Item N2.3: Change roles and Permisson Employment, Bank and Pay Salary Details for Admin user", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20221, 20223, driver), true, "Failed in Item N2.3: Change roles and Permisson Employment, Bank and Pay Salary Details for Admin user");

        signoutESS(driver);
        driver.close();

        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of test N10031.");
        myAssert.assertAll();
    }

    @Test (priority=10041)
    public static void testN10041_addPeriodFrequency_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10041.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        SystemLibrary.logMessage("Item N3: Edit period and frequency in Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiPayFrequencies(10001, 10001), true, "Failed in Item N3: Edit period and frequency in Micropay");

        AutoITLib.exitMeridian();
        logMessage("End of test N10041");
        myAssert.assertAll();
    }

    @Test (priority = 10051)
    public static void testN10051_ApplyLeaveViaAdmin_Step1_3() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10051.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N4.1: Apply prior period Annual Leave for MITCHELL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20421, 20421, testSerialNo, driver), true, "Failed in Item N4.1: Apply prior period Annual Leave for MITCHELL");

        logMessage("Item N4.2: Apply future period Sick Leave for SPACEY", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20431, 20431, testSerialNo, driver), true, "Failed in Item N4.2: Apply future period Sick Leave for SPACEY");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test N10051");
        myAssert.assertAll();
    }

    @Test (priority = 10061)
    public static void testN10061_EOP_Micr0pay_Step1_8() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10061.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N5.1: Run Leave Processing in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10051, 10051), true, "Failed in Item N5.1: Run Leave Processing in Micropay for the current pay period");

        logMessage("Item N5.2: Genarate  Auto Pay Transactions in Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.generateAutoPayMultiTransactions(10011, 10011), true, "Failed in Item N5.2: Genarate  Auto Pay Transactions in Micropay.");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N5.3: Produce pay advices in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiPayAdvice(10011, 10011), true, "Failed in Item N5.3: Produce pay advices in Micropay for the current pay period");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N5.4: Print pay advices in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiPayAdvice(10021, 10021), true, "Failed in Item N5.4: Print pay advices in Micropay for the current pay period");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N5.5:: Perform EOP in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiEOP(10011, 10011), true, "Failed in Item N5.5: Perform EOP in MicrOpay.");

        AutoITLib.exitMeridian();
        logMessage("End of test N10061");
        myAssert.assertAll();
    }

    @Test(priority = 10071) //a
    public void testN10071_Backup_Micr0pay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Item N10071: Backup Micr0pay");

        logMessage("Item N6: Back up Payroll DB - Step11.", testSerialNo);
        myAssert.assertEquals(DBManage.backupMultiDBSaveInFile(10001, 10001, testSerialNo), true, "Failed in Item N6: Back up Payroll DB - Step11.");

        Thread.sleep(30000); //Added 0.5 mins

        logMessage("Item N6.1: Validate the leave applications have been sent to Micropay Step12.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10001, 10001, emailDomainName, testSerialNo), true, "Failed in Failed in Item N6.1: Validate the leave applications have been sent to Micropay Step12.");

        SystemLibrary.logMessage("*** End of Item N10071: Backup Micr0pay");
        myAssert.assertAll();
    }

    @Test (priority = 10081)
    public static void testN10081_ValidateLeaveBalancesViaAdmin_Step2_12() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10081.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N7.1: Validate Mitchell S Annaul Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20241, 20241, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N7.1: Validate Mitchell S Annaul Leave Balance via Leave Page");

        logMessage("Item N7.2: Validate Robin S Personal Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main (20251, 20251, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N7.2: Validate Robin S Personal Leave Balance via Leave Page");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test N10081");
        myAssert.assertAll();
    }

    @Test (priority = 10091)
    public static void testN10091_ApplyLeaveViaAdmin_Step2_16() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10091.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N8.1: Apply prior period Annual Leave for Jennifer H", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20441, 20441, testSerialNo, driver), true, "Failed in  Item N8.1: Apply prior period Annual Leave for Jennifer H");

        logMessage("Item N8.2: Validate Jennifer H Annaul Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20261, 20261, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N8.2: Validate Jennifer H Annaul Leave Balance via Leave Page");

        logMessage("Item N8.3: Apply current period Sick Leave for Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20451, 20451, testSerialNo, driver), true, "Failed in Item N8.3: Apply current period Sick Leave for Sharon A");

        logMessage("Item N8.4: Validate Sharon A Sick Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20271, 20271, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in Item N8.4: Validate Sharon A Sick Leave Balance via Leave Page");

        logMessage("Item N8.5: Apply future period Annual Leave for Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20461, 20461, testSerialNo, driver), true, "Failed in  Item N8.5: Apply future period Annual Leave for Richard Z");

        logMessage("Item N8.5.1: Validate Richard Z Annual Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20275, 20275, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in Item N8.5.1: Validate Richard Z Annual Leave Balance via Leave Page");

        logMessage("Item N8.6: Edit Annual Leave to LSL for Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiApprovedLeave(20471, 20471, testSerialNo, driver), true, "Failed in  Item N8.6: Edit Annual Leave to LSL for Mitchell S");

        logMessage("Item N8.7: Validate Mitchell S Annual Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20281, 20281, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in Item N8.7: Validate Mitchell S Annual Leave Balance via Leave Page");

        logMessage("Item N8.8: Validate Mitchell S LS Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20291, 20291, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N8.8: Validate Mitchell S LS Leave Balance via Leave Page");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test N10091");
        myAssert.assertAll();
    }

    @Test(priority = 10101) //a
    public void testN10101_Backup_Micr0pay_Step2_30() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Item N10101: Backup Micr0pay");

        logMessage("Item N9: Backup Payroll DB - Step30.", testSerialNo);
        myAssert.assertEquals(DBManage.backupMultiDBSaveInFile(10011, 10011, testSerialNo), true, "Failed in Item N9: Backup Payroll DB - Step30.");

        Thread.sleep(30000); //Added 0.5 mins

        logMessage("Item N9.1: Validate the leave applications have been sent to Micropay Step31.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10005, 10005, emailDomainName, testSerialNo), true, "Failed in Failed in Item N9.1: Validate the leave applications have been sent to Micropay Step31");

        SystemLibrary.logMessage("*** End of Item N10101: Backup Micr0pay");
        myAssert.assertAll();
    }

    @Test (priority = 10111)
    public static void testN10111_validateLeaveApplications_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10111.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N10.1: Preview and Validate Leave Processing", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10061, 10061), true, "Failed in Item N10.1: Preview and Validate Leave Processing");

        AutoITLib.exitMeridian();
        logMessage("End of test N10111");
        myAssert.assertAll();
    }

    @Test(priority = 10121) //a
    public void testN10121_Restore_Micr0pay_Step2_32() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Item N10121: Restore Micr0pay");

        logMessage("Item N11: Restore Payroll DB - Step11.", testSerialNo);
        DBManage.deleteMultiDBFromFile(10001, 10001);
        myAssert.assertEquals(DBManage.restoreMultiDBFromFile(10001, 10001, testSerialNo), true, "Failed in Item N11: Restore Payroll DB - Step11.");

       logMessage("Item N11.1: Re-start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.restartMCS(), true, "Failed in Item N11.1: Re-start MCS.");

        Thread.sleep(240000); //Added 4 mins

        SystemLibrary.logMessage("*** End of Item N10121: Restore Micr0pay");
        myAssert.assertAll();
    }

    @Test(priority = 10122)
    public static void testN10122_validateLeaveApplicationsin_Micropay_Step2_33() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item N11.2: Validate the leave applications have been sent to Micropay Step33.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10011, 10011, emailDomainName, testSerialNo), true, "Failed in Failed in Item N11.2: Validate the leave applications have been sent to Micropay Step33");

        logMessage("*** End of Test N10122");
        myAssert.assertAll();
    }

    @Test (priority = 10131)
    public static void testN10131_ValidateLeaveApplicationInESS_AfterRestoreDB() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10131.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N12.1: Validate Jennifer H Annual Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20301, 20301, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N12.1: Validate Jennifer H Annual Leave Balance via Leave Page");

        logMessage("Item N12.2: Validate Sharon A Sick Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20311, 20311, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in Item N12.2: Validate Sharon A Sick Leave Balance via Leave Page");

        logMessage("Item N12.3: Validate Richard Z Annual Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20321, 20321, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in  Item N12.3: Validate Richard Z Annual Leave Balance via Leave Page");

        logMessage("Item N12.4: Validate Mitchell S Annual Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBalanceDetails_ViaLeavePage_Main(20331, 20331, testSerialNo, serverName, payrollDBName, emailDomainName, driver), true, "Failed in N12.4: Validate Mitchell S Annual Leave Balance via Leave Page");

        logMessage("Item N12.5: Validate Mitchell S LS Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20341, 20341, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N12.5: Validate Mitchell S LS Leave Balance via Leave Page");

        logMessage("Item N12.6: Validate Robin S Sick Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20351, 20351, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N12.6: Validate Robin S Sick Leave Balance via Leave Page");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test N10131");
        myAssert.assertAll();
    }

    @Test (priority = 10141)
    public static void testN10141_EditLeaveViaAdmin_Step_2_47() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10141.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N13.1: Edit Sick Leave for Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiApprovedLeave(20481, 20481, testSerialNo, driver), true, "Failed in  Item N13.1: Edit Sick Leave for Robin S");

        logMessage("Item N13.2: Validate Robin S Sick Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20361, 20361, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N13.2: Validate Robin S Sick Leave Balance via Leave Page");

        signoutESS(driver);
        driver.close();

        Thread.sleep(60000); //Added 1 mins

        logMessage("Item N13.3: Validate the leave applications have been sent to Micropay Step50.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10021, 10021, emailDomainName, testSerialNo), true, "Failed in Failed in Item N13.3: Validate the leave applications have been sent to Micropay Step50");

        logMessage("*** End of Test N10141");
        myAssert.assertAll();
    }

    @Test(priority = 10151) //a
    public void testN10151_Restore_Micr0pay_Step2_51() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Item N10151: Restore Micr0pay");

        logMessage("Item N14: Restore Payroll DB - Step11.", testSerialNo);
        DBManage.deleteMultiDBFromFile(10001, 10001);
        myAssert.assertEquals(DBManage.restoreMultiDBFromFile(10001, 10001, testSerialNo), true, "Failed in Item N14: Restore Payroll DB - Step11.");

        logMessage("Item N14.1: Re-start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.restartMCS(), true, "Failed in Item N14.1: Re-start MCS.");

        Thread.sleep(240000); //Added 4 mins

        logMessage("Item N14.2: Validate the leave applications have been sent to Micropay Step52.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10026, 10026, emailDomainName, testSerialNo), true, "Failed in Failed in Item N14.2: Validate the leave applications have been sent to Micropay Step52");

        SystemLibrary.logMessage("*** End of Item N10151: Restore Micr0pay");
        myAssert.assertAll();
    }

     @Test (priority = 10161)
     public static void testN10161_validateLeaveApplications_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10161.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N15.1: Preview and Validate Leave Processing", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10071, 10071), true, "Failed in Item N15.1: Preview and Validate Leave Processing");

        AutoITLib.exitMeridian();
        logMessage("End of test N10161");
        myAssert.assertAll();
    }

    @Test (priority = 10171)
    public static void testN10171_ValidateLeaveApplicationInESS_AfterRestoreDB() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10171.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N16.1: Validate Robin S Sick Leave Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20371, 20371, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item N16.1: Validate Robin S Sick Leave Balance via Leave Page");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test N10171");
        myAssert.assertAll();
    }

    @Test (priority = 10181)
    public static void testN10181_editProfileChangesInESS_Section3_Step3_55() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10181.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N17.1: Edit Carmin C Gender to Other", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20551, 20551, testSerialNo, driver), true, "Failed in Item N17.1: Edit Carmin C Gender to Other via Admin");

        logMessage("Item N17.2: Edit Charles L Postal Address", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20331, 20331, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item N17.2: Edit Charles L Postal Address");

        logMessage("Item N17.3: Edit Robert S Postal Address", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20341, 20341, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item N17.3: Edit Robert S Postal Address");

        Thread.sleep(12000); //Added 2 mins
        signoutESS(driver);
        driver.close();

        logMessage("*** End of test N10181");
        myAssert.assertAll();
    }

    @Test(priority = 10191)
    public static void testN10191_ImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10191.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);
        Thread.sleep(12000); //Added 2 mins
        logMessage("Item N18.1: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item N18.1: Implement eHR via Sage Micropay.");

        AutoITLib.close_ImplementHRScreen();
        AutoITLib.exitMeridian();
        logMessage("End of Test 10191.");
        myAssert.assertAll();
    }

    @Test(priority = 10201) //a
    public void testN10201_Backup_Micr0pay_Step3_59() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Item N10201: Backup Micr0pay");

        logMessage("Item N19.1: Backup Payroll DB - Step59.", testSerialNo);
        myAssert.assertEquals(DBManage.backupMultiDBSaveInFile(10021, 10021, testSerialNo), true, "Failed in Item N19.1: Backup Payroll DB - Step59.");

        SystemLibrary.logMessage("*** End of Item N10201: Backup Micr0pay");
        myAssert.assertAll();
    }

    @Test (priority = 10211)
    public static void testN10211_editProfileChangesInESS () throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test 10211.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N20.1: Romove Robert S Bank Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.delteMultiBankAccount(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item N20.1: Romove Robert S Bank Details");

        logMessage("Item N20.2: Edit Sue A Surname ORANGES", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20561, 20561, testSerialNo, driver), true, "Failed in Item N20.2: Edit Sue A Surname ORANGES");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test N10211");
        myAssert.assertAll();
    }

    @Test(priority = 10221) //a
    public void testN10221_Restore_Micr0pay_Step3_64() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Item N10221: Restore Micr0pay");

        Thread.sleep(180000); //Added 3 mins

        logMessage("Item N20.3: Validate the Profile Changes have been sent to Micropay Step63.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10031, 10031, emailDomainName, testSerialNo), true, "Failed in Failed in Item N20.3: Validate the Profile Changes have been sent to Micropay Step63");

        Thread.sleep(30000); //Added 0.5 mins

        logMessage("Item N21: Restore Payroll DB - Step59.", testSerialNo);
        DBManage.deleteMultiDBFromFile(10011, 10011);
        myAssert.assertEquals(DBManage.restoreMultiDBFromFile(10021, 10021, testSerialNo), true, "Failed in Item N21: Restore Payroll DB - Step59.");

        logMessage("Item N21.1: Re-start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.restartMCS(), true, "Failed in Item N21.1: Re-start MCS.");

        Thread.sleep(240000); //Added 4 mins

        logMessage("Item N21.2: Validate the Profile Changes have been sent to Micropay Step65.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10041, 10041, emailDomainName, testSerialNo), true, "Failed in Failed in Item N21.2: Validate the Profile Changes have been sent to Micropay Step65");

        SystemLibrary.logMessage("*** End of Item N10221: Restore Micr0pay");
        myAssert.assertAll();
    }

    @Test(priority = 10231)
    public static void testN10231_ImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10231.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N22.1: Validate Grid List in Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.saveGridInImplementEHRScreen_Main(20085, 20085), true, "Failed in Item N22.1: Validate Grid List in Implement eHR via Sage Micropay.");

        logMessage("Item N22.2: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item N22.2: Implement eHR via Sage Micropay.");

        AutoITLib.close_ImplementHRScreen();
        AutoITLib.exitMeridian();
        logMessage("End of Test 10231.");
        myAssert.assertAll();
    }

    @Test (priority=10241)
    public static void testN10241_ValidiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test N10241.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N23.1: Validate Robert S bank account Details in MP.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20221, 20221, emailDomainName, testSerialNo), true, "Falied in Item N23.1: Validate Robert S bank account Details in MP.");

        logMessage("Item N23.2: Validate Sue O surname Detail in MP.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20231, 20231, emailDomainName, testSerialNo), true, "Falied in Item N23.2: Validate Sue O surname Detail in MP.");

        AutoITLib.exitMeridian();
        logMessage("End of test N10241");
        myAssert.assertAll();
    }

    @Test(priority = 10251)
    public void testN10251_removeWebAPIKeyAndEditProfileChangesApplyLeave_Step4_69() throws InterruptedException, IOException, Exception {
        SystemLibrary.logMessage("*** Start test N10251.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item N24.1: Remove Web API Key.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeGeneralAPIKey_Main(testSerialNo, driver),  true, "Failed in Item N24.1: Remove Web API Key.");

        logMessage("Item N24.2: Edit Denise M Marital Status", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20571, 20571, testSerialNo, driver), true, "Failed in Item N24.2: Edit Denise M Marital Status");

        logMessage("Item N24.3: Edit Magaret C Residential Address", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails_Address_WebAPIOFF(20441, 20441, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item N24.3: Edit Magaret C Residential Address");

        SystemLibrary.logMessage("Item N25.2: Add API", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver),true, "Failed in Item N25.2: Add API");

        Thread.sleep(180000); //Added 3 mins

        // Update by Govind on 12/08/2020
        // ESS checks API calls three times (3 mins). if add profile change and Leave applications are not complete with in 3 mins, changes are not imported to Micr0pay
        // After discussion with Jim, We spilt this test conditions with two parts

        logMessage("Item N24.5: Remove Web API.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeGeneralAPIKey_Main(testSerialNo, driver),  true, "Failed in Item N24.5: Remove Web API.");

        logMessage("Item N24.4: Apply current period Time In Lieu #1 Leave for Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20491, 20491, testSerialNo, driver), true, "Failed in  Item N24.4: Apply current period Time In Lieu #1 Leave for Stanley B");

        SystemLibrary.logMessage("Item N25.1: Add API configuration Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver),true, "Failed in Item N25.1: Add API configuration Details");

        Thread.sleep(120000); //Added 2 mins


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test N10251.");
        myAssert.assertAll();
    }

    @Test(priority = 10261)
    public static void testN10261_ImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10261.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N26.1: Validate Grid List in Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.saveGridInImplementEHRScreen_Main(20091, 20091), true, "Failed in Item N26.1: Validate Grid List in Implement eHR via Sage Micropay.");

        logMessage("Item N26.2: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item N26.2: Implement eHR via Sage Micropay.");

        AutoITLib.close_ImplementHRScreen();
        AutoITLib.exitMeridian();
        logMessage("End of Test 10261.");
        myAssert.assertAll();
    }

    @Test (priority = 10271)
    public static void testN10271_ValidiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test N10271.");
        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N27.1: Preview and Validate Leave Processing", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10081, 10081), true, "Failed in Item N27.1: Preview and Validate Leave Processing");

        logMessage("Item N27.2: Validate the leave balances for Stanley B in Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20241, 20241, emailDomainName, testSerialNo), true, "Falied in Item N27.2: Validate the leave balances for Stanley B in Micropay.");

        AutoITLib.exitMeridian();
        logMessage("End of test N10271");
        myAssert.assertAll();
    }

    @Test(priority = 10281)
    public void testN10281_StopMCS_Step5_80() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test N10281.");

        logMessage("Item N28.1: Stop MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.stopMCS(), true, "Failed in Item N28.1: Stop MCS.");

        SystemLibrary.logMessage("*** End of Test N10281.");
        myAssert.assertAll();
    }

    @Test (priority = 10291)
    public static void testN10291_editProfileChangesInESS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10291.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N28.2: Edit Johnathon D Gender to Other", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20581, 20581, testSerialNo, driver), true, "Failed in Item N28.2: Edit Johnathon D Gender to Other");

        logMessage("Item N28.3: Add Martin G Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item N28.3: Add Martin G Bank Account Details");

        logMessage("Item N28.4: Apply Prior Period Annual Leave for Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20501, 20501, testSerialNo, driver), true, "Failed in  Item N28.4: Apply Prior Period Annual Leave for Stanley B");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test N10291");
        myAssert.assertAll();
    }

    @Test(priority = 10301)
    public void testN10301_StartMCS_Step4_86() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test N10301.");

        logMessage("Item N28.5: Start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.startMCS(), true, "Failed in Item N28.5: Start MCS.");
        Thread.sleep(150000); //Added 2.5 mins

        SystemLibrary.logMessage("*** End of Test N10301.");
        myAssert.assertAll();
    }

    @Test(priority = 10311)
    public static void testN10311_ImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10311.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N28.6: Validate Grid List in Implement eHR via Micropay after Start MCS.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.saveGridInImplementEHRScreen_Main(20101, 20101), true, "Failed in Item N28.6: Validate Grid List in Implement eHR via Micropay after Start MCS.");

        logMessage("Item N28.7: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item N28.7: Implement eHR via Sage Micropay.");

        AutoITLib.close_ImplementHRScreen();
        AutoITLib.exitMeridian();
        logMessage("End of Test 10311.");
        myAssert.assertAll();
    }

    @Test (priority = 10321)
    public static void testN10321_ValidiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test N10321.");
        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N28.8: Preview and Validate Leave Processing", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10091, 10091), true, "Failed in Item N28.8: Preview and Validate Leave Processing");

        logMessage("Item N28.9: Validate the leave balances for Stanley B in Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20251, 20251, emailDomainName, testSerialNo), true, "Falied in Item N28.9: Validate the leave balances for Stanley B in Micropay");

        AutoITLib.exitMeridian();
        logMessage("End of test N10321");
        myAssert.assertAll();
    }

    @Test(priority = 10331)
    public void testN10331_removeWebAPIKey_Step6_91() throws InterruptedException, IOException, Exception {
        SystemLibrary.logMessage("*** Start test N10331.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item N29.1: Remove Web API Key.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeGeneralAPIKey_Main(testSerialNo, driver),  true, "Failed in Item N29.1: Remove Web API Key.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test N10331.");
        myAssert.assertAll();
    }

    @Test(priority = 10341)
    public static void testN10341_ImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("Start Test 10341.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N29.2: Edit Hourly rate for LONG", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_RateDetails(10001, 10001, payrollDBName), true, "Failed in Item H29.2: Edit Hourly rate for LONG");

        logMessage("Item N29.3: Edit Cost Account for BROMFIELD", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.addMultiEmployee_CostAccount(10011, 10011, payrollDBName), true, "Failed in Item H29.3: Edit Cost Account for BROMFIELD");

        AutoITLib.exitMeridian();
        logMessage("End of Test 10341.");
        myAssert.assertAll();
    }

    @Test(priority = 10351)
    public void testN10351_WebAPIKeyAndSync_Step6_95() throws InterruptedException, IOException, Exception {
        SystemLibrary.logMessage("*** Start test N10351.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        ////////////////// Know issue failed. Temp change expected result to false on 18/08/2021 //////////////
        logMessage("Item N29.4: Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10061, 10061, driver), false, "Failed in Item N29.4: Sync Changes");

		Thread.sleep(20000); 
        SystemLibrary.logMessage("Item N29.5: Add API configuration", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver),true, "Failed in Item N29.5: Add API configuration.");

		Thread.sleep(300000); //Added 5 mins
        logMessage("Item N29.5.1: Reload Integration Page after Add WebAPI deatils.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.reload_Integrationpage(driver), true, "Failed in Failed in Item N29.5.1: Reload Integration Page after Add WebAPI deatils");

		Thread.sleep(300000); //Added 5 mins
        logMessage("Item N29.5.2: Validate Successfully imported 48 employee message after Add API", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.Validatesyncmessage_Main(10071, 10071, driver), true, "Failed in Failed in Item N29.5.2: Validate Successfully imported 48 employee message after Add API");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test N10351.");
        myAssert.assertAll();
    }

    @Test (priority = 10361)
    public static void testN10361_ValidateEmploymentDetailsInESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10361.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N29.6: Validate Young L Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item N29.6: Validate Young L Employment Details page.");

        logMessage("Item N29.7: Validate Freda B Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item N29.7: Validate Freda B Employment Details page.");

        signoutESS(driver);
        driver.close();
        logMessage("End of test N10361");
        myAssert.assertAll();
    }

    @Test(priority = 10371)
    public void testN10371_StopMCS_Step7_100() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test N10371.");

        logMessage("Item N30.1: Stop MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.stopMCS(), true, "Failed in Item N30.1: Stop MCS.");

        SystemLibrary.logMessage("*** End of Test N10371.");
        myAssert.assertAll();
    }

    @Test(priority = 10381)
    public static void testN10381_EditEMPdetailsINMicropay_Step7_100() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("Start Test 10381.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N30.2: Add Pay Class for KONG", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_RateDetails(10011, 10011, payrollDBName), true, "Failed in Item H30.2: Add Pay Class for KONG");

        logMessage("Item N30.3: Edit Tax Details for SAUL", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_TaxDetails(10041, 10041, payrollDBName), true, "Failed in Item H30.3: Edit Tax Details for SAUL");

        AutoITLib.exitMeridian();

        logMessage("End of Test 10381.");
        myAssert.assertAll();
    }

    @Test(priority = 10391)
    public static void testN10391_SyncChangesAfterEditDetailsInMP() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test N10391.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item N30.4: Sync Changes after Edit details in MP", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10076, 10076, driver), true, "Failed in Item N30.4: Sync Changes after Edit details in MP");

        logMessage("Item N30.4.1: Validate Integration page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item N30.4: Validate Integration page");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test N10391.");
        myAssert.assertAll();
    }

    @Test(priority = 10401)
    public void testN10401_StartMCS_Step7_105() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test N10401.");

        logMessage("Item N30.5: Start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.startMCS(), true, "Failed in Item N30.5: Start MCS.");
        Thread.sleep(150000); //Added 2.5 mins

        SystemLibrary.logMessage("*** End of Test N10401.");
        myAssert.assertAll();
    }

    @Test (priority = 10411)
    public static void testN10411_ValidateEmploymentDetailsInESS_Step6_108() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10411.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

		Thread.sleep(300000); //Added 5 mins
        logMessage("Item N30.5.1: Reload Integration Page after Add WebAPI deatils", testSerialNo);
        myAssert.assertEquals(GeneralBasic.reload_Integrationpage(driver), true, "Failed in Failed in Item N30.5.1: Reload Integration Page after Add WebAPI deatils");

        logMessage("Item N30.6: Validate Successfully imported 2 employee message after Restert MCS", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.Validatesyncmessage_Main(10081, 10081, driver),true, "Failed in Item N30.6: Validate Successfully imported 2 employee message after Restert MCS");

        logMessage("Item N30.7: Validate Peter K Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item N30.7: Validate Peter K Employment Details page.");

        logMessage("Item N30.8: Validate Janice B Tax Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item N30.8: Validate Janice B Tax Details page.");

        signoutESS(driver);
        driver.close();
        logMessage("End of test N10411");
        myAssert.assertAll();
    }

    @Test (priority = 10421)
    public static void testN10421_generatePayAdvice_Micr0pay_Step8_110() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10421.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.1: Run Leave Processing in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10101, 10101), true, "Failed in Item N31.1: Run Leave Processing in Micropay for the current pay period");

        logMessage("Item N31.2: Genarate  Auto Pay Transactions in Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.generateAutoPayMultiTransactions(10021, 10021), true, "Failed in Item N31.2: Genarate  Auto Pay Transactions in Micropay.");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.3: Produce pay advices in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiPayAdvice(10031, 10031), true, "Failed in Item N31.3: Produce pay advices in Micropay for the current pay period");

        Thread.sleep(150000);

        AutoITLib.exitMeridian();
        logMessage("End of test N10421");
        myAssert.assertAll();
    }

    @Test(priority = 10431)
    public static void testN10431_ValidatePatadvices_Step8_111() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10431");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N31.4.1: Validate pay advices in ESS for SMART - Step 111", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.4.1: Validate pay advices in ESS for SMART - Step 111");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test N10431.");
        myAssert.assertAll();
    }


    @Test(priority = 10441) //a
    public void testN10441_Backup_Micr0pay_Step8_113() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Item N10441: Backup Micr0pay");

        logMessage("Item N31.5: Back up Payroll DB - Step113.", testSerialNo);
        myAssert.assertEquals(DBManage.backupMultiDBSaveInFile(10031, 10031, testSerialNo), true, "Failed in Item N31.5: Back up Payroll DB - Step113.");

        SystemLibrary.logMessage("*** End of Item N10441: Backup Micr0pay");
        myAssert.assertAll();
    }

    @Test (priority = 10451)
    public static void testN10451_EOP_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10451.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.6:: Perform EOP in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiEOP(10021, 10021), true, "Failed in Item N31.6: Perform EOP in MicrOpay.");

        AutoITLib.exitMeridian();
        logMessage("End of test N10451");
        myAssert.assertAll();
    }

    @Test (priority = 10461)
    public static void testN10461_generatePayAdvice_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10461.");

       logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.8: Genarate  Auto Pay Transactions in Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.generateAutoPayMultiTransactions(10031, 10031), true, "Failed in Item N31.8: Genarate  Auto Pay Transactions in Micropay.");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.9: Produce pay advices in Micropay for the current pay period", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiPayAdvice(10041, 10041), true, "Failed in Item N31.9: Produce pay advices in Micropay for the current pay period");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        AutoITLib.exitMeridian();
        logMessage("End of test N10461");
        myAssert.assertAll();
    }

    @Test(priority = 10471)
    public static void testN10471_ValidatePatadvices_Step8_111() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10471.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N31.10.1: Validate pay advices in ESS for SMART - Step 115", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.10.1: Validate pay advices in ESS for SMART - Step 115");

        logMessage("Item N31.11.1: Validate pay advices in ESS for SINGELTON - Step 117", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.11.1: Validate pay advices in ESS for SINGELTON - Step 117");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test N10471.");
        myAssert.assertAll();
    }

    @Test(priority = 10481) //a
    public void testN10481_Restore_Micr0pay_Step8_119() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Item N10481: Restore Micr0pay");

        logMessage("Item N31.12: Restore Payroll DB - Step119.", testSerialNo);
        DBManage.deleteMultiDBFromFile(10031, 10031);
        myAssert.assertEquals(DBManage.restoreMultiDBFromFile(10031, 10031, testSerialNo), true, "Failed in Item N31.12: Restore Payroll DB - Step119.");

        SystemLibrary.logMessage("*** End of Item N10481: Restore Micr0pay");
        myAssert.assertAll();
    }

    @Test(priority = 10491)
    public static void testN10491_ValidatePatadvices_Step8_111() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10491.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N31.13.1: Validate pay advices in ESS for SMART - Step 120", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.13.1: Validate pay advices in ESS for SMART - Step 120");

        logMessage("Item N31.14.1: Validate pay advices in ESS for SINGELTON - Step 120", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.14.1: Validate pay advices in ESS for SINGELTON - Step 120");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test N10491.");
        myAssert.assertAll();
    }

    @Test (priority = 10501)
    public static void testN10501_EOP_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10501.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.15:: Perform EOP in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiEOP(10031, 10031), true, "Failed in Item N31.15: Perform EOP in MicrOpay.");

        AutoITLib.exitMeridian();
        logMessage("End of test N10501");
        myAssert.assertAll();
    }

    @Test(priority = 10511)
    public static void testN10511_changingGross_ExtendTimeSheetviaMicropay_Step8_121() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("Start Test N10511.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.16: changing the gross amount to add Extend Time for SINGLETON.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiExtendTimeSheetDetails(10001,10001, payrollDBName), true, "Failed in Item N31.16: changing the gross amount to add Extend Time for SINGLETON.");

        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item N31.17: Produce pay advices in Micropay for SINGLETON", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiPayAdvice(10051, 10051), true, "Failed in Item N31.17: Produce pay advices in Micropay for SINGLETON");

        AutoITLib.exitMeridian();
        logMessage("End of Test N10511");
        myAssert.assertAll();
    }

    @Test(priority = 10521)
    public static void testN10521_ValidatePayadvices_Step8_111() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test N10521.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item N31.18.1: Validate pay advices in ESS for SINGELTON - Step 122", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item N31.18.1: Validate pay advices in ESS for SINGELTON - Step 120");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test N10521.");
        myAssert.assertAll();
    }


    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item NNN", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module N - Changes to environment for Micropay test.");
    }

    //////////////// Debug here ///////////////


}
