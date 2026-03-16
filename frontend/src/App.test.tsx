/**
 * Acceptance tests for Todo App.
 * Tests are expected to fail until the implementation is complete.
 */
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from './App';
import * as api from './api';

vi.mock('./api');

describe('Todo App Acceptance Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('AC-5: Display list of todos on load', () => {
    it('fetches and displays todos from GET /api/todos when app loads', async () => {
      // covers AC-5
      const mockTodos = [
        { id: 1, title: 'Buy milk', completed: false },
        { id: 2, title: 'Walk dog', completed: true },
      ];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);

      render(<App />);

      await waitFor(() => {
        expect(api.getTodos).toHaveBeenCalled();
      });

      expect(screen.getByText('Buy milk')).toBeInTheDocument();
      expect(screen.getByText('Walk dog')).toBeInTheDocument();
    });
  });

  describe('AC-6: Add new todo', () => {
    it('calls POST /api/todos and new todo appears in list when user submits', async () => {
      // covers AC-6
      vi.mocked(api.getTodos).mockResolvedValue([]);
      const newTodo = { id: 3, title: 'New task', completed: false };
      vi.mocked(api.createTodo).mockResolvedValue(newTodo);

      render(<App />);

      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());

      const input = screen.getByPlaceholderText(/add.*todo|new.*todo|todo.*title/i);
      const addButton = screen.getByRole('button', { name: /add|submit|create/i });

      await userEvent.type(input, 'New task');
      await userEvent.click(addButton);

      expect(api.createTodo).toHaveBeenCalledWith({ title: 'New task' });
      await waitFor(() => {
        expect(screen.getByText('New task')).toBeInTheDocument();
      });
    });
  });

  describe('AC-7: Toggle todo completion', () => {
    it('calls PATCH /api/todos/{id} and todo completed state updates in UI', async () => {
      // covers AC-7
      const mockTodos = [{ id: 1, title: 'Toggle me', completed: false }];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);
      vi.mocked(api.updateTodo).mockResolvedValue({
        id: 1,
        title: 'Toggle me',
        completed: true,
      });

      render(<App />);

      await waitFor(() => expect(screen.getByText('Toggle me')).toBeInTheDocument());

      const checkbox = screen.getByRole('checkbox', { name: /toggle me/i });
      await userEvent.click(checkbox);

      expect(api.updateTodo).toHaveBeenCalledWith(1, { completed: true });
      await waitFor(() => {
        expect(checkbox).toBeChecked();
      });
    });
  });

  describe('AC-8: Delete todo', () => {
    it('calls DELETE /api/todos/{id} and todo is removed from UI', async () => {
      // covers AC-8
      const mockTodos = [{ id: 1, title: 'Delete me', completed: false }];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);
      vi.mocked(api.deleteTodo).mockResolvedValue();

      render(<App />);

      await waitFor(() => expect(screen.getByText('Delete me')).toBeInTheDocument());

      const deleteButton = screen.getByRole('button', { name: /delete|remove/i });
      await userEvent.click(deleteButton);

      expect(api.deleteTodo).toHaveBeenCalledWith(1);
      await waitFor(() => {
        expect(screen.queryByText('Delete me')).not.toBeInTheDocument();
      });
    });
  });

  describe('AC-10: Operations via backend API', () => {
    it('uses getTodos (GET) for initial load - no local-only state', async () => {
      // covers AC-10
      vi.mocked(api.getTodos).mockResolvedValue([]);

      render(<App />);

      await waitFor(() => {
        expect(api.getTodos).toHaveBeenCalled();
      });
      expect(api.getTodos).toHaveBeenCalledTimes(1);
    });
  });

  describe('AC-14: Empty state', () => {
    it('displays empty state when no todos exist', async () => {
      // covers AC-14
      vi.mocked(api.getTodos).mockResolvedValue([]);

      render(<App />);

      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());

      expect(
        screen.getByText(/no todos|empty|nothing yet|add your first/i)
      ).toBeInTheDocument();
    });
  });
});
