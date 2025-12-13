package com.ia.robot.ai.prompt;

import java.util.List;

/**
 * Templates d'instructions utilisateur.
 *
 * Version EDU :
 * - Génère des instructions adaptées à ORWELL, assistant pédagogique gabonais.
 * - Utilisé par l'endpoint /plan pour construire le prompt "utilisateur"
 *   qui sera combiné avec le system prompt ARCHITECT_PLAN_SYSTEM.
 */
public final class PromptTemplates {

    private PromptTemplates() {
    }

    /**
     * Construit le prompt utilisateur envoyé à ORWELL pour générer
     * un plan de cours ou de révision.
     *
     * @param domain      Thème ou chapitre (ex: "Fractions – 5e", "Cycle de l'eau – CE2").
     * @param constraints Liste libre de contraintes (niveau, matière, durée,
     *                    type d'établissement, examen ciblé, etc.).
     * @param mobile      Indique si l'on souhaite aussi des idées d'usage sur mobile/tablette.
     * @return Prompt textuel en français, orienté vers le contexte éducatif gabonais.
     */
    public static String buildPlanPrompt(String domain, List<String> constraints, boolean mobile) {
        StringBuilder sb = new StringBuilder();

        // Contexte général : éducation au Gabon
        sb.append("Contexte : tu prépares un plan de cours ou de révision pour le système éducatif gabonais.\n");
        sb.append("Le thème ou chapitre à traiter est : ")
                .append(domain)
                .append(".\n");

        if (constraints != null && !constraints.isEmpty()) {
            sb.append("Contexte pédagogique détaillé (niveau, matière, contraintes particulières) : ");
            sb.append(String.join(", ", constraints)).append(".\n");
        } else {
            sb.append("Quand ce n'est pas précisé, suppose un contexte standard : collège ou lycée au Gabon, ");
            sb.append("programme francophone classique, durée de 45 à 60 minutes.\n");
        }

        sb.append("Tu dois proposer un plan PROPRE et STRUCTURÉ, exploitable immédiatement par un enseignant ou un élève au Gabon.\n");
        sb.append("Utilise des exemples concrets de la vie quotidienne au Gabon (écoles, quartiers, marchés, transport, forêt, mer, villes comme Libreville, Port-Gentil, Franceville, Oyem, Gamba, Mandji, etc.).\n");
        sb.append("Adapte la difficulté, le vocabulaire et le niveau de détail au niveau scolaire indiqué dans les contraintes ou que tu déduis.\n");

        if (mobile) {
            sb.append("Dans la partie \"conseils_pour_le_gabon\", ajoute si possible quelques idées sur la manière d'utiliser ");
            sb.append("un téléphone ou une tablette en appui (par exemple : réviser avec une application, prendre des photos de schémas, ");
            sb.append("écouter des explications audio en classe ou à la maison).\n");
        }

        sb.append("Réponds STRICTEMENT en JSON en respectant le schéma suivant :\n");
        sb.append("{\n");
        sb.append("  \"titre\": \"Titre du cours ou du thème\",\n");
        sb.append("  \"niveau\": \"Niveau scolaire (ex: CE1, 3e, Terminale, L1)\",\n");
        sb.append("  \"matiere\": \"Matière (ex: Mathématiques, Français, Histoire-Géographie)\",\n");
        sb.append("  \"objectifs\": [\"Objectif 1\", \"Objectif 2\"],\n");
        sb.append("  \"plan_cours\": [\"Etape 1 du cours\", \"Etape 2\", \"Etape 3\"],\n");
        sb.append("  \"exercices\": [\"Exercice 1\", \"Exercice 2\", \"Exercice 3\"],\n");
        sb.append("  \"evaluation\": [\"Question 1\", \"Question 2\", \"Question 3\"],\n");
        sb.append("  \"conseils_pour_le_gabon\": [\"Conseil 1\", \"Conseil 2\"]\n");
        sb.append("}\n");
        sb.append("Ne rajoute aucune autre clé que celles indiquées ci-dessus et ne mets aucun texte en dehors de l'objet JSON.");

        return sb.toString();
    }
}
