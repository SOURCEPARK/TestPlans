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

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StartTestHandler extends AbstractHandler implements HttpHandler {
    private final static Logger LOG = LogManager.getLogger(StartTestHandler.class);
    class StartTestRequest {
        String testPlan;
        String testDescription;
        String[] platforms;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            if (DataBox.getInstance().getTestStatus().equals("RUNNING")) {
                sendJsonResponse(exchange, 404, "{\"errortext\":\"This instance can only run one test.\"," +
                        "\"errorcode\":\"404\"," +
                        "\"testRunId\":\"" + DataBox.getInstance().getTestRunId() + "\"," +
                        "\"message\":\"Test ["+DataBox.getInstance().getTestRunId()+"] is actually running\"}");

                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            StartTestRequest request = gson.fromJson(body, StartTestRequest.class);

            GitHandler gitHandler = new GitHandler();
            boolean checkoutSuccess = false;

            LOG.info("TestPlan: " + request.testPlan);
            String parts[] = request.testPlan.split("#");


            // HTTP authentication (username/password)
            checkoutSuccess = gitHandler.checkoutRepositoryWithCredentials(
                    parts[0],
                    DataBox.getInstance().getGitCheckoutFolder(),
                    DataBox.getInstance().getUsername(),
                    DataBox.getInstance().getPassword()
            );

            if (!checkoutSuccess) {
                System.out.println("Failed to checkout repository");
                sendJsonResponse(exchange, 500, "{\"error\":\"Failed to checkout repository\"}");
                sendJsonResponse(exchange, 501, "{\"errortext\":\"Failed to checkout repository.\"," +
                        "\"errorcode\":\"501\"," +
                        "\"testRunId\":\"" + DataBox.getInstance().getTestRunId() + "\"," +
                        "\"message\":\"\"Test start failed. Unable to checkout repository\"}");
                return;
            }

            //TODO: check if test case is present in checked out repository


            //TODO: Run external testing thread


            System.out.println("/start-test aufgerufen. Body: " + body);
            String uuid = UUID.randomUUID().toString();
            String response = "{\"testRunId\":\"" + uuid + "\",\"message\":\"Test started successfully\"}";

            DataBox.getInstance().setTestRunId(uuid);
            DataBox.getInstance().setTestName(parts[1]);
            DataBox.getInstance().setTestStartTime(System.currentTimeMillis());
            DataBox.getInstance().setTestStatus("RUNNING");

            sendJsonResponse(exchange, 200, response);
        } else {
            sendJsonResponse(exchange, 500, "{\"errortext\":\"Unsupported request type\"," +
                    "\"errorcode\":\"500\"," +
                    "\"testRunId\":\" N/A \"," +
                    "\"message\":\"Test restart failed\"}");
        }
    }
}
