package com.todo.acceptance;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spec acceptance tests for backend.
 * Covers AC-2 through AC-19 per requirements.md.
 * Tests are expected to fail until implementation exists.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpecAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private Environment environment;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/todos";
    }

    // covers AC-2
    @Test
    @Order(1)
    void backendIsBuiltWithJavaAndSpringBoot() {
        assertNotNull(restTemplate, "Spring Boot TestRestTemplate shall be available");
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertNotNull(response, "Backend shall respond to API requests");
    }

    // covers AC-3
    @Test
    @Order(2)
    void databaseIsH2SqlConfiguredViaSpringDatasourceUrl() {
        assertNotNull(environment, "Spring context shall be loaded");
        String url = environment.getProperty("spring.datasource.url");
        assertNotNull(url, "spring.datasource.url shall be configured");
        assertTrue(url.contains("h2"), "Database shall be H2 SQL (URL contains 'h2')");
    }

    // covers AC-4
    @Test
    @Order(3)
    void createTodoWithValidTitleReturns201AndCreatedObject() {
        Map<String, String> body = Map.of("title", "Spec Test Todo");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, jsonHeaders());

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("id"));
        assertTrue(response.getBody().containsKey("title"));
        assertEquals("Spec Test Todo", response.getBody().get("title"));
        assertTrue(response.getBody().containsKey("completed"));
    }

    // covers AC-5
    @Test
    @Order(4)
    void getTodosReturnsArrayOfAllTodos() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl(), List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
    }

    // covers AC-6
    @Test
    @Order(5)
    void putTodoWithTitleAndOrCompletedUpdatesAndReturns200() {
        Map<String, String> createBody = Map.of("title", "To Update");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(createBody, jsonHeaders()), Map.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Object id = createResponse.getBody().get("id");

        Map<String, Object> updateBody = Map.of("title", "Updated Title", "completed", true);
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateBody, jsonHeaders()),
                Map.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated Title", updateResponse.getBody().get("title"));
        assertTrue((Boolean) updateResponse.getBody().get("completed"));
    }

    // covers AC-7
    @Test
    @Order(6)
    void deleteTodoReturns204AndRemovesFromList() {
        Map<String, String> createBody = Map.of("title", "To Delete");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(createBody, jsonHeaders()), Map.class);
        Object id = createResponse.getBody().get("id");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    // covers AC-8
    @Test
    @Order(7)
    void togglingCompletedViaPutPersistsAndReturns200() {
        Map<String, String> createBody = Map.of("title", "Toggle Test");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(createBody, jsonHeaders()), Map.class);
        Object id = createResponse.getBody().get("id");

        Map<String, Boolean> updateBody = Map.of("completed", true);
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateBody, jsonHeaders()),
                Map.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertTrue((Boolean) updateResponse.getBody().get("completed"));
    }

    // covers AC-9
    @Test
    @Order(8)
    void h2IsConfiguredForTodoPersistence() {
        assertNotNull(dataSource, "DataSource shall be configured for persistence");
        assertNotNull(environment.getProperty("spring.datasource.url"), "H2 URL shall be set for Todo storage");
    }

    // covers AC-10
    @Test
    @Order(9)
    void applicationIsSingleUserWithNoAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "GET /api/todos shall succeed without auth (no 401)");
    }

    // covers AC-11 (backend part - JSON over HTTP)
    @Test
    @Order(10)
    void apiUsesJsonOverHttp() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().toString().toLowerCase().contains("application/json"));
    }

    // covers AC-12
    @Test
    @Order(11)
    void createReturns201UpdateReturns200DeleteReturns204NotFoundReturns404ValidationReturns400() {
        Map<String, String> createBody = Map.of("title", "Status Test");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(createBody, jsonHeaders()), Map.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Object id = createResponse.getBody().get("id");
        Map<String, String> updateBody = Map.of("title", "Updated");
        ResponseEntity<Map> updateResponse = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.PUT, new HttpEntity<>(updateBody, jsonHeaders()), Map.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(baseUrl() + "/999999", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());

        Map<String, String> invalidBody = Map.of("title", "");
        ResponseEntity<String> validationResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(invalidBody, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, validationResponse.getStatusCode());
    }

    // covers AC-13
    @Test
    @Order(12)
    void emptyOrWhitespaceOnlyTitleReturns400Not500() {
        Map<String, String> emptyBody = Map.of("title", "");
        ResponseEntity<String> emptyResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(emptyBody, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, emptyResponse.getStatusCode());

        Map<String, String> whitespaceBody = Map.of("title", "   ");
        ResponseEntity<String> whitespaceResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(whitespaceBody, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, whitespaceResponse.getStatusCode());
    }

    // covers AC-14
    @Test
    @Order(13)
    void getPutDeleteWithNonExistentIdReturns404() {
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl() + "/999999", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

        Map<String, String> body = Map.of("title", "Will Fail");
        ResponseEntity<Map> putResponse = restTemplate.exchange(baseUrl() + "/999999", HttpMethod.PUT, new HttpEntity<>(body, jsonHeaders()), Map.class);
        assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl() + "/999999", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
    }

    // covers AC-15
    @Test
    @Order(14)
    void createWithMissingTitleKeyOrNullTitleReturns400() {
        Map<String, Object> missingTitle = Map.of();
        ResponseEntity<String> response1 = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(missingTitle, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());

        HttpHeaders headers = jsonHeaders();
        HttpEntity<String> nullTitleRequest = new HttpEntity<>("{\"title\":null}", headers);
        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl(), nullTitleRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    // covers AC-16
    @Test
    @Order(15)
    void updateWithEmptyStringForTitleReturns400() {
        Map<String, String> createBody = Map.of("title", "Original");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(createBody, jsonHeaders()), Map.class);
        Object id = createResponse.getBody().get("id");

        Map<String, String> updateBody = Map.of("title", "");
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateBody, jsonHeaders()),
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }

    // covers AC-17
    @Test
    @Order(16)
    void titleExceedingMaxLength255Returns400Not500() {
        String longTitle = "a".repeat(256);
        Map<String, String> body = Map.of("title", longTitle);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(body, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // covers AC-18
    @Test
    @Order(17)
    void invalidPathParamsNegativeIdOrNonNumericReturn4xxNot500() {
        ResponseEntity<Map> negativeResponse = restTemplate.getForEntity(baseUrl() + "/-1", Map.class);
        assertTrue(negativeResponse.getStatusCode().is4xxClientError(), "Negative id shall return 4xx");

        ResponseEntity<Map> nonNumericResponse = restTemplate.getForEntity(baseUrl() + "/abc", Map.class);
        assertTrue(nonNumericResponse.getStatusCode().is4xxClientError(), "Non-numeric id shall return 4xx");
    }

    // covers AC-19
    @Test
    @Order(18)
    void malformedJsonReturns400Not500() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{ invalid json }", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Malformed JSON shall return 400, not 500");
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
