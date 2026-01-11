package com.eventplanner.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${booking.service.url:http://localhost:8083}")
    private String bookingServiceUrl;

    @Value("${eureka.client.enabled:true}")
    private boolean eurekaEnabled;

    @Bean
    @Primary
    public WebClient bookingServiceClient(WebClient.Builder builder) {
        String baseUrl = eurekaEnabled ? "http://booking-service" : bookingServiceUrl;
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}