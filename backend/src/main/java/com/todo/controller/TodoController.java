package com.todo.controller;

import com.todo.entity.Todo;
import com.todo.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for the Todo API.
 * Exposes CRUD operations at /api/todos.
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<Todo> listTodos() {
        return todoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
        return todoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Todo todo = new Todo(title.trim());
        Todo saved = todoRepository.save(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return todoRepository.findById(id)
                .map(todo -> {
                    if (body != null) {
                        if (body.containsKey("title")) {
                            String title = String.valueOf(body.get("title"));
                            if (title != null && !title.trim().isEmpty()) {
                                todo.setTitle(title.trim());
                            }
                        }
                        if (body.containsKey("completed")) {
                            todo.setCompleted(Boolean.TRUE.equals(body.get("completed")));
                        }
                    }
                    return ResponseEntity.ok(todoRepository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (!todoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        todoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
