package Lib;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static Lib.SystemLibrary.logMessage;

public class ExecuteAutoIT {

    public static String runAutoIT(String strCommand) throws InterruptedException, IOException {
        //AutoItCommand: Exe file path + SPACE + Parramaters
        logMessage("Execute AutoIT with Java/Selenium.");
        Process process=Runtime.getRuntime().exec(strCommand);

        //boolean exitValue=process.waitFor(10, TimeUnit.SECONDS);

        InputStream inputStream=process.getInputStream();
        OutputStream outputStream=process.getOutputStream();

        BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

        String line=null;
        String output=null;

        for (int i=0;i<100;i++){
            output=line;
            line=bufferedReader.readLine();
            if (line==null) break;
        }

        process.destroyForcibly();
        //System.out.println("Exited with code "+exitValue);
        return output;
    }

    public static String runAutoIT_OLD(String AutoITCommand) throws InterruptedException, IOException {
        //AutoItCommand: Exe file path + SPACE + Parramaters
        logMessage("Execute AutoIT with Java/Selenium.");

        Process myProcess=Runtime.getRuntime().exec(AutoITCommand);

        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(myProcess.getInputStream()));

        String line;
        int i=0;
        while((line=bufferedReader.readLine())!=null){
            i++;
            Thread.sleep(10000);
            System.out.println(line);
            break;

        }

        int exitVal= myProcess.waitFor();
        //System.out.println("Exited with code "+exitVal);
        return line;
    }
}
