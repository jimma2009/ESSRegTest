package Lib;


import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
//import org.openqa.selenium.WebDriver;
import org.testng.Assert;
//import org.testng.asserts.Assertion;
//import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import java.io.IOException;
import java.util.List;
//import java.util.Map;

import static Lib.SystemLibrary.*;

public class WebAPILib {
    public static RequestSpecification setupRequestHeader(String headerString, RequestSpecification myRS) throws InterruptedException {
        //HashMap<String, String> hash_map = new HashMap<String, String>();
        String[] headerGroup=SystemLibrary.splitString(headerString, ";");
        String[] headerSub=null;
        String headerName;
        String headerValue;
        logMessage("--- Start adding the follow header into request.");
        for (int i=0;i<headerGroup.length;i++){

            //System.out.println(headerGroup[i]);
            headerSub=SystemLibrary.splitString(headerGroup[i], ":");
            headerName=headerSub[0];
            headerValue=headerSub[1];
            myRS.given().header(headerSub[0], headerSub[1]);
            logMessage("Header: '"+headerName+"' with value '"+headerValue+"' is added.");
        }
        logMessage("--- End of adding the follow header into request.");
        return myRS;
    }

    public static String processWebAPI(String baseURL, String strBasePath, String headerString, String jsonStringPost, String jsonFilePost, int requestMethod, String responseCodeExpected, String responseCodeLineExpected, String responseHeaderFieldName, String responseHeaderValueExpected, String responseBodyExpected, String jsonPathSearch, String jsonPathSearchExpected, String updateStore, String validateResult, String returnParra) throws Exception {

        logMessage("--- Start processing Web API...");
        int errorCounter=0;
        //SoftAssert myAssert=new SoftAssert();
        String returnValue=null;

        RestAssured myRestAssured=new RestAssured();
        myRestAssured.baseURI=baseURL;

        if (strBasePath==null) strBasePath="";
        myRestAssured.basePath=strBasePath;

        RequestSpecification myRS=myRestAssured.given();

        ////////////////////// Set up Request Header /////////////////////
        if (headerString!=null){
            Lib.WebAPILib.setupRequestHeader(headerString, myRS);
        }

        if (jsonStringPost!=null){
            myRS.contentType("application/json");
            myRS.body(jsonStringPost);
        }

        ///////////////////// Set up Request Method ///////////////////
        Method method=Method.GET;  //Default method is GET
        switch (requestMethod){
            case 1:
                method=Method.GET;
                break;
            case 2:
                method=Method.POST;
                break;
            case 3:
        }

        Response response=myRS.request(method);
        long responseTime=response.time();

        logMessage("It takes "+String.valueOf(responseTime)+" milliseconds to get response.");

        /**
         * Handle response status code
         */


        //////////////////// Validate Reponse Status Code //////////////////////
        logMessage("--- Start Validating Response Status Code.");
        if (responseCodeExpected!=null){
            String currentStatusCode=String.valueOf(response.statusCode());
            logMessage("Current Response status code: "+currentStatusCode);
            Assert.assertEquals(currentStatusCode, responseCodeExpected);
        }
        logMessage("--- End of Validating Response Status Code.");

        /////////////////// Validate Response Code Line ///////////////////////
        logMessage("--- Start Validating Response Code Line.");
        //Handle respone Status line
        if (responseCodeLineExpected!=null) {
            String currentResponseStatusLine = response.getStatusLine();

            String logFileName = "responseCodeLine_" + getCurrentTimeAndDate() + ".txt";
            String logFileFullName = workingFilePath + logFileName;

            logMessage("Current Reposne Status Line: "+currentResponseStatusLine);
            SystemLibrary.createTextFile(logFileFullName, currentResponseStatusLine);

            String logURLAddress = serverUrlAddress + "TestLog//WorkingFile//" + logFileName;
            System.out.println("Click here to access the file: " + logURLAddress);

            SystemLibrary.updateAndValidateStoreStringFile(updateStore, validateResult,logFileFullName, responseCodeLineExpected);

        }
        logMessage("--- End of Validating Response Code Line.");


        ///////////////////////////// Handle Response Header  /////////////////////
        logMessage("--- Start Validating Response Header.");
        Headers responseHeaders=response.getHeaders();
        String strResponseHeader=responseHeaders.toString();
        logMessage("The respone Header is below:\n"+strResponseHeader);

        if (responseHeaderFieldName!=null){

            String outputHeaderValue ="";
            int totalHeaderItemfound=0;

            //System.out.println("Loop Header...");
            for (Header header: responseHeaders){
                if (responseHeaderFieldName.contains(header.getName())){
                    outputHeaderValue=outputHeaderValue+header.getName()+": "+header.getValue()+"\n";
                    totalHeaderItemfound++;
                }
            }

            String displayMessage="There are total "+totalHeaderItemfound+" header item found.";
            outputHeaderValue=outputHeaderValue+"\n"+displayMessage;

            String logFileName = "responseHeaderValue_" + getCurrentTimeAndDate() + ".txt";
            String logFileFullName = workingFilePath + logFileName;
            SystemLibrary.createTextFile(logFileFullName, outputHeaderValue);

            String logURLAddress = serverUrlAddress + "TestLog//WorkingFile//" + logFileName;
            System.out.println("Click here to access the file: " + logURLAddress);

            logMessage("The current Header value is below:\n"+outputHeaderValue);

            updateAndValidateStoreStringFile(updateStore, validateResult, logFileFullName, responseHeaderValueExpected);
        }
        logMessage("--- End of Validating Response Header.");

        ////////////////////// Handle Response Body /////////////////
        logMessage("--- Start Validating Response Body - Json.");
        String responseBody=response.getBody().asString();
        System.out.println("Resonse body is below:");
        System.out.println("*****************************");
        System.out.println(responseBody);
        System.out.println("*****************************");

        if (responseBodyExpected!=null){
            String logFileName = "responseBody_" + getCurrentTimeAndDate() + ".txt";
            String logFileFullName = workingFilePath + logFileName;
            SystemLibrary.createTextFile(logFileFullName, responseBody);

            String logURLAddress = serverUrlAddress + "TestLog//WorkingFile//" + logFileName;
            System.out.println("Click here to access the file: " + logURLAddress);

            updateAndValidateStoreStringFile(updateStore, validateResult, logFileFullName, responseBodyExpected);
        }
        logMessage("--- End of Validating Response Body - Json.");

        //////////////////// Handle Response Body Jath Path //////////////////
        if (jsonPathSearch!=null)
        {
            logMessage("--- Start Validating Response Body - Json Path.");

            JsonPath myJsonPath=response.jsonPath();
            logMessage("---- The Current Json Path Search Result is below:");
            String currentJsonPathSearchResult=processJsonPath(myJsonPath, jsonPathSearch);

            String logFileName = "jsonPathSearch_" + getCurrentTimeAndDate() + ".txt";
            String logFileFullName = workingFilePath + logFileName;
            SystemLibrary.createTextFile(logFileFullName, currentJsonPathSearchResult);

            String logURLAddress = serverUrlAddress + "TestLog//WorkingFile//" + logFileName;
            System.out.println("Click here to access the file: " + logURLAddress);

            updateAndValidateStoreStringFile(updateStore, validateResult, logFileFullName, jsonPathSearchExpected);

            logMessage("--- End of Validating Repsone Body - Json Path.");
        }

        ////////////////////// Handle Return Parrra //////////////////////
        if (returnParra!=null){
            JsonPath myJsonPath=response.jsonPath();
            returnValue=getSingleValueFromJson(myJsonPath, returnParra);
            logMessage("A single return value from Json Path:\""+returnParra+"\" is "+returnValue);
        }

        logMessage("--- End of processing Web API.");
        return returnValue;
    }

