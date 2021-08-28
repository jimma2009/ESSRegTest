package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static Lib.SystemLibrary.logMessage;

public class PageObj_EditUserInWebAPIManage {

    public static WebElement textBox_Username(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Login name']"));
    }

    public static WebElement textBox_FirstName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='First name']"));
    }

    public static WebElement textBox_LastName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Last name']"));
    }

    public static WebElement textBox_Phone(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Phone']"));
    }

    public static WebElement textBox_Password(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Password']"));
    }

    ////////////////////
    public static WebElement textBox_ConfirmPassword(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Confirm password']"));
    }

    public static WebElement textBox_EmailAddress(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Email address']"));
    }

    public static WebElement select_Type(WebDriver driver){
        return driver.findElement(By.xpath("//select[@name='cboUserType']"));
    }

    public static WebElement textBox_Description(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Description']"));
    }

    public static WebElement checkbox_Enable(WebDriver driver){
        return driver.findElement(By.xpath("//input[@name='bEnabled']"));
    }

    public static WebElement textBox_Filter(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='form-group']//input[@type='text']"));
    }

    public static WebElement button_Add(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()='Add']"));
    }

    public static void click_ButtonCustomer_Next(WebDriver driver) throws InterruptedException {
        driver.findElement(By.xpath("//button[@class='btn btn-primary ng-binding']")).click();
        Thread.sleep(6000);
    }

    public static WebElement textBox_CustomerTotusID(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Totus client ID']"));
    }

    public static WebElement textBox_CustomerName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Customer name']"));
    }

    public static WebElement textBox_CustomerPhone(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Phone']"));
    }

    public static WebElement textBox_CustomerEmail(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Email address']"));
    }

    public static WebElement textBox_CustomerUsername(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Username']"));
    }

    public static WebElement textBox_CustomerPassword(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Password']"));
    }

    public static WebElement textBox_CustomerDescription(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Description']"));
    }

    public static WebElement checkbox_CustomerEnable(WebDriver driver){
        return driver.findElement(By.xpath("//input[@name='bEnabled']"));
    }

    public static WebElement textBox_CustomerDBAlias(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Database alias']"));
    }

    public static WebElement textBox_CustomerDBName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='WebApiDatabase']"));
    }

    public static WebElement textBox_CustomerAPIKey(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Database token']"));
    }

    public static WebElement textBox_CustomerServer(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='Server name']"));
    }

    public static void click_ButtonCustomer_SubmitRequestaaaa(WebDriver driver) throws InterruptedException {
        driver.findElement(By.xpath("//button[@class='btn btn-primary ng-binding'and contains(., 'Submit request')]")).click();
        Thread.sleep(4000);
    }


    public static WebElement click_ButtonCustomer_SubmitRequest(WebDriver driver) {
        return driver.findElement(By.xpath("//button[@class='btn btn-primary ng-binding'and contains(., 'Submit request')]"));
    }

    public static WebElement form_CustomerConfirmation(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='panel panel-default' and contains(., 'Add a new customer - Confirmation')]"));
    }

    public static WebElement click_ButtonCustomer_Done(WebDriver driver) {
        return driver.findElement(By.xpath("//button[@class='btn btn-primary ng-binding'and contains(., 'Done')]"));
    }


}
