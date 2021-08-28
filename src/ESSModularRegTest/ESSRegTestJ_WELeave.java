package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasicHigh.*;
import static Lib.GeneralBasicHightWE.openWEDB_Main;
import static Lib.GeneralBasicWE.editWE_ESSConfiguration;
import static Lib.GeneralBasicWE.launchWageEasy;
import static Lib.SystemLibrary.logMessage;

public class ESSRegTestJ_WELeave {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;
    private static int moduleNo=110;
    private static String url_ESS;

    private static String moduleName="J";

    static {
        try {
            testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 10011) //a
    public void testJ10011_ScenarioPrepare() throws Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        SystemLibrary.logMessage("*** Start Item J10011: Scenario Preparation");

        /*logMessage("Clear up memory");
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");

        logMessage("Delete all mail.");
        JavaMailLib.deleteAllMail(emailDomainName);

        //////////////////////////// Delete and Restore DB  //////////////////////////
        logMessage("Item J1.3: Open Wage Easy DB.", testSerialNo);
        launchWageEasy();
        myAssert.assertEquals(openWEDB_Main(2001, 2001), true, "Failed in Item J1.3: Open Wage Easy DB.");

        logMessage("Item J1.4: Configure ESS.", testSerialNo);
        myAssert.assertEquals(editWE_ESSConfiguration(testSerialNo), true, "Failed in Item J1.4: Configure ESS.");
        */
        logMessage("Item J1.5: Update all employees email.");
        myAssert.assertEquals(DBManage_Access.updateEmployeeEmail(testSerialNo, emailDomainName), true, "Failed in Item J1.5: Update all employees email.");

        /*//Assing the rights to DB after restoring.
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
        SystemLibrary.logMessage("Item G1.6: Remove date from staging table.");
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
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver); //Log on ESS as Super User

        logMessage("Item G1.7", testSerialNo);
        myAssert.assertEquals(GeneralBasicHigh.editMultiContactDetails(11, 11, payrollDBName, testSerialNo, emailDomainName, driver), true, "Faied in Item G1.7: Setup Admin user contact details.");

        GeneralBasic.signoutESS(driver);
        driver.close();
*/
        SystemLibrary.logMessage("*** End of Item G10011: Scenario Preparation");
        myAssert.assertAll();
    }
}
