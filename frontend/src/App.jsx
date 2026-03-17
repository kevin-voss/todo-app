import { useState, useEffect } from 'react';
import { getTodos, createTodo, updateTodo, deleteTodo } from './api/todos';
import AddTodoForm from './components/AddTodoForm';
import TodoList from './components/TodoList';
import './App.css';

/**
 * Main Todo App component. Renders the todo list UI with add, edit, toggle, and delete.
 * Fetches todos from the backend on mount via getTodos().
 */
function App() {
  const [todos, setTodos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getTodos()
      .then(setTodos)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleCreate = async (title) => {
    try {
      const created = await createTodo(title);
      setTodos((prev) => [...prev, created]);
    } catch (e) {
      setError(e.message);
    }
  };

  const handleUpdate = async (id, data) => {
    try {
      const updated = await updateTodo(id, data);
      setTodos((prev) => prev.map((t) => (t.id === id ? updated : t)));
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTodo(id);
      setTodos((prev) => prev.filter((t) => t.id !== id));
    } catch (e) {
      setError(e.message);
    }
  };

  if (loading) return <div className="app-loading">Loading...</div>;
  if (error) return <div className="app-error">Error: {error}</div>;

  return (
    <div className="app">
      <h1>Todo App</h1>
      <AddTodoForm onSubmit={handleCreate} />
      <TodoList todos={todos} onUpdate={handleUpdate} onDelete={handleDelete} />
    </div>
  );
}

export default App;
