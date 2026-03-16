package com.example.todo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Todo> create(@Valid @RequestBody TodoCreateRequest request) {
        Todo todo = new Todo(request.getTitle());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        Todo saved = repository.save(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return repository.findById(id)
                .map(todo -> {
                    if (updates.containsKey("title")) {
                        Object title = updates.get("title");
                        if (title != null) {
                            todo.setTitle(title.toString());
                        }
                    }
                    if (updates.containsKey("completed")) {
                        Object completed = updates.get("completed");
                        if (completed instanceof Boolean) {
                            todo.setCompleted((Boolean) completed);
                        }
                    }
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(todo -> {
                    repository.delete(todo);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
