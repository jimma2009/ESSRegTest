package Lib;

//import com.sun.media.jfxmedia.logging.Logger;
import com.opencsv.CSVReader;
import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

//import org.openqa.selenium.internal.Locatable;
//import winium.elements.desktop.WiniumDriver;


/**
 * Created by j3m on 17/05/2017.
 */
public class SystemLibrary {

    public static String sysPath="C:\\TestAutomationProject\\";
    public static String projectPath=sysPath+"ESSRegTest\\";
    public static String logFilePathName=projectPath+"TestLog\\TestLog.txt";
    public static String storeFilePath=projectPath+"Store\\";
    public static String workingFilePath=projectPath+"TestLog\\WorkingFile\\";
    public static String driverPath=sysPath+"SelWebDriver\\";
    public static String dataSourcePath=projectPath+"DataSource\\";
    public static String screenshotPath=projectPath+"TestScreenshot\\";
    public static String autoITScriptPath="C:\\TestAutomationProject\\ESSRegTest\\src\\AutoITScript\\";
    public static String autoITLogPath=autoITScriptPath+"AutoITLog\\";
    public static String autoITAppPath="C:\\Program Files (x86)\\AutoIt3\\autoit3.exe";
    public static String templogFilePath="C:\\TestAutomationProject\\ESSRegTest\\TestLog\\templog.txt";
    public static String testKeysPath="E:\\AutoTestDBBackup\\TestKeys\\";

    public static int timeOutInSeconds=15;
    //public static int driverType=3; //3- Google Chrome by default
    public static int driverType=4; //4- Google Chrome Headless
    public static int sleepInMilliSeconds=5000;
    public static int datasheetTotalRowCount=1000;

    //This is the log IIS Server name
    //private static String serverUrlAddress="http://10.0.02/";
    //private static String serverUrlAddress="http://10.77.3.112/";
    public static String serverUrlAddress="http://localhost:81/";

    ////////////////////// Must Review the test SN number /////////////
    //public static String testSerialNumber="sn209";


    ///////////////////// Must Review SQL Server name before running test //////////////
    //public static String serverName="mptestvm29.eastasia.cloudapp.azure.com";
    //public static String serverName="mptestvm106.southeastasia.cloudapp.azure.com";
    //public static String serverName="ess-prod.australiaeast.cloudapp.azure.com";
    public static String serverName="localhost";


    /////////////// Must Review Payroll DB before run the test  //////////////////////////////////
    //public static String payrollDatabaseName="ESS_Auto_Payroll";
    //public static String payrollDatabaseName="ESS_Auto_Payroll2";
    //public static String payrollDatabaseName="ESS_Auto_Payroll3";
    //public static String payrollDatabaseName="ESS_Auto_Payroll1";


    /////////////////// Must Reivew the email server domain name before running the test ////////////////////////
    //public static String eMailDomainName ="ess-test.australiaeast.cloudapp.azure.com";
    //public static String eMailDomainName ="mptestvm29.eastasia.cloudapp.azure.com";
    //public static String eMailDomainName ="mptestvm106.southeastasia.cloudapp.azure.com";
    //public static String eMailDomainName ="mptest1002.dynu.net";

    public static int isTakeScreenshot=1;
    public static int isHeadless=1;
    public static ValueLib vl;

    public static boolean delay(WebDriver driver, WebElement element, int waitInSeconds){
        boolean isShown=false;

        //WebElement myDynamicElement = (new WebDriverWait(driver, waitInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[2]/div[1]/div/a/div/div")));
        WebElement myDynamicElement = (new WebDriverWait(driver, waitInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("")));

        return isShown;
    }


