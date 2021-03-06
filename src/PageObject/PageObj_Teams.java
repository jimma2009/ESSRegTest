package PageObject;

import Lib.GeneralBasic;
import Lib.SystemLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static Lib.SystemLibrary.*;

public class PageObj_Teams {
    public static WebElement lable_Teams(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"page-title\"]/h3"));
    }

    public static int getTotalNumberOfUnsignedMemberViaTeamTable(WebDriver driver) throws InterruptedException {
        int totalNumberOfUnassignedMember=Integer.parseInt(driver.findElement(By.xpath("//*[@id=\"list-item-Unassigned\"]/div[2]/span/div/span")).getText());
        SystemLibrary.logMessage("There are total "+totalNumberOfUnassignedMember+" Unassigned members.");
        return totalNumberOfUnassignedMember;
    }

    public static WebElement link_UnasignedInTeamTable(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"list-item-Unassigned\"]/div[1]/a/span[1]"));
    }

    public static WebElement link_ViewMoreUnderMembersListTable(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"single-team\"]/div/div/div/form/div[3]/div[3]/a"));
    }

    public static void displayAllUnassignedTeam(WebDriver driver) throws Exception {
        //Display Teams Page first
        link_UnasignedInTeamTable(driver).click();
        SystemLibrary.logMessage("Unassigned is clicked in Team Table.");
        Thread.sleep(15000);

        SystemLibrary.clickViewmoreButtonInTable(driver);

        SystemLibrary.logMessage("All Unassigned Team members are shown.");
    }

    public static WebElement ellipsis_Teams_10(WebDriver driver){
        return driver.findElement(By.xpath("//div[@id='team-page-controls']//button[@class='button--plain add-button']"));
    }

    public static boolean click_AddNewTeam(WebDriver driver) throws InterruptedException {
        boolean isClicked=false;
        Thread.sleep(1000);
        ellipsis_Teams_10(driver).click();
        Thread.sleep(2000);

        try {
            WebElement element = driver.findElement(By.linkText("Add new team"));
            element.click();
            isClicked=true;
        }
        catch (Exception e){
            SystemLibrary.logWarning("Add new team menu is not found.");
        }
        return isClicked;
    }

    public static String xpath_Table_Teams_Unassigned_Member="//*[@id=\"single-team\"]/div/div/div/form/div[3]/div[2]/div/div";

    public static WebElement textBox_AddNewTeamForm_Teamname(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"Name\"]"));
    }

    public static WebElement textBox_SearchfiledForm_Addmember(WebDriver driver){
        return driver.findElement(By.xpath("id(\"form-element-textbox_Search\")/div/input"));
    }
    public static WebElement select_selectmember_Addmember(WebDriver driver){
        return driver.findElement(By.xpath(".//*[@id='modal-content']/div[1]/form[1]/div[2]/div[1]/div[1]/div[1]/fieldset[1]/div[1]/div[1]/div[2]/div[1]"));
    }

    public static WebElement select_Addmember_Next(WebDriver driver){
        return driver.findElement(By.xpath("//button [text()='Next']"));
    }

    public static WebElement select_Addmember_Done(WebDriver driver){
        return driver.findElement(By.xpath("//button [text()='Done']"));
    }

    public static WebElement button_AddNewTeamForm_Add(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button"));
    }

    public static WebElement button_AddNewTeamForm_Close(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"button-modal-close\"]/i"));
    }

    public static boolean displayTeamMembers(String teamName, WebDriver driver) throws Exception {
        //Teams page or Team Member page is shown first
        clickViewmoreButtonInTable(driver);

        boolean isDisplay=false;
        int errorCounter=0;
        //Wait for LinkText teamName
        WebElement element=SystemLibrary.waitChild(teamName, 120, 5, driver);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if (element!=null){
            if ((teamName.equals("Directory"))||(teamName.equals("Unassigned"))||(teamName.equals("New starters"))){
                SystemLibrary.scrollToTop(driver);
                element.click();
            }
            else{
                SystemLibrary.scrollToTop(driver);
                displayElementInView(element, driver, -125);
                element.click();
            }

            //element.click();
            logMessage("Team '"+teamName+"' is clicked.");
            Thread.sleep(3000);
            GeneralBasic.waitSpinnerDisappear(60, driver);
            logScreenshot(driver);
        }
        else{
            errorCounter++;
        }

        //Wait for the form
        if (!teamName.equals("New starters")){
            ////////////// only wait if it is not New starters
            WebElement element_elllipsis=SystemLibrary.waitChild("//form[contains(., 'Summary')]", 120, 1, driver);
            if (element==null){
                errorCounter++;
            }
        }

        if (errorCounter==0) isDisplay=true;
        logMessage("Team '"+teamName+"' is shown. Screenshot below: ");
        logScreenshot(driver);

        Thread.sleep(3000);
        return isDisplay;
    }

    public static WebElement table_TeamMembers(WebDriver driver){
        //return driver.findElement(By.xpath("//*[@id=\"single-team\"]/div/div/div/form"));
        return driver.findElement(By.xpath("//div[@id='single-team']"));
    }

    public static WebElement table_Teams(WebDriver driver){
        return SystemLibrary.waitChild("//*[@id=\"team-list\"]/div/div/div/div/form", 10, 1, driver);
    }

    public static WebElement table_PageRow(WebDriver driver){
        return SystemLibrary.waitChild("//div[@class='page-row']", 10, 1, driver);
    }

    public static WebElement ellipsis_Teams_11(String teamName, WebDriver driver){
        return driver.findElement(By.xpath("//*[@id='list-item-" + teamName + "']/div[3]/div/div/button"));

    }

    //validateTeamEllipsis
    public static WebElement getEllipsisInTeamTable(String teamName, WebDriver driver) throws IOException{
        WebElement element=null;
        //Team Page or Team Member page is shown first
        element=driver.findElement(By.linkText(teamName));
        String currentXpath=SystemLibrary.getElementXPath(driver, element);
        String ellipsisButtonXpath= currentXpath.replace("/div[1]/a[1]", "/div/div/div");

        System.out.println("The Xpath of Ellipsis is '"+ellipsisButtonXpath+"'.");
        element=SystemLibrary.waitChild(ellipsisButtonXpath, 5, 1, driver);
        return element;

    }

    //validateTeamEllipsis

    public static WebElement ellipsis_TeamsTeam_20(WebDriver driver){
        return driver.findElement(By.xpath("//div[contains(@id, 'team')]/button[@class='button--plain add-button']"));
    }

    public static WebElement getEllipsis_TeamsTeam_20(String teamName, WebDriver driver) throws InterruptedException, IOException, Exception {
        //Display Teams Page first
        WebElement element=null;
        if (PageObj_Teams.displayTeamMembers(teamName, driver)){
            element=SystemLibrary.waitChild("//*[@id=\"page-controls\"]/div/div", 5, 1, driver);
        }

        return element;
    }

    public static WebElement getEllipsisInTeamsTeamTable(String teamName, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        //Team Page or Team Member page is shown first
        element=driver.findElement(By.linkText(teamName));
        logMessage("Selecting " + teamName + " from list of teams");
        element.click();
        Thread.sleep(2000);
        waitElementInvisible(timeOutInSeconds,"id(\"spinner0\")",driver);
        element= getEllipsisForTeamName(teamName, driver);
        return element;
    }

    public static WebElement getEllipsisForTeamName(String teamName, WebDriver driver) throws InterruptedException {
        WebElement element=driver.findElement(By.xpath("//span [text()='" + teamName + "']"));
        String currentXpath=SystemLibrary.getElementXPath(driver, element);
        String ellipsisButtonXpath = currentXpath.replace("id(\"page-title\")/h3[1]/span[2]", "id(\"page-controls\")/div/div");
        System.out.println("The Xpath of Ellipsis is '"+ellipsisButtonXpath+"'.");
        element=SystemLibrary.waitChild(ellipsisButtonXpath, 5, 1, driver);
        element.click();
        Thread.sleep(1000);
        return element;
    }

    public static WebElement getEllipsis_Teams_21(String teamName, String memberName, WebDriver driver) throws Exception {
        //Dispaly Team page first.
        displayTeamMembers(teamName, driver);
        SystemLibrary.clickViewmoreButtonInTable(driver);

        WebElement element_MemberName=driver.findElement(By.linkText(memberName));
        String currentXpath=SystemLibrary.getElementXPath(driver, element_MemberName);
        String ellipsisButtonXpath= currentXpath.replace("/div[1]/a[1]", "/div[3]/div/div/button");
        System.out.println("The Xpath of Ellipsis is '"+ellipsisButtonXpath+"'.");
        WebElement element=SystemLibrary.waitChild(ellipsisButtonXpath, 5, 1, driver);
        return element;
    }

    public static WebElement getEllipsis_Unassigned_11(String memberName, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        //Team Page or Unassigned page is shown first
        element=driver.findElement(By.linkText(memberName));
        System.out.println("The Xpath of element is '"+element+"'.");
        String currentXpath=SystemLibrary.getElementXPath(driver, element);
        String ellipsisButtonXpath= currentXpath.replace("/div[1]/a[1]", "/div[3]/div/div/button");
        System.out.println("The Xpath of Ellipsis is '"+ellipsisButtonXpath+"'.");
        element=SystemLibrary.waitChild(ellipsisButtonXpath, 5, 1, driver);
        return element;
    }
    public static WebElement getTeamRenameTeams(String teamRename, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        //Team name Ellipsis should be displayed
        element=driver.findElement(By.linkText(teamRename));
        return element;

    }

    public static WebElement getAddMemberTeams(String addMember, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        //Team name Ellipsis should be displayed
        element=driver.findElement(By.linkText(addMember));
        return element;

    }

    public static WebElement textBox_RenameTeamName(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"Name\"]"));
    }

    public static WebElement button_RenameTeam_Save(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/button"));
    }

    public static WebElement textBox_Name_AddTeamMember(WebDriver driver){
        return driver.findElement(By.xpath("//div[@class='text-with-selector']//div[@id='form-element-searchField']//div[@class='sign-class']//input[@id='searchField']"));
    }

    public static WebElement textBox_AddTeam(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"teamSearchField\"]"));
    }

    public static WebElement checkBox_AddMember_AssignAsManager(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"assignAsManagerForPerson\"]"));
    }

    public static WebElement checkBox_MoveToTeam_AssignAsManager(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"form-element-assignAsManagerForTeam\"]"));
    }

    public static WebElement button_AddMember_Next(WebDriver driver){
        //return driver.findElement(By.xpath("//button[text()='Next']"));
        return SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver);
    }

    public static WebElement button_AddMemeber_Next2(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/div[2]/button"));
    }

    public static WebElement button_AddMember_Done(WebDriver driver){
        return SystemLibrary.waitChild("//button[text()='Done']", 10, 1, driver);
        //return driver.findElement(By.xpath("//button[text()='Done']"));
    }

    public static WebElement dialogueBox_DeleteTeam(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]"));
    }
    public static WebElement dialogueBox_RemovefromTeam(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]"));
    }

    public static WebElement dialogueBox_addmember_dropdown(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[2]/div"));
    }

    public static WebElement lable_DeleteTeam_Messagebox(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/div[2]/div[1]/h4"));
    }

    public static WebElement lable_RemoveMember_Messagebox(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[1]/h4"));
    }

    public static WebElement dialogueBox_ChangeRole(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]"));
    }

    public static WebElement checkBox_ChangeRole_AssignAsManager(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"assignAsManager\"]"));
    }

    public static WebElement button_ChangeRole_Next(WebDriver driver){
        //return driver.findElement(By.xpath("//button[text()='Next']"));
        return SystemLibrary.waitChild("//button[text()='Next']", 10, 1, driver);
    }

    public static WebElement button_ChangeRole_Done(WebDriver driver){
        return driver.findElement(By.xpath("//button[text()='Done']"));
    }

    public static WebElement textBox_MemberMovetoTeam(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"teamSearchField\"]"));
    }
    public static WebElement button_Movetoteam_Done(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"modal-content\"]/div/form/div[3]/div/div/button"));
    }

    public static WebElement linkText_getMemberName(String memberName, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        element=driver.findElement(By.linkText(memberName));
        return element;
    }

    public static WebElement linkText_getDirectoryLeave(String DirectoryLeave, WebDriver driver) throws InterruptedException{
        WebElement element=null;
        element=driver.findElement(By.linkText(DirectoryLeave));
        return element;
    }
    public static WebElement tab_directoryLeave(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"single-team\"]/div/div/div/form/div[1]/div/div/div/button[2]"));
    }

    public static WebElement checkBox_movetoteam_AssignAsManager(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"form-element-assignAsManagerForTeam\"]"));
    }

    public static WebElement button_Leave_InTeamMemberListTable(WebDriver driver){
        return driver.findElement(By.xpath("//button[contains(text(),'Leave')]"));
    }

    public static WebElement button_Summary_InTeamMemberListTable(WebDriver driver){
        return driver.findElement(By.xpath("//*[@id=\"single-team\"]/div/div/div/form/div[1]/div/div/div/button[1]"));
    }

    public static boolean displayTeamMembers_Performance(String teamName, WebDriver driver) throws Exception {
        //Teams page or Team Member page is shown first
        boolean isDisplay=false;
        int errorCounter=0;
        //Wait for LinkText teamName
        WebElement element=SystemLibrary.waitChild(teamName, 120, 5, driver);
        GeneralBasic.waitSpinnerDisappear(120, driver);

        if ((teamName.equals("Directory"))||(teamName.equals("Unassigned"))||(teamName.equals("New starters"))||(teamName.equals("Directory"))){
            SystemLibrary.scrollToTop(driver);
        }

        if (element!=null){
            element.click();
            logMessage("Team '"+teamName+"' is clicked.");
        }
        else{
            errorCounter++;
        }


        GeneralBasic.waitSpinnerDisappear(120, driver);

        //Wait for the form
        WebElement element_elllipsis=SystemLibrary.waitChild("//form[contains(., 'Summary')]", 120, 1, driver);
        waitChild("//div[@class='page-header']//button", 60, 1, driver);



        if (errorCounter==0) isDisplay=true;
        logMessage("Team '"+teamName+"' is shown. Screenshot below: ");
        logScreenshot(driver);

        return isDisplay;
    }

    ///////////// New Team Contral Added by Jim on 07/10/2020 /////////////
    public static WebElement button_TeamPage_Ellipsis(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='team-page-controls']//button", 10, 1, driver);
    }

    public static WebElement button_TeamPage_3Options(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='page-controls']/button", 10, 1, driver);
    }

    public static WebElement button_TeamPage_3Optoins_SelectAll(WebDriver driver){
        return SystemLibrary.waitChild("//div[@id='page-controls']/button/i[contains(@class, 'icon-select-all')]", 10, 1, driver);
    }

    public static WebElement getEllipsisButtonInTeamPageViaAdmin(String teamName, WebDriver driver){
        String xpath_EllipsisButton="//div[@id='team-list']//div[contains(@class, 'list-item-row') and contains(., '"+teamName+"')]//button";
        logDebug(xpath_EllipsisButton);
        return SystemLibrary.waitChild("//div[@id='team-list']//div[contains(@class, 'list-item-row') and contains(., '"+teamName+"')]//button", 10, 1, driver);
    }

    public static WebElement button_TeamPage_3Optoins_View(WebDriver driver){
        return SystemLibrary.waitChild("//button[contains(text(),'View')]", 10, 1, driver);
    }



    public static WebElement button_SelectedTeams_TeamPage_Ellipsis(WebDriver driver){
        return SystemLibrary.waitChild("//div[@class='page-header']//button[@class='button--plain add-button']", 10, 1, driver);
    }

    public static WebElement linkText_ActivateMembers(WebDriver driver){
        return SystemLibrary.waitChild("//ul[@class='sub-nav show']//i[@class='icon-multiselect']", 10, 1, driver);
    }

    public static WebElement button_TeamPage_3Optoins_Activate(WebDriver driver){
        return SystemLibrary.waitChild("//button[contains(text(),'Activate')]", 10, 1, driver);
    }

    public static WebElement button_TeamPage_3Optoins_Cancel(WebDriver driver){
        return SystemLibrary.waitChild("//button[contains(text(),'Cancel')]", 10, 1, driver);
    }

    public static WebElement button_YesActivateSelected(WebDriver driver){
        return SystemLibrary.waitChild("//button[@type='submit' and contains(text(), 'Yes, activate selected')]", 10, 1, driver);
    }

    public static WebElement button_NoCancel(WebDriver driver){
        return SystemLibrary.waitChild("//button[contains(text(), 'No, cancel')]", 10, 1, driver);
    }

    public static WebElement select_Role_Dropdown(WebDriver driver){
       return driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/form/div[2]/div/div[3]/div/fieldset/div[2]/div/div[1]"));
     //  return driver.findElement(By.xpath("//*[@id='role']"));
    }

    public static WebElement role_Dropdown(WebDriver driver){
        return driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/form/div[2]/div/div[3]/div/fieldset/div[2]/div/div[2]"));
   //     return driver.findElement(By.xpath("//*[@id='role-select-list']"))

    }




}
