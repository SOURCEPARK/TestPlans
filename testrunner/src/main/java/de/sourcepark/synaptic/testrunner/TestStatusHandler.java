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
            String query = exchange.getRequestURI().getPath();
            System.out.println("/test-status requested. Query: " + query);
            String runId = query.substring(13);
            String errorJson = "";
            if (runId != null && !runId.equals(DataBox.getInstance().getTestRunId())) {
                errorJson = "\",\"errorCode\":\"404\",\"errorText\":\"Test run not found";
            }
            String response = "{\"runnerId\":\"" + DataBox.getInstance().getTestRunnerIdentity() +
                    "\",\"testRunId\":\"" + DataBox.getInstance().getTestRunId() +
                    "\",\"testName\":\"" + DataBox.getInstance().getTestName() + errorJson +
                    "\",\"testStatus\":\"" + DataBox.getInstance().getTestStatus() +
                    "\",\"status\":\"" + DataBox.getInstance().getTestStatus() +
                    "\",\"startTime\":\"" + DataBox.getInstance().getStartTime() +
                    "\",\"elapsedSeconds\":0,\"progress\":" + DataBox.getInstance().getTestProgress() + "}";
            sendJsonResponse(exchange, 200, response);
        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Unhandled request\"}");
        }
    }
}

