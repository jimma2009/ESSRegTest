package SGTest;

import Lib.GeneralBasicHigh;
import Lib.WebAPIKeyManagement;
import Lib.WiniumLib;
import org.testng.annotations.Test;

import static Lib.SystemLibrary.logMessage;

public class GenerateESSKeys {

    ////////////////// This is data driven using datasheet "TestKeys" tab, you need edit datesheet "DataSource - TestKeys Tab first.
    @Test
    public static void generateAllKeysViaDatasheet() throws Exception {
        logMessage("Start generating all Keys");
        GeneralBasicHigh.generateNewTestKeys_Main(10001, 10001);
    }

    /////////////////  This is not data driven, you can input testDerailNo manually.
    @Test
    public static void generateTenentKeys() throws Exception {
        logMessage("Start generating Tenant Keys");
        WiniumLib.generateESSTenant("sg001");
    }

    /////////////// This is not data driven, you can input test Serial number manually.
    @Test
    public static void generateWebAPIKeys() throws Exception {
        logMessage("Start generating Tenant Keys");
        WebAPIKeyManagement.generateWebAPIKey("sg006");
    }

    ///////////////// Debug here //////////////////


}
