/**
 * Adversarial QA tests: integration, edge cases, error handling.
 * Does NOT duplicate acceptance tests. Focuses on malformed API responses,
 * boundary values, and race conditions in the API client.
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { getTodos, createTodo, updateTodo, deleteTodo } from '../api/todos';

const originalFetch = globalThis.fetch;

beforeEach(() => {
  vi.stubGlobal('fetch', originalFetch);
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('API client - malformed response handling', () => {
  it('getTodos throws on malformed JSON response', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.reject(new SyntaxError('Unexpected token')),
    }));

    await expect(getTodos()).rejects.toThrow();
  });

  it('getTodos throws on 500 server error', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
    }));

    await expect(getTodos()).rejects.toThrow('Failed to fetch todos');
  });

  it('getTodos throws on network failure', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('Network error')));

    await expect(getTodos()).rejects.toThrow();
  });

  it('createTodo throws on 400 validation error', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 400,
    }));

    await expect(createTodo('')).rejects.toThrow('Failed to create todo');
  });

  it('updateTodo throws on 404', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 404,
    }));

    await expect(updateTodo(999999, { title: 'x' })).rejects.toThrow('Failed to update todo');
  });

  it('deleteTodo throws on 404', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 404,
    }));

    await expect(deleteTodo(999999)).rejects.toThrow('Failed to delete todo');
  });
});

describe('API client - boundary values', () => {
  it('createTodo accepts single character title', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: 1, title: 'x', completed: false }),
    }));

    const result = await createTodo('x');
    expect(result.title).toBe('x');
    expect(fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        body: JSON.stringify({ title: 'x' }),
      })
    );
  });

  it('createTodo sends very long title in request body', async () => {
    const longTitle = 'a'.repeat(500);
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: 1, title: longTitle, completed: false }),
    }));

    const result = await createTodo(longTitle);
    expect(result.title).toBe(longTitle);
    expect(fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        body: JSON.stringify({ title: longTitle }),
      })
    );
  });

  it('updateTodo accepts partial update with only completed', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: 1, title: 'Original', completed: true }),
    }));

    const result = await updateTodo(1, { completed: true });
    expect(result.completed).toBe(true);
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/1'),
      expect.objectContaining({
        body: JSON.stringify({ completed: true }),
      })
    );
  });
});

describe('API client - concurrent call handling', () => {
  it('concurrent getTodos calls all resolve', async () => {
    let callCount = 0;
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => {
      callCount++;
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([{ id: callCount, title: 'Todo', completed: false }]),
      });
    }));

    const results = await Promise.all([getTodos(), getTodos(), getTodos()]);
    expect(results).toHaveLength(3);
    expect(results.every((r) => Array.isArray(r))).toBe(true);
  });

  it('concurrent createTodo calls do not share mutable state', async () => {
    let idCounter = 0;
    vi.stubGlobal('fetch', vi.fn().mockImplementation((_url: string, opts?: RequestInit) => {
      const body = opts?.body ? JSON.parse(opts.body as string) : {};
      const id = ++idCounter;
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ id, title: body.title || '', completed: false }),
      });
    }));

    const [a, b, c] = await Promise.all([
      createTodo('A'),
      createTodo('B'),
      createTodo('C'),
    ]);
    expect(a.title).toBe('A');
    expect(b.title).toBe('B');
    expect(c.title).toBe('C');
    expect(new Set([a.id, b.id, c.id]).size).toBe(3);
  });
});

describe('API client - malformed response structure', () => {
  it('getTodos returns array when backend returns object (validation)', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ error: 'unexpected' }),
    }));

    const result = await getTodos();
    expect(Array.isArray(result)).toBe(true);
  });

  it('getTodos returns array when backend returns null', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(null),
    }));

    const result = await getTodos();
    expect(Array.isArray(result)).toBe(true);
  });

  it('getTodos handles response with items missing required fields without crash', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([{ id: 1 }]),
    }));

    const result = await getTodos();
    expect(Array.isArray(result)).toBe(true);
    expect(result[0]).toBeDefined();
  });
});
