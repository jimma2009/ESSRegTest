package TestNG;

import Lib.*;
import PageObject.PageObj_EditUserInWebAPIManage;
import PageObject.PageObj_Integration;
import autoitx4java.AutoItX;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.Key;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static Lib.GeneralBasic.displayDashboard;
import static Lib.GeneralBasic.signoutESS;
import static Lib.GeneralBasicHigh.editWebAPIConfiguration_Main;
import static Lib.SystemLibrary.*;
import static Lib.WebAPIKeyManagement.*;
import static Lib.WiniumLib.launchApplication;
import static autoitx4java.AutoItX.SW_MAXIMIZE;

public class DebugTest2 {

    private static String testSerialNo ="xxxxx";
    private static String emailDomainName ="mptest1002.dyny.net";

    @Test
    public static void test1() throws IOException {
        ExcelDataWriter.AddDataIntoExcelFile("C:\\test.xlsx", "Sheet1", 1, 2, "Hello result.");

    }

}
