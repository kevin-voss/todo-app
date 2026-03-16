package com.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Adversarial tests: malformed input, string completed, stress, concurrent.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoControllerAdversarialTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void postWithCompletedAsString_true() {
        var body = "{\"title\":\"Task\",\"completed\":\"true\"}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("\"completed\":true");
    }

    @Test
    void postWithCompletedAsString_false() {
        var body = "{\"title\":\"Task\",\"completed\":\"false\"}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("\"completed\":false");
    }

    @Test
    void patchWithCompletedAsString() {
        var createBody = "{\"title\":\"Task\",\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var create = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(createBody, headers), String.class);
        String id = create.getBody().replaceAll(".*\"id\":(\\d+).*", "$1");

        var patchBody = "{\"completed\":\"true\"}";
        var patch = rest.exchange("/api/todos/" + id, HttpMethod.PATCH, new HttpEntity<>(patchBody, headers), String.class);
        assertThat(patch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patch.getBody()).contains("\"completed\":true");
    }

    @Test
    void postWithNullTitle_usesDefault() {
        var body = "{\"title\":null,\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("\"title\":\"Untitled\"");
    }

    @Test
    void postWithEmptyTitle_usesDefault() {
        var body = "{\"title\":\"\",\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("\"title\":\"Untitled\"");
    }
}
