package Lib;

import autoitx4java.AutoItX;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static Lib.SystemLibrary.*;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

public class MCSLib {

    @Test
    public static void launchMCS() throws InterruptedException {

        AutoItX x = new AutoItX();
        x.run("C:\\TestAutomationProject\\ESSRegTest\\MCS\\mcs.bat");
        if (x.winWait("MicrOpay Client Service Config.  Version", "", 30) == true) {
            logMessage("MCS is launched.");
        } else {
            logError("MCS is NOT launched.");
        }
        AutoITLib.logScreenshotX();
    }

    public static void closeMCS() throws Exception {
        AutoItX x=new AutoItX();
        String title="MicrOpay Client Service Config.  Version";
        //x.controlClick("MicrOpay Client Service Config.  Version", "Close", "[NAME:CloseBtn]");
        AutoITLib.click_Window(title, 600, 512);
        x.sleep(2000);
        SystemLibrary.executeDOSCommand("Taskkill /IM MicropayClientServicesConfig.exe /F");
        logMessage("MCS tool is closed.");
    }

    //Override below
    public static boolean editMCS(String databaseType, String databaseDescription, String payrollDBServerName, String payrollDBName, String dbAuthenticationMethod, String dbLoginName, String dbPassword, String comDBServerName, String comDBName, String comDBAuthenticationMethod, String comDBLoginName, String comDBPassword, String connectionType, String apiKey, String serviceUsername, String servicePassword, String apiUrl, String hubUrl, String proxyUrl, String proxyUsername, String proxyPassword, String testSerailNo) throws InterruptedException, IOException {
        //launch MCS first
        boolean isDone=false;
        int errorCounter=0;

        apiKey=getValueFromGUIKeyFile("GUI Key", testSerailNo);
        serviceUsername=getValueFromGUIKeyFile("MCS UserName", testSerailNo);
        servicePassword=getValueFromGUIKeyFile("MCS UserName Password", testSerailNo);

        AutoItX x=new AutoItX();

        x.winActivate("MicrOpay Client Service Config.  Version");
        if (databaseDescription!=null){
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
        }

        switch (databaseDescription){

            case "ESS_Auto_Payroll1":
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll2":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll3":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_NZPayroll1":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
        }

        x.controlClick("MicrOpay Client Service Config.  Version", "Edit", "[NAME:EditBtn]");
        x.sleep(3000);
        logMessage("Edit button is clicked.");
        AutoITLib.logScreenshotX();

        if (x.winWait("Edit Database", "", 10)){
            if (databaseType!=null){
                if (databaseType!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBTypeComboBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBTypeComboBox]", "1", 1, 284, 9);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);

                    if (databaseType.equals("Meridian")){
                        x.send("{TAB}", false);
                    }else if (databaseType.equals("WageEasy")){
                        x.send("{DOWN}", false);
                        x.send("{TAB}", false);
                    }
                    logMessage("Database Type '"+databaseType+"' is input.");
                }

                if (databaseDescription!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBDescriptionTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBDescriptionTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBDescriptionTextBox]", databaseDescription);
                    x.sleep(2000);
                    logMessage("Database Description '"+databaseDescription+"' is input.");
                }

                if (payrollDBServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBServerNameTextBox]", payrollDBServerName);
                    x.sleep(2000);
                    logMessage("Payroll DB Server name '"+payrollDBServerName+"' is input.");
                }

