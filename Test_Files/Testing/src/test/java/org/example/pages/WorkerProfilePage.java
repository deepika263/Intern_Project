package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * WorkerProfilePage  (/workers)
 * --------------------------------
 * POM for the Worker Registration (profile) page.
 * Handles navigating via sidebar, opening the modal, filling it, and submitting.
 */
public class WorkerProfilePage extends BasePage {

    // ── Sidebar ──────────────────────────────────────────────────────────────
    @FindBy(xpath = "//span[@class='nav-label' and text()='Worker Registration']")
    private WebElement sidebarLink;

    // ── Register for a Job button ────────────────────────────────────────────
    @FindBy(xpath = "//button[contains(text(),'Register for a Job')]")
    private WebElement registerForJobBtn;

    // ── Modal form fields ────────────────────────────────────────────────────
    @FindBy(css = "input[formControlName='fullName']")
    private WebElement fullNameInput;

    @FindBy(css = "input[formControlName='phone']")
    private WebElement phoneInput;

    @FindBy(css = "input[formControlName='email']")
    private WebElement emailInput;

    @FindBy(css = "input[formControlName='location']")
    private WebElement locationInput;

    @FindBy(css = "select[formControlName='jobType']")
    private WebElement jobTypeSelect;

    @FindBy(css = "input[formControlName='experience']")
    private WebElement experienceInput;

    @FindBy(css = "textarea[formControlName='description']")
    private WebElement descriptionInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitBtn;

    // ── Success alert ────────────────────────────────────────────────────────
    @FindBy(css = ".alert-success")
    private WebElement successAlert;

    // ── Logout ────────────────────────────────────────────────────────────────
    @FindBy(xpath = "//a[contains(@class,'logout')]")
    private WebElement logoutLink;

    // ── Constructor ──────────────────────────────────────────────────────────
    public WorkerProfilePage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /** Click the sidebar "Worker Registration" link */
    public WorkerProfilePage navigateViaSidebar() {
        waitVisible(By.xpath("//span[@class='nav-label' and text()='Worker Registration']")).click();
        waitForUrl("workers");
        return this;
    }

    /** Open the registration modal and fill all fields */
    public WorkerProfilePage fillAndSubmit(String name, String phone, String email,
                                           String location, String jobType,
                                           String experience, String description) {
        waitVisible(By.xpath("//button[contains(text(),'Register for a Job')]")).click();

        waitVisible(By.cssSelector("input[formControlName='fullName']"));
        fullNameInput.sendKeys(name);
        phoneInput.sendKeys(phone);
        emailInput.sendKeys(email);
        locationInput.sendKeys(location);
        new Select(jobTypeSelect).selectByValue(jobType);
        experienceInput.sendKeys(experience);
        descriptionInput.sendKeys(description);
        submitBtn.click();

        // Wait for success alert
        waitVisible(By.cssSelector(".alert-success"));
        return this;
    }

    /** Click the sidebar Logout link */
    public void logout() {
        waitVisible(By.xpath("//a[contains(@class,'logout')]")).click();
        waitForUrl("worker-login");
    }

    /** Is the success alert displayed? */
    public boolean isSuccessAlertVisible() {
        return successAlert.isDisplayed();
    }
}