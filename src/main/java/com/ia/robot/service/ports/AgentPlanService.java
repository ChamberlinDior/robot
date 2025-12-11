package com.ia.robot.service.ports;

import com.ia.robot.dto.request.AgentPlanRequest;
import com.ia.robot.dto.response.AgentPlanResponse;

/**
 * Contrat principal pour la génération d'un plan d'architecture.
 */
public interface AgentPlanService {

    /**
     * Génère un plan structurée (avec tentative de mapping JSON).
     */
    AgentPlanResponse generatePlan(AgentPlanRequest request);

    /**
     * Retourne la sortie brute de l'agent (JSON string).
     */
    String generatePlanRaw(AgentPlanRequest request);
}
