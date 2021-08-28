package PageObject;

import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.IOException;
import java.util.List;

import static Lib.SystemLibrary.logError;

/**
 * Created by j3m on 22/05/2017.
 */
public class PageObj_Dashboard {
    private static WebElement element=null;

    public static WebElement label_Greeting(WebDriver driver){
        return SystemLibrary.waitChild("//div[@class='salvatrix']", 10, 1, driver);
    }

    public static WebElement panel_ApplyForLeave(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"apply-for-leave-widget\"]"));
    }

    public static WebElement wholeDashboardPanel(WebDriver driver){
        return driver.findElement(By.xpath(".//*[@id='page-container']/main"));
    }

    public static WebElement sideNavigationPanel(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"side-nav\"]"));
    }

    public static WebElement memberBanner(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[1]"));
    }

    public static WebElement panel_userFullName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"profile-widget\"]/div/a/div/div/h4"));
    }

    public static WebElement panel_PendingLeaveApprovals(WebDriver driver){
        return SystemLibrary.waitChild("//*[@id=\"pending-leave-approvals-widget\"]", 10, 1, driver);
    }

    public static WebElement panel_PendingProfileApprovals(WebDriver driver){
        return SystemLibrary.waitChild("//*[@id=\"pending-profile-approvals-widget\"]", 10, 1, driver);
    }

    public static WebElement panel_NewStarter(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='new-starters-widget']", 10, 1, driver);
    }

    public static WebElement panel_LeaveBalances(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"leave-balances-widget\"]"));

    }

    public static WebElement pendingLeaveApprovalsPanelLable(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[2]/div[2]/div/a/div/div[3]"));
        return element;
    }

    public static WebElement pendingLeaveApprovalsPanelValue(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[2]/div[2]/div/a/div/div[2]"));
        return element;
    }

    public static WebElement pendingProfileApprovalsPanel(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[2]/div[3]/div/a/div/div[3]"));
        return element;
    }


    public static WebElement pendingProfileApprovalsPanelValue(WebDriver driver){
        element=driver.findElement(By.xpath(".//*[@id='page-container']/main/div/span/div[2]/div[3]/div/a/div/div[2]"));
        return element;
    }


    public static WebElement pendingProfileApprovalsPanelLable(WebDriver driver){
        element=driver.findElement(By.xpath(".//*[@id='page-container']/main/div/span/div[2]/div[3]/div/a/div/div[3]"));
        return element;
    }

    public static WebElement leaveBalancesPanel(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[3]/div[2]/div/div/div[2]/form/div/div/div/div[2]/div"));
        return element;
    }

    public static String getPendingLeaveApprovalsMessage(WebDriver driver){
        return pendingLeaveApprovalsPanelValue(driver).getText()+" "+pendingLeaveApprovalsPanelLable(driver).getText();
    }

    public static String getPendingProfileApprovalsMessage(WebDriver driver){
        return pendingLeaveApprovalsPanelValue(driver).getText()+" "+pendingProfileApprovalsPanelLable(driver).getText();
    }

    public static WebElement panel_NextLeave(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"next-leave-widget\"]"));
    }

    public static WebElement panel_MembersOnLeaveToday(WebDriver driver){
        //return driver.findElement(By.xpath("//*[@id=\"members-on-leave-widget\"]"));
        return SystemLibrary.waitChild("//*[@id=\"members-on-leave-widget\"]", 15, 1, driver);
    }

    public static WebElement panel_ManagementTeams(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='management-team-widget']", 15, 1, driver);
    }

    public static WebElement panel_AuditReports(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='audit-logs-widget']", 15, 1, driver);
    }

    public static WebElement panel_YourManger(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"your-manager-widget\"]"));
    }
    public static WebElement panel_MembershipTeams(WebDriver driver){
        return SystemLibrary.waitChild("//*[@id=\"membership-team-widget\"]", 15, 1, driver);
    }

    public static String getLeaveBalanceFromDashboard(String leaveType,  WebDriver driver) throws InterruptedException {
        //Display Dashboard first
        Thread.sleep(15000);
        String result="";

        //New Xpath
        //String topXpath="//*[@id=\"page-container\"]/main/div/span/div[3]/div[2]/div/div/div[2]/form/div/div/div";
        String topXpath="//*[@id=\"leave-balances-widget\"]/div/div/div[2]/form/div/div";

        List<WebElement> myList=driver.findElements(By.xpath(topXpath));
        int itemCount=myList.size();

        String currentLeaveType="";
        if (itemCount==0){
            result=driver.findElement(By.xpath(topXpath)).getText();
        }
        else
        {
            for (int i=1;i<=itemCount;i++){
                currentLeaveType=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[3]/div[2]/div/div/div[2]/form/div/div/div["+i+"]/div[1]/div[3]/span[1]")).getText();
                if (currentLeaveType.equals(leaveType)){
                    result=driver.findElement(By.xpath("//*[@id=\"page-container\"]/main/div/span/div[3]/div[2]/div/div/div[2]/form/div/div/div["+i+"]/div[3]/div/div/span[1]/span[1]")).getText();
                    break;
                }
            }
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result);
        return result;
    }

    public static WebElement panel_LatestPayAdvice(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='pay-advice--widget']", 15, 1, driver);
    }

    public static WebElement list_ProfileChangeAuditReport(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-item-report-profile-changes-audit-report\"]/div[1]/a/span[1]"));
    }
    public static WebElement list_LeaveApplicationsAuditReport(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-item-report-leave-applications-audit-report\"]/div[1]/a/span[1]"));
    }
    public static WebElement list_MaintenanceAuditReport(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-item-report-maintenance-audit-report\"]/div[1]/a/span[1]"));
    }
    public static WebElement list_ActivationReport(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-item-report-activation-report\"]/div[1]/a/span[1]"));
    }

    public static WebElement panel_memberFullName_viaAdmin(WebDriver driver){
        return driver.findElement(By.xpath(".//*[@id='page-container']/div/main/div/div[1]/div/div/div/div[1]/div[2]/h3"));
    }

    public static WebElement button_DownloadPayAdvice(WebDriver driver){
        //return driver.findElement(By.xpath("//button[@class='button--plain add-button']"));
        return SystemLibrary.waitChild("//button[@class='button--plain add-button']", 10, 1, driver);
    }

    public static WebElement panel_Directory(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='widget-button' and contains(., 'Directory')]"));
    }

    public static String getLeaveBalanceDaysFromDashboardPage(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Dashbaord Page first
        String result="";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild("//div[@id='leave-balances-widget']//div[@class='list-item-row' and contains(., '"+leaveType+"')]//span[@class='value']", 2, 1, driver);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();
            result=result.replace(" days", "");
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" day(s).");
        return result;
    }

    public static String getLeaveBalanceHoursFromDashboardPage(String leaveType, WebDriver driver) throws IOException, InterruptedException {
        //Display Dashbaord Page first
        String result="";
        WebElement lable_LeaveBelanceValule=SystemLibrary.waitChild("//div[@id='leave-balances-widget']//div[@class='list-item-row' and contains(., '"+leaveType+"')]//span[@class='alt-value-unit']", 2, 1, driver);
        if (lable_LeaveBelanceValule!=null){
            result=lable_LeaveBelanceValule.getText();
            result=result.replace(" hours", "");
        }
        else{
            logError("The leave type '"+leaveType+"' is NOT found.");
        }

        SystemLibrary.logMessage("The balance of "+leaveType+" is "+result+" hour(s).");
        return result;
    }
}
