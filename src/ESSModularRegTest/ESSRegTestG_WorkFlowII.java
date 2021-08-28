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
import static Lib.SystemLibrary.logMessage;

public class ESSRegTestG_WorkFlowII {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=107;
    private static String payrollDBOrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName="G";

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


    @Test(priority = 10011) //a
    public void testG10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item G10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item G1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item G1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item G1.5: Update email address and change email type as work email.");

        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item G1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item G1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item G1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item G1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item G1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item G1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item G10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testG10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test G10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item G1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item G1.8: Add API configuration.");

        logMessage("Item G1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item G1.9: Sync All.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testG10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test G10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item G2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item G2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item G2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item G2.2: Validate Tem Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10031.");
        myAssert.assertAll();
    }

    @Test (priority=10041)
    public static void testG10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test G10041: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item G3: Import Team via TPU.");

        logMessage("*** End of Test G10041");
        myAssert.assertAll();
    }



    @Test(priority = 10051) //a
    public void testG10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test G10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item G4: Validate Team after import team", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item G4: Validate Team after import team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10051.");
        myAssert.assertAll();
    }


    @Test (priority = 10061)
    public void testG10061_AdminChangeWorkflowPermisisonAndValidate() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10061.");

        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G5.1: Validate Workflows page with Edit button.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateWorkflowsPage_Main(20131, 20131, testSerialNo, emailDomainName, driver), true, "Failed in Item F5.11: Validate Workflows page with Edit button.");

        logMessage("Item G5.2:Validate Admin Roles and Permisson page", testSerialNo);
        myAssert.assertEquals(validatePermissionPanel_Main(20081, 20081, emailDomainName, testSerialNo, driver), true, "Failed in Item G5.2: Validate Administrator roles permissions.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10061.");
        myAssert.assertAll();
    }


    ////////////////////////  Start Work flow testing below /////////////////////////////

    @Test (priority = 10071)
    public void testG10071_ActivateEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10071.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G6.1: Activate Jack F", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(103, 103, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.1: Activate Jack F");

        logMessage("Item G6.2: Activate Sue A.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(104, 104, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.2: Activate Sue A.");

        logMessage("Item G6.3: Activate Jules N.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(116, 116, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.3: Activate Jules N.");

        logMessage("Item G6.4: Activate Robert SINGLETON", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(102, 102, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.4: Activate Robert SINGLETON.");

        logMessage("Item G6.5: Activate Mitchell S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.5: Activate Mitchell S.");

        logMessage("Item G6.6: Activate Martin G", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20042, 20042, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.6: Martin G.");

        logMessage("Item G6.7: Activate Phantom F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(105, 105, payrollDBName, testSerialNo, emailDomainName, url_ESS,  driver), true, "Failed in Item G6.7: Activate Phantom F.");

        logMessage("Item G6.8: Activate Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(106, 106, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Faield in Item G6.8: Activate Christine R");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10071.");
        myAssert.assertAll();
    }

    @Test (priority = 10081)
    public void testG10081_FirstLogon_allEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test Logon  All Employees G10071.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G7.1: First Logon as Jack F", testSerialNo);
        WebDriver driver= GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.1: First Logon as Jack F.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.2: First Logon Sue A.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.2: First Logon Sue A.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.3: First Logon as Jules N", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10285, 10285, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.3: First Logon as Jules N.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.4: First Logon as Robert S", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10171, 10171, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.4: First Logon as Robert S.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.5: First Logon as Mitchell S.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.5: First Logon as Mitchell S.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.6: First Logon as Martin G.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10295, 10295, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.6: First Logon as Martin G.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.7: First Logon as Phantom F.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10161, 10161, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.7: First Logon as Phantom F.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G7.8: First Logon as Christine R.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10061, 10061, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G7.8: First Logon as Christine R.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test G10081.");
        myAssert.assertAll();

    }

    @Test (priority = 10091)
    public void testG10091_AddJulesNTheBoss_ValidateTeams() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10091.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item G8.1: Add Jules N to the Team - The Boss and assign as Manager via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20195, 20195, testSerialNo,  driver),  true, "Failed in Item G8.1: Add Jules N to the Team - The Boss and assign as Manager via Admin");

        SystemLibrary.logMessage("Item G8.2: Validate the Team - The Boss", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20196, 20196, testSerialNo, emailDomainName, driver), true, "Failed in Item G8.2: Validate the Team - The Boss.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10091.");
        myAssert.assertAll();

    }

    @Test (priority = 10101)
    public void testG10101_EditManagerrole() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10101.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G9.1: Change Managers Roles - Personal Contact from View to Edit via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20091, 20091, driver), true, "Failed in Item G9.1: Change Managers Roles - Personal Contact from View to Edit via Admin");

        logMessage("Item G9.2: Change Managers Roles - Work Contact from View to Edit via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20092, 20092, driver), true, "Failed in Item G9.2: Change Managers Roles - Work Contact from View to Edit via Admin");

        logMessage("Item G9.3: Validate Managers Role and Permission page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePermissionPanel_Main(20096, 20096, emailDomainName, testSerialNo, driver), true, "Failed in Item G9.3: Validate Managers Role and Permission page.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10101");
        myAssert.assertAll();

    }


    ///////////////////////////////// G21 ////////////////////////////////////////

    @Test (priority = 10111)
    public void testG10111_EditProfileChange_IncludeSecondLevelApproval () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10111.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:The Boss;and then:approval is required;by2:All manager;in2:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20141, 20141, emailDomainName, testSerialNo, driver), true, "Failed in Item G21.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:The Boss;and then:approval is required;by2:All manager;in2:the members team via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10111");
        myAssert.assertAll();

    }

    @Test (priority = 10121)
    public void testG10121_AddRobertSTeamB_ValidateTeams() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10121.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item G21.2: Add Robert S to Team B and assign as Member via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20201, 20201, testSerialNo, driver),  true, "Failed in Item G21.2: Add Jennifer H to Team B and assign as Member via Admin");

        SystemLibrary.logMessage("Item G21.3: Validate the Team - Team B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20211, 20211, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.3: Validate the Team - Team B.");

        logMessage("Item G21.4: Validate Robert S Teams Role - Approval process via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20226, 20226, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.4: Validate Robert S Teams Role - Approval process via Admin");


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10121.");
        myAssert.assertAll();

    }

    @Test (priority = 10131)
    public void testG10131_ValidateSueADashboard_AddMobileRobertS () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10131.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sue A");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.5: Validate Sue A Dashboard via Sue A ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20416, 20416, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.5: Validate Sue A Dashboard via Sue A ");

        logMessage("Item G21.6: Add Robert S Work Mobile number via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20171, 20171, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.6: Add Robert S Work Mobile number via Sue A.");

        logMessage("Item G21.7: Validate Robert S Contact Detail page via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20176, 20176, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.7: Validate Robert S Contact Detail page via Sue A.");


        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10131");
        myAssert.assertAll();
    }

    @Test(priority = 10132) //a
    public void testG10132_ValidateProfileChangesAuditReportWhenSetAllManagerThenAllMangger_1stApproveApplyChange() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test G10132.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item G21.7.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 1st Approver Apply Change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (231, 231, emailDomainName, testSerialNo, driver), true, "Failed in Item G21.7.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 1st Approver Apply Change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10132.");
        myAssert.assertAll();
    }

    @Test (priority = 10141)
    public void testG10141_ValidateRobertSemailsAfterContactUpdate () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10141.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G21.8.1: Validate Mitchell S email content for Robert S Work Mobile number change via Sue A", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20551, 20551, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G21.8.1: Validate Mitchell S email content for Robert S Work Mobile number change via Sue A");

        logMessage("Item G21.8.2: Validate Jules N email content for Robert S Work Mobile number change via Sue A", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20552, 20552, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G21.8.2: Validate Jules N email content for Robert S Work Mobile number change via Sue A");

        logMessage("Item G21.8.3: Validate Robert S email content Robert S Work Mobile number change via Sue A", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20553, 20553, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G21.8.3: Validate Robert S email content Robert S Work Mobile number change via Sue A");

        logMessage("Item G21.8.4: Validate Sue A email content Robert S Work Mobile number change via Sue A", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20554, 20554, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G21.8.4: Validate Sue A email content Robert S Work Mobile number change via Sue A");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10141.");
        myAssert.assertAll();

    }


    @Test (priority = 10151)
    public void testG10151_ValidateJackDashboardAdminOtherApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10151.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.9: Validate Jack F Dashboard for pending approvals via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20421, 20421, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G21.9: Validate Jack F Dashboard for pending approvals via Jack F");

        logMessage("Item G21.10: Validate Jack F Approval Page via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20461, 20461, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.10: Validate Jack F Approval Page via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.11: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20466, 20466, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.11: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10151");
        myAssert.assertAll();
    }


    @Test (priority = 10161)
    public void testG10161_ValidateMitchellSApprovePageApproveRobertSContacts() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.12: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20471, 20471, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.35: Validate Sue A Approval Page via Sue A");

        logMessage("Item G21.13: Approve Robert S personal contact via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20221, 20221, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.13: Approve Robert S personal contact via Mitchell S");

        logMessage("Item G21.14: Validate Mitchell S Approval Page via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20472, 20472, testSerialNo, emailDomainName, driver), true, "Failed in Item G21.35: Validate Sue A Approval Page via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10161");
        myAssert.assertAll();
    }

    @Test(priority = 10162) //a
    public void testG10162_ValidateProfileChangesAuditReportWhenSetAllManagerThenAllMangger_2ndApproverApproved() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test G10162.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item G21.14.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 2nd Approver Approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard (241, 241, emailDomainName, testSerialNo, driver), true, "Failed in Item G21.14.X1: Validate Profile Changes Audit Report When Set All Manager Then All Mangger - 2nd Approver Approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10162.");
        myAssert.assertAll();
    }

    @Test (priority = 10171)
    public void testG10171_ValidateRobertSemailsAfterContactApprove () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10171.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G21.15.1: Validate Jules N email content for Robert S Work Mobile number approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20558, 20558, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G21.15.1: Validate Jules N email content for Robert S Work Mobile number approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10171.");
        myAssert.assertAll();

    }

    @Test (priority = 10181)
    public void testG10181_ValidateJulesNApprovePageApproveRobertSContacts() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Jules N");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10276, 10276, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G21.16: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20476, 20476, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G21.16: Validate Sue A Approval Page via Sue A");

        logMessage("Item G21.17: Approve Robert S personal contact via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20222, 20222, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G21.17: Approve Robert S personal contact via Jules N");

        logMessage("Item G21.18: Validate Jules N Approval Page via Jules N", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20477, 20477, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G21.18: Validate Sue A Approval Page via Sue A");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10181");
        myAssert.assertAll();
    }

    @Test (priority = 10191)
    public void testG10191_ValidateRobertSemailsAfterContactApprove () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10171.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G21.19.1: Validate Robert S email content for Robert S Work Mobile number approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20561, 20561, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G21.19.1: Validate Robert S email content for Robert S Work Mobile number approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10171.");
        myAssert.assertAll();

    }

    ///////////////////////////////// G22 ////////////////////////////////////////

    @Test (priority = 10201)
    public void testG10201_ValidateMartinGDashboard_AddBankDetails () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10201.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Martin G");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10295, 10295, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G22.1: Validate Martin G Dashboard via Martin G ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20426, 20426, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.1: Validate Sue A Dashboard via Sue A ");

        logMessage("Item G22.2: Validate Martin G Teams and Roles via Martin G", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.2: Validate Martin G Teams and Roles via Martin G");

        logMessage("Item G22.3: Validate Martin G Teams Role - Approval process via Martin G", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20236, 20236, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.3: Validate Martin G Teams Role - Approval process via Martin G");

        logMessage("Item G22.4: Add Martin G Bank Account Details via Martin G", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.4: Add Martin G Bank Account Details via Martin G");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10201");
        myAssert.assertAll();
    }

    @Test (priority = 10211)
    public void testG10211_ValidateMartinGemailsAfterBankDetailsUpdate() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10211.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G22.5.1: Validate Mitchell S email content for Martin G Bank Details via Martin G", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20566, 20566, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.5.1: Validate Mitchell S email content for Martin G Bank Details via Martin G");

        logMessage("Item G22.5.2: Validate Jules N email content for Martin G Bank Details via Martin G", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20567, 20567, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.5.2: Validate Jules N email content for Martin G Bank Details via Martin G");

        logMessage("Item G22.5.3: Validate Martin Gemail content for Martin G Bank Details via Martin G", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20568, 20568, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.5.3: Validate Martin Gemail content for Martin G Bank Details via Martin G");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10211.");
        myAssert.assertAll();

    }

    @Test (priority = 10221)
    public void testG10221_ValidateJackDashboardAdminOtherApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10221.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G22.6: Validate Jack F Dashboard for pending approvals via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20431, 20431, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G22.6: Validate Jack F Dashboard for pending approvals via Jack F");

        logMessage("Item G22.7: Validate Jack F Approval Page via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20481, 20481, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.7: Validate Jack F Approval Page via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G22.8: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20436, 20436, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.8: Validate Admin Dashboard via Admin");

        logMessage("Item G22.9: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20486, 20486, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.9: Validate Other Approval List via Admin");

        logMessage("Item G22.10: Approve Martin G Bank Details change via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20231, 20231, testSerialNo, emailDomainName, driver), true, "Failed in Item G22.10: Approve Martin G Bank Details change via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10221");
        myAssert.assertAll();
    }

    @Test (priority = 10231)
    public void testG10231_ValidateMartinGemailsAfterBankDetailsApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10231.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G22.11.1: Validate Mitchell S email content for Martin G Bank Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20571, 20571, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.11.1: Validate Mitchell S email content for Martin G Bank Details Approve");

        logMessage("Item G22.11.2: Validate Jules N email content for Martin G Bank Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20572, 20572, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.11.2: Validate Jules N email content for Martin G Bank Details Approve");

        logMessage("Item G22.11.3: Validate Christine R email content for Martin G Bank Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20573, 20573, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G22.11.3: Validate Christine R email content for Martin G Bank Details Approve");

        logMessage("Item G22.11.4: Validate Martin G email content for Martin G Bank Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20574, 20574, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G22.11.4: Validate Martin G email content for Martin G Bank Details Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10231.");
        myAssert.assertAll();

    }

    @Test(priority = 10241)
    public static void testG10241_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("Start Test 10241.");

        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G22.13: Validate Employee Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20101, 20101, emailDomainName, testSerialNo), true, "Failed in Item G22.13: Validate Employee Detail Report via Sage Micropay");

        logMessage("Item G22.14: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item G22.14: Implement eHR via Sage Micropay.");
        close_ImplementHRScreen();

        logMessage("Item G22.15: Validate Employee Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20111, 20111, emailDomainName, testSerialNo), true, "Failed in Item G22.15: Validate Employee Detail Report via Sage Micropay");

        exitMeridian();
        logMessage("End of Test 10241.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G23 /////////////////////////////////////////////////

    @Test (priority = 10251)
    public void testG10251_EditProfileChange_AnyManagerApprovalAddException () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10251");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G23.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:Team B;and then:approval is required;by2:All manager;in2:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20151, 20151, emailDomainName, testSerialNo, driver), true, "Failed in Item G23.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:Team B;and then:approval is required;by2:All manager;in2:the members team via Admin");

        logMessage("Item G23.2: Edit Profile change workflow - Add exception workflow - When a member changes:Additional information;then:send a notification;to managers in:Sub E via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20152, 20152, emailDomainName, testSerialNo, driver), true, "Failed in Item G23.2: Edit Profile change workflow - Add exception workflow - When a member changes:Additional information;then:send a notification;to managers in:Sub E via Admin.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10251");
        myAssert.assertAll();

    }

    @Test (priority = 10261)
    public void testG10261_ValidateRobertSDashboard_EditMaritalAndEmergencyContact () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10261.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robert S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10171, 10171, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G23.3: Validate Robert S Dashboard via Robert S ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20441, 20441, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.3: Validate Robert S Dashboard via Robert S");

        logMessage("Item G23.4: Validate Robert S Teams and Roles via Robert S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.4: Validate Robert S Teams and Roles via Robert S");

        logMessage("Item G23.5: Validate Robert S Teams Role - Approval process via Robert S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20246, 20246, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.5: Validate Robert S Teams Role - Approval process via Robert S");

        logMessage("Item G23.6: Edit Marital Status for Robert S via Robert S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20291, 20291, testSerialNo, driver), true, "Failed in Item G23.6: Edit Marital Status for Robert S via Robert S");

        logMessage("Item G23.7: Add Robert S Emergency Contact via Robert S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20181, 20181, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.7: Add Robert S Emergency Contact via Robert S");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10261");
        myAssert.assertAll();
    }


    @Test (priority = 10271)
    public void testG10271_ValidateRobertSEmailsAfterEmergencyContactChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10271");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G23.8.1: Validate Robert S email content for Robert S Marital Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20581, 20581, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.8.1: Validate Robert S email content for Robert S Marital Details Change");

        logMessage("Item G23.8.2: Validate Robert S email content for Robert S Emergency Contact Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20582, 20582, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.8.2: Validate Robert S email content for Robert S Emergency Contact Details Change");

        logMessage("Item G23.8.3: Validate Sue A email content for Robert S Emergency Contact Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20583, 20583, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.8.3: Validate Sue A email content for Robert S Emergency Contact Details Change");

        logMessage("Item G23.8.4: Validate Jack F email content for Robert S Emergency Contact Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20584, 20584, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G23.8.4: Validate Jack F email content for Robert S Emergency Contact Details Change");

        logMessage("Item G23.8.5: Validate Mitchell S email content for Robert S Emergency Contact Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20585, 20585, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in G23.8.5: Validate Mitchell S email content for Robert S Emergency Contact Details Change");

        logMessage("Item G23.8.6: Validate Jules N email content for Robert S Emergency Contact Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20586, 20586, payrollDBName,  testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G23.8.6: Validate Jules N email content for Robert S Emergency Contact Details Change");

        SystemLibrary.logMessage("*** End of Test G10271.");
        myAssert.assertAll();
    }


    @Test (priority = 10281)
    public void testG10281_ValidateAdminDashboardAdminOtherApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10281.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G23.9: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20451, 20451, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.9: Validate Admin Dashboard via Admin");

        logMessage("Item G23.10: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20491, 20491, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.10: Validate My Approval List via Admin");

        logMessage("Item G23.11: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20496, 20496, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.11: Validate Other Approval List via Admin");

        logMessage("Item G23.12: Approve Robert S Emergency Contact Details change via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20236, 20236, testSerialNo, emailDomainName, driver), true, "Failed in Item G23.12: Approve Robert S Emergency Contact Details change via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10281");
        myAssert.assertAll();
    }



    @Test (priority = 10291)
    public void testG10291_ValidateRobertSEmailsAfterEmergencyContactApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10291");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G23.13.1: Validate Robert S email content for Robert S Emergency Contact Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20591, 20591, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.13.1: Validate Robert S email content for Robert S Emergency Contact Details Approve");

        logMessage("Item G23.13.2: Validate Sue A email content for Robert S Emergency Contact Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20592, 20592, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.13.2: Validate Sue A email content for Robert S Emergency Contact Details Approve");

        logMessage("Item G23.13.3: Validate Jack F email content for Robert S Emergency Contact Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20593, 20593, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in G23.13.3: Validate Jack F email content for Robert S Emergency Contact Details Approve");

        logMessage("Item G23.13.4: Validate Mitchell S email content for Robert S Emergency Contact Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20594, 20594, payrollDBName,  testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G23.13.4: Validate Mitchell S email content for Robert S Emergency Contact Details Approve");

        logMessage("Item G23.13.5: Validate Jules N email content for Robert S Emergency Contact Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20595, 20595, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in G23.13.5: Validate Jules N email content for Robert S Emergency Contact Details Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10291.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G24 /////////////////////////////////////////////////

    @Test (priority = 10301)
    public void testG10301_EditProfileChange_AnyManagerApprovalAddMoreThanOneExceptionSingleLevel () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10301");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G24.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20161, 20161, emailDomainName, testSerialNo, driver), true, "Failed in Item G24.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin");

        logMessage("Item G24.2: Edit Profile change workflow - Add exception workflow - When a member changes:Additional information;then:send a notification;to managers in:Sub E via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20162, 20162, emailDomainName, testSerialNo, driver), true, "Failed in Item G24.2: Edit Profile change workflow - Add exception workflow - When a member changes:Additional information;then:send a notification;to managers in:Sub E via Admin.");

        ////////////// Jim Changed: Temp add sign in and sign out //////////////////
        signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);
        //////

        logMessage("Item G24.3: Add exception workflow - Name; then:send a notification; to managers: Team C  via Admin / Work contact; then:send a notification;to managers:Team C  via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20163, 20163, emailDomainName, testSerialNo, driver), true, "Failed in Item G24.3: Add exception workflow - Name; then:send a notification; to managers: Team C  via Admin / Work contact; then:send a notification;to managers:Team C  via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10301");
        myAssert.assertAll();

    }


    @Test (priority = 10311)
    public void testG10311_ActivateEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10311.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G24.4.1: Activate Malley S", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20041, 20041, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G24.4.1: Activate Malley S.");

        logMessage("Item G24.4.2: Activate Tanya D.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(110, 110, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G24.4.2: Activate Tanya D");

        logMessage("Item G24.4.3: Activate Steve B.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20045, 20045, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G24.4.3: Activate Steve B.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10311.");
        myAssert.assertAll();
    }

    @Test (priority = 10321)
    public void testG10321_FirstLogon_allEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test Logon  All Employees G10321.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G24.5.1: First Logon Malley S.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver),true, "Failed in Item G24.5.1: First Logon Malley S.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G24.5.2: First Logon  Tanya D.", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10261, 10261, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver),true, "Failed in Item Item G24.5.2: First Logon Tanya D.");

        signoutESS(driver);
        driver.close();

        logMessage("Item G24.5.3: First Logon as Steve B", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10300, 10300, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item Item G24.5.3: First Logon as Steve B.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test G10321.");
        myAssert.assertAll();

    }


    @Test (priority = 10331)
    public void testG10331_AddMalleySTeamC_ValidateTeams() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10331.");

        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item G24.6: Add Malley S to Team C and assign as Manager via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20221, 20221, testSerialNo, driver),  true, "Failed in Item G24.6: Add Malley S to Team C and assign as Manager via Admin");

        SystemLibrary.logMessage("Item G24.7: Validate unassigned list", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20226, 20226, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.7: Validate unassigned list.");

        SystemLibrary.logMessage("Item G24.8: Validate the Team - Team C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20227, 20227, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.8: Validate the Team - Team C.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10331.");
        myAssert.assertAll();

    }

    @Test (priority = 10341)
    public void testG10341_ValidateAndChangePersonalInfoTanyaD() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10341.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Tanya D");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10261, 10261, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G24.9: Validate Tanya D Dashboard via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20456, 20456, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.9: Validate Tanya D Dashboard via Tanya D");

        logMessage("Item G24.10: Validate Tanya D Team Role - Approval process via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20251, 20251, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.10: Validate Tanya D Team Role - Approval process via Tanya D");

        logMessage("Item G24.11: Add Tanya D Bank Account Details via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.11: Add Tanya D Bank Account Details via Tanya D");

        logMessage("Item G24.12: Edit Tanya D Gender via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20296, 20296, testSerialNo, driver), true, "Failed in Item G24.12: Edit Tanya D Gender via Tanya D");

        logMessage("Item G24.13: Edit Tanya D Surname via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20301, 20301, testSerialNo, driver), true, "Failed in Item G24.13: Edit Tanya D Surname via Tanya D");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10341");
        myAssert.assertAll();
    }

    @Test (priority = 10351)
    public void testG10351_ValidateTanyaDEmailsAfterEmergencyContactChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10351");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G24.15.1: Validate Tanya D email content for Tanya D Name Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20601, 20601, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.1: Validate Tanya D email content for Tanya D Name Details Change");

        logMessage("Item G24.15.2: Validate Tanya D email content for Tanya D Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20602, 20602, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.2: Validate Tanya D email content for Tanya D Bank Accounts Details Change");

        logMessage("Item G24.15.3: Validate Tanya D email content for Tanya D for Additional Information Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20603, 20603, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.3: Validate Tanya D email content for Tanya D for Additional Information Details Change");

        logMessage("Item G24.15.4: Validate Sue A email content for Tanya D Name Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20604, 20604, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.4: Validate Sue A email content for Tanya D Name Details Change");

        logMessage("Item G24.15.5: Validate Steve B email content for Tanya D for Additional Information Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20605, 20605, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.5: Validate Steve B email content for Tanya D for Additional Information Details Change");

        logMessage("Item G24.15.6: Validate Steve B email content for Tanya D for Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20606, 20606, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.6: Validate Steve B email content for Tanya D for Bank Accounts Details Change");

        logMessage("Item G24.15.7: Validate Malley S email content for Tanya D Name Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20607, 20607, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.15.7: Validate Malley S email content for Tanya D Name Details Change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10351.");
        myAssert.assertAll();
    }

    @Test(priority = 10361)
    public static void testG10361_ApproveTanyaDPersonInfoDetailsValidateViaSteveB() throws Exception {
        logMessage("*** Start Test 10361.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Steve B");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10300, 10300, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G24.16: Validate Steve B Dashboard for pending approvals via Steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20461, 20461, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.16: Validate Steve B Dashboard for pending approvals via Steve B");

        logMessage("Item G24.17: Validate Steve B Approval Page via Steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.17: Validate Steve B Approval Page via Steve B");

        logMessage("Item G24.18: Decline Tanya D Bank Account Details add comment via Steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.18: Decline Tanya D Bank Account Details add comment via Steve B");

        logMessage("Item G24.19: Validate Steve B Approval Page via Steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20502, 20502, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.19: Validate Steve B Approval Page via Steve B");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10361.");
        myAssert.assertAll();
    }


    @Test (priority = 10371)
    public void testG10371_ValidateTanyaDEmailsAfterEmergencyContactApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10371");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G24.20.1: Validate Tanya D email content for Tanya D Name Details Decline", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20611, 20611, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G24.20.1: Validate Tanya D email content for Tanya D Name Details Decline");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10371.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G26 /////////////////////////////////////////////////

    @Test(priority = 10381)
    public void testG10381_AddTeamsAndMoveTeamMembersAndActivate() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test G10381");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G26.1: Add Other Team L.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(20231, 20231, driver), true, "Failed in Item G26.1: Add Other Team L");

        logMessage("Item G26.2: Move Team member Carmin C to Other Team L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.moveMemberToTeam_Main(20232, 20232, testSerialNo, emailDomainName, driver), true, "Failed in Item G26.2: Move Team member Carmin C to Other Team L");

        logMessage("Item G26.3: Move Team member Kathy N to Other Team L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.moveMemberToTeam_Main(20233, 20233, testSerialNo, emailDomainName, driver), true, "Failed in Item G26.3: Move Team member Kathy N to Other Team L");

        logMessage("Item G26.4: Validate Other Team L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20241, 20241, testSerialNo, emailDomainName, driver), true, "Failed in Item G26.4: Validate Other Team L");

        logMessage("Item G26.5: Activate Carmin C", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(10071, 10071, payrollDBName, testSerialNo, emailDomainName, url_ESS,  driver), true, "Failed in Item G26.5: Activate Carmin C");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10381.");
        myAssert.assertAll();
    }


    @Test (priority = 10391)
    public void testG10391_ValidateAndChangePersonalInfoCarminC() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10391.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Carmin C");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10310, 10310, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G26.6: Validate Carmin C Team Role - Approval process via Carmin C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item G24.10: Validate Tanya D Team Role - Approval process via Tanya D");

        logMessage("Item G26.7: Add Carmin C Medical Condition via Carmin C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20311, 20311, testSerialNo, driver), true, "Failed in Item G26.7: Add Carmin C Medical Condition via Carmin C");

        logMessage("Item G26.8: Add Preferred name to Carmin C via Carmin C", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20316, 20316, testSerialNo, driver), true, "Failed in Item G26.8: Add Preferred name to Carmin C via Carmin C");


        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10391");
        myAssert.assertAll();
    }

    @Test (priority = 10401)
    public void testG10401_ValidateCarminCEmailsAfterNameAndMedicalConditionsChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10401");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G26.9.1: Validate Carmin C email content for Carmin C Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20631, 20631, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.9.1: Validate Carmin C email content for Carmin C Medical Conditions Change");

        logMessage("Item G26.9.2: Validate Carmin C email content for Carmin C Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20632, 20632, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.9.2: Validate Carmin C email content for Carmin C Name Change");

        logMessage("Item G26.9.3: Validate Admin email content for Carmin C Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20633, 20633, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.9.3: Validate Admin email content for Carmin C Medical Conditions Change");

        logMessage("Item G26.9.4: Validate Sue A email content for Carmin C Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20634, 20634, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.9.4: Validate Sue A email content for Carmin C Name Change");

        logMessage("Item G26.9.5: Validate Malley S email content for Carmin C  Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20635, 20635, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.9.5: Validate Malley S email content for Carmin C  Name Change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10401.");
        myAssert.assertAll();
    }

    @Test (priority = 10411)
    public void testG10411_ValidateAdminDashboardAdminApprovals() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10411.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G26.10: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20471, 20471, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G26.10: Validate Admin Dashboard via Admin");

        logMessage("Item G26.11: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20511, 20511, testSerialNo, emailDomainName, driver), true, "Failed in Item G26.11: Validate My Approval List via Admin");

        logMessage("Item G26.12: Approve Carmin C Profile change Details via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20251, 20251, testSerialNo, emailDomainName, driver), true, "Failed in Item G26.12: Approve Carmin C Profile change Details via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10411");
        myAssert.assertAll();
    }


    @Test (priority = 10421)
    public void testG10421_ValidateCarminCEmailsAfterEmergencyContactApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10421");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G26.13.1: Validate Carmin C email content for Carmin C Medical Conditions Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20638, 20638, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G26.13.1: Validate Carmin C email content for Carmin C Medical Conditions Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10421.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G27 /////////////////////////////////////////////////

    @Test (priority = 10431)
    public void testG10431_ActivateRichardZEmployee() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10431.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G27.1: Activate Richard Z", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20046, 20046, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G27.1: Activate Richard Z");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10431.");
        myAssert.assertAll();
    }

    @Test (priority = 10441)
    public static void testG10441_ValidateAndAddRichardZProfileDaetails() throws Exception {
        logMessage("*** Start Test 10441.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Richard Z");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10305, 10305, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G27.2: Validate Richard Z Dashboard via Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20476, 20476, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.2: Validate Richard Z Dashboard via Richard Z");

        logMessage("Item G27.3: Validate Richard Z Account Settings Page via Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_AccountSettingsPage_Main(20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.3: Step 70: Validate Richard Z Account Settings Page via Richard Z");

        logMessage("Item G27.4: Validate Richard Z Approval process via Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20266, 20266, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.4: Validate Richard Z Approval process via Richard Z");

        logMessage("Item G27.5: Add Richard Z Bank Account Details via Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.5: Add Richard Z Bank Account Details via Richard Z");

        logMessage("Item G27.6: Add Richard Z Work Mobile number via Richard Z", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20191,20191, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.6: Add Richard Z Work Mobile number via Richard Z");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10441.");
        myAssert.assertAll();
    }

    @Test (priority = 10451)
    public void testG10451_ValidateRobertSEmailsAfterEmergencyContactChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10451");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G27.7.1: Validate Richard Z email content for Richard Z Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20641, 20641, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.7.1: Validate Richard Z email content for Richard Z Bank Accounts Details Change");

        logMessage("Item G27.7.2: Validate Richard Z email content for Richard Z Work Contacts Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20642, 20642, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.7.2: Validate Richard Z email content for Richard Z Work Contacts Change");

        logMessage("Item G27.7.3: Validate Admin email content for Richard Z Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20643, 20643, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.7.3: Validate Admin email content for Richard Z Bank Accounts Details Change");

        logMessage("Item G27.7.4: Validate Sue A email content for Richard Z Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20644, 20644, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.7.4: Validate Sue A email content for Richard Z Bank Accounts Details Change");

        logMessage("Item G27.7.5: Validate Malley S email content for Richard Z Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20645, 20645, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.7.5: Validate Malley S email content for Richard Z Bank Accounts Details Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10451.");
        myAssert.assertAll();
    }



    @Test (priority = 10461)
    public void testG10461_ValidateJackDashboardAdminOtherApproval() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10461.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G27.8: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.8: Validate My Approval List via Admin");

        logMessage("Item G27.9: Approve Richard Z Profile change Details via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20256, 20256, testSerialNo, emailDomainName, driver), true, "Failed in Item G27.9: Approve Richard Z Profile change Details via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10461");
        myAssert.assertAll();
    }


    @Test (priority = 10471)
    public void testG10471_ValidateRobertSEmailsAfterEmergencyContactApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10421");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G27.10.1: Validate Richard Z email content for Richard Z Bank Accounts Details  Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20648, 20648, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G27.10.1: Validate Richard Z email content for Richard Z Bank Accounts Details  Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10471.");
        myAssert.assertAll();
    }


    ///////////////////////////////////////////////// G28 /////////////////////////////////////////////////


    @Test (priority = 10481)
    public void testG10481_ActivateCharlesLAndRobinSEmployees() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10481.");
        logMessage("Logon as Admin.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G28.1: Activate Charles L", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20071, 20071, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G28.1: Activate Charles L");

        logMessage("Item G28.2: Activate Robin S", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20072, 20072, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G28.2: Activate Robin S");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10481.");
        myAssert.assertAll();
    }


    @Test (priority = 10491)
    public void testG10491_FirstLogon_ChangePostalAddressDOB () throws Exception {
        SystemLibrary.logMessage("*** Start Test First Logon Change Postal Address DOB G10491.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Item G28.3: First Logon Robin S.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver),true, "Failed in Item G28.3: First Logon Robin S.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Charles L");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G28.4: Change Charles L existing Postal Address via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20196, 20196, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.4: Change Charles L existing Postal Address via Charles L");

        logMessage("Item G28.5: Edit Charles L DOB via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20326, 20326, testSerialNo, driver), true, "Failed in Item G28.5: Edit Charles L DOB via Charles L");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10491.");
        myAssert.assertAll();

    }


    @Test (priority = 10501)
    public void testG10501_ValidateRobertSEmailsAfterEmergencyContactChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10501");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G28.6.1: Validate Charles L email content for Charles L Additional Information Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20651, 20651, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G28.6.1: Validate Charles L email content for Charles L Additional Information Change");

        logMessage("Item G28.6.2: Validate Steve B email content for Charles L Additional Information Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20652, 20652, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G28.6.2: Validate Steve B email content for Charles L Additional Information Change");

        logMessage("Item G28.6.3: Validate Robin S email content for Charles L Address Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20653, 20653, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G28.6.3: Validate Robin S email content for Charles L Address Change");

        logMessage("Item G28.6.4: Validate Charles L email content for Charles L Address Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20654, 20654, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G28.6.4: Validate Charles L email content for Charles L Address Change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10501.");
        myAssert.assertAll();
    }


    @Test(priority = 10511)
    public static void testG10511_ApproveRobinSApproval_RemoveCharlesLFromSubD() throws Exception {
        logMessage("*** Start Test 10511.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robin S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G28.7: Validate Robin S Approval Page via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.7: Validate Robin S Approval Page via Robin S");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G28.8: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20481, 20481, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G28.8: Validate Admin Dashboard via Admin");

        logMessage("Item G28.9: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20536, 20536, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.9: Validate Other Approval List via Admin");

        logMessage("Item G28.10: Remove Member Charles L from Team SUB D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiTeamMembers(20256, 20256, emailDomainName, testSerialNo, driver), true, "Failed in Item G28.10: Remove Member Charles L from Team SUB D");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10511.");
        myAssert.assertAll();

    }


    @Test (priority = 10521)
    public static void testG10521_ValidateRobinSDashboardAndTeamSubD () throws Exception {
        logMessage("*** Start Test 10521.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robin S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G28.11: Validate Robin S Dashboard via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20486, 20486, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.11: Validate Robin S Dashboard via Robin S");

        logMessage("Item G28.12: Validate the Team - Sub D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20266, 20266, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.12: Validate the Team - Sub D");

        logMessage("Item G28.13: Decline Charles L Address Details via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20258, 20258, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.13: Approve Charles L Address Details via Robin S");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10521.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G29 /////////////////////////////////////////////////

    @Test (priority = 10531)
    public static void testG10531_ValidateRobinSDashboardAndTeamSubD () throws Exception {
        logMessage("*** Start Test 10531.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G29.1: Remove Manager Robin S from Team SUB D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiTeamMembers(20281, 20281, emailDomainName, testSerialNo, driver), true, "Failed in Item G29.1: Remove Manager Robin S from Team SUB D");

        logMessage("Item G29.2: Validate the Team - Sub D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20286, 20286, testSerialNo, emailDomainName, driver), true, "Failed in Item G28.12: Validate the Team - Sub D");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10531.");
        myAssert.assertAll();
    }

    ///////////////////////////////////////////////// G30 /////////////////////////////////////////////////

    @Test (priority = 10541)
    public void testG10541_AddAdminTheBoss_AddPreferredMedicalConditionViaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10541.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G30.1: Add Admin to Team The Boss and assign as Member via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20291, 20291, testSerialNo, driver),  true, "Failed in Item G30.1: Add Admin to Team The Boss and assign as Member via Admin");

        logMessage("Item G30.2: Validate unassigned list", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(20294, 20294, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.2: Validate unassigned list.");

        logMessage("Item G30.3: Validate Admin Teams and Roles via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.3: Validate Admin Teams and Roles via Admin");

        logMessage("Item G30.4: Validate Admin Approval process via Admin ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20282, 20282, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.4: Validate Admin Approval process via Admin ");

        logMessage("Item G30.5: Add Admin Medical Condition via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20331, 20331, testSerialNo, driver), true, "Failed in Item G30.5: Add Admin Medical Condition via Admin");

        logMessage("Item G30.6: Add Preferred name to Admin via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20336, 20336, testSerialNo, driver), true, "Failed in Item G30.6: Add Preferred name to Admin via Admin");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10541.");
        myAssert.assertAll();

    }


    @Test (priority = 10551)
    public void testG10551_ValidateAdminmailsAfterPreferredMedicalConditionChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10551");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G30.7.1: Validate Admin email content for Admin Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20661, 20661, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.1: Validate Admin email content for Admin Medical Conditions Change");

        logMessage("Item G30.7.2: Validate Mitchell S email content for Admin Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20662, 20662, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.2: Validate Mitchell S email content for Admin Medical Conditions Change");

        logMessage("Item G30.7.3: Validate Jules N email content for Admin Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20663, 20663, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.3: Validate Jules N email content for Admin Medical Conditions Change");

        logMessage("Item G30.7.4: Validate Admin email content for Admin Preferred Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20664, 20664, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.4: Validate Admin email content for Admin Preferred Name Change");

        logMessage("Item G30.7.5: Validate Sue A email content for Admin Preferred Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20665, 20665, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.5: Validate Sue A email content for Admin Preferred Name Change");

        logMessage("Item G30.7.6: Validate Malley S email content for Admin Preferred Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20666, 20666, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.7.6: Validate Malley S email content for Admin Preferred Name Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10551.");
        myAssert.assertAll();
    }


    @Test(priority = 10561)
    public static void testG10561_ApproveAdminPersonalInformationValidate() throws Exception {
        logMessage("*** Start Test G10561.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Mitchell S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10131, 10131, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G30.8: Validate Mitchell S Dashboard via Mitchell S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20491, 20491, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.8: Validate Mitchell S Dashboard via Mitchell S");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        logMessage("Item G30.9: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20496, 20496, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.9: Validate Admin Dashboard via Admin");

        logMessage("Item G30.10: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in Item G30.10: Validate Other Approval List via Admin");

        logMessage("Item G30.11: Approve all Other Approval via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasic.approveAllOtherApproval_ViaAdmin(driver), true, "Failed in Item G30.11: Approve all Other Approval via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10561.");
        myAssert.assertAll();

    }

    @Test (priority = 10571)
    public void testG10571_ValidateAdminmailsAfterPreferredMedicalConditionApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10571");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G30.12.1: Validate Admin email content for Admin Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20671, 20671, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.12.1: Validate Admin email content for Admin Medical Conditions Change");

        logMessage("Item G30.12.2: Validate Jules N email content for Admin Medical Conditions Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20672, 20672, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.12.2: Validate Jules N email content for Admin Medical Conditions Change");

        logMessage("Item G30.12.3: Validate Mitchell S email content for Admin Medical Conditions Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20673, 20673, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G30.12.3: Validate Mitchell S email content for Admin Medical Conditions Change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10571.");
        myAssert.assertAll();
    }




    ///////////////////////////////// G31 ////////////////////////////////////////

    @Test(priority = 10581)
    public static void testG10581_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("Start Test 10581.");

        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G31.1: Validate Employee Robert S Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20121, 20121, emailDomainName, testSerialNo), true, "Failed in Item G31.1: Validate Employee Robert S Detail Report via Sage Micropay");

        logMessage("Item G31.2: Validate Employee Charles L Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20123, 20123, emailDomainName, testSerialNo), true, "Failed in Item G31.2: Validate Employee Charles L Detail Report via Sage Micropay");

        logMessage("Item G31.3: Validate Employee Tanya D Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20125, 20125, emailDomainName, testSerialNo), true, "Failed in Item G31.3: Validate Employee Tanya D Detail Report via Sage Micropay");

        logMessage("Item G31.4: Validate Employee Richard Z Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20127, 20127, emailDomainName, testSerialNo), true, "Failed in Item G31.4: Validate Employee Richard Z Detail Report via Sage Micropay");

        logMessage("Item G31.5: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item G31.5: Implement eHR via Sage Micropay.");
        close_ImplementHRScreen();

        logMessage("Item G31.6: Validate Employee Robert S Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20131, 20131, emailDomainName, testSerialNo), true, "Failed in Item G31.6: Validate Employee Robert S Detail Report via Sage Micropay");

        logMessage("Item G31.7: Validate Employee Charles L Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20133, 20133, emailDomainName, testSerialNo), true, "Failed in Item G31.7: Validate Employee Charles L Detail Report via Sage Micropay");

        logMessage("Item G31.8: Validate Employee Tanya D Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20135, 20135, emailDomainName, testSerialNo), true, "Failed in Item G31.8: Validate Employee Tanya D Detail Report via Sage Micropay");

        logMessage("Item G31.9: Validate Employee Richard Z Detail Report via Sage Micropay", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20137, 20137, emailDomainName, testSerialNo), true, "Failed in Item G31.9: Validate Employee Richard Z Detail Report via Sage Micropay");


        exitMeridian();
        logMessage("End of Test G10581.");
        myAssert.assertAll();
    }


    ///////////////////////////////// G32 ////////////////////////////////////////

    @Test (priority = 10591)
    public void testG10591_EditProfileChange_AnyManagerApprovalAddExceptionsAndActivateEmp () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10591");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete All email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20171, 20171, emailDomainName, testSerialNo, driver), true, "Failed in Item G32.1: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin");

        logMessage("Item G32.2: Delete Profile change workflow : Additional information then send a notification to managers in Sub E.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20172, 20172, emailDomainName, testSerialNo, driver), true, "Failed in Item G32.2: Delete Profile change workflow : Additional information then send a notification to managers in Sub E.");

        logMessage("Item G32.3: Delete Profile change workflow : Work contact then send a notification to managers in Team C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20173, 20173, emailDomainName, testSerialNo, driver), true, "Failed in Item G32.3: Delete Profile change workflow : Work contact then send a notification to managers in Team C.");

        logMessage("Item G32.4: Activate Stanley B", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(111, 111, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G6.1: Activate Jack F");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test 10591");
        myAssert.assertAll();

    }

    @Test (priority = 10601)
    public static void testG10601_AddStanleyBProfileDaetailsAndValidate() throws Exception {
        logMessage("*** Start Test 10601.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Stanley B");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10221, 10221, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.5: Add Stanley B Bank Account Details via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20061, 20061, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.5: Add Stanley B Bank Account Details via Stanley B");

        logMessage("Item G32.6: Validate Stanley B Teams and Roles via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main(20291, 20291, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.6: Validate Stanley B Teams and Roles via Stanley B");

        logMessage("Item G32.7: Validate Stanley B Teams Role - Approval process via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20296, 20296, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.7: Validate Stanley B Teams Role - Approval process via Stanley B");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10601.");
        myAssert.assertAll();
    }

    @Test (priority = 10602)
    public static void testG10602_validateOtherApprovalListViaAdmin() throws Exception {
        logMessage("*** Start Test 10602.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.8: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20551, 20551, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.8: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10602.");
        myAssert.assertAll();
    }

    @Test (priority = 10611)
    public static void testG10611_AddRobinSRedirectApprovalAndValidate() throws Exception {
        logMessage("*** Start Test 10611.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Steve B");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10300, 10300, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.9: Validate Ellipsis menu in Teams and Roles page via steve B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenInTeamsRolesDetailsPage_Main(20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.9: Validate Ellipsis menu in Teams and Roles page via steve B.");

        logMessage("Item G32.10: Add Robin S as Redirect Approval via steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addRedirectApprovals_Main(20306, 20306, testSerialNo, emailDomainName, driver), true, "Faield in Item G32.10: Add Robin S as Redirect Approval via steve B.");

        logMessage("Item G32.11: Validate Ellipsis menu in Teams and Roles page via steve B.");
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenInTeamsRolesDetailsPage_Main(20311, 20311, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.11: Validate Ellipsis menu in Teams and Roles page via steve B.");

        logMessage("Item G32.12: Edit Date on Redirect approver via steve B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editRedirectApprovals_Main(20316, 20316, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.12: Edit Date on Redirect approver via steve B.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10611.");
        myAssert.assertAll();
    }

    @Test(priority = 10621)
    public static void testG10621_validateRedirectedApproversAndOtherApprovalList() throws Exception {
        logMessage("*** Start Test 10621 ");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.13: validate Redirected Approvers via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_RedirectedApprovers_Main_ViaAdmin(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.13: validate Redirected Approvers via Admin.");

        logMessage("Item G32.14: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20556, 20556, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.14: Validate Other Approval List via Admin ");

        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("End of Test 10621.");
        myAssert.assertAll();
    }


    @Test (priority = 10631)
    public void testG10631_ValidateEmailsAfterStanleyBBankAccountadding() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10631");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G32.15.1: Validate Stanley B email content for Stanley B Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20681, 20681, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.15.1: Validate Stanley B email content for Stanley B Bank Accounts Details Change");

        logMessage("Item G32.15.2: Validate Steve B email content for Stanley B Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20682, 20682, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.15.2: Validate Stanley B email content for Stanley B Bank Accounts Details Change");

        logMessage("Item G32.15.3: Validate Robin S email content for redirected approvals to Robin S", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20683, 20683, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.15.3: Validate Robin S email content for redirected approvals to Robin S");

        logMessage("Item G32.15.4: Validate Steve B email content for redirected approvals to Robin S", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20684, 20684, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.15.4: Validate Steve B email content for redirected approvals to Robin S");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10631");
        myAssert.assertAll();
    }

    @Test (priority = 10641)
    public static void testG10641_ValidateRobinSDashboardAndApprove () throws Exception {
        logMessage("*** Start Test 10641.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robin S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.16: Validate Robin S Dashboard via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.16: Validate Robin S Dashboard via Robin S");

        logMessage("Item G32.17: Validate Robin S Approval Page via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20561, 20561, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.17: Validate Robin S Approval Page via Robin S");

        logMessage("Item G32.18: Approve Stanley B Bank Account Details via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.18: Approve Stanley B Bank Account Details via Robin S");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10641.");
        myAssert.assertAll();
    }

    @Test (priority = 10651)
    public static void testG10651_AddStanleyBProfileDetailsAndValidate() throws Exception {
        logMessage("*** Start Test 10651.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Stanley B");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10221, 10221, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.19: Add Preferred name to Stanley B via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20341, 20341, testSerialNo, driver), true, "Failed in Item G32.19: Add Preferred name to Stanley B via Stanley B");

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.20: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20571, 20571, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.20: Validate Other Approval List via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10651.");
        myAssert.assertAll();
    }

    @Test (priority = 10661)
    public void testG10661_ValidateEmailsAfterStanleyBBankAccountadding() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10661");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G32.21.1: Validate Steve B email content  for Stanley B Bank Accounts Details Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20691, 20691, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.21.1: Validate Steve B email content  for Stanley B Bank Accounts Details Approve");

        logMessage("Item G32.21.2: Validate Stanley B email content for Stanley B Bank Accounts Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20692, 20692, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.21.2: Validate Stanley B email content for Stanley B Bank Accounts Details Change");

        logMessage("Item G32.21.3: Validate Stanley B email content for Stanley Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20693, 20693, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.21.3: Validate Stanley B email content for Stanley Name Change");

        logMessage("Item G32.21.4: Validate Sue A email content for  Stanley Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20694, 20694, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.21.4: Validate Sue A email content for  Stanley Name Change");

        logMessage("Item G32.21.5: Validate Malley S email content for  Stanley Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20695, 20695, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G32.21.5: Validate Malley S email content for  Stanley Name Change");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10661.");
        myAssert.assertAll();
    }


    @Test (priority = 10671)
    public static void testG10671_ValidateRobinSDashboardAndApprove () throws Exception {
        logMessage("*** Start Test 10671.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robin S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G32.22: Validate Robin S Dashboard via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20506, 20506, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.16: Validate Robin S Dashboard via Robin S");

        logMessage("Item G32.23: Validate Robin S Approval Page via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20576, 20576, testSerialNo, emailDomainName, driver), true, "Failed in Item G32.17: Validate Robin S Approval Page via Robin S");


        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10671.");
        myAssert.assertAll();
    }


    ///////////////////////////////// G33 ////////////////////////////////////////

    @Test (priority = 10681)
    public void testG10681_EditProfileChange_AllManagerApprovalDeleteExceptionAddRedirect () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10681");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.1: Edit Profile change workflow - then:approval is required;by:All manager;in: Team A; and then: do nothing via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20181, 20181, emailDomainName, testSerialNo, driver), true, "Failed in Item G33.1: Edit Profile change workflow - then:approval is required;by:All manager;in: Team A; and then: do nothing via Admin");

        logMessage("Item G33.2: Delete Profile change workflow : Name then send a notification to managers in Team C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20182, 20182, emailDomainName, testSerialNo, driver), true, "Failed in Item G33.2: Delete Profile change workflow : Name then send a notification to managers in Team C.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Robert S", testSerialNo);
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10171, 10171, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item: Logon as Robert S.");

        logMessage("Item G33.3: Add Stanley B as Redirect Approval via Robert S from today to one week", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addRedirectApprovals_Main(20321, 20321, testSerialNo, emailDomainName, driver), true, "Faield in Item G33.3: Add Stanley B as Redirect Approval via Robert S from today to one week");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10681");
        myAssert.assertAll();

    }

    @Test (priority = 10691)
    public void testG10691_ValidateEmailsAfterStanleyBredirectedApprovals () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10691");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G33.4.1: Validate Stanley B email content for redirected approvals to Stanley B", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20701, 20701, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.4.1: Validate Stanley B email content for redirected approvals to Stanley B");

        logMessage("Item G33.4.2: Validate  Robert S email content for redirected approvals to Stanley B", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20702, 20702, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.4.2: Validate  Robert S email content for redirected approvals to Stanley B");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10691.");
        myAssert.assertAll();
    }

    @Test (priority = 10701)
    public void testG10701_ValidateStanleyBTeamsRolesAndEllipsisViaAdmin() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10701.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.5: Validate Stanley B Teams and Roles via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20331, 20331, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.5: Validate Stanley B Teams and Roles via Admin");

        logMessage("Item G33.6: Validate Stanley B Ellipsis menu in Teams and Roles page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_EllipsisMenInTeamsRolesDetailsPage_Main(20334, 20334, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.6: Validate Stanley B Ellipsis menu in Teams and Roles page via Admin.");

        logMessage("Item G33.7: Validate Robert S Teams Role - Approval process via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20337, 20337, testSerialNo, emailDomainName, driver), true, "Failed in Item 33.7: Validate Robert S Teams Role - Approval process via Admin");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G10701.");
        myAssert.assertAll();

    }

    @Test (priority = 10711)
    public static void testG10711_EditRobinSSurnameAndActivateSharonA () throws Exception {
        logMessage("*** Start Test 10711.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Robin S");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10321, 10321, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.8: Edit Robin S Surname via Robin S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20351, 20351, testSerialNo, driver), true, "Failed in Item G33.8: Edit Robin S Surname via Robin S");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.9: Activate Sharon A", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(112, 112, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item G33.9: Activate Sharon A");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10711 ");
        myAssert.assertAll();
    }


    @Test (priority = 10721)
    public static void testG10721_AddSharonABankDetailsAndStanleyBWorkOfficeNumber () throws Exception {
        logMessage("*** Start Test 10721.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Sharon A");
        WebDriver driver= GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.10: Add Sharon A Bank Account Details via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiBankAccount(20071, 20071, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.10: Add Sharon A Bank Account Details via Sharon A");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Stanley B");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10221, 10221, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.11: Add Stanley B Work Office number via steve B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20211,20211, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.11: Add Stanley B Work Office number via steve B");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10721.");
        myAssert.assertAll();
    }

    @Test (priority = 10731)
    public void testG10731_ValidateEmailRobinSurNameAddSharonABankAddStanleyBWorkNumber() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10731.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G33.12.1: Validate Robin S email content for Robin S Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20711, 20711, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.1: Validate Robin S email content for Robin S Name Change");

        logMessage("Item G33.12.2: Validate Stanley B email content for Robin S Name Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20712, 20712, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.2: Validate Stanley B email content for Robin S Name Change ");

        logMessage("Item G33.12.3: Validate Robert S email content for Robin S Name Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20713, 20713, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.3: Validate Robert S email content for Robin S Name Change ");

        logMessage("Item G33.12.4: Validate Sharon A email content for Sharon A  Bank Accounts Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20714, 20714, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F33.12.4: Validate Sharon A email content for Sharon A  Bank Accounts Change   ");

        logMessage("Item G33.12.5: Validate Stanley B email content for Sharon A  Bank Accounts Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20715, 20715, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F33.12.5: Validate Stanley B email content for Sharon A  Bank Accounts Change  ");

        logMessage("Item G33.12.6: Validate Robert S email content for Sharon A  Bank Accounts Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20716, 20716, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.6: Validate Robert S email content for Sharon A  Bank Accounts Change ");

        logMessage("Item G33.12.7: Validate Stanley B email content for Stanley B Work Contacts Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20717, 20717, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.7: Validate Stanley B email content for Stanley B Work Contacts Change ");

        logMessage("Item G33.12.8: Validate Robert S email content for Stanley B Work Contacts Change ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20718, 20718, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.8: Validate Robert S email content for Stanley B Work Contacts Change ");

        logMessage("Item G33.12.9: Validate Stanley B email content for Stanley B Work Contacts for your approval", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20719, 20719, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.12.9: Validate Stanley B email content for Stanley B Work Contacts for your approval");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10731.");
        myAssert.assertAll();
    }

    @Test(priority = 10741)
    public static void testG10741_validateRedirectedApproversAndOtherApprovalList() throws Exception {
        logMessage("*** Start Test 10741 ");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.13: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20581, 20581, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.13: Validate Other Approval List via Admin ");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Stanley B");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10221, 10221, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G33.14: Validate Stanley B Approval List via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20582, 20582, testSerialNo, emailDomainName, driver), true, "Falied in Item G33.14: Validate Stanley B Approval List via Stanley B");

        logMessage("Item G33.15: Approve Stanley B all Approvals via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.15: Approve Stanley B all Approvals via Stanley B");

        signoutESS(driver);
        driver.close();
        logMessage("End of Test 10741.");
        myAssert.assertAll();
    }


    @Test (priority = 10751)
    public void testG10751_ValidateEmailRobinSurNameAddSharonABankAddStanleyBWorkNumberApprove() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10751.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G33.16.1: Validate Robin S email content for Robin S Name Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20721, 20721, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.16.1: Validate Robin S email content for Robin S Name Approve");

        logMessage("Item G33.16.2: Validate Robert S email content for Robin S Name Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20722, 20722, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.16.2: Validate Robert S email content for Robin S Name Approve");

        logMessage("Item G33.16.3: Validate Sharon A email content for Sharon A  Bank Accounts Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20723, 20723, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.16.3: Validate Sharon A email content for Sharon A  Bank Accounts Approve ");

        logMessage("Item G33.16.4: Validate Robert S email content for Sharon A  Bank Accounts Approve ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20724, 20724, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F33.16.4: Validate Robert S email content for Sharon A  Bank Accounts Approve");

        logMessage("Item G33.16.5: Validate Robert S email content for Stanley B Work Contacts Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20725, 20725, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item F33.16.5: Validate Stanley B email content for Stanley B Work Contacts Approve");

        logMessage("Item G33.16.6: Validate Stanley B email content for Stanley B Work Contacts Approve ", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20726, 20726, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G33.16.6: Validate Stanley B email content for Sharon A  Bank Accounts Approve ");

        logMessage("Item G33.16.7: Validate Steve B email content for Sharon A  Bank Accounts Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20727, 20727, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G33.16.7: Validate Steve B email content for Sharon A  Bank Accounts Approve ");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10751.");
        myAssert.assertAll();
    }

    ///////////////////////////////// G34 ////////////////////////////////////////

    @Test (priority = 10761)
    public void testG10761_EditProfileChange_OneLevelAnyApprover () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10761");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.1: Edit Profile change workflow - then:approval is required;by:Tanya DOWN;and then:do nothing via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20191, 20191, emailDomainName, testSerialNo, driver), true, "Failed in Item G34.1: Edit Profile change workflow - then:approval is required;by:Tanya DOWN;and then:do nothing via Admin");


        signoutESS(driver);
        driver.close();

        logMessage("Logon as Sharon A");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.2: Validate Sharon A Teams Role - Approval process via Sharon A ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20341, 20341, testSerialNo, emailDomainName, driver), true, "Failed in Item G34.2: Validate Sharon A Teams Role - Approval process via Sharon A ");

        logMessage("Item G34.3: Edit Sharon A Given Name via Sharon A ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20361, 20361, testSerialNo, driver), true, "Failed in Item G34.3: Edit Sharon A Given Name via Sharon A ");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10761");
        myAssert.assertAll();

    }

    @Test (priority = 10771)
    public void testG10771_ValidateAllEmailAfterSharonAGivenNamechange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10771");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G34.4.1: Validate Sharon A email content for Sharon A Given Name change", testSerialNo);
        //myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20731, 20731, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G34.4.1: Validate Sharon A email content for Sharon A Given Name change");

        logMessage("Item G34.4.2: Validate Tanya D email content for Sharon A Given Name change ", testSerialNo);
        //myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20732, 20732, payrollDBName, testSerialNo, emailDomainName, url_ESS), false, "Failed in Item G34.4.2: Validate Tanya D email content for Sharon A Given Name change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10771.");
        myAssert.assertAll();
    }

    @Test (priority = 10781)
    public static void testG10781_ValidateTanyaDDashboardAndApprovalProcess () throws Exception {
        logMessage("*** Start Test 10781.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Tanya D");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10431, 10431, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.5: Validate Tanya D Dashboard via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20511, 20511, testSerialNo, emailDomainName, driver), true, "Failed in Item G34.5: Validate Tanya D Dashboard via Tanya D");

        logMessage("Item G34.6: Validate Tanya D Approval List via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20591, 20591, testSerialNo, emailDomainName, driver), true, "Falied in Item G34.6: Validate Tanya D Approval List via Tanya D");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.7: Edit Profile change workflow - then:approval is required;by:any manager;inthe members team;and then:do nothing via Admin ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20194, 20194, emailDomainName, testSerialNo, driver), true, "Failed in Item G34.7: Edit Profile change workflow - then:approval is required;by:any manager;inthe members team;and then:do nothing via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10781.");
        myAssert.assertAll();
    }


    @Test (priority = 10791)
    public static void testG10791_ValidateTanyaDDashboardAndApprovalProcess () throws Exception {
        logMessage("*** Start Test 10791.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Tanya D");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10431, 10431, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.8: Validate Tanya D Dashboard via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20516, 20516, testSerialNo, emailDomainName, driver), false, "Failed in Item G34.8: Validate Tanya D Dashboard via Tanya D");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.9: Edit Profile change workflow - then:approval is required;by:Tanya DOWN;and then:do nothing via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20197, 20197, emailDomainName, testSerialNo, driver), true, "Failed in Edit Profile change workflow - then:approval is required;by:Tanya DOWN;and then:do nothing via Admin ");

        signoutESS(driver);
        driver.close();

        logMessage("*** End of test G10791.");
        myAssert.assertAll();
    }


    @Test (priority = 10801)
    public static void testG10801_ValidatePhantomFDashboardAndApprove () throws Exception {
        logMessage("*** Start Test 10801.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Tanya D");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10431, 10431, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G34.10: Validate Tanya D Dashboard via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20521, 20521, testSerialNo, emailDomainName, driver), true, "Failed in Item G34.10: Validate Robin S Dashboard via Robin S");

        logMessage("Item G34.11: Approve Tanya D all Approvals via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item G33.15: Approve Stanley B all Approvals via Stanley B");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10801.");
        myAssert.assertAll();
    }

    @Test (priority = 10811)
    public void testG10811_ValidateAllEmailAfterSharonAGivenNameApprove () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10811");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G34.12.1: Validate Sharon A email content for Sharon A Given Name Approve", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20741, 20741, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G34.12.1: Validate Sharon A email content for Sharon A Given Name Approve");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10811.");
        myAssert.assertAll();
    }





