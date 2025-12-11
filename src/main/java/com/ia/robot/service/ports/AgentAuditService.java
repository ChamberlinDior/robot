package com.ia.robot.service.ports;

import com.ia.robot.dto.request.AgentAskRequest;
import com.ia.robot.dto.request.AgentPlanRequest;

/**
 * Contrat d'audit/traçabilité.
 *
 * V0: No-op possible.
 * V1: persistance vers AgentRun/AgentPromptLog.
 */
public interface AgentAuditService {

    void logPlanRequest(AgentPlanRequest request, String rawOutput);

    void logAskRequest(AgentAskRequest request, String rawOutput);
}
