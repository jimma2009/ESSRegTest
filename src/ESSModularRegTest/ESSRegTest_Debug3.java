package ESSModularRegTest;

import Lib.*;
import PageObject.PageObj_Teams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.*;
import static Lib.JavaMailLib.sendEmail_ESSRegTestComplete_Notificaiton;
import static Lib.JavaMailLib.sendEmail_ESSRegTestStart_Notificaiton;
import static Lib.SystemLibrary.*;

public class ESSRegTest_Debug3 {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;

    private static String payrollDBOrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String testEmailNotification;
    //////

    private static int moduleNo = 102;
    private static String moduleName="B";


    static {
        try {
            //testSerialNo=GeneralBasicHigh.getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

            //////////////// New Field /////////////
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");
            testEmailNotification =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailAddressForTestNotification");
            ///////

            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");
            if (payrollDBOrderNumber==null){
                payrollDBOrderNumber="1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test(priority = 10011) //a
    public void testB10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        sendEmail_ESSRegTestStart_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        SystemLibrary.logMessage("*** Start Item B10011: Scenario Preparation");

        logMessage("Configuring MCS");
        //configureMCS_Main(102, 102);
        configureMCS(moduleName, testSerialNo, payrollDBName, comDBName);


        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item B1.4: Delete and Restore Payroll and Common DB.");
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
        DBManage.logTestResultIntoDB_Main("Item B1.4", "pass", testSerialNo);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Item B1.5: Update email address and change email type as work email.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1001, 1001, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1002, 1002, testSerialNo, emailDomainName);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            GeneralBasic.updateEmployeeEmailInSageMicropayDB(1003, 1003, testSerialNo, emailDomainName);
        }
        DBManage.logTestResultIntoDB_Main("Item B1.5", "pass", testSerialNo);


        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Item B1.6: Remove data from staging table.");
        if ((payrollDBName.equals("ESS_Auto_Payroll1"))&&(comDBName.equals("ESS_Auto_COM1"))){
            DBManage.sqlExecutor_Main(1011, 1011);
        }
        else if ((payrollDBName.equals("ESS_Auto_Payroll2"))&&(comDBName.equals("ESS_Auto_COM2"))){
            DBManage.sqlExecutor_Main(1012, 1012);
        }
        if ((payrollDBName.equals("ESS_Auto_Payroll3"))&&(comDBName.equals("ESS_Auto_COM3"))){
            DBManage.sqlExecutor_Main(1013, 1013);
        }
        DBManage.logTestResultIntoDB_Main("Item B1.6", "pass", testSerialNo);

        //Step P5: Setup Super User's email
        SystemLibrary.logMessage("Item B1.7: Setup Admin user contact details.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);  //Launch ESS
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver); //Log on ESS as Super User

        logMessage("Item B1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item B1.7: Setup Admin user contact details.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Item B10011: Scenario Preparation");
        myAssert.assertAll();
    }

