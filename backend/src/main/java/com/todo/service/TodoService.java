package com.todo.service;

import com.todo.entity.Todo;
import com.todo.repository.TodoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    private Optional<Long> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return Optional.of((Long) auth.getPrincipal());
        }
        return Optional.empty();
    }

    public List<Todo> findAll() {
        return currentUserId()
                .map(todoRepository::findByUserIdOrderByCreatedAtAsc)
                .orElse(List.of());
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id)
                .filter(todo -> currentUserId().map(uid -> todo.getUserId().equals(uid)).orElse(false));
    }

    public Todo create(String title) {
        Long userId = currentUserId().orElseThrow(() -> new IllegalStateException("No authenticated user"));
        Todo todo = new Todo(title, userId);
        return todoRepository.save(todo);
    }

    public Optional<Todo> update(Long id, String title, Boolean completed) {
        return todoRepository.findById(id)
                .filter(todo -> currentUserId().map(uid -> todo.getUserId().equals(uid)).orElse(false))
                .map(todo -> {
                    if (title != null) {
                        todo.setTitle(title);
                    }
                    if (completed != null) {
                        todo.setCompleted(completed);
                    }
                    return todoRepository.save(todo);
                });
    }

    public boolean deleteById(Long id) {
        return findById(id)
                .map(todo -> {
                    todoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
