package com.ia.robot.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service JWT minimal.
 *
 * V0:
 * - Placeholder propre.
 * - Ne casse pas l'app si on n'active pas la sécurité.
 *
 * V1:
 * - Ajout parsing, validation, génération de tokens.
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret:}")
    private String secret;

    public boolean isConfigured() {
        return secret != null && !secret.isBlank();
    }

    public String getSecret() {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "JWT secret is not configured. Set app.jwt.secret in application.properties."
            );
        }
        return secret;
    }
}
