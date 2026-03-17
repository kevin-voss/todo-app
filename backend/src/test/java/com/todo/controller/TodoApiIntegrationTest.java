package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
 * Integration tests for Todo API - AC-3, AC-4, AC-5, AC-6, AC-7, AC-9.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean state between tests - implementation may use in-memory H2
    }

    // covers AC-3
    @Test
    void createTodo_returnsNewTodoWithIdTitleCompleted() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "First todo"));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("First todo"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    // covers AC-4
    @Test
    void listTodos_returnsArrayOfTodos() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // covers AC-4 (edge case: empty list)
    @Test
    void listTodos_whenEmpty_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // covers AC-5
    @Test
    void updateTodo_updatesTitleAndPersists() throws Exception {
        ResultActions create = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Original"))))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Updated title"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));

        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    // covers AC-6
    @Test
    void deleteTodo_removesFromListAndStorage() throws Exception {
        ResultActions create = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "To delete"))))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/todos/" + id))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isNotFound());
    }

    // covers AC-7
    @Test
    void toggleCompletion_updatesCompletedStateAndPersists() throws Exception {
        ResultActions create = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Toggle me"))))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(create.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("completed", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    // covers AC-9
    @Test
    void apiOperations_completeWithinReasonableTime() throws Exception {
        long start = System.currentTimeMillis();
        mockMvc.perform(get("/api/todos"));
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 2000, "API operations must complete within 2 seconds");
    }
}
