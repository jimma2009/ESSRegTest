package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.AutoITLibHigh.editMultiEmployee_Leave;
import static Lib.GeneralBasic.*;
import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.driverType;
import static Lib.SystemLibrary.logMessage;

public class ESSRegTestI_Sync {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static String payrollDB2Name;
    private static String comDB2Name;
    private static int moduleNo = 109;
    private static String payrollDBOrderNumber;
    private static String payrollDB2OrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static String moduleName ="I";

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
    public void testI10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item I10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(moduleNo, moduleNo);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item I1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item I1.4", "pass", null, testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item I1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1")) && (comDBName.equals("ESS_Auto_COM1"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll2")) && (comDBName.equals("ESS_Auto_COM2"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll3")) && (comDBName.equals("ESS_Auto_COM3"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item I1.5", "pass", null, testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item I1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1")) && (comDBName.equals("ESS_Auto_COM1"))) {
            DBManage.sqlExecutor_Main(1011, 1011);
        } else if ((payrollDBName.equals("ESS_Auto_Payroll2")) && (comDBName.equals("ESS_Auto_COM2"))) {
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3")) && (comDBName.equals("ESS_Auto_COM3"))) {
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item I1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item I1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item I1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item I1.7: Setup Admin user contact details.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item I10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test (priority = 10012)
    public static void testI10012_ValidateAPI_MicrOpay() throws Exception {
        logMessage("*** Start Test I10012 - Validate Web API.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver2=launchSageMicrOpayWebAPI(driverType);

        inputSageMicrOpayWebAPIAuthenticationDetails(testSerialNo, driver2);

        logMessage("Item I1.8.X1: Validate API - Employee Details - Include Terminated", testSerialNo);
        myAssert.assertEquals (validateAPI_MicrOpay_EmployeeDetails_Terminated_Main(20011, 20011, testSerialNo, emailDomainName, driver2), true, "Failed in Item I1.8.X1: Validate API - Employee Details - Include Terminated");

        logMessage("Item I1.8.X2: Validate API - Lookups - costaccount", testSerialNo);
        myAssert.assertEquals (validateAPI_Lookups_Main(20012, 20012, testSerialNo, emailDomainName, driver2), true, "Failed in Item I1.8.X2: Validate API - Lookups - costaccount");

        driver2.close();
        logMessage(("*** End of Test I10012."));
        myAssert.assertAll();
    }


    @Test(priority = 10021)
    public void testI10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test I10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item I1.8: Add API configuration", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item I1.8: Add API configuration.");

        logMessage("Item I1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Failed in Item I1.9: Sync All the 1st time.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test I10021.");
        myAssert.assertAll();
    }

    ////////////////////// Disabled by Jim on 06/09/2021 //////////////////////
  /*  @Test(priority = 10022)
    public void testI10022_SyncAllAndSyncChangesSimultaneously1() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test I10022.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Item I1.10.X1: Sync All while Sycn Changes Simultaneously.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllDataSimultaneously_Main(103, 103, driver), false, "Failed in Item I1.10.X1: Sync All while Sycn Changes Simultaneously.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test I10022.");
        myAssert.assertAll();
    }*/

    /////////// Disabled by Jim on 06/09/2021 //////////////////
  /*  @Test(priority = 10023)
    public void testI10023_SyncChangesAndSyncAllSimultaneously2() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test I10023.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        WebDriver driver2=GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon as Admin in driver 1");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        logMessage("Logon as Admin in driver 2");
        driver2.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver2); //Log on ESS as Super User

        //////////////// Change expected result to false on 16/08/2021 //////////////
        logMessage("Item I1.10.X2: Sync Changes while Sycn All Simultaneously.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChangesSimultaneously_Main(10091, 10091, driver, driver2), false, "Failed in Item I1.10.X2: Sync Changes while Sycn All Simultaneously.");

        signoutESS(driver);
        driver.close();

        signoutESS(driver2);
        driver2.close();
        SystemLibrary.logMessage("*** End of Test I10023.");
        myAssert.assertAll();
    }*/
    //////

    @Test(priority = 10031) //a
    public void testI10031_ValidateTeamsInitialStatus() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item I2.1: Validate Team Initial Status - Unassigned member count.");

        SystemLibrary.logMessage("Item I2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item I2.2: Validate Tem Initial Status - Unassigned Team member.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test I10031.");
        myAssert.assertAll();
    }


    @Test(priority = 10041)
    public static void testI10041_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test I10041: Import Team via TPU.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);


        logMessage("Item I3", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath + "System_Test_Team_Import.csv"), true, "Failed in Item I3: Import Team via TPU.");

        logMessage("*** End of Test I10041");
        myAssert.assertAll();
    }



    @Test(priority = 10051)
    public void testI10051_ValidateTeamsAfterImportTeam() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I10051.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I4", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item I4: Validate Team after import team.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test I10051.");
        myAssert.assertAll();
    }

