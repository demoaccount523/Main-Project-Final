package utils;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WindowUtils {

    public static void switchToNewWindowAndCloseOld(WebDriver driver, WebDriverWait wait) {
        String current = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();

        // close old (as you did)
        driver.close();

        for (String h : handles) {
            if (!h.equals(current)) {
                driver.switchTo().window(h);
                break;
            }
        }
    }

    public static void waitAndSwitchToNewWindow(WebDriver driver, WebDriverWait wait, int expectedWindows) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(expectedWindows));
        List<String> winList = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(winList.get(winList.size() - 1));
    }
}
