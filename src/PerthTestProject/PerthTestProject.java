package PerthTestProject;

import org.testng.annotations.Test;

import static Lib.SystemLibrary.launchWebDriver;
import static Lib.SystemLibrary.logMessage;

public class PerthTestProject {
    @Test
    public static void test2() throws InterruptedException {
        logMessage("*** Start test2.");

        logMessage("Test Item 2", "pr001");


        logMessage("*** End of test2.");
    }
}
