package com.ia.robot.model.entity;

import com.ia.robot.model.enums.AgentType;
import com.ia.robot.model.enums.OutputFormat;
import com.ia.robot.model.enums.RunStatus;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Représente une exécution complète d'un agent.
 *
 * Objectifs:
 * - Traçabilité
 * - Audit
 * - Debug
 * - Historique
 */
@Entity
@Table(name = "agent_runs",
        indexes = {
                @Index(name = "idx_agent_runs_request_id", columnList = "request_id"),
                @Index(name = "idx_agent_runs_status", columnList = "status"),
                @Index(name = "idx_agent_runs_agent_name", columnList = "agent_name")
        })
public class AgentRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", length = 80, nullable = false)
    private String requestId;

    @Column(name = "agent_name", length = 80, nullable = false)
    private String agentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", length = 30, nullable = false)
    private AgentType agentType = AgentType.ARCHITECT;

    @Enumerated(EnumType.STRING)
    @Column(name = "output_format", length = 20, nullable = false)
    private OutputFormat outputFormat = OutputFormat.JSON;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private RunStatus status = RunStatus.PENDING;

    @Column(name = "domain", length = 120)
    private String domain;

    @Lob
    @Column(name = "input_prompt", columnDefinition = "LONGTEXT")
    private String inputPrompt;

    @Lob
    @Column(name = "output_raw", columnDefinition = "LONGTEXT")
    private String outputRaw;

    @Lob
    @Column(name = "error_message", columnDefinition = "LONGTEXT")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public AgentRun() {
    }

    // -------------------------
    // Lifecycle
    // -------------------------

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // -------------------------
    // Helpers métier
    // -------------------------

    public void markRunning() {
        this.status = RunStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void markSuccess(String outputRaw) {
        this.status = RunStatus.SUCCESS;
        this.outputRaw = outputRaw;
        this.finishedAt = Instant.now();
    }

    public void markFailed(String message) {
        this.status = RunStatus.FAILED;
        this.errorMessage = message;
        this.finishedAt = Instant.now();
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getInputPrompt() {
        return inputPrompt;
    }

    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
    }

    public String getOutputRaw() {
        return outputRaw;
    }

    public void setOutputRaw(String outputRaw) {
        this.outputRaw = outputRaw;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
