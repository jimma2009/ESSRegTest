package Lib;
import autoitx4java.AutoItX;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import winium.elements.desktop.*;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static Lib.AutoITLib.*;
import static Lib.SystemLibrary.*;

public class WiniumLib {


    @Test
    public static void test1() throws MalformedURLException, InterruptedException {
        DesktopOptions desktopOptions=new DesktopOptions();
        desktopOptions.setApplicationPath("C:\\Program Files (x86)\\Sage MicrOpay\\Evolution.exe");


        WiniumDriver driver = new WiniumDriver(new URL("Http://localhost:9999"), desktopOptions);
        Thread.sleep(5000);

        driver.findElement(By.name("Sign in")).click();;
        //driver.findElement(By.className(""))
        Thread.sleep(5000);
        logMessage("Sign in button is clicked.");

    }

    public static boolean launchWiniumService() throws IOException, InterruptedException {
        Process p=new ProcessBuilder("C:\\TestAutomationProject\\Winium\\Winium.Desktop.Driver.exe").start();
        Thread.sleep(5000);
        logMessage("Winium Driver Service is started.");
        return true;
    }

    public static boolean closeWiniumService() throws Exception {
        SystemLibrary.executeDOSCommand("Taskkill /IM Winium.Desktop.Driver.exe /F");
        Thread.sleep(5000);
        logMessage("Winium Driver Service is closed.");
        return true;
    }

    public static WiniumDriver launchTPU() throws Exception{
        DesktopOptions desktopOptions=new DesktopOptions();
        desktopOptions.setApplicationPath("C:\\TestAutomationProject\\ESSRegTest\\TPU\\TPU.bat");

        WiniumDriver driver = new WiniumDriver(new URL("Http://localhost:9999"), desktopOptions);
        Thread.sleep(5000);

        logMessage("TPU Tool is launched.");
        return driver;
    }

