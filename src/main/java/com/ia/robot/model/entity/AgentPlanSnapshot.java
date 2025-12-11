package com.ia.robot.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Snapshot métier du plan généré par l'agent.
 *
 * Sépare l'historique "plan" de la log brute.
 */
@Entity
@Table(name = "agent_plan_snapshots",
        indexes = {
                @Index(name = "idx_plan_snapshots_run_id", columnList = "run_id"),
                @Index(name = "idx_plan_snapshots_domain", columnList = "domain")
        })
public class AgentPlanSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Run parent.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private AgentRun run;

    @Column(name = "domain", length = 120)
    private String domain;

    /**
     * JSON normalisé du plan.
     */
    @Lob
    @Column(name = "plan_json", columnDefinition = "LONGTEXT", nullable = false)
    private String planJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AgentPlanSnapshot() {
    }

    public AgentPlanSnapshot(AgentRun run, String domain, String planJson) {
        this.run = run;
        this.domain = domain;
        this.planJson = planJson;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPlanJson() {
        return planJson;
    }

    public void setPlanJson(String planJson) {
        this.planJson = planJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
