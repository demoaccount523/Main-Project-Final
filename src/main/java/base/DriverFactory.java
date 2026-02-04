package base;

import utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static void initDriver(String browserName) {
        // Fallback to config if parameter is null
        if (browserName == null || browserName.trim().isEmpty()) {
            browserName = ConfigReader.getString("browserName");
        }

        String env = ConfigReader.getString("execution_env");
        WebDriver driver = null;

        try {
            if (env != null && env.equalsIgnoreCase("remote")) {
                // --- REMOTE GRID EXECUTION ---
                DesiredCapabilities caps = new DesiredCapabilities();
                URL url = new URL(ConfigReader.getString("hubURL"));

                switch (browserName.toLowerCase()) {
                    case "chrome":
                        ChromeOptions chOptions = new ChromeOptions();
                        chOptions.addArguments("--no-sandbox", "--disable-gpu");
                        driver = new RemoteWebDriver(url, chOptions);
                        break;
                    case "edge":
                        EdgeOptions edOptions = new EdgeOptions();
                        driver = new RemoteWebDriver(url, edOptions);
                        break;
                    case "firefox":
                        FirefoxOptions ffOptions = new FirefoxOptions();
                        driver = new RemoteWebDriver(url, ffOptions);
                        break;
                    default:
                        throw new RuntimeException("Browser " + browserName + " not supported on Grid");
                }
            } else {
                // --- LOCAL EXECUTION ---
                switch (browserName.toLowerCase()) {
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("--remote-allow-origins=*");
                        chromeOptions.addArguments("--disable-gpu", "--no-sandbox");
                        // Fixed: Explicitly creating ChromeDriver with options
                        driver = new ChromeDriver(chromeOptions);
                        break;
                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        edgeOptions.addArguments("--remote-allow-origins=*");
                        driver = new EdgeDriver(edgeOptions);
                        break;
                    case "firefox":
                        driver = new FirefoxDriver();
                        break;
                    default:
                        // Default fallback to Chrome if something is wrong
                        driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Driver initialization failed: " + e.getMessage());
            e.printStackTrace();
        }

        if (driver != null) {
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            tlDriver.set(driver);
        } else {
            throw new RuntimeException("WebDriver instance is null. Check browser name and executable.");
        }
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove();
        }
    }
}