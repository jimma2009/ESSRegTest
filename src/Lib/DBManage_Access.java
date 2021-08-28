package Lib;

import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.*;

import static Lib.SystemLibrary.*;

public class DBManage_Access {

    @Test
    public static void test1() throws Exception {
        //String sql="Select * from EmpEmail";
        String sql="update EmpEmail inner join in employee on Employee.Id=EmpEmail.EmployeeID set EmailAddress='Test3'";
        sqlExecuter_AC(sql, "1", null, null, null, null);
    }

    public static boolean updateEmployeeEmail(String testSerialNo, String emailDomainName) throws Exception {
        boolean isDone=false;
        //String sql="update EmpEmail set EmpEmail.EmailAddress='Test'";
        String sql="update EmpEmail INNER join Employee on Employee.Id=EmpEmail.EmployeeId SET EmpEmail.EmailAddress=Employee.FirstName+'_'+Employee.LastName+'_"+testSerialNo+"@"+emailDomainName+"'";
        //String sql="update EmpEmail set EmpEmail.EmailAddress=Employee.FirstName+'_'+Employee.LastName+'_'"+testSerialNo+"@"+emailDomainName+" From EmpEmail inner join Employee on Employee.Id=EmpEmail.EmployeeId";
        isDone=sqlExecuter_AC(sql, "2", null, null, null, null);
        return isDone;
    }


    public static boolean sqlExecuter_AC(String sql, String exectueType, String storeFileFullName, String isUpdateStore, String isCompare, String expectedContent) throws Exception {
        boolean isDone=false;
        int errorCounter=0;

        String databaseURL = "jdbc:ucanaccess://C://AutoTestDBBackup//AutoTestDBRestoreBackup//SampleCompany_AutoTest.wed";
        if (exectueType==null) exectueType="1";
        Statement statement=null;
        ResultSet resultSet=null;
        int columnCount=0;
        String strText="";

        String logFileFullName= workingFilePath+"sqlAccessresult_"+getCurrentTimeAndDate()+".csv";

        if (exectueType=="1"){
            try (Connection connection = DriverManager.getConnection(databaseURL)) {

                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
                ResultSetMetaData rsmd = resultSet.getMetaData();
                columnCount = rsmd.getColumnCount();

                if (resultSet.wasNull()) {
                    //SystemLibrary.logMessage(strSQL);
                    SystemLibrary.logMessage("SQL Execution is completed without value return.");
                } else {

                    for (int j = 1; j <= columnCount; j++) {
                        strText = strText + "\"" + rsmd.getColumnName(j) + "\";";
                        // Do stuff with name
                    }
                    strText = strText + "\r\n";

                    while (resultSet.next()) {
                        for (int k = 1; k <= columnCount; k++) {
                            strText = strText + "\"" + resultSet.getString(k) + "\";";
                        }
                        strText = strText + "\r\n";
                    }

                    System.out.println(strText);


                    createTextFile(logFileFullName, strText);
                    if (!SystemLibrary.updateAndValidateStoreStringFile(isUpdateStore, isCompare, logFileFullName, storeFileFullName, expectedContent)){
                        errorCounter++;
                    }
                    /*while (resultSet.next()) {
                        int id = resultSet.getInt("Id");
                        System.out.println("Id is " + id);
                    }*/
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        else if (exectueType=="2"){
            try (Connection connection = DriverManager.getConnection(databaseURL)) {

                statement = connection.createStatement();
                System.out.println(sql);
                //statement.executeUpdate(sql);
                statement.executeUpdate(sql);
            }
            catch (SQLException e) {
                e.printStackTrace();
                errorCounter++;
            }
        }


        if (errorCounter==0) isDone=true;
        return isDone;
    }

    @Test
    public static void test2() throws Exception {
        logMessage("Debug test2");
        String firstName="Paula";
        String lastName="Connors";
        getEmployeeID(firstName, lastName);
    }

    public static void getEmployeeID(String firstName, String lastName) throws Exception {
        String sql="select Employee.Id from Employee where Employee.FirstName='"+firstName+"' and Employee.LastName='"+lastName+"'";
        sqlExecuter_AC(sql, "1", null, null, null, null);

    }


}
