package base;

import utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static void initDriver(String browserName, String headlessParam) {
        String env = ConfigReader.getString("execution_env").trim();
        boolean isHeadless = Boolean.parseBoolean(headlessParam);
        WebDriver driver = null;

        try {
            if (env.equalsIgnoreCase("remote")) {
                // --- REMOTE GRID (Always Normal/Visible on Node) ---
                URL url = new URL(ConfigReader.getString("hubURL"));

                if (browserName.equalsIgnoreCase("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                    driver = new RemoteWebDriver(url, options);
                } else if (browserName.equalsIgnoreCase("edge")) {
                    EdgeOptions options = new EdgeOptions();
                    driver = new RemoteWebDriver(url, options);
                } else if (browserName.equalsIgnoreCase("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    driver = new RemoteWebDriver(url, options);
                }
            } else {
                // --- LOCAL EXECUTION (Headless Controlled by XML) ---
                if (browserName.equalsIgnoreCase("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    if (isHeadless) options.addArguments("--headless=new");
                    options.addArguments("--remote-allow-origins=*");
                    driver = new ChromeDriver(options);
                } else if (browserName.equalsIgnoreCase("edge")) {
                    EdgeOptions options = new EdgeOptions();
                    if (isHeadless) options.addArguments("--headless=new");
                    driver = new EdgeDriver(options);
                } else if (browserName.equalsIgnoreCase("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    if (isHeadless) options.addArguments("-headless");
                    driver = new FirefoxDriver(options);
                }
            }
        } catch (Exception e) {
            System.err.println("Driver initialization failed: " + e.getMessage());
        }

        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().window().maximize();
            tlDriver.set(driver);
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