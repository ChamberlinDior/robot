package com.ia.robot.controller.advice;

import com.ia.robot.exception.AgentExecutionException;
import com.ia.robot.exception.BadRequestException;
import com.ia.robot.exception.NotFoundException;
import com.ia.robot.exception.ToolInvocationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the Robot API.
 *
 * Auteur (à ta demande) : Eva
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ToolInvocationException.class)
    public ResponseEntity<Map<String, Object>> handleToolInvocation(ToolInvocationException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "TOOL_ERROR", ex.getMessage());
    }

    @ExceptionHandler(AgentExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleAgentExecution(AgentExecutionException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "AGENT_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // Mode DEV optionnel : expose le message réel si activé en VM option
        // Exemple IntelliJ VM options :
        // -Dapp.errors.include-details=true
        boolean includeDetails = Boolean.parseBoolean(
                System.getProperty("app.errors.include-details", "false")
        );

        if (includeDetails) {
            String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : ex.getClass().getSimpleName();
            return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", msg);
        }

        // Mode safe par défaut
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred.");
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String code, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("status", status.value());
        payload.put("error", status.getReasonPhrase());
        payload.put("code", code);
        payload.put("message", message);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload);
    }
}
