package com.eventplanner.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
@Configuration
public class WebClientConfig {

   @Value("${EVENT_SERVICE_URL}")
private String eventServiceUrl;

@Bean
public WebClient eventServiceClient(WebClient.Builder builder) {
    return builder
            .baseUrl(eventServiceUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
}


