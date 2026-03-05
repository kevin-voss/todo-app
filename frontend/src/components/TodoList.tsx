import { useState, useEffect } from 'react';
import { getTodos, createTodo } from '../api/todos';
import { TodoItem } from './TodoItem';
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
      await createTodo(title);
      setNewTitle('');
      await loadTodos();
    } catch {
      setError('Failed to create');
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
        <p>No todos yet. Add one above.</p>
      ) : (
        <ul role="list">
          {todos.map((todo) => (
            <TodoItem
              key={todo.id}
              todo={todo}
              onUpdate={loadTodos}
              onDelete={loadTodos}
            />
          ))}
        </ul>
      )}
    </div>
  );
}
