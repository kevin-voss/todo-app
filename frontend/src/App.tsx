import { AuthProvider, useAuth } from './context/AuthContext';
import { TodoList } from './components/TodoList';
import { LoginForm } from './components/LoginForm';
import './App.css';

function AppContent() {
  const { user, isLoading, logout } = useAuth();

  if (isLoading) {
    return <p style={{ padding: '1rem' }}>Loading...</p>;
  }

  if (!user) {
    return <LoginForm />;
  }

  return (
    <main style={{ padding: '1rem', maxWidth: '600px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h1>Todo</h1>
        <div>
          <span style={{ marginRight: '0.5rem' }}>{user.email}</span>
          <button type="button" onClick={logout}>Log out</button>
        </div>
      </div>
      <div className="todo-list">
        <TodoList />
      </div>
    </main>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