    public static boolean createAdminExternalUserViaTPU() throws  Exception {
        boolean isDone=false;
        int errorCounter=0;

        String tenantKey = GeneralBasic.getTenantKey_FromTenantDetails();
        String testSerialNumber=GeneralBasic.getTestSerailNumber_Main(10,10);

        String timeStamp=getCurrentTimeAndDate();

        String username="U_"+testSerialNumber+"_"+timeStamp;
        String firstName="F_"+testSerialNumber+"_"+timeStamp;
        String lastName="L_"+testSerialNumber+"_"+timeStamp;
        String email="FL_"+testSerialNumber+"_"+timeStamp+"@sageautomation.com";

        WiniumLib.launchWiniumService();
        WiniumDriver driver=launchTPU();
        logScreenshotX();

        AutoItX x = new AutoItX();

        String tpuWinTitle = "Sage ESS Tenant Provisioning Utility";

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 666, 57);
        x.sleep(3000);
        logMessage("Tab - Admin User Creatation tab is clicked.");
        logScreenshotX();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 369);
        x.send(tenantKey);
        logMessage("Tenant Key '"+tenantKey+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 400);
        x.send(username);
        logMessage("UserName '"+username+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 423);
        x.send(firstName);
        logMessage("First Name '"+firstName+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 453);
        x.send(lastName);
        logMessage("Last Name '"+lastName+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 190, 484);
        x.send(email);
        logMessage("Email '"+email+"' is input.");

        logMessage("Screenshot before click Create button.");
        logScreenshotX();

        SystemLibrary.clearClipboard();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 731, 508);
        x.sleep(3000);
        logMessage("Button - Create is clicked.");
        logScreenshotX();

        if (x.winWait("ESS TPU", "Error occured. Please check the log", 5)){
            x.controlFocus("ESS TPU", "OK", "[CLASS:Button; INSTANCE:1]");
            x.controlClick("ESS TPU", "OK", "[CLASS:Button; INSTANCE:1]");
            x.sleep(5000);
            logMessage("OK button is clicked.");
            errorCounter++;
        }


        String fileName="ExtraAdminUser_"+GeneralBasic.getTestSerailNumber_Main(11, 11)+"_"+getCurrentTimeAndDate()+".txt";
        String fileFullPathName=projectPath+"UserList\\"+fileName;

        String logText="Firstname : "+firstName+"\n";
        logText=logText+"Lastname : "+lastName+"\n";

        logText=logText+driver.findElement(By.xpath("//*[@AutomationId='rtxtLog']")).getText();
        logMessage(logText);
        if (logText.contains("The USERNAME already exists")){
            logError("The USERNAME '"+username+"' already exists");
            errorCounter++;
        }else{

            createTextFile(fileFullPathName, logText);
            logMessage("Extra Admin detail is saved as '"+fileFullPathName+"'.");
            logMessage("Click here to access the Extra Admin Detail: '"+serverUrlAddress+"/userlist/"+fileName);

            /////////////// Backup old tenant file if existing ////////////
            moveFile(projectPath+"UserList\\ExtraAdminUser.txt", projectPath+"UserList\\ExtraAdminUser_Backup_"+getCurrentTimeAndDate()+".txt" );
            copyFile(fileFullPathName, projectPath+"UserList\\ExtraAdminUser.txt");

        }


        if (errorCounter==0) isDone=true;

        exitTPU();
        closeWiniumService();
        return isDone;
    }

    public static boolean generateESSTenant(String testSerialNo) throws Exception {
        boolean isDone=false;
        String fileFullPathName=null;
        SoftAssert myAssert=new SoftAssert();

        WiniumLib.launchWiniumService();
        WiniumDriver driver=launchTPU();
        logScreenshotX();

        AutoItX x=new AutoItX();
        String tpuWinTitle="Sage ESS Tenant Provisioning Utility";

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 223, 398);
        x.send("AutoTest");
        logMessage("Company Name 'AutoTest' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 219, 424);
        String customerID="Test"+testSerialNo;
        x.send(customerID);
        logMessage("Customer ID '"+customerID+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 220, 503);
        x.send("Last");
        logMessage("Last Name 'Last' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 233, 532);
        String email="testlast"+testSerialNo+"@sageautomation.com";
        x.send(email);
        logMessage("Email Address '"+email+"' is input.");

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 233, 558);
        String initial=testSerialNo.substring(0, 2);
        String lastNumber=testSerialNo.replace(initial, "");

        String phoneNumber=lastNumber+lastNumber+lastNumber;
        x.send(phoneNumber);
        logMessage("Phone Number '"+phoneNumber+"' is input.");

        logMessage("Screenshot before click Button - Create ESS Customer.");
        logScreenshotX();

        x.winActivate(tpuWinTitle);
        AutoITLib.click_Window(tpuWinTitle, 690, 589);
        x.sleep(25000);
        logMessage("Button - Create ESS Customer is clicked.");
        logScreenshotX();

        String logText=driver.findElement(By.xpath("//*[@AutomationId='textBoxInfo']")).getText();
        Thread.sleep(5000);
        String fileName="Test"+testSerialNo+"_"+getCurrentTimeAndDate()+".txt";

        fileFullPathName=projectPath+"UserList\\"+fileName;
        createTextFile(fileFullPathName, logText);
        logMessage("Tenant detail is saved as '"+fileFullPathName+"'.");
        logMessage("Click here to access the Tenant Detail: '"+serverUrlAddress+"/userlist/"+fileName);

        /////////////// Backup old tenant file is not required ////////////
        //moveFile(projectPath+"UserList\\TenantDetails.txt", projectPath+"UserList\\TenantDetails_Backup_"+getCurrentTimeAndDate()+".txt" );
        copyFile(fileFullPathName, projectPath+"UserList\\TenantDetails_"+testSerialNo+".txt");
        copyFile(fileFullPathName, testKeysPath+"TenantDetails_"+testSerialNo+".txt");
        Thread.sleep(5000);

        exitTPU();
        closeWiniumService();
        isDone=true;
        return isDone;
    }

    public static WiniumDriver launchApplication(String applicationPath) throws Exception{
        DesktopOptions desktopOptions=new DesktopOptions();
        desktopOptions.setApplicationPath(applicationPath);

        WiniumDriver driver = new WiniumDriver(new URL("Http://localhost:9999"), desktopOptions);
        Thread.sleep(5000);

        logMessage("Application '"+applicationPath+"' is launched.");
        return driver;
    }

    public static WiniumDriver launchMicrOpay() throws Exception {
        WiniumDriver driver=launchApplication("C:\\Program Files (x86)\\Sage MicrOpay\\Evolution.exe");
        Thread.sleep(3000);
        logMessage("Sage MicrOpay is laucnhed.");
        return driver;
    }

    //Override below
    public static boolean importTeamViaTPU(String testSerialNo, String tenantKey, String adminUsername, String password, String importFileFullPathName) throws Exception {
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();

        WiniumLib.launchWiniumService();
        WiniumDriver driver=launchTPU();

        logScreenshotX();

        AutoItX x=new AutoItX();

        String winTitle_TPU="Sage ESS Tenant Provisioning Utility";

        x.winActivate(winTitle_TPU);
        AutoITLib.click_Window(winTitle_TPU, 218, 78);
        x.sleep(2000);
        logMessage("Team Import tab is clicked.");
        logScreenshotX();


        if (testSerialNo!=null){
            String fileName="TenantDetails_"+testSerialNo+".txt";
            String tenantFileFullName=projectPath+"\\UserList\\"+fileName;
            tenantKey=SystemLibrary.getValueFromListFile("Tenant Key", " = ", tenantFileFullName);
            adminUsername=SystemLibrary.getValueFromListFile("Admin Login", " =", tenantFileFullName);
            password=SystemLibrary.getValueFromListFile("Admin Password", " =", tenantFileFullName);
        }

        if (tenantKey!=null){
            x.winActivate(winTitle_TPU);
            Thread.sleep(2000);
            click_Window(winTitle_TPU, 218, 360);
            x.send(tenantKey);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Tenant Key: '"+tenantKey+"' is input.");
        }

        if (adminUsername!=null){
            //click_Window(tpuWinTitle, 191, 326);
            x.send(adminUsername);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Admin Username: '"+adminUsername+"' is input.");
        }

        if (password!=null){
            //click_Window(tpuWinTitle, 191, 359);
            x.send(password);
            x.send("{TAB}", false);
            x.sleep(2000);
            x.send("{TAB}", false);
            logMessage("Password: '"+password+"' is input.");
        }

        if (importFileFullPathName!=null){
            //click_Window(tpuWinTitle, 191, 388);
            x.send(importFileFullPathName);
            x.sleep(2000);
            logMessage("File Path: '"+importFileFullPathName+"' is input.");
        }

        x.send("{TAB}", false);
        x.sleep(2000);
        x.send("{TAB}", false);
        x.sleep(2000);

        x.send("{TAB}", false);

        logMessage("Screenshot before clicking button - Import Teams.");
        logScreenshotX();

        x.send("{ENTER}, false");
        x.sleep(25000);

        logMessage("Import Teams button is clicked.");
        logScreenshotX();

        /*String logText=driver.findElement(By.xpath("//*[@AutomationId='textBoxInfo']")).getText();
        Thread.sleep(5000);
        String logFileName="Log_ImportTeams_"+testSerialNo+"_"+getCurrentTimeAndDate()+".txt";

        String logFileFullPathName=projectPath+"UserList\\"+logFileName;
        createTextFile(logFileFullPathName, logText);
        logMessage("Import detail is saved as '"+logFileFullPathName+"'.");
        logMessage("Click here to access the Tenant Detail: '"+serverUrlAddress+"/userlist/"+logFileName);
*/
        exitTPU();
        closeWiniumService();
        isDone=true;
        return isDone;
    }

    //Override above
    public static boolean importTeamViaTPU(String testSerialNo, String tenantKey, String adminUsername, String password, String importFileFullPathName, String payrollDBNumber) throws Exception {
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();

        WiniumLib.launchWiniumService();
        WiniumDriver driver=launchTPU();

        logScreenshotX();

        AutoItX x=new AutoItX();

        String winTitle_TPU="Sage ESS Tenant Provisioning Utility";

        x.winActivate(winTitle_TPU);
        AutoITLib.click_Window(winTitle_TPU, 218, 78);
        x.sleep(2000);
        logMessage("Team Import tab is clicked.");
        logScreenshotX();


        if (testSerialNo!=null){
            String fileName="TenantDetails_"+testSerialNo+".txt";
            String tenantFileFullName=projectPath+"\\UserList\\"+fileName;
            tenantKey=SystemLibrary.getValueFromListFile("Tenant Key", " = ", tenantFileFullName);
            adminUsername=SystemLibrary.getValueFromListFile("Admin Login", " =", tenantFileFullName);
            password=SystemLibrary.getValueFromListFile("Admin Password", " =", tenantFileFullName);
        }

        if (tenantKey!=null){
            x.winActivate(winTitle_TPU);
            Thread.sleep(2000);
            click_Window(winTitle_TPU, 218, 360);
            x.send(tenantKey);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Tenant Key: '"+tenantKey+"' is input.");
        }

        if (adminUsername!=null){
            //click_Window(tpuWinTitle, 191, 326);
            x.send(adminUsername);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Admin Username: '"+adminUsername+"' is input.");
        }

        if (password!=null){
            //click_Window(tpuWinTitle, 191, 359);
            x.send(password);
            x.send("{TAB}", false);
            x.sleep(2000);
            x.send("{TAB}", false);
            logMessage("Password: '"+password+"' is input.");
        }

        if (payrollDBNumber!=null){
            if (payrollDBNumber.equals("2")){
                x.winActivate(winTitle_TPU);
                Thread.sleep(2000);
                click_Window(winTitle_TPU, 521, 460);
                x.sleep(2000);
                click_Window(winTitle_TPU, 521, 495);
                x.sleep(2000);
                x.send("{TAB}", false);
                logMessage("The second Payroll DB is selected.");
            }
        }

        if (importFileFullPathName!=null){
            //click_Window(tpuWinTitle, 191, 388);
            x.send(importFileFullPathName);
            x.sleep(2000);
            logMessage("File Path: '"+importFileFullPathName+"' is input.");
        }

        x.send("{TAB}", false);
        x.sleep(2000);
        x.send("{TAB}", false);
        x.sleep(2000);

        x.send("{TAB}", false);

        logMessage("Screenshot before clicking button - Import Teams.");
        logScreenshotX();

        x.send("{ENTER}, false");
        x.sleep(25000);

        logMessage("Import Teams button is clicked.");
        logScreenshotX();

        /*String logText=driver.findElement(By.xpath("//*[@AutomationId='textBoxInfo']")).getText();
        Thread.sleep(5000);
        String logFileName="Log_ImportTeams_"+testSerialNo+"_"+getCurrentTimeAndDate()+".txt";

        String logFileFullPathName=projectPath+"UserList\\"+logFileName;
        createTextFile(logFileFullPathName, logText);
        logMessage("Import detail is saved as '"+logFileFullPathName+"'.");
        logMessage("Click here to access the Tenant Detail: '"+serverUrlAddress+"/userlist/"+logFileName);
*/
        exitTPU();
        closeWiniumService();
        isDone=true;
        return isDone;
    }

    public static boolean importLeaveViaTPU(String testSerialNo, String tenantKey, String adminUsername, String password, String importFileFullPathName) throws Exception {
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();

        WiniumLib.launchWiniumService();
        WiniumDriver driver=launchTPU();

        logScreenshotX();

        AutoItX x=new AutoItX();

        String winTitle_TPU="Sage ESS Tenant Provisioning Utility";

        x.winActivate(winTitle_TPU);
        //AutoITLib.click_Window(winTitle_TPU, 218, 78);
        AutoITLib.click_Window(winTitle_TPU, 367, 78);
        x.sleep(2000);
        logMessage("Leave Import tab is clicked.");
        logScreenshotX();


        if (testSerialNo!=null){
            String fileName="TenantDetails_"+testSerialNo+".txt";
            String tenantFileFullName=projectPath+"\\UserList\\"+fileName;
            tenantKey=SystemLibrary.getValueFromListFile("Tenant Key", " = ", tenantFileFullName);
            adminUsername=SystemLibrary.getValueFromListFile("Admin Login", " =", tenantFileFullName);
            password=SystemLibrary.getValueFromListFile("Admin Password", " =", tenantFileFullName);
        }

        if (tenantKey!=null){
            x.winActivate(winTitle_TPU);
            Thread.sleep(2000);
            click_Window(winTitle_TPU, 218, 360);
            x.send(tenantKey);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Tenant Key: '"+tenantKey+"' is input.");
        }

        if (adminUsername!=null){
            //click_Window(tpuWinTitle, 191, 326);
            x.send(adminUsername);
            x.send("{TAB}", false);
            x.sleep(10000);
            logMessage("Admin Username: '"+adminUsername+"' is input.");
        }

        if (password!=null){
            //click_Window(tpuWinTitle, 191, 359);
            x.send(password);
            x.send("{TAB}", false);
            x.sleep(2000);
            x.send("{TAB}", false);
            logMessage("Password: '"+password+"' is input.");
        }

        if (importFileFullPathName!=null){
            //click_Window(tpuWinTitle, 191, 388);
            x.send(importFileFullPathName);
            x.sleep(2000);
            logMessage("File Path: '"+importFileFullPathName+"' is input.");
        }

        x.send("{TAB}", false);
        x.sleep(2000);
        x.send("{TAB}", false);
        x.sleep(2000);

        x.send("{TAB}", false);

        logMessage("Screenshot before clicking button - Import Teams.");
        logScreenshotX();

        x.send("{ENTER}, false");
        x.sleep(15000);

        logMessage("Import Leave button is clicked.");
        logScreenshotX();

        /*String logText=driver.findElement(By.xpath("//*[@AutomationId='textBoxInfo']")).getText();
        Thread.sleep(5000);
        String logFileName="Log_ImportTeams_"+testSerialNo+"_"+getCurrentTimeAndDate()+".txt";

        String logFileFullPathName=projectPath+"UserList\\"+logFileName;
        createTextFile(logFileFullPathName, logText);
        logMessage("Import detail is saved as '"+logFileFullPathName+"'.");
        logMessage("Click here to access the Tenant Detail: '"+serverUrlAddress+"/userlist/"+logFileName);
*/
        exitTPU();
        closeWiniumService();
        isDone=true;
        return isDone;
    }

    /////////////// Debug here ///////////////////

}
