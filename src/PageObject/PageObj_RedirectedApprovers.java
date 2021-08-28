package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_RedirectedApprovers {
    public static WebElement label_RedirectedApprovers(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-title\"]/h3[text()='Redirected Approvers']"));
    }

    public static WebElement form_RedirectedApproversList(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='list-container']//form"));
    }
}
