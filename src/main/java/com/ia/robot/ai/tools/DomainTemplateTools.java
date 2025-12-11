package com.ia.robot.ai.tools;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DomainTemplateTools {

    public Map<String, Object> domainTemplate(String domain) {
        return Map.of(
                "domain", domain,
                "entities", List.of(domain, domain + "History"),
                "endpoints", List.of(
                        "POST /api/" + domain.toLowerCase(),
                        "GET /api/" + domain.toLowerCase() + "/{id}",
                        "GET /api/" + domain.toLowerCase(),
                        "PUT /api/" + domain.toLowerCase() + "/{id}",
                        "DELETE /api/" + domain.toLowerCase() + "/{id}"
                )
        );
    }
}
