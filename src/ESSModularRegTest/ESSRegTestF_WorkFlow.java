package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.AutoITLib.close_ImplementHRScreen;
import static Lib.AutoITLib.exitMeridian;
import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.driverType;
import static Lib.SystemLibrary.logMessage;

public class ESSRegTestF_WorkFlow {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=106;
    private static String payrollDBOrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName="F";

    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");
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

    @Test(priority = 10011)
    public void testF10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item F10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item F1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item F1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item F1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item F1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item F1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item F1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item F1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item F1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item F1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item F10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testF10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test F10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item F1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item F1.8: Add API configuration.");

        logMessage("Item F1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item F1.9: Sync All.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testF10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test F10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item F2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item F2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item F2.2: Validate Team Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10031.");
        myAssert.assertAll();
    }


    @Test (priority=10041)
    public static void testF10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test F10041: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item F3: Import Team via TPU.");

        logMessage("*** End of Test F10041");
        myAssert.assertAll();
    }



    @Test(priority = 10051) //a
    public void testF10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test F10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item F4: Validate Team after import team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10051.");
        myAssert.assertAll();
    }


    ////////////////////////  Start Work flow testing below /////////////////////////////



    @Test (priority = 10061)
    public void testF10061_ValidateWorkflowSettingsAndPermissons() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10061.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Validate Admin Roles and Permisson page");
        logMessage("Item F5.1", testSerialNo);
        myAssert.assertEquals(validatePermissionPanel_Main(10021, 10021, emailDomainName, testSerialNo, driver), true, "Failed in Item F5.1: Validate Administrator roles permissions.");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item F5.Ex.1: Activate Sharon A");
        logMessage("Item F5.Ex.1", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(112, 112, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5_Ex_1: Activate Sharon A");

        logMessage("Item F5.Ex.2: Activate Jack F");
        logMessage("Item F5.Ex.2", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(103, 103, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5_Ex_2: Activate Jack F");

        logMessage("Item F5.Ex.3: Activate Sue A.");
        logMessage("Item F5.Ex.3", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(104, 104, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5.Ex.3: Activate Sue A.");

        logMessage("Item F5.Ex.4: Activate Phanton F.");
        logMessage("Item F5.Ex.4", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(105, 105, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5.Ex.4: Activate Tanya D.");

        logMessage("Item F5.Ex.5: Activate Tanya D.");
        logMessage("Item F5.Ex.5", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(110, 110, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5.Ex.5: Activate Tanya D.");

        logMessage("Item F5.Ex.6: Activate Jules N.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(116, 116, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F5.Ex.6: Activate Jules N.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sharon Andrew.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F5.2: Validate Sharon Andrew Menu.");
        logMessage("Item F5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTopNavigationMenu_Main(20001, 20001, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.2: Validate Sharon Andrew Menu.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sue A.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F5.3: Validate Sue A top Menu.");
        logMessage("Item F5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTopNavigationMenu_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.3: Validate Sue A top Menu.");

        signoutESS(driver);
        driver.close();


        ////////////////
        logMessage("Logon as Jules N.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10276, 10276, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        signoutESS(driver);
        driver.close();


        SystemLibrary.logMessage("*** End of Test F10061.");
        myAssert.assertAll();
    }


    @Test (priority = 10071)
    public void testF10071_AdminChangeWorkflowPermisisonAndValidateRights() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10071.");

        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName,testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F5.4: Change Workflow from Edit to Deny");
        logMessage("Item F5.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20011, 20011, driver), true, "Failed in Item F5.4: Change Workflow from Edit to Deny");

        logMessage("Item F5.5:Validate Setting Menu without Workflow");
        logMessage("Item F5.5", testSerialNo);
        myAssert.assertEquals(GeneralBasic.displaySettings_Workflows(driver), false, "Failed in Item F5.5:Validate Setting Menu without Workflow");
        logMessage("Menue 'Workflows' should not be found as expected.");

        GeneralBasic.displayDashboard(driver);
        logMessage("Item F5.6: Change Workflow from Deny to View");
        logMessage("Item F5.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20021, 20021, driver), true, "Failed in Item F5.6: Change Workflow from Deny to View");

        logMessage("Item F5.7: Validate Workflows page without Edit button.");
        logMessage("Item F5.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.7: Validate Workflows page without Edit button.");

        logMessage("Item F5.8: Validate Workflows - Profile Change without Edit button.");
        logMessage("Item F5.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20012, 20012, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.8: Validate Workflows - Profile Change without Edit button.");

        logMessage("Item F5.9: Validate Workflows - Leave without Edit button.");
        logMessage("Item F5.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20013, 20013, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.9: Validate Workflows - Leave without Edit button.");

        ////////////////////

        logMessage("Item F5.10: Change Workflow from View to Edit");
        logMessage("Item F5.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20031, 20031, driver), true, "Failed in Item F5.10: Change Workflow from Deny to View");

        logMessage("Item F5.11: Validate Workflows page with Edit button.");
        logMessage("Item F5.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.11: Validate Workflows page with Edit button.");

        logMessage("Item F5.12: Validate Workflows - Profile Change with Edit button.");
        logMessage("Item F5.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20022, 20022, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.12: Validate Workflows - Profile Change with Edit button.");

        logMessage("Item F5.13: Validate Workflows - Leave with Edit button.");
        logMessage("Item F5.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20023, 20023, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.13: Validate Workflows - Leave without Edit button.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10071.");
        myAssert.assertAll();
    }

    @Test (priority = 10081)
    public void testF10081_Validate_Workflow_ProfileChange_EditWorkflowForm() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10081.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F6.1: Validate Edit Workflow - Profile change form - Then");
        logMessage("Item F6.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiEditWorkflowForm(20031, 20031, emailDomainName, testSerialNo, driver), true, "Failed in Item F6.1: Validate Edit Workflow - Profile change form - Then");

        logMessage("Item F6.2: Validate Edit Workflow - Profile change form - by");
        logMessage("Item F6.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiEditWorkflowForm(20032, 20032, emailDomainName, testSerialNo, driver), true, "Failed in Item F6.1: Validate Edit Workflow - Profile change form - Then");

        logMessage("Item F6.3: Validate Edit Workflow - Profile change form - in");
        logMessage("Item F6.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiEditWorkflowForm(20033, 20033, emailDomainName, testSerialNo, driver), true, "Failed in Item F6.1: Validate Edit Workflow - Profile change form - Then");

        logMessage("Item F6.4: Validate Edit Workflow - Profile change form - and then");
        logMessage("Item F6.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiEditWorkflowForm(20034, 20034, emailDomainName, testSerialNo, driver), true, "Failed in Item F6.1: Validate Edit Workflow - Profile change form - Then");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10081.");
        myAssert.assertAll();
    }

    @Test (priority = 10091)
    public void testF10091_ValidateDefaultLeaveProcess() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10091.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Logon as Member Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F7.1: Validate Default Approval Process via Member Sharon A");
        logMessage("Item F7.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item F7.1: Validate Default Approval Process via Member Sharon A");

        signoutESS(driver);
        driver.close();

        //////////////////////
        logMessage("Logon as Sue A.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F7.2: Validate Default Approval Process via Manager Sue A");
        logMessage("Item F7.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item F7.2: Validate Default Approval Process via Manager Sue A");

        signoutESS(driver);
        driver.close();

        /////////////////////////////////

        logMessage("Log on as Jack F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F7.3: Validate Default Approval Process via Manager Jack F");
        logMessage("Item F7.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item F7.3: Validate Default Approval Process via Manager Jack F");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test F10091.");
        myAssert.assertAll();
    }

    @Test (priority = 10101)
    public void testF10101_EditPreferredNameAndMaritalStatusViaSharonA() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10101.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Sharon A");
        WebDriver driver= GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.1: Edit Marital Status for Sharon A via Sharon A");
        logMessage("Item F8.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20011, 20011, testSerialNo, driver), true, "Failed in Item F8.1: Edit Marital Status for Sharon A via Sharon A");

        logMessage("Item F8.2: Edit Preferred name as Mia via Sharon A");
        logMessage("Item F8.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20012, 20012, testSerialNo, driver), true, "Failed in Item F8.2: Edit Preferred name as Mia via Sharon A");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test F10101.");
        myAssert.assertAll();
    }

    @Test (priority = 10111)
    public static void testF10111_ValidateEmailContent() throws Exception {
        logMessage("*** Start Test F10111...");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F8.3: Validate Sharon A Email content with Preferred Name change.");
        logMessage("Item F8.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20161, 20161, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.3: Validate Sharon A Email content with Preferred Name change.");

        logMessage("Item F8.4: Validate Sharon A Email content with Marital Status change.");
        logMessage("Item F8.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20162, 20162, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.4: Validate Sharon A Email content with Marital Status change.");

        logMessage("Item F8.5: Validate Sue A Email content with Preferred Name change.");
        logMessage("Item F8.5", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20163, 20163, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.5: Validate Sue A Email content with Preferred Name change.");

        logMessage("Item F8.6: Validate Sue A Email content with Marital Status change.");
        logMessage("Item F8.6", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20164, 20164,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.6: Validate Sue A Email content with Marital Status change.");

        logMessage("Item F8.7: Validate Jack F Email content with Preferred Name change.");
        logMessage("Item F8.7", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20165, 20165, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.7: Validate Jack F Email content with Preferred Name change.");

        logMessage("Item F8.8: Validate Jack F Email content with Marital Status change.");
        logMessage("Item F8.8", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20166, 20166, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.8: Validate Jack F Email content with Marital Status change.");

        myAssert.assertAll();
        logMessage("*** End of test F10111.");
    }

    @Test(priority = 10112) //a
    public void testF10112_ValidateProfileChangesAuditReportWhenApproveIsRequiredByAnyManagerInTheTeam1() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10112.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F8.8.X1: Validate Profile Changes Audit Report When Approve Is Required By AnyManager In the Team", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (71, 71, emailDomainName, testSerialNo, driver), true, "Failed in Item F8.8.X1: Validate Profile Changes Audit Report When Approve Is Required By AnyManager In the Team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10112.");
        myAssert.assertAll();
    }

    @Test (priority = 10121)
    public void testF10121_ValidateChangesBeforeApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10121.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Delete All emails.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Sharon A");
        WebDriver driver= GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item F8.9: Valdiate Sharon A Personal Information Page via Sharon A before approval");
        logMessage("Item F8.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item F8.9: Valdiate Sharon A Personal Information Page via Sharon A before approval");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Jack F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.10: Validate Other Approval List via Manager Jack F before Approval");
        logMessage("Item F8.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item F8.10: Validate Other Approval List via Manager Jack F before Approval");

        logMessage("Item F8.11: Validate Manager Jack F Dashboard - Profile Change panel before Approval");
        logMessage("Item F8.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20141, 20141, testSerialNo, emailDomainName, driver), true, "Failed in Item F8.11: Validate Manager Jack F Dashboard - Profile Change panel before Approval.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sue A.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.12: Validate Approval List via Manager Sue A before approval");
        logMessage("Item F8.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item F8.12: Validate Approval List via Manager Sue A before approval");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10121.");
        myAssert.assertAll();
    }

    @Test (priority = 10131)
    public void testF10131_ApproveAndDeclineProfileChangeAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10131.");
        logMessage("Logon as Sue A.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.13: Approve Sharon A the Preferred Name change via Sue A");
        logMessage("Item F8.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item F8.13: Approve Sharon A the Preferred Name change via Sue A");

        logMessage("Item F8.14: Reject the Sharon A Marital Status change via Sue A");
        logMessage("Item F8.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20071, 20071, testSerialNo, emailDomainName, driver), true, "Falied in Item F8.14: Reject the Sharon A Marital Status change via Sue A");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10131.");
        myAssert.assertAll();
    }

    @Test (priority=10141)
    public static void testF10141_ValidateAllEmailAfterApprovalAndReject() throws Exception {
        logMessage("*** Start Test F10141.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F8.15: Validate Sharon A Email content with Preferred name change");
        logMessage("Item F8.15", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20171, 20171, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.15: Validate Sharon A Email content with Preferred Name change.");

        logMessage("Item F8.16: Validate Jack F Email content with Preferred name change.");
        logMessage("Item F8.16", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20172, 20172,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.16: Validate Jack F Email content with Preferred name change.");

        logMessage("Item F8.17: Validate Sharon A Email content with Marital Status change");
        logMessage("Item F8.17", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20173, 20173, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.17: Validate Sharon A Email content with Marital Status change");

        logMessage("Item F8.18: Validate Jack F Email content with Marital Status change.");
        logMessage("Item F8.18", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20174, 20174, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F8.18: Validate Jack F Email content with Marital Status change.");

        logMessage("*** End of Test F10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10142) //a
    public void testF10142_ValidateProfileChangesAuditReportWhenApproveIsRequiredByAnyManagerInTheTeam2() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10142.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F8.18.X1: Validate Profile Changes Audit Report When Approve Is Required By AnyManager In the Team - Approve and Reject.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (81, 81, emailDomainName, testSerialNo, driver), true, "Failed in Item Item F8.18.X1: Validate Profile Changes Audit Report When Approve Is Required By AnyManager In the Team - Approve And Reject.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10142.");
        myAssert.assertAll();
    }

    @Test (priority = 10151)
    public void testF10151_valiateApprovalPageViaSueAAndFackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10151.");
        logMessage("Logon as Sue A.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.19: Validate Approval List via Sue A");
        logMessage("Item F8.19", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Falied in Item F8.19: Validate Approval List via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Jack F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F8.20: Validate Approval List via Jack F");
        logMessage("Item F8.20", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Falied in Item F8.20: Validate Approval List via Jack F");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10151.");
        myAssert.assertAll();
    }

    @Test (priority = 10161)
    public void testF10161_ActivateJennifer() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F9.1: Activate Jennifer H");
        logMessage("Item F9.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F9.1: Activate Jennifer H");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10161.");
        myAssert.assertAll();
    }


    @Test (priority = 10171)
    public void testF10171_EditJenniferHDetail() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10171.");

        logMessage("Item F9.2: Logon as Jennifer H.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item F9.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F9.2: Logon as Jennifer H.");

        logMessage("Item F9.3: Validate Approval Process via Jennifer A.");
        logMessage("Item F9.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.3: Validate Approval Process via Jennifer A.");

        logMessage("Item F9.4: Add Middle name Kate via Jennifer H");
        logMessage("Item F9.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20031, 20031, testSerialNo, driver), true, "Failed in Item F9.4: Add Middle name Kate via Jennifer H");

        logMessage("Item F9.5: Edit home number 02 3333 2222 via Jennifer H");
        logMessage("Item F9.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20021, 20021, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.5: Edit home number 02 3333 2222 via Jennifer H");

        logMessage("Item F9.6: Validate Jennifer H Personal Information page via Jennifer H");
        logMessage("Item F9.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.6: Validate Jennifer H Personal Information page via Jennifer H");

        logMessage("Item F9.7: Validate Jennifer H Contact Detail Page via Jennifer H");
        logMessage("Item F9.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.7: Validate Jennifer H Contact Detail Page via Jennifer H");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10171.");
        myAssert.assertAll();
    }

    @Test (priority = 10181)
    public static void testF10181_ValidateAllEmailAfterProfileChange() throws Exception {
        logMessage("*** Start test F10181.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F9.8: Validate Jennifer H email content for name change.");
        logMessage("Item F9.8", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20181, 20181, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.8: Validate Jennifer H email content for name change.");

        logMessage("Item F9.9: Validate Jennifer H email content for contact change.");
        logMessage("Item F9.9", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20182, 20182,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.9: Validate Jennifer H email content for contact change.");

        logMessage("Item F9.10: Validate Manager Sue A email content for name change.");
        logMessage("Item F9.10", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20183, 20183, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.10: Validate Jennifer H email content for name change.");

        logMessage("Item F9.11: Validate Manager Sue A email content for contact change.");
        logMessage("Item F9.11", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20184, 20184, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.11: Validate Manager Sue A email content for contact change.");

        myAssert.assertAll();
        logMessage("*** End of test F10181.");
    }

    @Test (priority = 10191)
    public void testF10191_ApproveAllChangeViaManagerSueA() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10191.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Sue A");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F9.12: Validate Sue A Dashboard via Sue A for 2 profile change");
        logMessage("Item F9.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.12: Validate Sue A Dashboard via Sue A for 2 profile change.");

        logMessage("Item F9.13: Validate Sue A approval page.");
        logMessage("Item F9.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.13: Validate Sue A approval page.");

        logMessage("Item F9.14: Approve All change via Manager Sue A.");
        logMessage("Item F9.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20081, 20081 ,testSerialNo, emailDomainName, driver), true, "Failed in Item F9.14: Approve All change via Manager Sue A.");

        logMessage("Item F9.15: Validate Sue A Approval Page via Sue A.");
        logMessage("Item F9.15", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovalsPage(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.15: Validate Sue A Approval Page via Sue A.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10191.");
        myAssert.assertAll();

    }

    @Test (priority = 10201)
    public void testF10201_ValidateJenniferHApprovalStatusAfterApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10201.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F9.16: Validate Jennifer H email content for name change after approval.");
        logMessage("Item F9.16", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20191, 20191, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.16: Validate Jennifer H email content for Name change after approval.");

        logMessage("Item F9.17: Validate Jennifer H email content for contact change after approval.");
        logMessage("Item F9.17", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20192, 20192,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F9.17: Validate Jennifer H email content for Contact change after approval.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Log on as Jennifer H.");
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F9.18: Validate Jennifer H Personal Information page after approval.");
        logMessage("Item F9.18", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item F9.18: Validate Jennifer A Personal informaiton page after approval.");

        logMessage("Item F9.19: Validate Jennifer H Contact Deails page after approval.");
        logMessage("Item F9.19", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20041, 20041, testSerialNo,  emailDomainName, driver), true, "Failed in Item F9.19: Validate Jennifer H contact detail after approval.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10201.");
        myAssert.assertAll();
    }

    @Test (priority = 10211)
    public void testF10211_ActivateMitchellSViaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10211.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F10.1: Activate Mitchell S.");
        logMessage("Item F10.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F10.1: Activate Mitchell S.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10211.");
        myAssert.assertAll();
    }

    @Test (priority = 10221)
    public void testF10221_EditMitchellSProfile() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10221.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F10.2: Validate Approval Process vis Mitchel S.");
        logMessage("Item F10.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.2: Validate Approval Process vis Mitchel S.");

        logMessage("Item F10.3: Edit Mobile Phone number via Mitchell S");
        logMessage("Item F10.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20051, 20051, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.3: Edit Mobile Phone number via Mitchell S");

        logMessage("Item F10.4: Edit Medical Condition vis Mitchell S");
        logMessage("Item F10.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20061, 20061, testSerialNo, driver), true, "Failed in Item F10.4: Edit Medical Condition vis Mitchell S");

        logMessage("Item F10.5: Validate Mitchell S Persson Information page.");
        logMessage("Item F10.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.5: Validate Mitchell S Persson Information page.");

        logMessage("Item F10.6: Validate Mitchell S Contact Detail page.");
        logMessage("Item F10.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.6: Validate Mitchell S Contact Detail page.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10221.");
        myAssert.assertAll();
    }

    @Test (priority = 10225)
    public void testF10225_ValidateAllEmailBeforeProfileApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10225.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F10.7: Validate Mitchell S email content for Contact change Before approval.");
        logMessage("Item F10.7", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20201, 20201, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.7: Validate Mitchell S email content for Contact change Before approval.");

        logMessage("Item F10.8: Validate Mitchell S email content for Medical Conditions change Before approval.");
        logMessage("Item F10.8", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20202, 20202, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.8: Validate Mitchell S email content for Medical Conditions change Before approval.");

        logMessage("Item F10.9: Validate Admin email content for Contact change Before approval.");
        logMessage("Item F10.9", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20203, 20203, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.9: Validate Admin email content for Contact change Before approval.");

        logMessage("Item F10.10: Validate Admin email content for Contact change Before approval.");
        logMessage("Item F10.10", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20204, 20204, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.10: Validate Admin email content for Medical Condition change Before approval.");

        SystemLibrary.logMessage("*** End of Test F10225.");
        myAssert.assertAll();
    }

    @Test (priority = 10231)
    public static void testF10231_ApprovleChangeViaAdmin() throws Exception {
        logMessage("*** Start Test F10231");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delelate All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F10.11: Validate Other Approval List via Admin");
        logMessage("Item F10.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.11: Validate Other Approval List via Admin");

        logMessage("Item F10.12: Validate My Approval List via Admin");
        logMessage("Item F10.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.12: Validate My Approval List via Admin");

        logMessage("Item F10.13: Approve all my Approval via Admin.");
        logMessage("Item F10.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20091, 20091, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.13: Approlve all profile change via Admin");

        logMessage("Item F10.14: Validate My Approval List via Admin");
        logMessage("Item F10.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item F10.14: Validate My Approval List after approval via Admin");

        logMessage("Item F10.15: Validate Mitchell S email content for Contact change after approval.");
        logMessage("Item F10.15", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20211, 20211,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.15: Validate Mitchell S email content for Contact change After approval.");

        logMessage("Item F10.16: Validate Mitchell S email content for Medical Conditions change After approval.");
        logMessage("Item F10.16", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20212, 20212, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F10.16: Validate Mitchell S email content for Medical Conditions change After approval.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10231");
        myAssert.assertAll();

    }

    @Test (priority = 10241)
    public void testF10241_ValidateMitchellSProfileViaMitchellS() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10241.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F10.17: Validate Mitchell S Personal Information page Via Mitchell S after approval");
        logMessage("Item F10.17", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20081, 20081, testSerialNo, emailDomainName, driver), true, "Failed in Item 10.17: Validate Mitchell S Personal Information page Via Mitchell S after approval.");

        logMessage("Item F10.18: Validate Mitchell S Contact Details page Via Mitchell S after approval");
        logMessage("Item F10.18", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item 10.18: Validate Mitchell S Contact Dtails page Via Mitchell S after approval.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10241");
        myAssert.assertAll();

    }


    @Test (priority = 10251)
    public void testF10251_EditProfileChangeToAllMamagers() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10251.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.1: Edit Profile change workflow - change any manager to all managers");
        logMessage("Item F11.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20041, 20041, emailDomainName, testSerialNo, driver), true, "Failed in Item F11.1: Edit Profile change workflow - change any manager to all managers");

        logMessage("Item F11.2: Validate Profile Change workflow after edting via Admin.");
        logMessage("Item F11.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20051, 20051, emailDomainName, testSerialNo, driver), true, "Failed in Item F11.2: Validate Profile Change workflow after edting via Admin.");

        logMessage("Item F11.3: Validate Christine R Approval Process via Admin");
        logMessage("Item F11.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.3: Validate Christine R Approval Process via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10251");
        myAssert.assertAll();

    }

    @Test (priority = 10261)
    public void testF10261_EditSharonAResidentialAddressAndEmergencyContactViaSharonA() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10261.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20161, 20161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.4: Validate Sharon A Approval Process via Sharon A");
        logMessage("Item F11.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20081, 20081, emailDomainName, testSerialNo, driver), true, "Failed in Item F11.4: Validate Sharon A Approval Process via Sharon A");

        logMessage("Item F11.5: Edit Sharon A Residential Address via Sharon A");
        logMessage("Item F11.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.5: Edit Sharon A Residential Address via Sharon A");

        logMessage("Item F11.6: Add Sharon A Emergency Contact via Sharon A");
        logMessage("Item F11.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20082, 20082, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.6: Add Sharon A Emergency Contact via Sharon A");

        logMessage("Item F11.7: Validate Sharon A Contact page via Sharon A");
        logMessage("Item F11.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20083, 20083, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.6: Add Sharon A Emergency Contact via Sharon A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10261");
        myAssert.assertAll();

    }

    @Test (priority = 10271)
    public void testF10271_ValidateAllEmailBeforeProfileApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10271.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F11.8.1: Validate Sharon A email content for Addres Change.");
        logMessage("Item F11.8.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20221, 20221, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.1: Validate Sharon A email content for Addres Change.");

        logMessage("Item F11.8.2: Validate Sharon A email content for Emergency Contact change");
        logMessage("Item F11.8.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20222, 20222, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.2: Validate Sharon A email content for Emergency Contact change");

        logMessage("Item F11.8.3: Validate Sue A email content for Addres Change.");
        logMessage("Item F11.8.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20223, 20223, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.3: Validate Sue A email content for Addres Change.");

        logMessage("Item F11.8.4: Validate Sue A email content for Emergency Contact change");
        logMessage("Item F11.8.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20224, 20224, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.4: Validate Sue A email content for Emergency Contact change");

        logMessage("Item F11.8.5: Validate Jack F email content for Addres Change.");
        logMessage("Item F11.8.5", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20225, 20225, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.5: Validate Jack F email content for Addres Change.");

        logMessage("Item F11.8.6: Validate Jack F email content for Emergency Contact change");
        logMessage("Item F11.8.6", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20226, 20226, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.8.6: Validate Jack F email content for Emergency Contact change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10271.");
        myAssert.assertAll();
    }

    @Test (priority = 10281)
    public void testF10281_ValidateApprovalPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10281.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Fack F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.9: Valiate Jack F Approval page via Jack F");
        logMessage("Item F11.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.9: Valiate Jack F Approval page via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sue A");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.10: Valiate Sue A Approval page via Sue A");
        logMessage("Item F11.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20141, 20141, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.10: Valiate Sue A Approval page via Jack F");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10281");
        myAssert.assertAll();
    }

    @Test(priority = 10282) //a
    public void testF10282_ValidateProfileChangesAuditReportWhenApproveIsRequiredByALLManagerInTheTeam3() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10282.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F11.10.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager In the Team.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (91, 91, emailDomainName, testSerialNo, driver), true, "Failed in Item Item F11.10.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager In the Team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10282.");
        myAssert.assertAll();
    }

    @Test (priority = 10291)
    public void testF10291_ApprovleEmergencyContactViaSueAAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10291.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.11: Approve Sharon A Emergency Contact via Sue A");
        logMessage("Item F11.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item Item F11.11: Approve Sharon A Emergency Contact via Sue A");

        logMessage("Item F11.12: Validate Sue A approval page after approval via Sue A");
        logMessage("Item F11.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.12: Validate Sue A approval page after approval via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Fack F");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.13: Valiate Jack F Approval page after Sue A approve Sharon A Emergency Contact via Jack F");
        logMessage("Item F11.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.13: Valiate Jack F Approval page after Sue A approve Sharon A Emergency Contact via Jack F");

        logMessage("Item F11.14: Validate Jack F email content for Emergency Contact approval by Sue A");
        logMessage("Item F11.14", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20231, 20231, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.14: Validate Jack F email content for Empergency Contact approval by Sue A");

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10291");
        myAssert.assertAll();
    }

    @Test(priority = 10292) //a
    public void testF10292_ValidateProfileChangesAuditReportWhenApproveIsRequiredByALLManager_1ManagerApproved() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10292.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F11.14.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager In the Team - One Manger Approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (101, 101, emailDomainName, testSerialNo, driver), true, "Failed in Item Item F11.14.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager - One Manager Approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10292.");
        myAssert.assertAll();
    }

    @Test (priority = 10301)
    public void testF10301_ApprovleEmergencyContactViaJackFAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10301.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Fack F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.15: Approve Sharon A Emergency Contact via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.15: Approve Sharon A Emergency Contact via Jack F");

        logMessage("Item F11.16: Validate Sharon A email content after Emergency Contact approved by both Sue A and Jack F", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20241, 20241, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.16: Validate Sharon A email content after Emergency Contact approved by both Sue A and Jack F");

        logMessage("Item F11.17.1: Validate Jack F Approvla page after Jack F approve Sharon A Emergency Contact change.");
        logMessage("Item F11.17", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20171, 20171, testSerialNo, emailDomainName, driver), false, "Failed in Item F11.17: Validate Jack F Approvla page after Jack F approve Sharon A Emergency Contact change.");

        logMessage("Item F11.17.2: Validate Jack F Approvla page after Jack F approve Sharon A Emergency Contact change.");
        logMessage("Item F11.17", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20172, 20172, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.17_2: Validate Jack F Approvla page after Jack F approve Sharon A Emergency Contact change.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10301");
        myAssert.assertAll();
    }

    @Test(priority = 10302) //a
    public void testF10302_ValidateProfileChangesAuditReportWhenApproveIsRequiredByALLManager_2ndManagerApproved() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10302.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F11.17.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager In the Team - 2nd Manger Approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (111, 111, emailDomainName, testSerialNo, driver), true, "Failed in Item Item F11.14.X1: Validate Profile Changes Audit Report When Approve Is Required By All Manager - the 2nd Manager Approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10302.");
        myAssert.assertAll();
    }

    ////////////////// From Govinda on 22/07/2019
    @Test (priority = 10311)
    public void testF10311_DeclineSharonAddressByJackFAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10311.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Logon as Jack F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.18: Decline Sharon A address change via Jack F");
        logMessage("Item F11.18", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.18: Decline Sharon A address change via Jack F");


        logMessage("Item F11.19: Validate Jack F Dashboard after Decline Sharon Address");
        logMessage("Item F11.19", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.19: Validate Jack F Dashboard after Decline Sharon Address.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sue A");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F11.20: Validate Sue A Approval Page via Sue A after Jack F decline Sharon A address change.");
        logMessage("Item F11.20", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.20: Validate Jack F Approvla page after Jack F approve Sharon A Emergency Contact change.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10311");
        myAssert.assertAll();

    }

    @Test (priority = 10321)
    public void testF10321_ValidateAllEmailsAfterJackFDeclineSharonAAddressChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10321.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F11.21.1:Validate Manager Sue A email content after Manager Jack F decline Sharon A address change");
        logMessage("Item F11.21.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20251, 20251, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.21.1: Validate Manager Sue A email content after Manager Jack F decline Sharon A address change");

        logMessage("Item F11.21.2: Validate Sharon A email content after Manager Jack F decline Sharon A address change");
        logMessage("Item F11.21.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20252, 20252, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F11.21.2: Validate Sharon A email content after Manager Jack F decline Sharon A address change");


        SystemLibrary.logMessage("*** End of Test F10321.");
        myAssert.assertAll();

    }

    //////////////// From Govinda on 23/07/2019

    @Test (priority = 10322)
    public void testF10322_AddMiddileName_DeleteMedicalCondition_Mitchell () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10322.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Mitchell SMART");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F12.1: Validate Mitchell S Approval process via Mitchell S");
        logMessage("Item F12.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20101, 20101, testSerialNo, emailDomainName, driver), true, "Failed in Item F12.1: Validate Mitchell S Approval process via Mitchell S.");

        logMessage("Item F12.2: Add Mitchell S Middle name via Mitchell S");
        logMessage("Item F12.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20091, 20091, testSerialNo, driver), true, "Failed in Item F12.2: Add Mitchell S Middle name via Mitchell S");

        logMessage("Item F12.3: Delete Mitchell S Medical Condition via Mitchell S");
        logMessage("Item F12.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20101, 20101, testSerialNo, driver), true, "Failed in Step F12.3: Delete Mitchell S Medical Condition via Mitchell S.");

        logMessage("Item F12.4: Validate Mitchell S Personal Information page Via Mitchell S after Changes");
        logMessage("Item F12.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in F12.4: Validate Mitchell S Personal Information page Via Mitchell S after Changes");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10322");
        myAssert.assertAll();

    }

    @Test (priority = 10332)
    public void testF10332_ValidateAllEmails_AfterDeleteMiddleName_AddMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10332.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F12.5.1: Validate Admin emails for Name Change");
        logMessage("Item F12.5.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20261, 20261, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F12.5.1: Validate Admin emails for Name Change");

        logMessage("Item F12.5.2: Validate Mitchell S email for Name Change");
        logMessage("Item F12.5.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20262, 20262, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F12.5.2: Validate Mitchell S email for Name Change");

        logMessage("Item F12.5.3: Validate Admin emails for Medical Condition Change");
        logMessage("Item F12.5.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20263, 20263,payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F12.5.1: Item F12.5.3: Validate Admin emails for Medical Condition Change");

        logMessage("Item F12.5.4: Validate Mitchell S email for Medical Condition Change");
        logMessage("Item F12.5.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20264, 20264, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F12.5.4: Validate Mitchell S email for Medical Condition Change");

        SystemLibrary.logMessage("*** End of Test F10332.");
        myAssert.assertAll();

    }


    @Test (priority = 10342)
    public void testF10342_DeclineProfileChangesviaAdminAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10342.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F12.6: Validate Admin Dashboard via Admin for 2 profile change");
        logMessage("Item F12.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20171, 20171, testSerialNo, emailDomainName, driver), true, "Failed in Item Item F12.6: Validate Admin Dashboard via Admin for 2 profile change");

        logMessage("Item F12.7: Validate My Approval List via Admin");
        logMessage("Item F12.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item F12.7: Validate My Approval List via Admin");

        logMessage("Item F12.8: Decline Mitchell S Middle name change and Medical Condition change via Admin");
        logMessage("Item F12.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item F12.8: Decline Mitchell S Middle name change and Medical Condition change via Admin");

        logMessage("Item F12.9: Validate My Approval List after Decline via Admin");
        logMessage("Item F12.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20201, 20201, testSerialNo, emailDomainName, driver), true, "Failed in Item F12.7: Validate My Approval List after Decline via Admin");

        logMessage("Item F12.10: Validate Admin Dashboard via Admin after Decline profile change");
        logMessage("Item F12.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item Item F12.9: Validate Admin Dashboard via Admin after Decline profile change");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10342");
        myAssert.assertAll();

    }


    @Test (priority = 10352)
    public void testF10352_ValidateAllEmailsviaMitchell_ValdiatePersonalInfoPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10332.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F12.11.1: Validate emails after Decline Middle name via Mitchell S");
        logMessage("Item F12.11.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20271, 20271, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F12.11.1: Validate emails after Decline Middle name via Mitchell S");

        logMessage("Item Item F12.11.2: Validate emails after Decline Medical Condition via Mitchell S");
        logMessage("Item F12.11.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20272, 20272, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F12.11.2: Validate emails after Decline Medical Condition via Mitchell S");

        logMessage("Logon as Mitchell SMART");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F12.12: Valdiate Mitchell S Personal Information Page via Mitchell S");
        logMessage("Item F12.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in F12.12: Valdiate Mitchell S Personal Information Page via Mitchell S");

        SystemLibrary.logMessage("*** End of Test F10352.");
        myAssert.assertAll();

    }

    ////////////////// From Govinda on 26/07/2019

    @Test (priority = 10362)
    public void testF10362_EditProfileChangeTosendNotificationAsMember() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10362.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F13.1: Edit Profile change workflow - send a notification as a Member");
        logMessage("Item F13.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20061, 20061, emailDomainName, testSerialNo, driver), true, "Failed in Item F13.1: Edit Profile change workflow - send a notification as a Member");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10362");
        myAssert.assertAll();

    }

    @Test (priority = 10372)
    public void testF10363_ValidateAndChangeContactDetailsOfSharonA() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10372.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Logon as Sharon Andrew.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F13.2: Validate Sharon A Dashboard via Sharon A");
        logMessage("Item F13.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item Item Item F13.2: Validate Sharon A Dashboard via Sharon A");

        logMessage("Item F13.3: Validate Sharon A Approval process via Sharon A");
        logMessage("Item F13.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20111, 20111, testSerialNo, emailDomainName, driver), true, "Failed in Item F13.3: Validate Sharon A Approval process via Sharon A");

        logMessage("Item F13.4: Change Sharon A existing Postal Address via Sharon A");
        logMessage("Item F13.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20091, 20091, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F13.4: Change Sharon A existing Postal Address via Sharon A");

        logMessage("Item F13.5: Change Sharon A existing Personal email via Sharon A");
        logMessage("Item F13.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20092, 20092, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F13.5: Change Sharon A existing Personal email via Sharon A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10372");
        myAssert.assertAll();

    }

    @Test(priority = 10373) //a
    public void testF10373_ValidateProfileChangesAuditReportWhenSendNotification() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10373.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F13.5.X1: Validate Profile Changes Audit Report When Send Notification.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (121, 121, emailDomainName, testSerialNo, driver), true, "Failed in Item F13.5.X1: Validate Profile Changes Audit Report When Send Notification.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10373.");
        myAssert.assertAll();
    }

    @Test (priority = 10381)
    public void testF10381_ValidateSharonAMmanageremailsAfterContactUpdate () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10381.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item F13.6.1: Validate Sue A email content for Sharon A Addres Change.");
        logMessage("Item F13.6.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20281, 20281, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F13.6.1: Validate Sue A email content for Sharon A Addres Change.");

        logMessage("Item F13.6.2: Validate Sue A email content for Sharon A Personal email change.");
        logMessage("Item F13.6.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20282, 20282, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F13.6.2: Validate Sue A email content for Sharon A Personal email change.");

        logMessage("Item F13.6.3: Validate Jack F email content for Sharon A Addres Change.");
        logMessage("Item F13.6.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20283, 20283, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F13.6.3: Validate Jack F email content for Sharon A Addres Change.");

        logMessage("Item F13.6.4: Validate Jack F email content for Sharon A Personal email change");
        logMessage("Item F13.6.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20284, 20284, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F13.6.4: Validate Jack F email content for Sharon A Personal email change");

        logMessage("Item F13.7.1: Validate Sharon A email content for Sharon A Addres Change.");
        logMessage("Item F13.7.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20285, 20285, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F13.7.1: Validate Sharon A email content for Sharon A Addres Change.");

        logMessage("Item F13.7.2: Validate Sharon A email content for Sharon A Personal email change");
        logMessage("Item F13.7.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20286, 20286, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F13.7.2: Validate Sharon A email content for Sharon A Personal email change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10381.");
        myAssert.assertAll();

    }



    @Test (priority = 10391)
    public static void testF10391_ValidateOtherApprovalViaAdmin() throws Exception {
        logMessage("*** Start Test F10391");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F13.8: Validate Other Approval List via Admin");
        logMessage("Item F13.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item F13.8: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10391");
        myAssert.assertAll();

    }

    @Test (priority = 10401)
    public void testF10401_EditProfileChangeTosendNotificationAsMember() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10441.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F14.1: Change Managers Roles - Personal Contact from View to Edit via Admin");
        logMessage("Item F14.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20051, 20051, driver), true, "Failed in Item F14.1: Change Managers Roles - Personal Contact from View to Edit via Admin");

        logMessage("Item F14.2: Change Managers Roles - Work Contact from View to Edit via Admin");
        logMessage("Item F14.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20061, 20061, driver), true, "Failed in Item F14.2: Change Managers Roles - Work Contact from View to Edit via Admin");

        logMessage("Item F14.3: Validate Managers Role and Permission page after Edit Personal and Work Contact details.");
        logMessage("Item F14.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20071, 20071, emailDomainName, testSerialNo, driver), true, "Failed in Item F14.3: Validate Managers Role and Permission page after Edit Personal and Work Contact details.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10401");
        myAssert.assertAll();

    }

    @Test(priority = 10411)
    public static void testF10411_AddSharonWorkContactOfficeNumberViaJackF() throws Exception {
        logMessage("*** Start Test 10411.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Jack F");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F14.4: Add Sharon A Work Contact Office number Via Jack F. ");
        logMessage("Item F14.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F14.4: Add Sharon A Work Contact Office number Via Jack F.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        logMessage("Item F14.5.1: Validate Jack F email content for Sharon A Work Contact Change.");
        logMessage("Item F14.5.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20291, 20291, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in F14.5.1: Validate Jack F email content for Sharon A Work Contact Change.");

        logMessage("Item F14.5.2: Validate Sue A email content for Sharon A Work Contact Change");
        logMessage("Item F14.5.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20292, 20292, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F14.5.2: Validate Sue A email content for Sharon A Work Contact Change");

        logMessage("Item F14.5.3: Validate Sharon A email content for Sharon A Work Contact Change");
        logMessage("Item F14.5.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20293, 20293, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F14.5.3: Validate Sharon A email content for Sharon A Work Contact Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("End of Test 10411.");
        myAssert.assertAll();

    }

    @Test(priority = 10412) //a
    public void testF10412_ValidateProfileChangesAuditReportWhenSendNotificationAsMember_ManagerApply() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10142.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F15.1.X1: Validate Profile Changes Audit Report When Send Notification As Member_Manager Apply.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (131, 131, emailDomainName, testSerialNo, driver), true, "Failed in Item F15.1.X1: Validate Profile Changes Audit Report When Send Notification As Member_Manager Apply.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10142.");
        myAssert.assertAll();
    }

    /////////////// Add on 02/08/2019  /////////////////////

    @Test (priority = 10421)
    public void testF10421_EditProfileChange_Donothing() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10421.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F15.1: Edit Profile change workflow - do nothing via Admin");
        logMessage("Item F15.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20071, 20071, emailDomainName, testSerialNo, driver), true, "Failed in Item F15.1: Edit Profile change workflow - do nothing via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10421");
        myAssert.assertAll();
    }

    @Test (priority = 10431)
    public void testF10431_ValidateAndChangePersonalInfoPhantomF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10431.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Phantom Fry.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10161, 10161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F15.2: Validate Phantom F Dashboard via Phantom F");
        logMessage("Item F15.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20201, 20201, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.2: Validate Phantom F Dashboard via Phantom F");

        logMessage("Item F15.3: Validate Phantom F Team Role - Approval process via Phantom F");
        logMessage("Item F15.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.3: Validate Phantom F Team Role - Approval process via Phantom F");

        logMessage("Item F15.4: Change Phantom F existing first name via Phantom F");
        logMessage("Item F15.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20221, 20221, testSerialNo, driver), true, "Failed in Item F15.4: Change Phantom F existing first name via Phantom F");

        logMessage("Item F15.5:Validate Phantom F  Personal Information via Phantom F");
        logMessage("Item F15.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.5:Validate Phantom F  Personal Information via Phantom F");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10431");
        myAssert.assertAll();
    }

    @Test(priority = 10432) //a
    public void testF10432_ValidateProfileChangesAuditReportWhenSetToDoDothing() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10432.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F15.5.X1: Validate Profile Changes Audit Report When Set To Do Dothing - Member apply change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (141, 141, emailDomainName, testSerialNo, driver), true, "Failed in Item F15.5.X1: Validate Profile Changes Audit Report When Set To Do Dothing - Member apply change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10432.");
        myAssert.assertAll();
    }

    @Test(priority = 10441)
    public static void testF10441_AddPhantomFContactDetailsViaSueA() throws Exception {
        logMessage("*** Start Test 10441.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F15.6: Validate Sue A Dashboard via Sue A for profile change");
        logMessage("Item F15.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.6: Validate Sue A Dashboard via Sue A for profile change.");

        logMessage("Item F15.7: Validate Sue A Approval Page via Sue A");
        logMessage("Item F15.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20221, 20221, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.7: Validate Sue A Approval Page via Sue A.");

        logMessage("Item F15.8: Validate Sue A Teams and Roles - Approval Process via Sue A");
        logMessage("Item F15.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20141, 20141, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.8: Validate Sue A Teams and Roles - Approval Process via Sue A");

        logMessage("Item F15.9: Validate Sue A Teams and Roles via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.9: Validate Sue A Teams and Roles via Sue A");

        logMessage("Item F15.10: Add Phantom F Personal email via Sue A");
        logMessage("Item F15.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20111, 20111, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.10: Add Phantom F Personal email via Sue A");

        logMessage("Item F15.11: Validate Phantom F Contact Detail page via Sue A.");
        logMessage("Item F15.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20121, 20121, testSerialNo, emailDomainName, driver), true, "Failed in Item F15.11: Validate Phantom F Contact Detail page via Sue A.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10441.");
        myAssert.assertAll();
    }

    @Test(priority = 10442) //a
    public void testF10442_ValidateProfileChangesAuditReportWhenSetToDoDothing_ManagerApplyChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10442.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F15.11.X1: Validate Profile Changes Audit Report When Set To Do Dothing - Manager apply change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (151, 151, emailDomainName, testSerialNo, driver), true, "Failed in Item F15.11.X1: Validate Profile Changes Audit Report When Set To Do Dothing - Manager apply change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10442.");
        myAssert.assertAll();
    }

    @Test (priority = 10451)
    public void testF10451_ValidatePhantomFemailsAfterContactUpdate () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10451.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F15.12.1: Validate Sue A email content for Phantom F First Name Change");
        logMessage("Item F15.12.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20311, 20311, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F15.12.1: Validate Phantom A email content for Phantom F First Name Change");

        logMessage("Item F15.12.2: Validate Phantom F email content for Phantom F First Name Change");
        logMessage("Item F15.12.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20312, 20312, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F15.12.2: Validate Phantom F email content for Phantom F First Name Change");

        logMessage("Item F15.12.3: Validate Phantom F email content for Phantom F Personal email");
        logMessage("Item F15.12.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20313, 20313, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F15.12.3: Validate Phantom F email content for Phantom F Personal email");

        SystemLibrary.logMessage("*** End of Test F10451.");
        myAssert.assertAll();

    }

    ////////////////////  F16 /////////////////
    @Test (priority = 10461)
    public void testF10461_EditProfileChange_ALLapproversTheApproverFromAnotherTeam() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10461.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F16.1: Edit Profile change workflow - then:approval is required;by:all managers;in:Team C;and then:do nothing via Admin");
        logMessage("Item F16.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20081, 20081, emailDomainName, testSerialNo, driver), true, "Failed in Item F16.1: Edit Profile change workflow - then:approval is required;by:all managers;in:Team C;and then:do nothing via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10461");
        myAssert.assertAll();

    }

    @Test (priority = 10471)
    public void testF10471_ValidateAndChangePersonalInfoTanyaD() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10471.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Tanya D");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10261, 10261, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F16.2: Validate Tanya D Dashboard via Tanya D");
        logMessage("Item F16.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20221, 20221, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.2: Validate Tanya D Dashboard via Tanya D");

        logMessage("Item F16.3: Validate Tanya D Team Role - Approval process via Tanya D");
        logMessage("Item F16.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.3: Validate Tanya D Team Role - Approval process via Tanya D");

        logMessage("Item F16.4: Add Tanya D Bank Account Details via Tanya D");
        logMessage("Item F16.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.4: Add Tanya D Bank Account Details via Tanya D");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10471");
        myAssert.assertAll();
    }

    @Test(priority = 10481)
    public static void testF10481_ValidateBankAccountDetailsViaSueA() throws Exception {
        logMessage("*** Start Test 10481.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F16.5: Validate Sue A Dashboard via Sue A.");
        logMessage("Item F16.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.5: Validate Sue A Dashboard via Sue A.");

        logMessage("Item F16.6: Approve Tanya D Bank Account Change via Sue A");
        logMessage("Item F16.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20141, 20141, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.6: Approve Tanya D Bank Account Change via Sue A");

        logMessage("Item F16.7: Validate Sue A Approval List after Bank Details Approved via Sue A");
        logMessage("Item F16.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item F16.7: Validate Sue A Approval List after Bank Details Approved via Sue A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10481.");
        myAssert.assertAll();

    }

    @Test (priority = 10491)
    public void testF10491_ValidateTanyaDemailsAfterBankDetailsUpdate () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10491.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F16.8.1: Validate Tanya D email content for Tanya D Bank Account Change");
        logMessage("Item F16.8.1", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20321, 20321, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F16.8.1: Validate Tanya D email content for Tanya D Bank Account Change");

        logMessage("Item F16.8.2: Validate Steve B email content for Tanya D Bank Account Change");
        logMessage("Item F16.8.2", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20322, 20322, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F16.8.2: Validate Steve B email content for Tanya D Bank Account Change");

        logMessage("Item F16.8.3: Validate Sue A email content for Tanya D Bank Account Change");
        logMessage("Item F16.8.3", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20323, 20323, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F16.8.3: Validate Sue A email content for Tanya D Bank Account Change");

        logMessage("Item F16.8.4: Validate Tanya D email content after Tanya D Bank Account Approve");
        logMessage("Item F16.8.4", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20324, 20324, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F16.8.4: Validate Tanya D email content after Tanya D Bank Account Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10451.");
        myAssert.assertAll();

    }

    /////////////////////// F17 //////////////////////////////
    @Test(priority = 10501)
    public static void testF10501_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10501.");

        logMessage("Item F17.1: Validate Employee Detail Report via Sage Micropay", testSerialNo);
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20031, 20031, emailDomainName, testSerialNo), true, "Failed in Item F17.1: Validate Employee Detail Report via Sage Micropay");

        exitMeridian();
        logMessage("End of Test 10501.");
        myAssert.assertAll();
    }

    @Test(priority = 10511)
    public static void testF10511_InImplementEHRviaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10511.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item F17.2.1: Implement eHR via Sage Micropay the 1st time.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item F17.2.1: Implement eHR via Sage Micropay the 1st time.");
        close_ImplementHRScreen();
        AutoITLib.exitMeridian();
            /*
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item F17.2.2: Implement eHR via Sage Micropay the 2nd time.");
        logMessage("Item F17.2.2", testSerialNo);

        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item F17.2.2: Implement eHR via Sage Micropay the 2nd time.");
        close_ImplementHRScreen();
        AutoITLib.exitMeridian();

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item F17.3: Validate Grid List in Implement eHR via Sage Micropay after implemanting eHR.");
        logMessage("Item F17.3", testSerialNo);

        myAssert.assertEquals(saveGridInImplementEHRScreen_Main(20041, 20041), true, "Failed in Item F17.3: Validate Grid List in Implement eHR via Sage Micropay after implemanting eHR.");
        exitMeridian();
        */

        logMessage("End of Test 10511.");
        myAssert.assertAll();
    }

    @Test(priority = 10521)
    public static void testF10521_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert=new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 10521.");

        logMessage("Item F17.4: Validate Employee Detail Report via Sage Micropay");
        logMessage("Item F17.4", testSerialNo);
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20051, 20051, emailDomainName, testSerialNo), true, "Failed in Item F17.4: Validate Employee Detail Report via Sage Micropay");

        exitMeridian();
        logMessage("End of Test 10501.");
        myAssert.assertAll();
    }

    /////////////////////// F18 From Govinda on 12/08/2019

    @Test (priority = 10531)
    public void testF10531_EditProfileChange_ALLapproversTheApproverFromAnotherTeam() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10531.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.1: Edit Profile change workflow - then:approval is required;by:any manager;in:the member's team;and then:approval is required;by:any manager;in:Team F via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20091, 20091, emailDomainName, testSerialNo, driver), true, "Failed in Item F18.1: Edit Profile change workflow - then:approval is required;by:any manager;in:the member's team;and then:approval is required;by:any manager;in:Team F via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10531");
        myAssert.assertAll();

    }

    @Test (priority = 10541)
    public void testF10541_ValidateAndChangeContactDetailsOfSharonA() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10541.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sharon Andrew.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.2: Validate Sharon A Dashboard via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.2: Validate Sharon A Dashboard via Sharon A");

        logMessage("Item F18.3: Validate Sharon A Approval process via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20171, 20171, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.3: Validate Sharon A Approval process via Sharon A");

        logMessage("Item F18.4: Edit Sharon A Surname as ANDREW via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20241, 20241, testSerialNo, driver), true, "Failed in Item F18.4: Edit Sharon A Surname as ANDREW via Sharon A");

        logMessage("Item F18.5: Add Sharon A Bank Account Details making it the Balance of Pay via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20012, 20012, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.5: Add Sharon A Bank Account Details making it the Balance of Pay via Sharon A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10541");
        myAssert.assertAll();
    }

    @Test(priority = 10542) //a
    public void testF10542_ValidateProfileChangesAuditReportWhenSetAnyManagerThenAnyMangger_MemberApplyChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10542.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F18.5.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger - Member Apply Change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (161, 161, emailDomainName, testSerialNo, driver), true, "Failed in Item F18.5.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger - Member Apply Change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10542.");
        myAssert.assertAll();
    }

    @Test (priority = 10551)
    public void testF10551_ValidateAllEmailAfterSharonA_EditSurnameBankAccount() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10551.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F18.6.1: Validate Sharon A email content for Sharon A Surname Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20421, 20421, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.6.1: Validate Sharon A email content for Sharon A Surname Change");

        logMessage("Item F18.6.2: Validate Sharon A email content for Sharon A Bank Account Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20422, 20422, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.6.2: Validate Sharon A email content for Sharon A Bank Account Change");

        logMessage("Item F18.6.3: Validate Sue A email content for Sharon A Surname Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20423, 20423, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.6.3: Validate Sue A email content for Sharon A Surname Change");

        logMessage("Item F18.6.4: Validate Sue A email content for Sharon A Bank Account Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20424, 20424, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.6.4: Validate Sue A email content for Sharon A Bank Account Change");

        logMessage("Item F18.6.5: Validate Jack F email content for Sharon A Surname Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20425, 20425, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item Item F18.6.5: Validate Jack F email content for Sharon A Surname Change");

        logMessage("Item F18.6.6: Validate Jack F email content for Sharon A Bank Account Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20426, 20426, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.6.6: Validate Jack F email content for Sharon A Bank Account Change");

        logMessage("Item F18.6.7: Validate Phanton F email content for Sharon A Surname Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20427, 20427, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.6.7: Validate Phanton F email content for Sharon A Surname Change");

        logMessage("Item F18.6.8: Validate Phanton F email content for Sharon A Bank Account Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20428, 20428, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.6.8: Validate Phanton F email content for Sharon A Bank Account Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10551.");
        myAssert.assertAll();

    }


    @Test (priority = 10561)
    public void testF10561_ValidateAndChangePersonalInfoPhantomF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10561.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Phantom Fry.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.7: Validate Phanton F Dashboard for pending approvals via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.7: Validate Phanton F Dashboard for pending approvals via Phanton F");

        logMessage("Item F18.8: Validate Phanton F Approval Page via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.8: Validate Phanton F Approval Page via Phanton F ");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Jack F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.9: Validate Jack F Dashboard for pending approvals via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.9: Validate Jack F Dashboard for pending approvals via Jack F");

        logMessage("Item F18.10: Validate Jack F Approval Page via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20242, 20242, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.10: Validate Jack F Approval Page via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10561");
        myAssert.assertAll();
    }

    @Test (priority = 10571)
    public void testF10571_ValidateAdminDashboardviaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10571.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.11: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.11: Validate Admin Dashboard via Admin");

        logMessage("Item F18.12: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20251, 20251, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.12: Validate My Approval List via Admin");

        logMessage("Item F18.13: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20252, 20252, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.13: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10571");
        myAssert.assertAll();

    }

    @Test(priority = 10581)
    public static void testF10581_ApproveSharonAPersonInfoDetailsValidateViaSueA() throws Exception {
        logMessage("*** Start Test 10581.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.14: Validate Sue A Dashboard for pending approvals via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20291, 20291, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.14: Validate Sue A Dashboard for pending approvals via Sue A");

        logMessage("Item F18.15: Validate Sue A Approval Page via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.15: Validate Sue A Approval Page via Sue A");

        logMessage("Item F18.16: Decline Sharon A Bank Account Details adding a comment via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.16: Decline Sharon A Bank Account Details adding a comment via Sue A");

        logMessage("Item F18.17: Approve Sharon A the Surname change via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20152, 20152, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.17: Approve Sharon A the Surname change via Sue A");

        logMessage("Item F18.18: Validate Sue A Approval Page via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20262, 20262, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.18: Validate Sue A Approval Page via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Sharon A.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.19: Validate Sharon A Personal Information page via  Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20251, 20251, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.19: Valdiate Sharon A Personal Information Page via Sharon A before approval");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10581.");
        myAssert.assertAll();

    }

    @Test(priority = 10582) //a
    public void testF10582_ValidateProfileChangesAuditReportWhenSetAnyManagerThenAnyMangger_1stManagerApproveChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10582.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F18.19.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger 1st Manager Approve Change", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (171, 171, emailDomainName, testSerialNo, driver), true, "Failed in Item F18.19.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger 1st Manager Approve Change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10582.");
        myAssert.assertAll();
    }

    @Test (priority = 10591)
    public void testF10591_ValidateAllEmailAfterSharonAApproveSurname_DeclineBankAcc() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10591.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F18.20.1: Validate Sharon A email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20431, 20431, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item Item F18.20.1: Validate Sharon A email content for Sharon A Approve Surname");

        logMessage("Item F18.20.2: Validate Sharon A email content for Sharon A Decline Bank Account", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20432, 20432, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.20.2: Validate Sharon A email content for Sharon A Decline Bank Account");

        logMessage("Item F18.20.3: Validate Sue A email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20433, 20433, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.20.3: Validate Sue A email content for Sharon A Approve Surname");

        logMessage("Item F18.20.4: Validate Sue A email content for Sharon A Decline Bank Account", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20434, 20434, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.20.4: Validate Sue A email content for Sharon A Decline Bank Account");

        logMessage("Item F18.20.5: Validate Jack F email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20435, 20435, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.20.5: Validate Jack F email content for Sharon A Approve Surname");

        logMessage("Item F18.20.6: Validate Jack F email content for Sharon A Decline Bank Account", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20436, 20436, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.20.6: Validate Jack F email content for Sharon A Decline Bank Account");

        logMessage("Item F18.20.7: Validate Phanton F email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20437, 20437, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.20.7: Validate Phanton F email content for Sharon A Approve Surname");

        logMessage("Item F18.20.8: Validate Phanton F email content for Sharon A Decline Bank Account", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20438, 20438, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.20.8: Validate Phanton F email content for Sharon A Decline Bank Account");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10591.");
        myAssert.assertAll();

    }

    @Test (priority = 10601)
    public void testF10601_ValidateSharonADashboard_ChangeContactDetailsViaJackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10601.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sharon Andrew.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.21: Validate Sharon A Dashboard via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.21: Validate Sharon A Dashboard via Sharon A");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Jack F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.22: Validate Jack F Dashboard via  Jack F ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20311, 20311, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.22: Validate Jack F Dashboard via  Jack F");

        logMessage("Item F18.23: Validate Jack F Approval Page via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.23: Validate Jack F Approval Page via Jack F");

        logMessage("Item F18.24: Change Sharon A Home Contact number Via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20131, 20131, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.24: Change Sharon A Home Contact number Via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10601");
        myAssert.assertAll();
    }

    @Test(priority = 10602) //a
    public void testF10602_ValidateProfileChangesAuditReportWhenSetAnyManagerThenAnyMangger_2ndManagerChangeChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10602.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F18.24.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger 2nd Manager Change", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (181, 181, emailDomainName, testSerialNo, driver), true, "Failed in Item F18.24.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger 2nd Manager Change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10602.");
        myAssert.assertAll();
    }

    @Test (priority = 10611)
    public void testF10611_ValidateAllEmailAfterSharonAHnumberChangeviaJackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10591.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F18.25.1: Validate Sharon A email content for Sharon A Change Home Contact number", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20441, 20441, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item Item F18.20.1: Validate Sharon A email content for Sharon A Change Home Contact number");

        logMessage("Item F18.25.2: Validate Phanton F email content for Sharon A Change Home Contact number via Jack F", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20442, 20442, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.20.2: Validate Phanton F email content for Sharon A Change Home Contact number via Jack F");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10611.");
        myAssert.assertAll();

    }


    @Test (priority = 10621)
    public void testF10621_ValidateSharonADashboard_ChangeContactDetailsViaJackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10621.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Phantom Fry.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F18.26: Validate Phanton F Dashboard via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20321, 20321, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.26: Validate Phanton F Dashboard via Phanton F");

        logMessage("Item F18.27: Validate Phanton F Approval Page via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.27: Validate Phanton F Approval Page via Phanton F");

        logMessage("Item F18.28: Approve Sharon A personal contact via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.28: Approve Sharon A personal contact via Phanton F");

        logMessage("Item F18.29: Approve Sharon A the Surname change via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20162, 20162, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.29: Approve Sharon A the Surname change via Phanton F");

        logMessage("Item F18.30: Validate Phanton F Approval Page via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20282, 20282, testSerialNo, emailDomainName, driver), true, "Failed in Item F18.30: Validate Phanton F Approval Page via Phanton F");


        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10621");
        myAssert.assertAll();
    }

    @Test(priority = 10622) //a
    public void testF10622_ValidateProfileChangesAuditReportWhenSetAnyManagerThenAnyMangger_2ndManagerApprove() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10622.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F18.30.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger - 2nd Manager Approved", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (191, 191, emailDomainName, testSerialNo, driver), true, "Failed in Item F18.30.X1: Validate Profile Changes Audit Report When Set Any Manager Then Any Mangger - 2nd Manager Approved");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10622.");
        myAssert.assertAll();
    }

    @Test (priority = 10631)
    public void testF10631_ValidateAllEmailAfterSharonAApproveSurname_ChangeContactDetailsViaJackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10631.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F18.31.1: Validate Sharon A email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20451, 20451, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.31.1: Validate Sharon A email content for Sharon A Approve Surname");

        logMessage("Item F18.31.2: Validate Sharon A email content for Sharon A Change Home Contact number", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20452, 20452, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F18.31.2: Validate Sharon A email content for Sharon A Change Home Contact number ");

        logMessage("Item F18.31.3: Validate Sue A email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20453, 20453, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.31.3: Validate Sue A email content for Sharon A Approve Surname");

        logMessage("Item F18.31.4: Validate Jack F email content for Sharon A Approve Surname", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20454, 20454, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F18.31.4: Validate Jack F email content for Sharon A Approve Surname");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10631.");
        myAssert.assertAll();
    }

    //////////////////////////// F19 from Govina on 16082019 //////////////////////

    @Test (priority = 10641)
    public void testF10641_EditProfileChange_ALLapproversTheApproverFromAnotherTeam() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10641.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.1: Edit Profile change workflow - then:approval is required;by:all managers;in:the member's team;and then:approval is required;by2:all managers;in2:The Boss via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20101, 20101, emailDomainName, testSerialNo, driver), true, "Failed in Item F19.1: Edit Profile change workflow - then:approval is required;by:all managers;in:the member's team;and then:approval is required;by2:all managers;in2:The Boss via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10641");
        myAssert.assertAll();

    }

    @Test (priority = 10651)
    public void testF10651_AddJulesNTheBoss_ValidateTeams() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10651.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item F19.2: Add Jules N to the Team - The Boss and assign as Manager via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20141, 20141, testSerialNo, driver),  true, "Failed in Item F19.2: Add Jules N to the Team - The Boss and assign as Manager via Admin");

        SystemLibrary.logMessage("Item F19.3: Validate the Team - Team B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20151, 20151, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.3: Validate the Team - Team B.");

        SystemLibrary.logMessage("Item F19.4: Validate the Team - The Boss", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20152, 20152, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.4: Validate the Team - The Boss.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10651.");
        myAssert.assertAll();

    }


    @Test (priority = 10661)
    public void testF10661_ValidatePhantonFDashboard_ChangePostelAddressMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10661.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Phantom Fry.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20011, 20011, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.5: Validate Phanton F Dashboard via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20331, 20331, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.5: Validate Phanton F Dashboard via Phanton F");

        logMessage("Item F19.6: Validate Phanton F Approval process via Phanton F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.6: Validate Phanton F Approval process via Phanton F");

        logMessage("Item F19.7: Change Phantom F existing Postal Address via Phantom F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20141, 20141, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.7: Change Phantom F existing Postal Address via Phantom F");

        logMessage("Item F19.8: Add Phantom F Medical Condition via Phantom F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20261, 20261, testSerialNo, driver), true, "Failed in Item F19.8: Add Phantom F Medical Condition via Phantom F");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10661");
        myAssert.assertAll();
    }

    @Test(priority = 10662) //a
    public void testF10662_ValidateProfileChangesAuditReportWhenSetAllManagerThenAllMangger_MemberChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10662.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F19.8.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - Member Change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (201, 201, emailDomainName, testSerialNo, driver), true, "Failed in Item F19.8.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - Member Change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10662.");
        myAssert.assertAll();
    }

    @Test (priority = 10671)
    public void testF10671_ValidateAllEmailAfterPhantonF_ChangePostelAddressMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10671.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F19.9.1: Validate Phantom F email content for  Phantom F Change Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20461, 20461, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.9.1: Validate Phantom F email content for  Phantom F Change Postal Address");

        logMessage("Item F19.9.2: Validate Phantom F email content for Phantom F Change Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20462, 20462, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.9.2: Validate Phantom F email content for Phantom F Change Medical Condition");

        logMessage("Item F19.9.3: Validate Sue A email content for Phantom F Change Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20463, 20463, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.9.3: Validate Sue A email content for Phantom F Change Postal Address");

        logMessage("Item F19.9.4: Validate Sue A email content for  Phantom F Change Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20464, 20464, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.9.4: Validate Sue A email content for  Phantom F Change Medical Condition");

        logMessage("Item F19.9.5: Validate Jack F email content for Phantom F Change Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20465, 20465, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item Item F19.9.5: Validate Jack F email content for Phantom F Change Postal Address ");

        logMessage("Item F19.9.6: Validate Jack F email content for  Phantom F Change Medical Condition ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20466, 20466, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.9.6: Validate Jack F email content for  Phantom F Change Medical Condition");

        logMessage("Item F19.9.7: Validate Jules N email content for  Phantom F Change Change Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20467, 20467, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.9.7: Validate Jules N email content for  Phantom F Change Change Postal Address");

        logMessage("Item F19.9.8: Validate Jules N email content  for  Phantom F Change Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20468, 20468, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.9.8: Validate Jules N email content  for  Phantom F Change Medical Condition");

        SystemLibrary.logMessage("*** End of Test F10671.");
        myAssert.assertAll();
    }

    @Test (priority = 10681)
    public void testF10681_ValidateSueADashboard_Approval_Process() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10681.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.10: Validate Sue A Dashboard for pending approvals via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20336, 20336, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.10: Validate Sue A Dashboard for pending approvals via Sue A");

        logMessage("Item F19.11: Validate Sue A Approval Page via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20291, 20291, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.11: Validate Sue A Approval Page via Sue A");

        logMessage("Item F19.12: Validate Sue A Teams Role - Approval process via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.12: Validate Sue A Teams Role - Approval process via Sue A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10681");
        myAssert.assertAll();
    }

    @Test (priority = 10691)
    public void testF10691_ValidateAndChangePersonalInfoPhantomFViaJackF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10691.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Detele All Email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.13: Validate Jack F Dashboard for pending approvals via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20341, 20341, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.13: Validate Jack F Dashboard for pending approvals via Jack F");

        logMessage("Item F19.14: Validate Jack F Approval Page via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20292, 20292, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.14: Validate Jack F Approval Page via Jack F");

        logMessage("Item F19.15: Validate Jack F Teams Role - Approval process via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20196, 20196, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.15: Validate Jack F Teams Role - Approval process via Jack F");

        logMessage("Item F19.16: Decline Phantom F address change without comment via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20171, 20171, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.16: Decline Phantom F address change without comment via Jack F");

        logMessage("Item F19.17: Approve Phantom F Medical Condition change via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20172, 20172, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.17: Approve Phantom F Medical Condition change via Jack F");

        logMessage("Item F19.18: Validate Jack F Dashboard after Jack F decline Sharon A Address change via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20342, 20342, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.18: Validate Jack F Dashboard after Jack F decline Sharon A Address change via Jack F");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10691");
        myAssert.assertAll();
    }

    @Test(priority = 10692) //a
    public void testF10692_ValidateProfileChangesAuditReportWhenSetAllManagerThenAllMangger_1stApproverApproved() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10692.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F19.18.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 1st Approver Approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (211, 211, emailDomainName, testSerialNo, driver), true, "Failed in Item F19.18.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 1st Approver Approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10692.");
        myAssert.assertAll();
    }

    @Test (priority = 10701)
    public void testF10701_ValidateAllEmailAfterPhantonF_ApproveDeclinePostelAddressMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10701.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F19.19.1: Validate Phantom F email content for  Phantom F Decline Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20471, 20471, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.19.1: Validate Phantom F email content for  Phantom F Decline Postal Address");

        logMessage("Item F19.19.2: Validate Phantom F email content for Phantom F Approve Medical Condition ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20472, 20472, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.19.2: Validate Phantom F email content for Phantom F Approve Medical Condition");

        logMessage("Item F19.19.3: Validate Sue A email content for Phantom F Decline Postal Address", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20473, 20473, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.19.3: Validate Sue A email content for Phantom F Decline Postal Address");

        logMessage("Item F19.19.4: Validate Sue A email content for  Phantom F Approve Medical Condition ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20474, 20474, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.19.4: Validate Sue A email content for  Phantom F Approve Medical Condition ");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);
        SystemLibrary.logMessage("*** End of Test 10701.");
        myAssert.assertAll();
    }


    @Test (priority = 10711)
    public void testF10711_ValidateSueADashboard_ApprovePhantomFMedical () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10711.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.20: Validate Sue A Dashboard for pending approvals via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20351, 20351, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.20: Validate Sue A Dashboard for pending approvals via Sue A");

        logMessage("Item F19.21: Validate Sue A Approval Page via Dashboard  via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.21: Validate Sue A Approval Page via Dashboard  via Sue A");

        logMessage("Item F19.22: Approve Phantom F Medical Condition change via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20175, 20175, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.22: Approve Phantom F Medical Condition change via Sue A");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10711");
        myAssert.assertAll();
    }

    @Test(priority = 10712) //a
    public void testF10712_ValidateProfileChangesAuditReportWhenSetAllManagerThenAllMangger_2ndApproverApproved() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test F10712.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item F19.22.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 2nd Approver Approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (221, 221, emailDomainName, testSerialNo, driver), true, "Failed in Item F19.22.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 2nd Approver Approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10712.");
        myAssert.assertAll();
    }

    @Test (priority = 10721)
    public void testF10721_ValidateAllEmailAfterPhantonF_ApproveMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10721.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F19.23.1: Validate Phantom F email content for Phantom F Approve Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20481, 20481, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.23.1: Validate Phantom F email content for Phantom F Approve Medical Condition");

        logMessage("Item F19.23.2: Validate Mitchell S email content for Phantom F Approve Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20482, 20482, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F19.23.2: Validate Mitchell S email content for Phantom F Approve Medical Condition");

        logMessage("Item F19.23.3: Validate Jules N email content for Phantom F  Approve Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20483, 20483, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.23.3: Validate Jules N email content for Phantom F  Approve Medical Condition");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10721.");
        myAssert.assertAll();
    }



    @Test (priority = 10731)
    public void testF10731_ValidateJulesADashboard_ApprovePhantomFMedical () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10731.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Jules N");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10276, 10276, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.24: Validate Jules N Dashboard via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20355, 20355, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.24: Validate Jules N Dashboard via Jules N");

        logMessage("Item F19.25: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(20311, 20311, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.25: Validate Jules N Approval Page via Jules N");

        logMessage("Item F19.26: Approve Phantom F Medical Condition change via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20181, 20181, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.26: Approve Phantom F Medical Condition change via Jules N");

        logMessage("Item F19.27: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(20312, 20312, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.27: Validate Jules N Approval Page via Jules N");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10731");
        myAssert.assertAll();
    }


    @Test (priority = 10741)
    public void testF10741_ValidateJulesADashboard_ApprovePhantomFMedical() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10741.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.28: Validate Mitchell S Dashboard via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20361, 20361, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.28: Validate Mitchell S Dashboard via Mitchell S");

        logMessage("Item F19.29: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(20316, 20316, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.29: Validate Mitchell S Approval Page via Mitchell S");

        logMessage("Item F19.30: Approve Phantom F Medical Condition change via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20182, 20182, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.30: Approve Phantom F Medical Condition change via Mitchell S");

        logMessage("Item F19.31: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovals_ProfileChange_ViaDashboard_NonAdmin(20317, 20317, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.31: Validate Mitchell S Approval Page via Mitchell S");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10741");
        myAssert.assertAll();
    }

    @Test (priority = 10751)
    public void testF10751_ValidateAllEmailAfterPhantonF_ApproveMedicalCondition() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10751.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F19.32.1: Validate Mitchell S email content for Phantom F Approve Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20491, 20491, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in F19.32.1: Validate Mitchell S email content for Phantom F Approve Medical Condition");

        logMessage("Item F19.32.2: Validate Phantom F email content for Phantom F Approve Medical Condition", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20492, 20492, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.32.2: Validate Phantom F email content for Phantom F Approve Medical Condition");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);
        SystemLibrary.logMessage("*** End of Test 10751.");
        myAssert.assertAll();
    }

    @Test (priority = 10761)
    public void testF10761_ValidateAndChangeContactDetailsOfPhantomF() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10761.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F19.33: Validate Phantom F Personal Information page Via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.33: Validate Phantom F Personal Information page Via Admin");

        logMessage("Item F19.34: Change Phantom F Work email via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20151, 20151, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.34: Change Phantom F Work email via Admin");

        logMessage("Item F19.35: Validate Phantom F Contact Detail Page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20161, 20161, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.35: Validate Phantom F Contact Detail Page via Admin.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10761");
        myAssert.assertAll();
    }

    /////////////// Must run headless ///////////////////
    @Test (priority = 10762)
    public void testF10762_ValidateProfileChangeAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10762.");
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

        logMessage("Item F19.35.X1: Valdiate Audit Report via Admin after Admin Edit employee work email contact.", testSerialNo);
        myAssert.assertEquals(downloadMultiAuditReportViaDashboard(421, 421, emailDomainName, testSerialNo, driver), true, "failed in Item F19.35.X1: Valdiate Audit Report via Admin after Admin Edit employee work email contact.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10762");
        myAssert.assertAll();

    }


    @Test (priority = 10771)
    public void testF10771_ValidateAllEmailAfterPhantonFProfileChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10771.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F19.36.1: Validate Phantom F email content for  Phantom F Profile Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20501, 20501, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item Item F19.36.1: Validate Phantom F email content for  Phantom F Profile Change");

        logMessage("Item F19.36.2: Validate Sue A email content for  Phantom F Profile Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20502, 20502, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.36.2: Validate Sue A email content for  Phantom F Profile Change");

        logMessage("Item F19.36.3: Validate Jack F email content for Phantom F Profile Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20503, 20503, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F19.36.3: Validate Jack F email content for Phantom F Profile Change");

        logMessage("Item F19.36.4: Validate Jules N email content for Phantom F Profile Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20504, 20504, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.36.4: Validate Jules N email content for Phantom F Profile Change");

        logMessage("Item F19.36.5: Validate Mitchell S email content  for  Phantom F Profile Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20505, 20505, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F19.36.5: Validate Mitchell S email content  for  Phantom F Profile Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10771.");
        myAssert.assertAll();


    }

    //////////////////////////// F20 from Govina on 23082019 //////////////////////


    @Test (priority = 10781)
    public void testF10781_EditProfileChange_IncludeSecondLevelApproval () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10781.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.1: Validate Admin Workflows Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20111, 20111, emailDomainName, testSerialNo, driver), true, "Failed in Item F20.1: Validate Admin Workflows Via Admin.");

        logMessage("Item F20.2: Edit Profile change workflow - then:approval is required;by:ALL managers;in:The Boss;and then:approval is required;by2:ANY manager;in2:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20121, 20121, emailDomainName, testSerialNo, driver), true, "Failed in Item F20.2: Edit Profile change workflow - then:approval is required;by:ALL managers;in:The Boss;and then:approval is required;by2:ANY manager;in2:the members team via Admin");

        logMessage("Item F20.3: Validate Malley S Teams Role - Approval process via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20201, 20201, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.3: Validate Malley S Teams Role - Approval process via Admin");

        logMessage("Item F20.4: Activate Malley S.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20041, 20041, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item F20.4: Activate Malley S.");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test 10781");
        myAssert.assertAll();

    }



    @Test (priority = 10791)
    public void testF10791_AddMalleySTeamC_ValidateTeams() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10791.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item F20.5: Add Malley S to Team C and assign as Member via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20161, 20161, testSerialNo, driver),  true, "Failed in Item F20.5: Add Malley S to Team C and assign as Member via Admin");

        SystemLibrary.logMessage("Item F20.6: Validate unassigned list", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20171, 20171, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.6: Validate unassigned list.");

        SystemLibrary.logMessage("Item F20.7: Validate the Team - Team C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20172, 20172, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.7: Validate the Team - Team C.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test F10791.");
        myAssert.assertAll();

    }


    @Test (priority = 10801)
    public void testF10801_ValidateMallySDashboard_EditDOBAndGender() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10801.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Malley S.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.8: Validate Malley S Dashboard via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20365, 20365, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.8: Validate Malley S Dashboard via Malley S");

        logMessage("Item F20.9: Validate Malley S Teams Role - Approval process via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20202, 20202, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.9: Validate Malley S Teams Role - Approval process via Malley S");

        logMessage("Item F20.10: Edit Malley S DOB via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20281, 20281, testSerialNo, driver), true, "Failed in Item F20.10: Edit Malley S DOB via Malley S");

        logMessage("Item F20.11: Edit Malley S Gender via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20282, 20282, testSerialNo, driver), true, "Failed in Item F20.11: Edit Malley S Gender via Malley S");

        logMessage("Item F20.12: Validate Malley S Personal Information page via  Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20286, 20286, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.12: Validate Malley S Personal Information page via  Malley S");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10801");
        myAssert.assertAll();
    }



    @Test (priority = 10811)
    public void testF10811_ValidateAllEmailAfterMalleyS_EditDOBGender() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10811.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F20.13.1: Validate Malley S email content for Malley S DOB", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20511, 20511, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.13.1: Validate Malley S email content for Malley S DOB");

        logMessage("Item F20.13.2: Validate Malley S email content for Malley S Gender ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20512, 20512, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.13.2: Validate Malley S email content for Malley S Gender");

        logMessage("Item F20.13.3: Validate Sue A email content for Malley S Change DOB", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20513, 20513, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.13.3: Validate Sue A email content for Malley S Change DOB");

        logMessage("Item F20.13.4: Validate Sue A email content for  Malley S Change Gender", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20514, 20514, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.13.4: Validate Sue A email content for  Malley S Change Gender");

        logMessage("Item F20.13.5: Validate Mitchell S email content for Malley S Change DOB", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20515, 20515, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item Item F20.13.5: Validate Mitchell S email content for Malley S Change DOB ");

        logMessage("Item F20.13.6: Validate Mitchell S email content for  Malley S Change Gender ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20516, 20516, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.13.6: Validate Mitchell S email content for  Malley S Change Gender");

        logMessage("Item F20.13.7: Validate Jules N email content for  Malley S Change DOB", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20517, 20517, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.13.7: Validate Jules N email content for  Malley S Change DOB");

        logMessage("Item F20.13.8: Validate Jules N email content  for  Malley S Change Gender", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20518, 20518, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.13.8: Validate Jules N email content  for  Malley S Change Gender");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test F10811.");
        myAssert.assertAll();

    }

    @Test (priority = 10821)
    public void testF10821_ValidateMitchellSDashboard_ApprovePage() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10821.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.14: Validate Mitchell S Dashboard via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20371, 20371, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.14: Validate Mitchell S Dashboard via Mitchell S");

        logMessage("Item FF20.15: Validate Mitchell S Teams Role - Approval process via Mitchell S", testSerialNo);
        myAssert.assertEquals(validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.15: Validate Mitchell S Teams Role - Approval process via Mitchell S");

        logMessage("Item FF20.16: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiApprovalsPage(20411, 20411, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.16: Validate Mitchell S Approval Page via Mitchell S");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F1010821");
        myAssert.assertAll();
    }


    @Test (priority = 10831)
    public void testF10831_ValidateAdminDashboard_ApprovePage_Approve() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10831.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.17: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20381, 20381, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.17: Validate Admin Dashboard via Admin");

        logMessage("Item F20.18: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20421, 20421, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.18: Validate My Approval List via Admin");

        logMessage("Item F20.19: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20422, 20422, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.19: Validate Other Approval List via Admin");

        logMessage("Item F20.20: Decline Phantom F Work contacts change via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20191, 20191, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.20: Decline Phantom F Work contacts change via Admin");

        logMessage("Item F20.21: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20431, 20431, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.21: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10831");
        myAssert.assertAll();

    }

    @Test (priority = 10841)
    public void testF10841_ValidateEmailDeclinePPhantomFWorkContactsViaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10841.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F20.22.1: Validate Phantom F email content for  Phantom F Work contacts change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20521, 20521, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.22.1: Validate Phantom F email content for  Phantom F Work contacts change");

        logMessage("Item F20.22.2: Validate Sue A email content for  Phantom F Work contacts change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20522, 20522, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.22.2: Validate Sue A email content for  Phantom F Work contacts change");

        logMessage("Item F20.22.3: Validate Jack F email content  for  Phantom F Work contacts change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20523, 20523, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.22.3: Validate Jack F email content  for  Phantom F Work contacts change");

        logMessage("Item F20.22.4: Validate Mitchell S email conten for  Phantom F Work contacts change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20524, 20524, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.22.4: Validate Mitchell S email conten for  Phantom F Work contacts change ");

        logMessage("Item F20.22.5: Validate Jules N email content for  Phantom F Work contacts change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20525, 20525, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.22.4: Validate Jules N email content for  Phantom F Work contacts change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10841.");
        myAssert.assertAll();
    }



    @Test (priority = 10851)
    public void testF10851_ValidateJulesADashboard_ApproveMalleySAdditionalInfo() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10851.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Jules N");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10285, 10285, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.23: Validate Jules N Dashboard via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20391, 20391, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.23: Validate Jules N Dashboard via Jules N");

        logMessage("Item F20.24: Validate  Jules N Teams Role - Approval process via  Jules N", testSerialNo);
        myAssert.assertEquals(validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20221, 20221, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.24: Validate  Jules N Teams Role - Approval process via  Jules N");

        logMessage("Item F20.25: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20441, 20441, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.25: Validate Jules N Approval Page via Jules N");

        logMessage("Item F20.26: Approve Malley S Additional Information change  via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20201, 20201, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.26: Approve Malley S Additional Information change  via Jules N");

        logMessage("Item F20.27: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20442, 20442, testSerialNo, emailDomainName, driver), true, "Failed in Item F19.27: Validate Jules N Approval Page via Jules N");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10851");
        myAssert.assertAll();
    }

    @Test (priority = 10861)
    public void testF10861_ValidateEmailApproveMalleySAdditionalInfoViaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10861.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F20.28.1: Validate Malley S email content for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20531, 20531, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.28.1: Validate Malley S email content for Malley S Additional Information change");

        logMessage("Item F20.28.2: Validate Sue A email content for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20532, 20532, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.28.2: Validate Sue A email content for Malley S Additional Information change");

        logMessage("Item F20.28.3: Validate Jules N email content  for  Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20533, 20533, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.28.3: Validate Jules N email content  for  Malley S Additional Information change");

        logMessage("Item F20.28.4: Validate Mitchell S email content  for  Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20534, 20534, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.28.4: Validate Mitchell S email content  for  Malley S Additional Information change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10861.");
        myAssert.assertAll();
    }

    @Test (priority = 10871)
    public void testF10871_ValidateMitchellSDashboard_ApproveMalleySAdditionalInfo() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10871.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.29: Validate Mitchell S Dashboard via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20395, 20395, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.29: Validate Mitchell S Dashboard via Mitchell S");

        logMessage("Item F20.30: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20446, 20446, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.30: Validate Mitchell S Approval Page via Mitchell S");

        logMessage("Item F20.31: Approve Malley S Additional Information change via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20206, 20206, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.31: Approve Malley S Additional Information change via Mitchell S");

        logMessage("Item F20.32: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20447, 20447, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.32: Validate Mitchell S Approval Page via Mitchell S");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10871");
        myAssert.assertAll();
    }



    @Test (priority = 10881)
    public void testF10881_ValidateEmailApproveMalleySAdditionalInfoViaMitchellS() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10881.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F20.33.1: Validate Malley S email content for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20536, 20536, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.33.1: Validate Malley S email content for Malley S Additional Information change");

        logMessage("Item F20.33.2: Validate Sue A email content for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20537, 20537, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.33.2: Validate Sue A email content for Malley S Additional Information change");

        logMessage("Item F20.33.3: Validate Jules N email content  for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20538, 20538, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item F20.33.3: Validate Jules N email content  for Malley S Additional Information change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10881.");
        myAssert.assertAll();
    }


    @Test (priority = 10891)
    public void testF10891_ValidateSueADashboard_ApprovePhantomFMedical () throws Exception {
        SystemLibrary.logMessage("*** Start Test F10891.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item F20.34: Validate Sue A Dashboard via Sue A ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20401, 20401, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.34: Validate Sue A Dashboard via Sue A ");

        logMessage("Item F20.35: Validate Sue A Approval Page via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20451, 20451, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.35: Validate Sue A Approval Page via Sue A");

        logMessage("Item F20.36: Approve Malley S profile change via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.36: Approve XXXXX profile change via Sue A");

        logMessage("Item F20.37: Validate Sue A Approval Page via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20452, 20452, testSerialNo, emailDomainName, driver), true, "Failed in Item F20.37: Validate Sue A Approval Page via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test F10891");
        myAssert.assertAll();
    }


    @Test (priority = 10901)
    public void testF10901_ValidateEmailApproveMalleySAdditionalInfoViasUEa() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10901.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item F20.38.1: Validate Malley S email content for Malley S Additional Information change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20541, 20541, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F20.38.1: Validate Malley S email content for Malley S Additional Information change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10901.");
        myAssert.assertAll();
    }


    ///////////////////////
    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item FFF", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module F - Work Flow test.");
    }

    ///////////////////////////// Debug here ///////////////////

    @Test (priority = 10261)
    public void testF10261_EditSharonAResidentialAddressAndEmergencyContactViaSharonA_Debug() throws Exception {
        SystemLibrary.logMessage("*** Start Test F10261.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sharon A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(20161, 20161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
/*

        logMessage("Item F11.4: Validate Sharon A Approval Process via Sharon A");
        logMessage("Item F11.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20081, 20081, emailDomainName, testSerialNo, driver), true, "Failed in Item F11.4: Validate Sharon A Approval Process via Sharon A");
*/

        logMessage("Item F11.5: Edit Sharon A Residential Address via Sharon A");
        logMessage("Item F11.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20081, 20081, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.5: Edit Sharon A Residential Address via Sharon A");

   /*     logMessage("Item F11.6: Add Sharon A Emergency Contact via Sharon A");
        logMessage("Item F11.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20082, 20082, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.6: Add Sharon A Emergency Contact via Sharon A");

        logMessage("Item F11.7: Validate Sharon A Contact page via Sharon A");
        logMessage("Item F11.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20083, 20083, testSerialNo, emailDomainName, driver), true, "Failed in Item F11.6: Add Sharon A Emergency Contact via Sharon A");
*/
        signoutESS(driver);
        driver.close();
        logMessage("*** End of test F10261");
        myAssert.assertAll();

    }


}
