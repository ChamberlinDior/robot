package com.ia.robot.ai.agent;

import com.ia.robot.exception.AgentExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Orchestrateur V0.
 *
 * Expose des méthodes stables utilisées par:
 * - Controller
 * - Services
 */
@Component
public class SimpleAgentRunner {

    private static final Logger log = LoggerFactory.getLogger(SimpleAgentRunner.class);

    private final ArchitectAgent architectAgent;

    public SimpleAgentRunner(ArchitectAgent architectAgent) {
        this.architectAgent = architectAgent;
    }

    public String runArchitectPlan(AgentContext context, String userPrompt) {
        try {
            return architectAgent.generatePlan(context, userPrompt);
        } catch (Exception e) {
            /*===MODIF-ORWELL: log diagnostique ===*/
            log.error("[ORWELL][PLAN] Failed. requestId={}, agentName={}, error={}",
                    context != null ? context.getRequestId() : "null",
                    context != null ? context.getAgentName() : "null",
                    e.getMessage(),
                    e
            );
            throw new AgentExecutionException("ArchitectAgent plan execution failed.", e);
        }
    }

    public String runAsk(AgentContext context, String question) {
        try {
            return architectAgent.answer(context, question);
        } catch (Exception e) {
            /*===MODIF-ORWELL: log diagnostique ===*/
            log.error("[ORWELL][ASK] Failed. requestId={}, agentName={}, questionSize={}, error={}",
                    context != null ? context.getRequestId() : "null",
                    context != null ? context.getAgentName() : "null",
                    question == null ? 0 : question.length(),
                    e.getMessage(),
                    e
            );
            throw new AgentExecutionException("ArchitectAgent ask execution failed.", e);
        }
    }
}
