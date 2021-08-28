package TestNG;

import Lib.WebAPILib;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static Lib.SystemLibrary.logMessage;

public class WebAPITest {

    @Test
    //Books TestNG Web API
    public void testBooksAPI() throws InterruptedException {
        logMessage("Start Web API test.");
        String url="http://restapi.demoqa.com/utilities/books/getallbooks";
        System.out.println("The URL is :"+url);
        RestAssured.baseURI=url;

        /*
        RequestSpecification httpRequest=RestAssured.given().
                header("ID", "TestNG ID").
                header("NAME", "TestNG Name").
                header("Title", "TestNG Title");
        */

        RequestSpecification httpRequest=RestAssured.given();
        Response response=httpRequest.request(Method.GET, "/getallbooks");

        int responseStatusCode=response.statusCode();
        System.out.println("resonseStatusCode="+responseStatusCode);

        String responseStatusLine=response.getStatusLine();
        System.out.println("The response status line is below:\n"+responseStatusLine);
        System.out.println("");

        Headers responseHeaders=response.getHeaders();
        System.out.println("The respone Header is below:\n"+responseHeaders.toString());
        System.out.println("");

        System.out.println("Loop Header...");
        for (Header header: responseHeaders){

            System.out.println(header.getName()+": "+header.getValue());
        }
        System.out.println("");

        String responseBody=response.getBody().asString();
        System.out.println("Resonse body is below:\n"+responseBody);

        System.out.println("");
        JsonPath jsonPath=response.jsonPath();
        List<String> allBookTitles=jsonPath.getList("books.title");


        int  i=1;
        for (String bookTitle: allBookTitles){
            i++;
            System.out.println(i+": "+bookTitle);
        }

        logMessage("End of Web API TestNG.");

    }

    @Test
    public void TestCoreWebAPI1() throws Exception {
        logMessage("Start Testing .Net Core Web API....");

        //Lib.WebAPILib.processWebAPI_Main(11, 11);
        Lib.WebAPILib.processWebAPI_Main(21, 21);

        logMessage("End of Testing .Net Core Web API.");
    }

    @Test
    public void TestCoreWebAPI2() throws IOException, InterruptedException {
        logMessage("Start Testing .Net Core Web API....");
        String baseURL="http://localhost:52159/api/products";
        //String baseURL="http://sagecorewebtest1.azurewebsites.net/api/products";

        String responseCodeExpected="200";
        String responseCodeLineExpected="";
        String responseHeaderExpected="";
        String responseBodayExpected="";
        String jsonPathSearch="";
        String jsonPathSearchExpected="";
        String updateStore="";
        String validateResult="1";


        //Lib.WebAPILib.processWebAPI(baseURL, "ID:TestID;Name:TestNG Name;Title:TestNG Title", 1, responseCodeExpected, responseCodeLineExpected, responseHeaderExpected, responseBodayExpected, jsonPathSearch, jsonPathSearchExpected, updateStore, validateResult);
        logMessage("End of Testing .Net Core Web API.");
    }

    @Test
    public void testHarshMap(){
        HashMap<String, String> hash_map = new HashMap<String, String>();

        hash_map.put("name1", "john");
        hash_map.put("name2", "marry");
        System.out.println(hash_map);

        System.out.println("name2= "+hash_map.get("marry"));
        System.out.println("Is HashMap empty? "+hash_map.isEmpty());
        hash_map.remove("name1");

        System.out.println(hash_map);
        System.out.println("Size of the HashMap: "+hash_map.size());
    }


    @Test (threadPoolSize = 10, invocationCount = 10, timeOut = 60000)
    public void ESSWebAPIDemoTest() throws  Exception{
        logMessage("*** Start ESS Web API Demo test.");

        Long id=Thread.currentThread().getId();
        logMessage(String.valueOf(id));
        WebAPILib.processWebAPI_Main(301, 301);

        logMessage("*** End of ESS Web API Demo test.");

    }

    @Test
    public void ESSWebAPIDemoTest1() throws  Exception{
        logMessage("*** Start ESS Web API Demo test.");

        //WebAPILib.processWebAPI_Main(201, 201);
        WebAPILib.processWebAPI_Main(10001, 10001);

        logMessage("*** End of ESS Web API Demo test.");

    }


}
