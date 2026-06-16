package org.example.tests;

import io.restassured.response.Response;
import org.example.api.ApiClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ApiTest – REST Assured + TestNG
 * =================================
 * Concepts demonstrated:
 *  ✅ REST Assured given/when/then BDD style
 *  ✅ POST, GET, DELETE HTTP methods
 *  ✅ Status code assertions
 *  ✅ JSON path extraction  (response.path("field"))
 *  ✅ Response body assertions
 *  ✅ Chained API test flow (create → read → delete)
 *  ✅ Shared state via instance variables (workerApiId, jobApiId)
 *  ✅ dependsOnMethods for ordered API tests
 *
 * NOTE: Run with your Spring Boot backend active on http://localhost:8080
 */
public class ApiTest {

    // ── Shared state (id's returned by create calls) ─────────────────────────
    private long workerApiId;
    private long jobApiId;

    // ── Test data ────────────────────────────────────────────────────────────
    private static final String API_WORKER_EMAIL = "ravi_api_03@skillbridge.com";
    private static final String API_SEEKER_EMAIL = "priya_api_03@skillbridge.com";

    // ════════════════════════════════════════════════════════════════════════
    //  SETUP
    // ════════════════════════════════════════════════════════════════════════

    @BeforeClass
    public void setup() {
        System.out.println("[API] REST Assured tests starting.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  AUTH TESTS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            description = "POST /api/auth/register – register a WORKER and get 200")
    public void api01_RegisterWorker() {
        Response response = ApiClient.registerUser(
                "Ravi Kumar", API_WORKER_EMAIL, "Test@1234", "WORKER");

        Assert.assertEquals(response.statusCode(), 200,
                "Worker registration should return 200");

        // Response should contain a token
        String token = response.path("token");
        Assert.assertNotNull(token, "Token should not be null after registration");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");

        System.out.println("[API] ✔ Worker registered via API. Token received.");
    }

    @Test(priority = 2,
            description = "POST /api/auth/register – register a SEEKER and get 200")
    public void api02_RegisterSeeker() {
        Response response = ApiClient.registerUser(
                "Priya Sharma", API_SEEKER_EMAIL, "Test@5678", "SEEKER");

        Assert.assertEquals(response.statusCode(), 200,
                "Seeker registration should return 200");

        String role = response.path("role");
        Assert.assertEquals(role, "SEEKER", "Role should be SEEKER");

        System.out.println("[API] ✔ Seeker registered via API.");
    }

    @Test(priority = 3,
            dependsOnMethods = "api01_RegisterWorker",
            description = "POST /api/auth/login – login as WORKER and receive JWT")
    public void api03_LoginWorker() {
        String token = ApiClient.login(API_WORKER_EMAIL, "Test@1234");

        Assert.assertNotNull(token, "JWT token must not be null");
        Assert.assertTrue(token.length() > 20, "JWT token looks too short");

        System.out.println("[API] ✔ Worker login returned JWT token.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  WORKER API TESTS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            description = "POST /api/workers – create worker profile and get id back")
    public void api04_CreateWorkerProfile() {
        workerApiId = ApiClient.registerWorkerProfile(
                "Ravi Kumar", "9876543210", API_WORKER_EMAIL,
                "Anna Nagar", "plumbing", 3, "Expert plumber.");

        Assert.assertTrue(workerApiId > 0,
                "Created worker should have a positive id, got: " + workerApiId);

        System.out.println("[API] ✔ Worker profile created. ID = " + workerApiId);
    }

    @Test(priority = 5,
            dependsOnMethods = "api04_CreateWorkerProfile",
            description = "GET /api/workers – list contains at least one worker")
    public void api05_GetAllWorkers() {
        Response response = ApiClient.getAllWorkers();

        Assert.assertEquals(response.statusCode(), 200);

        int count = response.jsonPath().getList("$").size();
        Assert.assertTrue(count > 0, "Worker list should not be empty");

        System.out.println("[API] ✔ GET all workers returned " + count + " workers.");
    }

    @Test(priority = 6,
            dependsOnMethods = "api04_CreateWorkerProfile",
            description = "GET /api/workers/{id} – fetch the worker we just created")
    public void api06_GetWorkerById() {
        Response response = ApiClient.getWorkerById(workerApiId);

        Assert.assertEquals(response.statusCode(), 200,
                "Should find worker with id " + workerApiId);

        String fullName = response.path("fullName");
        Assert.assertEquals(fullName, "Ravi Kumar",
                "fullName should match what we registered");

        System.out.println("[API] ✔ GET worker by ID returned correct name.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  JOB API TESTS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7,
            description = "POST /api/jobs – create a job post and get id back")
    public void api07_CreateJob() {
        jobApiId = ApiClient.createJob(
                "Fix kitchen sink", "PLUMBER",
                "T Nagar", "600017", "300-500",
                "Priya Sharma", "9123456780");

        Assert.assertTrue(jobApiId > 0,
                "Created job should have a positive id, got: " + jobApiId);

        System.out.println("[API] ✔ Job created. ID = " + jobApiId);
    }

    @Test(priority = 8,
            dependsOnMethods = "api07_CreateJob",
            description = "GET /api/jobs – list contains at least one job")
    public void api08_GetAllJobs() {
        Response response = ApiClient.getAllJobs();

        Assert.assertEquals(response.statusCode(), 200);

        int count = response.jsonPath().getList("$").size();
        Assert.assertTrue(count > 0, "Jobs list should not be empty");

        System.out.println("[API] ✔ GET all jobs returned " + count + " jobs.");
    }

    @Test(priority = 9,
            dependsOnMethods = "api07_CreateJob",
            description = "GET /api/jobs/stats – stats object has total > 0")
    public void api09_GetJobStats() {
        Response response = ApiClient.getJobStats();

        Assert.assertEquals(response.statusCode(), 200);

        int total = response.path("total") != null
                ? ((Number) response.path("total")).intValue() : 0;
        Assert.assertTrue(total > 0, "Total jobs in stats should be > 0");

        System.out.println("[API] ✔ Job stats: total = " + total);
    }

    @Test(priority = 10,
            dependsOnMethods = "api07_CreateJob",
            description = "POST /api/jobs/{id}/apply – worker applies to job")
    public void api10_ApplyToJob() {
        Response response = ApiClient.applyToJob(
                jobApiId, "Ravi Kumar", "9876543210", "PLUMBER");

        Assert.assertEquals(response.statusCode(), 200,
                "Apply to job should return 200");

        String status = response.path("status");
        Assert.assertEquals(status, "PENDING",
                "New application status should be PENDING");

        System.out.println("[API] ✔ Applied to job. Status = " + status);
    }

    @Test(priority = 11,
            dependsOnMethods = "api10_ApplyToJob",
            description = "GET /api/jobs/{id}/applicants – applicant list not empty")
    public void api11_GetApplicants() {
        Response response = ApiClient.getApplicants(jobApiId);

        Assert.assertEquals(response.statusCode(), 200);

        int count = response.jsonPath().getList("$").size();
        Assert.assertTrue(count > 0, "Applicant list should not be empty");

        System.out.println("[API] ✔ Applicants list has " + count + " entries.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  REVIEW API TESTS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 12,
            description = "POST /api/reviews – submit a review and get 200")
    public void api12_SubmitReview() {
        Response response = ApiClient.submitReview("Ravi Kumar", 5,
                "Excellent work, fixed the sink perfectly!");

        Assert.assertEquals(response.statusCode(), 200,
                "Submit review should return 200");

        int rating = ((Number) response.path("rating")).intValue();
        Assert.assertEquals(rating, 5, "Rating should be 5");

        System.out.println("[API] ✔ Review submitted. Rating = " + rating);
    }

    @Test(priority = 13,
            description = "GET /api/reviews – review list not empty")
    public void api13_GetAllReviews() {
        Response response = ApiClient.getAllReviews();

        Assert.assertEquals(response.statusCode(), 200);

        int count = response.jsonPath().getList("$").size();
        Assert.assertTrue(count > 0, "Review list should not be empty");

        System.out.println("[API] ✔ Reviews list has " + count + " entries.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CLEANUP – DELETE tests run last
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 14,
            dependsOnMethods = "api06_GetWorkerById",
            description = "DELETE /api/workers/{id} – delete the worker we created")
    public void api14_DeleteWorker() {
        Response response = ApiClient.deleteWorker(workerApiId);

        Assert.assertEquals(response.statusCode(), 200,
                "Delete worker should return 200");

        // Verify worker is gone
        Response getResponse = ApiClient.getWorkerById(workerApiId);
        Assert.assertEquals(getResponse.statusCode(), 404,
                "Worker should not exist after deletion");

        System.out.println("[API] ✔ Worker deleted. 404 confirmed on re-fetch.");
    }
}