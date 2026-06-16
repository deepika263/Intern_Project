package org.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * WorkerRegisterPage  (/worker-register)
 * ----------------------------------------
 * POM for the Worker Registration form.
 * @FindBy annotations declare locators; actions are exposed as clean methods.
 */
public class WorkerRegisterPage extends BasePage {

    // ── Locators via @FindBy ─────────────────────────────────────────────────
    @FindBy(css = "input[placeholder='Ravi Kumar']")
    private WebElement nameInput;

    @FindBy(css = "input[type='email']")
    private WebElement emailInput;

    @FindBy(css = "input[type='password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[contains(text(),'Register as worker')]")
    private WebElement registerBtn;

    // ── Constructor ──────────────────────────────────────────────────────────
    public WorkerRegisterPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /** Navigate to the page */
    public WorkerRegisterPage open(String baseUrl) {
        driver.get(baseUrl + "/worker-register");
        return this;
    }

    /** Fill and submit the registration form, then wait for /home */
    public void register(String name, String email, String password) {
        waitVisible(org.openqa.selenium.By.cssSelector("input[placeholder='Ravi Kumar']"));
        nameInput.sendKeys(name);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        registerBtn.click();
        waitForUrl("home");
    }
}