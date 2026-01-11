package com.eventplanner.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${event.service.url:http://localhost:8082}")
    private String eventServiceUrl;

    @Value("${eureka.client.enabled:true}")
    private boolean eurekaEnabled;

    @Bean
    @Primary
    public WebClient eventServiceClient(WebClient.Builder builder) {
        String baseUrl = eurekaEnabled ? "http://event-catalog-service" : eventServiceUrl;
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
