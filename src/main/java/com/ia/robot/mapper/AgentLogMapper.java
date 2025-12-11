package com.ia.robot.mapper;

import com.ia.robot.ai.agent.AgentContext;
import com.ia.robot.dto.request.AgentAskRequest;
import com.ia.robot.dto.request.AgentPlanRequest;
import com.ia.robot.model.entity.AgentPromptLog;
import com.ia.robot.model.entity.AgentRun;
import com.ia.robot.model.enums.AgentType;
import com.ia.robot.model.enums.OutputFormat;

import org.springframework.stereotype.Component;

@Component
public class AgentLogMapper {

    public AgentRun toRun(AgentContext ctx, String domain, String prompt) {
        AgentRun run = new AgentRun();
        run.setRequestId(ctx.getRequestId());
        run.setAgentName(ctx.getAgentName());
        run.setAgentType(AgentType.ARCHITECT);
        run.setOutputFormat(OutputFormat.JSON);
        run.setDomain(domain);
        run.setInputPrompt(prompt);
        return run;
    }

    public AgentPromptLog toUserPrompt(AgentRun run, String content) {
        return new AgentPromptLog(run, "user", content);
    }

    public AgentPromptLog toSystemPrompt(AgentRun run, String content) {
        return new AgentPromptLog(run, "system", content);
    }

    public AgentPromptLog toAssistantOutput(AgentRun run, String content) {
        return new AgentPromptLog(run, "assistant", content);
    }

    // Helpers V0 pour audit facile
    public String extractDomainSafe(AgentPlanRequest req) {
        return req != null ? req.domain() : null;
    }

    public String extractQuestionSafe(AgentAskRequest req) {
        return req != null ? req.question() : null;
    }
}
