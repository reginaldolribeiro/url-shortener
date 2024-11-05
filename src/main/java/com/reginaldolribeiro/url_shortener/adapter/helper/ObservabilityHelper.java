package com.reginaldolribeiro.url_shortener.adapter.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ObservabilityHelper {

    private final ObjectMapper objectMapper;

    public ObservabilityHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Adds a single key-value pair, handling different types
    public void addCustomParameter(String key, Object value) {
        switch (value) {
            case null -> {
                return;
            }
            case String s -> NewRelic.addCustomParameter(key, s);
            case Number number -> NewRelic.addCustomParameter(key, number.doubleValue());
            case Boolean b -> NewRelic.addCustomParameter(key, b);
            default -> {
                try {
                    // Fallback to JSON serialization for other object types
                    String json = objectMapper.writeValueAsString(value);
                    NewRelic.addCustomParameter(key, json);
                } catch (JsonProcessingException e) {
                    System.err.println("Failed to serialize " + key + " for New Relic: " + e.getMessage());
                }
            }
        }
    }

    // Adds multiple parameters from a Map
    public void addCustomParameters(Map<String, Object> parameters) {
        parameters.forEach(this::addCustomParameter);
    }

    // Adds the response body safely, as JSON if possible
    public void addResponseBody(ResponseEntity<?> responseEntity) {
        if (responseEntity.getBody() != null) {
            try {
                String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
                NewRelic.addCustomParameter("url-shortener.responseBody", responseBody);
            } catch (JsonProcessingException e) {
                System.err.println("Failed to serialize response body for New Relic: " + e.getMessage());
            }
        }
    }

}
