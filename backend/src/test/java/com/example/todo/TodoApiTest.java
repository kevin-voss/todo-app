package com.example.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance tests for Todo REST API.
 * Tests are expected to fail until the implementation is complete.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TodoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean state would require repository access; for now tests assume fresh or shared DB
    }

    @Nested
    @DisplayName("GET /api/todos")
    class GetTodos {

        @Test
        @DisplayName("returns list of Todo entities")
        // covers AC-1
        void getTodos_returnsList() throws Exception {
            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("returns empty array when no todos exist")
        // covers AC-14
        void getTodos_whenEmpty_returnsEmptyArray() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<?> todos = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
            assertThat(todos).isEmpty();
        }
    }

    @Nested
    @DisplayName("POST /api/todos")
    class PostTodos {

        @Test
        @DisplayName("creates and persists new Todo with id, title, completed")
        // covers AC-2
        void postTodos_validPayload_createsTodo() throws Exception {
            String payload = "{\"title\":\"Buy milk\"}";

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("Buy milk"))
                    .andExpect(jsonPath("$.completed").exists());
        }

        @Test
        @DisplayName("created todo is persisted and returned by GET")
        // covers AC-2, AC-9
        void postTodos_createdTodo_persistedAndRetrievable() throws Exception {
            String payload = "{\"title\":\"Persisted todo\"}";

            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id == " + id + ")].title").value("Persisted todo"));
        }

        @Test
        @DisplayName("rejects empty or missing title with 400")
        // covers AC-13
        void postTodos_emptyTitle_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("rejects missing title with 400")
        // covers AC-13
        void postTodos_missingTitle_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/todos/{id}")
    class PatchTodos {

        @Test
        @DisplayName("updates specified fields of existing Todo")
        // covers AC-3
        void patchTodos_existingId_updatesFields() throws Exception {
            // Create todo first
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Original\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(patch("/api/todos/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":true}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed").value(true))
                    .andExpect(jsonPath("$.title").value("Original"));
        }

        @Test
        @DisplayName("returns 404 for non-existent id")
        // covers AC-11
        void patchTodos_nonExistentId_returns404() throws Exception {
            mockMvc.perform(patch("/api/todos/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"completed\":true}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/todos/{id}")
    class DeleteTodos {

        @Test
        @DisplayName("removes Todo from storage")
        // covers AC-4
        void deleteTodos_existingId_removesTodo() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"To delete\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<?, ?> created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            Object id = created.get("id");

            mockMvc.perform(delete("/api/todos/" + id))
                    .andExpect(status().isNoContent());

            MvcResult getResult = mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andReturn();
            List<?> todos = objectMapper.readValue(getResult.getResponse().getContentAsString(), List.class);
            assertThat(todos).noneMatch(t -> ((Map<?, ?>) t).get("id").equals(id));
        }

        @Test
        @DisplayName("returns 404 or success for non-existent id")
        // covers AC-12
        void deleteTodos_nonExistentId_returns404Or204() throws Exception {
            MvcResult result = mockMvc.perform(delete("/api/todos/99999"))
                    .andReturn();

            int status = result.getResponse().getStatus();
            assertThat(status).isIn(204, 404);
        }
    }
}
