package com.ia.robot.exception;

/**
 * Exception 404 - ressource introuvable.
 *
 * Utilisation typique:
 * - ID inconnu
 * - Résultat absent
 * - Domaine ou configuration non trouvée
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Resource not found.");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
