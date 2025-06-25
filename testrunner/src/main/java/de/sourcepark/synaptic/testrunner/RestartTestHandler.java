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
import de.sourcepark.synaptic.testrunner.processing.ExecuterThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

class RestartTestHandler extends AbstractHandler implements HttpHandler {
    private final static Logger LOG = LogManager.getLogger(RestartTestHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String testRunId = exchange.getRequestURI().getPath().substring(14);
            if (testRunId.equals(DataBox.getInstance().getTestRunId())) {
                //TODO: Stop runnning test
                if (DataBox.getInstance().getTestStatus().equals("RUNNING")) {
                    ExecuterThread.setProcessRunning(false);
                    DataBox.getInstance().setTestStatus("SKIPPED");
                }
                ExecuterThread exc = new ExecuterThread("k8s", DataBox.getInstance().getTestName(), false);
                exc.start();
                try {
                    exc.join(3600*15);
                } catch (InterruptedException e) {
                    LOG.warn("Thread wait interrupted.", e);
                    throw new RuntimeException(e);
                }
                //TODO: Start new test

                exc = new ExecuterThread("k8s", DataBox.getInstance().getTestName(), true);
                exc.start();

                String uuid = UUID.randomUUID().toString();
                DataBox.getInstance().setTestStatus("RUNNING");
                DataBox.getInstance().setTestRunId(uuid);

                String response = "{\"testRunId\":\"" + uuid + "\"," +
                        "\"message\":\"Test restarted successfully\"}";
                sendJsonResponse(exchange, 200, response);
            }
            else {
                sendJsonResponse(exchange, 404, "{\"errortext\":\"Test ["+testRunId+"] nicht gefunden\"," +
                        "\"errorcode\":\"404\"," +
                        "\"testRunId\":\"" + DataBox.getInstance().getTestRunId() + "\"," +
                        "\"errortext\":\"Test restart failed. TestRunID ["+testRunId+"] is not known to this instance.\"," +
                        "\"message\":\"Test restart failed\"}");
            }
        } else {
            sendJsonResponse(exchange, 500, "{\"errortext\":\"Unsupported request type\"," +
                    "\"errorcode\":\"500\"," +
                    "\"errortext\":\"Wrong method. This endpoint only supports GET.\"," +
                    "\"testRunId\":\" N/A \"," +
                    "\"message\":\"Test restart failed\"}");
        }
    }
}