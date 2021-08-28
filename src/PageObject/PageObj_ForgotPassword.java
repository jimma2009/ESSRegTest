package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_ForgotPassword {
    public static WebElement textbox_EamilAddress(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='email']"));
    }

    public static WebElement button_Continue(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit'][text()='Continue']"));
    }
}
