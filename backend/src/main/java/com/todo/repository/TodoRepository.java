package com.todo.repository;

import com.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for Todo entities.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
