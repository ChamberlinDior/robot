package com.ia.robot.ai.agent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Contexte d'execution d'un agent ORWELL.
 *
 * Version EDU GABON :
 * - Fournit un contexte riche, oriente education au Gabon.
 * - Permet a l'agent d'adapter ses reponses au niveau scolaire,
 *   au type de profil (eleve, enseignant, parent, admin) et au
 *   contexte geographique (Gabon, ville, etablissement).
 *
 * Conventions :
 * - Aucun Lombok (pour eviter les soucis de build).
 * - Builder manuel, simple et fiable.
 *
 * Champs principaux :
 *  - requestId        : identifiant unique de la requete (UUID par defaut)
 *  - agentName        : nom de l'agent (ex: "ORWELL")
 *  - createdAt        : horodatage ISO-8601
 *
 * Champs EDU / GABON :
 *  - tenant           : contexte fonctionnel (ex: "GABON_EDU")
 *  - country          : pays (par defaut "Gabon")
 *  - city             : ville (Libreville, Mandji, Gamba, Port-Gentil, etc.)
 *  - establishment    : nom de l'etablissement (ex: "Lycee de Mandji")
 *  - profileType      : "ELEVE", "ENSEIGNANT", "PARENT", "ADMIN", "AUTRE"
 *  - educationLevel   : niveau (ex: "Pre-scolaire", "CP", "CE2", "3e",
 *                        "Terminale", "L1", "Master")
 *  - subject          : matiere principale concernee
 *                        (Mathematiques, Francais, Histoire-Geographie, etc.)
 *  - locale           : code langue/pays (par defaut "fr-GA")
 *  - channel          : canal d'acces (ex: "MOBILE", "WEB", "TABLETTE")
 */
public final class AgentContext {

    // --- Champs techniques de base ---
    private final String requestId;
    private final String agentName;
    private final String createdAt;

    // --- Champs EDU / GABON ---
    private final String tenant;
    private final String country;
    private final String city;
    private final String establishment;
    private final String profileType;
    private final String educationLevel;
    private final String subject;
    private final String locale;
    private final String channel;

    private AgentContext(Builder builder) {
        this.requestId = builder.requestId != null
                ? builder.requestId
                : UUID.randomUUID().toString();

        this.agentName = builder.agentName != null
                ? builder.agentName
                : "UnknownAgent";

        this.createdAt = builder.createdAt != null
                ? builder.createdAt
                : Instant.now().toString();

        // Contexte EDU / GABON avec valeurs par defaut stables
        this.tenant = builder.tenant != null
                ? builder.tenant
                : "GABON_EDU";

        this.country = builder.country != null
                ? builder.country
                : "Gabon";

        this.city = builder.city;
        this.establishment = builder.establishment;

        this.profileType = builder.profileType != null
                ? builder.profileType
                : "AUTRE";

        this.educationLevel = builder.educationLevel;
        this.subject = builder.subject;

        this.locale = builder.locale != null
                ? builder.locale
                : "fr-GA";

        this.channel = builder.channel != null
                ? builder.channel
                : "MOBILE";
    }

    // -------------------
    // Getters
    // -------------------

    public String getRequestId() {
        return requestId;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getTenant() {
        return tenant;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEstablishment() {
        return establishment;
    }

    public String getProfileType() {
        return profileType;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public String getSubject() {
        return subject;
    }

    public String getLocale() {
        return locale;
    }

    public String getChannel() {
        return channel;
    }

    // -------------------
    // Helpers statiques EDU / GABON
    // -------------------

    /**
     * Contexte par defaut pour un ELEVE au Gabon.
     *
     * Exemple :
     *   AgentContext ctx = AgentContext.forGabonStudent(
     *       "Libreville",
     *       "Lycee de Mandji",
     *       "Terminale",
     *       "Mathematiques"
     *   );
     */
    public static AgentContext forGabonStudent(String city,
                                               String establishment,
                                               String educationLevel,
                                               String subject) {
        return AgentContext.builder()
                .agentName("ORWELL")
                .profileType("ELEVE")
                .city(city)
                .establishment(establishment)
                .educationLevel(educationLevel)
                .subject(subject)
                .country("Gabon")
                .tenant("GABON_EDU")
                .locale("fr-GA")
                .channel("MOBILE")
                .build();
    }

    /**
     * Contexte par defaut pour un ENSEIGNANT au Gabon.
     */
    public static AgentContext forGabonTeacher(String city,
                                               String establishment,
                                               String educationLevel,
                                               String subject) {
        return AgentContext.builder()
                .agentName("ORWELL")
                .profileType("ENSEIGNANT")
                .city(city)
                .establishment(establishment)
                .educationLevel(educationLevel)
                .subject(subject)
                .country("Gabon")
                .tenant("GABON_EDU")
                .locale("fr-GA")
                .channel("WEB")
                .build();
    }

    /**
     * Contexte par defaut pour un PARENT au Gabon.
     */
    public static AgentContext forGabonParent(String city,
                                              String establishment,
                                              String childEducationLevel,
                                              String subject) {
        return AgentContext.builder()
                .agentName("ORWELL")
                .profileType("PARENT")
                .city(city)
                .establishment(establishment)
                .educationLevel(childEducationLevel)
                .subject(subject)
                .country("Gabon")
                .tenant("GABON_EDU")
                .locale("fr-GA")
                .channel("MOBILE")
                .build();
    }

    /**
     * Representation courte pour les logs techniques.
     * Evite de logguer des donnees sensibles.
     */
    public String toSafeLogString() {
        return "AgentContext{" +
                "requestId='" + requestId + '\'' +
                ", agentName='" + agentName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", tenant='" + tenant + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", establishment='" + establishment + '\'' +
                ", profileType='" + profileType + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", subject='" + subject + '\'' +
                ", locale='" + locale + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return toSafeLogString();
    }

    // -------------------
    // Builder
    // -------------------

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String requestId;
        private String agentName;
        private String createdAt;

        private String tenant;
        private String country;
        private String city;
        private String establishment;
        private String profileType;
        private String educationLevel;
        private String subject;
        private String locale;
        private String channel;

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder establishment(String establishment) {
            this.establishment = establishment;
            return this;
        }

        public Builder profileType(String profileType) {
            this.profileType = profileType;
            return this;
        }

        public Builder educationLevel(String educationLevel) {
            this.educationLevel = educationLevel;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public AgentContext build() {
            return new AgentContext(this);
        }
    }

    // -------------------
    // Egalite / hashCode
    // -------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentContext that)) return false;
        return Objects.equals(requestId, that.requestId)
                && Objects.equals(agentName, that.agentName)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(tenant, that.tenant)
                && Objects.equals(country, that.country)
                && Objects.equals(city, that.city)
                && Objects.equals(establishment, that.establishment)
                && Objects.equals(profileType, that.profileType)
                && Objects.equals(educationLevel, that.educationLevel)
                && Objects.equals(subject, that.subject)
                && Objects.equals(locale, that.locale)
                && Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                requestId,
                agentName,
                createdAt,
                tenant,
                country,
                city,
                establishment,
                profileType,
                educationLevel,
                subject,
                locale,
                channel
        );
    }
}
