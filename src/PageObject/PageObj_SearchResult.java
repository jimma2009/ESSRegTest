package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_SearchResult {

    //*[contains(text(), 'People') and @class='normal-header']

    public static WebElement header_UserFullName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[1]/div/div/div/div[1]/div[2]/h3"));
    }

}
