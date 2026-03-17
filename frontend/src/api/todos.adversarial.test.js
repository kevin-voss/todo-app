/**
 * QA adversarial tests for todos API client.
 * Targets error handling, malformed responses, edge cases.
 * Does NOT duplicate acceptance tests (todos.test.js).
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getTodos, createTodo, updateTodo, deleteTodo } from './todos';

describe('todos API client - adversarial', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('getTodos throws when response is not ok (404)', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 404 });
    await expect(getTodos()).rejects.toThrow('Failed to fetch todos');
  });

  it('getTodos throws when response is not ok (500)', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 500 });
    await expect(getTodos()).rejects.toThrow('Failed to fetch todos');
  });

  it('getTodos propagates network error', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'));
    await expect(getTodos()).rejects.toThrow('Network error');
  });

  it('getTodos throws when response.json() rejects (invalid JSON)', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.reject(new SyntaxError('Invalid JSON')),
    });
    await expect(getTodos()).rejects.toThrow();
  });

  it('createTodo throws when response is not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 400 });
    await expect(createTodo('Test')).rejects.toThrow('Failed to create todo');
  });

  it('updateTodo throws when response is not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 404 });
    await expect(updateTodo(1, { title: 'Updated' })).rejects.toThrow('Failed to update todo');
  });

  it('deleteTodo throws when response is not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 404 });
    await expect(deleteTodo(1)).rejects.toThrow('Failed to delete todo');
  });

  it('createTodo with empty string still sends request (backend may reject)', async () => {
    const mockRes = { ok: false, status: 400 };
    global.fetch = vi.fn().mockResolvedValue(mockRes);
    await expect(createTodo('')).rejects.toThrow();
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/todos'),
      expect.objectContaining({
        body: JSON.stringify({ title: '' }),
      })
    );
  });

  it('updateTodo with empty data object sends valid request', async () => {
    const mockRes = { ok: true, json: () => Promise.resolve({ id: 1, title: 'Same', completed: false }) };
    global.fetch = vi.fn().mockResolvedValue(mockRes);
    await updateTodo(1, {});
    expect(fetch).toHaveBeenCalledWith(
      expect.stringMatching(/\/api\/todos\/1$/),
      expect.objectContaining({
        body: JSON.stringify({}),
      })
    );
  });

  it('getTodos returns raw response when backend returns non-array (no validation)', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ error: 'unexpected' }),
    });
    const result = await getTodos();
    expect(result).toEqual({ error: 'unexpected' });
  });
});
