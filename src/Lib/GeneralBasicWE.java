package Lib;

import autoitx4java.AutoItX;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.*;

import static Lib.AutoITLib.logScreenshotX;
import static Lib.DBManage.restoreDB;
import static Lib.GeneralBasicHigh.configureMCS;
import static Lib.SystemLibrary.*;

public class GeneralBasicWE {

    public static String dbBackupRetorePath="C:\\AutoTestDBBackup\\AutoTestDBRestoreBackup\\";

    public static boolean launchWageEasy() throws InterruptedException {
        exitWageEasy();

        boolean isLaunched=false;

        AutoItX x = new AutoItX();
        x.run("C:\\Program Files (x86)\\Wage Easy Payroll\\WageEasy.exe");
        if (x.winWait(" Wage Easy Payroll", "", 60) == true) {
            logMessage("Wage Easy is launched.");
            isLaunched=true;
        } else {
            logError("Wage Easy is NOT launched.");
        }
        logScreenshotX();
        return isLaunched;
    }

    public static boolean exitWageEasy() throws InterruptedException {
        boolean isClosed=false;
        AutoItX x = new AutoItX();
        int attempt = 0;

        if (x.winWait(" Wage Easy Payroll", "", 10)) {
            x.winActivate(" Wage Easy Payroll");
            x.winClose(" Wage Easy Payroll");
            //x.send("{ALT}x", false);
            Thread.sleep(2000);
            logMessage("Exit Wage Easy is clicked.");

            logScreenshotX();

            x.controlFocus(" Close Wage Easy", "&Yes", "[CLASS:Button; INSTANCE:1]");
            x.controlClick(" Close Wage Easy", "&Yes", "[CLASS:Button; INSTANCE:1]");
            logMessage("Exit Wage Easy - Button Yes is clicked.");
            Thread.sleep(2000);
            logScreenshotX();

            x.controlFocus("Confirm", "&No", "[CLASS:TButton; INSTANCE:1]");
            x.controlClick("Confirm", "&No", "[CLASS:TButton; INSTANCE:1]");
            Thread.sleep(2000);
            logMessage("Exit Wage Easy - Backup NO Button Yes is clicked.");

            if (x.winWait(" Wage Easy Payroll", "", 5)) {
                if (x.processWaitClose("WageEasy.exe", 15)) {
                    logMessage("Wage Easy is closed.");
                    isClosed = true;
                } else {
                    x.processClose("WageEasy.exe");
                    logWarning("Wage Easy is terminated.");
                    isClosed = true;
                }
            }

            logScreenshotX();
        }
        else{
            logMessage("Wage Easy is NOT launched.");
            isClosed=true;
        }
        return isClosed;
    }


    public static boolean openWEDB(String backupFileName, String comment) throws SQLException, ClassNotFoundException, InterruptedException, UnknownHostException {
        boolean isRestored=false;
        int errorCounter=0;
        AutoItX x=new AutoItX();

        String backupFileFullName = dbBackupRetorePath+backupFileName;
        File backupFile=new File(backupFileFullName);
        if (!backupFile.exists()){
            String networkBackupFileFullName=backupFileFullName.replace("C:\\", "E:\\");
            if (SystemLibrary.copyFile(networkBackupFileFullName, backupFileFullName)){
                logMessage("Backpup file is copied from drive E: to C: successfully.");
            }else{
                logError("File is not found.");
                errorCounter++;
            }
        }

        //launchWageEasy();
        x.controlFocus(" Wage Easy Payroll", "", "[CLASS:TdxBarControl; INSTANCE:2]");
        AutoITLib.clickControlInWindow_NEWInUse(" Wage Easy Payroll", "", "[CLASS:TdxBarControl; INSTANCE:2]", 22, 20);
        Thread.sleep(2000);
        logMessage("Open menu is clicked.");
        logScreenshotX();

        x.controlFocus(" Open Company", "", "[CLASS:ComboBox; INSTANCE:2]");
        x.controlClick(" Open Company", "", "[CLASS:ComboBox; INSTANCE:2]");
        x.send(backupFileFullName);
        x.sleep(2000);
        logMessage("Backup file '"+backupFileFullName+"' is input.");
        logMessage("Screenshot before clicking OPen button.");
        logScreenshotX();

        x.controlClick(" Open Company", "&Open", "[CLASS:Button; INSTANCE:2]");
        x.sleep(2000);
        logMessage("OPen button is clicked.");

        x.controlFocus("Confirm", "&No", "[CLASS:TButton; INSTANCE:1]");
        x.controlClick("Confirm", "&No", "[CLASS:TButton; INSTANCE:1]");
        x.sleep(2000);
        logMessage("Backup DB 'No' button is clicked.");

        logMessage("Wage Easy Backup file '"+backupFileFullName+"' is opened.");

        //exitWageEasy();

        if (errorCounter==0) {
            isRestored = true;
            SystemLibrary.logMessage("Successfully Opennging the Wage Easy '"+backupFileFullName+"' file.");
        }else{
            SystemLibrary.logError("Failed Opennging the Wage Easy '"+backupFileFullName+"' file.");
        }
        return isRestored;
    }

