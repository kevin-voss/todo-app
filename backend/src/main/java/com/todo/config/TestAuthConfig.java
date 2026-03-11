package com.todo.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test configuration that provides a TestRestTemplate with JWT auth.
 * Active only when "test" profile is active. Automatically signs up and logs in
 * a test user before the first API request.
 */
@Configuration
@Profile("test")
public class TestAuthConfig {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "testpass123";

    private final AtomicReference<String> token = new AtomicReference<>();
    private final AtomicReference<String> baseUrl = new AtomicReference<>();

    @Bean
    @Primary
    public TestRestTemplate testRestTemplate(RestTemplateBuilder builder) {
        RestTemplateBuilder withAuth = builder.additionalInterceptors(
                (request, body, execution) -> {
                    String authToken = token.get();
                    if (authToken == null) {
                        String base = request.getURI().getScheme() + "://" + request.getURI().getHost()
                                + ":" + request.getURI().getPort();
                        baseUrl.compareAndSet(null, base);
                        authToken = obtainToken(baseUrl.get());
                        if (authToken != null) {
                            token.set(authToken);
                        }
                    }
                    if (authToken != null) {
                        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                    }
                    return execution.execute(request, body);
                });
        return new TestRestTemplate(withAuth);
    }

    private String obtainToken(String base) {
        if (base == null) {
            return null;
        }
        RestTemplate plain = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("email", TEST_EMAIL, "password", TEST_PASSWORD);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> signupResp = plain.exchange(
                base + "/api/auth/signup",
                HttpMethod.POST,
                request,
                Map.class
        );

        if (signupResp.getStatusCode() == HttpStatus.CREATED && signupResp.getBody() != null
                && signupResp.getBody().containsKey("token")) {
            return (String) signupResp.getBody().get("token");
        }

        ResponseEntity<Map> loginResp = plain.exchange(
                base + "/api/auth/login",
                HttpMethod.POST,
                request,
                Map.class
        );

        if (loginResp.getStatusCode() == HttpStatus.OK && loginResp.getBody() != null
                && loginResp.getBody().containsKey("token")) {
            return (String) loginResp.getBody().get("token");
        }

        return null;
    }
}
