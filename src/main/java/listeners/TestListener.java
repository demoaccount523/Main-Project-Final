package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener 
{
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;
    
    
    private static ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();

    public void onStart(ITestContext context) 
    {
      	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "Test-Report-" + timeStamp + ".html";

        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/ExtentReports/" + reportName);

        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Functional Testing");
        sparkReporter.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Computer Name", "LocalHost");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester Name", "Tarun");
        
    }

    public void onTestStart(ITestResult result) 
    {
        
        test = extent.createTest(result.getName());
        tlTest.set(test);
        tlTest.get().info("Test Started");
    }

    public void onTestSuccess(ITestResult result) 
    {
        
        tlTest.get().log(Status.PASS, "Test Case PASSED is: " + result.getName());
    }

    public void onTestFailure(ITestResult result) 
    {
       
        tlTest.get().log(Status.FAIL, "Test Case FAILED is: " + result.getName());
        tlTest.get().log(Status.FAIL, "Test Case FAILED cause is: " + result.getThrowable());
    }

    public void onTestSkipped(ITestResult result) 
    {
        tlTest.get().log(Status.SKIP, "Test Case SKIPPED is: " + result.getName());
    }

    public void onFinish(ITestContext context) 
    {
           extent.flush();
    }
}