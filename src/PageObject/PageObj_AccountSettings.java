package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class PageObj_AccountSettings {
    public static WebElement button_Acivate(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()=\"Activate\"]"));
    }

    public static WebElement button_ResendActivationLink(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()=\"Resend Activation Link\"]"));
    }

    public static WebElement panel_AccountInformation(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"ea-account-information\"]"));
    }

    public static WebElement lable_Username(WebDriver driver){
        return driver.findElement(By.xpath("//dd[contains(text(),'@')]"));
    }

    public static String getUsernameFromAccountSettingsPage(WebDriver driver){
        //User's Account Settings page is shown first.
        return lable_Username(driver).getText();
    }

    public static WebElement button_DisableLogin(WebDriver driver){
        return driver.findElement(By.xpath("//button[contains(@class,'button button--danger')][text()='Disable Login']"));
    }

    public static WebElement button_EnableLogin(WebDriver driver){
        return driver.findElement(By.xpath("//button[contains(@class,'button button--primary')][text()='Enable Login']"));
    }

}
