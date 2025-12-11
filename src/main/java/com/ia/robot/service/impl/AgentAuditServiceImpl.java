package com.ia.robot.service.impl;

import com.ia.robot.dto.request.AgentAskRequest;
import com.ia.robot.dto.request.AgentPlanRequest;
import com.ia.robot.service.ports.AgentAuditService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Audit V0: log console uniquement.
 *
 * V1:
 * - alimenter AgentRun / AgentPromptLog / AgentPlanSnapshot via repository.
 */
@Service
public class AgentAuditServiceImpl implements AgentAuditService {

    private static final Logger log = LoggerFactory.getLogger(AgentAuditServiceImpl.class);

    @Override
    public void logPlanRequest(AgentPlanRequest request, String rawOutput) {
        log.info("[AGENT_PLAN] domain={}, mobile={}, constraintsCount={}, rawSize={}",
                safe(request.domain()),
                request.mobile(),
                request.constraints() == null ? 0 : request.constraints().size(),
                rawOutput == null ? 0 : rawOutput.length()
        );
    }

    @Override
    public void logAskRequest(AgentAskRequest request, String rawOutput) {
        log.info("[AGENT_ASK] questionSize={}, rawSize={}",
                request.question() == null ? 0 : request.question().length(),
                rawOutput == null ? 0 : rawOutput.length()
        );
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