    public static String getSingleValueFromJson(JsonPath jsonPath, String returnParra){
        String[] myString=splitString(returnParra, ";");
        String fieldName=myString[0];
        int index=Integer.parseInt(myString[1]);

        List<String> myLists=jsonPath.getList(fieldName);
        String returnParraValue=myLists.get(index);

        return returnParraValue;
    }

    public static String processJsonPath(JsonPath jsonPath, String jsonPathSearch){

        String[] jsonPathSearchItems=SystemLibrary.splitString(jsonPathSearch, ";");
        String strQueryResult="";

        for (int j=0;j<jsonPathSearchItems.length;j++){
            strQueryResult=strQueryResult+"-------------------------\n";
            strQueryResult=strQueryResult+"Json Path Search String: "+jsonPathSearchItems[j]+"\n";
            List<String> myLists=jsonPath.getList(jsonPathSearchItems[j]);

            int  i=0;
            for (String listItem: myLists){

                //System.out.println(i+": "+listItem);
                strQueryResult=strQueryResult+i+": "+listItem+"\n";
                i++;
            }
        }

        System.out.println(strQueryResult);

        return strQueryResult;
    }



    public static String[] processWebAPI_Main(int startSerialNo, int endSerialNo) throws Exception {
        SystemLibrary.logMessage("*** Start processing multi Web API from row " + startSerialNo + " to " + endSerialNo +" in ProcessWebAPI sheet.");
        String[] returnValue=new String[100];
        int serialNo=0;
        String[][] cellData=SystemLibraryHigh.readDataFromDatasheet(startSerialNo, endSerialNo,30,"ProcessWebAPI");

        for (int i = 0; i < cellData.length; i++) {
            if (!Strings.isNullOrEmpty(cellData[i][0])) {
                serialNo = Integer.parseInt(cellData[i][0]);
                if (Strings.isNullOrEmpty(cellData[i][1])) {
                    returnValue[i]=processWebAPI(cellData[i][2], cellData[i][3], cellData[i][4], cellData[i][5], cellData[i][6], Integer.parseInt(cellData[i][7]), cellData[i][8], cellData[i][9], cellData[i][10], cellData[i][11], cellData[i][12], cellData[i][13], cellData[i][14], cellData[i][15], cellData[i][16], cellData[i][17]);
                } else {
                    System.out.println("Row " + serialNo + " is ignored.");
                }
                if (serialNo>=endSerialNo) break;
            }
        }
        SystemLibrary.logMessage("*** End of processing Multi Web API.");
        return returnValue;
    }
}
