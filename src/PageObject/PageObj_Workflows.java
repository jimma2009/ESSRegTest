package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObj_Workflows {
    public static WebElement label_Workflows(WebDriver driver){
        return driver.findElement(By.xpath("//div/li/a/span[text()='Workflows']"));
    }

    public static WebElement label_ProfileChangesWorkflowDefault(WebDriver driver){
        return driver.findElement(By.xpath("//div/a/span[text()='Profile Changes Workflow (Default)']"));
    }

    public static WebElement label_LeaveWorkflowDefault(WebDriver driver){
        return driver.findElement(By.xpath("//a[@class='link heading-container' and contains(., 'Leave Workflow (Default)')]"));
    }

    public static WebElement label_ProfileApprovalsInProgress(WebDriver driver) {
        return driver.findElement(By.xpath("(//div[text()='Approvals in progress'])[1]"));
    }

    public static WebElement value_ProfileApprovalsInProgress(WebDriver driver) {
        return driver.findElement(By.xpath("(//div[@class='lic-content'])[1]"));
    }

    public static WebElement label_LeaveApprovalsInProgress(WebDriver driver){
        return driver.findElement(By.xpath("(//div[text()='Approvals in progress'])[2]"));
    }

    public static WebElement value_LeaveApprovalsInProgress(WebDriver driver) {
        return driver.findElement(By.xpath("(//div[@class='lic-content'])[2]"));
    }

    public static WebElement button_EditWorkflow(WebDriver driver){
        return driver.findElement(By.xpath("//div/button[text()='Edit']"));
    }

    public static WebElement form_WorkflowsList(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='panel-body']"));
    }

    public static WebElement form_SingleWorkflow(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='single-workflow']"));
    }

    public static WebElement pageTitle(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='page-title']/h3"));
    }

    public static WebElement button_SaveWorkflow(WebDriver driver){
        return driver.findElement(By.xpath("//button[contains(@class,'button button--primary') and text()='Save']"));
    }

    public static WebElement button_Continue(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and text()='Continue']"));
    }
}
