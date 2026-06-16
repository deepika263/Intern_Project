package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * JobsPage  (/jobs)
 * -------------------
 * POM for the Jobs page. Covers navigating via sidebar,
 * posting a job, and verifying the posted card.
 */
public class JobsPage extends BasePage {

    // ── Sidebar ──────────────────────────────────────────────────────────────
    @FindBy(xpath = "//span[@class='nav-label' and text()='Job Registration']")
    private WebElement sidebarLink;

    // ── Post a Job button ────────────────────────────────────────────────────
    @FindBy(xpath = "//button[contains(text(),'Post a Job')]")
    private WebElement postJobBtn;

    // ── Form fields ──────────────────────────────────────────────────────────
    @FindBy(css = "input[placeholder='e.g. Fix kitchen sink']")
    private WebElement titleInput;

    @FindBy(css = ".form-card select")
    private WebElement categorySelect;

    @FindBy(css = "input[placeholder='e.g. Anna Nagar']")
    private WebElement locationInput;

    @FindBy(css = "input[placeholder='600001']")
    private WebElement pincodeInput;

    @FindBy(css = "input[placeholder='e.g. ₹300-500']")
    private WebElement budgetInput;

    @FindBy(css = "input[placeholder='Your name']")
    private WebElement postedByInput;

    @FindBy(css = "input[placeholder='Your phone number']")
    private WebElement phoneInput;

    @FindBy(xpath = "//button[contains(text(),'Post Job')]")
    private WebElement submitBtn;

    // ── Constructor ──────────────────────────────────────────────────────────
    public JobsPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /** Click "Job Registration" in the sidebar */
    public JobsPage navigateViaSidebar() {
        waitVisible(By.xpath("//span[@class='nav-label' and text()='Job Registration']")).click();
        waitForUrl("jobs");
        return this;
    }

    /** Post a job and wait for the job card to appear in the list */
    public void postJob(String title, String category, String location,
                        String pincode, String budget, String name, String phone) {
        waitVisible(By.xpath("//button[contains(text(),'Post a Job')]")).click();

        waitVisible(By.cssSelector("input[placeholder='e.g. Fix kitchen sink']"));
        titleInput.sendKeys(title);
        new Select(categorySelect).selectByVisibleText(category);
        locationInput.sendKeys(location);
        pincodeInput.sendKeys(pincode);
        budgetInput.sendKeys(budget);
        postedByInput.sendKeys(name);
        phoneInput.sendKeys(phone);

        // JS click to avoid interception by overlapping sidebar
        jsClick(submitBtn);

        // Form closes
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .invisibilityOfElementLocated(By.xpath("//h3[text()='Post a new job']")));

        // Job card appears
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + title + "')]")));
    }

    /** Returns true when a card with the given title is visible */
    public boolean isJobCardVisible(String title) {
        try {
            return waitVisible(By.xpath("//*[contains(text(),'" + title + "')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}