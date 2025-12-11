package com.ia.robot.dto.request;

import java.util.List;

/**
 * Requête de planification d'architecture par l'agent.
 *
 * V0:
 * - Simple, stable, sans dépendance validation pour éviter
 *   d'ajouter un starter supplémentaire.
 */
public record AgentPlanRequest(
        String domain,
        List<String> constraints,
        boolean mobile
) {
}
