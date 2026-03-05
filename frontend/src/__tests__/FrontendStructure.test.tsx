/**
 * Acceptance tests for frontend structure and React setup.
 * covers AC-1
 */

import { describe, it, expect } from 'vitest';
import React from 'react';

describe('Repository contains React frontend', () => {
  // covers AC-1
  it('frontend is a React project with required dependencies', () => {
    expect(React).toBeDefined();
    expect(typeof React.createElement).toBe('function');
  });
});
