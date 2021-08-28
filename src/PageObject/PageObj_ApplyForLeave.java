package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.List;

import static Lib.SystemLibrary.*;

public class PageObj_ApplyForLeave {
    private static WebElement element=null;

    public static WebElement panel_ApplyForLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[3]/div[1]/div/a/div"));
        return element;
    }


    public static WebElement textBox_StartDate(WebDriver driver){
        element=driver.findElement(By.id("startDate"));
        return element;
    }

    public static WebElement textBox_EndDate(WebDriver driver){
        element=driver.findElement(By.id("endDate"));
        return element;
    }

    /*
    public static WebElement textBox_LeaveHours(WebDriver driver){
        element=driver.findElement(By.id("leaveHours"));
        return element;
    }
    */

    public static WebElement textBox_LeaveHours(WebDriver driver) throws InterruptedException {
        return SystemLibrary.waitChild("//input[@id='leaveHours']", 180, 1,driver);
    }

    public static boolean selectLeaveTypeInApplyLeaveScreen(String leaveReason, WebDriver driver) throws InterruptedException, IOException {
        boolean isSelected=false;
        int errorCounter=0;

        //Click the dropdown button
        WebElement myDropDownList=SystemLibrary.waitChild("//*[@id=\"leaveReason\"]", 10, 1, driver);
        if (myDropDownList!=null){
            myDropDownList.click();
            Thread.sleep(2000);
            logMessage("Leave Reason dropdown list is clicked.");
            logScreenshot(driver);

            WebElement listItem=SystemLibrary.waitChild("//div[contains(text(),'"+leaveReason+"')]", 10, 1, driver);
            if (listItem!=null){
                listItem.click();
                Thread.sleep(2000);
                logMessage("Leave Reason"+leaveReason+" is clicked.");
                logScreenshot(driver);
            }else{
                logError("Leave Reason"+leaveReason+" is NOT found.");
                errorCounter++;
            }

        }else{
            logError("Leave Reason dropdown list is NOT shown.");
            errorCounter++;
        }

        if (errorCounter==0) isSelected=true;
        return isSelected;
    }

    public static boolean selectLeaveTypeInApplyLeaveScreen_OLD(String leaveReason, WebDriver driver) throws InterruptedException {
        boolean isSelected=false;
        int errorCounter=0;

        //Click the dropdown button
        WebElement myDropDownList=driver.findElement(By.xpath("//*[@id=\"leaveReason\"]"));
        Actions action = new Actions(driver);
        action.moveToElement(myDropDownList).click().build().perform();
        Thread.sleep(10000);

        List<WebElement> myList=driver.findElements(By.xpath("//*[@id=\"leaveReason-select-list\"]/div"));

        int totalListCount=myList.size();
        logMessage("There are total "+totalListCount+" Leave Reasons in the list.");
        String currentListItem=null;
        for (int i=1;i<=totalListCount;i++){
            WebElement currentElement=driver.findElement(By.xpath("//*[@id=\"leaveReason-select-list\"]/div["+i+"]"));
            if (leaveReason.equals(currentElement.getText())){
                action.moveToElement(currentElement).click().build().perform();
                //currentElement.click();
                Thread.sleep(10000);
                SystemLibrary.logMessage("Leave Type: "+leaveReason+" is selected.");
                isSelected=true;
                break;
            }
        }
        return isSelected;
    }

    public static boolean submitAttachmentInApplyLeaveForm(String attachmentFileName, WebDriver driver) throws InterruptedException, IOException {
        boolean isAttached=false;
        int errorCounter=0;

        WebElement button_Attachment=SystemLibrary.waitChild("//input[@name='attachmentname' and @type='file']", 60, 1, driver);
        if (button_Attachment!=null){
            String fileFullPath=dataSourcePath+attachmentFileName;

            button_Attachment.sendKeys(fileFullPath);
            Thread.sleep(5000);

            logMessage("File is attached.");
            logScreenshot(driver);
        }
        else{
            errorCounter++;
        }

        if (errorCounter==0) isAttached=true;
        return isAttached;
    }

    public static boolean submitAttachmentInApplyLeaveForm_OLD(String attachmentFileName, WebDriver driver) throws InterruptedException, IOException {
        boolean isAttached=false;
        int errorCounter=0;

        WebElement button_Attachment=SystemLibrary.waitChild("//input[@name='attachmentname']", 60, 1, driver);
        if (button_Attachment!=null){
            button_Attachment.click();
            Thread.sleep(5000);

            logMessage("Attachment is clicked.");
            logScreenshot(driver);
            String fileFullPath=dataSourcePath+attachmentFileName;
            Runtime.getRuntime().exec("C:\\TestAutomationProject\\ESSRegTest\\src\\AutoITScript\\UploadFileInESS.exe "+fileFullPath);
            Thread.sleep(30000);
        }
        else{
            errorCounter++;
        }

        if (errorCounter==0) isAttached=true;
        return isAttached;
    }

    public static WebElement textbox_comment(WebDriver driver){
        element=driver.findElement(By.id("comments"));
        return element;
    }

    public static WebElement button_Apply(WebDriver driver){
        element=driver.findElement(By.id("button-apply-leave-apply"));
        return element;
    }

    public static boolean editApplyForLeave(String startDate, String endDate, String leaveHours, String leaveReason, String attachmentFileName, String leaveComment, String clickCheckBlanceButton, WebDriver driver) throws InterruptedException, IOException {
        boolean isDone=false;
        int errorCounter=0;
        //displayLeavePage(driver) first

        //////////////////////
        if (SystemLibrary.waitChild("//form[@id='leave-apply']", 5,1, driver)==null) {
            PageObj_Leave.button_AddLeave(driver).click();
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            Thread.sleep(2000);
            GeneralBasic.waitSpinnerDisappear(300, driver);
            logMessage("Add Apply Leave button is clicked.");


        }
        else{
            logMessage("Apply for Leave form is shown.");
        }
        //////
        logScreenshot(driver);

        GeneralBasic.waitSpinnerDisappear(120, driver);
        if (startDate!=null){

            if (startDate.contains(";")){
                startDate=SystemLibrary.getExpectedDate(startDate, null);
            }

            //PageObj_ApplyForLeave.textBox_StartDate(driver).clear();
            PageObj_ApplyForLeave.textBox_StartDate(driver).click();
            Thread.sleep(3000);
            PageObj_ApplyForLeave.textBox_StartDate(driver).sendKeys(startDate);
            logMessage("Start Date: "+startDate+" is input.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
        }


        if (endDate!=null){

            if (endDate.contains(";")){
                endDate=SystemLibrary.getExpectedDate(endDate, null);
            }

            //PageObj_ApplyForLeave.textBox_EndDate(driver).clear();
            //PageObj_ApplyForLeave.textBox_EndDate(driver).click();
            Actions action=new Actions(driver);
            action.doubleClick(PageObj_ApplyForLeave.textBox_EndDate(driver)).build().perform();

            Thread.sleep(3000);
            PageObj_ApplyForLeave.textBox_EndDate(driver).sendKeys(endDate);
            logMessage("End Date: "+endDate+" is input.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(180, driver);
        }

        if (leaveReason!=null){
            if (!PageObj_ApplyForLeave.selectLeaveTypeInApplyLeaveScreen(leaveReason, driver)) errorCounter++;
            Thread.sleep(5000);
        }

        if (leaveHours!=null){
            Thread.sleep(5000);
            clearTextBox(PageObj_ApplyForLeave.textBox_LeaveHours(driver));
            PageObj_ApplyForLeave.textBox_LeaveHours(driver).sendKeys(leaveHours);
            PageObj_ApplyForLeave.textBox_LeaveHours(driver).sendKeys(Keys.TAB);
            logMessage("Leave Hour: "+leaveHours+" is input.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
        }

        if (attachmentFileName!=null){
            PageObj_ApplyForLeave.submitAttachmentInApplyLeaveForm(attachmentFileName, driver);
        }

        if (leaveComment!=null){
            clearTextBox(PageObj_ApplyForLeave.textbox_comment(driver));
            PageObj_ApplyForLeave.textbox_comment(driver).sendKeys(leaveComment);
            logMessage("Leaave Comment is input.");
        }


        //Wait for "Check Balance" button
        if (SystemLibrary.waitChild("//button[@type='button'][contains(text(),'Check balance')]", 5, 1, driver )!=null){
            if (clickCheckBlanceButton!=null){
                if (clickCheckBlanceButton.equals("2")){
                    logMessage("Check Balance button is NOT clicked.");
                }else{
                    button_CheckBalance(driver).click();
                    logMessage("Check Balance button is clicked.");
                }
            }else{
                button_CheckBalance(driver).click();
                logMessage("Check Balance button is clicked.");
            }


            if (SystemLibrary.waitChild("//span[@class='footnote'][contains(text(),'Leave balance after end date')]", 60, 1, driver)!=null){
                logMessage("Leave balance is shown.");
            }
            else{
                logError("Leave balacne is NOT shown.");
            }
        }else{
            logDebug("Check Balance Button is NOT shown.");
        }

        isDone=true;
        logMessage("Screenshot before click apply button.");
        logScreenshot(driver);

        if (errorCounter>0) isDone=false;
        return isDone;
    }

    public static WebElement form_ApplyForLeave(WebDriver driver){
        return driver.findElement(By.xpath("//form[@id='leave-apply']"));
    }

    public static WebElement button_CheckBalance(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='button'][contains(text(),'Check balance')]"));
    }


    public static WebElement frame_BalanceForecase(WebDriver driver){
        return  SystemLibrary.waitChild("//div[@class='balance-overview']", 2, 1, driver);
    }

    public static WebElement button_Close(WebDriver driver){
        return driver.findElement(By.xpath("//i[@class='icon-close']"));
    }

    public static String getLeaveBalanceFromApplyLeaveFormInHours(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Apply Leave Form first
        //Jim rework on 26/06/2018
        String result="";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild("//div[@class='balance-overview']//div//div[@class='row']", 5, 1, driver);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();

            result=result.replace("Leave balance after end date\n", "");
            int i=result.indexOf(" hrs");
            result=result.substring(0, i-1);
            System.out.println(result);
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" hour(s).");
        return result;
    }

    public static String getLeaveBalanceFromApplyLeaveFormInDays(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Apply Leave Form first
        //Jim rework on 26/06/2018
        String result="";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild("//div[@class='balance-overview']//div//div[@class='row']", 5, 1, driver);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();

            result=result.replace("Leave balance after end date\n", "");
            int iStart=result.indexOf("(");
            int iEnd=result.indexOf(" days)");
            result=result.substring(iStart+1, iEnd);
            System.out.println(result);
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" day(s).");
        return result;
    }

}
