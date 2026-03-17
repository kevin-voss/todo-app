package com.todo.controller;

import com.todo.entity.Todo;
import com.todo.service.TodoService;
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

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> listTodos() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        return todoService.create(title)
                .map(todo -> ResponseEntity.status(HttpStatus.CREATED).body(todo))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = body != null && body.containsKey("title") ? String.valueOf(body.get("title")) : null;
        Boolean completed = null;
        if (body != null && body.containsKey("completed")) {
            Object val = body.get("completed");
            completed = Boolean.TRUE.equals(val);
        }
        return todoService.update(id, title, completed)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (!todoService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
