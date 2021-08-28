package Lib;

import org.testng.util.Strings;

import java.net.UnknownHostException;
import java.sql.SQLException;

import static Lib.GeneralBasicWE.openWEDB;

public class GeneralBasicHightWE {

    public static boolean openWEDB_Main(int startSerialNo, int endSerialNo) throws InterruptedException, UnknownHostException {
        boolean isOpen=false;
        int errorCounter=0;
        SystemLibrary.logMessage("--- Start Opening WE DB from row " + startSerialNo + " to " + endSerialNo + " in \"DBManagement\" sheet.");

        int serialNo = 0;

        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 20, "DBManagement");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);

                if (Strings.isNullOrEmpty(cellData[i][1])) {

                    try {
                        if (!openWEDB(cellData[i][4], cellData[i][9])) errorCounter++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorCounter++;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        errorCounter++;
                    }

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                    errorCounter++;
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        SystemLibrary.logMessage("*** End of Restoring DB from \"DBManagement\" sheet.");
        if (errorCounter==0) isOpen=true;
        return isOpen;
    }
}
