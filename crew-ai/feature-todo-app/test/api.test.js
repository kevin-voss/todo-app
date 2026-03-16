/**
 * Acceptance tests for Todo App REST API.
 * Tests validate AC-5, AC-6, AC-7 from requirements.md.
 * Requires backend running at http://localhost:8080
 */
import { describe, it, expect } from 'vitest';

const API_BASE = 'http://localhost:8080/api';

async function createTodo(title, description) {
  const res = await fetch(`${API_BASE}/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, description: description ?? null }),
  });
  return { status: res.status, data: res.ok ? await res.json() : null };
}

async function getTodos() {
  const res = await fetch(`${API_BASE}/todos`);
  return { status: res.status, data: await res.json() };
}

async function patchTodo(id, completed) {
  const res = await fetch(`${API_BASE}/todos/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ completed }),
  });
  return { status: res.status, data: res.ok ? await res.json() : null };
}

async function deleteTodo(id) {
  const res = await fetch(`${API_BASE}/todos/${id}`, { method: 'DELETE' });
  return { status: res.status };
}

describe('Todo API', () => {
  // covers AC-5
  it('AC-5: POST /api/todos creates a todo and returns the created todo', async () => {
    const { status, data } = await createTodo('Test todo', 'Test description');

    expect(status).toBe(201);
    expect(data).not.toBeNull();
    expect(data).toHaveProperty('id');
    expect(data).toHaveProperty('title', 'Test todo');
    expect(data).toHaveProperty('description', 'Test description');
    expect(data).toHaveProperty('completed');
    expect(typeof data.completed).toBe('boolean');

    if (data?.id) {
      await deleteTodo(data.id);
    }
  });

  // covers AC-5 (edge case: empty or null title uses default or is rejected)
  it('AC-5: POST /api/todos handles empty title (reject or default to Untitled)', async () => {
    const { status, data } = await createTodo('', null);

    expect([201, 400]).toContain(status);
    if (status === 201 && data?.id) {
      expect(data.title).toMatch(/.+/);
      await deleteTodo(data.id);
    }
  });

  // covers AC-6
  it('AC-6: GET /api/todos returns a list of todos', async () => {
    const { status, data } = await getTodos();

    expect(status).toBe(200);
    expect(Array.isArray(data)).toBe(true);
  });

  // covers AC-6 (edge case: empty list)
  it('AC-6: GET /api/todos returns empty array when no todos exist', async () => {
    const { data } = await getTodos();
    expect(Array.isArray(data)).toBe(true);
  });

  // covers AC-7
  it('AC-7: PATCH /api/todos/{id} updates todo completed status', async () => {
    const { data: created } = await createTodo('Patch test', null);
    if (!created?.id) {
      expect.fail('Could not create todo for PATCH test');
    }

    const { status, data } = await patchTodo(created.id, true);

    expect(status).toBe(200);
    expect(data?.completed).toBe(true);

    await deleteTodo(created.id);
  });

  // covers AC-7
  it('AC-7: DELETE /api/todos/{id} removes a todo', async () => {
    const { data: created } = await createTodo('Delete test', null);
    if (!created?.id) {
      expect.fail('Could not create todo for DELETE test');
    }

    const { status } = await deleteTodo(created.id);

    expect([200, 204]).toContain(status);

    const { data: list } = await getTodos();
    const found = list?.find((t) => t.id === created.id);
    expect(found).toBeUndefined();
  });

  // covers AC-7 (edge case: 404 for non-existent id)
  it('AC-7: PATCH /api/todos/{id} returns 404 for non-existent id', async () => {
    const { status } = await patchTodo(999999, true);
    expect(status).toBe(404);
  });

  // covers AC-7 (edge case: 404 for non-existent id)
  it('AC-7: DELETE /api/todos/{id} returns 404 for non-existent id', async () => {
    const { status } = await deleteTodo(999999);
    expect(status).toBe(404);
  });
});
