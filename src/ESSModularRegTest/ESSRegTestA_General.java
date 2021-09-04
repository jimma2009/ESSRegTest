package ESSModularRegTest;

import Lib.*;
import PageObject.PageObj_General;
import PageObject.PageObj_NavigationBar;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.*;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.driverType;
import static Lib.SystemLibrary.logMessage;

//import static Lib.GeneralBasicHigh.configureMCS_Main;

public class ESSRegTestA_General {


    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static int moduleNo = 101;
    private static String moduleName="A";

    private static String payrollDBOrderNumber;


    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");
            if (payrollDBOrderNumber==null){
                payrollDBOrderNumber="1";
            }

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
    public void testA10011_ScenarioPrepare() throws Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Item A10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(101, 101);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item A1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item A1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item A1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item A1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item A1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item A1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item A1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item A1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item A1.7: Setup Admin user contact details.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item A10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test (priority = 10012)
    public static void testA10012_ValidateAPI_MicrOpay() throws Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("*** Start Test A10012: Validate Web API.");
        ESSSoftAssert myAssert=new ESSSoftAssert();

        WebDriver driver2=launchSageMicrOpayWebAPI(driverType);

        inputSageMicrOpayWebAPIAuthenticationDetails(testSerialNo, driver2);

        logMessage("Item A1.8.X1: Validate API - Employee Details - Include Terminated", testSerialNo);
        myAssert.assertEquals (validateAPI_MicrOpay_EmployeeDetails_Terminated_Main(20001, 20001, testSerialNo, emailDomainName, driver2), true, "Failed in Item A1.8.X1: Validate API - Employee Details - Include Terminated");

        logMessage("Item A1.8.X2: Validate API - Lookups - costaccount", testSerialNo);
        myAssert.assertEquals (validateAPI_Lookups_Main(20002, 20002, testSerialNo, emailDomainName, driver2), true, "Failed in Item A1.8.X2: Validate API - Lookups - costaccount");

