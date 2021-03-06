package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static Lib.SystemLibrary.*;

public class PageObj_TeamsNRoles {

    public static WebElement ellipsis_TeamsRolesMoreOption(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='teams-roles-more-options']/button"));
    }

    public static WebElement ellipsis_Menue_RedirectApprovals(WebDriver driver){
        return driver.findElement(By.xpath("//a[text()='Redirect approvals']"));
    }

    public static WebElement ellipsis_Menue_AddAdministratorRole(WebDriver driver){
        return SystemLibrary.waitChild("//a[text()='Add Administrator role']", 10, 1, driver);
    }

    public static WebElement ellipsis_Menue_RemoveAdministratorRole(WebDriver driver){
        return SystemLibrary.waitChild("//a[text()='Remove Administrator role']", 10, 1, driver);
    }


    public static boolean displayApprovalProcess(WebDriver driver) throws InterruptedException, IOException {
        boolean isShown=false;
        int errorCounter=0;
        //Display Teams & Roles page first
        ellipsis_TeamsRolesMoreOption(driver).click();
        Thread.sleep(3000);
        logMessage("Team & Roles ellipsis option is clicked.");

        driver.findElement(By.xpath("//a[@href='javascript:;' and contains(., 'Approval process')]")).click();
        Thread.sleep(10000);
        GeneralBasic.waitSpinnerDisappear(180, driver);
        GeneralBasic.waitSpinnerDisappear(60, driver);

        if (SystemLibrary.waitChild("//a[@href='javascript:;' and contains(., 'Approval process')]", 30, 1,driver)!=null){
            logMessage("Approval Process Form is shown.");
            SystemLibrary.logScreenshot(driver);
        }
		else{
            SystemLibrary.logError("Approval Porcess Form is NOT shown.");
            errorCounter++;
        }

        if (errorCounter==0) isShown=true;
        return  isShown;
    }

    public static WebElement form_ApprovalProcess(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='approval-process-modal']"));
    }

    public static WebElement button_CloseApprovalProcess(WebDriver driver){
        return driver.findElement(By.xpath("//button[@id='button-modal-close']"));
    }

    public static boolean validateElipsisMenuInTeamsAndRolesPage_OLD(String firstName, String middleName, String lastName, String preferredName, String expectedMenuItem, String testSerialNo, WebDriver driver) throws IOException, InterruptedException {
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            ellipsis_TeamsRolesMoreOption(driver).click();
            Thread.sleep(3000);
            logMessage("Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            SystemLibrary.logScreenshot(driver);

            String[] expectedSubItem=SystemLibrary.splitString(expectedMenuItem, ";");
            int totalCount=expectedSubItem.length;

            for (int i=0;i<totalCount;i++){
                if (SystemLibrary.waitChild("//ul[@id='teams-roles-sub-menu']//a[text()='"+expectedSubItem[i]+"']", 5, 1, driver)!=null) {
                    logMessage("Menu '"+expectedSubItem[i]+"' is found.");
                }
                else {
                    logError("Menu '"+expectedSubItem[i]+"' is NOT found.");
                    errorCounter++;
                }
            }
        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static WebElement textBox_StartDate_RedirectApproval(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='startDate']"));
    }

    public static WebElement textBox_EndDate_RedirectApproval(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='endDate']"));
    }

    public static boolean validateElipsisMenuInTeamsAndRolesPage(String firstName, String middleName, String lastName, String preferredName, String storeFileName, String isUpdated, String isCompare, String testSerialNo, String emailDomainName, WebDriver driver) throws Exception {
        boolean isPassed = false;
        int errorCounter = 0;

        if (GeneralBasic.displayPage_ViaSideNavigationBar(firstName, firstName, middleName, lastName, preferredName, "Teams & Roles", testSerialNo, driver)) {

            ellipsis_TeamsRolesMoreOption(driver).click();
            Thread.sleep(3000);
            logMessage("Ellipsis butotn is clicked in Teams & Roles page.");
            logMessage("Screenshot after clicking Ellipsis menu.");
            SystemLibrary.logScreenshot(driver);

            WebElement menuAll=SystemLibrary.waitChild("//ul[@id='teams-roles-sub-menu']", 10, 1, driver);
            if (menuAll!=null){
                if (!SystemLibrary.validateTextValueInWebElementInUse(menuAll, storeFileName, isUpdated, isCompare, null, testSerialNo, emailDomainName,  driver)) errorCounter++;
            }else{
                errorCounter++;
                logError("Ellipsis menu is NOT shown.");
            }

        }

        if (errorCounter == 0) isPassed = true;
        return isPassed;
    }

    public static WebElement getEllipsisButtonInTeamRolesPageViaAdmin(String teamName, WebDriver driver){
        String xpath_EllipsisButton="//div[@class='master-detail-content']//div[contains(@class, 'list-item-row') and contains(., '"+teamName+"')]//button";
        logDebug(xpath_EllipsisButton);
        return SystemLibrary.waitChild("//div[@class='master-detail-content']//div[contains(@class, 'list-item-row') and contains(., '"+teamName+"')]//button", 10, 1, driver);
    }

    public static WebElement select_Role_Dropdown(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id='role']"));
        //  return driver.findElement(By.xpath("//*[@id='role']"));
    }

    public static WebElement role_Dropdown(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id='role-select-list']"));
        //     return driver.findElement(By.xpath("//*[@id='role-select-list']"))
    }

}
