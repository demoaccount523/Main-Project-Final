//package base;
//
//import utils.ConfigReader;
//import org.openqa.selenium.WebDriver;
//
//public class BaseTest {
//    public void startDriver(String browserName) 
//    {
//        DriverFactory.initDriver(browserName);
//        DriverFactory.getDriver().get(ConfigReader.getString("baseURL"));
//    }
//
//    public void stopDriver() 
//    {
//        DriverFactory.quitDriver();

package base;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import utils.ConfigReader;

public class BaseTest {

    //@Parameters({"browserName"}) // This matches the parameter name in testng.xml
    public void setUp(String browser) {
        // Start driver with the browser passed from XML
        DriverFactory.initDriver(browser);
        DriverFactory.getDriver().get(ConfigReader.getString("baseURL"));
    }

    
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}