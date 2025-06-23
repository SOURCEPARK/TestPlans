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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.OffsetDateTime;

class HeartbeatHandler extends AbstractHandler implements HttpHandler {
    private final static Logger LOG = LogManager.getLogger(HeartbeatHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getPath();
            System.out.println("/heartbeat requested. Query: " + query);

            String runId = DataBox.getInstance().getTestRunId();
            if (query.length() > 13) {
                runId = query.substring(13);
            }
//            else {
//                return;
//                sendJsonResponse(exchange, 200, "\"message\":\"OK\"");
//                return;
//            }

            int elapsedSeconds = (int) ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000);

            if (runId != null && runId.equals(DataBox.getInstance().getTestRunId())) {
                DataBox.getInstance().setHeartbeatTime(System.currentTimeMillis());
                DataBox.getInstance().setHeartbeatSequence(DataBox.getInstance().getHeartbeatSequence() + 1);
                String response = "{\"timestamp\":\"" + OffsetDateTime.now() +
                        "\",\"runnerId\":\"" + DataBox.getInstance().getTestRunnerIdentity() +
                        "\",\"status\":\"" + DataBox.getInstance().getTestRunnerStatus() +
                        "\",\"sequence\":" + DataBox.getInstance().getHeartbeatSequence() +
                        "\",\"uptimeSeconds\":" + (int) ((System.currentTimeMillis() - DataBox.getInstance().getStartTime()) / 1000) +
                        "\",\"testRunId\":\"" + DataBox.getInstance().getTestRunId() +
                        "\",\"testName\":\"" + DataBox.getInstance().getTestName() +
                        "\",\"testStatus\":\"" + DataBox.getInstance().getTestStatus() +
                        "\",\"startTime\":\"" + DataBox.getInstance().getStartTime() +
                        "\",\"progress\":" + DataBox.getInstance().getTestProgress() +
                        "\",\"elapsedSeconds\":" + elapsedSeconds + "\"}";

                sendJsonResponse(exchange, 200, response);
            }
            else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Test run [" + runId
                        + "] not found, expected start or [" + DataBox.getInstance().getTestRunId() + "]\"}");
            }

        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Unhandled request\"}");
        }
    }
}
