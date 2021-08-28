package Lib;

import PageObject.PageObj_EditDataDetailsInWebAPIManage;
import PageObject.PageObj_EditUserInWebAPIManage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import java.io.IOException;

import static Lib.SystemLibrary.*;

public class WebAPIKeyManagement {

    public static String guiKey="";

    public static WebDriver launchGUIPage(int driverType) throws InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver("https://www.guidgenerator.com/", driverType);
        return driver;
    }

    public static WebDriver launchWebAPIManagementPortal(int driverType) throws InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver("https://au-east-micropayapi-dev-ami.azurewebsites.net", driverType);
        return driver;
    }

    public static String generateGUIKey(String testSerialNo) throws InterruptedException, IOException {

        String GUIKey=SystemLibrary.generateUnitKey();
        logMessage("GUI key is generated as below:");
        logMessage(GUIKey);

        String fileName="GUIKey_"+testSerialNo+".txt";
        String fileFullName=dataSourcePath+fileName;

        createTextFile(fileFullName, "GUI Key: "+GUIKey+"\r\n");
        return GUIKey;
    }

    public static String generateGUIKey_OLD(String testSerialNo) throws InterruptedException, IOException {
        WebDriver driver=launchGUIPage(3);
        logMessage("GUI Generator page is show.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//input[@id='btnGenerate']")).click();
        Thread.sleep(15);

        String GUIKey=driver.findElement(By.xpath("//textarea[@name='txtResults']")).getText();
        logScreenshot(driver);
        logMessage("GUI key is generated as below:");
        logMessage(GUIKey);

        String fileName="GUIKey_"+testSerialNo+".txt";
        String fileFullName=dataSourcePath+fileName;

        createTextFile(fileFullName, "GUI Key: "+GUIKey+"\r\n");
        driver.close();

        return GUIKey;
    }

    public static String generateGUIKey(String testSerialNo, String appType) throws InterruptedException, IOException {
        WebDriver driver=launchGUIPage(3);
        logMessage("GUI Generator page is show.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//input[@id='btnGenerate']")).click();
        Thread.sleep(15);

        String GUIKey=driver.findElement(By.xpath("//textarea[@name='txtResults']")).getText();
        logScreenshot(driver);
        logMessage("GUI key is generated as below:");
        logMessage(GUIKey);

        String fileName="GUIKey_"+testSerialNo+".txt";
        String fileFullName=dataSourcePath+fileName;

        createTextFile(fileFullName, "GUI Key: "+GUIKey+"\r\n");
        driver.close();

        return GUIKey;
    }

    public static boolean logonWebAPIPortal(String username, String password, WebDriver driver) throws InterruptedException, IOException {
        boolean isLogon=false;
        int errorCounter=0;

        WebElement textbox_Username=driver.findElement(By.xpath("//input[@id='inputUsername']"));
        textbox_Username.sendKeys(username);
        textbox_Username.sendKeys(Keys.TAB);
        logMessage("Username '"+username+" is input.");

        WebElement textbox_Password=driver.findElement(By.xpath("//input[@id='inputPassword']"));
        textbox_Password.sendKeys(password);
        textbox_Password.sendKeys(Keys.TAB);
        logMessage("Password '"+password+" is input.");

        logMessage("Screenshot before click Sign in button.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//button[contains(text(), 'Sign in')]")).click();
        Thread.sleep(2000);
        logMessage("Sign in button is clicked.");

        if (SystemLibrary.waitChild("//span[text()='Dashboard']", 15, 1, driver)!=null){
            logMessage("logon Web API Management Portal successfully.");
        }else{
            logError("Failed log on Web API Management Portal.");
            errorCounter++;
        }

        logScreenshot(driver);

        if (errorCounter==0) isLogon=true;
        return isLogon;
    }

    public static boolean displayDatabasePage(WebDriver driver) throws InterruptedException {
        boolean isDisplayed=false;
        driver.findElement(By.xpath("//a[@href='#/databases']")).click();
        Thread.sleep(3000);
        logMessage("Databases is clicked in left sidebar.");
        isDisplayed=true;
        return isDisplayed;
    }

    public static boolean displayUsersPage(WebDriver driver) throws InterruptedException, Exception {
        boolean isDisplayed=false;
        driver.findElement(By.xpath("//a[@href='#/users']")).click();
        Thread.sleep(15000);
        logMessage("Users is clicked in left sidebar.");
        logScreenshot(driver);
        isDisplayed=true;
        return isDisplayed;
    }

    public static boolean displayCustomersPage(WebDriver driver) throws InterruptedException {
        boolean isDisplayed=false;
        driver.findElement(By.xpath("//a[@href='#/customers']")).click();
        Thread.sleep(5000);
        logMessage("Customers is clicked in left sidebar.");
        isDisplayed=true;
        return isDisplayed;
    }

    //Override below
    public static boolean addMultiDatabaseInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Database in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddDBInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddDBInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addDatabaseInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Database in Web API Manage Portal.");

        if (errorCounter==00) isPassed=true;
        return isPassed;
    }

    //Override above
    public static boolean addMultiDatabaseInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, String dbName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Database in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddDBInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddDBInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addDatabaseInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], testSerialNo, dbName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Database in Web API Manage Portal.");

        if (errorCounter==00) isPassed=true;
        return isPassed;
    }

    //Override below
    public static boolean addDatabaseInWebAPIManagePortal(String databaseAlias, String databaseName, String databaseToken, String serverName, String databaseType, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {

        boolean isAdded=false;
        int errorCounter=0;

        displayDatabasePage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add Database button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (databaseAlias!=null){
                databaseAlias=databaseAlias+ testSerialNo;
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).sendKeys(databaseAlias);
                logMessage("Database Alias '"+databaseAlias+"' is input.");
            }
            if (databaseName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).sendKeys(databaseName);
                logMessage("Database Name '"+databaseName+"' is input.");
            }

            if (guiKey!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).sendKeys(guiKey);
                logMessage("Database Token '"+guiKey+"' is input.");
            }

            if (serverName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).sendKeys(serverName);
                logMessage("Server Name '"+serverName+"' is input.");
            }

            if (databaseType!=null){
                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboDbType']")));
                selectDBType.selectByVisibleText(databaseType);
                logMessage("Database Type '"+databaseType+"' is selected.");
            }

            Thread.sleep(10000);
            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            //Jim added on 22/11/2019
            WebElement button_Add=driver.findElement(By.xpath("//button[text()='Add']"));
            new Actions(driver).moveToElement(button_Add).perform();
            button_Add.click();
            Thread.sleep(10000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, "databaseAlias: "+databaseAlias+"\r\n");

        }
        else{
            errorCounter++;
            logError("Add database screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }

    //Override above
    public static boolean addDatabaseInWebAPIManagePortal(String databaseAlias, String tapiDBName, String databaseToken, String serverName, String databaseType, String testSerialNo, String dbName, WebDriver driver) throws IOException, InterruptedException {

        boolean isAdded=false;
        int errorCounter=0;

        displayDatabasePage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add Database button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (databaseAlias!=null){
                databaseAlias=databaseAlias+ testSerialNo+"_"+dbName;
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).sendKeys(databaseAlias);
                logMessage("Database Alias '"+databaseAlias+"' is input.");
            }
            if (tapiDBName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).sendKeys(tapiDBName);
                logMessage("Database Name '"+tapiDBName+"' is input.");
            }

            if (guiKey!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).sendKeys(guiKey);
                logMessage("Database Token '"+guiKey+"' is input.");
            }

            if (serverName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).sendKeys(serverName);
                logMessage("Server Name '"+serverName+"' is input.");
            }

            if (databaseType!=null){
                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboDbType']")));
                selectDBType.selectByVisibleText(databaseType);
                logMessage("Database Type '"+databaseType+"' is selected.");
            }

            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(5000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, "databaseAlias: "+databaseAlias+"\r\n");

        }
        else{
            errorCounter++;
            logError("Add database screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }

    //Override below
    public static boolean addUserInWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String testSerialNo, WebDriver driver) throws Exception {

        boolean isAdded=false;
        int errorCounter=0;

        displayUsersPage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add User button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (username!=null){
                username=username+testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                logMessage("Username '"+username+"' is input.");
            }
            if (firstName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                logMessage("First Name '"+firstName+"' is input.");
            }
            if (lastName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                logMessage("Last Name '"+lastName+"' is input.");
            }

            if (phone!=null){
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                logMessage("Phone '"+phone+"' is input.");
            }
            if (password!=null){
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                logMessage("Password '"+password+"' is input.");
            }

            if (confirmPassword!=null){
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                logMessage("Confirm Password '"+confirmPassword+"' is input.");
            }

            if (email!=null){
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                logMessage("Email '"+email+"' is input.");
            }

            String userTypeName=null;
            if (type!=null){
                if (type.equals("EnableHR")){
                    userTypeName="WebAPI UserName";
                }else if (type.equals("Meridian")){
                    userTypeName="MCS UserName";
                }else if (type.equals("EasyPay")){
                    userTypeName="MCS UserName";
                }else if (type.equals("WageEasy")){
                    userTypeName="MCS UserName";
                }

                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                selectDBType.selectByVisibleText(type);
                logMessage("Type '"+type+"' is selected.");
            }
            if (description!=null){
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                logMessage("Description '"+description+"' is input.");
            }
            if (itemEnabled!=null){
                if (itemEnabled.equals("1")){
                    if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                        PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                        logMessage("Enabled is checked.");
                    }
                }
            }

            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(15000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, userTypeName+": "+username+"\r\n");
            createTextFile(fileFullName, userTypeName+" Password: "+password+"\r\n");
        }
        else{
            errorCounter++;
            logError("Add User screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }

    //Override above
    public static boolean addUserInWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String testSerialNo, String dbName, WebDriver driver) throws Exception {

        boolean isAdded=false;
        int errorCounter=0;

        displayUsersPage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add User button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (username!=null){
                username=username+testSerialNo+"_"+dbName;
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                logMessage("Username '"+username+"' is input.");
            }
            if (firstName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                logMessage("First Name '"+firstName+"' is input.");
            }
            if (lastName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                logMessage("Last Name '"+lastName+"' is input.");
            }

            if (phone!=null){
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                logMessage("Phone '"+phone+"' is input.");
            }
            if (password!=null){
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                logMessage("Password '"+password+"' is input.");
            }

            if (confirmPassword!=null){
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                logMessage("Confirm Password '"+confirmPassword+"' is input.");
            }

            if (email!=null){
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                logMessage("Email '"+email+"' is input.");
            }

            String userTypeName=null;
            if (type!=null){
                if (type.equals("EnableHR")){
                    userTypeName="WebAPI UserName";
                }else if (type.equals("Meridian")){
                    userTypeName="MCS UserName";
                }

                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                selectDBType.selectByVisibleText(type);
                logMessage("Type '"+type+"' is selected.");
            }
            if (description!=null){
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                logMessage("Description '"+description+"' is input.");
            }
            if (itemEnabled!=null){
                if (itemEnabled.equals("1")){
                    if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                        PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                        logMessage("Enabled is checked.");
                    }
                }
            }

            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(15000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, userTypeName+": "+username+"\r\n");
            createTextFile(fileFullName, userTypeName+" Password: "+password+"\r\n");
        }
        else{
            errorCounter++;
            logError("Add User screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }

    //Override below
    public static boolean EditUserInWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String payrollDatabaseAlias, String testSerialNo, WebDriver driver) throws IOException, InterruptedException, Exception {

        boolean isEdited=false;
        int errorCounter=0;

        displayUsersPage(driver);

        if (username!=null){
            username=username+testSerialNo;
        }
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).clear();
        Thread.sleep(5000);
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).click();
        Thread.sleep(5000);
        String singleS=null;
        for (int i=0;i<username.length();i++){
            singleS=Character.toString(username.charAt(i));
            PageObj_EditUserInWebAPIManage.textBox_Filter(driver).sendKeys(singleS);
            Thread.sleep(5000);
        }

        logMessage("Username '"+username+"' is input in filter.");
        logScreenshot(driver);

        String xpath_userList="//div[contains(@class,'ngCanvas')]//div[@ng-style='rowStyle(row)' and contains(., '"+username+"')]//a[contains(@class,'edit-icon ng-scope') and contains(text(), 'Edit')]";
        WebElement editItem=SystemLibrary.waitChild(xpath_userList, 120, 1, driver);

        if (editItem!=null){

            logMessage("Item with Username '"+username+"' is found.");
            editItem.click();
            Thread.sleep(5000);
            logMessage("Edit button is clicked.");
            logScreenshot(driver);

            if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
                /*if (username!=null){
                    username=username+testSerialNumber;
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                    logMessage("Username '"+username+"' is input.");
                }*/
                if (firstName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                    logMessage("First Name '"+firstName+"' is input.");
                }
                if (lastName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                    logMessage("Last Name '"+lastName+"' is input.");
                }

                if (phone!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                    logMessage("Phone '"+phone+"' is input.");
                }
                if (password!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                    logMessage("Password '"+password+"' is input.");
                }

                if (confirmPassword!=null){
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                    logMessage("Confirm Password '"+confirmPassword+"' is input.");
                }

                if (email!=null){
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                    logMessage("Email '"+email+"' is input.");
                }
                if (type!=null){
                    Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                    selectDBType.selectByVisibleText(type);
                    logMessage("Type '"+type+"' is selected.");
                }
                if (description!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                    logMessage("Description '"+description+"' is input.");
                }
                if (itemEnabled!=null){
                    if (itemEnabled.equals("1")){
                        if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                            PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                            logMessage("Enabled is checked.");
                        }
                    }
                }

                logMessage("Screenshot before move into next step.");
                logScreenshot(driver);

                if (payrollDatabaseAlias!=null){
                    payrollDatabaseAlias=payrollDatabaseAlias+testSerialNo;
                    driver.findElement(By.xpath("//a[@class='ng-binding' and contains(., 'Payroll Databases')]")).click();
                    Thread.sleep(5000);
                    logMessage("Payroll Database link is clicked.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[contains(@class,'box1 col-md-6')]//input[contains(@placeholder,'Filter')]")).sendKeys(payrollDatabaseAlias);
                    Thread.sleep(3000);
                    logMessage("Payroll Database Alias '"+payrollDatabaseAlias+"' is input in filter.");

                    String xpath_DBAliasName="//select[@id='bootstrap-duallistbox-nonselected-list_payrollDatabaseList']//option[contains(text(),'"+payrollDatabaseAlias+"')]";
                    WebElement selectItem=waitChild(xpath_DBAliasName, 10, 1, driver);
                    logScreenshot(driver);

                    if (selectItem!=null){
                        selectItem.click();
                        Thread.sleep(2000);
                        logMessage("Payroll Database Alias name is found.");
                        driver.findElement(By.xpath(xpath_DBAliasName)).click();
                        Thread.sleep(3000);

                        driver.findElement(By.xpath("//button[@title='Move selected']")).click();
                        Thread.sleep(3000);
                        logMessage("Move button is clicked.");

                        logMessage("Screenshot before click Update button.");
                        logScreenshot(driver);
                        driver.findElement(By.xpath("//button[contains(text(),'Update')]")).click();
                        Thread.sleep(3000);
                        logMessage("Update button is clcked.");
                        logScreenshot(driver);
                    }
                    else{
                        logError("Payroll Database Alias name is NOT found. User is NOT updated.");
                        errorCounter++;
                    }

                }



            }
            else{
                errorCounter++;
                logError("Edit User screen is NOT shown.");
            }
        }else{
            logError("Username '"+username+"' is NOT found.");
            errorCounter++;
        }

        if (errorCounter==0) isEdited=true;
        return isEdited;
    }

    //Override above
    public static boolean EditUserInWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String payrollDatabaseAlias, String testSerialNo, String dbName, WebDriver driver) throws IOException, InterruptedException, Exception {

        boolean isEdited=false;
        int errorCounter=0;

        displayUsersPage(driver);

        if (username!=null){
            username=username+testSerialNo+"_"+dbName;
        }
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).clear();
        Thread.sleep(5000);
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).click();
        Thread.sleep(5000);
        String singleS=null;
        for (int i=0;i<username.length();i++){
            singleS=Character.toString(username.charAt(i));
            PageObj_EditUserInWebAPIManage.textBox_Filter(driver).sendKeys(singleS);
            Thread.sleep(5000);
        }

        logMessage("Username '"+username+"' is input in filter.");
        logScreenshot(driver);

        String xpath_userList="//div[contains(@class,'ngCanvas')]//div[@ng-style='rowStyle(row)' and contains(., '"+username+"')]//a[contains(@class,'edit-icon ng-scope') and contains(text(), 'Edit')]";
        WebElement editItem=SystemLibrary.waitChild(xpath_userList, 120, 1, driver);

        if (editItem!=null){

            editItem.click();
            Thread.sleep(3000);
            if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
                /*if (username!=null){
                    username=username+testSerialNumber;
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                    logMessage("Username '"+username+"' is input.");
                }*/
                if (firstName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                    logMessage("First Name '"+firstName+"' is input.");
                }
                if (lastName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                    logMessage("Last Name '"+lastName+"' is input.");
                }

                if (phone!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                    logMessage("Phone '"+phone+"' is input.");
                }
                if (password!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                    logMessage("Password '"+password+"' is input.");
                }

                if (confirmPassword!=null){
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                    logMessage("Confirm Password '"+confirmPassword+"' is input.");
                }

                if (email!=null){
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                    logMessage("Email '"+email+"' is input.");
                }
                if (type!=null){
                    Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                    selectDBType.selectByVisibleText(type);
                    logMessage("Type '"+type+"' is selected.");
                }
                if (description!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                    logMessage("Description '"+description+"' is input.");
                }
                if (itemEnabled!=null){
                    if (itemEnabled.equals("1")){
                        if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                            PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                            logMessage("Enabled is checked.");
                        }
                    }
                }

                logMessage("Screenshot before move into next step.");
                logScreenshot(driver);

                if (payrollDatabaseAlias!=null){
                    payrollDatabaseAlias=payrollDatabaseAlias+testSerialNo+"_"+dbName;
                    driver.findElement(By.xpath("//a[@class='ng-binding' and contains(., 'Payroll Databases')]")).click();
                    Thread.sleep(3000);
                    logMessage("Payroll Database line is clicked.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[contains(@class,'box1 col-md-6')]//input[contains(@placeholder,'Filter')]")).sendKeys(payrollDatabaseAlias);
                    Thread.sleep(3000);
                    logMessage("Payroll Database Alias '"+payrollDatabaseAlias+"' is input in filter.");

                    String xpath_DBAliasName="//select[@id='bootstrap-duallistbox-nonselected-list_payrollDatabaseList']//option[contains(text(),'"+payrollDatabaseAlias+"')]";
                    WebElement selectItem=driver.findElement(By.xpath(xpath_DBAliasName));
                    logScreenshot(driver);

                    if (selectItem!=null){
                        selectItem.click();
                        Thread.sleep(2000);
                        logMessage("Payroll Database Alias name is found.");
                        driver.findElement(By.xpath(xpath_DBAliasName)).click();
                        Thread.sleep(3000);

                        driver.findElement(By.xpath("//button[@title='Move selected']")).click();
                        Thread.sleep(3000);
                        logMessage("Move button is clicked.");

                        logMessage("Screenshot before click Update button.");
                        logScreenshot(driver);
                        driver.findElement(By.xpath("//button[contains(text(),'Update')]")).click();
                        Thread.sleep(3000);
                        logMessage("Update button is clcked.");
                        logScreenshot(driver);
                    }
                    else{
                        logError("Payroll Database Alias name is NOT found. User is NOT updated.");
                        errorCounter++;
                    }

                }



            }
            else{
                errorCounter++;
                logError("Edit User screen is NOT shown.");
            }
        }else{
            logError("Username '"+username+"' is NOT found.");
            errorCounter++;
        }

        if (errorCounter==0) isEdited=true;
        return isEdited;
    }


    //Override below
    public static boolean addMultiUserInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addUserInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    //Override above
    public static boolean addMultiUserInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, String dbName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addUserInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, dbName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    //Override below
    public static boolean editMultiUserInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!EditUserInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    //Override above
    public static boolean editMultiUserInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, String dbName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!EditUserInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, dbName, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static void generateWebAPIKey(String testSerialNo) throws Exception {
        //Make sure new SN is created before run this file.
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();
        WebDriver driver=launchWebAPIManagementPortal(driverType);
        logonWebAPIPortal("AdminTest", "password", driver);

        logMessage("Step 1: Generate GUI Key.");
        guiKey=generateGUIKey(testSerialNo);
        //guiKey="0599c612-ac82-423e-812f-dcd0987b0441";

        logMessage("Step 2: Add Web API database.");
        myAssert.assertEquals(addMultiDatabaseInWebAPIManagePortal(10001, 10001, testSerialNo, driver), true, "Failed in Step 2: Add Web API database.");

        logMessage("Step 3: Add Web API user.");
        myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10001, 10001, testSerialNo, driver), true, "Failed in Step 3: Add Web API user.");

        logoutWebAPIPortal("AdminTest", driver);
        driver.close();
        Thread.sleep(10000);

        driver=launchWebAPIManagementPortal(driverType);
        logonWebAPIPortal("AdminTest", "password", driver);

        logMessage("Step 4: Edit Web API user.");
        myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10002, 10002, testSerialNo, driver), true, "Failed in Step 4: Edit Web API user.");

        logMessage("Step 5: Add MCS user.");
        myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10011, 10011, testSerialNo, driver), true, "Failed in Step 5: Add MCS user.");

        logoutWebAPIPortal("AdminTest", driver);
        driver.close();
        driver=launchWebAPIManagementPortal(3);
        logonWebAPIPortal("AdminTest", "password", driver);

        logMessage("Step 6: Edit MCS user.");
        myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10012, 10012, testSerialNo, driver), true, "Failed in Step 6: Edit MCS user.");

        String fileName="GUIKey_"+testSerialNo+".txt";
        String fileFullName=dataSourcePath+fileName;

        logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);

        driver.close();
        myAssert.assertAll();
    }

    public static boolean logoutWebAPIPortal(String username, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone=false;
        int errorCounter=0;

        WebElement usernameButton=SystemLibrary.waitChild("//a[@class='dropdown-toggle ng-binding' and contains(., '"+username+"')]", 10, 1, driver);
        if (usernameButton!=null){
            usernameButton.click();
            Thread.sleep(2000);
            logMessage("Username button is clicked.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[contains(text(),'Logout')]")).click();
            Thread.sleep(3000);
            logMessage("Logout button is clicked.");
            logScreenshot(driver);

            isDone=true;

        }else{
            logError("Logout button is NOT shown.");
        }
        return isDone;
    }

    public static void generateWebAPIKey(String testSerialNo, String appType) throws Exception {
        //Make sure new SN is created before run this file.
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();
        WebDriver driver=null;
        String fileName=null;
        String fileFullName=null;

        if (appType==null){
            appType="Meridian";
        }

        switch (appType){
            case "Meridian":

                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 1: Generate GUI Key.");
                guiKey=generateGUIKey(testSerialNo, appType);
                //guiKey="0599c612-ac82-423e-812f-dcd0987b0441";

                logMessage("Step 2: Add Web API database.");
                myAssert.assertEquals(addMultiDatabaseInWebAPIManagePortal(10001, 10001, testSerialNo, driver), true, "Failed in Step 2: Add Web API database.");

                logMessage("Step 3: Add Web API user.");
                myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10001, 10001, testSerialNo, driver), true, "Failed in Step 3: Add Web API user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 4: Edit Web API user.");
                myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10002, 10002, testSerialNo, driver), true, "Failed in Step 4: Edit Web API user.");

                logMessage("Step 5: Add MCS user.");
                myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10011, 10011, testSerialNo, driver), true, "Failed in Step 5: Add MCS user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 6: Edit MCS user.");
                myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10012, 10012, testSerialNo, driver), true, "Failed in Step 6: Edit MCS user.");

                fileName="GUIKey_"+testSerialNo+".txt";
                fileFullName=dataSourcePath+fileName;

                logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);

                driver.close();
                break;

            case "WageEasy":
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 1: Generate GUI Key.");
                guiKey=generateGUIKey(testSerialNo, appType);
                //guiKey="0599c612-ac82-423e-812f-dcd0987b0441";

                logMessage("Step 2: Add Web API database.");
                myAssert.assertEquals(addMultiDatabaseInWebAPIManagePortal(10101, 10101 , testSerialNo, driver), true, "Failed in Step 2: Add Web API database.");

                logMessage("Step 3: Add Web API user.");
                myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10101, 10101, testSerialNo, driver), true, "Failed in Step 3: Add Web API user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 4: Edit Web API user.");
                myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10102, 10102, testSerialNo, driver), true, "Failed in Step 4: Edit Web API user.");

                logMessage("Step 5: Add MCS user.");
                myAssert.assertEquals(addMultiUserInWebAPIManagePortal(10111, 10111, testSerialNo, driver), true, "Failed in Step 5: Add MCS user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 6: Edit MCS user.");
                myAssert.assertEquals(editMultiUserInWebAPIManagePortal(10112, 10112, testSerialNo, driver), true, "Failed in Step 6: Edit MCS user.");

                fileName="GUIKey_"+testSerialNo+".txt";
                fileFullName=dataSourcePath+fileName;

                logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);

                driver.close();

                break;
        }

        if (fileFullName!=null){
            copyFile(fileFullName, testKeysPath+fileName);
        }

        myAssert.assertAll();
    }

    public static String generateGUIKey2(String testSerialNo, String appType, String dbName) throws InterruptedException, IOException {
        WebDriver driver=launchGUIPage(3);
        logMessage("GUI Generator page is show.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//input[@id='btnGenerate']")).click();
        Thread.sleep(15);

        String GUIKey=driver.findElement(By.xpath("//textarea[@name='txtResults']")).getText();
        logScreenshot(driver);
        logMessage("GUI key is generated as below:");
        logMessage(GUIKey);

        String fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
        String fileFullName=dataSourcePath+fileName;

        createTextFile(fileFullName, "GUI Key: "+GUIKey+"\r\n");
        driver.close();

        return GUIKey;
    }

    public static void generateWebAPIKey2(String testSerialNo, String appType, String dbName) throws Exception {
        //Make sure new SN is created before run this file.
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();
        WebDriver driver=null;
        String fileName=null;
        String fileFullName=null;

        if (appType==null){
            appType="Meridian";
        }

        switch (appType){
            case "Meridian":

                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 1: Generate GUI Key.");
                guiKey=generateGUIKey2(testSerialNo, appType, dbName);
                //guiKey="0599c612-ac82-423e-812f-dcd0987b0441";

                logMessage("Step 2: Add Web API database.");
                myAssert.assertEquals(addMultiDatabase2InWebAPIManagePortal(20001, 20001, testSerialNo, driver), true, "Failed in Step 2: Add Web API database.");

                logMessage("Step 3: Add Web API user.");
                myAssert.assertEquals(addMultiUser2InWebAPIManagePortal(20001, 20001, testSerialNo, driver), true, "Failed in Step 3: Add Web API user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 4: Edit Web API user.");
                myAssert.assertEquals(editMultiUser2InWebAPIManagePortal(20002, 20002, testSerialNo, driver), true, "Failed in Step 4: Edit Web API user.");

                logMessage("Step 5: Add MCS user.");
                myAssert.assertEquals(addMultiUser2InWebAPIManagePortal(20011, 20011, testSerialNo, driver), true, "Failed in Step 5: Add MCS user.");

                logoutWebAPIPortal("AdminTest", driver);
                driver.close();
                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 6: Edit MCS user.");
                myAssert.assertEquals(editMultiUser2InWebAPIManagePortal(20012, 20012, testSerialNo, driver), true, "Failed in Step 6: Edit MCS user.");

                fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
                fileFullName=dataSourcePath+fileName;

                logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);

                driver.close();
                break;
        }

        if (fileFullName!=null){
            copyFile(fileFullName, testKeysPath+fileName);
        }

        myAssert.assertAll();
    }

    public static boolean addMultiDatabase2InWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Database in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddDBInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddDBInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addDatabase2InWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Database in Web API Manage Portal.");

        if (errorCounter==00) isPassed=true;
        return isPassed;
    }

    public static boolean addDatabase2InWebAPIManagePortal(String databaseAlias, String databaseName, String databaseToken, String serverName, String databaseType, String dbName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {

        boolean isAdded=false;
        int errorCounter=0;

        displayDatabasePage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add Database button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (databaseAlias!=null){
                databaseAlias=databaseAlias+ testSerialNo;
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseAlias(driver).sendKeys(databaseAlias);
                logMessage("Database Alias '"+databaseAlias+"' is input.");
            }
            if (databaseName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseName(driver).sendKeys(databaseName);
                logMessage("Database Name '"+databaseName+"' is input.");
            }

            if (guiKey!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_DatabaseToken(driver).sendKeys(guiKey);
                logMessage("Database Token '"+guiKey+"' is input.");
            }

            if (serverName!=null){
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).clear();
                PageObj_EditDataDetailsInWebAPIManage.textBox_ServerName(driver).sendKeys(serverName);
                logMessage("Server Name '"+serverName+"' is input.");
            }

            if (databaseType!=null){
                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboDbType']")));
                selectDBType.selectByVisibleText(databaseType);
                logMessage("Database Type '"+databaseType+"' is selected.");
            }

            Thread.sleep(10000);
            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            //Jim added on 22/11/2019
            WebElement button_Add=driver.findElement(By.xpath("//button[text()='Add']"));
            new Actions(driver).moveToElement(button_Add).perform();
            button_Add.click();
            Thread.sleep(10000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, "databaseAlias: "+databaseAlias+"\r\n");

        }
        else{
            errorCounter++;
            logError("Add database screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }


    public static boolean addMultiUser2InWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addUser2InWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][15], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean addUser2InWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String dbName,String testSerialNo, WebDriver driver) throws Exception {

        boolean isAdded=false;
        int errorCounter=0;

        displayUsersPage(driver);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add User button is clicked.");
        logScreenshot(driver);

        if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
            if (username!=null){
                username=username+testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                logMessage("Username '"+username+"' is input.");
            }
            if (firstName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                logMessage("First Name '"+firstName+"' is input.");
            }
            if (lastName!=null){
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                logMessage("Last Name '"+lastName+"' is input.");
            }

            if (phone!=null){
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                logMessage("Phone '"+phone+"' is input.");
            }
            if (password!=null){
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                logMessage("Password '"+password+"' is input.");
            }

            if (confirmPassword!=null){
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                logMessage("Confirm Password '"+confirmPassword+"' is input.");
            }

            if (email!=null){
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                logMessage("Email '"+email+"' is input.");
            }

            String userTypeName=null;
            if (type!=null){
                if (type.equals("EnableHR")){
                    userTypeName="WebAPI UserName";
                }else if (type.equals("Meridian")){
                    userTypeName="MCS UserName";
                }else if (type.equals("EasyPay")){
                    userTypeName="MCS UserName";
                }else if (type.equals("WageEasy")){
                    userTypeName="MCS UserName";
                }

                Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                selectDBType.selectByVisibleText(type);
                logMessage("Type '"+type+"' is selected.");
            }
            if (description!=null){
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                logMessage("Description '"+description+"' is input.");
            }
            if (itemEnabled!=null){
                if (itemEnabled.equals("1")){
                    if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                        PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                        logMessage("Enabled is checked.");
                    }
                }
            }

            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(15000);
            logMessage("Add button is clicked.");
            logScreenshot(driver);

            String fileName="GUIKey_"+testSerialNo+"_"+dbName+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, userTypeName+": "+username+"\r\n");
            createTextFile(fileFullName, userTypeName+" Password: "+password+"\r\n");
        }
        else{
            errorCounter++;
            logError("Add User screen is NOT shown.");
        }


        if (errorCounter==0) isAdded=true;
        return isAdded;
    }

    public static boolean editMultiUser2InWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Editing multi Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!EditUser2InWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Editing multi Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean EditUser2InWebAPIManagePortal(String username, String firstName, String lastName, String phone, String password, String confirmPassword, String email, String type, String description, String itemEnabled, String payrollDatabaseAlias, String testSerialNo, WebDriver driver) throws IOException, InterruptedException, Exception {

        boolean isEdited=false;
        int errorCounter=0;

        displayUsersPage(driver);

        if (username!=null){
            username=username+testSerialNo;
        }
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).clear();
        Thread.sleep(5000);
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).click();
        Thread.sleep(5000);
        String singleS=null;
        for (int i=0;i<username.length();i++){
            singleS=Character.toString(username.charAt(i));
            PageObj_EditUserInWebAPIManage.textBox_Filter(driver).sendKeys(singleS);
            Thread.sleep(5000);
        }

        logMessage("Username '"+username+"' is input in filter.");
        logScreenshot(driver);

        String xpath_userList="//div[contains(@class,'ngCanvas')]//div[@ng-style='rowStyle(row)' and contains(., '"+username+"')]//a[contains(@class,'edit-icon ng-scope') and contains(text(), 'Edit')]";
        WebElement editItem=SystemLibrary.waitChild(xpath_userList, 120, 1, driver);

        if (editItem!=null){

            logMessage("Item with Username '"+username+"' is found.");
            editItem.click();
            Thread.sleep(5000);
            logMessage("Edit button is clicked.");
            logScreenshot(driver);

            if (waitChild("//tab-heading[@class='ng-scope' and contains(., 'Basic Details')]", 10, 1, driver)!=null){
                /*if (username!=null){
                    username=username+testSerialNumber;
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Username(driver).sendKeys(username);
                    logMessage("Username '"+username+"' is input.");
                }*/
                if (firstName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(firstName);
                    logMessage("First Name '"+firstName+"' is input.");
                }
                if (lastName!=null){
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_FirstName(driver).sendKeys(lastName);
                    logMessage("Last Name '"+lastName+"' is input.");
                }

                if (phone!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Phone(driver).sendKeys(phone);
                    logMessage("Phone '"+phone+"' is input.");
                }
                if (password!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Password(driver).sendKeys(password);
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(password);
                    logMessage("Password '"+password+"' is input.");
                }

                if (confirmPassword!=null){
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_ConfirmPassword(driver).sendKeys(confirmPassword);
                    logMessage("Confirm Password '"+confirmPassword+"' is input.");
                }

                if (email!=null){
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_EmailAddress(driver).sendKeys(email);
                    logMessage("Email '"+email+"' is input.");
                }
                if (type!=null){
                    Select selectDBType=new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                    selectDBType.selectByVisibleText(type);
                    logMessage("Type '"+type+"' is selected.");
                }
                if (description!=null){
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                    PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                    logMessage("Description '"+description+"' is input.");
                }
                if (itemEnabled!=null){
                    if (itemEnabled.equals("1")){
                        if (!PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).isSelected()){
                            PageObj_EditUserInWebAPIManage.checkbox_Enable(driver).click();
                            logMessage("Enabled is checked.");
                        }
                    }
                }

                logMessage("Screenshot before move into next step.");
                logScreenshot(driver);

                if (payrollDatabaseAlias!=null){
                    payrollDatabaseAlias=payrollDatabaseAlias+testSerialNo;
                    driver.findElement(By.xpath("//a[@class='ng-binding' and contains(., 'Payroll Databases')]")).click();
                    Thread.sleep(5000);
                    logMessage("Payroll Database link is clicked.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[contains(@class,'box1 col-md-6')]//input[contains(@placeholder,'Filter')]")).sendKeys(payrollDatabaseAlias);
                    Thread.sleep(3000);
                    logMessage("Payroll Database Alias '"+payrollDatabaseAlias+"' is input in filter.");

                    String xpath_DBAliasName="//select[@id='bootstrap-duallistbox-nonselected-list_payrollDatabaseList']//option[contains(text(),'"+payrollDatabaseAlias+"')]";
                    WebElement selectItem=waitChild(xpath_DBAliasName, 10, 1, driver);
                    logScreenshot(driver);

                    if (selectItem!=null){
                        selectItem.click();
                        Thread.sleep(2000);
                        logMessage("Payroll Database Alias name is found.");
                        driver.findElement(By.xpath(xpath_DBAliasName)).click();
                        Thread.sleep(3000);

                        driver.findElement(By.xpath("//button[@title='Move selected']")).click();
                        Thread.sleep(3000);
                        logMessage("Move button is clicked.");

                        logMessage("Screenshot before click Update button.");
                        logScreenshot(driver);
                        driver.findElement(By.xpath("//button[contains(text(),'Update')]")).click();
                        Thread.sleep(3000);
                        logMessage("Update button is clcked.");
                        logScreenshot(driver);
                    }
                    else{
                        logError("Payroll Database Alias name is NOT found. User is NOT updated.");
                        errorCounter++;
                    }

                }



            }
            else{
                errorCounter++;
                logError("Edit User screen is NOT shown.");
            }
        }else{
            logError("Username '"+username+"' is NOT found.");
            errorCounter++;
        }

        if (errorCounter==0) isEdited=true;
        return isEdited;
    }





    ////////////// Add Customer In Sage MicrOpay Web API Management Portal//////////////////////

    public static boolean addCustomerInWebAPIManagePortal(String totusID, String customerName, String itemEnabled, String testSerialNo, WebDriver driver) throws Exception {

        boolean isAdded = false;
        int errorCounter = 0;

        displayCustomersPage(driver);

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        logMessage("Add Customer button is clicked.");
        logScreenshot(driver);

        PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);
        logMessage("Next button is clicked on Add a new customer - Welcome Page.");
        logScreenshot(driver);


        if (waitChild("//div[@class='panel-title pull-left ng-binding' and contains(., 'Add a new customer - Customer information')]", 10, 1, driver) != null) {

            if (totusID != null) {
                totusID = totusID + testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_CustomerTotusID(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerTotusID(driver).sendKeys(totusID);
                Thread.sleep(2000);
                logMessage("TotusID '" + totusID + "' is input.");
            }

            if (customerName != null) {
                customerName = customerName + testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_CustomerName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerName(driver).sendKeys(customerName);
                Thread.sleep(2000);
                logMessage("Customer Name '" + customerName + "' is input.");
            }

            String initial=testSerialNo.substring(0, 2);
            String lastNumber=testSerialNo.replace(initial, "");
            String phoneNumber=lastNumber+lastNumber+lastNumber;

            PageObj_EditUserInWebAPIManage.textBox_CustomerPhone(driver).clear();
            PageObj_EditUserInWebAPIManage.textBox_CustomerPhone(driver).sendKeys(phoneNumber);
            Thread.sleep(2000);
            logMessage("Customer Phone '" + phoneNumber + "' is input.");

            PageObj_EditUserInWebAPIManage.textBox_CustomerEmail(driver).clear();
            PageObj_EditUserInWebAPIManage.textBox_CustomerEmail(driver).sendKeys("testlast" + testSerialNo + "@sageautomation.com");
            Thread.sleep(2000);
            logMessage("Customer Email '" + "testlast" + testSerialNo + "@sageautomation.com" + "' is input.");
            logScreenshot(driver);


            if (itemEnabled != null) {
                if (itemEnabled.equals("1")) {
                    if (!PageObj_EditUserInWebAPIManage.checkbox_CustomerEnable(driver).isSelected()) {
                        PageObj_EditUserInWebAPIManage.checkbox_CustomerEnable(driver).click();
                        Thread.sleep(3000);
                        logMessage("Enabled is checked at Add a new customer - Customer information Page.");
                    }
                }
            }

            String fileName="GUIKey_"+testSerialNo+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, "Totus Customer ID: "+totusID+"\r\n");
            createTextFile(fileFullName,  "Customer Name: " + customerName + "\r\n");
            Thread.sleep(2000);


            PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);
            logMessage("Next button is clicked on Add a new customer - Customer information.");
            logScreenshot(driver);
        }

        else {
            errorCounter++;
            logError("Add a new customer - Customer information is NOT shown.");
        }

        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }

    public static boolean addMultiCustomerInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Customer Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addCustomerInWebAPIManagePortal(cellData[i][13], cellData[i][14], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Customer Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean addCustomerUserInWebAPIManagePortal(String username, String type, String description, String itemEnabled, String testSerialNo, WebDriver driver) throws Exception {

        boolean isAdded = false;
        int errorCounter = 0;

        if (waitChild("//div[@class='panel-title pull-left ng-binding' and contains(., 'Add a new customer - User information')]", 10, 1, driver) != null) {

            if (username != null) {
                username = username + testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_CustomerUsername(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerUsername(driver).sendKeys(username);
                Thread.sleep(2000);
                logMessage("Username '" + username + "' is input.");
            }

            if (description != null) {
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_Description(driver).sendKeys(description);
                Thread.sleep(2000);
                logMessage("Description '" + description + "' is input.");
            }

            String userTypeName = null;
            if (type != null) {
                if (type.equals("EnableHR")) {
                    userTypeName = "WebAPI UserName";
                } else if (type.equals("Meridian")) {
                    userTypeName = "MCS UserName";
                } else if (type.equals("EasyPay")) {
                    userTypeName = "MCS UserName";
                } else if (type.equals("WageEasy")) {
                    userTypeName = "MCS UserName";
                }

                Select selectDBType = new Select(driver.findElement(By.xpath("//select[@name='cboUserType']")));
                selectDBType.selectByVisibleText(type);
                Thread.sleep(3000);
                logMessage("Type '" + type + "' is selected.");
            }

            if (itemEnabled != null) {
                if (itemEnabled.equals("1")) {
                    if (!PageObj_EditUserInWebAPIManage.checkbox_CustomerEnable(driver).isSelected()) {
                        PageObj_EditUserInWebAPIManage.checkbox_CustomerEnable(driver).click();
                        Thread.sleep(3000);
                        logMessage("Enabled is checked.");
                    }
                }
            }

            String fileName="GUIKey_"+testSerialNo+".txt";
            String fileFullName=dataSourcePath+fileName;
            createTextFile(fileFullName, userTypeName + ": " + username + "\r\n");
            Thread.sleep(2000);


            String password = PageObj_EditUserInWebAPIManage.textBox_CustomerPassword(driver).getAttribute("value");
            createTextFile(fileFullName, userTypeName + " Password: " + password + "\r\n");
            Thread.sleep(2000);

            logMessage("Screenshot before click Add button for '" + type + "'.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(3000);
            logMessage("Add button is clicked in User information Page.");
            logScreenshot(driver);
            logMessage("Screenshot after click Add button for '" + type + "' in User information Page.");

            /*
           PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);
           logMessage("Next button is clicked on Add a new customer - User information.");
           logScreenshot(driver);*/

        }

        else {
        	
            errorCounter++;
            logError("Add a new customer - User information screen is NOT shown.");
        }


        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }


    public static boolean addMultiCustomerUserInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Customer Users in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddUserInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddUserInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addCustomerUserInWebAPIManagePortal(cellData[i][2], cellData[i][9], cellData[i][10], cellData[i][11], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Customer Users in Web API Manage Portal.");

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean addCustomerDBInfoInWebAPIManagePortal(String databaseAlias, String databaseName, String server,  String type, String testSerialNo, WebDriver driver) throws Exception {

        boolean isAdded = false;
        int errorCounter = 0;

        if (waitChild("//div[@class='panel-title pull-left ng-binding' and contains(., 'Add a new customer - Database information')]", 10, 1, driver) != null) {

            if (databaseAlias!=null){
                databaseAlias=databaseAlias+ testSerialNo;
                PageObj_EditUserInWebAPIManage.textBox_CustomerDBAlias(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerDBAlias(driver).sendKeys(databaseAlias);
                Thread.sleep(2000);
                logMessage("Database Alias '"+databaseAlias+"' is input.");
            }

            if (databaseName!=null){
                PageObj_EditUserInWebAPIManage.textBox_CustomerDBName(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerDBName(driver).sendKeys(databaseName);
                Thread.sleep(2000);
                logMessage("Database Name '"+databaseName+"' is input.");
            }


            if (server!=null){
                PageObj_EditUserInWebAPIManage.textBox_CustomerServer(driver).clear();
                PageObj_EditUserInWebAPIManage.textBox_CustomerServer(driver).sendKeys(server);
                Thread.sleep(2000);
                logMessage("Server Name '"+server+"' is input.");
            }

            String userTypeName = null;
            if (type != null) {
                if (type.equals("EnableHR")) {
                    userTypeName = "EnableHR";
                } else if (type.equals("Meridian")) {
                    userTypeName = "Meridian";
                } else if (type.equals("EasyPay")) {
                    userTypeName = "EasyPay";
                } else if (type.equals("WageEasy")) {
                    userTypeName = "WageEasy";
                }

                Select selectDBType = new Select(driver.findElement(By.xpath("//select[@name='cboDbType']")));
                selectDBType.selectByVisibleText(type);
                Thread.sleep(3000);
                logMessage("Type '" + type + "' is selected.");
            }

            String fileName="GUIKey_"+testSerialNo+".txt";
            String fileFullName=dataSourcePath+fileName;

            createTextFile(fileFullName, "databaseAlias: "+databaseAlias+"\r\n");
            Thread.sleep(2000);
            String apikey = PageObj_EditUserInWebAPIManage.textBox_CustomerAPIKey(driver).getAttribute("value");
            createTextFile(fileFullName, "GUI Key: "+apikey+"\r\n");
            Thread.sleep(2000);

            driver.findElement(By.xpath("//form[@name='databaseForm']//button[@class='btn btn-xs btn-primary'][contains(text(),'Add')]")).click();
            // driver.findElement(By.xpath("//button[text()='Add']")).click();
            Thread.sleep(3000);
            logMessage("Add button is clicked in Database information Page.");
            logScreenshot(driver);
            logMessage("Screenshot after click Add button in Database information Page.");


            PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);
            logMessage("Next button is clicked on Add a new customer - Database information screen.");
            Thread.sleep(3000);
            logScreenshot(driver);

        }

        else {
            errorCounter++;
            logError("Add a new customer - Database information screen is NOT shown.");
        }


        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }


    public static boolean addMultiCustomerDBInfoInWebAPIManagePortal(int startSerialNo, int endSerialNo, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        SystemLibrary.logMessage("--- Start Adding multi Customer Database in Web API Manage Portal from row " + startSerialNo + " to " + endSerialNo + " in \"AddDBInWebAPIPortal\" sheet.");

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,50,"AddDBInWebAPIPortal");
        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!addCustomerDBInfoInWebAPIManagePortal(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][6], testSerialNo, driver)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("*** End of Adding multi Customer Database in Web API Manage Portal.");

        if (errorCounter==00) isPassed=true;
        return isPassed;
    }



    public static boolean submitCustomerInWebAPIManagePortal(WebDriver driver) throws IOException, InterruptedException, Exception {

        boolean isAdded = false;
        int errorCounter = 0;

        if (waitChild("//div[@class='panel-title pull-left ng-binding' and contains(., 'Add a new customer - Confirmation')]", 10, 1, driver) != null) {

            PageObj_EditUserInWebAPIManage.click_ButtonCustomer_SubmitRequest(driver).click();
            Thread.sleep(10000);
            logMessage("Submit Request button is clicked on Add a new customer - Customer information screen.");
            logScreenshot(driver);


        } else {
            errorCounter++;
            logError("Add a new customer - Confirmation screen is NOT shown.");
        }


        if (waitChild("//div[@class='panel-title pull-left ng-binding' and contains(., 'Add a new customer - Done')]", 10, 1, driver) != null) {

            logMessage("Add a new customer - Done screen Dispalyed.");
            logScreenshot(driver);
            Thread.sleep(2000);

        }  else {
            errorCounter++;
            logError("Add a new customer - Done is NOT shown.");
        }


        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }


    public static boolean validateAPIVersionInDatabase( String Databasename, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Login Admin First
        boolean isDone = false;
        int errorCounter = 0;

        displayDatabasePage(driver);
        Thread.sleep(3000);
        if (Databasename!=null){
            Databasename=Databasename+testSerialNo;
        }

        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).clear();
        Thread.sleep(3000);
        PageObj_EditUserInWebAPIManage.textBox_Filter(driver).click();
        Thread.sleep(3000);
        String singleS=null;
        for (int i=0;i<Databasename.length();i++){
            singleS=Character.toString(Databasename.charAt(i));
            PageObj_EditUserInWebAPIManage.textBox_Filter(driver).sendKeys(singleS);
            Thread.sleep(5000);
        }

        logMessage("Database name '"+Databasename+"' is input in filter.");
        logScreenshot(driver);

        if (storeFileName != null) {
            WebElement lable_Welcome = SystemLibrary.waitChild("//div[@class='panel-title pull-left ng-binding'][contains(., 'Databases')]", 10, 1, driver);
            if (lable_Welcome != null) {
                logMessage("Databases Page is shown.");
                WebElement Introduction = SystemLibrary.waitChild("//div[@class='panel-body']", 10, 1, driver);
                if (Introduction != null) {
                    if (!SystemLibrary.validateTextValueInWebElementInUse(Introduction, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                } else {
                    logError("Databases body panel is NOT shown.");
                    errorCounter++;
                }
            } else {
                logError("Databases message is NOT shown correctly.");
                errorCounter++;
            }

        }

        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }




    public static boolean validateAPIVersionInDatabase_Main(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo+" in AddDBInWebAPIPortal sheet.");
        boolean isLogon=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"AddDBInWebAPIPortal");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!validateAPIVersionInDatabase(cellData[i][2], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], testSerialNo, emailDomainName, driver )) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in ESS.");
        return isLogon;
    }



    public static void generateWebAPIKeyViaCustomer(String testSerialNo, String emailDomainName, String appType) throws Exception {
        //Make sure new SN is created before run this file.
        boolean isDone=false;
        SoftAssert myAssert=new SoftAssert();
        WebDriver driver=null;
        String fileName=null;
        String fileFullName=null;

        if (appType==null){
            appType="Meridian";
        }

        switch (appType){
            case "Meridian":

                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 1: Add a new customer - Customer information for Meridian DB");
                myAssert.assertEquals(addMultiCustomerInWebAPIManagePortal(10201, 10201, testSerialNo, driver), true, "Failed in Step 1: Add a new customer - Customer information for Meridian DB");

                logMessage("Step 2: Add a new customer API User Details - User information for Meridian DB");
                myAssert.assertEquals(addMultiCustomerUserInWebAPIManagePortal(10211, 10211, testSerialNo, driver), true, "Failed in Step 2: Add a new customer API User Details - User information for Meridian DB");

                logMessage("Step 3: Add a new customer MCS User Details - User information for Meridian DB");
                myAssert.assertEquals(addMultiCustomerUserInWebAPIManagePortal(10221, 10221, testSerialNo, driver), true, "Failed in Step 2: Add a new customer MCS User Details - User information for Meridian DB");

                logMessage("Next button is clicked on Add a new customer - User information");
                PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);

                logMessage("Step 4: Add a new customer - Database information for Meridian DB");
                myAssert.assertEquals(addMultiCustomerDBInfoInWebAPIManagePortal(10201, 10201, testSerialNo, driver), true, "Failed in Step 4: Add a new customer - Database information for Meridian DB");

                logMessage("Step 5: Submit multi Customer in Web API Manage Portal for Meridian DB");
                myAssert.assertEquals(submitCustomerInWebAPIManagePortal(driver), true, "Failed in Step 5: Submit multi Customer in Web API Manage Portal for Meridian DB");

                logMessage("Step 6: Validate WebAPI Version for Micropay in TAPI tool");
                myAssert.assertEquals(validateAPIVersionInDatabase_Main(10211, 10211, testSerialNo, emailDomainName, driver), true, "Failed in Step 6: Validate WebAPI Version for Micropay in TAPI tool");


                fileName="GUIKey_"+testSerialNo+".txt";
                fileFullName=dataSourcePath+fileName;

                logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);
                driver.close();

                break;

            case "WageEasy":

                driver=launchWebAPIManagementPortal(3);
                logonWebAPIPortal("AdminTest", "password", driver);

                logMessage("Step 1: Add a new customer - Customer information for WageEasy DB");
                myAssert.assertEquals(addMultiCustomerInWebAPIManagePortal(10301, 10301, testSerialNo, driver), true, "Failed in Step 1: Add a new customer - Customer information for WageEasy DB");

                logMessage("Step 2: Add a new customer API User Details - User information for WageEasy DB");
                myAssert.assertEquals(addMultiCustomerUserInWebAPIManagePortal(10311, 10311, testSerialNo, driver), true, "Failed in Step 2: Add a new customer API User Details - User information for WageEasy DB");

                logMessage("Step 3: Add a new customer MCS User Details - User information for WageEasy DB");
                myAssert.assertEquals(addMultiCustomerUserInWebAPIManagePortal(10321, 10321, testSerialNo, driver), true, "Failed in Step 2: Add a new customer MCS User Details - User information for WageEasy DB");

                logMessage("Next button is clicked on Add a new customer - User information.");
                PageObj_EditUserInWebAPIManage.click_ButtonCustomer_Next(driver);

                logMessage("Step 4: Add a new customer - Database information for WageEasy DB");
                myAssert.assertEquals(addMultiCustomerDBInfoInWebAPIManagePortal(10301, 10301, testSerialNo, driver), true, "Failed in Step 4: Add a new customer - Database information for WageEasy DB");

                logMessage("Step 5: Submit multi Customer in Web API Manage Portal for WageEasy DB");
                myAssert.assertEquals(submitCustomerInWebAPIManagePortal(driver), true, "Failed in Step 5: Submit multi Customer in Web API Manage Portal for Meridian DB");

                logMessage("Step 6: Validate WebAPI Version for WageEasy in TAPI tool");
                myAssert.assertEquals(validateAPIVersionInDatabase_Main(10311, 10311, testSerialNo, emailDomainName, driver), true, "Failed in Step 6: Validate WebAPI Version for WageEasy in TAPI tool");

                fileName="GUIKey_"+testSerialNo+".txt";
                fileFullName=dataSourcePath+fileName;

                logMessage("Web API Log file is generated. Click here to access: "+ serverUrlAddress+"DataSource/"+fileName);

                driver.close();
                break;
        }

        if (fileFullName!=null){
            copyFile(fileFullName, testKeysPath+fileName);
        }

        myAssert.assertAll();
    }



    ///////////// Debug her //////////////

    @Test
    public static void test1() throws IOException, InterruptedException {
        WebDriver driver=launchWebAPIManagementPortal(3);
        logonWebAPIPortal("AdminTest", "password", driver);

        Thread.sleep(5000);

        logoutWebAPIPortal("AdminTest", driver);
        driver.close();

    }




}
