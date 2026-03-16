/**
 * Acceptance tests for Todo App frontend structure and UI components.
 * Tests validate AC-8 from requirements.md.
 */
import { describe, it, expect } from 'vitest';
import path from 'path';
import { REPO_ROOT, pathExists, readFileSafe } from './helpers.js';

describe('Frontend Todo UI', () => {
  const frontendDir = path.join(REPO_ROOT, 'frontend');
  const srcDir = path.join(frontendDir, 'src');

  // covers AC-8
  it('AC-8: frontend has components for displaying todos', () => {
    const todoListPath = path.join(srcDir, 'components', 'TodoList.jsx');
    const todoListPathTsx = path.join(srcDir, 'components', 'TodoList.tsx');
    const hasTodoList = pathExists(todoListPath) || pathExists(todoListPathTsx);
    expect(hasTodoList, 'TodoList component should exist').toBe(true);
  });

  // covers AC-8
  it('AC-8: frontend has component for creating todos', () => {
    const todoFormPath = path.join(srcDir, 'components', 'TodoForm.jsx');
    const todoFormPathTsx = path.join(srcDir, 'components', 'TodoForm.tsx');
    const hasTodoForm = pathExists(todoFormPath) || pathExists(todoFormPathTsx);
    expect(hasTodoForm, 'TodoForm component should exist').toBe(true);
  });

  // covers AC-8
  it('AC-8: frontend has component for todo item with complete/delete controls', () => {
    const todoItemPath = path.join(srcDir, 'components', 'TodoItem.jsx');
    const todoItemPathTsx = path.join(srcDir, 'components', 'TodoItem.tsx');
    const hasTodoItem = pathExists(todoItemPath) || pathExists(todoItemPathTsx);
    expect(hasTodoItem, 'TodoItem component should exist').toBe(true);

    const content = readFileSafe(todoItemPath) || readFileSafe(todoItemPathTsx);
    const hasCompleteControl = /completed|complete|checkbox|toggle/i.test(content);
    const hasDeleteControl = /delete|remove|onDelete/i.test(content);
    expect(hasCompleteControl, 'TodoItem should support complete/toggle').toBe(true);
    expect(hasDeleteControl, 'TodoItem should support delete').toBe(true);
  });

  // covers AC-8
  it('AC-8: App wires Todo components and API client', () => {
    const appPath = path.join(srcDir, 'App.jsx');
    const appPathTsx = path.join(srcDir, 'App.tsx');
    const appContent = readFileSafe(appPath) || readFileSafe(appPathTsx);

    expect(appContent).toMatch(/TodoList|TodoForm|TodoItem/i);
    expect(appContent).toMatch(/fetch|api|todos/i);
  });

  // covers AC-8 (edge case: empty state)
  it('AC-8: frontend handles empty todo list (empty state)', () => {
    const todoListPath = path.join(srcDir, 'components', 'TodoList.jsx');
    const todoListPathTsx = path.join(srcDir, 'components', 'TodoList.tsx');
    const content = readFileSafe(todoListPath) || readFileSafe(todoListPathTsx);

    const hasEmptyState = /empty|length === 0|\.length|no todos|no items/i.test(content);
    expect(hasEmptyState, 'TodoList should handle empty state').toBe(true);
  });
});
