package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.logMessage;

//import static Lib.GeneralBasicHigh.configureMCS_Main;

public class ESSRegTestC_UserActivation {

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

    private static String moduleName="C";
    private static int moduleNo =103;

    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNumber, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

            //////////////// New Field /////////////
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");
            testEmailNotification=getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailAddressForTestNotification");
            ///////

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 10011) //a
    public void testC10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item C10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(103, 103);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item C1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item C1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item C1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item C1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item C1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item C1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item C1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item C1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item C1.7: Setup Admin user contact details.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item C10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testC10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test C10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item C1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item C1.8: Add API configuration.");

        ////////////////////////
        logMessage("Item C1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Failed in Item C1.9: Sync All the 1st time.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testC10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item C2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item C2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item C2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in ITem C2.2: Validate Tem Initial Status - Unassigned Team member.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10031.");
        myAssert.assertAll();
    }

    @Test (priority=10041)
    public static void testC10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test C10041: Import Team via TPU.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item C3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item C3: Import Team via TPU.");

        logMessage("*** End of test C10041");
        myAssert.assertAll();
    }

    /*
    @Test(priority = 10042)
    public void testC10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31168, testSerialNo, driver), true, "Failed in Item C3.1: Add multi Members into Teams.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10042: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10042)
    public void testC10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C3.1: Add multi Members into Teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31131, 31140, testSerialNo, driver), true, "Failed in Item C3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10042: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10043)
    public void testC10043_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10043: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31141, 31150, testSerialNo, driver), true, "Failed in Item C3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10043: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10044)
    public void testC10044_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10044: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31151, 31160, testSerialNo, driver), true, "Failed in Item C3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10044: Add Teams and Members.");
        myAssert.assertAll();
    }

    @Test(priority = 10045)
    public void testC10045_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10045: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31161, 31168, testSerialNo, driver), true, "Failed in Item C3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10045: Add Teams and Members.");
        myAssert.assertAll();
    }
    */

    @Test(priority = 10051) //a
    public void testC10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item C4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item C4: Validate Team after import team.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10051.");
        myAssert.assertAll();
    }


    ////////////////////////
    // Start User Activation function test

    @Test(priority = 10061)
    public void testC10061_ActivateUsersWithCancellation() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10061: Activate Users with Cancellation.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C5.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Failed in Item C5.1: Step 68: Validate Team Sub D.");

        logMessage("Item C5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item C5.2: Step 69: Check Carming account activation status");

        SystemLibrary.logMessage("Item C5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_WithClickActionButtonOnly_ViaAdmin(10011, 10011, testSerialNo, driver), true, "Failed in Item C5.3: Step 70: Click User Carming Activation button.");

        SystemLibrary.logMessage("Item C5.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in C5.4: Step 70: Validate account activation status after click Activation button.");

        SystemLibrary.logMessage("Item C5.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.cancelActivateMultiUserAccount_ViaAdmin(10031, 10031, testSerialNo, driver), true, "Failed in Item C5.5: Step 71: Cancel Activation.");

        SystemLibrary.logMessage("Item C5.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Failed in Item C5.6: Step 70: Validate account activation status after click Cancel Activation button.");

        SystemLibrary.logMessage("Item C5.7", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10011, 10011, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item C5.7: Step 72: Validate activation email.");

        SystemLibrary.logMessage("Item C5.8: Step 73: Activate user Via Email after cancelling activation.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaEmail(10051, 10051, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item C5.8: Step 73: Activate user Via Email after cancelling activation.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10061.");
        myAssert.assertAll();
    }

    @Test(priority = 10071)
    public void testC10071_ActivateUserWithPasswordValidation() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10071.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////// Temp disable this test by Jim on 01/05/2021 /////////////
        /*
        SystemLibrary.logMessage("Item C6.1", testSerialNo);
        //myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_PasswordValidation_ViaAdmin(10061, 10061, emailDomainName, testSerialNo, url_ESS, driver), true, "Failed in Item C6.1: Step 74: Activate Carming C - Validate Password Validation 1.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("Item C6.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_PasswordValidation_ViaAdmin(10062, 10062, emailDomainName, testSerialNo, url_ESS, driver), true, "Failed in Item C6.2: Step 74: Activate Carming C - Validate Password Validation 2.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("Item C6.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_PasswordValidation_ViaAdmin(10063, 10063, emailDomainName, testSerialNo, url_ESS, driver), true, "Failed in Item C6.3: Step 74: Activate Carming C - Validate Password Validation 3.");
        */
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10071.");
        myAssert.assertAll();
    }

    @Test (priority = 10081)
    public void testC10081_ActivateUserCarminC() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10081.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("Item C7.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(10071, 10071, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C7.1: Step 74 - Step 82: Activate Carming C.");

        SystemLibrary.logMessage("Item C7.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10081, 10081, testSerialNo, emailDomainName, driver), true, "Failed in Item C7.2: Step 82: Validate Carming C Account status.");

        SystemLibrary.logMessage("Step 83: Log on ESS as Carmin CUMMINGS");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(301, 301,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item C7.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10091, 10091, testSerialNo, emailDomainName, driver), true, "Failed in Item C7.3: Step 83: Logon As Carming C and Validate Account status.");

        ////////////////
        logMessage("Step 84: Log on as Admin and Validate Carm C account setting page again.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
        SystemLibrary.logMessage("Item C7.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10101, 10101, testSerialNo, emailDomainName, driver), true, "Failed in Item C7.4: Step 84: Log on as Admin and Validate Carm C account setting page again.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10081.");
        myAssert.assertAll();
    }

    @Test(priority = 10091)
    public void testC10091_ActivateUserMultiTimes() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10091.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item C8.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSearchResult_Main(10031, 10031, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.1: Step 131: Search BR and validate search result.");

        SystemLibrary.logMessage("Item C8.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10111, 10111, testSerialNo, emailDomainName, driver), true, "Faield in Item C8.2: Step 132: Check Pansy BROWN's account activation status");

        SystemLibrary.logMessage("Item C8.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(10121, 10121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C8.3: Step 133: Activating user should fail as expected.");

        SystemLibrary.logMessage("Item C8.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(81, 81, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.4: Step 134: Add Pansy B's working email only");

        SystemLibrary.logMessage("Item C8.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.5: Step 134: Validate Pansy B's Contact detail page after adding work email.");

        SystemLibrary.logMessage("Item C8.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(82, 82, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.6: Step 135: Edit Pansy B's phone number");

        SystemLibrary.logMessage("Item C8.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(10031, 10031, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.7: Step 135: Validate Pansy B's Contact Detail page after adding phone number.");

        SystemLibrary.logMessage("Item C8.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(83, 83, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.8: Step 136: Edit Pansy B's office number");

        SystemLibrary.logMessage("Item C8.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(10041, 10041, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.9: Step 136: Validate Pansy B's Contact Detail page after adding office number.");

        SystemLibrary.logMessage("Item C8.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(10121, 10121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C8.10: Step 137: Activating user should fail as expected.");

        SystemLibrary.logMessage("Item C8.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(21, 21, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.11: Step 138: validate Other Approval Details screen.");

        SystemLibrary.logMessage("Item C8.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(100051, 100051, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.12: Step 139: Validate Contact Detail page.");

        SystemLibrary.logMessage("Item C8.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(85, 85, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C8.13: Step 140: Apporve changes of Contact Details.");

        SystemLibrary.logMessage("Item C8.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(41, 41, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C8.14: Step 141 - Step 147: ReActive user account.");

        SystemLibrary.logMessage("Step 148: Log on Pansy BROWN using incorrect password.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        SystemLibrary.logMessage("Item C8.15", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(311, 311,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), false, "Failed in Item C8.15: Step 148: Log on Pansy BROWN using incorrect password.");
        logMessage("Failed log on ESS as expected.");

        //GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10091.");
        myAssert.assertAll();

    }


    @Test(priority = 10101)
    public static void testC10101_ActivateUserWithPasswordValidation2() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10101.");

        JavaMailLib.deleteAllMail(emailDomainName);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item C9.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item C9.1: Step 151: Validate Team G members.");

        SystemLibrary.logMessage("Item C9.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_WithClickActionButtonOnly_ViaAdmin(10131, 10131, testSerialNo, driver), true, "Failed in Item C9.2: Step 152: Click User Carming Activation button only.");

        logMessage("Item C9.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10141, 10141, testSerialNo, emailDomainName, driver), true, "Failed in Item C9.3: Step 70: Validate account activation status after click Activation button.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        ///////////////// Function test disabled by Jim on 09/07/2021 /////////////////
    /*  logMessage("Item C9.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_PasswordValidation_ViaAdmin(10151, 10151, emailDomainName, testSerialNo, url_ESS, driver), true, "Failed in Item C9.4: Step 74: Activate Carming C - Validate Password Validation 1.");

        logMessage("Delete All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);
*/

        SystemLibrary.logMessage("Item C9.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(51, 51, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C9.5: Step 153: Activate user Martin GREGG.");

        SystemLibrary.logMessage("Step 154: Log on as Admin and Validate Validate Martin GREGG's account status.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C9.6: Log on as Admin and Validate Martin GREGG account status", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10161, 10161, testSerialNo, emailDomainName, driver), true, "Failed in Item C9.6: Step 154: Log on as Admin and Validate Validate Martin GREGG account status.");


        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10101.");
        myAssert.assertAll();
    }

    ////////////////// Temp Disabled by Jim on 22/07/2021 because of 24 hrs time limitation ////////////////
    /*
    @Test(priority = 10121)
    public static void testC10121_ForgetPassword() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10121.");

        JavaMailLib.deleteAllMail(emailDomainName);
        SystemLibrary.logMessage("Item C10: Step 159 - Step 165: Reset Password for Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.forgetPassword_Main(61, 61, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item C10: Step 159 - Step 165: Reset Password for Martin G.");

        SystemLibrary.logMessage("*** End of Test C10121");
        myAssert.assertAll();
    }
    */

    @Test(priority = 10131)
    public static void testC10131_ValidateCarminPersonalInformationViaUserNameIcon() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10131.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Log on as Carmin.");
        logMessage("Item C11.1: Logon ESS as Carmin C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(321, 321,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C11.1: Log on as Carmin C");

        SystemLibrary.logMessage("Item C11.2: Validate Carmin C Dashbaord.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(71, 71, testSerialNo, emailDomainName, driver), true, "Failed in Item C11.2: Step 166: Validate Carmin C dashboard.");

        SystemLibrary.logMessage("Item C11.3: Validate Carmin C Business Card.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiUserBusinessCard(81, 81, testSerialNo, emailDomainName, driver), true, "Failed in C11.3: Step 167: Validate Validate Carmin C Business Card.");

        SystemLibrary.logMessage("Step 168: Display and Validate User Profile via User name icon");
        logMessage("Item C11.4", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displayPersonalInformationPage_ViaNavigationBarNameIcon(driver, "Carmin"), true, "Failed in Item C11.4: Step 168: Display User Profile via click User name icon");
        logMessage("Item C11.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(161, 161, testSerialNo, emailDomainName, driver), true, "Failed in Item C11.5: Step 168: Validate User Profile page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10131.");

        myAssert.assertAll();
    }

    //////////////////// Function disabled by Jim on 12/07/2021 ////////////////////
    /*@Test(priority = 10141)
    public static void testC10141_ResetPassword() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10141.");

        JavaMailLib.deleteAllMail(emailDomainName);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on as Carmin.");
        GeneralBasicHigh.logonESSMain(321, 321,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C12.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.resetPassword_Main(71, 71, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C12.1: Step 169 - Step 174: Reset Password");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test C10141.");
    }*/
    //////

    @Test(priority = 10151)
    public static void testC10151_DisableAccount() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test C10151.");

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Step 175: Log on as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item C12.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10171, 10171, testSerialNo, emailDomainName, driver), true, "Failed in Item C12.2: Step 175: Validate Carming C Account Detail page after reset password.");

        logMessage("Restart ESS and logon as Pansy.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logonESSMain(312, 312,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Restart ESS and logon as Admin again.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item C13.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.disableMultiUserAccount(81, 81, testSerialNo, driver), true, "Failed in Item C13.1: Step 176: Disable a Pansy's Account. ");

        logMessage("Step 177: Sign out Admin.");
        GeneralBasic.signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        SystemLibrary.logMessage("Item C13.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(331, 331,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), false, "Failed in Item C13.2: Step 177: Pany should not log on as it is disabled as expected.");

        driver.close();
        SystemLibrary.logMessage("*** End of Test 26: Disable a User Accound.");
        myAssert.assertAll();


        logMessage("*** End of Test C10151.");

    }

    @Test(priority = 10161)
    public static void testC10161_AcivateMultiUsers() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test 10161.");

        logMessage("Delete All Mails.");
        JavaMailLib.deleteAllMail(emailDomainName);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        ////////////////////////////////////////
        boolean isActivated;
        //for(int a=107;a<=107;a++){
        int b=0;
        for(int a=101;a<=114;a++){
            b++;
            String dbLogItem="Item C14."+b;
            logMessage("Step 178 activate user in UserAccountSettings sheet row "+a);
            isActivated=GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(a, a, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
            logMessage(dbLogItem, testSerialNo);
            myAssert.assertEquals(isActivated, true, "Failed in "+dbLogItem+": Step 178: Activate User in UserAccountSettings sheet row "+a+", the first attempt.");

            ////////////// activateMultiUserAccount_ViaAdmin will activate user twice. No need reactivate in higher level. //////
            ///////// Jim fixed on 15/10/2019
            /*
            if (!isActivated){
                logMessage("Acitvate the second time.");
                isActivated=GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(a, a, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
                logMessage(dbLogItem, testSerialNo);
                myAssert.assertEquals(isActivated, true, "Failed in "+dbLogItem+": Step 178: Activate User in UserAccountSettings sheet row "+a+", the second attempt.");
            }
            */
            //////////
        }


        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("Log some users before using email function.");

        SystemLibrary.logMessage("Log on ESS as Jack once.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10011, 10011,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("Log on ESS as Sue once.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10012, 10012,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
        GeneralBasic.signoutESS(driver);
        driver.close();

        myAssert.assertAll();
        SystemLibrary.logMessage("*** End of Test 10161: Activate Multi Users..");
    }

    @Test (priority = 10171)
    public static void testC10171_ActivateAceHARRYAndChangeNotificationEmail() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C10171.");

        JavaMailLib.deleteAllMail(emailDomainName);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item C15.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_WithClickActionButtonOnly_ViaAdmin(121, 121, testSerialNo, driver), true, "Failed in Item C15.1: Step 179: Click Activate button inAce HARRY Account Setting page.");

        SystemLibrary.logMessage("Item C15.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(10061, 10061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C15.2: Step 179 - Step 180: Add Ace H Work email and office number");

        logMessage("Item C15.3: Step 181: Change Username Permission to Edit.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(10051, 10051, driver), true, "Failed in Item C15.3: Step 181: Change Username Permission to Edit.");

        logMessage("Item C15.4: Step 181: Change Email Notification Permission to Edit.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(10052, 10052, driver), true, "Failed in Item C15.4: Step 181: Change Email Notification Permission to Edit.");

        logMessage("Item C15.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10181, 10181, testSerialNo, emailDomainName, driver), true, "Failed in Item C15.5: Step 182: Validate Ace H Account Settings.");

        logMessage("Item C15.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editAccountSettings_NotificationEmail_Main(10191, 10191, testSerialNo, driver), true, "Failed in Item C15.6: Step 183: Edit default email address.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10171.");
        myAssert.assertAll();
    }

    @Test (priority = 10181)
    public static void testC10181_ActivateUserAceH() throws Exception {
        logMessage("Start test C10181.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        JavaMailLib.deleteAllMail(emailDomainName);
        SystemLibrary.logMessage("Item C16.1: Step 184: Activate Ace HARRY.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(121, 121, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C16.1: Step 184: Activate Ace HARRY.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10181.");
        myAssert.assertAll();
    }

    @Test (priority = 10182)
    public static void testC10182_ChangeUsernameForAceH() throws Exception {
        logMessage("Start test C10181.");
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        JavaMailLib.deleteAllMail(emailDomainName);
        logMessage("Item C16.2: Change ACE H Username.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editAccountSettings_UserName_Main(10201, 10201, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C16.2: Change ACE H Username.");

        logMessage("Item C16.3: Validate Emails.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(10021, 10021, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item C16.3: Validate Ace Email.");

        logMessage("Step 187: Log on As ACE using new username.");
        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item C16.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10001, 10001,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item C16.4: Step 187: Log on As ACE using new username");

        logMessage("Item C16.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(10211, 10211, testSerialNo, emailDomainName, driver), true, "Failed in Item C16.5: Step 187: Validate ACE's Account Setting page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test C10182.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test (priority = 10191)
    public static void testC10191_DownloadAndValdiateActivationReport() throws Exception {
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Start Test C10191.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Step 188: Validate Activation Report.");
        GeneralBasic.displayDashboard(driver);

        logMessage("Item C17.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(31, 31, emailDomainName, testSerialNo, driver), true, "Failed in Item C17.1: Step 188_1: Validate Activation Report.");
        logMessage("Item C17.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(32, 32, emailDomainName, testSerialNo, driver), true, "Failed in Item C17.2: Step 188_2: Validate Activation Report.");
        logMessage("Item C17.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(33, 33, emailDomainName, testSerialNo, driver), true, "Failed in Item C17.3: Step 188_2: Validate Activation Report.");

        /////////////// Disabled on 22/07/2021 by Jim ///////////
        /*
        logMessage("Item C17.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editAccountSettings_UserName_Main(10221, 10221, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item C17.4: Changing back Ace H's user name after step 187.");
        */

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test 10191");
        myAssert.assertAll();
    }

    /////////////// Jim disable this function on 13/07/2021 ////////////////////
    /*@Test (priority = 10201)
    public static void testC10201_logonAllUsers() throws Exception {
        SystemLibrary.logMessage("*** Start test C10201: log on All Users.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver;
        int a=10021;
        int b=0;
        while (a<=10271){
            b++;
            driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            String logMessageToDB="Item C18."+b;
            logMessage(logMessageToDB, testSerialNo);
            myAssert.assertEquals(GeneralBasicHigh.logonESSMain(a, a,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in "+logMessageToDB+" on user line "+a+".");
            GeneralBasic.signoutESS(driver);
            driver.close();

            a=a+10;
        }

        logMessage("*** End of Test C10201.");
        myAssert.assertAll();
    }*/

    @Test(priority = 10211)
    public void testC10211_LogonESSUsingInvalidUsername() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test C20211.");

        logMessage("Item C19: Logon ESS using invalide Username.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(30001, 30001,  payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), false, "Failed in Item C19: Logon ESS using invalide Username.");

        driver.close();
        SystemLibrary.logMessage("*** End of Test C20211.");
        myAssert.assertAll();
    }


    ///////////////////////////
    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item CCC", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module C - User Activation test.");
    }

    /////////////////// Debug here ///////////////



}
