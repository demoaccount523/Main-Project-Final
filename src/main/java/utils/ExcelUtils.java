package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtils {
    private static Workbook workbook;
    private static String filePath;
    private static Map<String, Sheet> sheetMap = new HashMap<>();

    static {
        try {
            // Use System.getProperty("user.dir") to get the absolute project root
            String projectPath = System.getProperty("user.dir");
            File reportsDir = new File(projectPath + File.separator + "reports");

            // Ensure the directory exists physically
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            filePath = reportsDir.getAbsolutePath() + File.separator + "TestResults_" + timestamp + ".xlsx";

            System.out.println("Excel Path: " + filePath);

            workbook = new XSSFWorkbook();
            createSheetWithHeaders("Stays", new String[]{"S.No", "Name", "Price"});
            createSheetWithHeaders("Experiences", new String[]{"S.No", "Name", "Price", "Host", "Location"});
            createSheetWithHeaders("Services", new String[]{"S.No", "Name", "Price", "Rating"});
            
            // Force save immediately so the file is created even if tests fail later
            save(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createSheetWithHeaders(String sheetName, String[] headers) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        sheetMap.put(sheetName, sheet);
    }

    public static synchronized void writeRow(String sheetName, String... data) {
        Sheet sheet = sheetMap.get(sheetName);
        if (sheet != null) {
            int lastRow = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRow + 1);
            newRow.createCell(0).setCellValue(lastRow + 1);

            for (int i = 0; i < data.length; i++) {
                newRow.createCell(i + 1).setCellValue(data[i]);
            }
            save(); // Save after every write to ensure data is on disk
        }
    }

    private static void save() {
        // Using FileOutputStream with the absolute path from user.dir
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            fileOut.flush();
        } catch (IOException e) {
            System.err.println("Failed to save Excel file: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}