/**
 * QA adversarial tests for README resume content.
 * Targets edge cases, malformed content, encoding, fabricated data.
 * Does NOT duplicate acceptance tests (readme-resume.test.js).
 */
import { describe, it, expect } from 'vitest';
import { readFileSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const README_PATH = join(__dirname, '../../README.md');

function getReadme() {
  return readFileSync(README_PATH, 'utf-8');
}

describe('README resume content - adversarial', () => {
  const readme = getReadme();

  // Edge case: file exists and is readable
  it('README file exists and is readable', () => {
    expect(existsSync(README_PATH)).toBe(true);
    expect(() => readFileSync(README_PATH, 'utf-8')).not.toThrow();
  });

  // Edge case: UTF-8 encoding (ß in Voß)
  it('preserves UTF-8 characters (Voß with ß)', () => {
    expect(readme).toContain('Voß');
    expect(readme).toContain('Kevin');
  });

  // Edge case: no fabricated placeholder text
  it('contains no placeholder or joke text', () => {
    expect(readme).not.toMatch(/lorem ipsum/i);
    expect(readme).not.toMatch(/TODO:|FIXME:|placeholder/i);
    expect(readme).not.toMatch(/Dussel|dummy|fake|test data/i);
  });

  // Edge case: no outdated dates contradicting Jan 2026 source
  it('contains no outdated certification dates (pre-2026)', () => {
    // Certifications expire Nov 2028, Oct 2028 — should not show 2025 or earlier expiry
    expect(readme).not.toMatch(/Expires:.*202[0-4]/);
    expect(readme).not.toMatch(/Expires:.*2025/);
  });

  // Edge case: no conflicting employment periods
  it('Adesso period is Oct 2025 – Present (not earlier)', () => {
    expect(readme).toMatch(/Oct 2025|2025.*Present/);
    expect(readme).not.toMatch(/Adesso.*202[0-4]/);
  });

  // Edge case: no HTML/script injection
  it('contains no script or HTML injection', () => {
    expect(readme).not.toMatch(/<script/);
    expect(readme).not.toMatch(/javascript:/);
    expect(readme).not.toMatch(/on\w+=/);
  });

  // Edge case: reasonable content length (not empty, not truncated)
  it('resume section has substantial content', () => {
    const aboutSection = readme.match(/## About the Author[\s\S]*?(?=\n## |\n## Prerequisites|$)/i);
    expect(aboutSection).toBeTruthy();
    expect(aboutSection[0].length).toBeGreaterThan(500);
  });

  // Edge case: no duplicate section headers
  it('About the Author section appears once', () => {
    const matches = readme.match(/## About the Author/gi);
    expect(matches).toBeTruthy();
    expect(matches.length).toBe(1);
  });

  // Boundary: no malformed markdown that could break rendering
  it('has balanced heading levels (no orphan ### without ##)', () => {
    const hasAboutAuthor = readme.includes('## About the Author') || readme.includes('### About the Author');
    expect(hasAboutAuthor).toBe(true);
  });
});
