import type { Todo } from './types';

const API_BASE = 'http://localhost:8080/api';

export async function getTodos(): Promise<Todo[]> {
  const res = await fetch(`${API_BASE}/todos`);
  if (!res.ok) throw new Error('Failed to fetch');
  return res.json();
}

export async function createTodo(data: { title: string; completed?: boolean }): Promise<Todo> {
  const res = await fetch(`${API_BASE}/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to create');
  return res.json();
}

export async function updateTodo(id: number, data: Partial<Todo>): Promise<Todo> {
  const res = await fetch(`${API_BASE}/todos/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update');
  return res.json();
}

export async function deleteTodo(id: number): Promise<void> {
  const res = await fetch(`${API_BASE}/todos/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete');
}
