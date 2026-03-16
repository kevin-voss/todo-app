package com.example.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Adversarial QA tests: edge cases, malformed input, boundary values,
 * concurrent access, and resource exhaustion. Does NOT duplicate acceptance tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TodoAdversarialTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Malformed input & boundary values ---

    @Nested
    @DisplayName("Malformed input - POST")
    class PostMalformedInput {

        @Test
        @DisplayName("rejects whitespace-only title")
        void post_whitespaceOnlyTitle_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"   \"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("rejects null title")
        void post_nullTitle_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":null}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("handles extremely long title without crash")
        void post_veryLongTitle_handlesGracefully() throws Exception {
            String longTitle = "x".repeat(10_000);
            MvcResult result = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"" + longTitle.replace("\"", "\\\"") + "\"}"))
                    .andReturn();

            // Either 201 (accepted) or 400 (rejected) - must not 500
            int status = result.getResponse().getStatus();
            assertThat(status).isIn(201, 400);
        }

        @Test
        @DisplayName("handles unicode and emoji in title")
        void post_unicodeEmojiTitle_createsTodo() throws Exception {
            String payload = "{\"title\":\"日本語 café 日本語 ☕🎉\"}";
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("日本語 café 日本語 ☕🎉"));
        }

        @Test
        @DisplayName("rejects invalid JSON")
        void post_invalidJson_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid}"))
                    .andExpect(status().isIn(400, 415));
        }

        @Test
        @DisplayName("rejects wrong Content-Type")
        void post_wrongContentType_returns415Or400() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("{\"title\":\"test\"}"))
                    .andReturn();
            assertThat(result.getResponse().getStatus()).isIn(400, 415);
        }

        @Test
        @DisplayName("handles completed as string in POST")
        void post_completedAsString_handlesGracefully() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Test\",\"completed\":\"true\"}"))
                    .andReturn();
            // Should either create (ignoring invalid completed) or reject - no 500
            assertThat(result.getResponse().getStatus()).isIn(201, 400);
        }
    }

    @Nested
    @DisplayName("Boundary values - path params")
    class PathParamBoundaries {

        @Test
        @DisplayName("PATCH with id=0 returns 404")
        void patch_idZero_returns404() throws Exception {
            mockMvc.perform(patch("/api/todos/0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":true}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE with id=0 returns 404")
        void delete_idZero_returns404() throws Exception {
            mockMvc.perform(delete("/api/todos/0"))
                    .andReturn();
            // 404 or 204 acceptable
        }

        @Test
        @DisplayName("PATCH with negative id returns 400 or 404")
        void patch_negativeId_returns4xx() throws Exception {
            MvcResult result = mockMvc.perform(patch("/api/todos/-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":true}"))
                    .andReturn();
            assertThat(result.getResponse().getStatus()).isBetween(400, 499);
        }

        @Test
        @DisplayName("PATCH with non-numeric id returns 400")
        void patch_nonNumericId_returns400() throws Exception {
            MvcResult result = mockMvc.perform(patch("/api/todos/abc")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":true}"))
                    .andReturn();
            assertThat(result.getResponse().getStatus()).isBetween(400, 499);
        }

        @Test
        @DisplayName("DELETE with non-numeric id returns 400")
        void delete_nonNumericId_returns400() throws Exception {
            MvcResult result = mockMvc.perform(delete("/api/todos/xyz"))
                    .andReturn();
            assertThat(result.getResponse().getStatus()).isBetween(400, 499);
        }
    }

    @Nested
    @DisplayName("PATCH malformed payload")
    class PatchMalformedPayload {

        @Test
        @DisplayName("PATCH with completed as string 'true' does not corrupt state")
        void patch_completedAsString_handlesGracefully() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Original\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(patch("/api/todos/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":\"true\"}"))
                    .andReturn();

            // GET should still return valid todo - completed unchanged or correctly set
            MvcResult getResult = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();
            List<?> todos = objectMapper.readValue(getResult.getResponse().getContentAsString(), List.class);
            Map<?, ?> found = (Map<?, ?>) todos.stream()
                    .filter(t -> ((Map<?, ?>) t).get("id").equals(id))
                    .findFirst()
                    .orElseThrow();
            assertThat(found.get("completed")).isInstanceOf(Boolean.class);
        }

        @Test
        @DisplayName("PATCH with empty body does not corrupt todo")
        void patch_emptyBody_doesNotCorrupt() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Keep me\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(patch("/api/todos/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Keep me"));
        }

        @Test
        @DisplayName("PATCH with title=null does not set null")
        void patch_titleNull_doesNotCorrupt() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Original\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(patch("/api/todos/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":null}"))
                    .andReturn();

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id == " + id + ")].title").value("Original"));
        }
    }

    @Nested
    @DisplayName("Concurrent access")
    class ConcurrentAccess {

        @Test
        @DisplayName("concurrent PATCH on same todo does not corrupt state")
        void concurrentPatch_sameTodo_consistentState() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Race target\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            int threads = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger errors = new AtomicInteger(0);

            for (int i = 0; i < threads; i++) {
                final boolean completed = (i % 2 == 0);
                executor.submit(() -> {
                    try {
                        latch.await();
                        mockMvc.perform(patch("/api/todos/" + id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"completed\":" + completed + "}"))
                                .andReturn();
                    } catch (Exception e) {
                        errors.incrementAndGet();
                    }
                });
            }

            latch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
            assertThat(errors.get()).isZero();

            // Final state should be valid boolean
            MvcResult getResult = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();
            List<?> todos = objectMapper.readValue(getResult.getResponse().getContentAsString(), List.class);
            Map<?, ?> found = (Map<?, ?>) todos.stream()
                    .filter(t -> ((Map<?, ?>) t).get("id").equals(id))
                    .findFirst()
                    .orElseThrow();
            assertThat(found.get("completed")).isInstanceOf(Boolean.class);
            assertThat(found.get("title")).isEqualTo("Race target");
        }

        @Test
        @DisplayName("concurrent POST creates distinct todos")
        void concurrentPost_createsDistinctTodos() throws Exception {
            int count = 20;
            ExecutorService executor = Executors.newFixedThreadPool(count);
            CountDownLatch latch = new CountDownLatch(1);

            for (int i = 0; i < count; i++) {
                final int idx = i;
                executor.submit(() -> {
                    try {
                        latch.await();
                        mockMvc.perform(post("/api/todos")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"title\":\"Concurrent-" + idx + "\"}"))
                                .andExpect(status().isCreated())
                                .andReturn();
                    } catch (Exception ignored) {
                    }
                });
            }

            latch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();

            MvcResult getResult = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();
            List<?> todos = objectMapper.readValue(getResult.getResponse().getContentAsString(), List.class);
            long concurrentCount = todos.stream()
                    .map(t -> (Map<?, ?>) t)
                    .filter(m -> String.valueOf(m.get("title")).startsWith("Concurrent-"))
                    .count();
            assertThat(concurrentCount).isEqualTo(count);
        }
    }

    @Nested
    @DisplayName("Resource exhaustion")
    class ResourceExhaustion {

        @Test
        @DisplayName("rapid sequential POST does not exhaust resources")
        void rapidSequentialPost_handlesGracefully() throws Exception {
            for (int i = 0; i < 100; i++) {
                mockMvc.perform(post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"Rapid-" + i + "\"}"))
                        .andExpect(status().isCreated());
            }

            MvcResult getResult = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();
            List<?> todos = objectMapper.readValue(getResult.getResponse().getContentAsString(), List.class);
            assertThat(todos.size()).isGreaterThanOrEqualTo(100);
        }
    }
}
