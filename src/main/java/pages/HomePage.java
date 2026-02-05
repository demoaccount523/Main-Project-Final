package pages;

import utils.DateSelectionUtils1;
import utils.ExcelUtils;
import utils.ScreenshotUtils;
//import utils.ScreenshotUtils;
import utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitUtils.getWait(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "search-block-tab-STAYS")
    private WebElement staysTab;

    @FindBy(xpath = "//div[text()='Where']/parent::div")
    private WebElement whereContainer;

    @FindBy(xpath = "//div[text()='Where']/following-sibling::div/input")
    private WebElement whereInput;

    @FindBy(xpath = "//div[@class='f67r5k6 atm_mk_h2mmj6 atm_l8_1ieir7h atm_wq_cs5v99 atm_vy_1osqo2v atm_jb_idpfg4 atm_ks_zryt35 dir dir-ltr']")
    private WebElement whenContainer;

    @FindBy(xpath = "//div[text()='Who']")
    private WebElement whoContainer;

    @FindBy(xpath = "(//span[contains(@class,'atm_e2_qslrf5 ')])[2]")
    private WebElement adultsPlusBtn;

    @FindBy(xpath = "//div[text()='Search']")
    private WebElement searchBtn;

    @FindBy(xpath = "//button[@data-testid='category-bar-filter-button']")
    private WebElement filterBtn;

    @FindBy(xpath = "//input[@id='price_filter_max']")
    private WebElement maxPriceInput;

    @FindBy(xpath = "//div[@class='p1y54pk8 atm_7l_85zwdx dir dir-ltr']//a")
    private WebElement applyFiltersLink;

    private final By titlesBy = By.xpath("//div[contains(@class,'gsgwcjk ')]/div//div[@data-testid='listing-card-title']");
    private final By pricesBy = By.xpath("//span[@class='u1opajno atm_7l_1dmvgf5 atm_cs_bs05t3 atm_rd_us8791 atm_rq_glywfm atm_cs_l3jtxx__1v156lz dir dir-ltr']");

    public void openStaysTabIfPossible() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(staysTab)).click();
        } catch (Exception ignored) {}
    }

    public void enterWhereCity(String city) {
        wait.until(ExpectedConditions.visibilityOf(whereContainer)).click();
        wait.until(ExpectedConditions.visibilityOf(whereInput)).sendKeys(city);
    }

    public void openWhen() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(whenContainer)).click();
        } catch (Exception e) {
            try {
                WebElement checkIn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@aria-label,'Check-in')]")));
                checkIn.click();
            } catch (Exception ignored) {}
        }
    }

    public void selectCheckinCheckout(String checkinMonth, String checkinDate, String checkoutMonth, String checkoutDate) {
        DateSelectionUtils1.selectDate(driver, wait, checkinMonth, checkinDate);
        DateSelectionUtils1.selectDate(driver, wait, checkoutMonth, checkoutDate);
    }

    public void addAdultsClicks(int clicks) {
        wait.until(ExpectedConditions.elementToBeClickable(whoContainer)).click();
        for (int i = 0; i < clicks; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(adultsPlusBtn)).click();
        }
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
    }

    public void openFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(filterBtn)).click();
    }

    public void clearAndTypeMaxPrice(String value) {
        clearAndType(driver, maxPriceInput, value);
    }

    public void applyFiltersOrFallback() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(applyFiltersLink)).click();
        } catch (Exception e) {
            try {
                WebElement showBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Show')]")));
                showBtn.click();
            } catch (Exception e2) {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            }
        }
    }

    public void waitForListings() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(titlesBy));
        waitForCountToStabilize(driver, titlesBy, Duration.ofSeconds(8));
    }

    /**
     * Captures and prints the top 5 listings and logs them to Excel.
     */
    public int printTop5Listings(String testName) {
        ScreenshotUtils.capture(driver, testName);
        List<WebElement> titles = driver.findElements(titlesBy);
        List<WebElement> prices = driver.findElements(pricesBy);

        int limit = Math.min(5, Math.min(titles.size(), prices.size()));
        
        for (int i = 0; i < limit; i++) {
            try {
                String name = titles.get(i).getText();
                String price = prices.get(i).getText();
                System.out.println("Listing " + (i + 1) + ": " + name + " - " + price);
                
                // --- THE FIX: LOG EACH ROW TO EXCEL IMMEDIATELY ---
                ExcelUtils.writeRow("Stays", name, price);
                
            } catch (StaleElementReferenceException sere) {
                // Refresh list and retry once if stale
                titles = driver.findElements(titlesBy);
                prices = driver.findElements(pricesBy);
                i--;
            }
        }
        return limit;
    }

    private static void waitForCountToStabilize(WebDriver driver, By by, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        int lastCount = -1;
        int stableTicks = 0;

        while (System.currentTimeMillis() < end) {
            int count = driver.findElements(by).size();
            if (count > 0 && count == lastCount) {
                stableTicks++;
                if (stableTicks >= 3) return;
            } else {
                stableTicks = 0;
            }
            lastCount = count;
        }
    }

    private static void clearAndType(WebDriver driver, WebElement input, String value) 
    {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            input.click();
            String selectAll = System.getProperty("os.name").toLowerCase().contains("mac")
                    ? Keys.chord(Keys.COMMAND, "a")
                    : Keys.chord(Keys.CONTROL, "a");

            input.sendKeys(selectAll);
            input.sendKeys(Keys.DELETE);

            String current = input.getAttribute("value");
            if (current != null && !current.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value=''; arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", input);
            }
            input.sendKeys(value);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
                    input, value
            );
        }
    }
    
    // Logic for HomePage.java - You can keep this as a helper, but it's now called inside printTop5Listings
    public void captureStaysToExcel() {
        List<WebElement> titles = driver.findElements(titlesBy);
        List<WebElement> prices = driver.findElements(pricesBy);
        int count = Math.min(5, titles.size());

        for (int i = 0; i < count; i++) {
            ExcelUtils.writeRow("Stays", titles.get(i).getText(), prices.get(i).getText());
        }
    }
}