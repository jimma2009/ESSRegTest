package Lib;

import autoitx4java.AutoItX;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.testng.annotations.Test;

public class SikuliLib {
    @Test
    public static void test1() throws Exception {
        Screen s=new Screen();
        String filePath="C:\\SikuliDemo\\";
        String inputFilePath="C:\\SikuliDemo\\";

        Pattern button_AddMCS=new Pattern(filePath+"buttonAddMCS.PNG");

        WiniumLib.closeWiniumService();
        AutoItX x=new AutoItX();
        x.mouseClick("left", 0, 0, 1, 0);

        WiniumLib.launchWiniumService();
        WiniumDriver driver= WiniumLib.launchApplication("C:\\TestAutomationProject\\ESSRegTest\\MCS\\mcs.bat");
        x.winActivate("MicrOpay Client Service Config.  Version 3.0.1.3");

        s.wait(button_AddMCS, 20);

        s.doubleClick();

    }
}
