package com.ia.robot.ai.prompt;

import java.util.List;

/**
 * Templates d'instructions utilisateur.
 */
public final class PromptTemplates {

    private PromptTemplates() {}

    /*===MODIF-ORWELL: version stable sans accents sensibles ===*/
    public static String buildPlanPrompt(String domain, List<String> constraints, boolean mobile) {
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
            sb.append("Propose aussi les ecrans React Native minimaux lies a ce domaine.\n");
        }

        sb.append("Reponds strictement en JSON conforme au schema attendu.");

        return sb.toString();
    }
}
