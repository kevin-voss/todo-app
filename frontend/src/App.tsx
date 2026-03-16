import { useState, useEffect } from 'react';
import type { Todo } from './types';
import { getTodos, createTodo, updateTodo, deleteTodo } from './api';

function App() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newTitle, setNewTitle] = useState('');

  useEffect(() => {
    getTodos()
      .then(setTodos)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    const title = newTitle.trim();
    if (!title) return;
    setNewTitle('');
    createTodo({ title })
      .then((todo) => setTodos((prev) => [...prev, todo]))
      .catch((e) => setError(e.message));
  };

  const handleToggle = (todo: Todo) => {
    updateTodo(todo.id, { completed: !todo.completed })
      .then((updated) =>
        setTodos((prev) => prev.map((t) => (t.id === todo.id ? updated : t)))
      )
      .catch((e) => setError(e.message));
  };

  const handleDelete = (id: number) => {
    deleteTodo(id)
      .then(() => setTodos((prev) => prev.filter((t) => t.id !== id)))
      .catch((e) => setError(e.message));
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div data-testid="todo-app">
      <h1>Todo App</h1>
      <form onSubmit={handleAdd}>
        <input
          type="text"
          placeholder="Add todo"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
        />
        <button type="submit">Add</button>
      </form>
      {todos.length === 0 ? (
        <p>No todos yet</p>
      ) : (
        <ul>
          {todos.map((todo) => (
            <li key={todo.id}>
              <label>
                <input
                  type="checkbox"
                  checked={todo.completed}
                  onChange={() => handleToggle(todo)}
                />
                {todo.title}
              </label>
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

export default App;
