package com.ia.robot.controller.api;

import com.ia.robot.ai.agent.AgentContext;
import com.ia.robot.ai.agent.SimpleAgentRunner;
import com.ia.robot.exception.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final SimpleAgentRunner agentRunner;

    /*===CLE-MODIF-ORWELL: nom d'agent cohérent partout ===*/
    private static final String ARCHITECT_AGENT_NAME = "ArchitectAgent";

    public AgentController(SimpleAgentRunner agentRunner) {
        this.agentRunner = agentRunner;
    }

    /**
     * V0 - Agent plan endpoint
     * Expected body example:
     * {
     *   "domain": "Chauffeur",
     *   "constraints": ["Spring Boot", "MySQL", "Hibernate", "JWT later"],
     *   "mobile": true
     * }
     */
    @PostMapping(
            value = "/plan",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String plan(@RequestBody Map<String, Object> body) {
        String domain = readString(body, "domain", true);
        List<String> constraints = readStringList(body, "constraints");
        boolean mobile = readBoolean(body, "mobile", false);

        String userPrompt = buildPlanPrompt(domain, constraints, mobile);

        /*===CLE-MODIF-ORWELL: requestId explicite ===*/
        AgentContext context = AgentContext.builder()
                .requestId(UUID.randomUUID().toString())
                .agentName(ARCHITECT_AGENT_NAME)
                .build();

        return agentRunner.runArchitectPlan(context, userPrompt);
    }

    /**
     * V0 - Free ask endpoint
     * Expected body example:
     * {
     *   "question": "Propose-moi une architecture propre pour un module de cartes RFID."
     * }
     */
    @PostMapping(
            value = "/ask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String ask(@RequestBody Map<String, Object> body) {
        String question = readString(body, "question", true);

        /*===CLE-MODIF-ORWELL: ping local (ne dépend pas d'OpenAI) ===*/
        if ("ping".equalsIgnoreCase(question.trim())) {
            return "Pong! Comment puis-je vous aider avec Java/Spring ?";
        }

        AgentContext context = AgentContext.builder()
                .requestId(UUID.randomUUID().toString())
                .agentName(ARCHITECT_AGENT_NAME)
                .build();

        /*===CLE-MODIF-ORWELL: gestion d'erreur lisible côté client ===*/
        try {
            return agentRunner.runAsk(context, question);
        } catch (Exception ex) {
            log.error("[AGENT_ASK_ERROR] requestId={}, agentName={}, questionSize={}, message={}",
                    context.getRequestId(),
                    context.getAgentName(),
                    question.length(),
                    ex.getMessage(),
                    ex
            );

            // Message clair pour toi sans casser le flux de test
            return "ORWELL n'a pas pu appeler le moteur IA. " +
                    "Vérifie la clé OPENAI_API_KEY et le modèle configuré (OPENAI_MODEL). " +
                    "Consulte les logs Spring AI en DEBUG pour le détail.";
        }
    }

    // -------------------------
    // Prompt builder (V0)
    // -------------------------

    private String buildPlanPrompt(String domain, List<String> constraints, boolean mobile) {
        StringJoiner joiner = new StringJoiner("\n");

        joiner.add("Tu es ORWELL, un assistant d'architecture logicielle.");

        joiner.add("Je veux un plan d'architecture pour le domaine suivant : " + domain + ".");
        if (constraints != null && !constraints.isEmpty()) {
            joiner.add("Contraintes techniques : " + String.join(", ", constraints) + ".");
        } else {
            joiner.add("Contraintes techniques : Spring Boot, Java 21, MySQL, Hibernate.");
        }
        joiner.add("Le backend doit etre structure proprement (controller, dto, service, model, repository).");
        joiner.add("Je veux une sortie structuree et exploitable.");
        if (mobile) {
            joiner.add("Propose aussi les ecrans React Native minimaux lies a ce domaine.");
        }
        joiner.add("Reponds de maniere professionnelle et actionnable.");

        return joiner.toString();
    }

    // -------------------------
    // Safe readers
    // -------------------------

    private String readString(Map<String, Object> body, String key, boolean required) {
        Object value = body.get(key);
        if (value == null) {
            if (required) {
                throw new BadRequestException("Missing required field: " + key);
            }
            return null;
        }
        String s = String.valueOf(value).trim();
        if (required && s.isBlank()) {
            throw new BadRequestException("Field is blank: " + key);
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    private List<String> readStringList(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null) return List.of();
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of(String.valueOf(value));
    }

    private boolean readBoolean(Map<String, Object> body, String key, boolean defaultValue) {
        Object value = body.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
