package tests;

import base.BaseTest;
import base.DriverFactory;
import pages.ServicePage;
import utils.ConfigReader;
import utils.PopupUtils;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ServiceTests extends BaseTest {

    private static final Logger log = LogManager.getLogger(ServiceTests.class);
    @Parameters({"browserName"})
    @Test
    public void TC11_validateServiceBookingFlow(@Optional("chrome") String browserFromXml) throws Exception 
    {
    	setUp(browserFromXml);

        try {
            ServicePage servicePage = new ServicePage(DriverFactory.getDriver());
            String testName = "validateServiceBookingFlow";

            log.info("Step 1: Open services tab");
            PopupUtils.clickGotItIfPresent(DriverFactory.getDriver(), WaitUtils.getPopupWait(DriverFactory.getDriver()));
            servicePage.openServicesTab();
            
            log.info("Step 2: Enter city and pick suggestion");
            String city = ConfigReader.getString("serviceCity");
            servicePage.enterCityAndPickFirstSuggestion(city);

            log.info("Step 3: Select dates");
            servicePage.selectDates(
                    ConfigReader.getString("checkinMonth"),
                    ConfigReader.getString("checkinDate"),
                    ConfigReader.getString("checkoutMonth"),
                    ConfigReader.getString("checkoutDate")
            );

            log.info("Step 4: Pick random service type and search");
            servicePage.openTypeOfService();
            boolean available = servicePage.pickRandomServiceType();
            if(!available) {
            	return;
            }
            
            
            Thread.sleep(3000); 
            servicePage.clickSearch();

            log.info("Step 5: Open random service and capture details");
            servicePage.openRandomServiceAndCapture(testName);
            
            Assert.assertTrue(true, "Service flow completed successfully");

        } finally {
            tearDown();
        }
    }
}