package Lib;

import PageObject.*;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import javax.mail.MessagingException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static Lib.SystemLibrary.*;
import static java.lang.Math.abs;

public class GeneralBasic {

    //public static String url_ESS = "https://ess-prod.micropay.com.au";
    public static String url_ESS_PerformanceUAT = "https://ess-uat.cloud.micropay.com.au";
    public static String url_WebMail = "http://www.outlook.com";
    public static String url_SageWebAPI = "https://au-east-micropayapi-dev-api.azurewebsites.net/Explorer/Api/GET-api-EmployeeBasic-id";

    public static String catchupMailAddress = "esstester101@gmail.com";
    public static String catchupMailPassword = "ESSTesting99";

    public static String url_SageCloud = "https://taxtest.sageinternal.com.au/TaxDemo3/Landing/Index?ReturnUrl=%2FTaxDemo3%2F#/practiceList";

    public static int daysInCalendar = 27;
    //public static int daysInCalendar=19;


    public static String getTestSerailNumber_Main(int startSerialNo, int endSerialNo) throws IOException, InterruptedException {
        String testSerialNumber = null;
        List<String> tenantDetails = SystemLibrary.getTenantDetails("C:\\TestAutomationProject\\ESSRegTest\\UserList\\TenantDetails.txt");

        int totalLineNumber = tenantDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = tenantDetails.get(i);
            if (currentItem.contains("Admin Login =")) {
                testSerialNumber = currentItem.replace("Admin Login =", "");
                testSerialNumber = testSerialNumber.replace("Test", "");
                testSerialNumber = testSerialNumber.replace(".Admin", "");
                break;
            }
        }

        SystemLibrary.logMessage("The current Test Serial Number is '" + testSerialNumber + "'.");
        return testSerialNumber;
    }

    public static String getTestSerailNumber_Main_OLD(int startSerialNo, int endSerialNo) throws IOException, InterruptedException {
        //SystemLibrary.logMessage("--- Start log in ESS from row " + startSerialNo + " to " + endSerialNo);
        String testSerialNumber = null;
        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 20, "UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    testSerialNumber = (cellData[i][9]);
                } else {
                    //System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo >= endSerialNo) break;
            }

        }

        SystemLibrary.logMessage("Start Test with Serial Number " + testSerialNumber + ".");
        return testSerialNumber;
    }

    public static WebDriver launchSageCloud(int driverType) throws InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver(url_SageCloud, driverType);
        return driver;
    }

    public static WebDriver launchESS(String url_ESS, int driverType) throws InterruptedException, IOException {
        WebDriver driver = SystemLibrary.launchWebDriver(url_ESS, driverType);
        logScreenshot(driver);
        return driver;
    }

    public static WebDriver launchTestReport(int driverType) throws InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver("http://ess-test.australiaeast.cloudapp.azure.com/Test.html", driverType);
        return driver;
    }

    public static WebDriver launchESS_Performance(int driverType) throws MalformedURLException, InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver_Performance(url_ESS_PerformanceUAT, driverType);
        return driver;
    }

    public static boolean logonESS(String username, String password, String firstName, String lastName, String middleName, String preferredName, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        boolean isSignin = false;
        int errorCounter = 0;
        logMessage("-------------------------------");
        logMessage("--- Start logon ESS.");

        waitSpinnerDisappear(60, driver);
        waitChild("//h1[contains(text(),'Sign in')]", 60, 1, driver);

        //logDebug("Initial Username: '"+username+"'");
        if (username.equals("Test.Admin")) {
            //username="Test"+getTestSerailNumber_Main(101, 101)+".Admin";
            username = "Test" + testSerialNo + ".Admin";
            firstName = username;
            password = getAdminPassword_FromTenantDetails(testSerialNo);
        } else if (username.equals("ExtraAdminUser")) {
            username = getUsername_FromExtraAdminDetails();
            password = getPassword_FromExtraAdminDetails();
            firstName = getFirstname_FromExtraAdminDetails();
        } else if (username.equals("AUTO")) {
            username = GeneralBasic.generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
        } else if (username.contains("@sageautomation.com")) {
            username = username.replace("@sageautomation.com", "_" + testSerialNo + "@" + emailDomainName);
        }else if (username.equals("TEMP")){
            username= firstName + "_" + lastName + testSerialNo;
        }else {
            username = username.replace("@" + emailDomainName, "_" + testSerialNo + "@" + emailDomainName);
        }

        PageObj_ESSHome.userNameTextBox(driver).clear();
        PageObj_ESSHome.userNameTextBox(driver).sendKeys(username);
        logMessage("User Name: " + username + " is input.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//button[@id='continue']")).click();
        Thread.sleep(2000);
        logMessage("Continue button is clicked.");
        logScreenshot(driver);

        if (waitChild("//h1[contains(text(),'Enter password')]", 60, 1, driver)==null){
            errorCounter++;
            logError("Input Password screen is NOT shown.");
        }

        PageObj_ESSHome.passwordTextBox(driver).clear();
        PageObj_ESSHome.passwordTextBox(driver).sendKeys(password);

        Thread.sleep(2000);
        logMessage("Password '" + password + "' is input.");
        logScreenshot(driver);
        PageObj_ESSHome.signInButton(driver).click();
        logMessage("Sign In button is clicked.");
        Thread.sleep(30000);
        logScreenshot(driver);
        //GeneralBasic.waitSpinnerDisappear(180, driver);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logScreenshot(driver);

        WebElement dialogue_CompanyPolicy = SystemLibrary.waitChild("//div[@class='modal-wizard mw-has-header mw-has-close']", 30, 1, driver);
        while (dialogue_CompanyPolicy != null) {
            WebElement button_Next = SystemLibrary.waitChild("//button[@class='button--primary' and text()='Next']", 10, 1, driver);
            if (button_Next != null) {
                button_Next.click();
                Thread.sleep(2000);
                logMessage("Next button is clicked.");
                logScreenshot(driver);
            }

            WebElement button_OK = SystemLibrary.waitChild("//button[@class='button--primary' and text()='OK']", 10, 1, driver);
            if (button_OK != null) {
                button_OK.click();
                Thread.sleep(3000);
                logMessage("OK button is clicked.");
                logScreenshot(driver);


                driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Don')]")).click();
                Thread.sleep(2000);
                logMessage("Don't show these messages again button is clicked.");
                logScreenshot(driver);
                break;

               /* driver.findElement(By.xpath("//button[@class='button' and text()='Show again next time I login']")).click();
                Thread.sleep(2000);
                logMessage("Show again next time I login is clicked.");
                break;*/
            }
            dialogue_CompanyPolicy = SystemLibrary.waitChild("//div[@class='modal-wizard mw-has-header mw-has-close']", 15, 1, driver);

        }

        ////////////// Ajusted by Govinda on 29/12/2020 for handling Pendo Pop up in Dashboard Apply for leave panel/////////////
        WebElement pendo_Popup_Next=SystemLibrary.waitChild("//*[@id=\"pendo-guide-container\"]//button[text()='Next']", 10, 1, driver);
        if (pendo_Popup_Next!=null){
            pendo_Popup_Next.click();
            Thread.sleep(1000);
            logMessage("Next button Shown and clicked in pendo Popup.");
            logScreenshot(driver);
        }else{
            logMessage("Next Button Not Shown in pendo Popup.");
        }
        //////


        WebElement label_Welcome = SystemLibrary.waitChild("//*[@id=\"page-title\"]/h3/div", 35, 1, driver);
        if (label_Welcome != null) {
            String welcomeMessage = label_Welcome.getText();
            logMessage("Welcome Message: " + welcomeMessage);
            if (welcomeMessage.contains(firstName)) {
                logMessage("Welcome message is shown as expeected.");
            } else {
                if (preferredName != null) {
                    if (welcomeMessage.contains(preferredName)) {
                        logMessage("Welcome message is shown as expected.");
                    } else {
                        logError("Welcome message is NOT shown correctly.");
                        errorCounter++;
                    }
                } else {
                    logError("Welcome message is NOT shown correctly.");
                    errorCounter++;
                }
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) {
            logMessage("Log on as " + username + " successfully.");
            isSignin = true;
        } else {
            SystemLibrary.logError("Failed Signin ESS as " + username);
        }

        logScreenshot(driver);
        return isSignin;
    }

    public static boolean validateDashboard(WebDriver driver, String userFullName, String pendingLeaveApprovals, String pendingProfileApprovals, String leaveBalances, String nextLeave, String membersOnLeaveToday, String yourManager, String membershipTeams, String panelsToBeValidated, String messageStoreFileName, String screenshotStoreFileName, String updateStore, String validateStore, String expectedMessage, String testSerialNo, String emailDomainName) throws Exception {
        //The Dashboard page is shown first
        boolean isPassed = false;
        int errorCounter = 0;

        SystemLibrary.waitChild("//span[@class='avatar avatar']", 15, 1, driver);
        SystemLibrary.waitElementInvisible(120, "//div[@id='pending-leave-approvals-widget' and contains(., 'Fetching Pending Leave Approvals...')]", driver);

        logDebug("Original userFullName: '"+userFullName+"'.");
        if (userFullName != null) {

            if (userFullName.contains("Test.Admin")) {
                userFullName = "Test" + testSerialNo + ".Admin";
            } else if (userFullName.contains("ExtraAdminUser")) {
                userFullName = getFirstname_FromExtraAdminDetails() + " " + getLastname_FromExtraAdminDetails();
            }

            String strCurrentMessage = PageObj_Dashboard.panel_userFullName(driver).getText();
            if (strCurrentMessage.contains(userFullName)) {
                logMessage("User Full Name: " + userFullName + " is shown as expected.");
            } else {
                SystemLibrary.logError("User Full name is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + userFullName);
                errorCounter++;
            }
        }

        if (pendingLeaveApprovals != null) {
            WebElement panel = PageObject.PageObj_Dashboard.panel_PendingLeaveApprovals(driver);
            if (panel != null) {
                String strCurrentMessage = panel.getText();
                if (strCurrentMessage.contains(pendingLeaveApprovals)) {
                    logMessage("Pending Leave Approvals: " + pendingLeaveApprovals + " is shown as expected.");
                } else {
                    SystemLibrary.logError("Pending Leave Approvals is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + pendingLeaveApprovals);
                    errorCounter++;
                }
            } else {
                logError("Panel - Pending Leave Approvals is NOT shown.");
                errorCounter++;
            }
        }

        if (pendingProfileApprovals != null) {
            WebElement panel = PageObject.PageObj_Dashboard.panel_PendingProfileApprovals(driver);
            if (panel != null) {
                String strCurrentMessage = panel.getText();
                if (strCurrentMessage.contains(pendingProfileApprovals)) {
                    logMessage("Pending Profile Approvals status: " + pendingProfileApprovals + " is shown as expected.");
                } else {
                    SystemLibrary.logError("Pending Profile Approvals status is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + pendingProfileApprovals);
                    errorCounter++;
                }
            } else {
                logError("Panel - Pending Profile Approvals is NOT shown.");
                errorCounter++;
            }
        }

        if (leaveBalances != null) {
            String strCurrentMessage = PageObj_Dashboard.panel_LeaveBalances(driver).getText();
            if (strCurrentMessage.contains(leaveBalances)) {
                logMessage("Leave Balances: " + leaveBalances + " is shown as expected.");
            } else {
                SystemLibrary.logError("Leave Balances is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + leaveBalances);
                errorCounter++;
            }
        }

        if (nextLeave != null) {
            String strCurrentMessage = PageObj_Dashboard.panel_NextLeave(driver).getText();
            if (strCurrentMessage.contains(nextLeave)) {
                logMessage("Next Leave: " + nextLeave + " is shown as expected.");
            } else {
                SystemLibrary.logError("Next Leave is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + nextLeave);
                errorCounter++;
            }
        }

        if (membersOnLeaveToday != null) {
            if (PageObj_Dashboard.panel_MembersOnLeaveToday(driver) != null) {
                String strCurrentMessage = PageObj_Dashboard.panel_MembersOnLeaveToday(driver).getText();
                if (strCurrentMessage.contains(membersOnLeaveToday)) {
                    logMessage("Member On Leave Today: " + membersOnLeaveToday + " is shown as expected.");
                } else {
                    SystemLibrary.logError("Member On Leave Today is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + membersOnLeaveToday);
                    errorCounter++;
                }
            } else {
                logError("Member ON Leave Today is NOT shown.");
                errorCounter++;
            }

        }

        if (yourManager != null) {
            String strCurrentMessage = PageObj_Dashboard.panel_YourManger(driver).getText();
            if (strCurrentMessage.contains(yourManager)) {
                logMessage("Your Manager: " + yourManager + " is shown as expected.");
            } else {
                SystemLibrary.logError("Your Manager is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + yourManager);
                errorCounter++;
            }
        }

        if (membershipTeams != null) {
            String strCurrentMessage = PageObj_Dashboard.panel_MembershipTeams(driver).getText();
            if (strCurrentMessage.contains(membershipTeams)) {
                logMessage("Your Manager: " + membershipTeams + " is shown as expected.");
            } else {
                SystemLibrary.logError("Membership Teams is NOT shown as expected. \n\rActual result: " + strCurrentMessage + "\n\rExpected result: " + membershipTeams);
                errorCounter++;
            }
        }


        String panelMessage = "";
        String messageFileName = "";
        String messageFileFullPathName = "";

        if (panelsToBeValidated != null) {
            String[] panelNameList = SystemLibrary.splitString(panelsToBeValidated, ";");

            int totalCount = panelNameList.length;

            for (int i = 0; i < totalCount; i++) {

                if (panelNameList[i].equals("Greeting")) {
                    if (PageObj_Dashboard.label_Greeting(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Greeting Message:\n" + PageObj_Dashboard.label_Greeting(driver).getText();
                    } else {
                        logError("Greeting Message is NOT shown.");
                        errorCounter++;
                    }
                }

                if (panelNameList[i].equals("User Full Name")) {
                    panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_userFullName(driver).getText() + "\n";
                }

                if (panelNameList[i].equals("Pending Leave Approvals")) {
                    if (PageObj_Dashboard.panel_PendingLeaveApprovals(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_PendingLeaveApprovals(driver).getText() + "\n";
                    } else {
                        logError("Panel - Pending Leave Approvals is NOT Shown.");
                        errorCounter++;
                    }

                }

                if (panelNameList[i].equals("Pending Profile Approvals")) {
                    if (PageObj_Dashboard.panel_PendingProfileApprovals(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_PendingProfileApprovals(driver).getText() + "\n";
                    } else {
                        logError("Panel - Pending Profile Approvals is NOT Shown.");
                        errorCounter++;
                    }

                }

                if (panelNameList[i].equals("New Starters")) {
                    if (PageObj_Dashboard.panel_NewStarter(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_NewStarter(driver).getText() + "\n";
                    } else {
                        logError("Panel - New Starters is NOT Shown.");
                        errorCounter++;
                    }

                }

                if (panelNameList[i].equals("Latest Pay Advice")) {
                    if (PageObj_Dashboard.panel_LatestPayAdvice(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_LatestPayAdvice(driver).getText() + "\n";
                    } else {
                        logError("Panel - Latest pay Advice is NOT Shown.");
                        errorCounter++;
                    }
                }

                if (panelNameList[i].equals("Leave Balances")) {
                    panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_LeaveBalances(driver).getText() + "\n";
                }
                if (panelNameList[i].equals("Next Leave")) {
                    panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_NextLeave(driver).getText() + "\n";
                }

                if (panelNameList[i].equals("Member On Leave Today")) {
                    if (PageObj_Dashboard.panel_MembersOnLeaveToday(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_MembersOnLeaveToday(driver).getText() + "\n";
                    } else {
                        logError("Panel On Leave Today is NOT shown.");
                    }

                }

                if (panelNameList[i].equals("Your Manager")) {
                    panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_YourManger(driver).getText() + "\n";
                }

                if (panelNameList[i].equals("Membership Teams")) {
                    if (PageObj_Dashboard.panel_MembershipTeams(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_MembershipTeams(driver).getText() + "\n";
                    } else {
                        logError("Panel - Membership Teams is NOT Shown.");
                        errorCounter++;
                    }

                }

                if (panelNameList[i].equals("Management Teams")) {
                    if (PageObj_Dashboard.panel_ManagementTeams(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_ManagementTeams(driver).getText() + "\n";
                    } else {
                        logError("Panel - Management Teams is NOT shown.");
                        errorCounter++;
                    }

                }

                if (panelNameList[i].equals("Audit Reports")) {
                    if (PageObj_Dashboard.panel_AuditReports(driver) != null) {
                        panelMessage = panelMessage + panelNameList[i] + " Panel:\n" + PageObj_Dashboard.panel_AuditReports(driver).getText() + "\n";
                    } else {
                        logError("Panel - Audit Reports is NOT shown.");
                        errorCounter++;
                    }

                }
            }

            System.out.println(panelMessage);
            messageFileName = "DashboardMessage_" + getCurrentTimeAndDate() + ".txt";
            messageFileFullPathName = workingFilePath + messageFileName;

            //String currentSerialNumber = getTestSerailNumber_Main(101, 101);

            if (panelMessage.contains(testSerialNo)) {
                panelMessage = panelMessage.replace(testSerialNo, "SN123abc");
            }

            if (panelMessage.contains(emailDomainName)) {
                panelMessage = panelMessage.replace(emailDomainName, "sageautomation.com");
            }

            createTextFile(messageFileFullPathName, panelMessage);
            logMessage("The Dashboard message is saved as '" + messageFileFullPathName + "'.");
            String strFullURLAddress = serverUrlAddress + "TestLog/WorkingFile//" + messageFileName;
            System.out.println("Click here to access this file: " + strFullURLAddress);
        }


        if (messageStoreFileName != null) {
            if (!SystemLibrary.updateAndValidateStoreStringFile(updateStore, validateStore, messageFileFullPathName, messageStoreFileName, expectedMessage))
                errorCounter++;
        }

        String screenshotFileFullPathName = null;
        if (screenshotStoreFileName != null) {
            if (updateStore != null) {
                if (updateStore.equals("1")) {
                    screenshotFileFullPathName = SystemLibrary.logScreenshotElement(driver, PageObject.PageObj_Dashboard.wholeDashboardPanel(driver));
                    saveFileToStore(screenshotFileFullPathName, screenshotStoreFileName);
                }
            }
            if (validateStore != null) {
                if (validateStore.equals("1")) {
                    if (!compareImage(screenshotFileFullPathName, storeFilePath + screenshotStoreFileName)) {
                        logMessage("The expected file is \"" + storeFilePath + screenshotStoreFileName + "\"");
                        String strUrlAddress = serverUrlAddress + "Store/" + screenshotStoreFileName;
                        System.out.println("Click here to access the expected result: " + strUrlAddress);
                        errorCounter++;
                    }
                }
            }
        }

        if (errorCounter == 0) {
            isPassed = true;
        }
        return isPassed;
    }

    public static boolean displayDashboard(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        int errorCounter = 0;

        int phoneType=SystemLibrary.getPhoneType(driver);
        if (phoneType==5){
            WebElement menu_Hamburger=SystemLibrary.waitChild("//i[@class='icon-hamburger']", 60, 1, driver);
            if (menu_Hamburger!=null){
                menu_Hamburger.click();
                logMessage("Menu Hamburger is clicked.");
                driver.findElement(By.xpath("//a[contains(text(),'Dashboard')]")).click();
                Thread.sleep(20000);
                logMessage("Dashboard icon is clicked.");
                GeneralBasic.waitSpinnerDisappear(180, driver);

                if (SystemLibrary.waitChild("//div[@class='widget-profile']", 120, 1, driver) != null) {
                    logMessage("Dashboard page is shown.");
                    logScreenshot(driver);
                } else {
                    logError("Dashboard page is NOT shown.");
                    errorCounter++;
                }

            }else{
                logError("Menu Hamburger is NOT shown.");
            }
        }else{
            PageObj_NavigationBar.dashboard(driver).click();
            logMessage("Dashboard icon is clicked.");
            Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(180, driver);

            if (SystemLibrary.waitChild("//div[@class='widget-profile']", 120, 1, driver) != null) {
                logMessage("Dashboard page is shown.");
                logScreenshot(driver);
            } else {
                SystemLibrary.logError("Failed displaying Dashboard the 1st time.");
                errorCounter++;
                logMessage("Refreshing page the scenod time... ");
                driver.navigate().refresh();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(180, driver);

                if (SystemLibrary.waitChild("//div[@class='widget-profile']", 120, 1, driver) != null) {
                    logMessage("Dashboard page is shown after the Second Refresh.");
                } else {
                    logError("Failed display Dashboard page after the SECOND attempt.");
                    errorCounter++;
                }
            }
        }



        if (errorCounter == 0) isShown = true;

        return isShown;
    }

    public static boolean displayLeavePage(WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        PageObj_NavigationBar.dashboard(driver).click();
        Thread.sleep(4000);
        logMessage("Dashboard icon is clicked.");

        PageObj_Dashboard.panel_userFullName(driver).click();
        Thread.sleep(4000);
        logMessage("User Full Name panel is clicked.");

        PageObj_SideNavigationBar.sideMenu_leave(driver).click();
        Thread.sleep(4000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Leave tab is clicked");
        logMessage("Screenshot after click Leave tab.");
        SystemLibrary.logScreenshot(driver);
        return isShown;
    }

    public static boolean displaySettings_General(WebDriver driver) throws InterruptedException, IOException {
		Thread.sleep(3000);
		boolean isShown=false;
		int errorCounter=0;

		if (selectSettingsMenu_Main("General", driver)){
		    logMessage("General Page is shown.");

		}else{
		    logError("Menu is NOT Shown.");
		    errorCounter++;
        }

		if (errorCounter==0) isShown=true;

        logScreenshot(driver);
        return isShown;
    }

    public static boolean displaySettings_WhatsNew(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = selectSettingsMenu_Main("s New", driver);
        if (isShown) {
            logMessage("What's New Page is shown.");
        } else {
            logWarning("What's New Page is NOT shown.");
        }
        logScreenshot(driver);
        return isShown;
    }

    public static boolean selectSettingsMenu_Main_OLD(String subMenuName, WebDriver driver) throws InterruptedException {
        boolean isClicked = false;

        PageObj_NavigationBar.settings(driver).click();
        logMessage("Setting menu is clicked.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<WebElement> settingsList = driver.findElements(By.xpath("//*[@id=\"left-nav\"]/div[3]/li/ul/div"));

        /////////////////////////////
        int count = settingsList.size();
        //logMessage("count="+count);

        for (int i = 0; i < count; i++) {
            String currentItem = settingsList.get(i).getText();
            if (currentItem.equals(subMenuName)) {
                settingsList.get(i).click();
                Thread.sleep(2000);
                SystemLibrary.logMessage(currentItem + " is clicked.");
                GeneralBasic.waitSpinnerDisappear(120, driver);
                isClicked = true;
                break;
            }
        }

        return isClicked;
    }

    //Override below
    public static boolean addNewWebAPIConfiguration(String userName, String password, String apiKey, String databaseAlias, String termFromDate, String errorMessageReturn, String getValueFromFile, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);

        int orginalAPICount = PageObj_General.getWebAPIKeysTotalCount(driver);
        Thread.sleep(3000);

        PageObj_General.button_AddAPIKey(driver).click();
        logMessage("Add Button is clicked.");

        String dataSourceFilePathName = "C:\\TestAutomationProject\\ESSRegTest\\DataSource\\GUIKey_" + testSerialNo + ".txt";

        if (getValueFromFile != null) {
            if (getValueFromFile.equals("1")) {
                userName = SystemLibrary.getValueFromListFile("WebAPI UserName", ": ", dataSourceFilePathName);
                password = SystemLibrary.getValueFromListFile("WebAPI UserName Password", ": ", dataSourceFilePathName);
                apiKey = SystemLibrary.getValueFromListFile("GUI Key", ": ", dataSourceFilePathName);
                databaseAlias = SystemLibrary.getValueFromListFile("databaseAlias", ": ", dataSourceFilePathName);
            }
        }

        if (userName != null) {
            PageObj_General.textbox_Username(driver).click();
            PageObj_General.textbox_Username(driver).sendKeys(userName);
            logMessage("Username: " + userName + " is input.");
        }

        if (password != null) {
            PageObj_General.textbox_Password(driver).click();
            PageObj_General.textbox_Password(driver).sendKeys(password);
            logMessage("Password: " + password + " is input.");
        }

        if (apiKey != null) {
            PageObj_General.textbox_Apikey(driver).click();
            PageObj_General.textbox_Apikey(driver).sendKeys(apiKey);
            logMessage("Api Key: " + apiKey + " is input.");
        }

        if (databaseAlias != null) {
            PageObj_General.textbox_DatabaseAlias(driver).click();
            PageObj_General.textbox_DatabaseAlias(driver).sendKeys(databaseAlias);
            logMessage("Database Alias: " + databaseAlias + " is input.");
        }

        if (termFromDate != null) {

            if (termFromDate.contains(";")) {
                termFromDate = SystemLibrary.getExpectedDate(termFromDate, null);
            }

            PageObj_General.textbox_TermFromDate(driver).click();
            PageObj_General.textbox_TermFromDate(driver).sendKeys(termFromDate);
            logMessage("Import Terminated Employees from Date: " + termFromDate + " is input.");
        }

        logMessage("Screenshot before click Add Button.");
        logScreenshotElement(driver, PageObj_General.form_AddEditAPIKey(driver));

        PageObj_General.button_AddEdit(driver).click();
        logMessage("Button Add is clicked.");
        Thread.sleep(10000);
		logScreenshot(driver);
        String currentErrorMessage = PageObj_General.getReturnErrorMessageFromIntegrationScreen(driver);
        if (currentErrorMessage != null) {
            if (currentErrorMessage.length() > 0) {
                logMessage("The current return message is:\n" + currentErrorMessage);
                logMessage("The current screenshot of error message from screen.");
                logScreenshotElement(driver, PageObj_General.label_ErrorMessageReturn(driver));
            }
        }

        if (errorMessageReturn != null) {

            try {
                if (currentErrorMessage.equals(errorMessageReturn)) {
                    logMessage("The current return message is: " + currentErrorMessage);
                    logMessage("Message return is shown as expected.");
                } else {
                    String strMessage = "The current return message is NOT the same as expected. \nThe Current return Message is:\n " + currentErrorMessage + "\nExpceted Message is:\n " + errorMessageReturn;
                    logError(strMessage);
                    errorCounter++;
                }
            } catch (Exception e) {
                logMessage("No return message is currently return.");
                return false;
            }
        }

        int currentAPIKeyCount = PageObj_General.getWebAPIKeysTotalCount(driver);

        if ((currentAPIKeyCount - orginalAPICount) == 0) {
            logError("No API Key is added.");
            errorCounter++;
        }
        logMessage("Screenshot after clicking Add button");
        Thread.sleep(5000);
        logScreenshotElement(driver, driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/div[2]")));

        if (errorCounter > 0) isPassed = false;
        return isPassed;

    }

    //Override above
    public static boolean addNewWebAPIConfiguration(String userName, String password, String apiKey, String databaseAlias, String termFromDate, String errorMessageReturn, String getValueFromFile, String dbName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);

        int orginalAPICount = PageObj_General.getWebAPIKeysTotalCount(driver);
        Thread.sleep(3000);

        PageObj_General.button_AddAPIKey(driver).click();
        logMessage("Add Button is clicked.");

        String dataSourceFilePathName = "C:\\TestAutomationProject\\ESSRegTest\\DataSource\\GUIKey_" + testSerialNo + "_" + dbName + ".txt";

        if (getValueFromFile != null) {
            if (getValueFromFile.equals("1")) {
                userName = SystemLibrary.getValueFromListFile("WebAPI UserName", ": ", dataSourceFilePathName);
                password = SystemLibrary.getValueFromListFile("WebAPI UserName Password", ": ", dataSourceFilePathName);
                apiKey = SystemLibrary.getValueFromListFile("GUI Key", ": ", dataSourceFilePathName);
                databaseAlias = SystemLibrary.getValueFromListFile("databaseAlias", ": ", dataSourceFilePathName);
            }
        }

        if (userName != null) {
            PageObj_General.textbox_Username(driver).click();
            PageObj_General.textbox_Username(driver).sendKeys(userName);
            logMessage("Username: " + userName + " is input.");
        }

        if (password != null) {
            PageObj_General.textbox_Password(driver).click();
            PageObj_General.textbox_Password(driver).sendKeys(password);
            logMessage("Password: " + password + " is input.");
        }

        if (apiKey != null) {
            PageObj_General.textbox_Apikey(driver).click();
            PageObj_General.textbox_Apikey(driver).sendKeys(apiKey);
            logMessage("Api Key: " + apiKey + " is input.");
        }

        if (databaseAlias != null) {
            PageObj_General.textbox_DatabaseAlias(driver).click();
            PageObj_General.textbox_DatabaseAlias(driver).sendKeys(databaseAlias);
            logMessage("Database Alias: " + databaseAlias + " is input.");
        }

        if (termFromDate != null) {

            if (termFromDate.contains(";")) {
                termFromDate = SystemLibrary.getExpectedDate(termFromDate, null);
            }

            PageObj_General.textbox_TermFromDate(driver).click();
            PageObj_General.textbox_TermFromDate(driver).sendKeys(termFromDate);
            logMessage("Import Terminated Employees from Date: " + termFromDate + " is input.");
        }

        logMessage("Screenshot before click Add Button.");
        logScreenshotElement(driver, PageObj_General.form_AddEditAPIKey(driver));

        PageObj_General.button_AddEdit(driver).click();
        logMessage("Button Add is clicked.");
        Thread.sleep(10000);

        String currentErrorMessage = PageObj_General.getReturnErrorMessageFromIntegrationScreen(driver);
        if (currentErrorMessage != null) {
            if (currentErrorMessage.length() > 0) {
                logMessage("The current return message is:\n" + currentErrorMessage);
                logMessage("The current screenshot of error message from screen.");
                logScreenshotElement(driver, PageObj_General.label_ErrorMessageReturn(driver));
            }
        }

        if (errorMessageReturn != null) {

            try {
                if (currentErrorMessage.equals(errorMessageReturn)) {
                    logMessage("The current return message is: " + currentErrorMessage);
                    logMessage("Message return is shown as expected.");
                } else {
                    String strMessage = "The current return message is NOT the same as expected. \nThe Current return Message is:\n " + currentErrorMessage + "\nExpceted Message is:\n " + errorMessageReturn;
                    logError(strMessage);
                    errorCounter++;
                }
            } catch (Exception e) {
                logMessage("No return message is currently return.");
                return false;
            }
        }

        int currentAPIKeyCount = PageObj_General.getWebAPIKeysTotalCount(driver);

        if ((currentAPIKeyCount - orginalAPICount) == 0) {
            logError("No API Key is added.");
            errorCounter++;
        }
        logMessage("Screenshot after clicking Add button");
        Thread.sleep(5000);
        logScreenshotElement(driver, driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/div[2]")));

        if (errorCounter > 0) isPassed = false;
        return isPassed;

    }

    public static WebElement selectItemFromTable(String itemName, WebDriver driver) throws InterruptedException {
        //This function return element, the item must be a link
        //Another function searchItemFromTable return index
        boolean isFound = true;
        WebElement element = null;

        //////////////// Temp Disable //////////
        /*
        //Make sure All item listed first
        boolean isViewMoreExisting=true;
        while (isViewMoreExisting){
            try {
                driver.findElement(By.linkText("View more")).click();
                Thread.sleep(2000);
            }
            catch (Exception e){
                isViewMoreExisting=false;
            }
        }
        */
        //////

        try {
            element = driver.findElement(By.linkText(itemName));
            SystemLibrary.displayElementInView(element, driver, -250);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            isFound = false;
        }

        if (isFound) {
            logMessage("Item: " + itemName + " is selected.");
            try {
                SystemLibrary.logScreenshot(driver);
                SystemLibrary.logScreenshotElement(driver, element);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            SystemLibrary.logError("Item: \"" + itemName + "\" is NOT found.");
        }
        return element;
    }

    public static boolean removeGeneralAPIKey(String apiKey, WebDriver driver) throws InterruptedException, IOException {
        GeneralBasic.displaySettings_General(driver);
        boolean isRemoved = false;

        String xpath_EllipsisButton_API="//div[@class='list-item-row single-line-huge no-heading' and contains(., '"+apiKey+"')]//button";
        WebElement ellipsisButton_API=SystemLibrary.waitChild(xpath_EllipsisButton_API, 10, 1, driver);
        if (ellipsisButton_API!=null) {
            ellipsisButton_API.click();
            logMessage("API Ellipsis Button is found and clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[contains(text(),'Remove')]")).click();
            logMessage("Web API remove menu is clicked.");
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logScreenshot(driver);
            isRemoved = true;
        }
        return isRemoved;
    }

    public static boolean signoutESS(WebDriver driver) throws Exception {
        boolean isSignout = false;
        int errorCounter = 0;

        int phoneType=SystemLibrary.getPhoneType(driver);
        if (phoneType==5){
            WebElement menu_Hamburge=SystemLibrary.waitChild("//i[@class='icon-hamburger']", 60, 1, driver);
            if (menu_Hamburge!=null){
                if (menu_Hamburge.isDisplayed()) {
                    menu_Hamburge.click();
                    Thread.sleep(2000);
                    logMessage("Menu Hamburger is clicked.");
                }else{
                    logError("Menu Hamburger is NOT shown.");
                    errorCounter++;
                }
            }else{
                logError("Menu Hamburger is NOT shown.");
                errorCounter++;
            }
        }


        WebElement element = null;
        element = SystemLibrary.waitChild("//*[@id=\"profile-nav\"]/li[2]/a/i", 5, 1, driver);
        if ((element != null) && (element.isDisplayed())) {
            if (SystemLibrary.isElementClickable(element)){
                element.click();
                SystemLibrary.logMessage("Signout button 1 is clicked.");
                isSignout = true;
                return isSignout;
            }

        }

        element = SystemLibrary.waitChild("//*[@id=\"logout-link\"]/a/i", 5, 1, driver);
        if ((element != null) && (element.isDisplayed())) {
            if (SystemLibrary.isElementClickable(element)){
                element.click();
                SystemLibrary.logMessage("Signout button 2 is clicked.");
                isSignout = true;
                return isSignout;
            }

        }
        return false;
    }

    public static boolean displayApplyLeaveViaDashboard(WebDriver driver) throws InterruptedException, IOException {
        //displayDashboard() first
        boolean isShown = false;
        WebElement element = null;
        displayDashboard(driver);
        PageObj_Dashboard.panel_ApplyForLeave(driver).click();
        Thread.sleep(5000);

        element = SystemLibrary.waitChild("//*[@id=\"leave-apply\"]", 30, 1, driver);
        if (element != null) {
            logMessage("Apply for Leave form is shown.");
            isShown = true;
        } else {
            logError("Apply for Leave form is NOT shown.");
        }

        waitElementInvisible(30, "//*[@id=\\\"leaveHours-spinner\\\"]/div/div[1]/div", driver);
        Thread.sleep(10000);
        return isShown;
    }

    public static boolean displayApplyLeaveViaLeavePage(WebDriver driver) throws InterruptedException {
        //displayLeavePage() first
        boolean isShown = false;
        WebElement element = null;
        PageObj_Leave.button_AddLeave(driver).click();
        Thread.sleep(5000);

        element = SystemLibrary.waitChild("//*[@id=\"leave-apply\"]", 30, 1, driver);
        if (element != null) {
            logMessage("Apply for Leave form is shown.");
            isShown = true;
        } else {
            logError("Apply for Leave form is NOT shown.");
        }

        waitElementInvisible(30, "//*[@id=\\\"leaveHours-spinner\\\"]/div/div[1]/div", driver);
        Thread.sleep(10000);
        return isShown;
    }

    public static boolean displayApplyLeaveViaDashboard_Performance(WebDriver driver) throws InterruptedException, IOException {
        //displayDashboard() first
        boolean isShown = false;
        WebElement element = null;
        displayDashboard(driver);
        StopWatch myStopWatch = new StopWatch();


        logMessage("Timer Started...");
        myStopWatch.start();
        PageObj_Dashboard.panel_ApplyForLeave(driver).click();
        logMessage("Apply for Leave panel is clicked.");

        //driver.manage().logs().get(LogType.PERFORMANCE).getAll();

        Thread.sleep(5000);

        waitElementInvisible(30, "//*[@id=\\\"leaveHours-spinner\\\"]/div/div[1]/div", driver);

        element = SystemLibrary.waitChild("//*[@id=\"leave-apply\"]", 10, 1, driver);
        if (element != null) {
            logMessage("Apply for Leave form is shown.");
            isShown = true;
        } else {
            logError("Apply for Leave form is NOT shown.");
        }

        List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();

        System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
        for (LogEntry entry : entries) {
            System.out.println(
                    new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }

        myStopWatch.stop();
        logMessage("Timer Stopped.");
        logMessage("It takes " + myStopWatch.getTime() + " to displayApplyLeaveScreen.");
        Thread.sleep(10000);

        return isShown;
    }

    public static void applyLeaveViaDashboard_DONOTUSE(String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageEpected, String leaveBalanceEntitlement, WebDriver driver) throws InterruptedException, IOException {
        boolean isApplied = false;

        displayApplyLeaveViaDashboard(driver);


        if (startDate != null) {
            //PageObj_ApplyForLeave.textBox_StartDate(driver).clear();
            PageObj_ApplyForLeave.textBox_StartDate(driver).click();
            Thread.sleep(2000);
            PageObj_ApplyForLeave.textBox_StartDate(driver).sendKeys(startDate);
            logMessage("Start Date: " + startDate + " is input.");
            Thread.sleep(5000);
        }


        if (endDate != null) {
            //PageObj_ApplyForLeave.textBox_EndDate(driver).clear();
            PageObj_ApplyForLeave.textBox_EndDate(driver).click();
            Thread.sleep(2000);
            PageObj_ApplyForLeave.textBox_EndDate(driver).sendKeys(endDate);
            logMessage("End Date: " + endDate + " is input.");
            Thread.sleep(5000);
        }

        Thread.sleep(15000);
        if (leaveHours != null) {
            clearTextBox(PageObj_ApplyForLeave.textBox_LeaveHours(driver));
            PageObj_ApplyForLeave.textBox_LeaveHours(driver).sendKeys(leaveHours);
            logMessage("Leave Hour: " + leaveHours + " is input.");
            Thread.sleep(5000);
        }

        if (leaveReason != null) {
            PageObj_ApplyForLeave.selectLeaveTypeInApplyLeaveScreen(leaveReason, driver);
            Thread.sleep(5000);
        }

        if (attachmentPath != null) {
            PageObj_ApplyForLeave.submitAttachmentInApplyLeaveForm(attachmentPath, driver); //fucntion to be completed.
        }

        if (leaveComment != null) {
            PageObj_ApplyForLeave.textbox_comment(driver).sendKeys(leaveComment);
            logMessage("Leaave Comment is input.");
        }
        //Thread.sleep(12000);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObject.PageObj_ApplyForLeave.button_Apply(driver)));

        logMessage("Screenshot before click apply button.");
        logScreenshotElement(driver, PageObj_ApplyForLeave.form_ApplyForLeave(driver));

        PageObj_ApplyForLeave.button_Apply(driver).click();
        logMessage("Apply Leave button is clicked.");
        Thread.sleep(10000);

        element = SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div", 30, 1, driver);
        if (element != null) {
            logMessage(element.getText());
            logMessage("Screeshot before click OK button.");
            logScreenshotElement(driver, element);
            driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button")).click();
            logMessage("OK button is clicked");
        }

        logMessage("Apply for Leave is completed.");
        Thread.sleep(5000);

    }

    public static boolean syncAllData(WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displaySettings_General(driver)){
            //////////////////////
            Thread.sleep(5000);
            //driver.navigate().to(driver.getCurrentUrl());
            driver.navigate().refresh();
            Thread.sleep(15000);
            waitSpinnerDisappear(120, driver);
            //Get original message from Web API Log
            String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

            logMessage("Check 'Sync from Payroll'");
            tickCheckbox("1", PageObj_General.checkbox_SyncFromPayroll(driver), driver);

            logMessage("UnCheck 'Sync Balances For Leave Report.");
            tickCheckbox("2", PageObj_General.checkbox_SyncBalancesForLeaveReport(driver), driver);
            logScreenshot(driver);

            PageObj_General.button_Sync(driver).click();
            Thread.sleep(5000);
            logMessage("Sync button is clicked.");
            logScreenshot(driver);

            //Click OK button
            PageObj_General.button_YesSyncAll(driver).click();
            logMessage("Yes, Sync All button is clicked.");
            Thread.sleep(20000);
            waitSpinnerDisappear(120, driver);
            logScreenshot(driver);

            String currentMessage = null;
            //////////////// Jim adjusted on 19/07/2021 for 20 mins /////////////////
            int i = 0;
            for (i = 0; i <= 80; i++) {  //Wait maxim for 20 min
                //Click Refresh button
                PageObj_General.button_RefreshSync(driver).click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
                //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
                if (currentMessage.contains("Successfully imported")) {
                    logMessage("It tooks " + i * 15 + " Seconds to Sync from Payroll.");
                    break;
                }
            }

            logMessage("The final Sync from Payroll Message is '" + currentMessage + "'.");
            logScreenshot(driver);

            if (expectedMessage != null) {
                if (currentMessage.contains(expectedMessage)) {
                    logMessage("Completed Sync from Payroll Successfullly");
                    isPassed = true;
                }
                else {
                    logError("Failed Sync from Payroll Data.");
                    System.out.println("Expected log message: '" + expectedMessage + "'");
                    errorCounter++;
                }
            }
            //////

            ///////////////////// Sync Balance for Leave Report ///////////////////
            logMessage("Refresh General Page.");
            SystemLibrary.refreshPage(driver);

            logMessage("UnCheck 'Sync from Payroll'");
            tickCheckbox("2", PageObj_General.checkbox_SyncFromPayroll(driver), driver);

            logMessage("Check 'Sync Balances For Leave Report.");
            tickCheckbox("1", PageObj_General.checkbox_SyncBalancesForLeaveReport(driver), driver);
            logScreenshot(driver);

            PageObj_General.button_Sync(driver).click();
            Thread.sleep(3000);
            logMessage("Sync button is clicked.");

            //Click OK button
            PageObj_General.button_YesSyncLeaveBalance(driver).click();
            logMessage("Yes, Sync Leave Balance button is clicked.");
            Thread.sleep(20000);
            waitSpinnerDisappear(120, driver);

            currentMessage = null;
            //////////////// Jim adjusted on 19/07/2021 for 20 mins /////////////////
            int j = 0;
            for (j = 0; j <= 80; j++) {  //Wait maxim for 20 min
                //Click Refresh button
                PageObj_General.button_RefreshSync(driver).click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
                //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
                if (currentMessage.contains("Successfully synced leave balances")) {
                    logMessage("It tooks " + j * 15 + " Seconds to Sync Balance For Leave Report.");
                    break;
                }
            }

            logMessage("The final Sync Balance For Leave Report Message is '" + currentMessage + "'.");
            logScreenshot(driver);

            if (expectedMessage != null) {
                if (currentMessage.contains("Successfully synced leave")) {
                    logMessage("Completed Sync Balance for Leave Report Successfullly");
                }
                else {
                    logError("Failed Sync Balance for Leave Report.");
                    System.out.println("Expected log message: 'Successfully synced leave'");
                    errorCounter++;
                }
            }

            //////
        }else{
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean syncAllData_OLD(WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        if (displaySettings_General(driver)){
            //////////////////////
            Thread.sleep(5000);
            //driver.navigate().to(driver.getCurrentUrl());
            driver.navigate().refresh();
            Thread.sleep(15000);
            waitSpinnerDisappear(120, driver);
            //Get original message from Web API Log
            String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

            PageObj_General.button_Sync(driver).click();
            Thread.sleep(3000);
            logMessage("Ellipsis button in Web API Sync section is clicked.");
            logScreenshot(driver);
            PageObj_General.button_SyncAllData(driver).click();
            Thread.sleep(3000);
            logMessage("Sync All Data button is clicked.");
            //Click OK button
            PageObj_General.button_YesSyncAll(driver).click();
            logMessage("Yes, Sync All button is clicked.");
            Thread.sleep(20000);
            waitSpinnerDisappear(120, driver);

            String currentMessage = null;
            //////////////// Jim adjusted on 19/07/2021 for 20 mins /////////////////
            int i = 0;
            for (i = 0; i <= 80; i++) {  //Wait maxim for 20 min
                //Click Refresh button
                PageObj_General.button_RefreshSync(driver).click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
                //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
                if (currentMessage.contains("Successfully imported")) {
                    logMessage("It tooks " + i * 15 + " Seconds to Sync All Data.");
                    break;
                }
            }

            logMessage("The final Sync Message is '" + currentMessage + "'.");
            logScreenshot(driver);

            if (expectedMessage != null) {
                if (currentMessage.contains(expectedMessage)) {
                    logMessage("Completed Sync All Data Successfullly");
                    isPassed = true;
                }
            /*
            else if (currentMessage.contains("Finished SyncLeave for API Key")) {
                logMessage("Completed Sync All Data Successfullly");
                isPassed = true;
            }
            */
                else {
                    logError("Failed Sync All Data.");
                    System.out.println("Expected log message: '" + expectedMessage + "'");
                    errorCounter++;
                }
            }
            //////
        }else{
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }



    public static boolean syncAllDataSimultaneously(WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);
        Thread.sleep(5000);
        //driver.navigate().to(driver.getCurrentUrl());
        driver.navigate().refresh();
        Thread.sleep(5000);
        waitSpinnerDisappear(120, driver);
        //Get original message from Web API Log
        String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

        PageObj_General.button_Sync(driver).click();
        Thread.sleep(3000);
        logMessage("Ellipsis button in Web API Sync section is clicked.");

        PageObj_General.button_SyncAllData(driver).click();
        Thread.sleep(3000);
        logMessage("Sync All Data button is clicked.");
        //Click OK button
        PageObj_General.button_YesSyncAll(driver).click();
        logMessage("Yes, Sync All button is clicked.");
        Thread.sleep(20000);
        waitSpinnerDisappear(120, driver);

        ////////////////////////
        logMessage("Refresh current page.");
        driver.navigate().refresh();
        Thread.sleep(5000);
        waitSpinnerDisappear(120, driver);

        PageObj_General.button_Sync(driver).click();
        Thread.sleep(3000);
        logMessage("Ellipsis button in Web API Sync section is clicked.");

        PageObj_General.button_SyncChanges(driver).click();
        Thread.sleep(3000);
        logMessage("Sync Changes button is clicked.");
        //Click OK button
        PageObj_General.button_YesSyncChanges(driver).click();
        logMessage("Yes, Sync Changes button is clicked.");
        Thread.sleep(20000);
        waitSpinnerDisappear(120, driver);

        //////

        String currentMessage = null;

        int i = 0;
        for (i = 0; i <= 40; i++) {  //Wait maxim for 10 min
            //Click Refresh button
            PageObj_General.button_RefreshSync(driver).click();
            Thread.sleep(15000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
            //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
            if (currentMessage.contains("Successfully imported")) {
                logMessage("It tooks " + i * 15 + " Seconds to Sync All Data.");
                break;
            }
        }

        logMessage("The final Sync Message is '" + currentMessage + "'.");
        logScreenshot(driver);

        if (expectedMessage != null) {
            if (currentMessage.contains(expectedMessage)) {
                logMessage("Completed Sync All Data Successfullly");
                isPassed = true;
            }
            /*
            else if (currentMessage.contains("Finished SyncLeave for API Key")) {
                logMessage("Completed Sync All Data Successfullly");
                isPassed = true;
            }
            */
            else {
                logError("Failed Sync All Data.");
                System.out.println("Expected log message: '" + expectedMessage + "'");
                errorCounter++;
            }
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean applyLeaveViaLeavePage_OLD(String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBlanceButton, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;
        //displayLeavePage(driver) first
        String initialLeaveBalance = "";
        String currentLeaveBalance = "";

        if (leaveType != null) {
            initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
            logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
        }

        if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBlanceButton, driver)) {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObject.PageObj_ApplyForLeave.button_Apply(driver)));

            PageObj_ApplyForLeave.button_Apply(driver).click();
            logMessage("Apply Leave button is clicked.");
            Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            WebElement messagebox = SystemLibrary.waitChild("//div[@class='focus-trap-container']/form", 120, 1, driver);
            logMessage("Screenshot of dialogue.");
            logScreenshot(driver);
            String currentMessageText = messagebox.getText();
            logMessage("Message from dialogue below:");
            System.out.println(currentMessageText);

            if (messageExpected != null) {
                if (currentMessageText.contains(messageExpected)) {
                    logMessage("Message is shonw as expected.");
                } else {
                    logError("Message is NOT shown as expected.");
                    logMessage("Expected Message is below:");
                    System.out.println(messageExpected);
                    errorCounter++;
                }
            }

            driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
            logMessage("OK button is clicked");
            Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            //Wait for reload balance table, max waiting time 1 min
            waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
            logMessage("Screenshot after click OK Button");
            logScreenshot(driver);

            double currentLeaveBalannceTaken = 0;
            if (leaveType != null) {
                currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                if (leaveTakenExpected != null) {
                    if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                        logMessage("The day(s) of Leave Taken is as expected.");
                    } else {
                        logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                        errorCounter++;
                    }
                }
            }


        } else {
            errorCounter++;
        }

        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean cancelLeavePendingApproval_OLD(String leaveType, WebDriver driver) throws InterruptedException, IOException {
        //Display Leave page first

        boolean isCancelled = false;
        boolean checkpoint = true;

        String initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);

        PageObj_Leave.applyFilterInLeavePage("Pending;" + leaveType, driver);
        checkpoint = PageObj_Leave.clickMenuButtonInLeaveApplicationTable(1, "Cancel leave", driver);

        logMessage("Screenshot before click Cancen Leave button.");
        logScreenshot(driver);
        PageObj_Leave.button_CancelLeave(driver).click();
        Thread.sleep(10000);
        logMessage("Cancel Leave menu is clicked.");


        logMessage("Screenshot before click OK button.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button")).click();
        Thread.sleep(5000);
        logMessage("OK button is clicked.");
        String currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);

        double currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
        System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");


        return isCancelled;
    }

    public static boolean updateEmployeeEmailInSageMicropayDB(int startSerialNo, int endSerialNo, String testSerialNo, String emailDomainName) throws Exception {
        boolean isUpdated = false;

        String emailSuffix = getCurrentDate();
        //String strSQL="UPDATE _iptblEmployeeEmail SET cEmail = REPLACE(cEmail, '"+strOld+"', '"+strNew+"');";
        //String strSQL="update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_\"+getTestSerailNumber_Main(101, 101)+\"@mailinator' from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";

        //Disable by Jim
        //String strSQL = "update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_'+_iptblEmployee.cEmpCode+'_" + getTestSerailNumber_Main(101, 101) + "@"+emailDomainName +"', iEmailType=1 from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";
        String strSQL = "update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_'+_iptblEmployee.cEmpCode+'_" + testSerialNo + "@" + emailDomainName + "' from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";

        logMessage(strSQL);
        DBManage.sqlExecutor_WithCustomizedSQL_Main(startSerialNo, endSerialNo, strSQL);

        logMessage("Employee's email address is updated.");

        return isUpdated;
    }

    public static boolean updateEmployeeEmailInSageMicropayNZDB(String testSerialNo, String emailDomainName) throws Exception {
        boolean isUpdated = false;

        String emailSuffix = getCurrentDate();
        //String strSQL="UPDATE _iptblEmployeeEmail SET cEmail = REPLACE(cEmail, '"+strOld+"', '"+strNew+"');";
        //String strSQL="update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_\"+getTestSerailNumber_Main(101, 101)+\"@mailinator' from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";

        //Disable by Jim
        //String strSQL = "update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_'+_iptblEmployee.cEmpCode+'_" + getTestSerailNumber_Main(101, 101) + "@"+emailDomainName +"', iEmailType=1 from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";
        String strSQL = "update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_'+_iptblEmployee.cEmpCode+'_" + testSerialNo + "@" + emailDomainName + "' from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";

        logMessage(strSQL);
        DBManage.sqlExecutor_WithCustomizedSQL_Main(303, 303, strSQL);

        logMessage("Employee's email address is updated.");

        return isUpdated;
    }
    //Test SCM


    public static boolean downloadPayAdvice_ViaDashboard(String periodEndDate, String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws InterruptedException, IOException {
        //periodEndDate formate is 31/05/2017
        boolean isDownloaded = false;
        int errorCounter = 0;

        ////////////////////
        String payAdviceFileName = "Pay_Advice_PeriodEnd_" + convertDateFormat(periodEndDate) + ".pdf";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + payAdviceFileName;
        String strNewDownloadFileFullPathName = workingFilePath + "PayAdvice_" + getCurrentTimeAndDate() + ".pdf";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), payAdviceFileName);

        displayDashboard(driver);

        if (PageObj_Dashboard.button_DownloadPayAdvice(driver) != null) {
            PageObj_Dashboard.button_DownloadPayAdvice(driver).click();
            logMessage("Download Pay Advice button is clicked.");
            Thread.sleep(15000);

            File file = new File(getGoogleChromeDownloadPath(), payAdviceFileName);
            int i = 0;
            while (!file.exists()) {
                i++;
                Thread.sleep(1000);
                if (i > 300) break;
            }

            if (i < 300) {
                logMessage("It takes " + i + " Seconds to download Pay Advice.");
            } else {
                logError("Download Pay Advice overdue.");
                errorCounter++;
            }

            //Move download file into working folder
            if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
            logMessage("Pay Advice Report: '" + payAdviceFileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

            ///////////////////////////////////
            if (isUpdateStore != null) {
                if (isUpdateStore.equals("1")) {
                    if (!saveFileToStore(strNewDownloadFileFullPathName, storeFileName)) errorCounter++;
                }
            }

            Thread.sleep(10000);

            if (isCompare != null) {
                if (isCompare.equals("1")) {
                    if (!comparePDFFile(strNewDownloadFileFullPathName, storeFilePath + storeFileName)) errorCounter++;
                }
            }
        } else {
            logError("Download Pay Advice button is NOT shown.");
            errorCounter++;
        }


        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean displayPayAdviceSummary(WebDriver driver) throws InterruptedException, IOException {
        boolean isDisplayed = false;
        displayDashboard(driver);

        PageObj_Dashboard.panel_userFullName(driver).click();
        logMessage("Latest Pay Advice panel is clicked.");
        Thread.sleep(5000);

        PageObj_SideNavigationBar.sideMenu_payAdvicesAndSummaries(driver).click();

        if (SystemLibrary.waitChild("//*[@id=\"page-container\"]/div/main/div/div[2]/div[2]/div/div[1]/div/h4", 15, 1, driver).getText().equals("Pay Advices & Summaries")) {
            waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner0\"]/div/div[3]/div", driver);
            logMessage("Pay Advices & Summaries page is displayed");
            isDisplayed = true;
        }
        return isDisplayed;
    }

    public static boolean displayContactDetailsPage(WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        PageObj_NavigationBar.dashboard(driver).click();
        logMessage("Dashboard icon is clicked.");

        PageObj_Dashboard.panel_userFullName(driver).click();
        logMessage("User Full Name panel is clicked.");

        PageObj_SideNavigationBar.sideMenu_contactDetails(driver).click();
        logMessage("Contact Details tab is clicked");

        try {
            Thread.sleep(10000);
            //waitElementInvisible(timeOutInSeconds, "//*[@id=\\\"spinnerbalances-calendar-button\\\"]/div/div[1]/div", driver);
            WebElement pageLable = SystemLibrary.waitChild("//*[@id=\"page-container\"]/div/main/div/div[2]/div[2]/div/div[1]/div/h4", 30, 1, driver);
            if (pageLable.getText().equals("Contact Details")) {
                logMessage("Contact Details page is shown.");
                isShown = true;
            } else {
                logError("Contact Details page is NOT shown.");
            }

        } catch (Exception e) {
            SystemLibrary.logError("Failed displaying Contact Details page.");
        } finally {

            logMessage("Screenshot after click Contact Detail tab.");
            Thread.sleep(5000);
            //SystemLibrary.waitChild();
            SystemLibrary.logScreenshot(driver);
            return isShown;
        }

    }

    public static boolean editContactDetails(String firstName, String middleName, String lastName, String preferredName, String personalEmail, String personalMobile, String homeNumber, String approvePersonalContact, String workEmail, String officeNumber, String workMobile, String approveWorkContact, String residentialCountry, String residentialAddress, String residentialSuburb, String residentialPostcode, String residentialState, String userForPostalAddress, String postalCountry, String postalAddress, String postalSuburb, String postalPostcode, String postalState, String emergencyContactName, String emergencyContactRelationship, String emergencyContactMobilePhoneNumber, String emergencyContactPhoneNumber, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, ClassNotFoundException, SQLException {
        boolean isDone = false;
        int errorCounter = 0;

        if (firstName.contains("Admin")) {
            //firstName="Test"+getTestSerailNumber_Main(101, 101)+".Admin";
            firstName = "Test" + testSerialNo + ".Admin";
        }
        if (searchUserAndDisplayContactDetailsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //Initialize Page Object
            PageFac_ContactDetail po1 = PageFactory.initElements(driver, PageFac_ContactDetail.class);

            if ((personalEmail != null) || (personalMobile != null) || (homeNumber != null)) {

                if (po1.displayEditPersonalContactForm(driver)) {
                    if (personalEmail != null) {

                        if (personalEmail.equals("AUTO")) {
                            personalEmail = generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                        } else if (personalEmail.contains("sageautomation.com")) {
                            personalEmail = personalEmail.replace("sageautomation.com", emailDomainName);
                        }

                        WebElement element = po1.text_EditPersonalEmail;
                        element.click();
                        clearTextBox(element);
                        logScreenshot(driver);
                        element.sendKeys(personalEmail);
                        logMessage("Personal Email: " + personalEmail + " is input.");
                        logScreenshot(driver);
                    }

                    if (personalMobile != null) {
                        WebElement element = po1.text_EditPersonalMobile;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(personalMobile);
                        logMessage("Personal Mobile: " + personalMobile + " is input.");
                    }

                    if (homeNumber != null) {
                        if (homeNumber.equals("000000")) {
                            WebElement element = po1.text_EditHomeNumber;
                            clearTextBox(element);
                            logMessage("Home Number is cleared.");
                        } else {
                            WebElement element = po1.text_EditHomeNumber;
                            element.click();
                            clearTextBox(element);
                            element.sendKeys(homeNumber);
                        }

                    }

                    logMessage("Screenshot before click Save button.");
                    logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                    if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                        po1.button_SavePersonalWorkContact.click();
                        Thread.sleep(3000);

                        WebElement button_Continue = waitChild("//button[@class='button--primary' and text()='Yes, continue']", 5, 1, driver);
                        if (button_Continue != null) {
                            button_Continue.click();
                            Thread.sleep(3000);
                            logMessage("Button 'Yes, continue' is clicked.");
                        }

                        GeneralBasic.waitSpinnerDisappear(60, driver);

                        if (SystemLibrary.waitChild("//form[@autocomplete='off' and contains(., 'Edit Personal Contact')]", 5, 1, driver) != null) {
                            logWarning("Personal Contact is NOT edited.");
                            po1.button_CloseEditPersonalWorkContact.click();
                            errorCounter++;
                            logMessage("Edit Personal Contact dialogue is closed.");
                        }
                    } else {
                        po1.button_CloseEditPersonalWorkContact.click();
                        logWarning("No change has been made.");
                        //errorCounter++;  Jim comment on 06072018
                    }

                    Thread.sleep(2000);
                } else {
                    errorCounter++;
                }
            }

            if (approvePersonalContact != null) {
                logMessage("Waiting for Pending Approval button...");
                WebElement button_PendingApproval = SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'pending-button button')]", 120, 1, driver);

                if (button_PendingApproval != null) {
                    button_PendingApproval.click();
                    logMessage("Button Pending Approval is clicked in Personal Contact panel.");
                    Thread.sleep(3000);
                    if (approvePersonalContact.equals("1")) {
                        SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'approve-button button--success')]", 10, 1, driver).click();
                        logMessage("Approve button is clicked in Contact Detail - Personal Contact panel.");
                    } else if (approveWorkContact.equals("2")) {
                        SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'decline-button button--danger')]", 10, 1, driver).click();
                        logMessage("Decline button is clicked in Contact Detail - Work Contact panel.");
                    }
                }
            }

            if ((workEmail != null) || (officeNumber != null) || (workMobile != null) || (approveWorkContact != null)) {
                logDebug("Screenshot before display Work Contact form");
                logScreenshot(driver);
                po1.displayEditWorkContactForm(driver);
                if (workEmail != null) {

                    if (workEmail.equals("AUTO")) {
                        workEmail = generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                    } else if (workEmail.contains("sageautomation.com")) {
                        workEmail = workEmail.replace("sageautomation.com", emailDomainName);
                    }

                    WebElement element = po1.text_EditWorkEmail;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workEmail);
                }

                if (officeNumber != null) {
                    WebElement element = po1.text_EditOfficeNumber;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(officeNumber);
                }

                if (workMobile != null) {
                    WebElement element = po1.getText_EditWorkMobile;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workMobile);
                }

                logMessage("Screenshot before click Save button.");
                logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                    po1.button_SavePersonalWorkContact.click();
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    Thread.sleep(5000);

                    //Judge if the dialogue is still open
                    if (SystemLibrary.waitChild("//button[@type='submit' and text()='Save']", 5, 1, driver) != null) {
                        logWarning("Mandatory item is required. No change is made. Dialogue will be closed.");
                        po1.button_CloseEditPersonalWorkContact.click();
                        Thread.sleep(2000);
                        errorCounter++;
                    }
                    // Govinda Added
                    WebElement button_Continue = waitChild("//button[@class='button--primary' and text()='Yes, continue']", 5, 1, driver);
                    if (button_Continue != null) {
                        button_Continue.click();
                        Thread.sleep(5000);
                        logMessage("Button 'Yes, continue' is clicked.");
                    }

                } else {
                    po1.button_CloseEditPersonalWorkContact.click();
                    logWarning("No change has been made.");
                    //errorCounter++;  Jim comment on 06072018
                }
                GeneralBasic.waitSpinnerDisappear(60, driver);

                if (approveWorkContact != null) {
                    logMessage("Waiting for Pending Approval button...");
                    if (SystemLibrary.waitChild("//div[@class='display-area-workContact display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]", 120, 1, driver) != null) {
                        PageObj_ContactDetail.button_PendingApproval_ContactDetail_WorkContact(driver).click();
                        logMessage("Button Pending Approval is clicked in Work Contact panel.");
                        Thread.sleep(3000);
                        if (approveWorkContact.equals("1")) {
                            PageObj_ContactDetail.button_Approval_ContactDetail_WorkContact(driver).click();
                            logMessage("Approve button is clicked in Contact Detail - Work Contact panel.");
                        } else if (approveWorkContact.equals("2")) {
                            PageObj_ContactDetail.button_Decline_ContactDetail_WorkContact(driver).click();
                            logMessage("Decline button is clicked in Contact Detail - Work Contact panel.");
                        }
                    }
                }

                logMessage("Screen after editing Contact Details");
                logScreenshot(driver);
            }

            /////////////////////////////////////////////

            if ((residentialCountry != null) || (residentialAddress != null) || (residentialSuburb != null) || (residentialPostcode != null) || (residentialState != null) || (userForPostalAddress != null) || (postalCountry != null) || (postalAddress != null) || (postalSuburb != null) || (postalPostcode != null) || (postalState != null)) {
                if (!editContactDetails_Address(residentialCountry, residentialAddress, residentialSuburb, residentialPostcode, residentialState, userForPostalAddress, postalCountry, postalAddress, postalSuburb, postalPostcode, postalState, emergencyContactName, emergencyContactRelationship, emergencyContactMobilePhoneNumber, emergencyContactPhoneNumber, driver))
                    errorCounter++;
                logMessage("Screenshot after editing residential address.");
                logScreenshot(driver);
            }

            if ((emergencyContactName != null) || (emergencyContactRelationship != null) || (emergencyContactMobilePhoneNumber != null) || (emergencyContactPhoneNumber != null)) {
                if (!editContactDetails_EmergencyContact(emergencyContactName, emergencyContactRelationship, emergencyContactMobilePhoneNumber, emergencyContactPhoneNumber, driver))
                    errorCounter++;
                logMessage("Screenshot after editing Emergency Contact.");
                logScreenshot(driver);
            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editContactDetails_OLD(String firstName, String middleName, String lastName, String preferredName, String personalEmail, String personalMobile, String homeNumber, String approvePersonalContact, String workEmail, String officeNumber, String workMobile, String approveWorkContact, String residentialCountry, String residentialAddress, String residentialSuburb, String residentialPostcode, String residentialState, String userForPostalAddress, String postalCountry, String postalAddress, String postalSuburb, String postalPostcode, String postalState, String emergencyContactName, String emergencyContactRelationship, String emergencyContactMobilePhoneNumber, String emergencyContactPhoneNumber, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, ClassNotFoundException, SQLException {
        boolean isDone = false;
        int errorCounter = 0;

        if (firstName.contains("Admin")) {
            //firstName="Test"+getTestSerailNumber_Main(101, 101)+".Admin";
            firstName = "Test" + testSerialNo + ".Admin";
        }
        if (searchUserAndDisplayContactDetailsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //Initialize Page Object
            PageFac_ContactDetail po1 = PageFactory.initElements(driver, PageFac_ContactDetail.class);

            if ((personalEmail != null) || (personalMobile != null) || (homeNumber != null)) {

                if (po1.displayEditPersonalContactForm(driver)) {
                    if (personalEmail != null) {

                        if (personalEmail.equals("AUTO")) {
                            personalEmail = generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                        } else if (personalEmail.contains("sageautomation.com")) {
                            personalEmail = personalEmail.replace("sageautomation.com", emailDomainName);
                        }

                        WebElement element = po1.text_EditPersonalEmail;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(personalEmail);
                        logMessage("Personal Email: " + personalEmail + " is input.");
                    }

                    if (personalMobile != null) {
                        WebElement element = po1.text_EditPersonalMobile;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(personalMobile);
                        logMessage("Personal Mobile: " + personalMobile + " is input.");
                    }

                    if (homeNumber != null) {
                        if (homeNumber.equals("000000")) {
                            WebElement element = po1.text_EditHomeNumber;
                            clearTextBox(element);
                            logMessage("Home Number is cleared.");
                        } else {
                            WebElement element = po1.text_EditHomeNumber;
                            element.click();
                            clearTextBox(element);
                            element.sendKeys(homeNumber);
                        }

                    }

                    logMessage("Screenshot before click Save button.");
                    logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                    if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                        po1.button_SavePersonalWorkContact.click();
                        Thread.sleep(3000);

                        WebElement button_Continue = waitChild("//button[@class='button--primary' and text()='Yes, continue']", 5, 1, driver);
                        if (button_Continue != null) {
                            button_Continue.click();
                            Thread.sleep(3000);
                            logMessage("Button 'Yes, continue' is clicked.");
                        }

                        GeneralBasic.waitSpinnerDisappear(60, driver);

                        if (SystemLibrary.waitChild("//form[@autocomplete='off' and contains(., 'Edit Personal Contact')]", 5, 1, driver) != null) {
                            logWarning("Personal Contact is NOT edited.");
                            po1.button_CloseEditPersonalWorkContact.click();
                            errorCounter++;
                            logMessage("Edit Personal Contact dialogue is closed.");
                        }
                    } else {
                        po1.button_CloseEditPersonalWorkContact.click();
                        logWarning("No change has been made.");
                        //errorCounter++;  Jim comment on 06072018
                    }

                    Thread.sleep(2000);
                } else {
                    errorCounter++;
                }
            }

            if (approvePersonalContact != null) {
                logMessage("Waiting for Pending Approval button...");
                WebElement button_PendingApproval = SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'pending-button button')]", 120, 1, driver);

                if (button_PendingApproval != null) {
                    button_PendingApproval.click();
                    logMessage("Button Pending Approval is clicked in Personal Contact panel.");
                    Thread.sleep(3000);
                    if (approvePersonalContact.equals("1")) {
                        SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'approve-button button--success')]", 10, 1, driver).click();
                        logMessage("Approve button is clicked in Contact Detail - Personal Contact panel.");
                    } else if (approveWorkContact.equals("2")) {
                        SystemLibrary.waitChild("//div[@id='ea-personal-contact']//button[contains(@class,'decline-button button--danger')]", 10, 1, driver).click();
                        logMessage("Decline button is clicked in Contact Detail - Work Contact panel.");
                    }
                }
            }

            if ((workEmail != null) || (officeNumber != null) || (workMobile != null) || (approveWorkContact != null)) {
                logDebug("Screenshot before display Work Contact form");
                logScreenshot(driver);
                po1.displayEditWorkContactForm(driver);
                if (workEmail != null) {

                    if (workEmail.equals("AUTO")) {
                        workEmail = generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                    } else if (workEmail.contains("sageautomation.com")) {
                        workEmail = workEmail.replace("sageautomation.com", emailDomainName);
                    }

                    WebElement element = po1.text_EditWorkEmail;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workEmail);
                }

                if (officeNumber != null) {
                    WebElement element = po1.text_EditOfficeNumber;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(officeNumber);
                }

                if (workMobile != null) {
                    WebElement element = po1.getText_EditWorkMobile;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workMobile);
                }

                logMessage("Screenshot before click Save button.");
                logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                    po1.button_SavePersonalWorkContact.click();
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    Thread.sleep(5000);

                    //Judge if the dialogue is still open
                    if (SystemLibrary.waitChild("//button[@type='submit' and text()='Save']", 5, 1, driver) != null) {
                        logWarning("Mandatory item is required. No change is made. Dialogue will be closed.");
                        po1.button_CloseEditPersonalWorkContact.click();
                        Thread.sleep(2000);
                        errorCounter++;
                    }
                    // Govinda Added
                    WebElement button_Continue = waitChild("//button[@class='button--primary' and text()='Yes, continue']", 5, 1, driver);
                    if (button_Continue != null) {
                        button_Continue.click();
                        Thread.sleep(5000);
                        logMessage("Button 'Yes, continue' is clicked.");
                    }

                } else {
                    po1.button_CloseEditPersonalWorkContact.click();
                    logWarning("No change has been made.");
                    //errorCounter++;  Jim comment on 06072018
                }
                GeneralBasic.waitSpinnerDisappear(60, driver);

                if (approveWorkContact != null) {
                    logMessage("Waiting for Pending Approval button...");
                    if (SystemLibrary.waitChild("//div[@class='display-area-workContact display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]", 120, 1, driver) != null) {
                        PageObj_ContactDetail.button_PendingApproval_ContactDetail_WorkContact(driver).click();
                        logMessage("Button Pending Approval is clicked in Work Contact panel.");
                        Thread.sleep(3000);
                        if (approveWorkContact.equals("1")) {
                            PageObj_ContactDetail.button_Approval_ContactDetail_WorkContact(driver).click();
                            logMessage("Approve button is clicked in Contact Detail - Work Contact panel.");
                        } else if (approveWorkContact.equals("2")) {
                            PageObj_ContactDetail.button_Decline_ContactDetail_WorkContact(driver).click();
                            logMessage("Decline button is clicked in Contact Detail - Work Contact panel.");
                        }
                    }
                }

                logMessage("Screen after editing Contact Details");
                logScreenshot(driver);
            }

            /////////////////////////////////////////////

            if ((residentialCountry != null) || (residentialAddress != null) || (residentialSuburb != null) || (residentialPostcode != null) || (residentialState != null) || (userForPostalAddress != null) || (postalCountry != null) || (postalAddress != null) || (postalSuburb != null) || (postalPostcode != null) || (postalState != null)) {
                if (!editContactDetails_Address(residentialCountry, residentialAddress, residentialSuburb, residentialPostcode, residentialState, userForPostalAddress, postalCountry, postalAddress, postalSuburb, postalPostcode, postalState, emergencyContactName, emergencyContactRelationship, emergencyContactMobilePhoneNumber, emergencyContactPhoneNumber, driver))
                    errorCounter++;
                logMessage("Screenshot after editing residential address.");
                logScreenshot(driver);
            }

            if ((emergencyContactName != null) || (emergencyContactRelationship != null) || (emergencyContactMobilePhoneNumber != null) || (emergencyContactPhoneNumber != null)) {
                if (!editContactDetails_EmergencyContact(emergencyContactName, emergencyContactRelationship, emergencyContactMobilePhoneNumber, emergencyContactPhoneNumber, driver))
                    errorCounter++;
                logMessage("Screenshot after editing Emergency Contact.");
                logScreenshot(driver);
            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean displayLeavesForApprovalViaNavigationBar(String userType, WebDriver driver) throws InterruptedException, IOException {
        boolean isDisplayed = false;
        displayDashboard(driver);
        Thread.sleep(2000);
        if (userType.equals("Admin")) {
            PageObj_NavigationBar.approvals(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Clicking Approvals in the navigation bar.");

            PageObj_Approvals.approvals_MyApprovals_Dropdown(driver).click();
            Thread.sleep(1000);
            logMessage("My Approval Dropdown button is clicked.");

            PageObj_Approvals.approvals_MyApprovals_OtherApprovals(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            PageObj_Approvals.approvals_All(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("All tab is clicked.");

            PageObj_Approvals.approvals_LeaveApplications(driver).click();
            Thread.sleep(2000);
        } else if (userType.equals("Manager")) {
            logMessage("Clicking Approvals in the navigation bar.");
            PageObj_NavigationBar.approvals(driver).click();
            waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner0\"]/div/div[1]/div", driver);
            logMessage("Clicking Leave Applications waiting for Approval.");
            PageObj_Approvals.approvals_LeaveApplications(driver).click();
            Thread.sleep(2000);
        }
        isDisplayed = true;
        return isDisplayed;
    }

    public static boolean displayLeavesForApprovalViaPendingLeaveApprovalPanel(WebDriver driver) throws InterruptedException, IOException {
        boolean isDisplayed = false;
        displayDashboard(driver);
        String no_ofPendingLeaveApprovals = PageObj_Dashboard.pendingLeaveApprovalsPanelValue(driver).getText();
        logMessage("There are " + no_ofPendingLeaveApprovals + " leaves pending approval");
        logMessage("Clicking Pending Leave Approvals panel.");
        PageObj_Dashboard.pendingLeaveApprovalsPanelLable(driver).click();
        logMessage("Pending Leave Approvals panel is clicked.");
        waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner1\"]", driver);
        isDisplayed = true;
        return isDisplayed;
    }

    public static boolean approveLeave(String empName, String leaveDate, String approve, WebDriver driver) throws IOException, InterruptedException, ParseException {
        //Approvals page have to be shown first
        boolean isApproved = false;
        leaveDate = getLeaveDateInESSFormat(leaveDate);
        String approval = null;
        if (approve.equals("Yes")) {
            approval = "approve";
        } else {
            approval = "decline";
        }
        logMessage("Screenshot of Leave Applications");
        SystemLibrary.logScreenshot(driver);
        if (approval == "decline") {
            logMessage("Clicking Pending Approval Button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='Leave Application']/following-sibling::ul/li[text()='" + leaveDate + "']]//button/span")).click();
            logScreenshot(driver);
            logMessage("Clicking Decline button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='Leave Application']/following-sibling::ul/li[text()='" + leaveDate + "']]//button[contains(@class,'" + approval + "-button')]")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(@class,'button--danger') and text()='Decline']")).click();
        } else {
            logMessage("Clicking Pending Approval Button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='Leave Application']/following-sibling::ul/li[text()='" + leaveDate + "']]//button/span")).click();
            logScreenshot(driver);
            logMessage("Clicking Approve button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='Leave Application']/following-sibling::ul/li[text()='" + leaveDate + "']]//button[contains(@class,'" + approval + "-button')]")).click();
            isApproved = true;
        }
        logMessage("Screenshot of Leave Applications after Approve/Decline process.");
        logScreenshot(driver);
        return isApproved;
    }

    //Continue

    public static boolean downloadAuditReportsViaDashboard(String auditReportName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        //Display Dashboard first
        Thread.sleep(5000);
        auditReportName = auditReportName.toLowerCase();
        String strCurrentDownloadFileName = auditReportName.replace(" ", "-") + "-" + SystemLibrary.getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + strCurrentDownloadFileName;

        String strNewDownloadFileName = auditReportName.replace(" ", "_") + "_" + getCurrentTimeAndDate() + ".csv";
        String strNewDownloadFileFullPathName = workingFilePath + strNewDownloadFileName;

        //Delete OLD download file in download folder
        deleteFilesInFolder(getGoogleChromeDownloadPath(), auditReportName);
        if (auditReportName.equals("profile changes audit report")) {
            PageObj_Dashboard.list_ProfileChangeAuditReport(driver).click();
        } else if (auditReportName.equals("leave applications audit report")) {
            PageObj_Dashboard.list_LeaveApplicationsAuditReport(driver).click();
        } else if (auditReportName.equals("maintenance audit report")) {
            if (!SystemLibrary.isElementClickable(PageObj_Dashboard.list_MaintenanceAuditReport(driver))) {
                SystemLibrary.displayElementInView(PageObj_Dashboard.list_MaintenanceAuditReport(driver), driver, 10);
            }
            PageObj_Dashboard.list_MaintenanceAuditReport(driver).click();
        } else if (auditReportName.equals("activation report")) {
            PageObj_Dashboard.list_ActivationReport(driver).click();
        }

        Thread.sleep(40000);
        logMessage(auditReportName + " is clicked.");

        //Move download file into working folder
        if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null){
            logWarning("File is Not downloaded.");
            errorCounter++;
        }else{
            ////////////////////
            logMessage("Audit report: '" + auditReportName + "' is download as '" + strNewDownloadFileFullPathName + "'.");
            String csvTextFileFullName = parseCSVFile(strNewDownloadFileFullPathName);

            System.out.println("Click the link to access the report: " + serverUrlAddress + "TestLog/WorkingFile/" + strNewDownloadFileName.replace(".csv", ".txt"));

            String currentContent = SystemLibrary.getStingFromFile(strNewDownloadFileFullPathName);

            if (expectedContent != null) {
                if (!SystemLibrary.validateStringContainInFile(currentContent, storeFileName, isUpdateStore, isCompare, expectedContent, emailDomainName, testSerialNo))
                    errorCounter++;
            } else {
                if (!SystemLibrary.validateStringFile(currentContent, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo))
                    errorCounter++;
            }
            //////
        }



        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static int displayTeamsPage(WebDriver driver) throws InterruptedException, IOException {
        int isShown = 0;
        int phoneType=SystemLibrary.getPhoneType(driver);

        if (phoneType==5){
            WebElement menu_Hamburger=SystemLibrary.waitChild("//i[@class='icon-hamburger']", 60, 1, driver);
            if (menu_Hamburger!=null){
                menu_Hamburger.click();
                logMessage("Menu Hamburger is clicked.");
            }else{
                logError("Menu Hamburger is NOT clicked.");
                isShown=-1;
            }
        }

        Thread.sleep(10000);
        PageObj_NavigationBar.teams(driver).click();
        Thread.sleep(15000);
        logMessage("Navigation Bar - Team is clcked.");
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if (SystemLibrary.waitChild("//h3[text()='Teams']", 120, 1, driver) != null) {
            logMessage("Teams Page - Team List is shown.");
            isShown = 1;
        } else if (SystemLibrary.waitChild("//h3[text()='Unassigned']", 120, 1, driver) != null) {
            logMessage("Teams Page - Unassinged is shown.");
            isShown = 3;
        }else {
            logMessage("Teams page - Team Member is shown.");
            isShown = 2;
        }

        Thread.sleep(5000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        logMessage("Screenshot of Team Page.");
        logScreenshot(driver);

        return isShown;
    }


    public static boolean displaySettings_RolesPermissions(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        int errorCounter = 0;
        if (selectSettingsMenu_Main("Roles & Permissions", driver)) {
            if (PageObj_Roles.lable_Roles(driver).getText().equals("Roles")) {
                logMessage("Roles Page is shown.");
                isShown = true;
                Thread.sleep(1000);
            } else {
                logError("Roles Page is NOT shown.");
            }
        } else {
            errorCounter++;
        }

        logScreenshot(driver);
        if (errorCounter == 0) isShown = true;
        return isShown;
    }

    public static boolean displaySettings_RedirectedApprovers(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        if (selectSettingsMenu_Main("Redirected Approvers", driver)) {
            if (PageObj_RedirectedApprovers.label_RedirectedApprovers(driver).getText().equals("Redirected Approvers")) {
                logMessage("Redirected Approvers Page is shown.");
                isShown = true;
            } else {
                logError("Redirected Approvers Page is NOT shown.");
            }
        } else {
            logWarning("Redirected Approvers Page is NOT shown.");
        }
        logScreenshot(driver);
        return isShown;
    }

    public static boolean displaySettings_Leave(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        if (selectSettingsMenu_Main("Leave", driver)) {
            if (PageObj_SettingsLeave.label_Leave(driver).getText().equals("Leave")) {
                logMessage("Settings - Leave Page is shown.");
                isShown = true;
            } else {
                logError("Settings - Leave Page is NOT shown.");
            }
        } else {
            logWarning("Setttings - Leave Page is NOT shown.");
        }

        logScreenshot(driver);
        return isShown;
    }

    public static boolean displaySettings_Workflows(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        int errorCounter = 0;

        if (selectSettingsMenu_Main("Workflows", driver)) {
            if (PageObj_Workflows.pageTitle(driver).getText().contains("Workflows")) {
                logMessage("Workflows page is shown.");
                logScreenshot(driver);
                isShown = true;
            } else {
                logError("Failed show Workflow page.");
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        return isShown;
    }

    public static boolean displayProfileApprovalViaNavigationBar(String userType, WebDriver driver) throws InterruptedException, IOException {
        boolean isDisplayed = false;
        displayDashboard(driver);
        Thread.sleep(2000);
        if (userType.equals("Admin")) {
            logMessage("Clicking Approvals in the navigation bar.");
            PageObj_NavigationBar.approvals(driver).click();
            PageObj_Approvals.approvals_MyApprovals_Dropdown(driver).click();
            PageObj_Approvals.approvals_MyApprovals_OtherApprovals(driver).click();
            Thread.sleep(2000);
            PageObj_Approvals.approvals_All(driver).click();
            waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner0\"]/div/div[1]/div", driver);
            logMessage("Clicking Profile Changes waiting for Approval.");
            PageObj_Approvals.approvals_ProfileChanges(driver).click();
            Thread.sleep(2000);
        } else if (userType.equals("Manager")) {
            logMessage("Clicking Approvals in the navigation bar.");
            PageObj_NavigationBar.approvals(driver).click();
            waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner0\"]/div/div[1]/div", driver);
            logMessage("Clicking Profile Changes waiting for Approval.");
            PageObj_Approvals.approvals_ProfileChanges(driver).click();
            Thread.sleep(2000);
        }
        isDisplayed = true;
        return isDisplayed;
    }

    public static boolean displayProfileApprovalViaPendingProfileApprovalPanel(WebDriver driver) throws InterruptedException, IOException {
        boolean isDisplayed = false;
        displayDashboard(driver);
        String no_ofPendingProfileApprovals = PageObj_Dashboard.pendingProfileApprovalsPanelValue(driver).getText();
        logMessage("There are " + no_ofPendingProfileApprovals + " leaves pending approval");
        logMessage("Clicking Pending Profile Approvals panel.");
        PageObj_Dashboard.pendingProfileApprovalsPanelLable(driver).click();
        logMessage("Pending Profile Approvals panel is clicked.");
        waitElementInvisible(timeOutInSeconds, "//*[@id=\"spinner2\"]/div/div[3]/div", driver);
        isDisplayed = true;
        return isDisplayed;
    }

    public static boolean approveProfileChanges(String empName, String typeOfChange, String approve, WebDriver driver) throws IOException, InterruptedException {
        //Approvals page have to be shown first
        boolean isApproved = false;
        //typeOfChange = "Medical Conditions";
        String approval = null;
        if (approve.equals("Yes")) {
            approval = "approve";
        } else {
            approval = "decline";
        }
        logMessage("Screenshot of Profile Changes");
        SystemLibrary.logScreenshot(driver);
        logMessage("View the Changes");
        driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='Sharon ANDREWS']/following-sibling::b[text()='" + typeOfChange + "']]//li[.//p[text()='Select to view changes']]")).click();
        waitElementInvisible(timeOutInSeconds, "//*[@id=\"loader-spinner\"]/div/div[1]/div", driver);
        Thread.sleep(5000);
        SystemLibrary.logScreenshot(driver);
        //Go back to list of profile changes after viewing the selected change.
        displayProfileApprovalViaNavigationBar("Manager", driver);
        if (approval == "decline") {
            logMessage("Clicking Pending Approval Button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='" + typeOfChange + "']]//button/span")).click();
            logMessage("Screenshot of Profile change that needs Approval");
            logScreenshot(driver);
            logMessage("Clicking Decline button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='" + typeOfChange + "']]//button[contains(@class,'" + approval + "-button')]")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("//div[@class='mc-body']//textarea[@id='comments']")).sendKeys("Test to decline the Change.");
            logMessage("Screenshot of Decline Comment.");
            logScreenshot(driver);
            driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(@class,'button--danger') and text()='Decline']")).click();
        } else {
            logMessage("Clicking Pending Approval Button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='" + typeOfChange + "']]//button/span")).click();
            logScreenshot(driver);
            logMessage("Clicking Approve button.");
            driver.findElement(By.xpath("//div[@class='approval-details-wrapper'][.//b[text()='" + empName + "']/following-sibling::b[text()='" + typeOfChange + "']]//button[contains(@class,'" + approval + "-button')]")).click();
            isApproved = true;
        }
        logMessage("Screenshot of Profile Changes after Approve/Decline process.");
        logScreenshot(driver);
        return isApproved;
    }

    public static boolean getContentFromRolesTable(String rolesTypeName, String storeFileName, String isUpdate, String isCompare, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCount = 0;
        displaySettings_RolesPermissions(driver);
        String strOutput = null;
        if (rolesTypeName.equals("ADMINISTRATOR ROLES")) {
            strOutput = PageObj_Roles.rows_ADMINISTRATOR_ROLES(driver).getText();
        } else if (rolesTypeName.equals("MANAGER ROLES")) {
            strOutput = PageObj_Roles.rows_MANAGER_ROLES(driver).getText();
        } else if (rolesTypeName.equals("MEMBER ROLES")) {
            strOutput = PageObj_Roles.rows_MEMBER_ROLES(driver).getText();
        }

        logMessage("The table content under role: '" + rolesTypeName + "' is below:");
        System.out.println(strOutput);

        String currentFileName = rolesTypeName + "_" + getCurrentTimeAndDate() + ".txt";
        String currentFilePathName = workingFilePath + currentFileName;
        if (isUpdate != null) {
            if (isUpdate.equals(1)) {
                SystemLibrary.saveFileToStore(currentFilePathName, storeFileName);
            }
        }

        if (isCompare != null) {
            if (isCompare.equals(1)) {
                if (!SystemLibrary.compareTextFile(currentFilePathName, storeFilePath + storeFileName)) errorCount++;
            }
        }

        if (errorCount > 0) isPassed = false;
        return isPassed;
    }

    public static int searchItemFromTable(String searchKeys, String tableXpath, WebDriver driver) throws InterruptedException {
        //searchKeys are limited to maxim 2 key words only.
        //tableXpath should holding all the List<WebElement> within table
        //This function return index only
        List<WebElement> rowElements = driver.findElements(By.xpath(tableXpath));
        int totalCount = rowElements.size();
        String rowContent = null;
        WebElement currentElement;
        int outputIndex = 0;

        String[] searchKey = SystemLibrary.splitString(searchKeys, ";");
        int totalCountOfSearchKeys = searchKey.length;

        for (int i = 0; i < totalCount; i++) {
            currentElement = rowElements.get(i);
            rowContent = rowElements.get(i).getText();

            if (totalCountOfSearchKeys == 1) {
                if (rowContent.contains(searchKey[0])) {
                    logMessage("SearchKey: '" + searchKeys + "' is found in row " + i);
                    System.out.println("The row content is blow:\n");
                    System.out.println(rowContent);
                    outputIndex = i;
                    break;
                }
            } else if (totalCountOfSearchKeys >= 2) {
                if (rowContent.contains(searchKey[0]) && (rowContent.contains(searchKey[1]))) {
                    logMessage("SearchKey: '" + searchKeys + "' is found in row " + i);
                    System.out.println("The row content is blow:\n");
                    System.out.println(rowContent);
                    outputIndex = i;
                    break;
                }
            }

        }

        if (outputIndex == 0) logWarning("Search Key: '" + searchKeys + "' is NOT found.");
        return outputIndex;
    }

    public static boolean validatePermissionPanelScreenshot(String roleName, String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCounter = 0;
        displaySettings_RolesPermissions(driver);
        if (PageObj_Roles.displayRolesPermissionPage(roleName, driver)) {
            if (!SystemLibrary.validateScreenshot(storeFileName, isUpdateStore, isCompare, driver)) errorCounter++;
        } else {
            errorCounter++;
        }


        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean configPermissionStatus(String roleName, String itemName, String newStatus, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        displaySettings_RolesPermissions(driver);
        PageObj_Roles.displayRolesPermissionPage(roleName, driver);
        Thread.sleep(2000);
        String currentStatus = PageObj_Roles.getPermissionButton(itemName, driver).getAttribute("class");
        if (currentStatus.contains(newStatus.toLowerCase())) {
            logWarning("The current Role '" + roleName + "' Permission '" + itemName + "' status is " + currentStatus + ". No change is make.");
        } else {

            PageObj_Roles.getPermissionButtonEditable(itemName, driver).click();
            Thread.sleep(5000);
            SystemLibrary.logMessage("Permission Button is clicked.");

            SystemLibrary.logMessage("Screenshot after click Permission button.");
            SystemLibrary.logScreenshot(driver);

            driver.findElement(By.linkText(newStatus)).click();
            Thread.sleep(2000);
            SystemLibrary.logMessage("Screenshot after select option.");
            SystemLibrary.logScreenshot(driver);

            SystemLibrary.scrollToTop(driver);
            PageObj_Roles.button_Save(driver).click();
            logMessage("Save button is cliked.");
            Thread.sleep(4000);

            currentStatus = PageObj_Roles.getPermissionButton(itemName, driver).getAttribute("class");

            if (currentStatus.contains(newStatus.toLowerCase())) {
                logMessage("The Item '" + itemName + "' is changed as " + newStatus + " successfully.");
            } else {
                logError("Failed change item '" + itemName + "' permission status.");
                errorCounter++;
            }
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean addTeam(String teamName, WebDriver driver) throws InterruptedException, IOException {

        boolean isAdded = false;
        int errorCounter = 0;

        displayTeamsPage(driver);

        if (PageObj_Teams.click_AddNewTeam(driver)) {

            SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/form", 10, 1, driver);
            Thread.sleep(2000);

            if (teamName != null) {
                PageObj_Teams.textBox_AddNewTeamForm_Teamname(driver).click();
                clearTextBox(PageObj_Teams.textBox_AddNewTeamForm_Teamname(driver));
                PageObj_Teams.textBox_AddNewTeamForm_Teamname(driver).sendKeys(teamName);
            }

            PageObj_Teams.button_AddNewTeamForm_Add(driver).click();
            SystemLibrary.logMessage("Add button is clicked in Add a new Team form.");
            SystemLibrary.waitElementInvisible(250, "//div[@id='spinner0']", driver);
            Thread.sleep(2000);

            if (SystemLibrary.waitChild("//*[@id=\"form-element-Name\"]/span[2]", 10, 1, driver) != null) {
                errorCounter++;
                String messageText = driver.findElement(By.xpath("//*[@id=\"form-element-Name\"]/span[2]")).getText();
                logWarning(messageText);
                logMessage("New Team: '" + teamName + "' is NOT added.");
                errorCounter++;

                PageObj_Teams.button_AddNewTeamForm_Close(driver).click();
                logMessage("Form - Add a New Team close buton is clicked.");

            } else {
                logMessage("Team: '" + teamName + "' is Added.");
                isAdded = true;
            }
        } else {
            errorCounter++;
        }
        //To be continue

        if (errorCounter > 0) isAdded = false;
        return isAdded;
    }

    public static boolean validateTeamMembers(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String tabName, String dateToBeShown, String leaveIconInCalendar, String popupOnHoverLeaveIcon, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        Integer teamPageType = displayTeamsPage(driver);
        if (teamPageType==0){
            teamPageType=displayTeamsPage(driver);
        }

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {

            if (tabName != null) {
                if (tabName.equals("Leave")) {
                    driver.findElement(By.xpath("//button[contains(@class, 'button') and text()='Leave']")).click();
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Leave Tab is clicked.");
                    logScreenshot(driver);
                }
            }

            if (dateToBeShown != null) {
                if (dateToBeShown.contains(";")) dateToBeShown = getExpectedDate(dateToBeShown, null);
                scrollDateInCalendar(dateToBeShown, driver);
            }
            clickViewmoreButtonInTable(driver);
            WebElement teamMemberTable = null;

            if (teamName.equals("New starters")) {
                teamMemberTable = driver.findElement(By.xpath("//div[@class='panel-body']"));
            } else {
                teamMemberTable = PageObj_Teams.table_TeamMembers(driver);
            }

            logMessage("Screenshot of current page.");
            logScreenshot(driver);

            if (expectedContent != null) {
                if (!validateTextValueContainedInElement(teamMemberTable, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!validateTextValueInElement(teamMemberTable, storeFileName, isUpdateStore, isCompare, testSerialNo, testSerialNo, emailDomainName))
                    errorCounter++;
            }

            if ((errorCounter == 0) && (leaveIconInCalendar != null)) {
                String[] strList = SystemLibrary.splitString(leaveIconInCalendar, ";");
                WebElement leaveIcon = null;

                String xpath_LeaveIcon = null;
                int totalIcon = strList.length;
                for (int i = 0; i < totalIcon; i++) {

                    if (strList[i].contains(":")) {
                        String[] subList = SystemLibrary.splitString(strList[i], ":");
                        if (expectedContent != null) {
                            xpath_LeaveIcon = "//div[@class='list-item-row members-display-item leave single-line-medium' and contains(., '" + expectedContent + "')]//div[@class='tplr-day-display " + subList[0] + " " + subList[1] + "']//div[@class='segment-inner has-leave']";
                        } else {
                            xpath_LeaveIcon = "//div[@class='tplr-day-display " + subList[0] + " " + subList[1] + "']//div[@class='segment-inner has-leave']";
                        }
                    } else {
                        if (expectedContent != null) {
                            xpath_LeaveIcon = "//div[@class='list-item-row members-display-item leave single-line-medium' and contains(., '" + expectedContent + "')]//div[@class='tplr-day-display " + strList[0] + "']//div[@class='segment-inner has-leave']";
                        } else {
                            xpath_LeaveIcon = "//div[@class='tplr-day-display " + strList[0] + "']//div[@class='segment-inner has-leave']";
                        }
                    }


                    leaveIcon = SystemLibrary.waitChild(xpath_LeaveIcon, 2, 1, driver);
                    if (leaveIcon != null) {
                        logMessage("Icon with xpath '" + xpath_LeaveIcon + "' is found.");
                    } else {
                        errorCounter++;
                        logError("Icon with xpath '" + xpath_LeaveIcon + "' is NOT found.");
                    }
                }

                if ((errorCounter == 0) && (popupOnHoverLeaveIcon != null)) {
                    Actions builder = new Actions(driver);
                    builder.moveToElement(leaveIcon).build().perform();
                    logMessage("Waiting for Pop up...");
                    WebElement popoverFrom = SystemLibrary.waitChild("//div[@class='popover visible']", 15, 1, driver);
                    logScreenshot(driver);

                    String currentPopup = "No";
                    if (popoverFrom != null) {
                        logMessage("Message pop up when Hovering Leave Icon.");
                        currentPopup = "Yes";
                    } else {
                        logMessage("No message pop up when Hovering Leave Icon..");
                    }

                    if (popupOnHoverLeaveIcon.equals(currentPopup)) {
                        logMessage("Message pop up behave as expected.");
                    } else {
                        logError("Message pop up behave is NOT as expected. ");
                        errorCounter++;
                    }


                }
            }

        } else {
            logError("Team: '" + teamName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateTeamTable(String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        int displayMode = displayTeamsPage(driver);
        boolean isPassed = true;
        int errorCounter = 0;
        String strContent = "";
        String fileName = "TableContent_" + getCurrentTimeAndDate() + ".txt";
        String fileFullName = workingFilePath + fileName;

        clickViewmoreButtonInTable(driver);

        if (displayMode == 3) {
            //Only UnAssigned group
            strContent = PageObj_Teams.table_PageRow(driver).getText();
        } else {
            if (PageObj_Teams.table_Teams(driver)!=null){
                strContent = PageObj_Teams.table_Teams(driver).getText();
            }else{
                WebElement element_TeamPage=SystemLibrary.waitChild("//div[@class='panel-body']", 2, 1, driver);
                if (element_TeamPage!=null){
                    strContent=element_TeamPage.getText();
                }else{
                    errorCounter++;
                    logError("Team Page control is NOT found.");
                }
            }

        }

        ////////////////////////

        //String currentSerailNumber=GeneralBasic.testSerialNo;
        if (strContent.contains(testSerialNo)) {
            strContent = strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)) {
            strContent = strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent = SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent = SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");


        //////

        logMessage("The table content is below:\n");
        System.out.println(strContent);
        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content " + serverUrlAddress + "/TestLog/WorkingFile/" + fileName);

        if (isUpdateStore != null) {
            if (isUpdateStore.equals("1")) {
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare != null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)) errorCounter++;
            }
        }


        if (errorCounter > 0) isPassed = false;
        return isPassed;

    }

    public static boolean validateEllipsisMenu(String menuItemName, WebDriver driver) throws IOException, InterruptedException {
        //Ellipsis Menu should be shown first
        boolean isPassed = true;
        int errorCounter = 0;

        String[] item = SystemLibrary.splitString(menuItemName, ";");

        int totalCount = item.length;
        WebElement currentMenu = null;

        for (int i = 0; i < item.length; i++) {
            currentMenu = SystemLibrary.waitChild(item[i], 2, 5, driver);
            if (currentMenu != null) {
                SystemLibrary.logMessage("Menu: '" + item[i] + "' is found.");
            } else {
                SystemLibrary.logWarning("Menu: '" + item[i] + "' is NOT found.");
                errorCounter++;
            }
        }
        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }


    public static boolean validateEllipsisMenuInTeamPage(String teamName, String menuItemName, WebDriver driver) throws IOException, InterruptedException {
        SoftAssert myAssert = new SoftAssert();
        boolean isPassed = true;
        int errorCounter = 0;
        GeneralBasic.displayTeamsPage(driver);
        WebElement element = PageObj_Teams.getEllipsisInTeamTable(teamName, driver);
        element.click();
        isPassed = GeneralBasic.validateEllipsisMenu(menuItemName, driver);
        return isPassed;
    }

    public static boolean validateEllipsisMenuInTeamDirectory(String menuItemName, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = true;
        int errorCounter = 0;
        GeneralBasic.displayTeamsPage(driver);
        WebElement element = PageObj_Teams.ellipsis_Teams_11("Directory", driver);
        element.click();
        isPassed = GeneralBasic.validateEllipsisMenu(menuItemName, driver);
        return isPassed;
    }

    public static boolean validateEllipsisMenuInTeamUnassigned(String menuItemName, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = true;
        int errorCounter = 0;
        GeneralBasic.displayTeamsPage(driver);
        WebElement element = PageObj_Teams.ellipsis_Teams_11("Unassigned", driver);
        element.click();
        isPassed = GeneralBasic.validateEllipsisMenu(menuItemName, driver);
        return isPassed;
    }

    public static boolean validateEllipsisMenuInTeamsTeamTable(String teamName, String menuItemName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        GeneralBasic.displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_TeamsTeam_20(teamName, driver).click();
        SystemLibrary.logMessage("Teams: '" + teamName + "' Ellipsis is clicked.");
        isPassed = GeneralBasic.validateEllipsisMenu(menuItemName, driver);
        return isPassed;
    }

    public static boolean renameTeam(String teamOldName, String teamNewName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;
        GeneralBasic.displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_TeamsTeam_20(teamOldName, driver).click();
        SystemLibrary.logMessage("Teams: '" + teamOldName + "' Ellipsis is clicked.");
        Thread.sleep(10000);

        driver.findElement(By.linkText("Rename team")).click();
        SystemLibrary.logMessage("Rename Team menu is clicked.");

        if (SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div", 5, 1, driver) != null) {
            //Govind added on 20/06/18 added textBox clear
            clearTextBox(PageObj_Teams.textBox_RenameTeamName(driver));
            Thread.sleep(1000);
            PageObj_Teams.textBox_RenameTeamName(driver).sendKeys(teamNewName);
            SystemLibrary.logMessage("Team New Name: " + teamNewName + " is input.");

            logMessage("Log Screenshot before click Save button.");
            SystemLibrary.logScreenshot(driver);
            PageObj_Teams.button_RenameTeam_Save(driver).click();
            Thread.sleep(10000);
            SystemLibrary.logMessage("Save button is clicked.");

        } else {
            SystemLibrary.logError("Rename Team window is NOT shown.");
            errorCounter++;
        }
        logScreenshot(driver);
        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean addTeamMember(String teamName, String keyword, String assignAsManager, String firstName, String middleName, String lastName, String preferredName, String removeFromTeam, String defaultApprovalTeam, String expectedMessage, String testSerialNo, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isAdded = false;
        int errorCounter = 0;

        GeneralBasic.displayTeamsPage(driver);
        Thread.sleep(2000);
        if (PageObj_Teams.displayTeamMembers(teamName, driver)) {
            PageObj_Teams.ellipsis_TeamsTeam_20(driver).click();
            Thread.sleep(5000);
            SystemLibrary.logMessage("Teams: '" + teamName + "' Ellipsis is clicked.");
            logScreenshot(driver);

            WebElement link_AddAMember = SystemLibrary.waitChild("Add a member", 15, 5, driver);
            if (link_AddAMember != null) {
                link_AddAMember.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Add a Member menu is clicked.");
            } else {
                logMessage("Click the TeamsTeam_20 Again.");
                PageObj_Teams.ellipsis_TeamsTeam_20(driver).click();
                Thread.sleep(5000);
                SystemLibrary.logMessage("Teams: '" + teamName + "' Ellipsis is clicked the 2nd times.");

                ///////////////////
                link_AddAMember = SystemLibrary.waitChild("Add a member", 15, 5, driver);
                if (link_AddAMember != null) {
                    link_AddAMember.click();
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Add a Member menu is clicked.");
                }
                //////
            }
            Thread.sleep(2000);


            if (SystemLibrary.waitChild("//h4[@class='mc-header-heading' and text()='Add a Member']", 30, 1, driver) != null) {
                logMessage("Add a Member dialogue is shown.");
                Thread.sleep(3000);
                logScreenshot(driver);
                PageObj_Teams.textBox_Name_AddTeamMember(driver).click();
                Thread.sleep(2000);

                /*
                /////After Discussion with Jim, we are sending letter by letter 22042021
                String singleS=null;
                for (int i=0;i<keyword.length();i++){
                    singleS=Character.toString(keyword.charAt(i));
                    PageObj_Teams.textBox_Name_AddTeamMember(driver).sendKeys(singleS);
                    Thread.sleep(2000);
                }*/

                PageObj_Teams.textBox_Name_AddTeamMember(driver).sendKeys(keyword);
                logMessage("Search Keyword '" + keyword + "' is typed.");
                Thread.sleep(6000);

                GeneralBasic.waitSpinnerDisappear(120, driver);
                logScreenshot(driver);
                String fullName = null;
                if (firstName.contains("Admin")) {
                    firstName = "Test" + testSerialNo + ".Admin";
                }

                if (preferredName == null) {
                    fullName = firstName + " " + lastName;
                } else {
                    fullName = firstName + " (" + preferredName + ") " + lastName;
                }

                //Click name from the dropdown window
                //Govind updated on 20/06/18 removed one span from below
                WebElement element_DropdownItem = SystemLibrary.waitChild("//span[text()='" + fullName + "']", 30, 1, driver);
                element_DropdownItem.click();
                logMessage("Member '" + fullName + "' is clicked.");

                if (assignAsManager != null) {
                    SystemLibrary.tickCheckbox(assignAsManager, PageObj_Teams.checkBox_AddMember_AssignAsManager(driver), driver);
                }

                logMessage("Screenshot before click Next button in Add a Member screen.");
                logScreenshot(driver);
                PageObj_Teams.button_AddMember_Next(driver).click();
                logMessage("Next button is clicked in Add a Member screen.");
                Thread.sleep(5000);
                logScreenshot(driver);

                if (removeFromTeam != null) {
                    if (SystemLibrary.waitChild("//h5[contains(text(),'Remove From')]", 10, 1, driver) != null) {
                        WebElement checkbox_TeamName = SystemLibrary.waitChild("//div[@class='checkBoxWrapper' and contains(., '" + removeFromTeam + "')]//input", 10, 1, driver);
                        if (checkbox_TeamName != null) {
                            checkbox_TeamName.click();
                            Thread.sleep(3000);
                            logMessage("Remove from Team '" + removeFromTeam + "' is checked.");
                        } else {
                            logError("Remove from Team '" + removeFromTeam + "' is NOT found.");
                            errorCounter++;
                        }
                    } else {
                        logError("Remove From Team is NOT shown.");
                        errorCounter++;
                    }

                    logScreenshot(driver);
                }

                int counter1 = 0;
                while (SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver) != null) {

                    PageObj_Teams.button_AddMember_Next(driver).click();
                    Thread.sleep(5000);
                    counter1++;
                    logMessage("Click Next button " + counter1 + " times");
                    logScreenshot(driver);

                    if ((SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver) != null)){
                        if ((counter1>10)||(SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver).isEnabled()==false)) {
                            logError("Next Button is too much or Next Button is not enable.");
                            errorCounter++;
                            logScreenshot(driver);
                            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                            Thread.sleep(3000);
                            logMessage("Close button is clicked.");
                            logScreenshot(driver);
                            break;
                        }
                    }


                }

                /////////// Check and select Default Approval Team if required.  //////////////
                ////////////////
                if (defaultApprovalTeam != null) {
                    if (SystemLibrary.waitChild("//h5[@class='fieldset-legend undefined'][contains(text(),'Default Approval Team')]", 2, 1, driver) != null) {
                        //Locate the radio button
                        WebElement radioButton = SystemLibrary.waitChild("//div[@class='fc-radio-option ' and contains(., '" + defaultApprovalTeam + "')]//input", 1, 1, driver);
                        if (radioButton != null) {
                            radioButton.click();
                            logMessage("Default Approval Team: " + defaultApprovalTeam + " is selected.");
                        } else {
                            logError("Default Approval Team: " + defaultApprovalTeam + " is NOT found.");
                            errorCounter++;
                        }
                        logScreenshot(driver);
                    } else {
                        errorCounter++;
                        logError("Default Approval Team is NOT shown.");
                    }
                }
                //////

                Thread.sleep(5000);
                logMessage("Screenshot before click Done button.");
                logScreenshot(driver);
                if (PageObj_Teams.button_AddMember_Done(driver)!=null){
                    PageObj_Teams.button_AddMember_Done(driver).click();
                    Thread.sleep(10000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Done button is clicked in Add a Member screen.");
                    logScreenshot(driver);
                }


                if (expectedMessage != null) {
                    if (waitChild("//form/div[@class='mc-header']/h4[text()='Pending Approvals']", 30, 1, driver) != null) {
                        String currentMessage = driver.findElement(By.xpath("//form//div[@class='mc-body']")).getText();
                        logMessage("The current message is below:\n" + currentMessage);

                        if (currentMessage.contains(expectedMessage)) {
                            logMessage("Message is shown as expected.");
                        } else {
                            logError("Message is NOT shown as expected.");
                            logMessage("Expected message is below:\n" + expectedMessage);
                        }

                        driver.findElement(By.xpath("//div[@class='modal-footer']/button[@class='button--primary ' and text()='Ok']")).click();
                        Thread.sleep(10000);
                        waitSpinnerDisappear(120, driver);
                        logMessage("OK button is clicked.");
                    }
                }

                logScreenshot(driver);
                //Extra wait for OK butotn if it is shown
                /////////
                WebElement button_OK = waitChild("//div[@class='modal-footer']/button[@class='button--primary ' and text()='Ok']", 10, 1, driver);
                if (button_OK != null) {
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("OK button is clicked.");
                }

                //////

                GeneralBasic.waitSpinnerDisappear(120, driver);
                if (errorCounter==0){
                    logMessage("Team Member: '" + fullName + "' is added into Team '" + teamName + "' successfully.");
                    isAdded = true;
                }

            } else {
                errorCounter++;
            }
        }


        if (errorCounter == 0) isAdded = true;
        return isAdded;
    }


    public static boolean editWebAPIConfiguration(String userName, String apiKey, String databaseAlias, String termFromDate, String errorMessageReturn, String getValueFromFile, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (getValueFromFile != null) {
            String keyFilePathName = "C:\\TestAutomationProject\\ESSRegTest\\DataSource\\GUIKey_" + testSerialNo + ".txt";
            if (getValueFromFile.equals("1")) {
                userName = SystemLibrary.getValueFromListFile("WebAPI UserName", ": ", keyFilePathName);
                apiKey = SystemLibrary.getValueFromListFile("GUI Key", ": ", keyFilePathName);
                databaseAlias = SystemLibrary.getValueFromListFile("databaseAlias", ": ", keyFilePathName);
            }
        }

        displaySettings_General(driver);

        String xpath_EllipsisButton_API="//div[@class='list-item-row single-line-huge no-heading' and contains(., '"+apiKey+"')]//button";
        WebElement ellipsisButton_API=SystemLibrary.waitChild(xpath_EllipsisButton_API, 10, 1, driver);
        if (ellipsisButton_API!=null) {
            ellipsisButton_API.click();
            logMessage("API Ellipsis Button is found and clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[contains(text(),'Edit')]")).click();
            logMessage("Web API Edit menu is clicked.");
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logScreenshot(driver);

            ///////////////// Insert Here /////////////////

            if (userName != null) {
                PageObj_General.textbox_Username(driver).click();
                clearTextBox(PageObj_General.textbox_Username(driver));
                PageObj_General.textbox_Username(driver).sendKeys(userName);
                logMessage("Username: " + userName + " is input.");
            }

            //Cannot edit password


            if (apiKey != null) {
                PageObj_General.textbox_Apikey(driver).click();
                clearTextBox(PageObj_General.textbox_Apikey(driver));
                PageObj_General.textbox_Apikey(driver).sendKeys(apiKey);
                logMessage("Api Key: " + apiKey + " is input.");
            }

            if (databaseAlias != null) {
                PageObj_General.textbox_DatabaseAlias(driver).click();
                clearTextBox(PageObj_General.textbox_DatabaseAlias(driver));
                PageObj_General.textbox_DatabaseAlias(driver).sendKeys(databaseAlias);
                logMessage("Database Alias: " + databaseAlias + " is input.");
            }

            if (termFromDate != null) {

                if (termFromDate.contains(";")) {
                    termFromDate = SystemLibrary.getExpectedDate(termFromDate, null);
                }

                PageObj_General.textbox_TermFromDate(driver).click();
                clearTextBox(PageObj_General.textbox_TermFromDate(driver));
                PageObj_General.textbox_TermFromDate(driver).sendKeys(termFromDate);
                logMessage("Import Terminated Employees from Date: " + termFromDate + " is input.");
            }

            logMessage("Screenshot before click Save Button.");
            logScreenshotElement(driver, PageObj_General.form_AddEditAPIKey(driver));

            driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click();
            logMessage("Button Save is clicked.");
            Thread.sleep(30000);
            logScreenshot(driver);

            String currentErrorMessage = PageObj_General.getReturnErrorMessageFromIntegrationScreen(driver);
            if (currentErrorMessage != null) {
                if (currentErrorMessage.length() > 0) {
                    logMessage("The current return message is:\n" + currentErrorMessage);
                    logMessage("The current screenshot of error message from screen.");
                    logScreenshotElement(driver, PageObj_General.label_ErrorMessageReturn(driver));
                }
            }

            if (errorMessageReturn != null) {

                try {
                    if (currentErrorMessage.equals(errorMessageReturn)) {
                        logMessage("The current return message is: " + currentErrorMessage);
                        logMessage("Message return is shown as expected.");
                    } else {
                        String strMessage = "The current return message is NOT the same as expected. \nThe Current return Message is:\n " + currentErrorMessage + "\nExpceted Message is:\n " + errorMessageReturn;
                        logError(strMessage);
                        errorCounter++;
                    }
                } catch (Exception e) {
                    logMessage("No return message is currently return.");
                    errorCounter++;
                }
            }


            ////// End of Insert here //////

        }else{
            logError("API button is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean deleteTeam(String teamName, String storeFileName, String isUpdateStore, String isCompare, String isCancelDelete, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {

        boolean isDeleted = false;
        int errorCounter = 0;

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_TeamsTeam_20(teamName, driver).click();
        Thread.sleep(2000);
        SystemLibrary.logMessage("Teams: '" + teamName + "' Ellipsis is clicked.");

        driver.findElement(By.linkText("Delete team")).click();
        logMessage("Delete team menu is clicked.");
        Thread.sleep(2000);
        //after click
        String messageBoxLableText = PageObj_Teams.dialogueBox_DeleteTeam(driver).getText();

        if (storeFileName != null) {
            SystemLibrary.validateTextValueInElement(PageObj_Teams.dialogueBox_DeleteTeam(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName);
        }

        if (messageBoxLableText.contains("Cannot Delete the Team")) {
            logWarning("Failed delete Team: '" + teamName + "'. Warning Message below:");
            System.out.println(messageBoxLableText);
            logMessage("Screenshot before click OK Button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/div[3]/div/button")).click();
            logMessage("OK button is clicked.");
        } else if (messageBoxLableText.contains("Are you sure you want to delete")) {
            if (isCancelDelete != null) {
                if (isCancelDelete.equals("1")) {
                    driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/div[3]/div/button[2]")).click();
                    Thread.sleep(2000);
                    logMessage("Cancel button is clicked.");
                    logMessage("Delete Team: '" + teamName + "' is cacelled.");
                }
            } else {
                driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/div[3]/div/button[1]")).click();
                Thread.sleep(5000);
                logMessage("Yes, delete team Button is clicked.");
                logMessage("Team: '" + teamName + "' is deleted.");
                isDeleted = true;
            }

        }

        return isDeleted;
    }

    public static boolean removeMemberFromTeam_OLD(String teamName, String storeFileName, String isUpdateStore, String isCompare, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isRemoved = false;
        int errorCounter = 0;

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        SystemLibrary.logMessage("Team member: '" + fullName + "' Ellipsis is clicked.");

        driver.findElement(By.linkText("Remove from team")).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        logMessage("Remove from team menu is clicked.");

        //after click
        String messageBoxLableText = PageObj_Teams.lable_RemoveMember_Messagebox(driver).getText();

        if (storeFileName != null) {
            SystemLibrary.validateTextValueInElement(PageObj_Teams.dialogueBox_RemovefromTeam(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName);
        }
        if (messageBoxLableText.contains("Remove " + fullName + " from " + teamName)) {
            logMessage("Screenshot before click Done Button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/div/button")).click();
            Thread.sleep(5000);
            logMessage("Done button is clicked.");
            logMessage("Team Member: '" + fullName + "' is removed from '" + teamName + "'.");
            isRemoved = true;
        } else {
            SystemLibrary.logError("Team member '" + fullName + "' is NOT Removed from Team: '" + teamName + "'.");
        }
        return isRemoved;
    }

    public static boolean changeMemberRole_ViaTeamsPage(String teamName, String assignAsManager, String role, String storeFileName, String isUpdate, String isCompare, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        //funciton is under adjustment, current only work for assign as manager
        boolean isDone = false;
        int errorCounter = 0;

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        Thread.sleep(2000);
        logMessage("Ellipsis button is clicked.");
        GeneralBasic.waitSpinnerDisappear(60, driver);

        driver.findElement(By.linkText("Change role")).click();
        Thread.sleep(3000);
        logScreenshot(driver);
        logMessage("Change role menu is clicked.");
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logScreenshot(driver);
        //Waiting for Change Role dialogue
        if (SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/form", 10, 1, driver) != null) {
            if (assignAsManager != null) {
                int i = 0;
                while (!PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver).isEnabled()) {
                    Thread.sleep(1000);
                    i++;
                    if (i > 60) break;
                }
                SystemLibrary.tickCheckbox(assignAsManager, PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver), driver);
                Thread.sleep(2000);
            }

            if (role != null) {
                WebElement role_dropdown=PageObj_Teams.select_Role_Dropdown(driver);
                role_dropdown.click();
                logMessage("Role Dropdown button is clicked.");
                Thread.sleep(2000);
                logScreenshot(driver);

               String xpath_dropdownlist = "/html/body/div[1]/div/div[2]/div[2]/div/form/div[2]/div/div[3]/div/fieldset/div[2]/div/div[2]//div[contains(., '"+role+"')]";
                WebElement dropdownlistItem = SystemLibrary.waitChild(xpath_dropdownlist, 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Manager Role '" + role + "' is selected.");
                } else {
                    logError("Manager Role '" + role + "' is NOT found.");
                    errorCounter++;
                }

                logMessage("Log screenshot after changing manager role.");
                logScreenshot(driver);

            }

            WebElement nextButton=PageObj_Teams.button_ChangeRole_Next(driver);
            ///////////// Jim Adjusted with UI changes on 10/01/2021 //////////////
            if (nextButton!=null){
                nextButton.click();
                logMessage("Next button is clicked.");
                Thread.sleep(2000);
                logScreenshot(driver);
            }


            if (storeFileName != null) {
                if (!SystemLibrary.validateTextValueInElement(PageObj_Teams.dialogueBox_ChangeRole(driver), storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }

            ////////////////// Add script handling extra remove from screen.
            int nextButtonCounter=0;
            nextButton=null;
            for (int j=1;j<10;j++){
                nextButton= PageObj_Teams.button_ChangeRole_Next(driver);
                if (nextButton!=null){
                    ///////////// Pending handling extra scenarios in future ////////////

                    //////
                    nextButton.click();
                    Thread.sleep(2000);
                    logMessage("The Next button is clicked the "+String.valueOf(nextButtonCounter)+" times.");
                    logScreenshot(driver);
                }else{
                    break;
                }
            }
            //////


            WebElement button_ChangeRole_Done=SystemLibrary.waitChild("//button[text()='Done']", 2, 1, driver);
            if (button_ChangeRole_Done!=null){
                button_ChangeRole_Done.click();
                Thread.sleep(15000);
                logMessage("Done button Shown and clicked in Change Role Popup.");
            }else{
                logError("Done Button Not Shown in Change Role Popup.");
                errorCounter++;
            }

            logScreenshot(driver);
            /*   PageObj_Teams.button_ChangeRole_Done(driver).click();
            logMessage("Done button is clicked.");
            Thread.sleep(2000);
            logScreenshot(driver);
            */
            SystemLibrary.waitElementInvisible(30, "//*[@id=\"modal-loader-spinner\"]/div/div[2]/div", driver);

            if (SystemLibrary.waitChild("//div[@class='sb-content' and text()='Cannot assign person into the unassigned team.']", 3, 1, driver)!=null){
                logError("Cannot assign person into the unassigned team.");
                errorCounter++;
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                logMessage("Change role diglogue is closed.");
                logScreenshot(driver);
            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean moveMemberToTeam(String teamName, String newTeamName, String assignAsManager, String memberRole, String firstName, String middleName, String lastName, String preferredName, String removeFromTeam, String defaultApprovalTeam, String storeFileName, String isUpdate, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        boolean isContinue = true;
        GeneralBasic.displayTeamsPage(driver);

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }
        //PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        WebElement ellipsisButton_Member = PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver);

        if (ellipsisButton_Member != null) {
            ellipsisButton_Member.click();
            Thread.sleep(3000);
            SystemLibrary.logMessage("Team: '" + fullName + "' Ellipsis is clicked.");

            driver.findElement(By.linkText("Move to team")).click();
            logMessage("Move to team menu is clicked.");
        }

        if (SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/form", 15, 1, driver) != null) {
            Thread.sleep(5000);
            PageObj_Teams.textBox_MemberMovetoTeam(driver).click();

            //Only typing the first 3 charactors
            String subTeamName = newTeamName.substring(0, 3);
            PageObj_Teams.textBox_AddTeam(driver).sendKeys(subTeamName);
            Thread.sleep(5000);
            //Click team from the dropdown window
            WebElement element_DropdownItem = SystemLibrary.waitChild("//div/span[text()='" + newTeamName + "']", 30, 1, driver);
            element_DropdownItem.click();
            Thread.sleep(3000);
            logMessage("Team '" + newTeamName + "' is clicked from the dropdown list.");
            logScreenshot(driver);

            if (assignAsManager != null) {
                SystemLibrary.tickCheckbox(assignAsManager, PageObj_Teams.checkBox_MoveToTeam_AssignAsManager(driver), driver);
            }

            if (memberRole != null) {

                WebElement form = SystemLibrary.waitChild("//div[@class='focus-trap-container']//form", 10, 1, driver);
                SystemLibrary.clickElement(117, 320, driver, form);
                Thread.sleep(10000);
                logMessage("Member role is clicked.");
                logScreenshot(driver);

                WebElement memberRoleList = SystemLibrary.waitChild("//div[contains(@class,'list-item') and text()='" + memberRole + "']", 10, 1, driver);
                if (memberRoleList != null) {

                    if (memberRole.equals("Leave For Manager")) {
                        clickElement(135, 354, driver, form);
                    } else if (memberRole.equals("Member")) {
                        clickElement(135, 389, driver, form);
                    } else if (memberRole.equals("Member Terminated")) {
                        clickElement(135, 421, driver, form);
                    }

                    Thread.sleep(3000);
                    logMessage("Member Role '" + memberRole + "' is clicked.");
                    logScreenshot(driver);

                    if (storeFileName != null) {
                        if (!validateTextValueInElement(form, storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                            errorCounter++;
                    }


                } else {
                    errorCounter++;
                    logError("Member Role '" + memberRole + "' is NOT found.");
                    isContinue = false;

                }

            }

            if (assignAsManager != null) {
                if ((assignAsManager.equals("1") && (memberRole != null))) isContinue = false;
            }

            if (isContinue) {
//////////////////
                String buttonText = driver.findElement(By.xpath("//button[contains(@class,'button--primary')]")).getText();
                if (buttonText.equals("Next")) {
                    int counter1 = 0;
                    while (SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver) != null) {
                        logMessage("Screenshot before click button.");
                        logScreenshot(driver);

                        PageObj_Teams.button_AddMember_Next(driver).click();
                        Thread.sleep(5000);
                        counter1++;
                        logMessage("Click Next button " + counter1 + " times");
                        logScreenshot(driver);

                        WebElement panel_RemoveFrom = SystemLibrary.waitChild("//h5[@class='fieldset-legend undefined'][contains(text(),'Remove From')]", 5, 1, driver);
                        WebElement panel_DefaultApprovalTeam = SystemLibrary.waitChild("//h5[@class='fieldset-legend undefined'][contains(text(),'Default Approval Team')]", 5, 1, driver);

                        if ((panel_RemoveFrom != null) && (removeFromTeam != null)) {
                            String[] teamOption = SystemLibrary.splitString(removeFromTeam, ";");
                            int totalItem = teamOption.length;

                            for (int i = 0; i < totalItem; i++) {
                                String[] currentOption = SystemLibrary.splitString(teamOption[i], ":");
                                String xpath_Checkbox = "//div[@class='group clear pad-bottom']//div[@class='checkBoxWrapper' and contains(., '" + currentOption[0] + "')]//input[@type='checkbox']";
                                WebElement currentCheckbox = SystemLibrary.waitChild(xpath_Checkbox, 5, 1, driver);
                                if (currentCheckbox != null) {
                                    logMessage("Checkbox '" + currentOption[0] + "' is found.");
                                    SystemLibrary.tickCheckbox(currentOption[1], currentCheckbox, driver);
                                }
                            }

                        } else if ((panel_DefaultApprovalTeam != null) && (defaultApprovalTeam != null)) {
                            if (panel_DefaultApprovalTeam.isDisplayed()) {
                                logMessage("Screenshot before click button.");
                                logScreenshot(driver);

                                String xpath_RadioButton = "//div[@id='form-element-WorkflowType']/div[contains(., '" + defaultApprovalTeam + "')]//input[@name='WorkflowType']";
                                driver.findElement(By.xpath(xpath_RadioButton)).click();
                                Thread.sleep(3000);
                                driver.findElement(By.xpath(xpath_RadioButton)).click();
                                Thread.sleep(3000);
                                logMessage("Default Approval Team '" + defaultApprovalTeam + "' is selected.");
                            }

                        }

                    }
                }

                logMessage("Screenshot before click button.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//button[contains(@class,'button--primary')]")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Done button is clicked.");
                GeneralBasic.waitSpinnerDisappear(30, driver);

                WebElement extraMessageBox = SystemLibrary.waitChild("//div[@id='modal-content']//div//form[contains(., 'Move to team')]", 10, 1, driver);
                if (extraMessageBox != null) {
                    logMessage("Screenshot of next message before click Done buton");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[contains(@class,'button--primary') and contains(., 'Done')]")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Done button is clicked.");

                }

                isDone = true;
                logMessage("Team Member '" + fullName + "' under Team: '" + teamName + "' is moved into Team '" + newTeamName + "' successfully.");
                ////////
            } else {
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                Thread.sleep(3000);
                logError("Failed Move to team. Close button is clicked.");
                logScreenshot(driver);
            }


        } else {
            errorCounter++;
            logError("Move to Team form is NOT shown.");
        }

        if (errorCounter > 0) isDone = false;
        return isDone;
    }


    public static int searchUser(String keyword, WebDriver driver) throws IOException, InterruptedException {
        int totalCount = 0;
        PageObj_NavigationBar.textbox_Search(driver).click();
        clearTextBox(PageObj_NavigationBar.textbox_Search(driver));
        PageObj_NavigationBar.textbox_Search(driver).sendKeys(keyword);
        logMessage("Search keys '" + keyword + "' is input.");
        PageObj_NavigationBar.button_Search(driver).click();
        Thread.sleep(10000);

        GeneralBasic.waitSpinnerDisappear(30, driver);

        //Wait for search result table
        if (SystemLibrary.waitChild("//*[contains(text(), 'People') and @class='normal-header']", 60, 1, driver) != null) {
            totalCount = SystemLibrary.getTotalCountFromTable("//*[@id=\"page-container\"]/main/div/div[2]/div/div/div/div[2]/form/div", driver);
            logMessage("There are total " + totalCount + " item(s) found.");
        } else {
            logWarning("No item is found.");
        }

        logMessage("Screenshot after click search button.");
        logScreenshot(driver);
        return totalCount;
    }

    public static boolean searchUserAndDisplayPersonalInformationPage_ViaAdmin(String keyword, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        //This function is only working when log on as Admin User.
        boolean isDisplay = false;
        int errorCounter = 0;

        if ((keyword.startsWith("Test"))||(keyword.startsWith("Testsn"))) {
            keyword="Test"+testSerialNo+".Admin";
        }

        if ((firstName.startsWith("Test"))||(firstName.startsWith("Testsn"))){
            firstName="Test"+testSerialNo+".Admin";
        }

        String fullName = "";
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        if (searchUser(keyword, driver) > 0) {
            //WebElement userNameLink = SystemLibrary.waitChild(fullName, 10, 5, driver);
            String xpath_userNameLink="//a[@class='link heading-container' and contains(.., '"+fullName+"')]";
            WebElement userNameLink = SystemLibrary.waitChild(xpath_userNameLink, 10, 1, driver);
            if (userNameLink != null) {
                userNameLink.click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Screenshot after click item.");
                SystemLibrary.logScreenshot(driver);
                //logDebug("//h3[contains(text(),'"+fullName+"')");
                if (SystemLibrary.waitChild("//h3[contains(text(),'" + fullName + "')]", 30, 1, driver) != null) {
                    Thread.sleep(30000);
                    logMessage("User: " + fullName + "'s Personal Information Page is shown.");
                    isDisplay = true;
                } else {
                    logError("User: " + fullName + "'s Personal Information Page is NOT shown.");
                }
            } else {
                SystemLibrary.logError("User: '" + fullName + "' is NOT found.");
            }
        }

        return isDisplay;
    }

    public static boolean validateUserSideNavigationMenu(String keyword, String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;
        if (GeneralBasic.searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            if (storeFileName != null) {
                if (!SystemLibrary.validateTextValueInElement(PageObj_Dashboard.sideNavigationPanel(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;

        return isPassed;
    }


    public static boolean editPersonalInformation(String keyword, String givenName, String middleName, String surname, String preferredName, String newGivenName, String newMiddleName, String newSurname, String newPreferredName, String gender, String dob, String maritalStatus, String medicalCondition, String medicalTreatment, String actionMethod, String actionMethodOfMedicalConditions, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        //actionMethod is not developed yet.
        boolean isDone = false;
        int errorCounter = 0;

        //actionMethod: 1- Approved, 2- Declined, else Approve later.
        if (keyword.contains("ExtraAdminUser")) {
            keyword = getFirstname_FromExtraAdminDetails();
            givenName = keyword;
            surname = getLastname_FromExtraAdminDetails();
        }

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, givenName, middleName, surname, preferredName, testSerialNo, driver)) {
            PageObj_SideNavigationBar.sideMenu_personalInformation(driver).click();
            Thread.sleep(20000);
            logMessage("Side Navigation Bar - Personal Information is clicked.");

            //Initialize Page Object
            //PageObj_PersonalInfo PerInfo = PageFactory.initElements(driver, PageObj_PersonalInfo.class);

            if ((newGivenName != null) || (newMiddleName != null) || (newSurname != null) || (newPreferredName != null)) {
                int timeCounter = 0;
                while (!PageObj_PersonalInfo.button_EditName(driver).isEnabled()) {
                    Thread.sleep(1000);
                    timeCounter++;
                    if (timeCounter > 60) break;
                }
                logMessage("It takes " + timeCounter + " seconds to display Personal Information.");
                PageObj_PersonalInfo.button_EditName(driver).click();
                logMessage("Edit NAME button is clicked.");

                if (newGivenName != null) {
                    PageObj_PersonalInfo.textBox_EditName_GivenName(driver).click();
                    clearTextBox(PageObj_PersonalInfo.textBox_EditName_GivenName(driver));
                    PageObj_PersonalInfo.textBox_EditName_GivenName(driver).sendKeys(newGivenName);
                    logMessage("Given name: " + newGivenName + " is input.");
                }

                if (newMiddleName != null) {
                    PageObj_PersonalInfo.textBox_EditName_MiddleName(driver).click();
                    clearTextBox(PageObj_PersonalInfo.textBox_EditName_MiddleName(driver));
                    PageObj_PersonalInfo.textBox_EditName_MiddleName(driver).sendKeys(newMiddleName);
                    logMessage("Middle name: " + newMiddleName + " is input.");
                }

                if (newSurname != null) {
                    PageObj_PersonalInfo.textBox_EditName_Surname(driver).click();
                    clearTextBox(PageObj_PersonalInfo.textBox_EditName_Surname(driver));
                    PageObj_PersonalInfo.textBox_EditName_Surname(driver).sendKeys(newSurname);
                    logMessage(" Surname: " + newSurname + " is input.");
                }

                if (newPreferredName != null) {
                    //Attention: DO NOT use clear() function in this screen, as it does NOT trigger the enable status of Save Button.
                    //PageObj_PersonalInfo.textBox_EditName_Preferredname(driver).clear();
                    PageObj_PersonalInfo.textBox_EditName_Preferredname(driver).click();

                    if (!newPreferredName.equals("-1")) {
                        PageObj_PersonalInfo.textBox_EditName_Preferredname(driver).sendKeys(newPreferredName);
                        logMessage(" Preferred Name: " + newPreferredName + " is input.");
                    } else {
                        PageObj_PersonalInfo.textBox_EditName_Preferredname(driver).sendKeys(Keys.CONTROL, "a", Keys.DELETE);
                        logMessage("Preferred Name is deleted.");
                    }

                }

                logMessage("Screenshot before click Save button.");
                logScreenshotElement(driver, PageObj_PersonalInfo.form_EditName(driver));

                if (PageObj_PersonalInfo.button_EditName_Save(driver).isEnabled()) {
                    PageObj_PersonalInfo.button_EditName_Save(driver).click();
                    Thread.sleep(10000);
                    logMessage("Save button is clicked in the Edit Name screen.");
                    logMessage("Edit User: '" + keyword + "' Personal Information successfully.");
                } else {
                    PageObj_PersonalInfo.button_EditName_Close(driver).click();
                    errorCounter++;
                    logWarning("No change has been made.");
                }
            }

            ///////////////////////////////////
            if ((gender != null) || (dob != null) || (maritalStatus != null)) {
                PageObj_PersonalInfo.button_PersInfoAddinfoEdit(driver).click();
                Thread.sleep(3000);
                waitSpinnerDisappear(120, driver);
                logMessage("Edit Additional Information button is clicked.");

                if (gender != null) {
                    selectItemFromESSDropdownList(PageObj_PersonalInfo.dropdownlist_EditAddtionalInfo_Gender(driver), gender, driver);
                    logMessage("Gender '" + gender + "' is selected.");
                }

                if (dob != null) {
                    PageObj_PersonalInfo.textBox_EditAdditionalInfo_DOB(driver).click();
                    clearTextBox(PageObj_PersonalInfo.textBox_EditAdditionalInfo_DOB(driver));
                    PageObj_PersonalInfo.textBox_EditAdditionalInfo_DOB(driver).sendKeys(dob);
                    logMessage("Date of Birth " + dob + " is input.");
                }

                if (maritalStatus != null) {
                    selectItemFromESSDropdownList(PageObj_PersonalInfo.dropdownlist_EditAdditionalInfo_MaritalStatus(driver), maritalStatus, driver);
                    logMessage("Marital Status '" + maritalStatus + "' is selected.");
                }

                logMessage("Screenshot before click Save button.");
                logScreenshot(driver);

                if (driver.findElement(By.xpath("//button[@type='submit' and text()='Save']")).isEnabled()) {
                    driver.findElement(By.xpath("//button[@type='submit' and text()='Save']")).click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Edit Addtional Information is saved.");

                    if (SystemLibrary.waitChild("//form[@autocomplete='off' and contains(., 'Edit Additional Information')]", 5, 1, driver) != null) {
                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        Thread.sleep(3000);
                        logWarning("Close Edit Additional Information button is closed. No change is saved.");
                        errorCounter++;
                    }

                    logScreenshot(driver);
                } else {
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(3000);
                    logWarning("Close Edit Additional Information button is closed. No change is saved.");
                    errorCounter++;
                }
            }

            //////

            ///////////////////////// Medical Condition  ///////////////////////////
            if (actionMethodOfMedicalConditions != null) {
                if (!actionMethodOfMedicalConditions.equals("Delete")) {
                    //Script does NOT handle Deleting Medical Conditions at this stage
                    if (actionMethodOfMedicalConditions.equals("Add")) {
                        PageObj_PersonalInfo.button_AddMedicalConditions(driver).click();
                        Thread.sleep(3000);
                        logMessage("Add Medical Condition button is clicked.");
                    } else if (actionMethodOfMedicalConditions.equals("Edit")) {
                        PageObj_PersonalInfo.ellipsis_EditMedicalConditions(driver).click();
                        logMessage("Ellipsis Edit Medical Conditnons is clicked.");
                        Thread.sleep(3000);

                        PageObj_PersonalInfo.menu_Edit_MedicalCondition(driver).click();
                        logMessage("Edit Medical Condition butotn is clicked.");
                        Thread.sleep(3000);

                    }

                    if (SystemLibrary.waitChild("//form[@autocomplete='off']", 60, 1, driver) != null) {
                        logMessage("Add Medical Condition form is shown.");
                        logScreenshot(driver);

                        if (medicalCondition != null) {
                            PageObj_PersonalInfo.textBox_EditMedicalConditions_MedicalCondition(driver).click();
                            clearTextBox(PageObj_PersonalInfo.textBox_EditMedicalConditions_MedicalCondition(driver));
                            PageObj_PersonalInfo.textBox_EditMedicalConditions_MedicalCondition(driver).sendKeys(medicalCondition);
                            Thread.sleep(2000);
                            logMessage("Medical Condition: " + medicalCondition + " is input.");

                        }

                        if (medicalTreatment != null) {
                            PageObj_PersonalInfo.textBox_EditMedicalConditions_Treatment(driver).click();
                            clearTextBox(PageObj_PersonalInfo.textBox_EditMedicalConditions_Treatment(driver));
                            PageObj_PersonalInfo.textBox_EditMedicalConditions_Treatment(driver).sendKeys(medicalTreatment);
                            Thread.sleep(2000);
                            logMessage("Medical Treatment: " + medicalTreatment + " is input.");
                        }

                        logMessage("Screenshot before click Save button. ");
                        logScreenshot(driver);

                        if (driver.findElement(By.xpath("//button[@type='submit' and (text()='Save')]")).isEnabled()) {
                            PageObj_PersonalInfo.button_MedicalCondition_Save(driver).click();
                            Thread.sleep(4000);
                            GeneralBasic.waitSpinnerDisappear(120, driver);
                            logMessage("Save button is clicked.");
                            logScreenshot(driver);
                        } else {
                            PageObj_PersonalInfo.button_MedicalCondition_Close(driver).click();
                            errorCounter++;
                            logWarning("No change has been made.");
                            logScreenshot(driver);
                        }
                    }
                } else {
                    String currentPanelMessage = PageObj_PersonalInfo.panel_MedicalConditions(driver).getText();
                    if ((currentPanelMessage.contains(medicalCondition)) && (currentPanelMessage.contains(medicalTreatment))) {
                        driver.findElement(By.xpath("//div[@id='ea-medical-conditions']//button[@class='button--plain add-button']")).click();
                        Thread.sleep(3000);
                        logMessage("Ellipsis button in Medical Condition panel is clicked.");
                        logMessage("Screenshot before click delete button.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@id='ea-medical-conditions']//ul/li[contains(., 'Delete')]")).click();
                        Thread.sleep(2000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        logMessage("Delete button is clicked.");
                        logMessage("Screenshot after delete Medical Condition.");
                        logScreenshot(driver);


                    }


                }


            }
            //////

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static void waitSpinnerDisappear(int maxWaitInSeconds, WebDriver driver) throws InterruptedException {
        Thread.sleep(2000);
        SystemLibrary.waitElementInvisible(maxWaitInSeconds, "//*[contains(@class,'spinner')]", driver);
        Thread.sleep(2000);
    }

    public static boolean uploadUserPhoto(String keyword, String firstName, String lastName, String preferredName, String fileName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException, AWTException {
        //User Personal Information screen is shonw first

        boolean isUploaded = false;
        if (keyword.contains("ExtraAdminUser")) {
            keyword = getFirstname_FromExtraAdminDetails();
            firstName = keyword;
            lastName = getLastname_FromExtraAdminDetails();
        }

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, null, lastName, preferredName, testSerialNo, driver)) {
            PageObj_PersonalInfo.panel_UserPhoto(driver).click();
            Thread.sleep(20000);
            logMessage("User Photo Panel is clicked.");

            if (uploadFileInESS(fileName, driver)) {
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                Thread.sleep(5000);
                PageObj_PersonalInfo.button_SaveProfilePhoto(driver).click();
                Thread.sleep(4000);
                GeneralBasic.waitSpinnerDisappear(60, driver);

                isUploaded = true;
                logMessage("Screenshot after upload user photo.");
                logScreenshot(driver);
            }
        }

        return isUploaded;
    }

    public static boolean searchUserAndDisplayAccountSettingsPage_ViaAdmin(String keyword, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            PageObj_SideNavigationBar.sideMenu_accountSettings(driver).click();
            Thread.sleep(30000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            logMessage("User '" + firstName + " " + lastName + "' Account Settings page is shown.");
            logMessage("Screenshot after click user Account Settings in the sidebar menu.");
            logScreenshot(driver);
            isShown = true;
        }

        return isShown;
    }

    public static boolean validateAccountActivationStatu_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String expectedActivationStatus, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = true;
        int errorCounter = 0;

        int expectedStatus = Integer.parseInt(expectedActivationStatus);
        int currentActivationStatu = -1; //Not Activated.
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            String accountStatus = PageObj_AccountSettings.panel_AccountInformation(driver).getText();
            logMessage("Current Account Status is below");
            System.out.println(accountStatus);

            if (accountStatus.contains("Not activated")) {
                currentActivationStatu = 0;
                logMessage("Current Account is NOT activated.");
            } else if (accountStatus.contains("Account creation date") && (accountStatus.contains("Activated")) || (accountStatus.contains("Account activated"))) {
                currentActivationStatu = 1;
                logMessage("Current Account is Activated.");
            }

            if (currentActivationStatu == expectedStatus) {
                logMessage("Account activation status is shown as expected.");
            } else {
                logError("Account activation status is NOT shown as expected.");
                isPassed = false;

            }


        }
        return isPassed;

    }

    public static boolean activateUserAccount_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String password, String expectedMessage, String expectedActivate, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            //SystemLibrary.logMessage("Screenshot before activation.");
            //SystemLibrary.logScreenshot(driver);

            if (waitChild("//button[contains(text(),'Disable Login')]", 10, 1, driver)!=null){
                logWarning("User '" + firstName + " " + lastName + "' has been activated.");
                errorCounter++;
            }

            //judge Activate Button or Resend Activation Link
            if (SystemLibrary.waitChild("//button[text()=\"Activate\"]", 10, 1, driver) != null) {
                PageObj_AccountSettings.button_Acivate(driver).click();
                SystemLibrary.logMessage("Activate button is clicked in Account Setting screen.");
            } else if (SystemLibrary.waitChild("//button[text()=\"Resend Activation Link\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_ResendActivationLink(driver).click();
                SystemLibrary.logMessage("Resend Activation Link button is clicked in Account Setting screen.");
            } else {
                logWarning("User '" + firstName + " " + lastName + "' has been activated.");
                errorCounter++;
            }



            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);

            SystemLibrary.logMessage("Screenshot after click Activation button.");
            SystemLibrary.logScreenshot(driver);

            String strMessage = PageObj_AccountSettings.panel_AccountInformation(driver).getText();
            logMessage("The Account Information panel message is below");
            System.out.println(strMessage);

            if (expectedMessage != null) {
                if (strMessage.contains(expectedMessage)) {
                    logWarning("Message is shown as expected.");
                } else {
                    logError("Message is NOT shown as expected.");
                    logMessage("Expected Message is below: ");
                    logMessage(expectedMessage);
                    errorCounter++;
                }
            }

            int notContinueActivation = 0;
            if (strMessage.contains("The user could not be activated")) {
                notContinueActivation++;
            }

            if (expectedActivate != null) {
                if (expectedActivate.equals("2")) notContinueActivation++;
            }

            if (notContinueActivation == 0) {
                //////////////////// Jim Adjusted
                //String userName = driver.findElement(By.xpath("//div[@id='ea-username-password']//div[@class='display-field']/dd")).getText();
                String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                //Get Activation link from email...
                String emailAccountName = userName.replace("@" + emailDomainName, "");
                String activtionLink = JavaMailLib.getActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName, url_ESS);

                if (activtionLink != null) {
                    //Launch ESS User activation page
                    logMessage("Start Launch ESS Activation Page.");
                    WebDriver driver2 = SystemLibrary.launchWebDriver(activtionLink, SystemLibrary.driverType);

                    //SystemLibrary.waitChild("//button[@class='button button--wide' and text()='Send verification code']", 120, 1, driver2);

                    ///////////////////// New UI adjusted by Jim on 20/04/2021 ///////////////
                    if (SystemLibrary.waitChild("//button[@id='continue' and text()='Sign up']", 120, 1, driver2)==null) errorCounter++;
                    logMessage("Screenshot in Sign up page.");
                    logScreenshot(driver2);

                    if (SystemLibrary.waitChild("//p[contains(text(),'"+emailAccountName+"')]", 10, 1, driver2)!=null){
                        WebElement textBox_NewPassord=driver2.findElement(By.xpath("//input[@id='newPassword']"));
                        textBox_NewPassord.clear();
                        textBox_NewPassord.sendKeys(password);
                        textBox_NewPassord.sendKeys(Keys.TAB);
                        Thread.sleep(1000);
                        logMessage("New Password '"+password+"' is input.");

                        WebElement textBox_RePassword= driver2.findElement(By.xpath("//input[@id='reenterPassword']"));
                        textBox_RePassword.clear();
                        textBox_RePassword.sendKeys(password);
                        textBox_RePassword.sendKeys(Keys.TAB);
                        Thread.sleep(1000);
                        logMessage("Reenter Password '"+password+"' is input.");

                        logMessage("Screenshot after input password before click Sign up button.");
                        logScreenshot(driver2);

                        driver2.findElement(By.xpath("//button[@id='continue']")).click();
                        Thread.sleep(4000);
                        logMessage("Sign up button is clicked.");

                        WebElement label_Welcome = SystemLibrary.waitChild("//*[@id=\"page-title\"]/h3/div", 35, 1, driver2);
                        if (label_Welcome != null) {
                            String welcomeMessage = label_Welcome.getText();
                            logMessage("Welcome Message: " + welcomeMessage);
                            if (welcomeMessage.contains(firstName)) {
                                logMessage("Welcome message is shown as expeected.");
                            } else {
                                if (preferredName != null) {
                                    if (welcomeMessage.contains(preferredName)) {
                                        logMessage("Welcome message is shown as expected.");
                                    } else {
                                        logError("Welcome message is NOT shown correctly.");
                                        errorCounter++;
                                    }
                                } else {
                                    logError("Welcome message is NOT shown correctly.");
                                    errorCounter++;
                                }
                            }
                        } else {
                            errorCounter++;
                        }
                        ///////////////// Replaced by new code on 12/08/2021 /////////////
                        /*
                        if (preferredName!=null){
                            //////////////////////////
                            //if ((waitChild("//div[contains(text(),'Welcome "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+preferredName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+preferredName+"')]", 180, 1, driver2)!=null)){if ((waitChild("//div[contains(text(),'Welcome "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+preferredName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+preferredName+"')]", 180, 1, driver2)!=null)){if ((waitChild("//div[contains(text(),'Welcome "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+preferredName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+preferredName+"')]", 180, 1, driver2)!=null)){if ((waitChild("//div[contains(text(),'Welcome "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+preferredName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+preferredName+"')]", 180, 1, driver2)!=null)){
                            if (waitChild("//div[contains(text(),'"+preferredName+"')]", 180, 1, driver2)!=null){
                                logMessage("Activate user '"+firstName+" "+lastName+"' successfully.");
                                logScreenshot(driver2);
                                signoutESS(driver2);
                                logMessage("Signout ESS.");

                            }else{
                                logError("Failed Acitvating user '"+firstName+" "+lastName+"'.");
                                errorCounter++;
                            }
                            //////
                        }else{
                            //////////////////////////
                            //if ((waitChild("//div[contains(text(),'Welcome "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+firstName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+firstName+"')]", 180, 1, driver2)!=null)){
                            if (waitChild("//div[contains(text(),'"+firstName+"')]", 180, 1, driver2)!=null){
                                logMessage("Activate user '"+firstName+" "+lastName+"' successfully.");
                                logScreenshot(driver2);
                                signoutESS(driver2);
                                logMessage("Signout ESS.");

                            }else{
                                logError("Failed Acitvating user '"+firstName+" "+lastName+"'.");
                                errorCounter++;
                            }

                        }

                         */
                        //////


                        logScreenshot(driver2);
                        driver2.close();
                        logMessage("Close Activation page.");

                    }else{
                        errorCounter++;
                        logError("The expected Username '"+emailAccountName+"' is NOT shown. Activation is cancelled.");
                    }
                    //////
                    logMessage("End of Activating User '" + firstName + " " + lastName + "'.");
                } else {
                    errorCounter++;
                }
            } else {
                logMessage(("User is NOT Activated."));
            }

        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean activateUserAccount_ViaAdmin_OLD(String firstName, String middleName, String lastName, String preferredName, String password, String expectedMessage, String expectedActivate, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            //SystemLibrary.logMessage("Screenshot before activation.");
            //SystemLibrary.logScreenshot(driver);

            //judge Activate Button or Resend Activation Link
            if (SystemLibrary.waitChild("//button[text()=\"Activate\"]", 10, 1, driver) != null) {
                PageObj_AccountSettings.button_Acivate(driver).click();
                SystemLibrary.logMessage("Activate button is clicked in Account Setting screen.");
            } else if (SystemLibrary.waitChild("//button[text()=\"Resend Activation Link\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_ResendActivationLink(driver).click();
                SystemLibrary.logMessage("Resend Activation Link button is clicked in Account Setting screen.");
            } else {
                logWarning("User '" + firstName + " " + lastName + "' has been activated.");
                errorCounter++;
            }

            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);

            SystemLibrary.logMessage("Screenshot after click Activation button.");
            SystemLibrary.logScreenshot(driver);

            String strMessage = PageObj_AccountSettings.panel_AccountInformation(driver).getText();
            logMessage("The Account Information panel message is below");
            System.out.println(strMessage);

            if (expectedMessage != null) {
                if (strMessage.contains(expectedMessage)) {
                    logWarning("Message is shown as expected.");
                } else {
                    logError("Message is NOT shown as expected.");
                    logMessage("Expected Message is below: ");
                    logMessage(expectedMessage);
                    errorCounter++;
                }
            }

            int notContinueActivation = 0;
            if (strMessage.contains("The user could not be activated")) {
                notContinueActivation++;
            }

            if (expectedActivate != null) {
                if (expectedActivate.equals("2")) notContinueActivation++;
            }

            if (notContinueActivation == 0) {
                //////////////////// Jim Adjusted
                //String userName = driver.findElement(By.xpath("//div[@id='ea-username-password']//div[@class='display-field']/dd")).getText();
                String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                //Get Activation link from email...
                String emailAccountName = userName.replace("@" + emailDomainName, "");
                String activtionLink = JavaMailLib.getActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName, url_ESS);

                if (activtionLink != null) {
                    //Launch ESS User activation page
                    logMessage("Start Launch ESS Activation Page.");
                    WebDriver driver2 = SystemLibrary.launchWebDriver(activtionLink, SystemLibrary.driverType);
                    SystemLibrary.waitChild("//button[@class='button button--wide' and text()='Send verification code']", 120, 1, driver2);

                    logMessage("Screenshot before activation.");
                    logScreenshot(driver2);

                    PageObj_ESSActivation.button_SendVerificationCode(driver2).click();
                    logMessage("Send Verification Code button is clicked.");
                    SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);
                    PageObj_ESSActivation.button_OK_VerificationCodeEmailSent(driver2).click();
                    logMessage("OK button is clicked after Verification Code Email is sent.");
                    Thread.sleep(20000);

                    String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Verification Code", emailDomainName);
                    if (verificationCode != null) {
                        PageObj_ESSActivation.textbox_VerificationCode(driver2).click();
                        //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                        PageObj_ESSActivation.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                        logMessage("Verification code is input");

                        PageObj_ESSActivation.textbox_Password(driver2).click();
                        //PageObj_ESSActivation.textbox_Password(driver2).clear();
                        PageObj_ESSActivation.textbox_Password(driver2).sendKeys(password);
                        logMessage("Password is input.");

                        logMessage("Log screenshot before click Continue button in Activate User Account screen.");
                        logScreenshot(driver2);

                        PageObj_ESSActivation.button_Continue(driver2).click();
                        logMessage("Continue button is clicked.");

                        GeneralBasic.waitSpinnerDisappear(120, driver2);

                        logMessage("Screenshot after click continue button.");
                        logScreenshot(driver);
                    } else {
                        errorCounter++;
                    }

                    driver2.close();
                    logMessage("Activation page is closed.");
                    logMessage("End of Activating User '" + firstName + " " + lastName + "'.");
                } else {
                    errorCounter++;
                }
            } else {
                logMessage(("User is NOT Activated."));
            }

        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean reActivateUserAccount_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isActivated = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            SystemLibrary.logMessage("Screenshot before activation.");
            SystemLibrary.logScreenshot(driver);
            PageObj_AccountSettings.button_ResendActivationLink(driver).click();
            waitSpinnerDisappear(120, driver);
            SystemLibrary.logMessage("Activate button is clicked in Account Setting screen.");
            SystemLibrary.logMessage("Screenshot after click Resend Activation Link button.");
            SystemLibrary.logScreenshot(driver);

        }

        return isActivated;

    }

    public static boolean validateUserBanner(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            if (storeFileName != null) {
                logMessage("Screenshot of user banner");
                logScreenshotElement(driver, PageObj_UserBanner.userBannerFrame(driver));
                if (!SystemLibrary.validateTextValueInElement(PageObj_UserBanner.userBannerFrame(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean searchUserAndDisplayContactDetailsPage_ViaAdmin(String keyword, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            Thread.sleep(5000);
            PageObj_SideNavigationBar.sideMenu_contactDetails(driver).click();
			Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            Thread.sleep(20000);
            logMessage("User '" + firstName + " " + lastName + "' Contact Details page is shown.");
            logMessage("Screenshot after click Contact Details in the sidebar menu.");
            logScreenshot(driver);
            isShown = true;
        }

        return isShown;
    }

    public static boolean validateTeamMembersLeaveTab(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //not used anymore. Only Keep for old test items.
        boolean isPassed = true;
        int errorCounter = 0;

        displayTeamsPage(driver);

        String strContent = null;

        if (PageObj_Teams.displayTeamMembers(teamName, driver)) {
            PageObj_Teams.button_Leave_InTeamMemberListTable(driver).click();
            Thread.sleep(1000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Leave Tab is clicked in Team Member list Table.");

            logMessage("Screenshot after click Leave Tab in Team Member list Table.");
            logScreenshot(driver);

            if (expectedContent != null) {
                if (!SystemLibrary.validateTextValueContainedInElement(PageObj_Teams.table_TeamMembers(driver), storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!SystemLibrary.validateTextValueInElement(PageObj_Teams.table_TeamMembers(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }

        } else {
            errorCounter++;
            logError("Team: '" + teamName + "' is NOT found.");

        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean validateBankAccounts(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (GeneralBasic.searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            if (!SystemLibrary.validateTextValueInElement(PageObj_BankAccounts.frame_BankAccounts(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean searchUserAndDisplayBankAccountsPage(String keyword, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
        WebElement link_sideMenu_bankAccounts=SystemLibrary.waitChild("//span[@class='sni-label undefined'][contains(text(),'Bank Accounts')]", 2, 1, driver);
        if (link_sideMenu_bankAccounts!=null){
            link_sideMenu_bankAccounts.click();
            Thread.sleep(10000);
            waitSpinnerDisappear(120, driver);
            logMessage("Bank Accounts link enabled at user Side Menu.");

           /* PageObj_SideNavigationBar.sideMenu_bankAccounts(driver).click();
            Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(120, driver);*/

            if (SystemLibrary.waitChild("//h4[text()='Bank Accounts']", 30, 1, driver) != null) {
                logMessage("User '" + firstName + " " + lastName + "' Bank Accounts page is shown.");
                logMessage("Screenshot after click user Bank Accounts in the sidebar menu.");
                logScreenshot(driver);
                isShown = true;
            } else {
                errorCounter++;
            }

        }else{
            logError("Bank Accounts link is not enabled at user Side Menu.");
            errorCounter++;
        }

       }


        if (errorCounter > 0) isShown = false;
        return isShown;
    }

    public static boolean validateSuperannuation(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Superannuation", testSerialNo, driver)) {
            if (!SystemLibrary.validateTextValueInElement(PageObj_Superannuation.table_Superannuation(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean displayPage_ViaSideNavigationBar(String keyword, String firstName, String middleName, String lastName, String preferredName, String pageName, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(keyword, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //PageObj_SideNavigationBar.sideMenu_bankAccounts(driver).click();
            String xpath_SidebarSuper="//span[text()='" + pageName + "' and @class='sni-label undefined']";
            logDebug("xpath_SidebarSuper="+xpath_SidebarSuper);
            WebElement element_SideMenuItem = SystemLibrary.waitChild("//span[text()='" + pageName + "' and @class='sni-label undefined']", 15, 1, driver);
            if (element_SideMenuItem!=null){
                element_SideMenuItem.click();
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(120, driver);

                if (SystemLibrary.waitChild("//h4[text()='" + pageName + "']", 30, 1, driver) != null) {
                    logMessage("User '" + firstName + " " + lastName + "' " + pageName + " page is shown.");
                    logMessage("Screenshot after click user " + pageName + " in the sidebar menu.");
                    Thread.sleep(120000); //Waiting for loading informaiton message on page.
                    logScreenshot(driver);
                }else{
                    logError("User '" + firstName + " " + lastName + "' " + pageName + " page is NOT shown.");
                    errorCounter++;
                }
            }else{
                logWarning("The sidebar '"+pageName+"' is NOT found.");
                errorCounter++;
            }


        }

        if (errorCounter==0) isShown=true;
        return isShown;
    }

    public static boolean validateContactDetails(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Contact Details", testSerialNo, driver)) {
            if (!SystemLibrary.validateTextValueInElement(PageObj_ContactDetail.table_ContactDetailsAll(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateLeavePage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String messageExpected, String leaveTab, String leaveBalanceDate, String leaveTypeToBeExpand, String leaveFilter, String leaveUnit, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        GeneralBasic.displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        //Judge if the leave is showing...
        String userFullName = null;
        if (preferredName != null) {
            userFullName = firstName + " (" + preferredName + ") " + lastName;
        } else {
            userFullName = firstName + " " + lastName;
        }

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            if (leaveTab != null) {
                WebElement leaveTabButton = null;
                switch (leaveTab) {
                    case "Upcoming":
                        leaveTabButton = driver.findElement(By.xpath("//button[@id='button-leave-upcoming']"));
                        break;
                    case "Pending":
                        leaveTabButton = driver.findElement(By.xpath("//button[@id='button-leave-pending']"));
                        break;
                }

                leaveTabButton.click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Leave Tab '" + leaveTab + "' is clicked.");
                logScreenshot(driver);
            }

            if (leaveFilter != null) {
                if (!PageObj_Leave.applyFilterInLeavePage(leaveFilter, driver)) errorCounter++;
            }

            if (leaveBalanceDate != null) {
                driver.findElement(By.xpath("//button[@id='balances-calendar-button']")).click();
                Thread.sleep(3000);
                logMessage("Calendar Header is clicked.");
                logScreenshot(driver);

                selectDateInCalendar(leaveBalanceDate, driver);
            }

            if (leaveTypeToBeExpand != null) {
                WebElement leaveBalanceDetail = SystemLibrary.waitChild("//div[@id='leave-balances']//form/div[@class='le' and contains(., '" + leaveTypeToBeExpand + "')]", 2, 1, driver);
                if (leaveBalanceDetail != null) {
                    leaveBalanceDetail.click();
                    Thread.sleep(2000);
                    logMessage("Leave '" + leaveTypeToBeExpand + "' balance panel is clicked.");
                    logScreenshot(driver);
                } else {
                    logError("Leave '" + leaveTypeToBeExpand + "' balance panel is NOT found.");
                    errorCounter++;
                }
            }

            if (leaveUnit != null) {
                SystemLibrary.waitChild("//span[text()='" + leaveUnit + "']", 5, 1, driver);
            }

            if (messageExpected != null) {
                if (!SystemLibrary.validateTextValueContainedInElement(PageObj_Leave.table_LeaveAll(driver), storeFileName, isUpdateStore, isCompare, messageExpected, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!SystemLibrary.validateTextValueInElement(PageObj_Leave.table_LeaveAll(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
        } else {
            errorCounter++;
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validatePersonalInformation(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (GeneralBasic.searchUserAndDisplayPersonalInformationPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //Jim adjusted on 08/05/2020
            //if (!SystemLibrary.validateTextValueInElement(PageObj_PersonalInfo.table_PersonalInformationAll(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
            if (!SystemLibrary.validateTextValueInWebElementInUse(PageObj_PersonalInfo.table_PersonalInformationAll(driver), storeFileName, isUpdateStore, isCompare, expectedContent, null, testSerialNo, emailDomainName, driver)) {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean validateEmployment(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Employment", testSerialNo, driver)) {

            if (!SystemLibrary.validateTextValueInWebElementInUse(PageObj_Employment.table_EmploymentAll(driver), storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean sendFileName_PhotoAttachment(WebDriver driver) throws InterruptedException, IOException {
        boolean isSelected = false;
        int errorCounter = 0;
        driver.findElement(By.xpath("//span[@class='avatar avatar']")).sendKeys("C:\\TestAutomationProject\\ESSRegTest\\DataSource\\PhotoProfile1.png");
        Thread.sleep(3000);
        return isSelected;
    }


    public static boolean validateMyApprovals_ViaAdmin(String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;

        displayMyApprovalsPage_ViaNavigationBar(driver);

        if (storeFileName != null) {
            if (expectedTextContent == null) {
                isPassed = SystemLibrary.validateTextValueInElement(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName);
            } else if (expectedTextContent != null) {
                isPassed = SystemLibrary.validateTextValueContainedInElement(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, expectedTextContent, testSerialNo, emailDomainName);
            }
        }

        return isPassed;
    }

    public static boolean displayMyApprovalsPage_ViaNavigationBar(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        PageObj_NavigationBar.approvals(driver).click();
        Thread.sleep(10000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Approval item is clicked in the top navidation bar.");

        if (SystemLibrary.waitChild("//div[@id='page-title']/h3[text()='My Approvals']", 60, 1, driver) != null) {
            logMessage("My Approvals Page is shown.");
            SystemLibrary.logScreenshot(driver);
            isShown = true;
        } else {
            logError("My Approvals page is NOT shown.");
        }
        return isShown;
    }

    public static boolean displayOtherApprovalsPage_ViaNavigationBar(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        int errorCounter = 0;
        if (displayMyApprovalsPage_ViaNavigationBar(driver)) {
            PageObj_Approvals.label_MyApprovals(driver).click();
            logMessage("My Approvals lable is clicked.");
            Thread.sleep(10000);

            PageObj_Approvals.dropdownList_OtherApprovals(driver).click();
            logMessage("Dropdown List: Other Approvals is clicked.");
            Thread.sleep(10000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            if (SystemLibrary.waitChild("//div[@id='page-title']/h3[text()='Other Approvals']", 60, 1, driver) != null) {
                Thread.sleep(10000);
                logMessage("Other Approvals Page is shown.");
                SystemLibrary.logScreenshot(driver);

                int counter=0;
                while (waitChild("//div[contains(text(),\"Fetching approvals\")]", 10, 1, driver)!=null) {
                    logMessage("Fatching result...");
                    counter++;
                    if (counter>12){
                        logWarning("Waiting time "+String.valueOf(counter*10)+" seconds overdue.");
                        errorCounter++;
                        break;
                    }
                }

            } else {
                logError("Other Approvals page is NOT shown.");
                errorCounter++;
            }
        } else errorCounter++;

        if (errorCounter == 0) isShown = true;
        return isShown;
    }

    public static boolean validateOtherApprovals_ViaAdmin(String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String otherApprovalTab, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayOtherApprovalsPage_ViaNavigationBar(driver)) {
            if (otherApprovalTab != null) {

                if (otherApprovalTab.equals("Profile Changes")) {
                    WebElement tab_ProfileChagnes = SystemLibrary.waitChild("//button[contains(text(),'Profile Changes')]", 10, 1, driver);
                    if (tab_ProfileChagnes != null) {
                        tab_ProfileChagnes.click();
                        Thread.sleep(20000);
                        logMessage("Profile Changes tab is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("Profile Changes tab is NOT found.");
                        errorCounter++;
                    }
                }
                /*switch (otherApprovalTab){
                    case "All":
                        break;
                    case "Leave Applications":
                        WebElement tab_LeaveApplications=SystemLibrary.waitChild("//button[contains(text(),'Leave Applications')]", 10, 1, driver);
                        if (tab_LeaveApplications!=null){
                            tab_LeaveApplications.click();
                            Thread.sleep(4000);
                            logMessage("Leave Application tab is clicked.");
                            logScreenshot(driver);
                        }else{
                            logError("Leave Application tab is NOT found.");
                            errorCounter++;
                        }
                        break;
                    case "Profile Changes":
                        WebElement tab_ProfileChagnes=SystemLibrary.waitChild("//button[contains(text(),'Profile Changes')]", 10, 1, driver);
                        if (tab_ProfileChagnes!=null){
                            tab_ProfileChagnes.click();
                            Thread.sleep(4000);
                            logMessage("Profile Changes tab is clicked.");
                            logScreenshot(driver);
                        }else{
                            logError("Profile Changes tab is NOT found.");
                            errorCounter++;
                        }
                        break;
                }*/
            }

            if (!SystemLibrary.validateTextValueInWebElementInUse(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, expectedTextContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean approveAllOtherApproval_ViaAdmin(WebDriver driver) throws IOException, InterruptedException {
        boolean isApproved = false;
        displayOtherApprovalsPage_ViaNavigationBar(driver);

        PageObj_Approvals.button_Expand(driver).click();
        Thread.sleep(3000);
        logMessage("The Expand Button is clicked to display all buttons.");
        logScreenshot(driver);

        PageObj_Approvals.button_SelectAll(driver).click();
        Thread.sleep(3000);
        logMessage("Button 'Select All' is clicked.");
        logScreenshot(driver);

        PageObj_Approvals.button_ApproveAll(driver).click();
        logMessage("Button 'Apporve All' is clicked.");
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        logMessage("Screenshot after click Approve All button.");
        logScreenshot(driver);

        isApproved = true;
        return isApproved;

    }

    public static String generateEmployeeEmailAddress(String serverName, String payrollDBName, String firstName, String lastName, String testSerialNo, String emailDomainName) throws ClassNotFoundException, IOException, SQLException, InterruptedException {
        String employeeCode = null;
        employeeCode = DBManage.getEmployeeCodeFromPayrollDB(serverName, payrollDBName, firstName, lastName);
        //String strSQL="UPDATE _iptblEmployeeEmail SET cEmail = REPLACE(cEmail, '"+strOld+"', '"+strNew+"');";
        //String strSQL="update _ievEmployeeEmail set _ievEmployeeEmail.cEmail=_iptblEmployee.cFirstName+'_'+_iptblEmployee.cSurname+'_\"+getTestSerailNumber_Main(101, 101)+\"@mailinator' from _ievEmployeeEmail inner join _iptblEmployee on _ievEmployeeEmail.iEmployeeID=_iptblEmployee.idEmployee;";
        //String emailAddress = firstName + "_" + lastName + "_" + employeeCode + "_" + getTestSerailNumber_Main(101, 101) + "@mailinator.com";
        //String emailAddress = firstName + "_" + lastName + "_" + employeeCode + "_" + getTestSerailNumber_Main(101, 101) + "@"+emailDomainName;
        String emailAddress = firstName + "_" + lastName + "_" + employeeCode + "_" + testSerialNo + "@" + emailDomainName;
        logMessage("Employee " + firstName + " " + lastName + "'s email address is generated as '" + emailAddress + "'.");

        return emailAddress;
    }


    public static boolean forgetPassword(String firstName, String middleName, String lastName, String preferredName, String password, String emailAddress, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);

        ////////////////// Script adjusted by Jim on 09/07/2021 ///////////////

        if (emailAddress.equals("AUTO")) {
            emailAddress = generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
        }

        waitSpinnerDisappear(60, driver);
        waitChild("//h1[contains(text(),'Sign in')]", 60, 1, driver);

        PageObj_ESSHome.userNameTextBox(driver).clear();
        PageObj_ESSHome.userNameTextBox(driver).sendKeys(emailAddress);
        logMessage("User Name: " + emailAddress + " is input.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//button[@id='continue']")).click();
        Thread.sleep(2000);
        logMessage("Continue button is clicked.");
        logScreenshot(driver);

        WebElement link_ForgotPassword=waitChild("//a[@id='forgotPassword']", 60, 1, driver);

        if (link_ForgotPassword==null)  {
            errorCounter++;
            logError("Forgot Password is NOT shown.");
            logScreenshot(driver);
        }else {
            link_ForgotPassword.click();
            Thread.sleep(10000);
            logMessage("Forgot Password link is clikced.");
            logScreenshot(driver);

            if (SystemLibrary.waitChild("//input[@id='email' and @value='"+emailAddress+"']", 60, 1, driver)!=null){
                logMessage("Verify your email page is shown.");

                driver.findElement(By.xpath("//button[text()='Send code']")).click();
                Thread.sleep(15000);
                logMessage("Button Send Code is clicked.");
                logScreenshot(driver);

                /////////////////////////// Get code from Email //////////////////
                logMessage("Delay 30 seconds for email sent...");
                Thread.sleep(30000);

                String emailAccountName = emailAddress.replace("@" + emailDomainName, "");
                String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Password Reset", emailDomainName);

                if (SystemLibrary.waitChild("//input[@id='email' and @value='"+emailAddress+"']", 60, 1, driver)!=null){
                    logMessage("Input Verify code screen is shown.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//input[@id='verificationCodeToBeVerified']")).sendKeys(verificationCode+Keys.TAB);
                    logMessage("Verify code '"+verificationCode+"' is input.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[@id='emailVerificationForPasswordResetControl_but_verify_code']")).click();
                    Thread.sleep(15000);
                    logMessage("Verify Code button is clicked.");
                    logScreenshot(driver);

                    if (password!=null){
                        if (password.equals("AUTO")){
                            password="Sage#"+testSerialNo;
                        }
                    }

                    if (SystemLibrary.waitChild("//h1[contains(text(),'Reset your password')]", 60, 1, driver)!=null){
                        driver.findElement(By.xpath("//input[@id='newPassword']")).sendKeys(password+Keys.TAB);
                        Thread.sleep(3000);
                        logMessage("New password '"+password+"' is input.");
                        driver.findElement(By.xpath("//input[@id='reenterPassword']")).sendKeys(password+Keys.TAB);
                        Thread.sleep(3000);
                        logMessage("Comfirm new password is input.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//button[@id='continue' and text()='Sign in']")).click();
                        Thread.sleep(10000);
                        logMessage("Sign in button is clicked.");

                        ///////////////////// Copy from sign in function ///////////////////////
                        WebElement label_Welcome = SystemLibrary.waitChild("//*[@id=\"page-title\"]/h3/div", 60, 1, driver);
                        if (label_Welcome != null) {
                            String welcomeMessage = label_Welcome.getText();
                            logMessage("Welcome Message: " + welcomeMessage);
                            if (welcomeMessage.contains(firstName)) {
                                logMessage("Welcome message is shown as expeected.");
                            } else {
                                if (preferredName != null) {
                                    if (welcomeMessage.contains(preferredName)) {
                                        logMessage("Welcome message is shown as expected.");
                                        logScreenshot(driver);
                                        signoutESS(driver);
                                    } else {
                                        logError("Welcome message is NOT shown correctly.");
                                        errorCounter++;
                                        logScreenshot(driver);
                                    }
                                } else {
                                    logError("Welcome message is NOT shown correctly.");
                                    errorCounter++;
                                }
                            }
                        } else {
                            errorCounter++;
                        }
                        //////

                    }else{
                        errorCounter++;
                        logError("Reset Password screen is NOT shown.");
                    }

                }else{
                    errorCounter++;
                    logError("Input Verify code screen is NOT shonw.");
                    logScreenshot(driver);
                }
                ////// End of getting code from email //////


            }else{
                logError("Verify your email page is NOT shown.");
                errorCounter++;
            }

        }

        logScreenshot(driver);



        ////// End of script adjustment by Jim on 09/07/2021 //////


        driver.close();

        if (errorCounter==0){
            isDone=true;
            logMessage("Reset password successfully.");
        }else{
            logError("Failed Reset password.");
        }
        return isDone;

    }



    public static boolean validateUserBusinessCard(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is only working when log on as normal user and search other user
        boolean isPassed = false;
        int errorCounter = 0;

        String fullName = "";
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        if (searchUser(firstName, driver) > 0) {
            WebElement userNameLink = waitChild(fullName, 10, 5, driver);
            if (userNameLink != null) {
                userNameLink.click();
                GeneralBasic.waitSpinnerDisappear(30, driver);

                if (waitChild("//div[@class='light-overlay']", 10, 1, driver) != null) {
                    logMessage("User: " + firstName + " " + lastName + "'s Business Card is shown, screenshot below:");
                    WebElement element = driver.findElement(By.xpath("//div[@class='light-overlay']"));

                    logScreenshotElement(driver, element);

                    if (!validateTextValueInElement(element, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                        errorCounter++;
                }
            }
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean displayPersonalInformationPage_ViaNavigationBarNameIcon(WebDriver driver, String firstName) throws InterruptedException {
        boolean isShown = false;
        int errorCounter = 0;
        PageObj_NavigationBar.icon_UserName(driver).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(60, driver);
        logMessage("User name icon is clicked in top Navigation bar.");

        if (firstName != null) {
            if (driver.findElement(By.xpath("//h3[@class='bc-name']")).getText().contains(firstName)) {
                logMessage("User " + firstName + "'s Personal Information page is shown.");
            } else {
                errorCounter++;
                logError("User " + firstName + "'s Personal Information page is NOT shown.");
            }
        }

        isShown = true;

        return isShown;
    }

    public static boolean disableUserAccount(String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        //This function is used by Admin only
        boolean isDone = false;
        int errorCounter = 0;

        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            PageObj_AccountSettings.button_DisableLogin(driver).click();

            if (waitChild("//button[contains(@class,'button button--primary')][text()='Enable Login']", 1, 1, driver) != null) {
                logMessage("User " + firstName + "'s account is disabled.");
                isDone = true;
            } else {
                logError("Failed disable user " + firstName + "'s account.");
                errorCounter++;
            }
            logMessage("Screenshot after click 'Disable Login' button.");
            logScreenshot(driver);

        }

        return isDone;
    }

    public static boolean validate_SettingsLeaveScreenshot(String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displaySettings_Leave(driver)) {
            scrollToBottom(driver);
            if (validateScreenshotInElement(PageObj_SettingsLeave.form_AllLeaveSettings(driver), storeFileName, isUpdateStore, isCompare, driver)) {
                isPassed = true;
            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean editSettingsLeave(String leaveType, String colorValue, String displayBalance, String allowInsufficientBalance, String includeProRataCalculation, String onlyOnceEntitlementsReached, String leaveReason, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayEditLeaveFormInSettingPage(leaveType, driver)) {
            if (colorValue != null) {
                PageObj_SettingsLeave.button_Color(driver).click();
                Thread.sleep(2000);
                logMessage("Color Icon is clicked.");

                PageObj_SettingsLeave.input_Color(driver).clear();
                PageObj_SettingsLeave.input_Color(driver).sendKeys(colorValue);

                PageObj_SettingsLeave.input_Color(driver).sendKeys(Keys.ENTER);
                logMessage("Color Icon is clicked again to close the color panel.");
                isDone=true;
            }

            if (displayBalance != null) {
                logMessage("Adjust Checkbox: Dispaly balance");
                tickCheckbox(displayBalance, PageObj_SettingsLeave.checkbox_DisplayBalance(driver), driver);
                isDone=true;
            }

            if (allowInsufficientBalance != null) {
                logMessage("Adjust Checkbox: Allow insufficient balacne");
                tickCheckbox(allowInsufficientBalance, PageObj_SettingsLeave.checkbox_AllowInsufficientBalance(driver), driver);
                isDone=true;
            }

            if (includeProRataCalculation != null) {
                logMessage("Adjust Checkbox: Include pro rata calculation.");
                tickCheckbox(includeProRataCalculation, PageObj_SettingsLeave.checkbox_IncludeProRataCalculation(driver), driver);
                isDone = true;
            }

            if (onlyOnceEntitlementsReached!=null){
                logMessage("Ajust Checkbox: Only once entitlement is reached.");
                tickCheckbox(onlyOnceEntitlementsReached, PageObj_SettingsLeave.checkbox_OnlyOnceEntitlementReached(driver), driver);
                isDone=true;
            }

            if (leaveReason != null) {
                String currentList = null;
                String[] leaveReasonList = splitString(leaveReason, ";");
                int listCount = leaveReasonList.length;

                String leaveReasonsName = null;
                String leaveReasonsCheckAction = null;

                for (int i = 0; i < listCount; i++) {
                    currentList = leaveReasonList[i];
                    String[] leaveReasonItemList = splitString(currentList, ":");
                    leaveReasonsName = leaveReasonItemList[0];
                    leaveReasonsCheckAction = leaveReasonItemList[1];

                    logMessage("Adjust checkbox: " + leaveReasonsName);
                    tickCheckbox(leaveReasonsCheckAction, PageObj_SettingsLeave.getCheckbox_LeaveReasons(leaveReasonsName, driver), driver);
                    isDone = true;
                }
            }

            logMessage("Screenshot before click Save button");
            logScreenshot(driver);
            PageObj_SettingsLeave.button_SaveEditLeave(driver).click();
            logMessage("Save Edit Leave button is clicked.");

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean displayEditLeaveFormInSettingPage(String leaveType, WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        int errorCounter = 0;

        GeneralBasic.displaySettings_Leave(driver);

        List<WebElement> elements = driver.findElements(By.xpath("//html//div[@class='list-section-container']/div"));
        WebElement currentElement = null;
        String currentText = null;
        String buttonXpath = null;
        int totalCount = elements.size();

        for (int i = 1; i <= totalCount; i++) {
            currentElement = driver.findElement(By.xpath("//html//div[@class='list-section-container']/div[" + i + "]"));
            currentText = currentElement.getText();
            if (currentText.contains(leaveType)) {
                buttonXpath = "//html//div[@class='list-section-container']/div" + "[" + i + "]/div[3]/div/div/button";
                driver.findElement(By.xpath(buttonXpath)).click();
                logMessage("Leave " + leaveType + "'s Edit button is clicked.");

                logMessage("Edit " + leaveType + " form is shown.");
                isShown = true;
                logMessage("Screenshot of Edit Leave Form.");
                logScreenshotElement(driver, driver.findElement(By.xpath("//div[@id='modal-content']//div//form")));
                break;
            }
        }

        return isShown;
    }

    public static String getLeaveSettingStatusInLeaveSettingPage(String leaveItemToBeValidated, WebDriver driver) throws InterruptedException, IOException {
        //Format of leaveItemToBeValidated "Annual Leave:Display balance;Annual Leave:Annual Leave;Other Leave:Unpaid Leave"
        displaySettings_Leave(driver);
        String leaveSettingStatus = "";
        String[] itemList = splitString(leaveItemToBeValidated, ";");
        int itemCount = itemList.length;

        String currentItem = null;
        String currentSubItem = null;
        String currentLeaveType = null;

        for (int i = 0; i < itemCount; i++) {
            currentItem = itemList[i];

            String[] itemList2 = splitString(currentItem, ":");
            currentLeaveType = itemList2[0];
            currentSubItem = itemList2[1];
            //WebElement tickIcon = driver.findElement(By.xpath("//html//div[@class='list-section-container']/div[contains(., '" + currentLeaveType + "')]//div//div[@class='setting-item'][contains(., '" + currentSubItem + "')]/i"));
            String xpath_TickIcon="//html//div[@class='list-section-container']/div[contains(., '" + currentLeaveType + "')]//div//div[@class='liciwt-wrapper setting-item'][contains(., '" + currentSubItem + "')]/i";
            WebElement tickIcon= waitChild(xpath_TickIcon, 60, 1, driver);
            //WebElement tickIcon=SystemLibrary.waitChild("//html//div[@class='list-section-container']/div[contains(., '" + currentLeaveType + "')]//div//div[@class='setting-item'][contains(., '" + currentSubItem + "')]/i", 60, 1, driver);
            if (tickIcon!=null){
                leaveSettingStatus = leaveSettingStatus + currentLeaveType + ":" + currentSubItem + " '" + tickIcon.getAttribute("class") + "'\n";
            }else{
                leaveSettingStatus = leaveSettingStatus + currentLeaveType + ":" + currentSubItem + " '" + "NULL" + "'\n";

            }

        }

        logMessage("The current Leave Setting status is as below:");
        System.out.println(leaveSettingStatus);
        return leaveSettingStatus;
    }

    public static boolean validateLeaveSettingInLeaveSettingPage(String leaveItemToBeValidated, String storeFileName, String isUpdateStore, String isCompare, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        //Format of leaveItemToBeValidated "Annual Leave:Display balance;Annual Leave:Annual Leave;Other Leave:Unpaid Leave"
        boolean isPassed = false;
        int errorCounter = 0;

        String statusResult = getLeaveSettingStatusInLeaveSettingPage(leaveItemToBeValidated, driver);
        isPassed = validateStringFile(statusResult, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo);

        return isPassed;
    }

    public static boolean validateEditLeaveFormInLeaveSettingPage(String leaveType, String storeFileName, String isUpdate, String isCompare, WebDriver driver) throws Exception {
        boolean isPass = false;
        if (displayEditLeaveFormInSettingPage(leaveType, driver)) {
            isPass = validateScreenshotInElement(PageObj_SettingsLeave.form_EditLeave(driver), storeFileName, isUpdate, isCompare, driver);
            PageObj_SettingsLeave.button_CloseEditLeave(driver).click();
            logMessage("Edit Leave setting form is closed.");

        }

        return isPass;
    }

    public static boolean downloadLeaveBalancesReport(String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws Exception {
        boolean isDownloaded = false;
        int errorCounter = 0;

        ////////////////////
        String fileName = "leave-balances-directory-" + getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + fileName;
        String strNewDownloadFileFullPathName = workingFilePath + "LeaveBalanceDirectory_" + getCurrentTimeAndDate() + ".txt";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), fileName);

        displayDashboard(driver);
        PageObj_Dashboard.panel_Directory(driver).click();
        GeneralBasic.waitSpinnerDisappear(60, driver);
        Thread.sleep(3000);

        PageObj_Teams.ellipsis_TeamsTeam_20(driver).click();
        logMessage("Ellipsis button is clicked in Team Directory page.");
        Thread.sleep(1000);

        driver.findElement(By.linkText("Leave balances report")).click();
        logMessage("Leave balances report menu is clicked.");

        File file = new File(getGoogleChromeDownloadPath(), fileName);
        int i = 0;
        while (!file.exists()) {
            i++;
            Thread.sleep(1000);
            if (i > 300) break;
        }

        if (i < 300) {
            logMessage("It takes " + i + " Seconds to download Leave balance report.");
        } else {
            logError("Download Leave balance report overdue.");
            errorCounter++;
        }

        //Move download file into working folder
        if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
        logMessage("Leave Balances Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

        ///////////////////////////////////
        if (isUpdateStore != null) {
            if (isUpdateStore.equals("1")) {
                if (!saveFileToStore(strNewDownloadFileFullPathName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare != null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(strNewDownloadFileFullPathName, storeFilePath + storeFileName)) errorCounter++;
            }
        }

        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean validate_AccountSettings_Page(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Account Settings", testSerialNo, driver)) {
            WebElement masterFrame = driver.findElement(By.xpath("//div[@class='master-detail-content']"));

            if (!validateTextValueInWebElementInUse(masterFrame, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validate_TeamsAndRoles(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedMessage, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (firstName.contains("ExtraAdminUser")) {
            firstName = getFirstname_FromExtraAdminDetails();
            lastName = getLastname_FromExtraAdminDetails();
        }

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            WebElement masterFrame = driver.findElement(By.xpath("//div[@class='master-detail-content']"));
            if (expectedMessage != null) {
                if (!validateTextValueContainedInElement(masterFrame, storeFileName, isUpdateStore, isCompare, expectedMessage, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!validateTextValueInElement(masterFrame, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validate_ApprovalProcess_ViaTeamsAndRolesPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedMessage, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            if (PageObj_TeamsNRoles.displayApprovalProcess(driver)) {

                if (expectedMessage == null) {
                    if (!validateTextValueInElement(PageObj_TeamsNRoles.form_ApprovalProcess(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
                        errorCounter++;
                    }
                } else {
                    if (!validateTextValueContainedInElement(PageObj_TeamsNRoles.form_ApprovalProcess(driver), storeFileName, isUpdateStore, isCompare, expectedMessage, testSerialNo, emailDomainName)) {
                        errorCounter++;
                    }
                }

                PageObj_TeamsNRoles.button_CloseApprovalProcess(driver).click();
                Thread.sleep(3000);
                logMessage("Close Approval Process button is clicked.");
            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateTeamMember_ViaTeamsAndRolesPage(String firstName, String middleName, String lastName, String preferredName, String teamName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            driver.findElement(By.xpath("//a[@class='link heading-container' and contains(., '" + teamName + "')]")).click();
            Thread.sleep(5000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            WebElement teammemberTable = driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInElement(teammemberTable, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean approveItem(String firstName, String middleName, String lastName, String preferredName, String approvalsArea, String allItemActionMethod, String subItemName, String subItemActionMethod, String commentOfDecline, String storeFileName, String isUpdateStore, String isComare, String expecntedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        logMessage("Display Dashboard page first.");
        displayDashboard(driver);
        //driver.navigate().refresh();
        Thread.sleep(15000);
        logScreenshot(driver);
        if (approvalsArea != null) {
            if (approvalsArea.equals("1")) {
                displayMyApprovalsPage_ViaNavigationBar(driver);
            } else if (approvalsArea.equals("2")) {
                displayOtherApprovalsPage_ViaNavigationBar(driver);
            }

        } else {
            PageObj_NavigationBar.approvals(driver).click();
            Thread.sleep(15000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
        }

        logScreenshot(driver);
        if (allItemActionMethod != null) {
            PageObj_Approvals.button_Expand(driver).click();
            Thread.sleep(15000);
            logMessage("Multi Select Icon Button is clicked.");

            WebElement button_SelectAll=PageObj_Approvals.button_SelectAll(driver);
            if (button_SelectAll!=null){
                button_SelectAll.click();
                Thread.sleep(15000);
                logMessage("Select All button is clicked.");

                if (allItemActionMethod.equals("1")) {  //Approve All
                    logMessage("Screenshot before click Approve All button");
                    logScreenshot(driver);

                    PageObj_Approvals.button_ApproveAll(driver).click();
                    Thread.sleep(15000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Approve All button is clicked");
                } else if (allItemActionMethod.equals("2")) {  //Decline All
                    logMessage("Screenshot before click Decline All button.");
                    logScreenshot(driver);
                    PageObj_Approvals.button_DeclineAllSelected(driver).click();
                    Thread.sleep(15000);
                    logMessage("Decline all button is clicked.");

                    if (commentOfDecline != null) {
                        WebElement textareOfDeclineComment = driver.findElement(By.xpath("//textarea[@id='comments']"));
                        textareOfDeclineComment.click();
                        textareOfDeclineComment.clear();
                        textareOfDeclineComment.sendKeys(commentOfDecline);
                        logMessage("Decline comment is input.");
                    }

                    logMessage("Screenshot before click Declien submit button.");
                    logScreenshot(driver);
                    driver.findElement(By.xpath("//button[@type='submit' and text()='Decline']")).click();
                    Thread.sleep(10000);
                    logMessage("Decline is submitted.");

                }

                logMessage("Screenshot after process approval.");
                logScreenshot(driver);
                ////////////////
            }else{
                logError("Button Select All is not shown.");
                errorCounter++;
                logScreenshot(driver);
            }


        } else {
            ///////////////////////
            String userFullName = null;
            if (preferredName != null) {
                userFullName = preferredName + " " + lastName;
            } else {
                userFullName = firstName + " " + lastName;
            }

            List<WebElement> itemLists = driver.findElements(By.xpath("//div[@class='list-container']/Form/div"));
            int totalItemCount = itemLists.size() - 1;
            logMessage("There are total " + totalItemCount + " Approvals item(s).");

            ////div[@class='list-container']/form/div[@class='list-section' and contains(., 'Sharon ANDREWS') and contains(., 'Medical Conditions')]//button[contains(@class, 'pending-button button ')]
            String xpath_ApprovalItem = "";
            if (!subItemName.contains(";")) {
                xpath_ApprovalItem = "//div[@class='approval-details-wrapper' and contains(., '" + userFullName + "') and contains(., '" + subItemName + "')";
            } else {
                String[] subItemList = splitString(subItemName, ";");
                xpath_ApprovalItem = "//div[@class='approval-details-wrapper' and contains(., '" + userFullName + "')";
                int itemCount = subItemList.length;
                for (int i = 0; i < itemCount; i++) {
                    xpath_ApprovalItem = xpath_ApprovalItem + " and contains(., '" + subItemList[i] + "')";
                }
            }

            xpath_ApprovalItem = xpath_ApprovalItem + "]";
            logDebug("Approval Item xpath:");
            logDebug(xpath_ApprovalItem);

            String xpath_ButtonPendingApproval = xpath_ApprovalItem + "//button[contains(., 'Pending Approval')]";
            String xpath_ButtonApprove = xpath_ApprovalItem + "//button[contains(., 'Approve')]";
            String xpath_ButtonDecline = xpath_ApprovalItem + "//button[contains(., 'Decline')]";

            WebElement itemTobeApproved = waitChild(xpath_ApprovalItem, 15, 1, driver);
            if (itemTobeApproved != null) {
                logMessage("Screenshot of Item to be approved.");
                logScreenshot(driver);

                if (isComare != null) {
                    if (isComare.equals("1")) {
                        if (!validateTextValueInWebElementInUse(itemTobeApproved, storeFileName, isUpdateStore, isComare, expecntedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    }
                }

                logDebug("XPath_Button_PendingApproval='" + xpath_ButtonPendingApproval + "'");
                WebElement button_PendingApproval = driver.findElement(By.xpath(xpath_ButtonPendingApproval));

                if (!isVisibleInView(button_PendingApproval, driver)) {
                    displayElementInView(itemTobeApproved, driver, 5);
                }

                button_PendingApproval.click();
                logMessage("Screenshot after Button Pending Approval is clicked.");
                Thread.sleep(3000);
                logScreenshot(driver);

                //subItemActionMethod, 1- Approve, 2- Decline
                if (subItemActionMethod != null) {
                    if (subItemActionMethod.equals("1")) {
                        //1- click approve button
                        driver.findElement(By.xpath(xpath_ButtonApprove)).click();
                        Thread.sleep(10000);
                        logMessage("Approve Button is clicked.");
                    } else if (subItemActionMethod.equals("2")) {
                        //2- click decline button
                        driver.findElement(By.xpath(xpath_ButtonDecline)).click();
                        Thread.sleep(10000);
                        logMessage("Decline button is clicked.");

                        if (commentOfDecline != null) {
                            driver.findElement(By.xpath("//textarea[@id='comments']")).sendKeys(commentOfDecline);
                            driver.findElement(By.xpath("//textarea[@id='comments']")).sendKeys(Keys.TAB);
                            logMessage("Decline Comment is input.");

                            logMessage("Screenshot before click Decline button in Add Comment screen.");
                            logScreenshot(driver);
                        }

                        driver.findElement(By.xpath("//button[@type='submit' and text()='Decline']")).click();
                        Thread.sleep(10000);
                        waitSpinnerDisappear(60, driver);
                        logMessage("Decline button is clicked in 'Add Comment' screen.");

                    } else if (subItemActionMethod.equals("3")) {
                        WebElement button_Approve = waitChild("//button[contains(@class,'approve-button button--success')]", 10, 1, driver);
                        WebElement button_Decline = waitChild("//button[contains(@class,'decline-button button--danger')]", 10, 1, driver);

                        if (button_Approve != null) {
                            if (button_Approve.isDisplayed()) {
                                logMessage("Approve button is shown.");
                            } else {
                                logError("Approve button is NOT shown.");
                                errorCounter++;
                            }
                        } else {
                            logError("Approve button is NOT shown.");
                        }

                        if (button_Decline != null) {
                            if (button_Decline.isDisplayed()) {
                                logMessage("Decline button is shown.");
                            } else {
                                logError("Decline button is NOT shown.");
                                errorCounter++;
                            }
                        } else {
                            logError("Decliine button is NOT shown.");
                        }
                    }

                }
            } else {
                errorCounter++;
                logError("User '" + userFullName + "' Approval Item: '" + subItemName + ", is NOT found.");
            }
            logScreenshot(driver);
            ///////
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    /////////////////////////////// Workflows  //////////////////

    public static boolean validateWorkflowsPage(String typeOfWorkflows, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displaySettings_Workflows(driver)) {
            if (typeOfWorkflows != null) {

                if (typeOfWorkflows.equals("1")) {
                    PageObj_Workflows.label_ProfileChangesWorkflowDefault(driver).click();
                } else if (typeOfWorkflows.equals("2")) {
                    PageObj_Workflows.label_LeaveWorkflowDefault(driver).click();
                }
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage(typeOfWorkflows + " Changes Workflow is clicked.");
                logScreenshot(driver);

                if (!validateTextValueInElement(PageObj_Workflows.form_SingleWorkflow(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
                    errorCounter++;
                }
            } else {
                if (!validateTextValueInElement(PageObj_Workflows.form_WorkflowsList(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
                    errorCounter++;
                }

            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;


    }

    public static boolean editWorkFlow(String typeOfWorkflows, String actionMethod, String configurations, String itemToBeDeleted, String storeFileName, String isUpdate, String isCompare, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;
        displaySettings_Workflows(driver);
        Thread.sleep(10000);

        if (typeOfWorkflows != null) {
            if (typeOfWorkflows.equals("1")) {
                PageObj_Workflows.label_ProfileChangesWorkflowDefault(driver).click();
                logMessage("Profile Workflows is clicked.");
            } else if (typeOfWorkflows.equals("2")) {
                PageObj_Workflows.label_LeaveWorkflowDefault(driver).click();
                logMessage("Leave Workflows is clicked.");
            }
        }

        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Screenshot after selecting Type Of Workflow.");
        logScreenshot(driver);

        //////////// Temp disable refreshing function //////////
        //driver.navigate().refresh();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        PageObj_Workflows.button_EditWorkflow(driver).click();
        logMessage("Edit button is clicked.");
        logMessage("Screenshot of Edit Workflow.");
        logScreenshot(driver);

        if (actionMethod != null) {
            switch (actionMethod) {
                case "1": //Change the current workflow itself without making specific changes to exception.
                    if (!editWorkflow_Workflow(configurations, typeOfWorkflows, driver)) errorCounter++;
                    break;
                case "2": //Add an Exception.
                    if (!editWorkflow_AddAnException(configurations, driver)) errorCounter++;
                    break;
                case "3": //Edit an Exception.
                    if (!editWorkflow_EditException(configurations, driver)) errorCounter++;
                    break;
                case "4": //Delete an Exception.
                    if (!editWorkflow_DeleteException(itemToBeDeleted, driver)) errorCounter++;
                    break;
                case "5": //Copy response of one exception to another.
                    //changeWorkFlow_changeException_copyResponse(driver, exceptionConfigurationSplit, currentWorkFlow);
                    break;
                case "6": //Validate Status of Approvals Available to Admin
                    if (!editWorkflow_ValidateStatusOfApprovalAvailableToAdmin(storeFileName, isUpdate, isCompare, emailDomainName, testSerialNo, driver))
                        errorCounter++;
                    break;
                case "7": //Edit Approvals Available to Admin
                    if (!editWorkflow_EditApprovalAvailableToAdmin(configurations, driver)) errorCounter++;
                    break;

            }
        }

        Thread.sleep(10000);
        waitSpinnerDisappear(120, driver);
        logMessage("Screenshot before clicking Save button.");
        logScreenshot(driver);

        PageObj_Workflows.button_SaveWorkflow(driver).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Save Workflow button is clicked.");

        logMessage("Screenshot after click Save button.");
        logScreenshot(driver);

        GeneralBasic.waitSpinnerDisappear(120, driver);

        logMessage("Screenshot after selecting Type Of Workflow.");
        logScreenshot(driver);


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editWorkFlow_ValidateSaveWarning(String typeOfWorkflows, String actionMethod, String configurations, String itemToBeDeleted, String storeFileName, String isUpdate, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;
        displaySettings_Workflows(driver);

        if (typeOfWorkflows != null) {
            if (typeOfWorkflows.equals("1")) {
                PageObj_Workflows.label_ProfileChangesWorkflowDefault(driver).click();
                logMessage("Profile Workflows is clicked.");
            } else if (typeOfWorkflows.equals("2")) {
                PageObj_Workflows.label_LeaveWorkflowDefault(driver).click();
                logMessage("Leave Workflows is clicked.");
            }
        }

        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Screenshot after selecting Type Of Workflow.");
        logScreenshot(driver);

        driver.navigate().refresh();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        PageObj_Workflows.button_EditWorkflow(driver).click();
        logMessage("Edit button is clicked.");
        logMessage("Screenshot of Edit Workflow.");
        logScreenshot(driver);

        if (actionMethod != null) {
            switch (actionMethod) {
                case "1": //Change the current workflow itself without making specific changes to exception.
                    if (!editWorkflow_Workflow(configurations, typeOfWorkflows, driver)) errorCounter++;
                    break;
                case "2": //Add an Exception.
                    if (!editWorkflow_AddAnException(configurations, driver)) errorCounter++;
                    break;
                case "3": //Edit an Exception.
                    if (!editWorkflow_EditException(configurations, driver)) errorCounter++;
                    break;
                case "4": //Delete an Exception.
                    if (!editWorkflow_DeleteException(itemToBeDeleted, driver)) errorCounter++;
                    break;
                case "5": //Copy response of one exception to another.
                    //changeWorkFlow_changeException_copyResponse(driver, exceptionConfigurationSplit, currentWorkFlow);
                    break;
                case "6": //Validate Status of Approvals Available to Admin
                    if (!editWorkflow_ValidateStatusOfApprovalAvailableToAdmin(storeFileName, isUpdate, isCompare, emailDomainName, testSerialNo, driver))
                        errorCounter++;
                    break;
                case "7": //Edit Approvals Available to Admin
                    if (!editWorkflow_EditApprovalAvailableToAdmin(configurations, driver)) errorCounter++;
                    break;

            }
        }

        logMessage("Screenshot before clicking Save button.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//a[contains(text(),'Teams')]")).click();
        Thread.sleep(3000);
        logMessage("Team menu is clicked.");
        logScreenshot(driver);

        WebElement dialogue_FormSaveWarning = waitChild("//div[@class='focus-trap-container']", 10, 1, driver);
        if (dialogue_FormSaveWarning != null) {
            if (!validateTextValueInElement(dialogue_FormSaveWarning, storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
            driver.findElement(By.xpath("//button[contains(@class,'button--danger')]")).click();
            logMessage("Dischard changes button is clicked.");
            Thread.sleep(4000);
        } else {
            logError("Save Warning is NOT shown.");
            errorCounter++;
        }

        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editWorkflow_ValidateStatusOfApprovalAvailableToAdmin(String storeFileName, String isUpdate, String isCompare, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_MakeApprovalsAvailableToAdmin = waitChild("//div[@class='table-row table-sub-header' and contains(., 'Make approvals available to')]//div//button[@class='button--plain add-button']", 10, 1, driver);
        if (button_MakeApprovalsAvailableToAdmin != null) {
            button_MakeApprovalsAvailableToAdmin.click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(180, driver);
            logMessage("Button - Make Approvals Available to Admin is clicked.");
            WebElement dialogue_MakeApprovalAvailableToAdmin = waitChild("//div[@class='focus-trap-container']", 10, 1, driver);
            logScreenshot(driver);

            String textMessage = "";
            if (dialogue_MakeApprovalAvailableToAdmin != null) {
                textMessage = driver.findElement(By.xpath("//h4[@class='mc-header-heading']")).getText() + "\n";
                List<WebElement> checkboxLists = driver.findElements(By.xpath("//div[@class='fc-checkbox']"));
                WebElement checkBox = null;

                int totalCount = checkboxLists.size();
                for (int i = 0; i < totalCount; i++) {
                    textMessage = textMessage + checkboxLists.get(i).getText() + " ";
                    checkBox = driver.findElement(By.xpath(getElementXPath(driver, checkboxLists.get(i)) + "//input[@type='checkbox']"));
                    textMessage = textMessage + String.valueOf(checkBox.isSelected()) + "\n";

                }

                textMessage = textMessage + driver.findElement(By.xpath("//button[@type='submit']")).getText();
                if (!validateStringFile(textMessage, storeFileName, isUpdate, isCompare, emailDomainName, testSerialNo)) {
                    errorCounter++;
                }

                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                Thread.sleep(3000);
                logMessage("Close button is clicked.");
                logScreenshot(driver);


            } else {
                logError("The Dialogue - Make Approvals Available to is NOT shown.");
                errorCounter++;
            }


        } else {
            logError("Button - Make Approvals Available to Admin is NOT shown.");
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean editWorkflow_EditApprovalAvailableToAdmin(String configurations, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_MakeApprovalsAvailableToAdmin = waitChild("//div[@class='table-row table-sub-header' and contains(., 'Make approvals available to')]//div//button[@class='button--plain add-button']", 10, 1, driver);
        if (button_MakeApprovalsAvailableToAdmin != null) {
            button_MakeApprovalsAvailableToAdmin.click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(180, driver);
            logMessage("Button - Make Approvals Available to Admin is clicked.");

            ////////Govind Added on 10102019//////////
            WebElement dialogue_MakeApprovalAvailableToAdmin = waitChild("//div[@class='mc-header']", 10, 1, driver);

            //  WebElement dialogue_MakeApprovalAvailableToAdmin=SystemLibrary.waitChild("//div[@class='focus-trap-container']", 10, 1, driver);
            logScreenshot(driver);

            String textMessage = "";
            if (dialogue_MakeApprovalAvailableToAdmin != null) {

                String[] itemList = splitString(configurations, ";");
                int totalCount = itemList.length;
                for (int i = 0; i < totalCount; i++) {
                    String[] subItem = splitString(itemList[i], ":");
                    String itemName = subItem[0];
                    String itemValue = subItem[1];
                    String xpath_checkbox = "//div[@class='fc-checkbox' and contains(., '" + itemName + "')]//input[@type='checkbox']";
                    WebElement checkbox = waitChild(xpath_checkbox, 3, 1, driver);
                    if (checkbox != null) {
                        tickCheckbox(itemValue, checkbox, driver);
                        
                    } else {
                        logError("Checkbox '" + itemName + "' is NOT found.");
                        errorCounter++;
                    }

                }

                ////////////////////

                logMessage("Screenshot before click Save button.");
                logScreenshot(driver);
                driver.findElement(By.xpath("//button[@type='submit']")).click();
                Thread.sleep(3000);
                logMessage("Save button is clicked.");
                logScreenshot(driver);


            } else {
                logError("The Dialogue - Make Approvals Available to is NOT shown.");
                errorCounter++;
            }


        } else {
            logError("Button - Make Approvals Available to Admin is NOT shown.");
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean editWorkflow_Workflow(String configurations, String typeOfWorkflows, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isDone = false;

        String xpath_LabelWhen="";
        switch (typeOfWorkflows){
            case "1":
                xpath_LabelWhen="//div[@class='table-body']/div[contains(., 'When a Member changes')]//button";
                break;
            case "2":
                xpath_LabelWhen="//div[@class='table-body']/div[contains(., 'When a Member submits')]//button";
                break;
        }

        WebElement label_When= waitChild(xpath_LabelWhen, 10, 1, driver);
        label_When.click();
        //driver.findElement(By.xpath("//div[@class='table-body']/div[contains(., 'When a Member changes')]//button")).click();
        logMessage("Edit Workflow button is clicked. ");
        Thread.sleep(3000);

        String[] configurationList = splitString(configurations, ";");
        int totalCount = configurationList.length;

        for (int i = 0; i < totalCount; i++) {
            String[] currentOption = splitString(configurationList[i], ":");

            if (currentOption[0].equals("then")) {
                driver.findElement(By.xpath("//div[@class='step-container' and starts-with(./div, 'then')]/div[@class='step-option selected']")).click();
                logMessage(currentOption[0] + ":" + currentOption[1] + " is clicked.");
                Thread.sleep(4000);
                logMessage("Screenshot after click 'then' option.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                logMessage(currentOption[1] + " is selected.");
                logScreenshot(driver);
            } else if (currentOption[0].equals("by")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    logMessage(currentOption[1] + " is selected.");
                    logScreenshot(driver);
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(4000);

                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(4000);
                    logMessage(userFullName + " is selected.");
                    logScreenshot(driver);

                }
            } else if (currentOption[0].equals("in")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    logMessage("in: the member's team is selected.");
                    logScreenshot(driver);
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(4000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Send Keys '"+currentOption[1]+"'");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    logMessage("in: " + currentOption[1] + " is selected.");
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("and then")) {
                if (currentOption[1].equals("do nothing")) {
                    driver.findElement(By.xpath("//div[@id='doNothing']")).click();
                    logMessage("and then: do nothing is selected.");
                    Thread.sleep(4000);
                    logScreenshot(driver);
                } else if (currentOption[1].equals("approval is required")) {
                    driver.findElement(By.xpath("//div[@id='approvalRequired']")).click();
                    logMessage("and then: approval is required is selected.");
                    Thread.sleep(4000);
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("by2")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    //Jim added on 01/08/2019
                    driver.findElement(By.xpath("//div[contains(@id, 'Approver') and text()='" + currentOption[1] + "']")).click();
                    logMessage(currentOption[1] + " is selected.");
                    logScreenshot(driver);
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(4000);

                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(4000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(3000);
                    logMessage(userFullName + " is selected.");
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("in2")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    logMessage("in: the member's team is selected.");
                    Thread.sleep(4000);
                    logScreenshot(driver);
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(4000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("sendKeys '"+currentOption[1]+"'.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    logMessage("in: " + currentOption[1] + " is selected.");
                    logScreenshot(driver);
                }
            }
        }

        logMessage("Screenshot before click Done button.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//button[contains(@class,'button--primary')][contains(text(),'Done')]")).click();
        logMessage("Done button is clicked.");
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        isDone = true;
        return isDone;
    }

    public static boolean editWorkflow_Workflow_OLDBackup_12072019(String configurations, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isDone = false;

        driver.findElement(By.xpath("//div[@class='table-body']/div[contains(., 'When a Member changes')]//button")).click();
        logMessage("Edit Workflow button is clicked. ");
        Thread.sleep(3000);

        String[] configurationList = splitString(configurations, ";");
        int totalCount = configurationList.length;

        for (int i = 0; i < totalCount; i++) {
            String[] currentOption = splitString(configurationList[i], ":");

            if (currentOption[0].equals("then")) {
                driver.findElement(By.xpath("//div[@class='step-container' and starts-with(./div, 'then')]/div[@class='step-option selected']")).click();
                logMessage(currentOption[0] + ":" + currentOption[1] + " is clicked.");
                Thread.sleep(2000);
                logMessage("Screenshot after click 'then' option.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                logMessage(currentOption[1] + " is selected.");
            } else if (currentOption[0].equals("by")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    logMessage(currentOption[1] + " is selected.");
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(3000);

                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(3000);
                    logMessage(userFullName + " is selected.");

                }
            } else if (currentOption[0].equals("in")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    logMessage("in: the member's team is selected.");
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    logMessage("in: " + currentOption[1] + " is selected.");
                }
            } else if (currentOption[0].equals("and then")) {
                if (currentOption[1].equals("do nothing")) {
                    driver.findElement(By.xpath("//div[@id='doNothing']")).click();
                    logMessage("and then: do nothing is selected.");
                } else if (currentOption[1].equals("approval is required")) {
                    driver.findElement(By.xpath("//div[@id='approvalRequired']")).click();
                    logMessage("and then: approval is required is selected.");
                }
            } else if (currentOption[0].equals("by2")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    logMessage(currentOption[1] + " is selected.");
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(3000);

                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(3000);
                    logMessage(userFullName + " is selected.");

                }
            } else if (currentOption[0].equals("in2")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    logMessage("in: the member's team is selected.");
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    logMessage("in: " + currentOption[1] + " is selected.");
                }
            }
        }

        logMessage("Screenshot before click Done button.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//button[contains(@class,'button--primary')][contains(text(),'Done')]")).click();
        logMessage("Done button is clicked.");
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        isDone = true;
        return isDone;
    }

    public static boolean editWorkflow_AddAnException(String configurations, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isDone = false;

        driver.findElement(By.xpath("//button[@class='button--account button--round add-button']")).click();
        logMessage("Add An Exception button is clicked.");
        Thread.sleep(4000);
        logScreenshot(driver);

        String[] configurationList = splitString(configurations, ";");
        int totalCount = configurationList.length;

        for (int i = 0; i < totalCount; i++) {
            String[] currentOption = splitString(configurationList[i], ":");

            if (currentOption[0].equals("When")) {

                int totalCountOfCurrentWhenOption = currentOption.length - 1;
                logMessage("There are total " + totalCountOfCurrentWhenOption + " options under 'When' options.");

                for (int j = 1; j <= totalCountOfCurrentWhenOption; j++) {
                    driver.findElement(By.xpath("//div[contains(@class, 'step-option')][contains(text(),'" + currentOption[j] + "')]")).click();
                    logMessage(currentOption[0] + ":" + currentOption[j] + " is selected.");
                    Thread.sleep(5000);
                    logScreenshot(driver);
                }

                logMessage("Screenshot before clicking Continue buttin in 'When' option screen.");
                logScreenshot(driver);
				Thread.sleep(5000);
                PageObj_Workflows.button_Continue(driver).click();
                Thread.sleep(5000);
                logMessage("Continue button is clikced.");
                logScreenshot(driver);

            } else if (currentOption[0].equals("then")) {
                driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                Thread.sleep(5000);
                logMessage("Option '" + currentOption[1] + "' is selected.");
                logScreenshot(driver);
            } else if (currentOption[0].equals("to managers in")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    Thread.sleep(5000);
                    logMessage("in: the member's team is selected.");
                    logScreenshot(driver);
                } else {
                    WebElement element = waitChild("//input[@id='teamSearchField']", 120, 1,driver);
                    element.sendKeys(currentOption[1]);
                    //driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Search Text: '"+currentOption[1]+"' is input.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage("in: " + currentOption[1] + " is selected.");
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("by")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage(currentOption[1] + " is selected.");
                    logScreenshot(driver);
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(5000);
                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(5000);
                    logMessage(userFullName + " is selected.");
                    logScreenshot(driver);

                }
            } else if (currentOption[0].equals("in")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    Thread.sleep(5000);
                    logMessage("in: the member's team is selected.");
                    logScreenshot(driver);
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage("in: " + currentOption[1] + " is selected.");
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("and then")) {
                if (currentOption[1].equals("do nothing")) {
                    driver.findElement(By.xpath("//div[@id='doNothing']")).click();
                    Thread.sleep(5000);
                    logMessage("and then: do nothing is selected.");
                    logScreenshot(driver);
                } else if (currentOption[1].equals("approval is required")) {
                    driver.findElement(By.xpath("//div[@id='approvalRequired']")).click();
                    Thread.sleep(5000);
                    logMessage("and then: approval is required is selected.");
                    logScreenshot(driver);
                }
            } else if (currentOption[0].equals("by2")) {
                if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage(currentOption[1] + " is selected.");
                    logScreenshot(driver);
                } else {
                    String userFullName = currentOption[1];
                    String firstName = getFirstName(userFullName);

                    //driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                    Thread.sleep(5000);

                    driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                    logMessage(firstName + " is input.");
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                    Thread.sleep(5000);
                    logMessage(userFullName + " is selected.");
                    logScreenshot(driver);


                }
            } else if (currentOption[0].equals("in2")) {
                if (currentOption[1].equals("the member's team")) {
                    driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                    Thread.sleep(5000);
                    logMessage("in: the member's team is selected.");
                    logScreenshot(driver);
                } else {
                    driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Send Keys '"+currentOption[1]+"'.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage("in: " + currentOption[1] + " is selected.");
                    logScreenshot(driver);
                }
            }
        }

        logMessage("Screenshot before click Add button.");
        logScreenshot(driver);
        driver.findElement(By.xpath("//button[contains(@class,'button--primary')][contains(text(),'Add')]")).click();
        logMessage("Add button is clicked.");
        Thread.sleep(5000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logScreenshot(driver);

        WebElement button_overwrite = waitChild("//button[contains(@class,'button--danger') and text()='Yes, overwrite']", 5, 1, driver);
        if (button_overwrite != null) {
            button_overwrite.click();
            Thread.sleep(5000);
            logMessage("Button 'Yes, overwrite' is clicked.");
        }
        GeneralBasic.waitSpinnerDisappear(60, driver);
        logScreenshot(driver);

        isDone = true;
        return isDone;

    }


    public static boolean editWorkflow_EditException(String configurations, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isDone = false;
        int errorCounter = 0;

        String[] configurationList = splitString(configurations, ";");
        int totalCount = configurationList.length;

        String[] currentOption = splitString(configurationList[0], ":");

        String xpath_ExceptionItem = "//div[@class='table-body']//div[@class='table-row exception-row' and contains(., '" + currentOption[1] + "')]";
        String xpath_ButtonMenu_ExceptionItem = xpath_ExceptionItem + "//button[contains(@class, 'button--plain add-button')]";

        if (waitChild(xpath_ExceptionItem, 10, 1, driver) != null) {
            logMessage("Exception item '" + currentOption[1] + " is found.");
            driver.findElement(By.xpath(xpath_ButtonMenu_ExceptionItem)).click();
            Thread.sleep(5000);
            logMessage("Ellipsis button of " + currentOption[1] + " is clicked.");

            //driver.findElement(By.xpath("//a[@href='javascript:;' and contains(., 'Edit exception')]")).click();
            driver.findElement(By.linkText("Edit exception")).click();
            Thread.sleep(5000);
            logMessage("Edit exception menu is clicked.");

            for (int i = 1; i < totalCount; i++) {
                currentOption = splitString(configurationList[i], ":");

                if (currentOption[0].equals("then")) {

                    driver.findElement(By.xpath("//div[@class='step-container' and starts-with(./div, 'then')]/div[@class='step-option selected']")).click();
                    logMessage(currentOption[0] + ":" + currentOption[1] + " is clicked.");
                    Thread.sleep(5000);
                    logMessage("Screenshot after click 'then' option.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                    Thread.sleep(5000);
                    logMessage("Option '" + currentOption[1] + "' is selected.");
                    logScreenshot(driver);
                } else if (currentOption[0].equals("to managers in")) {
                    if (currentOption[1].equals("the member's team")) {
                        driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                        Thread.sleep(5000);
                        logMessage("in: the member's team is selected.");
                        logScreenshot(driver);
                    } else {
                        driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                        Thread.sleep(10000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Send Keys '"+currentOption[1]+"'.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                        Thread.sleep(5000);
                        logMessage("in: " + currentOption[1] + " is selected.");
                        logScreenshot(driver);
                    }
                } else if (currentOption[0].equals("by")) {
                    if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                        driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                        Thread.sleep(5000);
                        logMessage(currentOption[1] + " is selected.");
                        logScreenshot(driver);
                    } else {
                        String userFullName = currentOption[1];
                        String firstName = getFirstName(userFullName);

                        driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                        Thread.sleep(5000);

                        driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                        logMessage(firstName + " is input.");
                        Thread.sleep(10000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                        Thread.sleep(3000);
                        logMessage(userFullName + " is selected.");
                        logScreenshot(driver);

                    }
                } else if (currentOption[0].equals("in")) {
                    if (currentOption[1].equals("the member's team")) {
                        driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                        Thread.sleep(5000);
                        logMessage("in: the member's team is selected.");
                        logScreenshot(driver);
                    } else {
                        driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                        Thread.sleep(10000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Send keys '"+currentOption[1]+"'.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                        Thread.sleep(5000);
                        logMessage("in: " + currentOption[1] + " is selected.");
                        logScreenshot(driver);
                    }
                } else if (currentOption[0].equals("and then")) {
                    if (currentOption[1].equals("do nothing")) {
                        driver.findElement(By.xpath("//div[@id='doNothing']")).click();
                        Thread.sleep(5000);
                        logMessage("and then: do nothing is selected.");
                        logScreenshot(driver);
                    } else if (currentOption[1].equals("approval is required")) {
                        driver.findElement(By.xpath("//div[@id='approvalRequired']")).click();
                        Thread.sleep(5000);
                        logMessage("and then: approval is required is selected.");
                        logScreenshot(driver);
                    }
                } else if (currentOption[0].equals("by2")) {
                    if ((currentOption[1].contains("any manager")) || (currentOption[1].contains("all managers"))) {
                        driver.findElement(By.xpath("//div[text()='" + currentOption[1] + "']")).click();
                        Thread.sleep(5000);
                        logMessage(currentOption[1] + " is selected.");
                        logScreenshot(driver);
                    } else {
                        String userFullName = currentOption[1];
                        String firstName = getFirstName(userFullName);

                        driver.findElement(By.xpath("//div[@class='text-with-selector']")).click();
                        Thread.sleep(5000);

                        driver.findElement(By.xpath("//input[@id='searchField']")).sendKeys(firstName);

                        logMessage(firstName + " is input.");
                        Thread.sleep(10000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@class='selector']/div[@class='list-item' and contains(span, '" + userFullName + "')]")).click();
                        Thread.sleep(5000);
                        logMessage(userFullName + " is selected.");
                        logScreenshot(driver);

                    }
                } else if (currentOption[0].equals("in2")) {
                    if (currentOption[1].equals("the member's team")) {
                        driver.findElement(By.xpath("//div[text()=\"the member's team\"]")).click();
                        Thread.sleep(5000);
                        logMessage("in: the member's team is selected.");
                        logScreenshot(driver);
                    } else {
                        driver.findElement(By.xpath("//input[@id='teamSearchField']")).sendKeys(currentOption[1]);
                        Thread.sleep(10000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Send Keys '"+currentOption[1]+"'.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//div[@class='selector' and contains(., '" + currentOption[1] + "')]/div/span[text()='" + currentOption[1] + "']")).click();
                        Thread.sleep(5000);
                        logMessage("in: " + currentOption[1] + " is selected.");
                        logScreenshot(driver);
                    }
                }

            }
        } else {
            logError("The Item '" + currentOption[1] + "' is NOT found in table.");
            errorCounter++;
        }

        logMessage("Screenshot before click Done button.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//button[contains(@class,'button--primary')][contains(text(),'Done')]")).click();
        Thread.sleep(5000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Done button is clicked.");
        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editWorkflow_DeleteException(String itemToBeDeleted, WebDriver driver) throws Exception {
        //Workflow page is shown and workflow type is selected.
        boolean isDone = false;
        int errorCounter = 0;

        String[] itemList = splitString(itemToBeDeleted, ";");
        int totalCount = itemList.length;

        String xpath_MenuButton = "//div[@class='table-row exception-row'";

        for (int i = 0; i < totalCount; i++) {
            xpath_MenuButton = xpath_MenuButton + " and contains(., '" + itemList[i] + "')";
        }

        xpath_MenuButton = xpath_MenuButton + "]//button[@class='button--plain add-button']";
        WebElement menuButton = waitChild(xpath_MenuButton, 10, 1, driver);

        if (menuButton != null) {
            menuButton.click();
            Thread.sleep(10000);
            logMessage("Item '" + itemToBeDeleted + "' is found and Menu Button is clicked.");
            logScreenshot(driver);

            waitChild("//ul[@class='sub-nav show']//li[@class='dangerous' and contains(., 'Delete exception')]", 120, 1, driver).click();
            logMessage("Menu - Delete Exception is clicked.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[contains(@class,'button--danger') and text()='Yes, delete']")).click();
            Thread.sleep(10000);
            waitSpinnerDisappear(120, driver);
            logMessage("Button - Yes delete is clicked.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    ///////////// End of Workflows /////////////////

    public static boolean addBankAccount(String firstName, String middleName, String lastName, String preferredName, String bsbNumber, String bankName, String accountNumber, String accountName, String payAmmount, String balanceOfPay, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver);

        PageObj_BankAccounts.button_AddBankAccount(driver).click();
        Thread.sleep(3000);
        logMessage("Add Bank Account button is clicked.");
        logMessage("Screenshot before edit bank account.");
        logScreenshot(driver);


        if (bsbNumber != null) {
            PageObj_BankAccounts.textBox_BSBNumber(driver).click();
            clearTextBox(PageObj_BankAccounts.textBox_BSBNumber(driver));
            PageObj_BankAccounts.textBox_BSBNumber(driver).sendKeys(bsbNumber);
            ;
            logMessage("BSB Number '" + bsbNumber + "' is input.");

            WebElement element_BSBError = waitChild("//span[@class='validation-error'][contains(text(),'Must be 6 numbers.')]", 3, 1, driver);
            if (element_BSBError != null) {
                if (element_BSBError.getText().length() > 2) {
                    errorCounter++;
                    logWarning("BSB '" + element_BSBError.getText() + "'");
                }
            }
        }

        if (bankName != null) {
            PageObj_BankAccounts.textBox_BankName(driver).click();
            clearTextBox(PageObj_BankAccounts.textBox_BankName(driver));
            PageObj_BankAccounts.textBox_BankName(driver).sendKeys(bankName);
            ;
            logMessage("BankName '" + bankName + "' is input.");

            WebElement element_BankNameError = waitChild("//div[@id='form-element-BankName']//span[@class='required-msg validation-error']", 3, 1, driver);
            if (element_BankNameError != null) {
                if (element_BankNameError.getText().length() > 2) {
                    errorCounter++;
                    logWarning("Bank Name '" + element_BankNameError.getText() + "'");
                }
            }
        }

        if (accountNumber != null) {
            PageObj_BankAccounts.textBox_AccountNumber(driver).click();
            clearTextBox(PageObj_BankAccounts.textBox_AccountNumber(driver));
            PageObj_BankAccounts.textBox_AccountNumber(driver).sendKeys(accountNumber);
            ;
            logMessage("AccountNumber '" + accountNumber + "' is input.");

            WebElement element_AccountNumber = waitChild("//div[@id='form-element-AccountNumber']//span[@class='required-msg validation-error']", 3, 1, driver);
            if (element_AccountNumber != null) {
                if (element_AccountNumber.getText().length() > 2) {
                    errorCounter++;
                    logWarning("Account Number '" + element_AccountNumber.getText() + "'");
                }
            }
        }

        if (accountName != null) {
            PageObj_BankAccounts.textBox_AccountName(driver).click();
            clearTextBox(PageObj_BankAccounts.textBox_AccountName(driver));
            PageObj_BankAccounts.textBox_AccountName(driver).sendKeys(accountName);
            ;
            logMessage("AccountName '" + accountName + "' is input.");

            WebElement element_AccountName = waitChild("//div[@id='form-element-AccountName']//span[@class='required-msg validation-error']", 3, 1, driver);
            if (element_AccountName != null) {
                if (element_AccountName.getText().length() > 2) {
                    errorCounter++;
                    logWarning("Account Name '" + element_AccountName.getText() + "'");
                }
            }
        }

        if (balanceOfPay != null) {
            tickCheckbox(balanceOfPay, PageObj_BankAccounts.checkBox_BalanceOfPay(driver), driver);
        }

        if (payAmmount != null) {
            PageObj_BankAccounts.textBox_PayAmount(driver).click();
            clearTextBox(PageObj_BankAccounts.textBox_PayAmount(driver));
            PageObj_BankAccounts.textBox_PayAmount(driver).sendKeys(payAmmount);
            ;
            logMessage("Pay Amount '" + payAmmount + "' is input.");
        }

        logMessage("Screenshot before click Save button.");
        logScreenshot(driver);

        if (PageObj_BankAccounts.button_Save_AddBankAccount(driver).isEnabled()) {
            PageObj_BankAccounts.button_Save_AddBankAccount(driver).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Save button is clicked in the Add Bank Account screen.");

            if (waitChild("//button[@type='submit' and text()='Save']", 5, 1, driver) == null) {
                logMessage("Bank Account is added.");
            } else {
                logWarning("Bank Account cannot be added. Screenshot below.");
                logScreenshot(driver);

                if (validateTextValueInElement(PageObj_BankAccounts.form_AddBankAccount(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
                PageObj_BankAccounts.button_Close_AddBankAccount(driver).click();
                Thread.sleep(3000);
                errorCounter++;

            }

        } else {
            PageObj_BankAccounts.button_Close_AddBankAccount(driver).click();
            errorCounter++;
            logWarning("No change has been made.");

        }

        logScreenshot(driver);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean changeOrderOfBankAccount(String firstName, String middleName, String lastName, String preferredName, String accountNumber, String accountName, String orderDirection, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        //oderDirection 1- Up -1 Down
        boolean isDone = false;
        int errorCounter = 0;

        searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver);

        PageObj_BankAccounts.button_ChangeBankAccountOrder(driver).click();
        Thread.sleep(3000);
        logMessage("Change Bank Account Order button is clicked.");
        logMessage("Screenshot Before changing Bank Account order .");
        logScreenshot(driver);

        String xpath_BankAccountList = "//form/div[@class='list-item-row multiline' and contains(., '" + accountName + "') and contains(., '" + accountNumber + "')]";

        String xpath_ButtonMoveUp = "//form/div[@class='list-item-row multiline' and contains(., '" + accountName + "') and contains(., '" + accountNumber + "')]//button/i[@class='icon-move-up']";
        String xpath_ButtonMoveDown = "//form/div[@class='list-item-row multiline' and contains(., '" + accountName + "') and contains(., '" + accountNumber + "')]//button/i[@class='icon-move-down']";

        if (waitChild(xpath_BankAccountList, 5, 1, driver) != null) {
            logMessage("Bank Account: '" + accountName + " " + accountNumber + "' is found.");

            int clickTimes = 0;
            if (orderDirection != null) {
                int moveCount = Integer.parseInt(orderDirection);
                if (moveCount < 0) {
                    for (int i = 0; i > moveCount; i--) {
                        driver.findElement(By.xpath(xpath_ButtonMoveDown)).click();
                        clickTimes++;
                    }
                    logMessage("Move down button is clicked " + clickTimes + ".");
                } else if (moveCount > 0) {
                    for (int i = 0; i < moveCount; i++) {
                        driver.findElement(By.xpath(xpath_ButtonMoveUp)).click();
                    }
                    logMessage("Move Up button is clicked " + clickTimes + ".");
                }
            }

            logMessage("Screenshot after changing order.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[contains(@class,'button')][contains(text(),'Save')]")).click();
            Thread.sleep(5000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Save button is clicked.");
            logMessage("Screenshot after clicking Save button.");
            logScreenshot(driver);

        } else {
            logError("Bank Account '" + accountName + " " + accountNumber + "' is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editContactDetails_EmergencyContact(String emergencyContactName, String emergencyContactRelationship, String emergencyContactMobilePhoneNumber, String emergencyContactPhoneNumber, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        PageFac_ContactDetail pf = PageFactory.initElements(driver, PageFac_ContactDetail.class);

        if ((emergencyContactName != null) || (emergencyContactRelationship != null) || (emergencyContactMobilePhoneNumber != null) || (emergencyContactPhoneNumber != null)) {

        WebElement edit_EmergencyContact_Button= waitChild("//div[@id='ea-emergency-contact' and contains(., 'Emergency contact')]//span[@class='icon-right']//i[@class='icon-edit']", 2, 1, driver);
        if (edit_EmergencyContact_Button!=null){
            edit_EmergencyContact_Button.click();
            Thread.sleep(2000);
            waitSpinnerDisappear(120, driver);
            logMessage("Edit button Enabled and clicked in Emergency Contact Details.");
            /*
            pf.button_EditContactDetails_EmergencyContact.click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Edit Emergency contact button is clicked.");*/

            if (emergencyContactName != null) {
                WebElement element=pf.textbox_EmergencyContactName;
                element.click();
                clearTextBox(element);
                element.sendKeys(emergencyContactName);
                logMessage("Emergency Contact Name '" + emergencyContactName + "' is input.");
            }

            if (emergencyContactRelationship != null) {
                WebElement element=pf.textbox_EmergencyContactRelationshop;
                element.click();
                clearTextBox(element);
                element.sendKeys(emergencyContactRelationship);
                logMessage("Emergency Contact Name '" + emergencyContactRelationship + "' is input.");
            }

            if (emergencyContactMobilePhoneNumber != null) {
                WebElement element=pf.textbox_EmergencyMobileContact;
                element.click();
                clearTextBox(element);
                element.sendKeys(emergencyContactMobilePhoneNumber);
                logMessage("Emergency Contact Name '" + emergencyContactMobilePhoneNumber + "' is input.");
            }

            if (emergencyContactPhoneNumber != null) {
                WebElement element=pf.textbox_EmergencyPhoneContact;
                element.click();
                clearTextBox(element);
                element.sendKeys(emergencyContactPhoneNumber);
                logMessage("Emergency Contact Name '" + emergencyContactPhoneNumber + "' is input.");
            }

            logMessage("Screenshot before click Save button.");
            logScreenshot(driver);

            if (pf.butotn_SaveEmergencyContacts.isEnabled()) {
                Thread.sleep(3000);
                pf.butotn_SaveEmergencyContacts.click();
                Thread.sleep(3000);
                waitSpinnerDisappear(120, driver);
                logMessage("Save button is clicked.");

                if (waitChild("//form[@autocomplete='off']", 10, 1, driver) != null) {
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    logError("Emergency Contact is not save. Dialogue is closed.");
                    Thread.sleep(3000);
                    errorCounter++;
                }
            } else {
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                logError("Emergency Contact is not save. Dialogue is closed.");
                Thread.sleep(3000);
                errorCounter++;
            }

            }else{
                logError("Edit button Not Enabled and Emergency Contact Details Not Opens");
                errorCounter++;
            }

        }

        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean selectItemFromESSDropdownList(WebElement element_Dropdown, String itemName, WebDriver driver) throws InterruptedException {
        //Edit Address diaglogue is shown first.
        boolean isSelected = false;
        element_Dropdown.click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        String xpath_ListItem="//div[starts-with(@class, 'list-item')][contains(text(),'" + itemName + "')]";
        logDebug(xpath_ListItem);
        WebElement stateItem = waitChild(xpath_ListItem, 10, 1, driver);
        //logMessage("stateItem is Enabled: "+stateItem.isEnabled());
        //logMessage("stateItem is Displayed: "+stateItem.isDisplayed());
        //logMessage("stateItem is Selected: "+stateItem.isSelected());

        if (stateItem!=null){
            new Actions(driver).moveToElement(stateItem).perform();

            if (stateItem != null) {
                stateItem.click();
                logMessage("State '" + itemName + "' is clicked.");
                Thread.sleep(2000);
                isSelected = true;
            } else {
                logError("State '" + itemName + "' is NOT selected.");
            }
        }else{
            logError("Item '"+itemName+"' is NOT found.");
        }

        return isSelected;
    }

    public static boolean validateMyApprovals_ProfileChange_ViaDashboard_NonAdmin(String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //This function is for non admin user
        boolean isPassed = false;
        int errorCounter = 0;
        displayDashboard(driver);

        if (PageObj_Dashboard.panel_PendingProfileApprovals(driver) != null) {
            ///////////////////////////
            PageObj_Dashboard.panel_PendingProfileApprovals(driver).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Panel Pending Profile Approvals is clicked.");
            logScreenshot(driver);

            if (storeFileName != null) {

                if (!validateTextValueInWebElementInUse(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, expectedTextContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;

            }
            //////
        } else {
            logError("Panel - Pending Profile Approvals is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean displayApprovalsPage_ViaNavigationBar_NOTAdmin(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown = false;
        PageObj_NavigationBar.approvals(driver).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Approvals Link is click in the Navigation bar.");

        if (waitChild("//h3[contains(text(), 'Approvals')]", 60, 1, driver) != null) {
            logMessage("Screenshot of Approval Page.");
            isShown = true;
        } else {
            logError("Approvals page is NOT shown. Screenshot below:");
        }
        logScreenshot(driver);

        return isShown;
    }

    public static boolean validateApprovals_All_NONAdminUser(String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //This function is for non admin user
        boolean isPassed = false;

        displayApprovalsPage_ViaNavigationBar_NOTAdmin(driver);
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if (storeFileName != null) {
            if (expectedTextContent == null) {
                isPassed = validateTextValueInElement(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName);
            } else if (expectedTextContent != null) {
                isPassed = validateTextValueContainedInElement(PageObj_Approvals.form_ApprovalList(driver), storeFileName, isUpdateStore, isCompare, expectedTextContent, testSerialNo, emailDomainName);
            }
        }

        return isPassed;
    }

    public static boolean approvePersonalInformationChanges(String firstName, String middleName, String lastName, String preferredName, String panelName, String actionMethod, String declineComment, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        String xpath_Panel = null;
        String xpath_Button_PendingApproval = null;
        String xpath_Button_Approve = null;
        String xpath_Button_Decline = null;

        if (searchUserAndDisplayPersonalInformationPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver) && (actionMethod != null)) {
            switch (panelName) {
                case "NAME":
                    xpath_Panel = getElementXPath(driver, PageObj_PersonalInfo.panel_Name(driver));
                    break;
                case "ADDITIONAL INFOMATION":
                    xpath_Panel = getElementXPath(driver, PageObj_PersonalInfo.panel_AdditionalInformation(driver));
                    break;
                case "MEDICAL CONDITIONS":
                    xpath_Panel = getElementXPath(driver, PageObj_PersonalInfo.panel_MedicalConditions(driver));
                    break;
            }
            xpath_Button_PendingApproval = xpath_Panel + "//button[contains(@class,'pending-button button')]";
            xpath_Button_Approve = xpath_Panel + "//button[contains(@class,'approve-button button--success')]";
            xpath_Button_Decline = xpath_Panel + "//button[contains(@class,'decline-button button--danger')]";

            WebElement button_PendingApproval = driver.findElement(By.xpath(xpath_Button_PendingApproval));
            logMessage("Screenshot before click Pending Approval button.");
            logScreenshot(driver);

            if (button_PendingApproval.isEnabled()) {
                button_PendingApproval.click();
                logMessage("Pending Approval button is clicked.");
                Thread.sleep(3000);

                if (actionMethod.equals("1")) {
                    logMessage("Screenshot before click Approve button.");
                    logScreenshot(driver);
                    driver.findElement(By.xpath(xpath_Button_Approve)).click();
                    logMessage("Approve button is click.");
                } else if (actionMethod.equals("2")) {
                    driver.findElement(By.xpath(xpath_Button_Decline)).click();
                    logMessage("Decline button is click.");
                    Thread.sleep(3000);

                    driver.findElement(By.xpath("//textarea[@id='comments']")).sendKeys(declineComment);
                    logMessage("Screenshot before click Decline button.");
                    driver.findElement(By.xpath("//form[contains(., 'Add comment')]//button[@type='submit']")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                }
            } else {
                logError("Pending approval button is disabled.");
                errorCounter++;
            }


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean addRedirectApprovals(String firstName, String middleName, String lastName, String preferredName, String approverSearchKeyword, String approverFirstName, String approverMiddleName, String approverLastName, String approverPerferredName, String startDate, String endDate, String storeFileName, String isUpdateStore, String isCompare, String storeFileName2, String expectedMessageContain, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //actionMethod: 1- Add 2- Remove

        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            PageObj_TeamsNRoles.ellipsis_TeamsRolesMoreOption(driver).click();
            Thread.sleep(3000);
            logMessage("Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            PageObj_TeamsNRoles.ellipsis_Menue_RedirectApprovals(driver).click();
            logMessage("Redirect approvals menu is clicked.");

            if (waitChild("//form[contains(., 'Add Redirect Approvals')]", 60, 1, driver) != null) {

                if (storeFileName != null) {

                    WebElement elementMessageBox = waitChild("//div[@class='pending']", 5, 1, driver);
                    if (elementMessageBox != null)
                        if (!validateTextValueInElement(elementMessageBox, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                            errorCounter++;
                }

                WebElement element = driver.findElement(By.xpath("//input[@name='searchField']"));
                element.click();
                element.sendKeys(approverSearchKeyword);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Approver Search Keyword '" + approverSearchKeyword + "' is input.");

                if (waitChild("//span[@class='server-error-msg validation-error'][contains(text(),'No matches found')]", 10, 1, driver) != null) {
                    logError("Approver '" + approverSearchKeyword + "' is NOT found.");
                    logScreenshot(driver);
                    errorCounter++;

                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(2000);
                    logMessage("Close button is clicked.");
                } else {
                    String approversFullName = null;
                    if (approverPerferredName != null) {
                        approversFullName = approverFirstName + " (" + approverPerferredName + ") " + approverLastName;
                    } else {
                        approversFullName = approverFirstName + " " + approverLastName;
                    }

                    WebElement listItem = waitChild("//div[@class='list-item' and contains(., '" + approversFullName + "')]", 5, 1, driver);
                    if (listItem != null) {
                        listItem.click();
                        logMessage("Approver '" + approversFullName + "' is selected.");

                        if (startDate != null) {
                            if (startDate.contains(";")) {
                                startDate = getExpectedDate(startDate, null);
                            }

                            PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).click();
                            Thread.sleep(2000);
                            PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).sendKeys(Keys.DELETE);
                            PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).sendKeys(startDate);
                            logMessage("Start Date '" + startDate + "' is input.");
                        }

                        if (endDate != null) {
                            if (endDate.contains(";")) {
                                endDate = getExpectedDate(endDate, null);
                            }

                            PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).click();
                            Thread.sleep(2000);
                            PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).sendKeys(Keys.DELETE);
                            PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).sendKeys(endDate);
                            logMessage("End date '" + endDate + "' is input.");
                        }

                        logMessage("Screenshot before click Add button.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//button[@type='submit' and text()='Add']")).click();
                        logMessage("Add button is clicked.");
                        Thread.sleep(2000);
                        waitSpinnerDisappear(120, driver);

                        logMessage("Screenshot after clicking Add button.");
                        logScreenshot(driver);

                        //Validate message after add Redirect Approvals
                        if ((storeFileName2 != null) || (expectedMessageContain != null)) {
                            WebElement textArea = waitChild("//span[@class='heading-container-text']", 10, 1, driver);
                            if (textArea != null) {
                                if (storeFileName2 != null) {
                                    if (!validateTextValueInElement(textArea, storeFileName2, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
                                        if (expectedMessageContain != null) {
                                            if (!validateTextValueContainedInElement(textArea, storeFileName2, isUpdateStore, isCompare, expectedMessageContain, testSerialNo, emailDomainName))
                                                errorCounter++;
                                        } else {
                                            errorCounter++;
                                        }
                                    }
                                }
                            } else {
                                logError("Messagebox is NOT found after Redirect Approvals.");
                                errorCounter++;
                            }
                        }
                    } else {
                        logError("Approver '" + approversFullName + "' is NOT found.");
                        errorCounter++;

                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        Thread.sleep(2000);
                        logMessage("Close button is clicked.");
                    }
                }
            }
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean removeRedirectApprovals(String firstName, String middleName, String lastName, String preferredName, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11, String s12, String s13, String s14, String s15, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            WebElement ellipsisButton_RedirectApprovals = waitChild("//form[contains(., 'approvals are being redirected to')]//button[@class='button--plain add-button']", 2, 1, driver);
            if (ellipsisButton_RedirectApprovals != null) {
                ellipsisButton_RedirectApprovals.click();
                Thread.sleep(2000);
                logMessage("Ellipsis button - Redirect Approvals is clicked. ");
                logScreenshot(driver);

                driver.findElement(By.xpath("//li[contains(., 'Remove redirected approver')]")).click();
                Thread.sleep(3000);
                logMessage("Menu 'Remove redirected approver' is clicked.");

                logMessage("Screenshot before click Remove button.");
                logScreenshot(driver);

                WebElement removeButton = waitChild("//button[contains(@class,'button--danger') and text()='Yes, remove']", 2, 1, driver);
                if (removeButton != null) {
                    removeButton.click();
                    Thread.sleep(3000);
                    logMessage("Button 'Yes, remove' is clicked.");
                } else {
                    logError("Remove button is NOT found.");
                    errorCounter++;
                }

            } else {
                errorCounter++;
                logError("Ellipsis Button - RedirectApprovals is not found.");
            }

        } else {
            errorCounter++;

        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static WebElement selectUserFromTeamPage(String teamName, String firstName, String middleName, String lastName, String preferredName, WebDriver driver) throws InterruptedException {
        String userFullName = getUserFullname(firstName, middleName, lastName, preferredName);
        String userXpath = "//div[@class='list-section']//div[@class='list-section-container']/div[@class='list-item-row single-line-medium' and contains(., '" + teamName + "')]//span[@class='list-item-cell' and contains(., '" + userFullName + "')]//a";
        logMessage(userXpath);
        WebElement element = waitChild("//div[@class='list-section']//div[@class='list-section-container']/div[@class='list-item-row single-line-medium' and contains(., '" + teamName + "')]//span[@class='list-item-cell' and contains(., '" + userFullName + "')]//a", 2, 1, driver);
        if (element.equals(null)) {
            logError("User '" + userFullName + "' NOT found.");
        }
        return element;
    }

    public static String getUserFullname(String firstName, String middleName, String lastName, String preferredName) throws InterruptedException {
        String fullName = "";
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }
        logMessage("User's full name is '" + fullName + "'.");
        return fullName;
    }

    public static boolean validateRolesPage(String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        if (displaySettings_RolesPermissions(driver)) {
            if (validateTextValueInElement(PageObj_Roles.table_Roles(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
                isPassed = true;
            }
        }

        return isPassed;
    }

    public static boolean validateEllipsisMenuInBankAccountsPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (GeneralBasic.searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            driver.findElement(By.xpath("//div[@class='more-options-sub-menu']//button[@class='button--plain add-button']")).click();
            Thread.sleep(3000);

            if (!validateTextValueInElement(driver.findElement(By.xpath("//ul[@class='sub-nav show']")), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean editBankAccount(String firstName, String middleName, String lastName, String preferredName, String bsbNumber, String bankName, String accountNumber, String accountName, String payAmmount, String balanceOfPay, String storeFileName, String isUpdateStore, String isCompare, String newBSBNumber, String newBankName, String newAccountNumber, String newAccountName, String newPayAmount, String newBalanceOfPay, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver);


        WebElement ellipsisMenuButton = waitChild("//Form/div[@class='list-item-row multiline' and contains(., '" + accountNumber + "')]//button[@class='button--plain add-button']", 2, 1, driver);
        if (ellipsisMenuButton != null) {
            ellipsisMenuButton.click();
            Thread.sleep(2000);
            logMessage("Ellipsis Menu button is clicked.");
            logMessage("Screenshot after clicking Ellipsis menu button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//ul[@class='sub-nav show' and contains(., 'Edit')]//a")).click();
            logMessage("Edit button is clicked.");
            Thread.sleep(2000);

            logMessage("Screenshot before edit bank account.");
            logScreenshot(driver);

            if (newBSBNumber != null) {
                PageObj_BankAccounts.textBox_BSBNumber(driver).click();
                clearTextBox(PageObj_BankAccounts.textBox_BSBNumber(driver));
                PageObj_BankAccounts.textBox_BSBNumber(driver).sendKeys(newBSBNumber);
                ;
                logMessage("New BSB Number '" + newBSBNumber + "' is input.");

                WebElement element_BSBError = waitChild("//span[@class='validation-error'][contains(text(),'Must be 6 numbers.')]", 3, 1, driver);
                if (element_BSBError != null) {
                    if (element_BSBError.getText().length() > 2) {
                        errorCounter++;
                        logWarning("BSB '" + element_BSBError.getText() + "'");
                    }
                }
            }

            if (newBankName != null) {
                PageObj_BankAccounts.textBox_BankName(driver).click();
                clearTextBox(PageObj_BankAccounts.textBox_BankName(driver));
                PageObj_BankAccounts.textBox_BankName(driver).sendKeys(newBankName);
                ;
                logMessage("BankName '" + newBankName + "' is input.");

                WebElement element_BankNameError = waitChild("//div[@id='form-element-BankName']//span[@class='required-msg validation-error']", 3, 1, driver);
                if (element_BankNameError != null) {
                    if (element_BankNameError.getText().length() > 2) {
                        errorCounter++;
                        logWarning("Bank Name '" + element_BankNameError.getText() + "'");
                    }
                }
            }

            if (newAccountNumber != null) {
                PageObj_BankAccounts.textBox_AccountNumber(driver).click();
                clearTextBox(PageObj_BankAccounts.textBox_AccountNumber(driver));
                PageObj_BankAccounts.textBox_AccountNumber(driver).sendKeys(newAccountNumber);
                ;
                logMessage("AccountNumber '" + newAccountNumber + "' is input.");

                WebElement element_AccountNumber = waitChild("//div[@id='form-element-AccountNumber']//span[@class='required-msg validation-error']", 3, 1, driver);
                if (element_AccountNumber != null) {
                    if (element_AccountNumber.getText().length() > 2) {
                        errorCounter++;
                        logWarning("Account Number '" + element_AccountNumber.getText() + "'");
                    }
                }
            }

            if (newAccountName != null) {
                PageObj_BankAccounts.textBox_AccountName(driver).click();
                clearTextBox(PageObj_BankAccounts.textBox_AccountName(driver));
                PageObj_BankAccounts.textBox_AccountName(driver).sendKeys(newAccountName);
                ;
                logMessage("AccountName '" + newAccountName + "' is input.");

                WebElement element_AccountName = waitChild("//div[@id='form-element-AccountName']//span[@class='required-msg validation-error']", 3, 1, driver);
                if (element_AccountName != null) {
                    if (element_AccountName.getText().length() > 2) {
                        errorCounter++;
                        logWarning("Account Name '" + element_AccountName.getText() + "'");
                    }
                }
            }

            if (newBalanceOfPay != null) {
                tickCheckbox(newBalanceOfPay, PageObj_BankAccounts.checkBox_BalanceOfPay(driver), driver);
            }

            if (newPayAmount != null) {
                PageObj_BankAccounts.textBox_PayAmount(driver).click();
                clearTextBox(PageObj_BankAccounts.textBox_PayAmount(driver));
                PageObj_BankAccounts.textBox_PayAmount(driver).sendKeys(newPayAmount);
                ;
                logMessage("Pay Amount '" + newPayAmount + "' is input.");
            }

            logMessage("Screenshot before click Save button.");
            logScreenshot(driver);

            if (PageObj_BankAccounts.button_Save_AddBankAccount(driver).isEnabled()) {
                PageObj_BankAccounts.button_Save_AddBankAccount(driver).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Save button is clicked in the Add Bank Account screen.");

                if (waitChild("//button[@type='submit' and text()='Save']", 5, 1, driver) == null) {
                    logMessage("Bank Account is added.");
                } else {
                    logWarning("Bank Account cannot be added. Screenshot below.");
                    logScreenshot(driver);

                    if (validateTextValueInElement(PageObj_BankAccounts.form_AddBankAccount(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                        errorCounter++;
                    PageObj_BankAccounts.button_Close_AddBankAccount(driver).click();
                    Thread.sleep(3000);
                    errorCounter++;

                }

            } else {
                PageObj_BankAccounts.button_Close_AddBankAccount(driver).click();
                errorCounter++;
                logWarning("No change has been made.");

            }
        } else {
            logError("Bank account number '" + accountNumber + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean deleteContactDetails(String firstName, String middleName, String lastName, String preferredName, String personalEmail, String personalMobile, String homeNumber, String approveChangeOfPersonalContact, String workEmail, String officeNumber, String workMobile, String actionMethod, String residentialCountry, String residentialAddress, String residentialSuburb, String residentialPostcode, String residentialState, String userForPostalAddress, String postalCountry, String postalAddress, String postalSuburb, String postalPostcode, String postalState, String emergencyContactName, String emergencyContactRelationship, String emergencyContactMobilePhoneNumber, String emergencyContactPhoneNumber, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, ClassNotFoundException, SQLException {
        //Only working for Deleting Emergency Contact at this stage.
        boolean isDone = false;
        int errorCounter = 0;
        if (searchUserAndDisplayContactDetailsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //Initialize Page Object
            PageFac_ContactDetail po1 = PageFactory.initElements(driver, PageFac_ContactDetail.class);

            if ((personalEmail != null) || (personalMobile != null) || (homeNumber != null)) {

                if (po1.displayEditPersonalContactForm(driver)) {
                    if (personalEmail != null) {
                        WebElement element = po1.text_EditPersonalEmail;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(personalEmail);
                        logMessage("Personal Email: " + personalEmail + " is input.");
                    }

                    if (personalMobile != null) {
                        WebElement element = po1.text_EditPersonalMobile;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(personalMobile);
                        logMessage("Personal Mobile: " + personalMobile + " is input.");
                    }

                    if (homeNumber != null) {
                        WebElement element = po1.text_EditHomeNumber;
                        element.click();
                        clearTextBox(element);
                        element.sendKeys(homeNumber);
                    }

                    logMessage("Screenshot before click Save button.");
                    logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                    if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                        po1.button_SavePersonalWorkContact.click();

                        if (waitChild("//form[@autocomplete='off' and contains(., 'Edit Personal Contact')]", 5, 1, driver) != null) {
                            logWarning("Personal Contact is not edited.");
                            po1.button_CloseEditPersonalWorkContact.click();
                            errorCounter++;
                            logMessage("Edit Personal Contact dialogue is closed.");
                        }
                    } else {
                        po1.button_CloseEditPersonalWorkContact.click();
                        logWarning("No change has been made.");
                    }

                    Thread.sleep(2000);
                } else {
                    errorCounter++;
                }
            }

            if ((workEmail != null) || (officeNumber != null) || (workMobile != null) || (actionMethod != null)) {
                po1.displayEditWorkContactForm(driver);
                if (workEmail != null) {

                    if (workEmail.equals("AUTO")) {
                        workEmail = generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                    }
                    WebElement element = po1.text_EditWorkEmail;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workEmail);
                }

                if (officeNumber != null) {
                    WebElement element = po1.text_EditOfficeNumber;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(officeNumber);
                }

                if (workMobile != null) {
                    WebElement element = po1.getText_EditWorkMobile;
                    element.click();
                    clearTextBox(element);
                    element.sendKeys(workMobile);
                }

                logMessage("Screenshot before click Save button.");
                logScreenshotElement(driver, po1.form_EditPersonalWorkContac);

                if (po1.button_SavePersonalWorkContact.isDisplayed() && (po1.button_SavePersonalWorkContact.isEnabled())) {
                    po1.button_SavePersonalWorkContact.click();
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    Thread.sleep(5000);

                    //Judge if the dialogue is still open
                    if (waitChild("//button[@type='submit' and text()='Save']", 5, 1, driver) != null) {
                        logWarning("Mandatory item is required. No change is made. Dialogue will be closed.");
                        po1.button_CloseEditPersonalWorkContact.click();
                        Thread.sleep(2000);
                        errorCounter++;
                    }

                } else {
                    po1.button_CloseEditPersonalWorkContact.click();
                    logWarning("No change has been made.");
                }
                GeneralBasic.waitSpinnerDisappear(60, driver);

                if (actionMethod != null) {
                    logMessage("Waiting for Pending Approval button...");
                    if (waitChild("//div[@class='display-area-workContact display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]", 120, 1, driver) != null) {
                        PageObj_ContactDetail.button_PendingApproval_ContactDetail_WorkContact(driver).click();
                        logMessage("Button Pending Approval is clicked in Work Contact panel.");
                        Thread.sleep(3000);
                        if (actionMethod.equals("1")) {
                            PageObj_ContactDetail.button_Approval_ContactDetail_WorkContact(driver).click();
                            logMessage("Approve button is clicked in Contact Detail - Work Contact panel.");
                        } else if (actionMethod.equals("2")) {
                            PageObj_ContactDetail.button_Decline_ContactDetail_WorkContact(driver).click();
                            logMessage("Decline button is clicked in Contact Detail - Work Contact panel.");
                        }
                    }
                }

                logMessage("Screen after editing Contact Details");
                logScreenshot(driver);
            }

            /////////////////////////////////////////////

            if ((residentialCountry != null) || (residentialAddress != null) || (residentialSuburb != null) || (residentialPostcode != null) || (residentialState != null) || (userForPostalAddress != null) || (postalCountry != null) || (postalAddress != null) || (postalSuburb != null) || (postalPostcode != null) || (postalState != null)) {
                if (!editContactDetails_Address(residentialCountry, residentialAddress, residentialSuburb, residentialPostcode, residentialState, userForPostalAddress, postalCountry, postalAddress, postalSuburb, postalPostcode, postalState, emergencyContactName, emergencyContactRelationship, emergencyContactMobilePhoneNumber, emergencyContactPhoneNumber, driver))
                    errorCounter++;
            }

            if ((emergencyContactName != null) || (emergencyContactRelationship != null) || (emergencyContactMobilePhoneNumber != null) || (emergencyContactPhoneNumber != null)) {

                po1.button_EditContactDetails_EmergencyContact.click();
                Thread.sleep(3000);
                waitSpinnerDisappear(120, driver);
                logMessage("Edit Emergency contact button is clicked.");
                logMessage("Screenshot before delete Emergency Contact.");
                logScreenshot(driver);

                po1.button_DeleteEmergencyContacts.click();
                Thread.sleep(2000);
                logMessage("Delete Button is clicked.");

                po1.getButton_DeleteEmergencyContacts_YesDelete.click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Confirm Yes Delete button is clicked.");

                logMessage("Screenshot after Emergency Contact is deleted.");
                logScreenshot(driver);

            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateRedirectedApprovers_ViaAdmin(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displaySettings_RedirectedApprovers(driver)) {
            if (expectedContent != null) {
                if (!validateTextValueContainedInElement(PageObj_RedirectedApprovers.form_RedirectedApproversList(driver), storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!validateTextValueInElement(PageObj_RedirectedApprovers.form_RedirectedApproversList(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateSearchResult(String keyword, String storeFileName, String isUpdate, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        searchUser(keyword, driver);

        if (!validateTextValueInElement(driver.findElement(By.xpath("//div[@class='page-row']")), storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
            errorCounter++;

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateDirectoryTable(String storeFileName, String isUpdate, String isCompare, String tabName, String calendarDate, String expectedContent, String leaveIconInCalendar, String storeFileName2, String expectedContent2, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        displayDashboard(driver);
        PageObj_Dashboard.panel_Directory(driver).click();
        logMessage("Directory panel is clicked in Dashborad page.");
        logScreenshot(driver);

        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if (tabName != null) {
            if (tabName.equals("Leave")) {
                driver.findElement(By.xpath("//button[contains(@class, 'button') and text()='Leave']")).click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Leave Tab is clicked.");
                logScreenshot(driver);
            }
        }

        if (calendarDate != null) {
            if (calendarDate.contains(";")) calendarDate = getExpectedDate(calendarDate, null);
            scrollDateInCalendar(calendarDate, driver);
            Thread.sleep(2000);
        }

        clickViewmoreButtonInTable(driver);
        logScreenshot(driver);

        ////////////////////////
        WebElement element = driver.findElement(By.xpath("//div[@class='panel-body']"));
        if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
            errorCounter++;

        if ((errorCounter == 0) && (leaveIconInCalendar != null)) {
            String[] strList = splitString(leaveIconInCalendar, ";");
            WebElement leaveIcon = null;

            String xpath_LeaveIcon = null;
            int totalIcon = strList.length;
            for (int i = 0; i < totalIcon; i++) {

                if (strList[i].contains(":")) {
                    String[] subList = splitString(strList[i], ":");
                    if (expectedContent != null) {
                        xpath_LeaveIcon = "//div[@class='list-item-row members-display-item leave single-line-medium' and contains(., '" + expectedContent + "')]//div[@class='tplr-day-display " + subList[0] + " " + subList[1] + "']//div[@class='segment-inner has-leave']";
                    } else {
                        xpath_LeaveIcon = "//div[@class='tplr-day-display " + subList[0] + " " + subList[1] + "']//div[@class='segment-inner has-leave']";
                    }
                } else {
                    if (expectedContent != null) {
                        xpath_LeaveIcon = "//div[@class='list-item-row members-display-item leave single-line-medium' and contains(., '" + expectedContent + "')]//div[@class='tplr-day-display " + strList[0] + "']//div[@class='segment-inner has-leave']";
                    } else {
                        xpath_LeaveIcon = "//div[@class='tplr-day-display " + strList[0] + "']//div[@class='segment-inner has-leave']";
                    }
                }

                /////////////////////
                List<WebElement> leaveIconListInCurrentCalendarView = null;
                int totalIconCount = 0;

                leaveIcon = waitChild(xpath_LeaveIcon, 2, 1, driver);
                if (leaveIcon != null) {
                    logMessage("Icon with xpath '" + xpath_LeaveIcon + "' is found.");
                    leaveIconListInCurrentCalendarView = driver.findElements(By.xpath(xpath_LeaveIcon));
                    totalIconCount = leaveIconListInCurrentCalendarView.size();
                    logMessage("The total number of Leave Icon found is " + totalIconCount + ".");

                    /////////////////// New Code Added below by Jim on 16/10/2019 ////////////////
                    if (storeFileName2 != null) {
                        boolean validatePopup = false;
                        for (int j = 0; j < totalIconCount; j++) {
                            Actions builder = new Actions(driver);
                            builder.moveToElement(leaveIconListInCurrentCalendarView.get(j)).build().perform();
                            logMessage("Waiting for Pop up...");
                            WebElement popoverFrom = waitChild("//div[@class='popover visible']", 20, 1, driver);
                            logScreenshot(driver);

                            if (popoverFrom != null) {
                                if (validateTextValueInWebElementInUse(popoverFrom, storeFileName2, isUpdate, isCompare, expectedContent2, testSerialNo, emailDomainName, driver)) {
                                    validatePopup = true;
                                    break;
                                } else {
                                    String attempt = String.valueOf(j + 1);
                                    logWarning("The attempt " + attempt + " is failed.");
                                }

                            } else {
                                logError("No Hover popup message is shown.");
                                errorCounter++;
                            }
                        }
                        if (validatePopup == false) errorCounter++;
                    }
                    ////// End of new Code /////////////
                } else {
                    logError("Icon with xpath '" + xpath_LeaveIcon + "' is NOT found.");
                    errorCounter++;
                }
            }

        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean updateEOPDateAsLastDayOfNextMonthInMicropayDB() throws Exception {
        boolean isUpdated = false;

        String outputDate = getTheLastDayOfMonth_OLD(getExpectedDate("1;MONTHS", null));
        int outputMonthValue = getMonthNumber(outputDate) - 1;

        String strSQL = "update PRPeriod set PeriodEnd='" + outputDate + "', iMonth=" + String.valueOf(outputMonthValue) + " where iPayFrequencyID=6 and IsProcessed=0";
        logMessage(strSQL);
        DBManage.sqlExecutor_WithCustomizedSQL_Main(131, 131, strSQL);

        logMessage("Update EOP Date as the last Day of Next Month in Micropay is completed.");

        return isUpdated;
    }

    public static boolean editRedirectApprovals(String firstName, String middleName, String lastName, String preferredName, String approverSearchKeyword, String approverFirstName, String approverMiddleName, String approverLastName, String approverPerferredName, String startDate, String endDate, String storeFileName, String isUpdateStore, String isCompare, String storeFileName2, String expectedMessageContain, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //actionMethod: 1- Add 2- Remove

        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            WebElement ellipsisButton = waitChild("//form[contains(., 'approvals are being redirected to')]//button", 5, 1, driver);
            if (ellipsisButton != null) {
                ellipsisButton.click();
                Thread.sleep(3000);
                logMessage("Ellipsis button is clicked with Screenshot. ");
                logScreenshot(driver);

                driver.findElement(By.xpath("//li/a[@href='javascript:;' and text()='Edit redirected approvals']")).click();
                Thread.sleep(3000);
                logMessage("Edit Redirected approbals menu is clicked.");

                if (waitChild("//form[contains(., 'Edit Redirect Approvals')]", 60, 1, driver) != null) {

                    if (storeFileName != null) {

                        WebElement elementMessageBox = waitChild("//div[@class='pending']", 5, 1, driver);
                        if (elementMessageBox != null)
                            if (!validateTextValueInElement(elementMessageBox, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                                errorCounter++;
                    }

                    WebElement element = driver.findElement(By.xpath("//input[@name='searchField']"));
                    element.click();
                    element.sendKeys(approverSearchKeyword);
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Approver Search Keyword '" + approverSearchKeyword + "' is input.");

                    if (waitChild("//span[@class='server-error-msg validation-error'][contains(text(),'No matches found')]", 10, 1, driver) != null) {
                        logError("Approver '" + approverSearchKeyword + "' is NOT found.");
                        logScreenshot(driver);
                        errorCounter++;

                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        Thread.sleep(2000);
                        logMessage("Close button is clicked.");
                    } else {
                        String approversFullName = null;
                        if (approverPerferredName != null) {
                            approversFullName = approverFirstName + " (" + approverPerferredName + ") " + approverLastName;
                        } else {
                            approversFullName = approverFirstName + " " + approverLastName;
                        }

                        WebElement listItem = waitChild("//div[@class='list-item' and contains(., '" + approversFullName + "')]", 5, 1, driver);
                        if (listItem != null) {
                            listItem.click();
                            logMessage("Approver '" + approversFullName + "' is selected.");

                            if (startDate != null) {
                                if (startDate.contains(";")) {
                                    startDate = getExpectedDate(startDate, null);
                                }

                                PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).click();
                                Thread.sleep(2000);
                                PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).sendKeys(Keys.DELETE);
                                PageObj_TeamsNRoles.textBox_StartDate_RedirectApproval(driver).sendKeys(startDate);
                                logMessage("Start Date '" + startDate + "' is input.");
                            }

                            if (endDate != null) {
                                if (endDate.contains(";")) {
                                    endDate = getExpectedDate(endDate, null);
                                }

                                PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).click();
                                Thread.sleep(2000);
                                PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).sendKeys(Keys.DELETE);
                                PageObj_TeamsNRoles.textBox_EndDate_RedirectApproval(driver).sendKeys(endDate);
                                logMessage("End date '" + endDate + "' is input.");
                            }

                            logMessage("Screenshot before click Done button.");
                            logScreenshot(driver);

                            driver.findElement(By.xpath("//button[@type='submit' and text()='Done']")).click();
                            logMessage("Add button is clicked.");
                            Thread.sleep(2000);
                            waitSpinnerDisappear(120, driver);

                            logMessage("Screenshot after clicking Done button.");
                            logScreenshot(driver);

                            //Validate message after add Redirect Approvals
                            if ((storeFileName2 != null) || (expectedMessageContain != null)) {
                                WebElement textArea = waitChild("//span[@class='heading-container-text']", 10, 1, driver);
                                if (textArea != null) {
                                    if (storeFileName2 != null) {
                                        if (expectedMessageContain != null) {
                                            if (!validateTextValueContainedInElement(textArea, storeFileName2, isUpdateStore, isCompare, expectedMessageContain, testSerialNo, emailDomainName))
                                                errorCounter++;
                                        } else {
                                            if (!validateTextValueInElement(textArea, storeFileName2, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                                                errorCounter++;
                                        }
                                    }
                                } else {
                                    logError("Messagebox is NOT found after Redirect Approvals.");
                                    errorCounter++;
                                }
                            }
                        } else {
                            logError("Approver '" + approversFullName + "' is NOT found.");
                            errorCounter++;

                            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                            Thread.sleep(2000);
                            logMessage("Close button is clicked.");
                        }
                    }
                }
            } else {
                logError("Existing Redirect Approval is NOT found.");
                errorCounter++;
            }


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean applyLeave(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBlanceButton, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            logMessage("Add Apply Leave button is clicked.");

            if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBlanceButton, driver)) {
                WebDriverWait wait = new WebDriverWait(driver, 120);
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObj_ApplyForLeave.button_Apply(driver)));

                PageObj_ApplyForLeave.button_Apply(driver).click();
                logMessage("Apply Leave button is clicked.");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(300, driver);
                Thread.sleep(10000);

                WebElement messagebox = waitChild("//div[@id='modal-content']//div//form", 120, 1, driver);
                logMessage("Screenshot of dialogue.");
                logScreenshot(driver);
                String currentMessageText = messagebox.getText();
                logMessage("Message from dialogue below:");
                System.out.println(currentMessageText);

                if (messageExpected != null) {
                    if (currentMessageText.contains(messageExpected)) {
                        logMessage("Message is shonw as expected.");
                    } else {
                        logError("Message is NOT shown as expected.");
                        logMessage("Expected Message is below:");
                        System.out.println(messageExpected);
                        errorCounter++;
                    }
                }

                WebElement button_OK = waitChild("//button[contains(@class,'button--primary') and text()='Ok']", 60, 1, driver);
                if (button_OK != null) {
                    //////////////////////
                    driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
                    logMessage("OK button is clicked");
                    Thread.sleep(20000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    //Wait for reload balance table, max waiting time 1 min
                    waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                    logMessage("Screenshot after click OK Button");
                    logScreenshot(driver);
                    //////
                } else {
                    logError("Leave confirmation dialogue is NOT shown.");
                    errorCounter++;
                    driver.findElement(By.xpath("//i[@class='icon-close']")).click();
                    logMessage("Apply for Leave form is closed.");
                    Thread.sleep(4000);
                }


                double d_currentLeaveBalannceTaken = 0;
                String strCurrentLeaveBalanceTaken = "";
                if (leaveType != null) {
                    currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                    logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                    d_currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                    strCurrentLeaveBalanceTaken = String.valueOf(d_currentLeaveBalannceTaken);
                    System.out.println(strCurrentLeaveBalanceTaken + " day(s) is taken.");
                    Thread.sleep(3000);
                    if (leaveTakenExpected != null) {

                        double d_leaveTakenExpected = Double.valueOf(leaveTakenExpected);
                        if (abs(d_currentLeaveBalannceTaken - d_leaveTakenExpected) < 0.3) {
                            logMessage("The day(s) of Leave Taken is as expected.");
                        } else {
                            logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                            errorCounter++;
                        }

                    }
                }


            } else {
                errorCounter++;
            }

        } else {
            errorCounter++;
        }

        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean applyLeave_OLD2(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBlanceButton, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");

            if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBlanceButton, driver)) {
                WebDriverWait wait = new WebDriverWait(driver, 60);
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObj_ApplyForLeave.button_Apply(driver)));

                PageObj_ApplyForLeave.button_Apply(driver).click();
                logMessage("Apply Leave button is clicked.");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(180, driver);
                Thread.sleep(10000);

                WebElement messagebox = waitChild("//div[@class='focus-trap-container']/form", 120, 1, driver);
                logMessage("Screenshot of dialogue.");
                logScreenshot(driver);
                String currentMessageText = messagebox.getText();
                logMessage("Message from dialogue below:");
                System.out.println(currentMessageText);

                if (messageExpected != null) {
                    if (currentMessageText.contains(messageExpected)) {
                        logMessage("Message is shonw as expected.");
                    } else {
                        logError("Message is NOT shown as expected.");
                        logMessage("Expected Message is below:");
                        System.out.println(messageExpected);
                        errorCounter++;
                    }
                }

                driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
                logMessage("OK button is clicked");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(120, driver);

                //Wait for reload balance table, max waiting time 1 min
                waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                logMessage("Screenshot after click OK Button");
                logScreenshot(driver);

                double currentLeaveBalannceTaken = 0;
                String strCurrentLeaveBalanceTaken = "";
                if (leaveType != null) {
                    currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                    logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                    currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                    strCurrentLeaveBalanceTaken = String.valueOf(currentLeaveBalannceTaken);
                    System.out.println(strCurrentLeaveBalanceTaken + " day(s) is taken.");
                    if (leaveTakenExpected != null) {
                        if (strCurrentLeaveBalanceTaken.contains(leaveTakenExpected)) {
                            logMessage("The day(s) of Leave Taken is as expected.");
                        } else {
                            logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                            errorCounter++;
                        }
                    }
                }


            } else {
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean applyLeave_OLD(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBlanceButton, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");

            if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBlanceButton, driver)) {
                WebDriverWait wait = new WebDriverWait(driver, 30);
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObj_ApplyForLeave.button_Apply(driver)));

                PageObj_ApplyForLeave.button_Apply(driver).click();
                logMessage("Apply Leave button is clicked.");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(180, driver);
                Thread.sleep(10000);

                WebElement messagebox = waitChild("//div[@class='focus-trap-container']/form", 120, 1, driver);
                logMessage("Screenshot of dialogue.");
                logScreenshot(driver);
                String currentMessageText = messagebox.getText();
                logMessage("Message from dialogue below:");
                System.out.println(currentMessageText);

                if (messageExpected != null) {
                    if (currentMessageText.contains(messageExpected)) {
                        logMessage("Message is shonw as expected.");
                    } else {
                        logError("Message is NOT shown as expected.");
                        logMessage("Expected Message is below:");
                        System.out.println(messageExpected);
                        errorCounter++;
                    }
                }

                driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
                logMessage("OK button is clicked");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(120, driver);

                //Wait for reload balance table, max waiting time 1 min
                waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                logMessage("Screenshot after click OK Button");
                logScreenshot(driver);

                double currentLeaveBalannceTaken = 0;
                if (leaveType != null) {
                    currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                    logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                    currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                    System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                    if (leaveTakenExpected != null) {
                        if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                            logMessage("The day(s) of Leave Taken is as expected.");
                        } else {
                            logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                            errorCounter++;
                        }
                    }
                }


            } else {
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean selectDateInCalendar(String calendarDate, WebDriver driver) throws InterruptedException, IOException {
        //Calendar control should be shown first.
        boolean isDone = false;
        int errorCounter = 0;

        if (calendarDate.contains(";")) {
            calendarDate = getExpectedDate(calendarDate, null);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(calendarDate, dtf);
        LocalDate currentDate = LocalDate.now();

        boolean isDateForward = true;

        Month month = localDate.getMonth();
        String strMonthFull = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String strMonthShort = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String strDay = String.valueOf(localDate.getDayOfMonth());
        String strYear = String.valueOf(localDate.getYear());

        String topCalendarDate = strDay + " " + strMonthShort + " " + strYear;
        String topMonthYear = strMonthFull + " " + strYear;

        if (!localDate.isEqual(currentDate)) {
            isDateForward = localDate.isAfter(currentDate);

            //judge if within the same month and year in calendar
            WebElement currentMonthHeader = driver.findElement(By.xpath("//div[@class='month-header']"));
            if (currentMonthHeader.getText().contains(topMonthYear)) {
                logMessage("Expected day is within current month.");
                driver.findElement(By.xpath("//div[@class='day-indicator' and text()='" + strDay + "']")).click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            } else {
                int counter = 0;
                WebElement buttonBackward = driver.findElement(By.xpath("//button[@class='button--plain add-button prev-selector']"));
                WebElement buttonForward = driver.findElement(By.xpath("//button[@class='button--plain add-button next-selector']"));

                while (!currentMonthHeader.getText().contains(topMonthYear)) {
                    if (isDateForward) {
                        buttonForward.click();
                    } else {
                        buttonBackward.click();
                    }
                    Thread.sleep(1000);
                    counter++;
                    if (counter > 100) {
                        errorCounter++;
                        break;
                    }
                }
                logMessage("Click Calendar " + counter + " times.");

                driver.findElement(By.xpath("//div[@class='day-indicator' and text()='" + strDay + "']")).click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            }
            logMessage("The " + calendarDate + " is selected.");
            logScreenshot(driver);
        } else {
            driver.findElement(By.xpath("//button[contains(text(),'Today')]")).click();
            Thread.sleep(2000);
            waitSpinnerDisappear(120, driver);
            logMessage("Today button is clicked in Calendar.");
            logScreenshot(driver);
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static LocalDate getCurrentDateInSchollCalendar(WebDriver driver) {
        //Scroll Calendar is shown first
        List<WebElement> monthYearList = driver.findElements(By.xpath("//div[@class='first-of-month']"));
        List<WebElement> dayList = driver.findElements(By.xpath("//div[@class='day-of-month']"));

        String currentMonthYear = monthYearList.get(0).getText();
        String currentDay = dayList.get(0).getText();
        //String currentDay="5";
        //String currentMonthYear="JUL 2018";

        String monthLowerCase = currentMonthYear.substring(1, 3).toLowerCase();
        String monthUperCase = currentMonthYear.substring(0, 1);
        String currentMonth = monthUperCase + monthLowerCase;
        String currentYear = currentMonthYear.substring(4, 8);

        String currentDate = null;
        currentDate = currentDay + " " + currentMonth + " " + currentYear;
        //Jim common on 04/02/2020
        /*if (currentMonth.equals("May")) {
            currentDate = currentDay + " " + currentMonth + " " + currentYear;
        } else {
            currentDate = currentDay + " " + currentMonth + ". " + currentYear;
        }*/

        System.out.println(currentDate);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM yyyy");
        LocalDate localDate = LocalDate.parse(currentDate, dtf);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("The current Date on Scoll Calendar is " + localDate.format(dtf2));
        return localDate;
    }

    public static boolean scrollDateInCalendar(String dateToBeShown, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDateToBeShown = LocalDate.parse(dateToBeShown, dtf);
        LocalDate currentCalendarStartDate = getCurrentDateInSchollCalendar(driver);
        //LocalDate currentCalendarEndDate=currentCalendarStartDate.plusDays(27);
        LocalDate currentCalendarEndDate = currentCalendarStartDate.plusDays(daysInCalendar);

        logMessage("The start Data of current calendar is " + currentCalendarStartDate.format(dtf));
        logMessage("The end Data of current calendar is " + currentCalendarEndDate.format(dtf));

        int dateDiff = localDateToBeShown.compareTo(currentCalendarStartDate);

        WebElement button_ScrollCalendarNext = driver.findElement(By.xpath("//div[contains(@class,'tlc-next')]//button"));
        WebElement button_ScrollCalendarPrevious = driver.findElement(By.xpath("//div[contains(@class,'tlc-previous')]//button"));

        int counter = 0;
        if ((localDateToBeShown.compareTo(currentCalendarStartDate) >= 0) && (localDateToBeShown.compareTo(currentCalendarEndDate) <= 0)) {
            logMessage("Date '" + dateToBeShown + "' is shown within calendar range.");
        } else {
            logMessage("Date '" + dateToBeShown + "' is NOT shown within the current calendar range.");
            boolean judge = !((localDateToBeShown.compareTo(currentCalendarStartDate) >= 0) && (localDateToBeShown.compareTo(currentCalendarEndDate) <= 0));
            while (judge) {
                if (localDateToBeShown.compareTo(currentCalendarStartDate) < 0) {
                    button_ScrollCalendarPrevious.click();
                    Thread.sleep(1000);
                } else if (localDateToBeShown.compareTo(currentCalendarEndDate) > 0) {
                    button_ScrollCalendarNext.click();
                    Thread.sleep(1000);
                }
                counter++;

                if (counter > 100) {
                    errorCounter++;
                    break;
                }


                currentCalendarStartDate = getCurrentDateInSchollCalendar(driver);
                //currentCalendarEndDate=currentCalendarStartDate.plusDays(27);
                currentCalendarEndDate = currentCalendarStartDate.plusDays(daysInCalendar);
                //judge= !((localDateToBeShown.compareTo(currentCalendarStartDate)>=0)&&(localDateToBeShown.compareTo(currentCalendarEndDate)<=0));
                judge = !((localDateToBeShown.compareTo(currentCalendarStartDate) >= 0) && (localDateToBeShown.compareTo(currentCalendarEndDate) <= 0));
            }

            logMessage("Click calendar " + String.valueOf(counter) + " time(s).");
            logScreenshot(driver);

        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateLeaveForecastInApplyLeaveDialogue(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String storeFileName, String isUpdate, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {

            PageObj_Leave.button_AddLeave(driver).click();
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");

            ////////////////////////////////
            if (startDate != null) {

                if (startDate.contains(";")) {
                    startDate = getExpectedDate(startDate, null);
                }

                //PageObj_ApplyForLeave.textBox_StartDate(driver).clear();
                PageObj_ApplyForLeave.textBox_StartDate(driver).click();
                Thread.sleep(3000);
                PageObj_ApplyForLeave.textBox_StartDate(driver).sendKeys(startDate);
                logMessage("Start Date: " + startDate + " is input.");
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            }


            if (endDate != null) {

                if (endDate.contains(";")) {
                    endDate = getExpectedDate(endDate, null);
                }

                //PageObj_ApplyForLeave.textBox_EndDate(driver).clear();
                PageObj_ApplyForLeave.textBox_EndDate(driver).click();
                Thread.sleep(2000);
                PageObj_ApplyForLeave.textBox_EndDate(driver).sendKeys(endDate);
                logMessage("End Date: " + endDate + " is input.");
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            }

            if (leaveHours != null) {
                clearTextBox(PageObj_ApplyForLeave.textBox_LeaveHours(driver));
                PageObj_ApplyForLeave.textBox_LeaveHours(driver).sendKeys(leaveHours);
                logMessage("Leave Hour: " + leaveHours + " is input.");
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            }

            if (leaveReason != null) {
                PageObj_ApplyForLeave.selectLeaveTypeInApplyLeaveScreen(leaveReason, driver);
                Thread.sleep(5000);
            }

            WebElement button_CheckBalance = waitChild("//button[@class='button--primary button--wide' and text()='Check balance']", 60, 1, driver);
            if (button_CheckBalance != null) {
                button_CheckBalance.click();
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
            }

            logMessage("Loading leave forecast balance...");
            waitElementInvisible(120, "//div[@class='row loading-msg' and contains(text(), 'Checking balance')]", driver);
            logScreenshot(driver);

            WebElement panel_LeaveBalanceOverview = driver.findElement(By.xpath("//div[@class='balance-overview']"));
            if (expectedTextContent != null) {
                if (!validateTextValueContainedInElement(panel_LeaveBalanceOverview, storeFileName, isUpdate, isCompare, expectedTextContent, testSerialNo, emailDomainName))
                    errorCounter++;
            } else {
                if (!validateTextValueInElement(panel_LeaveBalanceOverview, storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }


            driver.findElement(By.xpath("//i[@class='icon-close']")).click();
            Thread.sleep(1000);
            logMessage("Apply for Leave dialogue is closed.");

            /////////////

        } else {
            errorCounter++;
        }

        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean updatePublicHoliday_TNGFreeDayInMicropayDB() throws Exception {
        boolean isUpdated = false;

        String outputDate = getExpectedDate("2;MONTHS;1;MONDAY", null);
        String strSQL = "update _iptblPHPlannerDates set dDate=CONVERT(datetime, '" + outputDate + "', 103) where iPublicHolidayID in (select idPublicHoliday from _iptblPublicHoliday where cPublicHoliday like 'TNG Free Day')";
        logMessage(strSQL);
        DBManage.sqlExecutor_WithCustomizedSQL_Main(211, 211, strSQL);

        logMessage("Update EOP Date as the last Day of Next Month in Micropay is completed.");

        return isUpdated;
    }

    public static boolean editPendingLeave(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBalanceButton, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            /*
            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");
            */

            WebElement leaveItem = waitChild("//div[contains(@id, 'list-item-leave-app') and contains(., '" + leaveReason + "') and contains(., 'Pending Approval')]//button[contains(@class, 'button--plain add-button')]/i[@class='icon-options']", 2, 1, driver);
            if (leaveItem != null) {
                Thread.sleep(4000);
                logMessage("Leave Reason " + leaveReason + " with Pending Approval is found.");
                leaveItem.click();
                Thread.sleep(4000);
                logMessage("Leave Item '" + leaveReason + "' is clicked.");

                waitChild("//ul[@class='sub-nav show']/div/li[contains(., 'Edit leave')]", 4, 1, driver).click();
                Thread.sleep(4000);
                logMessage("Menu Edit leave is clicked.");

                if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBalanceButton, driver)) {
                    WebDriverWait wait = new WebDriverWait(driver, 30);
                    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObj_ApplyForLeave.button_Apply(driver)));

                    PageObj_ApplyForLeave.button_Apply(driver).click();
                    logMessage("Apply Leave button is clicked.");
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(180, driver);
                    Thread.sleep(10000);

                    WebElement messagebox = waitChild("//div[@id='modal-content']//div//form", 120, 1, driver);
                    logMessage("Screenshot of dialogue.");
                    logScreenshot(driver);
                    String currentMessageText = messagebox.getText();
                    logMessage("Message from dialogue below:");
                    System.out.println(currentMessageText);

                    if (messageExpected != null) {
                        if (currentMessageText.contains(messageExpected)) {
                            logMessage("Message is shonw as expected.");
                        } else {
                            logError("Message is NOT shown as expected.");
                            logMessage("Expected Message is below:");
                            System.out.println(messageExpected);
                            errorCounter++;
                        }
                    }

                    driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
                    logMessage("OK button is clicked");
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    //Wait for reload balance table, max waiting time 1 min
                    waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                    logMessage("Screenshot after click OK Button");
                    logScreenshot(driver);

                    double currentLeaveBalannceTaken = 0;
                    if (leaveType != null) {
                        currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                        logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                        currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                        System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                        if (leaveTakenExpected != null) {
                            if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                                logMessage("The day(s) of Leave Taken is as expected.");
                            } else {
                                logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                                errorCounter++;
                            }
                        }
                    }


                } else {
                    errorCounter++;
                }
            } else {
                logError("Leave Reason " + leaveReason + " with Pending Approval is NOT found.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean validateLeavePage_ViaAdminApprovals(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String messageExpected, String leaveTab, String leaveBalanceDate, String leaveTypeToBeExpand, String approvalArea, String leaveType, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //This funciton is used for Admin user only

        //Approvals Area: 1- My Approvals, 2- Other Approvals
        //leaveTypeToBeExpand: This is for Balance Paenl
        //leaveType: This is for pick up leave from approval page.

        boolean isPassed = false;
        int errorCounter = 0;

        //Judge if the leave is showing...
        String userFullName = null;
        if (preferredName != null) {
            userFullName = firstName + " (" + preferredName + ") " + lastName;
        } else {
            userFullName = firstName + " " + lastName;
        }

        if (approvalArea.equals("1")) {
            if (!displayMyApprovalsPage_ViaNavigationBar(driver)) ;
            errorCounter++;
        } else if (approvalArea.equals("2")) {
            if (!displayOtherApprovalsPage_ViaNavigationBar(driver)) errorCounter++;
        } else {
            errorCounter++;
        }
        logScreenshot(driver);

        WebElement leaveItemInApprovalPage = null;
        if (errorCounter == 0) {
            String leaveItemLink = "//div[contains(@id, 'list-item') and contains(., '" + userFullName + "') and contains(., 'Leave Application') and contains(., '" + leaveType + "')]//a[@class='link descriptions']";
            leaveItemInApprovalPage = waitChild(leaveItemLink, 10, 1, driver);
            if (leaveItemInApprovalPage != null) {

                moveToElementAndClick(leaveItemInApprovalPage, driver);

                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("User " + userFullName + "'s " + leaveType + " is found and clicked. ");

                logMessage("Screenshot of Leave Page.");
                logScreenshot(driver);

                if (leaveTab != null) {
                    WebElement leaveTabButton = null;
                    switch (leaveTab) {
                        case "Upcoming":
                            leaveTabButton = driver.findElement(By.xpath("//button[@id='button-leave-upcoming']"));
                            break;
                        case "Pending":
                            leaveTabButton = driver.findElement(By.xpath("//button[@id='button-leave-pending']"));
                            break;
                    }

                    leaveTabButton.click();
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Leave Tab '" + leaveTab + "' is clicked.");
                    logScreenshot(driver);
                }

                if (leaveBalanceDate != null) {
                    driver.findElement(By.xpath("//button[@id='balances-calendar-button']")).click();
                    Thread.sleep(3000);
                    logMessage("Calendar Header is clicked.");
                    logScreenshot(driver);

                    selectDateInCalendar(leaveBalanceDate, driver);
                }

                if (leaveTypeToBeExpand != null) {
                    WebElement leaveBalanceDetail = waitChild("//div[@id='leave-balances']//form/div[@class='le' and contains(., '" + leaveTypeToBeExpand + "')]", 2, 1, driver);
                    if (leaveBalanceDetail != null) {
                        leaveBalanceDetail.click();
                        Thread.sleep(2000);
                        logMessage("Leave '" + leaveTypeToBeExpand + "' balance panel is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("Leave '" + leaveTypeToBeExpand + "' balance panel is NOT found.");
                        errorCounter++;
                    }
                }

                if (messageExpected != null) {
                    if (!validateTextValueContainedInElement(PageObj_Leave.table_LeaveAll(driver), storeFileName, isUpdateStore, isCompare, messageExpected, testSerialNo, emailDomainName))
                        errorCounter++;
                } else {
                    if (!validateTextValueInElement(PageObj_Leave.table_LeaveAll(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                        errorCounter++;
                }
            } else {
                logError("User " + userFullName + "'s " + leaveType + " is NOT found.");
                errorCounter++;
            }
        }

        //////////////////////////////


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean changeDefaultApprovalTeam(String firstName, String middleName, String lastName, String preferredName, String defaultApprovalTeam, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            driver.findElement(By.xpath("//div[@id='teams-roles-more-options']//button[@class='button--plain add-button']")).click();
            Thread.sleep(3000);
            logMessage("Ellipsis menu button is clicked.");
            logScreenshot(driver);

            WebElement menu_ChangeDefaultApprovalTeam = waitChild("//ul[@id='teams-roles-sub-menu']/li/a[contains(., 'Change default approval team')]", 2, 1, driver);
            if (menu_ChangeDefaultApprovalTeam != null) {
                menu_ChangeDefaultApprovalTeam.click();
                logMessage("Menu 'Change Default approval team' is clicked.");
                Thread.sleep(2000);

                String radioButtonXpath = "//div[contains(@class, 'option-container radio') and contains(., '" + defaultApprovalTeam + "')]/input[@type='radio']";
                WebElement radioButton = waitChild(radioButtonXpath, 60, 1, driver);
                if (radioButton != null) {
                    tickCheckbox("1", radioButton, driver);
                    logMessage("Radio butotn '" + defaultApprovalTeam + "' is checked.");
                    logMessage("Screenshot before click Done button.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[@type='submit' and text()='Done']")).click();
                    Thread.sleep(2000);
                    logMessage("Done button is clicked.");
                } else {
                    logError("Radio butotn '" + defaultApprovalTeam + "' is NOT found.");
                    logScreenshot(driver);
                    driver.findElement(By.xpath("//i[@class='icon-close']")).click();
                    logMessage("Button close is clicked.");
                    errorCounter++;
                }
            } else {
                logError("Menu 'Change Default approval team' is NOT found.");
                errorCounter++;
            }


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean downloadRolesReport(String typeOfRolesReport, String storeFileName, String isUpdataFile, String isCompare, String expectedMessage, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isDone = false;
        int errorCounter = 0;
        logMessage("Delete all old roles reports in download folder.");
        deleteFilesInFolder(getGoogleChromeDownloadPath(), "roles-report");

        displaySettings_RolesPermissions(driver);
        String rolesReportName = "";

        switch (typeOfRolesReport) {
            case "All":
                rolesReportName = "all-roles-report-" + getCurrentDate2() + ".csv";
                driver.findElement(By.xpath("//div[@id='roles-page-controls']//button[@class='button--plain add-button']")).click();
                Thread.sleep(3000);
                logMessage("All Roles ellipsis button is clicked.");
                driver.findElement(By.xpath("//ul[@class='sub-nav show']//li[@id='all-roles-report']//a[@href='javascript:;']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("All Roles Report menu is clicked.");
                break;
            case "Administrator":
                rolesReportName = "administrator-role-report-" + getCurrentDate2() + ".csv";
                driver.findElement(By.xpath("//div[@id='list-section-role-admin']//div[@class='list-item-row']//button[@class='button--plain add-button']")).click();
                Thread.sleep(3000);
                logMessage("Administrator Roles ellipsis button is clicked.");
                driver.findElement(By.xpath("//div[@id='list-section-role-admin']//li[@id='all-roles-report']//a[@href='javascript:;']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Administrator Roles Report menu is clicked.");
                break;
            case "Manager":
                rolesReportName = "manager-role-report-" + getCurrentDate2() + ".csv";
                driver.findElement(By.xpath("//div[@id='list-section-role-manager']//div[@class='list-item-row']//button[@class='button--plain add-button']")).click();
                Thread.sleep(3000);
                logMessage("Manager Roles ellipsis button is clicked.");
                driver.findElement(By.xpath("//div[@id='list-section-role-manager']//li[@id='all-roles-report']//a[@href='javascript:;']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Mamager Roles Report menu is clicked.");
                break;
            case "Member":
                rolesReportName = "member-role-report-" + getCurrentDate2() + ".csv";
                driver.findElement(By.xpath("//div[@id='list-section-role-member']//div[@class='list-item-row']//button[@class='button--plain add-button']")).click();
                Thread.sleep(3000);
                logMessage("Member Roles ellipsis button is clicked.");
                driver.findElement(By.xpath("//div[@id='list-section-role-member']//li[@id='all-roles-report']//a[@href='javascript:;']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Member Roles Report menu is clicked.");
                break;
        }

        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + rolesReportName;


        String strNewDownloadFileFullPathName = workingFilePath + "all_roles_report" + getCurrentTimeAndDate() + ".txt";
        String strNewDowloadFileFullPathName_ReplaceSN = workingFilePath + "all_roles_report_ReplaceSN_" + getCurrentTimeAndDate() + ".txt";


        File file = new File(strCurrentDownloadFileFullPathName);
        int i = 0;
        while (!file.exists()) {
            i++;
            Thread.sleep(1000);
            if (i > 300) break;
        }

        if (i < 300) {
            logMessage("It takes " + i + " Seconds to download file.");
        } else {
            logError("Download file is overdue.");
            errorCounter++;
        }

        //Move download file into working folder
        if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);

        String contentInNewDowloadFile = getStingFromFile(strNewDownloadFileFullPathName);
        if (contentInNewDowloadFile.contains(testSerialNo)) {
            contentInNewDowloadFile = contentInNewDowloadFile.replace(testSerialNo, "SN123abc");
        }

        if (contentInNewDowloadFile.contains(emailDomainName)) {
            contentInNewDowloadFile = contentInNewDowloadFile.replace(emailDomainName, "sageautomation.com");
        }

        contentInNewDowloadFile = replaceItemInString(contentInNewDowloadFile, "F_SN123abc", 26, "F2345678901234567890123456");
        contentInNewDowloadFile = replaceItemInString(contentInNewDowloadFile, "L_SN123abc", 26, "L2345678901234567890123456");

        createTextFile(strNewDowloadFileFullPathName_ReplaceSN, contentInNewDowloadFile);
        logMessage("Roles Report: '" + rolesReportName + "' is download as '" + strNewDowloadFileFullPathName_ReplaceSN + "'.");

        if (!updateAndValidateStoreStringFile(isUpdataFile, isCompare, strNewDowloadFileFullPathName_ReplaceSN, storeFileName, expectedMessage))
            errorCounter++;

        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static boolean searchUserAndDisplayPersonalInformationPage_ViaAdmin_Performance(String keyword, String firstName, String middleName, String lastName, String preferredName, WebDriver driver) throws IOException, InterruptedException {
        //This function is only working when log on as Admin User.
        boolean isDisplay = false;
        int errorCounter = 0;

        String fullName = "";
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        if (searchUser(keyword, driver) > 0) {
            WebElement userNameLink = waitChild(fullName, 10, 5, driver);
            if (userNameLink != null) {
                userNameLink.click();
                GeneralBasic.waitSpinnerDisappear(30, driver);
                logMessage("Screenshot after click item.");
                logScreenshot(driver);
                //logDebug("//h3[contains(text(),'"+fullName+"')");
                if (waitChild("//h3[contains(text(),'" + fullName + "')]", 30, 1, driver) != null) {
                    waitChild("//div[@id='ea-name']//div[@class='display-field no-label']//dd[text()='" + firstName + "  " + lastName + "']", 15, 1, driver);
                    logMessage("User: " + fullName + "'s Personal Information Page is shown.");
                    isDisplay = true;
                } else {
                    logError("User: " + fullName + "'s Personal Information Page is NOT shown.");
                }
            } else {
                logError("User: '" + fullName + "' is NOT found.");
            }
        }

        return isDisplay;
    }


    public static int displayTeamsPage_Performance(WebDriver driver) throws InterruptedException, IOException {
        int isShown = 0;
        Thread.sleep(5000);
        PageObj_NavigationBar.teams(driver).click();
        logMessage("Navigation Bar - Team is clcked.");
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if (waitChild("//h3/span[text()='Teams']", 120, 1, driver) != null) {
            Thread.sleep(5000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Teams Page - Team List is shown.");

            isShown = 1;
        } else {
            logMessage("Teams page - Team Member is shown.");
            isShown = 2;
        }

        logMessage("Screenshot of Team Page.");
        logScreenshot(driver);

        return isShown;
    }

    public static boolean validatePermissionPanel(String roleName, String storeFileName, String isUpdateStore, String isCompare, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        String outputString = "";
        String currentString = "";

        displaySettings_RolesPermissions(driver);
        if (PageObj_Roles.displayRolesPermissionPage(roleName, driver)) {
            ////////////////////////////////
            outputString = "Header: \n";
            outputString = outputString + driver.findElement(By.xpath("//div[@id='role-details']")).getText() + "\n";
            outputString = outputString + "**************************\n";

            List<WebElement> panelList = driver.findElements(By.xpath("//div[@class='gc-grid-cell rp highlight narrow no-padding']"));
            int panelCount = panelList.size();
            logMessage("There are total " + panelCount + " Panels.");

            String xpath_PanelTitle = "";
            String xpath_PanelViewMode = "";
            String xpath_Panel = "";
            for (int a = 0; a < panelCount; a++) {
                xpath_Panel = getElementXPath(driver, panelList.get(a));
                xpath_PanelTitle = xpath_Panel + "//div[@class='rp-header']//div[@class='rp-title']";
                xpath_PanelViewMode = xpath_Panel + "//div[@class='rp-header']//div[@class='view-mode']/i";

                WebElement panel_Title = driver.findElement(By.xpath(xpath_PanelTitle));
                WebElement panel_ViewMode = driver.findElement(By.xpath(xpath_PanelViewMode));

                outputString = outputString + String.valueOf(a + 1) + ". " + panel_Title.getText() + ": " + panel_ViewMode.getAttribute("class") + "\n";
                outputString = outputString + "----------------------\n";

                List<WebElement> subItemList = driver.findElements(By.xpath(xpath_Panel + "//div[@class='rp-row']"));
                int subItemCount = subItemList.size();
                //logMessage("There are total "+subItemCount+" sub items.");

                String xpath_subitemTitle = "";
                String xpath_subitemViewMode = "";
                String xpath_subitemRow = "";

                for (int b = 0; b < subItemCount; b++) {
                    xpath_subitemRow = getElementXPath(driver, subItemList.get(b));
                    xpath_subitemTitle = xpath_subitemRow + "//div[@class='rp-title']";
                    xpath_subitemViewMode = xpath_subitemRow + "//div[@class='view-mode']/i";

                    WebElement subitem_Title = driver.findElement(By.xpath(xpath_subitemTitle));
                    WebElement subitem_ViewMode = driver.findElement(By.xpath(xpath_subitemViewMode));

                    outputString = outputString + String.valueOf(a + 1) + "." + String.valueOf(b + 1) + "." + subitem_Title.getText() + ": " + subitem_ViewMode.getAttribute("class") + "\n";

                }


                ////////////////////////////////
                List<WebElement> subsubItemList = driver.findElements(By.xpath(xpath_Panel + "//div[@class='rp-row sub']"));
                int subsubItemCount = subsubItemList.size();
                //logMessage("There are total "+subsubItemCount+" Subsub items.");

                if (subsubItemCount > 0) {
                    //////////
                    String xpath_subsubitemTitle = "";
                    String xpath_subsubitemViewMode = "";
                    String xpath_subsubitemRow = "";

                    for (int c = 0; c < subsubItemCount; c++) {
                        xpath_subsubitemRow = getElementXPath(driver, subsubItemList.get(c));
                        xpath_subsubitemTitle = xpath_subsubitemRow + "//div[@class='rp-title']";
                        xpath_subsubitemViewMode = xpath_subsubitemRow + "//div[@class='view-mode']/i";

                        WebElement subsubitem_Title = driver.findElement(By.xpath(xpath_subsubitemTitle));
                        WebElement subsubitem_ViewMode = driver.findElement(By.xpath(xpath_subsubitemViewMode));

                        outputString = outputString + subsubitem_Title.getText() + ": " + subsubitem_ViewMode.getAttribute("class") + "\n";

                    }
                    //////
                }


                ///////////
                outputString = outputString + "----------------------\n";
            }

            if (!validateStringFile(outputString, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean configPermissionStatusNew(String roleName, String itemName, String newStatus, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_RolesPermissions(driver);
        PageObj_Roles.displayRolesPermissionPage(roleName, driver);
        Thread.sleep(2000);
        String currentStatus = PageObj_Roles.getPermissionButtonNew(itemName, driver).getAttribute("class");
        if (currentStatus.contains(newStatus.toLowerCase())) {
            logWarning("The current Role '" + roleName + "' Permission '" + itemName + "' status is " + currentStatus + ". No change is make.");
        } else {

            WebElement permissionButton = PageObj_Roles.getPermissionButtonEditableNew(itemName, driver);
            if (permissionButton != null) {
                scrollToTop(driver);
                logMessage("Scholl to Top with Screenshot. ");
                logScreenshot(driver);
                displayElementInView(permissionButton, driver, -120);
                logMessage("Screenshot before click Permission button.");
                logScreenshot(driver);
                permissionButton.click();
                Thread.sleep(5000);
                logMessage("Permission Button is clicked.");

                logMessage("Screenshot after click Permission button.");
                logScreenshot(driver);

                driver.findElement(By.linkText(newStatus)).click();
                Thread.sleep(2000);
                logMessage("Screenshot after select option.");
                logScreenshot(driver);

                scrollToTop(driver);
                PageObj_Roles.button_Save(driver).click();
                logMessage("Save button is cliked.");
                Thread.sleep(4000);

                currentStatus = PageObj_Roles.getPermissionButtonNew(itemName, driver).getAttribute("class");

                if (currentStatus.contains(newStatus.toLowerCase())) {
                    logMessage("The Item '" + itemName + "' is changed as " + newStatus + " successfully.");
                } else {
                    logError("Failed change item '" + itemName + "' permission status.");
                    errorCounter++;
                }
            } else {
                logError("Permission Button for item '" + itemName + "' is NOT found. NO change made.");
                errorCounter++;
            }


        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }


    public static boolean duplicateRole(String roleNameToBeDuplicated, String duplicatedRoleName, String duplicatedRoleDescription, WebDriver driver) throws IOException, InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        displaySettings_RolesPermissions(driver);

        WebElement ellipsisButton = waitChild("//div[@class='list-item-row' and contains(., '" + roleNameToBeDuplicated + "')]//button[@class='button--plain add-button']", 10, 1, driver);

        if (ellipsisButton != null) {
            ellipsisButton.click();
            logMessage(roleNameToBeDuplicated + " Role ellipsis button is clicked.");
            WebElement menuItem = waitChild("//div[@class='list-item-row' and contains(., '" + roleNameToBeDuplicated + "')]//li[@id='duplicate-role']//a[@href='javascript:;']", 10, 1, driver);

            if (menuItem != null) {
                logMessage("Screenshot after click button before selct Duplicate menu.");
                logScreenshot(driver);
                menuItem.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage(roleNameToBeDuplicated + " role Duplicate menu is clicked.");
            } else {
                errorCounter++;
            }


        } else {
            errorCounter++;
        }

        if (waitChild("//h4[@class='mc-header-heading' and text()='New Role']", 60, 1, driver) != null) {
            logMessage("Duplicate New role form is shown");
            logScreenshot(driver);

            if (duplicatedRoleName != null) {
                WebElement textBox_name = driver.findElement(By.xpath("//input[@id='Name']"));
                clearTextBox(textBox_name);
                Thread.sleep(3000);
                textBox_name.sendKeys(duplicatedRoleName);
            }

            if (duplicatedRoleDescription != null) {
                WebElement textArea_Description = driver.findElement(By.xpath("//textarea[@id='Description']"));
                textArea_Description.sendKeys(Keys.TAB);
                clearTextBox(textArea_Description);
                Thread.sleep(3000);
                textArea_Description.sendKeys(duplicatedRoleDescription);
            }

            logMessage("Screenshot before click Add button.");
            logScreenshot(driver);
            driver.findElement(By.xpath("//button[@type='submit']")).click();
            Thread.sleep(3000);
            waitSpinnerDisappear(300, driver);
            logMessage("Add button is clicked.");
            logScreenshot(driver);
        } else {
            logError("Duplicate Role form is NOT shown.");
            errorCounter++;
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean removeMemberFromTeam(String teamName, String storeFileName, String isUpdateStore, String isCompare, String firstName, String middleName, String lastName, String preferredName, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isRemoved = false;
        int errorCounter = 0;

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        logMessage("Team member: '" + fullName + "' Ellipsis is clicked.");
        logScreenshot(driver);

        driver.findElement(By.linkText("Remove from team")).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        logMessage("Remove from team menu is clicked.");
        logScreenshot(driver);

        String text_MessageBox = "";
        int counter = 0;
        WebElement button_Submit = null;
        String xpath_Form = "//form[contains(., 'Remove " + fullName + " from " + teamName + "')]";
        logDebug("The xpath_Form is " + xpath_Form);
        WebElement form_MessageBox = waitChild(xpath_Form, 60, 1, driver);
        if (form_MessageBox == null) errorCounter++;

        while (form_MessageBox != null) {
            counter++;
            text_MessageBox = text_MessageBox + form_MessageBox.getText() + "\n";
            logScreenshot(driver);

            button_Submit = waitChild("//button[@type='submit']", 60, 1, driver);
            if (button_Submit != null) {
                button_Submit.click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                Thread.sleep(1000);
                logMessage("Butotn is clicked " + counter + " time(s).");
            }
            xpath_Form = "//form[contains(., 'Remove " + fullName + " from " + teamName + "')]";
            form_MessageBox = waitChild(xpath_Form, 60, 1, driver);
        }

        xpath_Form = "//form[contains(., 'Pending Approvals')]";
        form_MessageBox = waitChild(xpath_Form, 60, 1, driver);
        if (form_MessageBox != null) {
            text_MessageBox = text_MessageBox + form_MessageBox.getText() + "\n";
            button_Submit = waitChild("//button[@type='submit']", 60, 1, driver);
            if (button_Submit != null) {
                counter++;
                button_Submit.click();
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                ;
                logMessage("Butotn is clicked " + counter + " time(s).");
            }
        }

        if (storeFileName != null) {
            if (!validateStringFile(text_MessageBox, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo))
                errorCounter++;
        }

        if (errorCounter == 0) isRemoved = true;
        return isRemoved;
    }

    public static boolean addAdministratorRole(String firstName, String middleName, String lastName, String preferredName, String role, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        //actionMethod: 1- Add 2- Remove

        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            PageObj_TeamsNRoles.ellipsis_TeamsRolesMoreOption(driver).click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            PageObj_TeamsNRoles.ellipsis_Menue_AddAdministratorRole(driver).click();
            logMessage("Add Administrator role menu is clicked.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            if (waitChild("//form[contains(., 'Add Administrator Role')]", 60, 1, driver) != null) {
                if (role != null) {
                    driver.findElement(By.xpath("//div[@id='role']")).click();
                    Thread.sleep(3000);
                    logMessage("Role dropdown list is clicked.");

                    WebElement roleListItem = waitChild("//div[@class='select-list']/div[contains(@class, 'list-item') and contains(., '" + role + "')]", 60, 1, driver);
                    if (roleListItem != null) {
                        roleListItem.click();
                        logMessage("Role '" + role + "' is clicked.");
                    }
                }
                logMessage("Screenshot before click Add Role button.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//button[@type='submit' and text()='Add Role']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Role '" + role + "' is added.");
            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean changeMemberRole(String firstName, String middleName, String lastName, String preferredName, String role, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {

        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            driver.findElement(By.xpath("//div[@class='list-section-header' and contains(., 'Member role')]//button")).click();
            Thread.sleep(3000);
            logMessage("Member Role Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[text()='Change member role']")).click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Menu 'Change member role' is clicked.");

            WebElement popupForm = waitChild("//form[contains(., 'Change Member Role')]", 120, 1, driver);
            if (popupForm != null) {
                logMessage("Form 'Change Member Role' is shown.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//div[@id='role']")).click();
                Thread.sleep(3000);
                logMessage("Member Role dropdown list is clicked.");
                logScreenshot(driver);

                WebElement itemList = waitChild("//div[@class='list-item' and contains(text(),'" + role + "')]", 60, 1, driver);
                if (itemList != null) {
                    itemList.click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(60, driver);
                    logMessage("Member role '" + role + "' is clicked.");
                    logMessage("Screenshot before click Save button.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[@class='button button--primary' and  text()='Save']")).click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Save button is clicked.");
                } else {
                    errorCounter++;
                }

            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateIntegrationPage(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (GeneralBasic.displaySettings_General(driver)) {
            WebElement panelBody = waitChild("//div[@class='panel-body']", 10, 1, driver);
            if (!validateTextValueInWebElementInUse(panelBody, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateIntegrationPage_WebAPIConfigForm(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (GeneralBasic.displaySettings_General(driver)) {
            PageObj_General.button_AddAPIKey(driver).click();
            Thread.sleep(3000);
            logMessage("Add New Web API button is clicked.");
            logScreenshot(driver);
            WebElement form_WebAPIConfig = waitChild("//div[@id='modal-content']//div//form[contains(., 'New WebApi Configuration')]", 10, 1, driver);
            if (!validateTextValueInWebElementInUse(form_WebAPIConfig, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isPassed = true;

        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Web API Configuration close button is clicked.");
        return isPassed;
    }

    public static boolean activateUserAccount_WithClickActionButtonOnly_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String password, String testSerialNo, WebDriver driver) throws IOException, Exception {
        //This function only click the activation button without validate email.
        boolean isActivated = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            //judge Activate Button or Resend Activation Link
            if (waitChild("//button[text()=\"Activate\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_Acivate(driver).click();
                logMessage("Activate button is clicked in Account Setting screen.");
            } else if (waitChild("//button[text()=\"Resend Activation Link\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_ResendActivationLink(driver).click();
                logMessage("Resend Activation Link button is clicked in Account Setting screen.");
            } else {
                logWarning("User '" + firstName + " " + lastName + "' has been activated.");
            }

            Thread.sleep(10000);
            waitSpinnerDisappear(120, driver);

            logScreenshot(driver);

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isActivated = true;
        return isActivated;
    }

    public static boolean cancleActivateUserAccount_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String testSerialNo, WebDriver driver) throws IOException, Exception {
        //This function only click the activation button without validate email.
        boolean isActivated = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            WebElement button_CancelActivation = waitChild("//button[contains(@class,'button button--danger') and text()='Cancel Activation']", 10, 1, driver);

            if (button_CancelActivation != null) {
                button_CancelActivation.click();
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Cancel Activation button is clicked.");
            } else {
                logError("Cancel Activation button is NOT shown.");
                errorCounter++;
            }

            logScreenshot(driver);

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isActivated = true;
        return isActivated;
    }

    public static boolean activateUserAccountViaEmail(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdate, String isCompare, String expectedMessage, String expectedActivation, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws InterruptedException, IOException, MessagingException, SQLException, ClassNotFoundException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);

        //Get Activation link from email...
        String emailAccountName = userName.replace("@" + emailDomainName, "");
        String activtionLink = JavaMailLib.getActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName, url_ESS);

        if (activtionLink != null) {
            //Launch ESS User activation page
            logMessage("Start Launch ESS Activation Page.");
            WebDriver driver2 = launchWebDriver(activtionLink, 3);
            Thread.sleep(5000);
            logMessage("Screenshot before activation.");
            logScreenshot(driver2);

            WebElement lable_Username= waitChild("//div[@class='card-container']//p[contains(text(),'"+userName+"')]", 120, 1, driver2);
            if (lable_Username!=null){
                WebElement textBox_NewPassord=driver2.findElement(By.xpath("//input[@id='newPassword']"));
                textBox_NewPassord.clear();
                textBox_NewPassord.sendKeys(password);
                textBox_NewPassord.sendKeys(Keys.TAB);
                Thread.sleep(1000);
                logMessage("New Password '"+password+"' is input.");

                WebElement textBox_RePassword= driver2.findElement(By.xpath("//input[@id='reenterPassword']"));
                textBox_RePassword.clear();
                textBox_RePassword.sendKeys(password);
                textBox_RePassword.sendKeys(Keys.TAB);
                Thread.sleep(1000);
                logMessage("Reenter Password '"+password+"' is input.");

                logMessage("Screenshot after input password before click Sign up button.");
                logScreenshot(driver2);

                driver2.findElement(By.xpath("//button[@id='continue']")).click();
                Thread.sleep(6000);
                logMessage("Sign up button is clicked.");

                if (preferredName!=null){
                    //////////////////////////
                    if ((waitChild("//div[contains(text(),'Welcome "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+preferredName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+preferredName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+preferredName+"')]", 180, 1, driver2)!=null)){
                        logMessage("Activate user '"+firstName+" "+lastName+"' successfully.");
                        logScreenshot(driver2);
                        signoutESS(driver2);
                        logMessage("Signout ESS.");

                    }else{
                        logError("Failed Acitvating user '"+firstName+" "+lastName+"'.");
                        errorCounter++;
                    }
                    //////
                }else{
                    //////////////////////////
                   if ((waitChild("//div[contains(text(),'Welcome "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good morning "+firstName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good evening "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Good afternoon "+firstName+"')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Hello "+firstName+".')]", 180, 1, driver2)!=null)||(waitChild("//div[contains(text(),'Gooooood moooorning, "+firstName+"')]", 180, 1, driver2)!=null)){
                        logMessage("Activate user '"+firstName+" "+lastName+"' successfully.");
                        logScreenshot(driver2);
                        signoutESS(driver2);
                        logMessage("Signout ESS.");

                    }else{
                        logError("Failed Acitvating user '"+firstName+" "+lastName+"'.");
                        errorCounter++;
                    }
                    //////
                }

                logScreenshot(driver2);
                driver2.close();
                logMessage("Close Activation page.");
            }else{
                logError("User name '"+userName+"' is NOT Found.");
                errorCounter++;
            }



        } else {
            logError("Activation Link is NOT found.");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static boolean activateUserAccountViaEmail_OLD(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdate, String isCompare, String expectedMessage, String expectedActivation, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws InterruptedException, IOException, MessagingException, SQLException, ClassNotFoundException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);

        //Get Activation link from email...
        String emailAccountName = userName.replace("@" + emailDomainName, "");
        String activtionLink = JavaMailLib.getActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName, url_ESS);

        if (activtionLink != null) {
            //Launch ESS User activation page
            logMessage("Start Launch ESS Activation Page.");
            WebDriver driver2 = launchWebDriver(activtionLink, 3);

            logMessage("Screenshot before activation.");
            logScreenshot(driver2);

            WebElement panel = waitChild("//div[@id='panel']", 10, 1, driver2);
            if (!validateTextValueInWebElementInUse(panel, storeFileName, isUpdate, isCompare, expectedMessage, testSerialNo, emailDomainName, driver2))
                errorCounter++;

            WebElement button_SendVerificationCode = waitChild("//button[@class='button button--wide' and text()='Send verification code']", 60, 1, driver2);

            if (button_SendVerificationCode != null) {
                PageObj_ESSActivation.button_SendVerificationCode(driver2).click();
                logMessage("Send Verification Code button is clicked.");
                waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);
                PageObj_ESSActivation.button_OK_VerificationCodeEmailSent(driver2).click();
                logMessage("OK button is clicked after Verification Code Email is sent.");


                logMessage("Delay 45 seconds for email verification code...");
                Thread.sleep(45000);

                String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Verification Code", emailDomainName);
                if (verificationCode != null) {
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).click();
                    //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                    logMessage("Verification code is input");

                    PageObj_ESSActivation.textbox_Password(driver2).click();
                    //PageObj_ESSActivation.textbox_Password(driver2).clear();
                    PageObj_ESSActivation.textbox_Password(driver2).sendKeys(password);
                    logMessage("Password is input.");

                    logMessage("Log screenshot before click Continue button in Activate User Account screen.");
                    logScreenshot(driver2);

                    PageObj_ESSActivation.button_Continue(driver2).click();
                    logMessage("Continue button is clicked.");

                    GeneralBasic.waitSpinnerDisappear(120, driver2);

                    logMessage("Screenshot after click continue button.");
                    logScreenshot(driver2);
                } else {
                    errorCounter++;
                }

            } else {
                if (expectedActivation != null) {
                    if (expectedActivation.equals("1")) {
                        errorCounter++;
                    }
                } else {
                    errorCounter++;
                }

            }

            driver2.close();
            logMessage("Activation page is closed.");
            logMessage("End of Activating User '" + userName + " " + lastName + "'.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static boolean activateUserAccount_PasswordValidation_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, String password, String expectedValidationMessage, String emailDomainName, String testSerialNo, String url_ESS, WebDriver driver) throws IOException, Exception {
        //Validation only. No activation performed.
        boolean isPassed = false;
        int errorCounter = 0;
        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {

            //judge Activate Button or Resend Activation Link
            if (waitChild("//button[text()=\"Activate\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_Acivate(driver).click();
                logMessage("Activate button is clicked in Account Setting screen.");
            } else if (waitChild("//button[text()=\"Resend Activation Link\"]", 5, 1, driver) != null) {
                PageObj_AccountSettings.button_ResendActivationLink(driver).click();
                logMessage("Resend Activation Link button is clicked in Account Setting screen.");
            } else {
                logWarning("User '" + firstName + " " + lastName + "' has been activated.");
            }

            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);

            logMessage("Screenshot after click Activation button.");
            logScreenshot(driver);

            String strMessage = PageObj_AccountSettings.panel_AccountInformation(driver).getText();
            logMessage("The Account Information panel message is below");
            System.out.println(strMessage);

            if (strMessage.contains("The user could not be activated. A personal or work email is required")) {
                errorCounter++;
                logMessage("Failed activate user.");
            } else {
                String userName = driver.findElement(By.xpath("//div[@id='ea-username-password']//div[@class='display-field']/dd")).getText();

                //logMessage("Delay 45 Seconds for email sent...");
                //Thread.sleep(45000);

                //Get Activation link from email...
                String emailAccountName = userName.replace("@" + emailDomainName, "");
                String activtionLink = JavaMailLib.getActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName, url_ESS);

                if (activtionLink != null) {
                    //Launch ESS User activation page
                    logMessage("Start Launch ESS Activation Page.");
                    WebDriver driver2 = launchWebDriver(activtionLink, 3);
                    waitChild("//button[@class='button button--wide' and text()='Send verification code']", 120, 1, driver2);

                    logMessage("Screenshot before activation.");
                    logScreenshot(driver2);

                    PageObj_ESSActivation.button_SendVerificationCode(driver2).click();
                    logMessage("Send Verification Code button is clicked.");
                    waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);
                    PageObj_ESSActivation.button_OK_VerificationCodeEmailSent(driver2).click();
                    logMessage("OK button is clicked after Verification Code Email is sent.");


                    logMessage("Delay 45 seconds for email verification code...");
                    Thread.sleep(45000);

                    String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Verification Code", emailDomainName);
                    if (verificationCode != null) {
                        PageObj_ESSActivation.textbox_VerificationCode(driver2).click();
                        //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                        PageObj_ESSActivation.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                        logMessage("Verification code is input");

                        PageObj_ESSActivation.textbox_Password(driver2).click();
                        //PageObj_ESSActivation.textbox_Password(driver2).clear();
                        PageObj_ESSActivation.textbox_Password(driver2).sendKeys(password);
                        Thread.sleep(10000);
                        logMessage("Password is input.");
                        logMessage("Log screenshot before click Continue button in Activate User Account screen.");
                        logScreenshot(driver2);

                        if (expectedValidationMessage != null) {
                            logMessage("Expected Validation Message is '" + expectedValidationMessage + "'.");
                            String xpath = "//span[contains(text(), '" + expectedValidationMessage + "')]";

                            WebElement lable_ValidationMessage = waitChild(xpath, 10, 1, driver2);
                            if (lable_ValidationMessage != null) {
                                logMessage("Message is shown as expected.");
                            } else {
                                logError("Message is NOT shown as expected.");
                                errorCounter++;
                            }
                        }



                       /*PageObj_ESSActivation.button_Continue(driver2).click();
                        logMessage("Continue button is clicked.");

                        GeneralBasic.waitSpinnerDisappear(120, driver2);

                        logMessage("Screenshot after click continue button.");
                        logScreenshot(driver);*/
                    } else errorCounter++;

                    driver2.close();
                    logMessage("Activation page is closed.");
                    logMessage("End of Activating User '" + userName + " " + lastName + "'.");
                    isPassed = true;
                } else {
                    errorCounter++;
                }


            }


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;

    }

    public static boolean validateEmailNotificationIconInContactDetailPage(String firstName, String middleName, String lastName, String preferredName, String emailType, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //email Type 1- Personal Contact, 2- Work Contact
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Contact Details", testSerialNo, driver)) {
            WebElement emailIcon = null;

            switch (emailType) {
                case "1":
                    emailIcon = waitChild("//div[@id='ea-personal-contact']//div[@class='popover-wrapper']", 10, 1, driver);
                    break;
                case "2":
                    emailIcon = waitChild("//div[@id='ea-work-contact']//div[@class='popover-wrapper']", 10, 1, driver);
                    break;
            }

            emailIcon.click();
            Thread.sleep(3000);
            logMessage("Emal icon is clicked.");
            logScreenshot(driver);

            WebElement popup = waitChild("//div[@class='popover-content']", 10, 1, driver);
            if (popup != null) {
                if (!validateTextValueInWebElementInUse(popup, storeFileName, isUpdateStore, isCompare, null, testSerialNo, emailDomainName, driver))
                    errorCounter++;
            } else {
                logError("No popup after clicking Email Icon.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean searchAndDisplayUserPersonalInformationPageViaDirecotry_ViaAdmin(String firstName, String middleName, String lastName, String preferredName, WebDriver driver) throws Exception {
        //This function is only working when log on as Admin User.
        boolean isDisplay = false;
        int errorCounter = 0;

        String fullName = "";
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers("Directory", driver)) {
                errorCounter++;
            }
        }

        clickViewmoreButtonInTable(driver);
        WebElement link_UserName = waitChild("//a[contains(., '" + fullName + "')]", 10, 1, driver);
        if (link_UserName != null) {
            link_UserName.click();
            Thread.sleep(3000);
            logMessage("User '" + fullName + "' is found and clicked in Directory table.");

            GeneralBasic.waitSpinnerDisappear(30, driver);
            logMessage("Screenshot after click item.");
            logScreenshot(driver);
            //logDebug("//h3[contains(text(),'"+fullName+"')");
            if (waitChild("//h3[contains(text(),'" + fullName + "')]", 30, 1, driver) != null) {
                Thread.sleep(10000);
                logMessage("User: " + fullName + "'s Personal Information Page is shown.");
                isDisplay = true;
            } else {
                logError("User: " + fullName + "'s Personal Information Page is NOT shown.");
                errorCounter++;
            }

        } else {
            logError("User '" + fullName + "' is NOT found and clicked in Directory table.");
            errorCounter++;
        }


        if (errorCounter == 0) isDisplay = true;
        return isDisplay;
    }

    public static boolean selectSettingsMenu_Main(String subMenuName, WebDriver driver) throws InterruptedException, IOException {
        boolean isClicked = false;
        int errorCounter = 0;

        PageObj_NavigationBar.settings(driver).click();
        Thread.sleep(3000);
        logMessage("Setting menu is clicked.");
        logScreenshot(driver);

        WebElement menuLink = waitChild("//ul[@class='sub-nav show']//a[contains(text(),'" + subMenuName + "')]", 10, 1, driver);
        if (menuLink != null) {
            menuLink.click();
            Thread.sleep(15000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Menu item '" + subMenuName + "' is clicked.");
        } else {
            logError("Sub Menu '" + subMenuName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isClicked = true;
        return isClicked;
    }

    public static boolean resetPassword(String firstName, String middleName, String lastName, String preferredName, String password, String emailAddress, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS, WebDriver driver) throws IOException, Exception {
        //logon user first
        boolean isDone = false;
        int errorCounter = 0;

        if (searchUserAndDisplayAccountSettingsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            WebElement button_ResetPassword = waitChild("//button[contains(text(),'Reset Password')]", 10, 1, driver);
            if (button_ResetPassword != null) {
                button_ResetPassword.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Reset Password button is clicked.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//button[contains(@class,'button--primary') and contains(text(), 'Ok')]")).click();
                Thread.sleep(3000);
                logMessage("Reset Password OK button is clicked.");

                logMessage("Delay 45 Seconds for email reset password...");
                Thread.sleep(45000);

                if (emailAddress.equals("AUTO")) {
                    emailAddress = generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                }

                //Get Activation link from email...
                String emailAccountName = emailAddress.replace("@" + emailDomainName, "");

                String resetPasswordLink = JavaMailLib.getResetPasswordLink(emailAccountName, "Reset your password request", emailDomainName, url_ESS);
                if (resetPasswordLink != null) {

                    //Launch ESS User activation page
                    logMessage("Start Launch ESS Reset Password Page.");
                    WebDriver driver2 = launchWebDriver(resetPasswordLink, 3);
                    Thread.sleep(10000);
                    logMessage("Screenshot before clicking Send Verification Code.");
                    logScreenshot(driver2);

                    PageObj_ESSResetPassword.button_SendVerificationCode(driver2).click();
                    Thread.sleep(3000);
                    logMessage("Send Verification Code button is clicked.");

                    waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);

                    PageObj_ESSResetPassword.button_OK_VerificationCodeEmailSent(driver2).click();
                    logMessage("OK button is clicked after Verification Code Email is sent.");

                    logMessage("Delay 30 seconds for email sent...");
                    Thread.sleep(30000);

                    String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Verification Code", emailDomainName);

                    if (verificationCode != null) {

                        PageObj_ESSResetPassword.textbox_VerificationCode(driver2).click();
                        //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                        PageObj_ESSResetPassword.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                        logMessage("Verification code is input");

                        PageObj_ESSResetPassword.textbox_Password(driver2).click();
                        //PageObj_ESSActivation.textbox_Password(driver2).clear();
                        PageObj_ESSResetPassword.textbox_Password(driver2).sendKeys(password);
                        Thread.sleep(3000);
                        logMessage("Password: '" + password + "' is input.");
                        logScreenshot(driver2);

                        PageObj_ESSActivation.button_Continue(driver2).click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(120, driver2);
                        logMessage("Continue button is clicked.");
                        logScreenshot(driver);
                    }

                    driver2.close();
                    logMessage("Reset Your Password page is closed.");
                    logMessage("End of Reset Your Password for User '" + emailAddress + " " + lastName + "'.");
                    isDone = true;

                } else {
                    errorCounter++;
                }

            } else {
                errorCounter++;
                logError("Reset Password is NOT found.");
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editAccountSettings_NotificationEmail(String firstName, String middleName, String lastName, String preferredName, String chooseDefaultEmailNotification, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        // chooseDefaultEmailNotification: 1- Personal Email, 2- Work Email
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Account Settings", testSerialNo, driver)) {
            if (chooseDefaultEmailNotification != null) {
                ////////////////// Jim adjusted on 12/07/2021 ///////////////////
                WebElement icon_Edit=SystemLibrary.waitChild("//div[@id='ea-notifications']//span[@class='icon-right']", 60, 1, driver);
                if (icon_Edit!=null){
                    logMessage("The Edit Notification Panel and icone are displaying.");
                    logScreenshot(driver);

                    icon_Edit.click();
                    Thread.sleep(5000);
                    logMessage("The Edit Icon is clicked.");
                    logScreenshot(driver);

                    String xpath_RadioButton=null;
                    if (chooseDefaultEmailNotification.equals("1")){
                        xpath_RadioButton="//input[@id='WorkEmail']";
                        logMessage("Work Email is selected.");

                    }else if (chooseDefaultEmailNotification.equals("2")){
                        xpath_RadioButton="//input[@id='PersonalEmail']";
                        logMessage("Personal Email is selected.");
                    }

                    driver.findElement(By.xpath(xpath_RadioButton)).click();
                    Thread.sleep(5000);
                    logScreenshot(driver);

                    WebElement button_Save=driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
                    if (button_Save.isEnabled()){
                        driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click();
                        Thread.sleep(15000);
                        logMessage("Save button is clicked.");
                        logScreenshot(driver);
                    }else{
                        errorCounter++;
                        logWarning("Save button is NOT enabled. Not change is made.");
                        logScreenshot(driver);
                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        logMessage("Close button is clicked.");
                        logScreenshot(driver);
                    }



                }else{
                    errorCounter++;
                    logError("The Edit Notification Panel and Icon are not shown.");
                    logScreenshot(driver);
                }

                //////


            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean editAccountSettings_NotificationEmail_Debug(String firstName, String middleName, String lastName, String preferredName, String chooseDefaultEmailNotification, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        // chooseDefaultEmailNotification: 1- Personal Email, 2- Work Email
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Account Settings", testSerialNo, driver)) {
            if (chooseDefaultEmailNotification != null) {
                driver.findElement(By.xpath("//div[@id='ea-notifications']//span[@class='icon-right']")).click();
                if (waitChild("//form[contains(., 'Edit Notifications')]", 30, 1, driver) != null) {
                    logMessage("Screenshot before changing default notificaiton email.");
                    logScreenshot(driver);
                    if (chooseDefaultEmailNotification.equals("1")) {
                        tickCheckbox("1", driver.findElement(By.xpath("//input[@id='WorkEmail']")), driver);
                        logMessage("Wrok email is selected.");
                    } else if (chooseDefaultEmailNotification.equals("2")) {
                        tickCheckbox("1", driver.findElement(By.xpath("//input[@id='PersonalEmail']")), driver);
                        logMessage("Personal email is selected.");
                    }
                    logMessage("Screenshot after changing default notificaiton email.");
                    logScreenshot(driver);

                    WebElement button_Save = waitChild("//button[@type='submit']", 60, 1, driver);
                    if (button_Save.isEnabled()) {
                        button_Save.click();
                        Thread.sleep(2000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        logMessage("Save button is clicked.");
                    } else {
                        logError("No change is made.");
                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        Thread.sleep(2000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Button_Close is clicked.");
                        errorCounter++;
                    }

                } else {
                    errorCounter++;
                }
            }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean editAccountSettings_Username(String firstName, String middleName, String lastName, String preferredName, String newUsername, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        // chooseDefaultEmailNotification: 1- Personal Email, 2- Work Email
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Account Settings", testSerialNo, driver)) {
            if (newUsername != null) {
                if (newUsername.equals("AUTO")) {
                    newUsername = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                } else if (newUsername.equals("TEMP")) {
                    newUsername = firstName + "_" + lastName + testSerialNo;
                }

                ///////////////// Jim Adjusted on 13/07/2021 ////////////////////
                driver.findElement(By.xpath("//div[@id='ea-username-password']//span[@class='icon-right']")).click();
                Thread.sleep(3000);
                logMessage("Edit button is clicked.");

                if (SystemLibrary.waitChild("//h4[contains(text(),'Edit Sign In Details')]", 30, 1, driver)!=null){
                    WebElement textBox_Username=waitChild("//input[@id='username']", 30, 1, driver);
                    clearTextBox(textBox_Username);
                    textBox_Username.click();
                    textBox_Username.sendKeys(newUsername);
                    logMessage("New Username: " + newUsername + " is input.");
                    logMessage("Screenshot before clicking Save button.");
                    logScreenshot(driver);

                    WebElement button_Save=waitChild("//button[contains(text(),'Save')]", 10, 1, driver);
                    if (button_Save.isEnabled()){
                        driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click();
                        Thread.sleep(5000);
                        logMessage("Save button is clicked.");
                    }else{
                        logWarning("The Save button is disabled. No change is made.");
                        errorCounter++;
                        logScreenshot(driver);
                        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                        Thread.sleep(2000);
                        logScreenshot(driver);
                    }

                }else{
                    logError("Edit Sign in Details screen is NOT shonw.");
                    errorCounter++;
                }

                //////


            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean uploadFileInESS(String attachmentFileName, WebDriver driver) throws InterruptedException, IOException {
        //Open window is shown first
        boolean isDone = false;
        int errorCounter = 0;
        logMessage("Screenshot before uploading file.");
        logScreenshot(driver);

        String fileFullPath = dataSourcePath + attachmentFileName;
        Runtime.getRuntime().exec("C:\\TestAutomationProject\\ESSRegTest\\src\\AutoITScript\\UploadFileInESS.exe " + fileFullPath);
        Thread.sleep(30000);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateLeavePage_Log_OLD(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String messageExpected, String leaveTab, String leaveDate, String leaveStatus, String leaveReason, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        GeneralBasic.displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        //Judge if the leave is showing...

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String xpath_leaveItemXapth = null;
            xpath_leaveItemXapth = "//div[contains(@id, 'list-item-leave-app')]";
            if (leaveReason != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveReason + "')]");
            if (leaveStatus != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveStatus + "')]");
            if (leaveDate != null) {
                leaveDate = convertDateFormat_Pro(leaveDate, "E dd MMM yyyy");
                leaveDate = leaveDate.replace(".", "");
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveDate + "')]");
            }

            WebElement elementLeaveItem = waitChild(xpath_leaveItemXapth, 10, 1, driver);
            if (elementLeaveItem != null) {
                logMessage("Leave item " + leaveReason + " is found.");

                driver.findElement(By.xpath(xpath_leaveItemXapth + "//i[@class='icon-options']")).click();
                Thread.sleep(4000);
                logMessage("Leave Item " + leaveReason + " Ellipsis butotn is clicked.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//a[contains(text(), 'Log')]")).click();
                Thread.sleep(3000);
                logMessage("Menu log is clicked.");
                logScreenshot(driver);

                WebElement logDialogue = waitChild("//div[@id='status-log-modal' and contains(., 'Log')]", 10, 1, driver);
                if (logDialogue != null) {
                    if (!validateTextValueInWebElementInUse(logDialogue, storeFileName, isUpdateStore, isCompare, messageExpected, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                    driver.findElement(By.xpath("//i[@class='icon-close']")).click();
                    Thread.sleep(3000);
                    logMessage("Close Log button is clicked.");
                } else {
                    errorCounter++;
                    logError("Log dialogue is NOT shown.");
                }
            } else {
                logError("Leave item " + leaveReason + " is NOT found.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateLeavePage_Attachment(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String messageExpected, String leaveTab, String leaveDate, String leaveStatus, String leaveReason, String testSerialNo, WebDriver driver) throws Exception {
        GeneralBasic.displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        //Judge if the leave is showing...
        String userFullName = null;
        if (preferredName != null) {
            userFullName = firstName + " (" + preferredName + ") " + lastName;
        } else {
            userFullName = firstName + " " + lastName;
        }

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String xpath_leaveItemXapth = null;
            xpath_leaveItemXapth = "//div[contains(@id, 'list-item-leave-app')]";
            if (leaveReason != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveReason + "')]");
            if (leaveStatus != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveStatus + "')]");
            if (leaveDate != null) {

                if (leaveDate.contains(";")){
                    leaveDate= getExpectedDate(leaveDate, null);
                }

                leaveDate = convertDateFormat_Pro(leaveDate, "E d MMM yyyy");
                leaveDate = leaveDate.replace(".", "");
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveDate + "')]");
            }

            WebElement elementLeaveItem = waitChild(xpath_leaveItemXapth, 10, 1, driver);
            logDebug(xpath_leaveItemXapth);
            if (elementLeaveItem != null) {
                displayElementInView(elementLeaveItem, driver, 10);
                logMessage("Leave item " + leaveReason + " is found.");
                WebElement element_AttachmentIcon = waitChild(xpath_leaveItemXapth + "//i[@class='icon-attachments attachments-icon']", 5, 1, driver);
                if (element_AttachmentIcon != null) {
                    logMessage("Screenshot before click attachment icon.");
                    logScreenshot(driver);
                    if (!downloadAndValidateLeaveAttachmentFile(storeFileName, isUpdateStore, isCompare, messageExpected, element_AttachmentIcon))
                        errorCounter++;
                } else {
                    logError("Attachment Icon is NOT found.");
                    errorCounter++;
                }


            } else {
                logError("Leave item '" + leaveReason + "' is NOT found.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean downloadAndValidateLeaveAttachmentFile(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, WebElement attachmentIcon) throws InterruptedException, IOException {
        //Leave Item is select and download icon is shown.
        //Can only validate PDF file at this moment.
        boolean ispassed = false;
        int errorCounter = 0;

        String fileExtention = getFileExtentionName(storeFileName);

        String newFileName = storeFileName.replace("." + fileExtention, "");

        //Delete OLD download file in download folder first
        deleteFilesInFolder(getGoogleChromeDownloadPath(), newFileName);

        newFileName = newFileName + "_" + getCurrentTimeAndDate() + "." + fileExtention;

        String strNewDownloadFileFullPathName = workingFilePath + newFileName;
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + storeFileName;
        attachmentIcon.click();
        logMessage("Attachment Icon is clicked.");
        Thread.sleep(10000);


        //Move download file into working folder
        if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;

        logMessage("Leave Attachment '" + storeFileName + "' is download as '" + strNewDownloadFileFullPathName + ".'");
        System.out.println("Click the link to access the report: " + serverUrlAddress + "TestLog/WorkingFile/" + newFileName);

        if (isUpdateStore != null) {
            if (isUpdateStore.equals("1")) {
                if (!saveFileToStore(strNewDownloadFileFullPathName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare != null) {
            if (isCompare.equals("1")) {
                if (!comparePDFFile(strNewDownloadFileFullPathName, storeFilePath + storeFileName))
                    errorCounter++;
            }
        }


        if (errorCounter == 0) ispassed = true;
        return ispassed;
    }

    public static boolean validatePayAdvicePaySummaryPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String isExpandPayAdviceItem, String validatePaySummary, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Pay Advices & Summaries", testSerialNo, driver)) {
            clickViewmoreButtonInTable(driver);

            if (isExpandPayAdviceItem != null) {
                if (isExpandPayAdviceItem.equals("1")) {
                    WebElement icon_Expand = waitChild("//i[@class='icon-expand']", 10, 1, driver);
                    if (icon_Expand != null) {
                        icon_Expand.click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Icon Expand is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("The Pay Advice Icon Expand is NOT found.");
                    }
                } else if (isExpandPayAdviceItem.equals("2")) {
                    WebElement icon_Expand = waitChild("//i[@class='icon-collapse']", 10, 1, driver);
                    if (icon_Expand != null) {
                        icon_Expand.click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Icon collapse is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("The Pay Advice Icon collapse is NOT found.");
                    }
                }

            }

            if (validatePaySummary != null) {
                if (validatePaySummary.equals("1")) {
                    driver.findElement(By.xpath("//button[contains(text(),'Summaries')]")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Summaries Tab is clicked.");
                    logScreenshot(driver);
                }
            }

            if (!validateTextValueInWebElementInUse(PageObj_PayAdviceAndPaymentSummary.panel_PayAdvicesAndPaymentSummaries(driver), storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) {
                errorCounter++;
            }

        } else {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static String forcastLeave_OLD(String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String sHoursNotYetProcessed, String grantedImmediately) throws InterruptedException {
        String result = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        double leaveEntitlementsPerYearInDays = Double.parseDouble(sLeaveEntitlementsPerYearInDays);
        double contractHoursPerDay = Double.parseDouble(sContractHoursPerDay);
        double currentLeaveEntitlementHours = Double.parseDouble(sCurrentLeaveEntitlementHours);


        LocalDate lastLeaveEntitlementDate = LocalDate.parse(sLeaveEntitlementDate, dtf);

        if (sLeaveForecastDate.contains(";")) {
            sLeaveForecastDate = getExpectedDate(sLeaveForecastDate, null);
        }

        LocalDate leaveForecastDate = LocalDate.parse(sLeaveForecastDate, dtf);

        long daysToCalculate = ChronoUnit.DAYS.between(lastLeaveEntitlementDate, leaveForecastDate);
        double yearsToCalculate = Math.floor((double) (daysToCalculate / 365));

        /*
        System.out.println("Contract hours per day: "+contractHoursPerDay);
        System.out.println("Leave Entitlement Days Per Yea: "+leaveEntitlementsPerYearInDays);

        System.out.println("Current Leave Entitlement Hours: "+currentLeaveEntitlementHours);
        System.out.println("Current Leave Entilement Date: "+sLeaveEntitlementDate);
        System.out.println("Leave Forecast Date: "+sLeaveForecastDate);
        System.out.println("Days for calculation: "+Long.toString(daysToCalculate));
        System.out.println("Years for calculation: "+Double.toString(yearsToCalculate));
        */

        double leaveEntitlementPerYearInHours = leaveEntitlementsPerYearInDays * contractHoursPerDay;
        double accrualHours = 0;

        ////////////////////////
        if (granted != null) {
            if (granted.equals("1")) {
                logMessage("Leave is Granted.");
                accrualHours = leaveEntitlementPerYearInHours * yearsToCalculate;
            } else {
                accrualHours = (leaveEntitlementPerYearInHours / 365.25) * daysToCalculate;
            }
        } else {
            accrualHours = (leaveEntitlementPerYearInHours / 365.25) * daysToCalculate;
        }

        ///////////////

        if (grantedImmediately != null) {
            if (grantedImmediately.equals("1")) {
                logMessage("Leave is Granted immediately.");
                accrualHours = accrualHours + leaveEntitlementPerYearInHours;
            }
        }

        double totalAccrualHoursAsAtForecastDate = accrualHours + currentLeaveEntitlementHours;

        if (sHoursNotYetProcessed != null) {
            double hoursNotYetProcessed = Double.parseDouble(sHoursNotYetProcessed);
            totalAccrualHoursAsAtForecastDate = totalAccrualHoursAsAtForecastDate - hoursNotYetProcessed;
        }
        double totalAccrualDaysAsAtForecastDate = totalAccrualHoursAsAtForecastDate / contractHoursPerDay;

        /*
        System.out.println("Leave Entitlement per yesr in Hrs: "+leaveEntitlementPerYearInHours);
        System.out.println("Accrual Hours: "+accrualHours);
        System.out.println("Accrual Days: "+accrualHours/contractHoursPerDay);
        */

        logMessage("Total Expected Accrual Days as at Forecast Date: " + totalAccrualDaysAsAtForecastDate);
        logMessage("Total Expected Accrual Hours as at Forecast Date: " + totalAccrualHoursAsAtForecastDate);

        result = String.valueOf(totalAccrualDaysAsAtForecastDate) + ";" + String.valueOf(totalAccrualHoursAsAtForecastDate);
        return result;
    }

    public static boolean validateLeaveBalance_ViaLeavePage_OLD(String firstName, String middleName, String lastName, String preferredName, String leaveType, String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String expectedLeaveEntitlementDays, String expectedLeaveEntitlementHours, String sHoursNotYetProcessed, String grantedImmediately, String testSerialNo, String serverName, String payrollDBName, WebDriver driver) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        if (waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver) != null) {
            String userFullName = GeneralBasic.getUserFullname(firstName, middleName, lastName, preferredName);
            WebElement label_Name = waitChild("//h3[@class='bc-name' and contains(text(), '" + userFullName + "')]", 10, 1, driver);
            if (label_Name != null) {
                logMessage("User '" + firstName + "' leave page is shown.");
            } else {
                displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
            }
        } else {
            displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
        }

        if (sLeaveForecastDate != null) {
            driver.findElement(By.xpath("//button[@id='balances-calendar-button']")).click();
            Thread.sleep(3000);
            logMessage("Calendar Header is clicked.");
            logScreenshot(driver);

            selectDateInCalendar(sLeaveForecastDate, driver);
        }

        String currentLeaveEntitlementDays = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
        String currentLeaveEntitlementHours = PageObj_Leave.getLeaveBalanceHoursFromLeavePage(leaveType, driver);

        if ((expectedLeaveEntitlementDays == null) && (expectedLeaveEntitlementHours == null)) {
            String[] expectedLeaveResult = splitString(forcastLeave(firstName, lastName, sLeaveEntitlementsPerYearInDays, sContractHoursPerDay, granted, sCurrentLeaveEntitlementHours, sLeaveEntitlementDate, sLeaveForecastDate, sHoursNotYetProcessed, grantedImmediately, leaveType, serverName, payrollDBName), ";");
            expectedLeaveEntitlementDays = expectedLeaveResult[0];
            expectedLeaveEntitlementHours = expectedLeaveResult[1];
        } else {
            logMessage("The Total Expected Accrual Days as At Forecast Date: " + expectedLeaveEntitlementDays);
            logMessage("The total Expected Accrual Hours as at Forecast Date: " + expectedLeaveEntitlementHours);
        }

        double differenceInDays = Double.valueOf(currentLeaveEntitlementDays) - Double.valueOf(expectedLeaveEntitlementDays);


        logMessage("The leave difference in Days is " + differenceInDays);
        if (abs(differenceInDays) > 1.00) {
            errorCounter++;

            logError("The current " + leaveType + " balance in days is NOT shown as expected.");
        } else {
            logMessage("The current " + leaveType + " balance in days is shown as expected.");
        }

        double differenceInHours = Double.valueOf(currentLeaveEntitlementHours) - Double.valueOf(expectedLeaveEntitlementHours);
        logMessage("The leave difference in Hours is " + differenceInHours);
        if (!leaveType.equals("Long Service Leave")) {
            if (abs(differenceInHours) > 5.00) {
                errorCounter++;
                logError("The current " + leaveType + " balance in Hours is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveType + " balance in Hours is shown as expected.");
            }
        } else {
            if (abs(differenceInHours) > 90.00) {
                errorCounter++;

                logError("The current " + leaveType + " balance in Hours is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveType + " balance in Hours is shown as expected.");
            }
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateLeaveBalance_ViaDashboard(String firstName, String middleName, String lastName, String preferredName, String leaveType, String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String sHoursNotYetProcessed, String grantedImmediately, String serverName, String payrollDBName, WebDriver driver) throws IOException, InterruptedException, SQLException, ClassNotFoundException {

        boolean isPassed = false;
        int errorCounter = 0;

        displayDashboard(driver);
        String userFullName = GeneralBasic.getUserFullname(firstName, middleName, lastName, preferredName);
        WebElement label_Name = waitChild("//h4[contains(text(),'" + userFullName + "')]", 10, 1, driver);
        if (label_Name != null) {
            logMessage("User '" + firstName + "' Dashboard page is shown.");
        } else {
            logError("User '" + firstName + "' Dashboard page is NOT shown.");
        }

        String currentLeaveEntitlementDays = PageObj_Dashboard.getLeaveBalanceDaysFromDashboardPage(leaveType, driver);
        String currentLeaveEntitlementHours = PageObj_Dashboard.getLeaveBalanceHoursFromDashboardPage(leaveType, driver);

        String[] expectedLeaveResult = splitString(forcastLeave(firstName, lastName, sLeaveEntitlementsPerYearInDays, sContractHoursPerDay, granted, sCurrentLeaveEntitlementHours, sLeaveEntitlementDate, sLeaveForecastDate, sHoursNotYetProcessed, grantedImmediately, leaveType, serverName, payrollDBName), ";");

        String expectedLeaveEntitlementDays = expectedLeaveResult[0];
        String expectedLeaveEntitlementHours = expectedLeaveResult[1];

        double differenceInDays = Double.valueOf(currentLeaveEntitlementDays) - Double.valueOf(expectedLeaveEntitlementDays);
        logMessage("The leave difference in Days is " + differenceInDays);
        if (abs(differenceInDays) > 1) {
            errorCounter++;

            logError("The current " + leaveType + " balance in days is NOT shown as expected.");
        } else {
            logMessage("The current " + leaveType + " balance in days is shown as expected.");
        }

        double differenceInHours = Double.valueOf(currentLeaveEntitlementHours) - Double.valueOf(expectedLeaveEntitlementHours);
        logMessage("The leave difference in Hours is " + differenceInHours);
        if (abs(differenceInHours) > 7.5) {
            errorCounter++;

            logError("The current " + leaveType + " balance in Hours is NOT shown as expected.");
        } else {
            logMessage("The current " + leaveType + " balance in Hours is shown as expected.");
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateApprovalsPage(String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //This function is for non admin user
        boolean isPassed = false;
        int errorCounter = 0;

        displayApprovalsPage_ViaNavigationBar_NOTAdmin(driver);
        WebElement frame_Approvals = waitChild("//div[@class='panel-body']", 60, 1, driver);
        if (frame_Approvals != null) {
            if (!validateTextValueInWebElementInUse(frame_Approvals, storeFileName, isUpdateStore, isCompare, expectedTextContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateRedirectApprovalsForm(String firstName, String middleName, String lastName, String preferredName, String approverSearchKeyword, String approverFirstName, String approverMiddleName, String approverLastName, String approverPerferredName, String startDate, String endDate, String storeFileName, String isUpdateStore, String isCompare, String expectedMessageContain, String matchesFound, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            PageObj_TeamsNRoles.ellipsis_TeamsRolesMoreOption(driver).click();
            Thread.sleep(3000);
            logMessage("Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            PageObj_TeamsNRoles.ellipsis_Menue_RedirectApprovals(driver).click();
            logMessage("Redirect approvals menu is clicked.");
            logScreenshot(driver);

            WebElement form_AddRedirectApprovals = waitChild("//form[contains(., 'Add Redirect Approvals')]", 60, 1, driver);
            if (form_AddRedirectApprovals != null) {

                if (approverSearchKeyword != null) {
                    WebElement txtbox_searchName = driver.findElement(By.xpath("//input[@name='searchField']"));
                    txtbox_searchName.click();
                    txtbox_searchName.sendKeys(approverSearchKeyword);
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    WebElement lable_currentMatch = waitChild("//span[contains(text(),'No matches found')]", 10, 1, driver);
                    if (matchesFound != null) {
                        if (matchesFound.equals("1") && (lable_currentMatch != null)) {
                            errorCounter++;
                            logError("Unexpected Error: No matches found.");
                        } else if (matchesFound.equals("2") && (lable_currentMatch == null)) {
                            errorCounter++;
                            logError(("Unexpected Error: No matches found is NOT shown."));
                        }
                    }

                }

                if (storeFileName != null) {
                    if (!validateTextValueInWebElementInUse(form_AddRedirectApprovals, storeFileName, isUpdateStore, isCompare, expectedMessageContain, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                }


                logMessage("Screenshot before close Form.");
                logScreenshot(driver);
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                Thread.sleep(2000);
                logMessage("Button close is clicked.");
            } else {
                errorCounter++;
                logError("Add Redirect Approvals form is NOT shown.");
            }
        }
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateRedirectApprovalsMenu(String firstName, String middleName, String lastName, String preferredName, String expectedMenuItems, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            WebElement ellipsisButton_RedirectApprovals = waitChild("//form[contains(., 'approvals are being redirected to')]//button[@class='button--plain add-button']", 2, 1, driver);
            if (ellipsisButton_RedirectApprovals != null) {
                ellipsisButton_RedirectApprovals.click();
                Thread.sleep(2000);
                logMessage("Ellipsis button - Redirect Approvals is clicked. ");
                logScreenshot(driver);

                String[] expectedSubItem = splitString(expectedMenuItems, ";");
                int totalCount = expectedSubItem.length;

                for (int i = 0; i < totalCount; i++) {
                    if (waitChild("//li[contains(., '" + expectedSubItem[i] + "')]", 5, 1, driver) != null) {
                        logMessage("Menu '" + expectedSubItem[i] + "' is found.");
                    } else {
                        logError("Menu '" + expectedSubItem[i] + "' is NOT found.");
                        errorCounter++;
                    }
                }


            } else {
                errorCounter++;
                logError("Ellipsis Button - Redirect Approvals item is NOT found.");
            }

        } else {
            errorCounter++;

        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateLeavePage_Log(String firstName, String middleName, String lastName, String preferredName, String leaveDate, String leaveReason, String leaveType, String leaveStatus, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            //"//div[@class='list-item-row la multiline' and contains(., 'Pending Approval')]"
            String xpath_LeaveItem = "//div[@class='list-item-row la multiline'";

            if (leaveDate != null) {
                leaveDate = convertDateFormat_Pro(leaveDate, "E d MMM yyyy");
                leaveDate = leaveDate.replace(".", "");
                xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveDate + "')";
            }
            if (leaveStatus != null) xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveStatus + "')";
            if (leaveReason != null) xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveReason + "')";
            if (leaveType != null) xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveType + "')";
            xpath_LeaveItem = xpath_LeaveItem + "]";
            //button[@class='button--plain add-button']";
            logDebug("The xpath is " + xpath_LeaveItem);

            WebElement leaveItem = waitChild(xpath_LeaveItem, 10, 1, driver);
            if (leaveItem != null) {
                WebElement ellipsisButton = waitChild(xpath_LeaveItem + "//i[@class='icon-options']", 10, 1, driver);
                if (ellipsisButton != null) {
                    ellipsisButton.click();
                    Thread.sleep(5000);
                    logMessage("Ellipsis button is clicked.");
                    logScreenshot(driver);
                }

                String textMessage = "Leave Item";
                if (leaveReason != null) {
                    textMessage = textMessage + " '" + leaveReason + "' ";
                }
                if (leaveType != null) {
                    textMessage = textMessage + " '" + leaveType + "' ";
                }
                textMessage = textMessage + " is selected.";

                logMessage(textMessage);
                Thread.sleep(3000);
                WebElement menu_Log = waitChild("//ul[@class='sub-nav show']/div[contains(., 'Log')]", 30, 1, driver);
                displayElementInView(menu_Log, driver, 10);
                logScreenshot(driver);
                driver.findElement(By.xpath("//ul[@class='sub-nav show']/div[contains(., 'Log')]")).click();
                logMessage("Menu Log is clicked.");
                Thread.sleep(10000);

                WebElement dialogue_Log = waitChild("//div[@id='status-log-modal']", 60, 1, driver);
                if (dialogue_Log != null) {
                    logMessage("Log screen is shown.");
                    logScreenshot(driver);
                    if (!validateTextValueInWebElementInUse(dialogue_Log, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(2000);
                    logMessage("Log close button is clicked.");
                } else {
                    logError("Dialogue Log is NOT shown.");
                    errorCounter++;
                }
            } else {
                errorCounter++;
                logError("Leave Item '" + leaveReason + "' '" + leaveType + "' is NOT found.");
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateLeaveBalance_ViaApplyLeaveDialogue(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String expectedLeaveEntitlementDays, String expectedLeaveEntitlementHours, String clickCheckBlanceButton, String grantedImmediately, String testSerialNo, String serverName, String payrollDBName, WebDriver driver) throws IOException, InterruptedException, SQLException, ClassNotFoundException {

        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");
            logScreenshot(driver);

            String currentLeaveBalanceValueInHours = "";
            String currentLeaveBalanceValueInDays = "";

            Double expectedLeaveInHoursAfterInputLeave = 0.0;
            Double expectedLeaveInDaysAfterInputLeave = 0.0;

            if (expectedLeaveEntitlementHours!=null){
                expectedLeaveInHoursAfterInputLeave=Double.valueOf(expectedLeaveEntitlementHours);
            }

            if (expectedLeaveEntitlementDays!=null){
                expectedLeaveInDaysAfterInputLeave=Double.valueOf(expectedLeaveEntitlementDays);
            }


            if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, null, null, clickCheckBlanceButton, driver)) {

                currentLeaveBalanceValueInHours = PageObj_ApplyForLeave.getLeaveBalanceFromApplyLeaveFormInHours(leaveReason, driver);
                currentLeaveBalanceValueInDays = PageObj_ApplyForLeave.getLeaveBalanceFromApplyLeaveFormInDays(leaveReason, driver);

                PageObj_ApplyForLeave.button_Close(driver).click();
                Thread.sleep(3000);
                logMessage("Apply for leave Form is closed.");
            } else {
                errorCounter++;
            }

            ///////////////////////////////////////
            if ((expectedLeaveEntitlementDays == null) && (expectedLeaveEntitlementHours == null)) {
                String[] expectedLeaveResult = splitString(forcastLeave(firstName, lastName, sLeaveEntitlementsPerYearInDays, sContractHoursPerDay, granted, sCurrentLeaveEntitlementHours, sLeaveEntitlementDate, sLeaveForecastDate, null, grantedImmediately, leaveReason, serverName, payrollDBName), ";");
                expectedLeaveEntitlementDays = expectedLeaveResult[0];
                expectedLeaveEntitlementHours = expectedLeaveResult[1];

                expectedLeaveInHoursAfterInputLeave = Double.valueOf(expectedLeaveEntitlementHours) - Double.valueOf(leaveHours);
                expectedLeaveInDaysAfterInputLeave = Double.valueOf(expectedLeaveEntitlementDays) - Double.valueOf(leaveHours) / Double.valueOf(sContractHoursPerDay);


                logMessage("The expected Hours after input Leave: " + expectedLeaveInHoursAfterInputLeave);
                logMessage("The expected Days after input Leave: " + expectedLeaveInDaysAfterInputLeave);
            } else {
                logMessage("The Total Expected Accrual Days as At Forecast Date: " + expectedLeaveEntitlementDays);
                logMessage("The total Expected Accrual Hours as at Forecast Date: " + expectedLeaveEntitlementHours);
            }

            double differenceInDays = Double.valueOf(currentLeaveBalanceValueInDays) - expectedLeaveInDaysAfterInputLeave;
            logMessage("The leave difference in Days is " + differenceInDays);
            if (abs(differenceInDays) > 1) {
                errorCounter++;

                logError("The current " + leaveReason + " balance in days is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveReason + " balance in days is shown as expected.");
            }

            double differenceInHours = Double.valueOf(currentLeaveBalanceValueInHours) - expectedLeaveInHoursAfterInputLeave;
            logMessage("The leave difference in Hours is " + differenceInHours);
            if (abs(differenceInHours) > 4) {
                errorCounter++;

                logError("The current " + leaveReason + " balance in Hours is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveReason + " balance in Hours is shown as expected.");
            }


        }
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateLeavePage_Log_TempBackup_03122018(String firstName, String middleName, String lastName, String preferredName, String leaveDate, String leaveReason, String leaveType, String leaveStatus, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            //"//div[@class='list-item-row la multiline' and contains(., 'Pending Approval')]"
            String xpath_LeaveItemEllipsisButton = "//div[@class='list-item-row la multiline'";

            if (leaveDate != null) {
                leaveDate = convertDateFormat_Pro(leaveDate, "E d MMM yyyy");
                leaveDate = leaveDate.replace(".", "");
                xpath_LeaveItemEllipsisButton = xpath_LeaveItemEllipsisButton + " and contains(., '" + leaveDate + "')";
            }
            if (leaveStatus != null)
                xpath_LeaveItemEllipsisButton = xpath_LeaveItemEllipsisButton + " and contains(., '" + leaveStatus + "')";
            if (leaveReason != null)
                xpath_LeaveItemEllipsisButton = xpath_LeaveItemEllipsisButton + " and contains(., '" + leaveReason + "')";
            if (leaveType != null)
                xpath_LeaveItemEllipsisButton = xpath_LeaveItemEllipsisButton + " and contains(., '" + leaveType + "')";
            xpath_LeaveItemEllipsisButton = xpath_LeaveItemEllipsisButton + "]//button[@class='button--plain add-button']";
            logDebug("The xpath is " + xpath_LeaveItemEllipsisButton);

            WebElement ellipsisButton_LeaveItem = waitChild(xpath_LeaveItemEllipsisButton, 10, 1, driver);
            if (ellipsisButton_LeaveItem != null) {
                ellipsisButton_LeaveItem.click();
                logMessage("Leave Item '\"+leaveReason+\"' '\"+leaveType+\"' is clicked.");
                Thread.sleep(3000);
                logScreenshot(driver);
                driver.findElement(By.xpath("//ul[@class='sub-nav show']/div[contains(., 'Log')]")).click();
                logMessage("Menu Log is clicked.");

                WebElement dialogue_Log = waitChild("//div[@id='status-log-modal']", 30, 1, driver);
                if (dialogue_Log != null) {
                    logMessage("Log screen is shown.");
                    logScreenshot(driver);
                    if (!validateTextValueInWebElementInUse(dialogue_Log, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(2000);
                    logMessage("Log close button is clicked.");
                } else {
                    logError("Dialogue Log is NOT shown.");
                    errorCounter++;
                }

            } else {
                errorCounter++;
                logError("Leave Item '" + leaveReason + "' '" + leaveType + "' is NOT found.");
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateApplyForLeaveForm(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentFileName, String leaveComment, String storeFileName, String isUpdate, String isCompare, String expectedContent, String clickCheckBlanceButton, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        String resultString = "";

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentFileName, leaveComment, clickCheckBlanceButton, driver);

            resultString = resultString + "Start Date: " + PageObj_ApplyForLeave.textBox_StartDate(driver).getAttribute("value") + "\n";
            resultString = resultString + "End Date: " + PageObj_ApplyForLeave.textBox_EndDate(driver).getAttribute("value") + "\n";
            resultString = resultString + "Leave Hours : " + PageObj_ApplyForLeave.textBox_LeaveHours(driver).getAttribute("value") + "\n";
            resultString = resultString + "Leave Reason : " + driver.findElement(By.xpath("//div[@id='leaveReason']")).getText() + "\n";
            resultString = resultString + "Comment : " + PageObj_ApplyForLeave.textbox_comment(driver).getAttribute("value") + "\n";

            resultString = resultString + driver.findElement(By.xpath("//form[@id='leave-apply']")).getText() + "\n";

            if (PageObj_ApplyForLeave.frame_BalanceForecase(driver) != null) {
                resultString = resultString + "Balance Forecast : " + PageObj_ApplyForLeave.frame_BalanceForecase(driver).getText() + "\n";
            } else {
                resultString = resultString + "Balance Forecast: NOT available.\n";
            }

            if (expectedContent != null) {
                if (expectedContent.equals("TODAY")) {
                    expectedContent = getExpectedDate("0;DAYS", null);
                }
                if (!validateStringContainInFile(resultString, storeFileName, isUpdate, isCompare, expectedContent, emailDomainName, testSerialNo)) {
                    if (resultString.contains(expectedContent)) {
                        logMessage("The current String contain's expected content.");
                    } else {
                        errorCounter++;
                    }

                }
            } else {
                if (!validateStringFile(resultString, storeFileName, isUpdate, isCompare, emailDomainName, testSerialNo))
                    errorCounter++;
            }

            PageObj_ApplyForLeave.button_Close(driver).click();
            logMessage("Apply for leave form is closed.");
        } else {
            errorCounter++;
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static String getAdminPassword_FromTenantDetails(String testSerialNo) throws IOException, InterruptedException {
        String adminPassword = null;
        //List<String> tenantDetails=SystemLibrary.getTenantDetails("C:\\TestAutomationProject\\ESSRegTest\\UserList\\TenantDetails.txt");
        /////////////// Jim Adjust on 14/07/2021 //////////////////
        //List<String> tenantDetails = getTenantDetails(projectPath + "UserList\\TenantDetails_" + testSerialNo + ".txt");
        List<String> tenantDetails = getTenantDetails(testKeysPath + "TenantDetails_" + testSerialNo + ".txt");
        //////

        int totalLineNumber = tenantDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = tenantDetails.get(i);
            if (currentItem.contains("Admin Password =")) {
                adminPassword = currentItem.replace("Admin Password =", "");
                break;
            }
        }

        logMessage("The current Admin Password is '" + adminPassword + "'.");
        return adminPassword;
    }

    public static String getAdminUsername_FromTenantDetails(String testSerialNo) throws IOException, InterruptedException {
        String adminUsername = null;
        List<String> tenantDetails = getTenantDetails(projectPath + "UserList\\TenantDetails_" + testSerialNo + ".txt");

        int totalLineNumber = tenantDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = tenantDetails.get(i);
            if (currentItem.contains("Admin Login =")) {
                adminUsername = currentItem.replace("Admin Login =", "");
                break;
            }
        }

        logMessage("The current Admin Username is '" + adminUsername + "'.");
        return adminUsername;
    }

    public static String getTenantKey_FromTenantDetails() throws IOException, InterruptedException {
        String tenantKey = null;
        List<String> tenantDetails = getTenantDetails("C:\\TestAutomationProject\\ESSRegTest\\UserList\\TenantDetails.txt");

        int totalLineNumber = tenantDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = tenantDetails.get(i);
            if (currentItem.contains("Tenant Key = ")) {
                tenantKey = currentItem.replace("Tenant Key = ", "");
                break;
            }
        }

        logMessage("The current Tenant Key is '" + tenantKey + "'.");
        return tenantKey;
    }

    public static String getUsername_FromExtraAdminDetails() throws IOException, InterruptedException {
        String extraAdminUsername = null;
        List<String> extraAdminDetails = getStringListFromFile("C:\\TestAutomationProject\\ESSRegTest\\UserList\\ExtraAdminUser.txt");

        int totalLineNumber = extraAdminDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = extraAdminDetails.get(i);
            if (currentItem.contains("Username : ")) {
                extraAdminUsername = currentItem.replace("Username : ", "");
                break;
            }
        }

        logMessage("The current Extra Admin Username is '" + extraAdminUsername + "'.");
        return extraAdminUsername;
    }

    public static String getPassword_FromExtraAdminDetails() throws IOException, InterruptedException {
        String extraAdminPassword = null;
        List<String> extraAdminDetails = getStringListFromFile("C:\\TestAutomationProject\\ESSRegTest\\UserList\\ExtraAdminUser.txt");

        int totalLineNumber = extraAdminDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = extraAdminDetails.get(i);
            if (currentItem.contains("Password : ")) {
                extraAdminPassword = currentItem.replace("Password : ", "");
                break;
            }
        }

        logMessage("The current Extra Admin Password is '" + extraAdminPassword + "'.");
        return extraAdminPassword;
    }

    public static String getFirstname_FromExtraAdminDetails() throws IOException, InterruptedException {
        String firstName = null;
        List<String> extraAdminDetails = getStringListFromFile("C:\\TestAutomationProject\\ESSRegTest\\UserList\\ExtraAdminUser.txt");

        int totalLineNumber = extraAdminDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = extraAdminDetails.get(i);
            if (currentItem.contains("Firstname : ")) {
                firstName = currentItem.replace("Firstname : ", "");
                break;
            }
        }

        logMessage("The current Extra Admin Firstname is '" + firstName + "'.");
        return firstName;
    }

    public static String getLastname_FromExtraAdminDetails() throws IOException, InterruptedException {
        String lastName = null;
        List<String> extraAdminDetails = getStringListFromFile("C:\\TestAutomationProject\\ESSRegTest\\UserList\\ExtraAdminUser.txt");

        int totalLineNumber = extraAdminDetails.size();
        for (int i = 0; i < totalLineNumber; i++) {
            String currentItem = extraAdminDetails.get(i);
            if (currentItem.contains("Lastname : ")) {
                lastName = currentItem.replace("Lastname : ", "");
                break;
            }
        }

        logMessage("The current Extra Admin Lastname is '" + lastName + "'.");
        return lastName;
    }


    public static boolean validateMemberRoleListInChangeMemberRoleDialogue(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdate, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            driver.findElement(By.xpath("//div[@class='list-section-header' and contains(., 'Member role')]//button")).click();
            Thread.sleep(3000);
            logMessage("Member Role Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[text()='Change member role']")).click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Menu 'Change member role' is clicked.");

            WebElement popupForm = waitChild("//form[contains(., 'Change Member Role')]", 120, 1, driver);
            if (popupForm != null) {
                logMessage("Form 'Change Member Role' is shown.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//div[@id='role']")).click();
                Thread.sleep(3000);
                logMessage("Member Role dropdown list is clicked.");
                logScreenshot(driver);

                WebElement memberRoleList = waitChild("//div[@class='select-list']", 10, 1, driver);
                if (memberRoleList != null) {
                    if (!validateTextValueInElement(memberRoleList, storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                        errorCounter++;
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(3000);
                    logMessage("Change Member Role dialogue is closed.");
                } else {
                    logError("Member Role list is NOT shown.");
                    errorCounter++;
                }

            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean removeAdministratorRole(String firstName, String middleName, String lastName, String preferredName, String role, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        //actionMethod: 1- Add 2- Remove

        boolean isDone = false;
        int errorCounter = 0;

        if (firstName.contains("ExtraAdminUser")) {
            firstName = getFirstname_FromExtraAdminDetails();
            lastName = getLastname_FromExtraAdminDetails();
        } else if (firstName.contains("Test.Admin")) {
            firstName = getAdminUsername_FromTenantDetails(testSerialNo);
            lastName = "Last";

        }


        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            WebElement ellipsisButton = waitChild("//div[@class='list-item-row' and contains(., 'Administrator')]//button[@class='button--plain add-button']", 10, 1, driver);
            if (ellipsisButton != null) {
                ellipsisButton.click();
                Thread.sleep(3000);
                logMessage("Administrator Ellipsis button is clicked.");
                logScreenshot(driver);

                WebElement ellipsisMenu_RemoveAdminRole = waitChild("//li[@class='dangerous' and contains(., 'Remove Administrator role')]", 10, 1, driver);
                if (ellipsisMenu_RemoveAdminRole != null) {
                    ellipsisMenu_RemoveAdminRole.click();
                    Thread.sleep(3000);
                    logMessage("Ellipsis Menu - Remove Administrator Role is clicked.");
                }

                WebElement dialogue_DeleteAdministratorRole = waitChild("//div[@class='focus-trap-container' and contains(., 'Delete Administrator Role')]", 60, 1, driver);
                logScreenshot(driver);
                if (dialogue_DeleteAdministratorRole != null) {
                    if (!dialogue_DeleteAdministratorRole.getText().contains("Are you sure you want to remove")) {
                        logError("Message 'Are you sure you want to remove' is NOT shown.");
                        errorCounter++;
                    }

                    driver.findElement(By.xpath("//button[contains(@class,'button--danger')]")).click();
                    Thread.sleep(5000);
                    logMessage("Button - Yes, remove role is clicked.");
                    logScreenshot(driver);
                } else {
                    logError("The Dialogue of Delete Administrator Role is NOT shown.");
                    errorCounter++;
                }
            } else {
                logError("Administrator Ellipsis button is NOT shown.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean approveContactDetailsChanges(String firstName, String middleName, String lastName, String preferredName, String panelName, String actionMethod, String declineComment, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isDone = false;
        int errorCounter = 0;

        String xpath_Panel = null;
        String xpath_Button_PendingApproval = null;
        String xpath_Button_Approve = null;
        String xpath_Button_Decline = null;

        if (searchUserAndDisplayContactDetailsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver) && (actionMethod != null)) {
            switch (panelName) {
                case "PERSONAL CONTACT":
                    xpath_Panel = getElementXPath(driver, PageObj_ContactDetail.panel_EmergencyContact(driver));
                    break;
                case "WORK CONTACT":
                    xpath_Panel = getElementXPath(driver, PageObj_ContactDetail.panel_WorkContact(driver));
                    break;
                case "ADDRESS":
                    xpath_Panel = getElementXPath(driver, PageObj_ContactDetail.panel_Address(driver));
                    break;
                case "EMERGENCY CONTACT":
                    xpath_Panel = getElementXPath(driver, PageObj_ContactDetail.panel_EmergencyContact(driver));
                    break;
            }
            xpath_Button_PendingApproval = xpath_Panel + "//button[contains(@class,'pending-button button')]";
            xpath_Button_Approve = xpath_Panel + "//button[contains(@class,'approve-button button--success')]";
            xpath_Button_Decline = xpath_Panel + "//button[contains(@class,'decline-button button--danger')]";

            WebElement button_PendingApproval = driver.findElement(By.xpath(xpath_Button_PendingApproval));
            logMessage("Screenshot before click Pending Approval button.");
            logScreenshot(driver);

            if (button_PendingApproval.isEnabled()) {
                button_PendingApproval.click();
                logMessage("Pending Approval button is clicked.");
                Thread.sleep(3000);

                if (actionMethod.equals("1")) {
                    logMessage("Screenshot before click Approve button.");
                    logScreenshot(driver);
                    driver.findElement(By.xpath(xpath_Button_Approve)).click();
                    logMessage("Approve button is click.");
                } else if (actionMethod.equals("2")) {
                    driver.findElement(By.xpath(xpath_Button_Decline)).click();
                    logMessage("Decline button is click.");
                    Thread.sleep(3000);

                    driver.findElement(By.xpath("//textarea[@id='comments']")).sendKeys(declineComment);
                    logMessage("Screenshot before click Decline button.");
                    driver.findElement(By.xpath("//form[contains(., 'Add comment')]//button[@type='submit']")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                }
            } else {
                logError("Pending approval button is disabled.");
                errorCounter++;
            }


        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validatePersonalInformation_WithoutSearch(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        displayDashboard(driver);

        driver.findElement(By.xpath("//div[@id='profile-widget']")).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Name panel in Dashboard screen is clicked.");
        logScreenshot(driver);

        if (!validateTextValueInElement(PageObj_PersonalInfo.table_PersonalInformationAll(driver), storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) {
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean validate_AccountSettings_Page_WtihoutSearch(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        displayDashboard(driver);

        driver.findElement(By.xpath("//div[@id='profile-widget']")).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Name panel in Dashboard screen is clicked.");
        logScreenshot(driver);

        WebElement sideMenu_AccountSettings = waitChild("//span[contains(text(),'Account Settings')]", 120, 1, driver);
        if (sideMenu_AccountSettings != null) {
            sideMenu_AccountSettings.click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Sidemenu - Account Setttings is clicked.");
            logScreenshot(driver);

            WebElement masterFrame = driver.findElement(By.xpath("//div[@class='master-detail-content']"));
            if (!validateTextValueInWebElementInUse(masterFrame, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;

        } else {
            logError("Account Settings side menu is NOT shown.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validate_TeamsAndRoles_WithoutSearch(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedMessage, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        displayDashboard(driver);

        driver.findElement(By.xpath("//div[@id='profile-widget']")).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Name panel in Dashboard screen is clicked.");
        logScreenshot(driver);

        WebElement sideMenu_TeamsAndRoles = waitChild("//span[contains(text(),'Teams & Roles')]", 120, 1, driver);
        if (sideMenu_TeamsAndRoles != null) {
            sideMenu_TeamsAndRoles.click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            Thread.sleep(3000);
            logMessage("Sidemenu - Teams & Roles is clicked.");
            logScreenshot(driver);

            WebElement masterFrame = driver.findElement(By.xpath("//div[@class='master-detail-content']"));
            if (!validateTextValueInElement(masterFrame, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName))
                errorCounter++;

        } else {
            logError("Teams and Roless side menu is NOT shown.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean validatePayAdvicePaySummaryPage_WithoutSearch(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String isExpandPayAdviceItem, String validatePaySummary, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = true;
        int errorCounter = 0;

        displayDashboard(driver);

        driver.findElement(By.xpath("//div[@id='profile-widget']")).click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Name panel in Dashboard screen is clicked.");
        logScreenshot(driver);

        WebElement sideMenu_TeamsAndRoles = waitChild("//span[contains(text(),'Pay Advices & Summaries')]", 120, 1, driver);
        if (sideMenu_TeamsAndRoles != null) {
            sideMenu_TeamsAndRoles.click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Sidemenu - Pay Advice and Summaries is clicked.");
            clickViewmoreButtonInTable(driver);
            logScreenshot(driver);

            if (isExpandPayAdviceItem != null) {
                if (isExpandPayAdviceItem.equals("1")) {
                    WebElement icon_Expand = waitChild("//i[@class='icon-expand']", 10, 1, driver);
                    if (icon_Expand != null) {
                        icon_Expand.click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Icon Expand is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("The Pay Advice Icon Expand is NOT found.");
                    }
                } else if (isExpandPayAdviceItem.equals("2")) {
                    WebElement icon_Expand = waitChild("//i[@class='icon-collapse']", 10, 1, driver);
                    if (icon_Expand != null) {
                        icon_Expand.click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(60, driver);
                        logMessage("Icon collapse is clicked.");
                        logScreenshot(driver);
                    } else {
                        logError("The Pay Advice Icon collapse is NOT found.");
                    }
                }

            }

            if (validatePaySummary != null) {
                if (validatePaySummary.equals("1")) {
                    driver.findElement(By.xpath("//button[contains(text(),'Summaries')]")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Summaries Tab is clicked.");
                    logScreenshot(driver);
                }
            }

            if (!validateTextValueInWebElementInUse(PageObj_PayAdviceAndPaymentSummary.panel_PayAdvicesAndPaymentSummaries(driver), storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) {
                errorCounter++;
            }

        } else {
            logError("The Side Menu - Pay Advice and Summaries is NOT found.");
            errorCounter++;
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean cancelPendingLeave(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBalanceButton, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            WebElement leaveItem = waitChild("//div[contains(@id, 'list-item-leave-app') and contains(., '" + leaveReason + "') and contains(., 'Pending Approval')]//button[contains(@class, 'button--plain add-button')]/i[@class='icon-options']", 2, 1, driver);
            if (leaveItem != null) {
                Thread.sleep(4000);
                logMessage("Leave Reason " + leaveReason + " with Pending Approval is found.");
                leaveItem.click();
                Thread.sleep(4000);
                logMessage("Leave Item '" + leaveReason + "' Ellipsis menu is clicked.");
                logScreenshot(driver);

                waitChild("//ul[@class='sub-nav show']/div/li[contains(., 'Cancel leave')]", 4, 1, driver).click();
                Thread.sleep(4000);
                logMessage("Menu Cancel leave is clicked.");

                WebElement button_ConfirmCancelLeave = waitChild("//button[@type='submit' and text()='Cancel Leave']", 10, 1, driver);
                if (button_ConfirmCancelLeave != null) {
                    button_ConfirmCancelLeave.click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Confirm Cancel Leave button is clicked.");

                    waitChild("//button[@type='submit' and text()='Ok']", 10, 1, driver).click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("OK button is clicked.");
                    logScreenshot(driver);

                    waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                    logScreenshot(driver);

                    double currentLeaveBalannceTaken = 0;
                    if (leaveType != null) {
                        currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                        logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                        currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                        System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                        if (leaveTakenExpected != null) {
                            if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                                logMessage("The day(s) of Leave Taken is as expected.");
                            } else {
                                logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                                errorCounter++;
                            }
                        }
                    }

                } else {
                    logError("Confirm Cancel Leave button is NOT shown.");
                    errorCounter++;
                }

            } else {
                logError("Leave Reason " + leaveReason + " with Pending Approval is NOT found.");
                errorCounter++;
            }
        } else {
            errorCounter++;
        }
        logMessage("Cancel Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean cancelLeave(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBalanceButton, String leaveStatus, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            String xpath_LeaveItem = "//div[@class='list-item-row la multiline'";
            if (leaveType != null) {
                xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveType + "')";
            }
            if (leaveStatus != null) {
                xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + leaveStatus + "')";
            }
            if (leaveTakenExpected != null) {
                String duration = String.valueOf(abs(Integer.parseInt(leaveTakenExpected))) + " day";
                xpath_LeaveItem = xpath_LeaveItem + " and contains(., '" + duration + "')";
            }
            xpath_LeaveItem = xpath_LeaveItem + "]";
            logDebug(xpath_LeaveItem);

            WebElement leaveItem = waitChild(xpath_LeaveItem + "//button[contains(@class, 'button--plain add-button')]/i[@class='icon-options']", 2, 1, driver);
            if (leaveItem != null) {
                Thread.sleep(4000);
                logMessage("Leave Reason '" + leaveReason + "' is found.");
                leaveItem.click();
                logScreenshot(driver);

                waitChild("//ul[@class='sub-nav show']/div/li[contains(., 'Cancel leave')]", 4, 1, driver).click();
                Thread.sleep(4000);
                logMessage("Menu Cancel leave is clicked.");

                WebElement button_ConfirmCancelLeave = waitChild("//button[@type='submit' and text()='Cancel Leave']", 10, 1, driver);
                if (button_ConfirmCancelLeave != null) {
                    button_ConfirmCancelLeave.click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Confirm Cancel Leave button is clicked.");

                    waitChild("//button[@type='submit' and text()='Ok']", 10, 1, driver).click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("OK button is clicked.");
                    logScreenshot(driver);

                    waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                    logScreenshot(driver);

                    double currentLeaveBalannceTaken = 0;
                    if (leaveType != null) {
                        currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                        logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                        currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                        System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                        if (leaveTakenExpected != null) {
                            if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                                logMessage("The day(s) of Leave Taken is as expected.");
                            } else {
                                logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                                errorCounter++;
                            }
                        }
                    }

                } else {
                    logError("Confirm Cancel Leave button is NOT shown.");
                    errorCounter++;
                }

            } else {
                logError("Leave Reason " + leaveReason + " is NOT found.");
                errorCounter++;
            }
        } else {
            errorCounter++;
        }
        logMessage("Cancle Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean valdiateEditWorkFlowForm(String typeOfWorkflows, String actionMethod, String configurations, String itemToBeDeleted, String storeFileName, String isUpdate, String isCompare, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        //This function only validate the default setting of the form
        boolean isPassed = false;
        int errorCounter = 0;
        displaySettings_Workflows(driver);
        Thread.sleep(10000);

        if (typeOfWorkflows != null) {
            if (typeOfWorkflows.equals("1")) {
                PageObj_Workflows.label_ProfileChangesWorkflowDefault(driver).click();
                logMessage("Profile Workflows is clicked.");
            } else if (typeOfWorkflows.equals("2")) {
                PageObj_Workflows.label_LeaveWorkflowDefault(driver).click();
                logMessage("Leave Workflows is clicked.");
            }
        }

        GeneralBasic.waitSpinnerDisappear(120, driver);
        logMessage("Screenshot after selecting Type Of Workflow.");
        logScreenshot(driver);

        driver.navigate().refresh();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        PageObj_Workflows.button_EditWorkflow(driver).click();
        logMessage("Edit button is clicked.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//div[@class='table-body']/div[contains(., 'When a Member changes')]//button")).click();
        logMessage("Edit Workflow button is clicked. ");
        Thread.sleep(3000);
        logScreenshot(driver);

        String itemName = "";
        switch (configurations) {
            case "then":
                itemName = "approval is required";
                break;
            case "by":
                itemName = "any manager";
                break;
            case "in":
                itemName = "the member";
                break;
            case "and then":
                itemName = "do nothing";
                break;

        }

        String xpath_ItemToBeClicked = "//div[@id='modal-content']//div//form//div[contains(text(),'" + itemName + "')]";
        WebElement itemToBeClicked = waitChild(xpath_ItemToBeClicked, 10, 1, driver);
        if (itemToBeClicked != null) {
            itemToBeClicked.click();
            Thread.sleep(5000);
            logMessage("Item '" + itemName + "' is clicked.");
            logScreenshot(driver);
        } else {
            errorCounter++;
        }

        WebElement form_EditWorkflow = driver.findElement(By.xpath("//div[@id='modal-content']//div//form"));
        if (!validateTextValueInWebElementInUse(form_EditWorkflow, storeFileName, isUpdate, isCompare, null, testSerialNo, emailDomainName, driver))
            errorCounter++;


        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
        Thread.sleep(2000);
        logMessage("Close Edit Workflow button is clicked.");


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean addNewStarter(String givenname, String surname, String personalEmail, String personalMobile, String basisOfEmployment, String payrollCompany, String jobTitle, String hireDate, String reportingManagerFirstName, String reportingManagerMiddleName, String reportingManagerLastName, String reportingManagerPreferredName, String baseAnnualSalery, String hoursOfWork, String perUnit, String normalHourlyRate, String taxedAs, String storeFileName, String isUpdate, String isCompare, String expectedContent, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Only working for Admin user
        boolean isDone = false;
        int errorCounter = 0;

        displayTeamsPage(driver);
        driver.findElement(By.xpath("//a/span[text()='New starters']")).click();
        Thread.sleep(3000);
        logMessage("Team New Starter is clicked.");
        logScreenshot(driver);

        WebElement button_AddNewStarter= waitChild("//div[@class='page-header']//button[@class='button--people button--round add-button']", 120, 1, driver);
        if (button_AddNewStarter!=null){
            button_AddNewStarter.click();
            Thread.sleep(3000);
            logMessage("Add new starter button is clicked.");
        }else{
            logError("Add New Starter button is not shown.");
            errorCounter++;
        }
        logScreenshot(driver);

        String xpath_AddNewStarterLable = "//h4[text()='Add a New Starter']";
        WebElement lable_AddNewStarter = waitChild(xpath_AddNewStarterLable, 15, 1, driver);
        if (lable_AddNewStarter != null) {
            logMessage("Add a New Starter page is shown.");

            WebElement button_Close = driver.findElement(By.xpath("//button[@id='button-modal-close']"));
            if (personalEmail != null) {

            } else {
                personalEmail = generateEmployeeEmailAddress("localhost", payrollDBName, givenname, surname, testSerialNo, emailDomainName);
            }

            if (addNewStarter_BasicDetails(givenname, surname, personalEmail, personalMobile, emailDomainName, driver)) {
                if (addNewStarter_Employeement(basisOfEmployment, payrollCompany, jobTitle, hireDate, reportingManagerFirstName, reportingManagerMiddleName, reportingManagerLastName, reportingManagerPreferredName, baseAnnualSalery, hoursOfWork, perUnit, normalHourlyRate, taxedAs, driver)) {
                    if (storeFileName != null) {
                        WebElement form_Summary = driver.findElement(By.xpath("//div[@class='master-detail-modal']//form"));
                        if (!validateTextValueInWebElementInUse(form_Summary, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    }

                    if (normalHourlyRate != null) {
                        Thread.sleep(3000);
                        if (normalHourlyRate.equals("666666")) {
                            //close Add a New Starter screen as 666666 is for validating purpose.
                            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                            logMessage("Close button is clicked in Add a New Starter screen.");
                        } else {
                            driver.findElement(By.xpath("//button[@class='button--primary' and text()='Send forms to New Starter']")).click();
                            Thread.sleep(30000);
                            logMessage("Button - Send Formss to New Starter is clicked.");
                            GeneralBasic.waitSpinnerDisappear(180, driver);
                            logScreenshot(driver);
                        }
                    } else {
                        driver.findElement(By.xpath("//button[@class='button--primary' and text()='Send forms to New Starter']")).click();
                        Thread.sleep(30000);
                        logMessage("Button - Send Formss to New Starter is clicked.");
                        GeneralBasic.waitSpinnerDisappear(180, driver);
                        logScreenshot(driver);
                    }


                } else {
                    logError("Failed add New Starter Employeement.");
                    errorCounter++;

                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    logMessage("Button Close is clicked in New Stater Basic Detail screen.");
                    Thread.sleep(3000);
                    logScreenshot(driver);
                }
            } else {
                logError("Failed Add New Starter Basic Details.");
                errorCounter++;
                button_Close.click();
                logMessage("Button Close is clicked in New Stater Basic Detail screen.");
                Thread.sleep(5000);
                logScreenshot(driver);
            }

            Thread.sleep(5000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            Thread.sleep(5000);
        } else {
            logError("Add a New Starter page is NOT shown.");
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean addNewStarter_BasicDetails(String givenName, String surname, String personalEmail, String personalMobile, String emailDomainName, WebDriver driver) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;
        //New Starter Basic Details Tab is hilighting.
        if (givenName != null) {
            driver.findElement(By.xpath("//input[@id='firstName']")).sendKeys(givenName + Keys.TAB);
            Thread.sleep(2000);
            logMessage("First Name '" + givenName + "' is input.");
        }

        if (surname != null) {
            driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys(surname + Keys.TAB);
            Thread.sleep(2000);
            logMessage("Surname '" + surname + "' is input.");
        }

        if (personalEmail != null) {
            driver.findElement(By.xpath("//input[@id='email']")).sendKeys(personalEmail + Keys.TAB);
            Thread.sleep(2000);
            logMessage("Personal Email '" + personalEmail + "' is input.");

            WebElement inputValidation = waitChild("//span[contains(text(),'This is not a valid email.')]", 5, 1, driver);
            if (inputValidation != null) errorCounter++;
        }

        if (personalMobile != null) {
            driver.findElement(By.xpath("//input[@id='mobile']")).sendKeys(personalMobile + Keys.TAB);
            Thread.sleep(2000);
            logMessage("Personal Mobile '" + personalMobile + "' is input.");
        }

        logMessage("Screenshot before click Next button.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//button[@class='button--primary' and text()='Next']")).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        logMessage("Next button is clicked in New Starter - Basic Detail tab.");
        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean addNewStarter_Employeement(String basisOfEmployment, String payrollCompany, String jobTitle, String hireDate, String reportingManagerFirstName, String reportingManagerMiddleName, String reportingManagerLastName, String reportingManagerPreferredName, String baseAnnualSalary, String hoursOfWork, String perUnit, String normalHourlyRate, String taxedAs, WebDriver driver) throws Exception {
        //////// Add New Starter Employment screen must be shown first
        boolean isDone = false;
        int errorCounter = 0;
        WebElement itemValidation = null;

        if (basisOfEmployment != null) {
            driver.findElement(By.xpath("//div[@id='BasisOfEmployment']")).click();
            Thread.sleep(2000);
            logMessage("Basis Of Employment is clicked.");

            String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + basisOfEmployment + "']";
            WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
            if (itemList != null) {
                itemList.click();
                logMessage("Item '" + basisOfEmployment + "' is selected.");
            } else {
                logError("Item '" + basisOfEmployment + "' is NOT found.");
                driver.findElement(By.xpath("//div[@id='BasisOfEmployment']")).click();
                errorCounter++;
            }


        }

        if (payrollCompany != null) {
            driver.findElement(By.xpath("//div[@id='PayrollCompany']")).click();
            Thread.sleep(2000);
            logMessage("Payroll Company is clicked.");

            String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + payrollCompany + "']";
            WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
            if (itemList != null) {
                itemList.click();
                logMessage("Item '" + payrollCompany + "' is selected.");
            } else {
                logError("Item '" + payrollCompany + "' is NOT found.");
                driver.findElement(By.xpath("//div[@id='PayrollCompany']")).click();
                errorCounter++;
            }


        }

        if (jobTitle != null) {
            driver.findElement(By.xpath("//div[@id='JobTitleSelect']")).click();
            Thread.sleep(2000);
            logMessage("Job Title is clicked.");

            String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + jobTitle + "']";
            WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
            if (itemList != null) {
                itemList.click();
                logMessage("Item '" + jobTitle + "' is selected.");
            } else {
                logError("Item '" + jobTitle + "' is NOT found.");
                driver.findElement(By.xpath("//div[@id='JobTitleSelect']")).click();
                errorCounter++;
            }
        }


        if (hireDate != null) {

            if (hireDate.contains(";")) {
                hireDate = getExpectedDate(hireDate, null);
            }

            WebElement text_HireDate = driver.findElement(By.xpath("//input[@id='HireDate']"));
            text_HireDate.click();
            Thread.sleep(3000);
            text_HireDate.sendKeys(hireDate);
            text_HireDate.sendKeys(Keys.TAB);

            logMessage("Start Date: " + hireDate + " is input.");
            Thread.sleep(3000);
        }

        if (reportingManagerFirstName != null) {
            WebElement textbox_Search = driver.findElement(By.xpath("//div[@class='text-with-selector']//input[@id='searchField']"));
            textbox_Search.click();
            textbox_Search.sendKeys(reportingManagerFirstName);
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(30, driver);
            logMessage("Reporting Manager First Name '" + reportingManagerFirstName + "' is input.");
            logScreenshot(driver);

            itemValidation = waitChild("//span[contains(text(),'No matches found')]", 10, 1, driver);
            if (itemValidation != null) {
                logError("Report Manager is not found.");
                errorCounter++;
            } else {
                String userFullName = getUserFullname(reportingManagerFirstName, reportingManagerMiddleName, reportingManagerLastName, reportingManagerPreferredName);
                WebElement managerList = waitChild("//div[@class='list-item' and contains(., '" + userFullName + "')]", 20, 1, driver);

                if (managerList != null) {
                    managerList.click();
                    Thread.sleep(3000);
                    logMessage("Reporting manager '" + userFullName + "' is selected.");
                } else {
                    logWarning("Reporting manager '" + userFullName + "' is NOT found.");
                    errorCounter++;
                }
            }


        }

        if (baseAnnualSalary != null) {
            driver.findElement(By.xpath("//input[@id='baseAnnualSalary']")).sendKeys(baseAnnualSalary + Keys.TAB);
            Thread.sleep(1000);
            logMessage("baseAnnualSalary '" + baseAnnualSalary + "' is input.");
        }

        if (hoursOfWork != null) {
            driver.findElement(By.xpath("//input[@id='hoursOfWork']")).sendKeys(hoursOfWork + Keys.TAB);
            Thread.sleep(1000);
            logMessage("Hours of Work '" + hoursOfWork + "' is input.");
        }

        if (perUnit != null) {
            driver.findElement(By.xpath("//div[@id='HoursOfWorkFrequency']")).click();
            Thread.sleep(3000);
            String xpath_perUnit = "//div[contains(@class, 'list-item') and text()='" + perUnit + "']";
            WebElement element_perUnit = waitChild(xpath_perUnit, 10, 1, driver);
            if (element_perUnit != null) {
                element_perUnit.click();
                Thread.sleep(2000);
                logMessage("Unit '" + perUnit + "' is selected.");
            } else {
                logError("Unit '" + perUnit + "' is NOT found.");
            }
        }

        logMessage("Screenshot after select Pay Detail items.");
        logScreenshot(driver);

        Thread.sleep(2000);
        if (normalHourlyRate != null) {
            double currentAnnualSalary = Double.valueOf(baseAnnualSalary);
            double currentHoursOfWork = Double.valueOf(hoursOfWork);

            if (normalHourlyRate.equals("666666")) {
                double expectedHourlyRate = calculateHourlyRateViaNewStarterPayDetailsScreen(currentAnnualSalary, currentHoursOfWork, perUnit);
                String str_CurrentHourlyRate = driver.findElement(By.xpath("//input[@id='normalRate']")).getAttribute("value");
                double currentHourlyRate = Double.valueOf(str_CurrentHourlyRate);

                if ((currentHourlyRate - expectedHourlyRate) < 0.5) {
                    logMessage("The current Hourly Rate is equal to the expected Hourly Rate.");
                } else {
                    logMessage("The current Hourly Rate is " + driver.findElement(By.xpath("//input[@id='normalRate']")).getText());
                    logMessage("The expected hourly rate is " + String.valueOf(expectedHourlyRate));
                    logError("The current Hourly Rate is NOT equal to expected Hourly Rate.");
                    errorCounter++;
                }

            } else {
                driver.findElement(By.xpath("//input[@id='normalRate']")).sendKeys(normalHourlyRate + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Normal Hour Rate '" + normalHourlyRate + "' is input.");
            }
        }

        if (taxedAs != null) {
            if (taxedAs.equals("TFN")) {
                driver.findElement(By.xpath("//input[@id='1']")).click();
            } else if (taxedAs.equals("ABN")) {
                driver.findElement(By.xpath("//input[@id='0']")).click();
            }
        }

        logMessage("Screenshot before click Next button.");
        logScreenshot(driver);

        /////////////////////////
        driver.findElement(By.xpath("//button[@class='button--primary' and text()='Next']")).click();
        Thread.sleep(3000);
        logMessage("Next button is clicked in Add New Starter - Employment screen.");
        logScreenshot(driver);

        itemValidation = waitChild("//div[@id='form-element-BasisOfEmployment']//span[@class='required-msg validation-error'][contains(text(),'Required')]", 5, 1, driver);
        if (itemValidation != null) {
            logError("Basic of Employment is required.");
            errorCounter++;
        }

        itemValidation = waitChild("//div[@id='form-element-PayrollCompany']//span[@class='required-msg validation-error'][contains(text(),'Required')]", 5, 1, driver);
        if (itemValidation != null) {
            logError("Payroll Company is required.");
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editContactDetails_Address(String residentialCountry, String residentialAddress, String residentialSuburb, String residentialPostcode, String residentialState, String userForPostalAddress, String postalCountry, String postalAddress, String postalSuburb, String postalPostcode, String postalState, String emergencyContactName, String emergencyContactRelationship, String emergencyContactMobilePhoneNumber, String emergencyContactPhoneNumber, WebDriver driver) throws InterruptedException, IOException {

        boolean isDone = false;
        int errorCounter = 0;

        PageFac_ContactDetail pf = PageFactory.initElements(driver, PageFac_ContactDetail.class);

        pf.button_EditContactDetails_Address.click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Edit Address button is clicked.");

        if (residentialCountry != null) {
            WebElement element=pf.textbox_ResidentialCountry;
            element.click();
            clearTextBox(element);
            element.sendKeys(residentialCountry);
            logMessage("Redidential Country: '" + residentialCountry + "' is input.");
        }

        if (residentialAddress != null) {
            WebElement element=pf.textbox_ResidentialAddress;
            element.click();
            clearTextBox(element);
            Thread.sleep(2000);
            element.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), residentialAddress);
            logMessage("Redidential Address: '" + residentialAddress + "' is input.");
        }

        ////////////////////////// New changes by Jim on 13/08/2021 //////////////
        if ((residentialSuburb!=null)&&(residentialPostcode!=null)&&(residentialState!=null)){
            WebElement element=pf.textbox_ResidentialSuburb;
            element.click();
            clearTextBox(element);
            element.sendKeys(residentialSuburb);
            Thread.sleep(10000);
            String xpath_ListSuburb="//div[@class='list-item' and contains(span, '"+residentialSuburb+" "+residentialPostcode+" "+residentialState+"')]";
            WebElement list_Suburb=waitChild(xpath_ListSuburb, 15, 1, driver);
            if (list_Suburb!=null){
                logMessage("Suburb list is shown.");
                logScreenshot(driver);
                list_Suburb.click();
                logMessage("Suburb '"+residentialSuburb+" "+residentialPostcode+" "+residentialState+" is selected.");
                Thread.sleep(3000);
                logScreenshot(driver);
            }else{
                logWarning("Suburb '"+residentialSuburb+" "+residentialPostcode+" "+residentialState+" is NOT selected.");
                logScreenshot(driver);
            }
        }else if ((residentialSuburb==null)&&(residentialPostcode==null)&&(residentialState!=null)){
            WebElement element = driver.findElement(By.xpath("//div[@id='res:State']"));
            if (pf.selectResidentialState_InContactDetail_EditAddressPage(element, residentialState, driver)) {
                logMessage("Residential State '" + residentialState + "' is selected.");
            }

        }else if ((residentialSuburb==null)&&(residentialPostcode!=null)&&(residentialState==null)){
            WebElement element=pf.textbox_ResidentialPostcode;
            element.click();
            clearTextBox(element);
            element.sendKeys(residentialPostcode+Keys.TAB);
            logMessage("Redidential Postcode: '" + residentialPostcode + "' is input.");
            Thread.sleep(2000);
        }
        ////// End of new change //////

        ///////////////////////
       /* if (residentialSuburb != null) {
            WebElement element=pf.textbox_ResidentialSuburb;
            element.click();
            clearTextBox(element);
            element.sendKeys(residentialSuburb+Keys.TAB);
            Thread.sleep(2000);
            driver.findElement(By.xpath("//h4[@class='mc-header-heading']")).click();
            Thread.sleep(1000);
            logMessage("Redidential Suburb: '" + residentialSuburb + "' is input.");
            Thread.sleep(8000);
            logScreenshot(driver);
        }


        if (residentialPostcode != null) {
            WebElement element=pf.textbox_ResidentialPostcode;
            element.click();
            clearTextBox(element);
            element.sendKeys(residentialPostcode+Keys.TAB);
            logMessage("Redidential Postcode: '" + residentialPostcode + "' is input.");
            Thread.sleep(2000);
        }

        if (residentialState != null) {
            WebElement element = driver.findElement(By.xpath("//div[@id='res:State']"));
            if (pf.selectResidentialState_InContactDetail_EditAddressPage(element, residentialState, driver)) {
                logMessage("Residential State '" + residentialState + "' is selected.");
            }

        }*/
        //////

        if (userForPostalAddress != null) {
            tickCheckbox(userForPostalAddress, pf.checkbox_UseForPostalAddress, driver);
            logMessage("Option User for postal address is change.");
        }

        if (postalCountry != null) {
            WebElement element=pf.textbox_PostalCountry;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalCountry);
            logMessage("Postal Country: '" + postalCountry + "' is input.");
        }

        if (postalAddress != null) {
            WebElement element=pf.textbox_PostalAddress;
            element.click();
            clearTextBox(element);
            Thread.sleep(2000);
            element.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalAddress);
            logMessage("Postal Address: '" + postalAddress + "' is input.");
        }

        //////////////// Adjust by Jim on 23/08/2021 /////////////////
        if ((postalSuburb!=null)&&(postalPostcode!=null)&&(postalState!=null)){
            WebElement element=pf.textbox_PostalSuburb;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalSuburb);
            Thread.sleep(10000);
            String xpath_ListSuburb="//div[@class='list-item' and contains(span, '"+postalSuburb+" "+postalPostcode+" "+postalState+"')]";
            WebElement list_Suburb=waitChild(xpath_ListSuburb, 15, 1, driver);
            if (list_Suburb!=null){
                logMessage("Suburb list is shown.");
                logScreenshot(driver);
                list_Suburb.click();
                logMessage("Suburb '"+postalSuburb+" "+postalPostcode+" "+postalState+" is selected.");
                Thread.sleep(3000);
                logScreenshot(driver);
            }else{
                logWarning("Suburb '"+postalSuburb+" "+postalPostcode+" "+postalState+" is NOT selected.");
                logScreenshot(driver);
            }
        }else if ((postalSuburb==null)&&(postalPostcode==null)&&(postalState!=null)){
            WebElement element = driver.findElement(By.xpath("//div[@id='res:State']"));
            if (pf.selectPostalState_InContactDetail_EditAddressPage(element, postalState, driver)) {
                logMessage("Postal State '" + postalState + "' is selected.");
            }

        }else if ((postalSuburb==null)&&(postalPostcode!=null)&&(postalState==null)){
            WebElement element=pf.textbox_PostalPostcode;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalPostcode+Keys.TAB);
            logMessage("Postal Postcode: '" + postalPostcode + "' is input.");
            Thread.sleep(2000);
        }
        ////// End of new code //////

        /////////////////////// Disable the old codes //////////////////
        /*
        if (postalCountry != null) {
            WebElement element=pf.textbox_PostalCountry;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalCountry);
            logMessage("Postal Country: '" + postalCountry + "' is input.");
        }

        if (postalAddress != null) {
            WebElement element=pf.textbox_PostalAddress;
            element.click();
            clearTextBox(element);
            Thread.sleep(2000);
            element.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalAddress);
            logMessage("Postal Address: '" + postalAddress + "' is input.");
        }

        if (postalSuburb != null) {
            WebElement element=pf.textbox_PostalSuburb;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalSuburb);
            Thread.sleep(2000);
            driver.findElement(By.xpath("//h4[@class='mc-header-heading']")).click();
            Thread.sleep(1000);
            logMessage("Postal Suburb: '" + postalSuburb + "' is input.");
        }

        if (postalPostcode != null) {
            WebElement element=pf.textbox_PostalPostcode;
            element.click();
            clearTextBox(element);
            element.sendKeys(postalPostcode);
            logMessage("Postal Postcode: '" + postalPostcode + "' is input.");
        }

        if (postalState != null) {
            WebElement element = driver.findElement(By.xpath("//div[@id='mail:State']"));
            if (pf.selectPostalState_InContactDetail_EditAddressPage(element, postalState, driver)) {
                logMessage("Postal State '" + postalState + "' is selected.");
            } else {
                errorCounter++;
            }

        }
        */

        ////// End of old code //////

        ////////////
        logMessage("Screenshot before click Save button.");
        logScreenshot(driver);

        WebElement button_Save = pf.button_SaveEditAddress;
        if (button_Save.isEnabled()) {
            button_Save.click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Save Edit Address button is clicked.");

            if (waitChild("//form[@autocomplete='off']", 10, 1, driver) != null) {
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                Thread.sleep(3000);
                logWarning("Edit Address is NOT saveed. Close Edit Address button is clicked.");
                errorCounter++;
            }

        } else {
            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
            Thread.sleep(3000);
            logWarning("Edit Address is NOT saveed. Close Edit Address button is clicked.");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;

    }

    public static boolean activateNewStarterViaEmail(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdate, String isCompare, String expectedMessage, String expectedActivation, String payrollDBName, String testSerialNo, String emailDomainName) throws InterruptedException, IOException, MessagingException, SQLException, ClassNotFoundException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);

        //Get Activation link from email...
        String emailAccountName = userName.replace("@" + emailDomainName, "");
        String activtionLink = JavaMailLib.getNewStarter_ActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName);
        String tempUsername = JavaMailLib.getNewStarterTempUsername(emailAccountName, "Welcome to Sage ESS", emailDomainName);

        if (activtionLink != null) {
            //Launch ESS User activation page
            logMessage("Start Launch ESS Activation Page.");
            WebDriver driver2 = launchWebDriver(activtionLink, 3);

            logMessage("Screenshot before activation.");
            logScreenshot(driver2);
			Thread.sleep(5000);
            if (waitChild("//p[contains(text(),'"+tempUsername+"')]", 30, 1, driver2)!=null){
                //////////// Run only if temp User name is shown correctly.
                driver2.findElement(By.xpath("//input[@id='newPassword']")).sendKeys(password);
                driver2.findElement(By.xpath("//input[@id='reenterPassword']")).sendKeys(password);

                logMessage("Screenshot before click Sign up button.");
                logScreenshot(driver2);

                driver2.findElement(By.xpath("//button[@id='continue']")).click();
                Thread.sleep(10000);
                logMessage("Sign up button is clicked.");
                logScreenshot(driver2);

                if (waitChild("//h3[contains(text(),'New Starter Forms')]", 120, 1, driver2)!=null){
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(180, driver2);
                    logMessage("Sign up new starter '"+userName+"' successfully.");
                    logScreenshot(driver2);

                    logMessage("Sign out ESS.");
                    GeneralBasic.signoutESS(driver2);

                }else{
                    logError("Failed signed up new starter '"+userName+"'.");
                    logScreenshot(driver2);
                    errorCounter++;
                }

                //////
            }else{
                logError("Temp user name '"+tempUsername+"' is NOT Shown.");
                errorCounter++;
                logScreenshot(driver2);
            }


            driver2.close();
            logMessage("Activation page is closed.");

            logMessage("End of Activating User '" + userName + " " + lastName + "'.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;

        return isDone;
    }


    public static boolean activateNewStarterViaEmail_OLD(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdate, String isCompare, String expectedMessage, String expectedActivation, String payrollDBName, String testSerialNo, String emailDomainName) throws InterruptedException, IOException, MessagingException, SQLException, ClassNotFoundException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        String userName = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);

        //Get Activation link from email...
        String emailAccountName = userName.replace("@" + emailDomainName, "");
        String activtionLink = JavaMailLib.getNewStarter_ActivationLinkFromMail(emailAccountName, "Welcome to Sage ESS", emailDomainName);
        String tempUsername = JavaMailLib.getNewStarterTempUsername(emailAccountName, "Welcome to Sage ESS", emailDomainName);

        if (activtionLink != null) {
            //Launch ESS User activation page
            logMessage("Start Launch ESS Activation Page.");
            WebDriver driver2 = launchWebDriver(activtionLink, 3);

            logMessage("Screenshot before activation.");
            logScreenshot(driver2);

            WebElement panel = waitChild("//div[@id='panel']", 10, 1, driver2);
            if (!validateTextValueInWebElementInUse(panel, storeFileName, isUpdate, isCompare, expectedMessage, testSerialNo, emailDomainName, driver2))
                errorCounter++;

            WebElement button_SendVerificationCode = waitChild("//button[@class='button button--wide' and text()='Send verification code']", 60, 1, driver2);

            if (button_SendVerificationCode != null) {
                PageObj_ESSActivation.button_SendVerificationCode(driver2).click();
                logMessage("Send Verification Code button is clicked.");
                waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);
                PageObj_ESSActivation.button_OK_VerificationCodeEmailSent(driver2).click();
                logMessage("OK button is clicked after Verification Code Email is sent.");


                logMessage("Delay 45 seconds for email verification code...");
                Thread.sleep(45000);

                String verificationCode = JavaMailLib.getVerificationCode(emailAccountName, "Verification Code", emailDomainName);
                if (verificationCode != null) {
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).click();
                    //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                    logMessage("Verification code is input");

                    PageObj_ESSActivation.textbox_Password(driver2).click();
                    //PageObj_ESSActivation.textbox_Password(driver2).clear();
                    PageObj_ESSActivation.textbox_Password(driver2).sendKeys(password);
                    logMessage("Password is input.");

                    logMessage("Log screenshot before click Continue button in Activate User Account screen.");
                    logScreenshot(driver2);

                    PageObj_ESSActivation.button_Continue(driver2).click();
                    logMessage("Continue button is clicked.");

                    GeneralBasic.waitSpinnerDisappear(120, driver2);

                    logMessage("Screenshot after click continue button.");
                    logScreenshot(driver2);
                } else {
                    errorCounter++;
                }

            } else {
                if (expectedActivation != null) {
                    if (expectedActivation.equals("1")) {
                        errorCounter++;
                    }
                } else {
                    errorCounter++;
                }

            }

            driver2.close();
            logMessage("Activation page is closed.");
            logMessage("End of Activating User '" + userName + " " + lastName + "'.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static boolean fillNewStarterForm(String firstName, String middleName, String lastName, String preferredName, String tempUserName, String password, String welcomeStoreFileName, String isUpdate, String isCompare, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;

        logMessage("Log on ESS as New Starter " + firstName + " " + lastName + ".");
        WebDriver driver = GeneralBasic.launchESS(url_ESS, driverType);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        GeneralBasic.logonESS(tempUserName, password, firstName, lastName, middleName, preferredName, payrollDBName, testSerialNo, emailDomainName, driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean logonESSAsNewStarter(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Activating email must be kept in inbox
        boolean isDone = false;
        int errorCounter = 0;

        logMessage("--- Start logon ESS as New Starter.");

        String employeeEmailAddress = generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
        String emailAccountName = employeeEmailAddress.replace("@" + emailDomainName, "");
        String newStartTempUsername = JavaMailLib.getNewStarterTempUsername(emailAccountName, "Welcome to Sage ESS", emailDomainName);

        //waitSpinnerDisappear(20, driver);
        //waitChild("//button[@class='btn btn-primary btn-lg btn-block' and text()='Sign In']", 30, 1, driver);

        waitSpinnerDisappear(20, driver);
        waitChild("//button[@class='btn btn-primary btn-lg btn-block' and text()='Sign In']", 30, 1, driver);

        PageObj_ESSHome.userNameTextBox(driver).clear();
        PageObj_ESSHome.userNameTextBox(driver).sendKeys(newStartTempUsername);
        logMessage("User Name: " + newStartTempUsername + " is input.");
        driver.findElement(By.xpath("//button[@id='continue']")).click();
        Thread.sleep(2000);
        logMessage("Continue button is clicked.");

        if (waitChild("//h1[contains(text(),'Enter password')]", 20, 1, driver)==null){
            errorCounter++;
        }

        logDebug("password: '"+password+"'");
        PageObj_ESSHome.passwordTextBox(driver).clear();
        PageObj_ESSHome.passwordTextBox(driver).sendKeys(password);

        Thread.sleep(2000);
        logMessage("Password '" + password + "' is input.");

        PageObj_ESSHome.signInButton(driver).click();
        logMessage("Sign In button is clicked.");
        Thread.sleep(15000);
        //GeneralBasic.waitSpinnerDisappear(180, driver);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logScreenshot(driver);

        String xpath_LableNewStarter = "//h3[contains(text(),'New Starter Forms')]";
        WebElement lable_NewStarter = waitChild(xpath_LableNewStarter, 10, 1, driver);
        if (lable_NewStarter != null) {
            logMessage("Logon as New Starter '" + firstName + "' successfully.");

            if (storeFileName != null) {
                WebElement lable_Welcome = waitChild("//div[@class='master-detail-content']//h4[contains(., 'Welcome " + firstName + "')]", 10, 1, driver);
                if (lable_Welcome != null) {
                    logMessage("Welcome message is shown.");
                    WebElement welcomePage = waitChild("//div[@class='panel-body']", 10, 1, driver);
                    if (welcomePage != null) {
                        if (!validateTextValueInWebElementInUse(welcomePage, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    } else {
                        logError("Welcome body panel is NOT shown.");
                        errorCounter++;
                    }
                } else {
                    logError("Welcome message is NOT shown correctly.");
                    errorCounter++;
                }


            }

        } else {
            logError("Failed logon New Starter '" + firstName + "'.");
            errorCounter++;
        }
        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStarter_PersonalInformation(String newGivenName, String newMiddleName, String newSurname, String newPreferredName, String gender, String dob, String maritalStatus, String medicalCondition, String treatment, WebDriver driver) throws InterruptedException, IOException {
        //Logon as New Starter using temp password only, can validate New Starter Personal Information page.
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_PersonalInformation = waitChild("//span[contains(text(),'Personal Information')]", 10, 1, driver);
        if (sidebarItem_PersonalInformation.isEnabled()) {
            sidebarItem_PersonalInformation.click();
            logMessage("The sidebar - Personal Information in New Starter Forms page is clicked.");

            if (newGivenName != null) {
                WebElement textbox_GivenName = waitChild("//input[@id='FirstName']", 10, 1, driver);
                clearTextBox(textbox_GivenName);
                textbox_GivenName.sendKeys(newGivenName + Keys.TAB);
                Thread.sleep(2000);
                logMessage("New Given Name is input.");
            }

            if (newMiddleName != null) {
                WebElement textbox_MiddleName = waitChild("//input[@id='MiddleName']", 10, 1, driver);
                clearTextBox(textbox_MiddleName);
                textbox_MiddleName.sendKeys(newMiddleName + Keys.TAB);
            }

            if (newSurname != null) {
                WebElement textbox_Surname = waitChild("//input[@id='LastName']", 10, 1, driver);
                clearTextBox(textbox_Surname);
                textbox_Surname.sendKeys(newSurname + Keys.TAB);
            }

            if (newPreferredName != null) {
                WebElement textbox_PreferredName = waitChild("//input[@id='PreferredName']", 10, 1, driver);
                clearTextBox(textbox_PreferredName);
                textbox_PreferredName.sendKeys(newPreferredName + Keys.TAB);
            }

            if (gender != null) {
                driver.findElement(By.xpath("//div[@id='Gender']")).click();
                Thread.sleep(2000);
                WebElement dropdownlistItem = waitChild("//div[@class='list-item' and contains(text(),'" + gender + "')]", 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Gener '" + gender + "' is selected.");
                } else {
                    logError("Gener '" + gender + "' is NOT found.");
                    errorCounter++;
                }
            }

            if (dob != null) {
                WebElement textbox_DOB = driver.findElement(By.xpath("//input[@id='BirthDate']"));
                clearTextBox(textbox_DOB);
                textbox_DOB.sendKeys(dob + Keys.TAB);
                Thread.sleep(2000);
            }

            if (maritalStatus != null) {
                driver.findElement(By.xpath("//div[@id='MaritalStatus']")).click();
                Thread.sleep(2000);
                WebElement dropdownlistItem = waitChild("//div[@class='list-item' and contains(text(),'" + maritalStatus + "')]", 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Marital Status '" + maritalStatus + "' is selected.");
                } else {
                    logError("Maritial Satus '" + maritalStatus + "' is NOT found.");
                    errorCounter++;
                }
            }

            if (medicalCondition != null) {
                String[] medicalConditionLists = splitString(medicalCondition, ";");
                String[] treatmentLists = splitString(treatment, ";");
                int totalItemCount = medicalConditionLists.length;
                String currentMedicalCondition = null;
                String currentTreatment = null;
                for (int i = 0; i < totalItemCount; i++) {
                    currentMedicalCondition = medicalConditionLists[i];
                    currentTreatment = treatmentLists[i];

                    driver.findElement(By.xpath("//h5[contains(., 'Medical conditions')]//i[@class='icon-add']")).click();
                    Thread.sleep(2000);
                    logMessage("Add Medical Condition button is clicked.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//input[@id='condition']")).sendKeys(currentMedicalCondition + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Medical Condition '" + currentMedicalCondition + "' is input.");

                    driver.findElement(By.xpath("//textarea[@id='treatment']")).sendKeys(currentTreatment + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Treamment '" + currentTreatment + "' is input.");

                    logMessage("Screenshot before click Save Medical Condition button.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//form[contains(., 'Add Medical Condition')]//button[text()='Save']")).click();
                    Thread.sleep(3000);
                    logMessage("Save button is clicked.");
                }
            }

            logMessage("Screenshot before click 'Save and continue' button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Save and continue button is clicked in New Starter - Personal Information screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Contact Details')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("Contact Details page is shwon as expected.");
            } else {
                logError("Contact Detail page is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The sidebar - Personal Information in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStarter_ContactDetails(String personalEmail, String personalMobile, String homeNumber, String residential_Country, String residential_Address, String residential_Suburb, String residential_PostCode, String residential_State, String residentialAddressUseForPostalAddress, String postal_Country, String postal_Address, String postal_Suburb, String postal_Postcode, String postal_State, String emergncy_ContactName, String emergencyContact_Relationship, String emergencyContact_MobilePhone, String emergencyContact_PhoneNumber, WebDriver driver) throws InterruptedException, IOException {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_ContactDetails = waitChild("//span[contains(text(),'Contact Details')]", 10, 1, driver);
        if (sidebarItem_ContactDetails.isEnabled()) {
            sidebarItem_ContactDetails.click();
            logMessage("The Sidebar Item - Contact Details in New Starter Forms page is clicked.");

            if (personalEmail != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='PersonalEmail']"));
                clearTextBox(element);
                element.sendKeys(personalEmail + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Email '" + personalEmail + "' is input.");
            }

            if (personalMobile != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='PersonalMobile']"));
                clearTextBox(element);
                element.sendKeys(personalMobile + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Mobile '" + personalMobile + "' is input.");
            }

            if (homeNumber != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='HomeNumber']"));
                clearTextBox(element);
                element.sendKeys(homeNumber + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Home Number '" + homeNumber + "' is input.");
            }


            //////////////// Residential Address
            if (residential_Country != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:Country']"));
                clearTextBox(element);
                element.sendKeys(residential_Country + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Residential Country '" + residential_Country + "' is input.");
            }

            if (residential_Address != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:AddressLine1']"));
                clearTextBox(element);
                element.sendKeys(residential_Address + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Residential Address '" + residential_Address + "' is input.");
            }

            if (residential_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:Suburb']"));
                clearTextBox(element);
                element.sendKeys(residential_Suburb + Keys.TAB);
                Thread.sleep(4000);
                GeneralBasic.waitSpinnerDisappear(90, driver);
                logMessage("Residential Suburb '" + residential_Suburb + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);

                String itemToBeSelect = residential_Suburb + " " + residential_PostCode + " " + residential_State;
                WebElement list_Address = waitChild("//div[@class='list-item' and contains(., '" + itemToBeSelect + "')]", 10, 1, driver);
                if (list_Address != null) {
                    if (list_Address.isDisplayed()) {
                        list_Address.click();
                        logMessage("Item '" + itemToBeSelect + "' is selected.");
                    }
                    Thread.sleep(2000);
                } else {
                    if (residential_Country.equals("Australia")) {
                        logError("Item '" + itemToBeSelect + "' is NOT selected.");
                        errorCounter++;
                    }

                }

            }

            if ((residential_PostCode != null) && (!residential_Country.equals("Australia"))) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:Postcode']"));
                clearTextBox(element);
                element.sendKeys(residential_PostCode + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Residential PostCode '" + residential_PostCode + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);
            }

            if ((residential_State != null) && (!residential_Country.equals("Australia"))) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:StateName']"));
                clearTextBox(element);
                element.sendKeys(residential_State + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Residential State '" + residential_State + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);
            }

            if (residentialAddressUseForPostalAddress != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='IsPostalAddressSameAsResidential']"));
                tickCheckbox(residentialAddressUseForPostalAddress, element, driver);
            }

            ///////////////////////// Postal Address
            if (postal_Country != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Country']"));
                clearTextBox(element);
                element.sendKeys(postal_Country + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Country '" + postal_Country + "' is input.");
            }

            if (postal_Address != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:AddressLine1']"));
                clearTextBox(element);
                element.sendKeys(postal_Address + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Address '" + postal_Address + "' is input.");
            }

            if (postal_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Suburb']"));
                clearTextBox(element);
                element.sendKeys(postal_Suburb + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Suburb '" + postal_Suburb + "' is input.");
            }

            if (postal_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Suburb']"));
                clearTextBox(element);
                element.sendKeys(postal_Suburb + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Postal Suburb '" + postal_Suburb + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);

                String itemToBeSelect = postal_Suburb + " " + postal_Postcode + " " + postal_State;
                WebElement list_Address = waitChild("//div[@class='list-item' and contains(., '" + itemToBeSelect + "')]", 10, 1, driver);
                if (list_Address != null) {
                    if (list_Address.isDisplayed()) {
                        list_Address.click();
                        logMessage("Item '" + itemToBeSelect + "' is selected.");
                    }
                    Thread.sleep(2000);
                } else {
                    if (postal_Country.equals("Australia")) {
                        logError("Item '" + itemToBeSelect + "' is NOT selected.");
                        errorCounter++;
                    }

                }

            }

            if ((postal_Postcode != null) && (!postal_Country.equals("Australia"))) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Postcode']"));
                clearTextBox(element);
                element.sendKeys(postal_Postcode + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("postal Post Code '" + postal_Postcode + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);
            }

            if ((postal_State != null) && (!postal_Country.equals("Australia"))) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:StateName']"));
                clearTextBox(element);
                element.sendKeys(postal_State + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Postal State '" + postal_State + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);
            }

            /////////////// Emergency Contact   ///////////////

            if (emergncy_ContactName != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='ContactName']"));
                clearTextBox(element);
                element.sendKeys(emergncy_ContactName + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact '" + emergncy_ContactName + "' is input.");
            }

            if (emergencyContact_Relationship != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='ContactRelationship']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_Relationship + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Relationship '" + emergencyContact_Relationship + "' is input.");
            }

            if (emergencyContact_MobilePhone != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='MobileContact']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_MobilePhone + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Mobile Phone '" + emergencyContact_MobilePhone + "' is input.");
            }

            if (emergencyContact_PhoneNumber != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='TelephoneContact']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_PhoneNumber + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Phone Number '" + emergencyContact_PhoneNumber + "' is input.");
            }


            logMessage("Screenshot before click 'Save and continue' button.");
            logScreenshot(driver);
            scrollToBottom(driver);
            Thread.sleep(3000);
            driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Save and continue button is clicked in New Starter - Contact Details screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Employment')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("Employment page is shwon as expected.");
            } else {
                logError("Employment page is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The sidebar - Contact Detials in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

///////////////////

    public static boolean validateNewStarter_Employment(String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Employment = waitChild("//span[contains(text(),'Employment')]", 10, 1, driver);
        if (sidebarItem_Employment.isEnabled()) {
            sidebarItem_Employment.click();
            logMessage("The Sidebar Item - Employment in New Starter Forms page is clicked.");

            if (storeFileName != null) {
                WebElement employmentForm = waitChild("//div[@class='master-detail-content' and contains(., 'Employment')]", 10, 1, driver);
                if (employmentForm != null) {
                    if (!validateTextValueInWebElementInUse(employmentForm, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                } else {
                    logError("Employment form is NOT shown.");
                    errorCounter++;
                }
            }
            logMessage("Screenshot before click 'Continue' button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[@class='button--primary button--wide'][contains(text(),'ontinue')]")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Continue button is clicked in New Starter - Contact Details screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Tax Details')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("New Starter - Tax Detail page is shwon as expected.");
            } else {
                logError("New Starter - Tax Detail page is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The sidebar - Employment in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateNewStarter_TaxDetailsForm(String tfnOption, String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_TaxDetails = waitChild("//span[contains(text(),'Tax Details')]", 10, 1, driver);
        if (sidebarItem_TaxDetails != null) {
            if (sidebarItem_TaxDetails.isEnabled()) {
                sidebarItem_TaxDetails.click();
                logMessage("The Sidebar Item - Tax Details in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (tfnOption != null) {
                    switch (tfnOption) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='HaveTfn']")).click();
                            logMessage("Option - I have TFN is checked.");
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='HaveProvidedOrWillProvide']")).click();
                            logMessage("Option - I have TFN or will Provide is checked.");
                            break;
                        case "3":
                            driver.findElement(By.xpath("//input[@id='MadeASeparateApplication']")).click();
                            logMessage("Option - I have made a separate application is checked.");
                            break;
                        case "4":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionUnderAge']")).click();
                            logMessage("Option - I am claiming an exemption Under 18 is checked.");
                            break;
                        case "5":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionOther']")).click();
                            logMessage("Option - I am claiming an exemption Other is checked.");
                            break;
                    }
                }

                Thread.sleep(3000);
                logScreenshot(driver);

                if (storeFileName != null) {
                    WebElement taxDetailsForm = waitChild("//div[@class='master-detail-content' and contains(., 'Tax Details')]", 10, 1, driver);
                    if (taxDetailsForm != null) {
                        if (!validateTextValueInWebElementInUse(taxDetailsForm, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    } else {
                        logError("Tax Details form is NOT shown.");
                        errorCounter++;
                    }
                }

               /* logMessage("Screenshot before click Save and Continue button.");
                logScreenshot(driver);
                SystemLibrary.scrollFromTopToHalfWindow(driver);
                WebElement button_SaveAndContinue = SystemLibrary.waitChild("//button[@class='button--primary button--wide' and contains(., 'ontinue')]", 10, 1, driver);
                button_SaveAndContinue.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Save and continue button is clicked.");
				*/
            } else {
                logWarning("The sidebar - Tax Details form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Tax Details is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStarter_TaxDetails(String tfnOption, String storeFileName, String isUpdate, String isCompare, String expectedContent, String taxFileNumber, String previousFamilyName, String choiceOfBasisOfEmp, String choiceOfTaxPurposes, String choiceOfTaxFreeThreshold, String choiceOfDebitLoan, String choiceOfFinancialSuppl, String abn, String instalmentRate, String CommissioneInstalmentRate, String rateOfWithHolding, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;
        Thread.sleep(3000);
        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_TaxDetails = waitChild("//span[contains(text(),'Tax Details')]", 10, 1, driver);
        if (sidebarItem_TaxDetails != null) {
            if (sidebarItem_TaxDetails.isEnabled()) {
                sidebarItem_TaxDetails.click();
                logMessage("The Sidebar Item - Tax Details in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (tfnOption != null) {
                    Thread.sleep(2000);
                    switch (tfnOption) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='HaveTfn']")).click();
                            Thread.sleep(2000);
                            logMessage("Option - I have TFN is checked.");
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='HaveProvidedOrWillProvide']")).click();
                            Thread.sleep(2000);
                            logMessage("Option - I have TFN or will Provide is checked.");
                            break;
                        case "3":
                            driver.findElement(By.xpath("//input[@id='MadeASeparateApplication']")).click();
                            Thread.sleep(2000);
                            logMessage("Option - I have made a separate application is checked.");
                            break;
                        case "4":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionUnderAge']")).click();
                            Thread.sleep(2000);
                            logMessage("Option - I am claiming an exemption Under 18 is checked.");
                            scrollFromTopToquarterWindow(driver);
                            break;
                        case "5":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionOther']")).click();
                            Thread.sleep(2000);
                            logMessage("Option - I am claiming an exemption Other is checked.");
                            scrollFromTopToquarterWindow(driver);
                            break;
                    }
                    logScreenshot(driver);
                }
                //////////////////////////

                if (taxFileNumber != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='TaxFileNumber']"));
                    clearTextBox(element);
                    element.sendKeys(taxFileNumber + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + taxFileNumber + "' is Added.");
                }

                if (previousFamilyName != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='PreviousFamilyName']"));
                    clearTextBox(element);
                    element.sendKeys(previousFamilyName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Previous Family Name '" + previousFamilyName + "' is Added.");
                    logScreenshot(driver);
                }

                if (choiceOfBasisOfEmp != null) {
                    Thread.sleep(2000);
                    //SystemLibrary.scrollFromTopToquarterWindow(driver);
                    switch (choiceOfBasisOfEmp) {
                        case "1":
                            WebElement fullTimexpath=driver.findElement(By.xpath("//label[contains(text(),'Full-time employment')]"));
                            if (!isElementClickable(fullTimexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            fullTimexpath.click();
                            logMessage("Option - Full Time Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement partTimexpath=driver.findElement(By.xpath("//label[contains(text(),'Part-time employment')]"));


                            if (!isElementClickable(partTimexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            partTimexpath.click();
                            logMessage("Option - Part Time Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "3":
                            WebElement labourhirexpath=driver.findElement(By.xpath("//label[contains(text(),'Labour hire')]"));
                            if (!isElementClickable(labourhirexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            labourhirexpath.click();
                            logMessage("Option - Labour Hire is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "4":
                            WebElement superannuationxpath=driver.findElement(By.xpath("//label[contains(text(),'Superannuation or annuity income stream')]"));
                            if (!isElementClickable(superannuationxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            superannuationxpath.click();
                            logMessage("Option - Super Annuation Or Annuity Income Stream is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "5":
                            WebElement casualempxpath=driver.findElement(By.xpath("//label[contains(text(),'Casual employment')]"));
                            if (!isElementClickable(casualempxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            casualempxpath.click();
                            logMessage("Option - Casual Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfTaxPurposes != null) {
                    scrollFromTopToquarterWindow(driver);
                    Thread.sleep(2000);
                    switch (choiceOfTaxPurposes) {
                        case "1":
                            WebElement ausResxpath=driver.findElement(By.xpath("//label[contains(text(),'An Australian resident for tax purposes')]"));
                            if (!isElementClickable(ausResxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            ausResxpath.click();
                            logMessage("Option - An Australian resident for tax purposes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement foreignResxpath=driver.findElement(By.xpath("//label[contains(text(),'A foreign resident for tax purposes')]"));
                            if (!isElementClickable(foreignResxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            foreignResxpath.click();
                            logMessage("Option - A foreign resident for tax purposes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "3":
                            WebElement workingholidayxpath=driver.findElement(By.xpath("//label[contains(text(),'A working holiday maker')]"));
                            if (!isElementClickable(workingholidayxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            workingholidayxpath.click();
                            logMessage("Option - A working holiday maker is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfTaxFreeThreshold != null) {
                    // SystemLibrary.scrollFromTopToquarterWindow(driver);
                    Thread.sleep(2000);
                    switch (choiceOfTaxFreeThreshold) {
                        case "1":
                            WebElement taxyesxpath=driver.findElement(By.xpath("//input[@id='tax-threshold-yes']"));
                            if (!isElementClickable(taxyesxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            taxyesxpath.click();
                            logMessage("Option - Tax-Free Threshold - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement taxnoxpath=driver.findElement(By.xpath("//input[@id='tax-threshold-no']"));
                            if (!isElementClickable(taxnoxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            taxnoxpath.click();
                            logMessage("Option - Tax-Free Threshold - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfDebitLoan != null) {
                    Thread.sleep(2000);
                    switch (choiceOfDebitLoan) {
                        case "1":
                            WebElement educyesxpath=driver.findElement(By.xpath("//input[@id='educ-debt-yes']"));
                            if (!isElementClickable(educyesxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            educyesxpath.click();
                            logMessage("Option - Debit Loan - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement educnoxpath=driver.findElement(By.xpath("//input[@id='educ-debt-no']"));
                            if (!isElementClickable(educnoxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            educnoxpath.click();
                            logMessage("Option - Debit Loan - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfFinancialSuppl != null) {
                    Thread.sleep(2000);
                    switch (choiceOfFinancialSuppl) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='fin-supp-debt-yes']")).click();
                            logMessage("Option - Financial Supplement debt - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='fin-supp-debt-no']")).click();
                            logMessage("Option - Financial Supplement debt - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }
                GeneralBasic.waitSpinnerDisappear(120, driver);
                scrollToBottom(driver);
                WebElement checkbox_Declaration = waitChild("//label[contains(text(),'I declare that the information that I have given i')]", 60, 1, driver);
                if (checkbox_Declaration != null) {
                    checkbox_Declaration.click();
                    Thread.sleep(2000);
                    scrollToBottom(driver);
                    logMessage("New Starter Tax Details Declaration check box is Checked.");
                } else {
                    logError("New Starter Tax Details Declaration check box is not Checked.");

                }

                if (abn != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='Abn']"));
                    clearTextBox(element);
                    Thread.sleep(2000);
                    element.sendKeys(Keys.DELETE);
                    element.sendKeys(abn + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + abn + "' is Added.");
                }

                if (instalmentRate != null) {
                    switch (instalmentRate) {
                        case "1":
                            driver.findElement(By.xpath("//label[contains(text(),\"Yes, Commissioner's instalment rate is:\")]")).click();
                            logMessage("Option - Yes, Commissioner's instalment rate is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//label[contains(text(),'No, flat rate of withholding is 20%.')]")).click();
                            logMessage("Option - No, flat rate of withholding is 20%. is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (CommissioneInstalmentRate != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='CommisionersInstalmentRate']"));
                    clearTextBox(element);
                    element.sendKeys(CommissioneInstalmentRate + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + CommissioneInstalmentRate + "' is Added.");
                }

                if (rateOfWithHolding != null) {
                    Thread.sleep(2000);
                    switch (rateOfWithHolding) {
                        case "1":
                            driver.findElement(By.xpath("//label[contains(text(),\"Yes, Commissioner's instalment rate is:\")]")).click();
                            logMessage("Option - Yes, Commissioner's instalment rate is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//label[contains(text(),'No, flat rate of withholding is 20%.')]")).click();
                            logMessage("Option - No, flat rate of withholding is 20%. is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }
                /////////

                Thread.sleep(3000);
                logScreenshot(driver);

                WebElement button_SaveAndContinue = waitChild("//button[@class='button--primary button--wide' and text()='Save and continue']", 10, 1, driver);
                if (button_SaveAndContinue != null) {
                    button_SaveAndContinue.click();
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Save and Continue button is clicked in New Starter - Tax Details screen.");
                    logScreenshot(driver);

                    WebElement lable_Superannuation = waitChild("//div[@class='page-header']//h4[contains(text(),'Superannuation')]", 60, 1, driver);
                    Thread.sleep(2000);
                    if (lable_Superannuation != null) {
                        logMessage("New Starter - Superannuation page is shown.");
                    } else {
                        logError("New Starter - Superannuation page is NOT shown.");
                        errorCounter++;
                    }
                }

            } else {
                logWarning("The sidebar - Tax Details form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Tax Details is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean validateNewStarter_SuperAnnuationForm(String choiceOfSuper, String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Superannuation = waitChild("//span[contains(text(),'Superannuation')]", 10, 1, driver);
        if (sidebarItem_Superannuation != null) {
            if (sidebarItem_Superannuation.isEnabled()) {
                sidebarItem_Superannuation.click();
                logMessage("The Sidebar Item - Superannuation in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (choiceOfSuper != null) {
                    switch (choiceOfSuper) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='NominatedByEmployer']")).click();
                            logMessage("Option - The Super fund nominationed by my employer is checked.");
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='NominatedByEmployee']")).click();
                            logMessage("Option - The APRA fund or retiredment savings account (RSA) I nominate is checked.");
                            break;
                        case "3":
                            driver.findElement(By.xpath("//input[@id='SelfManagedSuper']")).click();
                            logMessage("Option - The self-managed super fund (SMSF) i nominate is checked.");
                            break;
                    }
                }

                Thread.sleep(3000);
                logScreenshot(driver);

                if (storeFileName != null) {
                    WebElement superannuationForm = waitChild("//div[@class='master-detail-content' and contains(., 'Superannuation')]", 10, 1, driver);
                    if (superannuationForm != null) {
                        if (!validateTextValueInWebElementInUse(superannuationForm, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    } else {
                        logError("Superannuation form is NOT shown.");
                        errorCounter++;
                    }
                }

            } else {
                logWarning("The sidebar - Superannuation form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Superannuation is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean editNewStarter_SuperAnnuationForm(String choiceOfSuper, String storeFileName, String isUpdate, String isCompare, String expectedContent, String provideTFN, String fundABN, String fundName, String fundPhone, String fundAddress1, String fundAddress2, String fundAddress_Suburb, String fundAddress_Postcode, String fundAddress_State, String fundAccUSI, String fundAccName, String fundAccNumber, String fundAccESA, String fundAccBSB, String fundBankAccNumber, String attachmentAPRAFileName, String trusteeDirector, String attachmentSMSFFileName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Superannuation = waitChild("//span[contains(text(),'Superannuation')]", 10, 1, driver);
        if (sidebarItem_Superannuation != null) {
            if (sidebarItem_Superannuation.isEnabled()) {
                sidebarItem_Superannuation.click();
                logMessage("The Sidebar Item - Superannuation in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (choiceOfSuper != null) {
                    switch (choiceOfSuper) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='NominatedByEmployer']")).click();
                            logMessage("Option - The Super fund nominationed by my employer is checked.");
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='NominatedByEmployee']")).click();
                            logMessage("Option - The APRA fund or retiredment savings account (RSA) I nominate is checked.");
                            break;
                        case "3":
                            driver.findElement(By.xpath("//input[@id='SelfManagedSuper']")).click();
                            logMessage("Option - The self-managed super fund (SMSF) i nominate is checked.");
                            break;
                    }
                }

                if (provideTFN != null) {
                    WebElement element = driver.findElement(By.xpath("//label[contains(text(),'Provide TFN')]"));
                    tickCheckbox(provideTFN, element, driver);
                }

                Thread.sleep(2000);
                logScreenshot(driver);

                if (fundABN != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundAbn']"));
                    clearTextBox(element);
                    element.sendKeys(fundABN + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund ABN '" + fundABN + "' is Added.");
                }

                if (fundName != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundName']"));
                    clearTextBox(element);
                    element.sendKeys(fundName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Name '" + fundName + "' is Added.");
                }

                if (fundPhone != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundPhone']"));
                    clearTextBox(element);
                    element.sendKeys(fundPhone + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Phone '" + fundPhone + "' is Added.");
                }

                Thread.sleep(2000);
                logScreenshot(driver);

                if (fundAddress1 != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='AddressLine1']"));
                    clearTextBox(element);
                    element.sendKeys(fundAddress1 + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Address Line 1 '" + fundAddress1 + "' is Added.");
                }


                if (fundAddress2 != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='AddressLine2']"));
                    clearTextBox(element);
                    element.sendKeys(fundAddress2 + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Address Line 2 '" + fundAddress2 + "' is Added.");
                }

                if (fundAddress_Suburb != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='Suburb']"));
                    clearTextBox(element);
                    element.sendKeys(fundAddress_Suburb + Keys.TAB);
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("fund Address Suburb '" + fundAddress_Suburb + "' is input.");
                    logMessage("Screenshot of the address list");
                    logScreenshot(driver);

                    String itemToBeSelect = fundAddress_Suburb + " " + fundAddress_Postcode + " " + fundAddress_State;
                    WebElement list_Address = waitChild("//div[@class='list-item' and contains(., '" + itemToBeSelect + "')]", 10, 1, driver);
                    if (list_Address != null) {
                        if (list_Address.isDisplayed()) {
                            list_Address.click();
                            logMessage("Item '" + itemToBeSelect + "' is selected.");
                        }
                        Thread.sleep(2000);
                    } else {
                        logError("Item '" + itemToBeSelect + "' is NOT selected.");
                        errorCounter++;
                    }

                }

                Thread.sleep(2000);
                logScreenshot(driver);

                scrollFromTopToHalfWindow(driver);
                if (fundAccUSI != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundUsi']"));
                    clearTextBox(element);
                    element.sendKeys(fundAccUSI + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Account USI '" + fundAccUSI + "' is Added.");
                }

                if (fundAccName != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='AccountName']"));
                    clearTextBox(element);
                    element.sendKeys(fundAccName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Your Fund Account Name '" + fundAccName + "' is Added.");
                }

                if (fundAccNumber != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='MembershipNumber']"));
                    clearTextBox(element);
                    element.sendKeys(fundAccNumber + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Your Fund Account Number '" + fundAccNumber + "' is Added.");
                }

                Thread.sleep(2000);
                logScreenshot(driver);


                if (fundAccESA != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundEsa']"));
                    clearTextBox(element);
                    element.sendKeys(fundAccESA + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Account ESA '" + fundAccESA + "' is Added.");
                }

                if (fundAccBSB != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundBsb']"));
                    clearTextBox(element);
                    element.sendKeys(fundAccBSB + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Bank Account BSB '" + fundAccBSB + "' is Added.");
                }

                if (fundBankAccNumber != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='FundAccountNumber']"));
                    clearTextBox(element);
                    element.sendKeys(fundBankAccNumber + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Fund Bank Account Number '" + fundBankAccNumber + "' is Added.");
                }

                Thread.sleep(3000);
                logScreenshot(driver);

                ///////////////////////
                if (attachmentAPRAFileName!=null){
                    WebElement button_APRAAttachment=waitChild("//input[@type='file' and @class='fileInput']", 60, 1, driver);
                    if (button_APRAAttachment!=null){
                        String fileFullPath = dataSourcePath + attachmentAPRAFileName;
                        button_APRAAttachment.sendKeys(fileFullPath);
                        logMessage("Required File for Super is attached.");
                        logScreenshot(driver);
                    }
                }

                //////

                if (trusteeDirector != null) {
                    WebElement element = driver.findElement(By.xpath("//label[contains(text(),'I am the trustee, or a director of the corporate t')]"));
                    tickCheckbox(trusteeDirector, element, driver);
                }

                if (attachmentSMSFFileName!=null){
                    WebElement button_SMSFAttachment = waitChild("//input[@type='file' and @class='fileInput']", 60, 1, driver);
                    if (button_SMSFAttachment!=null){
                        String fileFullPath = dataSourcePath + attachmentSMSFFileName;
                        button_SMSFAttachment.sendKeys(fileFullPath);
                        logMessage("Required File for Super is attached.");
                        logScreenshot(driver);
                    }
                }

                logMessage("Screenshot before click Save and Continue button.");
                logScreenshot(driver);
                scrollFromTopToHalfWindow(driver);
                WebElement button_SaveAndContinue = waitChild("//button[@class='button--primary button--wide' and contains(., 'ontinue')]", 10, 1, driver);
                button_SaveAndContinue.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Save and continue button is clicked.");

            } else {
                logWarning("The sidebar - Superannuation form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Superannuation is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        logScreenshot(driver);
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static WebDriver launchSageMicrOpayWebAPI(int driverType) throws InterruptedException {
        WebDriver driver = launchWebDriver(url_SageWebAPI, driverType);
        return driver;
    }

    public static boolean validateAPI_MicrOpay_EmployeeDetails_Terminated(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String includeTerminated, String terminatedSince, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement element = waitChild("//a[contains(text(),'GET api/EmployeeBasic?includeTerminated={includeTe')]", 10, 1, driver);
        if (element != null) {
            element.click();
            Thread.sleep(2000);
            logMessage("Item 'Employee Detail - Include Terminated' is clicked.");
            logScreenshot(driver);

            if (includeTerminated != null) {
                element = driver.findElement(By.xpath("//input[@id='includeTerminated']"));
                clearTextBox(element);
                element.sendKeys(includeTerminated + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Include Terminated '" + includeTerminated + "' is input.");
            }

            if (terminatedSince != null) {
                element = driver.findElement(By.xpath("//input[@id='terminatedSince']"));
                clearTextBox(element);
                element.sendKeys(terminatedSince + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Terminated Since '" + terminatedSince + "' is input.");
            }

            driver.findElement(By.xpath("//button[contains(text(),'Go')]")).click();
            Thread.sleep(15000);

            WebElement element_Response = waitChild("//div[@id='response-info']", 120, 1, driver);
            Thread.sleep(10000);
            if (element_Response != null) {
                if (!validateTextValueInWebElementInUse(element_Response, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;
            } else {
                logError("Failed get web api response.");
                errorCounter++;
            }

        } else {
            logError("Item 'Employee Detail - Include Terminated' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean inputSageMicrOpayWebAPIAuthenticationDetails(String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        //Display Sage MicrOpay Web API page first.
        boolean isDone;
        String webAPIUserName = MCSLib.getValueFromGUIKeyFile("WebAPI UserName", testSerialNo);
        String webAPIPassword = MCSLib.getValueFromGUIKeyFile("WebAPI UserName Password", testSerialNo);
        String apiKey = MCSLib.getValueFromGUIKeyFile("GUI Key", testSerialNo);

        ////////////// Input Authentication details  /////////////////
        WebElement textbox_Username = driver.findElement(By.xpath("//input[@id='username']"));
        clearTextBox(textbox_Username);
        textbox_Username.sendKeys(webAPIUserName + Keys.TAB);
        Thread.sleep(2000);
        logMessage("Web API username '" + webAPIUserName + "' is input.");

        WebElement textbox_Password = driver.findElement(By.xpath("//input[@id='password']"));
        clearTextBox(textbox_Password);
        textbox_Password.sendKeys(webAPIPassword + Keys.TAB);
        Thread.sleep(2000);
        logMessage("Web API password '" + webAPIPassword + "' is input.");

        WebElement textbox_apiKey = driver.findElement(By.xpath("//input[@id='apiKey']"));
        clearTextBox(textbox_apiKey);
        textbox_apiKey.sendKeys(apiKey + Keys.TAB);
        Thread.sleep(2000);
        logMessage("Web API Key '" + apiKey + "' is input.");
        ///////

        logScreenshot(driver);
        isDone = true;
        return isDone;
    }


    public static boolean validateAPI_Lookups(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String type, String code, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement element = waitChild("//a[contains(text(),'GET api/Lookups?type={type}&code={code}')]", 10, 1, driver);
        if (element != null) {
            element.click();
            Thread.sleep(2000);
            logMessage("Item 'Lookups' is clicked.");
            logScreenshot(driver);

            if (type != null) {
                element = driver.findElement(By.xpath("//input[@id='type']"));
                clearTextBox(element);
                element.sendKeys(type + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Type '" + type + "' is input.");
            }

            if (code != null) {
                element = driver.findElement(By.xpath("//input[@id='code']"));
                clearTextBox(element);
                element.sendKeys(code + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Code '" + code + "' is input.");
            }

            logMessage("Screenshot before clicking GO button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[contains(text(),'Go')]")).click();
            Thread.sleep(15000);

            logMessage("Screenshot after click Go butotn.");
            logScreenshot(driver);

            WebElement element_Response = waitChild("//div[@id='response-info']", 120, 1, driver);
            if (element_Response != null) {
                if (!validateTextValueInWebElementInUse(element_Response, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;
            } else {
                logError("Failed get web api response.");
                errorCounter++;
            }


        } else {
            logError("Item 'Lookups' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean validateNewStarter_BankAccount(String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_BankAccount = waitChild("//span[contains(text(),'Bank Account')]", 10, 1, driver);
        if (sidebarItem_BankAccount.isEnabled()) {
            sidebarItem_BankAccount.click();
            logMessage("The Sidebar Item - Bank Account in New Starter Forms page is clicked.");

            if (storeFileName != null) {
                WebElement employmentForm = waitChild("//div[@class='master-detail-content' and contains(., ' Bank Account')]", 10, 1, driver);
                if (employmentForm != null) {
                    if (!validateTextValueInWebElementInUse(employmentForm, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                } else {
                    logError("Employment form is NOT shown.");
                    errorCounter++;
                }
            }

            logMessage("Screenshot before click 'Continue' button.");
            logScreenshot(driver);
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStarter_BankAccount(String bsb, String bankName, String accountNumber, String accountName, String expectedLableMessage, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_BankAccount = waitChild("//span[contains(text(),'Bank Account')]", 10, 1, driver);
        if (sidebarItem_BankAccount != null) {
            if (sidebarItem_BankAccount.isEnabled()) {
                sidebarItem_BankAccount.click();
                logMessage("The Sidebar Item - Bank Account in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (bsb != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='bsb']"));
                    clearTextBox(element);
                    element.sendKeys(bsb + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("BSB '" + bsb + "' is input.");
                }

                if (bankName != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='bankName']"));
                    clearTextBox(element);
                    element.sendKeys(bankName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Bank Name '" + bankName + "' is input.");
                }

                if (accountNumber != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='accountNumber']"));
                    clearTextBox(element);
                    element.sendKeys(accountNumber + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Account Number '" + accountNumber + "' is input.");
                }

                if (bsb != accountName) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='accountName']"));
                    clearTextBox(element);
                    element.sendKeys(accountName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Account Name '" + accountNumber + "' is input.");
                }

                if (expectedLableMessage != null) {
                    String xpath_Lable = "//div[contains(text(),'" + expectedLableMessage + "')]";
                    WebElement element = waitChild(xpath_Lable, 30, 1, driver);
                    if (element != null) {
                        logMessage("Lable message is shown as expected as below.");
                        logMessage(expectedLableMessage);
                    } else {
                        logError("Lable is not found in New Starter - Bank Account form.");
                        errorCounter++;
                    }
                }

                logMessage("Screenshot before click Save and Continue button.");
                logScreenshot(driver);
                WebElement button_SaveAndContinue = waitChild("//button[@class='button--primary button--wide' and text()='Save and continue']", 10, 1, driver);
                button_SaveAndContinue.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("Save and continue button is clicked.");

            } else {
                logWarning("The sidebar - Bank Account form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Bank Account is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        logScreenshot(driver);
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean validateNewStarter_Summary(String storeFileName, String isUpdatd, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Summary = waitChild("//span[contains(text(),'Summary')]", 10, 1, driver);
        if (sidebarItem_Summary != null) {
            if (sidebarItem_Summary.isEnabled()) {
                sidebarItem_Summary.click();
                logMessage("The Sidebar Item - Summary in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                WebElement element = waitChild("//div[@class='master-detail-content' and contains(., 'Summary')]", 30, 1, driver);
                if (element != null) {
                    if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdatd, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) {
                        errorCounter++;
                    }
                }

            } else {
                logWarning("The sidebar - Summary form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar - Summary is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        logScreenshot(driver);
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean completeNewStarter_Summary(String isConsent, String noConsentMessageExpected, String isCompleteNewStarter, String storeFileName, String isUpdated, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Summary = waitChild("//span[contains(text(),'Summary')]", 10, 1, driver);
        if (sidebarItem_Summary != null) {
            if (sidebarItem_Summary.isEnabled()) {
                sidebarItem_Summary.click();
                logMessage("The Sidebar Item - Summary in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                WebElement element = waitChild("//div[@class='master-detail-content' and contains(., 'Summary')]", 30, 1, driver);
                if (element != null) {
                    ///////////////////

                    if (isConsent != null) {
                        scrollToBottom(driver);
                        WebElement checkbox_GiveConsent = driver.findElement(By.xpath("//input[@id='DeclareGiveConsent']"));
                        if (isConsent.equals("1")) {
                            tickCheckbox("1", checkbox_GiveConsent, driver);
                            logMessage("Consent checkbox is checked.");
                            Thread.sleep(2000);
                        } else if (isConsent.equals("2")) {
                            tickCheckbox("2", checkbox_GiveConsent, driver);
                            logMessage("Consent checkbox is unChecked.");
                            Thread.sleep(2000);
                        }
                    }

                    logMessage("Screenshot before click button - Complete.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Complete']")).click();
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Comlete button is clicked.");
                    logScreenshot(driver);

                    element = waitChild("//body[@class='modal']/div[@id='content']/div/div[@id='modal']/div[@id='modal-content']/div/div[2]", 60, 1, driver);
                    if (element != null) {
                        if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdated, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                        if (isCompleteNewStarter != null) {
                            if (isCompleteNewStarter.equals("1")) {
                                driver.findElement(By.xpath("//button[@class='button--primary' and text()='Yes, complete & sign out']")).click();
                                Thread.sleep(2000);
                                GeneralBasic.waitSpinnerDisappear(120, driver);
                                logMessage("Button - 'Yes, complete & sign out' is clicked.");
                                logScreenshot(driver);

                                element = waitChild("//h1[contains(text(),'Sign in')]", 60, 1, driver);
                                if (element != null) {
                                    logMessage("New Starter is logged out.");
                                } else {
                                    logError("New Starter is NOT logged out.");
                                    errorCounter++;
                                }

                            } else if (isCompleteNewStarter.equals("2")) {
                                driver.findElement(By.xpath("//button[@class='button' and text()='Cancel']")).click();
                                Thread.sleep(2000);
                                GeneralBasic.waitSpinnerDisappear(120, driver);
                                logMessage("Button - 'Cancel' is clicked.");
                            }

                            logScreenshot(driver);

                        }
                    } else {
                        if (isCompleteNewStarter != null) {
                            if (isCompleteNewStarter.equals("1")) {
                                logError("Confirm the completing new starter form is not shown.");
                                errorCounter++;
                            }
                        }

                    }

                    if (noConsentMessageExpected != null) {
                        WebElement consentLableError = waitChild("//div[@class='status-bar error' and contains(., '" + noConsentMessageExpected + "')]", 30, 1, driver);
                        if (consentLableError != null) {
                            logMessage("User does NOT Consent message is shown as expected.");
                        } else {
                            logError("User dose NOT Consent massage is NOT shown.");
                            errorCounter++;
                        }
                    }

                    //////

                }else{
                    logError("Failed Completing New Starter.");
                    errorCounter++;
                }



            } else {
                logWarning("The sidebar - Summary form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar - Summary is NOT found.");
            errorCounter++;
        }
        ////////////////////////////

        logScreenshot(driver);
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean validateNewStaterDetials(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        String fullName = firstName + " " + lastName;
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers("New starters", driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {

            WebElement element = waitChild("//a[@class='link heading-container' and contains(., '" + fullName + "')]", 15, 1, driver);
            if (element != null) {
                element.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("New Starter '" + fullName + "' is clicked.");
                logScreenshot(driver);

                element = driver.findElement(By.xpath("//div[@class='panel-body']"));
                if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;
            } else {
                logError("New starter '" + fullName + "' is NOT found.");
                errorCounter++;
            }


        } else {
            logError("Team 'New Starters' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean sendNewStaterToPayroll(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        String fullName = firstName + " " + lastName;
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers("New starters", driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {

            WebElement element = waitChild("//a[@class='link heading-container' and contains(., '" + fullName + "')]", 15, 1, driver);
            if (element != null) {
                element.click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("New Starter '" + fullName + "' is clicked.");
                Thread.sleep(15000);

                logScreenshot(driver);

                WebElement button_SendToPayroll=waitChild("//button[contains(text(),'Send to payroll')]", 120, 1, driver);
                if (button_SendToPayroll!=null){
                    Thread.sleep(5000);
                    button_SendToPayroll.click();
                    Thread.sleep(5000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    logMessage("Send to payroll button is clicked.");
                    logScreenshot(driver);

                    //Validate page after click send to payroll button.
                    if (storeFileName != null) {
                        logMessage("Start validating New Starter detail page after clicking 'Send to Payroll' button.");
                        element = driver.findElement(By.xpath("//div[@class='panel-body']"));
                        if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    }
                }else{
                    logError("Button Send to Payroll is NOT shown.");
                    errorCounter++;
                }

            } else {
                logError("New starter '" + fullName + "' is NOT found.");
                errorCounter++;
            }


        } else {
            logError("Team 'New Starters' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean finaliseNewStaterInESS(String firstName, String middleName, String lastName, String preferredName, String teamName, String memberRole, String workEmail, String officeNumber, String workMobile, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        String fullName = firstName + " " + lastName;
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers("New starters", driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {

            WebElement element = waitChild("//a[@class='link heading-container' and contains(., '" + fullName + "')]", 15, 1, driver);
            if (element != null) {
                element.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("New Starter '" + fullName + "' is clicked.");
                logScreenshot(driver);

                if (waitChild("//h4[contains(text(),'Finalise New Starter')]", 10, 1, driver) != null) {
                    if (teamName != null) {
                        WebElement textBox_Team = driver.findElement(By.xpath("//input[@id='teamSearchField']"));
                        clearTextBox(textBox_Team);
                        textBox_Team.click();
                        textBox_Team.sendKeys(teamName + Keys.TAB);
                        Thread.sleep(2000);

                        WebElement listItem = waitChild("//span[contains(text(),'" + teamName + "')]", 60, 1, driver);
                        if (listItem != null) {
                            listItem.click();
                            logMessage("Team '" + teamName + "' is selected.");
                        } else {
                            logError("Team '" + teamName + "' is NOT found.");
                            errorCounter++;
                        }
                    }

                    if (memberRole != null) {
                        driver.findElement(By.xpath("//div[@id='role']")).click();
                        Thread.sleep(3000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        WebElement listItem = waitChild("//div[contains(@class, 'list-item') and text()='" + memberRole + "']", 60, 1, driver);
                        if (listItem != null) {
                            listItem.click();
                            Thread.sleep(3000);
                            logMessage("Member Role '" + memberRole + "' is clicked.");
                        } else {
                            logError("Member Role '" + memberRole + "' is NOT found.");
                        }

                    }

                    if (workEmail != null) {
                        if (workEmail.equals("AUTO")) {
                            workEmail = generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
                        }
                        element = driver.findElement(By.xpath("//input[@id='WorkEmail']"));
                        clearTextBox(element);
                        element.sendKeys(workEmail + Keys.TAB);
                        Thread.sleep(3000);
                        logMessage("Work Email: '" + workEmail + "' is input.");
                    }

                    if (officeNumber != null) {
                        element = driver.findElement(By.xpath("//input[@id='OfficeNumber']"));
                        clearTextBox(element);
                        element.sendKeys(officeNumber + Keys.TAB);
                        Thread.sleep(3000);
                        logMessage("Office Number: '" + officeNumber + "' is input.");
                    }

                    if (workMobile != null) {
                        element = driver.findElement(By.xpath("//input[@id='WorkMobile']"));
                        clearTextBox(element);
                        element.sendKeys(workMobile + Keys.TAB);
                        Thread.sleep(3000);
                        logMessage("Work Mobile: '" + workMobile + "' is input.");
                    }

                    logMessage("Screenshot before click Finalise button.");
                    logScreenshot(driver);


                } else {
                    errorCounter++;
                    logError("Add a new customer - Confirmation screen is NOT shown.");
                }

                WebElement button_Finalise = waitChild("//button[@class='button--primary button--wide' and text()='Finalise']", 60, 1, driver);
                if (button_Finalise != null) {
                    button_Finalise.click();
                    Thread.sleep(4000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Finalise button is clicked.");
                    logScreenshot(driver);
                } else {
                    logError("Button - Finalise is NOT found.");
                    errorCounter++;
                }

            } else {
                logError("New starter '" + fullName + "' is NOT found.");
                errorCounter++;
            }


            if (waitChild("//h3[contains(text(),'New Starters')]", 10, 1, driver) != null) {

                logMessage("Teams -> New starter screen displayed after  '" + fullName + "' Finalised.");

            }  else {
                errorCounter++;
                logError("Teams -> New starter screen displayed after  '" + fullName + "' Not Finalised..");
            }
            logScreenshot(driver);
           
        } else {
            logError("Team 'New Starters' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateIntegrationPage_SuperFund(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (GeneralBasic.displaySettings_General(driver)) {
            WebElement lable_DefaultSuperFunds = waitChild("//div[@class='panel-body']/div/div[@class='page-header']/h4[text()='Default Super Funds']", 10, 1, driver);
            if (lable_DefaultSuperFunds != null) {
                logMessage("Default Super Funds are shown.");
            } else {
                logError("Default Super funds are NOT shown.");
                errorCounter++;
            }

            WebElement form_SuperFund = waitChild("//div[@class='panel-body']/div/div[@class='page-row']//form[contains(., 'Super Fund')]", 10, 1, driver);

            if (!validateTextValueInWebElementInUse(form_SuperFund, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isPassed = true;

        return isPassed;
    }

    public static boolean addSuperPDSFileInIntegrationPage(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String superFundName, String pdsLink, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;
        if (GeneralBasic.displaySettings_General(driver)) {
            WebElement superList = waitChild("//body/div[@id='content']/div/section[@id='page-container']/main[@class='container full']/div[@class='panel-body']/div/div[@class='page-row']/div[@class='page-section background-color-white']/div[@class='ps-content show-content']/div[@id='default-super-funds']/div[@class='list-container']/form/div[starts-with(., '" + superFundName + "')]", 10, 1, driver);
            if (superList != null) {
                logMessage("Super Fund " + superFundName + "' are shown.");
                WebElement button_EditSuperFund = waitChild("//body/div[@id='content']/div/section[@id='page-container']/main[@class='container full']/div[@class='panel-body']/div/div[@class='page-row']/div[@class='page-section background-color-white']/div[@class='ps-content show-content']/div[@id='default-super-funds']/div[@class='list-container']/form/div[starts-with(., '" + superFundName + "')]//button", 10, 1, driver);
                if (button_EditSuperFund != null) {
                    button_EditSuperFund.click();
                    Thread.sleep(3000);

                    if (pdsLink != null) {
                        WebElement element = driver.findElement(By.xpath("//input[@id='pds-link']"));
                        element.sendKeys(Keys.CONTROL + "A");
                        element.sendKeys(Keys.DELETE);
                        element.sendKeys(pdsLink + Keys.TAB);
                        Thread.sleep(2000);
                        logMessage("PDS Link '" + pdsLink + "' is input");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//button[@class='button--primary' and text()='Save']")).click();
                        Thread.sleep(2000);
                        GeneralBasic.waitSpinnerDisappear(120, driver);
                        logMessage("Save button is clicked.");
                    }
                } else {
                    logError("Edit Super fund button is NOT shown.");
                    errorCounter++;
                }

            } else {
                logError("Super Fund " + superFundName + "' are NOT shown.");
                errorCounter++;
            }

            WebElement form_SuperFund = waitChild("//div[@class='panel-body']/div/div[@class='page-row']//form[contains(., 'Super Fund')]", 10, 1, driver);

            if (!validateTextValueInWebElementInUse(form_SuperFund, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            errorCounter++;
        }
        if (errorCounter == 0) isPassed = true;

        return isPassed;
    }

    public static double calculateHourlyRateViaNewStarterPayDetailsScreen(double annualSalary, double hoursOfWork, String perUnit) {
        double hourlyRate = 0;
        switch (perUnit) {
            case "2 weekly":
                hourlyRate = annualSalary / 26 / hoursOfWork;
                break;
            case "4 weekly":
                hourlyRate = annualSalary / 13 / hoursOfWork;
                break;
            case "Bi-monthly":
                hourlyRate = annualSalary / 24 / hoursOfWork;
                break;
            case "Fortnightly":
                hourlyRate = annualSalary / 26 / hoursOfWork;
                break;
            case "Monthly":
                hourlyRate = annualSalary / 12 / hoursOfWork;
                break;
            case "Weekly":
                hourlyRate = annualSalary / 52 / hoursOfWork;
                break;
        }
        return hourlyRate;
    }

    public static boolean removeNewStarterViaAdmin(String firstName, String middleName, String lastName, String preferredName, String emailDomainName, String testSerialNo, WebDriver driver) throws Exception {
        boolean isRemoved = false;
        int errorCounter = 0;

        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers("New starters", driver)) {
                errorCounter++;
            }
        }
        Thread.sleep(4000);
        String fullName = null;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        WebElement item = waitChild("//a[@class='link heading-container' and contains(., '" + fullName + "')]", 30, 1, driver);
        if (item != null) {
            logMessage("New Starter: '" + fullName + "' is found and item clicked.");
            item.click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logScreenshot(driver);
            Thread.sleep(7000);
            driver.findElement(By.xpath("//button[@class='button--plain add-button']")).click();
            Thread.sleep(3000);
            logMessage("Ellipsis button is clicked.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[contains(text(),'Remove new starter')]")).click();
            Thread.sleep(3000);
            logMessage("Menu 'Remove new starter' is clicked.");

            String xpath_PopupMessage = "//div[@class='mc-body' and text()='Are you sure you want to remove " + fullName + "?']";
            WebElement element = waitChild(xpath_PopupMessage, 60, 1, driver);
            if (element != null) {
                logMessage("Pop up screen is Shown.");
                logScreenshot(driver);
                driver.findElement(By.xpath("//button[contains(@class,'button--danger') and text()='Yes, remove']")).click();
                logMessage("Yes, Remove button is clicked.");
                Thread.sleep(5000);

            } else {
                logError("Pop up screen is NOT shown.");
                errorCounter++;
            }
        } else {
            logError("User '" + fullName + "' is NOT found.");
            errorCounter++;
        }


        /*displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        SystemLibrary.logMessage("Team member: '" + fullName + "' Ellipsis is clicked.");
        logScreenshot(driver);

        driver.findElement(By.linkText("Remove from team")).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);
        logMessage("Remove from team menu is clicked.");
        logScreenshot(driver);

        String text_MessageBox="";
        int counter=0;
        WebElement button_Submit=null;
        String xpath_Form="//form[contains(., 'Remove "+fullName+" from "+teamName+"')]";
        logDebug("The xpath_Form is "+xpath_Form);
        WebElement form_MessageBox=SystemLibrary.waitChild(xpath_Form, 60, 1, driver);
        if (form_MessageBox==null) errorCounter++;

        while (form_MessageBox!=null){
            counter++;
            text_MessageBox = text_MessageBox+form_MessageBox.getText()+"\n";
            logScreenshot(driver);

            button_Submit=SystemLibrary.waitChild("//button[@type='submit']", 60, 1, driver);
            if (button_Submit!=null){
                button_Submit.click();
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(60, driver);;
                logMessage("Butotn is clicked "+counter+" time(s).");
            }
            xpath_Form="//form[contains(., 'Remove "+fullName+" from "+teamName+"')]";
            form_MessageBox=SystemLibrary.waitChild(xpath_Form, 60, 1, driver);
        }

        xpath_Form="//form[contains(., 'Pending Approvals')]";
        form_MessageBox=SystemLibrary.waitChild(xpath_Form, 60, 1, driver);
        if (form_MessageBox!=null){
            text_MessageBox=text_MessageBox+form_MessageBox.getText()+"\n";
            button_Submit=SystemLibrary.waitChild("//button[@type='submit']", 60, 1, driver);
            if (button_Submit!=null){
                counter++;
                button_Submit.click();
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(60, driver);;
                logMessage("Butotn is clicked "+counter+" time(s).");
            }
        }

        if (storeFileName != null) {
            if (!SystemLibrary.validateStringFile(text_MessageBox, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo)) errorCounter++;
        }
*/
        if (errorCounter == 0) isRemoved = true;
        return isRemoved;
    }

    public static boolean validateNewStarter_PersonalInformation(String newGivenName, String newMiddleName, String newSurname, String newPreferredName, String gender, String dob, String maritalStatus, String medicalCondition, String treatment, String storeFileName, String isUpdate, String isCompare, String expectedContent, String clickSaveButton, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only, validate New Starter Personal Information page.
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_PersonalInformation = waitChild("//span[contains(text(),'Personal Information')]", 10, 1, driver);
        if (sidebarItem_PersonalInformation.isEnabled()) {
            sidebarItem_PersonalInformation.click();
            logMessage("The sidebar - Personal Information in New Starter Forms page is clicked.");

            if (newGivenName != null) {
                WebElement textbox_GivenName = waitChild("//input[@id='FirstName']", 10, 1, driver);
                clearTextBox(textbox_GivenName);
                textbox_GivenName.sendKeys(newGivenName + Keys.TAB);
                Thread.sleep(2000);
                logMessage("New Given Name is input.");
            }

            if (newMiddleName != null) {
                WebElement textbox_MiddleName = waitChild("//input[@id='MiddleName']", 10, 1, driver);
                clearTextBox(textbox_MiddleName);
                textbox_MiddleName.sendKeys(newMiddleName + Keys.TAB);
            }

            if (newSurname != null) {
                WebElement textbox_Surname = waitChild("//input[@id='LastName']", 10, 1, driver);
                clearTextBox(textbox_Surname);
                textbox_Surname.sendKeys(newSurname + Keys.TAB);
            }

            if (newPreferredName != null) {
                WebElement textbox_PreferredName = waitChild("//input[@id='PreferredName']", 10, 1, driver);
                clearTextBox(textbox_PreferredName);
                textbox_PreferredName.sendKeys(newPreferredName + Keys.TAB);
            }

            if (gender != null) {
                driver.findElement(By.xpath("//div[@id='Gender']")).click();
                Thread.sleep(2000);
                WebElement dropdownlistItem = waitChild("//div[@class='list-item' and contains(text(),'" + gender + "')]", 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Gener '" + gender + "' is selected.");
                } else {
                    logError("Gener '" + gender + "' is NOT found.");
                    errorCounter++;
                }
            }

            if (dob != null) {
                WebElement textbox_DOB = driver.findElement(By.xpath("//input[@id='BirthDate']"));
                clearTextBox(textbox_DOB);
                textbox_DOB.sendKeys(dob + Keys.TAB);
                Thread.sleep(2000);
            }

            if (maritalStatus != null) {
                driver.findElement(By.xpath("//div[@id='MaritalStatus']")).click();
                Thread.sleep(2000);
                WebElement dropdownlistItem = waitChild("//div[@class='list-item' and contains(text(),'" + maritalStatus + "')]", 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Marital Status '" + maritalStatus + "' is selected.");
                } else {
                    logError("Maritial Satus '" + maritalStatus + "' is NOT found.");
                    errorCounter++;
                }
            }

            if (medicalCondition != null) {
                driver.findElement(By.xpath("//h5[contains(., 'Medical conditions')]//i[@class='icon-add']")).click();
                Thread.sleep(2000);
                logMessage("Add Medical Condition button is clicked.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//input[@id='condition']")).sendKeys(medicalCondition + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Medical Condition '" + medicalCondition + "' is input.");

                driver.findElement(By.xpath("//textarea[@id='treatment']")).sendKeys(treatment + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Treamment '" + treatment + "' is input.");

                logMessage("Screenshot before click Save Medical Condition button.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//form[contains(., 'Add Medical Condition')]//button[text()='Save']")).click();
                Thread.sleep(3000);
                logMessage("Save button is clicked.");
            }

            logMessage("Screenshot before click 'Save and continue' button.");
            logScreenshot(driver);

            if (clickSaveButton != null) {
                if (clickSaveButton.equals("1")) {
                    driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
                    Thread.sleep(3000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Save and continue button is clicked in New Starter - Personal Information screen.");
                    logScreenshot(driver);

                    WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Contact Details')]", 20, 1, driver);
                    if (lable_ContactDetails != null) {
                        logError("Contact Details page is shwon as NOT expected.");
                        errorCounter++;
                    } else {
                        logMessage("Contact Detail page is NOT shown as expected.");
                        WebElement element_NewStarterPage = driver.findElement(By.xpath("//div[@class='master-detail-content']"));
                        if (!validateTextValueInWebElementInUse(element_NewStarterPage, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) {
                            errorCounter++;
                        }

                    }
                }
            }


        } else {
            logWarning("The sidebar - Personal Information in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateNewStarter_ContactDetailsForm(String personalEmail, String personalMobile, String homeNumber, String residential_Country, String residential_Address, String residential_Suburb, String residential_PostCode, String residential_State, String residentialAddressUseForPostalAddress, String postal_Country, String postal_Address, String postal_Suburb, String postal_Postcode, String postal_State, String emergncy_ContactName, String emergencyContact_Relationship, String emergencyContact_MobilePhone, String emergencyContact_PhoneNumber, String clickSaveButton, String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isPassed = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_ContactDetails = waitChild("//span[contains(text(),'Contact Details')]", 10, 1, driver);
        if (sidebarItem_ContactDetails.isEnabled()) {
            sidebarItem_ContactDetails.click();
            logMessage("The Sidebar Item - Contact Details in New Starter Forms page is clicked.");

            if (personalEmail != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='PersonalEmail']"));
                clearTextBox(element);
                element.sendKeys(personalEmail + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Email '" + personalEmail + "' is input.");
            }

            if (personalMobile != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='PersonalMobile']"));
                clearTextBox(element);
                element.sendKeys(personalMobile + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Mobile '" + personalMobile + "' is input.");
            }

            if (homeNumber != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='HomeNumber']"));
                clearTextBox(element);
                element.sendKeys(homeNumber + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Home Number '" + homeNumber + "' is input.");
            }


            //////////////// Residential Address
            if (residential_Country != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:Country']"));
                clearTextBox(element);
                element.sendKeys(residential_Country + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Residential Country '" + residential_Country + "' is input.");
            }

            if (residential_Address != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:AddressLine1']"));
                clearTextBox(element);
                element.sendKeys(residential_Address + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Residential Address '" + residential_Address + "' is input.");
            }

            if (residential_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='res:Suburb']"));
                clearTextBox(element);
                element.sendKeys(residential_Suburb + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Residential Suburb '" + residential_Suburb + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);

                String itemToBeSelect = residential_Suburb + " " + residential_PostCode + " " + residential_State;
                String xpath_ItemList = "//div[@class='list-item' and contains(., '" + itemToBeSelect + "')]";
                WebElement list_Address = waitChild(xpath_ItemList, 15, 1, driver);
                if (list_Address != null) {
                    list_Address.click();
                    logMessage("Item '" + itemToBeSelect + "' is selected.");
                } else {
                    logMessage("Item '" + itemToBeSelect + "' is NOT selected.");
                    //errorCounter++;
                }

            }

            if (residentialAddressUseForPostalAddress != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='IsPostalAddressSameAsResidential']"));
                tickCheckbox(residentialAddressUseForPostalAddress, element, driver);
            }

            ///////////////////////// Postal Address
            if (postal_Country != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Country']"));
                clearTextBox(element);
                element.sendKeys(postal_Country + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Country '" + postal_Country + "' is input.");
            }

            if (postal_Address != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:AddressLine1']"));
                clearTextBox(element);
                element.sendKeys(postal_Address + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Address '" + postal_Address + "' is input.");
            }

            if (postal_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Suburb']"));
                clearTextBox(element);
                element.sendKeys(postal_Suburb + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Postal Suburb '" + postal_Suburb + "' is input.");
            }

            if (postal_Suburb != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='mail:Suburb']"));
                clearTextBox(element);
                element.sendKeys(postal_Suburb + Keys.TAB);
                Thread.sleep(2000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Postal Suburb '" + postal_Suburb + "' is input.");
                logMessage("Screenshot of the address list");
                logScreenshot(driver);

                String itemToBeSelect = postal_Suburb + " " + postal_Postcode + " " + postal_State;
                WebElement list_Address = waitChild("//div[@class='list-item' and contains(., '" + itemToBeSelect + "')]", 10, 1, driver);
                if (list_Address != null) {
                    list_Address.click();
                    logMessage("Item '" + itemToBeSelect + "' is selected.");
                } else {
                    logError("Item '" + itemToBeSelect + "' is NOT selected.");
                    errorCounter++;
                }

            }

            /////////////// Emergency Contact
            if (emergncy_ContactName != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='ContactName']"));
                clearTextBox(element);
                element.sendKeys(emergncy_ContactName + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact '" + emergncy_ContactName + "' is input.");
            }

            if (emergencyContact_Relationship != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='ContactRelationship']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_Relationship + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Relationship '" + emergencyContact_Relationship + "' is input.");
            }

            if (emergencyContact_MobilePhone != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='MobileContact']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_MobilePhone + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Mobile Phone '" + emergencyContact_MobilePhone + "' is input.");
            }

            if (emergencyContact_PhoneNumber != null) {
                WebElement element = driver.findElement(By.xpath("//input[@id='TelephoneContact']"));
                clearTextBox(element);
                element.sendKeys(emergencyContact_PhoneNumber + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Emergency Contact Phone Number '" + emergencyContact_PhoneNumber + "' is input.");
            }

            logMessage("Screenshot before click 'Save and continue' button.");
            logScreenshot(driver);

            if (clickSaveButton != null) {
                driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Save and continue button is clicked in New Starter - Contact Details screen.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(60, driver);
                logMessage("Save and continue button is clicked in New Starter - Contact Details screen.");
                logScreenshot(driver);
            }

            WebElement lable_Employment = waitChild("//div[@class='page-header']//h4[contains(text(),'Employment')]", 10, 1, driver);
            if (lable_Employment != null) {
                logError("Employment page is shwon as NOT expected.");
                errorCounter++;
            } else {
                logMessage("Employment page is NOT shown as expected.");

                WebElement element_NewStarterPage = driver.findElement(By.xpath("//div[@class='master-detail-content']"));
                if (!validateTextValueInWebElementInUse(element_NewStarterPage, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) {
                    errorCounter++;
                }
            }
        } else {
            logWarning("The sidebar - Contact Detials in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean syncChanges(WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = false;
        int errorCounter = 0;

        if (displaySettings_General(driver)){
            //////////////////////
            Thread.sleep(5000);
            //driver.navigate().to(driver.getCurrentUrl());
            driver.navigate().refresh();
            Thread.sleep(15000);
            waitSpinnerDisappear(120, driver);
            //Get original message from Web API Log
            String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

            logMessage("Check 'Sync from Payroll'");
            tickCheckbox("1", PageObj_General.checkbox_SyncFromPayroll(driver), driver);

            logMessage("UnCheck 'Sync Balances For Leave Report.");
            tickCheckbox("2", PageObj_General.checkbox_SyncBalancesForLeaveReport(driver), driver);
            logScreenshot(driver);

            PageObj_General.button_Sync(driver).click();
            Thread.sleep(3000);
            logMessage("Sync button is clicked.");

            //Click OK button
            PageObj_General.button_YesSyncAll(driver).click();
            logMessage("Yes, Sync All button is clicked.");
            Thread.sleep(20000);
            waitSpinnerDisappear(120, driver);

            String currentMessage = null;
            //////////////// Jim adjusted on 19/07/2021 for 20 mins /////////////////
            int i = 0;
            for (i = 0; i <= 80; i++) {  //Wait maxim for 20 min
                //Click Refresh button
                PageObj_General.button_RefreshSync(driver).click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
                //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
                if (currentMessage.contains("Successfully imported")) {
                    logMessage("It tooks " + i * 15 + " Seconds to Sync from Payroll.");
                    break;
                }
            }

            logMessage("The final Sync from Payroll Message is '" + currentMessage + "'.");
            logScreenshot(driver);

            if (expectedMessage != null) {
                if (currentMessage.contains(expectedMessage)) {
                    logMessage("Completed Sync from Payroll Successfullly");
                }
                else {
                    logError("Failed Sync from Payroll Data.");
                    System.out.println("Expected log message: '" + expectedMessage + "'");
                    errorCounter++;
                }
            }
            //////

            ///////////////////// Sync Balance for Leave Report ///////////////////
            logMessage("Refresh General Page.");
            SystemLibrary.refreshPage(driver);

            logMessage("UnCheck 'Sync from Payroll'");
            tickCheckbox("2", PageObj_General.checkbox_SyncFromPayroll(driver), driver);

            logMessage("Check 'Sync Balances For Leave Report.");
            tickCheckbox("1", PageObj_General.checkbox_SyncBalancesForLeaveReport(driver), driver);
            logScreenshot(driver);

            PageObj_General.button_Sync(driver).click();
            Thread.sleep(3000);
            logMessage("Sync button is clicked.");

            //Click OK button
            PageObj_General.button_YesSyncLeaveBalance(driver).click();
            logMessage("Yes, Sync Leave Balance button is clicked.");
            Thread.sleep(20000);
            waitSpinnerDisappear(120, driver);

            currentMessage = null;
            //////////////// Jim adjusted on 19/07/2021 for 20 mins /////////////////
            int j = 0;
            for (j = 0; j <= 80; j++) {  //Wait maxim for 20 min
                //Click Refresh button
                PageObj_General.button_RefreshSync(driver).click();
                Thread.sleep(15000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
                //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
                if (currentMessage.contains("Successfully synced leave balances")) {
                    logMessage("It tooks " + j * 15 + " Seconds to Sync Balance For Leave Report.");
                    break;
                }
            }

            logMessage("The final Sync Balance For Leave Report Message is '" + currentMessage + "'.");
            logScreenshot(driver);

            if (expectedMessage != null) {
                if (currentMessage.contains("Successfully synced leave")) {
                    logMessage("Completed Sync Balance for Leave Report Successfullly");
                    isPassed = true;
                }
                else {
                    logError("Failed Sync Balance for Leave Report.");
                    System.out.println("Expected log message: 'Successfully synced leave'");
                    errorCounter++;
                }
            }

            //////
        }else{
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean syncChanges_OLD2(WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);
        Thread.sleep(5000);
        //driver.navigate().to(driver.getCurrentUrl());
        driver.navigate().refresh();
        Thread.sleep(15000);
        waitSpinnerDisappear(120, driver);
        //Get original message from Web API Log
        String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

        PageObj_General.button_Sync(driver).click();
        Thread.sleep(3000);
        logMessage("Ellipsis button in Web API Sync section is clicked.");

        PageObj_General.button_SyncChanges(driver).click();
        Thread.sleep(3000);
        logMessage("Sync Changes button is clicked.");
        logScreenshot(driver);
        //Click OK button
        PageObj_General.button_YesSyncChanges(driver).click();
        logMessage("Yes, Sync Changes button is clicked.");
        Thread.sleep(20000);
        waitSpinnerDisappear(120, driver);
        logScreenshot(driver);

        String currentMessage = null;

        int i = 0;
        for (i = 0; i <= 40; i++) {  //Wait maxim for 10 min
            //Click Refresh button
            PageObj_General.button_RefreshSync(driver).click();
            Thread.sleep(15000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
            //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
            if (currentMessage.contains("Successfully imported") || currentMessage.contains(expectedMessage)) {
                logMessage("It tooks " + i * 15 + " Seconds to Sync changes.");
                break;
            }
        }

        logMessage("The final Sync Message is '" + currentMessage + "'.");
        logScreenshot(driver);

        if (expectedMessage != null) {
            if (currentMessage.contains(expectedMessage)) {
                logMessage("Completed Sync Changes Successfullly");
                isPassed = true;
            }

            else {
                logError("Failed Sync Changes.");
                System.out.println("Expected log message: '" + expectedMessage + "'");
                errorCounter++;
            }
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean syncChangesSimultaneously(WebDriver driver, WebDriver driver2, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        ////////// Driver 1 //////
        displaySettings_General(driver);
        Thread.sleep(5000);
        //driver.navigate().to(driver.getCurrentUrl());
        driver.navigate().refresh();
        Thread.sleep(5000);
        waitSpinnerDisappear(120, driver);
        //Get original message from Web API Log
        String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

        PageObj_General.button_Sync(driver).click();
        Thread.sleep(3000);
        logMessage("Ellipsis button in Web API Sync section is clicked.");

        PageObj_General.button_SyncChanges(driver).click();
        Thread.sleep(3000);
        logMessage("Sync Changes button is clicked.");
        logScreenshot(driver);

        //////////////////////////////// Driver 2 ///////////////////////
        displaySettings_General(driver2);
        Thread.sleep(5000);
        //driver.navigate().to(driver.getCurrentUrl());
        driver2.navigate().refresh();
        Thread.sleep(5000);
        waitSpinnerDisappear(120, driver2);
        //Get original message from Web API Log
        String originalMessage2 = PageObj_General.getTopMessageFromWebAPILog(driver2);

        PageObj_General.button_Sync(driver2).click();
        Thread.sleep(1000);
        logMessage("Ellipsis button in Web API Sync section is clicked.");

        PageObj_General.button_SyncAllData(driver2).click();
        Thread.sleep(1000);
        logMessage("Sync All Data button is clicked.");

        //////

        //Click OK button in driver 1 and driver 2
        PageObj_General.button_YesSyncChanges(driver).click();
        logMessage("Yes, Sync Changes button is clicked.");
        Thread.sleep(1000);
        waitSpinnerDisappear(120, driver);
        logScreenshot(driver);

        PageObj_General.button_YesSyncAll(driver2).click();
        logMessage("Yes, Sync All button is clicked in driver 2.");
        Thread.sleep(1000);
        waitSpinnerDisappear(120, driver2);

        logMessage("Refresh both driver 1 and driver 2 pages.");
        PageObj_General.button_RefreshSync(driver).click();
        PageObj_General.button_RefreshSync(driver2).click();

       String currentMessage = null;

        int i = 0;
        for (i = 0; i <= 40; i++) {  //Wait maxim for 10 min
            //Click Refresh button
            PageObj_General.button_RefreshSync(driver).click();
            Thread.sleep(15000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            currentMessage = PageObj_General.getTopMessageFromWebAPILog(driver);
            //if (PageObj_Integration.button_SyncAllData(driver).isEnabled()){
            if (currentMessage.contains("Successfully imported") || currentMessage.contains(expectedMessage)) {
                logMessage("It tooks " + i * 15 + " Seconds to Sync changes.");
                break;
            }
        }

        logMessage("The final Sync Message is '" + currentMessage + "'.");
        logScreenshot(driver);

        if (expectedMessage != null) {
            if (currentMessage.contains(expectedMessage)) {
                logMessage("Completed Sync Changes Successfullly");
                isPassed = true;
            }

            else {
                logError("Failed Sync Changes.");
                System.out.println("Expected log message: '" + expectedMessage + "'");
                errorCounter++;
            }
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static String removeWebAPIKeyFromErrorMessage(String errorMessage) {
        String[] splitBySpace  = errorMessage.split(" ");
        if(splitBySpace.length > 0) {
            return errorMessage.substring(splitBySpace[0].length() + 1);
        }
        return errorMessage;
    }

    public static boolean selectUserViaTeamsPage(String teamName, String storeFileName, String isUpdateStore, String isCompare, String firstName, String lastName, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        Integer teamPageType = displayTeamsPage(driver);
        Thread.sleep(5000);
        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
                Thread.sleep(8000);
            }
        } else {
            logError("Team: '" + teamName + "' is NOT found.");
            errorCounter++;
        }
        Thread.sleep(1000);
        driver.findElement(By.xpath("//span[contains(text(),'" + firstName + " " + lastName + "')]")).click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);
        logScreenshot(driver);
		Thread.sleep(3000);
        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateNewStarter_Employment_ViaAdmin(String storeFileName, String isUpdate, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_ContactDetails = waitChild("//span[contains(text(),'Contact Details')]", 10, 1, driver);
        if (sidebarItem_ContactDetails.isEnabled()) {
            sidebarItem_ContactDetails.click();
            logMessage("The Sidebar Item - Contact Details in New Starter Forms page is clicked.");
            Thread.sleep(2000);
            scrollToBottom(driver);
            Thread.sleep(3000);
            driver.findElement(By.xpath("//button[@class='button--primary button--wide' and text()='Save and continue']")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Save and continue button is clicked in New Starter - Contact Details screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Employment')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("Employment page is shwon as expected.");
            } else {
                logError("Employment page is NOT shown.");
                errorCounter++;
            }

        }

        WebElement sidebarItem_Employment = waitChild("//span[contains(text(),'Employment')]", 10, 1, driver);
        if (sidebarItem_Employment.isEnabled()) {
            sidebarItem_Employment.click();
            logMessage("The Sidebar Item - Employment in New Starter Forms page is clicked.");
			logScreenshot(driver);
			
            if (storeFileName != null) {
                WebElement employmentForm = waitChild("//div[@class='master-detail-content' and contains(., 'Employment')]", 10, 1, driver);
                if (employmentForm != null) {
                    WebElement subElement_HireDate = waitChild("//input[@id='HireDate']", 10, 1, driver);
                    if (!validateTextValueInWebElementInUse(employmentForm, storeFileName, isUpdate, isCompare, expectedContent, subElement_HireDate, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                } else {
                    logError("Employment form is NOT shown.");
                    errorCounter++;
                }
            }
            logMessage("Screenshot before click 'Continue' button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[@class='button--primary button--wide'][contains(text(),'ontinue')]")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Continue button is clicked in New Starter - Contact Details screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Tax Details')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("New Starter - Tax Detail page is shwon as expected.");
            } else {
                logError("New Starter - Tax Detail page is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The sidebar - Employment in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static String forcastLeave(String firstName, String lastName, String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String sHoursNotYetProcessed, String grantedImmediately, String leaveType, String serverName, String payrollDBName) throws InterruptedException, SQLException, ClassNotFoundException {
        String result = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        double leaveEntitlementsPerYearInDays = Double.parseDouble(sLeaveEntitlementsPerYearInDays);
        double contractHoursPerDay = Double.parseDouble(sContractHoursPerDay);
        double currentLeaveEntitlementHours = Double.parseDouble(sCurrentLeaveEntitlementHours);


        LocalDate lastLeaveEntitlementDate = LocalDate.parse(sLeaveEntitlementDate, dtf);

        if (sLeaveForecastDate.contains(";")) {
            sLeaveForecastDate = getExpectedDate(sLeaveForecastDate, null);
        }

        Integer iLeaveType=100;
        switch (leaveType){
            case "Annual Leave":
                iLeaveType=1;
                break;
            case "Personal Leave":
                iLeaveType=0;
                break;
            case "Long Service Leave":
                iLeaveType=2;
                break;
            case "User Defined Leave":
                iLeaveType=111;
                break;
            case "Carers Leave":
                iLeaveType=0;
                break;
            case "Sick Leave":
                iLeaveType=0;
                break;
            case "Time In Lieu #1":
                iLeaveType=111;
                break;
        }

        LocalDate leaveForecastDate = LocalDate.parse(sLeaveForecastDate, dtf);
        String employeeCode=DBManage.getEmployeeCodeFromPayrollDB(serverName, payrollDBName, firstName, lastName);
        String sCurrentEOPDate=DBManage.getEmployeeEOPDate(serverName, payrollDBName, employeeCode, iLeaveType);

        LocalDate currentEOPDate=LocalDate.parse(sCurrentEOPDate, dtf);

        //In micrOpay, if forecast date is before the EOP date, the leave Forcasting in the past won't be calculated.
        // Only the current EOP date will be used for calculation. Script Adjusted by Jim on 13/02/2020
        long daysFromLeaveForecastDateToCurrentEOPDate=ChronoUnit.DAYS.between(currentEOPDate, leaveForecastDate);
        if (daysFromLeaveForecastDateToCurrentEOPDate<0){
            leaveForecastDate=currentEOPDate;
        }

        long daysToCalculate = ChronoUnit.DAYS.between(lastLeaveEntitlementDate, leaveForecastDate);
        double yearsToCalculate = Math.floor((double) (daysToCalculate / 365));

        /*
        System.out.println("Contract hours per day: "+contractHoursPerDay);
        System.out.println("Leave Entitlement Days Per Yea: "+leaveEntitlementsPerYearInDays);

        System.out.println("Current Leave Entitlement Hours: "+currentLeaveEntitlementHours);
        System.out.println("Current Leave Entilement Date: "+sLeaveEntitlementDate);
        System.out.println("Leave Forecast Date: "+sLeaveForecastDate);
        System.out.println("Days for calculation: "+Long.toString(daysToCalculate));
        System.out.println("Years for calculation: "+Double.toString(yearsToCalculate));
        */

        double leaveEntitlementPerYearInHours = leaveEntitlementsPerYearInDays * contractHoursPerDay;
        double accrualHours = 0;

        ////////////////////////
        if (granted != null) {
            if (granted.equals("1")) {
                logMessage("Leave is Granted.");
                accrualHours = leaveEntitlementPerYearInHours * yearsToCalculate;
            } else {
                accrualHours = (leaveEntitlementPerYearInHours / 365.25) * daysToCalculate;
            }
        } else {
            accrualHours = (leaveEntitlementPerYearInHours / 365.25) * daysToCalculate;
        }

        ///////////////

        if (grantedImmediately != null) {
            if (grantedImmediately.equals("1")) {
                logMessage("Leave is Granted immediately.");
                accrualHours = accrualHours + leaveEntitlementPerYearInHours;
            }
        }

        double totalAccrualHoursAsAtForecastDate = accrualHours + currentLeaveEntitlementHours;

        if (sHoursNotYetProcessed != null) {
            double hoursNotYetProcessed = Double.parseDouble(sHoursNotYetProcessed);
            totalAccrualHoursAsAtForecastDate = totalAccrualHoursAsAtForecastDate - hoursNotYetProcessed;
        }
        double totalAccrualDaysAsAtForecastDate = totalAccrualHoursAsAtForecastDate / contractHoursPerDay;

        /*
        System.out.println("Leave Entitlement per yesr in Hrs: "+leaveEntitlementPerYearInHours);
        System.out.println("Accrual Hours: "+accrualHours);
        System.out.println("Accrual Days: "+accrualHours/contractHoursPerDay);
        */

        logMessage("Total Expected Accrual Days as at Forecast Date: " + totalAccrualDaysAsAtForecastDate);
        logMessage("Total Expected Accrual Hours as at Forecast Date: " + totalAccrualHoursAsAtForecastDate);

        result = String.valueOf(totalAccrualDaysAsAtForecastDate) + ";" + String.valueOf(totalAccrualHoursAsAtForecastDate);
        return result;
    }


    //////////////////////

    public static boolean validateLeaveBalance_ViaLeavePage(String firstName, String middleName, String lastName, String preferredName, String leaveType, String sLeaveEntitlementsPerYearInDays, String sContractHoursPerDay, String granted, String sCurrentLeaveEntitlementHours, String sLeaveEntitlementDate, String sLeaveForecastDate, String expectedLeaveEntitlementDays, String expectedLeaveEntitlementHours, String sHoursNotYetProcessed, String grantedImmediately, String testSerialNo, String serverName, String payrollDBName, WebDriver driver) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        if (waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver) != null) {
            String userFullName = GeneralBasic.getUserFullname(firstName, middleName, lastName, preferredName);
            WebElement label_Name = waitChild("//h3[@class='bc-name' and contains(text(), '" + userFullName + "')]", 10, 1, driver);
            if (label_Name != null) {
                logMessage("User '" + firstName + "' leave page is shown.");
            } else {
                displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
            }
        } else {
            displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
        }

        if (sLeaveForecastDate != null) {
            driver.findElement(By.xpath("//button[@id='balances-calendar-button']")).click();
            Thread.sleep(3000);
            logMessage("Calendar Header is clicked.");
            logScreenshot(driver);

            selectDateInCalendar(sLeaveForecastDate, driver);
        }

        String currentLeaveEntitlementDays = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
        String currentLeaveEntitlementHours = PageObj_Leave.getLeaveBalanceHoursFromLeavePage(leaveType, driver);

        if ((expectedLeaveEntitlementDays == null) && (expectedLeaveEntitlementHours == null)) {
            String[] expectedLeaveResult = splitString(forcastLeave(firstName, lastName, sLeaveEntitlementsPerYearInDays, sContractHoursPerDay, granted, sCurrentLeaveEntitlementHours, sLeaveEntitlementDate, sLeaveForecastDate, sHoursNotYetProcessed, grantedImmediately, leaveType, serverName, payrollDBName), ";");
            expectedLeaveEntitlementDays = expectedLeaveResult[0];
            expectedLeaveEntitlementHours = expectedLeaveResult[1];
        } else {
            logMessage("The Total Expected Accrual Days as At Forecast Date: " + expectedLeaveEntitlementDays);
            logMessage("The total Expected Accrual Hours as at Forecast Date: " + expectedLeaveEntitlementHours);
        }

        double differenceInDays = Double.valueOf(currentLeaveEntitlementDays) - Double.valueOf(expectedLeaveEntitlementDays);


        logMessage("The leave difference in Days is " + differenceInDays);
        if (abs(differenceInDays) > 1.00) {
            errorCounter++;

            logError("The current " + leaveType + " balance in days is NOT shown as expected.");
        } else {
            logMessage("The current " + leaveType + " balance in days is shown as expected.");
        }

        double differenceInHours = Double.valueOf(currentLeaveEntitlementHours) - Double.valueOf(expectedLeaveEntitlementHours);
        logMessage("The leave difference in Hours is " + differenceInHours);
        if (!leaveType.equals("Long Service Leave")) {
            if (abs(differenceInHours) > 5.00) {
                errorCounter++;
                logError("The current " + leaveType + " balance in Hours is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveType + " balance in Hours is shown as expected.");
            }
        } else {
            if (abs(differenceInHours) > 90.00) {
                errorCounter++;

                logError("The current " + leaveType + " balance in Hours is NOT shown as expected.");
            } else {
                logMessage("The current " + leaveType + " balance in Hours is shown as expected.");
            }
        }


        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean downloadLeaveHistoryReport(String storeFileName, String isUpdateStore, String isCompare, String teamName, String expectedContent, String testSerialNo, WebDriver driver) throws Exception {
        boolean isDownloaded = false;
        int errorCounter = 0;

        String teamNameInFileName=teamName.toLowerCase();
        teamNameInFileName=teamNameInFileName.replace(" ", "-");
        ////////////////////
        String fileName = "leave-history-"+teamNameInFileName+"-" + getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + fileName;
        String strNewDownloadFileFullPathName = workingFilePath + "LeaveHistoryReport_" + getCurrentTimeAndDate() + ".txt";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), "leave-history-");

        displayDashboard(driver);
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {
            WebElement topButton= waitChild("//button[@class='button--plain add-button' and @xpath='1']", 5, 1, driver);
            if (topButton==null){
                topButton= waitChild("//button[@class='button--plain add-button']", 5, 1, driver);
                if (topButton!=null){
                    topButton.click();
                    logMessage("Ellipsis button is clicked.");
                    Thread.sleep(2000);
                    logScreenshot(driver);

                    WebElement menu_LeaveHistoryReport= waitChild("//a[contains(text(),'Leave history report')]", 2, 1, driver);
                    if (menu_LeaveHistoryReport!=null){
                        menu_LeaveHistoryReport.click();
                        logMessage("Leave History Report is clicked.");

                        /////////////////////
                        File file = new File(getGoogleChromeDownloadPath(), fileName);
                        int i = 0;
                        while (!file.exists()) {
                            i++;
                            Thread.sleep(1000);
                            if (i > 300) break;
                        }

                        if (i < 300) {
                            logMessage("It takes " + i + " Seconds to download Leave History report.");
                        } else {
                            logError("Download Leave History report overdue.");
                            errorCounter++;
                        }

                        ///////////////////////// Jim adjusted on 13/01/2021 /////////////////
                        if (errorCounter==0){
                            String fileContent=getStingFromFile(strCurrentDownloadFileFullPathName);
                            fileContent=fileContent.replace(testSerialNo, "ABC123");

                            createTextFile(strNewDownloadFileFullPathName, fileContent);
                            logMessage("Leave History Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

                            if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, strNewDownloadFileFullPathName, storeFileName, expectedContent)) {
                                errorCounter++;
                            }
                        }else{
                            logError("No further file validation performanced.");
                        }
                        //////
                    }else{
                        logError("Menu - Leave History Report is NOT shown.");
                    }


                }else{
                    errorCounter++;
                    logError("Ellipsis button in Team page is NOT found.");
                }
            }

        }


        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }


    public static boolean downloadLeaveBalanceReport_New(String storeFileName, String isUpdateStore, String isCompare, String teamName, String expectedContent, WebDriver driver) throws Exception {
        boolean isDownloaded = false;
        int errorCounter = 0;

        String teamNameInFileName=teamName.toLowerCase();
        teamNameInFileName=teamNameInFileName.replace(" ", "-");
        ////////////////////
        String fileName = "leave-balances-"+teamNameInFileName+"-" + getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + fileName;
        String strNewDownloadFileFullPathName = workingFilePath + "LeaveBalancesReport_" + getCurrentTimeAndDate() + ".txt";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), "leave-balances-");

        displayDashboard(driver);
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {
            WebElement topButton= waitChild("//button[@class='button--plain add-button' and @xpath='1']", 5, 1, driver);
            if (topButton==null){
                topButton= waitChild("//button[@class='button--plain add-button']", 5, 1, driver);
                if (topButton!=null){
                    topButton.click();
                    logMessage("Ellipsis button is clicked.");
                    Thread.sleep(2000);
                    logScreenshot(driver);

                    WebElement menu_LeaveHistoryReport= waitChild("//a[contains(text(),'Leave balances report')]", 2, 1, driver);
                    if (menu_LeaveHistoryReport!=null){
                        menu_LeaveHistoryReport.click();
                        logMessage("Leave Balances Report is clicked.");
						Thread.sleep(2000);

                        /////////////////////
                        File file = new File(getGoogleChromeDownloadPath(), fileName);
                        int i = 0;
                        while (!file.exists()) {
                            i++;
                            Thread.sleep(1000);
                            if (i > 300) break;
                        }

                        if (i < 300) {
                            logMessage("It takes " + i + " Seconds to download Leave Balances report.");
                        } else {
                            logError("Download Leave Balances report overdue.");
                            errorCounter++;
                        }

                        //Move download file into working folder
                        if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
                        logMessage("Leave Balances Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

                        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, strNewDownloadFileFullPathName, storeFileName, expectedContent)) errorCounter++;

                    }else{
                        logError("Menu - Leave Balance Report is NOT shown.");
                    }

                }else{
                    errorCounter++;
                    logError("Ellipsis button in Team page is NOT found.");
                }
            }

        }


        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean downloadAppliedLeaveByDateReport(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String teamName, String startDate, String endDate, String includeApprovedLeave, String testSerialNo, WebDriver driver) throws Exception {
        boolean isDownloaded = false;
        int errorCounter = 0;

        String teamNameInFileName=teamName.toLowerCase();
        teamNameInFileName=teamNameInFileName.replace(" ", "-");
        ////////////////////
        String fileName = "Applied-leave-by-date-"+teamNameInFileName+"-" + getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + fileName;
        String strNewDownloadFileFullPathName = workingFilePath + "AppliedLeaveByDateReport_" + getCurrentTimeAndDate() + ".txt";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), "Applied-leave-by-date-");

        displayDashboard(driver);
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {
            WebElement topButton= waitChild("//button[@class='button--plain add-button' and @xpath='1']", 5, 1, driver);
            if (topButton==null){
                topButton= waitChild("//button[@class='button--plain add-button']", 5, 1, driver);
                if (topButton!=null){
                    topButton.click();
                    logMessage("Ellipsis button is clicked.");
                    Thread.sleep(2000);
                    logScreenshot(driver);

                    WebElement menu_LeaveHistoryReport= waitChild("//a[contains(text(),'Applied leave by date')]", 2, 1, driver);
                    if (menu_LeaveHistoryReport!=null){
                        menu_LeaveHistoryReport.click();
                        logMessage("Menu - Applied Leave by date is clicked.");
                        Thread.sleep(5000);


                        ////////////////////////////
                        if (startDate!=null){

                            if (startDate.contains(";")){
                                startDate= getExpectedDate(startDate, null);
                            }

                            //////////////////
                            WebElement textbox_StartDate=driver.findElement(By.xpath("//input[@id='startDate']"));
                            textbox_StartDate.click();
                            Thread.sleep(3000);
                            textbox_StartDate.sendKeys(startDate);
                            logMessage("Start Date: '"+startDate+"' is input.");
                            Thread.sleep(2000);
                        }

                        if (endDate!=null){
                            if (endDate.contains(";")){
                                endDate= getExpectedDate(endDate, null);
                            }

                            //////////////////
                            WebElement textbox_StartDate=driver.findElement(By.xpath("//input[@id='endDate']"));
                            textbox_StartDate.click();
                            Thread.sleep(3000);
                            textbox_StartDate.sendKeys(endDate);
                            logMessage("The End Date: '"+endDate+"' is input.");
                            Thread.sleep(2000);
                        }

                  
                        if (includeApprovedLeave!=null){
                            WebElement checkbox_IncludeApprovedLeave=driver.findElement(By.xpath("//input[@id='isIncludeApprovedLeave']"));
                            tickCheckbox(includeApprovedLeave, checkbox_IncludeApprovedLeave, driver);
                        }

                        logMessage("Screenshot before click Download button.");
                        logScreenshot(driver);

                        driver.findElement(By.xpath("//button[@class='button--primary' and text()='Download']")).click();
                        Thread.sleep(60000);
                        logMessage("Button download is clicked.");

                        /////////////////////
                        File file = new File(getGoogleChromeDownloadPath(), fileName);
                        int i = 0;
                        while (!file.exists()) {
                            i++;
                            Thread.sleep(1000);
                            if (i > 300) break;
                        }

                        if (i < 300) {
                            logMessage("It takes " + i + " Seconds to download Applied Leave By Date report.");
                        } else {
                            logError("Download Applied Leave By Date report overdue.");
                            errorCounter++;
                        }

                        String fileContent=getStingFromFile(strCurrentDownloadFileFullPathName);
                        fileContent=fileContent.replace(testSerialNo, "ABC123");

                        createTextFile(strNewDownloadFileFullPathName, fileContent);
                        logMessage("Applied Leave By Date Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

                        //deleteFilesInFolder(getGoogleChromeDownloadPath(), fileName);

                        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, strNewDownloadFileFullPathName, storeFileName, expectedContent)) errorCounter++;
                        //Move download file into working folder
                        //if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;


                    }else{
                        logError("Menu - Applied Leave By Date Report is NOT shown.");
                    }


                }else{
                    errorCounter++;
                    logError("Ellipsis button in Team page is NOT found.");
                }
            }

        }


        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }


    public static boolean validateESSNewStarter_IntroductionPage_ViaAdim(String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String teamName, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Login Admin First
        boolean isDone = false;
        int errorCounter = 0;

        selectUserViaTeamsPage(teamName, storeFileName, isUpdateStore, isCompare, firstName, lastName, expectedContent, testSerialNo, emailDomainName, driver);

        String xpath_LableNewStarter = "//h3[contains(text(),'New Starter Forms')]";
        WebElement lable_NewStarter = waitChild(xpath_LableNewStarter, 10, 1, driver);
        if (lable_NewStarter != null) {
            logMessage("Logon as New Starter '" + firstName + "' successfully.");

            if (storeFileName != null) {
                WebElement lable_Welcome = waitChild("//div[@class='master-detail-content']//h4[contains(., 'Introduction')]", 10, 1, driver);
                if (lable_Welcome != null) {
                    logMessage("Introduction message is shown.");
                    WebElement Introduction = waitChild("//div[@class='panel-body']", 10, 1, driver);
                    if (Introduction != null) {
                        if (!validateTextValueInWebElementInUse(Introduction, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                            errorCounter++;
                    } else {
                        logError("Introduction body panel is NOT shown.");
                        errorCounter++;
                    }
                } else {
                    logError("Introduction message is NOT shown correctly.");
                    errorCounter++;
                }


            }

        } else {
            logError("Failed logon New Starter '" + firstName + "'.");
            errorCounter++;
        }
        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean validateESSNewStarter_WelcomePage(String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement sidebarItem_Welcome = waitChild("//span[contains(text(),'Welcome')]", 10, 1, driver);
        if (sidebarItem_Welcome.isEnabled()) {
            sidebarItem_Welcome.click();
            logMessage("The Sidebar Item - Employment in New Starter Forms page is clicked.");
            Thread.sleep(10000);
            logScreenshot(driver);

            if (storeFileName != null) {
                logMessage("Welcome message is shown.");
                WebElement welcomePage = waitChild("//div[@class='panel-body']", 10, 1, driver);
                if (welcomePage != null) {
                    if (!validateTextValueInWebElementInUse(welcomePage, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                } else {
                    logError("Welcome body panel is NOT shown.");
                    errorCounter++;
                }
            } else {
                logError("Welcome message is NOT shown correctly.");
                errorCounter++;
            }
        }
        logScreenshot(driver);

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStarter_Employment_ViaAdmin(String basisOfEmployment, String payrollCompany, String jobTitle, String hireDate, String reportingManagerFirstName, String reportingManagerMiddleName, String reportingManagerLastName, String reportingManagerPreferredName, String baseAnnualSalary, String hoursOfWork, String perUnit, String normalHourlyRate, String taxedAs, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;
        WebElement itemValidation = null;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_Employment = waitChild("//span[contains(text(),'Employment')]", 10, 1, driver);
        if (sidebarItem_Employment.isEnabled()) {
            sidebarItem_Employment.click();
            logMessage("The Sidebar Item - Employment in New Starter Forms page is clicked.");
            Thread.sleep(6000);
            logScreenshot(driver);
            if (basisOfEmployment != null) {
                driver.findElement(By.xpath("//div[@id='BasisOfEmployment']")).click();
                Thread.sleep(2000);
                logMessage("Basis Of Employment is clicked.");

                String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + basisOfEmployment + "']";
                WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
                if (itemList != null) {
                    itemList.click();
                    logMessage("Item '" + basisOfEmployment + "' is selected.");
                } else {
                    logError("Item '" + basisOfEmployment + "' is NOT found.");
                    driver.findElement(By.xpath("//div[@id='BasisOfEmployment']")).click();
                    errorCounter++;
                }
            }

            if (payrollCompany != null) {
                driver.findElement(By.xpath("//div[@id='PayrollCompany']")).click();
                Thread.sleep(2000);
                logMessage("Payroll Company is clicked.");

                String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + payrollCompany + "']";
                WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
                if (itemList != null) {
                    itemList.click();
                    logMessage("Item '" + payrollCompany + "' is selected.");
                } else {
                    logError("Item '" + payrollCompany + "' is NOT found.");
                    driver.findElement(By.xpath("//div[@id='PayrollCompany']")).click();
                    errorCounter++;
                }
            }

            if (jobTitle != null) {
                driver.findElement(By.xpath("//div[@id='JobTitleSelect']")).click();
                Thread.sleep(2000);
                logMessage("Job Title is clicked.");

                String xpath_ItemList = "//div[contains(@class, 'list-item') and text()='" + jobTitle + "']";
                WebElement itemList = waitChild(xpath_ItemList, 10, 1, driver);
                if (itemList != null) {
                    itemList.click();
                    logMessage("Item '" + jobTitle + "' is selected.");
                } else {
                    logError("Item '" + jobTitle + "' is NOT found.");
                    driver.findElement(By.xpath("//div[@id='JobTitleSelect']")).click();
                    errorCounter++;
                }
            }

            if (hireDate != null) {

                if (hireDate.contains(";")) {
                    hireDate = getExpectedDate(hireDate, null);
                }

                WebElement text_HireDate = driver.findElement(By.xpath("//input[@id='HireDate']"));
                text_HireDate.click();
                Thread.sleep(3000);
                text_HireDate.sendKeys(hireDate);
                text_HireDate.sendKeys(Keys.TAB);

                logMessage("Start Date: " + hireDate + " is input.");
                Thread.sleep(3000);
            }

            if (reportingManagerFirstName != null) {
                WebElement textbox_Search = driver.findElement(By.xpath("//div[@class='text-with-selector']//input[@id='searchField']"));
                textbox_Search.click();
                textbox_Search.sendKeys(reportingManagerFirstName);
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(30, driver);
                logMessage("Reporting Manager First Name '" + reportingManagerFirstName + "' is input.");
                logScreenshot(driver);

                itemValidation = waitChild("//span[contains(text(),'No matches found')]", 5, 1, driver);
                if (itemValidation != null) {
                    logError("Report Manager is not found.");
                    errorCounter++;
                } else {
                    String userFullName = getUserFullname(reportingManagerFirstName, reportingManagerMiddleName, reportingManagerLastName, reportingManagerPreferredName);
                    WebElement managerList = waitChild("//div[@class='list-item' and contains(., '" + userFullName + "')]", 10, 1, driver);

                    if (managerList != null) {
                        managerList.click();
                        Thread.sleep(3000);
                        logMessage("Reporting manager '" + userFullName + "' is selected.");
                    } else {
                        logWarning("Reporting manager '" + userFullName + "' is NOT found.");
                        errorCounter++;
                    }
                }


            }

            if (baseAnnualSalary != null) {
                WebElement baSalary = driver.findElement(By.xpath("//input[@id='baseAnnualSalary']"));
                clearTextBox(baSalary);
                baSalary.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), baseAnnualSalary);
                Thread.sleep(1000);
                logMessage("baseAnnualSalary '" + baseAnnualSalary + "' is input.");
            }

            if (hoursOfWork != null) {
                WebElement hoursWork = driver.findElement(By.xpath("//input[@id='hoursOfWork']"));
                clearTextBox(hoursWork);
                hoursWork.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), hoursOfWork);
                Thread.sleep(1000);
                logMessage("Hours of Work '" + hoursOfWork + "' is input.");
            }



            if (perUnit != null) {
                driver.findElement(By.xpath("//div[@id='HoursOfWorkFrequency']")).click();
                Thread.sleep(3000);
                String xpath_perUnit = "//div[contains(@class, 'list-item') and text()='" + perUnit + "']";
                WebElement element_perUnit = waitChild(xpath_perUnit, 10, 1, driver);
                if (element_perUnit != null) {
                    element_perUnit.click();
                    Thread.sleep(2000);
                    logMessage("Unit '" + perUnit + "' is selected.");
                } else {
                    logError("Unit '" + perUnit + "' is NOT found.");
                }
            }

            logMessage("Screenshot after select Pay Detail items.");
            logScreenshot(driver);

            Thread.sleep(2000);
            if ((normalHourlyRate != null) && (baseAnnualSalary != null)) {
                //   if ((residential_State != null) && (!residential_Country.equals("Australia"))) {
                double currentAnnualSalary = Double.valueOf(baseAnnualSalary);
                double currentHoursOfWork = Double.valueOf(hoursOfWork);

                if (normalHourlyRate.equals("666666")) {
                    double expectedHourlyRate = calculateHourlyRateViaNewStarterPayDetailsScreen(currentAnnualSalary, currentHoursOfWork, perUnit);
                    String str_CurrentHourlyRate = driver.findElement(By.xpath("//input[@id='normalRate']")).getAttribute("value");
                    double currentHourlyRate = Double.valueOf(str_CurrentHourlyRate);

                    if ((currentHourlyRate - expectedHourlyRate) < 0.5) {
                        logMessage("The current Hourly Rate is equal to the expected Hourly Rate.");
                    } else {
                        logMessage("The current Hourly Rate is " + driver.findElement(By.xpath("//input[@id='normalRate']")).getText());
                        logMessage("The expected hourly rate is " + String.valueOf(expectedHourlyRate));
                        logError("The current Hourly Rate is NOT equal to expected Hourly Rate.");
                        errorCounter++;
                    }
                }
            }

            else {
                WebElement normalRate = driver.findElement(By.xpath("//input[@id='normalRate']"));
                clearTextBox(normalRate);
                //  normalRate.sendKeys(normalHourlyRate + Keys.TAB);
                normalRate.sendKeys(Keys.HOME,Keys.chord(Keys.SHIFT,Keys.END), normalHourlyRate);
                Thread.sleep(2000);
                logMessage("Normal  HourlyRate '" + normalHourlyRate + "' is input.");

             /*
                    Thread.sleep(2000);
                    driver.findElement(By.xpath("//input[@id='normalRate']")).sendKeys(normalHourlyRate + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Normal Hour Rate '" + normalHourlyRate + "' is input.");
              */
            }

            if (taxedAs != null) {
                if (taxedAs.equals("TFN")) {
                    driver.findElement(By.xpath("//input[@id='1']")).click();
                } else if (taxedAs.equals("ABN")) {
                    driver.findElement(By.xpath("//input[@id='0']")).click();
                }
            }

            logMessage("Screenshot before click 'Continue' button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[@class='button--primary button--wide'][contains(text(),'ontinue')]")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logMessage("Continue button is clicked in New Starter - Contact Details screen.");
            logScreenshot(driver);

            WebElement lable_ContactDetails = waitChild("//div[@class='page-header']//h4[contains(text(),'Tax Details')]", 10, 1, driver);
            if (lable_ContactDetails != null) {
                logMessage("New Starter - Tax Detail page is shwon as expected.");
            } else {
                logError("New Starter - Tax Detail page is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The sidebar - Employment in New Starter Forms page is NOT enabled. ");
            errorCounter++;
        }


        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editNewStater_AfterReadyToApproveForms (String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String teamName, String choiceOfReadyToApproveForms, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        String fullName = firstName + " " + lastName;
        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
                Thread.sleep(3000);
            }
        } else {
            logError("Team: '" + teamName + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) {

            WebElement element = waitChild("//span[contains(text(),'" + firstName + " " + lastName + "')]", 15, 1, driver);
            if (element != null) {
                element.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage("New Starter '" + firstName + " " + lastName + "' is clicked.");
                logScreenshot(driver);


                if (choiceOfReadyToApproveForms != null) {
                    switch (choiceOfReadyToApproveForms) {
                        case "1":
                            driver.findElement(By.xpath("//div[@id='page-controls']//div//button[contains(., 'Send to payroll')]")).click();
                            GeneralBasic.waitSpinnerDisappear(120, driver);
                            logMessage("Send to payroll button is clicked.");
                            logScreenshot(driver);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//div[@id='page-controls']//div//button[contains(., 'Edit details')]")).click();
                            GeneralBasic.waitSpinnerDisappear(120, driver);
                            logMessage("Edit details button is clicked.");
                            logScreenshot(driver);
                            break;
                        case "3":
                            driver.findElement(By.xpath("//div[@id='page-controls']//button[contains(., 'Remove')]")).click();
                            GeneralBasic.waitSpinnerDisappear(120, driver);
                            logMessage("Remove button is clicked.");
                            logScreenshot(driver);
                            WebElement removeButton = waitChild("//button[contains(@class,'button--danger') and text()='Yes, remove']", 2, 1, driver);
                            if (removeButton != null) {
                                removeButton.click();
                                Thread.sleep(3000);
                                logMessage("Button 'Yes, remove' is clicked.");
                            } else {
                                logError("Remove button is NOT found.");
                                errorCounter++;
                            }
                            break;
                    }
                }

                //Validate page after click send to payroll button.
                if (storeFileName != null) {
                    logMessage("Start validating New Starter detail page after clicking 'Send to Payroll' button.");
                    element = driver.findElement(By.xpath("//div[@class='panel-body']"));
                    if (!validateTextValueInWebElementInUse(element, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                        errorCounter++;
                }

            } else {
                logError("New starter '" + fullName + "' is NOT found.");
                errorCounter++;
            }


        } else {
            logError("Team 'New Starters' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean editNewStarter_TaxDetails_nochangeDeclaration(String tfnOption, String storeFileName, String isUpdate, String isCompare, String expectedContent, String taxFileNumber, String previousFamilyName, String choiceOfBasisOfEmp, String choiceOfTaxPurposes, String choiceOfTaxFreeThreshold, String choiceOfDebitLoan, String choiceOfFinancialSuppl, String abn, String instalmentRate, String CommissioneInstalmentRate, String rateOfWithHolding, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Logon as New Starter using temp password only
        boolean isDone = false;
        int errorCounter = 0;

        WebElement button_Star = waitChild("//button[@class='button--primary button--wide' and text()='Start']", 10, 1, driver);
        if (button_Star != null) {
            button_Star.click();
            logMessage("Start button is clicked.");
            Thread.sleep(3000);
            logScreenshot(driver);
        }

        WebElement sidebarItem_TaxDetails = waitChild("//span[contains(text(),'Tax Details')]", 10, 1, driver);
        if (sidebarItem_TaxDetails != null) {
            if (sidebarItem_TaxDetails.isEnabled()) {
                sidebarItem_TaxDetails.click();
                logMessage("The Sidebar Item - Tax Details in New Starter Forms page is clicked.");
                Thread.sleep(3000);

                if (tfnOption != null) {
                    switch (tfnOption) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='HaveTfn']")).click();
                            logMessage("Option - I have TFN is checked.");
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='HaveProvidedOrWillProvide']")).click();
                            logMessage("Option - I have TFN or will Provide is checked.");
                            break;
                        case "3":
                            driver.findElement(By.xpath("//input[@id='MadeASeparateApplication']")).click();
                            logMessage("Option - I have made a separate application is checked.");
                            break;
                        case "4":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionUnderAge']")).click();
                            logMessage("Option - I am claiming an exemption Under 18 is checked.");
                            break;
                        case "5":
                            driver.findElement(By.xpath("//input[@id='ClaimingAnExemptionOther']")).click();
                            logMessage("Option - I am claiming an exemption Other is checked.");
                            break;
                    }

                }
                //////////////////////////

                if (taxFileNumber != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='TaxFileNumber']"));
                    clearTextBox(element);
                    element.sendKeys(taxFileNumber + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + taxFileNumber + "' is Added.");
                }

                if (previousFamilyName != null) {
                    WebElement element = driver.findElement(By.xpath("//input[@id='PreviousFamilyName']"));
                    clearTextBox(element);
                    element.sendKeys(previousFamilyName + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Previous Family Name '" + previousFamilyName + "' is Added.");
                }

                if (choiceOfBasisOfEmp != null) {
                    Thread.sleep(2000);
                    //SystemLibrary.scrollFromTopToquarterWindow(driver);
                    switch (choiceOfBasisOfEmp) {
                        case "1":
                            WebElement fullTimexpath=driver.findElement(By.xpath("//label[contains(text(),'Full-time employment')]"));
                            if (!isElementClickable(fullTimexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            fullTimexpath.click();
                            logMessage("Option - Full Time Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement partTimexpath=driver.findElement(By.xpath("//label[contains(text(),'Part-time employment')]"));
                            if (!isElementClickable(partTimexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            partTimexpath.click();
                            logMessage("Option - Part Time Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "3":
                            WebElement labourhirexpath=driver.findElement(By.xpath("//label[contains(text(),'Labour hire')]"));
                            if (!isElementClickable(labourhirexpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            labourhirexpath.click();
                            logMessage("Option - Labour Hire is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "4":
                            WebElement superannuationxpath=driver.findElement(By.xpath("//label[contains(text(),'Superannuation or annuity income stream')]"));
                            if (!isElementClickable(superannuationxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            superannuationxpath.click();
                            logMessage("Option - Super Annuation Or Annuity Income Stream is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "5":
                            WebElement casualempxpath=driver.findElement(By.xpath("//label[contains(text(),'Casual employment')]"));
                            if (!isElementClickable(casualempxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            casualempxpath.click();
                            logMessage("Option - Casual Employment is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfTaxPurposes != null) {
                    //     SystemLibrary.scrollFromTopToquarterWindow(driver);
                    Thread.sleep(2000);
                    switch (choiceOfTaxPurposes) {
                        case "1":
                            WebElement ausResxpath=driver.findElement(By.xpath("//label[contains(text(),'An Australian resident for tax purposes')]"));
                            if (!isElementClickable(ausResxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            ausResxpath.click();
                            logMessage("Option - An Australian resident for tax purposes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement foreignResxpath=driver.findElement(By.xpath("//label[contains(text(),'A foreign resident for tax purposes')]"));
                            if (!isElementClickable(foreignResxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            foreignResxpath.click();
                            logMessage("Option - A foreign resident for tax purposes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "3":
                            WebElement workingholidayxpath=driver.findElement(By.xpath("//label[contains(text(),'A working holiday maker')]"));
                            if (!isElementClickable(workingholidayxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            workingholidayxpath.click();
                            logMessage("Option - A working holiday maker is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfTaxFreeThreshold != null) {
                    // SystemLibrary.scrollFromTopToquarterWindow(driver);
                    Thread.sleep(2000);
                    switch (choiceOfTaxFreeThreshold) {
                        case "1":
                            WebElement taxyesxpath=driver.findElement(By.xpath("//input[@id='tax-threshold-yes']"));
                            if (!isElementClickable(taxyesxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            taxyesxpath.click();
                            logMessage("Option - Tax-Free Threshold - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement taxnoxpath=driver.findElement(By.xpath("//input[@id='tax-threshold-no']"));
                            if (!isElementClickable(taxnoxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            taxnoxpath.click();
                            logMessage("Option - Tax-Free Threshold - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfDebitLoan != null) {
                    Thread.sleep(2000);
                    switch (choiceOfDebitLoan) {
                        case "1":
                            WebElement educyesxpath=driver.findElement(By.xpath("//input[@id='educ-debt-yes']"));
                            if (!isElementClickable(educyesxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            educyesxpath.click();
                            logMessage("Option - Debit Loan - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            WebElement educnoxpath=driver.findElement(By.xpath("//input[@id='educ-debt-no']"));
                            if (!isElementClickable(educnoxpath)) {
                                scrollFromTopToquarterWindow(driver);
                            }
                            educnoxpath.click();
                            logMessage("Option - Debit Loan - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                    logScreenshot(driver);
                }

                if (choiceOfFinancialSuppl != null) {
                    switch (choiceOfFinancialSuppl) {
                        case "1":
                            driver.findElement(By.xpath("//input[@id='fin-supp-debt-yes']")).click();
                            logMessage("Option - Financial Supplement debt - Yes is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//input[@id='fin-supp-debt-no']")).click();
                            logMessage("Option - Financial Supplement debt - No is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                }
                GeneralBasic.waitSpinnerDisappear(120, driver);
                scrollToBottom(driver);

                if (abn != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='Abn']"));
                    clearTextBox(element);
                    Thread.sleep(2000);
                    element.sendKeys(abn + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + abn + "' is Added.");
                }

                if (instalmentRate != null) {
                    switch (instalmentRate) {
                        case "1":
                            driver.findElement(By.xpath("//label[contains(text(),\"Yes, Commissioner's instalment rate is:\")]")).click();
                            logMessage("Option - Yes, Commissioner's instalment rate is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//label[contains(text(),'No, flat rate of withholding is 20%.')]")).click();
                            logMessage("Option - No, flat rate of withholding is 20%. is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                }

                if (CommissioneInstalmentRate != null) {
                    Thread.sleep(2000);
                    WebElement element = driver.findElement(By.xpath("//input[@id='CommisionersInstalmentRate']"));
                    clearTextBox(element);
                    element.sendKeys(CommissioneInstalmentRate + Keys.TAB);
                    Thread.sleep(2000);
                    logMessage("Tax File Number '" + CommissioneInstalmentRate + "' is Added.");
                }

                if (rateOfWithHolding != null) {
                    switch (rateOfWithHolding) {
                        case "1":
                            driver.findElement(By.xpath("//label[contains(text(),'Yes, Commissioner's instalment rate is:')]")).click();
                            logMessage("Option - Yes, Commissioner's instalment rate is Selected.");
                            Thread.sleep(2000);
                            break;
                        case "2":
                            driver.findElement(By.xpath("//label[contains(text(),'No, flat rate of withholding is 20%.')]")).click();
                            logMessage("Option - No, flat rate of withholding is 20%. is Selected.");
                            Thread.sleep(2000);
                            break;
                    }
                }
                /////////

                Thread.sleep(3000);
                logScreenshot(driver);

                WebElement button_SaveAndContinue = waitChild("//button[@class='button--primary button--wide' and text()='Save and continue']", 10, 1, driver);
                if (button_SaveAndContinue != null) {
                    button_SaveAndContinue.click();
                    Thread.sleep(2000);
                    GeneralBasic.waitSpinnerDisappear(60, driver);
                    logMessage("Save and Continue button is clicked in New Starter - Tax Details screen.");
                    logScreenshot(driver);

                    WebElement lable_Superannuation = waitChild("//div[@class='page-header']//h4[contains(text(),'Superannuation')]", 60, 1, driver);
                    Thread.sleep(2000);
                    if (lable_Superannuation != null) {
                        logMessage("New Starter - Superannuation page is shown.");
                    } else {
                        logError("New Starter - Superannuation page is NOT shown.");
                        errorCounter++;
                    }
                }

            } else {
                logWarning("The sidebar - Tax Details form in New Starter Forms page is NOT enabled. ");
                errorCounter++;
            }

        } else {
            logError("Sidebar Tax Details is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean activateNewstarter_afterFinaliseViaEmail(String userName, String firstName, String middleName, String lastName, String preferredName, String password, String storeFileName, String isUpdate, String isCompare, String expectedMessage, String expectedActivation, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws InterruptedException, IOException, MessagingException, SQLException, ClassNotFoundException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        //String userName = GeneralBasic.generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);


        if (userName.equals("AUTO")) {
            userName=GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName,  firstName, lastName, testSerialNo, emailDomainName);
        }
        else{
            if (userName.contains("SN123abc")){
                //userEmailAddress=userEmailAddress.replace("SN123abc", GeneralBasic.getTestSerailNumber_Main(101, 101));
                userName=userName.replace("SN123abc", testSerialNo);
            }

            if (userName.contains("ess-test.australiaeast.cloudapp.azure.com")){
                userName=userName.replace("ess-test.australiaeast.cloudapp.azure.com", emailDomainName);
            }

            if (userName.contains("mptestvm29.eastasia.cloudapp.azure.com")){
                userName=userName.replace("mptestvm29.eastasia.cloudapp.azure.com", emailDomainName);
            }

            if (userName.contains("mptestvm106.southeastasia.cloudapp.azure.com")){
                userName=userName.replace("mptestvm106.southeastasia.cloudapp.azure.com", emailDomainName);
            }

            if (userName.contains("mailtestserver")){
                userName=userName.replace("mailtestserver", emailDomainName);
            }

            if (userName.contains("sageautomation.com")){
                userName=userName.replace("sageautomation.com", emailDomainName);
            }
        }

        //Get Activation link from email...
        //String userEmailAddress = userName.replace("@" + emailDomainName, "");
        String activtionLink = JavaMailLib.getActivationLinkFromMail(userName, "Welcome to Sage ESS", emailDomainName, url_ESS);

        if (activtionLink != null) {
            //Launch ESS User activation page
            logMessage("Start Launch ESS Activation Page.");
            WebDriver driver2 = launchWebDriver(activtionLink, 3);

            logMessage("Screenshot before activation.");
            logScreenshot(driver2);

            WebElement panel = waitChild("//div[@id='panel']", 10, 1, driver2);
            if (!validateTextValueInWebElementInUse(panel, storeFileName, isUpdate, isCompare, expectedMessage, testSerialNo, emailDomainName, driver2))
                errorCounter++;

            WebElement button_SendVerificationCode = waitChild("//button[@class='button button--wide' and text()='Send verification code']", 60, 1, driver2);

            if (button_SendVerificationCode != null) {
                PageObj_ESSActivation.button_SendVerificationCode(driver2).click();
                logMessage("Send Verification Code button is clicked.");
                waitChild("//*[@id=\"modal-content\"]/div/div[2]", 30, 1, driver2);
                PageObj_ESSActivation.button_OK_VerificationCodeEmailSent(driver2).click();
                logMessage("OK button is clicked after Verification Code Email is sent.");


                logMessage("Delay 45 seconds for email verification code...");
                Thread.sleep(45000);

                String verificationCode = JavaMailLib.getVerificationCode(userName, "Verification Code", emailDomainName);
                if (verificationCode != null) {
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).click();
                    //PageObj_ESSActivation.textbox_VerificationCode(driver2).clear();
                    PageObj_ESSActivation.textbox_VerificationCode(driver2).sendKeys(verificationCode);
                    logMessage("Verification code is input");

                    PageObj_ESSActivation.textbox_Password(driver2).click();
                    //PageObj_ESSActivation.textbox_Password(driver2).clear();
                    PageObj_ESSActivation.textbox_Password(driver2).sendKeys(password);
                    logMessage("Password is input.");

                    logMessage("Log screenshot before click Continue button in Activate User Account screen.");
                    logScreenshot(driver2);

                    PageObj_ESSActivation.button_Continue(driver2).click();
                    logMessage("Continue button is clicked.");

                    GeneralBasic.waitSpinnerDisappear(120, driver2);

                    logMessage("Screenshot after click continue button.");
                    logScreenshot(driver2);
                } else {
                    errorCounter++;
                }

            } else {
                if (expectedActivation != null) {
                    if (expectedActivation.equals("1")) {
                        errorCounter++;
                    }
                } else {
                    errorCounter++;
                }

            }

            driver2.close();
            logMessage("Activation page is closed.");
            logMessage("End of Activating User '" + userName + " " + lastName + "'.");
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;

        return isDone;
    }

    public static boolean logonESS_WithWhatNew(String emailAddress, String password, String firstName, String lastName, String middleName, String preferredName,  String isShownAgain, String payrollDBName, String testSerialNo, String emailDomainName,WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        boolean isSignin = false;
        int errorCounter = 0;
        logMessage("-------------------------------");
        logMessage("--- Start logon ESS.");

        waitSpinnerDisappear(20, driver);
        waitChild("//button[@class='btn btn-primary btn-lg btn-block' and text()='Sign In']", 30, 1, driver);

        if (emailAddress.equals("Test.Admin")) {
            //username="Test"+getTestSerailNumber_Main(101, 101)+".Admin";
            emailAddress = "Test" + testSerialNo + ".Admin";
            firstName = emailAddress;
            password = getAdminPassword_FromTenantDetails(testSerialNo);
        } else if (emailAddress.equals("ExtraAdminUser")) {
            emailAddress = getUsername_FromExtraAdminDetails();
            password = getPassword_FromExtraAdminDetails();
            firstName = getFirstname_FromExtraAdminDetails();
        } else if (emailAddress.equals("AUTO")) {
            emailAddress = GeneralBasic.generateEmployeeEmailAddress(serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
        } else if (emailAddress.contains("@sageautomation.com")) {
            emailAddress = emailAddress.replace("@sageautomation.com", "_" + testSerialNo + "@" + emailDomainName);
        } else {
            emailAddress = emailAddress.replace("@" + emailDomainName, "_" + testSerialNo + "@" + emailDomainName);
        }

        PageObj_ESSHome.userNameTextBox(driver).clear();
        PageObj_ESSHome.userNameTextBox(driver).sendKeys(emailAddress);
        logMessage("User Name: " + emailAddress + " is input.");
        PageObj_ESSHome.passwordTextBox(driver).clear();
        PageObj_ESSHome.passwordTextBox(driver).sendKeys(password);
        logMessage("Password '" + password + "' is input.");
        PageObj_ESSHome.signInButton(driver).click();
        logMessage("Sign In button is clicked.");
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(180, driver);
        logScreenshot(driver);

        WebElement dialogue_CompanyPolicy = waitChild("//div[@class='modal-wizard mw-has-header mw-has-close' and contains(., 'What') and contains(., 's New')]", 90, 1, driver);
        if (isShownAgain!=null){
            if (isShownAgain.equals("3")){
                if (dialogue_CompanyPolicy!=null){
                    logError("What's new is popping up unexpected.");
                    errorCounter++;
                }
            }else{
                if (dialogue_CompanyPolicy==null){
                    logError("What's New is NOT pop up.");
                    errorCounter++;
                }
            }
        }

        while (dialogue_CompanyPolicy != null) {
            WebElement button_Next = waitChild("//button[@class='button--primary' and text()='Next']", 10, 1, driver);
            if (button_Next != null) {
                button_Next.click();
                Thread.sleep(2000);
                logMessage("Next button is clicked.");
            }

            WebElement button_OK = waitChild("//button[@class='button--primary' and text()='OK']", 10, 1, driver);
            if (button_OK != null) {
                button_OK.click();
                Thread.sleep(3000);
                logMessage("OK button is clicked.");
                logScreenshot(driver);

                if (isShownAgain.equals("1")){
                    WebElement button_ShownAgain= waitChild("//button[text()='Show again next time I sign in']", 2, 1, driver);
                    if (button_ShownAgain!=null){
                        button_ShownAgain.click();
                        Thread.sleep(2000);
                        logMessage("Show again next time I sign in is clicked.");
                    }else{
                        logError("Shown again next time I sign in button is NOT shown.");
                        errorCounter++;
                    }
                }else if (isShownAgain.equals("2")){
                    WebElement button_DontShownAgain= waitChild("//button[@type='submit' and contains(text(), 'Don')]", 2, 1, driver);
                    if (button_DontShownAgain!=null){
                        button_DontShownAgain.click();
                        Thread.sleep(2000);
                        logMessage("Dont shown again next time I sign in button is clicked.");
                    }else{
                        logError("Dont shown again next time I sign in button is NOT shown.");
                        errorCounter++;
                    }
                }

                break;
            }
            dialogue_CompanyPolicy = waitChild("//div[@class='modal-wizard mw-has-header mw-has-close' and contains(., 'What') and contains(., 's New')]", 90, 1, driver);

        }

        WebElement label_Welcome = waitChild("//*[@id=\"page-title\"]/h3/div", 60, 1, driver);
        if (label_Welcome != null) {
            String welcomeMessage = label_Welcome.getText();
            logMessage("Welcome Message: " + welcomeMessage);
            if (welcomeMessage.contains(firstName)) {
                logMessage("Welcome message is shown as expeected.");
            } else {
                if (preferredName != null) {
                    if (welcomeMessage.contains(preferredName)) {
                        logMessage("Welcome message is shown as expected.");
                    } else {
                        logError("Welcome message is NOT shown correctly.");
                        errorCounter++;
                    }
                } else {
                    logError("Welcome message is NOT shown correctly.");
                    errorCounter++;
                }
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) {
            logMessage("Log on as " + emailAddress + " successfully.");
            isSignin = true;
        } else {
            logError("Failed Signin ESS as " + emailAddress);
        }

        logScreenshot(driver);
        return isSignin;
    }

    public static boolean downloadTeamsReportViaAdmin(String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomain, WebDriver driver) throws Exception {
        boolean isDownloaded = false;
        int errorCounter = 0;

        ////////////////////
        String fileName = "teams-report-" + getCurrentDate2() + ".csv";
        String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + fileName;
        String strNewDownloadFileFullPathName = workingFilePath + "TeamsReport_" + getCurrentTimeAndDate() + ".txt";

        //Delete OLD download file in download folder if there is one
        deleteFilesInFolder(getGoogleChromeDownloadPath(), fileName);

        displayTeamsPage(driver);

        driver.findElement(By.xpath("//div[@id='team-page-controls']//button[@class='button--plain add-button']")).click();
        Thread.sleep(2000);
        logMessage("Ellipsis button is clicked in Team Page.");
        logScreenshot(driver);

        WebElement menu_TeamReport= waitChild("//a[contains(text(),'Teams report')]", 10, 1, driver);
        if (menu_TeamReport!=null){
            menu_TeamReport.click();
            logMessage("Menu - Team Report is clicked.");

            File file = new File(getGoogleChromeDownloadPath(), fileName);
            int i = 0;
            while (!file.exists()) {
                i++;
                Thread.sleep(1000);
                if (i > 300) break;
            }

            if (i < 300) {
                logMessage("It takes " + i + " Seconds to download Teams report.");
            } else {
                logError("Download Teams report overdue.");
                errorCounter++;
            }

            //Move download file into working folder
            //if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
            //logMessage("Teams Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

            String textContent= getStingFromFile(strCurrentDownloadFileFullPathName);
            textContent=textContent.replace(testSerialNo, "ABC123");
            textContent=textContent.replace(emailDomain, "TestAutomation.com");
            createTextFile(strNewDownloadFileFullPathName, textContent);
            logMessage("Teams Report: '" + fileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

            deleteFilesInFolder(getGoogleChromeDownloadPath(), fileName);

            if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, strNewDownloadFileFullPathName, storeFileName)) {errorCounter++;}
            ///////////////////////////////////

        }else{
            logError("Menu - Team Report is NOT shown.");
            errorCounter++;
        }


        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean validateApprovalsPage_Attachment(String leaveReason, String leaveDate, String leaveStatus, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        //This function is for non admin user
        boolean isPassed = false;
        int errorCounter = 0;

        displayApprovalsPage_ViaNavigationBar_NOTAdmin(driver);
        WebElement frame_Approvals = waitChild("//div[@class='panel-body']", 60, 1, driver);
        if (frame_Approvals != null) {
            //////////////////////////////////
            String xpath_leaveItemXapth = null;
            xpath_leaveItemXapth = "//div[@class='list-container']";
            if (leaveReason != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveReason + "')]");
            if (leaveStatus != null)
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveStatus + "')]");
            if (leaveDate != null) {

                if (leaveDate.contains(";")){
                    leaveDate= getExpectedDate(leaveDate, null);
                }

                //leaveDate = SystemLibrary.convertDateFormat_Pro(leaveDate, "E dd MMM yyyy");
                leaveDate = convertDateFormat_Pro(leaveDate, "EEEE dd MMMM yyyy");
                leaveDate = leaveDate.replace(".", "");
                xpath_leaveItemXapth = xpath_leaveItemXapth.replace("]", " and contains(., '" + leaveDate + "')]");
            }

            WebElement elementLeaveItem = waitChild(xpath_leaveItemXapth, 10, 1, driver);
            logDebug(xpath_leaveItemXapth);
            if (elementLeaveItem != null) {
                displayElementInView(elementLeaveItem, driver, 10);
                logMessage("Leave item " + leaveReason + " is found.");
                WebElement element_AttachmentIcon = waitChild(xpath_leaveItemXapth + "//i[@class='icon-attachments attachments-icon']", 5, 1, driver);
                if (element_AttachmentIcon != null) {
                    logMessage("Screenshot before click attachment icon.");
                    logScreenshot(driver);
                    if (!downloadAndValidateLeaveAttachmentFile(storeFileName, isUpdateStore, isCompare, expectedTextContent, element_AttachmentIcon))
                        errorCounter++;
                } else {
                    logError("Attachment Icon is NOT found.");
                    errorCounter++;
                }


            } else {
                logError("Leave item '" + leaveReason + "' is NOT found.");
                errorCounter++;
            }

            //////
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean downloadPayAdviceReport(String firstName, String middleName, String lastName, String preferredName, String periodEndDate, String storeFileName, String isUpdateStore, String isCompare, String expectedResult, String testSerialNo, WebDriver driver) throws Exception {
        //periodEndDate formate is 31/05/2017
        boolean isDownloaded = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Pay Advices & Summaries", testSerialNo, driver)) {
            clickViewmoreButtonInTable(driver);
            logScreenshot(driver);

            ////////////////////
            String xpath_AttachmentButton="//div[@class='list-item-row multiline' and contains(.,'"+periodEndDate+"')]//span[1]//button[2]";
            logDebug(xpath_AttachmentButton);
            WebElement attachmentButton= waitChild(xpath_AttachmentButton, 10, 1, driver);
            if (attachmentButton!=null){
                logMessage("Pay Advice with EOP date '"+periodEndDate+"' is found.");

                String payAdviceFileName = "Pay_Advice_PeriodEnd_" + convertDateFormat(periodEndDate) + ".pdf";
                String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + payAdviceFileName;
                String strNewDownloadFileFullPathName = workingFilePath + "PayAdvice_" + getCurrentTimeAndDate() + ".pdf";
                //Delete OLD download file in download folder if there is one
                deleteFilesInFolder(getGoogleChromeDownloadPath(), payAdviceFileName);

                attachmentButton.click();
                logMessage("Download Pay Advice button is clicked.");
                Thread.sleep(15000);

                File file = new File(getGoogleChromeDownloadPath(), payAdviceFileName);
                int i = 0;
                while (!file.exists()) {
                    i++;
                    Thread.sleep(1000);
                    if (i > 300) break;
                }

                if (i < 300) {
                    logMessage("It takes " + i + " Seconds to download Pay Advice.");
                } else {
                    logError("Download Pay Advice overdue.");
                    errorCounter++;
                }

                //Move download file into working folder
                if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
                logMessage("Pay Advice Report: '" + payAdviceFileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

                ///////////////////////////////////
                if (isUpdateStore != null) {
                    if (isUpdateStore.equals("1")) {
                        if (!saveFileToStore(strNewDownloadFileFullPathName, storeFileName)) errorCounter++;
                    }
                }

                Thread.sleep(10000);

                if (isCompare != null) {
                    if (isCompare.equals("1")) {
                        if (!comparePDFFile(strNewDownloadFileFullPathName, storeFilePath + storeFileName)) errorCounter++;
                    }
                }

            }else{
                logError("Pay Advice with EOP date '"+periodEndDate+"' is NOT found.");
                errorCounter++;
            }

        }else{
            errorCounter++;
        }

        //////
        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean downloadPaymentSummaryReport(String firstName, String middleName, String lastName, String preferredName, String financialYear, String storeFileName, String isUpdateStore, String isCompare, String expectedResult, String testSerialNo, WebDriver driver) throws Exception {
        //financialYear formate is 2016
        boolean isDownloaded = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Pay Advices & Summaries", testSerialNo, driver)) {
            WebElement tab_Summaries = waitChild("//button[@class='button' and contains(text(), 'Summaries')]", 10, 1, driver);
            if (tab_Summaries != null) {
                tab_Summaries.click();
                logMessage("Summaries Tab is clicked.");
                Thread.sleep(2000);
                waitSpinnerDisappear(120, driver);
                logScreenshot(driver);

                String xpath_PaymentSummaryList = "//div[@class='list-item-row' and contains(., 'Tax year "+financialYear+"')]//button[@class='button button--square']";
                WebElement button_PaymentSummary = waitChild(xpath_PaymentSummaryList, 10, 1, driver);

                if (button_PaymentSummary != null) {
                    logMessage("Payment Sumamry with FY '" + financialYear + "' is found.");

                    ////////////////////////////////////////
                    ////////////////////////////////////////

                    String paymentSummaryFileName = "Pay_Summary_FY" + financialYear + ".pdf";
                    String strCurrentDownloadFileFullPathName = getGoogleChromeDownloadPath() + paymentSummaryFileName;
                    String strNewDownloadFileFullPathName = workingFilePath + "PaymentSummary_" + getCurrentTimeAndDate() + ".pdf";
                    //Delete OLD download file in download folder if there is one
                    deleteFilesInFolder(getGoogleChromeDownloadPath(), paymentSummaryFileName);

                    button_PaymentSummary.click();
                    logMessage("Download Payment Summary button is clicked.");
                    Thread.sleep(15000);

                    File file = new File(getGoogleChromeDownloadPath(), paymentSummaryFileName);
                    int i = 0;
                    while (!file.exists()) {
                        i++;
                        Thread.sleep(1000);
                        if (i > 300) break;
                    }

                    if (i < 300) {
                        logMessage("It takes " + i + " Seconds to download Payment Summary Report.");
                    } else {
                        logError("Download Payment Summary Report overdue.");
                        errorCounter++;
                    }

                    //Move download file into working folder
                    if (moveFile(strCurrentDownloadFileFullPathName, strNewDownloadFileFullPathName) == null) errorCounter++;
                    logMessage("Payment Summary Report: '" + paymentSummaryFileName + "' is download as '" + strNewDownloadFileFullPathName + "'.");

                    ///////////////////////////////////
                    if (isUpdateStore != null) {
                        if (isUpdateStore.equals("1")) {
                            if (!saveFileToStore(strNewDownloadFileFullPathName, storeFileName)) errorCounter++;
                        }
                    }

                    Thread.sleep(10000);

                    if (isCompare != null) {
                        if (isCompare.equals("1")) {
                            if (!comparePDFFile(strNewDownloadFileFullPathName, storeFilePath + storeFileName)) errorCounter++;
                        }
                    }

                    //////
                    //////

                } else {
                    logError("The Payment Sumamry with '" + financialYear + "' is NOT found.");
                    errorCounter++;
                }
            }else{
                logError("The Payment Sumamry Tab is NOT found.");
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDownloaded = true;
        return isDownloaded;
    }

    public static boolean addNonPayrollUser(String givenname, String surname, String personalEmail, String personalMobile, String storeFileName, String isUpdate, String isCompare, String expectedContent, String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Only working for Admin user
        boolean isDone = false;
        int errorCounter = 0;

        displayTeamsPage(driver);
        driver.findElement(By.xpath("//a/span[text()='New starters']")).click();
        Thread.sleep(3000);
        logMessage("Team New Starter is clicked.");
        logScreenshot(driver);

        driver.findElement(By.xpath("//button[@class='button--people button--round add-button']")).click();
        Thread.sleep(3000);
        logMessage("Add new starter button is clicked.");
        logScreenshot(driver);

        String xpath_AddNewStarterLable = "//h4[text()='Add a New Starter']";
        WebElement lable_AddNewStarter = waitChild(xpath_AddNewStarterLable, 15, 1, driver);
        if (lable_AddNewStarter != null) {
            logMessage("Add a New Starter page is shown.");

            WebElement button_Close = driver.findElement(By.xpath("//button[@id='button-modal-close']"));
            if (personalEmail != null) {

            } else {
                personalEmail = generateEmployeeEmailAddress("localhost", payrollDBName, givenname, surname, testSerialNo, emailDomainName);
            }

            ////////////////////////////////////

            //New Starter Basic Details Tab is hilighting.
            if (givenname != null) {
                driver.findElement(By.xpath("//input[@id='firstName']")).sendKeys(givenname + Keys.TAB);
                Thread.sleep(2000);
                logMessage("First Name '" + givenname + "' is input.");
            }

            if (surname != null) {
                driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys(surname + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Surname '" + surname + "' is input.");
            }

            if (personalEmail != null) {
                driver.findElement(By.xpath("//input[@id='email']")).sendKeys(personalEmail + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Email '" + personalEmail + "' is input.");

                WebElement inputValidation = waitChild("//span[contains(text(),'This is not a valid email.')]", 5, 1, driver);
                if (inputValidation != null) errorCounter++;
            }

            if (personalMobile != null) {
                driver.findElement(By.xpath("//input[@id='mobile']")).sendKeys(personalMobile + Keys.TAB);
                Thread.sleep(2000);
                logMessage("Personal Mobile '" + personalMobile + "' is input.");
            }

            WebElement checkbox_NonPayrollUser= waitChild("//input[@id='IsNonPayroll']", 10, 1, driver);
            if (checkbox_NonPayrollUser!=null){
                checkbox_NonPayrollUser.sendKeys(Keys.SPACE);
                Thread.sleep(2000);
                logMessage("Non Payroll User is selected.");
            }else{
                logError("Non Payroll User checkbox is NOT shown.");
                errorCounter++;
            }

            logMessage("Screenshot before click Next button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//button[@class='button--primary' and text()='Next']")).click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            logMessage("Next button is clicked in New Starter - Basic Detail tab.");
            logScreenshot(driver);

            WebElement panel_Summary= waitChild("//div[@class='mc-highlight-wrapper' and contains(., 'Summary')]", 10, 1, driver);
            if (panel_Summary!=null){
                logMessage("The Summary Panel is Shown.");
                logScreenshot(driver);

                if (!validateTextValueInWebElementInUse(panel_Summary, storeFileName, isUpdate, isCompare, expectedContent, testSerialNo, emailDomainName, driver)){
                    errorCounter++;
                }

                WebElement button_AddNonPayrollUser= waitChild("//button[@class='button--primary' and text()='Add non-payroll user']", 10, 1, driver);
                if (button_AddNonPayrollUser!=null){
                    button_AddNonPayrollUser.click();
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);
                    logMessage("Button Add Non Payroll User is clickced.");
                    logScreenshot(driver);
                }
                else{
                    logError("Button - Add Non Payroll User is NOT shown.");
                    errorCounter++;
                    button_Close.click();
                    logMessage("Button Close is clicked in New Stater Basic Detail screen.");
                    Thread.sleep(5000);
                    logScreenshot(driver);
                }
            }else{
                logError("Summary Panel is NOT shown.");
                errorCounter++;

                button_Close.click();
                logMessage("Button Close is clicked in New Stater Basic Detail screen.");
                Thread.sleep(5000);
                logScreenshot(driver);
            }

        } else {
            logError("Add a New Starter page is NOT shown.");
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean validateBalanceDetails_ViaLeavePage(String firstName, String middleName, String lastName, String preferredName, String leaveType, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String serverName, String payrollDBName, String emailDomainName, WebDriver driver) throws Exception {
        displayDashboard(driver);
        boolean isPassed = false;
        int errorCounter = 0;

        if (waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver) != null) {
            String userFullName = GeneralBasic.getUserFullname(firstName, middleName, lastName, preferredName);
            WebElement label_Name = waitChild("//h3[@class='bc-name' and contains(text(), '" + userFullName + "')]", 10, 1, driver);
            if (label_Name != null) {
                logMessage("User '" + firstName + "' leave page is shown.");
            } else {
                displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
            }
        } else {
            displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver);
        }

        if (storeFileName != null) {
            WebElement leaveBalance = waitChild("//div[@id='leave-balances']//form/div[@class='le' and contains(., '" +leaveType+ "')]", 10, 1, driver);
            if (leaveBalance != null) {
                leaveBalance.click();
                Thread.sleep(4000);
                logMessage("Leave Type is clicked.");
                logScreenshot(driver);
                if (!validateTextValueInWebElementInUse(leaveBalance, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;
            } else {
                logError("Leave Balance Panel is NOT shown.");
                errorCounter++;
            }

        } else {
            logWarning("The Leave Balance Panel is NOT enabled. ");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }



    public static boolean editApprovedLeave(String firstName, String middleName, String lastName, String preferredName, String startDate, String endDate, String leaveHours, String leaveReason, String attachmentPath, String leaveComment, String messageExpected, String leaveBalanceEntitlement, String leaveType, String leaveTakenExpected, String clickCheckBalanceButton, String leaveReasonEdit, String testSerialNo, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Leave", testSerialNo, driver)) {
            String initialLeaveBalance = "";
            String currentLeaveBalance = "";

            if (leaveType != null) {
                initialLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                logMessage("The initial leave balance of '" + leaveType + "' is " + initialLeaveBalance);
            }

            /*
            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Apply Leave button is clicked.");
            */

            WebElement leaveItem = waitChild("//div[contains(@id, 'list-item-leave-app') and contains(., '" + leaveReasonEdit + "') and contains(., 'Approved')]//button[contains(@class, 'button--plain add-button')]/i[@class='icon-options']", 2, 1, driver);
            if (leaveItem != null) {
                Thread.sleep(4000);
                logMessage("Leave Reason " + leaveReasonEdit + " with Approved is found.");
                leaveItem.click();
                Thread.sleep(4000);
                logMessage("Leave Item '" + leaveReasonEdit + "' is clicked.");

                waitChild("//ul[@class='sub-nav show']/div/li[contains(., 'Edit leave')]", 4, 1, driver).click();
                Thread.sleep(4000);
                logMessage("Menu Edit leave is clicked.");

                if (PageObj_ApplyForLeave.editApplyForLeave(startDate, endDate, leaveHours, leaveReason, attachmentPath, leaveComment, clickCheckBalanceButton, driver)) {
                    WebDriverWait wait = new WebDriverWait(driver, 30);
                    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PageObj_ApplyForLeave.button_Apply(driver)));

                    PageObj_ApplyForLeave.button_Apply(driver).click();
                    logMessage("Apply Leave button is clicked.");
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(180, driver);
                    Thread.sleep(10000);

                    WebElement messagebox = waitChild("//div[@id='modal-content']//div//form", 120, 1, driver);
                    logMessage("Screenshot of dialogue.");
                    logScreenshot(driver);
                    String currentMessageText = messagebox.getText();
                    logMessage("Message from dialogue below:");
                    System.out.println(currentMessageText);

                    if (messageExpected != null) {
                        if (currentMessageText.contains(messageExpected)) {
                            logMessage("Message is shonw as expected.");
                        } else {
                            logError("Message is NOT shown as expected.");
                            logMessage("Expected Message is below:");
                            System.out.println(messageExpected);
                            errorCounter++;
                        }
                    }

                    driver.findElement(By.xpath("//button[contains(@class,'button--primary') and text()='Ok']")).click();
                    logMessage("OK button is clicked");
                    Thread.sleep(10000);
                    GeneralBasic.waitSpinnerDisappear(120, driver);

                    //Wait for reload balance table, max waiting time 1 min
                    waitElementInvisible(60, "//*[@id=\"spinnerbalances-calendar-button\"]/div/div[1]/div", driver);
                    logMessage("Screenshot after click OK Button");
                    logScreenshot(driver);

                    double currentLeaveBalannceTaken = 0;
                    if (leaveType != null) {
                        currentLeaveBalance = PageObj_Leave.getLeaveBalanceDaysFromLeavePage(leaveType, driver);
                        logMessage("The current Leave balance of '" + leaveType + "' is " + currentLeaveBalance);
                        currentLeaveBalannceTaken = Double.valueOf(initialLeaveBalance) - Double.valueOf(currentLeaveBalance);
                        System.out.println(String.valueOf(currentLeaveBalannceTaken) + " day(s) is taken.");
                        if (leaveTakenExpected != null) {
                            if (currentLeaveBalannceTaken == Double.valueOf(leaveTakenExpected)) {
                                logMessage("The day(s) of Leave Taken is as expected.");
                            } else {
                                logError("The day(s) of current Leave Taken is NOT as expected. Expected Leave taken: " + leaveTakenExpected + ".");
                                errorCounter++;
                            }
                        }
                    }


                } else {
                    errorCounter++;
                }
            } else {
                logError("Leave Reason " + leaveReason + " with Pending Approval is NOT found.");
                errorCounter++;
            }

        } else {
            errorCounter++;
        }


        logMessage("Apply for Leave via Leave Page is completed.");
        Thread.sleep(5000);
        if (errorCounter == 0) isDone = true;
        return isDone;

    }



    public static boolean deleteBankAccount(String firstName, String middleName, String lastName, String preferredName, String accountNumber, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        boolean isDone = false;
        int errorCounter = 0;

        searchUserAndDisplayBankAccountsPage(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver);

        WebElement ellipsisMenuButton = waitChild("//Form/div[@class='list-item-row multiline' and contains(., '" + accountNumber + "')]//button[@class='button--plain add-button']", 2, 1, driver);
        if (ellipsisMenuButton != null) {
            ellipsisMenuButton.click();
            Thread.sleep(2000);
            logMessage("Ellipsis Menu button is clicked.");
            logMessage("Screenshot after clicking Ellipsis menu button.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//ul[@class='sub-nav show']//a/i[@class='icon-remove']")).click();
            logMessage("Delete button is clicked.");
            Thread.sleep(5000);
            logScreenshot(driver);

            logMessage("Screenshot after click Delete button.");
            logScreenshot(driver);

        } else {
            logError("Bank account number '" + accountNumber + "' is NOT found.");
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean editContactDetails_Address_WebAPIOFF(String firstName, String middleName, String lastName, String preferredName,  String residentialCountry, String residentialAddress, String residentialSuburb, String residentialPostcode, String residentialState, String userForPostalAddress, String postalCountry, String postalAddress, String postalSuburb, String postalPostcode, String postalState,  String payrollDBName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, ClassNotFoundException, SQLException {
        boolean isDone = false;
        int errorCounter = 0;

        if (firstName.contains("Admin")) {
            //firstName="Test"+getTestSerailNumber_Main(101, 101)+".Admin";
            firstName = "Test" + testSerialNo + ".Admin";
        }
        if (searchUserAndDisplayContactDetailsPage_ViaAdmin(firstName, firstName, middleName, lastName, preferredName, testSerialNo, driver)) {
            //Initialize Page Object
            PageFac_ContactDetail po1 = PageFactory.initElements(driver, PageFac_ContactDetail.class);

        PageFac_ContactDetail pf = PageFactory.initElements(driver, PageFac_ContactDetail.class);

        pf.button_EditContactDetails_Address.click();
        Thread.sleep(3000);
        waitSpinnerDisappear(120, driver);
        logMessage("Edit Address button is clicked.");

        if (residentialCountry != null) {
            pf.textbox_ResidentialCountry.click();
            clearTextBox(pf.textbox_ResidentialCountry);
            Thread.sleep(2000);
            pf.textbox_ResidentialCountry.sendKeys(residentialCountry);
            logMessage("Redidential Country: '" + residentialCountry + "' is input.");
        }

        if (residentialAddress != null) {
            pf.textbox_ResidentialAddress.click();
            clearTextBox(pf.textbox_ResidentialAddress);
            Thread.sleep(2000);
            pf.textbox_ResidentialAddress.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), residentialAddress);
            logMessage("Redidential Address: '" + residentialAddress + "' is input.");
        }

        if (residentialSuburb != null) {
            pf.textbox_ResidentialSuburb.click();
            clearTextBox(pf.textbox_ResidentialSuburb);
            Thread.sleep(2000);
            pf.textbox_ResidentialSuburb.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), residentialSuburb);
            logMessage("Redidential Suburb: '" + residentialSuburb + "' is input.");
        }

        if (residentialPostcode != null) {
            pf.textbox_ResidentialPostcode.click();
            clearTextBox(pf.textbox_ResidentialPostcode);
            Thread.sleep(2000);
            pf.textbox_ResidentialPostcode.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), residentialPostcode);
            logMessage("Redidential Postcode: '" + residentialPostcode + "' is input.");
        }

        if (residentialState != null) {
            pf.textbox_ResidentialState.click();
            clearTextBox(pf.textbox_ResidentialState);
            Thread.sleep(2000);
            pf.textbox_ResidentialState.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), residentialState);
            logMessage("Redidential Postcode: '" + residentialState + "' is input.");
        }

        if (userForPostalAddress != null) {
            tickCheckbox(userForPostalAddress, pf.checkbox_UseForPostalAddress, driver);
            logMessage("Option User for postal address is change.");
        }


        if (postalCountry != null) {
            pf.textbox_PostalCountry.click();
            clearTextBox(pf.textbox_PostalCountry);
            Thread.sleep(2000);
            pf.textbox_PostalCountry.sendKeys(postalCountry);
            logMessage("Postal Country: '" + postalCountry + "' is input.");
        }

        if (postalAddress != null) {
            pf.textbox_PostalAddress.click();
            clearTextBox(pf.textbox_PostalAddress);
            Thread.sleep(2000);
            pf.textbox_PostalAddress.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalAddress);
            logMessage("Postal Address: '" + postalAddress + "' is input.");
        }

        if (postalSuburb != null) {
            pf.textbox_PostalSuburb.click();
            clearTextBox(pf.textbox_PostalSuburb);
            pf.textbox_PostalSuburb.sendKeys(postalSuburb);
            Thread.sleep(2000);
            pf.textbox_PostalSuburb.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalSuburb);
            logMessage("Postal Suburb: '" + postalSuburb + "' is input.");
        }

        if (postalPostcode != null) {
            pf.textbox_PostalPostcode.click();
            clearTextBox(pf.textbox_PostalPostcode);
            Thread.sleep(2000);
            pf.textbox_PostalPostcode.sendKeys(postalPostcode);
            pf.textbox_PostalPostcode.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalPostcode);
            logMessage("Postal Postcode: '" + postalPostcode + "' is input.");
        }

        if (postalState != null) {
            pf.textbox_PostalState.click();
            clearTextBox(pf.textbox_PostalState);
            Thread.sleep(2000);
            pf.textbox_PostalState.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), postalState);
            logMessage("Postal State '" + postalState + "' is selected.");
        }

        ////////////
        logMessage("Screenshot before click Save button.");
        logScreenshot(driver);

        WebElement button_Save = pf.button_SaveEditAddress;
        if (button_Save.isEnabled()) {
            button_Save.click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);
            logMessage("Save Edit Address button is clicked.");

            if (waitChild("//form[@autocomplete='off']", 10, 1, driver) != null) {
                driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                Thread.sleep(3000);
                logWarning("Edit Address is NOT saveed. Close Edit Address button is clicked.");
                errorCounter++;
            }

            } else {
            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
            Thread.sleep(3000);
            logWarning("Edit Address is NOT saveed. Close Edit Address button is clicked.");
            errorCounter++;
            }
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }


    public static boolean Validatesyncmessage (WebDriver driver, String expectedMessage) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);
        Thread.sleep(5000);
        //driver.navigate().to(driver.getCurrentUrl());
        driver.navigate().refresh();
        Thread.sleep(5000);
        waitSpinnerDisappear(120, driver);
        //Get original message from Web API Log
        String originalMessage = PageObj_General.getTopMessageFromWebAPILog(driver);

        if (expectedMessage != null) {
            if (originalMessage.contains(expectedMessage)) {
                logMessage("Successfully imported Employess Message displayed");
                isPassed = true;
            }

            else {
                logError("Failed imported Employess Message displayed.");
                System.out.println("Expected log message: '" + expectedMessage + "'");
                errorCounter++;
            }
        }

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean reload_Integrationpage(WebDriver driver) throws InterruptedException, IOException {
        boolean isPassed = true;
        int errorCounter = 0;

        displaySettings_General(driver);
        Thread.sleep(5000);

        logMessage("Reload Integration Page 1st time for 2.5 Mins");
        driver.navigate().refresh();
        Thread.sleep(150000);
        waitSpinnerDisappear(120, driver);
        logMessage("Reload Integration Page 2nd time for 5 Mins");
        driver.navigate().refresh();
        Thread.sleep(150000);
        logScreenshot(driver);
        waitSpinnerDisappear(120, driver);

        if (errorCounter > 0) isPassed = false;
        return isPassed;
    }

    public static boolean validateEllipsisButtonMenuInTeamPageViaAdmin(String teamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPass=false;
        int errorCounter=0;
        GeneralBasic.displayTeamsPage(driver);
        WebElement ellipsisButton=PageObj_Teams.getEllipsisButtonInTeamPageViaAdmin(teamName, driver);
        if (ellipsisButton!=null){
            if (!isVisibleInView(ellipsisButton, driver)){
                displayElementInView(ellipsisButton, driver, -16);
                Thread.sleep(2000);
                logScreenshot(driver);
            }
            ellipsisButton.click();
            Thread.sleep(2000);
            logMessage("Ellipsis button in Team '"+teamName+"' row is clicked.");
            logScreenshot(driver);
            WebElement subMenu= waitChild("//ul[@class='sub-nav show']", 10, 1, driver);
            if (subMenu!=null){
                if (!SystemLibraryHigh.validateTextInElement_Main(101, 101, subMenu, testSerialNo, emailDomainName)) errorCounter++;
            }else{
                logError("Sub Menu is NOT found.");
                errorCounter++;
            }
        }else{
            logError("Ellipsis button in Team '"+teamName+"' row is NOT found.");
            errorCounter++;
        }
        if (errorCounter==0) isPass=true;
        return isPass;
    }

    public static boolean validateSelectedTeams(String teamName, String tabName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;

        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);
        scrollToTop(driver);

        PageObj_Teams.button_TeamPage_3Options(driver).click();
        logMessage("Triple Option button is clicked.");
        Thread.sleep(2000);
        logScreenshot(driver);

        int errorCounter=0;

        if (teamName!=null){
            if (teamName.equals("666666")){
                PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
                logMessage("All Teams are clicked in Team Page.");
                Thread.sleep(2000);
                logScreenshot(driver);
            }else{
                String[] teamNameList= splitString(teamName, ";");
                String currentTeamList=null;
                String xpath_TeamCheckbox=null;
                WebElement currentCheckbox=null;
                int totalItemCount= teamNameList.length;
                for (int i=0;i<totalItemCount;i++){
                    currentTeamList=teamNameList[i];
                    xpath_TeamCheckbox="//div[contains(@id, 'list-item') and contains(., '"+currentTeamList+"')]//div[@class='checkbox-column']";
                    //logDebug(xpath_TeamCheckbox);
                    currentCheckbox= waitChild(xpath_TeamCheckbox, 10, 1, driver);
                    if (currentCheckbox!=null){
                        displayElementInView_New(currentCheckbox, driver, -125);
                        Thread.sleep(2000);
                        logScreenshot(driver);
                        //new Actions(driver).moveToElement(currentCheckbox).sendKeys(Keys.SPACE).perform();
                        //Thread.sleep(2000);
                        currentCheckbox.click();
                        logMessage("Team "+currentTeamList+" is checked.");
                        logScreenshot(driver);
                        scrollToTop(driver);
                    }else{
                        logError("The team "+currentTeamList+" is NOT found.");
                        errorCounter++;
                    }
                }
            }

        }else{
            logError("Team name is not provided.");
            errorCounter++;
        }

        scrollToTop(driver);
        logMessage("Scroll to Top");
        logScreenshot(driver);

        if (PageObj_Teams.button_TeamPage_3Optoins_View(driver).isEnabled()){
            clickViewmoreButtonInTable(driver);
            scrollToTop(driver);

            PageObj_Teams.button_TeamPage_3Optoins_View(driver).click();
            Thread.sleep(120000);
            GeneralBasic.waitSpinnerDisappear(20, driver);
            logMessage("View button is clicked.");
            logScreenshot(driver);

            WebElement teamTable=null;
            if (tabName!=null){
                if (tabName.equals("Leave")){
                    driver.findElement(By.xpath("//button[contains(text(),'Leave')]")).click();
                    Thread.sleep(5000);
                    logMessage("Leave tab is clikced.");
                    logScreenshot(driver);
                }
            }

            teamTable=driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInWebElementInUse(teamTable, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) errorCounter++;
        }else{
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean validateEllipsisTopButtonMenuWithinSelectedTeamPageViaAdmin(String teamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPass=false;
        int errorCounter=0;
        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);

        if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
            errorCounter++;
        }

        WebElement ellipsisButton= waitChild("//div[@class='page-header']//button", 2, 1, driver);
        if (ellipsisButton!=null){
            ellipsisButton.click();
            Thread.sleep(2000);
            logMessage("Ellipsis button in Team '"+teamName+"' row is clicked.");
            logScreenshot(driver);
            WebElement subMenu= waitChild("//ul[@class='sub-nav show']", 10, 1, driver);
            if (subMenu!=null){
                if (!SystemLibraryHigh.validateTextInElement_Main(111, 111, subMenu, testSerialNo, emailDomainName)) errorCounter++;
            }else{
                logError("Sub Menu is NOT found.");
                errorCounter++;
            }
        }else{
            logError("Ellipsis button in Team '"+teamName+"' row is NOT found.");
            errorCounter++;
        }
        if (errorCounter==0) isPass=true;
        return isPass;
    }

    public static boolean validateMemberEllipsisButtonMenuWithinSelectedTeamPageViaAdmin(String teamName, String firstname, String lastName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPass=false;
        int errorCounter=0;
        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);

        if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
            errorCounter++;
        }

        String memberName=firstname+" "+lastName;
        WebElement ellipsisButton= waitChild("//div[contains(@class, 'list-item') and contains(., '"+memberName+"')]//button", 2, 1, driver);
        if (ellipsisButton!=null){
            ellipsisButton.click();
            Thread.sleep(2000);
            logMessage("Ellipsis button in Team '"+teamName+"' row is clicked.");
            logScreenshot(driver);
            WebElement subMenu= waitChild("//ul[@class='sub-nav show']", 10, 1, driver);
            if (subMenu!=null){
                if (!SystemLibraryHigh.validateTextInElement_Main(121, 121, subMenu, testSerialNo, emailDomainName)) errorCounter++;
            }else{
                logError("Sub Menu is NOT found.");
                errorCounter++;
            }
        }else{
            logError("Ellipsis button in Team '"+teamName+"' row is NOT found.");
            errorCounter++;
        }
        if (errorCounter==0) isPass=true;
        return isPass;
    }

    public static boolean displaySelectTeamPage(String teamName, WebDriver driver) throws Exception {
        boolean isShown=false;
        int errorCounter=0;

        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);
        scrollToTop(driver);

        PageObj_Teams.button_TeamPage_3Options(driver).click();
        logMessage("Triple Option button is clicked.");
        Thread.sleep(2000);
        logScreenshot(driver);

        /////////////////////////////

        if (teamName!=null) {
            if (teamName.equals("666666")) {
                PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
                logMessage("All Teams are clicked in Team Page.");
                Thread.sleep(2000);
                logScreenshot(driver);
            } else {
                String[] teamNameList = splitString(teamName, ";");
                String currentTeamList = null;
                String xpath_TeamCheckbox = null;
                WebElement currentCheckbox = null;
                int totalItemCount = teamNameList.length;
                for (int i = 0; i < totalItemCount; i++) {
                    currentTeamList = teamNameList[i];
                    xpath_TeamCheckbox = "//div[contains(@id, 'list-item') and contains(., '" + currentTeamList + "')]//div[@class='checkbox-column']";
                    logDebug(xpath_TeamCheckbox);
                    currentCheckbox = waitChild(xpath_TeamCheckbox, 10, 1, driver);
                    if (currentCheckbox != null) {
                        displayElementInView_New(currentCheckbox, driver, -125);
                        Thread.sleep(2000);
                        logScreenshot(driver);
                        //new Actions(driver).moveToElement(currentCheckbox).sendKeys(Keys.SPACE).perform();
                        //Thread.sleep(2000);
                        currentCheckbox.click();
                        logMessage("Team " + currentTeamList + " is checked.");
                        logScreenshot(driver);
                        scrollToTop(driver);
                    } else {
                        logError("The team " + currentTeamList + " is NOT found.");
                        errorCounter++;
                    }
                }
            }
        }else{
            logError("Team name is not provided.");
            errorCounter++;
        }

        scrollToTop(driver);
        logMessage("Scroll to Top");
        logScreenshot(driver);

        if (PageObj_Teams.button_TeamPage_3Optoins_View(driver).isEnabled()){
            PageObj_Teams.button_TeamPage_3Optoins_View(driver).click();
            Thread.sleep(60000);
            GeneralBasic.waitSpinnerDisappear(20, driver);
            logMessage("View button is clicked.");
            logScreenshot(driver);
        }else{
            logError("View button in team is NOT enabled.");
            errorCounter++;
        }

        //////

        if (errorCounter==0) isShown=true;
        return isShown;
    }

    public static boolean logonESS_Debug(String emailAddress, String password, String expectedMessage, WebDriver driver) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        boolean isSignin = false;
        int errorCounter = 0;
        logMessage("-------------------------------");
        logMessage("--- Start logon ESS.");

        waitSpinnerDisappear(20, driver);

        WebElement textbox_Username=PageObj_ESSHome.userNameTextBox(driver);
        if (textbox_Username!=null){
            ////////////////////
            textbox_Username.clear();
            textbox_Username.sendKeys(emailAddress);
            logMessage("User Name: " + emailAddress + " is input.");
            driver.findElement(By.xpath("//button[@id='continue']")).click();
            Thread.sleep(2000);
            logMessage("Continue button is clicked.");

            if (waitChild("//h1[contains(text(),'Enter password')]", 20, 1, driver)==null){
                errorCounter++;

            }else{
                PageObj_ESSHome.passwordTextBox(driver).clear();
                PageObj_ESSHome.passwordTextBox(driver).sendKeys(password);
                logDebug("password: "+password+" is input.");

                Thread.sleep(2000);
                logMessage("Password '" + password + "' is input.");
                logMessage("Screenshot before click Sign in Button.");
                logScreenshot(driver);

                PageObj_ESSHome.signInButton(driver).click();
                logMessage("Sign In button is clicked.");
                Thread.sleep(10000);
                GeneralBasic.waitSpinnerDisappear(180, driver);

                if (waitChild("//h4[contains(text(),'"+expectedMessage+"')]", 120, 1, driver)!=null){
                    logMessage("Log on as " + emailAddress + " successfully.");
                }else{
                    logError("Failed Signin ESS as " + emailAddress);
                    errorCounter++;
                }
            }


            //////
        }else{
            logError("Textbox Username is NOT shown.");
            logError("Failed Signin ESS as " + emailAddress);
            errorCounter++;
        }


        logScreenshot(driver);
        if (errorCounter==0) isSignin=true;
        return isSignin;
    }

    public static boolean validateEllipsisTopButtonMenuInViewSelectedTeamPageViaAdmin(String teamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPass=false;
        int errorCounter=0;

        if (displaySelectTeamPage(teamName, driver)){
            driver.findElement(By.xpath("//div[@class='page-header']//button[@class='button--plain add-button']")).click();
            Thread.sleep(3000);
            logMessage("Top Ellipsis button is clicked in View Selected Team page.");
            logScreenshot(driver);

            WebElement menu_TopEllipsis= waitChild("//ul[@class='sub-nav show']", 10, 1, driver);
            if (menu_TopEllipsis!=null){
                if (!SystemLibraryHigh.validateTextValueInWebElementInUse_Main(131, 131, menu_TopEllipsis, testSerialNo, emailDomainName, driver)) errorCounter++;
            }else{
                logError("Top Ellipsis button menu is NOT shown.");
            }

        }else{
            errorCounter++;
        }

        if (errorCounter==0) isPass=true;
        return isPass;
    }

    public static boolean validateEllipsisMemberButtonMenuInViewSelectedTeamPageViaAdmin(String teamNames, String memberNames, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPass=false;
        int errorCounter=0;

        if (displaySelectTeamPage(teamNames, driver)){
            /*driver.findElement(By.xpath("//div[@class='page-header']//button[@class='button--plain add-button']")).click();
            Thread.sleep(3000);
            logMessage("Top Ellipsis button is clicked in View Selected Team page.");
            logScreenshot(driver);*/

            String[] teamMemberList= splitString(memberNames, ";");
            int totalMemberCount=teamMemberList.length;
            String currentMemberName=null;

            for (int i=0;i<totalMemberCount;i++){
                currentMemberName=teamMemberList[i];
                List<WebElement> buttonList=driver.findElements(By.xpath("//div[@class='list-item-row members-display-item single-line-huge' and contains(., '"+currentMemberName+"')]//button[@class='button--plain add-button']"));
                WebElement subMenu_MemberButton=null;
                int totalButtonCount=buttonList.size();
                for (int j=0;j<totalButtonCount;j++){
                    logDebug("The member button xpath is "+ getElementXPath(driver, buttonList.get(j)));
                    buttonList.get(j).click();
                    Thread.sleep(3000);
                    logMessage("The team member '"+currentMemberName+"' button "+String.valueOf(j+1)+" is clicked.");
                    logScreenshot(driver);

                    subMenu_MemberButton=driver.findElement(By.xpath("//ul[@class='sub-nav show']"));
                    if (!SystemLibraryHigh.validateTextValueInWebElementInUse_Main(141, 141, subMenu_MemberButton, testSerialNo, emailDomainName, driver)) errorCounter++;
                    scrollToTop(driver);
                    driver.findElement(By.xpath("//button[text()='Summary']")).click();

                }
            }

            WebElement menu_TopEllipsis= waitChild("//ul[@class='sub-nav show']", 10, 1, driver);
            if (menu_TopEllipsis!=null){
                if (!SystemLibraryHigh.validateTextValueInWebElementInUse_Main(131, 131, menu_TopEllipsis, testSerialNo, emailDomainName, driver)) errorCounter++;
            }else{
                logError("Top Ellipsis button menu is NOT shown.");
            }


        }else{
            errorCounter++;
        }

        if (errorCounter==0) isPass=true;
        return isPass;
    }
    
    
    public static boolean selecteAllTeams_UncheckanyTeam(String teamName, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);
        scrollToTop(driver);

        PageObj_Teams.button_TeamPage_3Options(driver).click();
        logMessage("Triple Option button is clicked.");
        Thread.sleep(2000);
        logScreenshot(driver);

        if (teamName!=null){
            if (teamName.equals("666666")){
                PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
                logMessage("All Teams are clicked in Team Page.");
                Thread.sleep(2000);
                logScreenshot(driver);
            }else{
                String[] teamNameList= splitString(teamName, ";");
                String currentTeamList=null;
                String xpath_TeamCheckbox=null;
                WebElement currentCheckbox=null;
                int totalItemCount= teamNameList.length;
                for (int i=0;i<totalItemCount;i++){
                    currentTeamList=teamNameList[i];
                    xpath_TeamCheckbox="//div[contains(@id, 'list-item') and contains(., '"+currentTeamList+"')]//div[@class='checkbox-column']";
                    logDebug(xpath_TeamCheckbox);
                    currentCheckbox= waitChild(xpath_TeamCheckbox, 10, 1, driver);
                    if (currentCheckbox!=null){
                        displayElementInView_New(currentCheckbox, driver, -125);
                        Thread.sleep(2000);
                        logScreenshot(driver);
                        //new Actions(driver).moveToElement(currentCheckbox).sendKeys(Keys.SPACE).perform();
                        //Thread.sleep(2000);
                        currentCheckbox.click();
                        logMessage("Team "+currentTeamList+" is checked.");
                        logScreenshot(driver);
                        scrollToTop(driver);
                    }else{
                        logError("The team "+currentTeamList+" is NOT found.");
                        errorCounter++;
                    }
                }
            }

        }else{
            logError("Team name is not provided.");
            errorCounter++;
        }

        scrollToTop(driver);
        logMessage("Scroll to Top");
        logScreenshot(driver);

        if (unCheckTeamName!=null){
            String[] teamNameList= splitString(unCheckTeamName, ";");
            String currentTeamList=null;
            String xpath_TeamCheckbox=null;
            WebElement currentCheckbox=null;
            int totalItemCount= teamNameList.length;
            for (int i=0;i<totalItemCount;i++){
                currentTeamList=teamNameList[i];
                xpath_TeamCheckbox="//div[contains(@id, 'list-item') and contains(., '"+currentTeamList+"')]//div[@class='checkbox-column']";
                logDebug(xpath_TeamCheckbox);
                currentCheckbox= waitChild(xpath_TeamCheckbox, 10, 1, driver);
                if (currentCheckbox!=null){
                    displayElementInView_New(currentCheckbox, driver, -125);
                    Thread.sleep(2000);
                    logScreenshot(driver);
                    currentCheckbox.click();
                    logMessage("Team "+currentTeamList+" is Unchecked.");
                    logScreenshot(driver);
                    scrollToTop(driver);
                }else{
                    logError("The team "+currentTeamList+" is NOT found.");
                    errorCounter++;
                }
            }
        }

        scrollToTop(driver);
        logMessage("Scroll to Top");
        logScreenshot(driver);

        if (PageObj_Teams.button_TeamPage_3Optoins_View(driver).isEnabled()){
            PageObj_Teams.button_TeamPage_3Optoins_View(driver).click();
            Thread.sleep(120000);
            GeneralBasic.waitSpinnerDisappear(20, driver);
            logMessage("View button is clicked.");
            logScreenshot(driver);
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean validateAllSelectedTeams_UncheckAnyTeam(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (selecteAllTeams_UncheckanyTeam(teamName, unCheckTeamName, testSerialNo, emailDomainName, driver)) {
            logScreenshot(driver);
            WebElement teamTable=driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInWebElementInUse(teamTable, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
            errorCounter++;
        } else {
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateCheckboxes_SelectActivateMembers(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (selecteAllTeams_UncheckanyTeam(teamName, unCheckTeamName, testSerialNo, emailDomainName, driver)) {
            logScreenshot(driver);
            PageObj_Teams.button_SelectedTeams_TeamPage_Ellipsis(driver).click();
            logMessage("Ellipsis in TeamPage is clicked.");
            Thread.sleep(4000);
            PageObj_Teams.linkText_ActivateMembers(driver).click();
            Thread.sleep(4000);
            logMessage("ActivateMembers linkText is clicked.");
            logScreenshot(driver);
            WebElement teamTable=driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInWebElementInUse(teamTable, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;
        } else {
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateSelectActivateMembersPopup(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (selecteAllTeams_UncheckanyTeam(teamName, unCheckTeamName, testSerialNo, emailDomainName, driver)) {
            logScreenshot(driver);
            PageObj_Teams.button_SelectedTeams_TeamPage_Ellipsis(driver).click();
            Thread.sleep(4000);
            logMessage("Ellipsis in TeamPage is clicked.");
            PageObj_Teams.linkText_ActivateMembers(driver).click();
            Thread.sleep(4000);
            logMessage("ActivateMembers linkText is clicked.");
            logScreenshot(driver);
            PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
            Thread.sleep(4000);
            logMessage("All Teams are clicked in Team Page.");
            PageObj_Teams.button_TeamPage_3Optoins_Activate(driver).click();
            Thread.sleep(4000);
            logMessage("3Optoins Activate is clicked.");
            logScreenshot(driver);

            //WebElement activateMembersPopup=driver.findElement(By.xpath("//div[@id='modal-content']"));
            WebElement activateMembersPopup= waitChild("//div[@id='modal-content']", 10, 1, driver);
            if (activateMembersPopup!=null){
                if (!validateTextValueInWebElementInUse(activateMembersPopup, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                    errorCounter++;

                    PageObj_Teams.button_NoCancel(driver).click();
                    Thread.sleep(4000);
            }else{
                logError("Popup is NOT shown.");
                errorCounter++;
            }

            logScreenshot(driver);

        } else {
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean massActivateMembers_Teams(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (selecteAllTeams_UncheckanyTeam(teamName, unCheckTeamName, testSerialNo, emailDomainName, driver)) {
            logScreenshot(driver);
            PageObj_Teams.button_SelectedTeams_TeamPage_Ellipsis(driver).click();
            logMessage("Ellipsis in TeamPage is clicked.");
            Thread.sleep(4000);
            PageObj_Teams.linkText_ActivateMembers(driver).click();
            Thread.sleep(10000);
            logMessage("ActivateMembers linkText is clicked.");
            logScreenshot(driver);
            //PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
            driver.findElement(By.xpath("//div[@class='page-header' and contains(., 'Selected Teams')]//button[@class='button button--square']")).click();
            Thread.sleep(4000);
            logMessage("All Teams are seleted in Team Page.");
            logScreenshot(driver);

            ////////////// Updated by Jim on 06082021 ////////////
            /*PageObj_Teams.button_TeamPage_3Optoins_Activate(driver).click();
            Thread.sleep(4000);
            logMessage("3Optoins Activate is clicked.");
            */
            //////
            WebElement button_Activate=waitChild("//button[contains(text(),'Activate')]", 10, 1, driver);
            if (button_Activate!=null){
                for (int i=0;i<60000000;i++){  //Maxium waiting for 10 mins
                    button_Activate=waitChild("//button[contains(text(),'Activate')]", 10, 1, driver);
                    if (button_Activate.isEnabled()){
                        button_Activate.click();
                        Thread.sleep(4000);
                        logMessage("3Optoins Activate is clicked.");
                        logScreenshot(driver);

                        PageObj_Teams.button_YesActivateSelected(driver).click();
                        Thread.sleep(4000);
                        logMessage("Yes, activate Selected Button is clicked.");
                        logScreenshot(driver);
                        break;
                    }else{
                        logMessage("Waiting for loading all team members.");
                        Thread.sleep(10000);
                    }
                }
            }

            logScreenshot(driver);


           } else {
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


    public static boolean validateSelectedTeams_AfterMassActivateMembers(String teamName, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String unCheckTeamName, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {

        boolean isPassed = false;
        int errorCounter = 0;

        if (selecteAllTeams_UncheckanyTeam(teamName, unCheckTeamName, testSerialNo, emailDomainName, driver)) {
            logScreenshot(driver);
            PageObj_Teams.button_SelectedTeams_TeamPage_Ellipsis(driver).click();
            logMessage("Ellipsis in TeamPage is clicked.");
            Thread.sleep(6000);
            logScreenshot(driver);

            PageObj_Teams.linkText_ActivateMembers(driver).click();
            logMessage("ActivateMembers linkText is clicked.");
            Thread.sleep(120000);

            logScreenshot(driver);

            WebElement selectedTeams=driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInWebElementInUse(selectedTeams, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver))
                errorCounter++;

        } else {
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static boolean validateViewSelectedTeamsOnLeave(String teamName, String tabName, String calendarDate, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;

        GeneralBasic.displayTeamsPage(driver);
        clickViewmoreButtonInTable(driver);
        scrollToTop(driver);

        PageObj_Teams.button_TeamPage_3Options(driver).click();
        logMessage("Triple Option button is clicked.");
        Thread.sleep(4000);
        logScreenshot(driver);

        int errorCounter=0;

        if (teamName!=null){
            if (teamName.equals("666666")){
                PageObj_Teams.button_TeamPage_3Optoins_SelectAll(driver).click();
                logMessage("All Teams are clicked in Team Page.");
                Thread.sleep(4000);
                logScreenshot(driver);
            }else{
                String[] teamNameList= splitString(teamName, ";");
                String currentTeamList=null;
                String xpath_TeamCheckbox=null;
                WebElement currentCheckbox=null;
                int totalItemCount= teamNameList.length;
                for (int i=0;i<totalItemCount;i++){
                    currentTeamList=teamNameList[i];
                    xpath_TeamCheckbox="//div[contains(@id, 'list-item') and contains(., '"+currentTeamList+"')]//div[@class='checkbox-column']";
                    logDebug(xpath_TeamCheckbox);
                    currentCheckbox= waitChild(xpath_TeamCheckbox, 10, 1, driver);
                    if (currentCheckbox!=null){
                        displayElementInView_New(currentCheckbox, driver, -125);
                        Thread.sleep(4000);
                        logScreenshot(driver);
                        //new Actions(driver).moveToElement(currentCheckbox).sendKeys(Keys.SPACE).perform();
                        //Thread.sleep(2000);
                        currentCheckbox.click();
                        logMessage("Team "+currentTeamList+" is checked.");
                        logScreenshot(driver);
                        scrollToTop(driver);
                    }else{
                        logError("The team "+currentTeamList+" is NOT found.");
                        errorCounter++;
                    }
                }
            }

        }else{
            logError("Team name is not provided.");
            errorCounter++;
        }

        scrollToTop(driver);
        logMessage("Scroll to Top");
        logScreenshot(driver);

        if (PageObj_Teams.button_TeamPage_3Optoins_View(driver).isEnabled()){
            clickViewmoreButtonInTable(driver);
            scrollToTop(driver);

            PageObj_Teams.button_TeamPage_3Optoins_View(driver).click();
            Thread.sleep(60000);
            GeneralBasic.waitSpinnerDisappear(20, driver);
            logMessage("View button is clicked.");
            logScreenshot(driver);

            WebElement teamTable=null;
            if (tabName!=null){
                if (tabName.equals("Leave")){
                    driver.findElement(By.xpath("//button[contains(text(),'Leave')]")).click();
                    Thread.sleep(6000);
                    logMessage("Leave tab is clikced.");
                    logScreenshot(driver);

                    ///////////////////////
                    scrollDateInCalendar(calendarDate, driver);
                    ////////////// Add extra delay 2.5 mins because of the slow performance of calendar ///////////
                    Thread.sleep(150000);
                    //////
                }
            }

            teamTable=driver.findElement(By.xpath("//div[@class='panel-body']"));
            if (!validateTextValueInWebElementInUse(teamTable, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) errorCounter++;
        }else{
            logError("View button is NOT Enabled.");
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static boolean validatePopupDuplicateRole(String roleNameToBeDuplicated, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;

        displaySettings_RolesPermissions(driver);

        WebElement ellipsisButton = waitChild("//div[@class='list-item-row' and contains(., '" + roleNameToBeDuplicated + "')]//button[@class='button--plain add-button']", 10, 1, driver);

        if (ellipsisButton != null) {
            ellipsisButton.click();
            logMessage(roleNameToBeDuplicated + " Role ellipsis button is clicked.");
            WebElement menuItem = waitChild("//div[@class='list-item-row' and contains(., '" + roleNameToBeDuplicated + "')]//li[@id='duplicate-role']//a[@href='javascript:;']", 10, 1, driver);

            WebElement NewRole=null;
            if (menuItem != null) {
                logMessage("Screenshot after click button before selct Duplicate menu.");
                logScreenshot(driver);
                menuItem.click();
                Thread.sleep(3000);
                GeneralBasic.waitSpinnerDisappear(120, driver);
                logMessage(roleNameToBeDuplicated + " role Duplicate menu is clicked.");
            } else {
                errorCounter++;
            }


            NewRole=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form"));
            if (!validateTextValueInWebElementInUse(NewRole, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName, driver)) errorCounter++;

        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
        Thread.sleep(2000);
        logMessage("Close button is clicked.");
        logScreenshot(driver);


    } else {
        logError("Duplicate Role form is NOT shown.");
        errorCounter++;
    }

       if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validatechangeMemberRoleDialogue_ViaTeamsPage(String teamName, String assignAsManager, String storeFileName, String isUpdate, String isCompare, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        //funciton is under adjustment, current only work for assign as manager
        boolean isDone = false;
        int errorCounter = 0;

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        GeneralBasic.waitSpinnerDisappear(60, driver);

        driver.findElement(By.linkText("Change role")).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);

        //Waiting for Change Role dialogue
        if (waitChild("//*[@id=\"modal-content\"]/div/form", 10, 1, driver) != null) {
            if (assignAsManager != null) {
                int i = 0;
                while (!PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver).isEnabled()) {
                    Thread.sleep(1000);
                    i++;
                    if (i > 60) break;
                }
                tickCheckbox(assignAsManager, PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver), driver);
            }
            logMessage("Log screenshot before click Next button.");
            logScreenshot(driver);

            if (storeFileName != null) {
                if (!validateTextValueInElement(PageObj_Teams.dialogueBox_ChangeRole(driver), storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }

            Thread.sleep(1000);
            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
            Thread.sleep(2000);
            logMessage("Close button is clicked.");
            logScreenshot(driver);

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }



    public static boolean validatechangeRoleAfterClickManagerRole_ViaTeamsPage(String teamName, String assignAsManager, String storeFileName, String isUpdate, String isCompare, String firstName, String middleName, String lastName, String preferredName, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        //funciton is under adjustment, current only work for assign as manager
        boolean isDone = false;
        int errorCounter = 0;

        String fullName;
        if (preferredName == null) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " (" + preferredName + ") " + lastName;
        }

        displayTeamsPage(driver);
        PageObj_Teams.getEllipsis_Teams_21(teamName, fullName, driver).click();
        GeneralBasic.waitSpinnerDisappear(60, driver);
        Thread.sleep(1500);
        driver.findElement(By.linkText("Change role")).click();
        GeneralBasic.waitSpinnerDisappear(30, driver);

        //Waiting for Change Role dialogue
        if (waitChild("//*[@id=\"modal-content\"]/div/form", 10, 1, driver) != null) {
            if (assignAsManager != null) {
                int i = 0;
                while (!PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver).isEnabled()) {
                    Thread.sleep(1000);
                    i++;
                    if (i > 60) break;
                }
                tickCheckbox(assignAsManager, PageObj_Teams.checkBox_ChangeRole_AssignAsManager(driver), driver);

            logMessage("Log screenshot after click Next button.");
            logScreenshot(driver);
            Thread.sleep(2000);
            waitSpinnerDisappear(120, driver);

            WebElement role_dropdown=PageObj_Teams.select_Role_Dropdown(driver);
            role_dropdown.click();
            logMessage("Role Dropdown button is clicked.");
            Thread.sleep(1000);
            logScreenshot(driver);

            }

            if (storeFileName != null) {
                if (!validateTextValueInElement(PageObj_Teams.role_Dropdown(driver), storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
            Thread.sleep(1000);
            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
            Thread.sleep(2000);
            logMessage("Close button is clicked.");
            logScreenshot(driver);

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validateChangeRoleTeamsElipsisMenuInTeamsAndRolesPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdate, String isCompare, String teamName, String typeOfChangeRole, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isDone = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            PageObj_TeamsNRoles.getEllipsisButtonInTeamRolesPageViaAdmin(teamName, driver).click();
            Thread.sleep(3000);
            logMessage("Team Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            switch (typeOfChangeRole) {
                case "Manager":
                    WebElement ellipsis_ChangeRoleManager = waitChild("//*[@id='manager-role-sub-menu']/ul/li/a", 10, 1, driver);
                    if (ellipsis_ChangeRoleManager != null) {
                        ellipsis_ChangeRoleManager.click();
                        Thread.sleep(2000);
                        logMessage("Change Manager Role is clicked.");
                    }
                    break;
                case "Member":
                    WebElement ellipsis_ChangeRoleMember = waitChild("//*[@id='member-role-sub-menu']/ul/li/a", 10, 1, driver);
                    if (ellipsis_ChangeRoleMember != null) {
                        ellipsis_ChangeRoleMember.click();
                        Thread.sleep(2000);
                        logMessage("Change Member Role is clicked.");
                    }
                    break;
            }

            WebElement role_dropdown=PageObj_TeamsNRoles.select_Role_Dropdown(driver);
            if (role_dropdown != null) {
                role_dropdown.click();
                Thread.sleep(2000);
                logMessage("Role Dropdown button is clicked.");
            }

            if (storeFileName != null) {
                if (!validateTextValueInElement(PageObj_TeamsNRoles.role_Dropdown(driver), storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                    errorCounter++;
            }
            Thread.sleep(1000);
            driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
            Thread.sleep(2000);
            logMessage("Close button is clicked.");
            logScreenshot(driver);

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean changeRole_ViaTeamsNRolesPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdate, String isCompare, String role, String teamName, String typeOfChangeRole, String testSerialNo, String emailDomainName, WebDriver driver) throws InterruptedException, IOException, Exception {
        //funciton is under adjustment, current only work for assign as manager
        boolean isDone = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            PageObj_TeamsNRoles.getEllipsisButtonInTeamRolesPageViaAdmin(teamName, driver).click();
            Thread.sleep(3000);
            logMessage("Team Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            switch (typeOfChangeRole) {
                case "Manager":
                    WebElement ellipsis_ChangeRoleManager = waitChild("//*[@id='manager-role-sub-menu']/ul/li/a", 10, 1, driver);
                    if (ellipsis_ChangeRoleManager != null) {
                        ellipsis_ChangeRoleManager.click();
                        Thread.sleep(2000);
                        logMessage("Change Manager Role is clicked.");
                    }
                    break;
                case "Member":
                    WebElement ellipsis_ChangeRoleMember = waitChild("//*[@id='member-role-sub-menu']/ul/li/a", 10, 1, driver);
                    if (ellipsis_ChangeRoleMember != null) {
                        ellipsis_ChangeRoleMember.click();
                        Thread.sleep(2000);
                        logMessage("Change Member Role is clicked.");
                    }
                    break;
            }

            if (waitChild("//*[@id='modal-content']/div/form", 10, 1, driver) != null) {

            WebElement NewRole=null;
            if (role != null) {
            WebElement role_dropdown=PageObj_TeamsNRoles.select_Role_Dropdown(driver);
                if (role_dropdown != null) {
                    role_dropdown.click();
                    Thread.sleep(2000);
                    logMessage("Role Dropdown button is clicked.");
                }
            WebElement dropdownlistItem = waitChild("//*[@id='role-select-list']//div[contains(., '"+role+"')]", 10, 1, driver);
                if (dropdownlistItem != null) {
                    dropdownlistItem.click();
                    logMessage("Role '" + role + "' is selected.");
                    } else {
                    logError("Role '" + role + "' is NOT found.");
                    errorCounter++;
                }
                if (storeFileName != null) {
                    NewRole=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form"));
                    if (!validateTextValueInElement(NewRole, storeFileName, isUpdate, isCompare, testSerialNo, emailDomainName))
                        errorCounter++;
                }
            }


            Thread.sleep(1000);
                WebElement button_ChangeRole_Save= waitChild("//button[text()='Save']", 2, 1, driver);
                if (button_ChangeRole_Save!=null){
                    button_ChangeRole_Save.click();
                    Thread.sleep(1000);
                    logMessage("Save button Shown and clicked in Change Role Popup.");
                }else{
                    logError("Save Button Not Shown in Change Role Popup.");
                    errorCounter++;
                }

        }

        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean changeMemberRole_Debug(String firstName, String middleName, String lastName, String preferredName, String role, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {

        boolean isDone = false;
        int errorCounter = 0;

        if (displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {
            driver.findElement(By.xpath("//div[@class='list-section-header' and contains(., 'Member role')]//button")).click();
            Thread.sleep(3000);
            logMessage("Member Role Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            logScreenshot(driver);

            driver.findElement(By.xpath("//a[text()='Change member role']")).click();
            Thread.sleep(3000);
            waitSpinnerDisappear(120, driver);
            logMessage("Menu 'Change member role' is clicked.");

            WebElement popupForm = waitChild("//form[contains(., 'Change Member Role')]", 120, 1, driver);
            if (popupForm != null) {
                logMessage("Form 'Change Member Role' is shown.");
                logScreenshot(driver);

                driver.findElement(By.xpath("//div[@id='role']")).click();
                Thread.sleep(3000);
                logMessage("Member Role dropdown list is clicked.");
                logScreenshot(driver);

                WebElement itemList = waitChild("//div[@class='list-item' and contains(text(),'" + role + "')]", 60, 1, driver);
                if (itemList != null) {
                    itemList.click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(60, driver);
                    logMessage("Member role '" + role + "' is clicked.");
                    logMessage("Screenshot before click Save button.");
                    logScreenshot(driver);

                    driver.findElement(By.xpath("//button[@class='button button--primary' and  text()='Save']")).click();
                    Thread.sleep(3000);
                    waitSpinnerDisappear(120, driver);
                    logMessage("Save button is clicked.");
                } else {
                    errorCounter++;
                }

            } else {
                errorCounter++;
            }
        } else {
            errorCounter++;
        }

        if (errorCounter == 0) isDone = true;
        return isDone;
    }

    public static boolean validate_EllipsisMenuInTeamPage(String teamName, String storeFileName, String isUpdate, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        //Format of testName
        //Teams, Unassigned, Directory, TEAMS I AM A MEMBER OF, OTHER TEAMS, TEAMS I MANAGER
        //Member:Team A, Manager:Team B, Other Teams:Team C

        boolean isPassed=false;
        int errorCounter=0;

        if (displayTeamsPage(driver)==1){
            //////////////////////////

            String xpath_EllipsisButton= getXpathOfEllipsisButtonInTeamsPage(teamName);
            WebElement ellipsisButton=waitChild(xpath_EllipsisButton, 5, 1, driver);
            if (ellipsisButton!=null){
                ellipsisButton.click();
                Thread.sleep(2000);
                logMessage("Ellipsis button in "+teamName+" is clicked.");
                logScreenshot(driver);

                WebElement ellipsisMenue=waitChild("//ul[@class='sub-nav show']", 5, 1, driver);
                if (!validateTextValueInWebElementInUse(ellipsisMenue, storeFileName, isUpdate, isCompare, null, testSerialNo, emailDomainName, driver)){
                    errorCounter++;
                }

            }else{
                logError("The Ellipsis Button is NOT shown.");
                errorCounter++;
            }
            //////
        }else{
            logError("The Team List page is NOT shown.");
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static String getXpathOfEllipsisButtonInTeamsPage(String teamName){
        //Format of testName
        //Teams, Unassigned, Directory, TEAMS I AM A MEMBER OF, OTHER TEAMS, TEAMS I MANAGER
        //Member:Team A, Manager:Team B, Other Teams:Team C
        String xpath_EllipsisButton=null;

        if (teamName.contains(":")){
            String[] teamList=splitString(teamName, ":");
            if (teamList[0].equals("Member")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-3']//div[@class='list-item-row single-line-medium' and contains(., '"+teamList[1]+"')]//button[@class='button--plain add-button']";
            }else if (teamList[0].equals("Manager")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-4']//div[@class='list-item-row single-line-medium' and contains(., '"+teamList[1]+"')]//button[@class='button--plain add-button']";
            }else if (teamList[0].equals("Other Teams")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-7']//div[@class='list-item-row single-line-medium' and contains(., '"+teamList[1]+"')]//button[@class='button--plain add-button']";
            }

        }else{
            if (teamName.equals("Teams")){
                xpath_EllipsisButton="//div[@class='page-header']//div[@id='team-page-controls']//button";
            }else if (teamName.equals("Unassigned")){
                xpath_EllipsisButton="//div[@id='list-item-Unassigned']//button[@class='button--plain add-button']";
            }else if (teamName.equals("Directory")){
                xpath_EllipsisButton="//div[@id='list-item-Directory']//button[@class='button--plain add-button']";
            }else if (teamName.equals("TEAMS I AM A MEMBER OF")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-3']//div[@class='list-section-header']//button[@class='button--plain add-button']";
            }else if (teamName.equals("OTHER TEAMS")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-7']//div[@class='list-section-header']//button[@class='button--plain add-button']";
            }else if (teamName.equals("TEAMS I MANAGE")){
                xpath_EllipsisButton="//div[@id='list-section-team-section-4']//div[@class='list-section-header']//button[@class='button--plain add-button']";
            }else if (teamName.equals("All team members")){
                xpath_EllipsisButton="//div[@class='li-heading no-details' and contains(., 'All team members')]//button[@class='button--plain add-button']";
            }
        }

        logDebug("the "+teamName+" xpath of Ellipsis is : "+xpath_EllipsisButton);
        return xpath_EllipsisButton;
    }

    public static boolean validate_EllipsisMenuWithinSingleTeamTable(String teamName, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws IOException, InterruptedException, Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        Integer teamPageType = displayTeamsPage(driver);

        if (teamPageType == 1) {
            if (!PageObj_Teams.displayTeamMembers(teamName, driver)) {
                errorCounter++;
            }
        }

        if (errorCounter == 0) {

            logMessage("Screenshot of current page.");
            logScreenshot(driver);

            String xpath_EllipsisButton="//div[@class='page-header']//button[@class='button--plain add-button']";
            WebElement ellipsisButton=waitChild(xpath_EllipsisButton, 3, 1, driver);

            if (ellipsisButton!=null){
                ellipsisButton.click();
                Thread.sleep(2000);
                logMessage("Ellipsis button is clicked within Team '"+teamName+"' table.");
                logScreenshot(driver);

                WebElement ellipsisMenu=waitChild("//ul[@class='sub-nav show']", 2, 1, driver);
                if (ellipsisMenu!=null){
                    if (!validateTextValueInElement(ellipsisMenu, storeFileName, isUpdateStore, isCompare, testSerialNo, testSerialNo, emailDomainName)) errorCounter++;

                }else{
                    logError("Ellipsis Menu within Team '"+teamName+"' is NOT shown.");
                }

            }else{
                logError("Ellipsis button is NOT shown within Team table.");
                errorCounter++;
            }
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }


}


