/**
 * Acceptance tests for README resume content (Jan 2026).
 * Spec: crew-ai/feature-first-task/spec/requirements.md
 * Target: README.md at project root
 *
 * These tests are EXPECTED TO FAIL until the Developer implements the README update.
 */
import { describe, it, expect } from 'vitest';
import { readFileSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const README_PATH = join(__dirname, '../../README.md');

function getReadme() {
  return readFileSync(README_PATH, 'utf-8');
}

describe('README resume content (Jan 2026)', () => {
  const readme = getReadme();

  // covers AC-1
  it('includes Kevin Voß professional identity (name, title, location)', () => {
    expect(readme).toContain('Kevin Voß, B.Sc.');
    expect(readme).toContain('Senior Fullstack GenAI Software Engineer');
    expect(readme).toContain('Hannover, Germany');
  });

  // covers AC-2
  it('includes the full Professional Summary from Jan 2026 resume', () => {
    expect(readme).toContain('4.5+ years of professional experience');
    expect(readme).toContain('cloud-native and AI-powered systems');
    expect(readme).toContain('reducing cloud costs by up to 90%');
    expect(readme).toContain('Passionate about combining hands-on engineering with innovative AI tooling');
    expect(readme).toContain('practical, high-impact solutions');
  });

  // covers AC-3
  it('includes Core Skills with all five categories and items', () => {
    expect(readme).toMatch(/AI\s*[&|and]\s*GenAI/i);
    expect(readme).toContain('LLMs');
    expect(readme).toContain('RAG');
    expect(readme).toContain('Vector Databases');
    expect(readme).toContain('Frontend');
    expect(readme).toContain('React');
    expect(readme).toContain('Backend');
    expect(readme).toContain('Spring Boot');
    expect(readme).toContain('Cloud & DevOps');
    expect(readme).toContain('AWS');
    expect(readme).toContain('Methods & Tools');
    expect(readme).toContain('TDD');
  });

  // covers AC-4
  it('includes Professional Experience with Adesso SE and Micromerce GmbH', () => {
    expect(readme).toContain('Adesso SE');
    expect(readme).toContain('Oct 2025');
    expect(readme).toContain('Present');
    expect(readme).toContain('Micromerce GmbH');
    expect(readme).toContain('Dec 2021');
    expect(readme).toContain('Sep 2025');
  });

  // covers AC-4 (edge case: all Adesso projects)
  it('includes Adesso key achievements: GS1, Mercedes-Benz, Siemens RAG', () => {
    expect(readme).toMatch(/GS1|Rule Transformer/i);
    expect(readme).toMatch(/Mercedes|WebSocket/i);
    expect(readme).toMatch(/Siemens|RAG Validator/i);
  });

  // covers AC-4
  it('includes Micromerce key achievements', () => {
    expect(readme).toMatch(/microfrontend|microservices|90%|AI tools|mentor/i);
  });

  // covers AC-5
  it('includes Education: B.Sc. Hildesheim, LUH, Abitur Werner von Siemens', () => {
    expect(readme).toMatch(/Hildesheim|Business Informatics/i);
    expect(readme).toMatch(/Leibniz|LUH|Computer Science/i);
    expect(readme).toMatch(/Werner von Siemens|Abitur/i);
    expect(readme).toContain('04/2019');
    expect(readme).toContain('03/2023');
  });

  // covers AC-6
  it('includes Certifications with AWS certs and expiry dates', () => {
    expect(readme).toContain('AWS Certified Developer');
    expect(readme).toContain('AWS Certified Cloud Practitioner');
    expect(readme).toContain('Nov 2028');
    expect(readme).toContain('Oct 2028');
  });

  // covers AC-7
  it('includes Additional: languages, work preferences, interests', () => {
    expect(readme).toMatch(/German.*Native|Native.*German/i);
    expect(readme).toMatch(/English.*Fluent|Fluent.*English/i);
    expect(readme).toMatch(/remote|hybrid/i);
    expect(readme).toMatch(/small companies|startups/i);
    expect(readme).toContain('Programming');
    expect(readme).toContain('Cooking');
    expect(readme).toContain('Piano');
  });

  // covers AC-7 (edge case)
  it('includes "Highly adaptable, quickly picking up new skills"', () => {
    expect(readme).toContain('Highly adaptable');
    expect(readme).toContain('quickly picking up new skills');
  });

  // covers AC-8
  it('uses valid Markdown (headings, lists, or tables)', () => {
    expect(readme).toMatch(/#+\s+\w+/);
    expect(readme).toMatch(/\n[-*]\s|\n\d+\.\s|^\|.+\|/m);
  });

  // covers AC-9
  it('contains no fabricated or outdated data', () => {
    expect(readme).not.toContain('Dussel');
    expect(readme).not.toMatch(/Kevin ist ein/i);
  });
});
