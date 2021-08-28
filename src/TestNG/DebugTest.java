package TestNG;

import Lib.*;
import autoitx4java.AutoItX;
//import javafx.scene.layout.Priority;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.concurrent.TimeUnit;

import static Lib.SystemLibrary.*;


public class DebugTest {

    private static String testSerialNo ="xxxxx";
    private static String emailDomainName ="xxx.xxx.xxx";
    private static String payrollDBName="XXXXXX";
    private static String url_ESS="XXXXX";


    @Test(priority = 11771)
    public static void test11771_PreConfigureNZDB() throws Exception {
        SoftAssert myAssert = new SoftAssert();
        SystemLibrary.logMessage("*** Start Test 11771.");

        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        SystemLibrary.logMessage("Start Deleting Payroll and Common DB.");
        myAssert.assertEquals(DBManage.deleteMultiDB(10001, 10002), true, "Failed Deleting Payroll and commond db.");

        //Step P1: Restore Payroll DB
        SystemLibrary.logMessage("Before Step 743_1: Start restoring NZ Payroll and Common DB.");
        myAssert.assertEquals(DBManage.restoreMultiDB(10001, 10002), true, "Failed in Step 743_1: Restoring Payroll and Common db.");
        SystemLibrary.logMessage("End of restoring payroll DB.");

        //Assing the rights to DB after restoring.
        logMessage("Step 743_2: Assign User rights after restore NZ DB.");
        DBManage.sqlExecutor_Main(301, 301);
        DBManage.sqlExecutor_Main(302, 302);

        //Step P2: Update employee email address
        SystemLibrary.logMessage("Step 743_3: Start update email address and change email type as work email.");
        //Without update email type
        GeneralBasic.updateEmployeeEmailInSageMicropayNZDB(testSerialNo, emailDomainName);
        SystemLibrary.logMessage("Step p2: End of updating email address.");

        //Step P3: Remove data from staging table
        SystemLibrary.logMessage("Step P3: Remove date from staging table.");
        DBManage.sqlExecutor_Main(304, 304);

        SystemLibrary.logMessage("*** End of Test 11771.");
        myAssert.assertAll();
    }

    @Test (priority = 11781)
    public static void test11781_ConfigureWebAPIForNZDB() throws Exception {

        SoftAssert myAssert = new SoftAssert();
        SystemLibrary.logMessage("*** Start Test 11781.");

        ////////////////// Configure Web API below ////////////////////


        logMessage("End of Test 11781");
        myAssert.assertAll();
    }

    @Test (priority = 11791)
    public static void test11791() throws Exception{
        SoftAssert myAssert=new SoftAssert();
        SystemLibrary.logMessage("*** Start Test 11791.");

        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);
/*

        logMessage("Step 744: Validate Integration Page - Web API Form.");
        myAssert.assertEquals(GeneralBasicHigh.validateIntegrationPage_Main(10161, 10161, driver), true, "Failed in Step 744: Validate Integration Page - Web API Form.");
*/

        SystemLibrary.logMessage("Step 745 - Step 746: Add new Web API Key for NZ DB.");
        myAssert.assertEquals(GeneralBasicHigh.addNewWebAPIConfiguration_Main(101, 101, testSerialNo, driver), true, "Failed in Step 7_1: add New Web API Key in Step 6.");



        GeneralBasic.signoutESS(driver);
        driver.close();
        logMessage("*** End of Test 11791.");
        myAssert.assertAll();

    }




    @Test
    public static void test2() throws Exception {
        logMessage("*** Start Test 2");
        SoftAssert myAssert = new SoftAssert();

        AutoItX x=new AutoItX();
        String messageText=x.controlGetText("Sign in to Sage MicrOpay", "", "[CLASS:TPanel; INSTANCE:5]");
        logMessage(messageText);

        myAssert.assertAll();
    }

    @Test
    public static void Test3() throws Exception {

        String origianlItem="012345F_SN123abc_204017_04032019,,L_SN123abc_204017_04032019,,,,\n" +
                "Administrator ,Administrator,,,TestSN123abc.Admin,,Last,,0298844071,0423666000,AdminTesterPersonal99@sageautomation.com\n" +
                "Member,Member,Another Team K,EMP26,Ace,,F_SN123abc_204017_04032019,HARRY,BDM - Business De";
        String subItem="F_SN123abc";
        int subItemLength=26;
        String replacementItem="12345678901234567890123456";
        String outputItem=replaceItemInString(origianlItem, subItem, subItemLength, replacementItem);
        logMessage(origianlItem);
        logMessage(outputItem);
    }

    public static String replaceItemInString(String origianlItem, String subItem, int length, String replacementItem) throws Exception {
        SoftAssert myAssert=new SoftAssert();
        int itemCount=0;
        String a=origianlItem;
        //int i=a.indexOf("_SN123abc_");
        int i=a.indexOf(subItem);
        while (i!=-1){
            itemCount++;
            String stringToBeReplace=a.substring(i, i+length);
            myAssert.assertAll();
            a=a.replaceFirst(stringToBeReplace, replacementItem);
            i=a.indexOf(subItem);
        }
        logMessage(itemCount+" replacement(s).");
        return a;
    }

}