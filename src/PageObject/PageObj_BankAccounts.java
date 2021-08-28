package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_BankAccounts {
    public static WebElement frame_BankAccounts(WebDriver driver) {
        return driver.findElement(By.xpath("//div[@class='master-detail-content']"));
    }

    public static WebElement button_AddBankAccount(WebDriver driver){
        return driver.findElement(By.xpath("//button[@class='button--financial button--round add-button']"));
    }

    public static WebElement textBox_BSBNumber(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"bsb\"]"));
    }

    public static WebElement textBox_BankName(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"BankName\"]"));
    }

    public static WebElement textBox_AccountNumber(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"AccountNumber\"]"));
    }

    public static WebElement textBox_AccountName(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"AccountName\"]"));
    }

    public static WebElement textBox_PayAmount(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"PayAmount\"]"));
    }

    public static WebElement checkBox_BalanceOfPay(WebDriver driver) {
        return driver.findElement(By.xpath("//input[@id='IsBalanceOfPayIndicator']"));
    }

    public static WebElement button_Save_AddBankAccount(WebDriver driver) {
        return driver.findElement(By.xpath("//button[@type='submit' and text()='Save']"));
    }

    public static WebElement button_Close_AddBankAccount(WebDriver driver) {
        return driver.findElement(By.xpath("//*[@id=\"button-modal-close\"]"));
    }

    public static WebElement form_AddBankAccount(WebDriver driver){
        return driver.findElement(By.xpath("//form[contains(., 'Add bank account')]"));
    }

    public static WebElement button_ChangeBankAccountOrder(WebDriver driver){
        return driver.findElement(By.xpath("//button/i[@class='icon-reorder']"));
    }




}
