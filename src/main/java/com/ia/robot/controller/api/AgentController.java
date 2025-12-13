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

    // ---------------------------------------------------------------------
    // Utilitaires internes
    // ---------------------------------------------------------------------

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        String v = safeTrim(value);
        return v.isEmpty() ? defaultValue : v;
    }

    // ---------------------------------------------------------------------
    // 1) PLAN D’ARCHITECTURE (toujours disponible pour structurer les modules)
    // ---------------------------------------------------------------------

    /**
     * Endpoint historique /plan.
     *
     * Dans le contexte Education Gabon, ce plan sert à dessiner
     * l'architecture des modules Java/Spring Boot :
     * - gestion des élèves,
     * - notes, bulletins, emplois du temps,
     * - vie scolaire,
     * - examens, etc.
     *
     * Exemple de payload:
     * {
     *   "domain": "Gestion des eleves",
     *   "constraints": ["Java 21", "Spring Boot", "MySQL", "Hibernate"],
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

        // Contexte technique mais dans le tenant GABON_EDU
        AgentContext context = AgentContext.builder()
                .requestId(UUID.randomUUID().toString())
                .agentName(ARCHITECT_AGENT_NAME)
                .tenant("GABON_EDU")
                .country("Gabon")
                .city("Libreville")
                .profileType("ADMIN")
                .educationLevel("GLOBAL")
                .subject(domain)
                .channel("BACKEND")
                .build();

        return agentRunner.runArchitectPlan(context, userPrompt);
    }

    // ---------------------------------------------------------------------
    // 2) QUESTIONS LIBRES GENERIQUES (fallback élève gabonais générique)
    // ---------------------------------------------------------------------

    /**
     * Endpoint generique /ask.
     *
     * Exemple de payload:
     * {
     *   "question": "Explique-moi la difference entre un verbe transitif et intransitif."
     * }
     *
     * Si aucun contexte n'est fourni, ORWELL se comporte comme un
     * tuteur pour un eleve gabonais generique.
     */
    @PostMapping(
            value = "/ask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String ask(@RequestBody Map<String, Object> body) {
        String question = readString(body, "question", true);
        String q = safeTrim(question);

        // Petit ping local pour tester sans OpenAI
        if ("ping".equalsIgnoreCase(q)) {
            return "Pong ! ORWELL est en ligne pour t'aider a reviser au Gabon.";
        }

        try {
            // Contexte null -> SimpleAgentRunner applique le fallback
            // AgentContext.forGabonStudent(...) generique.
            return agentRunner.runAsk(null, q);
        } catch (Exception ex) {
            log.error("[AGENT_ASK_ERROR] generic /ask failed. questionSize={}, message={}",
                    q.length(),
                    ex.getMessage(),
                    ex
            );

            return "ORWELL n'a pas pu repondre pour le moment. " +
                    "Verifie la configuration de la cle OPENAI_API_KEY, du modele et les logs serveur.";
        }
    }

    // ---------------------------------------------------------------------
    // 3) ENDPOINTS SPECIFIQUES EDUCATION GABON
    // ---------------------------------------------------------------------

    /**
     * /ask/student : interroger ORWELL au nom d'un ELEVE au Gabon.
     *
     * Exemple payload:
     * {
     *   "city": "Mandji",
     *   "establishment": "Lycee de Mandji",
     *   "educationLevel": "Terminale",
     *   "subject": "Mathematiques",
     *   "question": "Explique-moi la derivation comme si j'avais 16 ans."
     * }
     */
    @PostMapping(
            value = "/ask/student",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String askForStudent(@RequestBody Map<String, Object> body) {
        String question = readString(body, "question", true);
        String city = defaultIfBlank(readString(body, "city", false), "Libreville");
        String establishment = defaultIfBlank(readString(body, "establishment", false), "Etablissement non precise");
        String educationLevel = defaultIfBlank(readString(body, "educationLevel", false), "niveau non precise");
        String subject = defaultIfBlank(readString(body, "subject", false), "matiere generale");

        try {
            return agentRunner.runAskForGabonStudent(
                    city,
                    establishment,
                    educationLevel,
                    subject,
                    question
            );
        } catch (Exception ex) {
            log.error("[AGENT_ASK_STUDENT_ERROR] city={}, establishment={}, level={}, subject={}, message={}",
                    city,
                    establishment,
                    educationLevel,
                    subject,
                    ex.getMessage(),
                    ex
            );
            return "ORWELL n'a pas pu repondre pour l'eleve. Consulte les logs serveur pour le detail.";
        }
    }

    /**
     * /ask/teacher : interroger ORWELL au nom d'un ENSEIGNANT au Gabon.
     *
     * Exemple payload:
     * {
     *   "city": "Gamba",
     *   "establishment": "College de Gamba",
     *   "educationLevel": "3e",
     *   "subject": "Physique-Chimie",
     *   "question": "Propose-moi un plan de cours de 45 minutes sur la densite."
     * }
     */
    @PostMapping(
            value = "/ask/teacher",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String askForTeacher(@RequestBody Map<String, Object> body) {
        String question = readString(body, "question", true);
        String city = defaultIfBlank(readString(body, "city", false), "Libreville");
        String establishment = defaultIfBlank(readString(body, "establishment", false), "Etablissement non precise");
        String educationLevel = defaultIfBlank(readString(body, "educationLevel", false), "niveau non precise");
        String subject = defaultIfBlank(readString(body, "subject", false), "matiere generale");

        try {
            return agentRunner.runAskForGabonTeacher(
                    city,
                    establishment,
                    educationLevel,
                    subject,
                    question
            );
        } catch (Exception ex) {
            log.error("[AGENT_ASK_TEACHER_ERROR] city={}, establishment={}, level={}, subject={}, message={}",
                    city,
                    establishment,
                    educationLevel,
                    subject,
                    ex.getMessage(),
                    ex
            );
            return "ORWELL n'a pas pu repondre pour l'enseignant. Consulte les logs serveur pour le detail.";
        }
    }

    /**
     * /ask/parent : interroger ORWELL au nom d'un PARENT au Gabon.
     *
     * Exemple payload:
     * {
     *   "city": "Port-Gentil",
     *   "establishment": "Ecole Vie Comblee",
     *   "childEducationLevel": "CE2",
     *   "subject": "Francais",
     *   "question": "Comment aider mon enfant a mieux lire a la maison ?"
     * }
     */
    @PostMapping(
            value = "/ask/parent",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String askForParent(@RequestBody Map<String, Object> body) {
        String question = readString(body, "question", true);
        String city = defaultIfBlank(readString(body, "city", false), "Libreville");
        String establishment = defaultIfBlank(readString(body, "establishment", false), "Etablissement non precise");
        String childEducationLevel = defaultIfBlank(readString(body, "childEducationLevel", false), "niveau non precise");
        String subject = defaultIfBlank(readString(body, "subject", false), "matiere generale");

        try {
            return agentRunner.runAskForGabonParent(
                    city,
                    establishment,
                    childEducationLevel,
                    subject,
                    question
            );
        } catch (Exception ex) {
            log.error("[AGENT_ASK_PARENT_ERROR] city={}, establishment={}, childLevel={}, subject={}, message={}",
                    city,
                    establishment,
                    childEducationLevel,
                    subject,
                    ex.getMessage(),
                    ex
            );
            return "ORWELL n'a pas pu repondre pour le parent. Consulte les logs serveur pour le detail.";
        }
    }

    // ---------------------------------------------------------------------
    // Prompt builder pour /plan (version EDU Gabon)
    // ---------------------------------------------------------------------

    private String buildPlanPrompt(String domain, List<String> constraints, boolean mobile) {
        StringJoiner joiner = new StringJoiner("\n");

        joiner.add("Tu es ORWELL, un assistant d'architecture pour l'education au Gabon.");
        joiner.add("Concentre-toi sur des modules en Java 21 / Spring Boot pour les etablissements scolaires gabonais.");
        joiner.add("Domaine fonctionnel a structurer : " + domain + ".");

        if (constraints != null && !constraints.isEmpty()) {
            joiner.add("Contraintes techniques : " + String.join(", ", constraints) + ".");
        } else {
            joiner.add("Contraintes techniques : Java 21, Spring Boot, MySQL, Hibernate.");
        }

        joiner.add("Respecte une architecture propre: controller/api, dto/request, dto/response, service/ports, service/impl, model/entity, repository.");

        if (mobile) {
            joiner.add("Propose aussi les ecrans mobiles minimaux (React Native) utiles aux eleves, enseignants ou parents au Gabon.");
        }

        joiner.add("Reponds de maniere professionnelle, claire et exploitable par une equipe Java au Gabon.");

        return joiner.toString();
    }

    // ---------------------------------------------------------------------
    // Lecteurs sûrs pour le JSON d'entree
    // ---------------------------------------------------------------------

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
