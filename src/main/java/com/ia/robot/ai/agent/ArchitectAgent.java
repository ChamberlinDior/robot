package com.ia.robot.ai.agent;

import com.ia.robot.ai.live.OrwellLiveClient;
import com.ia.robot.ai.prompt.SystemPrompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Agent "Architect" / ORWELL - Mode Education Gabon.
 *
 * Role global:
 * - Pour /plan : transformer un besoin (module ou service) en plan d'architecture JSON
 *   pour des solutions Java / Spring Boot au service de l'education au Gabon.
 * - Pour /ask : repondre aux questions des eleves, enseignants, parents ou admins
 *   de maniere claire, adaptee au niveau scolaire et au contexte gabonais.
 *
 * Contexte EDU / GABON (via AgentContext) :
 * - tenant         : ex. "GABON_EDU"
 * - country        : "Gabon"
 * - city           : Libreville, Mandji, Gamba, Port-Gentil, etc.
 * - establishment  : nom de l'etablissement
 * - profileType    : "ELEVE", "ENSEIGNANT", "PARENT", "ADMIN", "AUTRE"
 * - educationLevel : "Pre-scolaire", "CP", "CE2", "3e", "Terminale", "L1", "Master", etc.
 * - subject        : matiere (Mathematiques, Francais, Histoire-Geographie, etc.)
 * - locale         : ex. "fr-GA"
 * - channel        : "MOBILE", "WEB", "TABLETTE"
 *
 * === CLE-MODIF-ORWELL ===
 * Mode LIVE (optionnel, non bloquant) :
 * - Si OrwellLiveClient est present dans le contexte Spring ET active
 *   via ORWELL_WEB_ENABLED=true, les questions libres (/ask) peuvent
 *   etre enrichies par le web.
 * - Sinon, fallback 100% ChatClient standard.
 *
 * Robustesse :
 * - SystemPrompts utilises de facon safe (fallback si null/blank).
 * - Toute erreur LIVE est ignoree pour ne jamais casser l'experience utilisateur.
 */
@Component
public class ArchitectAgent {

    private final ChatClient chatClient;

    // Injection optionnelle du client LIVE
    private final ObjectProvider<OrwellLiveClient> liveClientProvider;

    public ArchitectAgent(ChatClient chatClient,
                          ObjectProvider<OrwellLiveClient> liveClientProvider) {
        this.chatClient = chatClient;
        this.liveClientProvider = liveClientProvider;
    }

    // -------------------------------------------------
    // Helpers internes
    // -------------------------------------------------

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String orDefault(String value, String def) {
        return hasText(value) ? value.trim() : def;
    }

    /**
     * Construit un resume de contexte education / Gabon a injecter dans les prompts.
     * Ce bloc permet a ORWELL de savoir a qui il parle et dans quel environnement.
     */
    private String buildEducationContextBlock(AgentContext ctx) {
        if (ctx == null) {
            // Fallback ultra simple si aucun contexte n'est fourni
            return """
                    Contexte Gabon / Education:
                    - Pays: Gabon
                    - Niveau: inconnu
                    - Profil: ELEVE
                    - Ville: Libreville
                    - Etablissement: non precise
                    - Matiere: generale
                    """;
        }

        String country = orDefault(ctx.getCountry(), "Gabon");
        String tenant = orDefault(ctx.getTenant(), "GABON_EDU");
        String city = orDefault(ctx.getCity(), "Libreville");
        String establishment = orDefault(ctx.getEstablishment(), "Etablissement non precise");
        String profileType = orDefault(ctx.getProfileType(), "ELEVE");
        String educationLevel = orDefault(ctx.getEducationLevel(), "niveau non precise");
        String subject = orDefault(ctx.getSubject(), "matiere generale");
        String locale = orDefault(ctx.getLocale(), "fr-GA");
        String channel = orDefault(ctx.getChannel(), "MOBILE");

        return "Contexte Gabon / Education:\n"
                + "- Tenant: " + tenant + "\n"
                + "- Pays: " + country + "\n"
                + "- Ville: " + city + "\n"
                + "- Etablissement: " + establishment + "\n"
                + "- Profil utilisateur: " + profileType + "\n"
                + "- Niveau scolaire: " + educationLevel + "\n"
                + "- Matiere: " + subject + "\n"
                + "- Langue/Locale: " + locale + "\n"
                + "- Canal: " + channel + "\n";
    }

