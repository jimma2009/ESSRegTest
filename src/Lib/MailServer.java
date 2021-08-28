package Lib;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.IOException;
import java.sql.SQLException;

import static Lib.SystemLibrary.*;


public class MailServer {

    @Test
    public static void mailDebug1() throws InterruptedException, IOException {
        logMessage("Launch MailServer...");
        WebDriver driver = launchWebMail(SystemLibrary.driverType);
        String activationLink = null;

        if (openEmail("ama_esstest", "Welcome to Sage ESS", driver)) {
            logMessage("End of mail test.");
        }
    }

    public static WebDriver launchWebMail(int driverType) throws InterruptedException {
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Mailinator, driverType);
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Mailnesia, driverType);
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Mailforspam, driverType);
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Mytrashmail, driverType);
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Tempremail, driverType);
        //WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_Tempremail, driverType);
        WebDriver driver=SystemLibrary.launchWebDriver(GeneralBasic.url_WebMail, driverType);
        return driver;
    }

    public static boolean openEmail(String emailAccountName, String emailSubject, WebDriver driver) throws InterruptedException, IOException {
        boolean isOpen=false;
        int errorCounter=0;

        WebElement button_SignIn=SystemLibrary.waitChild("//div[@class='buttonLargeBlue']", 3, 1, driver);
        if (button_SignIn!=null){
            button_SignIn.click();
            logMessage("This first Sign in button is clicked.");
        }

        //click OK button 3 times if eamil not found.
        for (int i=1;i<=3;i++){
            ///////////////////
            textBox_EmailAccountName(driver).click();
            Thread.sleep(2000);
            textBox_EmailAccountName(driver).clear();
            Thread.sleep(2000);
            textBox_EmailAccountName(driver).sendKeys(GeneralBasic.catchupMailAddress);
            logMessage("Email Account Name '"+ emailAccountName +"' is input.");
            Thread.sleep(2000);

            button_Next(driver).click();
            Thread.sleep(3000);
            logMessage("Next button is clicked.");

            textBox_Password(driver).sendKeys(GeneralBasic.catchupMailPassword);
            button_Go(driver).click();
            logMessage("Sign in button is clicked.");
            Thread.sleep(10000);

            logMessage("Screenshot after log in mail "+String.valueOf(i)+" times.");
            SystemLibrary.logScreenshot(driver);

            //div[@aria-label='Message list']//div[@role='option' and contains(., 'Profile Change submission') and @xpath='1']
            WebElement mailList=SystemLibrary.waitChild("//div[@aria-label='Message list']//div[@role='option' and contains(., '"+emailSubject+"'') and @xpath='1']", 10, 1, driver);
            if (mailList!=null){

                SystemLibrary.logError("There is NO Email message for '"+emailAccountName+"'!");
                errorCounter++;
            }
            else{
                mailList.click();
                Thread.sleep(3000);
                logMessage("Mail with subject 'Profile Change submission' is opened.");

                logMessage("Screenshot of email content.");
                logScreenshot(driver);

                WebElement currentMailReceiver=SystemLibrary.waitChild("//div[text()='"+emailAccountName+"@ess-test.australiaeast.cloudapp.azure.com;']", 5, 1, driver);
                if (currentMailReceiver!=null){
                    String xpath_MailBody="//div[contains(p, 'Hi "+emailAccountName.substring(0, 2)+"')]";
                    String mailBodyMessage=driver.findElement(By.xpath(xpath_MailBody)).getText();
                    String fileFullName=workingFilePath+"MailContent_"+ getCurrentTimeAndDate()+".txt";
                    SystemLibrary.createTextFile(fileFullName, mailBodyMessage);
                }
                else{
                    SystemLibrary.logError("The email account name is NOT found.");
                    errorCounter++;
                }

            }

            //////
            if (errorCounter==0) break;
        }



        if (errorCounter==0) isOpen=true;
        return isOpen;
    }

    public static WebElement button_Next(WebDriver driver) {
        return driver.findElement(By.xpath("//input[@type='submit' and @value='Next']"));
    }

    public static WebElement textBox_Password(WebDriver driver){
        return driver.findElement(By.xpath("//input[@name='passwd']"));
    }

    public static WebElement textBox_EmailAccountName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@type='email']"));
    }

    public static WebElement button_Go(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='idSIButton9' and @value='Sign in']"));
    }

    public static WebElement buttonList_JunkEmail(WebDriver driver){
        return driver.findElement(By.xpath("//span[text()='Junk Email' and @xpath='1']"));
    }

    public static WebElement buttonList_Inbox(WebDriver driver){
        return driver.findElement(By.xpath("//span[text()='Inbox' and @xpath='2']"));
    }

    public static String getMailContent(String emailAccountName, String emailSubject) throws InterruptedException, IOException {
        String emailContent=null;
        WebDriver driver= launchWebMail(driverType);

        if (openEmail(emailAccountName, emailSubject, driver)){
            emailContent=driver.findElement(By.xpath("//div[@class='body']")).getText();
            logMessage("The current email content is below");
            System.out.println(emailContent);
        }
        driver.close();
        return emailContent;
    }

    public static String getActivationLinkFromMail(String emailAccountName, String emailSubject) throws InterruptedException, IOException {
        logMessage("Start getting activation link from email.");
        logMessage("Launch MailServer...");
        WebDriver driver= launchWebMail(SystemLibrary.driverType);
        String activationLink=null;

        if (openEmail(emailAccountName, emailSubject, driver)){
            WebElement element_ActivationLink=SystemLibrary.waitChild("//p[contains(text(),'Activate your account:')]//a[contains(text(), 'https://ess-prod.micropay.com.au/#/welcome')]", 60, 1, driver);
            if (element_ActivationLink!=null){
                activationLink=element_ActivationLink.getText();
                logMessage("The Activation Link is '"+activationLink+"'.");
            }
        }

        driver.close();
        logMessage("MailServer is closed.");
        return activationLink;
    }

    public static String getVerificationCode(String emailAccountName, String emailSubject) throws InterruptedException, IOException {
        logMessage("Start getting Verification Code.");
        logMessage("Launch MailServer...");
        WebDriver driver= launchWebMail(SystemLibrary.driverType);

        String verificationCode=null;

        if (openEmail(emailAccountName, emailSubject, driver)){
            WebElement verificationCodeLine=SystemLibrary.waitChild("//p[contains(text(), 'Verification code: ')]", 10, 1, driver);
            if (verificationCodeLine!=null){
                verificationCode=verificationCodeLine.getText();
                verificationCode=verificationCode.replace("Verification code: ", "");
                logMessage("The Verification Code is '"+verificationCode+"'.");
            }
        }

        driver.close();
        logMessage("MailServer is closed.");
        return verificationCode;
    }

    public static String getResetPasswordLink(String emailAccountName, String emailSubject) throws InterruptedException, IOException {
        logMessage("Start getting Reset Password link from email.");
        logMessage("Launch MailServer...");
        WebDriver driver= launchWebMail(SystemLibrary.driverType);
        String resetPasswordLink=null;

        if (openEmail(emailAccountName, emailSubject, driver)){
            WebElement resetPasswordLine=SystemLibrary.waitChild("//a[contains(text(), 'https://ess-prod.micropay.com.au/#/resetpassword')]", 5, 1, driver);
            if (resetPasswordLine!=null){
                resetPasswordLink=driver.findElement(By.xpath("//p[contains(text(),'Reset your password: ')]//a[contains(text(), 'https://ess-prod.micropay.com.au/#/resetpassword')]")).getAttribute("href");
                logMessage("The Reset Password Link is '"+resetPasswordLink+"'.");
            }
            else{
                logError("Reset Password linke is not found in this email.");
            }
        }

        driver.close();
        logMessage("MailServer is closed.");
        return resetPasswordLink;
    }

    public static boolean validateMultiEmailContent(int startSerialNo, int endSerialNo, String testSerialNo, String payrollDBName, String emailDomainName, String url_ESS) throws Exception {
        logMessage("--- Start Validating multi Email Content from row " + startSerialNo + " to " + endSerialNo + " in \"MailServer\" sheet.");
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"MailServer");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!JavaMailLib.validateEmailContent(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                        SystemLibrary.logError("failed in the first email attempt. Try the second attempt... ");
                        if (!JavaMailLib.validateEmailContent(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                            errorCounter++;
                            SystemLibrary.logError("Failed in the Second email attempt.");
                        }

                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        logMessage("*** End of Validating Mulit Email Content in \"MailServer\" sheet.");
        return isPassed;
    }

    public static boolean validateEmailContent(String firstName, String middleName, String lastName, String preferredName, String emailSubject, String storeFileName, String isUpdateStore, String isCompare, String payrollDBName, String testSerialNo, String emailDomainName) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        //if first name include "Tester", then it is an Administrator.
        String emailAccountName=null;
        if (firstName.contains("Tester")){
            emailAccountName="AdminTesterWork99";
        }
        else{
            String emailAddress=GeneralBasic.generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName, firstName, lastName, testSerialNo, emailDomainName);
            //emailAccountName=emailAddress.replace("@mailinator.com", "");
            emailAccountName=emailAddress.replace(emailDomainName, "");
        }


        String strEmailContent=getMailContent(emailAccountName, emailSubject);

        if (strEmailContent!=null){
            if (!SystemLibrary.validateStringFile(strEmailContent, storeFileName, isUpdateStore, isCompare, emailDomainName, testSerialNo)) {
                errorCounter++;
            }
        }
        else{
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


}
