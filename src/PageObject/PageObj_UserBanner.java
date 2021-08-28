package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_UserBanner {

    public static WebElement header_UserFullName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[1]/div/div/div/div[1]/div[2]/h3"));
    }

    public static WebElement lable_WorkEmail(WebDriver driver){
        return driver.findElement(By.xpath("//a[@class='hyperlink' and contains(text(), '@')]"));
    }

    public static WebElement userBannerFrame(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='bc-container']"));
    }
}
