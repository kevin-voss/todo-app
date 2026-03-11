import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { signup as apiSignup, login as apiLogin } from '../api/auth';
import { setAuthToken } from '../api/todos';

const TOKEN_KEY = 'todo_jwt_token';

interface AuthContextType {
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  signup: (email: string, password: string) => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem(TOKEN_KEY)
  );
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setAuthToken(token);
  }, [token]);

  useEffect(() => {
    setIsLoading(false);
  }, []);

  const signup = useCallback(async (email: string, password: string) => {
    const t = await apiSignup(email, password);
    localStorage.setItem(TOKEN_KEY, t);
    setToken(t);
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const t = await apiLogin(email, password);
    localStorage.setItem(TOKEN_KEY, t);
    setToken(t);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        token,
        isAuthenticated: !!token,
        isLoading,
        signup,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
