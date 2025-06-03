package de.sourcepark.synaptic.testrunner;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StartTestHandler extends AbstractHandler implements HttpHandler {

    class StartTestRequest {
        String testPlan;
        String testDescription;
        String[] platforms;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            StartTestRequest request = gson.fromJson(body, StartTestRequest.class);

            GitHandler gitHandler = new GitHandler();
            boolean checkoutSuccess = false;


            // HTTP authentication (username/password)
            checkoutSuccess = gitHandler.checkoutRepositoryWithCredentials(
                    request.testPlan,
                    "/Users/barmeier/Temp/test-repo",
                    DataBox.getInstance().getUsername(),
                    DataBox.getInstance().getPassword()
            );

            if (!checkoutSuccess) {
                System.out.println("Failed to checkout repository");
                sendJsonResponse(exchange, 500, "{\"error\":\"Failed to checkout repository\"}");
                return;
            }

            System.out.println("/start-test aufgerufen. Body: " + body);
            UUID uuid = UUID.randomUUID();
            String response = "{\"testRunId\":\"" + uuid + "\",\"message\":\"Test started successfully\"}";
            sendJsonResponse(exchange, 200, response);
        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Invalid request\"}");
        }
    }
}
