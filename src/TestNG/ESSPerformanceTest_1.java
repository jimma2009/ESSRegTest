package TestNG;

import Lib.*;
import PageObject.PageObj_Teams;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static Lib.DBManage.sqlExecutor_WithCustomizedSQL_Main;
import static Lib.SystemLibrary.clickViewmoreButtonInTable;
import static Lib.SystemLibrary.logMessage;


public class ESSPerformanceTest_1 {

    public static final int threadPool=2;
    public static final int threadCount=2;

    private static String testSerialNo ="xxxxx";
    private static String emailDomainName ="xxx.xxx.xxx";
    private static String payrollDBName="XXXXXX";

    @Test(priority = 0)
    public static void initializePerformanceTest() throws Exception {
        SoftAssert myAssert = new SoftAssert();

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all testing data in DB.");
        String strSQL="Delete from perfTestResult";
        sqlExecutor_WithCustomizedSQL_Main(1001, 1001, strSQL);

    }

    @Test(invocationCount = threadCount, priority = 101)
    public static void PerformanceTest_LogonESS_1() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        stopWatch.start();
        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to logon ESS.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Logon ESS 1", threadID.intValue(), timeSpand.intValue(), "Logon ESS 1.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }


    @Test(threadPoolSize=threadPool, invocationCount=threadCount, priority = 102)
    public static void PerformanceTest_LogonESS_2() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        stopWatch.start();
        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to logon ESS.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Logon ESS 2", threadID.intValue(), timeSpand.intValue(), "Logon ESS 1.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test(invocationCount = threadCount, priority = 103)
    public static void PerformanceTest_DisplayEmployeePersonalDetail_1() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver);
        stopWatch.start();
        GeneralBasic.searchUserAndDisplayPersonalInformationPage_ViaAdmin_Performance("Andrew", "Andrew", null, "Apple", null, driver);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to Display Personal Imformation.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Display Personal Information 1", threadID.intValue(), timeSpand.intValue(), "PerformanceTest_DisplayEmployeePersonalDetail_1");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test(threadPoolSize=threadPool, invocationCount = threadCount, priority = 104)
    public static void PerformanceTest_DisplayEmployeePersonalDetail_2() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver);
        stopWatch.start();
        GeneralBasic.searchUserAndDisplayPersonalInformationPage_ViaAdmin_Performance("Andrew", "Andrew", null, "Apple", null, driver);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to Display Personal Imformation.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Display Personal Information 2", threadID.intValue(), timeSpand.intValue(), "PerformanceTest_DisplayEmployeePersonalDetail_2");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test(invocationCount = threadCount, priority = 105)
    public static void PerformanceTest_DisplayUnassignedTeam_1() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName,  testSerialNo, emailDomainName, driver);

        GeneralBasic.displayTeamsPage_Performance(driver);
        stopWatch.start();
        PageObj_Teams.displayTeamMembers_Performance("Unassigned", driver);

        clickViewmoreButtonInTable(driver);

        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to Display All Unssigned Team members.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Display Unassinged Team Members 1", threadID.intValue(), timeSpand.intValue(), "PerformanceTest_DisplayUnssignedTeamMembers_1");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test(threadPoolSize=threadPool, invocationCount = threadCount, priority = 106)
    public static void PerformanceTest_DisplayUnassignedTeam_2() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        GeneralBasicHigh.logonESSMain(1001, 1001, payrollDBName, testSerialNo, emailDomainName, driver);

        GeneralBasic.displayTeamsPage_Performance(driver);
        stopWatch.start();
        PageObj_Teams.displayTeamMembers_Performance("Unassigned", driver);

        clickViewmoreButtonInTable(driver);

        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to Display All Unssigned Team members.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Display Unassinged Team Members 2", threadID.intValue(), timeSpand.intValue(), "PerformanceTest_DisplayUnssignedTeamMembers_1");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test (priority = 1001)
    public static void reportPerformance() throws InterruptedException, IOException, SQLException, ClassNotFoundException {
        boolean isDone=false;
        int errorCounter=0;
        String fileFullName="C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult_"+SystemLibrary.getCurrentTimeAndDate()+".xlsx";
        String sheetName="PerformanceResult";

        int actualTiming=0;
        int threadTimesCount=0;
        ResultSet resultSet=null;

        SystemLibrary.copyFile("C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", fileFullName);

        ExcelDataWriter.exportPerformanceResultIntoExcel("Logon ESS 1", 44119, 5000, 1, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Logon ESS 2", 44711, 5000, 2, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Display Personal Information 1", 17604, 5000, 3, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Display Personal Information 2", 18417, 5000, 4, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Display Unassinged Team Members 1", 121609, 5000, 5, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Display Unassinged Team Members 2", 122277, 5000, 6, 0, fileFullName, sheetName);


        SystemLibrary.logMessage("Performance Report is generated as below \n");
        SystemLibrary.logMessage(fileFullName);
        if (errorCounter==0) isDone=true;

    }





}
