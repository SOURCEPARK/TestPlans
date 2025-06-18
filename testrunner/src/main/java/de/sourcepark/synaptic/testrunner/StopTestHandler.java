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
import de.sourcepark.synaptic.testrunner.processing.ExecuterThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class StopTestHandler extends AbstractHandler implements HttpHandler {
    private final static Logger LOG = LogManager.getLogger(StopTestHandler.class);

    class StopTestRequest {
        String testPlan;
        String testDescription;
        String[] platforms;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String testRunId = exchange.getRequestURI().getPath().substring(11);
            if (!testRunId.equals(DataBox.getInstance().getTestRunId())) {
                LOG.info("Unknown test requested for stop. Unknown tests are always in status stopped.");
            }
            ExecuterThread.setProcessRunning(false);
            DataBox.getInstance().setTestStatus("STOPPED");
            String response = "{\"testRunId\":\"" + DataBox.getInstance().getTestRunId() +
                    "\",\"message\":\"Test gestoppt\"}";
            sendJsonResponse(exchange, 200, response);

        } else {
            sendJsonResponse(exchange, 500, "{\"errortext\":\"Unsupported request type\"," +
                    "\"errorcode\":\"500\"," +
                    "\"testRunId\":\" N/A \"," +
                    "\"message\":\"Test restart failed\"}");
        }
    }
}

