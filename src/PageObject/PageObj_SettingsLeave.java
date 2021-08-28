package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PageObj_SettingsLeave {

    public static WebElement label_Leave(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-title\"]/h3"));
    }

    public static WebElement form_AllLeaveSettings(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='list-section-container']"));
    }

    public static WebElement input_Color(WebDriver driver){
        return driver.findElement(By.xpath("//div[contains(@class,'sketch-picker')]/div/div[contains(., 'hex')]//input"));
    }

    public static WebElement button_Color(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='cl-colour-swatch']"));
    }

    public static WebElement checkbox_DisplayBalance(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='balance']"));
    }

    public static WebElement checkbox_AllowInsufficientBalance(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='insufficientBalance']"));
    }

    public static WebElement checkbox_IncludeProRataCalculation(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='proRata']"));
    }

    public static WebElement checkbox_OnlyOnceEntitlementReached(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='lslProRataCalc']"));
    }

    public static WebElement getCheckbox_LeaveReasons(String leaveReasons, WebDriver driver){
        return driver.findElement(By.xpath("//div[contains(., '"+leaveReasons+"')]/input[@type='checkbox']"));
    }

    public static WebElement button_SaveEditLeave(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and text()='Save']"));
    }

    public static WebElement form_EditLeave(WebDriver driver){
        return driver.findElement(By.xpath("//form[contains(., 'Edit')]"));
    }

    public static WebElement button_CloseEditLeave(WebDriver driver){
        return driver.findElement(By.xpath("//i[@class='icon-close']"));
    }

}