                if (payrollDBName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBNameTextBox]", payrollDBName);
                    x.sleep(2000);
                    logMessage("Payroll DB name '"+payrollDBName+"' is input.");
                }

                if (dbLoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:DBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBUserNameTextBox]", "1", 2);
                    x.send(dbLoginName);
                    x.sleep(2000);
                    logMessage("DB Login Name"+dbLoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:DBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("DB Passworde"+dbPassword+" is input.");
                }

                //////////////////////////////////
                if (comDBServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", comDBServerName);
                    x.sleep(2000);
                    logMessage("Common DB Server name '"+comDBServerName+"' is input.");
                }

                if (comDBName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBNameTextBox]", comDBName);
                    x.sleep(2000);
                    logMessage("common DB name '"+comDBName+"' is input.");
                }

                if (comDBLoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB2UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBUserNameTextBox]", "1", 2);
                    x.send(comDBLoginName);
                    x.sleep(2000);
                    logMessage("Common DB Login Name"+comDBLoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("Common DB Passworde"+comDBPassword+" is input.");
                }

                if (connectionType!=null){
                    if (connectionType.equals("1")){
                        if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                            x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                            x.sleep(2000);

                            if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                                x.sleep(2000);
                                if (x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                    logMessage("Web API Option is shown.");
                                }else{
                                    logError("Web API option screen is NOT shown.");
                                    errorCounter++;
                                }

                            }else{
                                logMessage("Web API Option is shown.");
                            }
                        }else{
                            logMessage("Web API Option is shown.");
                        }
                        ////////////////////////////
                        if (errorCounter==0){
                            if (apiKey!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIKeyEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIKeyEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIKeyEdit]", apiKey);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Key '"+apiKey+"' is input.");
                            }

                            if (serviceUsername!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUsernameEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUsernameEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUsernameEdit]", serviceUsername);
                                x.sleep(2000);
                                logMessage("Service User Name '"+serviceUsername+"' is input.");
                            }

                            if (serviceUsername!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUsernameEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUsernameEdit]", "1", 2);
                                x.send("^A", false);
                                x.sleep(1000);
                                x.controlSend("Edit Database", "", "[NAME:APIUsernameEdit]", serviceUsername);
                                x.sleep(2000);
                                logMessage("Service User Name '"+serviceUsername+"' is input.");
                            }

                            if (servicePassword!=null){
                                /////////////////////
                                clearClipboard();
                                String evadeServicePassword=escapeJava(servicePassword);
                                StringSelection stringSelection = new StringSelection(evadeServicePassword);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);

                                x.controlFocus("Edit Database", "", "[NAME:APIPasswordEdit]");
                                for (int i=0;i<30;i++){
                                    x.send("{BS}", false);
                                }

                                //x.controlSend("Edit Database", "", "[NAME:APIPasswordEdit]", servicePassword);
                                x.send("^V", false);
                                logMessage("Ctrl V is input.");
                                x.sleep(3000);
                                x.send("{TAB}", false);
                                logMessage("TAB is input.");
                                x.sleep(2000);
                                //////
