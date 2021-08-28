package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by j3m on 17/05/2017.
 */
public class PageObj_ESSHome {
    private static WebElement element=null;

    public static WebElement userNameTextBox(WebDriver driver){
        element=driver.findElement(By.id("signInName"));
        return element;
    }

    public static WebElement passwordTextBox(WebDriver driver){
        element=driver.findElement(By.xpath("//input[@id='password']"));
        return element;
    }

    public static WebElement signInButton(WebDriver driver){
        element=driver.findElement(By.xpath("//button[@id='continue']"));
        return element;
    }

    public static WebElement link_ForgotYourPassword(WebDriver driver){
        return driver.findElement(By.xpath("//a[text()='Forgot your password?']"));
    }

}
