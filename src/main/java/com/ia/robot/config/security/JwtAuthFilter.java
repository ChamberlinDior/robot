package com.ia.robot.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre JWT minimal.
 *
 * V0:
 * - Ne fait rien de bloquant.
 * - Existera proprement quand tu passeras en V1.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!securityEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        // V1: ici tu ajouteras l'extraction du header Authorization,
        // la validation du token, puis le SecurityContext.
        if (!jwtService.isConfigured()) {
            // Si la sécurité est activée mais pas configurée, on laisse passer
            // pour éviter un blocage brutal en dev.
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
