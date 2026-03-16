package com.todo.controller;

import com.todo.entity.Todo;
import com.todo.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/todos")
    public List<Todo> getAll() {
        return repository.findAll();
    }

    @PostMapping("/todos")
    public ResponseEntity<Todo> create(@RequestBody Map<String, Object> body) {
        String title = body.get("title") != null ? body.get("title").toString() : "";
        if (title.isBlank()) {
            title = "Untitled";
        }
        boolean completed = coerceCompleted(body.get("completed"));
        Todo todo = new Todo(title.trim(), completed);
        Todo saved = repository.save(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Todo> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Todo todo = opt.get();
        if (body.containsKey("title")) {
            String t = body.get("title") != null ? body.get("title").toString() : "";
            todo.setTitle(t.isBlank() ? "Untitled" : t.trim());
        }
        if (body.containsKey("completed")) {
            todo.setCompleted(coerceCompleted(body.get("completed")));
        }
        return ResponseEntity.ok(repository.save(todo));
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Coerce completed from boolean or string "true"/"false" to avoid ClassCastException.
     */
    private static boolean coerceCompleted(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return "true".equalsIgnoreCase(s.trim());
        return false;
    }
}
