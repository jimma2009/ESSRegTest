package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static Lib.SystemLibrary.*;
import static Lib.SystemLibrary.logError;

public class PageObj_Leave {

    private static WebElement element=null;

    public static WebElement table_LeaveAll(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='master-detail-content']"));
    }

    public static WebElement button_AddLeave(WebDriver driver){
        element=driver.findElement(By.id("button-leave-add"));
        return element;
    }

    public static WebElement button_Filter(WebDriver driver){
        element=driver.findElement(By.id("button-leave-filter"));
        return element;
    }

    public static WebElement checkBox_Status_Pending(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"Submitted\"]"));
        return element;
    }

    public static WebElement checkBox_Status_InApproval(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"Inapproval\"]"));
        return element;
    }

    public static WebElement checkBox_Status_Approved(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"Approved\"]"));
        return element;
    }

    public static WebElement checkBox_Status_Declined(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"Declined\"]"));
        return element;
    }

    public static WebElement checkBox_Status_Cancelled(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"Cancelled\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_AnnualLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"1\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_PersonalLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"4\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_LongServiceLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"2\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_TimeInLieu2(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"5\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_TimeInLieu1(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"6\"]"));
        return element;
    }

    public static WebElement checkBox_LeaveType_OtherLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"3\"]"));
        return element;
    }

    public static WebElement form_FilterLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form"));
        return element;
    }

    public static WebElement button_ApplyFilter(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button[2]"));
        return element;
    }

    public static WebElement button_CancelLeave(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button"));
        return element;
    }
    public static String getLeaveBalanceDaysFromLeavePage(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Leave Page first
        //Jim rework on 26/06/2018
        String result="";
        Thread.sleep(3000);
        String xpath_LeaveBalanceValue="//div[@id='leave-balances']//form//div[@class='le' and contains(., '"+leaveType+"')]//span[@class='value']";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild(xpath_LeaveBalanceValue, 2, 1, driver);
        Thread.sleep(3000);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();
            result=result.replace(" days", "");
            Thread.sleep(3000);
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" day(s).");
        return result;
    }

    public static boolean clickMenuButtonInLeaveApplicationTable(int rowNumber, String menuName, WebDriver driver) throws InterruptedException, IOException {
        //Leave Page is shown
        boolean isClicked=false;

        driver.findElement(By.cssSelector("div.list-item-row.la.multiline:nth-child("+rowNumber+") div.li-alt.la.has-actions div.actions-container span:nth-child(1) div.more-options-sub-menu.la-options:nth-child(2) > button.button--plain.add-button")).click();
        Thread.sleep(2000);

        int menuCount=driver.findElements(By.xpath("//*[@id=\"list-item-leave-app-19\"]/div[3]/div/span/div/ul/div")).size();

        for (int i=1;i<=menuCount;i++){
            element=driver.findElement(By.xpath("//*[@id=\"list-item-leave-app-19\"]/div[3]/div/span/div/ul/div["+i+"]/li/a/span"));
            if (menuName.equals(element.getText())){

                SystemLibrary.logMessage("Screenshot before click Menu in Leave Application Table.");
                SystemLibrary.logScreenshot(driver);
                element.click();
                Thread.sleep(5000);
                SystemLibrary.logMessage("Menu: "+menuName+" is clicked.");
                isClicked=true;
                break;
            }
        }

        return isClicked;
    }

    public static boolean applyFilterInLeavePage(String itemsToBeSearch, WebDriver driver) throws InterruptedException, IOException {
        //Display Leave page first
        boolean isApplied=false;
        int errorCounter=0;

        displayFileterLeaveApplications(driver);

        if (itemsToBeSearch.equals("RESET")){
            WebElement button_ResetFilters= SystemLibrary.waitChild("//button[contains(text(),'Reset Filters')]", 5, 1, driver);
            if (button_ResetFilters!=null){
                if (button_ResetFilters.isEnabled()){
                    button_ResetFilters.click();
                    logMessage("Reset Filter button is clicked.");
                }else{
                    logWarning("Reset Filter button is disabled.");
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    Thread.sleep(3000);
                    logMessage("Button close filter is clicked.");
                }
            }
            else{
                logError("Reset Filter button is NOT shown.");
                errorCounter++;
            }
        }
        else{
            //////////////////////////
            String[] itemLists=SystemLibrary.splitString(itemsToBeSearch, ";");
            String currentList="";
            String[] sublist;
            String xpath_Checkbox=null;

            for (int i=0;i<itemLists.length;i++){
                currentList=itemLists[i];
                sublist=SystemLibrary.splitString(currentList, ":");
                xpath_Checkbox="//div[@class='fc-checkbox' and contains(., '"+sublist[0]+"')]/input[@type='checkbox']";

                WebElement checkbox=SystemLibrary.waitChild(xpath_Checkbox, 3, 1, driver);
                if (checkbox!=null){
                    logMessage("Checkbox '"+sublist[0]+"' is found.");
                    SystemLibrary.tickCheckbox(sublist[1], checkbox, driver);
                }
                else{
                    logError("Checkbox '"+sublist[0]+"' is NOT found.");
                    driver.findElement(By.xpath("//button[@id='button-modal-close']")).click();
                    logMessage("Button close is clicked.");
                    errorCounter++;
                }
            }

            SystemLibrary.logMessage("Screenshot before Apply Filter button.");
            SystemLibrary.logScreenshotElement(driver, form_FilterLeave(driver));
            button_ApplyFilter(driver).click();
            Thread.sleep(4000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            SystemLibrary.logMessage("Apply Filter button is clicked.");
            ////////////////////
        }


        if (errorCounter==0) isApplied=true;
        return isApplied;
    }

    public static boolean displayFileterLeaveApplications(WebDriver driver) throws InterruptedException, IOException {
        //Display Leave page first
        boolean isShown=false;
        button_Filter(driver).click();
        Thread.sleep(5000);

        if (SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/form", 30, 1, driver)!=null){
            SystemLibrary.logMessage("Fileter Leave Applicatinos screen is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("File Leave Application screen is NOT shown.");
        }
        logScreenshot(driver);
        return isShown;
    }

    public static String getLeaveBalanceHoursFromLeavePage(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Leave Page first
        //Jim rework on 26/06/2018
        String result="";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild("//div[@id='leave-balances']//form//div[@class='le' and contains(., '"+leaveType+"')]//span[@class='alt-value-unit']", 2, 1, driver);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();
            result=result.replace(" hrs", "");
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" hour(s).");
        return result;
    }



}
