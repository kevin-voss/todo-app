package com.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AC-1 through AC-4: Backend REST API acceptance tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoControllerAcceptanceTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void getTodos_emptyList() {
        var res = rest.getForEntity("/api/todos", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo("[]");
    }

    @Test
    void postTodo_createsAndReturns() {
        var body = "{\"title\":\"Buy milk\",\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("\"title\":\"Buy milk\"");
        assertThat(res.getBody()).contains("\"completed\":false");
    }

    @Test
    void patchTodo_updatesCompleted() {
        var createBody = "{\"title\":\"Task\",\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var create = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(createBody, headers), String.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = create.getBody().replaceAll(".*\"id\":(\\d+).*", "$1");

        var patchBody = "{\"completed\":true}";
        var patch = rest.exchange("/api/todos/" + id, HttpMethod.PATCH, new HttpEntity<>(patchBody, headers), String.class);
        assertThat(patch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patch.getBody()).contains("\"completed\":true");
    }

    @Test
    void deleteTodo_removes() {
        var createBody = "{\"title\":\"To delete\",\"completed\":false}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var create = rest.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(createBody, headers), String.class);
        String id = create.getBody().replaceAll(".*\"id\":(\\d+).*", "$1");

        var del = rest.exchange("/api/todos/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var list = rest.getForEntity("/api/todos", String.class);
        assertThat(list.getBody()).doesNotContain("\"id\":" + id);
    }

    @Test
    void patchNonExistent_returns404() {
        var body = "{\"completed\":true}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var res = rest.exchange("/api/todos/99999", HttpMethod.PATCH, new HttpEntity<>(body, headers), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
