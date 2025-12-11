package com.ia.robot.dto.response;

import java.time.Instant;

/**
 * Wrapper standard pour les réponses API.
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String timestamp
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now().toString());
    }

    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data, Instant.now().toString());
    }
}