    @Test(priority = 10021)
    public void testB10021_WebAPIKeyAndSync() throws InterruptedException, IOException, Exception {
        //Step 2.3
        SystemLibrary.logMessage("*** Start Test B10021.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver); //Log on ESS as Super User

        SystemLibrary.logMessage("Item B1.8", testSerialNo);
        myAssert.assertEquals(addNewWebAPIConfiguration_Main(103, 103, testSerialNo, driver), true, "Failed in Item B1.8: Add API configuration.");

        logMessage("Item B1.9: Sync All", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.syncAllData_Main(103, 103, driver), true, "Failed in Item B1.9: Sync All.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10021.");
        myAssert.assertAll();
    }

    /////////////// Start Team Function test below //////////////////
    @Test(priority = 10031)
    public void testB10031_CreateTeam() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10031.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B2.1", testSerialNo);
        myAssert.assertEquals(validateTeamTable_Main(10011, 10011, testSerialNo, emailDomainName, driver), true, "Failed in Item B2.1: Validate Teams Page.");

        SystemLibrary.logMessage("Item B2.2", testSerialNo);
        myAssert.assertEquals(validateTeamMembers_Main(10021, 10021, testSerialNo, emailDomainName, driver), true, "Failed in Item B2.2: Validate Unassigned Team member.");

        logMessage("Item B2.3", testSerialNo);
        myAssert.assertEquals(GeneralBasic.addTeam("Unassigned", driver), false, "Failed in Item B2.3: Add Unassinged Team. ");

        SystemLibrary.logMessage("Item B2.4", testSerialNo);
        myAssert.assertEquals(GeneralBasic.addTeam("Team Anything", driver), true, "Failed in Item B2.4: Add Team Anything.");

        SystemLibrary.logMessage("Item B2.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(11, 11, testSerialNo, emailDomainName, driver), true, "Failed in Item B2.5: Valdiate Team Anything.");

        SystemLibrary.logMessage("Item B2.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(21, 21, testSerialNo, emailDomainName, driver), true, "Failed in Item B2.6: Validate Team Anything listed.");

        SystemLibrary.logMessage("Item B2.7", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMenuInTeamDirectory("Leave history report;Leave balances report", driver), true, "Failed in Item B2.7: Validate Directory ellipsis menu.");

        SystemLibrary.logMessage("Item B2.8", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMenuInTeamUnassigned("Leave history report;Leave balances report", driver), true, "Failed in Item B2.8: Validate Unassigned ellipsis menu.");

        SystemLibrary.logMessage("Item B2.9", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMenuInTeamPage("Team Anything", "Leave history report;Leave balances report", driver), true, "Failed in Item B2.9: Validate Team Anything ellipsis menu.");

        SystemLibrary.logMessage("Item B2.10", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMenuInTeamsTeamTable("Team Anything", "Add a member;Leave history report;Leave balances report;Rename team;Delete team", driver), true, "Failed in Item B2.10: Validate Teams / Team Anything ellipsis menu.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10031.");
        myAssert.assertAll();

    }

    @Test(priority = 10041)
    public void testB10041_RenameTeamsAndAddTeamMember() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10041");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Item B3.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.renameTeam_Main(31, 31, driver), true, "Failed in Item B3.1: Rename Team to Team A.");

        SystemLibrary.logMessage("Item B3.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(41, 41, testSerialNo, driver), true, "Failed in Item B3.2: Add Member to Team.");

        SystemLibrary.logMessage("Item B3.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(41, 41, testSerialNo, emailDomainName, driver), true, "Failed in Item B3.3: Validate Teams member Table.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10041");

        myAssert.assertAll();
    }

    @Test(priority = 10051)
    public void testB10051_MoveDeleteAddTeamsAndTeamMembers() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10051");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        GeneralBasic.displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21("Team A", "Peter KONG", driver).click();
        Thread.sleep(2000);
        SystemLibrary.logMessage("Item B4.1", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMenu("Move to team;Change role;Remove from team", driver), true, "Failed in Item B4.1: Validate Team Member Elipsis Menu.");

        SystemLibrary.logMessage("Item B4.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(42, 42, testSerialNo, emailDomainName, driver), true, "Failed in Item B4.2: Change member role.");

        SystemLibrary.logMessage("Item B4.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.deleteMultiTeam(45, 45, testSerialNo, emailDomainName, driver), false, "Failed in Item B4.3: Team should NOT be deleted as expected.");

        SystemLibrary.logMessage("Item B4.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiTeamMembers(50, 50, emailDomainName, testSerialNo, driver), true, "Failed in Item B4.4 : Remove Member from Team.");

        SystemLibrary.logMessage("Item B4.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(55, 55, testSerialNo, emailDomainName, driver), true, "Failed in Item B4.5: Validate Team Member Table.");

        SystemLibrary.logMessage("Item B4.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.deleteMultiTeam(60, 60, testSerialNo, emailDomainName, driver), false, "Failed in Item B4.6: Cancel Delete team.");

        SystemLibrary.logMessage("Item B4.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.deleteMultiTeam(65, 65, testSerialNo, emailDomainName, driver), true, "Failed in Item B4.7: Step 51: Delete Team.");

        SystemLibrary.logMessage("Item B4.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(65, 65, driver), true, "Failed in Item B4.8: Step 52: Add Team A.");

        SystemLibrary.logMessage("Item B4.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(65, 65, testSerialNo, driver), true, "Failed in Item B4.9: Step 53: Add Team Member Robind S.");

        SystemLibrary.logMessage("Item B4.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(66, 66, testSerialNo, emailDomainName, driver), true, "Failed in Item B4.10: test Step 54: Validate Team Members after Red-Adding.");

        SystemLibrary.logMessage("Item B4.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(67, 67, testSerialNo, emailDomainName, driver), true, "Failed in Item B4.10: test Step 55: Validate Teams after Re-Adding.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10051");

        myAssert.assertAll();
    }

    @Test(priority = 10061)
    public void testB10061_MoveMemberToTeamWithValidate() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10061");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        //Step 56-59: Move to Team
        SystemLibrary.logMessage("Step 56- Step 59: Move member to Team.");

        logMessage("Item B5.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.moveMemberToTeam_Main(71, 71, testSerialNo, emailDomainName, driver), true, "Failed in Item B5.1: Step 56- Step 59: Move Team member.");

        logMessage("Item B5.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(72, 72, testSerialNo, emailDomainName, driver), true, "Failed in Item B5.2: Step 59_1: Validate Original Team Member after move member.");

        logMessage("Item B5.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(73, 73, testSerialNo, emailDomainName, driver), true, "Failed in Item B5.3: Step 59_2: Validate Target Team Member after move member..");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10061");

        myAssert.assertAll();
    }


    //////////////////////
    //Must run without headless
    @Test(priority = 10071)
    public void testB10071_StepsPreAddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        SystemLibrary.logMessage("*** Start Test B10071.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Item B6.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiPersonalInformation(12, 12, testSerialNo, driver), true, "Failed in Item B6.1: Step 62: Add Preferred Name.");

        SystemLibrary.logMessage("Item B6.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.uploadUserPhoto_Main(21, 21, testSerialNo, driver), true, "Failed in Item B6.1: Step 62_2: Upload Photo for Steve BARRY.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10071");

        myAssert.assertAll();
    }

    @Test(priority = 10081)
    public void testB10081_AddTeamsAndMembers_1() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10081.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Item B7.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(10031, 10031, testSerialNo, emailDomainName, driver), true, "Failed in Item B7.1: Step 63: Validate Team A.");

        SystemLibrary.logMessage("Item B7.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(81, 81, testSerialNo, driver), true, "Failed in Item B7.2: Step 64- Step 65: Add BARRY to Team A.");

        SystemLibrary.logMessage("Item B7.3: Add multiple teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(91, 101, driver), true, "Failed in Item B7.3 Step 66: Add Multi Teams.");

        SystemLibrary.logMessage("Start Item B7.4: Step 66: Add multi Members into Muli Teams. ");
        logMessage("Item B7.4.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(111, 111, testSerialNo, driver), true, "Failed in Item B7.4.1 Step 66 - Row 111. Add Members into Team The Boss");
        logMessage("Item B7.4.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(112, 112, testSerialNo, driver), true, "Failed in Item B7.4.2 Step 66 - Row 112. Add Members into Team The Boss");
        logMessage("Item B7.4.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(113, 113, testSerialNo, driver), true, "Failed in Item B7.4.3Step 66 - Row 113. Add Members into Team The Boss");
        logMessage("Item B7.4.4", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(114, 114, testSerialNo, driver), true, "Failed in Item B7.4.4 Step 66 - Row 114. Add Members into Team The Boss");

        logMessage("Item B7.4.5", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(121, 121, testSerialNo, driver), true, "Failed in Item B7.4.5 Step 66 - Row 121. Add Members into Team B");
        logMessage("Item B7.4.6", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(122, 122, testSerialNo, driver), true, "Failed in Item B7.4.6 Step 66 - Row 122. Add Members into Team B");
        logMessage("Item B7.4.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(123, 123, testSerialNo, driver), true, "Failed in Item B7.4.7 Step 66 - Row 123. Add Members into Team B");
        logMessage("Item B7.4.8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(124, 124, testSerialNo, driver), true, "Failed in Item B7.4.8 Step 66 - Row 124. Add Members into Team B");
        logMessage("Item B7.4.9", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(125, 125, testSerialNo, driver), true, "Failed in Item B7.4.9 Step 66 - Row 125. Add Members into Team B");

        logMessage("Item B7.4.10", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(131, 131, testSerialNo, driver), true, "Failed in Item B7.4.10 Step 66 - Row 131. Add Members into Team C");
        logMessage("Item B7.4.11", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(132, 132, testSerialNo, driver), true, "Failed in Item B7.4.11 Step 66 - Row 132. Add Members into Team C");
        logMessage("Item B7.4.12", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(133, 133, testSerialNo, driver), true, "Failed in Item B7.4.12 Step 66 - Row 133. Add Members into Team C");
        logMessage("Item B7.4.13", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(134, 134, testSerialNo, driver), true, "Failed in Item B7.4.13 Step 66 - Row 134. Add Members into Team C");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10081.");
        myAssert.assertAll();
    }

    @Test(priority = 10091)
    public void testB10091_AddTeamsAndMembers_2() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10091.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Start Step 66: Add multi Members into Muli Teams. ");

        logMessage("Item B7.4.14", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(141, 141, testSerialNo, driver), true, "Failed in Item B7.4.14: Step 66 - Row 141. Add Members into Sub D");
        logMessage("Item B7.4.15", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(142, 142, testSerialNo, driver), true, "Failed in Item B7.4.15: Step 66 - Row 142. Add Members into Sub D");
        logMessage("Item B7.4.16", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(143, 143, testSerialNo, driver), true, "Failed in Item B7.4.16: Step 66 - Row 143. Add Members into Sub D");

        logMessage("Item B7.4.17", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(151, 151, testSerialNo, driver), true, "Failed in Item B7.4.17: Step 66 - Row 151. Add Members into Sub E");
        logMessage("Item B7.4.18", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(152, 152, testSerialNo, driver), true, "Failed in Item B7.4.18: Step 66 - Row 152. Add Members into Sub E");
        logMessage("Item B7.4.19", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(153, 153, testSerialNo, driver), true, "Failed in Item B7.4.19: Step 66 - Row 153. Add Members into Sub E");

        logMessage("Item B7.4.20", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(161, 161, testSerialNo, driver), true, "Failed in Item B7.4.20: Step 66 - Row 161. Add Members into Team F");
        logMessage("Item B7.4.21", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(162, 162, testSerialNo, driver), true, "Failed in Item B7.4.21: Step 66 - Row 162. Add Members into Team F");
        logMessage("Item B7.4.22", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(163, 163, testSerialNo, driver), true, "Failed in Item B7.4.22: Step 66 - Row 163. Add Members into Team F");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10091.");
        myAssert.assertAll();
    }

    @Test(priority = 10101)
    public void testB10101_AddTeamsAndMembers_3() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10101.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Start Step 66: Add multi Members into Muli Teams. ");

        logMessage("Item B7.4.23", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(171, 171, testSerialNo, driver), true, "Failed in Item B7.4.23: Step 66 - Row 171. Add Members into Team G");
        logMessage("Item B7.4.24", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(172, 172, testSerialNo, driver), true, "Failed in Item B7.4.24: Step 66 - Row 172. Add Members into Team G");
        logMessage("Item B7.4.25", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(173, 173, testSerialNo, driver), true, "Failed in Item B7.4.25: Step 66 - Row 173. Add Members into Team G");
        logMessage("Item B7.4.26", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(174, 174, testSerialNo, driver), true, "Failed in Item B7.4.26: Step 66 - Row 174. Add Members into Team G");

        logMessage("Item B7.4.27", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(181, 181, testSerialNo, driver), true, "Failed in Item B7.4.27: Step 66 - Row 181. Add Members into Team H");
        logMessage("Item B7.4.28", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(182, 182, testSerialNo, driver), true, "Failed in Item B7.4.28: Step 66 - Row 182. Add Members into Team H");

        logMessage("Item B7.4.29", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(191, 191, testSerialNo, driver), true, "Failed in Item B7.4.29: Step 66 - Row 191. Add Members into Team This Team I");
        logMessage("Item B7.4.30", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(192, 192, testSerialNo, driver), true, "Failed in Item B7.4.30: Step 66 - Row 192. Add Members into Team This Team I");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10101.");
        myAssert.assertAll();
    }

    @Test(priority = 10111)
    public void testB10111_AddTeamsAndMembers_4() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10111.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B7.4.31", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(201, 201, testSerialNo, driver), true, "Failed in Item B7.4.31: Step 66 - Row 201. Add Members into Team That Team J");
        logMessage("Item B7.4.32", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(202, 202, testSerialNo, driver), true, "Failed in Item B7.4.32: Step 66 - Row 202. Add Members into Team That Team J");
        logMessage("Item B7.4.33", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(203, 203, testSerialNo, driver), true, "Failed in Item B7.4.33: Step 66 - Row 203. Add Members into Team That Team J");

        logMessage("Item B7.4.34", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(211, 211, testSerialNo, driver), true, "Failed in Item B7.4.34: Step 66 Row 211. Add Members into Team Another Team K");
        logMessage("Item B7.4.35", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(212, 212, testSerialNo, driver), true, "Failed in Item B7.4.35: Step 66 Row 212. Add Members into Team Another Team K");

        SystemLibrary.logMessage("End of Step 66: Add Multi Members into Multi Teams.");

        logMessage("Item B8", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(221, 221, testSerialNo, emailDomainName, driver), true, "Failed Item B8: in Step 67: Validate Team Table after adding members.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10111.");
        myAssert.assertAll();
    }

    @Test(priority = 10121)
    public static void testB10121_AddCarminToTeamBAndValidateTeamB() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10121.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        SystemLibrary.logMessage("Step 155 - Step 156: Add Carmin into Team B.");
        logMessage("Item B9.1", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(271, 271, testSerialNo, driver), true, "Failed in Item B9.1: Step 155 - Step 156: Add Carmin into Team B.");

        SystemLibrary.logMessage("Step 156: Validate Team B after adding.");
        logMessage("Item B9.2", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(271, 271, testSerialNo, emailDomainName, driver), true, "Failed in Item B9.2: Step 156: Validate Team B after adding.");

        SystemLibrary.logMessage("Step 157: Validate Team H.");
        logMessage("Item B9.3", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(10071, 10071, testSerialNo, emailDomainName, driver), true, "Failed in Item B9.3: Step 157: Validate Team H.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10121.");
        myAssert.assertAll();
    }

    ///////////////////////// Team Function Test II ///////////////////

    @Test(priority = 10131)
    public static void testB10131_ValidateTeamPageSettings1() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10131.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        ////////////////// Item B10.1 ///////////////
        int errorCounter=0;
        logMessage("Item B10.1: Validate Team Page - Ellipsis Options", testSerialNo);
        GeneralBasic.displayTeamsPage(driver);
        WebElement button_Ellipsis=PageObj_Teams.button_TeamPage_Ellipsis(driver);
        if (button_Ellipsis!=null){
            button_Ellipsis.click();
            Thread.sleep(2000);
            logMessage("Click Elllipsis button in Team Page.");
            logScreenshot(driver);

            WebElement ellipsisMenu=SystemLibrary.waitChild("//div[@id='team-page-controls']//ul[@class='sub-nav show']", 10, 1, driver);
            if (ellipsisMenu!=null){
                if (!SystemLibraryHigh.validateTextInElement_Main(91, 91, ellipsisMenu, testSerialNo, emailDomainName)){
                    errorCounter++;
                }
            }else{
                errorCounter++;
            }
        }else{
            errorCounter++;
        }

        boolean itemResult=false;
        if (errorCounter==0){
            itemResult=true;
        }else{
            itemResult=false;
        }
        myAssert.assertEquals(itemResult, true, "Failed in Item B10.1: Validate Ellipsis Menu in Team Page.");
        //////

        ///////////////////////// Item B10.2 Validate Team Page 3 tick Option //////////////
        errorCounter=0;
        itemResult=false;
        logMessage("Item B10.2: Validate Team Page - 3 Tick Options", testSerialNo);
        GeneralBasic.displayTeamsPage(driver);

        WebElement button_3Options=PageObj_Teams.button_TeamPage_3Options(driver);
        if (button_3Options!=null){
            itemResult=true;
        }else{
            itemResult=false;
        }
        myAssert.assertEquals(itemResult, true, "Failed in Item B10.2: Validate Team Page - 3 Tick Options.");
        //////

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10131.");
        myAssert.assertAll();
    }

    @Test(priority = 10141)
    public static void testB10141_EditAdminRolePermission_NewStarter_Edit() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10141.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B10.3: Edit Role and Permission - New Starter - from Deney to Edit", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.configPermissionStatusNew_Main(331, 331, driver), true, "Falied in Item B10.3: Edit Role and Permission - New Starter - from Deney to Edit.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10141.");
        myAssert.assertAll();
    }

    @Test(priority = 10151)
    public static void testB10151_ValidateTeamPageSettings2() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10151.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);
        String teamName=null;

        logMessage("Item B10.4: Validate Directory Ellipsis Options.", testSerialNo);
        teamName="Directory";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.4: Validate Directory Ellipsis Options.");

        logMessage("Item B10.5: Validate Unassigned Ellipsis Options.", testSerialNo);
        teamName="Unassigned";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.5: Validate Unassgined Ellipsis Options.");

        logMessage("Item B10.6: Validate New Starters Ellipsis Options.", testSerialNo);
        teamName="New starters";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), false, "Failed in Item B10.6: Validate New Starters Ellipsis Options.");

        logMessage("Item B10.7: Validate This Team I Ellipsis Options.", testSerialNo);
        teamName="This Team I";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.7: Validate This Team I Ellipsis Options.");

        logMessage("Item B10.8: Validate Other Teams Ellipsis Options.", testSerialNo);
        teamName="Other Teams";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), false, "Failed in Item B10.8: Validate Other Teams Ellipsis Options.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10151.");
        myAssert.assertAll();
    }

    @Test(priority = 10161)
    public static void testB10161_AddMoreTeamsAndValidateTeamsPageSettings2() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10161.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B10.9: Add new teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(531, 546, driver), true, "Failed in Item B10.9: Add new teams.");

        SystemLibrary.logMessage("Item B10.10: Validate Team page after adding multiple teams.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(551, 551, testSerialNo, emailDomainName,driver), true, "Failed in Item B10.10: Validate Team page after adding multiple teams.");

        logMessage("Item B10.11: Validate new Team Iceland.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(561, 561, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.11: Validate new Team Iceland.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10161.");
        myAssert.assertAll();
    }

    @Test(priority = 10171)
    public static void testB10171_ValidteTeamCheckBox() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10171.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B10.12: Validate All Selected Teams", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(571, 571, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.12: Validate All Selected Teams.");

        logMessage("Item B10.13: Validate Selected Team Iceland, Team C and Team Woy Woy.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(581, 581, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.13: Validate Selected Team Iceland, Team C and Team Woy Woy.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10171.");
        myAssert.assertAll();
    }

    @Test(priority = 10181)
    public static void testB10181_ActivateJenniferH() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10181.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B10.14: Activate Jennifer H", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(107, 107, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item B10.14: Activate Jennifer H.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10181.");
        myAssert.assertAll();
    }

    @Test(priority = 10191)
    public static void testB10191_ValidteJenniferHTeamPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10191.");

        logMessage("Item B10.15: Logon as Jennifer H.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.15: Logon as Jennifer H.");

        ///////////////////////// Item B10.16: Validate Jennifer H Team Page 3 tick Option //////////////
        int errorCounter=0;
        boolean itemResult=false;

        logMessage("Item B10.16: Validate Jennifer H Team Page 3 tick Option.", testSerialNo);
        GeneralBasic.displayTeamsPage(driver);

        WebElement button_3Options=PageObj_Teams.button_TeamPage_3Options(driver);
        if (button_3Options!=null){
            itemResult=true;
        }else{
            itemResult=false;
        }
        myAssert.assertEquals(itemResult, true, "Failed in Item B10.16: Validate Jennifer H Team Page 3 tick Option.");
        //////

        logMessage("Item B10.17: Validate Directory Ellipsis Options.", testSerialNo);
        String teamName="Directory";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), false, "Failed in Item B10.17: Validate Directory Ellipsis Options.");

