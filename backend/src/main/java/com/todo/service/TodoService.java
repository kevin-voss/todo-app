package com.todo.service;

import com.todo.entity.Todo;
import com.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Todo create(String title) {
        Todo todo = new Todo(title);
        return todoRepository.save(todo);
    }

    public Optional<Todo> update(Long id, String title, Boolean completed) {
        return todoRepository.findById(id)
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
        if (!todoRepository.existsById(id)) {
            return false;
        }
        todoRepository.deleteById(id);
        return true;
    }
}
