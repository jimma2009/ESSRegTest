package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_ESSActivation {
    public static WebElement button_SendVerificationCode(WebDriver driver){
        return driver.findElement(By.xpath("//button[contains(text(), 'Send verification code')]"));
    }

    public static WebElement button_OK_VerificationCodeEmailSent(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()='Ok']"));
    }

    public static WebElement textbox_VerificationCode(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"passcode\"]"));
    }

    public static WebElement textbox_Password(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"password\"]"));
    }

    public static WebElement button_Continue(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and text()='Continue']"));
    }
}
