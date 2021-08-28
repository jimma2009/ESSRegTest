package Lib;

//import static Lib.GeneralBasic.url_ESS;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

import static Lib.SystemLibrary.*;

public class JavaMailLib {

   /* public static String protocol = "imap";
    public static String host = "imap.gmail.com";
    public static String port = "993";*/


    public static String protocol = "imap";

    ///////////////////// Must reivew Catchup Email Address before running test ////////////////
    //public static String host = "ess-test.australiaeast.cloudapp.azure.com";
    //public static String host = "mptestvm29.eastasia.cloudapp.azure.com";
    //public static String host = "mptestvm106.southeastasia.cloudapp.azure.com";
    //public static String host = "mptest1002.dynu.net";

    public static String port = "143";
    //public static String catchupEamilAddress ="testerall@"+host;
    public static String catchupEmailPassword ="HandiTesting99";



    @Test
    public static void testMail1() throws InterruptedException, MessagingException, IOException {
        logMessage("Debug here.");
        //getMailContent("esstester101@gmail.com","Test From Gmail");
        //getMailContent("Carmin_CUMMINGS_EMP12_sn011@ess-test.australiaeast.cloudapp.azure.com","Welcome to Sage ESS");
        //getActivationLinkFromMail("Carmin_CUMMINGS_EMP12_sn011","Welcome to Sage ESS");
        //getVerificationCode("Carmin_CUMMINGS_EMP12_sn010","Verification Code");
        //String resetPasswordLink=getResetPasswordLink("Carmin_CUMMINGS_EMP12_sn011","Reset your password request");
        //logMessage("Reset password link is below: ");
        //System.out.println(resetPasswordLink);
        getAllMailContent("mptest1020.dyny.net");
        //deleteAllMail();
        //getMailContent("nobody@ess-test.australiaeast.cloudapp.azure.com", "1 to nobody", null);
    }


    public static String getMailContent_Main(String userEmailAddress, String emailSubject, String expectedKeyContent) throws InterruptedException, IOException, MessagingException {
        String mailContent=null;
        int counter=0;

        while (mailContent==null){
            Thread.sleep(30000);
            counter++;
            logMessage("Try "+counter+" times.");
            mailContent=JavaMailLib.getMailContent(userEmailAddress, emailSubject, expectedKeyContent);

            if (counter>=10){
                logError("Email is not found after trying "+counter+" times within "+counter*24.5+" seconds.");
                break;
            }
        }

        return mailContent;
    }

