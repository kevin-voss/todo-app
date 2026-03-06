/**
 * Spec acceptance tests for frontend.
 * Covers AC-1, AC-11 per requirements.md.
 * Tests are expected to fail until implementation exists.
 */

import { describe, it, expect } from 'vitest';
import React from 'react';
import { getTodos, createTodo, updateTodo, deleteTodo } from '../api/todos';

describe('Spec Acceptance - Frontend', () => {
  // covers AC-1
  it('frontend is built with React and JavaScript', () => {
    expect(React).toBeDefined();
    expect(typeof React.createElement).toBe('function');
    expect(typeof React.Component).toBe('function');
  });

  // covers AC-11
  it('API client exports getTodos for REST communication', () => {
    expect(typeof getTodos).toBe('function');
  });

  // covers AC-11
  it('API client exports createTodo for REST communication', () => {
    expect(typeof createTodo).toBe('function');
  });

  // covers AC-11
  it('API client exports updateTodo for REST communication', () => {
    expect(typeof updateTodo).toBe('function');
  });

  // covers AC-11
  it('API client exports deleteTodo for REST communication', () => {
    expect(typeof deleteTodo).toBe('function');
  });
});
