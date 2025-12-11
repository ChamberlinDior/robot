package com.ia.robot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ia.robot.exception.BadRequestException;

import java.util.Map;

/**
 * JSON utils V0.
 *
 * - Fournit un ObjectMapper minimal
 * - Utile pour petits helpers et tests
 *
 * NB:
 * - Dans les services Spring, privilégie l'ObjectMapper injecté.
 */
public final class JsonUtils {

    private static final ObjectMapper OM = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(Object value) {
        try {
            return OM.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("JSON serialization failed.", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return OM.readValue(json, type);
        } catch (Exception e) {
            throw new BadRequestException("JSON parsing failed.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) {
        try {
            return OM.readValue(json, Map.class);
        } catch (Exception e) {
            throw new BadRequestException("JSON parsing to Map failed.", e);
        }
    }
}