        driver2.close();
        SystemLibrary.logMessage("*** End of Item A10012.");
        myAssert.assertAll();
    }

    @Test(priority = 10021) //a
    public void testA10021_1_ValidateDashboard() throws InterruptedException, IOException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //Step 1
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test A10021: Login as Super user, validate dashboard and audit report.");
        SystemLibrary.logMessage("Step 1: Log in as Super User");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Step 1.2: Validate Dashboard.");
        GeneralBasic.displayDashboard(driver);
        logMessage("Item A2.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(31, 31, testSerialNo, emailDomainName, driver), true, "Failed in Item A2.1: validate dashboard in step 2.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test A10021: Login as Super user, validate dashboard and audit report.");
        myAssert.assertAll();
    }

    //must run without headless
    @Test(priority = 10031) //a
    public void testA10031_ValidateAuditReport() throws InterruptedException, IOException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //Step 1

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test Item A10031: Login as Admin user, validate audit report.");
        SystemLibrary.logMessage("Step 1: Log in as Admin User");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage(("Step 1.4: Access 4 Audit Report. "));
        GeneralBasic.displayDashboard(driver);

        logMessage("Item A2.2.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(11, 11, emailDomainName, testSerialNo, driver), true, "Failed in Item A2.2.1: Download Profile Change Audit report.");

        logMessage("Item A2.2.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(12, 12, emailDomainName, testSerialNo, driver), true, "Failed in Item A2.2.2: Download Leave Applictions Audit report.");

        logMessage("Item A2.2.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(13, 13, emailDomainName, testSerialNo, driver), true, "Failed in Item A2.2.3: Download Maintenance Audit report.");

        logMessage("Item A2.2.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(14, 14, emailDomainName, testSerialNo, driver), true, "Failed in Item A2.2.4: Download Activation report.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** Start Test Item A10031: Login as Admin user, validate audit report.");
        myAssert.assertAll();
    }

    @Test (priority = 10041) //a
    public void testA10041_ValidateDashboardAfterBackCommand() throws Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test A10041.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        GeneralBasic.displayDashboard(driver);
        driver.navigate().back();
        logMessage("Browser Back commmand is sent.");

        SystemLibrary.logMessage("Item A2.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(31, 31, testSerialNo, emailDomainName, driver), true, "Failed in Item A2.3: Validate Dashboard after Clicking browser Back button.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10041");
        myAssert.assertAll();
    }

    @Test(priority = 10051) //a
    public void testA10051_ValidateMenu() throws InterruptedException, IOException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test A10051: Test user menu options.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,payrollDBName,  testSerialNo, emailDomainName, driver); //Log on ESS as Super User

        logMessage("Item A2.4.1", testSerialNo);
        myAssert.assertEquals(SystemLibraryHigh.validateTextInElement_Main(10011, 10011, PageObj_NavigationBar.topSecondNavaigationBar(driver),testSerialNo, emailDomainName), true, "Failed in Item A2.4.1: Validate menu option.");

        logMessage("Item A2.4.2", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displayTeamsPage(driver), 1, "Fail in Item A2.4.2: Display Teams page in Step 3.");
        myAssert.assertEquals(GeneralBasic.displayLeavesForApprovalViaNavigationBar("Admin", driver), true, "Failed in Step 3: Display Leave For Approval page.");

        SystemLibrary.logMessage("Check settings menus and pages.");

        logMessage("Item A2.4.3", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_RolesPermissions(driver), true, "Failed in Item A2.4.3: Validate Menu - Roles and Permissions.");

        logMessage("Item A2.4.4", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_Workflows(driver), true, "Failed in Item A2.4.4: Validate Menu - Workflows.");

        logMessage("Item A2.4.5", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_RedirectedApprovers(driver), true, "Failed in Item A2.4.5: Validate Menu - Redirect Approval.");

        logMessage("Item A2.4.6", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_Leave(driver), true, "Failed in Item A2.4.6: Validate Menu - Leave.");

        logMessage("Item A2.4.7", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_General(driver), true, "Failed in Item A2.4.7: Validate Menu - Integration.");

        logMessage("Item A2.4.8: Validate Menu - Whats New", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_WhatsNew(driver), true, "Failed in Item A2.4.8: Validate Menu - Whats New.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test 10051.");
        myAssert.assertAll();
    }

    @Test(priority = 10061) //a
    public void testA10061_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test A10061.");
        ESSSoftAssert myAssert = new ESSSoftAssert();

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item A3.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Faield in Item A3.1: Validate Integration page.");

        logMessage("Item A3.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_WebAPIConfigForm_Main(10031, 10031, testSerialNo, emailDomainName,  driver), true, "Faield in Item A3.2: Validate WebAPI Configuration Form in Integration page.");

        SystemLibrary.logMessage("Item A3.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(101, 101, testSerialNo, driver), true, "Failed in Item A3.3: Add New Web API Key.");

        logMessage("Restarting ESS.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName,  testSerialNo, emailDomainName, driver); //Log on ESS as Super User

        logMessage("Item A3.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Faield in Item A3.4: Validate Integration page after adding Web API.");

        logMessage("Item A3.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeIntegrationAPIKey_Main(testSerialNo, driver), true, "Failed in Item A3.5: Remove Web API Key again.");

        GeneralBasic.displayDashboard(driver);
        logMessage("Item A3.6: Validate Integration page after remove Web API.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Faield in Item A3.6: Validate Integration page after remove Web API.");

        logMessage("ReAdd Web API.");
        GeneralBasicHigh.addNewWebAPIConfiguration_Main(101, 101, testSerialNo, driver);

        logMessage("Refresh Integration page by displaying Dashboard first.");
        GeneralBasic.displayDashboard(driver);

        logMessage("Item A3.7: Validate Integration page after Re-Adding Web API.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Failed in Item A3.7: Validate Integration page after Re-Adding Web API.");

        SystemLibrary.logMessage("Item A3.8", testSerialNo);
        myAssert.assertEquals(editWebAPIConfiguration_Main(102, 102, testSerialNo, driver), true, "Failed in Item A3.8: Edit API configuration.");

        logMessage("Item A3.9: Sync All Data", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(102, 102, driver), true, "Failed in Item A3.9: Sync All Data.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10061.");
        myAssert.assertAll();
    }

    @Test (priority = 10071) //a
    public void testA10071_ReSyncAllData() throws InterruptedException, IOException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test A10071.");
        logMessage("Logon ESS as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 2.14 - 2.16
        SystemLibrary.logMessage("Item A3.10", testSerialNo);
        myAssert.assertEquals(editWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item A3.10: Edit Web API configuration.");

        logMessage("Item A3.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item A3.11: Re-Sync All Data.");

        //Step 2.17
        SystemLibrary.logMessage("Step 17: Click view more button in API Log screen.");
        GeneralBasic.displaySettings_General(driver);
        int origianlLogCount = PageObj_General.getTotalLogCount(driver);

        SystemLibrary.displayElementInView(PageObj_General.button_ViewMore(driver), driver, 10);
        PageObj_General.button_ViewMore(driver).click();
        Thread.sleep(5000);
        GeneralBasic.waitSpinnerDisappear(60, driver);
        SystemLibrary.logMessage("View More button is clicked.");

        int currentLogCount = PageObj_General.getTotalLogCount(driver);

        int newLogCount = currentLogCount - origianlLogCount;
        SystemLibrary.logMessage(newLogCount + " lines of logs are shown.");

        logMessage("Item A3.12", testSerialNo);
        myAssert.assertEquals(newLogCount, 20, "Failed in Item A3.12: Validate View More butotn and content in API log screen.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10071.");
        myAssert.assertAll();
    }

    @Test(priority = 10081) //a
    public void testA10081_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test A10081.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item A4.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item A4.1: Validate Unassigned member count.");

        SystemLibrary.logMessage("Item A4.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in ITem A4.2: Validate Unassigned Team member.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10081.");
        myAssert.assertAll();
    }

    @Test(priority = 10091) //a
    public void testA10091_ValidateRolesAndPermissionInitialStatus() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test A10091.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item A5.1", testSerialNo);
        myAssert.assertEquals(validateMultiRolesPage(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item A5.1: Validate Roles Page.");

        logMessage("Item A5.2", testSerialNo);
        myAssert.assertEquals(validatePermissionPanel_Main(10021, 10021, emailDomainName, testSerialNo, driver), true, "Failed in Item A5.2: Validate Administrator roles permissions.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10091.");
        myAssert.assertAll();
    }

    @Test(priority = 10101) //a
    public void testA10101_ConfigureAndResoreRolesPermissions() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test A10101.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item A6.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10031, 10031, driver), true, "Fail in Item A6.1: Change Role and Permissions - Change Setting to Deny.");

        SystemLibrary.logMessage("Item A6.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10032, 10032, driver), true, "Fail in Item A6.2: Change Role and Permissions - Change Maintenance to Deny.");

        SystemLibrary.logMessage("Item A6.3.1", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_RolesPermissions(driver), true, "Fail in Item A6.3.1: Display Setting - Role and Permission menu.");

        SystemLibrary.logMessage("Item A6.3.2", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_General(driver), false, "Fail in Item A6.3.2: Display Setting - Integration Menu.");

        SystemLibrary.logMessage("Item A6.3.3", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_Workflows(driver), false, "Fail in Item A6.3.3 Display Setting - Workflow Menu.");

        SystemLibrary.logMessage("Item A6.3.4", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_RedirectedApprovers(driver), false, "Fail in A6.3.4: Display Setting - Redirect Approval Menu.");

        SystemLibrary.logMessage("Item A6.3.5", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_Leave(driver), false, "Fail in A6.3.5: Display Setting - Leave Menu.");

        SystemLibrary.logMessage("Item A6.3.6: Display Setting - Whats New", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_WhatsNew(driver), true, "Fail in Item A6.3.6: Display Setting - Whats New");

        //Step 26 Check team page
        SystemLibrary.logMessage("Item A6.4", testSerialNo);
        myAssert.assertEquals(GeneralBasic.addTeam("Unassigned", driver), false, "Failed in Item A6.4: Test adding existing unassigned Team.");

        //Step 27 Restore Settings
        SystemLibrary.logMessage("Item A6.5: Restore Settings.");

        SystemLibrary.logMessage("Item A6.5.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10041, 10041, driver), true, "Failed in Item A6.5.1: Restore Settings.");

        SystemLibrary.logMessage("Item A6.5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10042, 10042, driver), true, "Failed in Item A6.5.2: Restore Settings.");

        SystemLibrary.logMessage("Item A6.5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10043, 10043, driver), true, "Failed in Item A6.5.3: Restore Settings.");

        SystemLibrary.logMessage("Item A6.5.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10044, 10044, driver), true, "Failed in Item A6.5.4: Restore Settings.");

        SystemLibrary.logMessage("Item A6.5.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(10045, 10045, driver), true, "Failed in Item A6.5.5: Restore Settings.");

        logMessage("Item A6.6", testSerialNo);
        myAssert.assertEquals(validatePermissionPanel_Main(10022, 10022, emailDomainName, testSerialNo, driver), true, "Failed in ITem A6.6: Validate Roles Permissions after Resotre settings");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("End of Test A10101.");
        myAssert.assertAll();

    }

    @Test(priority = 10111)
    public void testA10111_ValidateSidebarAndEditPreferredName() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test A10111");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item A6.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item A6.7: Search Barry and vailate side Navigation.");

        /////////////// Temp disable changes for Steve Barry by Jim on 21/04/2021 //////////////
        /*SystemLibrary.logMessage("Item A6.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(12, 12, testSerialNo, driver), true, "Failed in Item A6.8: Add Preferred Name.");
        */
        ///////////


        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10111");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10121) //a
    public void testA10121_UploadUserPhoto() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test A10121");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item A7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.uploadUserPhoto_Main(21, 21, testSerialNo, driver), true, "Failed in Item A7: Upload Photo for Steve BARRY");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10121");
        myAssert.assertAll();
    }

    /////////////// Temp disable for upload loao via TPU /////////////
    /*
    @Test (priority = 10131)
    public static void testA10131_UploadLogoViaTPUTool() throws Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("*** Start test A10131: Upload Logo Via TPU tool.");
        ESSSoftAssert myAssert = new ESSSoftAssert();

        logMessage("Item A8.1: Step 610: Upload Logo via TPU Tool.");
        logMessage("Item A8.1", testSerialNo);
        myAssert.assertEquals(uploadCompanyLogoViaTPU(testSerialNo), true, "Failed in Item A8.1: Step 610: Upload Logo via TPU Tool.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("Logon ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item A8.2: Validate Logo via Admin", testSerialNo);
        WebElement logo=SystemLibrary.waitChild("//div[@id='primary-nav']//a[@class='logo-area company-logo active']//img", 10, 1, driver);
        int errorCounter=0;
        if (logo!=null){
            if (!SystemLibraryHigh.validateScreenshotInElement_Main(20021, 20021, logo, driver)) errorCounter++;
        }else{
            errorCounter++;
        }
        myAssert.assertEquals(errorCounter, 0, "Failed in Item A8.2: Validate Logo via Admin");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test A10131.");
        myAssert.assertAll();

    }
    */
    //////

    ///////////////////////////////
    @Test(priority = 10141) //a
    public void testA10141_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start Test A10141.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item A9.1: Validate Team Page Initial Status.", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item A9.1: Validate Team Page Initial Status.");

        SystemLibrary.logMessage("Item A9.2: Validate Team Member Initial Status.", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20331, 20331, testSerialNo, emailDomainName, driver), true, "Failed in Item A9.2: Validate Team Member Initial Status.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public static void testA10151_ImportTeamViaTPU() throws Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("*** Start test A10151: Import Team via TPU.");
        ESSSoftAssert myAssert = new ESSSoftAssert();

        logMessage("Item A10: Import Team Via TPU.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath + "System_Test_Team_Import.csv"), true, "Failed in Item A10: Import Team Via TPU.");

        logMessage("*** End of test A10151");
        myAssert.assertAll();
    }

    ////// Must run without headless ////////////
    @Test(priority = 10161)
    public void testA10161_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        ESSSoftAssert myAssert = new ESSSoftAssert();
        SystemLibrary.logMessage("*** Start test A10161.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item A11: Validate Team after import team", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20341, 20341, testSerialNo, emailDomainName, driver), true, "Failed in Item A11: Validate Team after import team.");

        logMessage("Item A11.X1: Validate Maintenance Audit Report at the end of Module A.");
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(10002, 10002, emailDomainName, testSerialNo, driver), true, "Failed in Item A11.X1: Validate Maintenance Audit Report at the end of Module A.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test A10161.");
        myAssert.assertAll();
    }

    @Test (priority = 10171)
    public void testA10171_ValidateEmployeeDetailViaiPhone6Plus() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test A10171: Validate ESS pages via iPhone 6 Plus.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        WebDriver driver = GeneralBasic.launchESS(url_ESS,5);  //Iphone 6 Point resolution
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item A12.1: Validate Admin Dashboard via iPhone6 Plue.", testSerialNo);
        displayDashboard(driver);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(30001, 30001, testSerialNo, emailDomainName, driver), true, "Failed in Item A12.1: Validate Admin Dashboard via iPhone6 Plue.");

        logMessage("Item A12.2: Validate Admin Team Page via iPhone 6 Plus", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(30001, 30001, testSerialNo, emailDomainName, driver), true, "Failed in Item A12.2: Validate Admin Team Page via iPhone 6 Plus");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test A10171.");
        myAssert.assertAll();
    }


    @Test (priority = 10181)
    public void testA10181_ValidateEmployeeDetailViaiPhone6Plus() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test A10181: Test Sync Change and Sycn All running together.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        WebDriver driver = GeneralBasic.launchESS(url_ESS,5);  //Iphone 6 Point resolution
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item A12.2: Validate Admin Team Page via iPhone 6 Plus", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(30001, 30001, testSerialNo, emailDomainName, driver), true, "Failed in Item A12.2: Validate Admin Team Page via iPhone 6 Plus");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test E10021.");
        myAssert.assertAll();
    }


    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item AAA", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module A - General Function test.");
    }

    ////////////////// Debug here ////////////////////





}


