package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static Lib.SystemLibrary.logMessage;
import static Lib.SystemLibrary.logWarning;

public class PageFac_ContactDetail {

    /*
    @FindBy(how=How.XPATH, using = "//*[@id=\"ea-personal-contact\"]/h5/span")
    public static WebElement button_EditPersonalContact;

    @FindBy(how=How.XPATH, using = "//*[@id=\"Email\"]")
    public static WebElement text_EditPersonalEmail;

    @FindBy(how=How.XPATH, using = "//*[@id=\"Mobile\"]")
    public static WebElement text_EditPersonalMobile;

    @FindBy(how=How.XPATH, using = "//*[@id=\"Home\"]")
    public static WebElement text_EditPersonalContact;

    @FindBy(how=How.XPATH, using = "//*[@id=\"modal-content\"]/div/div[2]/form/div[3]/div/button")
    public static WebElement button_SavePersonalContact;
    */

    @FindBy(xpath = "//*[@id=\"ea-personal-contact\"]/h5/span")
    public static WebElement button_EditPersonalContact;

    @FindBy(xpath= "//*[@id=\"ea-work-contact\"]/h5/span")
    public static WebElement button_EditWorkContact;

    public static boolean displayEditWorkContactForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        button_EditWorkContact.click();
        Thread.sleep(5000);