    public static String getActivationLinkFromMail(String emailAccountName, String emailSubject, String emailDomainName, String url_ESS) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting Activation from email.");
        String activationLink=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;

        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);

        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);


                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);
                //WebElement element_ActivationLink=SystemLibrary.waitChild("//p[contains(text(),'Activate your account:')]//a[contains(text(), 'https://ess-prod.micropay.com.au/#/welcome')]", 60, 1, driver);
                WebElement element_ActivationLink=SystemLibrary.waitChild("//p[contains(text(),'Activate your account:')]//a[contains(text(), '"+url_ESS+"/#/welcome')]", 60, 1, driver);
                if (element_ActivationLink!=null){
                    activationLink=element_ActivationLink.getText();
                    logMessage("The Activation Link is '"+activationLink+"'.");
                }
                driver.close();
            }
        }

        logMessage("MailServer is closed.");
        return activationLink;
    }

    public static String getVerificationCode(String emailAccountName, String emailSubject, String emailDomainName) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting Verification Code from email.");
        String verificationCode=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;
        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);
        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);

                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);
                WebElement verificationCodeLine=SystemLibrary.waitChild("//strong", 10, 1, driver);
                if (verificationCodeLine!=null){
                    verificationCode=verificationCodeLine.getText();
                    //verificationCode=verificationCode.replace("Verification code: ", "");
                    logMessage("The Verification Code is '"+verificationCode+"'.");
                }
                driver.close();
            }
        }

        logMessage("MailServer is closed.");
        return verificationCode;
    }

    public static String getVerificationCode_OLD(String emailAccountName, String emailSubject, String emailDomainName) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting Verification Code from email.");
        String verificationCode=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;
        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);
        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);

                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);
                WebElement verificationCodeLine=SystemLibrary.waitChild("//p[contains(text(), 'Verification code: ')]", 10, 1, driver);
                if (verificationCodeLine!=null){
                    verificationCode=verificationCodeLine.getText();
                    verificationCode=verificationCode.replace("Verification code: ", "");
                    logMessage("The Verification Code is '"+verificationCode+"'.");
                }
                driver.close();
            }
        }

        logMessage("MailServer is closed.");
        return verificationCode;
    }

    public static boolean validateMultiEmailContent(int startSerialNo, int endSerialNo, String payrollDBName,  String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        logMessage("--- Start Validating multi Email Content from row " + startSerialNo + " to " + endSerialNo + " in \"MailServer\" sheet.");
        boolean isPassed=false;
        int errorCounter=0;

        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"MailServer");
        //logMessage("The length of data is "+String.valueOf(cellData.length));

        for (int i = 0; i < cellData.length; i++) {
            //logMessage("The value of the first column is "+cellData[i][0]);
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!JavaMailLib.validateEmailContent(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], payrollDBName, testSerialNo, emailDomainName, url_ESS)){
                        errorCounter++;
                    }
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter==0) isPassed=true;
        logMessage("*** End of Validating Mulit Email Content in \"MailServer\" sheet.");
        return isPassed;
    }

    public static boolean validateEmailContent(String  userEmailAddress, String firstName, String middleName, String lastName, String preferredName, String emailSubject, String storeFileName, String isUpdateStore, String isCompare, String expectedMessageContain, String expectedKeyContent, String payrollDBName, String testSerialNo, String emailDomainName, String url_ESS) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        //if first name include "Tester", then it is an Administrator.

        if (userEmailAddress.equals("AUTO")) {
            userEmailAddress=GeneralBasic.generateEmployeeEmailAddress(SystemLibrary.serverName, payrollDBName,  firstName, lastName, testSerialNo, emailDomainName);
        }
        else{
            if (userEmailAddress.contains("SN123abc")){
                //userEmailAddress=userEmailAddress.replace("SN123abc", GeneralBasic.getTestSerailNumber_Main(101, 101));
                userEmailAddress=userEmailAddress.replace("SN123abc", testSerialNo);
            }

            if (userEmailAddress.contains("ess-test.australiaeast.cloudapp.azure.com")){
                userEmailAddress=userEmailAddress.replace("ess-test.australiaeast.cloudapp.azure.com", emailDomainName);
            }

            if (userEmailAddress.contains("mptestvm29.eastasia.cloudapp.azure.com")){
                userEmailAddress=userEmailAddress.replace("mptestvm29.eastasia.cloudapp.azure.com", emailDomainName);
            }

            if (userEmailAddress.contains("mptestvm106.southeastasia.cloudapp.azure.com")){
                userEmailAddress=userEmailAddress.replace("mptestvm106.southeastasia.cloudapp.azure.com", emailDomainName);
            }

            if (userEmailAddress.contains("mailtestserver")){
                userEmailAddress=userEmailAddress.replace("mailtestserver", emailDomainName);
            }

            if (userEmailAddress.contains("sageautomation.com")){
                userEmailAddress=userEmailAddress.replace("sageautomation.com", emailDomainName);
            }
        }

        String strEmailContent=getMailContent_Main(userEmailAddress, emailSubject, expectedKeyContent);

        //logMessage("strEmailContent.lenght="+String.valueOf(strEmailContent.length()));
        if (strEmailContent!=null){
            //Replace any ESS URL with standard sageessurl.com
            if (strEmailContent.contains(url_ESS)){
                strEmailContent=strEmailContent.replace(url_ESS, "http://url_ESS.com");
            }
            if (!validateStringContainInFile(strEmailContent, storeFileName, isUpdateStore, isCompare, expectedMessageContain, emailDomainName, testSerialNo)) errorCounter++;
        }
        else{
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static String deleteAllMail(String emailDomainName) throws MessagingException, InterruptedException, IOException {
        String mailContent="";

        Properties properties=setServerProperties(protocol, emailDomainName, port);
        Session session=Session.getDefaultInstance(properties);

        Store store=session.getStore(protocol);
        String catchupEamilAddress ="testerall@"+emailDomainName;
        store.connect(catchupEamilAddress, catchupEmailPassword);

        //Open the inbox
        Folder folderInbox=store.getFolder("INBOX");
        folderInbox.open(Folder.READ_WRITE);

        //Fetch the message
        Message[] messages=folderInbox.getMessages();
        int totalMailCount=messages.length;

        logMessage("There are total "+String.valueOf(totalMailCount)+" mail(s).");



        for (int i=0;i<totalMailCount;i++){
            Address[] fromAddress=messages[i].getFrom();
            Address[] toAddress=messages[i].getRecipients(RecipientType.TO);
            String from=fromAddress[0].toString();
            String to=toAddress[0].toString();
            String subject=messages[i].getSubject();
            String sendDate=messages[i].getSentDate().toString();
            String contentType=messages[i].getContentType();
            Object messageContent=messages[i].getContent();
            //String mailBody=getMailBody(messages[i]);


            mailContent=mailContent+"------------------------\n";
            mailContent=mailContent+"Message #"+(i+1)+":\n";
            mailContent=mailContent+"\t From: "+from+"\n";
            mailContent=mailContent+"\t To: "+to+"\n";
            mailContent=mailContent+"\t Subject: "+subject+"\n";
            mailContent=mailContent+"\t Send Date: "+sendDate+"\n";
            mailContent=mailContent+"\t Content Type: "+contentType+"\n";
            //mailContent=mailContent+"\t Mail Body: \n"+mailBody+"\n";

            messages[i].setFlag(Flags.Flag.DELETED, true);

        }


        System.out.println(mailContent);
        logMessage("All mails are deleted.");
        folderInbox.close(true);
        store.close();

        return mailContent;
    }

    public static String getAllMailContent(String emailDomainName) throws MessagingException, InterruptedException, IOException {
        String mailContent="";

        String host=emailDomainName;
        String catchupEamilAddress="testerall@"+emailDomainName;
        Properties properties=setServerProperties(protocol, host, port);
        Session session=Session.getDefaultInstance(properties);

        Store store=session.getStore(protocol);
        store.connect(catchupEamilAddress, catchupEmailPassword);

        //Open the inbox
        Folder folderInbox=store.getFolder("INBOX");
        folderInbox.open(Folder.READ_ONLY);

        //Fetch the message
        Message[] messages=folderInbox.getMessages();
        int totalMailCount=messages.length;

        logMessage("There are total "+String.valueOf(totalMailCount)+" mail(s).");

        for (int i=0;i<totalMailCount;i++){
            Address[] fromAddress=messages[i].getFrom();
            Address[] toAddress=messages[i].getRecipients(RecipientType.TO);
            String from=fromAddress[0].toString();
            String to=toAddress[0].toString();
            String subject=messages[i].getSubject();
            String sendDate=messages[i].getSentDate().toString();
            String contentType=messages[i].getContentType();
            //Object messageContent=messages[i].getContent();
            String mailBody=getMailBody(messages[i]);


            mailContent=mailContent+"------------------------\n";
            mailContent=mailContent+"Message #"+(i+1)+":\n";
            mailContent=mailContent+"\t From: "+from+"\n";
            mailContent=mailContent+"\t To: "+to+"\n";
            mailContent=mailContent+"\t Subject: "+subject+"\n";
            mailContent=mailContent+"\t Send Date: "+sendDate+"\n";
            mailContent=mailContent+"\t Content Type: "+contentType+"\n";
            mailContent=mailContent+"\t Mail Body: \n"+mailBody+"\n";


        }

        System.out.println(mailContent);
        folderInbox.close(false);
        store.close();

        return mailContent;
    }



    private static Properties setServerProperties(String protocol, String host, String port) {
        Properties properties = new Properties();

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting

        ///////////////////// Temp disable below: ////////////////////////
        //properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),"javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

        return properties;
    }


    private static String getMailBody(Message message) throws IOException, MessagingException {
        String result = null;

        if (message instanceof MimeMessage) {
            MimeMessage m = (MimeMessage) message;
            Object contentObject = m.getContent();
            if (contentObject instanceof Multipart) {
                BodyPart clearTextPart = null;
                BodyPart htmlTextPart = null;
                Multipart content = (Multipart) contentObject;
                int count = content.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart part = content.getBodyPart(i);
                    if (part.isMimeType("text/plain")) {
                        clearTextPart = part;
                        break;
                    } else if (part.isMimeType("text/html")) {
                        htmlTextPart = part;
                    }
                }

                if (clearTextPart != null) {
                    result = (String) clearTextPart.getContent();
                } else if (htmlTextPart != null) {
                    String html = (String) htmlTextPart.getContent();
                    result = Jsoup.parse(html).text();
                }

            } else if (contentObject instanceof String) // a simple text message
            {
                result = (String) contentObject;
            } else // not a mime message
            {
                //logger.log(Level.WARNING,"notme part or multipart {0}",message.toString());
                result = null;
            }

        }
        return result;
    }


    public static String getResetPasswordLink(String emailAccountName, String emailSubject, String emailDomainName, String url_ESS) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting Reset password Link from email.");
        String resetPasswordLink=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;
        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);
        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);

                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);

                WebElement resetPasswordLine=SystemLibrary.waitChild("//a[contains(text(), '"+url_ESS+"/#/resetpassword')]", 5, 1, driver);
                if (resetPasswordLine!=null){
                    resetPasswordLink=driver.findElement(By.xpath("//p[contains(text(),'Reset your password: ')]//a[contains(text(), '"+url_ESS+"/#/resetpassword')]")).getAttribute("href");
                    logMessage("The Reset Password Link is '"+resetPasswordLink+"'.");
                }
                else{
                    logError("Reset Password link is not found in this email.");
                }
                driver.close();
            }
        }


        logMessage("MailServer is closed.");
        return resetPasswordLink;
    }

    public static String getMailContent(String userEmailAddress, String emailSubject, String expectedKeyContent) throws MessagingException, InterruptedException, IOException {
        String mailContent="";

        String host=SystemLibrary.getEmailServerNameFromEmaill(userEmailAddress);
        String catchupEamilAddress ="testerall@"+host;

        Properties properties=setServerProperties(protocol, host, port);
        Session session=Session.getDefaultInstance(properties);

        Store store=session.getStore(protocol);
        store.connect(catchupEamilAddress, catchupEmailPassword);

        //Open the inbox
        Folder folderInbox=store.getFolder("INBOX");
        folderInbox.open(Folder.READ_ONLY);

        //Fetch the message
        Message[] messages=folderInbox.getMessages();
        int totalMailCount=messages.length;

        //logMessage("There are total "+String.valueOf(totalMailCount)+" mail(s).");

        for (int i=0;i<totalMailCount;i++){
            Address[] fromAddress=messages[i].getFrom();
            Address[] toAddress=messages[i].getRecipients(RecipientType.TO);
            String from=fromAddress[0].toString();
            String to=toAddress[0].toString();
            String subject=messages[i].getSubject();
            String sendDate=messages[i].getSentDate().toString();
            String contentType=messages[i].getContentType();
            Object messageContent=messages[i].getContent();
            String mailBody=getMailBody(messages[i]);
            //logDebug("Mail: "+i+"/n");
            //logDebug(mailBody);

            //if ((to.contains(userEmailAddress)&&(subject.contains(emailSubject)))){
            if ((to.contains(userEmailAddress))&&(subject.contains(emailSubject))){
                if (expectedKeyContent!=null){
                    if (mailBody.contains(expectedKeyContent)){
                        logMessage("Mail found.");
                        logMessage("Send Date: "+sendDate);
                        //mailContent="------------------------\n";
                        //mailContent=mailContent+"Message #"+(i+1)+":\n";
                        mailContent=mailContent+"<p>From: "+from+"\n";
                        mailContent=mailContent+"<p>To: "+to+"\n";
                        mailContent=mailContent+"<p>Subject: "+subject+"\n";
                        //mailContent=mailContent+"\t Send Date: "+sendDate+"\n";
                        //mailContent=mailContent+"\t Content Type: "+contentType+"\n";
                        mailContent=mailContent+"<p>Mail Body: \n"+mailBody+"\n";
                        break;
                    }
                }else{
                    logMessage("Mail found.");
                    logMessage("Send Date: "+sendDate);
                    //mailContent="------------------------\n";
                    //mailContent=mailContent+"Message #"+(i+1)+":\n";
                    mailContent=mailContent+"<p>From: "+from+"\n";
                    mailContent=mailContent+"<p>To: "+to+"\n";
                    mailContent=mailContent+"<p>Subject: "+subject+"\n";
                    //mailContent=mailContent+"\t Send Date: "+sendDate+"\n";
                    //mailContent=mailContent+"\t Content Type: "+contentType+"\n";
                    mailContent=mailContent+"<p>Mail Body: \n"+mailBody+"\n";
                    break;
                }

            }
        }

        if (mailContent.length()>0) System.out.println(mailContent);
        folderInbox.close(false);
        store.close();

        if (mailContent.length()==0) {
            mailContent=null;
            //logWarning("Email '"+userEmailAddress+"' with Subject '"+emailSubject+"' is NOT found.");
        }

        return mailContent;
    }

    public static String getNewStarter_ActivationLinkFromMail(String emailAccountName, String emailSubject, String emailDomainName) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting New Starter Activation link from email.");
        String activationLink=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;

        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);

        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);

                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);
                WebElement element_ActivationLink=SystemLibrary.waitChild("//a[contains(text(),'New Starter Forms')]", 60, 1, driver);
                if (element_ActivationLink!=null){
                    activationLink=element_ActivationLink.getAttribute("href");
                    logMessage("The Activation Link is '"+activationLink+"'.");
                }
                driver.close();
            }
        }

        logMessage("MailServer is closed.");
        return activationLink;
    }

    public static String getNewStarterTempUsername(String emailAccountName, String emailSubject, String emailDomainName) throws InterruptedException, IOException, MessagingException {
        logMessage("Start getting Temp New Starter Username from email.");
        String tempUserName=null;

        String userEmailAddress=emailAccountName+"@"+emailDomainName;
        String mailContent=getMailContent_Main(userEmailAddress, emailSubject, null);
        if (mailContent!=null){
            String mailFileName="Mail_"+ getCurrentTimeAndDate()+".html";
            String mailFileFullName=SystemLibrary.workingFilePath+"\\"+mailFileName;
            String mailURL=SystemLibrary.serverUrlAddress+"TestLog/WorkingFile/"+mailFileName;
            if (createTextFile(mailFileFullName, mailContent)){
                logMessage("Mail content is saved as '"+mailFileFullName+"'.");
                logMessage("Click here to open this file: "+mailURL);

                WebDriver driver=launchWebDriver(mailURL, SystemLibrary.driverType);
                WebElement tempUserNameLine=SystemLibrary.waitChild("//b", 10, 1, driver);
                if (tempUserNameLine!=null){
                    tempUserName=tempUserNameLine.getText();
                    logMessage("The Temp Username is '"+tempUserName+"'.");
                }
                driver.close();
            }
        }

        logMessage("MailServer is closed.");
        return tempUserName;
    }

  


    @Test
    public static void sendEmail() throws MessagingException {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "mptest1002.dynu.net");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "mptest1002.dynu.net");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("tester1@mptest1002.dynu.net", catchupEmailPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("jim.ma@sage.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("jim.ma@sage.com, tester2@mptest1002.dynu.net"));
        message.setSubject("Mail Subject test");

        String msg = "This is my second email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    @Test
    public static void sendEmailViaGMail(String emailAddress, String emailSubject, String emailContent) throws MessagingException, InterruptedException {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("esstester102@gmail.com", catchupEmailPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("esstester102@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailAddress));
        message.setSubject(emailSubject);

        String msg = emailContent;

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        logMessage("An Email with Subject '"+emailSubject+"' was sent to "+emailAddress+".");
    }

    public static void sendEmail_ESSRegTestComplete_Notificaiton(String testEmailNotification, String testRoundCode, String testSerialNo, String moduleName, String moduleFunctionName) throws MessagingException, InterruptedException {
        String generalMessage=null;
        if (testRoundCode!=null){
            generalMessage="ESS Automated Regression Test - "+testRoundCode+" - Module "+moduleName+" "+moduleFunctionName+" - "+testSerialNo+" ";
        }else{
            generalMessage="ESS Automated Regression Test - Module "+moduleName+" "+moduleFunctionName+" - "+testSerialNo+" ";
        }

        String strReportLink="https://essreport.azurewebsites.net/TestReport?TestSerialNo="+testSerialNo+"&LatestItem=Yes";
        String emailSubject="Test Completed: The "+generalMessage+" - Do Not Reply";
        String emailConent="<H2 style=\"color:seagreen;\">"+generalMessage+"&nbspis completed."+"</H2><hr/><p>Dear Team Member,</p><p>The "+generalMessage+" is completed. You can access the test report via the link below: </p><p><a href=\""+strReportLink+"\">"+strReportLink+"</a></p><p>If any unexpected test result is detected, further log review and manual investigation may be required.</p><br/><p>Kind Regards,</p><p>Test Team</p><hr/><p style=\"color:SkyBlue;font-size:x-small;\">P.S. Please DO NOT Reply this email as it was generated automatically by test machine.</p>";
        JavaMailLib.sendEmailViaGMail(testEmailNotification, emailSubject, emailConent);

    }

    public static void sendEmail_ESSRegTestStart_Notificaiton(String testEmailNotification, String testRoundCode, String testSerialNo, String moduleName, String moduleFunctionName) throws MessagingException, InterruptedException {
        String generalMessage=null;
        if (testRoundCode!=null){
            generalMessage="ESS Automated Regression Test - "+testRoundCode+" - Module "+moduleName+" "+moduleFunctionName+" - "+testSerialNo+" ";
        }else{
            generalMessage="ESS Automated Regression Test - Module "+moduleName+" "+moduleFunctionName+" - "+testSerialNo+" ";
        }

        String strReportLink="https://essreport.azurewebsites.net/TestReport?TestSerialNo="+testSerialNo+"&LatestItem=Yes";
        String emailSubject="Test Started: The "+generalMessage+" - Do Not Reply";
        String emailConent="<H2 style=\"color:seagreen;\">"+generalMessage+"&nbspis started."+"</H2><hr/><p>Dear Team Member,</p><p>The "+generalMessage+" is started. You can access the test report via the link below: </p><p><a href=\""+strReportLink+"\">"+strReportLink+"</a></p><p>If any unexpected test result is detected, further log review and manual investigation may be required.</p><br/><p>Kind Regards,</p><p>Test Team</p><hr/><p style=\"color:SkyBlue;font-size:x-small;\">P.S. Please DO NOT Reply this email as it was generated automatically by test machine.</p>";
        JavaMailLib.sendEmailViaGMail(testEmailNotification, emailSubject, emailConent);
    }


  ////////////// Debug here  //////////////


}
