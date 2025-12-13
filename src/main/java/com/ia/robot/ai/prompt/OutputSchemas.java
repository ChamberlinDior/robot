package com.ia.robot.ai.prompt;

/**
 * Schemas de sortie attendus (documentation interne).
 *
 * Version EDU (Gabon) :
 * - Decrit le JSON que ORWELL doit renvoyer pour un plan de cours
 *   ou de revision, adapte au contexte educatif gabonais.
 * - DOIT rester coherent avec :
 *     - SystemPrompts.ARCHITECT_PLAN_SYSTEM
 *     - PromptTemplates.buildPlanPrompt(...)
 */
public final class OutputSchemas {

    private OutputSchemas() {
    }

    /**
     * Schema de sortie pour le plan pedagogue genere par ORWELL.
     *
     * Clefs obligatoires :
     *  - titre                 : titre du cours / chapitre
     *  - niveau                : niveau scolaire (CE2, 3e, Terminale, L1, etc.)
     *  - matiere               : matiere concernee (Mathematiques, Francais, etc.)
     *  - objectifs             : liste d'objectifs pedagogiques clairs
     *  - plan_cours            : deroulement du cours en etapes
     *  - exercices             : liste d'exercices progressifs
     *  - evaluation            : questions pour verifier les acquis
     *  - conseils_pour_le_gabon: conseils adaptes au contexte gabonais
     *
     * Remarque :
     *  - Les valeurs ci-dessous ne sont que des exemples.
     *  - ORWELL doit toujours respecter cette structure JSON,
     *    sans ajouter d'autres cles et sans texte hors JSON.
     */
    public static final String ARCHITECT_PLAN_SCHEMA = """
            {
              "titre": "Titre du cours ou du theme",
              "niveau": "Niveau scolaire (ex: CP, CE2, 3e, Terminale, L1)",
              "matiere": "Matiere (ex: Mathematiques, Francais, Histoire-Geographie)",
              "objectifs": [
                "Objectif 1 - ce que l'eleve doit savoir faire a la fin",
                "Objectif 2 - competences ou connaissances a renforcer"
              ],
              "plan_cours": [
                "Etape 1 - Accroche et rappel des pre-requis",
                "Etape 2 - Explication de la notion principale avec exemples du Gabon",
                "Etape 3 - Mise en pratique guidee en classe",
                "Etape 4 - Synthese et trace ecrite simple"
              ],
              "exercices": [
                "Exercice 1 - Application directe de la lecon",
                "Exercice 2 - Probleme un peu plus complexe",
                "Exercice 3 - Situation concrete liee a la vie au Gabon"
              ],
              "evaluation": [
                "Question 1 - verifie la comprehension de base",
                "Question 2 - verifie la capacite a expliquer avec ses mots",
                "Question 3 - verifie la capacite a appliquer dans une situation concrete"
              ],
              "conseils_pour_le_gabon": [
                "Conseil 1 - Idee pour adapter la lecon au contexte local (ex: exemples de Mandji, Gamba, Libreville, etc.)",
                "Conseil 2 - Suggestion d'activites simples, realisables avec peu de moyens (craies, ardoises, feuilles A4)",
                "Conseil 3 - Proposition d'astuces pour elever en difficulte (ex: utiliser des dessins, manipulations, jeux de roles)"
              ]
            }
            """;
}
