package Lib;

import org.testng.annotations.Test;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import static Lib.SystemLibrary.logMessage;

public class ESSSoftAssert extends SoftAssert {

    private boolean isPassed=true;


    @Override
    public void onAssertFailure(IAssert assertCommand, AssertionError ex) {
        String currentItemNo=SystemLibrary.vl.itemNo;
        String curretnTestSerialNo=SystemLibrary.vl.serialNo;
        String currentItemName=SystemLibrary.vl.itemName;


        System.out.println("The Test '"+currentItemNo+"' is failed..");
        isPassed=false;
        try {
            DBManage.logTestResultIntoDB_Main(currentItemNo, "fail", currentItemName, curretnTestSerialNo);
        }
        catch (InterruptedException e){
            SystemLibrary.logError(e.toString());
        }
        catch (Exception e){
            SystemLibrary.logError(e.toString());
        }
        isPassed=true;
    }

    @Override
    public void onAssertSuccess(IAssert assertCommand){
        String currentItemNo=SystemLibrary.vl.itemNo;
        String curretnTestSerialNo=SystemLibrary.vl.serialNo;
        String currentItemName=SystemLibrary.vl.itemName;

        if (isPassed){
            System.out.println("The Test '"+currentItemNo+"' is passed.");
            try {
                DBManage.logTestResultIntoDB_Main(currentItemNo, "pass", currentItemName, curretnTestSerialNo);
            }
            catch (InterruptedException e){
                SystemLibrary.logError(e.toString());
            }
            catch (Exception e){
                SystemLibrary.logError(e.toString());
            }
        }
        isPassed=true;
   }

   //////////////// Debug here /////////////////



}
