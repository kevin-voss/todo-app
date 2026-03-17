/**
 * API client for the todo backend.
 * Base URL: VITE_API_URL env var or http://localhost:8080
 */

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

/**
 * Fetches all todos from the backend.
 * @returns {Promise<Array<{id: number, title: string, completed: boolean}>>}
 */
export async function getTodos() {
  const res = await fetch(`${API_BASE}/api/todos`);
  if (!res.ok) throw new Error('Failed to fetch todos');
  return res.json();
}

/**
 * Creates a new todo.
 * @param {string} title - The todo title
 * @returns {Promise<{id: number, title: string, completed: boolean}>}
 */
export async function createTodo(title) {
  const res = await fetch(`${API_BASE}/api/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title }),
  });
  if (!res.ok) throw new Error('Failed to create todo');
  return res.json();
}

/**
 * Updates an existing todo.
 * @param {number} id - The todo id
 * @param {{title?: string, completed?: boolean}} data - Fields to update
 * @returns {Promise<{id: number, title: string, completed: boolean}>}
 */
export async function updateTodo(id, data) {
  const res = await fetch(`${API_BASE}/api/todos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update todo');
  return res.json();
}

/**
 * Deletes a todo.
 * @param {number} id - The todo id
 * @returns {Promise<void>}
 */
export async function deleteTodo(id) {
  const res = await fetch(`${API_BASE}/api/todos/${id}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error('Failed to delete todo');
}
