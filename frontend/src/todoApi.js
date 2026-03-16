const API_BASE = 'http://localhost:8080/api';

export async function getTodos() {
  const res = await fetch(`${API_BASE}/todos`);
  if (!res.ok) throw new Error(`Failed to fetch: ${res.status}`);
  return res.json();
}

export async function createTodo(title, completed = false) {
  const res = await fetch(`${API_BASE}/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title: title || 'Untitled', completed }),
  });
  if (!res.ok) throw new Error(`Failed to create: ${res.status}`);
  return res.json();
}

export async function updateTodo(id, updates) {
  const res = await fetch(`${API_BASE}/todos/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updates),
  });
  if (!res.ok) throw new Error(`Failed to update: ${res.status}`);
  return res.json();
}

export async function deleteTodo(id) {
  const res = await fetch(`${API_BASE}/todos/${id}`, { method: 'DELETE' });
  if (!res.ok && res.status !== 204) throw new Error(`Failed to delete: ${res.status}`);
}
