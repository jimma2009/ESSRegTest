package TestNG;

import Lib.*;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static Lib.DBManage.sqlExecutor_WithCustomizedSQL_Main;
import static Lib.SystemLibrary.logMessage;

public class ESSPerformanceTest_2 {


    @BeforeClass
    public static void initializePerformanceTest() throws Exception {
        SoftAssert myAssert = new SoftAssert();

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all testing data in DB.");
        String strSQL="Delete from perfTestResult";
        sqlExecutor_WithCustomizedSQL_Main(1001, 1001, strSQL);

    }

    @Test(priority = 101)
    public static void PerformanceTest_ApplyLeave_1() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        stopWatch.start();
        logMessage("Apply Leave 2");
        Thread.sleep(1000);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to logon ESS.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Apply Leave 2", threadID.intValue(), timeSpand.intValue(), "Apply Leave 1.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @Test(priority = 102)
    public static void PerformanceTest_ApplyLeave_2() throws Exception {

        SoftAssert myAssert=new SoftAssert();
        StopWatch stopWatch=new StopWatch();

        WebDriver driver = GeneralBasic.launchESS_Performance(3);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        stopWatch.start();
        logMessage("Apply Leave 2");
        Thread.sleep(1000);
        stopWatch.split();
        stopWatch.stop();

        Long timeSpand=stopWatch.getTime();
        Long threadID=Thread.currentThread().getId();

        logMessage("It takes "+String.valueOf(timeSpand)+" to logon ESS.");
        //exportPerformanceTestResultIntoExcel("Debug test - Thread "+String.valueOf(threadID), "C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", "User", 6, 0, timeSpand, 1000, 1000);
        DBManage.exportPerformanceResultIntoDB("Apply Leave 2", threadID.intValue(), timeSpand.intValue(), "Apply Leave 2.");

        GeneralBasic.signoutESS(driver);
        driver.close();
        myAssert.assertAll();
    }

    @AfterSuite
    public static void reportPerformance() throws InterruptedException, IOException, SQLException, ClassNotFoundException {
        boolean isDone=false;
        int errorCounter=0;
        String fileFullName="C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult_"+ SystemLibrary.getCurrentTimeAndDate()+".xlsx";
        String sheetName="PerformanceResult";

        int actualTiming=0;
        int threadTimesCount=0;
        ResultSet resultSet=null;

        SystemLibrary.copyFile("C:\\TestAutomationProject\\ESSRegTest\\PerformanceResult\\PerformanceResult.xlsx", fileFullName);

        ExcelDataWriter.exportPerformanceResultIntoExcel("Apply Leave 1", 1000, 5000, 7, 0, fileFullName, sheetName);
        ExcelDataWriter.exportPerformanceResultIntoExcel("Apply Leave 2", 1000, 5000, 8, 0, fileFullName, sheetName);

        SystemLibrary.logMessage("Performance Report is generated as below \n");
        SystemLibrary.logMessage(fileFullName);
        if (errorCounter==0) isDone=true;

    }
}
