package de.sourcepark.synaptic.testrunner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

class TestStatusHandler extends AbstractHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            System.out.println("/test-status requested. Query: " + query);
            String response = "{\"runnerId\":\"testrunner-01\",\"testRunId\":\"run-2025-05-09-001\",\"testName\":\"ExampleName\",\"status\":\"RUNNING\",\"startTime\":\"2025-05-09T10:30:00Z\",\"elapsedSeconds\":45,\"progress\":0.5}";
            sendJsonResponse(exchange, 200, response);
        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Unhandled request\"}");
        }
    }
}

