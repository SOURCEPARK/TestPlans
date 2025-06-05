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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractHandler {

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}

