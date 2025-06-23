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

    /**
     * Sends a heartbeat to the monitoring API.
     *
     * @param timestamp     Timestamp of the heartbeat
     * @param runnerId      ID of the testrunner
     * @param status        Current status of the testrunner (RUNNING, IDLE, ERROR)
     * @param sequence      Sequence number of the heartbeat
     * @param uptimeSeconds Uptime of the testrunner in seconds (optional)
     * @return true if the heartbeat was successfully sent, false otherwise
     */
    public boolean sendHeartbeat(OffsetDateTime timestamp, String runnerId, String status,
                                 int sequence, Integer uptimeSeconds) {
        try {
            String testRunId = DataBox.getInstance().getTestRunId();
            String testrunnerStatus = DataBox.getInstance().getTestRunnerStatus();
            Map<String, Object> heartbeat = null;
            if (!testRunId.equals("NOT_YET_SET")) {
                if (testrunnerStatus.equals("RUNNING")) {
                    Long elapsedSeconds = ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000);
                    heartbeat = Map.ofEntries(
                            Map.entry("timestamp", timestamp.toString()),
                            Map.entry("runnerId", runnerId),
                            Map.entry("status", status),
                            Map.entry("sequence", sequence),
                            Map.entry("uptimeSeconds", uptimeSeconds != null ? uptimeSeconds : 0),
                            Map.entry("testRunId", DataBox.getInstance().getTestRunId()),
                            Map.entry("testName", DataBox.getInstance().getTestName()),
                            Map.entry("testStatus", DataBox.getInstance().getTestStatus()),
                            Map.entry("startTime", DataBox.getInstance().getStartTime()),
                            Map.entry("progress", DataBox.getInstance().getTestProgress()),
                            Map.entry("message", "Test is running..."),
                            Map.entry("elapsedSeconds", elapsedSeconds)
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
                            Map.entry("uptimeSeconds", uptimeSeconds != null ? uptimeSeconds : 0)
                    );
                }
            } else {
                heartbeat = Map.ofEntries(
                        Map.entry("timestamp", timestamp.toString()),
                        Map.entry("runnerId", runnerId),
                        Map.entry("status", status),
                        Map.entry("sequence", sequence),
                        Map.entry("uptimeSeconds", uptimeSeconds != null ? uptimeSeconds : 0)
                );
            }

            return Tools.sendPostRequest("/test-runner/heartbeat/" + runnerId, heartbeat);
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
                    if (!sendHeartbeat(OffsetDateTime.now(), DataBox.getInstance().getTestRunnerIdentity(),
                            DataBox.getInstance().getTestRunnerStatus(),
                            (int) DataBox.getInstance().getHeartbeatSequence(),
                            (int) ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000))) {
                        LOG.info("Failed to send heartbeat. Stopping heartbeat thread.");
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
