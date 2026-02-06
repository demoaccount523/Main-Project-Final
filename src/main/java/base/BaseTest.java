package base;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import utils.ConfigReader;

public class BaseTest {

//    @Parameters({"browserName", "headless"})
     // ADDED: This ensures TestNG runs this before every test automatically
    public void setUp(String browser, String headless) {
        DriverFactory.initDriver(browser, headless);
        // Ensure baseURL matches your config.properties key
        DriverFactory.getDriver().get(ConfigReader.getString("baseURL"));
    }

     // ADDED: This ensures TestNG cleans up after every test
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}