import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getTodos, createTodo, updateTodo, deleteTodo } from './todos';

// covers AC-2
describe('todos API client', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  // covers AC-2: frontend can call backend API
  it('getTodos fetches from /api/todos', async () => {
    const mockRes = { ok: true, json: () => Promise.resolve([]) };
    global.fetch = vi.fn().mockResolvedValue(mockRes);

    await getTodos();

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/todos'),
      expect.any(Object)
    );
  });

  // covers AC-2: frontend can call backend API
  it('createTodo POSTs to /api/todos with title', async () => {
    const mockRes = { ok: true, json: () => Promise.resolve({ id: 1, title: 'Test', completed: false }) };
    global.fetch = vi.fn().mockResolvedValue(mockRes);

    await createTodo('New todo');

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/todos'),
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: 'New todo' }),
      })
    );
  });

  // covers AC-5
  it('updateTodo PUTs to /api/todos/{id} with update data', async () => {
    const mockRes = { ok: true, json: () => Promise.resolve({ id: 1, title: 'Updated', completed: true }) };
    global.fetch = vi.fn().mockResolvedValue(mockRes);

    await updateTodo(1, { title: 'Updated', completed: true });

    expect(fetch).toHaveBeenCalledWith(
      expect.stringMatching(/\/api\/todos\/1$/),
      expect.objectContaining({
        method: 'PUT',
        body: JSON.stringify({ title: 'Updated', completed: true }),
      })
    );
  });

  // covers AC-8: frontend and backend can run together - API client targets backend
  it('uses backend API URL for requests', async () => {
    const mockRes = { ok: true, json: () => Promise.resolve([]) };
    global.fetch = vi.fn().mockResolvedValue(mockRes);

    await getTodos();

    expect(fetch).toHaveBeenCalledWith(
      expect.stringMatching(/http:\/\/.+\/api\/todos/),
      expect.any(Object)
    );
  });

  // covers AC-6
  it('deleteTodo DELETEs /api/todos/{id}', async () => {
    const mockRes = { ok: true };
    global.fetch = vi.fn().mockResolvedValue(mockRes);

    await deleteTodo(1);

    expect(fetch).toHaveBeenCalledWith(
      expect.stringMatching(/\/api\/todos\/1$/),
      expect.objectContaining({ method: 'DELETE' })
    );
  });
});
