package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.*;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.MCSLib.restartMCS;
import static Lib.SystemLibrary.*;

public class ESSRegTestH_NewStarter {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static String payrollDB2Name;
    private static String comDB2Name;
    private static int moduleNo = 108;
    private static String payrollDBOrderNumber;
    private static String payrollDB2OrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName="H";

    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            payrollDB2Name = getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDB2Name");
            comDBName = getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            comDB2Name = getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDB2Name");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");
            if (payrollDBOrderNumber==null){
                payrollDBOrderNumber="1";
            }

            payrollDB2OrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDB2OrderNo");
            if (payrollDB2OrderNumber==null){
                payrollDB2OrderNumber="1";
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
    public void testH10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item H10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item H1.4: Delete and Restore Payroll and Common DB.");
        DBManage.deleteAndRestoreSageMicrOpayDB(payrollDBName, comDBName, emailDomainName);

        //Assing the rights to DB after restoring.
        if ((payrollDBName.equals("ESS_Auto_Payroll1")) && (comDBName.equals("ESS_Auto_COM1"))) {
            DBManage.sqlExecutor_Main(201, 201);
            DBManage.sqlExecutor_Main(202, 202);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll2")) && (comDBName.equals("ESS_Auto_COM2"))) {
            DBManage.sqlExecutor_Main(401, 401);
            DBManage.sqlExecutor_Main(402, 402);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll3")) && (comDBName.equals("ESS_Auto_COM3"))) {
            DBManage.sqlExecutor_Main(411, 411);
            DBManage.sqlExecutor_Main(412, 412);
        }
        DBManage.logTestResultIntoDB_Main("Item H1.4", "pass", null, testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item H1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1")) && (comDBName.equals("ESS_Auto_COM1"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll2")) && (comDBName.equals("ESS_Auto_COM2"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll3")) && (comDBName.equals("ESS_Auto_COM3"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item H1.5", "pass", null, testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item H1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1")) && (comDBName.equals("ESS_Auto_COM1"))) {
            DBManage.sqlExecutor_Main(1011, 1011);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll2")) && (comDBName.equals("ESS_Auto_COM2"))) {
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3")) && (comDBName.equals("ESS_Auto_COM3"))) {
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item H1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item H1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item H1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item H10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test (priority = 10012)
    public static void testH10012_ValidateAPI_MicrOpay() throws Exception {
        logMessage("*** Start Test H10012 - Validate Web API.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver2=launchSageMicrOpayWebAPI(driverType);

        inputSageMicrOpayWebAPIAuthenticationDetails(testSerialNo, driver2);

        logMessage("Item H1.8.X1: Validate API - Employee Details - Include Terminated", testSerialNo);
        myAssert.assertEquals (validateAPI_MicrOpay_EmployeeDetails_Terminated_Main(20001, 20001, testSerialNo, emailDomainName, driver2), true, "Failed in Item H1.8.X1: Validate API - Employee Details - Include Terminated");

        logMessage("Item H1.8.X2: Validate API - Lookups - costaccount", testSerialNo);
        myAssert.assertEquals (validateAPI_Lookups_Main(20002, 20002, testSerialNo, emailDomainName, driver2), true, "Failed in Item H1.8.X2: Validate API - Lookups - costaccount");

        driver2.close();
        logMessage(("*** End of Test H10012."));
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testH10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test H10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item H1.8: Add API configuration", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item H1.8: Add API configuration.");

        logMessage("Item H1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Failed in Item H1.9: Sync All the 1st time.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test H10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testH10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test H10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item H2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item H2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item H2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item H2.2: Validate Team Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test H10031.");
        myAssert.assertAll();
    }


    @Test(priority = 10041)
    public static void testH10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start test H10041: Import Team via TPU.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item H3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath + "System_Test_Team_Import.csv"), true, "Failed in Item H3: Import Team via TPU.");

        logMessage("*** End of test H10041");
        myAssert.assertAll();
    }



    @Test(priority = 10051)
    public void testH10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item H4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item H4: Validate Team after import team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10051.");
        myAssert.assertAll();
    }

    @Test(priority = 10061)
    public void testH10061_Add2PayrollCompaniesViaMicrOpay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10061.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H5.1: Add a second Payroll Company.");
        logMessage("Item H5.1", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.addMultiPayrollCompany(20001, 20001), true, "Failed in Item H5.l: Add a second Payroll Company");

        logMessage("Item H5.2: Add a Third Payroll Company.");
        logMessage("Item H5.2", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.addMultiPayrollCompany(20002, 20002), true, "Failed in Item H5.2: Add a third Payroll Company");

        AutoITLib.exitMeridian();

        SystemLibrary.logMessage("*** End of test H10061.");
        myAssert.assertAll();
    }


    @Test(priority = 10071)
    public void testH10071_SyncAndAssignJenniferHAsAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10071.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000);

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H6: Sync All data after adding a new Payroll Companies in MicrOpay.");
        logMessage("Item H6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item H6: Sync All data after adding a new Payroll Companies in MicrOpay.");

        logMessage("Item H7: Activate Jennifer H.");
        logMessage("Item H7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item H7: Activate Jennifer H.");

        logMessage("Item H8: Configure Admin Role - New Starters - from Deny to Edit");
        logMessage("Item H8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20041, 20041, driver), true, "Failed in Item H8: Configure Admin Role - New Starters - from Deny to Edit");

        logMessage("Item H9: Assign Jennifer H as Admin user.");
        logMessage("Item H9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiAdministratorRole(20091, 20091, testSerialNo, driver), true, "Failed in Item H9: Assign Jennifer H as Admin user.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10071.");
        myAssert.assertAll();
    }

    @Test(priority = 10081)
    public void testH10081_AddNewStarterGraceJ() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10081.");

        logMessage("Delete All Emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on ESS as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        /////////////// Here is a known issue for second Admin role who need resync to get payroll company populated. Work around is re-sync.
        logMessage("Item H10: Sync All the 2nd time via Jennifer H", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item H10: Sync All the 2nd time via Jennifer H" );

        logMessage("Item H11: Valdiate Team page via Jennifer H", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item H11: Valdiate Team page via Jennifer H");

        logMessage("Item H12: Validate New Starter Team via Jennifer H.");
        logMessage("Item H12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item H12: Validate New Starter Team via Jennifer H.");

        logMessage("Check if payroll comany is sync successfully.");
        if (!GeneralBasicHigh.validateIntegrationPage_Main(20061, 20061, testSerialNo, emailDomainName, driver)){
            logWarning("Sync all the 3rd time.");
            logMessage("Item H10: Sync All the 3nd time via Jennifer H", testSerialNo);
            myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item H10: Sync All the 3nd time via Jennifer H" );
        }

        logMessage("Item H13: Add new starter Grace J", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(101, 101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H13: Add new starter Grace J");

        logMessage("Item H14: Validate New Starter Team via Jennifer H after add the 1st new starter Grace J.");
        logMessage("Item H14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item H14: Validate New Starter Team via Jennifer H after add the 1st new starter Grace J.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10081.");
        myAssert.assertAll();
    }


    @Test(priority = 10091)
    public void testH10091_ActivateNewStarterGraceJ() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10091.");

        logMessage("Item H15: Validate Grace J email content.");
        logMessage("Item H15", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20301, 20301, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H15: Validate Grace J email content.");

        logMessage("Item H16: Activate New Starter via Email");
        logMessage("Item H16", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20031, 20031, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H16: Activate New Starter vis Email");

        SystemLibrary.logMessage("*** End of test H10091.");
        myAssert.assertAll();
    }

    @Test (priority=10101)
    public static void testH10101_FillNewStarterFormAfterActivatingNewStarter() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10101.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item H17.1: Log on New Starter Grace J the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.1: Log on New Starter Grace J the first time and Valdiate Welcome page.");

        logMessage("log out New Starter.");
        GeneralBasic.signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item H17.2: log on ESS as Grace J again without validating welcome message", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.2: log on ESS as Grace J again without validating welcome message.");

        logMessage("Item H17.3: Fill in New Starter Grace J Personal Information", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.3: Fill in New Starter Grace J Personal Information.");

        logMessage("Item H17.4: Fill in New Starter Grace J Contact Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.4: Fill in New Starter Grace J Contact Details");

        logMessage("Item H17.5: Validate New Starter Grace J Employment", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.5: Validate New Starter Grace J Employment");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10101");
        myAssert.assertAll();
    }

    @Test (priority=10111)
    public static void testH10111_ValidateNewStarterTaxDetailsForm() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10111.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item H17.6: log on ESS as Grace J again without validating welcome message", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.6: log on ESS as Grace J again without validating welcome message");

        logMessage("Item H17.7.1: Validate New Starter Tax Detail - I have a tax file number", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.7.1: Validate New Starter Tax Detail - I have a tax file number");

        logMessage("Item H17.7.2: Validate New Starter Tax Detail - I have or will provide my TFN", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20002, 20002, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.7.2: Validate New Starter Tax Detail - I have or will provide my TFN");

        logMessage("Item H17.7.3: Validate New Starter Tax Detail - I have made a separate application to the ATO", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20003, 20003, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.7.3: Validate New Starter Tax Detail - I have made a separate application to the ATO");

        logMessage("Item H17.7.4: Validate New Starter Tax Detail - I am claiming an exemption bacause I am under 18 years of age", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20004, 20004, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.7.4: Validate New Starter Tax Detail - I am claiming an exemption bacause I am under 18 years of age");

        logMessage("Item H17.7.5: Validate New Starter Tax Detail - I am claminng an exemption because I am in receipt of a pension", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20005, 20005, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H17.7.5: Validate New Starter Tax Detail - I am claminng an exemption because I am in receipt of a pension");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10111");
        myAssert.assertAll();
    }

    @Test (priority=10121)
    public static void testH10121_EditNewStarterTaxDetailsForm() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10121.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H18: Edit New Starter Tax Detail  - I have or will provide my TFN", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H18: Edit New Starter Tax Detail  - I have or will provide my TFN");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10121");
        myAssert.assertAll();
    }


    ///////////////////////////////

    @Test (priority=10131)
    public static void testH10131_ValidateAndEditNewStarterSuperannuationForm() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10131.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        //WebDriver driver = GeneralBasic.launchESS(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H19.1: Validate New Starter - Superannuation - The Super fund nominated by my employer", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H19.1: Validate New Starter - Superannuation - The Super fund nominated by my employer");

        logMessage("Item H19.2: Validate New Starter - Superannuation - The APRA fund or RSA I nominatee", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20002, 20002, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H19.2: Validate New Starter - Superannuation - The APRA fund or RSA I nominatee");

        logMessage("Item H19.3: Validate New Starter - Superannuation - The self-managed Super fund (SMSF) I nominate.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20003, 20003, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H19.3: Validate New Starter - Superannuation - The self-managed Super fund (SMSF) I nominate.");

        logMessage("Item H20: Edit New Starter - Superannuation", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H20: Edit New Starter - Superannuation");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10131");
        myAssert.assertAll();
    }

    @Test (priority=10141)
    public static void testH10141_EditNewStarterBankAccount() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10141.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        //WebDriver driver = GeneralBasic.launchESS(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H21: Edit New Starter - Bank Account", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20001, 20001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H21: Edit New Starter - Bank Account");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10141");
        myAssert.assertAll();
    }

    @Test (priority=10151)
    public static void testH10151_ValidateNewStarterSummary() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10151.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        //WebDriver driver = GeneralBasic.launchESS(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H22.1: Validate New Starter - Summary", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20001, 20002, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H22.1: Validate New Starter - Summary");

        logMessage("Item H22.2: Complete New Starter - Summary without Consent", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H22.2: Complete New Starter - Summary without Consent");

        //////////// Restart ESS ////////
        logMessage("Restart ESS...");
        GeneralBasic.signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        //WebDriver driver = GeneralBasic.launchESS(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);
        //////

        logMessage("Item H22.3: Validate Confirm form and Cancel Submit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20012, 20012, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H22.3: Validate Confirm form and Cancel Submit");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10151");
        myAssert.assertAll();
    }


    @Test (priority=10161)
    public static void testH10161_CompleteNewStarterSummary() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10161.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("log on ESS as Grace J again without validating welcome message");
        GeneralBasicHigh.logonESSAsNewStarter_Main(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H22.4: Complete New Starter Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H22.4: Complete New Starter Summary");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10161");
        myAssert.assertAll();
    }

    @Test (priority=10171)
    public static void testH10171_LogonAsNewStarterAfterCompleteSummary() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10171.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item H22.5: log on ESS as Grace J again after comleting new sarter.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item H22.5: log on ESS as Grace J again after comleting new sarter.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10171");
        myAssert.assertAll();
    }

    @Test (priority = 10172)
    public static void testH10172_ValdiateEmailAfterCompleteSummary() throws Exception{
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start test 10172.");

        logMessage("Item H22.6: Valdate Grace J email after complete Summary.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20303, 20303, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H22.6: Valdate Grace J email after complete Summary.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("End of test H10172");
        myAssert.assertAll();
    }

    @Test (priority=10181)
    public static void testH10181_ValidateNewStarterDetails() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10181.");

        logMessage("Log on ESS as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H23: Validate Jennifer H Dashboard after completing new starter Summary.", testSerialNo);
        myAssert.assertEquals(validateDashBoard_Main(20411, 20411, testSerialNo, emailDomainName, driver), true, "Falied in Item H23: Validate Jennifer H's Dashboard after completing new starter summary.");

        logMessage("Item H24: Validate new starter item in Team page.", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item H24: Validate new starter item in Team page.");

        logMessage("Item H25: Validate new starter Grace J Detail page.", testSerialNo);
        myAssert.assertEquals(validateNewStarterDetails_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item H25: Validate new starter Grace J details.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10181");
        myAssert.assertAll();
    }

    @Test (priority=10191)
    public static void testH10191_SendNewStarterToPayroll() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10191.");

        logMessage("Log on ESS as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H26: Send new starter Grace J to Payroll.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20011, 20011, testSerialNo, emailDomainName, driver), true, "Falied in Item H26: Send new starter Grace J to Payroll.");

        logMessage("Item H27: Validate Team New Starter status after send payroll via Jennifer H.", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item H27: Validate Team New Starter status after send payroll via Jennifer H.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10191");
        myAssert.assertAll();
    }

    @Test (priority=10201)
    public static void testH10201_EditNewStarterInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10201.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H28: Edit New Employee - Grace J and send to Payroll.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee(20001, 20001), true, "Falied in Item H28: Edit New Employee - Grace J and send to Payroll.");

        AutoITLib.exitMeridian();
        logMessage("End of test H10201");
        myAssert.assertAll();
    }


    ////////////////// Need restart MCS? ///////////////
    @Test (priority=10211)
    public static void testH10211_ValdiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10211.");

        Thread.sleep(300000); //Delay 5 mins after change made in MicrOpay, Jim on 07/07/2020
        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H29: Print and Validate Grace J Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20061, 20061, emailDomainName, testSerialNo), true, "Falied in Item H29: Print and Validate Grace J Employee Detail Report.");

        AutoITLib.exitMeridian();
        logMessage("End of test H10211");
        myAssert.assertAll();
    }

    @Test (priority = 10221)
    public static void testH10221_SyncAllAndValidateNewStarterStatusAfterSendNewStarterToPayroll() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10221.");

        logMessage("Log on ESS as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H30: Sync All Data via Jennifer H after Sending New Starter to Payroll.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main (10011, 10011, driver), true, "Falied in Item H30: Sync All Data via Jennifer H after Sending New Starter to Payroll.");

        logMessage("Item H31: Validate Team New Starter status after Send to Payroll and Sync All data.", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20251, 20251, testSerialNo, emailDomainName, driver), true, "Failed in Item H31: Validate Team New Starter status after Send to Payroll and Sync All data.");

        logMessage("Item H32: Validate New Starter Detail before Finalise.", testSerialNo);
        myAssert.assertEquals(validateNewStarterDetails_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item H32: Validate New Starter Detail Before Finalise.");

        logMessage("Item H33: Duplicate the Member Role to Member Terminated.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20121, 20121, driver), true, "Failed in Item H33: Duplicate the Member Role to Member Terminated.");

        logMessage("Item H34: Duplicate the Member Terminated role to Leave For Manager.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20131, 20131, driver), true, "Failed in Item H34: Duplicate the Member Terminated role to Leave For Manager.");

        logMessage("Item H35: Validate Role page after duplicating the Member Terminated role.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiRolesPage(20132, 20132, testSerialNo, emailDomainName, driver), true, "Failed in Item H35: Valdiate Role page after duplicating the Member Terminated role.");

        logMessage("Item H36: Validate Leave for Manager Roles Permission.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20133, 20133, emailDomainName, testSerialNo, driver), true, "Failed in Item H36: Validate Member Terminated Roles Permission.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10221");
        myAssert.assertAll();
    }

    @Test (priority = 10222)
    public static void testH10222_FinaliseNewStarter() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10222.");

        logMessage("Item H37.x1: Restart MCS");
        MCSLib.restartMCS();
        Thread.sleep(300000); //Delay 5 mins, Jim adjusted on 07/07/2020

        logMessage("Log on ESS as Jennifer H");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H37: Finalise New Starter GRACE J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H37: Finalise New Starter GRACE J.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10222");
        myAssert.assertAll();
    }

    @Test (priority = 10231)
    public static void testH10231_ValidateNewStarterDetail_1_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10231.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H38.1: Validate New Starter GRACE J Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20321, 20321, testSerialNo, emailDomainName, driver), true, "Failed in Item H38.1: Validate New Starter GRACE J Personal Information page.");

        logMessage("Item H38.2: Validate New Starter GRACE J Contact Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20201, 20201, testSerialNo, emailDomainName, driver), true, "Failed in Item Item H38.2: Validate New Starter GRACE J Contact Details page");

        logMessage("Item H38.3: Validate New Starter GRACE J Account Settings page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item H38.3: Validate New Starter GRACE J Account Settings page.");

        logMessage("Item H38.4: Validate New Starter Grace J Teams and Roles page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item H38.4: Validate New Starter Grace J Teams and Roles page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10231");
        myAssert.assertAll();
    }

    @Test (priority = 10241)
    public static void testH10241_ValidateNewStarterDetail_2_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10241.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H38.5: Change roles and Permisson Employment, Bank and Pay Salary Details to view for Admin user", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20141, 20143, driver), true, "Failed in Item H38.5: Change roles and Permisson Employment, Bank and Pay Salary Details to view for Admin user");

        logMessage("Item H38.6: Validate New Starter Grace J Bank Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item H38.4: Validate New Starter Grace J Bank Details page.");

        logMessage("Item H38.7: Validate New Starter Grace J Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item H38.4: Validate New Starter Grace J Bank Details page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10241");
        myAssert.assertAll();
    }

    //////////////////// New Test Case  ////////////////////

    @Test (priority = 10251)
    public static void testH10251_NewStarterPermissionTest() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10251.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H39: Change Admin Permission - New Starter to View", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20101, 20101, driver), true, "Failed in Item H39: Change Admin Permission - New Starter to View");


        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10251");
        myAssert.assertAll();
    }

    @Test (priority = 10261)
    public static void testH10261_Validate_Team_NewStarterpage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10261.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H40: Validate Admin Permission - After changeing New Starter to View.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item H40: Validate Admin Permission - After changeing New Starter to View.");

        logMessage ("Item H41: Validate Admin Dashboard after changing New Starter to View.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20446, 20446, testSerialNo, emailDomainName, driver), true, "Failed in Item H41: Validate Admin Dashboard after changing New Starter to View");

        logMessage("Item H42.1: Validate Team - New Starters page after change new starter to View.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item H42.1: Validate Team - New Starters page after change new starter to View.");

        logMessage("Item H42.2: Validate the button - Add New Starter in Team - New Starter page.", testSerialNo);
        WebElement button_AddNewStarter=SystemLibrary.waitChild("//i[@class='icon-add']", 30, 1, driver);
        myAssert.assertEquals((button_AddNewStarter==null), true, "Failed in Item H42.2: Validate the button - Add New Starter in Team - New Starter page." );

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10261");
        myAssert.assertAll();
    }

    @Test (priority = 10271)
    public static void testH10271_ChangeRolePermission_NewStarterToEdit() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10271.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H43: Change Admin Permission - New Starter to Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20151, 20151, driver), true, "Failed in Item H43: Change Admin Permission - New Starter to Edit");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10271");
        myAssert.assertAll();
    }

    @Test (priority = 10281)
    public static void testH10281_Validate_Team_NewStarterPageAfterChangePermissionToEdit() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10281.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H44: Validate Team - New Starters page after change new starter to Edit.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item H44: Validate Team - New Starters page after change new starter to Edit.");

        logMessage("Item H45: Validate the button - Add New Starter in Team - New Starter page.", testSerialNo);
        WebElement button_AddNewStarter=SystemLibrary.waitChild("//i[@class='icon-add']", 30, 1, driver);
        myAssert.assertEquals((button_AddNewStarter!=null), true, "Failed in Item H45: Validate the button - Add New Starter in Team - New Starter page." );

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10281");
        myAssert.assertAll();
    }


    @Test (priority = 10301)
    public static void testH10301_TestSuperFundInIntegrationPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10301.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H46: Validate Super Fund in Integration Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_SuperFund_Main(10001, 10001, testSerialNo, emailDomainName, driver), true, "Failed in Item H46: Validate Super Fund in Integration Page");

        logMessage("Item H47: Add Super Fund PDS", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiSuperPDSFileInIntegrationPage(10011, 10011, testSerialNo, emailDomainName, driver ), true, "Failed in Item H47: 17 Add Super Fund PDS");

        logMessage("Item H48: Validate Super Fund in Integration Page after adding Super Fund PDF.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_SuperFund_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item H48: Validate Super Fund in Integration Page after adding Super Fund PDF.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10301");
        myAssert.assertAll();
    }

    @Test (priority = 10311)
    public static void testH10311_ValidateAddNewStarterItem() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10311.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H49.1: Add new starter Tommy J - Invalidate email address", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(111, 111, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item H49.1: Add new starter Tommy J - Invalidate email address.");

        logMessage("Item H49.2: Add new starter Tommy J - Without Basis of Employment and Payroll Company.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(112, 112, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item H49.2: Add new starter Tommy J - Without Basis of Employment and Payroll Company.");

        //Step 37
        logMessage("Item H49.3: Add New Starter with incorrect reporting manager name", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(113, 113, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item H49.3: Add New Starter with incorrect manager name");

        ////////////////////////
        logMessage("Item H49.4: Add New Starter with Pay Frequency 2 weekly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(114, 114, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.4: Add New Starter with incorrect manager name");

        logMessage("Item H49.5: Add New Starter with Pay Frequency 4 weekly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(115, 115, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.5: Add New Starter with Pay Frequency 4 weekly");

        logMessage("Item H49.6: Add New Starter with Pay Frequency Bi-monthly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(116, 116, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.6: Add New Starter with incorrect manager name");

        logMessage("Item H49.7: Add New Starter with Pay Frequency Fortnightly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(117, 117, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.7: Add New Starter with incorrect manager name");

        logMessage("Item H49.8: Add New Starter with Pay Frequency Monthly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(118, 118, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.8: Add New Starter with incorrect manager name");

        logMessage("Item H49.9: Add New Starter with Pay Frequency Weekly", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(119, 119, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.9: Add New Starter with incorrect manager name");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10311");
        myAssert.assertAll();

    }

    @Test (priority = 10321)
    public static void testH10321_AddNewStarterItemAndValidateSummary() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10321.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H50: Add New Starter Tommy J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(121, 121, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H49.9: Add New Starter Tommy J.");

        Thread.sleep(5000);
        logMessage("Item H51: Remove new starter Tommy J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiNewStartersViaAdmin(131, 131, emailDomainName, testSerialNo, driver), true, "Failed in Item H51: Remove new starter Tommy J.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10321");
        myAssert.assertAll();

    }


    @Test (priority = 10331)
    public static void testH10331_AddNewStarterTommyJThe2ndTimesAndActivateTommyJ() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10331.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H52: Add New Starter Tommy J the 2nd Times.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(121, 121, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H52: Add New Starter Tommy J the 2nd times.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("Item H53: Validate Tommy J email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20781, 20781, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H53: Validate Tommy J email content.");

        logMessage("Item H54: Activate New Starter Tommy J via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20081, 20081, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H54: Activate New Starter Tommy J via Email");

        logMessage("End of test H10331");
        myAssert.assertAll();
    }

    @Test (priority=10341)
    public static void testH10341_LogonNewStarterTommyJThe1stTime() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10341.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item H55: Log on New Starter Tommy J the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H55: Log on New Starter Tommy J the first time and Valdiate Welcome page.");

        logMessage("log out New Starter.");
        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("*** End of test H10341");
        myAssert.assertAll();
    }


    @Test (priority=10351)
    public static void testH10351_ValidateNewStarterPersonalInfoFormMandatoryItemForTommyJ() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10351.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H56: Validate New Starter Tommy J Persoanl Info page without input value and click Save button.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_PersonalInformation(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H56: Validate New Starter Tommy J Persoanl Info page without input value and click Save button.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10351");
        myAssert.assertAll();
    }

    @Test (priority=10361)
    public static void testH10361_FillInNewStarterPersonalInfoFormForTommyJ() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10361.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H57: Edit New Starter Tommy J Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H57: Edit New Starter Tommy J Persoanl Info form ");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10361");
        myAssert.assertAll();
    }

    /////////////////////
    @Test (priority=10371)
    public static void testH10371_ValidateNewStarterContactDetailsForTommyJAfterInputCountryAustra() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10371.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H58: Validate New Starter Tommy J Contact Details Form after input Contry - Austra.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H58: Validate New Starter Tommy J Contact Details Form after input Contry - Austra");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10371");
        myAssert.assertAll();
    }

    @Test (priority=10381)
    public static void testH10381_ValidateNewStarterContactDetailsForTommyJAfterInputCountryAustralia() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10381.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H59: Validate New Starter Tommy J Contact Details Form after input Contry - Australia and Suburb.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H59: Validate New Starter Tommy J Contact Details Form after input Contry - Australia and Suburb.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10381");
        myAssert.assertAll();
    }

    @Test (priority=10391)
    public static void testH10391_ValidateNewStarterContactDetailsForTommyJAfterSelectCheckboxUseForPostalAddress() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10391.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H60: Validate New Starter Tommy J Contact Details Form after selecting checkbox - Use for postal address.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20032, 20032, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H60: Validate New Starter Tommy J Contact Details Form after selecting checkbox - Use for postal address.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10391");
        myAssert.assertAll();
    }

    @Test (priority=10401)
    public static void testH10401_EditNewStarterTommyJDetails() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10401.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H61: Edit New Starter - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20033, 20033, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H61: Edit New Starter - Contact Details and Save");

        logMessage("Item H62: Validate New Starter Tommy J Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H62: Validate New Starter Tommy J Employeement Form and continue");

        logMessage("Item H63: Validate New Starter Tommy J Tax Detail - I have a tax file number", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H63: Validate New Starter Tommy J Tax Detail - I have a tax file number");

        logMessage("Item H64: Add New Starter Tommy J Tax Detail for the Option I have a tax file number", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20026, 20026, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H64: Add New Starter Tommy J Tax Detail for the Option I have a tax file number");

        logMessage("Item H65: Validate New Starter Tommy J Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H65: Item H65: Validate New Starter Tommy J Superannuation form ");

        logMessage("Item H66: New Starter Tommy J Superannuation form - Check TFN ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20026, 20026, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H66: New Starter Tommy J Superannuation form - Check TFN");

        logMessage("Item H67: Validate New Starter Tommy J - Bank Account", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_BankAccount(20011, 20011, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H67: Validate New Starter Tommy J - Bank Account");

        logMessage("Item H68: Add New Starter Tommy J - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H68: Add New Starter Tommy J - Bank Account Details");

        logMessage("Item H69: Validate New Starter Tommy J Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20026, 20026, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H69: Validate New Starter Tommy J Summary form");

        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("End of test H10401");
        myAssert.assertAll();
    }


    @Test (priority=10411)
    public static void testH10411_ValidateNewStarterTommyJSummary() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10411.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H70: Validate New Starter Tommy J Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H70: Validate New Starter Tommy J Summary form");

        logMessage("Item H71: Validate Confirm form and Cancel Submit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20036, 20036, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H71: Validate Confirm form and Cancel Submit");

        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("End of test H10411");
        myAssert.assertAll();
    }

    @Test (priority=10412)
    public static void testH10412_ValidateNewStarterTommyJSummary_2() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10412.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Tommy J again without validating welcome message");
        logonESSAsNewStarter_Main(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H72: Complete New Starter Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H72: Complete New Starter Summary");

        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("End of test H10412");
        myAssert.assertAll();
    }


    @Test (priority=10421)
    public static void testH10421_ValidateNewStarterTommyJLogin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10411.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item H73: Validate Tommy J Logon Page after Complete New Starter Summary", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20055, 20055, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item Item H73: Validate Tommy J Logon Page after Complete New Starter Summary");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10421");
        myAssert.assertAll();
    }

    @Test (priority=10431)
    public static void testH10431_AddNewStarter_EmployeeMinimalFields() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10431.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H75.1: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20551, 20551, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.1: Validate Admin Dashboard via Admin");

        logMessage("Item H75.2: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20351, 20351, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.2: Validate Team - New Starters page via Admin.");

        logMessage("Item H75.3: Add New Starter Max RODGERS.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(141, 141, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.3: Add New Starter Max RODGERS.");

        logMessage("Item H75.4: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20356, 20356, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.4: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10431");
        myAssert.assertAll();
    }

    @Test (priority = 10441)
    public static void testH10441_ValidateEmailActivateMaxR() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10441.");

        logMessage("Item H75.5: Validate Max R email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20921, 20921, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H75.5: Validate Max R email content.");

        logMessage("Item H75.6: Activate New Starter Max R via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20091, 20091, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H75.6: Activate New Starter Max R via Email");

        logMessage("End of test H10441");
        myAssert.assertAll();
    }

    @Test (priority=10451)
    public static void testH10451_EditNewStarterPersonalInfoFormForMaxR() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10451.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item H75.7: Log on New Starter Max R the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.7: Log on New Starter Max R the first time and Valdiate Welcome page.");

        logMessage("Item H75.8: Edit New Starter Max R Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.8: Edit New Starter Max R Persoanl Info page");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10451");
        myAssert.assertAll();
    }

    @Test (priority=10461)
    public static void testH10461_ValidateNewStarter_EditdetailsMaxR() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10461.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H75.9: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20556, 20556, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.9: Validate Admin Dashboard via Admin");

        logMessage("Item H75.10: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20361, 20361, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.10: Validate Team - New Starters page via Admin.");

        logMessage("Item H75.11: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20366, 20366, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.11: click Employee - Teams Newstarter via Admin.");

        logMessage("Item H75.12: Validate New Starter  Max R Contact Details Form.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20036, 20036, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.12: Validate New Starter  Max R Contact Details Form.");

        logMessage("Item H75.13: Edit New Starter Max R Contact Details Form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.13: edit New Starter Max R Contact Details Form ");

        logMessage("Item H75.14: Validate New Starter Tommy J Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment_ViaAdmin(20016, 20016, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.14: Validate New Starter Tommy J Employeement Form and continue");

        logMessage("Item H75.15: Validate New Starter Max R Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.15: Validate New Starter Max R Tax Detail");

        logMessage("Item H75.16: Add New Starter Max R Tax Detail for the Option I have made a separate application... ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20036, 20036, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.16: Add New Starter Max R Tax Detail for the Option I have made a separate application...");


        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10461");
        myAssert.assertAll();
    }


    //Must run without headless
    @Test (priority=10471)
    public static void testH10471_ValidateNewStarter_EditdetailsMaxR() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10471.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User


        logMessage("Item H75.17.1: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20366, 20366, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.17.1: click Employee - Teams Newstarter via Admin.");

        logMessage("Item H75.17: Validate New Starter Max R Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.17: Validate New Starter Max R Superannuation form ");

        logMessage("Item H75.18: Add New Starter Max R Superannuation Detail - Option APRA fund ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20036, 20036, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.18: Add New Starter Max R Superannuation Detail - Option APRA fund ");

        logMessage("Item H75.19: Add New Starter Max R - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.19: Add New Starter Max R - Bank Account Details");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10471");
        myAssert.assertAll();
    }

    @Test (priority=10472)
    public static void testH10472_CompleteNewStarterMaxR() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10472.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Click Employee - Teams Newstarter via Admin.");
        GeneralBasicHigh.selectUserViaTeamsPage_Main(20366, 20366, testSerialNo, emailDomainName, driver);

        logMessage("Item H75.20: Validate New Starter Max R Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20046, 20046, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.20: Validate New Starter Max R Summary form");

        logMessage("Item H75.21: Complete New Starter Max R Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.21: Complete New Starter Max R Summary");

        logMessage("Item H75.22: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20371, 20371, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.22: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10472");
        myAssert.assertAll();
    }

    @Test(priority = 10481)
    public void testH10481_ValidateMaxREmail_ValidateNewStarterViaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10481.");

        logMessage("Item H75.23: Validate Max R Deactivated email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20931, 20931, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H75.23: Validate Max R Deactivated email content.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H75.24: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20381, 20381, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.24: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10481.");
        myAssert.assertAll();
    }


