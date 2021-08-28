package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_PayAdviceAndPaymentSummary {
    public static WebElement panel_PayAdvicesAndPaymentSummaries(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='pay-advices-summaries']"));
    }
}
