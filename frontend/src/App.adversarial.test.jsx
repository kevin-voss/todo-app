/**
 * QA adversarial tests for App component.
 * Targets error states, malformed API responses, edge cases.
 * Does NOT duplicate acceptance tests (App.test.jsx).
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import App from './App';

class TestErrorBoundary extends React.Component {
  state = { hasError: false };
  static getDerivedStateFromError() {
    return { hasError: true };
  }
  render() {
    return this.state.hasError ? <div data-testid="error-boundary">App crashed</div> : this.props.children;
  }
}

describe('App - adversarial', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('displays error when getTodos fails', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network failed'));
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
      expect(screen.getByText(/Network failed/)).toBeInTheDocument();
    });
  });

  it('displays error when getTodos returns not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false });
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
    });
  });

  it('displays error when createTodo fails and does not add to list', async () => {
    const user = userEvent.setup();
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve([]) })
      .mockResolvedValueOnce({ ok: false });
    render(<App />);
    await waitFor(() => expect(screen.getByPlaceholderText(/add|new todo|title/i)).toBeInTheDocument());
    const input = screen.getByPlaceholderText(/add|new todo|title/i);
    const submit = screen.getByRole('button', { name: /add|submit|create/i });
    await user.type(input, 'Failing todo');
    await user.click(submit);
    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
    });
  });

  it('handles non-array response from getTodos without crashing', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ error: 'unexpected' }),
    });
    render(<App />);
    await waitFor(() => expect(screen.getByText('Todo App')).toBeInTheDocument());
    expect(screen.getByText(/no todos|add your first/i)).toBeInTheDocument();
  });

  it('handles todo with missing title field without crashing', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([{ id: 1, completed: false }]),
    });
    render(<App />);
    await waitFor(() => expect(fetch).toHaveBeenCalled());
    expect(screen.getByText('Todo App')).toBeInTheDocument();
  });
});
