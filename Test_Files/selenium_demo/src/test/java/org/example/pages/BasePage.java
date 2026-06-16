package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage
 * --------
 * Parent of every Page Object. Holds the shared driver, explicit wait,
 * and common helper methods that all pages can reuse.
 *
 * POM Concept: every page of the app gets its own class that encapsulates
 * its locators and actions.  Tests only call page methods – they never
 * touch raw Selenium calls directly.
 */
public abstract class BasePage {

    protected WebDriver        driver;
    protected WebDriverWait    wait;
    protected JavascriptExecutor js;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js     = (JavascriptExecutor) driver;
        // Initialises @FindBy annotated fields in subclasses
        PageFactory.initElements(driver, this);
    }

    /** Wait until element is visible, then return it. */
    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Scroll element to centre of viewport, then click via JS. */
    protected void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    /** Wait for the URL to contain the given fragment. */
    protected void waitForUrl(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }
}