package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DateSelectionUtils1 {

    /**
     * Reusable method for date selection based on target Month and Date
     */
    public static void selectDate(WebDriver driver, WebDriverWait wait, String targetMonth, String targetDate) {
        
        while (true) {
            // Get current month header using your specified XPath
            WebElement monthHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@style, 'AtomicCalendar-HorizontalScroller')]//h2")));
            
            String currentMonth = monthHeader.getText();

            if (currentMonth.equalsIgnoreCase(targetMonth))
            {
                try
                {
                    // Using your exact XPath for the date button
                     WebElement dateElement=driver.findElement(By.xpath("//button[contains(@aria-label, '"+targetDate+"') and contains(@aria-label, '"+targetMonth+"')]"));   
                            
                    dateElement.click();
                    return; // Successfully clicked, exit method
                }
                catch (StaleElementReferenceException ignored)
                {
                    // Element went away, loop again to re-find
                }
            }
            else
            {
                // Click next button using your specified XPath
                WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[contains(@class,'l1ovpqvx atm_npmupv_14b5rvc_10sa') and @type='button' and contains(@aria-label,'Move forward')])")));
                
                nextBtn.click();
                
                try
                {
                    // Wait for the old header to disappear to ensure the page has transitioned
                    wait.until(ExpectedConditions.stalenessOf(monthHeader));
                }
                catch (Exception ignored)
                {
                    // Transitioning logic
                }
            }
        }
    }
}