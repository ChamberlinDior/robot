package com.ia.robot.exception;

/**
 * Exception liée à l'exécution d'un agent.
 *
 * Utilisation typique:
 * - erreur runtime lors du call LLM
 * - prompt invalide côté agent
 * - parsing critique impossible (si tu choisis de le rendre bloquant en V1)
 */
public class AgentExecutionException extends RuntimeException {

    public AgentExecutionException() {
        super("Agent execution error.");
    }

    public AgentExecutionException(String message) {
        super(message);
    }

    public AgentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
