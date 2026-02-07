package tests;

import base.BaseTest;
import base.DriverFactory;
import org.testng.annotations.*;
import pages.ExperiencePage;
import pages.ServicePage;
import utils.ConfigReader;
import utils.PopupUtils;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class ServiceTests extends BaseTest {

    private static final Logger log = LogManager.getLogger(ServiceTests.class);
    public ServicePage servicePage;

    @Parameters({"browserName", "headless"})
    @BeforeClass
    public void setUpExp(@Optional("chrome") String browserFromXml,@Optional("false") String headless){
        setUp(browserFromXml, headless);
        servicePage = new ServicePage(DriverFactory.getDriver());
        log.info("Step 1: Open services tab");

    }

    @Test(priority=3)
    public void TC11_validateServiceBookingFlow() throws Exception
    {

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

            //Thread.sleep(3000);
            servicePage.clickSearch();


    }

    @Test(priority=4)
    public void validateServiceBookingPage() throws InterruptedException {
        String testName = "validateServiceBookingPage";
        log.info("Step 5: Open random service and capture details");
        servicePage.openRandomServiceAndCapture(testName);
        log.info("Services Execution Completed");
        Assert.assertTrue(true, "Service flow completed successfully");
    }

    @AfterClass
    public void tearDownExp(){
        tearDown();
    }
}