///////////////////////////////// G35 ////////////////////////////////////////

    @Test (priority = 10821)
    public void testG10821_EditProfileChange_TwoLevelApproversOneLevelRedirected () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10821");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G35.1: Edit Profile change workflow - then:approval is required;by:Tanya D;and then:approval is required;by2:Robert S via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20201, 20201, emailDomainName, testSerialNo, driver), true, "Failed in Item G35.1: Edit Profile change workflow - then:approval is required;by:Tanya D;and then:approval is required;by2:Robert S via Admin");


        signoutESS(driver);
        driver.close();

        logMessage("Logon as Tanya D");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10431, 10431, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G35.2: Change Tanya D existing Postal Address via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20221, 20221, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item G35.2: Change Tanya D existing Postal Address via Tanya D");

        logMessage("Item G35.3: Add Tanya D Medical Condition via Tanya D", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20371, 20371, testSerialNo, driver), true, "Failed in Item G35.3: Add Tanya D Medical Condition via Tanya D");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10821");
        myAssert.assertAll();

    }

    @Test (priority = 10831)
    public void testG10831_ValidateEmailsAfterTanyaDAddressAndMedicalchange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10831");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G35.4.1: Validate Tanya D email content for Tanya D", testSerialNo);
        //myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20751, 20751, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.4.1: Validate Tanya D email content for Tanya D");

        logMessage("Item G35.4.2: Validate Stanley B email content for Tanya D ", testSerialNo);
        // myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20752, 20752, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.4.2: Validate Stanley B email content for Tanya D");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10831");
        myAssert.assertAll();
    }

    @Test(priority = 10841)
    public static void testG10841_validateApprovalListAndArroveDeclineViaStanleyB() throws Exception {
        logMessage("*** Start Test 10841 ");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Stanley B");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10221, 10221, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G35.5: Validate Stanley B Approval List via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20601, 20601, testSerialNo, emailDomainName, driver), true, "Falied in Item G35.5: Validate Stanley B Approval List via Stanley B");

        logMessage("Item G35.6: Approve Tanya D Approve Postel Address via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20291, 20291, testSerialNo, emailDomainName, driver), true, "Failed in Item G35.6: Approve Tanya D Approve Postel Address via Stanley B");

        logMessage("Item G35.7: Decline Tanya D Medical conditions with comment via Stanley B", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20292, 20292, testSerialNo, emailDomainName, driver), true, "Failed in Item G35.7: Decline Tanya D Medical conditions with comment via Stanley B");

        signoutESS(driver);
        driver.close();
        logMessage("End of Test 10741.");
        myAssert.assertAll();
    }



    @Test (priority = 10851)
    public void testG10851_ValidateEmailTanyaDAddressAndMedicalChangesApproveAndDecline() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10851.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G35.8.1: Validate Tanya D email content for Tanya D Addresses approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20761, 20761, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.8.1: Validate Tanya D email content for Tanya D Addresses approved");

        logMessage("Item G35.8.2: Validate Robert S email content for Tanya D Addresses approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20762, 20762, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.8.2: Validate Robert S email content for Tanya D Addresses approved");

        logMessage("Item G35.8.3: Validate Tanya D email content for Tanya D Medical Decline", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20763, 20763, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.8.3: Validate Tanya D email content for Tanya D Medical Decline");

        logMessage("Item G35.8.4: Validate Robert S email content for Tanya D Medical Decline", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20764, 20764, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G35.8.4: Validate Robert S email content for Tanya D Medical Decline");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10851.");
        myAssert.assertAll();
    }

    ///////////////////////////////// G36 ////////////////////////////////////////

    @Test(priority = 10861)
    public void testG10861_AddDuplicateAdminAndRemoveMemberFromTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test G10861.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G36.1: Add Duplicate Administrator role Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20161, 20161, driver), true, "Failed in Item G36.1: Add Duplicate Administrator role Via Admin.");

        logMessage("Item G36.2: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20194, 20194, emailDomainName, testSerialNo, driver), true, "Failed in Item G36.2: Edit Profile change workflow - then:approval is required;by:ANY manager;in:the members team via Admin");

        logMessage("Item G36.3: Select Second Admin and untick Administrator in Profile change workflow Via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20211, 20211, emailDomainName, testSerialNo, driver), true, "Failed in Item G36.3: Select Second Admin and untick Administrator in Profile change workflow Via Admin");

        logMessage("Item G36.4: Remove Steve B from Team Sub E", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiTeamMembers(20301, 20301, emailDomainName, testSerialNo, driver), true, "Failed in Item G36.4: Remove Steve B from Team Sub E");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test G10861.");
        myAssert.assertAll();
    }

    @Test (priority = 10871)
    public void testG10871_AddMartinFirstNameGDashboard_AddBankDetails () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10871.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Charles L");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G36.5: Validate Charles L Teams Role - Approval process via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20346, 20346, testSerialNo, emailDomainName, driver), true, "Failed in Item G36.5: Validate Charles L Teams Role - Approval process via Charles L");

        logMessage("Item G36.6: Add Charles L Middle name via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20381, 20381, testSerialNo, driver), true, "Failed in Item G36.6: Add Charles L Middle name via Charles L");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10871");
        myAssert.assertAll();
    }

    @Test (priority = 10881)
    public void testG10881_ValidateEmailContentCharlesLAddedMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10881.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G36.7.1: Validate Admin email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20771, 20771, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G36.7.1: Validate Admin email content for Charles L Add Middle Name");

        logMessage("Item G36.7.2: Validate Charles L email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20772, 20772, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G36.7.2: Validate Charles L email content for Charles L Add Middle Name");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10881.");
        myAssert.assertAll();
    }

    @Test(priority = 10891)
    public static void testG10891_ValidateAdminDashboadMyApprovalAndDecline() throws Exception {
        logMessage("*** Start Test G10891.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G36.8: Validate Admin Dashboard via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20526, 20526, testSerialNo, emailDomainName, driver), true, "Failed in Item G36.8: Validate Admin Dashboard via Admin");

        logMessage("Item G36.9: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20611, 20611, testSerialNo, emailDomainName, driver), true, "Failed in Item G36.9: Validate My Approval List via Admin");

        logMessage("Item G36.10: Decline Charles L Middle name via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item G36.10: Decline Charles L Middle name via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10891.");
        myAssert.assertAll();
    }

    @Test (priority = 10901)
    public void testG10901_ValidateEmailContentCharlesLDeclineMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10901.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G36.11.1: Validate Charles L email content for Charles L Middle Name Decline", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20776, 20776, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G36.11.1 Validate Charles L email content for Charles L Middle Name Decline");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10901.");
        myAssert.assertAll();
    }

    ///////////////////////////////// G37 ////////////////////////////////////////

    @Test (priority = 10911)
    public void testG10911_AddJackFSecondAdminAddCharlesLMiddleName () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10911.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G37.1: Assign Jack F as Admin 2 role via Admin.",  testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiAdministratorRole(20351, 20351, testSerialNo, driver), true, "Failed in Item G37.1: Assign Jack F as Admin 2 role via Admin.");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Charles L");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G37.2: Validate Charles L Directory via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20311, 20311,  testSerialNo, emailDomainName, driver), true, "Failed in Item G37.2: Validate Charles L Directory via Charles L.");

        logMessage("Item G37.3: Validate Charles L Teams Role - Approval process via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20361, 20361, testSerialNo, emailDomainName, driver), true, "Failed in Item G37.3: Validate Charles L Teams Role - Approval process via Charles L");

        logMessage("Item G37.4: Add Charles L Middle name via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20391, 20391, testSerialNo, driver), true, "Failed in Item G37.4: Add Charles L Middle name via Charles L");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10911");
        myAssert.assertAll();
    }

    @Test (priority = 10921)
    public void testG10921_ValidateEmailContentCharlesLAddedMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10921.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("delay 2 mins to receive eamil...");
        Thread.sleep(120000);

        logMessage("Item G37.5.1: Validate Admin 2  Jack F email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20791, 20791, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G37.5.1: Validate Admin 2  Jack F email content for Charles L Add Middle Name");

        logMessage("Item G37.5.2: Validate Charles L email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20792, 20792, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G37.5.2: Validate Charles L email content for Charles L Add Middle Name");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10921.");
        myAssert.assertAll();
    }


    @Test (priority = 10931)
    public void testG10931_ValidateJackDashboardAndDeclineNameChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10931.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G37.6: Validate Admin 2 Jack F Dashboard via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20531, 20531, testSerialNo, emailDomainName, driver), true, "Failed in Item G37.6: Validate Admin 2 Jack F Dashboard via Jack F");

        logMessage("Item G37.7: Validate Admin 2 Jack F My Approval List via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20621, 20621, testSerialNo, emailDomainName, driver), true, "Failed in Item G37.7: Validate Admin 2 Jack F My Approval List via Jack F");

        logMessage("Item G37.8: Decline Charles L Middle name via Admin 2 Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20311, 20311, testSerialNo, emailDomainName, driver), true, "Failed in Item G37.8: Decline Charles L Middle name via Admin 2 Jack F");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10931");
        myAssert.assertAll();
    }

    @Test (priority = 10941)
    public void testG10941_ValidateEmailContentCharlesLDeclineMiddleName () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10941");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G37.9.1: Validate Charles L email content for Charles L Middle Name Decline by Admin 2 Jack F", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20796, 20796, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G37.9.1: Validate Charles L email content for Charles L Middle Name Decline by Admin 2 Jack F");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G10941.");
        myAssert.assertAll();
    }

    ///////////////////////////////// G38 ////////////////////////////////////////

    @Test(priority = 10951)
    public void testG10951_AddDuplicateAdmin3UsingAdmin2AndAddProfileWorkflow() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test G10951.");

        logMessage("Log on ESS as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G38.1: Add Duplicate Admin role Admin 3 using Admin 2 Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.duplicateMultiRole(20171, 20171, driver), true, "Failed in Item G38.1: Add Duplicate Admin role Admin 3 using Admin 2 Via Admin.");

        logMessage("Item G38.2: Select Admin 3 and Admin 2 and untick Administrator in Profile change workflow Via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow(20221, 20221, emailDomainName, testSerialNo, driver), true, "Failed in Item G38.2: Select Admin 3 and Admin 2 and untick Administrator in Profile change workflow Via Admin");

        logMessage("Item G38.3: Assign Malley S as Admin 3 role via Admin.",  testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiAdministratorRole(20371, 20371, testSerialNo, driver), true, "Failed in Item G38.3: Assign Malley S as Admin 3 role via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test G10951.");
        myAssert.assertAll();
    }

    @Test (priority = 10961)
    public void testG10961_AddCharlesLMiddleNameAndValidateApprovalProcess () throws Exception {
        SystemLibrary.logMessage("*** Start Test G10961.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Charles L");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G38.4: Validate Charles L Directory via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(20321, 20321,  testSerialNo, emailDomainName, driver), true, "Failed in Item G38.4: Validate Charles L Directory via Charles L.");

        logMessage("Item G38.5: Validate Charles L Teams Role - Approval process via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20381, 20381, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.5: Validate Charles L Teams Role - Approval process via Charles L");

        logMessage("Item G38.6: Add Charles L Middle name via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20401, 20401, testSerialNo, driver), true, "Failed in Item G38.6: Add Charles L Middle name via Charles L");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10961");
        myAssert.assertAll();
    }

    @Test (priority = 10981)
    public void testG10981_ValidateEmailContentCharlesLAddedMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10981.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G38.7.1: Validate Admin 2  Jack F email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20801, 20801, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G38.7.1: Validate Admin 2  Jack F email content for Charles L Add Middle Name");

        logMessage("Item G38.7.2: Validate Charles L email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20802, 20802, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G38.7.2: Validate Charles L email content for Charles L Add Middle Name");

        logMessage("Item G38.7.3: Validate Admin 3 Malley S email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20803, 20803, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G38.7.3: Validate Admin 3 Malley S email content for Charles L Add Middle Name");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 10981.");
        myAssert.assertAll();
    }

    @Test (priority = 10991)
    public void testG10991_ValidateJackFMalleyS_DashboardAndApproval_DeclineNameChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G10991.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G38.8: Validate Admin 2 Jack F Dashboard via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20541, 20541, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.8: Validate Admin 2 Jack F Dashboard via Jack F");

        logMessage("Item G38.9: Validate Admin 2 Jack F My Approval List via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20631, 20631, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.9: Validate Admin 2 Jack F My Approval List via Jack F");

        logMessage("Item G38.10: Validate Admin 2 Jack F Other Approval List via Jack F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20636, 20636, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.10: Validate Admin 2 Jack F Other Approval List via Jack F");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Malley S.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G38.11: Validate Admin 2 Malley S Dashboard via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20546,  20546, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.11: Validate Admin 2 Jack F Dashboard via Jack F");

        logMessage("Item G38.12: Validate Admin 2 Malley S My Approval List via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20641, 20641, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.12: Validate Admin 2 Malley S My Approval List via Malley S");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G10991");
        myAssert.assertAll();
    }

    @Test(priority = 11001)
    public static void testG11001_ValidateAdminDashboadMyApprovalAndDecline() throws Exception {
        logMessage("*** Start Test G11001.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G38.13: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20646, 20646, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.13: Validate My Approval List via Admin");

        logMessage("Item G38.14: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20651, 20651, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.14: Validate Other Approval List via Admin ");

        logMessage("Item G38.15: Decline Charles L Middle name from Other Approval via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20321, 20321, testSerialNo, emailDomainName, driver), true, "Failed in Item G38.15: Decline Charles L Middle name from Other Approval via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G11001.");
        myAssert.assertAll();
    }

    @Test (priority = 11011)
    public void testG11011_ValidateEmailContentCharlesLDeclineMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11011");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G38.16.1: Validate Charles L email content for Charles L Middle Name Decline by Admin", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20806, 20806, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G38.16.1: Validate Charles L email content for Charles L Middle Name Decline by Admin");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G11011.");
        myAssert.assertAll();
    }


    ///////////////////////////////// G39 ////////////////////////////////////////

    @Test (priority = 11021)
    public void testG11021_AddRedirectApprovalsAndEditMaritalStatus() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Malley S.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G39.1: Validate  Jack L Teams and Roles via via Admin 3 Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_TeamsRolesDetailsScreen_Main (20391, 20391, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.1: Validate  Jack L Teams and Roles via via Admin 3 Malley S");

        logMessage("Item G39.2: Add Carmin C as Redirect Approval for Jack L via Admin 3 Malley S for today ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addRedirectApprovals_Main(20401, 20401, testSerialNo, emailDomainName, driver), true, "Faield in Item G39.2: Add Carmin C as Redirect Approval for Jack L via Admin 3 Malley S for today");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Phantom F.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10161, 10161, payrollDBName,  testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G39.3: Validate Phantom F Teams Role - Approval process via Phantom F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20411, 20411, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.3: Validate Phantom F Teams Role - Approval process via Phantom F");

        logMessage("Item G39.4: Edit Marital Status for Phantom F via Phantom F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20411, 20411, testSerialNo, driver), true, "Failed in Item G39.4: Edit Marital Status for Phantom F via Phantom F");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G11021.");
        myAssert.assertAll();

    }

    @Test (priority = 11031)
    public void testG11031_ValidatePhantomFEmailsAfterMaritalStatusChange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11031");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G39.5.1: Validate Phantom F email content for Phantom F Marital Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20881, 20881, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in G39.5.1: Validate Phantom F email content for Phantom F Marital Details Change");

        logMessage("Item G39.5.2: Validate Sue A email content for Phantom F Marital Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20882, 20882, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.5.2: Validate Sue A email content for Phantom F Marital Details Change");

        logMessage("Item G39.5.3: Validate Carmin C email content for Phantom F Marital Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20883, 20883, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.5.3: Validate Carmin C email content for Phantom F Marital Details Change");

        logMessage("Item G39.5.4: Validate Jack F email content for Phantom F Marital Details Change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20884, 20884, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.5.4: Validate Jack F email content for Phantom F Marital Details Change");

        logMessage("Item G39.5.5: Validate Jack F email content for Carmin C Redirect Approvals", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20885, 20885, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.5.5: Validate Jack F email content for Carmin C Redirect Approvals");

        logMessage("Item G39.5.6: Validate Carmin C email content for Carmin C Redirect Approvals", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20886, 20886, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.5.6: Validate Carmin C email content for Carmin C Redirect Approvals");


        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G11031.");
        myAssert.assertAll();
    }

    @Test (priority = 11041)
    public void testG11041_AddCharlesLMiddleNameAndValidateApprovalProcess () throws Exception {
        SystemLibrary.logMessage("*** Start Test G11041.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Charles L");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G39.6: Validate Charles L Teams Role - Approval process via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20421, 20421, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.6: Validate Charles L Teams Role - Approval process via Charles L");

        logMessage("Item G39.7: Add Charles L Middle name via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20421, 20421, testSerialNo, driver), true, "Failed in Item G39.7: Add Charles L Middle name via Charles L");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G11041");
        myAssert.assertAll();
    }

    @Test (priority = 11051)
    public void testG11051_ValidateEmailContentCharlesLAddedMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11051.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G39.8.1: Validate Charles L email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20891, 20891, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.8.1: Validate Charles L email content for Charles L Add Middle Name");

        logMessage("Item G39.8.2: Validate Carmin C email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20892, 20892, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.8.2: Validate Carmin C email content for Charles L Add Middle Name");

        logMessage("Item G39.8.3: Validate Malley S email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20893, 20893, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.8.3: Validate Malley S email content for Charles L Add Middle Name");

        logMessage("Item G39.8.4: Validate Jack F email content for Charles L Add Middle Name", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20894, 20894, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.8.4: Validate Jack F email content for Charles L Add Middle Name");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test 11051.");
        myAssert.assertAll();
    }

    @Test(priority = 11061)
    public static void testG11061_ValidateAdminOtherApprovalAndApproveAndDecline() throws Exception {
        logMessage("*** Start Test G11061.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G39.9: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20661, 20661, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.9: Validate My Approval List via Admin");

        logMessage("Item G39.10: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20666, 20666, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.10: Validate Other Approval List via Admin ");

        logMessage("Item G39.11: Decline Charles L Middle name from Other Approval via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20331, 20331, testSerialNo, emailDomainName, driver), true, "Failed in Item G39.11: Decline Charles L Middle name from Other Approval via Admin");

        logMessage("Item G39.12: Approve Phantom F Marital Status via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20336, 20336, testSerialNo, emailDomainName, driver), true, "Falied in Item G39.12: Approve Phantom F Marital Status via Admin");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G11061.");
        myAssert.assertAll();
    }

    @Test (priority = 11071)
    public void testG11071_ValidatePhantomFCharlesLEmailsAfterApproveDecline() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11071");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G39.13.1: Validate Charles L email content for Charles L Name Change declined", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20901, 20901, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.1: Validate Charles L email content for Charles L Name Change declined");

        logMessage("Item G39.13.2: Validate Carmin C email content for Charles L Name Change declined", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20902, 20902, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.2: Validate Carmin C email content for Charles L Name Change declined");

        logMessage("Item G39.13.3: Validate Malley S email content for Charles L Name Change declined", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20903, 20903, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.3: Validate Malley S email content for Charles L Name Change declined");

        logMessage("Item G39.13.4: Validate Jack F email content for Charles L Name Change declined", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20904, 20904, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.4: Validate Jack F email content for Charles L Name Change declined");

        logMessage("Item G39.13.5: Validate Sue A email content for Phantom F Marital Details Approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20905, 20905, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.5: Validate Sue A email content for Phantom F Marital Details Approved");

        logMessage("Item G39.13.6: Validate Carmin C email content for Phantom F Marital Details Approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20906, 20906, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.6: Validate Carmin C email content for Phantom F Marital Details Approved");

        logMessage("Item G39.13.7: Validate Jack F email content for Phantom F Marital Details Approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20907, 20907, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.7: Validate Jack F email content for Phantom F Marital Details Approved");

        logMessage("Item G39.13.8: Validate Phantom F email content for Phantom F Marital Details Approved", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20908, 20908, payrollDBName,  testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G39.13.8: Validate Phantom F email content for Phantom F Marital Details Approved");

        logMessage("Delete all email.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G11071.");
        myAssert.assertAll();
    }

    ///////////////////////////////// G40 ////////////////////////////////////////

    @Test (priority = 11081)
    public void testG11081_RemoveRedirectApprovalsAndAddMiddleName() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11081.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Jack F.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10091, 10091, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G40.1: Remove Carmin C as Redirect Approval Via Jack L ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeRedirectApprovals_Main (20426, 20426, testSerialNo, emailDomainName, driver), true, "Faield in Item G40.1: Remove Carmin C as Redirect Approval Via Jack L");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Charles L");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G40.2: Add Charles L Middle name via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20426, 20426, testSerialNo, driver), true, "Failed in Item G40.2: Add Charles L Middle name via Charles L");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Admin");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G40.3: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20671, 20671, testSerialNo, emailDomainName, driver), true, "Failed in Item G40.3: Validate Other Approval List via Admin ");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G11081.");
        myAssert.assertAll();

    }

    ///////////////////////////////// G41 ////////////////////////////////////////

    @Test (priority = 11091)
    public void testG11091_EditMaritalStatusCharlesLViaMalley() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11091.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Malley S.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G41.1: Edit Marital Status for Charles L via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20431, 20431, testSerialNo, driver), true, "Failed in Item G41.1: Edit Marital Status for Charles L via Malley S");

        logMessage("Item G41.2: Validate Charles L Personal Information page via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20436, 20436, testSerialNo, emailDomainName, driver), true, "Failed in Item G41.2: Validate Charles L Personal Information page via Malley S");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G11091.");
        myAssert.assertAll();
    }

    @Test (priority = 11092)
    public void testG11092_ValidateProfileChangesAuditReport() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11092.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Log on as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G41.2.X1: Valdiate Audit Report via Admin after Approver Edit employee Marital Status.", testSerialNo);
        myAssert.assertEquals(downloadMultiAuditReportViaDashboard(431, 431, emailDomainName, testSerialNo, driver), true, "failed in Item G41.2.X1: Valdiate Audit Report via Admin after Approver Edit employee Marital Status.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G11092.");
        myAssert.assertAll();

    }

    ///////////////////////////////// G42 ////////////////////////////////////////

    @Test (priority = 11101)
    public void testG11101_AddRobinSSubDViaMalleyS_Validate() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11101.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Log on as Malley S.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item G42.1: Add Robin S to SUB D and assign as Manager via Malley S", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(20326, 20326, testSerialNo, driver),  true, "Failed in Item G42.1: Add Robin S to SUB D and assign as Manager via Malley S ");

        signoutESS(driver);
        driver.close();

        logMessage("Logon as Charles L");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10326, 10326, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G42.2: Validate Charles L Teams Role - Approval process via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ApprovalProcess_ViaTeamsAndRolesPage_Main(20431, 20431, testSerialNo, emailDomainName, driver), true, "Failed in Item G42.2: Validate Charles L Teams Role - Approval process via Charles L");

        logMessage("Item G42.3: Add Charles L Medical Condition via Charles L", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20441, 20441, testSerialNo, driver), true, "Failed in Item G42.3: Add Charles L Medical Condition via Charles L");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test G11101.");
        myAssert.assertAll();

    }

    @Test (priority = 11111)
    public void testG11111_ValidateEmailsAfterTanyaDAddressAndMedicalchange() throws Exception {
        SystemLibrary.logMessage("*** Start Test G11111");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item G42.4.1: Validate Charles L email content for Charles L Medical Condition change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20911, 20911, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G42.4.1: Validate Charles L email content for Charles L Medical Condition change");

        logMessage("Item G42.4.2: Validate Malley S email content for Charles L Medical Condition change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20912, 20912, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G42.4.2: Validate Malley S email content for Charles L Medical Condition change");

        logMessage("Item G42.4.3: Validate Jack L email content for Charles L Medical Condition change", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20913, 20913, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item G42.4.3: Validate Jack L email content for Charles L Medical Condition change");

        logMessage("Delete all email.");
        // JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** End of Test G11111");
        myAssert.assertAll();
    }

    @Test(priority = 11121)
    public static void testG11121_ValidateAdminDashboadMyApprovalAndDecline() throws Exception {
        logMessage("*** Start Test G11121.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Admin");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item G42.5: Validate My Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20681, 20681, testSerialNo, emailDomainName, driver), true, "Failed in Item G42.5: Validate My Approval List via Admin");

        logMessage("Item G42.6: Validate Other Approval List via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20686, 20686, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G42.6: Validate Other Approval List via Admin ");

        signoutESS(driver);
        driver.close();

        logMessage("Log on as Malley S.");
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10290, 10290, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        logMessage("Item G42.7: Validate My Approval List via Malley S ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiMyApprovals_ViaAdmin(20691, 20691, testSerialNo, emailDomainName, driver), true, "Failed in Item G42.7: Validate My Approval List via Malley S ");

        logMessage("Item G42.8: Validate Other Approval List via Malley S ", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateMultiOtherApprovals_ViaAdmin(20696, 20696, testSerialNo, emailDomainName, driver), true, "Failed in Item Item G42.8: Validate Other Approval List via Malley S ");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of test G11121.");
        myAssert.assertAll();
    }


