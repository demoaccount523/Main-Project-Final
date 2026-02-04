package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PopupUtils {

    // Same XPath from your code
    private static final By GOT_IT_BTN = By.xpath("//button[text()='Got it']");

    public static void clickGotItIfPresent(WebDriver driver, WebDriverWait popupWait) {
        try {
            popupWait.until(ExpectedConditions.visibilityOfElementLocated(GOT_IT_BTN)).click();
        } catch (Exception ignored) {
        }
    }
}
