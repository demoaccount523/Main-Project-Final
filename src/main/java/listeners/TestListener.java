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

public class TestListener implements ITestListener {
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;
    // Keeping ThreadLocal to ensure parallel execution works correctly
    private static ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        // Video Step: Initialize SparkReporter [01:07:31]
    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "Test-Report-" + timeStamp + ".html";

        // 2. Define the report location with the dynamic name
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/" + reportName);

        // Video Step: Configure UI [01:08:40]
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Functional Testing");
        sparkReporter.config().setTheme(Theme.STANDARD);

        // Video Step: Set System Info [01:10:01]
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Computer Name", "LocalHost");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester Name", "Tarun");
        
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create test and store in ThreadLocal [Same as video logic, but safe]
        test = extent.createTest(result.getName());
        tlTest.set(test);
        tlTest.get().info("Test Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Log status using ThreadLocal get() [01:12:01]
        tlTest.get().log(Status.PASS, "Test Case PASSED is: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Log failure and error details [01:14:27]
        tlTest.get().log(Status.FAIL, "Test Case FAILED is: " + result.getName());
        tlTest.get().log(Status.FAIL, "Test Case FAILED cause is: " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Log skip status [01:15:11]
        tlTest.get().log(Status.SKIP, "Test Case SKIPPED is: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Write report [01:15:43]
        extent.flush();
    }
}