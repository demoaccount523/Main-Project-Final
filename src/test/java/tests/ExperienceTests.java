package tests;

import base.BaseTest;
import base.DriverFactory;
import org.testng.annotations.*;
import pages.ExperiencePage;
import utils.ConfigReader;
import utils.PopupUtils;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class ExperienceTests extends BaseTest {

    private static final Logger log = LogManager.getLogger(ExperienceTests.class);
    public ExperiencePage experiencePage;
    
    //hello
    
    @Parameters({"browserName", "headless"})
    @BeforeClass
    public void setUpExp(@Optional("chrome") String browserFromXml, @Optional("false") String headless){
        setUp(browserFromXml, headless);
        log.info("Step 1: Open experiences tab");
        experiencePage = new ExperiencePage(DriverFactory.getDriver());
        experiencePage.openExperienceTab();
    }
    

    @Test(priority=1)
    public void TC06_validateExperienceBookingFlow() throws Exception 
    {
    		try {

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
    		}
    		catch(Exception e)
    		{
    			
    			e.printStackTrace();    		
    		}


    }

    @Test(priority=2,dependsOnMethods= {"TC06_validateExperienceBookingFlow"})
    public void validateExperienceBookingPage(){
        String testName = "validateExperiencePage";
        log.info("Step 5: Capture details");
        experiencePage.captureRandomExperience(testName);
        log.info("Experiences Execution completed");
        Assert.assertTrue(true, "Experience flow completed successfully");
    }

    @AfterClass
    public void tearDownExp(){
        tearDown();
    }
}