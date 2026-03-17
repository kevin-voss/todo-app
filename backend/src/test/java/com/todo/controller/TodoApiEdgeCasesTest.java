package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Edge case tests per spec: 404 for non-existent id, empty/whitespace title handling.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoApiEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // covers AC-5/AC-6 edge case: non-existent id returns 404
    @Test
    void getTodo_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get("/api/todos/99999"))
                .andExpect(status().isNotFound());
    }

    // covers AC-5/AC-6 edge case: non-existent id returns 404
    @Test
    void updateTodo_nonExistentId_returns404() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "Updated"));
        mockMvc.perform(put("/api/todos/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // covers AC-5/AC-6 edge case: non-existent id returns 404
    @Test
    void deleteTodo_nonExistentId_returns404() throws Exception {
        mockMvc.perform(delete("/api/todos/99999"))
                .andExpect(status().isNotFound());
    }

    // covers AC-7 edge case: toggling non-existent todo returns 404
    @Test
    void toggleTodo_nonExistentId_returns404() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("completed", true));
        mockMvc.perform(put("/api/todos/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // covers AC-3 edge case: empty or whitespace-only title - reject or trim
    @Test
    void createTodo_emptyTitle_rejectedOrTrimmed() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", ""));
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    // covers AC-3 edge case: whitespace-only title
    @Test
    void createTodo_whitespaceOnlyTitle_rejectedOrTrimmed() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "   "));
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }
}
