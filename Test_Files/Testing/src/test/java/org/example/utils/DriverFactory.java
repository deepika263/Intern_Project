package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * DriverFactory – Cross Browser Support
 * ----------------------------------------
 * Creates the correct WebDriver based on the browser name passed in.
 * Browser name comes from testng.xml <parameter name="browser" value="chrome"/>
 * so you never touch Java code to switch browsers.
 *
 * Supported browsers: chrome, firefox, edge
 *
 * Usage in test class:
 *   @Parameters("browser")
 *   @BeforeClass
 *   public void setup(String browser) {
 *       driver = DriverFactory.createDriver(browser);
 *   }
 */
public class DriverFactory {

    private DriverFactory() {}  // utility class – no instantiation

    /**
     * Creates and returns a WebDriver for the given browser name.
     * Browser name is case-insensitive: "Chrome", "CHROME", "chrome" all work.
     *
     * @param browser  browser name from testng.xml parameter
     * @return         configured WebDriver instance
     */
    public static WebDriver createDriver(String browser) {
        switch (browser.trim().toLowerCase()) {

            // ── Chrome ──────────────────────────────────────────────────────
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                // options.addArguments("--headless");  // uncomment for CI
                return new ChromeDriver(options);
            }

            // ── Firefox ──────────────────────────────────────────────────────
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                // options.addArguments("--headless");  // uncomment for CI
                return new FirefoxDriver(options);
            }

            // ── Edge ─────────────────────────────────────────────────────────
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                // options.addArguments("--headless");  // uncomment for CI
                return new EdgeDriver(options);
            }

            // ── Unknown browser ──────────────────────────────────────────────
            default:
                throw new IllegalArgumentException(
                        "Browser not supported: '" + browser + "'. " +
                                "Use: chrome, firefox, or edge");
        }
    }
}