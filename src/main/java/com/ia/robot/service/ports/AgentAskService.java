package com.ia.robot.service.ports;

import com.ia.robot.dto.request.AgentAskRequest;
import com.ia.robot.dto.response.AgentAskResponse;

/**
 * Contrat de Q/A simple.
 */
public interface AgentAskService {

    AgentAskResponse ask(AgentAskRequest request);

    String askRaw(AgentAskRequest request);
}
