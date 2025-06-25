/**
 * Copyright SOURCEPARK GmbH 2021. Alle Rechte vorbehalten.
 * <p>
 * SOURCEPARK GmbH Gesellschaft fuer Softwareentwicklung
 * <p>
 * Hohenzollerndamm 150 Haus 7a
 * 14199 Berlin
 * <p>
 * Tel.:   +49 (0) 30 / 39 80 68 30
 * Fax:    +49 (0) 30 / 39 80 68 39
 * e-mail: kontakt@sourcepark.de
 * www:    www.sourcepark.de
 */
package de.sourcepark.synaptic.testrunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.Map;

public class HeartBeater extends Thread {
    private static final Logger LOG = LogManager.getLogger(HeartBeater.class);
    long heartbeatInterval;

    public HeartBeater(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    protected static String getHeartbeatJSON() throws JsonProcessingException {
        String testRunId = DataBox.getInstance().getTestRunId();
        String testrunnerStatus = DataBox.getInstance().getTestRunnerStatus();
        OffsetDateTime timestamp = OffsetDateTime.now();
        String runnerId = DataBox.getInstance().getTestRunnerIdentity();
        String status = DataBox.getInstance().getTestRunnerStatus();
        int sequence = (int) DataBox.getInstance().getHeartbeatSequence();
        int uptimeSeconds = (int) ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000);


        Map<String, Object> heartbeat = null;
        if (!testRunId.equals("NOT_YET_SET")) {
            if (testrunnerStatus.equals("RUNNING")) {
                Long elapsedSeconds = ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000);
                heartbeat = Map.ofEntries(
                        Map.entry("timestamp", timestamp.toString()),
                        Map.entry("runnerId", runnerId),
                        Map.entry("status", status),
                        Map.entry("sequence", sequence),
                        Map.entry("uptimeSeconds", uptimeSeconds),
                        Map.entry("testRunId", DataBox.getInstance().getTestRunId()),
                        Map.entry("testName", DataBox.getInstance().getTestName()),
                        Map.entry("testStatus", DataBox.getInstance().getTestStatus()),
                        Map.entry("startTime", DataBox.getInstance().getStartTime()),
                        Map.entry("progress", DataBox.getInstance().getTestProgress()),
                        Map.entry("message", DataBox.getInstance().getTestMessage()),
                        Map.entry("elapsedSeconds", DataBox.getInstance().getElapsedSeconds())
                );
            } else if (testrunnerStatus.equals("IDLE")) {
                heartbeat = Map.ofEntries(
                        Map.entry("timestamp", timestamp.toString()),
                        Map.entry("runnerId", runnerId),
                        Map.entry("status", status),
                        Map.entry("sequence", sequence),
                        Map.entry("testRunId", DataBox.getInstance().getTestRunId()),
                        Map.entry("testName", DataBox.getInstance().getTestName()),
                        Map.entry("testStatus", DataBox.getInstance().getTestStatus()),
                        Map.entry("uptimeSeconds", uptimeSeconds)
                );
            }
        } else {
            heartbeat = Map.ofEntries(
                    Map.entry("timestamp", timestamp.toString()),
                    Map.entry("runnerId", runnerId),
                    Map.entry("status", status),
                    Map.entry("sequence", sequence),
                    Map.entry("uptimeSeconds", uptimeSeconds)
            );
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(heartbeat);
    }


    /**
     * Sends a heartbeat to the monitoring API.
     *

     * @return true if the heartbeat was successfully sent, false otherwise
     */
    public boolean sendHeartbeat() {
        try {

            String runnerId = DataBox.getInstance().getTestRunnerIdentity();
            return Tools.sendPostRequest("/test-runner/heartbeat/" + runnerId, HeartBeater.getHeartbeatJSON());
        } catch (Exception e) {
            LOG.error("Failed to send heartbeat", e);
            return false;
        }
    }


    @Override
    public void run() {
        try {

            while (true) {
                try {
                    // Sleep for 1 second
                    DataBox.getInstance().setHeartbeatTime(System.currentTimeMillis());
                    DataBox.getInstance().setHeartbeatSequence(DataBox.getInstance().getHeartbeatSequence() + 1);
                    if (!sendHeartbeat()) {
                        LOG.info("Failed to send heartbeat.");
                        //return;
                    }
                    Thread.sleep(heartbeatInterval * 1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        } catch (Throwable e) {
            LOG.error("Failed to send heartbeat", e);
        } finally {
            LOG.info("Heartbeat thread stopped.");
        }
    }

    public void stopExecution() {
        this.interrupt();
    }
}
