import { useState, useEffect } from 'react';
import { getTodos, createTodo, updateTodo, deleteTodo } from '../api/todos';
import type { Todo } from '../types/todo';

export function TodoList() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [newTitle, setNewTitle] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadTodos = async () => {
    try {
      setError(null);
      const data = await getTodos();
      setTodos(data);
    } catch {
      setTodos([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTodos();
  }, []);

  const handleAdd = async () => {
    const title = newTitle.trim();
    if (!title) return;
    try {
      const created = await createTodo(title);
      setTodos((prev) => [...prev, created]);
      setNewTitle('');
    } catch {
      setError('Failed to create');
    }
  };

  const handleToggle = async (todo: Todo) => {
    try {
      const updated = await updateTodo(todo.id, { completed: !todo.completed });
      setTodos((prev) =>
        prev.map((t) => (t.id === todo.id ? updated : t))
      );
    } catch {
      setError('Failed to update');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteTodo(id);
      setTodos((prev) => prev.filter((t) => t.id !== id));
    } catch {
      setError('Failed to delete');
    }
  };

  const handleTitleChange = async (todo: Todo, title: string) => {
    const trimmed = title.trim();
    if (!trimmed || trimmed === todo.title) return;
    try {
      const updated = await updateTodo(todo.id, { title: trimmed });
      setTodos((prev) =>
        prev.map((t) => (t.id === todo.id ? updated : t))
      );
    } catch {
      setError('Failed to update');
    }
  };

  const isEmpty = todos.length === 0 && !error;

  return (
    <div>
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
        <input
          type="text"
          placeholder="Add todo"
          aria-label="Add todo"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleAdd()}
        />
        <button type="button" onClick={handleAdd}>
          Add
        </button>
      </div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {loading ? (
        <p>Loading...</p>
      ) : isEmpty ? (
        <p>No todos. Add one to get started.</p>
      ) : (
        <ul role="list">
          {todos.map((todo) => (
            <li key={todo.id} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
              <input
                type="checkbox"
                checked={todo.completed}
                onChange={() => handleToggle(todo)}
                aria-label={`Mark "${todo.title}" as ${todo.completed ? 'incomplete' : 'complete'}`}
              />
              <input
                type="text"
                value={todo.title}
                onChange={(e) => handleTitleChange(todo, e.target.value)}
                onBlur={(e) => handleTitleChange(todo, e.target.value)}
                style={{ flex: 1 }}
              />
              <button type="button" onClick={() => handleDelete(todo.id)}>
                Delete
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
