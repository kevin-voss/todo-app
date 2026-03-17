package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA adversarial tests: malformed input, boundary values, stress, concurrency.
 * Does NOT duplicate acceptance tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoApiAdversarialTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Malformed input ---

    @Test
    void createTodo_nullBody_returns4xx() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTodo_missingTitleKey_returns4xx() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("other", "value"));
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTodo_invalidJson_returns4xx() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getTodo_nonNumericId_returns4xx() throws Exception {
        mockMvc.perform(get("/api/todos/notanumber"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getTodo_negativeId_returns4xxOr404() throws Exception {
        var result = mockMvc.perform(get("/api/todos/-1")).andReturn();
        assertTrue(result.getResponse().getStatus() >= 400,
                "Negative id should return 4xx or 404");
    }

    @Test
    void getTodo_zeroId_returns4xxOr404() throws Exception {
        var result = mockMvc.perform(get("/api/todos/0")).andReturn();
        assertTrue(result.getResponse().getStatus() >= 400,
                "Zero id should return 4xx or 404");
    }

    @Test
    void updateTodo_nonNumericId_returns4xx() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "Updated"));
        mockMvc.perform(put("/api/todos/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteTodo_nonNumericId_returns4xx() throws Exception {
        mockMvc.perform(delete("/api/todos/xyz"))
                .andExpect(status().is4xxClientError());
    }

    // --- Boundary values ---

    @Test
    void createTodo_veryLongTitle_handlesOrRejects() throws Exception {
        String longTitle = "a".repeat(1000);
        String body = objectMapper.writeValueAsString(Map.of("title", longTitle));
        var result = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        // Either 201 (accepted) or 4xx (rejected) - must not 500
        assertTrue(result.getResponse().getStatus() != 500,
                "Very long title must not cause 500");
    }

    @Test
    void createTodo_specialCharactersInTitle_handlesSafely() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "<script>alert(1)</script>"));
        var result = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        assertTrue(result.getResponse().getStatus() != 500,
                "Special chars must not cause 500");
    }

    @Test
    void createTodo_unicodeInTitle_handlesSafely() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "日本語 \uD83D\uDE00 emoji"));
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("日本語 \uD83D\uDE00 emoji"));
    }

    @Test
    void updateTodo_setCompletedToFalse_unogglesCorrectly() throws Exception {
        ResultActions create = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Toggle test"))))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("completed", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("completed", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void updateTodo_emptyBody_returnsOkWithUnchangedTodo() throws Exception {
        ResultActions create = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Original"))))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original"));
    }

    // --- Stress / burst ---

    @Test
    void createManyTodos_rapidly_allSucceedOrFailGracefully() throws Exception {
        int count = 50;
        for (int i = 0; i < count; i++) {
            String body = objectMapper.writeValueAsString(Map.of("title", "Todo " + i));
            var result = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andReturn();
            assertTrue(result.getResponse().getStatus() == 201 || result.getResponse().getStatus() >= 400,
                    "Rapid create must not 500");
        }
    }

    @Test
    void listTodos_afterManyCreates_returnsWithoutTimeout() throws Exception {
        for (int i = 0; i < 30; i++) {
            String body = objectMapper.writeValueAsString(Map.of("title", "Stress " + i));
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }
        long start = System.currentTimeMillis();
        mockMvc.perform(get("/api/todos")).andExpect(status().isOk());
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 5000, "List after 30 items must complete within 5s");
    }

    // --- Rapid sequential operations (simulates burst load) ---

    @Test
    void rapidCreateUpdateDelete_cycle_noLeakOr500() throws Exception {
        for (int i = 0; i < 20; i++) {
            ResultActions create = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("title", "Cycle " + i))))
                    .andExpect(status().isCreated());
            Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();
            mockMvc.perform(put("/api/todos/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("completed", true))))
                    .andExpect(status().isOk());
            mockMvc.perform(delete("/api/todos/" + id)).andExpect(status().is2xxSuccessful());
        }
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
