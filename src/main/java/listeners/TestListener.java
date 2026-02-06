package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import base.DriverFactory;
import utils.ExtentManager;
//import utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.*;

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ExtentManager.getExtent();
    private static final ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();

    public void onTestStart(ITestResult result) {
        String browser = result.getTestContext().getCurrentXmlTest().getParameter("browserName");
        ExtentTest test = extent.createTest(result.getMethod().getMethodName() + " (" + browser + ")");
        tlTest.set(test);
        tlTest.get().info("Test Started on browser: " + browser);
    }

    public void onTestSuccess(ITestResult result)
    {
        tlTest.get().pass("Test Passed");
    }

    public void onTestFailure(ITestResult result) 
    {
        WebDriver driver = DriverFactory.getDriver();
        tlTest.get().fail("Test Failed");
        
        // Safety check: if DriverFactory fails, attempt to get it from the instance
        if (driver == null) 
        {
            driver = (WebDriver) result.getTestContext().getAttribute("WebDriver");
        }

    }

    public void onTestSkipped(ITestResult result) {
        if (tlTest.get() != null) {
            tlTest.get().skip("Test Skipped: " + result.getThrowable());
        }
    }

    public void onFinish(ITestContext context) {
        extent.flush();
    }
}