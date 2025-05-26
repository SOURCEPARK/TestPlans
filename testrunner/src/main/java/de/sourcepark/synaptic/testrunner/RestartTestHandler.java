package de.sourcepark.synaptic.testrunner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class RestartTestHandler extends AbstractHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("/restart-test aufgerufen. Body: " + body);
            String response = "{\"testRunId\":\"run-2025-05-09-001\",\"message\":\"Test erfolgreich neu gestartet\"}";
            sendJsonResponse(exchange, 200, response);
        } else {
            sendJsonResponse(exchange, 400, "{\"error\":\"Ungültige Anfrage\"}");
        }
    }
}