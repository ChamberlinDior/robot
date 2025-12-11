package com.ia.robot.dto.response;

import java.time.Instant;

/**
 * Format d'erreur standardisé.
 */
public record ErrorResponse(
        int status,
        String error,
        String code,
        String message,
        String timestamp
) {
    public static ErrorResponse of(int status, String error, String code, String message) {
        return new ErrorResponse(status, error, code, message, Instant.now().toString());
    }
}
