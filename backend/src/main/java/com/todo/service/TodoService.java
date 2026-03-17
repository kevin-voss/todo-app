package com.todo.service;

import com.todo.entity.Todo;
import com.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic layer for Todo operations.
 * Handles validation and delegates persistence to the repository.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    /**
     * Creates a todo with the given title.
     * Rejects empty or whitespace-only titles.
     *
     * @param title the todo title (must be non-empty after trim)
     * @return the created todo, or empty if title is invalid
     */
    public Optional<Todo> create(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Optional.empty();
        }
        Todo todo = new Todo(title.trim());
        return Optional.of(todoRepository.save(todo));
    }

    /**
     * Updates an existing todo by id.
     *
     * @param id       the todo id
     * @param title    optional new title (ignored if null or empty)
     * @param completed optional new completed state (ignored if null)
     * @return the updated todo, or empty if not found
     */
    public Optional<Todo> update(Long id, String title, Boolean completed) {
        return todoRepository.findById(id)
                .map(todo -> {
                    if (title != null && !title.trim().isEmpty()) {
                        todo.setTitle(title.trim());
                    }
                    if (completed != null) {
                        todo.setCompleted(completed);
                    }
                    return todoRepository.save(todo);
                });
    }

    /**
     * Deletes a todo by id.
     *
     * @param id the todo id
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        if (!todoRepository.existsById(id)) {
            return false;
        }
        todoRepository.deleteById(id);
        return true;
    }
}