///////////////////////////////// G43 ////////////////////////////////////////

    @Test(priority = 11131)
    public static void testG11131_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 11131.");

        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G43.1: Validate Employee Robert S Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20141, 20141, emailDomainName, testSerialNo), true, "Failed in Item G43.1: Validate Employee Robert S Detail Report Before implenting HR");

        logMessage("Item G43.2: Validate Employee Charles L Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20142, 20142, emailDomainName, testSerialNo), true, "Failed in Item G43.2: Validate Employee Charles L Detail Report Before implenting HR");

        logMessage("Item G43.3: Validate Employee Tanya D Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20143, 20143, emailDomainName, testSerialNo), true, "Failed in Item G43.3: Validate Employee Tanya D Detail Report Before implenting HR");

        logMessage("Item G43.4: Validate Employee Robin S Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20144, 20144, emailDomainName, testSerialNo), true, "Failed in Item G43.4: Validate Employee Robin S Detail Report Before implenting HR");

        logMessage("Item G43.5: Validate Employee Phantom F Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20145, 20145, emailDomainName, testSerialNo), true, "Failed in Item G43.5: Validate Employee Phantom F Detail Report Before implenting HR");

        logMessage("Item G43.6: Validate Employee Stanley B Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20146, 20146, emailDomainName, testSerialNo), true, "Failed in Item G43.6: Validate Employee Stanley B Detail Report Before implenting HR");

        logMessage("Item G43.7: Validate Employee Sharon A Detail Report Before implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20147, 20147, emailDomainName, testSerialNo), true, "Failed in Item G43.7: Validate Employee Sharon A Detail Report Before implenting HR");

        exitMeridian();
        logMessage("End of Test G11131.");
        myAssert.assertAll();

    }

    @Test(priority = 11132)
    public static void testG11132_ValidateEHRReportAndImplementEHRViaMicropay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 11132.");
        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G43.8.X1: Validate eHR Report before Implementing eHR.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EHRReport_Main(20001, 20001, emailDomainName, testSerialNo), true, "Failed in Item G43.8.X1: Validate eHR Report before Implementing eHR");
        exitMeridian();

        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G43.8: Implement eHR via Sage Micropay.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item G43.8: Implement eHR via Sage Micropay.");
        close_ImplementHRScreen();

        exitMeridian();
        logMessage("End of Test G11132.");
        myAssert.assertAll();
    }


    @Test(priority = 11141)
    public static void testG11141_ValidateEmployeeDetailReportViaMicropay() throws Exception {
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Start Test 11141.");

        logMessage("logon Meridian");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item G43.9: Validate Employee Robert S Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20151, 20151, emailDomainName, testSerialNo), true, "Failed in Item G43.9: Validate Employee Robert S Detail Report After implenting HR");

        logMessage("Item G43.10: Validate Employee Charles L Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20152, 20152, emailDomainName, testSerialNo), true, "Failed in Item G43.10: Validate Employee Charles L Detail Report After implenting HR");

        logMessage("Item G43.11: Validate Employee Tanya D Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20153, 20153, emailDomainName, testSerialNo), true, "Failed in Item G43.11: Validate Employee Tanya D Detail Report After implenting HR");

        logMessage("Item G43.12: Validate Employee Robin S Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20154, 20154, emailDomainName, testSerialNo), true, "Failed in Item G43.12: Validate Employee Robin S Detail Report After implenting HR");

        logMessage("Item G43.13: Validate Employee Phantom F Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20155, 20155, emailDomainName, testSerialNo), true, "Failed in Item G43.13: Validate Employee Phantom F Detail Report After implenting HR");

        logMessage("Item G43.14: Validate Employee Stanley B Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20156, 20156, emailDomainName, testSerialNo), true, "Failed in Item G43.14: Validate Employee Stanley B Detail Report After implenting HR");

        logMessage("Item G43.15: Validate Employee Sharon A Detail Report After implenting HR", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.print_EmployeeDetailsReport_Main(20157, 20157, emailDomainName, testSerialNo), true, "Failed in Item G43.15: Validate Employee Sharon A Detail Report After implenting HR");

        exitMeridian();
        logMessage("End of Test G11141.");
        myAssert.assertAll();
    }


    ///////////////////////
    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item GGG", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module G - WorkFLow II Function test.");
    }

    /////////////////// Debug here ///////////////
}
