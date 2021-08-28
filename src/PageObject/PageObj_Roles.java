package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import static Lib.SystemLibrary.*;
import static Lib.SystemLibrary.logMessage;
import static Lib.SystemLibrary.logScreenshot;

public class PageObj_Roles {
    public static WebElement lable_Roles(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-title\"]/h3"));
    }

    public static WebElement rows_ADMINISTRATOR_ROLES(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-section-role-admin\"]"));
    }

    public static WebElement rows_MANAGER_ROLES(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-section-role-manager\"]"));
    }

    public static WebElement rows_MEMBER_ROLES(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-section-role-member\"]"));
    }

    public static WebElement lable_Roles_SubName(WebDriver driver){
        return driver.findElement((By.xpath("//*[@id=\"role-details\"]/h3")));
    }

    public static WebElement panel_PermissionAll(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[4]"));
    }

    public static boolean displayRolesPermissionPage(String roleName, WebDriver driver) throws Exception {
        //Display Roles Page First
        boolean isShown=false;
        int errorCounter=0;
        driver.findElement(By.linkText(roleName)).click();
        logScreenshot(driver);
        logMessage("Role "+roleName+" is clicked.");
        GeneralBasic.waitSpinnerDisappear(60, driver);

        if (lable_Roles_SubName(driver).getText().equals(roleName)){
            logMessage("Role: "+roleName+"'s Permission Page is shown.");
        }
        else{
            SystemLibrary.logError("Failed display "+roleName+"'s Permission Page.");
            errorCounter++;
        }

        SystemLibrary.displayElementInView(panel_PermissionAll(driver), driver, -60);
        SystemLibrary.zoomPage("0.8", driver);
        SystemLibrary.logScreenshot(driver);
        SystemLibrary.zoomPage("1", driver);
        logMessage("Log screenshot after zoom back.");
        SystemLibrary.logScreenshot(driver);


        if (errorCounter==0) isShown=true;
        return isShown;
    }

    public static WebElement button_Edit(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-controls\"]/div/div/button"));
    }

    public static WebElement button_Cancel(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-controls\"]/div/div/button[1]"));
    }

    public static WebElement button_Save(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-controls\"]/div/div/button[2]"));
    }


    public static WebElement getPermissionPanel(String itemName, WebDriver driver) throws InterruptedException {
        WebElement outputPanel=null;
        List<WebElement> panelItems=driver.findElements(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[4]/div"));

        int totalPanelCount=panelItems.size();
        System.out.println("There are total "+totalPanelCount+" panels in Permissions page.");

        WebElement currentPanel=null;
        String currentPanelName=null;

        for(int i=1;i<=totalPanelCount;i++){
            currentPanel=driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[4]/div["+i+"]"));
            currentPanelName=driver.findElement(By.xpath(SystemLibrary.getElementXPath(driver, currentPanel)+"/div[1]/div/div[1]")).getText();
            if (currentPanelName.equals(itemName)){
                logMessage("Panel '"+itemName+"' is found.");
                outputPanel=currentPanel;
                break;
            }
        }
        if (outputPanel!=null) {
            String outputPanelXpath=SystemLibrary.getElementXPath(driver, outputPanel);
            SystemLibrary.logDebug("The Panel Xpath is '"+outputPanelXpath+"'.");
        }

        return outputPanel;
    }


    public static WebElement getPermissionSubPanel(String itemName, WebDriver driver) throws InterruptedException{
        WebElement outputElement=null;
        String subPanelXpath=null;
        String[] itemLists=SystemLibrary.splitString(itemName, ";");
        int itemCount=itemLists.length;


        if (itemCount==1){
            subPanelXpath=SystemLibrary.getElementXPath(driver, getPermissionPanel(itemName, driver))+"/div[1]/div";
            outputElement=driver.findElement(By.xpath(subPanelXpath));
        }
        else{
            String panelName=itemLists[0];
            String currentPanelXpath=SystemLibrary.getElementXPath(driver, getPermissionPanel(itemLists[0], driver));
            String subPanelListsXpath=currentPanelXpath+"/div[2]/div";
            List<WebElement> subPanels=driver.findElements(By.xpath(subPanelListsXpath));

            int totalSubPanelCount=subPanels.size();

            for (int i=0;i<=totalSubPanelCount;i++){
                outputElement=subPanels.get(i);
                //SystemLibrary.logMessage("currentElement.getText()='"+outputElement.getText()+"'");

                if (outputElement.getText().equals(itemLists[1])){
                    logMessage("Sub Panel '"+itemName+"; is found.");
                    subPanelXpath=SystemLibrary.getElementXPath(driver, outputElement);
                    break;
                }
            }

        }

        //SystemLibrary.logDebug("The sub pane '"+itemName+"' 's xpath is '"+subPanelXpath+"'.");
        //System.out.println("the length of string is "+subPanelXpath.length());
        return outputElement;

    }

    public static WebElement getPermissionButton(String itemName, WebDriver driver) throws InterruptedException{
        String subPanelXpath=SystemLibrary.getElementXPath(driver, getPermissionSubPanel(itemName, driver));
        String permissionButtonXpath="";
        if (subPanelXpath.length()<70){
            permissionButtonXpath=subPanelXpath+"/div[2]/div[2]/div/i";
        }
        else{
            permissionButtonXpath=subPanelXpath+"/div[2]/div/div/i";
        }

        System.out.println("Permission button xpath is '"+permissionButtonXpath+"'.");
        return driver.findElement(By.xpath(permissionButtonXpath));
    }

    public static String getPermissionStatus(String itemName, WebDriver driver) throws InterruptedException {
        String permissionStatus=getPermissionButton(itemName, driver).getAttribute("class");
        logMessage("The permission status of '"+itemName+"' is "+permissionStatus);
        return permissionStatus;
    }

    public static WebElement getPermissionButtonClickable(String itemName, WebDriver driver) throws InterruptedException{
        //itemName is PanelName;ItemName
        //Button must be clickable first.

        WebElement outputElement=null;
        String[] itemLists=SystemLibrary.splitString(itemName, ";");
        if (itemLists.length==1) {
            String panelButtonXPath = SystemLibrary.getElementXPath(driver, getPermissionPanel(itemName, driver)) + "/div[1]/div/div[1]";
            outputElement=driver.findElement(By.xpath(panelButtonXPath));
        }
        else {
            String panelXpath=SystemLibrary.getElementXPath(driver, getPermissionPanel(itemLists[0], driver));
            String buttonXpath=panelXpath+"/div[2]/div[1]/div[2]/div/div/div/button/i";
            outputElement=driver.findElement(By.xpath(buttonXpath));
        }

        return outputElement;
    }

    public static WebElement getPermissionButtonEditable(String itemName, WebDriver driver) throws Exception {

        WebElement element=null;
        String permissionButtonXpath=SystemLibrary.getElementXPath(driver, getPermissionButton(itemName, driver));
        SystemLibrary.logDebug("The Xpath of Permission button is '"+permissionButtonXpath+"'.");

        SystemLibrary.scrollToTop(driver);
        //SystemLibrary.displayElementInView(button_Edit(driver), driver, -10);
        button_Edit(driver).click();
        logMessage("The Edit button on top is clicked.");
        Thread.sleep(4000);

        SystemLibrary.displayElementInView(PageObj_Roles.panel_PermissionAll(driver), driver, -60);
        String permissionButtonXpathEditable= permissionButtonXpath.replace("/div[1]/i[1]", "/div[1]/div[1]/button[1]/i[1]");
        SystemLibrary.logDebug("The Xpath of Permission Button Editable is '"+permissionButtonXpathEditable+"'.");
        element= driver.findElement(By.xpath(permissionButtonXpathEditable));
        return element;
    }

    public static WebElement table_Roles(WebDriver driver) {
        return driver.findElement(By.xpath("//div[@class='list-container']//form"));
    }



    public static WebElement getPermissionButtonNew(String itemName, WebDriver driver) throws InterruptedException{
        ///////////////////////////
        WebElement permissionButton=null;
        String[] itemList=SystemLibrary.splitString(itemName, ";");
        String xpath_PermissionButton="";
        if (itemList.length==1){
            xpath_PermissionButton="//div[@class='rp-header-title rp-row' and contains(., '"+itemName+"')]//div[@class='view-mode']/i";
        }
        else if (itemList.length==2){
            xpath_PermissionButton="//div[@class='gc-grid-cell rp highlight narrow no-padding' and contains(., '"+itemList[0]+"')]//div[@class='rp-row' and contains(., '"+itemList[1]+"')]//div[@class='view-mode']/i";
        }
        else if (itemList.length==3){
            xpath_PermissionButton="//div[@class='gc-grid-cell rp highlight narrow no-padding' and contains(., '"+itemList[0]+"')]//div[@class='rp-row sub' and contains(., '"+itemList[2]+"')]//div[@class='view-mode']/i";
        }
        permissionButton=SystemLibrary.waitChild(xpath_PermissionButton, 30, 1, driver);

        return permissionButton;
    }

    public static WebElement getPermissionButtonEditableNew(String itemName, WebDriver driver) throws Exception {
        SystemLibrary.scrollToTop(driver);
        //SystemLibrary.displayElementInView(button_Edit(driver), driver, -10);
        button_Edit(driver).click();
        logMessage("The Edit button on top is clicked.");
        Thread.sleep(4000);
        SystemLibrary.logScreenshot(driver);

       /* SystemLibrary.displayElementInView(PageObj_Roles.panel_PermissionAll(driver), driver, -60);
        logMessage("Display All penel in view.");
        logScreenshot(driver);
*/

        WebElement permissionButton=null;
        String[] itemList=SystemLibrary.splitString(itemName, ";");
        String xpath_PermissionButton="";
        if (itemList.length==1){
            xpath_PermissionButton="//div[@class='rp-header-title rp-row' and contains(., '"+itemName+"')]//div[@class='edit-mode']//button";
        }
        else if (itemList.length==2){
            xpath_PermissionButton="//div[@class='gc-grid-cell rp highlight narrow no-padding' and contains(., '"+itemList[0]+"')]//div[@class='rp-row' and contains(., '"+itemList[1]+"')]//div[@class='edit-mode']//button";
        }
        else if (itemList.length==3){
            xpath_PermissionButton="//div[@class='gc-grid-cell rp highlight narrow no-padding' and contains(., '"+itemList[0]+"')]//div[@class='rp-row sub' and contains(., '"+itemList[2]+"')]//div[@class='edit-mode']//button";
        }
        permissionButton=SystemLibrary.waitChild(xpath_PermissionButton, 30, 1, driver);

        return permissionButton;
    }

    public static WebElement getEllipsisButtonAdminInRolePageViaAdmin(String Admin, WebDriver driver){
        String xpath_EllipsisButton="//div[@id='list-section-role-admin']//div[contains(@class, 'list-item-row') and contains(., '"+Admin+"')]//button";
        logDebug(xpath_EllipsisButton);
        return SystemLibrary.waitChild("//div[@id='list-section-role-admin']//div[contains(@class, 'list-item-row') and contains(., '"+Admin+"')]//button", 10, 1, driver);
    }

    public static WebElement getEllipsisButtonMngrInRolePageViaAdmin(String Mngr, WebDriver driver){
        String xpath_EllipsisButton="//div[@id='list-section-role-manager']//div[contains(@class, 'list-item-row') and contains(., '"+Mngr+"')]//button";
        logDebug(xpath_EllipsisButton);
        return SystemLibrary.waitChild("//div[@id='list-section-role-manager']//div[contains(@class, 'list-item-row') and contains(., '"+Mngr+"')]//button", 10, 1, driver);
    }

}