        if (lable_EditWorkContact.getText().equals("Edit Work Contact")){
            logMessage("Edit Work Contact form is shown.");
            isShown=true;
        }
        else{
            SystemLibrary.logError("Edit Work Contact form is NOT shown.");
        }
        return isShown;
    }

    public static boolean displayEditPersonalContactForm(WebDriver driver) throws InterruptedException {
        boolean isShown=false;
        //button_EditPersonalContact.click();  Jim adjusted on 04/07/2018
        WebElement button_Edit=SystemLibrary.waitChild("//div[@id='ea-personal-contact']//i[@class='icon-edit']", 2, 1, driver);
        if (button_Edit!=null){
            button_Edit.click();
            if (SystemLibrary.waitChild("//h4[@class='mc-header-heading' and text()='Edit Personal Contact']", 60, 1, driver)!=null){
                logMessage("Edit Personal Contact form is shown.");
                isShown=true;
            }
            else{
                SystemLibrary.logError("Edit Personal Contact form is NOT shown.");
            }
        }
        else{
            logWarning("Edit button is NOT shown.");
        }
        return isShown;
    }

    @FindBy(xpath = "//*[@id=\"modal-content\"]/div/div[2]/form")
    public static WebElement form_EditPersonalWorkContac;

    @FindBy(xpath="//*[@id=\"button-modal-close\"]")
    public static WebElement button_CloseEditPersonalWorkContact;

    @FindBy(xpath="//*[@id=\"modal-content\"]/div/div[2]/form/div[1]/h4")
    public static WebElement lable_EditPersonalContact;

    @FindBy(xpath="//*[@id=\"modal-content\"]/div/div[2]/form/div[1]/h4")
    public static WebElement lable_EditWorkContact;

    @FindBy(xpath = "//*[@id=\"Email\"]")
    public static WebElement text_EditPersonalEmail;

    @FindBy(xpath = "//*[@id=\"Mobile\"]")
    public static WebElement text_EditPersonalMobile;

    @FindBy(xpath = "//*[@id=\"Home\"]")
    public static WebElement text_EditHomeNumber;

    @FindBy(xpath="//*[@id=\"WorkEmail\"]")
    public static WebElement text_EditWorkEmail;

    @FindBy(xpath="//*[@id=\"WorkPhone\"]")
    public static WebElement text_EditOfficeNumber;

    @FindBy(xpath="//*[@id=\"WorkMobile\"]")
    public static WebElement getText_EditWorkMobile;


    @FindBy(xpath="//button[@type='submit' and text()='Save']")
    public static WebElement button_SavePersonalWorkContact;

    @FindBy(xpath="//dd[contains(text(), '@')]")
    public static WebElement lable_WorkEmail;


    /////////// Edit Address //////////////
    @FindBy(xpath="//input[@id='res:Country']")
    public static WebElement textbox_ResidentialCountry;

    @FindBy(xpath="//input[@id='res:AddressLine1']")
    public static WebElement textbox_ResidentialAddress;

    @FindBy(xpath="//input[@id='res:Suburb']")
    public static WebElement textbox_ResidentialSuburb;

    @FindBy(xpath="//input[@id='res:Postcode']")
    public static WebElement textbox_ResidentialPostcode;

    @FindBy(xpath="//input[@id='res:StateName']")
    public static WebElement textbox_ResidentialState;

    @FindBy(xpath="//input[@id='IsPostalAddressSameAsResidential']")
    public static WebElement checkbox_UseForPostalAddress;


    ///////////////////
    @FindBy(xpath="//input[@id='mail:Country']")
    public static WebElement textbox_PostalCountry;

    @FindBy(xpath="//input[@id='mail:AddressLine1']")
    public static WebElement textbox_PostalAddress;

    @FindBy(xpath="//input[@id='mail:Suburb']")
    public static WebElement textbox_PostalSuburb;

    @FindBy(xpath="//input[@id='mail:Postcode']")
    public static WebElement textbox_PostalPostcode;

    @FindBy(xpath="//input[@id='mail:StateName']")
    public static WebElement textbox_PostalState;

    public static boolean selectResidentialState_InContactDetail_EditAddressPage(WebElement stateDropdown, String stateName, WebDriver driver) throws InterruptedException {
        //Edit Address diaglogue is shown first.
        boolean isSelected=false;
        stateDropdown.click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        WebElement stateItem=SystemLibrary.waitChild("//div[@id='res:State-select-list']/div[starts-with(@class, 'list-item') and text()='"+stateName+"']", 10, 1, driver);
        if (stateItem!=null){
            stateItem.click();
            logMessage("State '"+stateName+"' is clicked.");
            Thread.sleep(2000);
            isSelected=true;
        }
        else{
            SystemLibrary.logError("State '"+stateName+"' is NOT selected.");
        }
        return isSelected;
    }

    public static boolean selectPostalState_InContactDetail_EditAddressPage(WebElement stateDropdown, String stateName, WebDriver driver) throws InterruptedException {
        //Edit Address diaglogue is shown first.
        boolean isSelected=false;
        stateDropdown.click();
        Thread.sleep(3000);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        WebElement stateItem=SystemLibrary.waitChild("//div[@id='mail:State-select-list']/div[starts-with(@class, 'list-item') and text()='"+stateName+"']", 10, 1, driver);
        logMessage(SystemLibrary.getElementXPath(driver, stateItem));
        logMessage("StateItem is Display: "+stateItem.isDisplayed());
        if (stateItem!=null){
            stateItem.click();
            logMessage("State '"+stateName+"' is clicked.");
            Thread.sleep(2000);
            isSelected=true;
        }
        else{
            SystemLibrary.logError("State '"+stateName+"' is NOT selected.");
        }
        return isSelected;
    }

    //////

    @FindBy(xpath="//button[@type='submit' and text()='Save']")
    public static WebElement button_SaveEditAddress;


    @FindBy(xpath = "//div[@id='ea-address' and contains(., 'Address')]//span[@class='icon-right']//i[@class='icon-edit']")
    public static WebElement button_EditContactDetails_Address;

    @FindBy(xpath = "//div[@id='ea-emergency-contact' and contains(., 'Emergency contact')]//span[@class='icon-right']//i[@class='icon-edit']")
    public static WebElement button_EditContactDetails_EmergencyContact;

    /////////////////////// Edit Emergency Contacts page
    @FindBy(xpath="//input[@id='ContactName']")
    public static WebElement textbox_EmergencyContactName;

    @FindBy(xpath="//input[@id='ContactRelationship']")
    public static WebElement textbox_EmergencyContactRelationshop;

    @FindBy(xpath="//input[@id='MobileContact']")
    public static WebElement textbox_EmergencyMobileContact;

    @FindBy(xpath="//input[@id='TelephoneContact']")
    public static WebElement textbox_EmergencyPhoneContact;

    @FindBy(xpath="//button[@class='delete-button button--danger  button--inverted margin-bottom' and text()='Delete']")
    public static WebElement button_DeleteEmergencyContacts;

    @FindBy(xpath="//button[contains(@class,'confirm-delete-button button button--danger')]")
    public static WebElement getButton_DeleteEmergencyContacts_YesDelete;

    @FindBy(xpath="//button[@type='submit' and text()='Save']")
    public static WebElement butotn_SaveEmergencyContacts;



}
