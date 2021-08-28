package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_EditDataDetailsInWebAPIManage {
    public static WebElement textBox_DatabaseAlias(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='database alias']"));
    }

    public static WebElement textBox_DatabaseName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='WebApiDatabase']"));
    }

    public static WebElement textBox_DatabaseToken(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='database token']"));
    }

    public static WebElement textBox_ServerName(WebDriver driver){
        return driver.findElement(By.xpath("//input[@placeholder='server name']"));
    }

    public static WebElement textBox_DatabaseType(WebDriver driver){
        return driver.findElement(By.xpath("//select[@name='cboDbType']"));
    }

    public static WebElement button_Add(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()='Add']"));
    }


}
