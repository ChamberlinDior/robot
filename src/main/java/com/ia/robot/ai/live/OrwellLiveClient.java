package com.ia.robot.ai.live;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ===CLE-MODIF-ORWELL===
 * Client "LIVE" minimal basé sur OpenAI Responses API + tool web_search.
 *
 * Objectif:
 * - Donner à ORWELL une capacité d'info à jour quand nécessaire.
 * - Ne PAS casser le mode standard Spring AI.
 *
 * Stratégie:
 * - Activable via variable d'env:
 *     ORWELL_WEB_ENABLED=true
 * - Modèle dédié:
 *     OPENAI_WEB_MODEL=gpt-5.1-codex-max (ou un autre modèle compatible web_search)
 * - Effort de reasoning configurable:
 *     ORWELL_WEB_REASONING=low|medium|high|none (selon modèle)
 */
@Component
public class OrwellLiveClient {

    private static final String RESPONSES_URL = "https://api.openai.com/v1/responses";

    private final ObjectMapper objectMapper;
    private final HttpClient http;

    @Value("${orwell.web.enabled:false}")
    private boolean enabled;

    @Value("${orwell.web.model:gpt-5.1-codex-max}")
    private String model;

    @Value("${orwell.web.reasoning:low}")
    private String reasoningEffort;

    public OrwellLiveClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Appel LIVE avec web_search.
     * Retourne une réponse texte robuste (extraction tolérante).
     */
    public String askLive(String systemPrompt, String userQuestion) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set for LIVE mode.");
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);

            // Input style "messages"
            payload.put("input", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userQuestion)
            ));

            // Outil de recherche web (si supporté par le modèle)
            payload.put("tools", List.of(
                    Map.of("type", "web_search")
            ));

            // Reasoning option (tolérant selon modèles)
            payload.put("reasoning", Map.of("effort", reasoningEffort));

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(RESPONSES_URL))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() >= 400) {
                throw new IllegalStateException("LIVE mode HTTP error: " + res.statusCode() + " - " + res.body());
            }

            JsonNode root = objectMapper.readTree(res.body());
            String text = extractText(root);

            if (text == null || text.isBlank()) {
                // fallback soft: renvoyer une version courte
                return "Je n'ai pas pu extraire une réponse textuelle claire du mode LIVE.";
            }

            return text;

        } catch (Exception e) {
            throw new IllegalStateException("LIVE mode failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extraction tolérante:
     * - Essaie output_text direct
     * - Sinon parcourt output/content
     */
    private String extractText(JsonNode root) {
        if (root == null) return null;

        if (root.hasNonNull("output_text")) {
            return root.get("output_text").asText();
        }

        JsonNode output = root.get("output");
        if (output != null && output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    for (JsonNode c : content) {
                        // Cas fréquents: type=output_text ou type=text
                        JsonNode type = c.get("type");
                        if (type != null) {
                            String t = type.asText("");
                            if ("output_text".equalsIgnoreCase(t) || "text".equalsIgnoreCase(t)) {
                                JsonNode textNode = c.get("text");
                                if (textNode != null && !textNode.asText("").isBlank()) {
                                    return textNode.asText();
                                }
                            }
                        }
                        // fallback très permissif
                        JsonNode textNode = c.get("text");
                        if (textNode != null && !textNode.asText("").isBlank()) {
                            return textNode.asText();
                        }
                    }
                }
            }
        }
        return null;
    }
}
