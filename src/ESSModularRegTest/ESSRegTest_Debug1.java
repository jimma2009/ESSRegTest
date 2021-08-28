package ESSModularRegTest;

import Lib.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static Lib.DBManage.getSmokeTestStatus;
import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.getCurrentTestSerialNumber;
import static Lib.GeneralBasicHigh.getTestKeyConfigureFromDatasheet_Main;
import static Lib.SystemLibrary.logMessage;

public class ESSRegTest_Debug1 {

    private static String testSerialNo;
    private static String emailDomainName;
    private static String payrollDBName;
    private static String comDBName;

    private static String payrollDBOrderNumber;
    private static String url_ESS;

    ///////////////////// New Field ////////////////
    private static String moduleFunctionName;
    private static String testRoundCode;
    private static String emailAddressForTestNotification;
    //////

    private static int moduleNo = 101;
    private static String moduleName="A";


    static {
        try {
            //testSerialNo = getCurrentTestSerialNumber(moduleNo, moduleName);
            emailDomainName =getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailDomainName");
            payrollDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBName");
            comDBName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "comDBName");
            url_ESS=getTestKeyConfigureFromDatasheet_Main(moduleNo, "url_ESS");
            payrollDBOrderNumber=getTestKeyConfigureFromDatasheet_Main(moduleNo, "payrollDBOrderNo");
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");

            if (payrollDBOrderNumber==null){
                payrollDBOrderNumber="1";
            }

            //////////////// New Field /////////////
            testRoundCode =getTestKeyConfigureFromDatasheet_Main(moduleNo, "testRoundCode");
            moduleFunctionName=getTestKeyConfigureFromDatasheet_Main(moduleNo, "moduleFunctionName");
            emailAddressForTestNotification=getTestKeyConfigureFromDatasheet_Main(moduleNo, "emailAddressForTestNotification");
            ///////


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public static void test1() throws InterruptedException, SQLException, ClassNotFoundException {
        logMessage("Hi");

        //logMessage(getTestResultFromDB("Item A6.2", "sn228"));
        int testStatus=getSmokeTestStatus();
        switch (testStatus){
            case 0:
                logMessage("No test required.");
                break;
            case 1:
                logMessage("New test started.");
                try {
                    Process process = Runtime.getRuntime().exec("cmd /c start C:\\temp\\test1.bat");
                    OutputStream outputStream=process.getOutputStream();

                    System.out.println(outputStream.toString());
                    Thread.sleep(5000);
                    process.destroy();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                logMessage("Terminate test.");
                break;
        }
    }



    @Test
    public static void ManageDBs() throws InterruptedException, UnknownHostException {
        int dbRowNumber_Start=101;
        int dbRowNumber_End=102;

        DBManage.deleteMultiDB(dbRowNumber_Start, dbRowNumber_End);
        DBManage.restoreMultiDB(dbRowNumber_Start, dbRowNumber_End);
        //DBManage.backupMultiDB(dbRowNumber_Start, dbRowNumber_End);
    }


    @Test
    public static void test11() throws InterruptedException {
        logMessage("Hello Java!");
    }


    @Test(priority = 10042)
    public void testE10042_AddTeamsAndMembers() throws IOException, InterruptedException, Exception {
        ESSSoftAssert myAssert = new ESSSoftAssert();
        if (testSerialNo==null) testSerialNo=getCurrentTestSerialNumber(moduleNo, moduleName);

        logMessage("*** Start Test E10042: Add Teams and Members.");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, SystemLibrary.driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasicHigh.logonESSMain(101, 101, payrollDBName, testSerialNo, emailDomainName, driver);

        logMessage("Item E3.1: Add multi Members into Teams.", testSerialNo);
     //   myAssert.assertEquals(GeneralBasicHigh.addMultiTeam(31111, 31122, driver), true, " ");
        myAssert.assertEquals(GeneralBasicHigh.addTeamMember_Main(31142, 31142, testSerialNo, driver), true, "Failed in Item E3.1: Add multi Members into Teams.");

        signoutESS(driver);
        driver.close();
        logMessage("*** End of Test E10042: Add Teams and Members.");
        myAssert.assertAll();
    }

}
