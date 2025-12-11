package com.ia.robot.util;

import com.ia.robot.exception.BadRequestException;

import java.util.Collection;
import java.util.Map;

/**
 * Validation utilitaire V0.
 *
 * - Centralise des checks simples
 * - Évite la répétition dans controller/service/runner
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new BadRequestException(message);
        }
    }

    public static String requireText(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new BadRequestException("Field '" + fieldName + "' is required.");
        }
        return value.trim();
    }

    public static <T> T requireNotNull(T value, String fieldName) {
        if (value == null) {
            throw new BadRequestException("Field '" + fieldName + "' is required.");
        }
        return value;
    }

    public static void requireNotEmpty(Collection<?> value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new BadRequestException("Field '" + fieldName + "' must not be empty.");
        }
    }

    public static void requireNotEmpty(Map<?, ?> value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new BadRequestException("Field '" + fieldName + "' must not be empty.");
        }
    }
}
