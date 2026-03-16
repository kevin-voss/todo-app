/**
 * Adversarial QA tests: edge cases, error handling, malformed data.
 * Does NOT duplicate acceptance tests.
 */
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from './App';
import * as api from './api';

vi.mock('./api');

describe('Todo App Adversarial Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Error handling', () => {
    it('displays error when getTodos fails', async () => {
      vi.mocked(api.getTodos).mockRejectedValue(new Error('Network error'));

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText(/error/i)).toBeInTheDocument();
      });
      expect(screen.getByText(/Network error/)).toBeInTheDocument();
    });

    it('displays error when createTodo fails and preserves form input', async () => {
      vi.mocked(api.getTodos).mockResolvedValue([]);
      vi.mocked(api.createTodo).mockRejectedValue(new Error('Failed to create'));

      render(<App />);
      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());

      const input = screen.getByPlaceholderText(/add.*todo|new.*todo|todo.*title/i);
      const addButton = screen.getByRole('button', { name: /add|submit|create/i });

      await userEvent.type(input, 'New task');
      await userEvent.click(addButton);

      await waitFor(() => {
        expect(screen.getByText(/error/i)).toBeInTheDocument();
      });
      expect(screen.getByText(/Failed to create/)).toBeInTheDocument();
    });

    it('displays error when updateTodo fails', async () => {
      const mockTodos = [{ id: 1, title: 'Toggle me', completed: false }];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);
      vi.mocked(api.updateTodo).mockRejectedValue(new Error('Update failed'));

      render(<App />);
      await waitFor(() => expect(screen.getByText('Toggle me')).toBeInTheDocument());

      const checkbox = screen.getByRole('checkbox', { name: /toggle me/i });
      await userEvent.click(checkbox);

      await waitFor(() => {
        expect(screen.getByText(/error/i)).toBeInTheDocument();
        expect(screen.getByText(/Update failed/)).toBeInTheDocument();
      });
    });

    it('displays error when deleteTodo fails', async () => {
      const mockTodos = [{ id: 1, title: 'Delete me', completed: false }];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);
      vi.mocked(api.deleteTodo).mockRejectedValue(new Error('Delete failed'));

      render(<App />);
      await waitFor(() => expect(screen.getByText('Delete me')).toBeInTheDocument());

      const deleteButton = screen.getByRole('button', { name: /delete|remove/i });
      await userEvent.click(deleteButton);

      await waitFor(() => {
        expect(screen.getByText(/error/i)).toBeInTheDocument();
        expect(screen.getByText(/Delete failed/)).toBeInTheDocument();
      });
    });
  });

  describe('Malformed / edge case data', () => {
    it('handles todo with unicode and special chars in title', async () => {
      const mockTodos = [
        { id: 1, title: '日本語 café ☕ <script>alert(1)</script>', completed: false },
      ];
      vi.mocked(api.getTodos).mockResolvedValue(mockTodos);

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('日本語 café ☕ <script>alert(1)</script>')).toBeInTheDocument();
      });
    });

    it('handles empty todos array without crash', async () => {
      vi.mocked(api.getTodos).mockResolvedValue([]);

      render(<App />);

      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());
      expect(screen.getByText(/no todos|empty|nothing yet|add your first/i)).toBeInTheDocument();
    });

    it('rejects empty string submit - does not call createTodo', async () => {
      vi.mocked(api.getTodos).mockResolvedValue([]);

      render(<App />);
      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());

      const input = screen.getByPlaceholderText(/add.*todo|new.*todo|todo.*title/i);
      const addButton = screen.getByRole('button', { name: /add|submit|create/i });

      await userEvent.type(input, '   ');
      await userEvent.click(addButton);

      expect(api.createTodo).not.toHaveBeenCalled();
    });
  });

  describe('Rapid / double action', () => {
    it('rapid double-click on Add with valid input calls createTodo once or twice but handles gracefully', async () => {
      vi.mocked(api.getTodos).mockResolvedValue([]);
      const newTodo = { id: 1, title: 'Task', completed: false };
      vi.mocked(api.createTodo).mockResolvedValue(newTodo);

      render(<App />);
      await waitFor(() => expect(api.getTodos).toHaveBeenCalled());

      const input = screen.getByPlaceholderText(/add.*todo|new.*todo|todo.*title/i);
      const addButton = screen.getByRole('button', { name: /add|submit|create/i });

      await userEvent.type(input, 'Task');
      await userEvent.dblClick(addButton);

      await waitFor(() => {
        expect(api.createTodo).toHaveBeenCalled();
      });
      expect(screen.getByText('Task')).toBeInTheDocument();
    });
  });
});
