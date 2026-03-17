import TodoItem from './TodoItem';

/**
 * List of todos with empty state. Renders TodoItem for each todo.
 */
function TodoList({ todos, onUpdate, onDelete }) {
  if (todos.length === 0) {
    return <p>No todos. Add your first todo above.</p>;
  }

  return (
    <ul style={{ listStyle: 'none', padding: 0 }}>
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          todo={todo}
          onUpdate={onUpdate}
          onDelete={onDelete}
        />
      ))}
    </ul>
  );
}

export default TodoList;