/////////////////// Start Sync changes test for Super ///////////////////

    @Test(priority = 10061)
    public void testI10061_ConfigESSBeforeTestSyncChangesInSuper() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10061.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.1: Configure Admin Roles - Finacial - Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main (20181, 20181, driver), true, "Failed in Item I5.1: Configure Admin Roles - Finacial - Edit.");


        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10061.");
        myAssert.assertAll();
    }

    @Test(priority = 10071)
    public static void testI10071_ValidateJenniferHSuperPageBeforeTestSyncChangesInSuper() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10071.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.2: Validate Jennifer H Super Page Initial Status.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main (20011, 20011, testSerialNo, emailDomainName, driver), true, "Failed in Item I5.2: Validate Jennifer H Super Page Initial Status.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10071.");
        myAssert.assertAll();
    }

    @Test(priority = 10081)
    public static void testI10081_AddThe2ndSuperForJenniferHSuperInMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10081.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I5.3: Add Super for Jennifer H in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_Super(10001, 10001, payrollDBName), true, "Failed in Item I5.3: Add Super for Jennifer Hin MicrOpay");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10081.");
        myAssert.assertAll();
    }

    @Test(priority = 10091)
    public static void testI10091_SyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10091.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.4: Sync Changes after Adding new Super for Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I5.4: Sync Changes after Adding new Super for Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10091.");
        myAssert.assertAll();
    }

    @Test(priority = 10101)
    public static void testI10101_ValidateJenniferHSuperPageBeforeTestSyncChangesInSuper() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10101.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I5.5: Validate Jennifer H Super Page After Adding new Super and Sync Changes..", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main (20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item I5.5: Validate Jennifer H Super Page After Adding new Super and Sync Changes..");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10101.");
        myAssert.assertAll();
    }

    @Test(priority = 10111)
    public static void testI10111_DeleteJenniferHAllSuperInMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10111.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I5.6: Delete Super Fund for Jennifer H in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_Super(10011, 10011, payrollDBName), true, "Failed in Item I5.6: Delete Super Fund for Jennifer H in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10111.");
        myAssert.assertAll();
    }

    @Test(priority = 10121)
    public static void testI10121_SyncChangesAfterDeleteJenniferHThe2ndSuper() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10121.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.7: Sync Changes after deleting the new Super for Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I5.7: Sync Changes after deleting the new Super for Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10121.");
        myAssert.assertAll();
    }

    @Test(priority = 10131)
    public static void testI10131_ValidateJenniferHSuperPageAfterDelete2ndSuperAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10131.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I5.8: Validate Jennifer H Super Page After Delete the 2nd Super And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main (20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item I5.8: Validate Jennifer H Super Page After Delete the 2nd Super And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10131.");
        myAssert.assertAll();
    }

    @Test (priority = 10141)
    public static void testI10141_EditJenniferHSuperViaSageMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10141.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I5.9: Edit Super Fund for Jennifer H in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_Super(10021, 10021, payrollDBName), true, "Failed in Item I5.9: Edit Super Fund for Jennifer H in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public static void testI10151_SyncChangesAfterEditingJenniferHSuperFund() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10151.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.10: Sync Changes after Editing the Super for Jennifer H.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I5.10: Sync Changes after editing the Super for Jennifer H.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10151.");
        myAssert.assertAll();
    }

    @Test(priority = 10161)
    public static void testI10161_ValidateJenniferHSuperPageAfterEditSuperAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10161.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I5.11: Validate Jennifer H Super Page After Editing Super And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main (20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item I5.11: Validate Jennifer H Super Page After Editing Super And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10161.");
        myAssert.assertAll();
    }

    @Test (priority = 10171)
    public static void testI10171_AddSuperForTanyaDViaSageMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10171.");

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I5.12: Add Postcode for Tanya D in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_PersonalInformation(10021, 10021, payrollDBName), true, "Failed in Item I5.12: Add postcode for Tanya D in MicrOpay.");

        logMessage("Item I5.13: Change Tanya D Tax Status as Australia Resident", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_TaxDetails(10001, 10001, payrollDBName), true, "Failed in Item I5.13: Change Tanya D Tax Status as Australia Resident.");

        logMessage("Item I5.14: Add Super Fund for Tanya D in MicrOpay.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_Super(10031, 10031, payrollDBName), true, "Failed in Item I5.14: Add Super Fund for Tanya D in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10171.");
        myAssert.assertAll();
    }

    @Test(priority = 10181)
    public static void testI10181_SyncChangesAfterAddSuperFundForTanyaD() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10181.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I5.15: Sync Changes after Adding the Super for Tanya D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I5.15: Sync Changes after Adding the Super for Tanya D");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10181.");
        myAssert.assertAll();
    }

    @Test (priority = 10191)
    public static void testI10191_ValidateTanyaDSuperPageAfterAddSuperAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10191.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I5.16: Validate Tanya D Super Page After Add Super And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSuperannuation_Main (20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item I5.16: Validate Tanya D Super Page After Add Super And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10191.");
        myAssert.assertAll();
    }

    @Test(priority = 10201)
    public void testI10201_ConfigESSBeforeTestSyncChangesInEmployment() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10201.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I6.1: Configure Admin Roles - Employment - View", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main (20191, 20191, driver), true, "Failed in Item I6.1: Configure Admin Roles - Employment - View.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10201.");
        myAssert.assertAll();
    }

    @Test (priority = 10211)
    public static void testI10211_ValidateTanyaDInitialTaxDetailsPage() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10211.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I6.2: Validate Tanya D Initial Tax Details via Employment page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main (20021, 20021, testSerialNo, emailDomainName, driver), true, "Failed in Item I6.2: Validate Tanya D Initial Tax Details page.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10211.");
        myAssert.assertAll();
    }

    @Test (priority = 10221)
    public static void testI10221_ChangeTanyaDTaxStatusAsAForeighResidentViaSageMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10221.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I6.3: Change Tanya D Tax Status as a Foreign Resident", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_TaxDetails(10011, 10011, payrollDBName), true, "Failed in Item I6.3: Change Tanya D Tax Status as a Foreign Resident");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10221.");
        myAssert.assertAll();
    }

    //////////////////////
    @Test(priority = 10231)
    public static void testI10231_SyncChangesAfterAddSuperFundForTanyaD() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10231.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I6.4: Sync Changes after changing Tanya D Tax Status as Foreign Resident.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I6.4: Sync Changes after changing Tanya D Tax Status as Foreign Resident.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10231.");
        myAssert.assertAll();
    }

    @Test (priority = 10241)
    public static void testI10241_ValidateTanyaDTaxDetailsPageAfterTaxStatusChangeAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10241.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I6.5: Validate Tanya D Tax Details Page After Tax Status Change And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main (20031, 20031, testSerialNo, emailDomainName, driver), true, "Failed in Item I6.5: Validate Tanya D Tax Details Page After Tax Status Change And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10241.");
        myAssert.assertAll();
    }

    @Test (priority = 10251)
    public static void testI10251_AddHELPForTanyaDViaSageMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10251.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I6.6: Change Tanya D Tax Status as HELP", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_TaxDetails(10021, 10021, payrollDBName), true, "Failed in Item I6.6: Change Tanya D Tax Status as HELP");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10251.");
        myAssert.assertAll();
    }

    @Test(priority = 10261)
    public static void testI10261_SyncChangesAfterAddSuperFundForTanyaD() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10261.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I6.7: Sync Changes after changing Tanya D Tax Status as HELP.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I6.7: Sync Changes after changing Tanya D Tax Status as HELP.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10261.");
        myAssert.assertAll();
    }

    @Test (priority = 10271)
    public static void testI10271_ValidateTanyaDTaxDetailsPageAfterTaxStatusChangeToHELPAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10271.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I6.8: Validate Tanya D Tax Details Page After Tax Status Change To HELP And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main (20041, 20041, testSerialNo, emailDomainName, driver), true, "Failed in Item I6.8: Validate Tanya D Tax Details Page After Tax Status Change To HELP And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10271.");
        myAssert.assertAll();
    }

    @Test (priority = 10281)
    public static void testI10281_AddHELPForTanyaDViaSageMicrOpay() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10281.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I6.9: Change Tanya D Tax Status by un-selecting Finacial Supplement.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiEmployee_TaxDetails(10031, 10031, payrollDBName), true, "Failed in Item I6.9: Change Tanya D Tax Status by un-selecting Finacial Supplement");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        logMessage("*** End of test I10281.");
        myAssert.assertAll();
    }

    @Test(priority = 10291)
    public static void testI10291_SyncChangesAfterEditTanyaDTaxDetalByUnSelectingFinacialSupplement() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10291.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //Step 3.17 - 3.18
        SystemLibrary.logMessage("Item I6.10: Sync Changes After Editing Tanya D Tax Detals by Un-Selecting Finacial Supplement.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I6.10: Sync Changes After Editing Tanya D Tax Detals by Un-Selecting Finacial Supplement.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10291.");
        myAssert.assertAll();
    }

    @Test (priority = 10301)
    public static void testI10301_ValidateTanyaDTaxDetailsPageAfterEditTaxDetalSelectingFinacialSupplementAndSyncChanges() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10301.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I6.11: Validate Tanya D Tax Details Page After Edit Tax Detals by Un-Selecting Finacial Supplement And Sync Changes", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateEmployment_Main (20051, 20051, testSerialNo, emailDomainName, driver), true, "Failed in Item I6.11: Validate Tanya D Tax Details Page After Edit Tax Detals by Un-Selecting Finacial Supplement And Sync Changes");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10301.");
        myAssert.assertAll();
    }

    ///////////////// Leave Scheme change - Add Annual Leave //////////////////////

    @Test (priority = 10311)
    public static void testI10311_ValidateMartinGInitialLeavePage() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10311.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.1: Validate Martin G Initial Leave Page", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main (20271, 20271, testSerialNo, emailDomainName, driver), true, "Failed in Item I7.1: Validate Martin G Initial Leave Page.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10311.");
        myAssert.assertAll();
    }

    @Test (priority = 10321)
    public static void testI10321_AddEmployeeAnnualLeaveScheme() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start test I10321.");
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I7.2: Add Annual Leave to Martin G in MicrOpay.", testSerialNo);
        myAssert.assertEquals(editMultiEmployee_Leave(10021, 10021, payrollDBName), true, "Failed in Item I7.2: Add AL to Martin G in MicrOpay.");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        SystemLibrary.logMessage("*** End of test I10321.");
        myAssert.assertAll();
    }

    @Test(priority = 10331)
    public static void testI10331_SyncChangesAfterAddAnnualLeaveToMartinG() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10331.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        //ESS application fixed the issue Sync changes for leave update from MicrOpay. change expected result from "false to true".
        //Framework updated by Jim on 21/06/2021
        SystemLibrary.logMessage("Item I7.3: Sync Changes After Adding Annual Leave for Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I7.3: Sync Changes After Adding Annual Leave for Martin G.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10331.");
        myAssert.assertAll();
    }

    @Test (priority = 10341)
    public static void testI10341_ValidateMartinGLeavePageAfterAddALAndSyncChange() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10341.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.4: Validate Martin G Leave Page after adding Annual Leave and Sync change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main (20281, 20281, testSerialNo, emailDomainName, driver), true, "Failed in Item I7.4: Validate Martin G Leave Page after adding Annual Leave and Sync change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10341.");
        myAssert.assertAll();
    }

    ///////////////// Leave Scheme change - Add Sick Leave //////////////////////

    @Test (priority = 10351)
    public static void testI10351_AddEmployeeSickLeaveScheme() throws Exception {
        SystemLibrary.logMessage("*** Start test I10351.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I7.5: Add Sick Leave to Martin G in MicrOpay.", testSerialNo);
        myAssert.assertEquals(editMultiEmployee_Leave(10031, 10031, payrollDBName), true, "Failed in Item I7.5: Add Sick Leave to Martin G in MicrOpay");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        SystemLibrary.logMessage("*** End of test I10351.");
        myAssert.assertAll();
    }

    @Test(priority = 10361)
    public static void testI10361_SyncChangesAfterAddSickLeaveToMartinG() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10361.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.6: Sync Changes After Adding Sick Leave for Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), true, "Failed in Item I7.6: Sync Changes After Adding Sick Leave for Martin G.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10361.");
        myAssert.assertAll();
    }

    @Test (priority = 10371)
    public static void testI10371_ValidateMartinGLeavePageAfterAddSickLeaveAndSyncChange() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10371.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.7: Validate Martin G Leave Page after adding Sick Leave and Sync change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main (20291, 20291, testSerialNo, emailDomainName, driver), false, "Failed in Item I7.7: Validate Martin G Leave Page after adding Sick Leave and Sync change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10371.");
        myAssert.assertAll();
    }

    ///////////////// Leave Scheme change - Add Long Service Leave //////////////////////

    @Test (priority = 10381)
    public static void testI10381_AddEmployeeLongServiceLeaveScheme() throws Exception {
        SystemLibrary.logMessage("*** Start test I10381.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I7.8: Add Long Service Leave to Martin G in MicrOpay.", testSerialNo);
        myAssert.assertEquals(editMultiEmployee_Leave(10041, 10041, payrollDBName), true, "Failed in Item I7.8: Add Long Service Leave to Martin G in MicrOpay");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        SystemLibrary.logMessage("*** End of test I10381.");
        myAssert.assertAll();
    }

    @Test(priority = 10391)
    public static void testI10391_SyncChangesAfterAddLongServiceLeaveToMartinG() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10361.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.9: Sync Changes After Adding Long Service Leave for Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), false, "Failed in Item I7.9: Sync Changes After Adding Long Service Leave for Martin G.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10391.");
        myAssert.assertAll();
    }

    @Test (priority = 10401)
    public static void testI10401_ValidateMartinGLeavePageAfterAddLSLAndSyncChange() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10401.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.10: Validate Martin G Leave Page after adding LSL and Sync change.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateLeavePage_Main (20301, 20301, testSerialNo, emailDomainName, driver), true, "Failed in Item I7.10: Validate Martin G Leave Page after adding LSL and Sync change.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10401.");
        myAssert.assertAll();
    }


    ///////////////// Leave Scheme change - Add User Defined Leave //////////////////////

    @Test (priority = 10411)
    public static void testI10411_AddEmployeeUserDefinedLeaveScheme() throws Exception {
        SystemLibrary.logMessage("*** Start test I10411.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        logMessage("Item I7.11: Add User Defined Leave to Martin G in MicrOpay.", testSerialNo);
        myAssert.assertEquals(editMultiEmployee_Leave(10051, 10051, payrollDBName), true, "Failed in Item I7.8: Add Long Service Leave to Martin G in MicrOpay");

        AutoITLib.closeEmployeeDetailScreen();
        AutoITLib.exitMeridian();
        SystemLibrary.logMessage("*** End of test I10411.");
        myAssert.assertAll();
    }

    @Test(priority = 10421)
    public static void testI10421_SyncChangesAfterAddLongServiceLeaveToMartinG() throws IOException, InterruptedException, Exception {
        SystemLibrary.logMessage("*** Start test I10421.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        SystemLibrary.logMessage("Item I7.12: Sync Changes After Adding User Defined Leave for Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncChanges_Main(10021, 10021, driver), false, "Failed in Item I7.12: Sync Changes After Adding User Defined Leave for Martin G.");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of test I10421.");
        myAssert.assertAll();
    }

    ////////////// Create and add scenarios Second Database //////////////
    ////////////// Start Multi Approval Test Scenario 1 /////////////////

    @Test (priority = 15011)
    public static void testI15011_createWebAPIKeysSecondDB() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Item I15011: create Web APIKeys for Second DB");

        logMessage("Item I15.1: Generating WBAPI Keys for Second Database.");
        GeneralBasicHigh.generateWebAPIKey_SecondDatabase_Main(101, 120);

        SystemLibrary.logMessage("*** End of Item I15011: create Web APIKeys for Second DB");
        myAssert.assertAll();
    }


    @Test(priority = 15021) //a
    public void testI15021_ScenarioPrepareSecondDB() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        //sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);
        SystemLibrary.logMessage("*** Start Item I15021: Scenario Preparation for Second DB");

        logMessage("Item I15.2: Configuring MCS for Second Database");
        configureMCS2(moduleName, testSerialNo, payrollDB2Name, comDB2Name);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item I15.3: Delete and Restore Second Payroll and Common DB.");
        DBManage.deleteAndRestoreSageMicrOpayDB(payrollDB2Name, comDB2Name, emailDomainName);

        //Assing the rights to DB after restoring.
        if ((payrollDB2Name.equals("ESS2_Auto_Payroll")) && (comDB2Name.equals("ESS_Auto_COM1"))) {
            DBManage.sqlExecutor_Main(20001, 20001);
            DBManage.sqlExecutor_Main(20002, 20002);
        } else if ((payrollDB2Name.equals("ESS2_Auto_Payroll2")) && (comDB2Name.equals("ESS2_Auto_COM2"))) {
            DBManage.sqlExecutor_Main(20003, 20003);
            DBManage.sqlExecutor_Main(20004, 20004);
        }
        DBManage.logTestResultIntoDB_Main("Item I15.3", "pass", null, testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item I15.4: Update email address for Second DB and change email type as work email");
        if ((payrollDB2Name.equals("ESS2_Auto_Payroll")) && (comDB2Name.equals("ESS_Auto_COM1"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(20005, 20005, testSerialNo, emailDomainName);
        } else if ((payrollDB2Name.equals("ESS2_Auto_Payroll2")) && (comDB2Name.equals("ESS2_Auto_COM2"))) {
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(20006, 20006, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item I15.4", "Pass", null, testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item I15.5: Remove data from staging table for Second DB.", testSerialNo);
        if ((payrollDB2Name.equals("ESS2_Auto_Payroll")) && (comDB2Name.equals("ESS_Auto_COM1"))) {
            DBManage.sqlExecutor_Main(20007, 20007);
        } else if ((payrollDB2Name.equals("ESS22_Auto_Payroll2")) && (comDB2Name.equals("ESS2_Auto_COM2"))) {
            DBManage.sqlExecutor_Main(20008, 20008);
        }
        DBManage.logTestResultIntoDB_Main("Item I15.5", "Pass", testSerialNo);

        SystemLibrary.logMessage("*** End of Item I15021: Scenario Preparation for Second DB");
        myAssert.assertAll();
    }

    @Test (priority = 15031)
    public static void testI15031_logintoSecondMicrOpayDB() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo == null) testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start test I15031.");

        AutoITLib.exitMeridian();
        logMessage("Item I15.6: login to Micropay Second Database", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.logonMeridianSecondDB_Main(111, 111, payrollDB2Name, payrollDB2OrderNumber), true, "Failed in Item I15.6: login to Micropay Second Database");

        AutoITLib.exitMeridian();
        logMessage("End of test I15031");
        myAssert.assertAll();
    }

    @Test(priority = 15041)
    public void testI15041_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test I15041.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon as Admin");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item I15.7: Add API configuration for Second Database", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(10101, 10101, testSerialNo, driver), true, "Failed in Item I15.7: Add API configuration for Second Database");

        logMessage("Item I15.8: Sync All after Second Database added and Validate", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(10101, 10101, driver), true, "Failed in Failed in I15.8: Sync All after Second Database added and Validate");

        logMessage("Item I15.9: Valdiate Team page via Admin", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(30901, 30901, testSerialNo, emailDomainName, driver), true, "Failed in Item I15.9: Valdiate Team page via Admin");

        signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test I15041.");
        myAssert.assertAll();
    }


    @Test(priority = 15051)
    public static void testI15051_ImportTeamViaTPU() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test I15051.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item I16.1: Import Teams from 1st Payroll DBs.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath + "Team_Import_TwoDataBases.csv", "1"), true, "Failed in Item I16.1: Import Teams from 1st Payroll DB.");

        logMessage("Item I16.2: Import Teams from 2nd Payroll DBs.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath + "Team_Import_TwoDataBases.csv", "2"), true, "Failed in Item I16.2: Import Teams from 2nd Payroll DB.");

        SystemLibrary.logMessage("*** End of Test I15051.");
        myAssert.assertAll();
    }



    @Test(priority = 15061) //a
    public void testI15061_ActivateSomeTeamMemebersinTeamA() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15061.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        logMessage("Item I17.1.X1: Add Email address for Kathy N.");
        GeneralBasicHigh.editMultiContactDetails(20551, 20551, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, driver);

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("Item I7.1: Activate Manager Jaime A.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20501, 20501, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I7.1: Activate Manager Jaime A.");

        SystemLibrary.logMessage("Item I17.2: Activate Manager Robert S.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20502, 20502, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.2: Activate Manager Robert S in Team A.");

        SystemLibrary.logMessage("Item I17.3: Activate Alex A.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20503, 20503, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.3: Activate Alex A in Team A.");

       /* SystemLibrary.logMessage("Item I17.4: Activate Alexandrajane D.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20504, 20504, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.4: Activate Alexandrajane D in Team A.");

        SystemLibrary.logMessage("Item I17.5: Activate Anthony B.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20505, 20505, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.5: Activate Anthony B in Team A");

        SystemLibrary.logMessage("Item I17.6: Activate Celine C.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20506, 20506, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.6: Activate Celine C in Team A");

        SystemLibrary.logMessage("Item I17.7: Activate Charles L.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20507, 20507, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.7: Activate Charles L in Team A");

        SystemLibrary.logMessage("Item I17.8: Activate Christine R.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20508, 20508, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.8: Activate Christine R in Team A");

        SystemLibrary.logMessage("Item I17.9: Activate Denise M.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20509, 20509, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.9: Activate Denise M in Team A");

        SystemLibrary.logMessage("Item I17.10: Activate Ernie M.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20510, 20510, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.10: Activate Ernie M in Team A");

        SystemLibrary.logMessage("Item I17.11: Activate Gregory S.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20511, 20511, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.11: Activate Gregory S in Team A");

        SystemLibrary.logMessage("Item I17.12: Activate Johnathon D.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20512, 20512, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.12: Activate Johnathon D in Team A");

        SystemLibrary.logMessage("Item I17.13: Activate Kate F.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20513, 20513, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.13: Activate Kate F in Team A");

        SystemLibrary.logMessage("Item I17.14: Activate Kathy N.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20514, 20514, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.14: Activate Kathy N in Team A");

        SystemLibrary.logMessage("Item I17.15: Activate Martin G.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20515, 20515, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.15: Activate Marthin G in Team A");

        SystemLibrary.logMessage("Item I17.16: Activate Patricia I.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20516, 20516, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.16: Activate Patricia I in Team A");

        SystemLibrary.logMessage("Item I17.17: Activate Paul F.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20517, 20517, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.17: Activate Paul F in Team A");

        SystemLibrary.logMessage("Item I17.18: Activate Ricky M.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20518, 20518, "ESS2_Auto_Payroll", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.18: Activate Ricky M in Team A");
        */

        SystemLibrary.logMessage("Item I17.19: Activate Robin S.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20519, 20519, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.19: Activate Robin S in Team A");
        /*
        SystemLibrary.logMessage("Item I17.20: Activate Steve B.", testSerialNo);
        myAssert.assertEquals(activateMultiUserAccount_ViaAdmin(20520, 20520, "ESS_Auto_Payroll1", testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I17.20: Activate Steve B in Team A");
        */
        driver.close();
        SystemLibrary.logMessage("*** End of Test I15061.");
        myAssert.assertAll();
    }

    @Test(priority = 15071) //a
    public void testI15071_LogonESSAs2ManagersInTeamA() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        SystemLibrary.logMessage("*** Start Test I15071.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.1: Logon ESS as Manager Jaime A.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20131, 20131, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.1: Logon ESS as Manager Jaime A.");
        Thread.sleep(2000);
        signoutESS(driver);
        driver.close();

        driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.2: Logon ESS as Manager Robert S.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10171, 10171, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item I18.2: Logon ESS as Manager Robert S.");
        Thread.sleep(2000);
        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15071.");
        myAssert.assertAll();
    }

    ////////////////// Edit multi Personal Information and Apply multi Leave via Admin ////////////////

    @Test(priority = 15072)
    public void testI15072_EditPersonalInformationAndApplyLeaveForAllMembersInTeamAViaAdmin() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15072");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon ESS as Admin", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDB2Name, testSerialNo, emailDomainName, driver);

        logMessage("Item I18.2.X1: Apply Leaves for All Team members in Team A for Multiple Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20511, 20528, testSerialNo, driver), true, "Failed in Item I18.2.X1: Apply Leaves for All Team member in Team A for Multiple Approval Scenario 1.");

        logMessage("Item I18.2.X2: Change Personal Information for All Team members in Team A for Multiple Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20601, 20618, testSerialNo, driver), true, "Failed in Item I18.2.X2: Change Personal Information in Team A for Multiple Approval Scenario 1.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15072.");
        myAssert.assertAll();
    }

    ////////

    /*
    @Test(priority = 15081) //a
    public void testI15081_EditPersonalInformationAndApplyALViaAlecA() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15081.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.3: Logon ESS as Alec A.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20132, 20132, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.1: Logon ESS as Alex A.");
        Thread.sleep(2000);

       logMessage("Item I18.4: Edit Personal Information via Alec A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20601, 20601, testSerialNo, driver), true, "Failed in Item I18.4: Edit Personal Information via Alec A.");

        logMessage("Item I18.5: Apply Leave via Alec A.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20511, 20511, testSerialNo, driver), true, "Failed in Item I18.5: Apply Leave via Alec A.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15081.");
        myAssert.assertAll();
    }

    @Test(priority = 15091) //a
    public void testI15091_EditPersonalInformationAndApplyLeaveViaAlexandrajaneD() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15091.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.6: Logon ESS as Alexandrajane D.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20133, 20133, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.6: Logon ESS as Alexandrajane D.");
        Thread.sleep(2000);

        //////// No need change for Alexandrajane D
        //logMessage("Item I18.7: Edit Personal Information via Alexandrajane D.", testSerialNo);
        //myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20602, 20602, testSerialNo, driver), true, "Failed in Item I18.7: Edit Personal Information via Alexandrajane D.");

        logMessage("Item I18.8: Apply AL via Alexandrajane D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20512, 20512, testSerialNo, driver), true, "Failed in Item I18.8: Apply AL via Alexandrajane D.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15091.");
        myAssert.assertAll();
    }

    @Test(priority = 15101)
    public void testI15101_EditPersonalInformationAndApplyLeaveViaAnthonyB() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15101.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.9: Logon ESS as Anthony B.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20134, 20134, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.9: Logon ESS as Alexandrajane D.");
        Thread.sleep(2000);

        logMessage("Item I18.10: Edit Personal Information via Anthony B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20603, 20603, testSerialNo, driver), true, "Failed in Item I18.10: Edit Personal Information via Anthony B.");

        logMessage("Item I18.11: Apply Leave via Anthony B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20513, 20513, testSerialNo, driver), true, "Failed in Item I18.11: Apply Unpaid Leave via Anthony B.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15101.");
        myAssert.assertAll();
    }

    @Test(priority = 15111)
    public void testI15111_EditPersonalInformationAndApplyLeaveViaCelineC() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15111.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.12: Logon ESS as Celine C.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20135, 20135, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.12: Logon ESS as Celine C.");
        Thread.sleep(2000);

        logMessage("Item I18.13: Edit Personal Information via Celine C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20604, 20604, testSerialNo, driver), true, "Failed in Item I18.13: Edit Personal Information via Celine C.");

        logMessage("Item I18.14: Apply Leave via Celine C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20514, 20514, testSerialNo, driver), true, "Failed in Item I18.14: Apply Unpaid Leave via Celine C.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15111.");
        myAssert.assertAll();
    }

    @Test(priority = 15112)
    public void testI15112_EditPersonalInformationAndApplyLeaveViaCharlesL() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15112.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.15: Logon ESS as Charles L.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20136, 20136, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.15: Logon ESS as Charles L.");
        Thread.sleep(2000);

        logMessage("Item I18.16: Edit Personal Information via Celine C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20605, 20605, testSerialNo, driver), true, "Failed in Item I18.16: Edit Personal Information via Charles L.");

        logMessage("Item I18.17: Apply Leave via Celine C.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20515, 20515, testSerialNo, driver), true, "Failed in Item I18.17: Apply Unpaid Leave via Charles L.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15112.");
        myAssert.assertAll();
    }

    @Test(priority = 15113)
    public void testI15113_EditPersonalInformationAndApplyLeaveViaChristineR() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15113.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.18: Logon ESS as Christine R.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20137, 20137, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.18: Logon ESS as Christine R.");
        Thread.sleep(2000);

        logMessage("Item I18.19: Add Edit PersonalInformation via Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20606, 20606, testSerialNo, driver), true, "Failed in Item I18.19: Edit Personal Information via Christine R.");

        logMessage("Item I18.20: Apply Leave via Christine R.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20516, 20516, testSerialNo, driver), true, "Failed in Item I18.20: Apply Unpaid Leave via Christine R.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15113.");
        myAssert.assertAll();
    }

    @Test(priority = 15114)
    public void testI15114_EditPersonalInformationAndApplyLeaveViaDeniseM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15114");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.21: Logon ESS as Denise M.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20138, 20138, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.21: Logon ESS as Denise M.");
        Thread.sleep(2000);

        logMessage("Item I18.22: Edit Personal Information via Denise M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20607, 20607, testSerialNo, driver), true, "Failed in Item I18.22: Edit Personal Information via Denise M.");

        logMessage("Item I18.23: Apply Leave via Denise M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20517, 20517, testSerialNo, driver), true, "Failed in Item I18.23: Apply Unpaid Leave via Denise M.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15114.");
        myAssert.assertAll();
    }

    @Test(priority = 15115)
    public void testI15115_EditPersonalInformationAndApplyLeaveViaErnieM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15115");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.24: Logon ESS as Ernie M.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20139, 20139, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.24: Logon ESS as Ernie M.");
        Thread.sleep(2000);

        logMessage("Item I18.25: Edit Personal Information via Ernie M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20608, 20608, testSerialNo, driver), true, "Failed in Item I18.25: Edit Personal Information via Ernie M.");

        logMessage("Item I18.26: Apply Leave via Ernie M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20518, 20518, testSerialNo, driver), true, "Failed in Item I18.26: Apply Unpaid Leave via Ernie M.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15115.");
        myAssert.assertAll();
    }

    @Test(priority = 15116)
    public void testI15116_EditPersonalInformationAndApplyLeaveViaGregoryS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15116");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.27: Logon ESS as Gregory S.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20140, 20140, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.27: Logon ESS as Gregory S.");
        Thread.sleep(2000);

        logMessage("Item I18.28: Edit Personal Information via Gregory S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20609, 20609, testSerialNo, driver), true, "Failed in Item I18.28: Edit Personal Information via Gregory S.");

        logMessage("Item I18.29: Apply Leave via Gregory S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20519, 20519, testSerialNo, driver), true, "Failed in Item I18.29: Apply Unpaid Leave via Gregory S.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15116.");
        myAssert.assertAll();
    }

    @Test(priority = 15117)
    public void testI15117_EditPersonalInformationAndApplyLeaveViaJohnathonD() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15117");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.30: Logon ESS as Johnathon D.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20141, 20141, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.30: Logon ESS as Johnathon D.");
        Thread.sleep(2000);

        logMessage("Item I18.31: Edit Personal Information via Johnathon D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20610, 20610, testSerialNo, driver), true, "Failed in Item I18.31: Edit Personal Information via Johnathon D.");

        logMessage("Item I18.32: Apply Leave via Johnathon D.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20520, 20520, testSerialNo, driver), true, "Failed in Item I18.32: Apply Unpaid Leave via Johnathon D.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15117.");
        myAssert.assertAll();
    }

    @Test(priority = 15118)
    public void testI15118_EditPersonalInformationAndApplyLeaveViaKateF() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15118");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.33: Logon ESS as Kate F.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20142, 20142, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.33: Logon ESS as Kate F.");
        Thread.sleep(2000);

        logMessage("Item I18.34: Edit Personal Information via Kate F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20611, 20611, testSerialNo, driver), true, "Failed in Item I18.34: Edit Personal Information via Kate F.");

        logMessage("Item I18.35: Apply Leave via Kate F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20521, 20521, testSerialNo, driver), true, "Failed in Item I18.35: Apply Unpaid Leave via Kate F.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15118.");
        myAssert.assertAll();
    }

    @Test(priority = 15119)
    public void testI15119_EditPersonalInformationAndApplyLeaveViaKathyN() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15119");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.36: Logon ESS as Kathy N.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20143, 20143, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.36: Logon ESS as Kathy N.");
        Thread.sleep(2000);

        logMessage("Item I18.37: Edit Personal Information via Kathy N.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20612, 20612, testSerialNo, driver), true, "Failed in Item I18.37: Edit Personal Information via Kathy N.");

        logMessage("Item I18.38: Apply Leave via Kathy N.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20522, 20522, testSerialNo, driver), true, "Failed in Item I18.38: Apply Unpaid Leave via Kathy N.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15119.");
        myAssert.assertAll();
    }

    @Test(priority = 15120)
    public void testI15120_EditPersonalInformationAndApplyLeaveViaMartinG() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15120");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.39: Logon ESS as Martin G.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20144, 20144, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.39: Logon ESS as Martin G.");
        Thread.sleep(2000);

        logMessage("Item I18.40: Edit Personal Information via Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20613, 20613, testSerialNo, driver), true, "Failed in Item I18.40: Edit Personal Information via Martin G.");

        logMessage("Item I18.41: Apply Leave via Martin G.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20523, 20523, testSerialNo, driver), true, "Failed in Item I18.41: Apply Unpaid Leave via Martin G.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15120.");
        myAssert.assertAll();
    }

    @Test(priority = 15130)
    public void testI15130_EditPersonalInformationAndApplyLeaveViaPatriciaI() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15130");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.42: Logon ESS as Patricia I.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20145, 20145, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.42: Logon ESS as Patricia I.");
        Thread.sleep(2000);

        logMessage("Item I18.43: Edit Personal Information via Patricia I.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20614, 20614, testSerialNo, driver), true, "Failed in Item I18.43: Edit Personal Information via Patricia I.");

        logMessage("Item I18.44: Apply Leave via Patricia I.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20524, 20524, testSerialNo, driver), true, "Failed in Item I18.44: Apply Unpaid Leave via Patricia I.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15130.");
        myAssert.assertAll();
    }

    @Test(priority = 15140)
    public void testI15140_EditPersonalInformationAndApplyLeaveViaPaulF() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15140");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.45: Logon ESS as Paul F.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20146, 20146, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.45: Logon ESS as Paul F.");
        Thread.sleep(2000);

        ///////////// No need change for Paul F /////////////
        //logMessage("Item I18.46: Edit Personal Information via Paul F.", testSerialNo);
        //myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20615, 20615, testSerialNo, driver), true, "Failed in Item I18.46: Edit Personal Information via Paul F.");

        logMessage("Item I18.47: Apply Leave via Paul F.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20525, 20525, testSerialNo, driver), true, "Failed in Item I18.47: Apply Unpaid Leave via Paul F.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15140.");
        myAssert.assertAll();
    }

    @Test(priority = 15150)
    public void testI15150_EditPersonalInformationAndApplyLeaveViaRickyM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15150");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.48: Logon ESS as Ricky M.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20147, 20147, payrollDB2Name, testSerialNo, emailDomainName, driver), true, "Falied in Item I18.48: Logon ESS as Ricky M.");
        Thread.sleep(2000);

        logMessage("Item I18.49: Edit Personal Information via Ricky M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20616, 20616, testSerialNo, driver), true, "Failed in Item I18.49: Edit Personal Information via Ricky M.");

        logMessage("Item I18.50: Apply Leave via Ricky M.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20526, 20526, testSerialNo, driver), true, "Failed in Item I18.50: Apply Unpaid Leave via Ricky M.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15150.");
        myAssert.assertAll();
    }

    @Test(priority = 15160)
    public void testI15160_EditPersonalInformationAndApplyLeaveViaRobinS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15160");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.51: Logon ESS as Robin S.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20148, 20148, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.51: Logon ESS as Robin S.");
        Thread.sleep(2000);

        logMessage("Item I18.52: Edit Personal Information via Robin S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20617, 20617, testSerialNo, driver), true, "Failed in Item I18.52: Edit Personal Information via Robin S.");

        logMessage("Item I18.53: Apply Leave via Robin S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20527, 20527, testSerialNo, driver), true, "Failed in Item I18.53: Apply Unpaid Leave via Robin S.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15160.");
        myAssert.assertAll();
    }

    @Test(priority = 15170)
    public void testI15170_EditPersonalInformationAndApplyLeaveViaSteveB() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15170");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I18.54: Logon ESS as Steve B.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20149, 20149, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I18.54: Logon ESS as Steve B.");
        Thread.sleep(2000);

        logMessage("Item I18.55: Edit Personal Information via Steve B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20618, 20618, testSerialNo, driver), true, "Failed in Item I18.55: Edit Personal Information via Steve B.");

        logMessage("Item I18.56: Apply Leave via Steve B.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20528, 20528, testSerialNo, driver), true, "Failed in Item I18.56: Apply Unpaid Leave via Steve B.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15170.");
        myAssert.assertAll();
    }
*/

    @Test(priority = 15180)
    public void testI15180_ApproveAllPendingApprovalsViaTeamAManagerRobertS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15180");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I19.01: Logon ESS as Team A Manger Robert S.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20150, 20150, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I19: Log on ESS as Team A Manger Robert S.");
        Thread.sleep(2000);

        logMessage("Item I19.02: Validate All approval list via Team Manger Robert S.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20711, 20711, testSerialNo, emailDomainName,driver), true, "Failed in Item I19.02: Validate All approval list via Team Manager Robert S.");

        logMessage("Item I19.03: Approve All Pending Approvals via Team A Manger Robert S.", testSerialNo);
        //Approve test twice is not required anymoore. Application fixed for all multi approval list. Jim adjusted on 21/06/2021
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20370, 20370, testSerialNo, emailDomainName, driver), true, "Failed in Item I19.03: Approve all pending approval via Team A Manager Robert S.");

        logMessage("Item I19.04: Validate All Aproval list via Team A manager Rober S after Approve All.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20721, 20721, testSerialNo, emailDomainName,driver), true, "Failed in Item I19.04: Validate All approval list via Team Manager Robert S after approve all list..");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15180.");
        myAssert.assertAll();
    }

    @Test(priority = 15190)
    public void testI15190_ValidateAllApprovalItemInPayrollDBsInFlowScenario1() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15190");
        logMessage("Delay exectuion for 5 mins before Validation.");
        Thread.sleep(300000);

        logMessage("Item I20.01: Validate the leave applications have been sent to DB1 in Multi Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10101, 10101, emailDomainName, testSerialNo), true, "Failed in Failed in Item I20.01: Validate the leave applications have been sent to DB1 in Multi Approval Scenario 1.");

        logMessage("Item I20.02: Validate the leave applications have been sent to DB2 in Multi Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10102, 10102, emailDomainName, testSerialNo), true, "Failed in Failed in Item I20.02: Validate the leave applications have been sent to DB2 in Multi Approval Scenario 1.");

        logMessage("Item I20.03: Validate the Profile Changes have been sent to DB1 in Multi Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10103, 10103, emailDomainName, testSerialNo), true, "Failed in Failed in Item I20.03: Validate the Profile Changes have been sent to DB1 in Multi Approval Scenario 1.");

        logMessage("Item I20.04: Validate the Profile Changes have been sent to DB2 in Multi Approval Scenario 1.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10104, 10104, emailDomainName, testSerialNo), true, "Failed in Failed in Item I20.04: Validate the Profile Chnages have been sent to DB2 in Multi Approval Scenario 1.");


        SystemLibrary.logMessage("*** End of Test I15190.");
        myAssert.assertAll();
    }

    ////////////// End of Multi Approval Test Scenario 1 /////////////////

    ///////////// Start Multi Approval Test Scenario 2 //////////////

    @Test(priority = 15201)
    public void testI15201_PrepareMultiApprovalTestScenario2() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15201");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        logMessage("Delate Data in Staging Table.");
        DBManage.sqlExecutor_Main(10121, 10122);

       /* logMessage("Log on Sage MicrOpay Payroll DB 1 as Admin");
        AutoITLib.exitMeridian();
        AutoITLibHigh.logonMeridian_Main(101, 101, payrollDBName, payrollDBOrderNumber);

        SystemLibrary.logMessage("Item I21.01: Edit Monthly POE date in Micropay DB1.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.editMultiPayFrequencies(10051, 10051), true, "Failed in Item I21.01: Edit Monthly POE date in Micropay DB1.");

        logMessage("Item I21.02: Run Leave Processing in Micropay DB1 for the current pay period.", testSerialNo);
        myAssert.assertEquals(AutoITLibHigh.performMultiLeaveProcessing(10121, 10121), true, "Failed in Item I20.05: Run Leave Processing in Micropay DB1 for the current pay period");

        logMessage("Item I21.03: Implement eHR via Sage Micropay DB1.", testSerialNo);
        myAssert.assertEquals(AutoITLib.implementEHR(), true, "Failed in Item I21.03: Implement eHR via Sage Micropay DB1.");

        AutoITLib.exitMeridian();*/

        SystemLibrary.logMessage("*** End of Test I15201.");

        myAssert.assertAll();
    }

    @Test(priority = 15211)
    public void testI15211_EditPersonalInformationAndApplyLeaveViaRickyM() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15211");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Logon ESS as Admin", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDB2Name, testSerialNo, emailDomainName, driver);

        logMessage("Item I22.01: Apply Leaves for All Team members in Team A for Multiple Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20541, 20558, testSerialNo, driver), true, "Failed in Item I22.01: Apply Leaves for All Team member in Team A for Multiple Approval Scenario 2.");

        logMessage("Item I22.02: Change Personal Information for All Team members in Team A for Multiple Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20631, 20648, testSerialNo, driver), true, "Failed in Item I22.02: Change Personal Information in Team A for Multiple Approval Scenario 2.");

        logMessage("Item I22.03: Apply Leaves for All Team members in Team B for Multiple Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.applyMultiLeave(20571, 20579, testSerialNo, driver), true, "Failed in Item I22.03: Apply Leaves for All Team members in Team B for Multiple Approval Scenario 2.");

        logMessage("Item I22.04: Change Personal Information for All Team members in Team B for Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(20661, 20669, testSerialNo, driver), true, "Failed in Item I22.04: Change Personal Information in Team B for Multi Approval Scenario 2.");

        signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test I15211.");
        myAssert.assertAll();
    }

    //////////////////// Debug here //////////////////
    @Test(priority = 15221)
    public void testI15221_ApproveAllPendingApprovalsViaTeamAManagerRobertS() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15221");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        WebDriver driver2 = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);

        logMessage("Item I23.01: Logon ESS as Team A Manger Robert S.", testSerialNo);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20150, 20150, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Falied in Item I19: Log on ESS as Team A Manger Robert S.");
        Thread.sleep(2000);

        logMessage("Item I23.03: Logon ESS as Team B Manger Robin S.", testSerialNo);
        driver2.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(20148, 20148, payrollDBName, testSerialNo, emailDomainName, driver2), true, "Falied in Item I23.03: Logon ESS as Team B Manger Robin S.");
        Thread.sleep(2000);

        logMessage("Item I23.02: Validate All approval list via Team A Manger Robert S in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20731, 20731, testSerialNo, emailDomainName,driver), true, "Failed in Item I23.02: Validate All approval list via Team A Manger Robert S in Multi Approval Scenario 2.");

        logMessage("Item I23.04: Validate All approval list via Team B Manger Robind S in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20732, 20732, testSerialNo, emailDomainName,driver2), true, "Failed in Item I23.04: Validate All approval list via Team B Manger Bobin S in Multi Approval Scenario 2.");

        ////////////////////////
        logMessage("Item I23.05: Approve All Pending Approvals via Team A Manger Robert S in Multi Approval Scenario 2.", testSerialNo);
        //Approve test 3 times, bugs fixed. Only one approve is required. Framework adjusted by Jim on 21/06/2021
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20381, 20381, testSerialNo, emailDomainName, driver), true, "Failed in Item I23.05: Approve all pending approval via Team A Manager Robert S in Multi Approval Scenario 2.");

        logMessage("Item I23.06: Approve All Pending Approvals via Team B Manger Robin S in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.approveMultiItem(20382, 20382, testSerialNo, emailDomainName, driver2), true, "Failed in Item I23.06: Approve all pending approval via Team B Manager Robin S in Multi Approval Scenario 2.");

        logMessage("Item I23.07: Validate All Aproval list via Team A manager Rober S after Approve All in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20741, 20741, testSerialNo, emailDomainName,driver2), true, "Failed in Item I23.07: Validate All Aproval list via Team A manager Rober S after Approve All in Multi Approval Scenario 2.");

        logMessage("Item I23.08: Validate All Aproval list via Team B manager Robind S after Approve All in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateApprovals_All_NONAdminUser_Main(20742, 20742, testSerialNo, emailDomainName,driver2), true, "Failed in Item I23.08: Validate All Aproval list via Team B manager Robin S after Approve All in Multi Approval Scenario 2.");

        signoutESS(driver);
        driver.close();

        signoutESS(driver2);
        driver2.close();

        SystemLibrary.logMessage("*** End of Test I15221.");
        myAssert.assertAll();
    }

    @Test(priority = 15231)
    public void testI15231_ValidateAllApprovalItemInPayrollDBsInFlowScenario2() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test I15231");

        logMessage("Item I24.01: Validate the leave applications have been sent to DB1 in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10131, 10131, emailDomainName, testSerialNo), true, "Failed in Failed in Item I24.01: Validate the leave applications have been sent to DB1 in Multi Approval Scenario 2.");

        logMessage("Item I24.02: Validate the leave applications have been sent to DB2 in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10132, 10132, emailDomainName, testSerialNo), true, "Failed in Failed in Item I24.02: Validate the leave applications have been sent to DB2 in Multi Approval Scenario 2.");

        logMessage("Item I24.03: Validate the Profile Changes have been sent to DB1 in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10133, 10133, emailDomainName, testSerialNo), true, "Failed in Failed in Item I24.03: Validate the Profile Changes have been sent to DB1 in Multi Approval Scenario 2.");

        logMessage("Item I24.04: Validate the Profile Changes have been sent to DB2 in Multi Approval Scenario 2.", testSerialNo);
        myAssert.assertEquals(DBManage.sqlExecuter_Compare_Main(10134, 10134, emailDomainName, testSerialNo), true, "Failed in Failed in Item I24.04: Validate the Profile Chnages have been sent to DB2 in Multi Approval Scenario 2.");


        SystemLibrary.logMessage("*** End of Test I15231.");
        myAssert.assertAll();
    }

    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item III", "Completed", "End of Test Module I - Sync Function Test", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module I - Sync Function test.");
    }



    /////////////// Debug here ///////////////////////








}