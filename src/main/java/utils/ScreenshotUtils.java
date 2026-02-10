package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    public static String capture(WebDriver driver, String testName) 
    {
        try {
            // 1. WAIT FOR PAGE TO BE FULLY LOADED
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(d -> ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete"));

            // 2. CREATE DIRECTORIES (Safety Step)
            // Path.of ensures the folders "reports/screenshots" exist in your project root
            Files.createDirectories(Path.of(System.getProperty("user.dir"), "reports", "screenshots"));

            // 3. GENERATE FILENAME AND PATH
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss_SSS"));
            String destination = System.getProperty("user.dir") + "/reports/screenshots/" + testName + "_" + ts + ".png";

            // 4. CAPTURE AND COPY
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File(destination));
            
            return destination;
        } 
        catch (Exception e) 
        {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}