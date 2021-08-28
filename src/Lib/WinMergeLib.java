package Lib;

import autoitx4java.AutoItX;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static Lib.SystemLibrary.*;

public class WinMergeLib {

    @Test
    public static void launchWinMerge() throws InterruptedException {

        Runtime rs = Runtime. getRuntime();
        try {
            rs. exec("C:\\Program Files (x86)\\WinMerge\\WinMergeU.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }

        AutoItX x = new AutoItX();
        if (x.winWait("WinMerge", "", 30) == true) {
            logMessage("WinMerge is launched.");
        } else {
            logError("WinMerge is NOT launched.");
        }
        //AutoITLib.logScreenshotX();
    }

    @Test
    public static void closeWindMerge() throws Exception {
        AutoItX x=new AutoItX();
        x.winActivate("WinMerge", "");
        x.send("!F", false);
        x.send("x");
        logMessage("WinMerge tool is closed.");
        x.sleep(5000);

        if (x.winWait("WinMerge", "", 5)){
            killProcess("WinMergeU.exe");
        }
    }



    @Test
    public static void compareTestFileViaWinMerge(String filePathName1, String filePathName2) throws Exception {

        String compareResultFileName="TextFileCompareResult_"+ getCurrentTimeAndDate()+".html";
        String compareResultFileUrl=serverUrlAddress+"FileCompareResult/"+compareResultFileName;
        String compareResultFilePathName=projectPath+"FileCompareResult\\"+compareResultFileName;

        String compareResultFilePath= workingFilePath+"WinMergeCompareResult_"+SystemLibrary.getCurrentTimeAndDate();
        launchWinMerge();

        AutoItX x=new AutoItX();
        x.winActivate("WinMerge");
        x.sleep(2000);

        //x.controlClick("WinMerge", "", "[CLASS:ToolbarWindow32; INSTANCE:1]", "1", 1);
        AutoITLib.clickControlInWindow("WinMerge", "", "[CLASS:ToolbarWindow32; INSTANCE:1]", 50, 63);
        x.sleep(2000);

        x.controlFocus("WinMerge - [Select Files or Folders]", "", "[CLASS:ComboBoxEx32; INSTANCE:1]");
        x.controlClick("WinMerge - [Select Files or Folders]", "", "[CLASS:ComboBoxEx32; INSTANCE:1]");
        x.send("^a", false);
        x.send("{DEL}", false);
        x.send(filePathName1);


        x.controlFocus("WinMerge - [Select Files or Folders]", "", "[CLASS:ComboBoxEx32; INSTANCE:2]");
        x.controlClick("WinMerge - [Select Files or Folders]", "", "[CLASS:ComboBoxEx32; INSTANCE:2]");
        x.send("^a", false);
        x.send("{DEL}", false);
        x.send(filePathName2);

        x.controlFocus("WinMerge - [Select Files or Folders]", "Co&mpare", "[CLASS:Button; INSTANCE:19]");
        if (x.controlEnable("WinMerge - [Select Files or Folders]", "Co&mpare", "[CLASS:Button; INSTANCE:19]")){
            x.controlClick("WinMerge - [Select Files or Folders]", "Co&mpare", "[CLASS:Button; INSTANCE:19]");
            x.sleep(5000);

            x.send("!t", false);
            x.sleep(2000);
            x.send("r");
            x.sleep(2000);

            x.controlFocus("Save As", "xt", "[CLASS:Edit; INSTANCE:1]");
            x.controlClick("Save As", "", "[CLASS:Edit; INSTANCE:1]");
            x.send("^a", false);
            x.send("{DEL}", false);
            x.send(compareResultFilePathName);

            x.controlFocus("Save As", "&Save", "[CLASS:Button; INSTANCE:2]");
            x.controlClick("Save As", "&Save", "[CLASS:Button; INSTANCE:2]");
            x.sleep(5000);
            logMessage("Compare result is saved as '"+compareResultFilePathName);
            logMessage("Click here to access the compare result.");
            logMessage(compareResultFileUrl);

            x.controlFocus("WinMerge", "&Ok", "[CLASS:Button; INSTANCE:1]");
            x.controlClick("WinMerge", "&Ok", "[CLASS:Button; INSTANCE:1]");


        }else{
            logError("Invalidate File Path.");
        }

        closeWindMerge();

    }


}
