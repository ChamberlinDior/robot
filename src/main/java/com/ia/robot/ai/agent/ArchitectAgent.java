package com.ia.robot.ai.agent;

import com.ia.robot.ai.prompt.SystemPrompts;
import com.ia.robot.ai.live.OrwellLiveClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Agent "Architect" V0/V1.
 *
 * Rôle:
 * - Transformer un besoin en plan d'architecture JSON
 * - Répondre à des questions d'architecture
 *
 * Correction:
 * - On rend l'usage de SystemPrompts optionnel et safe.
 * - Si un SystemPrompt est null/blank ou problématique,
 *   on retombe sur le defaultSystem défini dans ChatClientConfig.
 *
 * ===CLE-MODIF-ORWELL===
 * Extension "LIVE" (optionnelle, non bloquante):
 * - Si OrwellLiveClient est présent dans le contexte Spring
 *   ET activé via ORWELL_WEB_ENABLED=true,
 *   alors les questions libres (ask) peuvent être enrichies par le web.
 * - Sinon, fallback 100% ChatClient standard.
 *
 * IMPORTANT:
 * - Cette classe compile même si tu n'as pas encore ajouté OrwellLiveClient,
 *   grâce à ObjectProvider (injection optionnelle).
 */
@Component
public class ArchitectAgent {

    private final ChatClient chatClient;

    /*===MODIF-ORWELL: injection optionnelle du mode LIVE ===*/
    private final ObjectProvider<OrwellLiveClient> liveClientProvider;

    public ArchitectAgent(ChatClient chatClient,
                          ObjectProvider<OrwellLiveClient> liveClientProvider) {
        this.chatClient = chatClient;
        this.liveClientProvider = liveClientProvider;
    }

    /*===MODIF-ORWELL: helper safe system ===*/
    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /*===MODIF-ORWELL: récupération safe du live client ===*/
    private OrwellLiveClient liveClientOrNull() {
        return liveClientProvider != null ? liveClientProvider.getIfAvailable() : null;
    }

    /*===MODIF-ORWELL: fallback system minimal pour LIVE ===*/
    private String liveAskSystemFallback() {
        return "Tu es ORWELL, un assistant technique et pédagogique, clair et professionnel.";
    }

    /**
     * Génère un plan d'architecture.
     *
     * Choix volontaire:
     * - On garde le mode standard pour maximiser la stabilité JSON.
     */
    public String generatePlan(AgentContext context, String userPrompt) {
        try {
            /*===MODIF-ORWELL: system prompt optionnel ===*/
            if (hasText(SystemPrompts.ARCHITECT_PLAN_SYSTEM)) {
                return chatClient.prompt()
                        .system(SystemPrompts.ARCHITECT_PLAN_SYSTEM)
                        .user(userPrompt)
                        .call()
                        .content();
            }

            // Fallback: defaultSystem ORWELL (ChatClientConfig)
            return chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();

        } catch (Exception e) {
            // On laisse SimpleAgentRunner envelopper proprement
            throw e;
        }
    }

    /**
     * Répond à une question libre.
     *
     * ===CLE-MODIF-ORWELL===
     * - Priorité au mode LIVE si disponible et activé.
     * - Sinon, mode standard.
     */
    public String answer(AgentContext context, String question) {
        try {
            /*===MODIF-ORWELL: tentative LIVE d'abord (si activée) ===*/
            OrwellLiveClient live = liveClientOrNull();
            if (live != null && live.isEnabled()) {
                try {
                    String system = hasText(SystemPrompts.ARCHITECT_ASK_SYSTEM)
                            ? SystemPrompts.ARCHITECT_ASK_SYSTEM
                            : liveAskSystemFallback();

                    return live.askLive(system, question);

                } catch (Exception ignored) {
                    // Fallback silencieux: on ne casse jamais l'expérience utilisateur
                }
            }

            /*===MODIF-ORWELL: system prompt optionnel côté standard ===*/
            if (hasText(SystemPrompts.ARCHITECT_ASK_SYSTEM)) {
                return chatClient.prompt()
                        .system(SystemPrompts.ARCHITECT_ASK_SYSTEM)
                        .user(question)
                        .call()
                        .content();
            }

            // Fallback: defaultSystem ORWELL (ChatClientConfig)
            return chatClient.prompt()
                    .user(question)
                    .call()
                    .content();

        } catch (Exception e) {
            throw e;
        }
    }
}
