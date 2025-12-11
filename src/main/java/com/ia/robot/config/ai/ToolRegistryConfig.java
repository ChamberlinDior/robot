package com.ia.robot.config.ai;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Point d'extension dedie a l enregistrement/organisation des tools.
 *
 * Contexte projet:
 * - ChatClientConfig fixe l identite et le comportement global d ORWELL.
 * - AiConfig centralise la politique globale (education, ancrage temporel 2025,
 *   integrite academique, preparation analyse de fichiers).
 * - Ici, on garde une configuration robuste et evolutive pour les tools.
 *
 * V0 (etat stable):
 * - Les tools sont detectes par @Component dans:
 *   com.ia.robot.ai.tools
 *
 * V1 (objectif):
 * - Centraliser une strategie de selection, de nommage,
 *   d autorisations, et de monitoring des tools.
 * - Preparer l integration d un tool d analyse de fichiers
 *   sans casser l existant.
 *
 * Principes:
 * - Pas de couplage dur avec une implementation de tool precise.
 * - Pas de dependance additionnelle.
 * - Fournir une policy injectable par les futurs tools/services.
 */
@Configuration
public class ToolRegistryConfig {

    /**
     * Politique globale d acces aux tools pour ORWELL.
     *
     * Cette policy sert de garde-fou et de documentation vivante:
     * - quels packages sont consideres comme "safe"
     * - quels types de tools on privilegie
     * - comment on gere l ancrage pedagogique et la future analyse de fichiers
     *
     * Elle n impose rien par elle-meme tant que tu ne l utilises pas
     * dans les classes de tools. Donc aucun risque de casser le projet.
     */
    @Bean
    public OrwellToolPolicy orwellToolPolicy(AiConfig.OrwellAiPolicy aiPolicy) {

        // Packages autorises pour la detection logique et future validation.
        // Tu peux ajouter plus tard:
        // "com.ia.robot.ai.files" ou "com.ia.robot.ai.retrieval", etc.
        Set<String> allowedPackages = Set.of(
                "com.ia.robot.ai.tools"
        );

        // Noms de tools recommandés (whitelist souple).
        // Tant que tu n actives pas de verif stricte, ceci sert de reference.
        // Quand tu creeras FileTools, ajoute le nom ici pour plus de coherence.
        List<String> recommendedToolNames = List.of(
                "extractTextFromFile",
                "analyzeUploadedFile",
                "summarizeText",
                "generateExercises",
                "explainConcept"
        );

        // Regles d usage generales.
        String usageGuidelines = """
                - Utiliser les tools pour structurer les actions cote agent.
                - Favoriser les tools pedagogiques pour:
                  * resume de contenu,
                  * explications multi-niveaux,
                  * generation d exercices adaptes au contexte gabonais.
                - Respecter l integrite academique:
                  * ne pas aider a tricher a une evaluation en cours.
                - Respecter le cadre temporel:
                  * se caler sur 2025 pour les demandes 'recentes' ou 'actuelles'.
                - Pour les fichiers:
                  * privilegier extraction puis analyse pedagogique.
                """;

        // Mode strict desactive par defaut pour ne rien casser.
        // Tu pourras l activer quand tous tes tools seront stabilises.
        boolean strictMode = false;

        return new OrwellToolPolicy(
                aiPolicy.agentName(),
                aiPolicy.referenceYear(),
                aiPolicy.educationModeEnabled(),
                aiPolicy.fileAnalysisEnabled(),
                strictMode,
                allowedPackages,
                recommendedToolNames,
                usageGuidelines
        );
    }

    /**
     * Policy de gouvernance des tools.
     *
     * Champs:
     * - agentName: ORWELL.
     * - referenceYear: 2025 (coherence avec AiConfig).
     * - educationModeEnabled: active le biais pedagogique des tools.
     * - fileAnalysisEnabled: autorise les tools lies aux fichiers.
     * - strictMode: si true, tu pourras refuser les tools hors liste/pack.
     * - allowedPackages: packages consideres valides.
     * - recommendedToolNames: liste de reference pour nomination/coherence.
     * - usageGuidelines: regles textuelles partagees.
     *
     * Cette record peut etre consommee par:
     * - FileTools
     * - FileAnalysisService
     * - un futur ToolMonitor
     * - ou un validateur de registry si tu veux durcir le controle.
     */
    public record OrwellToolPolicy(
            String agentName,
            int referenceYear,
            boolean educationModeEnabled,
            boolean fileAnalysisEnabled,
            boolean strictMode,
            Set<String> allowedPackages,
            List<String> recommendedToolNames,
            String usageGuidelines
    ) {
    }
}
