package Lib;

import org.openqa.selenium.WebDriver;
import org.testng.util.Strings;

import java.io.IOException;
import java.sql.SQLException;

public class AutoITLibHigh {


    public static boolean logonMeridian_Main(int startSerialNo, int endSerialNo, String payrollDBName, String payrollDBOrderNumber) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        SystemLibrary.logMessage("--- Start log in Meridian from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,20,"M_UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    AutoITLib.launchMeridain();
                    if (!AutoITLib.logonMeridian(cellData[i][2], cellData[i][3], payrollDBName, payrollDBOrderNumber)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in Meridian.");
        return isLogon;
    }

    public static boolean validateMultiEmployeePersonalDetailsScreen(int startSerialNo, int endSerialNo, String payrollDBName, String emailDomainName, String testSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Validating Multi Employee Personal Details screen in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_UserPersonalInformation sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_UserPersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.validateEmployeePersonalDetailsScreen(cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][24], cellData[i][12],cellData[i][13], cellData[i][14], payrollDBName, emailDomainName, testSerialNo)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Validating multi Employees Personal Details screen.");
        return isLogon;
    }


    public static boolean addMultiEmployee_AddsDeds(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Employee Adds Deds in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_EployeeAddsDeds sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_EployeeAddsDeds");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.addEmployee_AddsDeds(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Employee Adds Deds in Meridian.");
        return isLogon;
    }

    public static boolean addMultiCostAccount(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Adding Multi Cost Account in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_CostAccount sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_CostAccount");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.addCostAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Cost Account in Meridian.");
        return isLogon;
    }


    public static boolean addMultiEmployee_CostAccount(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Employee Cost Account in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_CostAccount sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_CostAccount");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.addEmployee_CostAccount(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Employee Cost Account in Meridian.");
        return isLogon;
    }


    public static boolean saveGridInImplementEHRScreen_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Saving and Validate Grid in Implement eHR Screen from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.saveGridInImplementEHRScreen(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9]))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Saving and Validate Grid in Implement eHR Screen.");
        return isPassed;
    }

    public static boolean generateEHRPReImpReport_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Generating eHR Pre-Implementation Report in Implement eHR Screen from row " + startSerialNo + " to " + endSerialNo + " in \"ValidateItems\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "ValidateItems");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.generateEHRPReImpReport(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], emailDomainName, testSerialNo))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Generating eHR Pre-Implementation Report in Implement eHR Screen.");
        return isPassed;
    }

    public static boolean performMultiLeaveProcessing(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Performing Multi Leave Processing in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_LeaveProcessing sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_LeaveProcessing");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.performLeaveProcessing(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of performing Multi Leave Processing in Meridian.");
        return isLogon;
    }

    public static boolean print_EmployeeDetailsReport_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start printing Employee Details Report via Sage Micropay from row " + startSerialNo + " to " + endSerialNo + " in \"M_EmployeeDetailsReport\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "M_EmployeeDetailsReport");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.print_EmployeeDetailsReport(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], emailDomainName, testSerialNo))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of printing Employee Details Report via Sage Micropay.");
        return isPassed;
    }

    public static boolean printMultiTransactionReport(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start printing multi Transaction Report via Sage Micropay from row " + startSerialNo + " to " + endSerialNo + " in \"M_TransactionReport\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "M_TransactionReport");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.printTransactionReport(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9],cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of printing printing multi Transaction Report Report via Sage Micropay.");
        return isPassed;
    }

    public static boolean processMultiPayAdvice(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Processing multi Pay Advice via Sage Micropay from row " + startSerialNo + " to " + endSerialNo + " in \"M_PayAdvice\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "M_PayAdvice");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.processPayAdvice(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9],cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Processing multi Pay Advice via Sage Micropay.");
        return isPassed;
    }

    public static boolean editMultiEmployee_PayDetails(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Editing Multi Employee Pay Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_PayDetails sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_PayDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_PayDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17],cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Pay Details in Meridian.");
        return isDone;
    }

    public static boolean editMultiEmployee_Leave(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Editing Multi Employee Leave in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_Leave sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_Leave");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_Leave(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17],cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Leave in MicrOpay.");
        return isDone;
    }

    public static boolean editMultiEmployee_PersonalInformation(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi Employee Personal Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_PersonalInformation sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_PersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_PersonalInformation(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17],cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Personal Information in MicrOpay.");
        return isDone;
    }

    public static boolean processMultiEOP(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start Processing multi End of Pay via Sage Micropay from row " + startSerialNo + " to " + endSerialNo + " in \"M_EOP\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "M_EOP");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.processEOP(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6])) errorCounter++;

                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of Processing EOP via Sage Micropay.");
        return isPassed;
    }

    public static boolean addMultiPayrollCompany(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {

        SystemLibrary.logMessage("--- Start Adding Multi Payroll Company in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_PayrollCompany sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_PayrollCompany");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.addPayrollCompany(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Payroll Company in Meridian.");
        return isLogon;
    }

    public static boolean editMultiNewEmployee(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //Function to be completed...
        SystemLibrary.logMessage("--- Start Editing Multi New Employee in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editNewEmployee(cellData[i][6], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Employee in Meridian.");
        return isLogon;
    }

    public static boolean editMultiEmployee_Super(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi Employee Leave in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_Super sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_Super");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_Super(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], cellData[i][30], cellData[i][31], cellData[i][32], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Super in MicrOpay.");
        return isDone;
    }

    public static boolean editMultiEmployee_TaxDetails(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi Employee Tax Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_TaxDetails sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_TaxDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_TaxDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], cellData[i][25], cellData[i][26], cellData[i][27], cellData[i][28], cellData[i][29], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Tax Details in MicrOpay.");
        return isDone;
    }

    public static boolean print_EHRReport_Main(int startSerialNo, int endSerialNo, String emailDomainName, String testSerialNo) throws Exception {
        SystemLibrary.logMessage("--- Start printing eHR Report via Sage Micropay from row " + startSerialNo + " to " + endSerialNo + " in \"M_eHRReport\" sheet.");
        boolean isPassed = false;
        int errorCounter = 0;

        int serialNo = 0;
        String[][] cellData = SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo, 50, "M_eHRReport");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.printEHRReport(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], emailDomainName, testSerialNo))
                        errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (Integer.parseInt(cellData[i][0]) >= endSerialNo) break;
            }

        }
        if (errorCounter == 0) isPassed = true;
        SystemLibrary.logMessage("*** End of printing Employee Details Report via Sage Micropay.");
        return isPassed;
    }


    public static boolean editMultiEmployee_PersonalInformation1(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //SystemLibrary.launchWebDriver() first
        SystemLibrary.logMessage("--- Start Editing Multi Employee Personal Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_PersonalInformation sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_PersonalInformation");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_PersonalInformation1(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17],cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], cellData[i][23], cellData[i][24], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Personal Information in MicrOpay.");
        return isDone;
    }

    public static boolean editMultiEmployee_PayDetails1(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Editing Multi Employee Pay Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_PayDetails sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_PayDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_PayDetails1(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17],cellData[i][18], cellData[i][19], cellData[i][20], cellData[i][21], cellData[i][22], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Pay Details in Meridian.");
        return isDone;
    }

    public static boolean validateMultiNewEmployeeMaintenanceScreen(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {

        SystemLibrary.logMessage("--- Start Validating Multi NewEmployee Maintenance Screen in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.validateNewEmployeeMaintenanceScreen(cellData[i][16], cellData[i][17], cellData[i][18], cellData[i][19])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Validating multi New Employee Maintenance Screen.");
        return isLogon;
    }

    public static boolean editMultiPayrollCompany(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Payroll Company in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_PayrollCompany sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_PayrollCompany");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editPayrollCompany(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Payroll Company in Meridian.");
        return isLogon;
    }

    public static boolean editMultiNewEmployee_New(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //Function to be completed...
        SystemLibrary.logMessage("--- Start Editing Multi New Employee in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editNewEmployee_New(cellData[i][20], cellData[i][21], cellData[i][6], cellData[i][2], cellData[i][4], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Employee in Meridian.");
        return isLogon;
    }

    public static boolean editMultiNewEmployee_Voluntary(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //Function to be completed...
        SystemLibrary.logMessage("--- Start Editing Multi New Employee in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editNewEmployee_Voluntary(cellData[i][6], cellData[i][2], cellData[i][4], cellData[i][7], cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Employee in Meridian.");
        return isLogon;
    }

    public static boolean addMultiLookup(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {

        SystemLibrary.logMessage("--- Start Adding Multi Payroll Company in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Lookup sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Lookup");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.addLookup(cellData[i][2], cellData[i][3], cellData[i][5], cellData[i][7])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Payroll Company in Meridian.");
        return isLogon;
    }


    public static boolean processMultiBatch_NewEmployeeMaintenanceScreen(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {

        SystemLibrary.logMessage("--- Start Validating Multi NewEmployee Maintenance Screen in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.processbatch_NewEmployeeMaintenanceScreen(cellData[i][22], cellData[i][23])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Validating multi New Employee Maintenance Screen.");
        return isLogon;
    }

    public static boolean deleteMultiEmployeeMaintenanceScreen(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        //Function to be completed...
        SystemLibrary.logMessage("--- Start Editing Multi New Employee in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_NewEmployee sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_NewEmployee");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.deleteNewEmployeeMaintenanceScreen(cellData[i][2], cellData[i][4], cellData[i][24])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Editing Multi New Employee in Meridian.");
        return isLogon;
    }


    public static boolean logonMeridianSecondDB_Main(int startSerialNo, int endSerialNo, String payrollDB2Name, String payrollDB2OrderNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        SystemLibrary.logMessage("--- Start log in Meridian from row " + startSerialNo + " to " + endSerialNo);
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"M_UserLogin");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    AutoITLib.launchMeridain();
                    if (!AutoITLib.logonMeridianSecondDB(cellData[i][2], cellData[i][3], payrollDB2Name, payrollDB2OrderNo)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of log in Meridian.");
        return isLogon;
    }
    
    public static boolean editMultiPayFrequencies(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Adding Multi Payroll Company in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_PayFrequencies sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_PayFrequencies");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editPayFrequencies(cellData[i][2], cellData[i][3])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of Adding Multi Payroll Company in Meridian.");
        return isLogon;
    }


    public static boolean generateAutoPayMultiTransactions(int startSerialNo, int endSerialNo) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Performing Multi Leave Processing in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_PayFrequencies sheet.");
        boolean isLogon=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_PayFrequencies");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.generateAutoPayTransactions(cellData[i][4])) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isLogon=true;
        SystemLibrary.logMessage("*** End of performing Multi Leave Processing in Meridian.");
        return isLogon;
    }

    public static boolean editMultiEmployee_RateDetails(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Editing Multi Employee Pay Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_Employee_RateDetails sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_Employee_RateDetails");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.editEmployee_RateDetails(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], cellData[i][10], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Pay Details in Meridian.");
        return isDone;
    }



    public static boolean editMultiExtendTimeSheetDetails(int startSerialNo, int endSerialNo, String payrollDBName) throws IOException, InterruptedException, ClassNotFoundException, SQLException, Exception {
        SystemLibrary.logMessage("--- Start Editing Multi Employee Pay Details in Meridian from row " + startSerialNo + " to " + endSerialNo+" in M_ExtendedTime sheet.");
        boolean isDone=false;
        int errorCounter=0;
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,40,"M_ExtendedTime");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    if (!AutoITLib.edit_ExtendTimeSheet(cellData[i][2], cellData[i][3], cellData[i][6], cellData[i][7],cellData[i][8], cellData[i][9], payrollDBName)) errorCounter++;
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }

        }

        if (errorCounter==0) isDone=true;
        SystemLibrary.logMessage("*** End of Editing Multi Employee Pay Details in Meridian.");
        return isDone;
    }

}
