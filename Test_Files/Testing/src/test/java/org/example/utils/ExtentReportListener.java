package org.example.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportListener
 * ---------------------
 * Implements ITestListener so TestNG calls these methods automatically
 * for every @Test in any class that uses this listener.
 *
 * Attached globally via testng.xml — no @Listeners needed on each class.
 *
 * Output: reports/SkillBridge_Report_<timestamp>.html
 *
 * Add to pom.xml:
 * <dependency>
 *     <groupId>com.aventstack</groupId>
 *     <artifactId>extentreports</artifactId>
 *     <version>5.1.1</version>
 * </dependency>
 */
public class ExtentReportListener implements ITestListener {

    // ── One ExtentReports instance shared across all tests ───────────────────
    private static ExtentReports extent;

    // ── One ExtentTest per @Test method – ThreadLocal for thread safety ───────
    private static ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    // ════════════════════════════════════════════════════════════════════════
    //  Suite level – create report file before anything runs
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onStart(ITestContext context) {
        // Timestamp the report file so old reports aren't overwritten
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        String reportPath = "reports/SkillBridge_Report_" + timestamp + ".html";

        // ── Configure the HTML reporter ──────────────────────────────────────
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("SkillBridge Test Report");
        spark.config().setReportName("SkillBridge – Full Test Suite");
        spark.config().setTheme(Theme.DARK);           // DARK or STANDARD
        spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");
        spark.config().setEncoding("UTF-8");

        // ── Create the ExtentReports instance ────────────────────────────────
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // ── System info shown in report sidebar ──────────────────────────────
        extent.setSystemInfo("Application",  "SkillBridge");
        extent.setSystemInfo("Environment",  "http://localhost:4200");
        extent.setSystemInfo("API URL",      "http://localhost:3000");
        extent.setSystemInfo("Browser",      "Chrome");
        extent.setSystemInfo("OS",           System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Tester",       System.getProperty("user.name"));

        System.out.println("[Report] Suite started – report will be saved to: " + reportPath);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Suite level – flush (write) the report after everything finishes
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            System.out.println("[Report] Report written to reports/ folder.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Test level – called before each @Test
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestStart(ITestResult result) {
        // Create a test node in the report for this @Test method
        String testName        = result.getName();
        String testDescription = result.getMethod().getDescription();

        ExtentTest test = extent.createTest(
                testName,
                testDescription.isEmpty() ? testName : testDescription
        );

        // Tag which class this test belongs to (API Tests / UI Tests)
        test.assignCategory(result.getTestClass().getName()
                .replace("org.example.tests.", ""));

        // Store in ThreadLocal so onTestSuccess/Failure can access it
        testNode.set(test);
        System.out.println("[Report] Started: " + testName);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Test level – PASS
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestSuccess(ITestResult result) {
        testNode.get()
                .log(Status.PASS, "✔ Test passed: " + result.getName());
        System.out.println("[Report] PASSED: " + result.getName());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Test level – FAIL
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();

        // Log the failure with the full exception stack trace
        test.log(Status.FAIL, "✘ Test failed: " + result.getName());
        test.log(Status.FAIL, result.getThrowable());

        System.out.println("[Report] FAILED: " + result.getName()
                + " → " + result.getThrowable().getMessage());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Test level – SKIP (dependency failed)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();

        if (test == null) {
            // Test was skipped before onTestStart was called
            test = extent.createTest(result.getName());
            testNode.set(test);
        }

        test.log(Status.SKIP, "⚠ Test skipped: " + result.getName());

        // Show WHY it was skipped (usually a dependency failure)
        if (result.getThrowable() != null) {
            test.log(Status.SKIP, result.getThrowable().getMessage());
        }

        System.out.println("[Report] SKIPPED: " + result.getName());
    }
}