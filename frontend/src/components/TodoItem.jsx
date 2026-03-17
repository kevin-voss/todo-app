import { useState } from 'react';

/**
 * Single todo row: checkbox, title, edit button, delete button.
 * Supports inline edit on Enter.
 */
function TodoItem({ todo, onUpdate, onDelete }) {
  const [editing, setEditing] = useState(false);
  const [editTitle, setEditTitle] = useState(todo.title);

  const handleToggle = () => {
    onUpdate(todo.id, { ...todo, completed: !todo.completed });
  };

  const handleDelete = () => {
    onDelete(todo.id);
  };

  const startEdit = () => {
    setEditing(true);
    setEditTitle(todo.title);
  };

  const handleEditSubmit = (e) => {
    e.preventDefault();
    const title = editTitle.trim();
    if (!title) {
      setEditing(false);
      setEditTitle(todo.title);
      return;
    }
    onUpdate(todo.id, { title });
    setEditing(false);
    setEditTitle('');
  };

  const handleEditKeyDown = (e) => {
    if (e.key === 'Enter') handleEditSubmit(e);
    if (e.key === 'Escape') {
      setEditing(false);
      setEditTitle(todo.title);
    }
  };

  return (
    <li
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
        onChange={handleToggle}
        aria-label={`Toggle ${todo.title}`}
      />
      {editing ? (
        <form onSubmit={handleEditSubmit} style={{ flex: 1, display: 'flex' }}>
          <input
            type="text"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            onKeyDown={handleEditKeyDown}
            autoFocus
            style={{ flex: 1, padding: '0.5rem' }}
          />
        </form>
      ) : (
        <>
          <span
            style={{
              flex: 1,
              textDecoration: todo.completed ? 'line-through' : 'none',
            }}
          >
            {todo.title}
          </span>
          <button type="button" onClick={startEdit}>
            Edit
          </button>
          <button type="button" onClick={handleDelete}>
            Delete
          </button>
        </>
      )}
    </li>
  );
}

export default TodoItem;
