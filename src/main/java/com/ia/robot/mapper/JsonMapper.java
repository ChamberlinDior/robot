package com.ia.robot.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ia.robot.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class JsonMapper {

    private final ObjectMapper om;

    public JsonMapper(ObjectMapper om) {
        this.om = om;
    }

    public String toJson(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("JSON serialization failed.", e);
        }
    }

    public <T> T fromJson(String json, Class<T> type) {
        try {
            return om.readValue(json, type);
        } catch (Exception e) {
            throw new BadRequestException("JSON parsing failed.", e);
        }
    }
}
