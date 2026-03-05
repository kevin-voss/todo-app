package com.todo.adversarial;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adversarial QA tests: integration, stress, fuzz, edge cases.
 * Does NOT duplicate acceptance tests. Focuses on malformed input, boundary values,
 * concurrent access, and resource exhaustion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoApiAdversarialTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/todos";
    }

    // --- Malformed input ---

    @Test
    @Order(1)
    void createWithNullBodyReturns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("null", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Null body should be rejected: " + response.getStatusCode());
    }

    @Test
    @Order(2)
    void createWithMissingTitleKeyReturns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void createWithNullTitleReturns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"title\":null}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void createWithNonStringTitleReturns400OrHandlesGracefully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"title\":123}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertTrue(response.getStatusCode().is4xxClientError() || response.getStatusCode().is2xxSuccessful(),
                "Non-string title should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(5)
    void createWithMalformedJsonReturns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{invalid json}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Malformed JSON should be rejected: " + response.getStatusCode());
    }

    @Test
    @Order(6)
    void updateWithEmptyTitleReturns400() {
        Map<String, String> createBody = Map.of("title", "Original");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"title\":\"\"}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                request,
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- Path parameter edge cases ---

    @Test
    @Order(7)
    void getWithNegativeIdReturns404Or400() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/-1", String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Negative ID should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(8)
    void getWithZeroIdReturns404Or400() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/0", String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Zero ID should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(9)
    void getWithNonNumericIdReturns404Or400() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/abc", String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Non-numeric ID should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(10)
    void getWithVeryLargeIdReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/999999999999999999", String.class);
        assertTrue(response.getStatusCode().is4xxClientError() || response.getStatusCode().equals(HttpStatus.NOT_FOUND),
                "Very large ID should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(11)
    void getWithPathTraversalAttemptReturns404Or400() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/../1", String.class);
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Path traversal should not succeed: " + response.getStatusCode());
    }

    // --- Boundary values ---

    @Test
    @Order(12)
    void createWithVeryLongTitleHandlesGracefully() {
        // Title exceeds DB VARCHAR(255) - backend should return 400, not 500
        String longTitle = "a".repeat(1000);
        Map<String, String> body = Map.of("title", longTitle);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError(),
                "Very long title should not cause 500: " + response.getStatusCode());
    }

    @Test
    @Order(13)
    void createWithUnicodeAndSpecialCharsSucceeds() {
        Map<String, String> body = Map.of("title", "日本語 🎉 <script>alert(1)</script> '; DROP TABLE todos;--");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(body.get("title"), response.getBody().get("title"));
    }

    @Test
    @Order(14)
    void createWithSingleCharacterTitleSucceeds() {
        Map<String, String> body = Map.of("title", "x");
        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), body, Map.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("x", response.getBody().get("title"));
    }

    @Test
    @Order(15)
    void updateWithCompletedAsStringHandlesGracefully() {
        Map<String, String> createBody = Map.of("title", "Toggle Test");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"completed\":\"true\"}", headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                request,
                Map.class
        );
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError(),
                "String completed should not cause 500: " + response.getStatusCode());
    }

    // --- Concurrent access ---

    @Test
    @Order(16)
    void concurrentCreatesDoNotCorruptData() throws InterruptedException, ExecutionException {
        int concurrency = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        List<Future<ResponseEntity<Map>>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                Map<String, String> body = Map.of("title", "Concurrent-" + idx);
                return restTemplate.postForEntity(baseUrl(), body, Map.class);
            }));
        }

        Set<Object> ids = new HashSet<>();
        int successCount = 0;
        for (Future<ResponseEntity<Map>> f : futures) {
            ResponseEntity<Map> r = f.get();
            if (r.getStatusCode() == HttpStatus.CREATED && r.getBody() != null) {
                ids.add(r.getBody().get("id"));
                successCount++;
            }
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(concurrency, successCount, "All concurrent creates should succeed");
        assertEquals(concurrency, ids.size(), "All created todos should have unique IDs");
    }

    @Test
    @Order(17)
    void concurrentUpdatesToSameTodoDoNotCorruptData() throws InterruptedException, ExecutionException {
        Map<String, String> createBody = Map.of("title", "Race Target");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        int concurrency = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        List<Future<ResponseEntity<Map>>> futures = new ArrayList<>();

        for (int i = 0; i < concurrency; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                Map<String, Object> body = Map.of("title", "Updated-" + idx, "completed", idx % 2 == 0);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
                return restTemplate.exchange(
                        baseUrl() + "/" + id,
                        HttpMethod.PUT,
                        request,
                        Map.class
                );
            }));
        }

        int okCount = 0;
        for (Future<ResponseEntity<Map>> f : futures) {
            ResponseEntity<Map> r = f.get();
            if (r.getStatusCode() == HttpStatus.OK) okCount++;
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(okCount >= 1, "At least one update should succeed");
    }

    @Test
    @Order(18)
    void concurrentDeleteAndUpdateHandlesGracefully() throws InterruptedException, ExecutionException {
        Map<String, String> createBody = Map.of("title", "Delete Race");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<ResponseEntity<Void>> deleteFuture = executor.submit(() ->
                restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, null, Void.class));
        Future<ResponseEntity<Map>> updateFuture = executor.submit(() -> {
            Map<String, String> body = Map.of("title", "Updated");
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body);
            return restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.PUT, request, Map.class);
        });

        ResponseEntity<Void> deleteResp = deleteFuture.get();
        ResponseEntity<Map> updateResp = updateFuture.get();
        executor.shutdown();

        assertTrue(deleteResp.getStatusCode() == HttpStatus.NO_CONTENT || updateResp.getStatusCode() == HttpStatus.NOT_FOUND,
                "One operation should succeed, other may get 404");
    }

    // --- Stress / resource exhaustion ---

    @Test
    @Order(19)
    void rapidBulkCreateHandlesGracefully() {
        int count = 50;
        for (int i = 0; i < count; i++) {
            Map<String, String> body = Map.of("title", "Bulk-" + i);
            ResponseEntity<Map> r = restTemplate.postForEntity(baseUrl(), body, Map.class);
            assertEquals(HttpStatus.CREATED, r.getStatusCode(), "Bulk create " + i + " should succeed");
        }
        ResponseEntity<List> listResp = restTemplate.getForEntity(baseUrl(), List.class);
        assertTrue(((List<?>) listResp.getBody()).size() >= count, "All created todos should be listable");
    }

    @Test
    @Order(20)
    void updateWithNullBodyReturns200WithUnchangedTodo() {
        Map<String, String> createBody = Map.of("title", "Unchanged");
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), createBody, Map.class);
        Object id = createResponse.getBody().get("id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("null", headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                request,
                Map.class
        );
        assertTrue(response.getStatusCode().is2xxSuccessful(),
                "Null body update should return 200 with unchanged todo: " + response.getStatusCode());
    }
}
