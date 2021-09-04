package PageObject;

import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static Lib.SystemLibrary.logMessage;
import static Lib.SystemLibrary.waitChild;

public class PageObj_General {

    private static WebElement element=null;

    public static WebElement button_AddAPIKey(WebDriver driver){
        element=driver.findElement(By.xpath("//button[@class='button--primary button--round add-button']"));
        return element;
    }

    public static WebElement form_AddEditAPIKey(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form"));
        return element;
    }
    public static WebElement textbox_Username(WebDriver driver){
        element=driver.findElement(By.id("User"));
        return element;
    }

    public static WebElement textbox_Password(WebDriver driver){
        element=driver.findElement(By.id("Password"));
        return element;
    }

    public static WebElement textbox_Apikey(WebDriver driver){
        element=driver.findElement(By.id("ApiKey"));
        return element;
    }

    public static WebElement textbox_DatabaseAlias(WebDriver driver){
        element=driver.findElement(By.id("DatabaseAlias"));
        return element;
    }

    public static WebElement textbox_TermFromDate(WebDriver driver){
        element=driver.findElement(By.id("TerminationDateBuffer"));
        return element;
    }

    public static WebElement button_AddEdit(WebDriver driver){
        element=driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button"));
        return element;
    }

    public static WebElement label_ErrorMessageReturn(WebDriver driver){
        element=SystemLibrary.waitChild("//*[@id=\"app-error\"]/div", 5, 1, driver);
        return element;
    }

    public static String getReturnErrorMessageFromIntegrationScreen(WebDriver driver) throws InterruptedException {
        if (label_ErrorMessageReturn(driver)!=null) {
            return label_ErrorMessageReturn(driver).getText();
        }
        else{
            SystemLibrary.logMessage("No message return.");
            return "";
        }
    }

    public static WebElement button_SyncAllData(WebDriver driver){
        return driver.findElement(By.xpath("//li/a[contains(text(),'Sync All Data')]"));
    }

    public static WebElement button_SyncChanges(WebDriver driver){
        return driver.findElement(By.xpath("//a[contains(text(),'Sync Changes')]"));
    }

    public static WebElement button_SyncOK(WebDriver driver){
        //return driver.findElement(By.xpath("id(\"modal-content\")/div[1]/form[1]/div[3]/div[1]/button[1]"));
        return driver.findElement(By.xpath("//button[contains(@class,'button--primary')][contains(text(),'Ok')]"));
    }

    public static WebElement button_RefreshSync(WebDriver driver){
        element=driver.findElement(By.xpath("//i[@class='icon-refresh']"));
        return element;
    }

    public static boolean clickIntegrationAPIKeysButton(String apiKey, WebDriver driver) throws InterruptedException {
        boolean isClicked=false;

        //Get current Web API Key xpath
        WebElement element=SystemLibrary.waitChild("//span[text()='"+apiKey+"']", 5, 1, driver);
        if (element!=null){
            String xPath_APIKey=SystemLibrary.getElementXPath(driver, element);
            logMessage("The current Xpath of API Key '"+apiKey+"' is "+xPath_APIKey);

            String xPath_APIKeyButton=xPath_APIKey.replace("/div[1]/div[3]/span[1]", "/div[3]/div/div/button");
            driver.findElement(By.xpath(xPath_APIKeyButton)).click();
            Thread.sleep(2000);
            logMessage("API Key '"+apiKey+"' button is clicked.");
            isClicked=true;
        }
        else{
            SystemLibrary.logError("API Key: '"+apiKey+"' is NOT found.");
        }

        return isClicked;
    }

    public static String getTopMessageFromWebAPILog(WebDriver driver){
        //Integration Web API page first
        String messageText="";
        WebElement element=SystemLibrary.waitChild("//div[@class='list end-list-options-container-spacer']//div[@class='list-container']/form/div[1]", 15, 1,driver);
        if (element!=null) messageText=element.getText();
        return messageText;
    }

    public static int getWebAPIKeysTotalCount(WebDriver driver) throws InterruptedException {
        //////////////// Jim updated on 28/07/2021 /////////////////////
        List<WebElement> apiKeyLists = driver.findElements(By.xpath("//div[@class='page-row' and contains(., 'Web API Keys')]//div[@class='list-container']/form/div"));
        int listCount = apiKeyLists.size();

        //////


        /////////////////// OLD Code below ////////////////////
        //GeneralBasic.displaySettings_Integration(driver); first
		//Updated on 06/07/2021
	    //List<WebElement> apiKeyLists = driver.findElements(By.xpath("//*[@id=\"page-container\"]/main/div/div[2]/div/div/div/div/form/div"));
        //List<WebElement> apiKeyLists = driver.findElements(By.xpath("//*[@id=\"page-container\"]/main/div/div[3]/div/div/div/div/form/div"));
        //int listCount = apiKeyLists.size();

        //////
        logMessage("There are total " + listCount + " API Key(s).");
        return listCount;
    }

    public static WebElement button_ViewMore(WebDriver driver){
        return driver.findElement(By.xpath("//a[@class='link'][text()='View more']"));
    }

    public static int getTotalLogCount(WebDriver driver) throws InterruptedException {
        List<WebElement> logList=driver.findElements(By.xpath("//div[@class='list end-list-options-container-spacer']//div[@class='list-container']/form/div"));
        int logRowCount= logList.size();
        logMessage("There are total "+logRowCount+" rows in Web API log table.");
        return logRowCount;
    }

    public static WebElement button_Sync(WebDriver driver){
        WebElement element=null;
        element=waitChild("//button[@id='button-sync']", 15, 1, driver);
        return element;
    }

    public static WebElement button_YesSyncAll(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Yes, sync all')]"));
    }

    public static WebElement button_YesSyncChanges(WebDriver driver){
        return driver.findElement(By.xpath("//button[@class='button--primary' and text()='Yes, sync changes']"));
    }

    public static WebElement checkbox_SyncFromPayroll(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='SyncPayroll']"));
    }

    public static WebElement checkbox_SyncBalancesForLeaveReport(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='SyncLB']"));
    }

    public static WebElement button_YesSyncLeaveBalance(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Yes, sync leave balances')]"));
    }
}
