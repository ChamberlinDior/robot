package com.ia.robot.service.impl;

import com.ia.robot.ai.agent.AgentContext;
import com.ia.robot.ai.agent.SimpleAgentRunner;
import com.ia.robot.dto.request.AgentAskRequest;
import com.ia.robot.dto.response.AgentAskResponse;
import com.ia.robot.exception.BadRequestException;
import com.ia.robot.service.ports.AgentAskService;
import com.ia.robot.service.ports.AgentAuditService;
import org.springframework.stereotype.Service;

/**
 * Service V0 pour les questions libres (ask).
 *
 * Pipeline:
 * - Validate request
 * - Build AgentContext
 * - Run agent
 * - Audit
 * - Return DTO
 */
@Service
public class AgentAskServiceImpl implements AgentAskService {

    private static final String ARCHITECT_AGENT_NAME = "ArchitectAgent";

    private final SimpleAgentRunner agentRunner;
    private final AgentAuditService auditService;

    public AgentAskServiceImpl(SimpleAgentRunner agentRunner,
                               AgentAuditService auditService) {
        this.agentRunner = agentRunner;
        this.auditService = auditService;
    }

    @Override
    public AgentAskResponse ask(AgentAskRequest request) {
        String raw = askRaw(request);
        return new AgentAskResponse(raw);
    }

    @Override
    public String askRaw(AgentAskRequest request) {
        validate(request);

        AgentContext context = AgentContext.builder()
                .agentName(ARCHITECT_AGENT_NAME)
                .build();

        String raw = agentRunner.runAsk(context, request.question());

        // Audit V0 : no-op possible si impl minimaliste
        auditService.logAskRequest(request, raw);

        return raw;
    }

    private void validate(AgentAskRequest request) {
        if (request == null) {
            throw new BadRequestException("AgentAskRequest is null.");
        }
        if (request.question() == null || request.question().isBlank()) {
            throw new BadRequestException("Field 'question' is required.");
        }
    }
}
