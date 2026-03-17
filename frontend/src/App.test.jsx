import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';

// covers AC-2
describe('App', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  // covers AC-2: React app loads in browser and can call backend API
  it('loads and fetches todos on mount', async () => {
    const mockTodos = [{ id: 1, title: 'First todo', completed: false }];
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockTodos),
    });

    render(<App />);

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/api/todos'), expect.anything());
    });
  });

  // covers AC-4: all todos are displayed
  it('displays list of todos when loaded', async () => {
    const mockTodos = [
      { id: 1, title: 'Todo 1', completed: false },
      { id: 2, title: 'Todo 2', completed: true },
    ];
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockTodos),
    });

    render(<App />);

    await waitFor(() => {
      expect(screen.getByText('Todo 1')).toBeInTheDocument();
      expect(screen.getByText('Todo 2')).toBeInTheDocument();
    });
  });

  // covers AC-4 edge case: empty list displays empty state
  it('displays empty state when no todos', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([]),
    });

    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/no todos|empty|add your first/i)).toBeInTheDocument();
    });
  });

  // covers AC-3: user can create a todo
  it('allows creating a new todo', async () => {
    const user = userEvent.setup();
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve([]) })
      .mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ id: 1, title: 'New todo', completed: false }),
      });

    render(<App />);

    await waitFor(() => expect(fetch).toHaveBeenCalled());

    const input = screen.getByPlaceholderText(/add|new todo|title/i);
    const submit = screen.getByRole('button', { name: /add|submit|create/i });

    await user.type(input, 'New todo');
    await user.click(submit);

    await waitFor(() => {
      expect(screen.getByText('New todo')).toBeInTheDocument();
    });
  });

  // covers AC-7: user can toggle completion
  it('allows toggling todo completion', async () => {
    const user = userEvent.setup();
    const mockTodos = [{ id: 1, title: 'Todo 1', completed: false }];
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(mockTodos) })
      .mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ id: 1, title: 'Todo 1', completed: true }),
      });

    render(<App />);

    await waitFor(() => expect(screen.getByText('Todo 1')).toBeInTheDocument());

    const checkbox = screen.getByRole('checkbox');
    await user.click(checkbox);

    await waitFor(() => {
      expect(checkbox).toBeChecked();
    });
  });

  // covers AC-6: user can delete a todo
  it('allows deleting a todo', async () => {
    const user = userEvent.setup();
    const mockTodos = [{ id: 1, title: 'To delete', completed: false }];
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(mockTodos) })
      .mockResolvedValueOnce({ ok: true });

    render(<App />);

    await waitFor(() => expect(screen.getByText('To delete')).toBeInTheDocument());

    const deleteBtn = screen.getByRole('button', { name: /delete|remove/i });
    await user.click(deleteBtn);

    await waitFor(() => {
      expect(screen.queryByText('To delete')).not.toBeInTheDocument();
    });
  });

  // covers AC-5: user can edit a todo
  it('allows editing a todo', async () => {
    const user = userEvent.setup();
    const mockTodos = [{ id: 1, title: 'Original', completed: false }];
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(mockTodos) })
      .mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ id: 1, title: 'Edited', completed: false }),
      });

    render(<App />);

    await waitFor(() => expect(screen.getByText('Original')).toBeInTheDocument());

    const editBtn = screen.getByRole('button', { name: /edit/i });
    await user.click(editBtn);

    const input = screen.getByDisplayValue('Original');
    await user.clear(input);
    await user.type(input, 'Edited');
    await user.keyboard('{Enter}');

    await waitFor(() => {
      expect(screen.getByText('Edited')).toBeInTheDocument();
    });
  });
});
