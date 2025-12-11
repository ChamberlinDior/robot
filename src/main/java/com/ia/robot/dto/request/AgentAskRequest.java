package com.ia.robot.dto.request;

/**
 * Requête simple de question à l'agent.
 */
public record AgentAskRequest(
        String question
) {
}
