package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.logMessage;
import static Lib.SystemLibrary.serverName;

public class ESSRegTestL_WorkFlow_Leave {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=111;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName="L";

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
    public void testL10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start L10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item L1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item L1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item L1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item L1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item L1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item L1.6", "pass", testSerialNo);

        SystemLibrary.logMessage("*** End of Item L10011: Scenario Preparation");
        myAssert.assertAll();
    }


    //////////////// Temp Disable What's new funciton test by Jim on 02/08/2021 .//////////////////
    /*
    @Test(priority = 10012)
    public void testL10012_TestWhatsNew() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start L10012: What's New function.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item L1.6X.1: Validate Whats New Pop up message when logon ESS the fisrt time as Super  Admin User.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain_WithWhatNEW(20101, 20101, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item L1.6X.1: Validate Whats New Pop up message when logon ESS the first time as Super Admin User.");

        signoutESS(driver);
        driver.close();

        ///////////////////////////////////
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item L1.6X.2: Validate Whats New Pop up message again via Admin and set not show in next logon.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain_WithWhatNEW(20102, 20102, payrollDBName, testSerialNo, emailDomainName, driver), false, "Failed in Item L1.6X.2: Validate Whats New Pop up message again via Admin and set not show in next logon.");

        signoutESS(driver);
        driver.close();

        //////////////////////////////////////
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logMessage("Item L1.6X.3: Validate if Whats New Pop up message disappears via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain_WithWhatNEW(20103, 20103, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item L1.6X.3: Validate if Whats New Pop up message disappears via Admin.");

        signoutESS(driver);
        driver.close();


        SystemLibrary.logMessage("*** End of Item L10012.");
        myAssert.assertAll();
    }
    */

	//////
	
	@Test(priority = 10013)
    public void testL10013_AddAdminContactDetails() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start L10013: What's New function.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item L1.7: Setup Admin user contact details", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item L1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Item L10013.");
        myAssert.assertAll();	
    }

    @Test(priority = 10021)
    public void testL10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test L10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item L1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item L1.8: Add API configuration.");

        logMessage("Item L1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item L1.9: Sync All.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10021.");
        myAssert.assertAll();
    }

    @Test(priority = 10031) //a
    public void testL10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item L2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item L2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item L2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item L2.2: Validate Team Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10031.");
        myAssert.assertAll();
    }


    @Test (priority=10041)
    public static void testL10041_ImportTeamViaTPU() throws Exception {
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        logMessage("*** Start Test L10041: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();

        logMessage("Item L3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"System_Test_Team_Import.csv"), true, "Failed in Item L3: Import Team via TPU.");

        logMessage("*** End of test L10041");
        myAssert.assertAll();
    }

  

    @Test(priority = 10051) //Must run without headless
    public void testL10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test L10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item L4: Validate Team after import team.", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item L4: Validate Team after import team.");

        logMessage("Item L5: Validate Team Report via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiTeamsReportViaAdmin(30001, 30001, testSerialNo, emailDomainName, driver), true, "Failed in Item L5: Validate Team Report via Admin.");

        logMessage("Item L5.X1: Validate initial Profile Change Audit report.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30021, 30021, emailDomainName, testSerialNo, driver), true, "Failed in Item L5.X1: Validate initial Profile Change Audit report.");

        logMessage("Item L5.X2: Validate initial Leave Application Audit report.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30022, 30022, emailDomainName, testSerialNo, driver), true, "Failed in Item L5.X2: Validate initial Leave Application Audit report.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10051.");
        myAssert.assertAll();
    }

    @Test(priority = 10061) //Must run without headless
    public void testL10061_ActivateUsers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test L10061.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L6.1: Activate Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(104, 104, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L6.1: Activate Sue A");

        logMessage("Item L6.2: Activate Phantom F", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(105, 105, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L6.2: Activate Phantom F");

        logMessage("Item L6.3: Activate Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(112, 112, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L6.3: Activate Sharon A.");

        logMessage("Item L6.4: Activate Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L6.4: Activate Jennifer H.");

        logMessage("Item L6.5: Activate Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(106, 106, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L6.5: Activate Christine R.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10061.");
        myAssert.assertAll();
    }

    @Test(priority = 10071)
    public void testL10071_AddMedicalConditionAndEmergencyContactForSharonAViaSharonA() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10071.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon ESS as Sharon A.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10211, 10211, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L7.1: Add Sharon A Medical Conditions via Sharon A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20471, 20471, testSerialNo, driver), true, "Failed in Item L7.1: Add Sue A Medical Conditions via Sharon A");

        logMessage("Item L7.2: Add Sharon A Emgergeny Contact 1 via Sharon A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20241, 20241, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item L7.2: Add Sharon A Emgergeny Contact 1 vis Sharon A.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10071.");
        myAssert.assertAll();
    }

    @Test(priority = 10081)
    public void testL10081_AddMedicalConditionAndEmergencyContactForSueAViaSueA() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10071.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon ESS as Sue A.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L7.3: Add Sue A Medical Conditions via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20481, 20481, testSerialNo, driver), true, "Failed in Item L7.3: Add Sue A Medical Conditions via Sue A");

        logMessage("Item L7.4: Add Sue A Emgergeny Contact 1 via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20251, 20251, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item L7.4: Add Sue A Emgergeny Contact 1 vis Sharon A.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10081.");
        myAssert.assertAll();
    }

    @Test(priority = 10091)
    public void testL10091_ApproveAllChangesViaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10091.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L7.5: Approve All Other Approval via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20351, 20351, testSerialNo, emailDomainName, driver), true, "Failed in Item L7.5: Approve All Other Approval via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10091.");
        myAssert.assertAll();
    }

    //////////// Test Apply Leave When Payroll is Offline //////////////////
    @Test(priority = 10101)
    public void testL10101_StopMCS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10101.");
        logMessage("Item L8.1: Stop MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.stopMCS(), true, "Failed in Item L8.1: Stop MCS.");

        SystemLibrary.logMessage("*** End of Test L10101.");
        myAssert.assertAll();
    }

    @Test(priority = 10111)
    public void testL10111_JenniferHApplyAL() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10111.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L8.2: Validate Jennifer H Leave Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20351, 20351, testSerialNo, emailDomainName, driver), true, "Failed in Item L8.2: Validate Jennifer H Leave Page via Jennifer H.");

        logMessage("Item L8.3: Jennifer H Apply AL", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20361, 20361, testSerialNo, driver), true, "Failed in Item L8.3: Jennifer H Apply AL");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10111.");
        myAssert.assertAll();
    }

    @Test(priority = 10121)
    public void testL10121_ApproveJenniferHALViaSueA() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10121.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon ESS as Sue A.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L8.4: Validate Jennifer H Leave Page via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20371, 20371, testSerialNo, emailDomainName, driver), true, "Failed in Item L8.4: Validate Jennifer H Leave Page via Sue A.");

        logMessage("Item L8.5: Approve Jennifer H AL via Sue A.", testSerialNo);
        myAssert.assertEquals(approveMultiItem(20361, 20361, testSerialNo, emailDomainName, driver), true, "Falied in Item L8.5: Approve Jennifer H AL via Sue A.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10121.");
        myAssert.assertAll();
    }

    @Test(priority = 10131)
    public void testL10131_ValdiateJenniferHLeavePageViaJenniferH() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10131.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L8.6: Validate Jennifer H Leave Page via Jennifer H after AL approved.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20381, 20381, testSerialNo, emailDomainName, driver), true, "Failed in Item L8.6: Validate Jennifer H Leave Page via Jennifer H after AL approved.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10131.");
        myAssert.assertAll();
    }

    @Test(priority = 10141)
    public void testL10141_StartMCS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10141.");
        logMessage("Item L8.7: Start MCS.", testSerialNo);
        myAssert.assertEquals(MCSLib.startMCS(), true, "Failed in Item L8.7: Start MCS.");

        SystemLibrary.logMessage("*** End of Test L10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public void testL10151_ValdiateJenniferHLeavePageViaJenniferH() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10151.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L8.8: Validate Jennifer H Leave Page via Jennifer H after AL approved and MCS Started.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main(20391, 20391, testSerialNo, emailDomainName, driver), true, "Failed in Item L8.8: Validate Jennifer H Leave Page via Jennifer H after AL approved and MCS Started.");

        logMessage("Item L8.9: Validate Jennifer H AL balance in leave page via Jennifer H after MCS Started.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaLeavePage_Main(20171, 20171, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L8.9: Validate Jennifer H AL balance in leave page via Jennifer H after MCS Started.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10151.");
        myAssert.assertAll();
    }

    ///////////////// Donwload Pay Advice ////////////////
    @Test(priority = 10161) //Must run without headless
    public void testL10161_DownloadJenniferHPayAdviceInDashboardViaJenniferH() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10161.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L9.01: Download Jennifer H Pay Advice from Dashboard via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiPayAdviceReport_ViaDashboard(30011, 30011, driver), true, "Failed in Item L9.01: Download Jennifer H Pay Advice from Dashboard via Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10161.");
        myAssert.assertAll();
    }

    @Test(priority = 10171)
    public void testL10171_DownloadJenniferHPayAdviceInPayAdvicePageViaJenniferH() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10171.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L9.02: Validate Jennifer H Pay Advice Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item L9.02: Validate Jennifer H Pay Advice Page via Jennifer H.");

        logMessage("Item L9.03: Validate Jennifer H Payment Summary Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePageAdvicePaySummaryPage_Main(20012, 20012, testSerialNo, emailDomainName, driver), true, "Failed in Item L9.03: Validate Jennifer H Payment Summary Page via Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10171.");
        myAssert.assertAll();
    }

    @Test(priority = 10181) //Must run without headless
    public void testL10181_DownloadJenniferHPayAdviceAndPaymentSummaryInPayAdvicePageViaJenniferH() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L10181.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon ESS as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L9.04: Download Pay Advice EOP 31/01/2020 in Pay Advice & Payment Summary Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiPayAdviceReport(20021, 20021, testSerialNo, driver), true, "Failed in Item L9.04: Download Pay Advice with EOP 31/01/202020 in Pay Advice & Payment Summary Page via Jennifer H.");

        logMessage("Item L9.05: Download Pay Advice with EOP 31/01/2017 in Pay Advice & Payment Summary Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiPayAdviceReport(20022, 20022, testSerialNo, driver), true, "Failed in Item L9.05: Download Pay Advice with EOP 31/01/2017 in Pay Advice & Payment Summary Page via Jennifer H.");

        logMessage("Item L9.06: Download Payment Summary Tax Year 2016 in Pay Advice & Payment Summary Page via Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiPaymentSummaryReport(20031, 20031, testSerialNo, driver), true, "Failed in Item L9.06: Download Payment Summary Tax Year 2016 in Pay Advice & Payment Summary Page via Jennifer H..");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10181.");
        myAssert.assertAll();
    }

    ////////////////// Add Non Payroll User /////////////////////
    @Test(priority = 10191)
    public void testL10191_AddNonPayrollUser() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10191.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L10.01: Set Role and Permission - New Starter - Edit for Administrator.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(20211, 20211, driver), true, "Failed in Item L10.01: Set Role and Permission - New Starter - Edit for Administrator.");

        logMessage("Item L10.02: Add Non Payroll User John1 SMITH1 via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiNonPayrollUser(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item L10.02: Add Non Payroll User via Admin.");

        logMessage("Item L10.03: Validate Non Payroll User John1 SMITH1 Personal Information page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20511, 20511, testSerialNo, emailDomainName, driver), true, "Failed in Item L10.03: Validate Non Payroll User John1 SMITH1 Personal Information page via Admin.");

        logMessage("Item L10.04: Validate Non Payroll User John1 SMITH1 Contact Details page via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item L10.04: Validate Non Payroll User John1 SMITH1 Contact Details page via Admin.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10191.");
        myAssert.assertAll();
    }

    @Test(priority = 10201)
    public void testL10201_ActivateNonPayrollUserJohn1SMITH1() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10201.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L10.05: Activate Non Payroll User John1 SMITH1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(20161, 20161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L10.05: Activate Non Payroll User John1 SMITH1.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10201.");
        myAssert.assertAll();
    }

    @Test(priority = 10211)
    public void testL10211_LogonESSAsJohn1SMITH1() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10211.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Item L10.06: Log on as John1 SMITH1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20113, 20113, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item L10.06: log on as John1 SMITH1.");

        logMessage("Item L10.07: Validate Dashboard via John1 SMITH1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateDashBoard_Main(20621, 20621, testSerialNo, emailDomainName, driver), true, "Failed in Item L10.07: Validate Dashboard via John1 SMITH1.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10211.");
        myAssert.assertAll();
    }

    @Test(priority = 10221)
    public void testL10221_AddNonPayrollUserJohn1SMITH1AsManagerInTeamC() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10221.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L10.08: Addd Non Payroll User John1 SMITH1 as extra manager of Team C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(30581, 30581, testSerialNo, driver), true, "Failed in Item L10.08: Addd Non Payroll User John1 SMITH1 as the extra Manager of Team C.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10221.");
        myAssert.assertAll();
    }

    @Test(priority = 10231)
    public void testL10231_ValidateTeamCViaJohn1S() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10231.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Log on as John1 SMITH1.");
        GeneralBasicHigh.logonESSMain(20113, 20113, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L10.09: Validate Team C via Non Payroll User John1 SMITH1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(30582, 30582, testSerialNo, emailDomainName, driver), true, "Failed in Item L10.09: Validate Team C via Non Payroll User John1 SMITH1.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10231.");
        myAssert.assertAll();
    }

    @Test(priority = 10241)
    public void testL10241_John1SMITH1ApplySickLeaveForJenniferH() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** Start Test L10241.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        logMessage("Log on as John1 SMITH1.");
        GeneralBasicHigh.logonESSMain(20113, 20113, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L10.10: John1 SMITH1 apply Sick Leave For Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20401, 20401, testSerialNo, driver), true, "Failed in Item L10.10: John1 SMITH1 apply Sick Leave For Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10241.");
        myAssert.assertAll();
    }

    @Test(priority = 10251)
    public void testL10251_ValidateJenniferHEmail() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item L10.11: Validate Jannifer H email after Non Payroll User John1 SMITH1 appplied sick leave.", testSerialNo);
        myAssert.assertEquals(JavaMailLib.validateMultiEmailContent(20991, 20991, payrollDBName, testSerialNo, emailDomainName, url_ESS), true, "Failed in Item L10.11: Validate Jannifer H email after Non Payroll User John1 SMITH1 appplied sick leave.");

        SystemLibrary.logMessage("*** End of Test L10251.");
        myAssert.assertAll();
    }


    @Test(priority = 10261) //Must run without headless
    public void testL10261_ValidateAuditReportAfterLeaveAndProfileChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test L10261.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.1: Validate Profile Change Audit report.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30031, 30031, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.1: Validate initial Profile Change Audit report.");

        logMessage("Item L11.2: Validate Leave Application Audit report.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30032, 30032, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.2: Validate initial Leave Application Audit report.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10261.");
        myAssert.assertAll();
    }

    @Test(priority = 10271)
    public void testL10271_ChangeWorkFlowToDoNothing() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10261.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.3.1: Edit Leave Work flow to Do Nothing.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20231, 20231, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.3.1: Edit Leave Work flow to Do Nothing.");

        logMessage("Item L11.3.2: Edit Profile Changes Work flow to Do Nothing.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20232, 20232, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.3.2: Edit Profile Changes Work flow to Do Nothing.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10271.");
        myAssert.assertAll();
    }

    @Test(priority = 10281)
    public void testL10281_ChristineRApplyALAndAddMobilePhone() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10281.");

        logMessage("Log on as Christine R.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10061, 10061, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.4.1: Christine R apply a AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave (20411, 20411, testSerialNo, driver), true, "Failed in Item Item L11.4.1: Christine R apply a AL.");

        logMessage("Item L11.4.2: Christine R Add mobile phone.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20291, 20291, payrollDBName, testSerialNo, emailDomainName, driver), true, "Item L11.4.2: Christine R Add mobile phone.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10281.");
        myAssert.assertAll();
    }

    @Test(priority = 10291) //Must run without headless
    public void testL10291_ValidateLeaveAuditReportAfterChristineRApplyAL() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test L10291.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.5: Validate Leave Application Audit report after Christine R Apply AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30041, 30041, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.5: Validate Leave Application Audit report after Christine R Apply AL.");

        logMessage("Item L11.5.X1: Validate Profile Change Audit Report after Christine R Add Mobile Phone.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30042, 30042, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.5.X1: Validate Profile Change Audit Report after Christine R Add Mobile Phone.");

        logMessage("Item L11.6: Validate Leave History Report after Christine R Apply AL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(30051, 30051, testSerialNo, driver), true, "Failed in Item L11.6: Validate Leave History Report after Christine R Apply AL.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10291.");
        myAssert.assertAll();
    }

    @Test(priority = 10301)
    public void testL10301_ChangeWorkflowToSendANotification() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10291.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.7.1: Edit Leave Work flow to Send a Notification.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20233, 20233, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.7.1: Edit Leave Work flow to Send a Notification.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        logMessage("Item L11.7.2: Edit Profile Change Work flow to Send a Notification.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20234, 20234, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.7.2: Edit Profile Change Work flow to Send a Notification.");

      /*  logMessage("Item L11.7.1: Restore Default Leave Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20241, 20241, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.7.1: Restore Default Leave Work flow.");

        logMessage("Item L11.7.2: Restore Default Leave Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20242, 20242, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.7.2: Restore Default Leave Work flow.");
*/
        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10301.");
        myAssert.assertAll();
    }

    @Test(priority = 10311)
    public void testL10311_ChristineRApplySLAndEditMobilePhone() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10311.");

        logMessage("Log on as Christine R.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10061, 10061, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.8.1: Christine R apply a SL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave (20412, 20412, testSerialNo, driver), true, "Failed in Item L11.8.1: Christine R apply a SL.");

        logMessage("Item L11.8.2: Christine R Edit mobile phone.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(20292, 20292, payrollDBName, testSerialNo, emailDomainName, driver), true, "Item L11.8.2: Christine R Edit mobile phone.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10311.");
        myAssert.assertAll();
    }

    @Test(priority = 10321) //Must run without headless
    public void testL10321_ValidateLeaveAuditReportAfterChristineRApplyAL() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test L10321.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.9.1: Validate Leave Application Audit report after Christine R Apply SL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30043, 30043, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.9.1: Validate Leave Application Audit report after Christine R Apply SL.");

        logMessage("Item L11.9.2: Validate Profile Change Audit Report after Christine R Edit Mobile Phone.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30044, 30044, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.9.2: Validate Profile Change Audit Report after Christine R Edit Mobile Phone.");

        logMessage("Item L11.9.3: Validate Leave History Report after Christine R Apply SL.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiLeaveHistoryReport(30052, 30052, testSerialNo, driver), true, "Failed in Item L11.9.3: Validate Leave History Report after Christine R Apply SL.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10321.");
        myAssert.assertAll();
    }

    @Test(priority = 10331)
    public void testL10331_RestoreDefaultWorkflow() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10331.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L11.10.1: Restore Default Leave Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20241, 20241, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.10.1: Restore Default Leave Work flow.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        logMessage("Item L11.10.2: Restore Default Profile Changes Work flow.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiWorkflow (20242, 20242, emailDomainName, testSerialNo, driver), true, "Failed in Item L11.10.2: Restore Default Profile Changes Work flow.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10331.");
        myAssert.assertAll();
    }

    @Test(priority = 10341)
    public void testL10341_ValidateLSLDefaultSetting() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10341.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L12.1: Validate the default LSL Setting - Include Pro rata calculation checked and Only Once Entitlement is Reached is unchecked.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveSettingInLeaveSettingPage_Main (10021, 10021, emailDomainName, testSerialNo, driver), true, "Failed in Item L12.1: Validate the default LSL Setting - Include Pro rata calculation is checked and Only Once Entitlement is Reached is unchecked.");

        logMessage("Item L12.2: Validate Mitchell S LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20181, 20181, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.2: Validate Mitchell S LSL Balance via Leave Page");

        logMessage("Item L12.2X: Validate Mitchell S LSL Balance via Apply Leave Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20182, 20182, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.2X: Validate Mitchell S LSL Balance via Apply Leave Form");

        logMessage("Item L12.3: Validate Christine R LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20191, 20191, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.3: Validate Christine R LSL Balance via Leave Page");

        logMessage("Item L12.3X: Validate Christine R LSL Balance via Apply Leave Form.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20192, 20192, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.3X: Validate Christine R LSL Balance via Apply Leave Form.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10341.");
        myAssert.assertAll();
    }

    @Test(priority = 10351)
    public void testL10351_EditAndValidateLSLSetting_I() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10351.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L12.4: Edit LSL Setting - Check - Only Once entitlement is reached", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main (10031, 10031, driver), true, "Failed in Item L12.4: Edit LSL Setting - Check - Only Once entitlement is reached.");

        logMessage("Item L12.5: Validate Mitchell S LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20201, 20201, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.5: Validate Mitchell S LSL Balance via Leave Page");

        logMessage("Item L12.5X: Validate Mitchell S LSL Balance via Apply Leave Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20202, 20202, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.5X: Validate Mitchell S LSL Balance via Apply Leave Form");

        logMessage("Item L12.6: Validate Christine R LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20211, 20211, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.6: Validate Christine R LSL Balance via Leave Page");

        logMessage("Item L12.6X: Validate Christine R LSL Balance via Apply Leave Form.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20212, 20212, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.6X: Validate Christine R LSL Balance via Apply Leave Form.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10351.");
        myAssert.assertAll();
    }

    @Test(priority = 10361)
    public void testL10361_EditAndValidateLSLSetting_II() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10361.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L12.7: Edit LSL Setting - Uncheck Include Pro Rata Calculation.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main (10041, 10041, driver), true, "Failed in Item L12.7: Edit LSL Setting - Uncheck Include Pro Rata Calculation.");

        logMessage("Item L12.8: Validate Mitchell S LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20221, 20221, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.8: Validate Mitchell S LSL Balance via Leave Page");

        logMessage("Item L12.8X: Validate Mitchell S LSL Balance via Apply Leave Form", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeaveBalance_ViaApplyLeaveDialogue_Main(20222, 20222, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.8X: Validate Mitchell S LSL Balance via Apply Leave Form");

        logMessage("Item L12.9: Validate Christine R LSL Balance via Leave Page", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20231, 20231, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.9: Validate Christine R LSL Balance via Leave Page");

        logMessage("Item L12.9X: Validate Christine R LSL Balance via Apply Leave Form.", testSerialNo);
        myAssert.assertEquals(validateLeaveBalance_ViaLeavePage_Main(20232, 20232, testSerialNo, serverName, payrollDBName, driver), true, "Failed in Item L12.9X: Validate Christine R LSL Balance via Apply Leave Form.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10361.");
        myAssert.assertAll();
    }

    @Test(priority = 10371)
    public void testL10371_RestoreDefaultLSLSetting() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test L10371.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L12.10: Restore the defaut LSL Setting - Include Pro rata calculation is checked and Only Once Entitlement is Reached is unchecked.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editSettingsLeave_Main (10041, 10041, driver), true, "Failed in Item L12.10: Restore the defaut LSL Setting - Include Pro rata calculation is checked and Only Once Entitlement is Reached is unchecked.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L10361.");
        myAssert.assertAll();
    }

    /////////////////////// Add New Test before this script /////////////////////////
    @Test(priority = 19001) //Must run without headless
    public void testL19001_ValidateMedicalConditionAndEmergencyContactViaSueA() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start Test L19001.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon ESS as Sue A.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10241, 10241, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Item L19001.1: Validate Shaorn A Medical Conditions via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20491, 20491, testSerialNo, emailDomainName, driver), true, "Failed in Item L19001.1: Validate Shaorn A Medical Conditions via Sue A.");

        logMessage("Item L19001.2: Validate Sue A Medical Conditions via Sue A", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validatePersonalInformation_Main(20501, 20501, testSerialNo, emailDomainName, driver), true, "Failed in Item L19001.2: Validate Sue A Medical Conditions via Sue A.");

        logMessage("Item L19001.3: Validate Sharon A Emgergeny Contact 1 via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20261, 20261, testSerialNo, emailDomainName, driver), true, "Failed in Item L19001.3: Validate Sharon A Emgergeny Contact 1 via Sue A.");

        logMessage("Item L19001.4: Validate Sue A Emgergeny Contact 1 via Sue A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validate_ContactDetails_Main(20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item L19001.4: Validate Sue A Emgergeny Contact 1 via Sue A.");


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test L19001.");
        myAssert.assertAll();
    }



    ///////////////////////////



    @Test (priority=20002)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item LLL", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module L - Work Flow - Leave test.");
    }
    //////////////////////// Debug here ///////////////////





}
