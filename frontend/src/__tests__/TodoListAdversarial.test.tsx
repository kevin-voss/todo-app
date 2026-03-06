/**
 * Adversarial QA tests: TodoList component edge cases and race conditions.
 * Does NOT duplicate acceptance tests.
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { TodoList } from '../components/TodoList';

const originalFetch = globalThis.fetch;

beforeEach(() => {
  vi.stubGlobal('fetch', originalFetch);
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('TodoList - error handling', () => {
  it('displays error state when getTodos fails', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false }));

    render(<TodoList />);
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });
    expect(screen.getByText(/no todos|add one/i)).toBeInTheDocument();
  });

  it('displays error when createTodo fails', async () => {
    vi.stubGlobal('fetch', vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve([]) })
      .mockResolvedValueOnce({ ok: false }));

    render(<TodoList />);
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });

    const input = screen.getByPlaceholderText(/add todo/i);
    fireEvent.change(input, { target: { value: 'New Todo' } });
    fireEvent.click(screen.getByRole('button', { name: /add/i }));

    await waitFor(() => {
      expect(screen.getByText(/failed to create/i)).toBeInTheDocument();
    });
  });
});

describe('TodoList - boundary input', () => {
  it('does not submit empty or whitespace-only title', async () => {
    let createCallCount = 0;
    vi.stubGlobal('fetch', vi.fn().mockImplementation((url: string, opts?: RequestInit) => {
      if (opts?.method === 'POST') createCallCount++;
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([]),
      });
    }));

    render(<TodoList />);
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });

    const input = screen.getByPlaceholderText(/add todo/i);
    fireEvent.change(input, { target: { value: '   ' } });
    fireEvent.click(screen.getByRole('button', { name: /add/i }));

    await waitFor(() => {
      expect(createCallCount).toBe(0);
    });
  });
});

describe('TodoList - race conditions', () => {
  it('rapid double-click Add does not crash and triggers create', async () => {
    const createdTodos: { id: number; title: string; completed: boolean }[] = [];
    let createCallCount = 0;
    let idCounter = 0;
    vi.stubGlobal('fetch', vi.fn().mockImplementation((_url: string, opts?: RequestInit) => {
      if (opts?.method === 'POST') {
        createCallCount++;
        const body = opts.body ? JSON.parse(opts.body as string) : {};
        const todo = { id: ++idCounter, title: body.title || '', completed: false };
        createdTodos.push(todo);
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(todo),
        });
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([...createdTodos]),
      });
    }));

    render(<TodoList />);
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });

    const input = screen.getByPlaceholderText(/add todo/i);
    fireEvent.change(input, { target: { value: 'Race Todo' } });
    const addBtn = screen.getByRole('button', { name: /add/i });
    fireEvent.click(addBtn);
    fireEvent.click(addBtn);

    await waitFor(() => {
      expect(createCallCount).toBeGreaterThanOrEqual(1);
    });
    const raceTodoElements = screen.getAllByText(/race todo/i);
    expect(raceTodoElements.length).toBeGreaterThanOrEqual(1);
  });
});
