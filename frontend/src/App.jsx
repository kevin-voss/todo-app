import { useState, useEffect } from 'react';
import { getTodos, createTodo, updateTodo, deleteTodo } from './api/todos';

/**
 * Main Todo App component. Renders the todo list UI with add, edit, toggle, and delete.
 * Fetches todos from the backend on mount via getTodos().
 */
function App() {
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editingTitle, setEditingTitle] = useState('');

  useEffect(() => {
    getTodos()
      .then(setTodos)
      .catch(() => setTodos([]));
  }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    const title = newTitle.trim();
    if (!title) return;
    try {
      const created = await createTodo(title);
      setTodos((prev) => [...prev, created]);
      setNewTitle('');
    } catch {
      // ignore
    }
  };

  const handleToggle = async (todo) => {
    try {
      const updated = await updateTodo(todo.id, { ...todo, completed: !todo.completed });
      setTodos((prev) => prev.map((t) => (t.id === todo.id ? updated : t)));
    } catch {
      // ignore
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTodo(id);
      setTodos((prev) => prev.filter((t) => t.id !== id));
    } catch {
      // ignore
    }
  };

  const startEdit = (todo) => {
    setEditingId(todo.id);
    setEditingTitle(todo.title);
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    if (editingId == null) return;
    const title = editingTitle.trim();
    if (!title) {
      setEditingId(null);
      return;
    }
    try {
      const updated = await updateTodo(editingId, { title });
      setTodos((prev) => prev.map((t) => (t.id === editingId ? updated : t)));
      setEditingId(null);
      setEditingTitle('');
    } catch {
      // ignore
    }
  };

  const handleEditKeyDown = (e) => {
    if (e.key === 'Enter') handleEditSubmit(e);
    if (e.key === 'Escape') {
      setEditingId(null);
      setEditingTitle('');
    }
  };

  return (
    <div style={{ maxWidth: 480, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Todo App</h1>

      <form onSubmit={handleAdd} style={{ marginBottom: '1rem' }}>
        <input
          type="text"
          placeholder="Add new todo"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
          style={{ marginRight: '0.5rem', padding: '0.5rem' }}
        />
        <button type="submit">Add</button>
      </form>

      {todos.length === 0 && (
        <p>No todos. Add your first todo above.</p>
      )}

      <ul style={{ listStyle: 'none', padding: 0 }}>
        {todos.map((todo) => (
          <li
            key={todo.id}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              marginBottom: '0.5rem',
            }}
          >
            <input
              type="checkbox"
              checked={todo.completed}
              onChange={() => handleToggle(todo)}
              aria-label={`Toggle ${todo.title}`}
            />
            {editingId === todo.id ? (
              <form onSubmit={handleEditSubmit} style={{ flex: 1, display: 'flex' }}>
                <input
                  type="text"
                  value={editingTitle}
                  onChange={(e) => setEditingTitle(e.target.value)}
                  onKeyDown={handleEditKeyDown}
                  autoFocus
                  style={{ flex: 1, padding: '0.5rem' }}
                />
              </form>
            ) : (
              <>
                <span style={{ flex: 1, textDecoration: todo.completed ? 'line-through' : 'none' }}>
                  {todo.title}
                </span>
                <button type="button" onClick={() => startEdit(todo)}>
                  Edit
                </button>
                <button type="button" onClick={() => handleDelete(todo.id)}>
                  Delete
                </button>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