    public static boolean displayConfigurationScreen() throws InterruptedException {
        boolean isShown=false;
        AutoItX x=new AutoItX();
        x.controlFocus(" Wage Easy Payroll", "Main Menu", "[CLASS:TdxBarControl; INSTANCE:1]");
        //x.controlClick(" Wage Easy Payroll", "Main Menu", "[CLASS:TdxBarControl; INSTANCE:1]");
        x.send("{ALT}FD", false);
        logMessage("Keys ALT+F+D is input.");

        if (x.winWait(" Configuration", "", 10000)){
            logMessage("Configuration screen is shown.");
            isShown=true;
        }else{
            logError("Configuration screen is NOT shown.");
        }
        logScreenshotX();
        return isShown;
    }

    public static boolean displayConfiguration_ESSScreen() throws InterruptedException {
        boolean isShown=false;
        int errorCounter=0;

        if (displayConfigurationScreen()){
            AutoItX x=new AutoItX();
            x.controlFocus(" Configuration", "", "[CLASS:TdxNavBar; INSTANCE:1]");
            //x.controlClick(" Configuration","", "[CLASS:TdxNavBar; INSTANCE:1]", "1", 1, 57, 289);
            AutoITLib.clickControlInWindow_NEWInUse (" Configuration", "", "[CLASS:TdxNavBar; INSTANCE:1]", 51, 289);
            x.sleep(4000);
            logMessage("Configuration tab is clicked within Configuration screen.");
            logScreenshotX();

            AutoITLib.clickControlInWindow_NEWInUse (" Configuration", "", "[CLASS:TdxNavBar; INSTANCE:1]", 32, 265);
            x.sleep(4000);
            logMessage("ESS Item is clicked within Configuration Tab screen.");

            if (x.controlShow(" Configuration", "ESS Configuration", "[CLASS:TGroupBox; INSTANCE:1]")){
                logMessage("ESS Configuration screen is shown.");
            }else{
                logError("ESS Configuration screen is NOT shown.");
                errorCounter++;
            }

        }
        logScreenshotX();

        if (errorCounter==0) isShown=true;
        return isShown;
    }

    public static boolean editWE_ESSConfiguration(String testSerailNo) throws InterruptedException, IOException {
        boolean isDone=false;
        int errorCounter=0;

        String apiKey="";
        String serviceUsername="";
        String servicePassword="";

        if (displayConfiguration_ESSScreen()){
            apiKey=MCSLib.getValueFromGUIKeyFile("GUI Key", testSerailNo);
            serviceUsername=MCSLib.getValueFromGUIKeyFile("MCS UserName", testSerailNo);
            servicePassword=MCSLib.getValueFromGUIKeyFile("MCS UserName Password", testSerailNo);

            AutoItX x=new AutoItX();

            //Adding user name
            x.controlFocus(" Configuration", "", "[CLASS:TcxDBMaskEdit; INSTANCE:2]");
            x.send("^A", false);
            x.sleep(2000);
            x.send("{DEL}", false);
            x.sleep(2000);
            x.send(serviceUsername+"{Tab}", false);
            logMessage("Username '"+serviceUsername+"' is input.");

            //Adding password
            x.controlFocus(" Configuration", "", "[CLASS:TcxDBMaskEdit; INSTANCE:3]");
            x.send("^A", false);
            x.sleep(2000);
            x.send("{DEL}", false);
            x.sleep(2000);
            x.send(servicePassword+"{Tab}", false);
            logMessage("User password '"+servicePassword+"' is input.");

            //Adding Web API Key
            x.controlFocus(" Configuration", "", "[CLASS:TcxDBMaskEdit; INSTANCE:1]");
            x.send("^A", false);
            x.sleep(2000);
            x.send("{DEL}", false);
            x.sleep(2000);
            x.send(apiKey+"{Tab}", false);
            logMessage("API Key '"+apiKey+"' is input.");

            logMessage("Screneshot before click Test Connection button.");
            logScreenshotX();

            x.controlFocus(" Configuration", "Test Connection", "[CLASS:TButton; INSTANCE:1]");
            x.controlClick(" Configuration", "Test Connection", "[CLASS:TButton; INSTANCE:1]");
            x.sleep(2000);
            logMessage("Test Connection button is clicked.");

            if (x.winWaitActive("Wage Easy Payroll", "", 10000)){
                logMessage("Test Connection screen is shown.");
                logScreenshotX();
                x.controlFocus("Wage Easy Payroll", "OK", "[CLASS:TButton; INSTANCE:1]");
                x.controlClick("Wage Easy Payroll", "OK", "[CLASS:TButton; INSTANCE:1]");
                logMessage("OK button is clicked.");
            }

            closeESSConfigurationScreen();


        }
        else{
            errorCounter++;
        }

        if (errorCounter==0){
            isDone=true;
        }
        return isDone;
    }

    public static boolean closeESSConfigurationScreen() throws InterruptedException {
        boolean isClosed=false;
        AutoItX x=new AutoItX();

        if (x.winWaitActive(" Configuration", "", 3000)){
            x.winClose(" Configuration");
            logMessage("Configuration window is closed.");
            isClosed=true;
        }
        return isClosed;
    }

    ///////////////// Debug here  /////////////////
    @Test
    public static void test1() throws InterruptedException, SQLException, IOException, ClassNotFoundException {
        logMessage("Debug test 1");

        editWE_ESSConfiguration("aa391");

    }







}
