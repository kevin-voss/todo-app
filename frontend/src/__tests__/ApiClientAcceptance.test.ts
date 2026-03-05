/**
 * Acceptance tests for frontend API client - HTTP communication with backend.
 * covers AC-11
 *
 * Frontend shall have an API client that issues HTTP requests to backend REST endpoints.
 * This test verifies the API module exists and exports the required functions.
 */

import { describe, it, expect } from 'vitest';
import { getTodos, createTodo, updateTodo, deleteTodo } from '../api/todos';

describe('Frontend communicates with backend via REST API', () => {
  // covers AC-11
  it('API client exports getTodos, createTodo, updateTodo, deleteTodo for HTTP REST calls', () => {
    expect(typeof getTodos).toBe('function');
    expect(typeof createTodo).toBe('function');
    expect(typeof updateTodo).toBe('function');
    expect(typeof deleteTodo).toBe('function');
  });
});
