package com.ia.robot.repository;

import com.ia.robot.model.entity.AgentPlanSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentPlanSnapshotRepository extends JpaRepository<AgentPlanSnapshot, Long> {
}
