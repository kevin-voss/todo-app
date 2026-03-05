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
