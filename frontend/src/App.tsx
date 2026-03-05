import { TodoList } from './components/TodoList';
import './App.css';

function App() {
  return (
    <main style={{ padding: '1rem', maxWidth: '600px', margin: '0 auto' }}>
      <h1>Todo</h1>
      <div className="todo-list">
        <TodoList />
      </div>
    </main>
  );
}

export default App;
