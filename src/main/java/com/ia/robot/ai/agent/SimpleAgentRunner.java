package com.ia.robot.ai.agent;

import com.ia.robot.exception.AgentExecutionException;
import com.ia.robot.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SimpleAgentRunner – Orchestrateur central d’ORWELL (Education Gabon).
 *
 * Rôle :
 * - Point d’entrée stable pour les contrôleurs et services.
 * - Encapsule les appels à ArchitectAgent (plan + questions libres).
 * - Gère :
 *   - Le contexte Education / Gabon (via AgentContext).
 *   - La validation basique des entrées.
 *   - Le logging (durée, taille des prompts, contexte).
 *   - Le wrapping des erreurs dans AgentExecutionException.
 *
 * Usage typique :
 * - runArchitectPlan(context, userPrompt)
 * - runAsk(context, question)
 * - runAskForGabonStudent / runAskForGabonTeacher / runAskForGabonParent
 *
 * Objectif :
 * - Avoir un orchestrateur simple, fiable, prêt pour la démo
 *   devant un ministre ou un directeur d’établissement.
 */
@Component
public class SimpleAgentRunner {

    private static final Logger log = LoggerFactory.getLogger(SimpleAgentRunner.class);

    private final ArchitectAgent architectAgent;

    public SimpleAgentRunner(ArchitectAgent architectAgent) {
        this.architectAgent = architectAgent;
    }

    // ---------------------------------------------------------------------
    // Helpers internes
    // ---------------------------------------------------------------------

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private AgentContext ensureContextForPlan(AgentContext context) {
        if (context != null) {
            return context;
        }
        // Contexte minimal pour un plan d’architecture (cote technique)
        return AgentContext.builder()
                .agentName("ArchitectAgent")
                .tenant("GABON_EDU")
                .country("Gabon")
                .city("Libreville")
                .profileType("ADMIN")
                .educationLevel("GLOBAL")
                .subject("Architecture Systeme")
                .channel("BACKEND")
                .build();
    }

    private AgentContext ensureContextForAsk(AgentContext context) {
        if (context != null) {
            return context;
        }
        // Fallback : eleve generique au Gabon (utile pour les tests rapides)
        return AgentContext.forGabonStudent(
                "Libreville",
                "Etablissement non precise",
                "niveau non precise",
                "matiere generale"
        );
    }

    private void validateNotBlank(String value, String fieldName) {
        if (safeTrim(value).isEmpty()) {
            throw new BadRequestException(fieldName + " must not be empty.");
        }
    }

    // ---------------------------------------------------------------------
    // PLAN D’ARCHITECTURE (endpoint /plan)
    // ---------------------------------------------------------------------

    /**
     * Execute la generation d’un plan d’architecture.
     */
    public String runArchitectPlan(AgentContext context, String userPrompt) {
        AgentContext effectiveCtx = ensureContextForPlan(context);
        String prompt = safeTrim(userPrompt);
        validateNotBlank(prompt, "Plan prompt");

        long start = System.nanoTime();
        try {
            log.info(
                    "[ORWELL][PLAN] Start. requestId={}, agentName={}, tenant={}, country={}, city={}, level={}, subject={}, promptSize={}",
                    effectiveCtx.getRequestId(),
                    effectiveCtx.getAgentName(),
                    effectiveCtx.getTenant(),
                    effectiveCtx.getCountry(),
                    effectiveCtx.getCity(),
                    effectiveCtx.getEducationLevel(),
                    effectiveCtx.getSubject(),
                    prompt.length()
            );

            String result = architectAgent.generatePlan(effectiveCtx, prompt);

            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            log.info(
                    "[ORWELL][PLAN] Success. requestId={}, durationMs={}",
                    effectiveCtx.getRequestId(),
                    durationMs
            );

            return result;
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            log.error(
                    "[ORWELL][PLAN] Failed. requestId={}, agentName={}, durationMs={}, error={}",
                    effectiveCtx.getRequestId(),
                    effectiveCtx.getAgentName(),
                    durationMs,
                    e.getMessage(),
                    e
            );
            throw new AgentExecutionException("ArchitectAgent plan execution failed.", e);
        }
    }

    // ---------------------------------------------------------------------
    // QUESTIONS LIBRES (endpoint /ask)
    // ---------------------------------------------------------------------

    /**
     * Execute une question libre vers ORWELL.
     */
    public String runAsk(AgentContext context, String question) {
        AgentContext effectiveCtx = ensureContextForAsk(context);
        String q = safeTrim(question);
        validateNotBlank(q, "Question");

        long start = System.nanoTime();
        try {
            log.info(
                    "[ORWELL][ASK] Start. requestId={}, agentName={}, tenant={}, country={}, city={}, profileType={}, level={}, subject={}, questionSize={}",
                    effectiveCtx.getRequestId(),
                    effectiveCtx.getAgentName(),
                    effectiveCtx.getTenant(),
                    effectiveCtx.getCountry(),
                    effectiveCtx.getCity(),
                    effectiveCtx.getProfileType(),
                    effectiveCtx.getEducationLevel(),
                    effectiveCtx.getSubject(),
                    q.length()
            );

            String answer = architectAgent.answer(effectiveCtx, q);

            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            log.info(
                    "[ORWELL][ASK] Success. requestId={}, durationMs={}",
                    effectiveCtx.getRequestId(),
                    durationMs
            );

            return answer;
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            log.error(
                    "[ORWELL][ASK] Failed. requestId={}, agentName={}, durationMs={}, error={}",
                    effectiveCtx.getRequestId(),
                    effectiveCtx.getAgentName(),
                    durationMs,
                    e.getMessage(),
                    e
            );
            throw new AgentExecutionException("ArchitectAgent ask execution failed.", e);
        }
    }

    // ---------------------------------------------------------------------
    // SURCHARGES CONVENIENCE POUR L’EDUCATION AU GABON
    // ---------------------------------------------------------------------

    /**
     * Surcharge pratique pour interroger ORWELL au nom d’un ELEVE gabonais.
     */
    public String runAskForGabonStudent(
            String city,
            String establishment,
            String educationLevel,
            String subject,
            String question
    ) {
        AgentContext ctx = AgentContext.forGabonStudent(
                city,
                establishment,
                educationLevel,
                subject
        );
        return runAsk(ctx, question);
    }

    /**
     * Surcharge pratique pour interroger ORWELL au nom d’un ENSEIGNANT gabonais.
     */
    public String runAskForGabonTeacher(
            String city,
            String establishment,
            String educationLevel,
            String subject,
            String question
    ) {
        AgentContext ctx = AgentContext.forGabonTeacher(
                city,
                establishment,
                educationLevel,
                subject
        );
        return runAsk(ctx, question);
    }

    /**
     * Surcharge pratique pour interroger ORWELL au nom d’un PARENT au Gabon.
     */
    public String runAskForGabonParent(
            String city,
            String establishment,
            String childEducationLevel,
            String subject,
            String question
    ) {
        AgentContext ctx = AgentContext.forGabonParent(
                city,
                establishment,
                childEducationLevel,
                subject
        );
        return runAsk(ctx, question);
    }
}
