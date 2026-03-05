import { TodoList } from './components/TodoList';

function App() {
  return (
    <main style={{ padding: '1rem', maxWidth: '600px', margin: '0 auto' }}>
      <h1>Todo</h1>
      <TodoList />
    </main>
  );
}

export default App;
