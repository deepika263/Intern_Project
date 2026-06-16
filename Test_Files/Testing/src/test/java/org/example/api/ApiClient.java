package org.example.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * ApiClient
 * ---------
 * REST Assured wrapper for all SkillBridge backend API calls.
 *
 * FIX: Spring Boot CORS config only allows Origin: http://localhost:4200
 * REST Assured sends no Origin by default → 403 Forbidden.
 * Solution: every request goes through base() which adds the correct Origin header.
 */
public class ApiClient {

    private static final String BASE_URI = "http://localhost:3000";
    private static final String ALLOWED_ORIGIN = "http://localhost:4200";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    /**
     * Base request spec with the Origin header that satisfies Spring Boot CORS.
     * Every method in this class uses base() instead of given() directly.
     */
    private static RequestSpecification base() {
        return given().header("Origin", ALLOWED_ORIGIN);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  AUTH
    // ════════════════════════════════════════════════════════════════════════

    public static Response registerUser(String name, String email,
                                        String password, String role) {
        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                name, email, password, role);

        return base()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register")
                .then()
                .extract().response();
    }

    public static String login(String email, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        return base()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  WORKERS
    // ════════════════════════════════════════════════════════════════════════

    public static Response getAllWorkers() {
        return base()
                .when()
                .get("/api/workers")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static Response getWorkerById(long id) {
        return base()
                .pathParam("id", id)
                .when()
                .get("/api/workers/{id}")
                .then()
                .extract().response();
    }

    public static long registerWorkerProfile(String fullName, String phone,
                                             String email, String location,
                                             String jobType, int experience,
                                             String description) {
        String body = String.format(
                "{\"fullName\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\"," +
                        "\"location\":\"%s\",\"jobType\":\"%s\",\"experience\":%d," +
                        "\"description\":\"%s\"}",
                fullName, phone, email, location, jobType, experience, description);

        return ((Number) base()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/workers")
                .then()
                .statusCode(200)
                .extract()
                .path("id")).longValue();
    }

    public static Response deleteWorker(long id) {
        return base()
                .pathParam("id", id)
                .when()
                .delete("/api/workers/{id}")
                .then()
                .extract().response();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  JOBS
    // ════════════════════════════════════════════════════════════════════════

    public static Response getAllJobs() {
        return base()
                .when()
                .get("/api/jobs")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static Response getJobStats() {
        return base()
                .when()
                .get("/api/jobs/stats")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static long createJob(String title, String skillCategory, String location,
                                 String pincode, String budget,
                                 String postedBy, String phone) {
        String body = String.format(
                "{\"title\":\"%s\",\"skillCategory\":\"%s\",\"location\":\"%s\"," +
                        "\"pincode\":\"%s\",\"budget\":\"%s\",\"postedBy\":\"%s\",\"phone\":\"%s\"}",
                title, skillCategory, location, pincode, budget, postedBy, phone);

        return ((Number) base()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/jobs")
                .then()
                .statusCode(200)
                .extract()
                .path("id")).longValue();
    }

    public static Response applyToJob(long jobId, String workerName,
                                      String workerPhone, String workerSkill) {
        String body = String.format(
                "{\"workerName\":\"%s\",\"workerPhone\":\"%s\",\"workerSkill\":\"%s\"}",
                workerName, workerPhone, workerSkill);

        return base()
                .contentType(ContentType.JSON)
                .pathParam("jobId", jobId)
                .body(body)
                .when()
                .post("/api/jobs/{jobId}/apply")
                .then()
                .extract().response();
    }

    public static Response getApplicants(long jobId) {
        return base()
                .pathParam("jobId", jobId)
                .when()
                .get("/api/jobs/{jobId}/applicants")
                .then()
                .statusCode(200)
                .extract().response();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  REVIEWS
    // ════════════════════════════════════════════════════════════════════════

    public static Response getAllReviews() {
        return base()
                .when()
                .get("/api/reviews")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static Response submitReview(String employee, int rating, String comment) {
        String body = String.format(
                "{\"employee\":\"%s\",\"rating\":%d,\"comment\":\"%s\"}",
                employee, rating, comment);

        return base()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/reviews")
                .then()
                .extract().response();
    }
}