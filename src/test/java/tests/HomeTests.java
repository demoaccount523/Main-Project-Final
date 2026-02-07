package tests;

import base.BaseTest;
import base.DriverFactory;
import pages.HomePage;
import utils.ConfigReader;
import utils.PopupUtils;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HomeTests extends BaseTest {

    private static final Logger log = LogManager.getLogger(HomeTests.class);
    @Parameters({"browserName", "headless"})
    @Test(priority = 0)
    public void TC01_validateHomeBookingFlow(@Optional("chrome") String browserFromXml , @Optional("false") String headless) {
    	setUp(browserFromXml,headless);

        try {
            HomePage homePage = new HomePage(DriverFactory.getDriver());
            String testName = "validateHomeBookingFlow";
            log.info("Step 1: Verify Home page loads");
            String title = DriverFactory.getDriver().getTitle();
            Assert.assertTrue(title.length() > 0, "Title should not be empty");
            
            PopupUtils.clickGotItIfPresent(DriverFactory.getDriver(), WaitUtils.getPopupWait(DriverFactory.getDriver()));

            log.info("Step 2: Enter city");
            String city = ConfigReader.getString("cityName");
            homePage.enterWhereCity(city);

            log.info("Step 3: Select dates and add guests");
            homePage.openWhen();
            homePage.selectCheckinCheckout(
                ConfigReader.getString("checkinMonth"), ConfigReader.getString("checkinDate"),
                ConfigReader.getString("checkoutMonth"), ConfigReader.getString("checkoutDate")
            );
            homePage.addAdultsClicks(ConfigReader.getInt("homeAdultsClicks"));

            log.info("Step 4: Click Search and handle popups");
            homePage.clickSearch();
            PopupUtils.clickGotItIfPresent(DriverFactory.getDriver(), WaitUtils.getPopupWait(DriverFactory.getDriver()));

            log.info("Step 5: Apply price filters and capture listings");
            String maxPrice = ConfigReader.getString("maxPrice");
            homePage.openFilters();
            homePage.clearAndTypeMaxPrice(maxPrice);
            homePage.applyFiltersOrFallback();
            
            homePage.waitForListings();
            
            // This now matches the method name in your HomePage.java
            int count = homePage.printTop5Listings(testName); 
            
            log.info("Home flow completed. Printed " + count + " listings to console.");

        } finally {
            tearDown();
        }
    }
}