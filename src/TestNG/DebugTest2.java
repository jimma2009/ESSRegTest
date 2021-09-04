package TestNG;

import Lib.*;
import org.testng.annotations.Test;

import java.io.IOException;

public class DebugTest2 {

    private static String testSerialNo ="xxxxx";
    private static String emailDomainName ="mptest1002.dyny.net";

    @Test
    public static void test1() throws IOException {
        ExcelDataWriter.AddDataIntoExcelFile("C:\\test.xlsx", "Sheet1", 1, 2, "Hello result.");

    }

}
