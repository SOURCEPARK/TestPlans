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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class Tools {

    private static final Logger LOG = LogManager.getLogger(Tools.class);
    /**
     * Helper method to send POST requests to the API.
     *
     * @param endpoint  API endpoint
     * @param payload   Request payload
     * @return true if the request was successful (HTTP 200), false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean sendPostRequest(String endpoint, Map<String, Object> payload) throws IOException {

        HttpClient httpClient = HttpClients.createDefault();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        HttpPost request = new HttpPost(DataBox.getInstance().getGuiServerUrl() + endpoint);
        request.setHeader("Content-Type", "application/json");

        String jsonPayload = objectMapper.writeValueAsString(payload);

        return sendPostRequest(endpoint, jsonPayload);
    }
    public static boolean sendPostRequest(String endpoint, String payload) throws IOException {

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(DataBox.getInstance().getGuiServerUrl() + endpoint);
        request.setHeader("Content-Type", "application/json");

        LOG.info("Sending payload: " + AbstractHandler.prettyPrintJsonString(payload));
        request.setEntity(new StringEntity(payload));

        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        // Ensure the response entity is consumed
        EntityUtils.consume(response.getEntity());
        if (statusCode >= 400) {
            LOG.error("API request failed with status code: " + statusCode+ " response body: " + response.getEntity());
        }
        return statusCode <= 399;
    }



}