/*
                                x.controlFocus("Edit Database", "", "[NAME:APIPasswordEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIPasswordEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIPasswordEdit]", servicePassword);
                                x.sleep(2000);
*/
                                logMessage("Service Password '"+servicePassword+"' is input.");
                            }

                            if (apiUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUrlEdit]", apiUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Url '"+apiUrl+"' is input.");
                            }

                            if (hubUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:HubUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:HubUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:HubUrlEdit]", hubUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("Hub Url '"+hubUrl+"' is input.");
                            }

                            logMessage("Screenshot before click Save button.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Save", "[NAME:SaveBtn]");
                            x.sleep(2000);
                            logMessage("Save button is clicked.");

                        }else{
                            logError("Web API Connection is NOT adjusted.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Cancel", "[NAME:CancelBtn]");
                            x.sleep(2000);
                            logMessage("Cancel button is clicked.");

                            x.controlClick("Cancel?", "&Yes", "[CLASS:Button; INSTANCE:1]");
                            x.sleep(2000);
                            logMessage("Cancel-Yes butotn is clicked.");

                        }
                    }
                }

            }
        }
        else{
            errorCounter++;
            logError("Edit WebAPI Database screen is NOT shown.");
        }

        String title="MicrOpay Client Service Config.  Version";
        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");

        logMessage("Screenshot before close MCS tool.");
        AutoITLib.logScreenshotX();

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    //Override above
    public static boolean editMCS(String databaseType, String databaseDescription, String payrollDBServerName, String payrollDBName, String dbAuthenticationMethod, String dbLoginName, String dbPassword, String comDBServerName, String comDBName, String comDBAuthenticationMethod, String comDBLoginName, String comDBPassword, String connectionType, String apiKey, String serviceUsername, String servicePassword, String apiUrl, String hubUrl, String proxyUrl, String proxyUsername, String proxyPassword, String testSerailNo, String dbName) throws InterruptedException, IOException {
        //launch MCS first
        boolean isDone=false;
        int errorCounter=0;

        apiKey=getValueFromGUIKeyFile("GUI Key", testSerailNo, dbName);
        serviceUsername=getValueFromGUIKeyFile("MCS UserName", testSerailNo, dbName);
        servicePassword=getValueFromGUIKeyFile("MCS UserName Password", testSerailNo, dbName);

        AutoItX x=new AutoItX();

        x.winActivate("MicrOpay Client Service Config.  Version");
        if (databaseDescription!=null){
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
        }

        switch (databaseDescription){

            case "ESS_Auto_Payroll1":
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll2":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll3":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_NZPayroll1":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
        }

        x.controlClick("MicrOpay Client Service Config.  Version", "Edit", "[NAME:EditBtn]");
        x.sleep(3000);
        logMessage("Edit button is clicked.");
        AutoITLib.logScreenshotX();

        if (x.winWait("Edit Database", "", 10)){
            if (databaseType!=null){
                if (databaseType!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBTypeComboBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBTypeComboBox]", "1", 1, 284, 9);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);

                    if (databaseType.equals("Meridian")){
                        x.send("{TAB}", false);
                    }else if (databaseType.equals("WageEasy")){
                        x.send("{DOWN}", false);
                        x.send("{TAB}", false);
                    }
                    logMessage("Database Type '"+databaseType+"' is input.");
                }

                if (databaseDescription!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBDescriptionTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBDescriptionTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBDescriptionTextBox]", databaseDescription);
                    x.sleep(2000);
                    logMessage("Database Description '"+databaseDescription+"' is input.");
                }

                if (payrollDBServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBServerNameTextBox]", payrollDBServerName);
                    x.sleep(2000);
                    logMessage("Payroll DB Server name '"+payrollDBServerName+"' is input.");
                }

                if (payrollDBName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBNameTextBox]", payrollDBName);
                    x.sleep(2000);
                    logMessage("Payroll DB name '"+payrollDBName+"' is input.");
                }

                if (dbLoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:DBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBUserNameTextBox]", "1", 2);
                    x.send(dbLoginName);
                    x.sleep(2000);
                    logMessage("DB Login Name"+dbLoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:DBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("DB Passworde"+dbPassword+" is input.");
                }

                //////////////////////////////////
                if (comDBServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", comDBServerName);
                    x.sleep(2000);
                    logMessage("Common DB Server name '"+comDBServerName+"' is input.");
                }

                if (comDBName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBNameTextBox]", comDBName);
                    x.sleep(2000);
                    logMessage("common DB name '"+comDBName+"' is input.");
                }

                if (comDBLoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB2UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBUserNameTextBox]", "1", 2);
                    x.send(comDBLoginName);
                    x.sleep(2000);
                    logMessage("Common DB Login Name"+comDBLoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("Common DB Passworde"+comDBPassword+" is input.");
                }

                if (connectionType!=null){
                    if (connectionType.equals("1")){
                        if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                            x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                            x.sleep(2000);

                            if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                                x.sleep(2000);
                                if (x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                    logMessage("Web API Option is shown.");
                                }else{
                                    logError("Web API option screen is NOT shown.");
                                    errorCounter++;
                                }

                            }else{
                                logMessage("Web API Option is shown.");
                            }
                        }else{
                            logMessage("Web API Option is shown.");
                        }
                        ////////////////////////////
                        if (errorCounter==0){
                            if (apiKey!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIKeyEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIKeyEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIKeyEdit]", apiKey);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Key '"+apiKey+"' is input.");
                            }

                            if (serviceUsername!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUsernameEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUsernameEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUsernameEdit]", serviceUsername);
                                x.sleep(2000);
                                logMessage("Service User Name '"+serviceUsername+"' is input.");
                            }

                            if (servicePassword!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIPasswordEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIPasswordEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIPasswordEdit]", servicePassword);
                                x.sleep(2000);
                                logMessage("Service Password '"+servicePassword+"' is input.");
                            }

                            if (apiUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUrlEdit]", apiUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Url '"+apiUrl+"' is input.");
                            }

                            if (hubUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:HubUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:HubUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:HubUrlEdit]", hubUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("Hub Url '"+hubUrl+"' is input.");
                            }

                            logMessage("Screenshot before click Save button.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Save", "[NAME:SaveBtn]");
                            x.sleep(2000);
                            logMessage("Save button is clicked.");

                        }else{
                            logError("Web API Connection is NOT adjusted.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Cancel", "[NAME:CancelBtn]");
                            x.sleep(2000);
                            logMessage("Cancel button is clicked.");

                            x.controlClick("Cancel?", "&Yes", "[CLASS:Button; INSTANCE:1]");
                            x.sleep(2000);
                            logMessage("Cancel-Yes butotn is clicked.");

                        }
                    }
                }

            }
        }
        else{
            errorCounter++;
            logError("Edit WebAPI Database screen is NOT shown.");
        }

        String title="MicrOpay Client Service Config.  Version";
        x.winActivate(title);

        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");

        logMessage("Screenshot before close MCS tool.");
        AutoITLib.logScreenshotX();

        if (errorCounter==0) isDone=true;
        return isDone;
    }


    //Override below
    public static boolean editMCS_Main(int startSerialNo, int endSerialNo, String testSerialNo) throws Exception {

        SystemLibrary.logMessage("--- Start configure mulit MCS from row " + startSerialNo + " to " + endSerialNo);
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"ConfigureMCS");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    launchMCS();
                    if (!editMCS(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], testSerialNo)) errorCounter++;
                    closeMCS();
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Configuring MCS.");
        return isDone;
    }

    //Override above
    public static boolean editMCS_Main(int startSerialNo, int endSerialNo, String testSerialNo, String dbName) throws Exception {

        SystemLibrary.logMessage("--- Start configure mulit MCS from row " + startSerialNo + " to " + endSerialNo);
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"ConfigureMCS");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    launchMCS();
                    if (!editMCS(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], testSerialNo, dbName)) errorCounter++;
                    closeMCS();
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Configuring MCS.");
        return isDone;
    }

    //Override below
    public static String getValueFromGUIKeyFile(String itemName, String testSerialNo) throws IOException, InterruptedException {
        String value=null;
        List<String> list=getStringListFromFile(dataSourcePath+"GUIKey_"+testSerialNo+".txt");

        int totalLineNumber=list.size();
        for(int i=0;i<totalLineNumber;i++){
            String currentItem=list.get(i);
            if (currentItem.contains(itemName+": ")){
                value=currentItem.replace(itemName+": ", "");
                break;
            }
        }

        SystemLibrary.logMessage("The value of '"+itemName+"' is '" + value + "'.");
        return value;

    }

    //Override above
    public static String getValueFromGUIKeyFile(String itemName, String testSerialNo, String dbName) throws IOException, InterruptedException {
        String value=null;
        List<String> list=getStringListFromFile(dataSourcePath+"GUIKey_"+testSerialNo+"_"+dbName+".txt");

        int totalLineNumber=list.size();
        for(int i=0;i<totalLineNumber;i++){
            String currentItem=list.get(i);
            if (currentItem.contains(itemName+": ")){
                value=currentItem.replace(itemName+": ", "");
                break;
            }
        }

        SystemLibrary.logMessage("The value of '"+itemName+"' is '" + value + "'.");
        return value;

    }

    public static boolean restartMCS() throws Exception {
        boolean isDone=false;
        launchMCS();
        AutoItX x=new AutoItX();
        String title="MicrOpay Client Service Config.  Version";
        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Start the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();
        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");

        Thread.sleep(120000);
        closeMCS();
        isDone=true;
        return isDone;
    }

    public static boolean stopMCS() throws Exception {
        boolean isDone=false;
        launchMCS();
        AutoItX x=new AutoItX();
        String title="MicrOpay Client Service Config.  Version";
        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        closeMCS();
        isDone=true;
        return isDone;
    }

    public static boolean startMCS() throws Exception {
        boolean isDone=false;
        launchMCS();
        AutoItX x=new AutoItX();
        String title="MicrOpay Client Service Config.  Version";

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Start the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();
        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");

        Thread.sleep(120000);
        closeMCS();
        isDone=true;
        return isDone;
    }


    public static String getValueFromGUIKey2File(String itemName, String dbName, String testSerialNo) throws IOException, InterruptedException {
        String value=null;
        List<String> list=getStringListFromFile(dataSourcePath+"GUIKey_" + dbName + "_" + testSerialNo +".txt");

        int totalLineNumber=list.size();
        for(int i=0;i<totalLineNumber;i++){
            String currentItem=list.get(i);
            if (currentItem.contains(itemName+": ")){
                value=currentItem.replace(itemName+": ", "");
                break;
            }
        }

        SystemLibrary.logMessage("The value of '"+itemName+"' is '" + value + "'.");
        return value;

    }


    public static boolean editMCS2(String databaseType, String databaseDescription, String payrollDB2ServerName, String payrollDB2Name, String dbAuthenticationMethod, String dbLoginName, String dbPassword, String comDB2ServerName, String comDB2Name, String comDB2AuthenticationMethod, String comDB2LoginName, String comDB2Password, String connectionType, String apiKey, String serviceUsername, String servicePassword, String apiUrl, String hubUrl, String proxyUrl, String proxyUsername, String proxyPassword, String dbName, String testSerailNo) throws InterruptedException, IOException {
        //launch MCS first
        boolean isDone=false;
        int errorCounter=0;

        apiKey=getValueFromGUIKey2File("GUI Key", testSerailNo, dbName);
        serviceUsername=getValueFromGUIKey2File("MCS UserName", testSerailNo, dbName);
        servicePassword=getValueFromGUIKey2File("MCS UserName Password", testSerailNo, dbName);

        AutoItX x=new AutoItX();

        x.winActivate("MicrOpay Client Service Config.  Version");
        if (databaseDescription!=null){
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
            x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{UP}");
            x.sleep(1000);
        }

        switch (databaseDescription){

            case "ESS_Auto_Payroll1":
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll2":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS_Auto_Payroll3":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
            case "ESS2_Auto_Payroll":
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                x.controlSend("MicrOpay Client Service Config.  Version", "", "[NAME:DBListGridView]", "{DOWN}");
                x.sleep(2000);
                logMessage("Item '"+databaseDescription+"' is highlighted.");
                break;
        }

        x.controlClick("MicrOpay Client Service Config.  Version", "Edit", "[NAME:EditBtn]");
        x.sleep(3000);
        logMessage("Edit button is clicked.");
        AutoITLib.logScreenshotX();

        if (x.winWait("Edit Database", "", 10)){
            if (databaseType!=null){
                if (databaseType!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBTypeComboBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBTypeComboBox]", "1", 1, 284, 9);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);
                    x.send("{UP}", false);
                    x.sleep(1000);

                    if (databaseType.equals("Meridian")){
                        x.send("{TAB}", false);
                    }else if (databaseType.equals("WageEasy")){
                        x.send("{DOWN}", false);
                        x.send("{TAB}", false);
                    }
                    logMessage("Database Type '"+databaseType+"' is input.");
                }

                if (databaseDescription!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBDescriptionTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBDescriptionTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBDescriptionTextBox]", databaseDescription);
                    x.sleep(2000);
                    logMessage("Database Description '"+databaseDescription+"' is input.");
                }

                if (payrollDB2ServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBServerNameTextBox]", payrollDB2ServerName);
                    x.sleep(2000);
                    logMessage("Payroll DB Server name '"+payrollDB2ServerName+"' is input.");
                }

                if (payrollDB2Name!=null){
                    x.controlFocus("Edit Database", "", "[NAME:DBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:DBNameTextBox]", payrollDB2Name);
                    x.sleep(2000);
                    logMessage("Payroll DB name '"+payrollDB2Name+"' is input.");
                }

                if (dbLoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB1UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:DBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBUserNameTextBox]", "1", 2);
                    x.send(dbLoginName);
                    x.sleep(2000);
                    logMessage("DB Login Name"+dbLoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:DBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:DBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("DB Passworde"+dbPassword+" is input.");
                }

                //////////////////////////////////
                if (comDB2ServerName!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBServerNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBServerNameTextBox]", comDB2ServerName);
                    x.sleep(2000);
                    logMessage("Common DB Server name '"+comDB2ServerName+"' is input.");
                }

                if (comDB2Name!=null){
                    x.controlFocus("Edit Database", "", "[NAME:CommonDBNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBNameTextBox]", "1", 2);
                    x.controlSend("Edit Database", "", "[NAME:CommonDBNameTextBox]", comDB2Name);
                    x.sleep(2000);
                    logMessage("common DB name '"+comDB2Name+"' is input.");
                }

                if (comDB2LoginName!=null){
                    x.controlClick("Edit Database", "Use SQL Server authentication", "[NAME:DB2UseSQLRB]");
                    logMessage("Use SQL Server authentication is checked.");
                    x.sleep(2000);

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBUserNameTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBUserNameTextBox]", "1", 2);
                    x.send(comDB2LoginName);
                    x.sleep(2000);
                    logMessage("Common DB Login Name"+comDB2LoginName+" is input.");

                    x.controlFocus("Edit Database", "", "[NAME:CommonDBPasswordTextBox]");
                    x.controlClick("Edit Database", "", "[NAME:CommonDBPasswordTextBox]", "1", 2);
                    x.send(dbPassword);
                    x.sleep(2000);
                    logMessage("Common DB Passworde"+comDB2Password+" is input.");
                }

                if (connectionType!=null){
                    if (connectionType.equals("1")){
                        if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                            x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                            x.sleep(2000);

                            if (!x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                x.controlClick("Edit Database", "WebAPI", "[NAME:checkBoxWebAPI]", "1", 1);
                                x.sleep(2000);
                                if (x.controlCommandIsVisible("Edit Database", "Web API Connection", "[NAME:groupBox2]")){
                                    logMessage("Web API Option is shown.");
                                }else{
                                    logError("Web API option screen is NOT shown.");
                                    errorCounter++;
                                }

                            }else{
                                logMessage("Web API Option is shown.");
                            }
                        }else{
                            logMessage("Web API Option is shown.");
                        }
                        ////////////////////////////
                        if (errorCounter==0){
                            if (apiKey!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIKeyEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIKeyEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIKeyEdit]", apiKey);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Key '"+apiKey+"' is input.");
                            }

                            if (serviceUsername!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUsernameEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUsernameEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUsernameEdit]", serviceUsername);
                                x.sleep(2000);
                                logMessage("Service User Name '"+serviceUsername+"' is input.");
                            }

                            if (servicePassword!=null){
                                /////////////////////
                                clearClipboard();
                                String evadeServicePassword=escapeJava(servicePassword);
                                StringSelection stringSelection = new StringSelection(evadeServicePassword);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);

                                x.controlFocus("Edit Database", "", "[NAME:APIPasswordEdit]");
                                for (int i=0;i<30;i++){
                                    x.send("{BS}", false);
                                }

                                //x.controlSend("Edit Database", "", "[NAME:APIPasswordEdit]", servicePassword);
                                x.send("^V", false);
                                logMessage("Ctrl V is input.");
                                x.sleep(3000);
                                x.send("{TAB}", false);
                                logMessage("TAB is input.");
                                x.sleep(2000);
                                //////
