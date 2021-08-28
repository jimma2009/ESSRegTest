package ESSModularRegTest;

import Lib.DBManage;
import org.testng.annotations.Test;

import java.net.UnknownHostException;

import static Lib.SystemLibrary.logMessage;

public class ESSRegTest_Debug2 {
    public static void main(String[] args){
        System.out.println("Hello Java.");
    }

    @Test
    public static void test1(){
        System.out.println("Hello TestNG 2.");
    }

    @Test
    public static void test2() throws InterruptedException {
        logMessage("Hi");
    }

    @Test
    public static void ManageDBs() throws InterruptedException, UnknownHostException {
        int dbRowNumber_Start=191;
        int dbRowNumber_End=192;

        //DBManage.deleteMultiDB(dbRowNumber_Start, dbRowNumber_End);
        //DBManage.restoreMultiDB(dbRowNumber_Start, dbRowNumber_End);
        DBManage.backupMultiDB(dbRowNumber_Start, dbRowNumber_End);
    }


    @Test
    public static void test11() throws InterruptedException {
        logMessage("Hello Java!");
    }

}
