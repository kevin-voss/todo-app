package com.todo.acceptance;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance tests for Todo REST API.
 * Validates CRUD operations, validation, and error handling per API contracts.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoApiAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/todos";
    }

    // covers AC-4 (backend runnable)
    @Test
    @Order(1)
    void backendCanBeStartedAndResponds() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // covers AC-6
    @Test
    @Order(2)
    void createTodoWithTitleReturns201AndCreatedObject() {
        Map<String, String> body = Map.of("title", "Test Todo");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("id"));
        assertTrue(response.getBody().containsKey("title"));
        assertEquals("Test Todo", response.getBody().get("title"));
        assertTrue(response.getBody().containsKey("completed"));
    }

    // covers AC-7
    @Test
    @Order(3)
    void getTodosReturnsArrayOfAllTodoItems() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl(), List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
    }

    // covers AC-8
    @Test
    @Order(4)
    void updateTodoTitleReturns200AndUpdatedObject() {
        // Create a todo first
        Map<String, String> createBody = Map.of("title", "Original Title");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Object id = createResponse.getBody().get("id");

        // Update it
        Map<String, String> updateBody = Map.of("title", "Updated Title");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(updateBody);
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                request,
                Map.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated Title", updateResponse.getBody().get("title"));
    }

    // covers AC-9
    @Test
    @Order(5)
    void deleteTodoReturns204AndRemovesFromList() {
        Map<String, String> createBody = Map.of("title", "To Delete");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify it's gone
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    // covers AC-10
    @Test
    @Order(6)
    void updateTodoCompletedStatusPersists() {
        Map<String, String> createBody = Map.of("title", "Toggle Test");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        Map<String, Boolean> updateBody = Map.of("completed", true);
        HttpEntity<Map<String, Boolean>> request = new HttpEntity<>(updateBody);
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                request,
                Map.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertTrue((Boolean) updateResponse.getBody().get("completed"));
    }

    // covers AC-11
    @Test
    @Order(7)
    void backendRespondsWithJsonOverHttp() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));
    }

    // covers AC-13
    @Test
    @Order(8)
    void createTodoWithEmptyTitleIsRejected() {
        Map<String, String> body = Map.of("title", "");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(9)
    void createTodoWithWhitespaceOnlyTitleIsRejected() {
        Map<String, String> body = Map.of("title", "   ");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // covers AC-14
    @Test
    @Order(10)
    void getNonExistentTodoReturns404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl() + "/999999", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(11)
    void updateNonExistentTodoReturns404() {
        Map<String, String> body = Map.of("title", "Will Fail");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/999999",
                HttpMethod.PUT,
                request,
                Map.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(12)
    void deleteNonExistentTodoReturns404() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/999999",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