/*
                                x.controlFocus("Edit Database", "", "[NAME:APIPasswordEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIPasswordEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIPasswordEdit]", servicePassword);
                                x.sleep(2000);
*/
                                logMessage("Service Password '"+servicePassword+"' is input.");
                            }

                            if (apiUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:APIUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:APIUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:APIUrlEdit]", apiUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("API Url '"+apiUrl+"' is input.");
                            }

                            if (hubUrl!=null){
                                x.controlFocus("Edit Database", "", "[NAME:HubUrlEdit]");
                                x.controlClick("Edit Database", "", "[NAME:HubUrlEdit]", "1", 2);
                                x.controlSend("Edit Database", "", "[NAME:HubUrlEdit]", hubUrl);
                                x.sleep(1000);
                                x.send("{TAB}", false);
                                x.sleep(2000);
                                logMessage("Hub Url '"+hubUrl+"' is input.");
                            }

                            logMessage("Screenshot before click Save button.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Save", "[NAME:SaveBtn]");
                            x.sleep(2000);
                            logMessage("Save button is clicked.");

                        }else{
                            logError("Web API Connection is NOT adjusted.");
                            AutoITLib.logScreenshotX();

                            x.controlClick("Edit Database", "Cancel", "[NAME:CancelBtn]");
                            x.sleep(2000);
                            logMessage("Cancel button is clicked.");

                            x.controlClick("Cancel?", "&Yes", "[CLASS:Button; INSTANCE:1]");
                            x.sleep(2000);
                            logMessage("Cancel-Yes butotn is clicked.");

                        }
                    }
                }

            }
        }
        else{
            errorCounter++;
            logError("Edit WebAPI Database screen is NOT shown.");
        }

        String title="MicrOpay Client Service Config.  Version";
        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");

        logMessage("Screenshot before close MCS tool.");
        AutoITLib.logScreenshotX();

        if (errorCounter==0) isDone=true;
        return isDone;
    }


    public static boolean editMCS2_Main(int startSerialNo, int endSerialNo, String testSerialNo) throws Exception {

        SystemLibrary.logMessage("--- Start configure mulit MCS from row " + startSerialNo + " to " + endSerialNo);
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"ConfigureMCS");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    launchMCS();
                    if (!editMCS2(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23],  testSerialNo)) errorCounter++;
                    closeMCS();
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Configuring MCS.");
        return isDone;
    }


    ////////////////////////// Debug here //////////////////
    @Test
    public static void test1() throws InterruptedException {
        //launchMCS();
        AutoItX x=new AutoItX();

        String title="MicrOpay Client Service Config.  Version";
        x.winActivate(title);

        ///////// Stop the MCS Service ///////////////
        AutoITLib.click_Window(title, 289, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();

        //////// Start the MCS Service /////////////
        AutoITLib.click_Window(title, 385, 512);
        x.sleep(2000);
        logMessage("Stop the MCS Service button is clicked.");
        AutoITLib.logScreenshotX();

        x.controlClick("", "OK", "[CLASS:Button; INSTANCE:1]");
        x.sleep(2000);
        logMessage("The OK button is clicked.");
        AutoITLib.logScreenshotX();


    }
}
