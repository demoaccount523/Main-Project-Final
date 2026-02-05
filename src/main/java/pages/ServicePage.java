package pages;

import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import utils.*;

//import utils.ScreenshotUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class ServicePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ServicePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitUtils.getWait(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//a[@id='search-block-tab-SERVICES']")
    private WebElement servicesTab;

    @FindBy(xpath = "//div[text()='Where']/following-sibling::div/input")
    private WebElement whereInput;

//    @FindBy(id = "bigsearch-query-location-suggestion-0")
//    private WebElement firstLocationSuggestion;
//    @FindBy(xpath = "//div[@role='option' and contains(., '"+ ConfigReader.getString("serviceCity") +"')]")
//    private WebElement firstLocationSuggestion;

    @FindBy(xpath = "//div[text()='Type of service']")
    private WebElement typeOfService;

    @FindBy(xpath = "//div[text()='Search']")
    private WebElement search;

    // Element representing the selected service type text to use in the catch block
    private String selectedServiceType = "Unknown Service";

    public void openServicesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(servicesTab)).click();
    }

    public void enterCityAndPickFirstSuggestion(String city) throws InterruptedException {
        WebElement w = wait.until(ExpectedConditions.visibilityOf(whereInput));
        w.click();
        w.sendKeys(city);
        //Thread.sleep(3000);
        WebElement firstLocationSuggestion=driver.findElement(By.xpath("//div[@role='option' and contains(., '"+ ConfigReader.getString("serviceCity")+"')]"));

        wait.until(ExpectedConditions.visibilityOf(firstLocationSuggestion)).click();
    }

    public void selectDates(String checkinMonth, String checkinDate, String checkoutMonth, String checkoutDate) {
        DateSelectionUtils1.selectDate(driver, wait, checkinMonth, checkinDate);
        DateSelectionUtils1.selectDate(driver, wait, checkoutMonth, checkoutDate);
    }

    public void openTypeOfService() {
        wait.until(ExpectedConditions.elementToBeClickable(typeOfService)).click();
    }

    public boolean pickRandomServiceType() {
        try {
            List<WebElement> serv = driver.findElements(By.xpath("//button[not(@disabled)][contains(@id,'service-type-item-service_type_tag')]"));
            if (!serv.isEmpty()) {
                int randomNum = new Random().nextInt(Math.min(10, serv.size()));
                WebElement choice = serv.get(randomNum);
                // Store the name of the service (e.g., Photography) for the bug report
                this.selectedServiceType = choice.getText(); 
                choice.click();
            }
            
            else {
                System.out.println("No Services available in this Location yet.");
                return false;
            }
            
        } catch (Exception ignored) {
            
        }
        return true;
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(search)).click();
    }

    public void openRandomServiceAndCapture(String testName) throws InterruptedException {

        try {

            By listingCard = By.xpath("//div[contains(@class,'g1sqkrme ')]/div");
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(listingCard));

            // Check if there are no results before trying to click a random listing
            List<WebElement> results = driver.findElements(listingCard);
            
            if (results.isEmpty()) {
                // If the list is empty, throw an exception to trigger the catch block
                throw new NoSuchElementException("No results found");
            }

            int randomNum2 = new Random().nextInt(Math.min(10, results.size()));
            Set<String> oldWindows = driver.getWindowHandles();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Services')]")));
            wait.until(ExpectedConditions.elementToBeClickable(results.get(randomNum2))).click();
            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));

            String current = driver.getWindowHandle();
            driver.close();
            for (String w : driver.getWindowHandles()) {
                if (!w.equals(current)) {
                    driver.switchTo().window(w);
                    break;
                }
            }
            ScreenshotUtils.capture(driver, testName);
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1"))).getText();
            System.out.println("Service Title: " + title);
            
            String rating = "N/A";
            try {
                rating = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'s1dlmy8j ')]/parent::span"))).getText();
                System.out.println("Rating: " + rating);
            } catch (Exception ignored) {}

            String bill = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(@class,'p1wsu7fi ')][1]"))).getText();
            System.out.println("Starting price: " + bill);

            // --- THE FIX: CALL THE EXCEL LOGGING METHOD HERE ---
            logServiceToExcel(title, bill, rating);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0,document.body.scrollHeight);");

        } catch (Exception e) {
            // BUG REPORTING LOGIC
            System.out.println("--------------------------------------------------");
            System.out.println("BUG DETECTED: There is nothing in " + selectedServiceType + 
                               " but the key was enabled so I pressed it but nothing is there in the next page so it is a bug.");
            System.out.println("--------------------------------------------------");
            e.printStackTrace();
            // Capture screenshot of the empty results page
           // ScreenshotUtils.capture(driver, testName + "_EmptyResultsBug");
        }
    }
    
 // Logic for ServicePage.java
    public void logServiceToExcel(String title, String price, String rating) {
        ExcelUtils.writeRow("Services", title, price, rating);
    }
}