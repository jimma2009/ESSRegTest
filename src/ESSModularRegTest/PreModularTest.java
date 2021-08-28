package ESSModularRegTest;

import Lib.DBManage;
import Lib.GeneralBasicHigh;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static Lib.SystemLibrary.logMessage;

public class PreModularTest {

    @Test (priority = 0)
    public static void createAllKeys() throws Exception {
        logMessage("Start Export test config into Log DB.");
        int option=1;
        DBManage.exportESSConfigIntoDB_Main(101, 310);

        /*if (getComputerName()!=null){
            if (getComputerName().equals("EC2AMAZ-MJD1STQ")){
                option=2;
            }else{
                option=1;
            }
        }*/

        switch (option){
            case 1:
                logMessage("Start generating all Keys via Customer.");
                GeneralBasicHigh.generateNewTestKeysViaCustomer_Main(101, 310);
                break;
            case 2:
                logMessage("Start generating all Keys via Admin User.");
                GeneralBasicHigh.generateNewTestKeys_Main(101, 310);
                break;
        }
    }

    public static String getComputerName(){
        String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            System.out.println(hostname);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
    }


    ////////////// Debug here //////////////


}