/**
 * Copyright SOURCEPARK GmbH 2021. Alle Rechte vorbehalten.
 *
 * SOURCEPARK GmbH Gesellschaft fuer Softwareentwicklung
 *
 * Hohenzollerndamm 150 Haus 7a
 * 14199 Berlin
 *
 * Tel.:   +49 (0) 30 / 39 80 68 30
 * Fax:    +49 (0) 30 / 39 80 68 39
 * e-mail: kontakt@sourcepark.de
 * www:    www.sourcepark.de
 */
package de.sourcepark.synaptic.testrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Client for the Testrunner Monitoring API.
 * Provides methods to execute each request defined in the OpenAPI specification.
 */
public class MonitoringApiClient {
    private static final Logger logger = LogManager.getLogger(MonitoringApiClient.class);
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    protected HeartBeater heartBeater;

    /**
     * Creates a new MonitoringApiClient.
     *
     * @param baseUrl Base URL of the monitoring API
     */
    public MonitoringApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        heartBeater = new HeartBeater(60);
        heartBeater.start();
    }



    /**
     * Sends a test status update to the monitoring API.
     *
     * @param runnerId        ID of the testrunner
     * @param testRunId       ID of the test run
     * @param testName        Name of the test (optional)
     * @param status          Status of the test (RUNNING, PASSED, FAILED, SKIPPED, PENDING, UNREACHABLE)
     * @param startTime       Start time of the test (optional)
     * @param elapsedSeconds  Elapsed time in seconds (optional)
     * @param progress        Progress of the test (0.0 - 1.0)
     * @param errorcode       Error code if the test failed (optional)
     * @param errortext       Error testDescription if the test failed (optional)
     * @return true if the status was successfully sent, false otherwise
     */
    public boolean sendTestStatus(String runnerId, String testRunId, String testName,
                                 String status, OffsetDateTime startTime, Integer elapsedSeconds,
                                 Double progress, String errorcode, String errortext) {
        try {
            Map<String, Object> testStatus = Map.of(
                "runnerId", runnerId,
                "testRunId", testRunId,
                "testName", testName != null ? testName : "",
                "status", status,
                "startTime", startTime != null ? startTime.toString() : null,
                "elapsedSeconds", elapsedSeconds != null ? elapsedSeconds : 0,
                "progress", progress != null ? progress : 0.0,
                "errorcode", errorcode != null ? errorcode : "",
                "errortext", errortext != null ? errortext : ""
            );

            return Tools.sendPostRequest("/test-status", testStatus);
        } catch (Exception e) {
            logger.error("Failed to send test status", e);
            return false;
        }
    }

    /**
     * Sends a test completion report to the monitoring API.
     *
     * @param runnerId   ID of the testrunner
     * @param testRunId  ID of the test run
     * @param report     Test report content
     * @return true if the report was successfully sent, false otherwise
     */
    public boolean sendTestCompleted(String runnerId, String testRunId, String report) {
        try {
            Map<String, Object> testCompleted = Map.of(
                "runnerId", runnerId,
                "testRunId", testRunId,
                "report", report != null ? report : ""
            );

            return Tools.sendPostRequest("/test-completed", testCompleted);
        } catch (Exception e) {
            logger.error("Failed to send test completed report", e);
            return false;
        }
    }

    /**
     * Registers a testrunner with the monitoring API.
     *
     * @param runnerId   ID of the testrunner
     * @param url        URL where the testrunner can be reached
     * @param platforms  List of platforms supported by the testrunner
     * @return true if the registration was successful, false otherwise
     */
    public boolean registerRunner(String runnerId, String url, List<String> platforms) {
        try {
            Map<String, Object> registerRunner = Map.of(
                "runnerId", runnerId,
                "url", url,
                "platforms", platforms
            );

            return Tools.sendPostRequest("/register-runner", registerRunner);
        } catch (Exception e) {
            logger.error("Failed to register runner", e);
            return false;
        }
    }

    public void stop() {
        heartBeater.stopExecution();
    }

    public void join() throws InterruptedException {
        heartBeater.join();
    }
}
