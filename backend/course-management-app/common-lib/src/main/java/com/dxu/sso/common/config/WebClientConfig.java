package com.dxu.sso.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("userWebClientBuilder")
    public WebClient.Builder userWebClientBuilder() {
        return WebClient.builder();
    }
}