    private OrwellLiveClient liveClientOrNull() {
        return liveClientProvider != null ? liveClientProvider.getIfAvailable() : null;
    }

    private String liveAskSystemFallback() {
        return """
                Tu es ORWELL, un assistant pedagogique gabonais, expert pour expliquer
                les notions scolaires (de la pre-scolaire a l'universite) de facon claire,
                simple et adaptee au niveau de l'utilisateur, dans un contexte gabonais.
                """;
    }

    // -------------------------------------------------
    // Generation de plan (endpoint /plan)
    // -------------------------------------------------

    /**
     * Genere un plan d'architecture JSON pour un module ou un service,
     * toujours dans l'optique de soutenir l'education au Gabon
     * (suivi des eleves, gestion des notes, plateformes d'apprentissage, etc.).
     *
     * Choix :
     * - On garde le schema JSON technique, mais on injecte le contexte EDU Gabon
     *   dans le prompt utilisateur pour que l'architecture reste alignee au terrain.
     */
    public String generatePlan(AgentContext context, String userPrompt) {
        try {
            String ctxBlock = buildEducationContextBlock(context);

            // On enrichit le prompt utilisateur avec le contexte education / Gabon
            String finalPrompt = ctxBlock + "\n"
                    + "Considere que ce module fait partie d'une plate-forme de digitalisation de l'education au Gabon.\n"
                    + "Adapte ton plan a ce contexte (etablissements scolaires gabonais, contraintes locales, connectivite variable, etc.).\n\n"
                    + "Demande detaillee :\n"
                    + userPrompt;

            if (hasText(SystemPrompts.ARCHITECT_PLAN_SYSTEM)) {
                return chatClient.prompt()
                        .system(SystemPrompts.ARCHITECT_PLAN_SYSTEM)
                        .user(finalPrompt)
                        .call()
                        .content();
            }

            // Fallback : system par defaut du ChatClient
            return chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content();

        } catch (Exception e) {
            // SimpleAgentRunner se charge d'envelopper proprement
            throw e;
        }
    }

    // -------------------------------------------------
    // Questions libres (endpoint /ask)
    // -------------------------------------------------

    /**
     * Repond a une question libre.
     *
     * Logique:
     * 1) On construit un "contexte Gabon / Education" a partir d'AgentContext
     * 2) On tente le mode LIVE d'abord (si disponible et active)
     * 3) Si LIVE echoue ou n'est pas dispo, on bascule sur le ChatClient standard
     *
     * L'objectif est de produire une reponse :
     * - claire, courte ou detaillee selon la question
     * - adaptee au niveau scolaire fourni
     * - illustree par des exemples gabonais (noms de villes, situations locales, etc.)
     */
    public String answer(AgentContext context, String question) {
        try {
            String ctxBlock = buildEducationContextBlock(context);

            String enrichedQuestion = ctxBlock + "\n"
                    + "Tu parles a un eleve, un enseignant, un parent ou un acteur de l'education au Gabon.\n"
                    + "Explique de facon simple, progressive, avec des exemples realistes dans le contexte gabonais.\n"
                    + "Si la notion est complexe, commence par une explication tres simple puis ajoute des details.\n\n"
                    + "Question utilisateur :\n"
                    + question;

            // 1) Tentative LIVE (si active)
            OrwellLiveClient live = liveClientOrNull();
            if (live != null && live.isEnabled()) {
                try {
                    String system = hasText(SystemPrompts.ARCHITECT_ASK_SYSTEM)
                            ? SystemPrompts.ARCHITECT_ASK_SYSTEM
                            : liveAskSystemFallback();

                    return live.askLive(system, enrichedQuestion);

                } catch (Exception ignored) {
                    // On ignore toute erreur LIVE pour assurer un fallback propre
                }
            }

            // 2) Mode standard ChatClient
            if (hasText(SystemPrompts.ARCHITECT_ASK_SYSTEM)) {
                return chatClient.prompt()
                        .system(SystemPrompts.ARCHITECT_ASK_SYSTEM)
                        .user(enrichedQuestion)
                        .call()
                        .content();
            }

            // Fallback system par defaut
            return chatClient.prompt()
                    .user(enrichedQuestion)
                    .call()
                    .content();

        } catch (Exception e) {
            throw e;
        }
    }
}
