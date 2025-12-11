package com.ia.robot.exception;

/**
 * Exception 400 - requête invalide.
 *
 * V0 stable: constructors complets pour éviter
 * "'BadRequestException()' cannot be applied to '(java.lang.String)'"
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("Bad request.");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
