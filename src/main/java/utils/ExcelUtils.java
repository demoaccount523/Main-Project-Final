package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelUtils {
    private static Workbook workbook = new XSSFWorkbook();
    private static String filePath;

    // This block runs automatically the moment you use ExcelUtils.writeRow()
    static {
        try {
            
            String folderPath = System.getProperty("user.dir") + "/reports/ExcelOutputs";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            // 2. Create File Path with Timestamp
            String date = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
            filePath = folderPath + "/TestResults_" + date + ".xlsx";

            // 3. Create Sheets with Headers
            createSheet("Stays", new String[]{"S.No", "Name", "Price"});
            createSheet("Experiences", new String[]{"S.No", "Name", "Price", "Host", "Location"});
            createSheet("Services", new String[]{"S.No", "Name", "Price", "Rating"});
            
            save(); // Save the initial file
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createSheet(String name, String[] headers) {
        Sheet sheet = workbook.createSheet(name);
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    public static void writeRow(String sheetName, String... data) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet != null) {
            int rowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum); // S.No
            
            for (int i = 0; i < data.length; i++) {
                row.createCell(i + 1).setCellValue(data[i]);
            }
            save();
        }
    }

    private static void save() {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        } catch (Exception e) {
            System.err.println("Excel Save Error: " + e.getMessage());
        }
    }
}