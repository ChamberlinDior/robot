package com.ia.robot.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Réponse structurée d'un plan d'architecture généré par l'agent.
 *
 * On utilise des structures génériques (Map/List) pour rester flexible
 * tant que le schéma final évolue.
 */
public record AgentPlanResponse(
        Map<String, Object> architecture,
        List<Map<String, Object>> entities,
        List<Map<String, Object>> endpoints,
        List<Map<String, Object>> dto,
        List<Map<String, Object>> screensMobile,
        List<String> checklist,
        String raw // utile pour debug et traçabilité V0
) {
}
