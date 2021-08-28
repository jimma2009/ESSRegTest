package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import Lib.SystemLibraryHigh;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import java.io.IOException;
import java.util.List;

import static Lib.SystemLibrary.*;

public class PageObj_Approvals {
    private static WebElement element = null;

    public static WebElement approvals_MyApprovals_Dropdown(WebDriver driver) {
        element = driver.findElement(By.xpath("//*[@id=\"page-title\"]/h3"));
        return element;
    }

    public static WebElement label_MyApprovals(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='page-title']/h3[text()='My Approvals']"));
    }

    public static WebElement dropdownList_OtherApprovals(WebDriver driver){
        return driver.findElement(By.linkText("Other Approvals"));
    }

    public static WebElement dropdownList_MyApprovals(WebDriver driver){
        return driver.findElement(By.linkText("My Approvals"));
    }


    public static WebElement approvals_MyApprovals_MyApprovals(WebDriver driver) {
        element = driver.findElement(By.xpath("//*[@id=\"page-title\"]/ul/li[1]/a"));
        return element;
    }

    public static WebElement approvals_MyApprovals_OtherApprovals(WebDriver driver) {
        element = driver.findElement(By.xpath("//*[@id=\"page-title\"]/ul/li[2]/a"));
        return element;
    }

    public static WebElement approvals_All(WebDriver driver) {
        element = driver.findElement(By.xpath("//button[contains(@class,'button')][contains(text(),'All')]"));
        return element;
    }

    public static WebElement approvals_LeaveApplications(WebDriver driver) {
        element = driver.findElement(By.xpath("//button[contains(@class,'button')][contains(text(),'Leave Applications')]"));
        return element;
    }

    public static WebElement approvals_ProfileChanges(WebDriver driver) {
        element = driver.findElement(By.xpath("//button[contains(@class,'button')][contains(text(),'Profile Changes')]"));
        return element;
    }

    public static WebElement form_ApprovalList(WebDriver driver) {
        element = driver.findElement(By.xpath("//div[@class='list-container']"));
        return element;
    }

    public static WebElement button_Expand(WebDriver driver){
         return driver.findElement(By.xpath("//div[@class='page-header']/div[@id='page-controls']/button[@class='button--plain add-button']"));
    }

    public static WebElement button_SelectAll(WebDriver driver){
        return SystemLibrary.waitChild("//button[@class='button button--square']", 120, 1, driver);
    }

    public static WebElement button_ApproveAll(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='page-controls']//button[contains(@class,'button button--success')]"));
    }

    public static WebElement button_DeclineAllSelected(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='page-controls']//button[contains(@class,'button button--danger')]"));
    }

    public static WebElement getLinkDescriptionInOtherApprovalScreen(String firstName, String middleName, String lastName, String preferredName, String changeType, String submitDate, WebDriver driver){
        WebElement element=null;
        String strXpath=null;
        if (submitDate==null){
            strXpath="//div[@class='approval-content' and contains(., '"+firstName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"')]";
        }
        else{
            strXpath="//div[@class='approval-content' and contains(., '"+firstName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"') and contains(., '"+submitDate+"')]";
        }

        element= SystemLibrary.waitChild(strXpath, 5000, 1, driver);

        return element;
    }

    public static WebElement lable_ApproveArea(WebDriver driver){
        return driver.findElement(By.xpath("//h3[@class='selectable']"));
    }

    public static WebElement getApprovalLink(String firstName, String middleName, String lastName, String preferredName, String changeType, String submitDate, WebDriver driver){
        WebElement element=null;
        String strXpath=null;
        if (submitDate==null){
            if (preferredName==null){
                strXpath="//div[@class='approval-content' and contains(., '"+firstName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"')]//a[@class='link descriptions']";
            }
            else{
                strXpath="//div[@class='approval-content' and contains(., '"+preferredName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"')]//a[@class='link descriptions']";
            }

        }
        else{
            if (preferredName==null){
                strXpath="//div[@class='approval-content' and contains(., '"+firstName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"') and contains(., '"+submitDate+"')]//a[@class='link descriptions']";
            }
            else{
                strXpath="//div[@class='approval-content' and contains(., '"+preferredName+"') and contains(., '"+lastName+"') and contains(., '"+changeType+"') and contains(., '"+submitDate+"')]//a[@class='link descriptions']";
            }

        }

        element= SystemLibrary.waitChild(strXpath, 10, 1, driver);

        return element;
    }

    public static WebElement getApprovalLink_Main(int startSerialNo, int endSerialNo, WebDriver driver) throws Exception {
        logMessage("--- Start getting Approval Link from row " + startSerialNo + " to " + endSerialNo + " in \"ApproveItems\" sheet.");
        WebElement linkElement=null;
        boolean isPassed=true;
        int errorCounter=0;

        int serialNo=0;

        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ApproveItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    linkElement=getApprovalLink(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][8], cellData[i][16], driver);
                    if (linkElement==null) errorCounter++;
                }
                else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0])>=endSerialNo) break;
            }

        }
        if (errorCounter>0) isPassed=false;
        logMessage("*** End of getting Approval Link ");
        return linkElement;
    }

    public static boolean validateMultiWebControlInOtherApporvalsPage(WebDriver driver) throws InterruptedException, IOException {
        //display Other Approvals page first
        boolean isPassed=false;
        int errorCounter=0;

        WebElement icon_MultiSelection=SystemLibrary.waitChild("//i[@class='icon-multiselect']", 10, 1, driver);
        if (icon_MultiSelection!=null){
            icon_MultiSelection.click();
            Thread.sleep(2000);
            logMessage("Multi Selection icon is clicked.");
            logScreenshot(driver);

            logMessage("Step I: Validate Button Approve All.");
            WebElement button_ApproveAllNew=SystemLibrary.waitChild("//div[@id='page-controls']//button[contains(@class,'button')][contains(text(),'Approve')]", 10, 1, driver);
            if (button_ApproveAllNew!=null){
                if (button_ApproveAllNew.isEnabled()==true){
                    logError("Button Approve All is enabled, NOT as expected.");
                    errorCounter++;
                }
            }else{
                logError("Button Approve All is NOT shown.");
                errorCounter++;
            }

            logMessage("Step II: Validate Button Declinet All.");
            WebElement button_DeclineAllNew=SystemLibrary.waitChild("//div[@id='page-controls']//button[contains(@class,'button')][contains(text(),'Decline')]", 10, 1, driver);
            if (button_ApproveAllNew!=null){
                if (button_DeclineAllNew.isEnabled()==true){
                    logError("Button Decline All is enabled, NOT as expected.");
                    errorCounter++;
                }
            }else{
                logError("Button Decline All is NOT shown.");
                errorCounter++;
            }

            logMessage("Step III: Validate Icon Select All");
            WebElement icon_SelectAll=SystemLibrary.waitChild("//div[@class='page-header']//button[@class='button button--square']", 10, 1, driver);
            if (icon_SelectAll!=null){
                List<WebElement> checkbox_All=driver.findElements(By.className("checkbox-column"));
                if (checkbox_All!=null){
                    if (checkbox_All.get(0).isSelected()==true){
                        logError("Checkbox box is ticked, NOT as expected.");
                        errorCounter++;
                    }
                }else{
                    logError("Checkbox is NOT shown.");
                    errorCounter++;
                }
            }else{
                logError("Icon Select All is NOT shown.");
                errorCounter++;
            }

            if (errorCounter==0){
                icon_SelectAll.click();
                Thread.sleep(3000);
                logMessage("Step IV: Icon Select All is clicked.");
                logScreenshot(driver);

                logMessage("Step V: Valdiate Approval All butotn.");
                if (!button_ApproveAllNew.isEnabled()){
                    logError("Button Approve All is disabled, NOT as expected.");
                    errorCounter++;
                }

                logMessage("Step VI: Validate Decline All button.");
                if (!button_DeclineAllNew.isEnabled()){
                    logError("Button Decline All is disabled, NOT as expected.");
                    errorCounter++;
                }

                logMessage("Step VII: Validate single check box.");
                List<WebElement> checkbox_All=driver.findElements(By.className("checkbox-column"));
                if (checkbox_All.get(0).isSelected()==true){
                    logError("Checkbox is NOT selected, NOT as expected.");
                    errorCounter++;
                }

                logMessage("Step VIII: click cancel butotn.");
                WebElement button_Cancel=SystemLibrary.waitChild("//button[contains(text(),'Cancel')]", 10, 1, driver);
                if (button_Cancel!=null){
                    button_Cancel.click();
                    Thread.sleep(2000);
                    logMessage("Cancel butotn is clicked.");
                }
                else{
                    logError("Cancel button is NOT shown.");
                    errorCounter++;
                }

            }else{
                logError("Stop validation with Error.");
            }

        }
        else{
            logError("Multi Selection icon is NOT shown.");
            errorCounter++;

        }


        if (errorCounter==0) isPassed=true;
        return isPassed;
    }

    public static boolean openLeavePageViaApprovalPage(String userFullName, WebDriver driver) throws Exception{
        boolean isDone=false;
        int errorCounter=0;

        GeneralBasic.displayOtherApprovalsPage_ViaNavigationBar(driver);
        String xpath_LeaveLink="//div[contains(@id, 'list-item')]//a[@class='link descriptions' and contains(., '"+userFullName+"')]";
        WebElement leaveLink=SystemLibrary.waitChild(xpath_LeaveLink, 10, 1, driver);
        if (leaveLink!=null){
            SystemLibrary.displayElementInView(leaveLink, driver, 10);
            leaveLink.click();
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(120, driver);

            WebElement lable_Leave=SystemLibrary.waitChild("//div[@id='pl-header']//h4[contains(text(),'Leave')]", 10, 1, driver);
            if (lable_Leave!=null){
                logMessage(userFullName+"'s Leave page is shown.");
                logScreenshot(driver);
            }else{
                logError(userFullName+"'s Leave page is NOT shown.");
                errorCounter++;
            }

        }else{
            logError("User: "+userFullName+"'s leave link is NOT found.");
            errorCounter++;
        }

        if (errorCounter==0) isDone=true;
        return isDone;
    }
}
