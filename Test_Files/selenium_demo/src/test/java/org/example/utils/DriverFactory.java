package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * DriverFactory
 * -------------
 * Centralises ChromeDriver creation. Every test class calls
 * DriverFactory.createDriver() so browser config lives in one place.
 */
public class DriverFactory {

    private DriverFactory() {}   // utility class – no instantiation

    public static WebDriver createDriver() {
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");   // uncomment for CI
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        return new ChromeDriver(options);
    }
}