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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

class RestartTestHandler extends AbstractHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String testRunId = exchange.getRequestURI().getPath().substring(14);
            if (testRunId.equals(DataBox.getInstance().getTestRunId())) {
                //TODO: Stop runnning test

                //TODO: Start new test

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
                        "\"message\":\"Test restart failed\"}");
            }
        } else {
            sendJsonResponse(exchange, 500, "{\"errortext\":\"Unsupported request type\"," +
                    "\"errorcode\":\"500\"," +
                    "\"testRunId\":\" N/A \"," +
                    "\"message\":\"Test restart failed\"}");
        }
    }
}