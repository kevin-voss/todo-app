/**
 * AC-5 through AC-8: Frontend acceptance tests with mocked API.
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';
import * as todoApi from './todoApi';

vi.mock('./todoApi');

describe('App acceptance', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(todoApi.getTodos).mockResolvedValue([]);
  });

  it('AC-5/6: loads and displays todos from API', async () => {
    vi.mocked(todoApi.getTodos).mockResolvedValue([
      { id: 1, title: 'Buy milk', completed: false },
    ]);
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText('Buy milk')).toBeInTheDocument();
    });
  });

  it('AC-6: shows empty state when no todos', async () => {
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText(/no todos/i)).toBeInTheDocument();
    });
  });

  it('AC-5: creates todo on submit', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.createTodo).mockResolvedValue({
      id: 1,
      title: 'New task',
      completed: false,
    });
    render(<App />);
    await waitFor(() => expect(todoApi.getTodos).toHaveBeenCalled());
    await user.type(screen.getByPlaceholderText(/add todo/i), 'New task');
    await user.click(screen.getByRole('button', { name: /add/i }));
    await waitFor(() => {
      expect(todoApi.createTodo).toHaveBeenCalledWith('New task', false);
      expect(screen.getByText('New task')).toBeInTheDocument();
    });
  });

  it('AC-7: toggles completion via PATCH', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.getTodos).mockResolvedValue([
      { id: 1, title: 'Task', completed: false },
    ]);
    vi.mocked(todoApi.updateTodo).mockResolvedValue({
      id: 1,
      title: 'Task',
      completed: true,
    });
    render(<App />);
    await waitFor(() => expect(screen.getByText('Task')).toBeInTheDocument());
    await user.click(screen.getByRole('checkbox'));
    await waitFor(() => {
      expect(todoApi.updateTodo).toHaveBeenCalledWith(1, { completed: true });
    });
  });

  it('AC-7: deletes todo via DELETE', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.getTodos).mockResolvedValue([
      { id: 1, title: 'To delete', completed: false },
    ]);
    vi.mocked(todoApi.deleteTodo).mockResolvedValue();
    render(<App />);
    await waitFor(() => expect(screen.getByText('To delete')).toBeInTheDocument());
    await user.click(screen.getByRole('button', { name: /delete/i }));
    await waitFor(() => {
      expect(todoApi.deleteTodo).toHaveBeenCalledWith(1);
      expect(screen.queryByText('To delete')).not.toBeInTheDocument();
    });
  });
});
