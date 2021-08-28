package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_Employment {
    public static WebElement table_EmploymentAll(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='master-detail-content']"));
    }
}
