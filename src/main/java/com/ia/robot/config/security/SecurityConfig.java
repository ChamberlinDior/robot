package com.ia.robot.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité V0.
 *
 * Objectif:
 * - En dev (par défaut): sécurité ouverte pour accélérer le build.
 * - Plus tard: activation progressive du JWT.
 *
 * Active le mode sécurisé avec:
 *   app.security.enabled=true
 *
 * Auteur (à ta demande) : Eva
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // ✅ Configuration commune
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults());

        // -------------------------
        // Mode V0 (dev-friendly)
        // -------------------------
        if (!securityEnabled) {
            http
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .httpBasic(b -> b.disable())
                    .formLogin(f -> f.disable());

            return http.build();
        }

        // -------------------------
        // Mode sécurisé (futur JWT)
        // -------------------------
        http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Health
                        .requestMatchers(HttpMethod.GET, "/api/health/**").permitAll()

                        // Agent APIs - protégées quand securityEnabled=true
                        .requestMatchers("/api/agent/**").authenticated()

                        // Tout le reste
                        .anyRequest().authenticated()
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable());

        /**
         * ✅ Quand tu activeras réellement le JWT:
         * - On branchera ici ton JwtAuthFilter avec:
         *   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
         *
         * Tu as déjà les classes JwtAuthFilter / JwtService dans ton package,
         * donc on le fera en version "propre et finale" au moment voulu.
         */

        return http.build();
    }
}
