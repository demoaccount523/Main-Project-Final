package pages;

import utils.DateSelectionUtils1;
import utils.ExcelUtils;
import utils.ScreenshotUtils;
import utils.WaitUtils;
import utils.WindowUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.util.*;

public class ExperiencePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ExperiencePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitUtils.getWait(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "search-block-tab-EXPERIENCES")
    private WebElement experiencesTab;

    @FindBy(xpath = "//input[@placeholder='Search by city or landmark']")
    private WebElement whereInput;

    @FindBy(xpath = "(//b[@class='b1viecjw atm_cs_14spzga dir dir-ltr'])[1]")
    private WebElement firstSuggestion;

    @FindBy(xpath = "//div[text()='Who']")
    private WebElement who;

    @FindBy(xpath = "//div[text()='Search']")
    private WebElement search;

    public void openExperienceTab() {
        wait.until(ExpectedConditions.elementToBeClickable(experiencesTab)).click();
    }

    public void enterCityAndPickSuggestion(String city) throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOf(whereInput)).sendKeys(city);
        Thread.sleep(3000); 
        wait.until(ExpectedConditions.visibilityOf(firstSuggestion)).click();
    }

    public void selectDates(String checkinMonth, String checkinDate, String checkoutMonth, String checkoutDate) {
        DateSelectionUtils1.selectDate(driver, wait, checkinMonth, checkinDate);
        DateSelectionUtils1.selectDate(driver, wait, checkoutMonth, checkoutDate);
    }

    public void setStepperValue(String stepperType, int targetValue) {
        WebElement valueElement = driver.findElement(By.xpath("//span[@data-testid='stepper-" + stepperType + "-value']"));
        WebElement plusButton = driver.findElement(By.xpath("//button[@data-testid='stepper-" + stepperType + "-increase-button']"));
        WebElement minusButton = driver.findElement(By.xpath("//button[@data-testid='stepper-" + stepperType + "-decrease-button']"));

        int currentValue = Integer.parseInt(valueElement.getText());
        int diff = targetValue - currentValue;

        if (diff > 0) {
            for (int i = 0; i < diff; i++) plusButton.click();
        } else if (diff < 0) {
            for (int i = 0; i < Math.abs(diff); i++) minusButton.click();
        }
    }

    public void openWhoAndSetGuests(int adults, int children, int infants) {
        wait.until(ExpectedConditions.elementToBeClickable(who)).click();
        setStepperValue("adults", adults);
        setStepperValue("children", children);
        setStepperValue("infants", infants);
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(search)).click();
    }

    public boolean isNoExactMatchesPresent() {
        try {
            WebElement noMatches = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'No exact matches')]")));
            return noMatches.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void captureRandomExperience(String testName) {
        
        By GFF = By.xpath("(//div[contains(@class,'c14whb16')])[1]//a/following-sibling::div/div[2]/div[1]");
        By LCH = By.xpath("(//div[contains(@class,'c14whb16')])[2]//a/following-sibling::div/div[2]/div[1]");
        By Explore = By.xpath("//div[contains(@class,'g16uu4ny ')]//a/following-sibling::div/div[2]/div[1]");

        By activeBlock = null;

        if (!driver.findElements(GFF).isEmpty() && driver.findElement(GFF).isDisplayed()) {
            activeBlock = GFF;
        } else if (!driver.findElements(LCH).isEmpty() && driver.findElement(LCH).isDisplayed()) {
            activeBlock = LCH;
        } else if (!driver.findElements(Explore).isEmpty() && driver.findElement(Explore).isDisplayed()) {
            activeBlock = Explore;
        }

        List<WebElement> titles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(activeBlock));

        int i = 1;
        HashMap<Integer, String> map = new HashMap<>();
        for (WebElement t : titles) {
            map.put(i++, t.getText());
        }

        int p = (int) (Math.random() * (i - 1)) + 1;
        String title = map.get(p);

        System.out.println("Chosen Place: " + title);

        WebElement fixplace = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(".//div[@data-testid='listing-card-title' and normalize-space()=\"" + title + "\"]/ancestor::div/a")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fixplace);

        // MODIFIED: Switch to new window WITHOUT closing the old one to maintain driver stability
        utils.WindowUtils.waitAndSwitchToNewWindow(driver, wait, 2);

        String heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1"))).getText();
        String host = driver.findElement(By.xpath("//div[contains(text(),'Hosted by')]")).getText();
        
        // Location Xpath (Using a more stable version of your xpath)
        String location = "Not Found";
        try {
            WebElement locele = driver.findElement(By.xpath("//div[@data-section-id='LOCATION_DEFAULT']//h2/following-sibling::div/div"));
            location = locele.getText();
        } catch (Exception e) {
            // Fallback to your specific class xpath if the above fails
            location = driver.findElement(By.xpath("(//div[contains(@class,'toa59ve')])[2]")).getText();
        }
        
        System.out.println("Place: " + heading);
        System.out.println("Host: " + host);
        System.out.println("Location: " + location);

        List<WebElement> tasks = driver.findElements(By.xpath("//div/button[contains(@class,'l1ovpqvx')]/div/div[2]/h3"));
        for (int tIndex = 0; tIndex < tasks.size(); tIndex++) {
            System.out.println("Task " + (tIndex + 1) + ": " + tasks.get(tIndex).getText());
        }
        
        ScreenshotUtils.capture(driver, testName);

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class='d7gzrif atm_cs_1mexzig dir dir-ltr'])[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();

        WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[text()='Total']/parent::div/following-sibling::div")
        ));

        String finalPrice = price.getText();
        System.out.println("Total INR: " + finalPrice);

        // Log to Excel
        logExperienceToExcel(heading, finalPrice, host, location);
    }
    
    public void logExperienceToExcel(String name, String price, String host, String location) {
        ExcelUtils.writeRow("Experiences", name, price, host, location);
    }
}