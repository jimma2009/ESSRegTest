package PageObject;

import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_ContactDetail {
    public static WebElement button_EditWorkContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-work-contact']//h5[@class='gc-column-header']//span[@class='icon-right']"));
    }

    public static WebElement table_ContactDetailsAll(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='master-detail-content']"));
    }

    public static boolean displayEditPersonalContactForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        button_EditWorkContact(driver).click();
        if (SystemLibrary.waitChild("//h4[@class='mc-header-heading' and text()='Edit Work Contact']", 60, 1, driver)!=null){
            SystemLibrary.logMessage("Edit Personal Contact form is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("Edit Personal Contact form is NOT shown.");
        }
        return isShown;
    }

    public static WebElement button_PendingApproval_ContactDetail_WorkContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='display-area-workContact display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]"));
    }

    public static WebElement button_PendingApproval_ContactDetail_PersonalContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='display-area-personalContact display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]"));
    }

    public static WebElement button_Approval_ContactDetail_WorkContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='approve-status-action-controls approval-mode']//div[@class='approve-button-container']//div[@class='approval-buttons']//button[contains(@class,'approve-button button--success')]"));
    }

    public static WebElement button_Decline_ContactDetail_WorkContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='approve-status-action-controls approval-mode']//div[@class='approve-button-container']//div[@class='approval-buttons']//button[contains(@class,'decline-button button--danger')]"));
    }

    public static WebElement panel_PersonalContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-personal-contact']"));
    }

    public static WebElement panel_WorkContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-work-contact']"));
    }

    public static WebElement panel_Address(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-address']"));
    }

    public static WebElement panel_EmergencyContact(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-emergency-contact']"));
    }
}
