package com.ia.robot.exception;

/**
 * Exception liée à l'invocation d'un tool.
 *
 * Status conseillé côté handler:
 * - 422 UNPROCESSABLE_ENTITY
 *
 * V0:
 * - Utilisée quand un tool échoue ou renvoie une sortie invalide
 */
public class ToolInvocationException extends RuntimeException {

    public ToolInvocationException() {
        super("Tool invocation error.");
    }

    public ToolInvocationException(String message) {
        super(message);
    }

    public ToolInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
