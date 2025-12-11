package com.ia.robot.ai.tools;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DevTools {

    public Map<String, String> stack() {
        return Map.of(
                "java", "21",
                "springBoot", "3.5.x",
                "ai", "Spring AI OpenAI",
                "db", "MySQL/Hibernate"
        );
    }
}
