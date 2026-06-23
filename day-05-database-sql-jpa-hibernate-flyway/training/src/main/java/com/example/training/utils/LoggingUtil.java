package com.example.training.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;  // ← TAMBAH INI
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class LoggingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void audit(String event, String action, Object details) {
        Map<String, Object> structured = new LinkedHashMap<>();
        structured.put("type", "AUDIT");
        structured.put("event", event);
        structured.put("action", action);
        structured.put("correlation_id", MDC.get("correlation_id"));
        structured.put("details", maskSensitive(details));

        try {
            log.info(mapper.writeValueAsString(structured));
        } catch (JsonProcessingException e) {
            log.info("AUDIT | event={} | action={} | correlation_id={}", 
                event, action, MDC.get("correlation_id"));
        }
    }

    public static void security(String event, String reason, String username) {
        Map<String, Object> structured = new LinkedHashMap<>();
        structured.put("type", "SECURITY");
        structured.put("event", event);
        structured.put("reason", reason);
        structured.put("correlation_id", MDC.get("correlation_id"));
        structured.put("user", username);

        try {
            log.warn(mapper.writeValueAsString(structured));
        } catch (JsonProcessingException e) {
            log.warn("SECURITY | event={} | reason={} | user={} | correlation_id={}",
                event, reason, username, MDC.get("correlation_id"));
        }
    }

    public static void error(String event, String message, Throwable ex) {
        Map<String, Object> structured = new LinkedHashMap<>();
        structured.put("type", "ERROR");
        structured.put("event", event);
        structured.put("message", message);
        structured.put("correlation_id", MDC.get("correlation_id"));
        structured.put("exception", ex != null ? ex.getClass().getSimpleName() : null);

        try {
            log.error(mapper.writeValueAsString(structured), ex);
        } catch (JsonProcessingException e) {
            log.error("ERROR | event={} | message={} | correlation_id={}",
                event, message, MDC.get("correlation_id"), ex);
        }
    }

    private static Object maskSensitive(Object input) {
        if (input == null) return null;
        String json = input.toString();
        return json
            .replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***MASKED***\"")
            .replaceAll("\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***MASKED***\"")
            .replaceAll("\"nik\"\\s*:\\s*\"[^\"]*\"", "\"nik\":\"***MASKED***\"")
            .replaceAll("\"email\"\\s*:\\s*\"[^\"]*\"", "\"email\":\"***MASKED***\"");
    }
}
