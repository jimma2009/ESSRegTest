package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//v1c Feedback - Multiple functions can be made into one function by a loop li[i] to iterate through the side menu tabs.
public class PageObj_SideNavigationBar {
    private static WebElement element = null;

    public static WebElement sideMenu_personalInformation(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Personal Information')]"));
        return element;
    }

    public static WebElement sideMenu_contactDetails(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Contact Details')]"));
        return element;
    }

    public static WebElement sideMenu_accountSettings(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[text()='Account Settings' and @class='sni-label undefined']"));
        return element;
    }

    public static WebElement sideMenu_teamsAndRoles(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Teams & Roles')]"));
        return element;
    }

    public static WebElement sideMenu_employment(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Employment')]"));
        return element;
    }

    public static WebElement sideMenu_bankAccounts(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Bank Accounts')]"));
        return element;
    }

    public static WebElement sideMenu_superannuation(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Superannuation')]"));
        return element;
    }

    public static WebElement sideMenu_leave(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Leave')]"));
        return element;
    }

    public static WebElement sideMenu_payAdvicesAndSummaries(WebDriver driver) {
        element = driver.findElement(By.xpath("//span[@class='sni-label undefined'][contains(text(),'Pay Advices & Summaries')]"));
        return element;
    }

    public static WebElement getSideNavigationItem(String itemName, WebDriver driver) throws InterruptedException {
        //Display Personal Information page first
        WebElement element=SystemLibrary.waitChild("//*[contains(text(),'"+itemName+"') and ./@class='sni-label undefined']", 10, 1, driver);
        if (element!=null){
            SystemLibrary.logMessage("Item: '"+itemName+"' is found.");
        }
        else{
            SystemLibrary.logError("Item: '"+itemName+"' is NOT found.");
        }
        return element;
    }

    public static boolean clickItemInSideNavigationBar(String itemName, WebDriver driver) throws InterruptedException {
        boolean isClicked=false;
        WebElement element=getSideNavigationItem(itemName, driver);
        if (element!=null){
            SystemLibrary.logDebug("The xpath of the item is "+SystemLibrary.getElementXPath(driver, element));
            element.click();
            SystemLibrary.logMessage("Item: '"+itemName+"' is clicked in Side Navigation Bar.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(30, driver);
            isClicked=true;
        }
        return isClicked;
    }

}
