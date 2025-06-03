package de.sourcepark.synaptic.testrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class Tools {


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
        request.setEntity(new StringEntity(jsonPayload));

        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        // Ensure the response entity is consumed
        EntityUtils.consume(response.getEntity());

        return statusCode == 200;
    }
}
