package com.ia.robot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ia.robot.ai.agent.AgentContext;
import com.ia.robot.ai.agent.SimpleAgentRunner;
import com.ia.robot.dto.request.AgentPlanRequest;
import com.ia.robot.dto.response.AgentPlanResponse;
import com.ia.robot.exception.BadRequestException;
import com.ia.robot.service.ports.AgentAuditService;
import com.ia.robot.service.ports.AgentPlanService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service V0 pour la génération de plans d'architecture.
 *
 * Pipeline:
 * - Validate request
 * - Build prompt
 * - Run ArchitectAgent via runner
 * - Parse output JSON de manière tolérante
 * - Audit
 */
@Service
public class AgentPlanServiceImpl implements AgentPlanService {

    private static final String ARCHITECT_AGENT_NAME = "ArchitectAgent";

    private final SimpleAgentRunner agentRunner;
    private final ObjectMapper objectMapper;
    private final AgentAuditService auditService;

    public AgentPlanServiceImpl(SimpleAgentRunner agentRunner,
                                ObjectMapper objectMapper,
                                AgentAuditService auditService) {
        this.agentRunner = agentRunner;
        this.objectMapper = objectMapper;
        this.auditService = auditService;
    }

    @Override
    public AgentPlanResponse generatePlan(AgentPlanRequest request) {
        String raw = generatePlanRaw(request);

        // Parsing tolérant V0
        try {
            Map<String, Object> root = objectMapper.readValue(raw, new TypeReference<>() {});
            return new AgentPlanResponse(
                    castMap(root.get("architecture")),
                    castListOfMaps(root.get("entities")),
                    castListOfMaps(root.get("endpoints")),
                    castListOfMaps(root.get("dto")),
                    castListOfMaps(root.get("screens_mobile")),
                    castListOfStrings(root.get("checklist")),
                    raw
            );
        } catch (Exception ex) {
            return new AgentPlanResponse(
                    Map.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of("Output parsing fallback: raw JSON returned."),
                    raw
            );
        }
    }

    @Override
    public String generatePlanRaw(AgentPlanRequest request) {
        validate(request);

        String prompt = buildPrompt(request);

        AgentContext context = AgentContext.builder()
                .agentName(ARCHITECT_AGENT_NAME)
                .build();

        String raw = agentRunner.runArchitectPlan(context, prompt);

        // Audit V0 : no-op possible si impl minimaliste
        auditService.logPlanRequest(request, raw);

        return raw;
    }

    // -------------------------
    // Internal helpers
    // -------------------------

    private void validate(AgentPlanRequest request) {
        if (request == null) {
            throw new BadRequestException("AgentPlanRequest is null.");
        }
        if (request.domain() == null || request.domain().isBlank()) {
            throw new BadRequestException("Field 'domain' is required.");
        }
    }

    private String buildPrompt(AgentPlanRequest request) {
        String domain = request.domain();
        List<String> constraints = request.constraints();
        boolean mobile = request.mobile();

        StringBuilder sb = new StringBuilder();
        sb.append("Je veux un plan d'architecture Spring Boot pour le domaine suivant : ")
                .append(domain).append(".\n");

        if (constraints != null && !constraints.isEmpty()) {
            sb.append("Contraintes techniques : ")
                    .append(String.join(", ", constraints)).append(".\n");
        } else {
            sb.append("Contraintes techniques : Java 21, Spring Boot, MySQL, Hibernate.\n");
        }

        sb.append("Respecte une architecture propre: controller/api, dto/request, dto/response, ")
                .append("service/ports, service/impl, model/entity, repository.\n");

        if (mobile) {
            sb.append("Propose aussi les écrans React Native minimaux liés à ce domaine.\n");
        }

        sb.append("Réponds strictement en JSON conformément au schéma de l'agent.");

        return sb.toString();
    }

    // -------------------------
    // Safe casts (V0)
    // -------------------------

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        if (value instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castListOfMaps(Object value) {
        if (value instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<String> castListOfStrings(Object value) {
        if (value instanceof List<?> list) {
            return (List<String>) list;
        }
        return List.of();
    }
}
