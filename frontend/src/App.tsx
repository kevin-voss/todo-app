import { useState } from 'react';

/**
 * Stub App component. Implementation to be developed.
 * Tests will fail until full behavior is implemented.
 */
function App() {
  const [todos] = useState<{ id: number; title: string; completed: boolean }[]>([]);

  return (
    <div data-testid="todo-app">
      <h1>Todo App</h1>
      <p>Implementation pending</p>
      <ul>
        {todos.map((t) => (
          <li key={t.id}>{t.title}</li>
        ))}
      </ul>
    </div>
  );
}

export default App;
