package PageObject;

import org.openqa.selenium.WebElement;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.util.List;

public class PageObj_PersonalInfo {

    private static WebElement element=null;

    public static WebElement panel_Name(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-name']"));
    }

    public static WebElement panel_AdditionalInformation(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-additional-info']"));
    }

    public static WebElement panel_MedicalConditions(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-medical-conditions']"));
    }


    public static WebElement button_EditName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"ea-name\"]/h5/span/i"));
    }

    public static WebElement button_PersInfoAddinfoEdit(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-additional-info']//span[@class='icon-right']"));
    }

    public static WebElement button_PersInfoMedcondAdd(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"ea-medical-conditions\"]/h5/span/i"));
    }

    public static WebElement button_PersInfoMedcondEdit(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"ea-medical-conditions\"]/h5/span/i"));
    }

    public static WebElement textBox_EditName_GivenName(WebDriver driver){
        return driver.findElement(By.xpath(".//*[@id='FirstName']"));
    }

    public static WebElement textBox_EditName_MiddleName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"MiddleName\"]"));
    }

    public static WebElement textBox_EditName_Surname(WebDriver driver){
      return driver.findElement(By.xpath(".//*[@id='LastName']"));
    }

    public static WebElement textBox_EditName_Preferredname(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='PreferredName']"));
    }

    public static WebElement displayPersonalInformationForm(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[2]/div[2]"));
    }

    public static WebElement lable_EditName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/form/div[1]/h4"));
    }

    public static WebElement button_EditName_Save(WebDriver driver){
        return driver.findElement(By.xpath(".//*[@id='modal-content']/div/div[2]/form/div[3]/div/button"));
    }

    public static WebElement button_AddMedicalConditions(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-medical-conditions']/h5[contains(., 'Medical conditions')]/span/i"));
    }

    public static WebElement button_PersonalInfoNameEdit(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"ea-name\"]/h5/span"));
    }

    public static WebElement button_PersInfoAddinfoDOB(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"BirthDate\"]"));
    }

    public static WebElement form_EditName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/form"));
    }

    public static WebElement button_EditName_Close(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"button-modal-close\"]"));
    }

    public static boolean displayPersonalInfoEditNameForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        PageObj_PersonalInfo.button_EditName(driver);
        SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/div[2]/form", 60, 1, driver);

        if (lable_EditName(driver).getText().equals("Edit Name")){
            SystemLibrary.logMessage("Edit Name form is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("Edit Name form is NOT shown.");
        }
        return isShown;
    }

    public static boolean displayPersonalInfoEditAddInfoForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        PageObj_PersonalInfo.button_PersInfoAddinfoEdit(driver);
        SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/div[2]/form/div[1]/h4", 60, 1, driver);

        if (lable_EditName(driver).getText().equals("Edit Additional Information")){
            SystemLibrary.logMessage("Edit Additional Information form is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("Edit Additional Information form is NOT shown.");
        }
        return isShown;
    }

    public static boolean displayPersonalInfoAddInfoForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        PageObj_PersonalInfo.button_PersInfoMedcondEdit(driver);
        SystemLibrary.waitChild("//*[@id=\"modal-content\"]/div/div[2]/form/div[1]/h4", 60, 1, driver);

        if (lable_EditName(driver).getText().equals("Edit Additional Information")){
            SystemLibrary.logMessage("Edit Additional Information form is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("Edit Additional Information form is NOT shown.");
        }
        return isShown;
    }

    public static WebElement panel_UserPhoto(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-container\"]/div/main/div/div[1]/div/div/div/div[1]/div[1]/label/span/span"));
    }

    public static WebElement button_SaveProfilePhoto(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and text()='Save']"));
    }

    public static WebElement table_PersonalInformationAll(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='master-detail-content']"));
    }

    public static WebElement button_PendingApproval_InNamePanel(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='ea-name']//button[@class='pending-button button ']"));
    }

    public static WebElement button_PendingApproval_InAdditionalInformationPanel(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='display-area-personalAdditionalInformation display-field-container pending']//div[@class='approve-status-action-controls']//div[@class='approve-button-container']//button[contains(@class,'pending-button button')]"));
    }

    public static WebElement textBox_EditMedicalConditions_MedicalCondition(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='condition']"));
    }

    public static WebElement textBox_EditMedicalConditions_Treatment(WebDriver driver){
        return driver.findElement(By.xpath("//textarea[@id='treatment']"));
    }

    public static WebElement button_MedicalCondition_Save(WebDriver driver){
        return driver.findElement(By.xpath("//button[@type='submit' and (text()='Save')]"));
    }

    public static WebElement button_MedicalCondition_Close(WebDriver driver){
        return driver.findElement(By.xpath("//i[@class='icon-close']"));
    }

    public static WebElement dropdownlist_EditAddtionalInfo_Gender(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='Gender']"));
    }

    public static WebElement textBox_EditAdditionalInfo_DOB(WebDriver driver){
        return driver.findElement(By.xpath("//input[@id='BirthDate']"));
    }

    public static WebElement dropdownlist_EditAdditionalInfo_MaritalStatus(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='MaritalStatus']"));
    }

    public static WebElement ellipsis_EditMedicalConditions(WebDriver driver) {
        return driver.findElement(By.xpath("//div[@id='ea-medical-conditions']//button[@class='button--plain add-button']"));
    }

    public static WebElement menu_Edit_MedicalCondition(WebDriver driver) {
        return driver.findElement(By.xpath("//ul[@class='sub-nav show no-width']/li/a[contains(., 'Edit')]"));
    }
}






