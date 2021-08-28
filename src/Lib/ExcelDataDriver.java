
package Lib;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;

/**
 * Created by MPTester25 on 08/05/2017.
 */
public class ExcelDataDriver {
    //private XSSFSheet ExcelWSheet;
    // private XSSFWorkbook ExcelWBook;
    //XSSFWorkbook
    //HSSFWorkbook

    private HSSFSheet ExcelWSheet;
    private HSSFWorkbook ExcelWBook;

    //Constructor to connect to the Excel with sheetname and Path
    public ExcelDataDriver(String Path, String sheetName) throws Exception {

        try {
            // Open the Excel file
            FileInputStream ExcelFile = new FileInputStream(Path);

            // Access the required test data sheet
            //ExcelWBook = new XSSFWorkbook(ExcelFile);
            ExcelWBook = new HSSFWorkbook(ExcelFile);
            ExcelWSheet = ExcelWBook.getSheet(sheetName);
        }catch (Exception e){
            throw (e);
        }
    }

    //This method is to set the rowcount of the excel.
    public int excel_get_rows() throws Exception {

        try{
            return ExcelWSheet.getPhysicalNumberOfRows();
        }catch (Exception e){
            throw (e);
        }
    }

    //This method to get the data and get the value as strings.
    public String getCellDataasstring(int RowNum, int ColNum) throws Exception {

        try {
            String cellData = ExcelWSheet.getRow(RowNum).getCell(ColNum).getStringCellValue();
            //System.out.println("The value of CellData " + CellData);
            if (cellData.equals("")) cellData=null;
            return cellData;
        }catch (Exception e) {
            //return "Errors in Getting Cell Data";
            return null;
        }
    }

    //This method to get the data and get the value as number.
    public double getCellDataasnumber(int RowNum, int ColNum) throws Exception {

        try {
            double CellData = ExcelWSheet.getRow(RowNum).getCell(ColNum).getNumericCellValue();
            //System.out.println("The value of CellData " + CellData);
            return CellData;
        }catch (Exception e){
            return -999999.00;
        }
    }
}
