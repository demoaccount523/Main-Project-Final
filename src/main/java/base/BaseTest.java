package base;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import utils.ConfigReader;

public class BaseTest 
{

    public void setUp(String browser, String headless) 
    {
        DriverFactory.initDriver(browser, headless);
        DriverFactory.getDriver().get(ConfigReader.getString("baseURL"));
    }

     
    public void tearDown() 
    {
        DriverFactory.quitDriver();
    }
}