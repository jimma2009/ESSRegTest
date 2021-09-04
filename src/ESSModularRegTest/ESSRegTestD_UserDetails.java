package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.driverType;
import static Lib.SystemLibrary.logMessage;

//import static Lib.GeneralBasicHigh.configureMCS_Main;

public class ESSRegTestD_UserDetails {

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

    private static String moduleName="D";
    private static int moduleNo=104;

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
    public void testD10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item D10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(104, 104);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item D1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item D1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item D1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item D1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item D1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item D1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item D1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item D1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item D1.7: Setup Admin user contact details.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item D10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testD10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test D10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item D1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item D1.8: Add API configuration.");

        logMessage("Item D1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item D1.9: Sync All.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testD10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item D2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item D2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item D2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in ITem D2.2: Validate Tem Initial Status - Unassigned Team member.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10031.");
        myAssert.assertAll();
    }


    @Test (priority=10041)
    public static void testD10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test D10041: Import Team via TPU.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item D3: Import Team via TPU.");

        logMessage("*** End of test C10041");
        myAssert.assertAll();
    }

    /*
    @Test(priority = 10042)
    public void testD10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31168, testSerialNo, driver), true, "Failed in Item D3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10042: Add Teams and Members.");
        myAssert.assertAll();
    }


    @Test(priority = 10042)
    public void testD10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31140, testSerialNo, driver), true, "Failed in Item D3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10042: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10043)
    public void testD10043_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10043: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31141, 31150, testSerialNo, driver), true, "Failed in Item D3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10043: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10044)
    public void testD10044_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10044: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31151, 31160, testSerialNo, driver), true, "Failed in Item D3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10044: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10045)
    public void testD10045_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10045: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31161, 31168, testSerialNo, driver), true, "Failed in Item D3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10045: Add Teams and Members.");
        myAssert.assertAll();
    }
    */

    @Test(priority = 10051) //a
    public void testD10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item D4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item D4: Validate Team after import team.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10051.");
        myAssert.assertAll();
    }


    ////////////////////////
    // Start User Detail function test

    @Test(priority = 10061)
    public void testD10061_ValidateAdminUserDetail() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10061.");

        logMessage("Log on as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D5.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.1: Validate Admin Dashboard.");

        logMessage("Item D5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_WithoutSearch_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.2: Validate Admin Personal Information Page.");

        logMessage("Item D5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.3: Validate Admin Contact Details ");

        logMessage("Item D5.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.4: Validate Admin Account Settings.");

        logMessage("Item D5.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.5: Validate Admin Teams and Roles page.");

        logMessage("Item D5.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.6: Validate Admin Employment page.");

        logMessage("Item D5.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.7: Validate Admin Bank Accounts page.");

        logMessage("Item D5.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.8: Validate Admin Superannuation page.");

        logMessage("Item D5.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.9: Validate Admin Leave page.");

        logMessage("Item D5.10.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.10.1: Validate Admin Pay Advice page.");
        logMessage("Item D5.10.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20002, 20002, testSerialNo, emailDomainName, driver), true, "Failed in Item D5.10.2: Validate Admin Payment Summary page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10061.");
        myAssert.assertAll();
    }

    @Test(priority = 10071)
    public void testD10071_EditCarminCPersonalDetails() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10071.");

        logMessage("Log on as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item D6.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(31, 31, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.1: Step 85: Validate Carming C Banner.");

        SystemLibrary.logMessage("Item D6.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.2: Step 86: Validate Carming C sidebr menu.");

        SystemLibrary.logMessage("Item D6.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.3: Step 87: Validate Carming C Contact Detail page.");

        logMessage("Item D6.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmailNotificationIconInContactDetailPage_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.4: Step 88: Validate Carmin C Email Notification icon.");

        logMessage("Item D6.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(31, 31, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.5: Step 89: Edit Carmin C Work mobule number.");

        logMessage("Item D6.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item D6.6: Step 90: Validate CarmDashboard.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10071.");
        myAssert.assertAll();

    }

    @Test (priority = 10081)
    public void testD10081_SearchUserViaDirectory() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10081.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D7.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.searchUserAndDisplayPersonalInformationPageViaDirecotry_ViaAdmin_Main(10051, 10051, driver), true, "Failed in Item D7.1: Step 93: Search Robin S via Directory.");

        logMessage("Item D7.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(51, 51, testSerialNo, emailDomainName, driver), true, "Failed in Item D7.2: Step 93: Check Robin SPACEY's Banner.");

        logMessage("Item D7.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(61, 61, testSerialNo, emailDomainName, driver), true, "Failed in Item D7.3: Step 94: Validate Robin SPACEY's side Navigation Menu.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10081.");
        myAssert.assertAll();

    }

    @Test (priority = 10091)
    public void testD10091_ChangeRolePermission_FinancialToViewAndValidateUserDetails() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10091.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D8.X1: Change Financial to View.");
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(41, 41, driver), true, "Faield in Item D8.X1: Change Financial to View.");

        SystemLibrary.logMessage("Item D8.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(43, 43, emailDomainName, testSerialNo, driver), true, "Failed in Item D8.1: step 95: Change Admin Permission for Financial to View");

        SystemLibrary.logMessage("Item D8.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(71, 71, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.2: Step 96: Check SPACEY's side Navigation Menu.");
        SystemLibrary.logMessage("Item D8.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.3: Step 96: Validate SPACEY's Bank Accounts.");

        SystemLibrary.logMessage("Item D8.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.4: Step 98: Validate MONTGOMERY Superannuation.");

        SystemLibrary.logMessage("Item D8.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.5: Step 99: Validate Search Result.");

        SystemLibrary.logMessage("Item D8.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.6: Step 100: Validate Stanley B Contact Detail.");

        SystemLibrary.logMessage("Item D8.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(81, 81, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.7: Step 101: Validate Stanley B's Banner.");

        SystemLibrary.logMessage("Item D8.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(31, 31, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.8: Step 102: Validate Stanley B's Leave detail.");

        SystemLibrary.logMessage("Item D8.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(51, 51, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.9: Step 104: Validate Pansy BROWN's Contact Details");

        SystemLibrary.logMessage("Item D8.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(91, 91, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.10: Step 105: Validate User Pansy BROWN's Banner");

        SystemLibrary.logMessage("Item D8.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(61, 61, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.11: Step 106: Validate Margaret CURTIS's Contact Details.");

        SystemLibrary.logMessage("Item D8.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(62, 62, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.12: Step Step 107: Validate Johnathon D's Contact Details.");

        SystemLibrary.logMessage("Item D8.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(63, 63, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.13: Step 108: Validate Young LONG's Contact Details.");

        SystemLibrary.logMessage("Item D8.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(64, 64, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.14: Step 109: Validate Freda B's Contact Details.");

        SystemLibrary.logMessage("Item D8.15", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(65, 65, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.15: Step 110: Validate Peter KONG's Contact Details.");

        SystemLibrary.logMessage("Item D8.16", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(66, 66, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.16: Step 111: Validate Sharon ANDREW's Contact Details.");

        SystemLibrary.logMessage("Item D8.17", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item D8.17: Step 112: Validate Victoria's Personal Information.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10091.");
        myAssert.assertAll();

    }


    @Test(priority = 10101)
    public void testD10101_EditPermissionAndValidateEmployeementDetail() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10101.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item D9.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(111, 111, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.1: Step 113: Validate Tanya D's sidear menu.");

        SystemLibrary.logMessage("Item D9.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatus_Main(51, 51, driver), true, "Failed in Item D9.2: Step 114: Change Roles and Permissions - Employment to View.");

        SystemLibrary.logMessage("Item D9.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(121, 121, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.3: Step 115: Validate Tanya D's side Navigation Menu.");

        SystemLibrary.logMessage("Item D9.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.4: Step 115: Validate Tanya D's Empoyment Information.");

        SystemLibrary.logMessage("Item D9.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(12, 12, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.5: Step 116: Validate Richard Z's Empoyment Information.");

        SystemLibrary.logMessage("Item D9.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(13, 13, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.6: Step 117: Validate Ernie M's Empoyment Information.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10101.");
        myAssert.assertAll();

    }

    @Test(priority = 10121)
    public void testD10121_ValidateUserDetail_AnthonyBAndAceH() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10121.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName,  driver);

        SystemLibrary.logMessage("Item D9.7.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(131, 131,testSerialNo,  emailDomainName, driver), true, "Failed in Item D9.7.1: Step 118: Validate Anthony B's Banner.");

        SystemLibrary.logMessage("Item D9.7.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(132, 132, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.7.2: Step 118: Validate Anthony B's Personal Information.");

        SystemLibrary.logMessage("Item D9.7.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(71, 71, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.7.3: Step 119: Validate Anthony B's Contract Details.");

        SystemLibrary.logMessage("Item D9.7.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(21, 21,testSerialNo,  emailDomainName, driver), true, "Failed in Item D9.7.4: Step 120 - Step 122: Validate Employment Details.");

        SystemLibrary.logMessage("Item D9.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserBanner_Main(141, 141, testSerialNo, emailDomainName, driver), true, "Failed in Item D9.8: Step 124: Validate Ace H's Banner.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10121.");
        myAssert.assertAll();
    }

    //////////////////////////

    @Test(priority = 10131)
    public void testD10131_ChangeAndApprovePersonalDetail() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10131.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item D10.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(151, 151, testSerialNo, driver), true, "Failed in Item D10.1: Step 125: Remove Christine R's Preferred name Sue.");

        SystemLibrary.logMessage("Item D10.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item D10.2: Step 126: Search Su and validate search result.");

        SystemLibrary.logMessage("Item D10.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(5, 5, testSerialNo, emailDomainName, driver), true, "Failed in Item D10.3: Step 127: Validate My Approvals Page.");

        SystemLibrary.logMessage("Item D10.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item D10.4: Step 128: validate Other Approval Details screen.");

        SystemLibrary.logMessage("Item D10.5", testSerialNo);
        myAssert.assertEquals(GeneralBasic.approveAllOtherApproval_ViaAdmin(driver), true, "Failed in Item D10.5: Step 129: Approve all Other Approval.");

        SystemLibrary.logMessage("Item D10.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(15, 15, testSerialNo, emailDomainName, driver), true, "Failed in Item D10.6: Step 129: Validate Other Approval after approval All.");

        SystemLibrary.logMessage("Item D10.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item D10.7: Step 130: Search Su and validate search result.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10131.");
        myAssert.assertAll();

    }

    @Test(priority = 10141)
    public static void testD10141_editContactDetailsActivateUser() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test D10141: Download and Validate Pay Advice.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);
        logMessage("Log on as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D11.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item D11.1: Add Pansy B Contact Details.");

        logMessage("Item D11.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20011,20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item D11.2: Activate User Pansy B.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10141: Download and Validate Pay Advice.");

        myAssert.assertAll();
    }

    //Must run without Headless
    @Test(priority = 10142)
    public static void testD10142_DownloadAndValidatePayAdvice() throws InterruptedException, ClassNotFoundException, SQLException, IOException, Exception {
        SystemLibrary.logMessage("*** Start Test D10141: Download and Validate Pay Advice.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);
        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("Step 149: Log on user Pansy and Validate Dashbarod.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item D11.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(312, 312, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D11.3: logon as Pansy B.");

        logMessage("Item D11.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(51, 51, testSerialNo, emailDomainName, driver), true, "Failed in Item D11.4: Step 149: Log on user Pansy and Validate Dashbarod.");

        logMessage("Item D11.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiPayAdviceReport_ViaDashboard(21, 21, driver), true, "Failed in Item D11.5: Step 150: Download and Validate Pay Advice.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10142: Download and Validate Pay Advice.");

        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public static void testD10151_ValidateUserPersonalInformationViaUserNameIcon() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test 10151.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon As Admin.");
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D12.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20021,20021, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item D12.1: Activate Carmin C.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon As Carmin C.");
        SystemLibrary.logMessage("Item D12.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(321, 321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D12.2: Logon as Carmin C.");

        SystemLibrary.logMessage("Item D12.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(71, 71, testSerialNo, emailDomainName, driver), true, "Failed in Item D12.3: Step 166: Validate dashboard.");

        SystemLibrary.logMessage("Item D12.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiUserBusinessCard(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item D12.4: Step 167: Validate Robin SPACEY Business Card.");

        SystemLibrary.logMessage("Item D12.5", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displayPersonalInformationPage_ViaNavigationBarNameIcon(driver, "Carmin"), true, "Failed in Item D12.5: Step 168: Display User Profile via click User name icon");
        logMessage("Item D12.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(161, 161, testSerialNo, emailDomainName, driver), true, "Failed in Item D12.6: Step 168: Validate User Profile page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10151.");

        myAssert.assertAll();
    }

    //////////////////// Duplicate manager role ////////////////////


    @Test (priority = 10161)
    public void testD10161_ValidateManagerEllipsisDefaultMessageInRolesPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.1: Validate Ellipsis and Duplicate Manager role Popup window Description.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePopupDuplicateRole_Main(20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item Item 13.1: Validate Ellipsis and Duplicate Manager role Popup window Description.");

        logMessage("Item D13.2: Add Duplicate Manager Role 2 and Description.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20251, 20251, driver), true, "Failed in Item D13.2: Add Duplicate Manager Role 2 and Description.");

        logMessage("Item D13.3: Validate Role page after duplicating Manager Role 2 role added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiRolesPage(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.3: Validate Role page after duplicating Manager Role 2 role added.");

        logMessage("Item D13.4: Validate Manager Role and Permission page after duplicating Manager Role 2 role added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20271, 20271, emailDomainName, testSerialNo, driver), true, "Failed in D13.4: Validate Managers Role and Permission page after duplicating Manager Role 2 role added.");

        logMessage("Item D13.5: Validate Manager Role 2 Role and Permission page after duplicating Manager Role 2 role added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20276, 20276, emailDomainName, testSerialNo, driver), true, "Failed in D13.4: Validate Manager Role 2 Role and Permission page after duplicating Manager Role 2 role added.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test D10161");
        myAssert.assertAll();

    }

    @Test (priority = 10171)
    public void testD10171_EditManagerrole2AndValidateMangareRoles() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10171.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.6: Change Manager Role 2 - Emergency Contact from View to Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20281, 20281, driver), true, "Failed in Item D13.6: Change Managers Roles - Emergency Contact from View to Edit");

        logMessage("Item D13.7: Add Duplicate Manager Role 3 and Description from Manager Role 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20291, 20291, driver), true, "Failed in Item D13.7: Add Duplicate Manager Role 3 and Description from Manager Role 2.");

        logMessage("Item D13.8: Validate Manager and Permission page after Duplicate Manager Role 3 added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20301, 20301, emailDomainName, testSerialNo, driver), true, "Failed in Item D13.8: Validate Manager and Permission page after Duplicate Manager Role 3 added.");

        logMessage("Item D13.9: Validate Manager Role 2 and Permission page after Duplicate Manager Role 3 added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20302, 20302, emailDomainName, testSerialNo, driver), true, "Failed in Item D13.9: Validate Manager Role 2 and Permission page after Duplicate Manager Role 3 added.");

        logMessage("Item D13.10: Validate Manager Role 3 and Permission page after Duplicate Manager Role 3 added.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20303, 20303, emailDomainName, testSerialNo, driver), true, "Failed in Item D13.10: Validate Manager Role 3 and Permission page after Duplicate Manager Role 3 added.");

        logMessage("Item D13.11: Validate Roles report from Roles Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiRolesReport(20311, 20311, emailDomainName, testSerialNo, driver), true, "Failed in Item D13.11: Validate Roles report from Roles Page.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10171");
        myAssert.assertAll();

    }

    @Test(priority = 10181)
    public void testD10181_changeRoleAndValidateInTeamsPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10181");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.13: Validate Manager Role Drop Down in Change Role dialogue screen from Teams Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatechangeRoleAfterClickManagerRole_ViaTeamsPage_Main(30591, 30591, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.13: Validate Manager Role Drop Down in Change Role dialogue screen from Teams Page.");

        logMessage("Item D13.14: Change Martin G role from Member to Manager role 2 Via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30601, 30601, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.14: Change Martin G role from Member to Manager role 2 Via Admin");

        logMessage("Item D13.15: Validate Team G after Martin G role from Member to Manager role 2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30611, 30611, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.15: Validate Team G after Martin G role from Member to Manager role 2");

        logMessage("Item D13.15.1: Validate Team G after Martin G role from Member to Manager role 2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20476, 20476, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.15.1: Validate Team G after Martin G role from Member to Manager role 2");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10181");

        myAssert.assertAll();
    }

    @Test(priority = 10191)
    public void testD10191_changeRoleAndValidateInTeamsPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10191");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.16: Change Ernie M team role in Team That Team J from Manager to Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30621, 30621, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.16: Change Ernie M team role in Team That Team J from Manager to Member.");

        logMessage("Item D13.17: Validate That Team J after Ernie M role from Manager to Member", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30631, 30631, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.17: Validate That Team J after Ernie M role from Manager to Member");

        logMessage("Item D13.17.1: Validate Teams & Roles after Ernie M role from Manager to Member", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20481, 20481, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.17.1: Validate That Team J after Ernie M role from Manager to Member");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10191");

        myAssert.assertAll();
    }



    @Test(priority = 10201)
    public void testD10201_changeRoleAndValidateInTeamsPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10201");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.18: Change Jennifer H team role in Team H from Manager role to another Manager role - Third Mngr Role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30641, 30641, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.18: Change Jennifer H team role in Team H from Manager role to another Manager role - Third Mngr Role.");

        logMessage("Item D13.19: Validate Team H after Jennifer H role from Manager role to another Manager role - Third Mngr Role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30651, 30651, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.19: Validate Team H after Jennifer H role from Manager role to another Manager role - Third Mngr Role.");

        logMessage("Item D13.19.1: Validate Teams & Roles after Jennifer H role from Manager role to another Manager role - Third Mngr Role", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20491, 20491, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.19.1: Validate Teams & Roles after Jennifer H role from Manager role to another Manager role - Third Mngr Role");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10201");

        myAssert.assertAll();
    }

    @Test(priority = 10211)
    public void testD10211_addMemberAsManagerAndValidateInTeamsPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10211.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.20: Add Jules N to the Team F and assign as Manager via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(30661, 30661, testSerialNo, driver),  true, "Failed in Item D13.20: Add Jules N to the Team - Team F and assign as Manager via Admin");

        logMessage("Item D13.21: Validate Team F After Jules N added Team F and assign as Manager", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30671, 30671, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.21: Validate Team F After Jules N added Team F and assign as Manager");

        logMessage("Item D13.21.1: Validate Teams & Roles after Jules N added Team F and assign as Manager", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.21.1: Validate Teams & Roles after Jules N added Team F and assign as Manager");

        logMessage("Item D13.22: Validate Team Report via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30121, 30121, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.22: Validate Team Report via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10211.");
        myAssert.assertAll();

    }

    @Test (priority = 10221)
    public void testD10221_ActivateEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10221.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item D13.23: Activate Martin G", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20042, 20042, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D13.23: Martin G.");

        logMessage("Item D13.24: Activate Phantom F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(105, 105, payrollDBName, testSerialNo, emailDomainName, url_ESS,  driver), true, "Failed in Item D13.24: Activate Phantom F.");

        logMessage("Item D13.25: Activate Jennifer H", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D13.25: Activate Jennifer H");

        logMessage("Item D13.25.1: Activate Aaron M", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20481, 20481, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D13.25.1: Activate Aaron M");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10221.");
        myAssert.assertAll();
    }


    @Test (priority = 10231)
    public void testD10231_AddEmergencyContactDetails_Validate() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10231.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D13.26: Logon as Phantom F.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10161, 10161, payrollDBName,  testSerialNo, emailDomainName, driver), true, "Failed in D13.26: Logon as Phantom F.");

        logMessage("Item D13.27: Try to Add Pansy B Emgergeny Contact via Phantom F when Manager Role in View.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20491, 20491, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item 13.27: Try to Add Pansy B Emgergeny Contact via Phantom F when Manager Role in View.");

        logMessage("Item D13.28: Validate Pansy B Emgergeny Contact via Phantom F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.28: Validate Pansy B Emgergeny Contact via Phantom F.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10231.");
        myAssert.assertAll();
    }

    @Test (priority = 10241)
    public void testD10241_AddEmergencyContactDetails_Validate() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10241.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D13.29: Logon as Jennifer H First Time.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item D13.29: Logon as Jennifer H First Time.");

        logMessage("Item D13.30: Add Alec A Emgergeny Contact via Jennifer H when Manager Role in Edit.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20511, 20511, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.30: Add Alec A Emgergeny Contact via Jennifer H when Manager Role in Edit.");

        logMessage("Item D13.31: Validate Alec A Emgergeny Contact via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.31: Validate Alec A Emgergeny Contact via Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10241.");
        myAssert.assertAll();
    }

    @Test (priority = 10251)
    public void testD10251_AddEmergencyContactDetails_Validate() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10251.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D13.32: Logon as Martin G First Time.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10295, 10295, payrollDBName,  testSerialNo, emailDomainName, driver), true, "Failed in Item D13.32: Logon as Martin G First Time.");

        logMessage("Item D13.33: Add Johnathon D Emgergeny Contact via Martin G when Manager Role in Edit.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20531, 20531, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.33: Add Johnathon D Emgergeny Contact via Martin G when Manager Role in Edit.");

        logMessage("Item D13.34: Validate Johnathon D Emgergeny Contact via Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.34: Validate Johnathon D Emgergeny Contact via Martin G.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10251.");
        myAssert.assertAll();
    }

    @Test(priority = 10261)
    public void testD10261_ChangeManagerRolesRolesPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10261.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.35: Validate Team Report after the team role changes via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30131, 30131, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.35: Validate Team Report after the team role changes via Admin.");

        logMessage("Item D13.36: Change Manager - Bank Accounts to Deny", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20321, 20321, driver), true, "Failed in Item D13.36: Change Manager - Bank Accounts to Deny");

        logMessage("Item D13.37: Change Manager Role 2 - Bank Accounts to View", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20331, 20331, driver), true, "Failed in Item D13.37: Change Manager Role 2 - Bank Accounts to View");

        logMessage("Item D13.38: Change Manager Role 3 - Bank Accounts to Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20341, 20341, driver), true, "Failed in Item D13.38: Change Manager Role 3 - Bank Accounts to Edit");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10261.");
        myAssert.assertAll();

    }


    @Test (priority=10262)
    public static void testD10262_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test D10261: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D13.39: Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"Team_ManagerRole_ModuleD_Team.csv"), true, "Failed in Item D13.39: Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.");

        logMessage("*** End of Test D10261");
        myAssert.assertAll();
    }

    /*
    @Test(priority = 10263)
    public void testD10263_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10263: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.39.1:  Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31123, 31125, driver), true, "");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31169, 31180, testSerialNo, driver), true, "Failed in Item D13.39.1: Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10263: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10263)
    public void testD10263_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10263: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.39.1:  Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31123, 31125, driver), true, "");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31169, 31178, testSerialNo, driver), true, "Failed in Item D13.39.1: Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10263: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10264)
    public void testD10264_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test D10264: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31179, 31180, testSerialNo, driver), true, "Failed in Item D13.39.1: Keep Old Team and Create Three Teams Sydney Melbourne Perth and add members.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test D10264: Add Teams and Members.");
        myAssert.assertAll();
    }
    */

    @Test (priority = 10271)
    public static void testD10271_Validate_Team_Report() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10271.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D13.40: Validate Team Report after Aaron M added Manager to Sydney Melbourne Perth Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30141, 30141, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.40: Validate Team Report after Aaron M added Manager to Sydney Melbourne Perth Teams.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10271.");
        myAssert.assertAll();
    }

    @Test(priority = 10281)
    public void testD10281_ValidateBankAccountsPageForManagerRoles() throws Exception {
        SystemLibrary.logMessage("*** Start Test D10281.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item D13.41: Logon as Aaron M.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName,  testSerialNo, emailDomainName, driver), true, "Failed in Item D13.41: Logon as Aaron M");

        logMessage("Item D13.42: Validate Side Navigation Young L Bank Details page when Manager Role is Deny via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(20591, 20591, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.42: Validate Side Navigation Young L Bank Details page when Manager Role is Deny via Manager Aaron M.");

        logMessage("Item D13.42.1: Validate Young L Bank Details page when Manager Role is Deny via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20111, 20111, testSerialNo, emailDomainName, driver), false, "Failed in Item D13.42.1: Validate Young L Bank Details page when Manager Role is Deny via Manager Aaron M.");

        logMessage("Item D13.43: Validate Side Navigation Peter K Bank Details page when Manager role 2 is View via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(20593, 20593, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.43: Validate Side Navigation Peter K Bank Details page when Manager role 2 is View via Manager Aaron M.");

        logMessage("Item D13.43.1: Validate Peter K Bank Details page when Manager role 2 is View via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.43.1: Validate Peter K Bank Details page when Manager role 2 is View via Manager Aaron M.");

        logMessage("Item D13.44: Validate Side Navigation Justine M Bank Details page when Manager role 3 is Edit via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateUserSideNavigationMenu_Main(20596, 20596, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.44: Validate Side Navigation Justine M Bank Details page when Manager role 3 is Edit via Manager Aaron M.");

        logMessage("Item D13.44.1: Validate Justine M Bank Details page when Manager role 3 is Edit via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item D13.44.1: Validate Justine M Bank Details page when Manager role 3 is Edit via Manager Aaron M.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10281.");
        myAssert.assertAll();

    }

    ////////////// Duplicate manager role - Teams Roles - Conditions //////////////

    @Test(priority = 10291)
    public void testD10291_changeRoleAndValidateInTeamsRolesPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10291");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D14.1: Validate Charles L - This Team I Team and Roles Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20511, 20511, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.1: Validate Charles L - This Team I Team and Roles Page");

        logMessage("Item D14.2: Validate Manager Role Drop Down in Change Role dialogue screen from Teams Roles Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateChangeRoleTeamsElipsisMenuInTeamsAndRolesPage_Main(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.2: Validate Manager Role Drop Down in Change Role dialogue screen from Teams Roles Page.");

        logMessage("Item D14.3: Change Charles L role from Manager to Manager role 2 Via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiRole_ViaTeamsNRolesPage(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.3: Change Charles L role from Manager to Manager role 2 Via Admin");

        logMessage("Item D14.4: Validate This Team I Team and Roles Page after change Role", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.4: Validate This Team I Team and Roles Page after change Role");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10291");

        myAssert.assertAll();
    }

    @Test(priority = 10301)
    public void testD10301_changeRoleAndValidateInTeamsRolesPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test D10301");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D14.5: Validate Charles L Change Role PopUp in Teams Page after change role in Teams and Role Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatechangeMemberRoleDialogueViaTeamsPage_Main(30681, 30681, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.5: Validate Charles L Change Role PopUp in Teams Page after change role in Teams and Role Page");

        logMessage("Item D14.6: Validate Team Report after Charles L change Manager role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30151, 30151, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.6: Validate Team Report after Charles L change Manager role.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10301");

        myAssert.assertAll();
    }

    @Test(priority = 10311)
    public void testD10311_changeRoleAndValidateInTeamsRolesPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10311");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D14.7: Change Charles L team role in Teams Page from Manager to Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30691, 30691, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.7: Change Charles L team role in Teams Page from Manager to Member.");

        logMessage("Item D14.8: Validate This Team I after Charles L role from Manager to Member", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30701, 30701, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.8: Validate This Team I after Charles L role from Manager to Member");

        logMessage("Item D14.9: Validate Teams & Roles after Charles L role from Manager to Member", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20551, 20551, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.9: Validate Teams & Roles after Charles L role from Manager to Member");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10311");

        myAssert.assertAll();
    }


    @Test(priority = 10321)
    public void testD10321_changeRoleAndValidateInTeamsPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10321");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D14.10: Change Charles L role in Teams Page from Member to Manager role 2 Role", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30711, 30711, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.10: Change Charles L role in Teams Page from Member to Manager role 2 Role");

        logMessage("Item D14.11: Validate Teams & Roles after Charles L role from Member to Manager role 2 Role", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20561, 20561, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.11: Validate Teams & Roles after Charles L role from Member to Manager role 2 Role");

        logMessage("Item D14.12: Change Charles L role in Teams Page from from Manager role 2 to Manager Role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(30721, 30721, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.12: Change Charles L role in Teams Page from from Manager role 2 to Manager Role.");

        logMessage("Item D14.13: Validate This Team I after Charles L role from Manager role 2 to Manager Role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30731, 30731, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.13: Validate This Team I after Charles L role from Manager role 2 to Manager Role.");

        logMessage("Item D14.14: Validate Teams & Roles after Charles L role from Manager role 2 to Manager Role", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20571, 20571, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.14: Validate Teams & Roles after Charles L role from Manager role 2 to Manager Role");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10321");

        myAssert.assertAll();
    }

    @Test(priority = 10331)
    public void testD10331_changeRoleAndValidateInTeamsPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test D10331");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D14.15: Validate Team Report after Charles L above change roles.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30161, 30161, testSerialNo, emailDomainName, driver), true, "Failed in Item D14.15: Validate Team Report after Charles L above change roles.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10331");

        myAssert.assertAll();
    }

    //////////////////////// Test Access Right for Report /////////////////

    @Test(priority = 10341)
    public void testD10341_EditAndValidateAccessRightFor2ManagerRoles_ReportAccessDenyViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10341");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.01: Chagne the 2nd Manger role Access Rights - Reports - Deny.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20351, 20351, driver), true, "Failed in Item D15.01: Change 2nd Manager roles for Access Report - Deny");

        logMessage("Item D15.02: Validate Aaron M Team And Roles page via Admin", testSerialNo);
        myAssert.assertEquals(validate_TeamsRolesDetailsScreen_Main(20581, 20581, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.02: Validate Aaron M Team And Roles page via Admin.");

        logMessage("Item D15.03.01: Validate Ellipsis menu in Teams Lable row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30741, 30741, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.01: Validate Ellipsis menu in Teams Lable row in Team Page via Admin.");

        logMessage("Item D15.03.02: Validate Ellipsis menu in Directory row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30742, 30742, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.02: Validate Ellipsis menu in Directory row in Team Page via Admin.");

        logMessage("Item D15.03.03: Validate Ellipsis menu in Unassigned row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30743, 30743, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.03: Validate Ellipsis menu in Unassigned row in Team Page via Admin.");

        logMessage("Item D15.03.04: Validate Ellipsis menu in TEAMS I AM A MEMBER OF row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30744, 30744, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.04: Validate Ellipsis menu in TEAMS I AM A MEMBER OF row in Team Page via Admin.");

        logMessage("Item D15.03.05: Validate Ellipsis menu in OTHER TEAMS row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30745, 30745, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.05: Validate Ellipsis menu in OTHER TEAMS row in Team Page via Admin.");

        logMessage("Item D15.03.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30746, 30746, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Admin.");

        logMessage("Item D15.03.07: Validate Ellipsis menu in Other Teams:Sydney row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30747, 30747, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.07: Validate Ellipsis menu in Other Teams:Sydney row in Team Page via Admin.");

        logMessage("Item D15.03.08: Validate Ellipsis menu in Other Teams:Perth row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30748, 30748, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.08: Validate Ellipsis menu in Other Teams:Perth row in Team Page via Admin.");

        logMessage("Item D15.03.09: Validate Ellipsis menu in Other Teams:Melbourne row in Team Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30749, 30749, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.03.09: Validate via Admin. Ellipsis menu in Other Teams:Melbourne row in Team Page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10341");

        myAssert.assertAll();
    }

    @Test(priority = 10351)
    public void testD10351_ValidateAccessRightFor2ManagerRoles_ReportAccessDenyViaManagerAaronM() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10351");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon as Manager Aaron M.");
        GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.04.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30761, 30761, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.04.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30762, 30762, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30763, 30763, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30764, 30764, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30765, 30765, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30766, 30766, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30767, 30767, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.04.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30768, 30768, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.04.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.");

        logMessage("Item D15.04.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30769, 30769, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.04.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10351");

        myAssert.assertAll();
    }

    /////////////////// Change All 3 Manger roles Report Access - Deny ////////////////
    @Test(priority = 10361)
    public void testD10361_EditAll3ManagerRoles_ReportAccessDeny_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10361");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.05.01: Congfigure Manager role Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20361, 20361, driver), true, "Failed in Item D15.05.01: Congfigure Manager role Report Access Right - Deny  via Admin");

        logMessage("Item D15.05.02: Congfigure Second Mngr role Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20362, 20362, driver), true, "Failed in Item D15.05.02: Congfigure Second Mngr role Report Access Right - Deny  via Admin");

        logMessage("Item D15.05.03: Congfigure Third Mngr role Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20363, 20363, driver), true, "Failed in Item D15.05.03: Congfigure Third Mngr role Report Access Right - Deny  via Admin");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10361");

        myAssert.assertAll();
    }

    @Test(priority = 10371)
    public void testD10371_ValidateAccessRightAfterConfigAll3ManagerRoles_ReportAccessDeny_ViaManagerAaronM() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10371");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon as Manager Aaron M.");
        GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.06.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30781, 30781, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30782, 30782, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30783, 30783, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30784, 30784, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30785, 30785, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30786, 30786, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30787, 30787, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30788, 30788, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.");

        logMessage("Item D15.06.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30789, 30789, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.06.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10371");

        myAssert.assertAll();
    }

    ///////////////// Config Manager Role  - Leave Reports and Medical Emergency View, Team Report Deny /////////////
    ////////////////  Config Second Mngr Role - Team Reports and Medical Emergency View, Leave Reports Deny //////////
    ////////////////  Config Third Mngr Role - Leave Report and Team Report View but Medical Emergency Deny /////////

    @Test(priority = 10381)
    public void testD10381_ConfigAll3ManagerRoles_Viaration_NoCommonPermission_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10381");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.07.01: Congfigure Manager role  - Leave Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20371, 20371, driver), true, "Failed in Item D15.07.01: Congfigure Manager role  - Leave Report Access Right - View  via Admin.");

        logMessage("Item D15.07.02: Congfigure Manager role  - Team Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20372, 20372, driver), true, "Failed in Item D15.07.02: Congfigure Manager role  - Team Report Access Right - Deny  via Admin.");

        logMessage("Item D15.07.03: Congfigure Manager role  - Medical and Emergency Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20373, 20373, driver), true, "Failed in Item D15.07.03: Congfigure Manager role  - Medical and Emergency Report Access Right - View  via Admin.");

////////////

        logMessage("Item D15.07.04: Congfigure the Second Manager role  - Leave Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20374, 20374, driver), true, "Failed in Item D15.07.04: Congfigure the Second Manager role  - Leave Report Access Right - Deny  via Admin.");

        logMessage("Item D15.07.05: Congfigure the Second Manager role  - Team Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20375, 20375, driver), true, "Failed in Item D15.07.05: Congfigure the Second Manager role  - Team Report Access Right - View  via Admin.");

        logMessage("Item D15.07.06: Congfigure the Second Manager role  - Medical and Emergency Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20376, 20376, driver), true, "Failed in Item D15.07.06: Congfigure the Second Manager role  - Medical and Emergency Report Access Right - View  via Admin.");

////////////

        logMessage("Item D15.07.07: Congfigure the Third Manager role  - Leave Report Access Right - View ia Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20377, 20377, driver), true, "Failed in Item D15.07.07: Congfigure the Third Manager role  - Leave Report Access Right - View via Admin.");

        logMessage("Item D15.07.08: Congfigure the Third Manager role  - Team Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20378, 20378, driver), true, "Failed in Item D15.07.08: Congfigure the Third Manager role  - Team Report Access Right - View  via Admin.");

        logMessage("Item D15.07.09: Congfigure the Third Manager role  - Medical and Emergency Report Access Right - Deny via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20379, 20379, driver), true, "Failed in Item D15.07.09: Congfigure the Third Manager role  - Medical and Emergency Report Access Right - Deny  via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10381");

        myAssert.assertAll();
    }

    @Test(priority = 10391)
    public void testD10391_ValidateAccessRightAfterConfigAll3ManagerRoles_Viaration_ViaManagerAaronM() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10391");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon as Manager Aaron M.");
        GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.08.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30801, 30801, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30802, 30802, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.08.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.");

        logMessage("Item D15.08.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30803, 30803, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.08.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30804, 30804, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.08.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30805, 30805, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.08.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30806, 30806, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.08.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30807, 30807, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30808, 30808, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30809, 30809, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.");

        logMessage("Item D15.08.10.X1: Validate Ellipsis menu in Sydney Team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuWithinSingleTeamTable_Main(30861, 30861, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.10.X1: Validate Ellipsis menu in Sydney Team.");

        logMessage("Item D15.08.10.X2: Validate Ellipsis menu in Perth Team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuWithinSingleTeamTable_Main(30862, 30862, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.10.X2: Validate Ellipsis menu in Perth Team.");

        logMessage("Item D15.08.10.X3: Validate Ellipsis menu in Melbourne Team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuWithinSingleTeamTable_Main(30863, 30863, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.08.10.X3: Validate Ellipsis menu in Melbourne Team.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10391");

        myAssert.assertAll();
    }


    /////////////////// Configure All 3 Manager roles - Report Access Right Combination - Share one common Permission. /////////
    @Test(priority = 10401)
    public void testD10401_ConfigAll3ManagerRoles_Viaration_ShareOneCommonPermission_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10401");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.09.01: Congfigure Manager role  - Leave Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20391, 20391, driver), true, "Failed in Item D15.09.01: Congfigure Manager role  - Leave Report Access Right - View  via Admin.");

        logMessage("Item D15.09.02: Congfigure Manager role  - Team Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20392, 20392, driver), true, "Failed in Item D15.09.02: Congfigure Manager role  - Team Report Access Right - Deny  via Admin.");

        logMessage("Item D15.09.03: Congfigure Manager role  - Medical and Emergency Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20393, 20393, driver), true, "Failed in Item D15.09.03: Congfigure Manager role  - Medical and Emergency Report Access Right - View  via Admin.");

////////////

        logMessage("Item D15.09.04: Congfigure the Second Manager role  - Leave Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20394, 20394, driver), true, "Failed in Item D15.09.04: Congfigure the Second Manager role  - Leave Report Access Right - Deny  via Admin.");

        logMessage("Item D15.09.05: Congfigure the Second Manager role  - Team Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20395, 20395, driver), true, "Failed in Item D15.09.05: Congfigure the Second Manager role  - Team Report Access Right - Deny  via Admin.");

        logMessage("Item D15.09.06: Congfigure the Second Manager role  - Medical and Emergency Report Access Right - View  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20396, 20396, driver), true, "Failed in Item D15.09.06: Congfigure the Second Manager role  - Medical and Emergency Report Access Right - View  via Admin.");

////////////

        logMessage("Item D15.09.07: Congfigure the Third Manager role  - Leave Report Access Right - View ia Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20397, 20397, driver), true, "Failed in Item D15.09.07: Congfigure the Third Manager role  - Leave Report Access Right - View via Admin.");

        logMessage("Item D15.09.08: Congfigure the Third Manager role  - Team Report Access Right - Deny  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20398, 20398, driver), true, "Failed in Item D15.09.08: Congfigure the Third Manager role  - Team Report Access Right - Deny  via Admin.");

        logMessage("Item D15.09.09: Congfigure the Third Manager role  - Medical and Emergency Report Access Right - View via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20399, 20399, driver), true, "Failed in Item D15.09.09: Congfigure the Third Manager role  - Medical and Emergency Report Access Right - View  via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10401");

        myAssert.assertAll();
    }

    @Test(priority = 10411)
    public void testD10411_ValidateAccessRightAfterConfigAll3ManagerRoles_Viaration_ViaManagerAaronM() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10411");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon as Manager Aaron M.");
        GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.10.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30821, 30821, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.10.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30822, 30822, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.10.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.");

        logMessage("Item D15.10.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30823, 30823, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.10.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30824, 30824, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.10.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30825, 30825, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.10.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30826, 30826, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.10.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30827, 30827, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.10.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30828, 30828, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.10.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.");

        logMessage("Item D15.10.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30829, 30829, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.10.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10411");

        myAssert.assertAll();
    }

    /////////////////// Configure Manager Aaron M with Admin role
    @Test(priority = 10421)
    public void testD10421_ConfigManagerAaronMWithAdminRole_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10421");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.11: Congfigure Manager Aaron M with Admin role via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiAdministratorRole(20591, 20591, testSerialNo, driver), true, "Failed in Item D15.11: Congfigure Manager Aaron M with Admin role via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10421");

        myAssert.assertAll();
    }

    @Test(priority = 10431)
    public void testD10431_ValidateAccessRightAfterConfigManagerAaronWithAdminRole_ViaAaronM() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test D10431");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Logon as Manager Aaron M.");
        GeneralBasicHigh.logonESSMain(20121, 20121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item D15.12.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30841, 30841, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.01: Validate Ellipsis menu in Teams Lable row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30842, 30842, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.02: Validate Ellipsis menu in Directory row in Team Page via Manager Aarn M.");

        logMessage("Item D15.12.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30843, 30843, testSerialNo, emailDomainName, driver), false, "Failed in Item D15.12.03: Validate Ellipsis menu in All team members row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30844, 30844, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.04: Validate Ellipsis menu in TEAMS I AM MEMBER OF row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30845, 30845, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.05: Validate Ellipsis menu in TEAMS I MANAGE row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30846, 30846, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.06: Validate Ellipsis menu in Member:Unassigned row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30847, 30847, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.07: Validate Ellipsis menu in Manager:Sydney row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30848, 30848, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.08: Validate Ellipsis menu in Manager:Perth row in Team Page via Manager Aaron M.");

        logMessage("Item D15.12.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenuInTeamPage_Main(30849, 30849, testSerialNo, emailDomainName, driver), true, "Failed in Item D15.12.09: Validate Ellipsis menu in Manager:Melbourne row in Team Page via Manager Aaron M.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10431");

        myAssert.assertAll();
    }

    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item DDD", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module D - User Employee Detail Function test.");
    }

    ///////////////////// Debug here  ////////////////////

    @Test(priority = 10021)
    public void testD10021_WebAPIKeyAndSync_Debug() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test D10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

      /*  SystemLibrary.logMessage("Item D1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item D1.8: Add API configuration.");
*/
        //logMessage("Item D1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item D1.9: Sync All.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test D10021.");
        myAssert.assertAll();
    }




}
