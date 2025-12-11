package com.ia.robot.repository;

import com.ia.robot.model.entity.AgentRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRunRepository extends JpaRepository<AgentRun, Long> {
}
