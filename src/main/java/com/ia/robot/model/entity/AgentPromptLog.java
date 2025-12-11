package com.ia.robot.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Journal fin des échanges d'un agent.
 *
 * Utile pour:
 * - debug prompts
 * - audit qualité
 * - amélioration des templates
 */
@Entity
@Table(name = "agent_prompt_logs",
        indexes = {
                @Index(name = "idx_prompt_logs_run_id", columnList = "run_id"),
                @Index(name = "idx_prompt_logs_created_at", columnList = "created_at")
        })
public class AgentPromptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Run parent.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private AgentRun run;

    /**
     * system | user | assistant | tool
     */
    @Column(name = "role", length = 30, nullable = false)
    private String role;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "token_estimate")
    private Integer tokenEstimate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AgentPromptLog() {
    }

    public AgentPromptLog(AgentRun run, String role, String content) {
        this.run = run;
        this.role = role;
        this.content = content;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public AgentRun getRun() {
        return run;
    }

    public void setRun(AgentRun run) {
        this.run = run;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTokenEstimate() {
        return tokenEstimate;
    }

    public void setTokenEstimate(Integer tokenEstimate) {
        this.tokenEstimate = tokenEstimate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
