package com.ia.robot.config.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration racine pour les elements lies a l IA.
 *
 * Objectifs:
 * - Centraliser les conventions globales d ORWELL.
 * - Fournir un point unique pour les parametres transverses
 *   (identite, cadre pedagogique, cadre temporel, regles de prudence).
 * - Laisser ChatClientConfig gerer l instanciation concrete du ChatClient.
 * - Laisser ToolRegistryConfig gerer l exposition des tools.
 *
 * Philosophie:
 * - Ne pas casser le projet.
 * - Rester minimal en dependances.
 * - Ajouter des primitives stables que les autres couches pourront injecter.
 *
 * Extension V1.1:
 * - ORWELL tuteur prive multi-niveaux aligne contexte Gabon.
 * - Ajout d un cadre temporel explicite 2025 pour limiter les ancrages obsoletes.
 * - Preparation propre a la future analyse de fichiers.
 */
@Configuration
public class AiConfig {

    /**
     * Parametres globaux pour ORWELL.
     *
     * Cette structure est volontairement simple et stable:
     * - Injectables dans controllers/services/tools.
     * - Evolutives sans modifier le reste de l architecture.
     */
    @Bean
    public OrwellAiPolicy orwellAiPolicy() {
        return new OrwellAiPolicy(
                "ORWELL",
                2025,
                "fr",
                true,
                true,
                """
                ORWELL est un assistant IA professionnel et un tuteur prive multi-niveaux.
                Il accompagne pre-primaire, primaire, college, lycee et universitaire.
                Il adapte les explications au contexte gabonais et la reference francophone standard.
                """,
                """
                ORWELL privilegie l apprentissage actif.
                Il refuse d aider a tricher a une evaluation en cours.
                Il propose plutot methodes, entrainements et explications progressives.
                """,
                """
                Quand une information est sensible au temps ou incertaine,
                ORWELL signale l incertitude et conseille une verification.
                """
        );
    }

    /**
     * Politique globale ORWELL.
     *
     * Champs:
     * - agentName: nom stable de l assistant.
     * - referenceYear: ancrage temporel souhaite pour les reponses (2025).
     * - defaultLanguage: langue par defaut si l utilisateur ne precise pas.
     * - educationModeEnabled: active les comportements pedagogiques.
     * - fileAnalysisEnabled: active la logique de preparation a l analyse de fichiers.
     * - educationMission: message court de mission pedagogique.
     * - academicIntegrityPolicy: regles d integrite academique.
     * - safetyAndFreshnessPolicy: prudence sur l actualite et verification.
     *
     * Note:
     * - Cette record peut etre deplacee dans un package model/config
     *   si tu preferes plus tard, sans impact majeur.
     */
    public record OrwellAiPolicy(
            String agentName,
            int referenceYear,
            String defaultLanguage,
            boolean educationModeEnabled,
            boolean fileAnalysisEnabled,
            String educationMission,
            String academicIntegrityPolicy,
            String safetyAndFreshnessPolicy
    ) {
    }
}
