/**
 * Adversarial QA tests: TodoItem component edge cases.
 * Does NOT duplicate acceptance tests. Focuses on malformed todo data,
 * rapid interactions, and boundary values.
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { TodoItem } from '../components/TodoItem';

const originalFetch = globalThis.fetch;

beforeEach(() => {
  vi.stubGlobal('fetch', originalFetch);
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('TodoItem - malformed todo data', () => {
  it('renders without crash when todo has missing completed field', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true }));

    const malformedTodo = { id: 1, title: 'Test', completed: undefined } as { id: number; title: string; completed?: boolean };
    const onUpdate = vi.fn();
    const onDelete = vi.fn();

    render(<TodoItem todo={malformedTodo} onUpdate={onUpdate} onDelete={onDelete} />);

    expect(screen.getByText('Test')).toBeInTheDocument();
  });

  it('renders without crash when todo has missing title', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true }));

    const malformedTodo = { id: 1, title: undefined, completed: false } as { id: number; title?: string; completed: boolean };
    const onUpdate = vi.fn();
    const onDelete = vi.fn();

    render(<TodoItem todo={malformedTodo} onUpdate={onUpdate} onDelete={onDelete} />);

    expect(screen.getByRole('checkbox')).toBeInTheDocument();
  });

  it('escapes XSS in title (does not execute script)', () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true }));

    const xssTodo = { id: 1, title: '<script>alert(1)</script>', completed: false };
    const onUpdate = vi.fn();
    const onDelete = vi.fn();

    render(<TodoItem todo={xssTodo} onUpdate={onUpdate} onDelete={onDelete} />);

    const span = screen.getByText('<script>alert(1)</script>');
    expect(span).toBeInTheDocument();
    expect(span.innerHTML).not.toContain('<script>');
  });
});

describe('TodoItem - rapid interaction', () => {
  it('rapid double-click toggle does not corrupt state', async () => {
    let updateCallCount = 0;
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => {
      updateCallCount++;
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ id: 1, title: 'Toggle', completed: updateCallCount % 2 === 1 }),
      });
    }));

    const todo = { id: 1, title: 'Toggle', completed: false };
    const onUpdate = vi.fn();
    const onDelete = vi.fn();

    render(<TodoItem todo={todo} onUpdate={onUpdate} onDelete={onDelete} />);

    const checkbox = screen.getByRole('checkbox');
    fireEvent.click(checkbox);
    fireEvent.click(checkbox);
    fireEvent.click(checkbox);

    await waitFor(() => {
      expect(updateCallCount).toBeGreaterThanOrEqual(1);
    });
    expect(screen.getByText('Toggle')).toBeInTheDocument();
  });

  it('rapid double-click Delete does not crash', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true }));

    const todo = { id: 1, title: 'To Delete', completed: false };
    const onUpdate = vi.fn();
    const onDelete = vi.fn();

    render(<TodoItem todo={todo} onUpdate={onUpdate} onDelete={onDelete} />);

    const deleteBtn = screen.getByRole('button', { name: /delete/i });
    fireEvent.click(deleteBtn);
    fireEvent.click(deleteBtn);

    await waitFor(() => {
      expect(onDelete).toHaveBeenCalled();
    });
  });
});