    public static String getCurrentTimeAndDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now)); //2016/11/16 12:08:43
        String currentDataTime=dtf.format(now).toString();
        //System.out.println(currentDataTime);
        currentDataTime=currentDataTime.replace(":","");
        currentDataTime=currentDataTime.replace("/","");
        currentDataTime=currentDataTime.replaceAll("\\s+","");

        String currentTime=currentDataTime.substring(8);
        String currentDate=currentDataTime.substring(0,8);

        String outputDataTime=currentTime+"_"+currentDate;
        //System.out.println(outputDataTime);
        return outputDataTime;
    }

    public static String getCurrentDate(){
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String currentDate=dtf.format(now);
        currentDate=currentDate.replace("/","");
        return currentDate;
    }

    public static String getCurrentDate2(){
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now)); //2016/11/16 12:08:43
        String strDateOutput=dtf.format(now);

        //Return format sample "2018-01-12"
        return strDateOutput;
    }

    public static String logScreenshot(WebDriver driver) throws IOException, InterruptedException {

        String messageText=driver.getTitle();
        String fileName="Screenshot";
        String fileFormat="png";
        String fileNameToBeGenerated=fileName+"_"+ getCurrentTimeAndDate()+"."+fileFormat;
        String fileFullPathName=projectPath+"TestScreenshot\\"+fileNameToBeGenerated;

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        //The below method will save the screen shot in d drive with name "screenshot.png"
        FileUtils.copyFile(scrFile, new File(fileFullPathName));

        SystemLibrary.logMessage(messageText+ " Screenshot is saved as \""+fileFullPathName+"\".");
        System.out.println("Click here to access the screenshot: "+serverUrlAddress+"TestScreenshot/"+fileNameToBeGenerated);

        return fileFullPathName;

    }

    public static String logScreenshotElement(WebDriver driver, WebElement element) throws InterruptedException{


        String fileName="Screenshot_element";
        String fileFormat="png";
        String fileNameToBeGenerated=fileName+"_"+ getCurrentTimeAndDate()+"."+fileFormat;
        String fileFullPathName=screenshotPath+fileNameToBeGenerated;

        // Get entire page screenshot
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        BufferedImage  fullImg = null;
        try {
            fullImg = ImageIO.read(screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the location of element on the page
        Point point = element.getLocation();

        // Get width and height of the element
        int elementWidth = element.getSize().getWidth();
        int elementHeight = element.getSize().getHeight();

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
                elementWidth, elementHeight);
        try {
            ImageIO.write(eleScreenshot, "png", screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Copy the element screenshot to disk
        try {
            FileUtils.copyFile(screenshot, new File(fileFullPathName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SystemLibrary.logMessage("An element screenshot saved as \""+fileFullPathName+"\".");
        System.out.println("Click here to access the screenshot: "+serverUrlAddress+"TestScreenshot/"+fileNameToBeGenerated);
        System.out.println("");
        return fileFullPathName;
    }

    @Test
    public static void test11() throws InterruptedException, IOException {
        WebDriver driver=launchWebDriver("http://www.google.com", 4);
        logScreenshot(driver);

    }
    public static WebDriver launchWebDriver(String url, int driverType ) throws InterruptedException {
        //driverType
        // 1 - IE,
        // 2 - Firefox,
        // 3 - Google Chrome, No Headless
        // 4- Google Chrome Headless,
        // 5- Google Chron Headless using iPhone 6 Plue
        WebDriver driver=null;
        String driverName="";
        File file=null;
        ChromeOptions chromeOptions=new ChromeOptions();

        switch (driverType) {

            case 1:
                file = new File(driverPath+"IEDriverServer.exe");
                System.out.println(sysPath+"IEDriverServer.exe");
                System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

                driver = new InternetExplorerDriver();
                driverName="InternetExplorerDriver";
                break;

            /*
            case 2:
                file =new File(driverPath+"geckodriver.exe");
                System.out.println(file.getAbsoluteFile());
                //System.setProperty("webdriver.firefox.driver", file.getAbsolutePath());
                System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
                //System.setProperty("webdriver.firefox.marionette", file.getAbsolutePath());
                ProfilesIni profile=new ProfilesIni();
                //FirefoxProfile myprofile=profile.getProfile("C:\\SelTestProject\\SelTesterFirefoxProfile");
                FirefoxProfile myprofile=profile.getProfile("SelTester");
                myprofile.setAcceptUntrustedCertificates(true);
                myprofile.setAssumeUntrustedCertificateIssuer(false);

                DesiredCapabilities d=new DesiredCapabilities();
                d.setCapability("marionette", true);
                driver = new FirefoxDriver(myprofile);
                //driver = new FirefoxDriver();
                driverName="FirefoxDriver";
                break;
            */
            case 2:
                file =new File(driverPath+"geckodriver.exe");
                System.out.println(file.getAbsoluteFile());
                System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
                driver=new FirefoxDriver();
                driverName="FirefoxDriver";
                break;

            case 3:
                file = new File(driverPath+"//chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

                driver = new ChromeDriver();
                chromeOptions=new ChromeOptions();
                chromeOptions.addArguments("test-type");
                driverName="ChromeDriver";
                break;

            case 4:
                file = new File(driverPath+"//chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
                chromeOptions.addArguments("test-type");
                chromeOptions.addArguments("headless");
                chromeOptions.addArguments("window-size=1680x1050");
                driver = new ChromeDriver(chromeOptions);

                driverName="ChromeDriver";
                break;

            case 5:
                file = new File(driverPath+"//chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
                chromeOptions.addArguments("test-type");
                chromeOptions.addArguments("headless");
                //chromeOptions.addArguments("window-size=1080x1920");
                //chromeOptions.addArguments("window-size=496x896");
                chromeOptions.addArguments("window-size=414x736");
                driver = new ChromeDriver(chromeOptions);

                driverName="ChromeDriver";
                break;
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //driver.navigate().to("https://esp.cloud.micropay.com.au/espsage/");
        //driver.get(url);
        driver.navigate().to(url);
        Thread.sleep(1000);
        driver.manage().window().maximize();
        logMessage("WebDriver: "+driverName+" is launched.");
        logMessage("Web page: \""+url+"\" is opened.");
        Thread.sleep(3000);
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        logMessage("Web Page title: \""+driver.getTitle()+"\".");

        /*
        try {
            captureScreenshot("LaunchWebDriver");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        return driver;
    }



    public static void closeWebDriver(WebDriver driver) throws InterruptedException {

        if (driver!=null) {
            driver.close();
            logMessage("WebDriver is closed.");
        }
        else{
            System.out.println("driver is NOT launched.");
        }
    }

    public static Boolean createTextFile(String fileFullName, String logMessage) throws InterruptedException {
        Boolean isSaved=false;
        try {
            FileWriter myFW=new FileWriter(fileFullName, true);
            myFW.append(logMessage);
            Thread.sleep(500);
            myFW.close();
            isSaved=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("The file \""+fileName+"\" is saved.");
        return isSaved;
    }

    public static void logMessage(String logText) throws InterruptedException {//Multi line log
        //String outputString="------------"+System.getProperty("line.separator")+getCurrentTimeAndDate()+System.getProperty("line.separator")+logText+System.getProperty("line.separator");
        //Single line log
        String outputString= getCurrentTimeAndDate()+": "+logText+System.getProperty("line.separator");
        //fw.write(outputString);
        createTextFile(logFilePathName, outputString);
        System.out.println(outputString);
    }

    public static void logMessage(String logText, String testSerialNo) throws InterruptedException {//Multi line log
        //String outputString="------------"+System.getProperty("line.separator")+getCurrentTimeAndDate()+System.getProperty("line.separator")+logText+System.getProperty("line.separator");
        //Single line log
        String outputString= getCurrentTimeAndDate()+": "+logText+System.getProperty("line.separator");
        //fw.write(outputString);
        createTextFile(logFilePathName, outputString);
        String itemNo=logText;
        String itemName;
        int logTextLength=logText.length();

        if (itemNo.startsWith("Item")){

            /////////////////
            int position=itemNo.indexOf(":");

            if (position==-1){
                itemNo=itemNo+":";
            }
            position=itemNo.indexOf(":");
            itemNo=itemNo.substring(0, position);

            if (position==logTextLength){
                itemName=null;
            }else{
                itemName=logText.substring(position+2, logTextLength);
            }

            ///

            vl.itemNo =itemNo;
            vl.serialNo=testSerialNo;
            vl.itemName=itemName;

        }
        System.out.println(outputString);
    }


    public static void logError(String logText){
        try
        {
            FileWriter myFW = new FileWriter(logFilePathName,true); //the true will append the new data

            //Multi line log
            //String outputString="------------"+System.getProperty("line.separator")+getCurrentTimeAndDate()+System.getProperty("line.separator")+logText+System.getProperty("line.separator");
            //Single line log
            String outputString= getCurrentTimeAndDate()+": Error: "+logText+System.getProperty("line.separator");
            //fw.write(outputString);
            myFW.append(outputString);
            myFW.close();
            System.out.println(outputString);
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static void logWarning(String logText){
        try
        {
            FileWriter myFW = new FileWriter(logFilePathName,true); //the true will append the new data

            //Multi line log
            //String outputString="------------"+System.getProperty("line.separator")+getCurrentTimeAndDate()+System.getProperty("line.separator")+logText+System.getProperty("line.separator");
            //Single line log
            String outputString= getCurrentTimeAndDate()+": Warning: "+logText+System.getProperty("line.separator");
            //fw.write(outputString);
            myFW.append(outputString);
            myFW.close();
            System.out.println(outputString);
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static void logDebug(String logText){
        try
        {
            FileWriter myFW = new FileWriter(logFilePathName,true); //the true will append the new data

            //Multi line log
            //String outputString="------------"+System.getProperty("line.separator")+getCurrentTimeAndDate()+System.getProperty("line.separator")+logText+System.getProperty("line.separator");
            //Single line log
            String outputString= getCurrentTimeAndDate()+": Debug: "+logText+System.getProperty("line.separator");
            //fw.write(outputString);
            myFW.append(outputString);
            myFW.close();
            System.out.println(outputString);
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static String getStoreFileFullPathName(String storeFileName){
        String storeFileFullPathName=storeFilePath+storeFileName;
        System.out.println("The store File \""+storeFileName+"\" is located as \""+storeFilePath+storeFileName+".");
        String urlAddress = serverUrlAddress + "Store/" + storeFileName;
        System.out.println("Click here to access the store file Expected: " + urlAddress);
        return storeFileFullPathName;
    }


    public static boolean compareImage(String file1, String file2) throws InterruptedException {
        //Image image1 = Toolkit.getDefaultToolkit().getImage(file1);
        //Image image2 = Toolkit.getDefaultToolkit().getImage(file2);
        boolean isIdentical=true;

        BufferedImage out=null;
        try {
            BufferedImage image1=ImageIO.read(new File(file1));
            BufferedImage image2=ImageIO.read(new File(file2));

            final int w=image1.getWidth();
            final int h=image1.getHeight();
            final int highlight=Color.MAGENTA.getRGB();

            final int[] p1=image1.getRGB(0,0,w,h,null,0,w);
            final int[] p2=image2.getRGB(0,0,w,h,null,0,w);

            int totalCounter=0;
            if (p1.length<=p2.length){
                totalCounter=p1.length;
            }
            else{
                totalCounter=p2.length;
            }

            for (int i=0;i<totalCounter;i++){
                if (p1[i]!=p2[i]) {
                    isIdentical=false;
                    p1[i]=highlight;
                }
            }

            out=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
            out.setRGB(0, 0, w, h, p1, 0,  w);

            if (!isIdentical){

                try {
                    String fileNameToBeGenerated = "ImageCompare_" + SystemLibrary.getCurrentTimeAndDate() + ".png";
                    String compareResultFileNamePath = projectPath + "FileCompareResult\\" + fileNameToBeGenerated;
                    ImageIO.write(out, "png", new File(compareResultFileNamePath));

                    SystemLibrary.logMessage("The File 1 is \"" + file1);
                    SystemLibrary.logMessage("The File 2 is \"" + file2);

                    SystemLibrary.logError("The image is NOT identical, the image compare result is save as \"" + compareResultFileNamePath + "\".");
                    String urlAddress = serverUrlAddress + "FileCompareResult/" + fileNameToBeGenerated;
                    System.out.println("Click here to access the image compare result: " + urlAddress);
                }
                catch (ArrayIndexOutOfBoundsException e){
                    logMessage("image is NOT identical.Image size is different.");
                    isIdentical=false;
                }

            }
            else{
                SystemLibrary.logMessage("The Image is identical");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            isIdentical=false;
        }
        catch (ArrayIndexOutOfBoundsException e){
            isIdentical=false;
            logMessage("The file is NOT identical. File size is different.");
        }


        return isIdentical;
    }

    public static boolean compareTextFile(String filePathName1, String filePathName2) throws Exception {
        SystemLibrary.logMessage("--- Start comparing text file.");
        boolean isPassed = false;
        int errorCounter=0;

        String compareResultMessage="File 1:\n"+filePathName1+"\nFile 2:\n"+filePathName2+"\n";


        //Create file object
        File file1 = new File(filePathName1);
        File file2 = new File(filePathName2);

        //Create file content list object
        List<String> fileContentLists1=new ArrayList<String>();
        List<String> fileContentLists2=new ArrayList<String>();

        //Create file single line string
        String file1Line=null;
        String file2Line=null;

        try {
            //Consume too much memory if using readAllBytes function.
            //content1 = new String(Files.readAllBytes(Paths.get(filePathName1)));
            //content2 = new String(Files.readAllBytes(Paths.get(filePathName2)));

            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            while ((file1Line = br1.readLine()) != null) {
                fileContentLists1.add(file1Line);
            }

            BufferedReader br2 = new BufferedReader(new FileReader(file2));
            while ((file2Line = br2.readLine()) != null) {
                fileContentLists2.add(file2Line);
            }

            if (fileContentLists1.equals(fileContentLists2)){

                SystemLibrary.logMessage("File is identical.");
            }
            else{
                SystemLibrary.logError("The 2 Files are NOT identical.");
                compareResultMessage=compareResultMessage+"The 2 files are NOT identical.\n";
                errorCounter++;

                WinMergeLib.compareTestFileViaWinMerge(filePathName1, filePathName2);

                /*Patch patch= DiffUtils.diff(fileContentLists1, fileContentLists2);
                List<Delta> deltas=patch.getDeltas();
                int deltasCount=deltas.size();
                for (int i=0;i<deltasCount;i++){
                    compareResultMessage=compareResultMessage+deltas.get(i)+"\n";
                    System.out.println(deltas.get(i));
                }
                createTextFile(compareResultFilePathName, compareResultMessage);
                logMessage("The Text file compare result is saved as "+compareResultFilePathName+".");
                logMessage("Click here to access the result "+compareResultFileUrl);*/
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean compareTextFile_OLD(String filePathName1, String filePathName2) throws InterruptedException {
        SystemLibrary.logMessage("--- Start comparing text file.");
        boolean isPassed = false;
        int errorCounter=0;

        String compareResultMessage="File 1:\n"+filePathName1+"\nFile 2:\n"+filePathName2+"\n";
        String compareResultFileName="TextFileCompareResult_"+ getCurrentTimeAndDate()+".txt";
        String compareResultFileUrl=serverUrlAddress+"FileCompareResult/"+compareResultFileName;
        String compareResultFilePathName=projectPath+"FileCompareResult\\"+compareResultFileName;

        //Create file object
        File file1 = new File(filePathName1);
        File file2 = new File(filePathName2);

        //Create file content list object
        List<String> fileContentLists1=new ArrayList<String>();
        List<String> fileContentLists2=new ArrayList<String>();

        //Create file single line string
        String file1Line=null;
        String file2Line=null;

        try {
            //Consume too much memory if using readAllBytes function.
            //content1 = new String(Files.readAllBytes(Paths.get(filePathName1)));
            //content2 = new String(Files.readAllBytes(Paths.get(filePathName2)));

            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            while ((file1Line = br1.readLine()) != null) {
                fileContentLists1.add(file1Line);
            }

            BufferedReader br2 = new BufferedReader(new FileReader(file2));
            while ((file2Line = br2.readLine()) != null) {
                fileContentLists2.add(file2Line);
            }

            if (fileContentLists1.equals(fileContentLists2)){

                SystemLibrary.logMessage("File is identical.");
            }
            else{
                SystemLibrary.logError("The 2 Files are NOT identical.");
                compareResultMessage=compareResultMessage+"The 2 files are NOT identical.\n";
                errorCounter++;
                Patch patch= DiffUtils.diff(fileContentLists1, fileContentLists2);
                List<Delta> deltas=patch.getDeltas();
                int deltasCount=deltas.size();
                for (int i=0;i<deltasCount;i++){
                    compareResultMessage=compareResultMessage+deltas.get(i)+"\n";
                    System.out.println(deltas.get(i));
                }
                createTextFile(compareResultFilePathName, compareResultMessage);
                logMessage("The Text file compare result is saved as "+compareResultFilePathName+".");
                logMessage("Click here to access the result "+compareResultFileUrl);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean saveFileToStore(String currentFilePathName, String storeFileName) throws IOException, InterruptedException {
        boolean isSaved=false;
        File currentFile=new File(currentFilePathName);
        File storeFile=new File(storeFilePath+storeFileName);

        if (storeFile.exists()){
            logWarning("The old store file is deleted.");
        }

        CopyOption[] myCopyOption=new CopyOption[] {
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
        };

        if (currentFile.exists()) {
            SystemLibrary.logMessage("The store File \"" + Files.copy(currentFile.toPath(), storeFile.toPath(), myCopyOption[0]) + "\" is updated.");
            //String urlAddress = serverUrlAddress + "Store/" + storeFileName;
            //System.out.println("Click here to access the store file: " + urlAddress);
            Thread.sleep(10000);
            isSaved = true;
        }
        else{
            logError(currentFilePathName+"is NOT found. Store File is NOT updated.");
        }

        return isSaved;
    }

    public static boolean comparePDFFile(String filePathName1, String filePathName2) throws InterruptedException {
        boolean isIdentical=false;
        String pdfFileCompareResultFolderName="PDFCompareResult_"+ getCurrentTimeAndDate();
        String pdfFileCompareResultPath=projectPath+"FileCompareResult\\"+pdfFileCompareResultFolderName;
        PDFUtil pdfUtil=new PDFUtil();

        pdfUtil.setCompareMode(CompareMode.TEXT_MODE);
        pdfUtil.compareAllPages(true);
        SystemLibrary.logMessage("--- Start comparing PDF file...");


        try {
            int pageCountFile1=pdfUtil.getPageCount(filePathName1);
            int pageCountFile2=pdfUtil.getPageCount(filePathName2);

            System.out.println("File 1: "+filePathName1);
            System.out.println("File 1 page count: "+pageCountFile1);
            System.out.println("File 2: "+filePathName2);
            System.out.println("File 2 page count: "+pageCountFile2);

            if (pdfUtil.compare (filePathName1, filePathName2)){
                SystemLibrary.logMessage("PDF files are identical.");
                isIdentical=true;
            }
            else{
                SystemLibrary.logError("PDF files are NOT identical.");

                pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
                //pdfUtil.setCompareMode(CompareMode.TEXT_MODE);
                pdfUtil.highlightPdfDifference(true);
                pdfUtil.highlightPdfDifference(Color.MAGENTA);

                new File(pdfFileCompareResultPath).mkdir();
                pdfUtil.setImageDestinationPath(pdfFileCompareResultPath);

                pdfUtil.compare(filePathName1, filePathName2);
                /*
                if(pageCountFile1<pageCountFile2) {
                    pdfUtil.compare(filePathName1, filePathName2, 1, pageCountFile1);
                }
                else {
                    pdfUtil.compare(filePathName1, filePathName2, 1, pageCountFile2);
                }
                */
                String logMessageToBeShown="The PDF file compare result is saved as under \""+pdfFileCompareResultPath+"\" directory.";
                //logMessageToBeShown=logMessageToBeShown.replace("//", "/");
                logMessage(logMessageToBeShown);

                //Get all the file names from the new folder
                File folder = new File(pdfFileCompareResultPath);
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("File " +serverUrlAddress+"FileCompareResult/"+pdfFileCompareResultFolderName+"//"+listOfFiles[i].getName());
                    }
                    else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isIdentical;
    }

    public static void displayElementInView_OLD(WebElement element, WebDriver driver, int offset) throws Exception {
        //need adjust offset manually based on the screen resolution
        //Such as -10 or +10

        if (!element.isDisplayed()){
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollTo(" + element.getLocation().getX() + "," + (element.getLocation().getY()
                    + offset) + ");");
            Thread.sleep(1000);
        }

    }

    public static void displayElementInView(WebElement element, WebDriver driver, int offset) throws Exception {
        //need adjust offset manually based on the screen resolution
        //Such as -10 or +10

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(" + element.getLocation().getX() + "," + (element.getLocation().getY()
                + offset) + ");");
        Thread.sleep(1000);
    }

    public static void displayElementInView_New(WebElement element, WebDriver driver, int offset){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", element);
        js.executeScript("window.scrollBy(0, "+offset+")", "");
    }

    /*public static void scrollToElement(WebElement element) {

        Coordinates coordinates = ((Locatable)element).getCoordinates();
        coordinates.inViewPort();
        coordinates.onPage();
    }*/

    public static void scrollToElementByOffset(WebElement element, WebDriver driver, int offset) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(" + element.getLocation().getX() + "," + (element.getLocation().getY()
                + offset) + ");");
    }

    public static void clickElement(int x, int y, WebDriver driver, WebElement element) throws InterruptedException {
        Actions builder = new Actions(driver);
        builder.moveToElement(element, x, y).click().build().perform();
        SystemLibrary.logMessage(element.getTagName()+" is clicked.");
    }

    public static String getElementXPath(WebDriver driver, WebElement element) {
        return (String)((JavascriptExecutor)driver).executeScript("gPt=function(c){if(c.id!==''){return'id(\"'+c.id+'\")'}if(c===document.body){return c.tagName}var a=0;var e=c.parentNode.childNodes;for(var b=0;b<e.length;b++){var d=e[b];if(d===c){return gPt(c.parentNode)+'/'+c.tagName+'['+(a+1)+']'}if(d.nodeType===1&&d.tagName===c.tagName){a++}}};return gPt(arguments[0]).toLowerCase();", element);
    }

    public static WebElement waitChild(String strValue, int timeOutInSeconds, int searchByType, WebDriver driver){
        WebElement element=null;

        /*
        searchByType 1- xpath, 2- id, 3-name, 4- partialLinkText
         */

        try{
            switch (searchByType) {

                case 1:
                    element = (new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(strValue)));
                    break;

                case 2:
                    element = (new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.id(strValue)));
                    break;
                case 3:
                    element = (new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.name(strValue)));
                    break;
                case 4:
                    element = (new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(strValue)));
                    break;
                case 5:
                    element = (new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.linkText(strValue)));
                    break;

            }
        }
        catch(Exception e){
            //logWarning("Element: "+strValue+" is NOT found.");
            return null;
        }

        return element;

    }

    public static String[] splitString(String originalString, String delimiter) {
        String[] myStringArray = originalString.split(delimiter);
        return myStringArray;
    }

    public static void dragAndDropWebElement(WebElement sourceElement, WebElement targetElement, int xCoordinates, int yCoordinates, WebDriver driver) throws InterruptedException {
        SystemLibrary.logMessage("Start drag and drop web element.");

        Actions builder=new Actions(driver);
        Action action1_ClickAndHold = builder.clickAndHold(sourceElement).build();

        action1_ClickAndHold.perform();

        //action1_mouseMove is done by Java Robot
        Point coordinates = targetElement.getLocation();
        try {
            Robot robot = new Robot(); //Robot for controlling mouse actions
            robot.mouseMove(coordinates.getX()+xCoordinates, coordinates.getY() + yCoordinates);
            Thread.sleep(5000);

        }
        catch (Exception e){

        }

        Action action3_MouseRelease=builder.release(sourceElement).build();
        action3_MouseRelease.perform();

        SystemLibrary.logMessage("*** End of drag and drop web element.");
    }

    public static void closeWebApplication(WebDriver driver) throws InterruptedException {

        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        int totalTabCount=tabs.size();
        System.out.println("There are total "+totalTabCount+" tab/Window(s).");

        for (int i=0;i<totalTabCount;i++){
            driver.switchTo().window(tabs.get(i));
            driver.close();
        }

        logMessage("All tabs/windows are closed.");
    }

    public static void moveToElementAndClick(WebElement element, WebDriver driver) throws InterruptedException {
        Actions builder=new Actions(driver);
        builder.moveToElement(element).build().perform();
        element.click();
        SystemLibrary.logMessage("Moved to element and clicked.");
    }

    public static String readContentFromFile(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    public static boolean updateAndValidateStoreStringFile(String updateStore, String validateResult, String logFileFullName, String storeFileName) throws Exception {
        boolean isDone=false;
        int errorCounter=0;
        if (updateStore!=null){
            if (updateStore.equals("1")){
                if (!SystemLibrary.saveFileToStore(logFileFullName, storeFileName)) errorCounter++ ;
            }
        }

        Thread.sleep(10000);

        if (validateResult!=null){
            if (validateResult.equals("1")){
                if (!SystemLibrary.compareTextFile(logFileFullName, storeFilePath+storeFileName)) errorCounter++;
            }
        }
        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static boolean waitElementInvisible(int timeOutInSeconds, String strXPath, WebDriver driver) throws InterruptedException {
        boolean isDisappear=false;
        new WebDriverWait(driver, timeOutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(strXPath)));
        isDisappear=true;
        Thread.sleep(1000);
        return isDisappear;
    }

    public static String getLeaveDateInESSFormat(String leaveDate) throws ParseException {
        String[] dates = splitString(leaveDate,"-");
        String outputDate = "";

        if (dates.length<2){
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date inputDate = inputDateFormat.parse(dates[0]);
            SimpleDateFormat outputDate1Format = new SimpleDateFormat("EEEE dd MMMM yyyy");
            outputDate = outputDate1Format.format(inputDate);
        }
        else {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date inputDate1 = inputDateFormat.parse(dates[0]);
            Date inputDate2 = inputDateFormat.parse(dates[1]);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy");

            String outputDate1 = outputDateFormat.format(inputDate1);
            String outputDate2 = outputDateFormat.format(inputDate2);
            outputDate = String.join(" - ", outputDate1, outputDate2);

        }
        return outputDate;
    }

    public static String getGoogleChromeDownloadPath(){
        String systemUserName=System.getProperty("user.name");
        String chromeDownloadPath="C:\\Users\\"+systemUserName+"\\Downloads\\";
        //System.out.println("The Google Chrome Download path is '"+chromeDownloadPath+"'.");
        return chromeDownloadPath;
    }

    public static String[] getFilesInDirectory(String strFilePath, String strFileNameToBeSearched) {
        File dir = new File(strFilePath);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept (File dir, String name) {
                //return name.startsWith(strFileNameToBeSearched);
                return name.contains(strFileNameToBeSearched);
            }
        };
        String[] children = dir.list(filter);
        if (children == null) {
            System.out.println("Either dir does not exist or is not a directory");
        } else {
            int fileCount=children.length;
            System.out.println("Total "+fileCount+" files found.");
            for (int i=0; i< fileCount; i++) {
                String filename = children[i];
                System.out.println(filename);
            }
        }
        return children;
    }

    public static int deleteFilesInFolder(String strFolderPath, String strFileNameToBedeleted) throws InterruptedException {
        int numberOfFileDeleted=0;
        String[] filenameList=getFilesInDirectory(strFolderPath, strFileNameToBedeleted);

        if (filenameList!=null){
            int fileCount=filenameList.length;
            File myFile;
            for(int i=0;i<fileCount;i++){
                String strFileFullName=strFolderPath+filenameList[i];
                myFile=new File(strFileFullName);
                myFile.delete();
                logMessage("File '"+strFileFullName+"' is deleted.");
                numberOfFileDeleted++;
            }
        }
        logMessage("Total "+numberOfFileDeleted+" file(s) deleted.");
        return numberOfFileDeleted;
    }

    public static String moveFile(String strFileFullNameToBeMoved, String strNewFileFullName){
        File myOldFile=new File(strFileFullNameToBeMoved);
        File myNewFile=new File(strNewFileFullName);

        if (myOldFile.renameTo(myNewFile)){
            //logMessage("Old File '"+strFileFullNameToBeMoved+"' is moved as '"+strNewFileFullName+"' successfully.");
        }
        else{
            logError("Failed move file '"+strFileFullNameToBeMoved+".");
            return null;
        }

        return strNewFileFullName;
    }

    public static String renameFile(String strFileFullNameToBeMoved, String strNewFileFullName){
        File myOldFile=new File(strFileFullNameToBeMoved);
        File myNewFile=new File(strNewFileFullName);

        if (myOldFile.renameTo(myNewFile)){
            //logMessage("Old File '"+strFileFullNameToBeMoved+"' is moved as '"+strNewFileFullName+"' successfully.");
        }
        else{
            logError("Failed move file '"+strFileFullNameToBeMoved+".");
            return null;
        }

        return strNewFileFullName;
    }

    public static String parseCSVFile(String csvFilePathName) throws InterruptedException {
        CSVReader reader = null;
        String csvTextFilePathName=null;
        try {
            reader = new CSVReader(new FileReader(csvFilePathName));

            List<String[]> rowList;
            rowList=reader.readAll();

            int rowCount=rowList.size();
            //System.out.println("Total "+rowCount+" line(s) found.");

            String[] columnlist=rowList.get(0);
            int columnCount=columnlist.length;

            String strOutputString="";

            for (int i=0;i<rowCount;i++){
                columnlist=rowList.get(i);
                for (int j=0;j<columnCount;j++){
                    strOutputString=strOutputString+columnlist[j]+"\t";
                }
                strOutputString=strOutputString+System.getProperty("line.separator");
            }

            //System.out.println(strOutputString);

            csvTextFilePathName=csvFilePathName.replace(".csv", ".txt");
            SystemLibrary.createTextFile(csvTextFilePathName, strOutputString);
            /*
            while ((columnList = reader.readNext()) != null) {

                System.out.println(line)
            }
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvTextFilePathName;
    }

    public static boolean clickViewmoreButtonInTable(WebDriver driver) throws Exception {
        boolean isPassed=true;
        WebElement element=SystemLibrary.waitChild("//a[@class='link'][contains(text(),'View more')]", 30, 1, driver);

        int i=0;
        while (element!=null){
            i++;
            displayElementInView(element, driver, 10);
            element.click();
            Thread.sleep(30000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            SystemLibrary.logMessage("Click View more "+i+" time(s).");

            element=SystemLibrary.waitChild("//a[@class='link'][contains(text(),'View more')]", 30, 1, driver);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            if (i>20) break;  //Not more than 5 mins.
        }

        return isPassed;
    }

    public static boolean clickViewmoreButtonInTable_OLD(WebDriver driver) throws Exception {
        boolean isPassed=true;
        WebElement element=SystemLibrary.waitChild("//a[@class='link'][contains(text(),'View more')]", 30, 1, driver);

        int i=0;
        while (element!=null){
            i++;
            displayElementInView(element, driver, 10);
            element.click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            SystemLibrary.logMessage("Click View more "+i+" time(s).");

            element=SystemLibrary.waitChild("//a[@class='link'][contains(text(),'View more')]", 30, 1, driver);
            GeneralBasic.waitSpinnerDisappear(5, driver);
            if (i>20) break;  //Not more than 5 mins.
        }

        return isPassed;
    }

    public static void scrollToBottom(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void scrollToTop(WebDriver driver) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight,0)");
        Thread.sleep(2000);
    }

    public static boolean validateTextValueInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String testSerialNo, String emailDomainName) throws Exception {

        boolean isPassed=false;
        int errorCounter=0;
        String strContent=null;
        String fileName="TextInElement_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        strContent= element.getText();
        logMessage("The Text content of the current element is below:\n");
        System.out.println(strContent);

        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);
        if (strContent.contains(testSerialNo)){
            strContent=strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)){
            strContent=strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent=SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent=SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");


        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)) errorCounter++;
            }
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;

    }

    public static boolean tickCheckbox(String strCheckOption, WebElement element_CheckBox, WebDriver driver) throws InterruptedException {
        //checkOption: 1- Check, 2- Uncheck
        boolean isDone=false;
        int errorCounter=0;

        if (strCheckOption.equals("1")){
            if (element_CheckBox.isSelected()==false){
                //element_CheckBox.click();
                new Actions(driver).moveToElement(element_CheckBox).click().perform();
                if (element_CheckBox.isSelected()){
                    logMessage("Element is checked.");
                }
                else{
                    logWarning("Element is NOT checked. Try 2nd attempt.");
                    ///////////////
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    //executor.executeScript("arguments[0].scrollIntoView(true);",  PageObj_General.checkbox_SyncFromPayroll(driver));
                    executor.executeScript("arguments[0].click();", element_CheckBox);
                    if (element_CheckBox.isSelected()){
                        logMessage("Element is checked.");
                    }else{
                        logError("Element is NOT checked.");
                        errorCounter++;
                    }
                    //////
                }

            }
        }
        else if (strCheckOption.equals("2")){
            if (element_CheckBox.isSelected()){
                //element_CheckBox.click();
                new Actions(driver).moveToElement(element_CheckBox).click().perform();
                if (!element_CheckBox.isSelected()){
                    logMessage("Element is unchecked.");
                }else{
                    logWarning("Checkbox is NOT unchecked. Try 2nd attempt.");
                    ////////////////////////
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    //executor.executeScript("arguments[0].scrollIntoView(true);",  PageObj_General.checkbox_SyncFromPayroll(driver));
                    executor.executeScript("arguments[0].click();", element_CheckBox);
                    if (!element_CheckBox.isSelected()){
                        logMessage("Element is Unchecked.");
                    }else{
                        logError("Element is NOT Unchecked.");
                        errorCounter++;
                    }
                    //////
                }

            }
        }
        if (errorCounter>0) isDone=false;
        return isDone;
    }

    public static int getTotalCountFromTable(String tableXpath, WebDriver driver){
        int totalCount=0;
        List<WebElement> elements=driver.findElements(By.xpath(tableXpath));
        totalCount=elements.size();
        return totalCount;
    }

    public static boolean uploadFileViaWindowDialogue(String fileFullPathName) throws AWTException, InterruptedException {
        //Window File Dialogue should have been opened
        boolean isUpdated=false;

        StringSelection ss=new StringSelection(fileFullPathName);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        Robot myRobot=new Robot();
        myRobot.keyPress(KeyEvent.VK_ENTER);
        myRobot.keyRelease(KeyEvent.VK_ENTER);
        myRobot.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(1000);
        myRobot.keyPress(KeyEvent.VK_V);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_V);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(1000);
        myRobot.keyPress(KeyEvent.VK_ENTER);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(1000);

        isUpdated=true;
        return isUpdated;


    }

    public static boolean uploadFileViaWindowDialogue_old(String fileFullPathName) throws AWTException, InterruptedException {
        //Window File Dialogue should have been opened
        boolean isUpdated=false;

        StringSelection ss=new StringSelection(fileFullPathName);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

        Robot myRobot=new Robot();

        myRobot.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(1000);
        myRobot.keyPress(KeyEvent.VK_V);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_V);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(1000);
        myRobot.keyPress(KeyEvent.VK_ENTER);
        Thread.sleep(1000);
        myRobot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(1000);

        isUpdated=true;
        return isUpdated;
    }

    public static boolean validateScreenshotInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws IOException, Exception {

        boolean isPassed=true;
        int errorCounter=0;
        String strContent=null;

        String fileFullName=logScreenshotElement(driver, element);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(15000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareImage(fileFullName, storeFilePath + storeFileName)) errorCounter++;
            }
        }

        if (errorCounter>0) isPassed=false;
        return isPassed;

    }

    public static boolean validateScreenshot(String storeFileName, String isUpdateStore, String isCompare, WebDriver driver) throws IOException, Exception {

        boolean isPassed=true;
        int errorCounter=0;
        String strContent=null;

        String fileFullName=logScreenshot(driver);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(15000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareImage(fileFullName, storeFilePath + storeFileName)) errorCounter++;
            }
        }

        if (errorCounter>0) isPassed=false;
        return isPassed;

    }

    public static String getCurrentDate3(){
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("EEE d LLL");
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        //System.out.println(dtf.format(now)); //2016/11/16 12:08:43
        String strDateOutput=dtf.format(now);
        strDateOutput=strDateOutput.replace(". ", " ");
        strDateOutput=strDateOutput.replace(".", "");

        //Return format sample "2018-01-12"
        return strDateOutput;
    }

    public static String convertDateFormat(String strOLDDate){
        //31/05/2017 -> 2017-4-30
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date=LocalDate.parse(strOLDDate, formatter);

        formatter=DateTimeFormatter.ofPattern("yyyy-M-dd");
        String strDateOutput=formatter.format(date);
        System.out.println(strDateOutput);

        return strDateOutput;

    }

    public static void executeDOSCommand(String dosCommand) throws Exception {
        SystemLibrary.logMessage("Execute Dos Command '"+dosCommand+"'.");

        try {
            final Process process = Runtime.getRuntime().exec(dosCommand );
            final InputStream in = process.getInputStream();
            int ch;
            while((ch = in.read()) != -1) {
                System.out.print((char)ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static boolean validateStringFile(String strToBeValidated, String storeFileName, String isUpdateStore, String isCompare, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;
        String fileName="StringFile_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        logMessage("The content of the current String to be validated is below:\n");
        System.out.println(strToBeValidated);
        String currentSerailNumber=testSerialNo;
        if (strToBeValidated.contains(currentSerailNumber)){
            strToBeValidated=strToBeValidated.replace(currentSerailNumber, "SN123abc");
        }

        if (strToBeValidated.contains(emailDomainName)){
            strToBeValidated=strToBeValidated.replace(emailDomainName, "sageautomation.com");
        }

        createTextFile(fileFullName, strToBeValidated);
        logMessage("Click the link to access the text content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, fileFullName, storeFileName)) errorCounter++;

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static String getFirstName(String fullName) throws IOException, InterruptedException, Exception {
        String firstName=null;
        int position=fullName.indexOf(" ");
        firstName=fullName.substring(0, position);
        return firstName;
    }


    public static boolean validateTextValueContainedInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName) throws Exception {

        boolean isPassed=false;
        int errorCounter=0;
        String strContent=null;
        String fileName="TextInElement_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        String[] expectedStringList=SystemLibrary.splitString(expectedTextContent, ";");
        int listTotalCount=expectedStringList.length;

        strContent= element.getText();
        logMessage("The Text content of the current element is below:\n");
        System.out.println(strContent);

        ///////////////////
        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);

        if (strContent.contains(testSerialNo)){
            strContent=strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)){
            strContent=strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent=SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent=SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");
        ////

        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)&&(expectedTextContent!=null)){
                    for(int i=0;i<listTotalCount;i++){
                        if (!strContent.contains(expectedStringList[i])) {
                            logError("The expected item '"+expectedStringList[i]+"' is NOT shown.");
                            errorCounter++;
                        }
                        else{
                            logMessage("The item '"+expectedStringList[i]+"' is found as expected.");
                        }
                    }

                }
            }
        }


        if (errorCounter==0){
            isPassed=true;
        }
        return isPassed;
    }

    public static boolean validateStringContainInFile_OLD(String strToBeValidated, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String emailDomainName) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;
        String fileName="StringFile_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        logMessage("The content of the current String to be validated is below:\n");
        System.out.println(strToBeValidated);

        String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);
        if (strToBeValidated.contains(currentSerailNumber)){
            strToBeValidated=strToBeValidated.replace(currentSerailNumber, "SN123abc");
        }

        if (strToBeValidated.contains(emailDomainName)){
            strToBeValidated=strToBeValidated.replace(emailDomainName, "sageautomation.com");
        }

        createTextFile(fileFullName, strToBeValidated);
        logMessage("Click the link to access the text content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, fileFullName, storeFileName)){
            if (expectedTextContent!=null){
                String contentInStoreFile=getStingFromFile(storeFilePath+storeFileName);
                String[] expectedItem=SystemLibrary.splitString(expectedTextContent, ";");
                for (int i=0;i<expectedItem.length;i++){
                    if (!contentInStoreFile.contains(expectedItem[i])){
                        logError("Content '"+expectedItem[i]+"' is NOT found.");
                        errorCounter++;
                    }
                    else{
                        logMessage("Expected content: '"+expectedItem[i]+"' is found.");
                    }
                }
            }
            else errorCounter++;

        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }


    public static String getStingFromFile(String filePath)
    {
        String outputString=null;
        File file=new File(filePath);
        if (file.exists()){
            StringBuilder stringBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
            {
                stream.forEach(s -> stringBuilder.append(s).append("\n"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            outputString=stringBuilder.toString();
        }
        else{
            logError("File '"+filePath+"' is NOT found.");
        }

        return outputString;
    }

    public static LocalDate getAdjustedDate(String timeAdjustment, String fromDate) throws InterruptedException {
        //Use getExpectedDate method for full function.
        LocalDate outputDate;
        //Format and usage of timeAdjustment: -1;DAYS or 2;MONTHS or 5;WEEKS or 8;YEARS
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today=null;

        if (fromDate!=null){
            today=LocalDate.parse(fromDate, dtf);
        }
        else{
            today=LocalDate.now();
        }

        System.out.println(today);
        if (timeAdjustment!=null){
            String[] timeItem=SystemLibrary.splitString(timeAdjustment, ";");
            long timeAdjustNumber=Long.parseLong(timeItem[0]);
            String timeAdjustUnit=timeItem[1];
            ChronoUnit myUnit=ChronoUnit.DAYS;

            switch (timeAdjustUnit){
                case "DAYS":
                    myUnit=ChronoUnit.DAYS;
                    break;
                case "WEEKS":
                    myUnit=ChronoUnit.WEEKS;
                    break;
                case "MONTHS":
                    myUnit=ChronoUnit.MONTHS;
                    break;
                case "YEARS":
                    myUnit=ChronoUnit.YEARS;
                    break;
            }

            outputDate=today.plus(timeAdjustNumber, myUnit);
        }
        else{
            outputDate=today;
        }
        //logMessage("The adjusted date for '"+timeAdjustment+"' is "+dtf.format(outputDate));
        return outputDate;
    }

    public static String getExpectedDate(String timeAdjustment, String fromDate) throws InterruptedException {
        LocalDate outputDate;
        //"3;MONTH;2;MONDAY" means get the date of next 3 months the 2nd Monday in that month.
        //-3;DAYS means the past 3 days
        //1;MONTHS;4;THURSDAY;3;DAYS means next month, the 4th Thursday and add 3 days (Sunday)
        //0;DAYS means today

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] timeItem=SystemLibrary.splitString(timeAdjustment, ";");

        outputDate=getAdjustedDate(timeItem[0]+";"+timeItem[1], fromDate);
        String strOutputDate=outputDate.format(dtf);


        if (timeItem.length>=4){
            if (timeAdjustment.contains("LASTDAYOFMONTH")){
                strOutputDate=SystemLibrary.getTheLastDayOfMonth(dtf.format(outputDate));
            }
            else{
                int weekNumberInMonth=Integer.parseInt(timeItem[2]);
                String weekDayExpected=timeItem[3];
                DayOfWeek expectedDayOfWeek=DayOfWeek.MONDAY;

                switch (weekDayExpected){
                    case "MONDAY":
                        expectedDayOfWeek=DayOfWeek.MONDAY;
                        break;
                    case "TUESDAY":
                        expectedDayOfWeek=DayOfWeek.TUESDAY;
                        break;
                    case "WEDNESDAY":
                        expectedDayOfWeek=DayOfWeek.WEDNESDAY;
                        break;
                    case "THURSDAY":
                        expectedDayOfWeek=DayOfWeek.THURSDAY;
                        break;
                    case "FRIDAY":
                        expectedDayOfWeek=DayOfWeek.FRIDAY;
                        break;
                    case "SATURSDAY":
                        expectedDayOfWeek=DayOfWeek.SATURDAY;
                        break;
                    case "SUNDAY":
                        expectedDayOfWeek=DayOfWeek.SUNDAY;
                        break;

                }
                outputDate=outputDate.with(TemporalAdjusters.dayOfWeekInMonth(weekNumberInMonth, expectedDayOfWeek));
                strOutputDate=dtf.format(outputDate);
            }


        }


        if(timeItem.length==6){
            //Add extra time
            strOutputDate=dtf.format(getAdjustedDate(timeItem[4]+";"+timeItem[5], strOutputDate));
        }


        logMessage("The adjusted date for '"+timeAdjustment+"' is "+strOutputDate);

        return strOutputDate;
    }

    public static String getTheLastDayOfMonth_OLD(String strDate) throws InterruptedException {
        //strDate format is dd/MM/YYYY
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate convertedDate = LocalDate.parse(strDate, dtf);
        convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
        dtf=DateTimeFormatter.ofPattern("yyy-MM-dd");
        String outputDate=dtf.format(convertedDate);
        logMessage("The last date of the month is "+outputDate);
        return outputDate;
    }

    public static int getMonthNumber(String strDate) throws InterruptedException {
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyy-MM-dd");

        LocalDate convertedDate = LocalDate.parse(strDate, dtf);
        int monthValue=convertedDate.getMonthValue();
        logMessage("The month value is "+String.valueOf(monthValue));
        return monthValue;

    }

    public static String getTheLastDayOfMonth(String strDate){
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate convertedDate = LocalDate.parse(strDate, dtf);
        convertedDate = convertedDate.withDayOfMonth(
                convertedDate.getMonth().length(convertedDate.isLeapYear()));
        String strOutputDate=convertedDate.format(dtf);
        System.out.println("The last date of '"+strDate+"' is "+strOutputDate);
        return strOutputDate;
    }

    public static boolean updateAndValidateStoreStringFile(String isUpdateStore, String isCompare, String logFileFullName, String storeFileName, String expectedTextContent) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, logFileFullName, storeFileName)){
            if (expectedTextContent!=null){
                String contentInStoreFile=getStingFromFile(logFileFullName);
                String[] expectedItem=SystemLibrary.splitString(expectedTextContent, ";");
                for (int i=0;i<expectedItem.length;i++){
                    if (!contentInStoreFile.contains(expectedItem[i])){
                        logError("Content '"+expectedItem[i]+"' is NOT found.");
                        errorCounter++;
                    }
                    else{
                        logMessage("Expected content: '"+expectedItem[i]+"' is found.");
                    }
                }
            }
            else errorCounter++;

        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }



    public static boolean copyFile(String strFileFullNameToBeMoved, String strNewFileFullName){
        boolean isDone=false;
        int errorCounter=0;
        File myOldFile=new File(strFileFullNameToBeMoved);
        File myNewFile=new File(strNewFileFullName);

        try {
            Files.copy(myOldFile.toPath(), myNewFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;
    }

    public static WebDriver launchWebDriver_Performance(String url, int driverType ) throws InterruptedException {
        //driverType 1 - IE, 2 - Firefox, 3 - Google Chrome
        WebDriver driver=null;
        String driverName="";
        File file=null;
        switch (driverType) {

            case 1:
                file = new File(driverPath+"IEDriverServer.exe");
                System.out.println(sysPath+"IEDriverServer.exe");
                System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

                driver = new InternetExplorerDriver();
                driverName="InternetExplorerDriver";
                break;

            /*
            case 2:
                file =new File(driverPath+"geckodriver.exe");
                System.out.println(file.getAbsoluteFile());
                //System.setProperty("webdriver.firefox.driver", file.getAbsolutePath());
                System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
                //System.setProperty("webdriver.firefox.marionette", file.getAbsolutePath());
                ProfilesIni profile=new ProfilesIni();
                //FirefoxProfile myprofile=profile.getProfile("C:\\SelTestProject\\SelTesterFirefoxProfile");
                FirefoxProfile myprofile=profile.getProfile("SelTester");
                myprofile.setAcceptUntrustedCertificates(true);
                myprofile.setAssumeUntrustedCertificateIssuer(false);

                DesiredCapabilities d=new DesiredCapabilities();
                d.setCapability("marionette", true);
                driver = new FirefoxDriver(myprofile);
                //driver = new FirefoxDriver();
                driverName="FirefoxDriver";
                break;
            */
            case 2:
                file =new File(driverPath+"geckodriver.exe");
                System.out.println(file.getAbsoluteFile());
                System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
                driver=new FirefoxDriver();
                driverName="FirefoxDriver";
                break;

            case 3:
                file = new File(driverPath+"//chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());


                ChromeOptions chromeOptions=new ChromeOptions();
                chromeOptions.addArguments("test-type");
                chromeOptions.addArguments("headless");
                chromeOptions.addArguments("window-size=1680x1050");
                driver = new ChromeDriver(chromeOptions);

                driverName="ChromeDriver";
                break;
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //driver.navigate().to("https://esp.cloud.micropay.com.au/espsage/");
        //driver.get(url);
        driver.navigate().to(url);
        driver.manage().window().maximize();
        logMessage("WebDriver: "+driverName+" is launched.");
        logMessage("Web page: \""+url+"\" is opened.");

        ////////////////////
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        logMessage("Web Page title: \""+driver.getTitle()+"\".");

        /*
        try {
            captureScreenshot("LaunchWebDriver");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return driver;
    }

    public static boolean validateTextValueInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String testSerialNo, String emailDomainName) throws Exception {

        boolean isPassed=false;
        int errorCounter=0;
        String strContent=null;
        String fileName="TextInElement_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        if (expectedTextContent.contains(emailDomainName)){
            expectedTextContent=expectedTextContent.replace(emailDomainName, "sageautomation.com");
        }

        String[] expectedStringList=SystemLibrary.splitString(expectedTextContent, ";");
        int listTotalCount=expectedStringList.length;

        strContent= element.getText();
        logMessage("The Text content of the current element is below:\n");
        System.out.println(strContent);

        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);
        if (strContent.contains(testSerialNo)){
            strContent=strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)){
            strContent=strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent=SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent=SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");


        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)&&(expectedTextContent!=null)){
                    for(int i=0;i<listTotalCount;i++){
                        if (!strContent.contains(expectedStringList[i])) {
                            logError("The expected item '"+expectedStringList[i]+"' is NOT shown.");
                            errorCounter++;
                        }
                        else{
                            logMessage("The item '"+expectedStringList[i]+"' is found as expected.");
                        }
                    }

                }
            }
        }


        if (errorCounter==0){
            isPassed=true;
        }
        return isPassed;
    }

    public static boolean validateTextValueInWebElementInUse(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        if (expectedContent!=null){
            if (!SystemLibrary.validateTextValueInElement(element, storeFileName, isUpdateStore, isCompare, expectedContent, testSerialNo, emailDomainName)) errorCounter++;
        }
        else{
            if (!SystemLibrary.validateTextValueInElement(element, storeFileName, isUpdateStore, isCompare, testSerialNo, emailDomainName)) errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static void zoomPage(String zoomSize, WebDriver driver) throws InterruptedException {
        //zoomSize 1=100%, 1.5=150%, 0.8=80%
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("document.body.style.zoom = '"+zoomSize+"'");
    }

    public static String convertDateFormat_Pro(String strOLDDate, String datePattern){
        //31/05/2017 -> 2017-4-30
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date=LocalDate.parse(strOLDDate, formatter);

        formatter=DateTimeFormatter.ofPattern(datePattern);
        String strDateOutput=formatter.format(date);
        System.out.println(strDateOutput);

        return strDateOutput;
    }

    public static String convertDateFormat_3(String strOLDDate){
        //2017-04-30 -> 31/05/2017
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date=LocalDate.parse(strOLDDate, formatter);

        formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String strDateOutput=formatter.format(date);
        System.out.println(strDateOutput);

        return strDateOutput;

    }

    public static String getFileExtentionName(String filename){
        return FilenameUtils.getExtension(filename);
    }

    public static boolean updateAndValidateStorePDFFile(String updateStore, String validateResult, String logFileFullName, String storeFileName, String expectedContent) throws IOException, InterruptedException {
        boolean isDone=false;
        int errorCounter=0;
        if (updateStore!=null){
            if (updateStore.equals("1")){
                if (!SystemLibrary.saveFileToStore(logFileFullName, storeFileName)) errorCounter++ ;
            }
        }

        Thread.sleep(10000);

        if (validateResult!=null){
            if (validateResult.equals("1")){
                if (expectedContent==null){
                    if (!SystemLibrary.comparePDFFile(logFileFullName, storeFilePath+storeFileName)) errorCounter++;
                }else{

                }

            }
        }
        if (errorCounter==0) isDone=true;
        return isDone;
    }


    public static String getStringFromPDFFile(String fileFullPathName) throws IOException {
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        File file = new File(fileFullPathName);
        PDFParser parser=new PDFParser(new RandomAccessFile(file, "r"));

        // PDFBox 2.0.8 require org.apache.pdfbox.io.RandomAccessRead
        // RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        // PDFParser parser = new PDFParser(randomAccessFile);

        parser.parse();
        cosDoc = parser.getDocument();
        pdfStripper = new PDFTextStripper();
        pdDoc = new PDDocument(cosDoc);
        pdfStripper.setStartPage(1);
        pdfStripper.setEndPage(getPDFPageNumber(fileFullPathName));
        String parsedText = pdfStripper.getText(pdDoc);
        System.out.println(parsedText);
        pdDoc.close();

        return parsedText;
    }

    public static int getPDFPageNumber(String fileFullPathName) throws IOException {
        PDDocument doc=PDDocument.load(new File(fileFullPathName));
        int count=doc.getNumberOfPages();
        return count;
    }

    public static boolean validateStringContainInFile(String strToBeValidated, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, String emailDomainName, String testSerialNo) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;
        String fileName="StringFile_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        logMessage("The content of the current String to be validated is below:\n");
        System.out.println(strToBeValidated);

        String currentSerailNumber=testSerialNo;
        if (testSerialNo!=null){
            if (strToBeValidated.contains(currentSerailNumber)){
                strToBeValidated=strToBeValidated.replace(currentSerailNumber, "SN123abc");
            }
        }

        if (emailDomainName!=null){
            if (strToBeValidated.contains(emailDomainName)){
                strToBeValidated=strToBeValidated.replace(emailDomainName, "sageautomation.com");
            }
        }


        createTextFile(fileFullName, strToBeValidated);
        logMessage("Click the link to access the text content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        String strToBeValidiated2=getStingFromFile(fileFullName);

        if (!updateAndValidateStoreStringFile(isUpdateStore, isCompare, fileFullName, storeFileName)){
            if (expectedTextContent!=null){
                String[] expectedItem=SystemLibrary.splitString(expectedTextContent, ";");
                for (int i=0;i<expectedItem.length;i++){
                    if (!strToBeValidiated2.contains(expectedItem[i])){
                        logError("Content '"+expectedItem[i]+"' is NOT found.");
                        errorCounter++;
                    }
                    else{
                        logMessage("Expected content: '"+expectedItem[i]+"' is found.");
                    }
                }
            }
            else errorCounter++;

        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static void clearupDriver() throws Exception {
        SystemLibrary.logMessage("Terminate Any existing ChromeDriver.exe and Chrome.exe.");
        SystemLibrary.executeDOSCommand("Taskkill /IM chrome.exe /F");
        SystemLibrary.executeDOSCommand("Taskkill /IM chromedriver.exe /F");
    }

    public static String getHostName() throws UnknownHostException {
        String hostName=null;
        InetAddress myHost = InetAddress.getLocalHost();
        hostName=myHost.getHostName();
        System.out.println("This host name is '"+hostName+"'.");
        return hostName;
    }

    public static Boolean isVisibleInView(WebElement element, WebDriver driver) throws InterruptedException {
        boolean isInView;
        isInView=(Boolean)((JavascriptExecutor)driver).executeScript(
                "var elem = arguments[0],                 " +
                        "  box = elem.getBoundingClientRect(),    " +
                        "  cx = box.left + box.width / 2,         " +
                        "  cy = box.top + box.height / 2,         " +
                        "  e = document.elementFromPoint(cx, cy); " +
                        "for (; e; e = e.parentElement) {         " +
                        "  if (e === elem)                        " +
                        "    return true;                         " +
                        "}                                        " +
                        "return false;                            "
                , element);
        if (isInView){
            logMessage("Element is in view.");
        }else{
            logWarning("Element is NOT in view");
        }
        return isInView;
    }

    public static Boolean isElementClickable(WebElement element, WebDriver driver){
        boolean isClickable=false;
        try {
            new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(element));
            System.out.println("Element is clickable");
            isClickable=true;
        }
        catch(TimeoutException e) {
            System.out.println("Element isn't clickable");
        }
        return isClickable;
    }

    public static List<String> getTenantDetails(String filePathName) throws IOException {
        List<String> tenantDetails=getStringListFromFile(filePathName);
        return tenantDetails;
    }

    public static List<String> getStringListFromFile(String filePathName) throws IOException {
        List<String> records = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(filePathName));
        String line;
        while ((line = reader.readLine()) != null)
        {
            records.add(line);
        }
        reader.close();
        return records;
    }

    public static String getClipboard() throws IOException, UnsupportedFlavorException, InterruptedException {
        Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
        String str_Clipboard=(String)c.getData(DataFlavor.stringFlavor);
        logMessage("The current Clipboard is below:");
        System.out.println(str_Clipboard);
        return str_Clipboard;
    }


    public static void clearClipboard(){
        StringSelection stringSelection=new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public static boolean isElementClickable(WebElement element){
        boolean isClickable=false;
        if (ExpectedConditions.elementToBeClickable(element)!=null){
            isClickable=true;
        }
        return isClickable;
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

    public static String getValueFromListFile(String itemName, String seperator, String filePathName) throws IOException, InterruptedException {
        String itemValue=null;
        List<String> itemList=SystemLibrary.getStringListFromFile(filePathName);

        int totalLineNumber=itemList.size();
        for(int i=0;i<totalLineNumber;i++){
            String currentItem=itemList.get(i);
            if (currentItem.contains(itemName)){
                itemValue=currentItem.replace(itemName+seperator, "");
                break;
            }
        }

        SystemLibrary.logMessage("The item '"+itemName+"' value is '" + itemValue + "'.");
        return itemValue;
    }

    public static Boolean createTextTempFile(String fileFullName, String logMessage) throws InterruptedException {
        Boolean isSaved=false;
        try {
            FileWriter myFW=new FileWriter(fileFullName, true);
            myFW.write(logMessage);
            Thread.sleep(500);
            myFW.close();
            isSaved=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("The file \""+fileName+"\" is saved.");
        return isSaved;
    }

    public static String getEmailServerNameFromEmaill(String emailAddress){
        int i=emailAddress.indexOf("@");
        return emailAddress.substring(emailAddress.indexOf("@")+1);
    }


    public static String generateUnitKey() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }


    public static WebDriver launchDebugPage1(int driverType) throws InterruptedException {
        WebDriver driver = SystemLibrary.launchWebDriver("C:\\TestAutomationProject\\ESSRegTest\\src\\PageObject\\DebugPage1.html", driverType);
        return driver;
    }

    public static boolean isProcessRunning(String processName) throws Exception {
        int processCount=0;
        Process p = Runtime.getRuntime().exec("tasklist");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {

            //System.out.println(line);
            if (line.contains(processName)) {
                processCount++;
            }
        }

        if (processCount==0){
            logMessage("Process '"+processName+"' is NOT runnning.");
            return false;
        }else{
            logMessage("There are total "+String.valueOf(processCount)+" '"+processName+"' session(s) running.");
            return true;
        }
    }

    public static void killProcess(String processName) throws Exception {
        if(isProcessRunning(processName)){
            Runtime.getRuntime().exec("taskkill /F /IM " + processName);
            logMessage("Process '"+processName+"' is terminated.");
        }
    }

    //////////////// Modify String content in text file  //////////////
    public static boolean modifyTextFile(String filePath, String oldString, String newString) throws InterruptedException {
        boolean isDone=false;
        int errorCounter=0;


        File fileToBeModified = new File(filePath);

        if (fileToBeModified.exists()){
            String oldContent = "";
            BufferedReader reader = null;
            FileWriter writer = null;

            try
            {
                reader = new BufferedReader(new FileReader(fileToBeModified));
                //Reading all the lines of input text file into oldContent
                String line = reader.readLine();
                while (line != null)
                {
                    oldContent = oldContent + line + System.lineSeparator();
                    line = reader.readLine();
                }

                //Replacing oldString with newString in the oldContent
                if (!oldContent.contains(oldString)) errorCounter++;
                String newContent = oldContent.replaceAll(oldString, newString);
                //Rewriting the input text file with newContent
                writer = new FileWriter(fileToBeModified);
                writer.write(newContent);
            }
            catch (IOException e)
            {
                errorCounter++;
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    //Closing the resources
                    reader.close();
                    writer.close();
                }
                catch (IOException e)
                {
                    errorCounter++;
                    e.printStackTrace();
                }
            }
        }
        else{
            logError("File '"+filePath+"' is NOT found.");
            errorCounter++;
        }



        if (errorCounter==0) {
            isDone=true;
            logMessage("File: '"+filePath+"' is updated.");
        }
        else{
            logMessage("File: '"+filePath+"' is NOT updated.");
        }
        return isDone;
    }

    public static boolean validateTextValueInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String expectedTextContent, WebElement subElement, String testSerialNo, String emailDomainName) throws Exception {

        boolean isPassed=false;
        int errorCounter=0;
        String strContent=null;
        String fileName="TextInElement_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        if (expectedTextContent.contains(emailDomainName)){
            expectedTextContent=expectedTextContent.replace(emailDomainName, "sageautomation.com");
        }

        String[] expectedStringList=SystemLibrary.splitString(expectedTextContent, ";");
        int listTotalCount=expectedStringList.length;

        strContent= element.getText();

        if (subElement!=null){
            strContent=strContent+"\r\n"+"Extra Sub Element Value: "+subElement.getAttribute("value");
        }

        logMessage("The Text content of the current element is below:\n");
        System.out.println(strContent);

        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);
        if (strContent.contains(testSerialNo)){
            strContent=strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)){
            strContent=strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent=SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent=SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");


        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)&&(expectedTextContent!=null)){
                    for(int i=0;i<listTotalCount;i++){
                        if (!strContent.contains(expectedStringList[i])) {
                            logError("The expected item '"+expectedStringList[i]+"' is NOT shown.");
                            errorCounter++;
                        }
                        else{
                            logMessage("The item '"+expectedStringList[i]+"' is found as expected.");
                        }
                    }

                }
            }
        }


        if (errorCounter==0){
            isPassed=true;
        }
        return isPassed;
    }

    public static boolean validateTextValueInWebElementInUse(WebElement element, String storeFileName, String isUpdateStore, String isCompare, String expectedContent, WebElement subElement, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed=false;
        int errorCounter=0;

        if (expectedContent!=null){
            if (!SystemLibrary.validateTextValueInElement(element, storeFileName, isUpdateStore, isCompare, expectedContent, subElement, testSerialNo, emailDomainName)) errorCounter++;
        }
        else{
            if (!SystemLibrary.validateTextValueInElement(element, storeFileName, isUpdateStore, isCompare, subElement, testSerialNo, emailDomainName)) errorCounter++;
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean validateTextValueInElement(WebElement element, String storeFileName, String isUpdateStore, String isCompare, WebElement subElement, String testSerialNo, String emailDomainName) throws Exception {

        boolean isPassed=false;
        int errorCounter=0;
        String strContent=null;
        String fileName="TextInElement_"+ getCurrentTimeAndDate()+".txt";
        String fileFullName= workingFilePath +fileName;

        strContent= element.getText();

        if (subElement!=null){
            strContent=strContent+"\r\n"+"Extra Sub Element Value: "+subElement.getAttribute("value");
        }

        logMessage("The Text content of the current element is below:\n");
        System.out.println(strContent);

        //String currentSerailNumber=GeneralBasic.getTestSerailNumber_Main(101, 101);
        if (strContent.contains(testSerialNo)){
            strContent=strContent.replace(testSerialNo, "SN123abc");
        }

        if (strContent.contains(emailDomainName)){
            strContent=strContent.replace(emailDomainName, "sageautomation.com");
        }

        strContent=SystemLibrary.replaceItemInString(strContent, "F_SN123abc", 26, "F2345678901234567890123456");
        strContent=SystemLibrary.replaceItemInString(strContent, "L_SN123abc", 26, "L2345678901234567890123456");


        createTextFile(fileFullName, strContent);
        logMessage("Click the link to access the table content "+ serverUrlAddress +"/TestLog/WorkingFile/"+fileName);

        if (isUpdateStore!=null) {
            if (isUpdateStore.equals("1")){
                if (!saveFileToStore(fileFullName, storeFileName)) errorCounter++;
            }
        }

        Thread.sleep(10000);

        if (isCompare!=null) {
            if (isCompare.equals("1")) {
                if (!compareTextFile(fileFullName, storeFilePath + storeFileName)) errorCounter++;
            }
        }

        if (errorCounter==0) isPassed=true;
        return isPassed;

    }

    public static void scrollFromTopToHalfWindow(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 600)");
    }

    public static void scrollFromTopToquarterWindow(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 300)");
    }

    public static void scrollFromBottomToHalfWindow(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(600, 0)");
    }

    public static String generateNewTestSerialNo(String snPrefix) throws InterruptedException, SQLException, ClassNotFoundException {
        String newTestSerialNO=null;
        String currentTestSerialNo=DBManage.getLatestSerialNumberFromReportDB(snPrefix, null);
        int newNumber=Integer.valueOf(currentTestSerialNo.replace(snPrefix, ""))+1;
        String strNewNumber=String.valueOf(newNumber);
        int sizeOfNewNumber=strNewNumber.length();
        if (sizeOfNewNumber==1){
            newTestSerialNO=snPrefix+"00"+strNewNumber;
        }else if (sizeOfNewNumber==2){
            newTestSerialNO=snPrefix+"0"+strNewNumber;
        }else{
            newTestSerialNO=snPrefix+String.valueOf(newNumber);
        }

        logMessage("The new testSerailNo is '"+newTestSerialNO+"'.");
        return newTestSerialNO;
    }

    public static int getPhoneType(WebDriver driver){
        int phoneType=1;
        Dimension windowDimension=driver.manage().window().getSize();
        int winWidth=windowDimension.getWidth();
        int winHeight=windowDimension.getHeight();

        if ((winWidth==414)&&(winHeight==736)){
            phoneType=5; //Iphone6 Plus
        }
        return phoneType;
    }

    public static boolean isClickable(WebElement element, WebDriver driver)
    {
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static void clearTextBox(WebElement element) throws InterruptedException {
        String inputText = element.getAttribute("value");
        element.sendKeys(Keys.CONTROL+"a");
        element.sendKeys(Keys.DELETE);
        if( inputText != null ) {
            for(int i=0; i<inputText.length();i++) {
                element.sendKeys(Keys.BACK_SPACE);
            }
        }
    }

    public static void refreshPage(WebDriver driver) throws InterruptedException {
        driver.navigate().refresh();
        Thread.sleep(10000);
    }
    //////////////////////// Debug here //////////////////////


}
