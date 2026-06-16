package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * SeekerRegisterPage  (/seeker-register)
 * -----------------------------------------
 * POM for the Seeker Registration form.
 */
public class SeekerRegisterPage extends BasePage {

    @FindBy(css = "input[placeholder='Priya Sharma']")
    private WebElement nameInput;

    @FindBy(css = "input[type='email']")
    private WebElement emailInput;

    @FindBy(css = "input[type='password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[contains(text(),'Register as seeker')]")
    private WebElement registerBtn;

    public SeekerRegisterPage(WebDriver driver) {
        super(driver);
    }

    /** Navigate to the seeker register page */
    public SeekerRegisterPage open(String baseUrl) {
        driver.get(baseUrl + "/seeker-register");
        return this;
    }

    /** Fill and submit the form, wait for redirect to /home */
    public void register(String name, String email, String password) {
        waitVisible(By.cssSelector("input[placeholder='Priya Sharma']"));
        nameInput.sendKeys(name);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        registerBtn.click();
        waitForUrl("home");
    }
}