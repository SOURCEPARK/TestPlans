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

class TestStatusHandler extends AbstractHandler implements HttpHandler {
    private final static Logger LOG = LogManager.getLogger(TestStatusHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            System.out.println("/test-status requested. Query: " + query);
            String runId = query.substring(10);
            if (runId != null && runId.equals(DataBox.getInstance().getTestRunId())) {
                String response = "{\"runnerId\":\"" + DataBox.getInstance().getTestRunnerIdentity() +
                        "\",\"testRunId\":\"" + DataBox.getInstance().getTestRunId() +
                        "\",\"testName\":\"" + DataBox.getInstance().getTestName() +
                        "\",\"status\":\"" + DataBox.getInstance().getTestStatus() +
                        "\",\"startTime\":\"" + DataBox.getInstance().getStartTime() +
                        "\",\"elapsedSeconds\":N/A,\"progress\":" + DataBox.getInstance().getTestProgress() + "}";
                sendJsonResponse(exchange, 200, response);
            }
            else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Test run not found\"}");
            }

        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Unhandled request\"}");
        }
    }
}

