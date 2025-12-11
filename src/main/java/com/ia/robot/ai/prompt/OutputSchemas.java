package com.ia.robot.ai.prompt;

/**
 * Schemas de sortie attendus (documentation interne V0).
 */
public final class OutputSchemas {

    private OutputSchemas() {}

    public static final String ARCHITECT_PLAN_SCHEMA = """
            {
              "architecture": {
                "style": "clean|hexagonal|layered",
                "packages": ["controller", "dto", "service", "model", "repository"],
                "notes": []
              },
              "entities": [
                { "name": "", "fields": [], "relations": [] }
              ],
              "endpoints": [
                { "method": "GET|POST|PUT|PATCH|DELETE", "path": "", "description": "" }
              ],
              "dto": [
                { "name": "", "type": "request|response", "fields": [] }
              ],
              "screens_mobile": [
                { "name": "", "purpose": "" }
              ],
              "checklist": []
            }
            """;
}
