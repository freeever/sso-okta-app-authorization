package com.dxu.sso.course.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(org.springframework.security.config.web.server.ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges.pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())   // OKTA login
                .oauth2Client(Customizer.withDefaults()); // OAuth2 client for token relay

        return http.build();
    }
}