//////////// New Merge added to Master on 10022020  /////////

    @Test (priority=10491)
    public static void testH10491_AddNewStarter_EmployeeMinimalFields()  throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10491.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H76.1: Add New Starter Fred McFREDDY.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(151, 151, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H76.1: Add New Starter Fred McFREDDY.");

        logMessage("Item H76.2: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20391, 20391, testSerialNo, emailDomainName, driver), true, "Failed in Item H76.2: Validate Team - New Starters page via Admin.");

        logMessage("Item H76.3: Valdiate Fred M Welcome page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateESSNewStarter_IntroductionPage_ViaAdim_Main(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H76.3: Valdiate Fred M Welcome page via Admin");

        logMessage("Item H76.4: Valdiate Team page via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20401, 20401, testSerialNo, emailDomainName, driver), true, "Failed in Item H76.4: Valdiate Team page via Admin");

        logMessage("Item H76.5: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20561, 20561, testSerialNo, emailDomainName, driver), true, "Failed in Item H76.5: Validate Admin Dashboard via Admin");


        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10491");
        myAssert.assertAll();
    }


    @Test (priority=10501)
    public static void testH10501_ValidateEmailActivateMaxR() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10501.");

        logMessage("Item H77.1: Validate Fred M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20941, 20941, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H77.1: Validate Fred M email content");

        logMessage("Item H77.2: Activate New Starter Fred M via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20101, 20101, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H77.2: Activate New Starter Fred M via Email");

        logMessage("End of test H10501");
        myAssert.assertAll();
    }

    @Test (priority=10511)
    public static void testH10511_EditNewStarterForm_Validate_ForFredM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10511.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item H77.3: Log on New Starter Fred M the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20091, 20091, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H77.3: Log on New Starter Fred M the first time and Valdiate Welcome page.");


        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10511");
        myAssert.assertAll();
    }

    @Test(priority = 10521)
    public static void testH10521_EditNewStarterForm_Validate_ViaFredM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10521.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Fred M again without validating welcome message");
        logonESSAsNewStarter_Main(20096, 20096, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H77.4: Edit New Starter Fred M Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H77.4: Edit New Starter Fred M Persoanl Info page");

        logMessage("Item H77.5: Validate New Starter Fred M Contact Details Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H77.5: Validate New Starter Fred M Contact Details Form");

        logMessage("Item H77.6: Edit New Starter Fred M - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20056, 20056, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H77.6: Edit New Starter Fred M - Contact Details and Save");

        logMessage("Item H77.7: Validate New Starter Fred M Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H77.7: Validate New Starter Fred M Employeement Form and continue");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10521");
        myAssert.assertAll();
    }


    @Test(priority = 10531)
    public static void testH10531_EditNewStarterForm_Validate_ViaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10531.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H78.1: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20566, 20566, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.1: Validate Admin Dashboard via Admin");

        logMessage("Item H78.2: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20411, 20411, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.2: Validate Team - New Starters page via Admin.");

        logMessage("Item H78.3: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20421, 20421, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.3: click Employee - Teams Newstarter via Admin. ");

        logMessage("Item H78.4: Add New Starter Fred M Tax Detail for the Option I am claiming an exemption", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.4: Add New Starter Fred M Tax Detail for the Option I am claiming an exemption");

        logMessage("Item H78.5: Validate New Starter Fred M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20046, 20046, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.5: Validate New Starter Fred M Tax Detail");

        logMessage("Item H78.5.1: Click New Starter Fred M Tax Detail to Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20048, 20048, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.5: Validate New Starter Fred M Tax Detail");

        logMessage("Item H78.6: Validate New Starter Fred M Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.6: Validate New Starter Fred M Superannuation form ");

        logMessage("Item H78.7: Add New Starter Fred M Superannuation Detail - Option SMSF fund ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20046, 20046, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.7: Add New Starter Fred M Superannuation Detail - Option SMSF fund ");

        logMessage("Item H78.8: Add New Starter Fred M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.8: Add New Starter Fred M - Bank Account Details");

        logMessage("Item H78.9: Validate New Starter Fred M Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.9: Validate New Starter Fred M Summary form");

        logMessage("Item H78.10: Complete New Starter Fred M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20066, 20066, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.10: Complete New Starter Fred M Summary");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10531");
        myAssert.assertAll();
    }

    @Test (priority=10541)
    public static void testH10541_SendNewStarterToPayroll() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10541.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H78.11: Send new starter Fred M to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20041, 20041, testSerialNo, emailDomainName, driver), true, "Falied in Item H78.11: Send new starter Fred M to Payroll Via Admin.");

        logMessage("Item H78.12: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20431, 20431, testSerialNo, emailDomainName, driver), true, "Failed in Item H78.12: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10541");
        myAssert.assertAll();
    }

    @Test(priority = 10551)
    public void testH10551_ValidateFredMEmail_ValidateNewStarterViaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10551.");

        logMessage("Item H78.13: Validate Fred M Deactivated email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20946, 20946, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H78.13: Validate Fred M Deactivated email content.");

        SystemLibrary.logMessage("*** End of test H10551.");
        myAssert.assertAll();
    }

    @Test(priority = 10561)
    public void testH10561_ConfigureRoleNewStarters_EditToView_Validate() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10561.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H79.1: Configure Admin Role - New Starters - from Edit to View", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20191, 20191, driver), true, "Failed in Item H79.1: Configure Admin Role - New Starters - from Edit to View");

        logMessage("Item H79.2: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20571, 20571, testSerialNo, emailDomainName, driver), true, "Failed in Item H79.2: Validate Admin Dashboard via Admin");

        logMessage("Item H79.3: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20441, 20441, testSerialNo, emailDomainName, driver), true, "Failed in Item H79.3: Validate Team - New Starters page via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10561.");
        myAssert.assertAll();
    }

    @Test (priority=10571)
    public static void testH10571_AddNewStarter_EmployeeMinimalFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10571.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H80.1: Configure Admin Role - New Starters - from View to Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20201, 20201, driver), true, "Failed in Item H80.1: Configure Admin Role - New Starters - from View to Edit");

        logMessage("Item H80.2: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20581, 20581, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.2: Validate Admin Dashboard via Admin");

        logMessage("Item H80.3: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20451, 20451, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.3: Validate Team - New Starters page via Admin.");

        logMessage("Item H80.4: Add New Starter Joey JACKS.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(161, 161, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.4: Add New Starter Joey JACKS.");

        logMessage("Item H80.5: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20461, 20461, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.5: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10571");
        myAssert.assertAll();

    }

    @Test (priority=10581)
    public static void testH10581_ValidateNewStarter_EditdetailsJJoey() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10581.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H80.6: click Employee Joey J - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main( 20471, 20471, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.6: click Employee Joey J - Teams Newstarter via Admin.");

        logMessage("Item H80.7: Valdiate Joey J Welcome page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateESSNewStarter_IntroductionPage_ViaAdim_Main(20111, 20111, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.7: Valdiate Joey J Welcome page via Admin");

        logMessage("Item H80.8: Edit New Starter Joey J Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20121, 20121, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.8: Edit New Starter Joey J Persoanl Info page");

        logMessage("Item H80.9: Validate New Starter Joey J Contact Details Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.9: Validate New Starter Joey J Contact Details Form");

        logMessage("Item H80.10: Edit New Starter Joey J - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20066, 20066, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H80.10: Edit New Starter Joey J - Contact Details and Save");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10581");
        myAssert.assertAll();
    }

    @Test (priority=10591)
    public static void testH10591_ValidateEmailActivateJoeyJ()  throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10591.");

        logMessage("Item H81.1: Validate Joey J email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20951, 20951, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H81.1: Validate Joey J email content");

        logMessage("Item H81.2: Activate New Starter Joey J via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20111, 20111, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H81.2: Activate New StarterJoey J via Email");

        logMessage("End of test H10591");
        myAssert.assertAll();

    }

    @Test (priority=10601)
    public static void testH10601_EditNewStarterForm_Validate_ViaJoeyJ() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10601.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Joey J again without validating welcome message");
        logonESSAsNewStarter_Main(20125, 20125, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H81.3: Validate New Starter Joey J Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20031, 20031, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in H81.3: Validate New Starter Joey J Employeement Form and continue");

        logMessage("Item H81.4: Valdiate Joey J Welcome page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateESSNewStarter_WelcomePage_Main(20131, 20131, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.4: Valdiate Joey J Welcome page via Admin");

        logMessage("Item H81.5: Validate New Starter Joey J Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation (20136, 20136, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.4: Valdiate Joey J Welcome page via Admin");
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail (20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.4: Valdiate Joey J Welcome page via Admin");
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment (20036, 20036, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.4: Valdiate Joey J Welcome page via Admin");
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.5: Validate New Starter Joey J Tax Detail");

        logMessage("Item H81.6: Edit New Starter Joey J Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20056, 20056, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.6: Validate New Starter Joey J Tax Detail");

        logMessage("Item H81.7: Validate New Starter Joey J Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_SuperAnnuationForm(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H65: Item H81.7: Validate New Starter Joey J Superannuation form ");

        logMessage("Item H81.8: New Starter Joey J Superannuation form - The super fund nominated by my employer", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20056, 20056, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.8: New Starter Joey J Superannuation form - The super fund nominated by my employer");

        logMessage("Item H81.9: Add New Starter Joey J - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.9: Add New Starter Joey J - Bank Account Details");

        logMessage("Item H81.10: Validate New Starter Joey J Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.10: Validate New Starter Joey J Summary form");

        logMessage("Item H81.11: Complete New Starter Joey J Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20076, 20076, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.11: Complete New Starter Joey J Summary");


        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);
        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10601");
        myAssert.assertAll();
    }


    @Test (priority=10611)
    public static void testH10611_ValidateNewStarter_Teams_Newstarter() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10611.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H81.12: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20481, 20481, testSerialNo, emailDomainName, driver), true, "Failed in Item H81.12: Validate Team - New Starters page via Admin.");

        logMessage("Item H81.13: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20486, 20486, testSerialNo, emailDomainName, driver), true, "Failed in Item H75.10: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10611");
        myAssert.assertAll();

    }


    @Test (priority=10621)
    public static void testH10621_AddNewStarter_EmployeeContractorABNFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10621.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H82.1: Add New Starter Jack MESSI.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(171, 171, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in H82.1: Add New Starter Jack MESSI.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10621");
        myAssert.assertAll();

    }


    @Test (priority=10631)
    public static void testH10631_ValidateNewStarter_EditdetailsJackM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10631.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H82.2: click Employee Jack M - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main( 20491, 20491, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.2: click Employee Jack M - Teams Newstarter via Admin");

        logMessage("Item H82.3: Valdiate Jack M Welcome page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateESSNewStarter_IntroductionPage_ViaAdim_Main(20141, 20141, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.3: Valdiate Jack M Welcome page via Admin");

        logMessage("Item H82.4: Edit New Starter Jack M Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20151, 20151, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.4: Edit New Starter Jack M Persoanl Info page");

        logMessage("Item H82.5: Validate New Starter Jack M Contact Details Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20076, 20076, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.5: Validate New Starter Jack M Contact Details Form");

        logMessage("Item H82.6: Edit New Starter Jack M - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.6: Edit New Starter Jack M - Contact Details and Save");

        logMessage("Item H82.7: Validate New Starter Jack M Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment_ViaAdmin(20041, 20041, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.7: Validate New Starter Jack M Employeement Form and continue");

        logMessage("Item H82.8: Edit New Starter Jack M Employeement Form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_Employment_ViaAdmin(20046, 20046, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.8: Edit New Starter Jack M Employeement Form");

        logMessage("Item H82.9: Validate New Starter Jack M Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment_ViaAdmin(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H82.9: Validate New Starter Jack M Employeement Form and continue");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10631");
        myAssert.assertAll();
    }

    @Test (priority=10641)
    public static void testH10641_ValidateEmailActivateJackM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10641.");

        logMessage("Item H83.1: Validate Jack M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20961, 20961, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H83.1: Validate Jack M email content");

        logMessage("Item H83.2: Activate New Starter Jack M via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20121, 20121, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H83.2: Activate New Starter Jack M via Email");

        logMessage("End of test H10641");
        myAssert.assertAll();

    }


    @Test (priority=10651)
    public static void testH10651_EditNewStarterForm_Validate_ViaJackM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10651.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS,3);
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Jack M again without validating welcome message");
        logonESSAsNewStarter_Main(20155, 20155, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H83.3: Edit New Starter Jack M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item H83.3: Validate New Starter Jack M Tax Detail");

        logMessage("Item H83.4: Validate New Starter Jack M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20066, 20066, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.4: Validate New Starter Jack M Tax Detail");

        logMessage("Item H83.5: Edit New Starter Jack M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.5: Validate New Starter Jack M Tax Detail");

        logMessage("Item H83.6: Validate New Starter Jack M Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.6: Validate New Starter Jack M Superannuation form ");

        logMessage("Item H83.7: Add New Starter Jack M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.7: Add New Starter Jack M - Bank Account Details");

        logMessage("Item H83.8: Validate New Starter Jack M Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.8: Validate New Starter Jack M Summary form");

        logMessage("Item H83.9: Complete New Starter Jack M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20086, 20086, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H83.9: Complete New Starter Jack M Summary");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10651");
        myAssert.assertAll();
    }


    @Test (priority = 10661)
    public static void testH10661_EditGenderMaritalStatusAnthonyBViaMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10661.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H84.1: Edit Gender and Marital Status for Anthony B in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_PersonalInformation1(10031, 10031, payrollDBName), true, "Failed in Item H84.1: Edit Gender and Marital Status for Anthony B in MicrOpay.");

        logMessage("Item H84.2: Edit Employment Type and Job Classification for Anthony B in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_PayDetails1(10021, 10021, payrollDBName), true, "Failed in Item H84.2: Edit Employment Type and Job Classification for Anthony B in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test H10661.");
        myAssert.assertAll();
    }

    @Test(priority = 10671)
    public void testH10671_AddPayrollCompaniesViaMicrOpay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10671.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H84.3: Add Payroll Company and not associate a default superannuation fund.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.addMultiPayrollCompany(20011, 20011), true, "Failed in Item H84.3: Add Payroll Company and not associate a default superannuation fund.");

        AutoITLib.exitMeridian();

        SystemLibrary.logMessage("*** End of test H10671.");
        myAssert.assertAll();
    }

    @Test(priority = 10681)
    public void testH10681_SyncAndAssignJenniferHAsAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10681.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000); //Delay for 5 mins. Jim adjusted on 07/07/2020

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H85.1: Sync All data after adding a new Payroll Companies in MicrOpay.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(104, 104, driver), true, "Failed in Item H85.1: Sync All data after adding a new Payroll Companies in MicrOpay.");

        logMessage("Item H85.2: Validate Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item H85.2: Validate Teams via Admin.");

        logMessage("Item H85.3: Validate Newstarter - Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20506, 20506, testSerialNo, emailDomainName, driver), true, "Failed in Item H85.3: Validate Newstarter - Teams via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10681.");
        myAssert.assertAll();
    }

    @Test (priority=10691)
    public static void testH10691_AddNewStarter_EmployeeContractorTFNFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10691.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H86.1: Add New Starter Natalie MERCHANT.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(181, 181, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H86.1: Add New Starter Natalie MERCHANT.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10691");
        myAssert.assertAll();
    }

    @Test (priority=10701)
    public static void testH10701_ValidateEmailActivateNatalieM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10641.");

        logMessage("Item H87.1: Validate Natalie M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20971, 20971, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H87.1: Validate Natalie M email content.");

        logMessage("Item H87.2: Activate New Starter Natalie M via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20131, 20131, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H87.2: Activate New Starter Natalie M via Email");

        logMessage("End of test H10701");
        myAssert.assertAll();

    }

    @Test (priority=10711)
    public static void testH10711_EditNewStarterForm_Validate_ForNatalieM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10711.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item H87.3: Log on New Starter Natalie M the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20161, 20161, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.3: Log on New Starter Natalie M the first time and Valdiate Welcome page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10711");
        myAssert.assertAll();
    }


    @Test(priority = 10721)
    public static void testH10721_Validate_NewStarter_ViaNatalieM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10721.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Natalie M again without validating welcome message");
        logonESSAsNewStarter_Main(20161, 20161, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H87.4: Edit New Starter Natalie M Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20171, 20171, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.4: Edit New Starter Natalie M Persoanl Info page.");

        logMessage("Item H87.5: Validate New Starter Natalie M Contact Details Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20091, 20091, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.5: Validate New Starter Natalie M Contact Details Form");

        logMessage("Item H87.6: Edit New Starter Natalie M - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.6: Edit New Starter Natalie M - Contact Details and Save");

        logMessage("Item H87.7: Validate New Starter Natalie M Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20061, 20061, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.7: Validate New Starter Natalie M Employeement Form and continue");

        logMessage("Item H87.8: Validate New Starter Natalie M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.8: Validate New Starter Natalie M Tax Detail");

        logMessage("Item H87.9: Edit New Starter Natalie M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20091, 20091, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.9: Edit New Starter Natalie M Tax Detail");

        logMessage("Item H87.10: Validate New Starter Natalie M Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.10: Validate New Starter Natalie M Superannuation form ");

        logMessage("Item H87.11: Add New Starter Natalie M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.11: Add New Starter Natalie M - Bank Account Details");

        logMessage("Item H87.12: Validate New Starter Natalie M Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20091, 20091, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.12: Validate New Starter Natalie M Summary form");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10721");
        myAssert.assertAll();
    }

    @Test (priority=10731)
    public static void testH10731_AddNewStarter_EmployeeContractorABNFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10731.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H87.13: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20591, 20591, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.13: Validate Admin Dashboard via Admin");

        logMessage("Item H87.14: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20511, 20511, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.14: Validate Team - New Starters page via Admin.");

        logMessage("Item H87.15: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.15: click Employee - Teams Newstarter via Admin. ");

        logMessage("Item H87.16: Complete New Starter Natalie M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.16: Complete New Starter Natalie M Summary.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10731");
        myAssert.assertAll();

    }

    @Test (priority=10741)
    public static void testH10741_EditNewStarterNatalieM_AfterReadyApproveForms() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10741.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H87.17: Edit New Starter Natalie M After Ready To Approve Forms", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.15: click Employee - Teams Newstarter via Admin. ");
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStater_AfterReadyToApproveForms(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.17: Edit New Starter Natalie M After Ready To Approve Forms");

        logMessage("Item H87.18: Edit New Starter Natalie M Employeement Form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_Employment_ViaAdmin(20071, 20071, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in H87.18: Edit New Starter Natalie M Employeement Form");

        logMessage("Item H87.19.1: Validate New Starter Natalie M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails_nochangeDeclaration(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19.1: Validate New Starter Natalie M Tax Detail");

        logMessage("Item H87.19.2: Validate New Starter Natalie M Superannuation form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19.2: Validate New Starter Natalie M Superannuation form");

        logMessage("Item H87.19.3: Add New Starter Natalie M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19.3: Add New Starter Natalie M - Bank Account Details");

        logMessage("Item H87.19.4: Complete New Starter Natalie M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19.4: Complete New Starter Natalie M Summary.");

        logMessage("Item H87.20: Send new starter Natalie M to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.20: Send new starter Natalie M to Payroll Via Admin.");
        myAssert.assertEquals(GeneralBasicHigh.sendMultiNewStarterToPayroll(20061, 20061, testSerialNo, emailDomainName, driver), true, "Falied in Item H87.20: Send new starter Natalie M to Payroll Via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10741");
        myAssert.assertAll();
    }

    @Test (priority = 10742)
    public static void testH10742_AddGenderMaritalStatusViaLookupTables_MicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10742.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H86.1_1: Add Gender and Marital Status at Lookup Tables in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.addMultiLookup(10001, 10001), true, "Failed in H86.1_1: Add Gender and Marital Status at Lookup Tables in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test H10742.");
        myAssert.assertAll();
    }

    @Test (priority=10743)
    public static void testH10743_AddNewStarter_EmployeeContractorTFNFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10743.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000); //Delay for 5 mins, Jim Adjusted on 07/07/2020

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H86.1_2: Sync All data after adding a new Payroll Companies in MicrOpay.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(104, 104, driver), true, "Failed in Item H86.1_2: Sync All data after adding a new Payroll Companies in MicrOpay.");

        logMessage("Item H86.1_3: Add New Starter Nat MER.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNewStarters_Main(186, 186, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H86.1_3: Add New Starter Nat MER.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10743");
        myAssert.assertAll();
    }

    @Test (priority=10744)
    public static void testH10744_ValidateEmailActivateNatM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10744.");

        logMessage("Item H87.1_1: Validate Nat M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20976, 20976, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H87.1_1: Validate Nat M email content");

        logMessage("Item H87.2_1: Activate New Starter Nat M via Email", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiNewStarter_ViaEmail(20136, 20136, payrollDBName, testSerialNo, emailDomainName), true, "Failed in Item H87.2_1: Activate New Starter Nat M via Email");

        logMessage("End of test H10744");
        myAssert.assertAll();

    }

    @Test (priority=10745)
    public static void testH10745_EditNewStarterForm_Validate_ForNatM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10745.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item H87.3_1: Log on New Starter Nat M the first time and Valdiate Welcome page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSAsNewStarter_Main(20166, 20166, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.3_1: Log on New Starter Nat M the first time and Valdiate Welcome page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10745");
        myAssert.assertAll();
    }

    @Test(priority = 10746)
    public static void testH10746_Validate_NewStarter_ViaNatM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10746.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on ESS as Nat M again without validating welcome message");
        logonESSAsNewStarter_Main(20166, 20166, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item H87.4_1: Edit New Starter Nat M Persoanl Info page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_PersonalInformation(20176, 20176, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.4_1: Edit New Starter Nat M Persoanl Info page.");

        logMessage("Item H87.5_1: Validate New Starter Nat M Contact Details Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_ContactDetail(20096, 20096, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.5_1: Validate New Starter Nat M Contact Details Form");

        logMessage("Item H87.6_1: Edit New Starter Nat M - Contact Details and Save", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_ContactDetail(20106, 20106, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.6_1: Edit New Starter Nat M - Contact Details and Save");

        logMessage("Item H87.7_1: Validate New Starter Nat M Employeement Form and continue ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Employment(20066, 20066, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.7_1: Validate New Starter Nat M Employeement Form and continue");

        logMessage("Item H87.8_1: Validate New Starter Nat M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_TaxDetailsForm(20086, 20086, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.8_1: Validate New Starter Nat M Tax Detail");

        logMessage("Item H87.9_1: Edit New Starter Nat M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails(20096, 20096, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.9_1: Edit New Starter Nat M Tax Detail");

        logMessage("Item H87.10_1: Validate New Starter Nat M Superannuation form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20076, 20076, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.10_1: Validate New Starter Nat M Superannuation form ");

        logMessage("Item H87.11_1: Add New Starter Nat M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20076, 20076, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.11_1: Add New Starter Nat M - Bank Account Details");

        logMessage("Item H87.12_1: Validate New Starter Nat M Summary form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiNewStarter_Summary(20096, 20096, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.12_1: Validate New Starter Nat M Summary form");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10746");
        myAssert.assertAll();
    }

    @Test (priority=10747)
    public static void testH10747_AddNewStarter_EmployeeContractorABNFields() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10747.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H87.13_1: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20596, 20596, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.13_1: Validate Admin Dashboard via Admin");

        logMessage("Item H87.14_1: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20516, 20516, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.14_1: Validate Team - New Starters page via Admin.");

        logMessage("Item H87.15_1: click Employee - Teams Newstarter via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20526, 20526, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.15_1: click Employee - Teams Newstarter via Admin. ");

        logMessage("Item H87.16_1: Complete New Starter Nat M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20106, 20106, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.16_1: Complete New Starter NataNatlie1 M Summary.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10747");
        myAssert.assertAll();

    }

    @Test (priority=10748)
    public static void testH10748_EditNewStarterNatalieM_AfterReadyApproveForms() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10748.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H87.17_1: Edit New Starter Nat M After Ready To Approve Forms", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20536, 20536, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.15_1: click Employee - Teams Newstarter via Admin. ");
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStater_AfterReadyToApproveForms(20056, 20056, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.17_1: Edit New Starter Nat M After Ready To Approve Forms");

        logMessage("Item H87.18_1: Edit New Starter Nat M Employeement Form ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_Employment_ViaAdmin(20076, 20076, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in H87.18_1: Edit New Starter Nat M Employeement Form");

        logMessage("Item H87.19_1_1: Validate New Starter Nat M Tax Detail", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_TaxDetails_nochangeDeclaration(20106, 20106, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19_1_1: Validate New Starter Nat M Tax Detail");

        logMessage("Item H87.19_2_1: Validate New Starter Nat M Superannuation form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_SuperAnnuation(20086, 20086, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19_2_1: Validate New Starter Nat M Superannuation form");

        logMessage("Item H87.19_3_1: Add New Starter Nat M - Bank Account Details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiNewStarter_BankAccount(20086, 20086, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19_3_1: Add New Starter Nat M - Bank Account Details");

        logMessage("Item H87.19_4_1: Complete New Starter Nat M Summary.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.completeMultiNewStarter_Summary(20106, 20106, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.19_4_1: Complete New Starter Nat M Summary.");

        logMessage("Item H87.20_1: Send new starter Nat M to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.selectUserViaTeamsPage_Main(20536, 20536, testSerialNo, emailDomainName, driver), true, "Failed in Item H87.20_1: Send new starter Nat M to Payroll Via Admin. ");
        myAssert.assertEquals(GeneralBasicHigh.sendMultiNewStarterToPayroll(20066, 20066, testSerialNo, emailDomainName, driver), true, "Falied in Item H87.20_1: Send new starter Nat M to Payroll Via Admin.");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10748");
        myAssert.assertAll();

    }

    @Test (priority=10751)
    public static void testH10751_SendNewStarterToPayroll() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10751.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H88.1: Send new starter Tom J to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20071, 20071, testSerialNo, emailDomainName, driver), true, "Falied in Item H88.1: Send new starter Tom J to Payroll Via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10751");
        myAssert.assertAll();
    }


    @Test (priority=10761)
    public static void testH10761_SendNewStarterToPayroll() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10761.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H88.2: Send new starter Max R to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20081, 20081, testSerialNo, emailDomainName, driver), true, "Falied in Item H88.2: Send new starter Max R to Payroll Via Admin.");

        logMessage("Item H88.3: Send new starter Joey J to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20091, 20091, testSerialNo, emailDomainName, driver), true, "Falied in Item H88.3: Send new starter Joey J to Payroll Via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10761");
        myAssert.assertAll();
    }

    @Test (priority=10771)
    public static void testH10771_SendNewStarterToPayroll() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10771.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item H88.4: Send new starter Jack M to Payroll Via Admin.", testSerialNo);
        myAssert.assertEquals(sendMultiNewStarterToPayroll(20101, 20101, testSerialNo, emailDomainName, driver), true, "Falied in Item H88.4: Send new starter Jack M to Payroll Via Admin.");

        logMessage("Item H88.5: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in Item H88.5: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10771");
        myAssert.assertAll();
    }


    @Test (priority=10781)
    public static void testH10781_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10781.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H89.1: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20011, 20011),  true, "Falied in Item H89.1: Validate New Employee Maintenance Screen ");

        AutoITLib.exitMeridian();
        logMessage("End of test H10781");
        myAssert.assertAll();
    }

    @Test (priority=10791)
    public static void testH10791_EditNewStarterInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10791.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H90.1: Edit New Employee - Tom J and Save.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20021, 20021), true, "Falied in Item H90.1: Edit New Employee - Tom J and Save");

        logMessage("Item H90.2: New Employee - Joey J and Save.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20031, 20031), true, "Falied in Item H90.2: New Employee - Joey J and Save.");

        logMessage("Item H90.3: New Employee - Tom J and send to Payroll.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20041, 20041), true, "Falied in Item H90.3: New Employee - Tom J and send to Payroll.");

        AutoITLib.exitMeridian();
        logMessage("End of test H10791");
        myAssert.assertAll();

    }

    @Test (priority=10801)
    public static void testH10801_ValdiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10801.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H90.4: Print and Validate Tom J Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20161, 20161, emailDomainName, testSerialNo), true, "Falied in Item H90.4: Print and Validate Tom J Employee Detail Report");

        AutoITLib.exitMeridian();
        logMessage("End of test H10801");
        myAssert.assertAll();
    }

    @Test(priority = 10811)
    public void testH10811_EditPayrollCompaniesViaMicrOpay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10811.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H90.5: Edit a Payroll Company details", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiPayrollCompany(20015, 20015), true, "Failed in Item H90.5: Edit a Payroll Company details");

        AutoITLib.exitMeridian();

        SystemLibrary.logMessage("*** End of test H10811.");
        myAssert.assertAll();
    }

    @Test(priority = 10821)
    public void testH10821_SyncallandImportNewEmployee() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10821.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000);

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H91.1: Sync All data after update Payroll Companies in MicrOpay.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(10031, 10031, driver), true, "Failed in Item H91.1: Sync All data after update Payroll Companies in MicrOpay.");

        logMessage("Item H91.2: Validate Integration page after Sync.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.2: Validate Integration page after Sync.");

        logMessage("Item H91.3: Validate New Starter Tom J Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20451, 20451, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.3: Validate New Starter Tom J Personal Information page.");

        logMessage("Item H91.4: Validate Newstarter - Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20551, 20551, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.4: Validate Newstarter - Teams via Admin.");

        logMessage("Item H91.5: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20601, 20601, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.5: Validate Admin Dashboard via Admin");

        logMessage("Item H91.6: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20561, 20561, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.6: Validate Team - New Starters page via Admin.");


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10821.");
        myAssert.assertAll();
    }

    @Test (priority = 10831)
    public static void testH10831_FinaliseNewStarter() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10831.");

        logMessage("Item H91.7: Restart MCS");
        MCSLib.restartMCS();
        Thread.sleep(300000); //Delay 5 mins, Jim added on 05/07/2020

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H91.8: Finalise New Starter Tom J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20111, 20111, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.8: Finalise New Starter Tom J.");

        logMessage("Item H91.9: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20571, 20571, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.9: Validate Team - New Starters page via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10831");
        myAssert.assertAll();
    }

    @Test (priority = 10841)
    public static void testH10841_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10841.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H91.10: Validate New Starter Tom J Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20461, 20461, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.10: Validate New Starter Tom J Personal Information page.");

        logMessage("Item H91.11: Validate New Starter Tom J Contact Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.11: Validate New Starter Tom J Contact Details page.");

        logMessage("Item H91.12: Validate New Starter Tom J Account Settings page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.12: Validate New Starter Tom J Account Settings page.");

        logMessage("Item H91.13: Validate New Starter Tom J Teams and Roles page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20441, 20441, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.13: Validate New Starter Tom J Teams and Roles page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10841");
        myAssert.assertAll();
    }

    @Test (priority = 10851)
    public static void testH10851_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10851.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H91.14: Validate New Starter Tom J Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.14: Validate New Starter Tom J Employment Details page.");

        logMessage("Item H91.15: Validate New Starter Tom J Bank Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateBankAccounts_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.15: Validate New Starter Tom J Bank Details page.");

        ////////////////// Temp disable Super Validation on 16/08/2021 //////////////////
        /*logMessage("Item H91.16: Validate New Starter Tom J Superannuation page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.16: Validate New Starter Tom J Superannuation page.");
        */
        //////

        logMessage("Item H91.17: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20611, 20611, testSerialNo, emailDomainName, driver), true, "Failed in Item H91.17: Validate Admin Dashboard via Admin");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10851");
        myAssert.assertAll();
    }

    @Test (priority = 10861)
    public static void testH10861_ValidateEmailActivateTomJ() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10861.");

        logMessage("Item H91.18: Validate Tom J email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20981, 20981, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H91.18: Validate Tom J email content");

        logMessage("Item H91.19: Activate Tom J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaEmail(20141, 20141, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in H91.19: Activate Tom J.");

        logMessage("End of test H10861");
        myAssert.assertAll();

    }

    @Test (priority=10871)
    public static void testH10871_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10871.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H92.1: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20051, 20051),  true, "Falied in Item H92.1: Validate New Employee Maintenance Screen");

        logMessage("Item H92.2: Edit New Employee - Max R and Complete Process.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20061, 20061), true, "Falied in Item H92.2: Edit New Employee - Max R and Complete Process.");

        logMessage("Item H92.3: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20071, 20071),  true, "Falied in Item H92.3: Validate New Employee Maintenance Screen");


        AutoITLib.exitMeridian();
        logMessage("End of test H10871");
        myAssert.assertAll();
    }

    @Test (priority=10881)
    public static void testH10881_ValdiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10881.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H92.4: Print and Validate Max R Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20171, 20171, emailDomainName, testSerialNo), true, "Falied in Item H92.4: Print and Validate Max R Employee Detail Report");

        AutoITLib.exitMeridian();
        logMessage("End of test H10881");
        myAssert.assertAll();

    }

    @Test(priority = 10891)
    public void testH10891_SyncallandImportNewEmployee() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10891.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000);

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H92.5: Validate Newstarter - Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20586, 20586, testSerialNo, emailDomainName, driver), true, "Failed in Item H92.5: Validate Newstarter - Teams via Admin.");

        logMessage("Item H92.6: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20591, 20591, testSerialNo, emailDomainName, driver), true, "Failed in Item H92.6: Validate Team - New Starters page via Admin.");

        logMessage("Item H92.7: Sync All data after update Emp.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(10041, 10041, driver), true, "Failed in Item H92.7: Sync All data after update Emp.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10891.");
        myAssert.assertAll();
    }

    @Test (priority = 10901)
    public static void testH10901_FinaliseNewStarter() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H10901.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H92.8: Finalise New Starter Max R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20121, 20121, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H92.8: Finalise New Starter Max R.");

        logMessage("Item H92.9: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20601, 20601, testSerialNo, emailDomainName, driver), true, "Failed in Item H92.9: Validate Team - New Starters page via Admin.");

        logMessage("Item H92.10: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20626, 20626, testSerialNo, emailDomainName, driver), true, "Failed in Item Item H92.10: Validate Admin Dashboard via Admin");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10901");
        myAssert.assertAll();
    }

    @Test (priority=10911)
    public static void testH10911_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H109111.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H93.1: Edit New Employee - Fred M and Complete Process.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20081, 20081), true, "Falied in Item H92.2: H93.1: Edit New Employee - Fred M and Complete Process.");

        logMessage("Item H93.2: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20091, 20091),  true, "Falied in Item H93.2: Validate New Employee Maintenance Screen");

        AutoITLib.exitMeridian();
        logMessage("End of test H109111");
        myAssert.assertAll();
    }

    @Test (priority=10921)
    public static void testH10921_ValdiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10921.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H93.3: Print and Validate Fred M Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20181, 20181, emailDomainName, testSerialNo), true, "Falied in Item H92.4: H93.3: Print and Validate Fred M Employee Detail Report.");

        AutoITLib.exitMeridian();
        logMessage("End of test H10921");
        myAssert.assertAll();

    }

    @Test (priority=10931)
    public static void testH10931_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H109131.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H94.1: Edit New Employee - Joey J and Complete Process.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20101, 20101), true, "Falied in Item H94.1: Edit New Employee - Joey J and Complete Process.");

        AutoITLib.exitMeridian();
        logMessage("End of test H109131");
        myAssert.assertAll();
    }

    @Test (priority=10941)
    public static void testH10941_ValdiateEmployeeDetailReportInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10941.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H94.2: Print and Validate Joey J Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20191, 20191, emailDomainName, testSerialNo), true, "Falied in Item H94.2: Print and Validate Joey J Employee Detail Report.");

        logMessage("Item H94.3: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20111, 20111),  true, "Falied in Item H94.3: Validate New Employee Maintenance Screen");

        AutoITLib.exitMeridian();
        logMessage("End of test H10941");
        myAssert.assertAll();

    }

    @Test (priority=10951)
    public static void testH10951_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10951.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);


        logMessage("Item H95.1: Edit New Employee - Jack M  and Complete Process.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_Voluntary(20121, 20121), true, "Falied in Item H95.1: Edit New Employee - Jack M and Complete Process.");

        logMessage("Item H95.2: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20131, 20131),  true, "Falied in Item H95.2: Validate New Employee Maintenance Screen");


        AutoITLib.exitMeridian();
        logMessage("End of test H10951");
        myAssert.assertAll();
    }


    @Test (priority=10961)
    public static void testH10961_validateNewEmployeeMaintenanceScreen_Micr0pay() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10961.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);


        logMessage("Item H96.1: Edit New Employee - Nat M and Complete Process.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiNewEmployee_New(20141, 20141), true, "Falied in Item H96.1: Edit New Employee - Nat M and Complete Process.");

        logMessage("Item H96.2: Validate New Employee Maintenance Screen", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.validateMultiNewEmployeeMaintenanceScreen (20151, 20151),  true, "Falied in Item H96.2: Validate New Employee Maintenance Screen");


        AutoITLib.exitMeridian();
        logMessage("End of test H10961");
        myAssert.assertAll();
    }

    @Test (priority=10971)
    public static void testH10971_processBatchEmployeeMaintenanceInMicrOpay() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10971.");

        logMessage("Log on Sage MicrOpay as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H97.1: Process Batch New Employee Maintenance Screen.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.processMultiBatch_NewEmployeeMaintenanceScreen(20161, 20161), true, "Falied in Item H97.1: Process Batch New Employee Maintenance Screen");

        AutoITLib.exitMeridian();
        logMessage("Relogin Micropay");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item H97.2: Print and Validate Jack M Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20201, 20201, emailDomainName, testSerialNo), true, "Falied in Item H97.2: Print and Validate Jack M Employee Detail Report.");

        logMessage("Item H97.3: Print and Validate Nat M Employee Detail Report.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20211, 20211, emailDomainName, testSerialNo), true, "Falied in Item H97.3: Print and Validate Nat M Employee Detail Report.");

        AutoITLib.exitMeridian();
        logMessage("End of test H10971");
        myAssert.assertAll();

    }

    @Test(priority = 10981)
    public void testH10981_SyncAndFinaliseNewStarterviaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10981.");

        logMessage("Restart MCS.");
        restartMCS();
        Thread.sleep(300000); //delay 5 mins

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H98.1: Sync All data after adding a new Payroll Companies in MicrOpay.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(10051, 10051, driver), true, "Failed in Item H98.1: Sync All data after adding a new Payroll Companies in MicrOpay.");

        logMessage("Item H98.2: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20631, 20631, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.2: Validate Admin Dashboard via Admin");

        logMessage("Item H98.3: Validate Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20611, 20611, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.3: Validate Teams via Admin.");

        logMessage("Item H98.4: Validate Newstarter - Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20616, 20616, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.4: Validate Newstarter - Teams via Admin.");

        logMessage("Item H98.5: Finalise New Starter Fred M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20131, 20131, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.5: Finalise New Starter Fred M.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test H10981.");
        myAssert.assertAll();
    }

    @Test (priority = 10991)
    public static void testH10991_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H10991.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H98.6: Validate New Starter Fred M Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.6: Validate New Starter Fred M Personal Information page.");

        logMessage("Item H98.7: Validate New Starter Fred M Contact Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.7: Validate New Starter Fred M Contact Details page.");

        logMessage("Item H98.8: Validate New Starter Fred M Account Settings page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20166, 20166, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.8: Validate New Starter Fred M Account Settings page.");

        logMessage("Item H98.9: Validate New Starter Fred M Teams and Roles page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20456  , 20456, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.9: Validate New Starter Fred M Teams and Roles page.");

        logMessage("Item H98.10: Validate New Starter Fred M Employment Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item H98.10: Validate New Starter Fred M Employment Details page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H10991");
        myAssert.assertAll();
    }


    @Test (priority = 11001)
    public static void testH11001_Validate_Activate_Finalise_NewStarter_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H11001.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H99.1: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20641, 20641, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.1: Validate Admin Dashboard via Admin");

        logMessage("Item H99.2: Validate Team - New Starters page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(20621, 20621, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.2: Validate Team - New Starters page via Admin.");

        logMessage("Item H99.3: Add Jack M to Team That Team J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20631, 20631, testSerialNo, driver), true, "Failed in Item H99.3: Add Jack M to Team That Team J.");

        logMessage("Item H99.4: Validate Newstarter - Teams via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20641, 20641, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.4: Validate Newstarter - Teams via Admin.");

        logMessage("Item H99.5: Finalise New Starter Jack M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20141, 20141, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.5: Finalise New Starter Jack M.");

        logMessage("Item H99.6: Validate New Starter Jack M Account Settings page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20171, 20171, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.6: Validate New Starter Jack M Account Settings page.");

        logMessage("Item H99.7: Validate New Starter Jack M Teams and Roles page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20461  , 20461, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.7: Validate New Starter Jack M Teams and Roles page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H11001");
        myAssert.assertAll();
    }

    @Test (priority = 11011)
    public static void testH11011_ValidateEmailActivateTomJ() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H11011.");

        logMessage("Item H99.8: Validate Jack M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20996, 20996, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H99.8: Validate Jack M email content");

        logMessage("Item H99.9: Activate Jack M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaEmail(20181, 20181, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in H99.9: Activate Jack M.");
        logMessage("End of test H11011");
        myAssert.assertAll();
    }

    @Test (priority = 11021)
    public static void testH11021_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H11021.");

        logMessage("Log on ESS as Jack M");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10441, 10441, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H99.10: Validate New Starter Jack M Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in H99.10: Validate New Starter Jack M Personal Information page.");

        logMessage("Item H99.11: Validate New Starter Jack M Contact Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20311, 20311, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.11: Validate New Starter Jack M Contact Details page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H11021");
        myAssert.assertAll();
    }

    @Test (priority = 11031)
    public static void testH11031_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test H11031.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H99.12: Add Nat M to Team That Team J.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20651, 20651, testSerialNo, driver), true, "Failed in Item H99.12: Add Nat M to Team That Team J.");

        logMessage("Item H99.13: Add Nat M to The Boss.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20661, 20661, testSerialNo, driver), true, "Failed in Item H99.13: Add Nat M to Team The Boss.");

        logMessage("Item H99.14: Finalise New Starter Nat M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.finaliseMultiNewStarterInESS(20151, 20151, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.14: Finalise New Starter Nat M.");

        logMessage("Item H99.15: Validate New Starter Nat M Account Settings page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.15: Validate New Starter Nat M Account Settings page.");

        logMessage("Item H99.16: Validate New Starter Nat M Teams and Roles page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20471  , 20471, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.16: Validate New Starter Nat M Teams and Roles page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H11031");
        myAssert.assertAll();
    }

    @Test (priority = 11041)
    public static void testH11041_ValidateEmailActivateTomJ() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H11041.");

        logMessage("Item H99.17: Validate Nat M email content.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(21001, 21001, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item H99.17: Validate Nat M email content");

        logMessage("Item H99.18: Activate Nat M.", testSerialNo);
        //myAssert.assertEquals(GeneralBasicHigh.activateMultiNewstarter_afterFinalise_ViaEmail(20201, 20201, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in H99.18: Activate Nat M.");
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaEmail(20201, 20201, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in H99.18: Activate Nat M.");

        logMessage("End of test H11041");
        myAssert.assertAll();
    }


    @Test (priority = 11051)
    public static void testH11051_ValidateNewStarterDetail_InESS() throws Exception{
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test H11051.");

        logMessage("Log on ESS as Nat M");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10451, 10451, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item H99.19: Validate New Starter Nat M Personal Information page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in H99.19: Validate New Starter Nat M Personal Information page.");

        logMessage("Item H99.20: Validate New Starter Nat M Contact Details page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20321, 20321, testSerialNo, emailDomainName, driver), true, "Failed in Item H99.20: Validate New Starter Nat M Contact Details page.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of test H11051");
        myAssert.assertAll();
    }




    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item HHH", "Completed", "End of Test Module H - New Starter Function Test", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module H - New Starter Function test.");
    }



    /////////////// Debug here ///////////////////////


}