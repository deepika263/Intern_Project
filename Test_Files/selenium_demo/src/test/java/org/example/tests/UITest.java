package org.example.tests;

import org.example.pages.JobsPage;
import org.example.pages.SeekerRegisterPage;
import org.example.pages.WorkerProfilePage;
import org.example.pages.WorkerRegisterPage;
import org.example.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UITest – End-to-End Selenium + TestNG + POM
 * =============================================
 * Concepts demonstrated:
 *  ✅ Page Object Model (POM)  – each page has its own class
 *  ✅ @FindBy annotations      – locators declared in page classes
 *  ✅ PageFactory              – initialises @FindBy fields automatically
 *  ✅ BasePage inheritance     – shared wait/driver logic reused
 *  ✅ TestNG lifecycle         – @BeforeClass / @AfterClass
 *  ✅ @Test with priority      – enforces execution order
 *  ✅ dependsOnMethods         – skips later tests if an earlier one fails
 *  ✅ TestNG Assert            – explicit pass/fail checks
 *  ✅ DriverFactory            – centralised browser setup
 *
 * Flow:
 *  1. Worker registers  → /home
 *  2. Navigate to /workers via sidebar
 *  3. Fill Worker Profile modal, submit  → success alert
 *  4. Logout via sidebar  → /worker-login
 *  5. Seeker registers  → /home
 *  6. Post a Job via /jobs  → card visible
 */
public class UITest {

    private static final String BASE_URL = "http://localhost:4200";

    // ── Test data ────────────────────────────────────────────────────────────
    private static final String WORKER_NAME  = "Ravi Kumar";
    private static final String WORKER_EMAIL = "ravi_ui_02@skillbridge.com";
    private static final String WORKER_PASS  = "Test@1234";
    private static final String WORKER_PHONE = "9876543210";
    private static final String WORKER_LOC   = "Anna Nagar, Chennai";
    private static final String WORKER_EXP   = "3";
    private static final String WORKER_DESC  = "Expert plumber with 3 years experience.";

    private static final String SEEKER_NAME  = "Priya Sharma";
    private static final String SEEKER_EMAIL = "priya_ui_02@skillbridge.com";
    private static final String SEEKER_PASS  = "Test@5678";
    private static final String SEEKER_PHONE = "9123456780";

    private static final String JOB_TITLE    = "Fix kitchen sink";
    private static final String JOB_CATEGORY = "Plumber";
    private static final String JOB_LOCATION = "T Nagar";
    private static final String JOB_PINCODE  = "600017";
    private static final String JOB_BUDGET   = "300-500";

    // ── Driver & Page Objects ────────────────────────────────────────────────
    private WebDriver          driver;
    private WorkerRegisterPage workerRegPage;
    private WorkerProfilePage  workerProfilePage;
    private SeekerRegisterPage seekerRegPage;
    private JobsPage           jobsPage;

    // ════════════════════════════════════════════════════════════════════════
    //  SETUP / TEARDOWN
    // ════════════════════════════════════════════════════════════════════════

    @BeforeClass
    public void setup() {
        driver = DriverFactory.createDriver();

        // Instantiate all Page Objects once – they share the same driver
        workerRegPage     = new WorkerRegisterPage(driver);
        workerProfilePage = new WorkerProfilePage(driver);
        seekerRegPage     = new SeekerRegisterPage(driver);
        jobsPage          = new JobsPage(driver);

        System.out.println("[UI] Browser launched.");
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("[UI] Browser closed.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TEST STEPS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            description = "Worker registers and lands on /home")
    public void test01_WorkerRegistration() {
        workerRegPage.open(BASE_URL)
                .register(WORKER_NAME, WORKER_EMAIL, WORKER_PASS);

        Assert.assertTrue(driver.getCurrentUrl().contains("home"),
                "Expected /home after worker registration");
        System.out.println("[UI] ✔ Worker registered.");
    }

    @Test(priority = 2,
            dependsOnMethods = "test01_WorkerRegistration",
            description = "Navigate to /workers via sidebar")
    public void test02_NavigateToWorkers() {
        workerProfilePage.navigateViaSidebar();

        Assert.assertTrue(driver.getCurrentUrl().contains("workers"),
                "Expected URL to contain 'workers'");
        System.out.println("[UI] ✔ On /workers page.");
    }

    @Test(priority = 3,
            dependsOnMethods = "test02_NavigateToWorkers",
            description = "Fill and submit the Worker Profile modal")
    public void test03_WorkerProfileRegistration() {
        workerProfilePage.fillAndSubmit(
                WORKER_NAME, WORKER_PHONE, WORKER_EMAIL,
                WORKER_LOC, "plumbing", WORKER_EXP, WORKER_DESC);

        Assert.assertTrue(workerProfilePage.isSuccessAlertVisible(),
                "Expected success alert after worker profile registration");
        System.out.println("[UI] ✔ Worker profile registered.");
    }

    @Test(priority = 4,
            dependsOnMethods = "test03_WorkerProfileRegistration",
            description = "Logout via sidebar and land on /worker-login")
    public void test04_Logout() {
        workerProfilePage.logout();

        Assert.assertTrue(driver.getCurrentUrl().contains("worker-login"),
                "Expected URL to contain 'worker-login' after logout");
        System.out.println("[UI] ✔ Logged out.");
    }

    @Test(priority = 5,
            dependsOnMethods = "test04_Logout",
            description = "Seeker registers and lands on /home")
    public void test05_SeekerRegistration() {
        seekerRegPage.open(BASE_URL)
                .register(SEEKER_NAME, SEEKER_EMAIL, SEEKER_PASS);

        Assert.assertTrue(driver.getCurrentUrl().contains("home"),
                "Expected /home after seeker registration");
        System.out.println("[UI] ✔ Seeker registered.");
    }

    @Test(priority = 6,
            dependsOnMethods = "test05_SeekerRegistration",
            description = "Post a Job and verify card appears in job list")
    public void test06_PostJob() {
        jobsPage.navigateViaSidebar()
                .postJob(JOB_TITLE, JOB_CATEGORY, JOB_LOCATION,
                        JOB_PINCODE, JOB_BUDGET, SEEKER_NAME, SEEKER_PHONE);

        Assert.assertTrue(jobsPage.isJobCardVisible(JOB_TITLE),
                "Expected job card with title: " + JOB_TITLE);
        System.out.println("[UI] ✔ Job posted and card visible.");
    }
}