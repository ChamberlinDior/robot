package com.ia.robot.repository;

import com.ia.robot.model.entity.AgentPromptLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentPromptLogRepository extends JpaRepository<AgentPromptLog, Long> {
}
