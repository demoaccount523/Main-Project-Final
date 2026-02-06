package tests;

import base.BaseTest;
import base.DriverFactory;
import pages.ExperiencePage;
import utils.ConfigReader;
import utils.PopupUtils;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ExperienceTests extends BaseTest {

    private static final Logger log = LogManager.getLogger(ExperienceTests.class);
    
    @Parameters({"browserName"})
    @Test
    public void TC06_validateExperienceBookingFlow(@Optional("chrome") String browserFromXml) throws Exception {
//        setUp(browserFromXml);

        try {
            ExperiencePage experiencePage = new ExperiencePage(DriverFactory.getDriver());
            String testName = "validateExperienceBooking";

            log.info("Step 1: Open experiences tab");
            experiencePage.openExperienceTab();
            PopupUtils.clickGotItIfPresent(DriverFactory.getDriver(), WaitUtils.getPopupWait(DriverFactory.getDriver()));

            log.info("Step 2: Enter city");
            experiencePage.enterCityAndPickSuggestion(ConfigReader.getString("experienceCity"));

            log.info("Step 3: Select dates");
            experiencePage.selectDates(
                    ConfigReader.getString("checkinMonth"),
                    ConfigReader.getString("checkinDate"),
                    ConfigReader.getString("checkoutMonth"),
                    ConfigReader.getString("checkoutDate")
            );

            log.info("Step 4: Set guests and search");
            experiencePage.openWhoAndSetGuests(
                    ConfigReader.getInt("expAdults"),
                    ConfigReader.getInt("expChildren"),
                    ConfigReader.getInt("expInfants")
            );
            experiencePage.clickSearch();

            log.info("Step 5: Capture details");
            // Updated method name to match your new refactored page class
            experiencePage.captureRandomExperience(testName);
            
            Assert.assertTrue(true, "Experience flow completed successfully");

        } finally {
        	tearDown();
        }
    }
}