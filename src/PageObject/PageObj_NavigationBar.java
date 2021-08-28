package PageObject;

import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class PageObj_NavigationBar {
    private static WebElement element = null;

    public static WebElement dashboard(WebDriver driver) {
        element = driver.findElement(By.xpath("//a[contains(text(),'Dashboard')]"));
        return element;
    }

    public static WebElement teams(WebDriver driver) {
        element = driver.findElement(By.xpath("//a[contains(text(),'Teams')]"));
        return element;
    }

    public static WebElement approvals(WebDriver driver) {
        element = driver.findElement(By.xpath("//a[contains(text(),'Approvals')]"));
        return element;
    }

    public static WebElement settings(WebDriver driver) {
        element = driver.findElement(By.xpath("//a[contains(text(),'Settings')]"));
        return element;
    }

    public static WebElement textbox_Search(WebDriver driver) {
        element = driver.findElement(By.xpath("//div[@id='right-nav']//form//input[@id='searchField']"));
        return element;
    }

    public static WebElement button_Search(WebDriver driver) {
        element = driver.findElement(By.xpath(".//*[@id='right-nav']/form/div/button"));
        return element;
    }

    public static boolean searchESS(String searchText, String returnMessageExpected, WebDriver driver) throws InterruptedException {
        boolean isFound = true;

        textbox_Search(driver).click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textbox_Search(driver).clear();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textbox_Search(driver).sendKeys(searchText);

        button_Search(driver).click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            element = driver.findElement(By.xpath(".//*[@id='page-container']/main/div/div[2]/div/div/span"));
            isFound = false;
            String returnMessage = element.getText();
            System.out.println("The current return message is " + returnMessage);

            if (returnMessageExpected != null) {
                System.out.println("The return Message Expected is " + returnMessageExpected);

                if (returnMessage.equals(returnMessageExpected)) {
                    SystemLibrary.logMessage("Search Text: \"" + searchText + "\" return message is shown as expected.");
                } else {
                    SystemLibrary.logError("Search Text: \"" + searchText + "\" return message is NOT shown as expected.");
                }
            }
        } catch (Exception e1) {
            try {
                element = driver.findElement(By.partialLinkText(searchText));
                String returnMessage = element.getText();
                System.out.println("The current return message is " + returnMessage);
                isFound = true;
                if (returnMessageExpected != null) {
                    System.out.println("The return Message Expected is " + returnMessageExpected);

                    if (returnMessage.equals(returnMessageExpected)) {
                        SystemLibrary.logMessage("Search Text: \"" + searchText + "\" return message is shown as expected.");
                    } else {
                        SystemLibrary.logError("Search Text: \"" + searchText + "\" return message is NOT shown as expected.");
                    }

                }


            } catch (Exception e) {
                SystemLibrary.logMessage(searchText + " is NOT found.");
                isFound = false;
            }

        }

        if (element != null) SystemLibrary.logScreenshotElement(driver, element);
        return isFound;
    }

    public static WebElement icon_UserName(WebDriver driver) {
        //return driver.findElement(By.xpath("//li[@id='avatar']//a//span[@class='undefined avatar']"));
        //ESS Icon is updated on 17/08/2020 Jim
        return driver.findElement(By.xpath("//li[@id='avatar']//span[@class='avatar']"));
    }

    public static WebElement topSecondNavaigationBar(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='secondary-nav']", 10, 1, driver);
    }

}

