package com.ia.robot.ai.tools;

import org.springframework.stereotype.Component;

@Component
public class ValidationTools {

    public boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String require(String value, String fieldName) {
        if (isBlank(value)) throw new IllegalArgumentException("Missing field: " + fieldName);
        return value.trim();
    }
}
