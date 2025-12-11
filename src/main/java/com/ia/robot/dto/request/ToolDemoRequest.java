package com.ia.robot.dto.request;

import java.util.Map;

/**
 * Requête de démonstration d'un tool.
 * Utile pour tester proprement les tools quand on activera la phase V1.
 */
public record ToolDemoRequest(
        String toolName,
        Map<String, Object> parameters
) {
}