        logMessage("Item B10.18: Validate Team C Ellipsis Options.", testSerialNo);
        teamName="Team C";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), false, "Failed in Item B10.18: Validate Team C Ellipsis Options.");

        logMessage("Item B10.19: Validate Another Team K Ellipsis Options.", testSerialNo);
        teamName="Another Team K";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.19: Validate Another Team K Ellipsis Options.");

        logMessage("Item B10.20: Validate Jennifer H Team Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(591, 591, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.20: Validate Jennifer Team Page.");

        logMessage("Item B10.21: Validate All Selection Teams via Team Member and Manager.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(601, 601, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.21: Validate All Selection Teams via Team Member and Manager.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10191.");
        myAssert.assertAll();
    }

    @Test (priority=10201)
    public static void testB10201_ImportTeamViaTPU() throws Exception {

        logMessage("*** Start Test B10201: Import Team via TPU.");
        ESSSoftAssert myAssert= new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item B10.22: Import Team members into Team Holland.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"Team_Holland_ModuleB_TeamTest.csv"), true, "Failed in Item B10.22: Import Team members into Team Holland.");

        logMessage("*** End of Test B10201");
        myAssert.assertAll();
    }

    @Test(priority = 10211)
    public static void testB10211_ValidteJenniferHTeamPage() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10211.");

        logMessage("Logon as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, driver);

        ///////////////////////// Item B10.16: Validate Jennifer H Team Page 3 tick Option //////////////

        logMessage("Item B10.23: Validate Directory Holland Ellipsis Options.", testSerialNo);
        String teamName="Holland";
        myAssert.assertEquals(GeneralBasic.validateEllipsisButtonMenuInTeamPageViaAdmin(teamName, testSerialNo, emailDomainName, driver), false, "Failed in Item B10.23: Validate Directory Holland Ellipsis Options.");

        logMessage("Item B10.24: Validate Holland Team Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(611, 611, testSerialNo, emailDomainName, driver), true, "Failed in Item B10.24: Validate Holland Team Member.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10211.");
        myAssert.assertAll();
    }

    @Test(priority = 10221)
    public static void testB10221_ActivatePeterK() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Test B10221.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B11.01: Activate Peter K", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(161, 161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver), true, "Failed in Item B11.01: Activate Peter K");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10221.");
        myAssert.assertAll();
    }

    @Test(priority = 10231)
    public static void testB10231_AddPeterKintoTeamWoyWoyAsMemberAndValidateTeamPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10231.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B11.02: Add Peter K into Team Woy Woy as member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(621, 621, testSerialNo, driver), true, "Failed in Item B11.02: Add Peter K into Team Woy Woy as member.");

        logMessage("Item B11.03: Validate Team Member in Woy Woy", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(631, 631, testSerialNo, emailDomainName, driver), true, "Falied in Item B11.03: Validate Team Member in Woy Woy");

        logMessage("Item B11.03.X1: Validate Team - Directory list.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(651, 651, testSerialNo, emailDomainName, driver), true, "Falied in Item B11.03.X1: Validate Team - Directory list.");

        logMessage("Item B11.03.X2: Validate Top Team Ellipsis Button 8 Menus within Team Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisTopButtonMenuWithinSelectedTeamPageViaAdmin("Woy Woy", testSerialNo, emailDomainName, driver), true, "Failed in Item B11.03.X2: Validate Top Team Ellipsis Button 8 Menus within Team Page.");

        logMessage("Item B11.03.X3: Validate Memeber Ellipsis Button Menus within Team Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateMemberEllipsisButtonMenuWithinSelectedTeamPageViaAdmin("Woy Woy", "Peter", "KONG", testSerialNo, emailDomainName, driver), true, "Failed in Item B11.03.X3: Validate Memeber Ellipsis Button Menus within Team Page.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10231.");
        myAssert.assertAll();
    }

    @Test(priority = 10241)
    public static void testB10241_LogonESSAsTeamMemberAndValidateTeamPage() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10241.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item B11.04: Log on ESS as Peter KONG.", testSerialNo);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        myAssert.assertEquals(logonESSMain(561, 561, payrollDBName, testSerialNo, emailDomainName, driver), true, "Failed in Item B11.04: Log on ESS as Peter KONG.");

        logMessage("Item B11.05: Validate Peter K team Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(641, 641, testSerialNo, emailDomainName, driver), true, "Falied in Item B11.05: Validate Peter K Team Page.");

        logMessage("Item B11.06: Validate Top Team Ellipsis Button 8 Menus within Team Page as Team Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisTopButtonMenuWithinSelectedTeamPageViaAdmin("Woy Woy", testSerialNo, emailDomainName, driver), false, "Failed in Item B11.06: Validate Top Team Ellipsis Button 8 Menus within Team Page as Team Member.");

        logMessage("Item B11.07: Validate Memeber Ellipsis Button Menus within Team Page as Team Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateMemberEllipsisButtonMenuWithinSelectedTeamPageViaAdmin("Woy Woy", "Peter", "KONG", testSerialNo, emailDomainName, driver), false, "Failed in Item B11.07: Validate Memeber Ellipsis Button Menus within Team Page as Team Member.");


        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10241.");
        myAssert.assertAll();
    }

    @Test(priority = 10251)
    public static void testB10251_ValidateSelectedAllTeamFunciton() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10251.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B12.01: Validate Selected All Team - Leave Tab via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(661, 661, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.01: Validate Selected All Team - Leave Tab via Admin.");

        logMessage("Item B12.02: Validate Selected One Team - Leave Tab via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(671, 671, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.02: Validate Selected One Team - Leave Tab via Admin.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10251.");
        myAssert.assertAll();
    }

    @Test(priority = 10261)
    public static void testB10261_ValidateSelected3Teams() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10261.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B12.03: Add Jules N as Manager in Team Victoria.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(681, 681, testSerialNo, driver), true, "Failed in Item B12.03: Add Jules N as Manager in Team Victoria.");

        logMessage("Item B12.04: Validate Selected 3 Teams - Unassigned, Japan and Victoria via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(691, 691, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.04: Validate Selected 3 Teams via Admin.");

        logMessage("Item B12.05: Validate Top Ellipis Menu in selection team screen. ", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisTopButtonMenuInViewSelectedTeamPageViaAdmin("Unassigned;Paris;Victoria", testSerialNo, emailDomainName, driver), true, "Failed in Item B12.05: Validate Top Ellipis Menu in selection team screen. ");


        logMessage("Item B12.06: Validate Members Ellipsis Menu in View Selected Team Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasic.validateEllipsisMemberButtonMenuInViewSelectedTeamPageViaAdmin("Unassigned;Paris;Victoria", "Jules NEWBY;Marcia TRANS", testSerialNo, emailDomainName, driver), true, "Failed in Item B12.06: Validate Members Ellipsis Menu in View Selected Team Page.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10261.");
        myAssert.assertAll();
    }

    @Test (priority = 10271)
    public static void testB10271_ChangeMoveUserTeamRoles() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10271.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);
        //GeneralBasic.logonESS_Debug("Testac333.Admin", "N%l$ZzQ3", driver);

        logMessage("Item B12.07: Change Jules N team role in Team Victoria from Manager to Member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(701, 701, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.07: Change Jules N team role in Team Victoria from Manager to Member.");

        logMessage("Item B12.08: Validate Victoria Team List after change Manager role as team member.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(711, 711, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.08: Validate Victoria Team List after change Manager role as team member.");

        logMessage("Item B12.09: Change Marcia TRANS role in Unassigned Team as Team Mananger (Negative Testing).", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.changeMultiMemberRole_ViaTeamsPage(721, 721, testSerialNo, emailDomainName, driver), false, "Failed in Item B12.09: Change Marcia TRANS role in Unassigned Team as Team Manager (Negative Testing).");

        logMessage("Item B12.10: Move Marcia TRANS to Team Newcastle.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.moveMemberToTeam_Main(731, 731, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.10: Move Marcia TRANS to Team Newcastle.");

        logMessage("Item B12.11: Validate Members in Team Newcastle.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(741, 741, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.11: Validate Members in Team Newcastle.");

        logMessage("Item B12.12: Validate Members in Team Unassigned.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(751, 751, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.11: Validate Members in Team Unassigned.");

        logMessage("Item B12.13: Remove Marcia T from Team Newcastle.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.removeMultiTeamMembers(761, 761, emailDomainName, testSerialNo, driver), true, "Failed in Item B12.13: Remove Marcia T from Team Newcastle.");

        logMessage("Item B12.14: Validate Members in Team Newcastle after remove member from Team Newcastle.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(771, 771, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.14: Validate Members in Team Newcastle after remove member from Team Newcastle.");

        logMessage("Item B12.15: Validate Members in Team Unassigned after remove member from Team Newcastle.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(781, 781, testSerialNo, emailDomainName, driver), true, "Failed in Item B12.15: Validate Members in Team Unassigned after remove member from Team Newcastle.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10271.");
        myAssert.assertAll();
    }

    @Test (priority = 10281)
    public static void testB10281_ImportLeaveViaTPU() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10281.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item B13.01: Import Leave via TPU.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importLeaveViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"leave_import_Test.csv"), true, "Failed in Item B13.01: Import Leave Via TPU.");

        SystemLibrary.logMessage("*** End of Test B10281.");
        myAssert.assertAll();
    }

    @Test(priority = 10291)
    public static void testB10291_ValidateLeaveAuditReportAfterImportLeaveViaTPU() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10291.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        //WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B13.02: Validate Leave Application Audit Report after import Leave via TPU.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(441, 441, emailDomainName, testSerialNo, driver), true, "Failed in Item B13.02: Validate Leave Application Audit Report after import TPU.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10291.");
        myAssert.assertAll();
    }

    @Test(priority = 10301)
    public static void testB10301_ValidateTeamPageViaManager() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10301.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Logon as Jennifer H.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(10101, 10101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B14: Validate Selected All Team via Team Manager Jennifer H with View All funciton.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_Main(791, 791, testSerialNo, emailDomainName, driver), true, "Failed in Item B14: Validate Selected All Team via Team Manager Jennifer H with View All funciton.");

        logMessage("Item B15: Validate Leave Application in View selected Team page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateViewSelectedTeamsOnLeave_Main(801, 801, testSerialNo, emailDomainName, driver), true, "Failed in Item B15: Validate Leave Application in View selected Team page.");

        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10301.");
        myAssert.assertAll();
    }

















    ///////////////////////// Mass Activation //////////////

    @Test (priority = 10341)
    public static void testB10341_ImportTeamViaTPU() throws Exception {
        SystemLibrary.logMessage("*** Start Test B10341.");
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("Item B20.1.X: Import Team via TPU.", testSerialNo);
        myAssert.assertEquals(WiniumLib.importTeamViaTPU(testSerialNo, null, null, null, SystemLibrary.dataSourcePath+"Team_MassActivation_ModuleB_Team.csv"), true, "Failed in Item B20.1.X: Import Team via TPU.");

        SystemLibrary.logMessage("*** End of Test B10341.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10351)
    public static void testB110351_ValidteActivationReport_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test B10351.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B20.1: Download and validate Activation Report as the Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30101, 30101, emailDomainName, testSerialNo, driver), true, "Failed in Item B20.1: Download and validate Activation Report as the Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test B10351.");
        myAssert.assertAll();
    }


    @Test(priority = 10361)
    public static void testB10361_ValidteTeamPageViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test B10361.");

        logMessage("Logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS,  SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B20.2: Validate Team Page Via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamTable_Main(851, 851, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.2: Validate Team Page Via Admin.");

        logMessage("Item B20.3: Click View More in the Teams page and Validate All teams Selected.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateTeamMembers_Main(861, 861, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.3: Click View More in the Teams page and Validate All teams Selected.");

        logMessage("Item B20.4: Validate All Selected Teams except Team Uganda.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateAllSelectedTeams_UncheckAnyTeam_Main(871, 871, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.4: Validate All Selected Teams except Team Uganda.");

        logMessage("Item B20.5: Validate Checkboxes when Select to Activate Members in Teams Page.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateCheckboxes_SelectActivateMembers_Main(881, 881, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.5: Validate Checkboxes when Select to Activate Members in Teams Page.");

        logMessage("Item B20.6: Validate Popup message - Select to Activate Members.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectActivateMembersPopup_Main(891, 891, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.6: Validate Popup message - Select to Activate Members.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10361.");
        myAssert.assertAll();
    }

    @Test(priority = 10371)
    public static void testB10371_PerformMassActivationAndValidteTeamPageViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test B10241.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B20.7: Perform Mass Activation via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.massActivateMembers_Teams_Main(901, 901, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.7: Perform Mass Activation via Admin.");

        logMessage("Item B20.8: Validate after Mass Activation via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.validateSelectedTeams_AfterMassActivateMembers_Main(911, 911, testSerialNo, emailDomainName, driver), true, "Failed in Item B20.8: Validate after Mass Activation via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10371.");
        myAssert.assertAll();
    }

    //Must run without headless
    @Test(priority = 10381)
    public static void testB10381_ValidteActivationReport_ViaAdmin() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);
        SystemLibrary.logMessage("*** Start Test B10381.");

        Thread.sleep(2000);
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Logon as Admin.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, 3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item B20.9: Download and validate Activation Report after mass activation via Admin.", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.downloadMultiAuditReportViaDashboard(30111, 30111, emailDomainName, testSerialNo, driver), true, "Failed in Item B20.9: Download and validate Activation Report after mass activation via Admin.");

        GeneralBasic.signoutESS(driver);
        driver.close();

        SystemLibrary.logMessage("*** End of Test B10381.");
        myAssert.assertAll();
    }






    @Test (priority=20001)
    public static void endOfTest() throws  Exception{
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        DBManage.logTestResultIntoDB_Main("Item BBB", "Completed", testSerialNo);
        sendEmail_ESSRegTestComplete_Notificaiton(testEmailNotification, testRoundCode, testSerialNo, moduleName, moduleFunctionName);

        logMessage("End of Test Module B - Team Function test.");
    }

    //////////////////////// Debug here ////////////////////

    @Test
    public static void testB10181_ActivatePeterK() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);


        SystemLibrary.logMessage("*** Start Test B10181.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        //logMessage("Activate Peter K", testSerialNo);
        //GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(161, 161, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);

        logMessage("Activate Gregory S", testSerialNo);
        GeneralBasicHigh.activateMultiUserAccount_ViaAdmin(113, 113, payrollDBName, testSerialNo, emailDomainName, url_ESS, driver);


        //////
        GeneralBasic.signoutESS(driver);
        driver.close();
        SystemLibrary.logMessage("*** End of Test B10181.");
        myAssert.assertAll();
    }

}
