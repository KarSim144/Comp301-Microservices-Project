@Configuration
public class WebClientConfig {

    private static final String EVENT_SERVICE_URL =
        "https://event-catalog-service-uik6.onrender.com";

    @Bean
    public WebClient eventServiceClient(WebClient.Builder builder) {
        return builder
                .baseUrl(EVENT_SERVICE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
