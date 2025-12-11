package com.ia.robot.ai.agent;

import java.time.Instant;
import java.util.UUID;

/**
 * Contexte d'exécution minimal d'un agent.
 *
 * Version V0:
 * - Pas de Lombok (pour éviter les erreurs builder introuvable)
 * - Builder manuel simple et fiable
 */
public final class AgentContext {

    private final String requestId;
    private final String agentName;
    private final String createdAt;

    private AgentContext(Builder builder) {
        this.requestId = builder.requestId != null ? builder.requestId : UUID.randomUUID().toString();
        this.agentName = builder.agentName != null ? builder.agentName : "UnknownAgent";
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now().toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String requestId;
        private String agentName;
        private String createdAt;

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

        public AgentContext build() {
            return new AgentContext(this);
        }
    }
}
