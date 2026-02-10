package pages;

import utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import java.util.*;

public class ExperiencePage 
{

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ExperiencePage(WebDriver driver) 
    {
        this.driver = driver;
        this.wait = WaitUtils.getWait(driver);
        PageFactory.initElements(driver, this);
    }

    // --- Page Factory Elements ---

    @FindBy(id = "search-block-tab-EXPERIENCES")
    private WebElement experiencesTab;

    @FindBy(xpath = "//input[@placeholder='Search by city or landmark']")
    private WebElement whereInput;

    @FindBy(xpath = "//div[text()='Who']")
    private WebElement whoDropdown;

    @FindBy(xpath = "//div[text()='Search']")
    private WebElement searchButton;

    @FindBy(xpath = "//*[contains(text(),'No exact matches')]")
    private WebElement noMatchesMessage;

    @FindBy(xpath = "//h1")
    private WebElement detailPageHeading;

    @FindBy(xpath = "//div[contains(text(),'Hosted by')]")
    private WebElement hostLabel;

    @FindBy(xpath = "//div[@data-section-id='LOCATION_DEFAULT']//h2/following-sibling::div/div")
    private WebElement primaryLocation;

    @FindBy(xpath = "(//div[contains(@class,'toa59ve')])[2]")
    private WebElement secondaryLocation;

    @FindBy(xpath = "//div/button[contains(@class,'l1ovpqvx')]/div/div[2]/h3")
    private List<WebElement> taskItems;

    @FindBy(xpath = "(//div[@class='d7gzrif atm_cs_1mexzig dir dir-ltr'])[1]")
    private WebElement availabilityButton;

    @FindBy(xpath = "//div[text()='Total']/parent::div/following-sibling::div")
    private WebElement totalPrice;

    // --- Actions ---

    public void openExperienceTab() 
    {
        wait.until(ExpectedConditions.elementToBeClickable(experiencesTab)).click();
    }

    public void enterCityAndPickSuggestion(String city) 
    {
        wait.until(ExpectedConditions.visibilityOf(whereInput)).sendKeys(city);
        String dynamicXpath = "//div[contains(@role, 'option') and contains(., '" + ConfigReader.getString("experienceCity") + "')]";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath))).click();
    }

    public void selectDates(String checkinMonth, String checkinDate, String checkoutMonth, String checkoutDate) 
    {
        DateSelectionUtils1.selectDate(driver, wait, checkinMonth, checkinDate);
        DateSelectionUtils1.selectDate(driver, wait, checkoutMonth, checkoutDate);
    }

    public void openWhoAndSetGuests(int adults, int children, int infants) 
    {
        wait.until(ExpectedConditions.elementToBeClickable(whoDropdown)).click();
        setStepperValue("adults", adults);
        setStepperValue("children", children);
        setStepperValue("infants", infants);
    }

    private void setStepperValue(String stepperType, int targetValue) 
    {
        WebElement valueElement = driver.findElement(By.xpath("//span[@data-testid='stepper-" + stepperType + "-value']"));
        WebElement plusButton = driver.findElement(By.xpath("//button[@data-testid='stepper-" + stepperType + "-increase-button']"));
        WebElement minusButton = driver.findElement(By.xpath("//button[@data-testid='stepper-" + stepperType + "-decrease-button']"));

        int currentValue = Integer.parseInt(valueElement.getText());
        int diff = targetValue - currentValue;

        for (int i = 0; i < Math.abs(diff); i++) 
        {
            if (diff > 0) plusButton.click();
            else minusButton.click();
        }
    }

    public void clickSearch() 
    {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public boolean isNoExactMatchesPresent() 
    {
        try {
            return noMatchesMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void captureRandomExperience(String testName) 
    {
        // Find which layout is currently visible
        By activeBlock = determineActiveLayout();
        List<WebElement> titles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(activeBlock));

        // Select a random title
        Random random = new Random();
        WebElement randomTitle = titles.get(random.nextInt(titles.size()));
        String chosenTitle = randomTitle.getText();
        System.out.println("Chosen Place: " + chosenTitle);

        // Click on chosen place
        String titleXpath = ".//div[@data-testid='listing-card-title' and normalize-space()='" + chosenTitle + "']/ancestor::div/a";
        WebElement resultLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(titleXpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resultLink);

        WindowUtils.waitAndSwitchToNewWindow(driver, wait, 2);

        // Scrape Details
        String heading = wait.until(ExpectedConditions.visibilityOf(detailPageHeading)).getText();
        String host = hostLabel.getText();
        String location = getSafeLocation();

        System.out.println("Place: " + heading + " | Host: " + host + " | Location: " + location);

        // Print Tasks
        for (WebElement t : taskItems) 
        {
            System.out.println("Task: " + t.getText());
        }


        ScreenshotUtils.capture(driver, testName);

        // Handle Price logic
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", availabilityButton);
        availabilityButton.click();

        String finalPrice = wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText();
        System.out.println("Total INR: " + finalPrice);

        ExcelUtils.writeRow("Experiences", heading, finalPrice, host, location);
    }

    private By determineActiveLayout() 
    {
        By layoutOne = By.xpath("(//div[contains(@class,'c14whb16')])[1]//a/following-sibling::div/div[2]/div[1]");
        By layoutTwo = By.xpath("(//div[contains(@class,'c14whb16')])[2]//a/following-sibling::div/div[2]/div[1]");
        By layoutThree = By.xpath("//div[contains(@class,'g16uu4ny ')]//a/following-sibling::div/div[2]/div[1]");

        if (isElementVisible(layoutOne)) return layoutOne;
        if (isElementVisible(layoutTwo)) return layoutTwo;
        return layoutThree;
    }

    private boolean isElementVisible(By locator) 
    {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    private String getSafeLocation() 
    {
        try 
        {
            return primaryLocation.getText();
        } catch (Exception e) 
        {
            try { return secondaryLocation.getText(); } 
            catch (Exception ex) { return "Not Found"; }
        }
    }
}