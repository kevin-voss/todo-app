import { useState } from 'react';
import { updateTodo, deleteTodo } from '../api/todos';
import type { Todo } from '../types/todo';

interface TodoItemProps {
  todo: Todo;
  onUpdate: () => void;
  onDelete: () => void;
}

export function TodoItem({ todo, onUpdate, onDelete }: TodoItemProps) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(todo.title);

  async function handleToggle() {
    await updateTodo(todo.id, { completed: !todo.completed });
    onUpdate();
  }

  async function handleSave() {
    if (title.trim()) {
      await updateTodo(todo.id, { title: title.trim() });
      onUpdate();
    } else {
      setTitle(todo.title);
    }
    setEditing(false);
  }

  async function handleDelete() {
    await deleteTodo(todo.id);
    onDelete();
  }

  return (
    <li style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
      <input
        type="checkbox"
        checked={todo.completed}
        onChange={handleToggle}
        aria-label={`Mark "${todo.title}" as ${todo.completed ? 'incomplete' : 'complete'}`}
      />
      {editing ? (
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onBlur={handleSave}
          onKeyDown={(e) => e.key === 'Enter' && handleSave()}
          style={{ flex: 1 }}
          autoFocus
        />
      ) : (
        <span
          onClick={() => setEditing(true)}
          style={{
            flex: 1,
            textDecoration: todo.completed ? 'line-through' : 'none',
            cursor: 'pointer',
          }}
        >
          {todo.title}
        </span>
      )}
      <button type="button" onClick={handleDelete}>
        Delete
      </button>
    </li>
  );
}
