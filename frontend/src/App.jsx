import { useState, useEffect, useRef } from 'react';
import { getTodos, createTodo, updateTodo, deleteTodo } from './todoApi';

export default function App() {
  const [todos, setTodos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [title, setTitle] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isSubmittingRef = useRef(false);

  useEffect(() => {
    getTodos()
      .then(setTodos)
      .catch((err) => {
        setError(err.message || 'Failed to load todos');
      })
      .finally(() => setLoading(false));
  }, []);

  function handleSubmit(e) {
    e.preventDefault();
    const trimmed = title.trim();
    if (!trimmed || isSubmittingRef.current) return;
    isSubmittingRef.current = true;
    setIsSubmitting(true);
    createTodo(trimmed, false)
      .then((created) => {
        setTodos((prev) => [...prev, created]);
        setTitle('');
      })
      .catch((err) => {
        setError(err.message || 'Failed to create todo');
      })
      .finally(() => {
        isSubmittingRef.current = false;
        setIsSubmitting(false);
      });
  }

  function handleToggle(todo) {
    updateTodo(todo.id, { completed: !todo.completed })
      .then((updated) => {
        setTodos((prev) =>
          prev.map((t) => (t.id === updated.id ? updated : t))
        );
      })
      .catch((err) => {
        setError(err.message || 'Failed to update todo');
      });
  }

  function handleDelete(id) {
    deleteTodo(id)
      .then(() => {
        setTodos((prev) => prev.filter((t) => t.id !== id));
      })
      .catch((err) => {
        setError(err.message || 'Failed to delete todo');
      });
  }

  return (
    <div className="app">
      <h1>Todo</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Add todo..."
          disabled={isSubmitting}
        />
        <button type="submit" disabled={isSubmitting}>
          Add
        </button>
      </form>
      {error && <p className="error">{error}</p>}
      {loading ? (
        <p>Loading...</p>
      ) : (
        <ul>
          {todos.length === 0 ? (
            <li>No todos</li>
          ) : (
            todos.map((todo) => (
              <li key={todo.id}>
                <input
                  type="checkbox"
                  checked={todo.completed}
                  onChange={() => handleToggle(todo)}
                />
                <span className={todo.completed ? 'completed' : ''}>
                  {todo.title}
                </span>
                <button onClick={() => handleDelete(todo.id)}>Delete</button>
              </li>
            ))
          )}
        </ul>
      )}
    </div>
  );
}
