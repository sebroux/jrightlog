
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.*;

/**
 *
 * @author seb
 */
public class ExcelExport {

    public static void main (String[] args) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File("output.xls"));
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
            workbook.write();
            workbook.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (WriteException ex) {
            Logger.getLogger(ExcelExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}