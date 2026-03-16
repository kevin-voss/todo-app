/**
 * Adversarial tests: double-click, API errors, edge cases.
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';
import * as todoApi from './todoApi';

vi.mock('./todoApi');

describe('App adversarial', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(todoApi.getTodos).mockResolvedValue([]);
  });

  it('rapid double-click on Add does not create duplicate todos', async () => {
    const user = userEvent.setup();
    let resolveCreate;
    const createPromise = new Promise((r) => { resolveCreate = r; });
    vi.mocked(todoApi.createTodo).mockReturnValue(createPromise);

    render(<App />);
    await waitFor(() => expect(todoApi.getTodos).toHaveBeenCalled());

    const input = screen.getByPlaceholderText(/add todo/i);
    const addBtn = screen.getByRole('button', { name: /add/i });
    await user.type(input, 'Task');
    await user.dblClick(addBtn);

    resolveCreate({ id: 1, title: 'Task', completed: false });
    await waitFor(() => expect(todoApi.createTodo).toHaveBeenCalledTimes(1));
    expect(screen.getAllByText('Task').length).toBeLessThanOrEqual(1);
  });

  it('getTodos rejection is caught and does not throw', async () => {
    vi.mocked(todoApi.getTodos).mockRejectedValue(new Error('Network error'));
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    render(<App />);
    await waitFor(() => {
      expect(screen.getByText(/failed to load|network error/i)).toBeInTheDocument();
    });

    consoleSpy.mockRestore();
  });

  it('createTodo rejection is caught', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.createTodo).mockRejectedValue(new Error('Create failed'));

    render(<App />);
    await waitFor(() => expect(todoApi.getTodos).toHaveBeenCalled());
    await user.type(screen.getByPlaceholderText(/add todo/i), 'Task');
    await user.click(screen.getByRole('button', { name: /add/i }));

    await waitFor(() => {
      expect(screen.getByText(/failed|create failed/i)).toBeInTheDocument();
    });
  });

  it('updateTodo rejection is caught', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.getTodos).mockResolvedValue([
      { id: 1, title: 'Task', completed: false },
    ]);
    vi.mocked(todoApi.updateTodo).mockRejectedValue(new Error('Update failed'));

    render(<App />);
    await waitFor(() => expect(screen.getByText('Task')).toBeInTheDocument());
    await user.click(screen.getByRole('checkbox'));

    await waitFor(() => {
      expect(screen.getByText(/failed|update failed/i)).toBeInTheDocument();
    });
  });

  it('deleteTodo rejection is caught', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.getTodos).mockResolvedValue([
      { id: 1, title: 'To delete', completed: false },
    ]);
    vi.mocked(todoApi.deleteTodo).mockRejectedValue(new Error('Delete failed'));

    render(<App />);
    await waitFor(() => expect(screen.getByText('To delete')).toBeInTheDocument());
    await user.click(screen.getByRole('button', { name: /delete/i }));

    await waitFor(() => {
      expect(screen.getByText(/failed|delete failed/i)).toBeInTheDocument();
    });
  });

  it('trims whitespace from new todo title', async () => {
    const user = userEvent.setup();
    vi.mocked(todoApi.createTodo).mockResolvedValue({
      id: 1,
      title: 'Trimmed',
      completed: false,
    });

    render(<App />);
    await waitFor(() => expect(todoApi.getTodos).toHaveBeenCalled());
    await user.type(screen.getByPlaceholderText(/add todo/i), '  Trimmed  ');
    await user.click(screen.getByRole('button', { name: /add/i }));

    await waitFor(() => {
      expect(todoApi.createTodo).toHaveBeenCalledWith('Trimmed', false);
    });
  });

  it('Add button is disabled while createTodo is in flight', async () => {
    const user = userEvent.setup();
    let resolveCreate;
    const createPromise = new Promise((r) => { resolveCreate = r; });
    vi.mocked(todoApi.createTodo).mockReturnValue(createPromise);

    render(<App />);
    await waitFor(() => expect(todoApi.getTodos).toHaveBeenCalled());
    await user.type(screen.getByPlaceholderText(/add todo/i), 'Task');
    const addBtn = screen.getByRole('button', { name: /add/i });
    await user.click(addBtn);

    expect(addBtn).toBeDisabled();
    resolveCreate({ id: 1, title: 'Task', completed: false });
    await waitFor(() => expect(screen.getByRole('button', { name: /add/i })).not.toBeDisabled());
  });
});
