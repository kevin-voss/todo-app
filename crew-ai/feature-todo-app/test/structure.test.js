/**
 * Acceptance tests for Todo App project structure and configuration.
 * Tests validate AC-1, AC-2, AC-3, AC-4, AC-9, AC-10 from requirements.md.
 */
import { describe, it, expect } from 'vitest';
import path from 'path';
import { REPO_ROOT, pathExists, readFileSafe } from './helpers.js';

describe('Project Structure', () => {
  // covers AC-1
  it('AC-1: repository contains a separate frontend project built with React JS', () => {
    const frontendDir = path.join(REPO_ROOT, 'frontend');
    expect(pathExists(frontendDir), 'frontend/ directory should exist').toBe(true);

    const packageJsonPath = path.join(frontendDir, 'package.json');
    expect(pathExists(packageJsonPath), 'frontend/package.json should exist').toBe(true);

    const packageJson = JSON.parse(readFileSafe(packageJsonPath) || '{}');
    const deps = { ...packageJson.dependencies, ...packageJson.devDependencies };
    const hasReact = deps.react !== undefined;
    expect(hasReact, 'package.json should include React dependency').toBe(true);
  });

  // covers AC-2
  it('AC-2: repository contains a separate backend project built with Java Spring Boot', () => {
    const backendDir = path.join(REPO_ROOT, 'backend');
    expect(pathExists(backendDir), 'backend/ directory should exist').toBe(true);

    const pomPath = path.join(backendDir, 'pom.xml');
    const gradlePath = path.join(backendDir, 'build.gradle');
    const hasBuildFile = pathExists(pomPath) || pathExists(gradlePath);
    expect(hasBuildFile, 'backend should have pom.xml or build.gradle').toBe(true);

    const buildContent = readFileSafe(pomPath) || readFileSafe(gradlePath);
    expect(buildContent).toMatch(/spring-boot|springboot/i);
  });

  // covers AC-3
  it('AC-3: backend includes H2 SQL database as dependency and has it configured', () => {
    const backendDir = path.join(REPO_ROOT, 'backend');
    const pomPath = path.join(backendDir, 'pom.xml');
    const gradlePath = path.join(backendDir, 'build.gradle');
    const buildContent = readFileSafe(pomPath) || readFileSafe(gradlePath);

    expect(buildContent).toMatch(/h2|com\.h2database/i);

    const propsPath = path.join(backendDir, 'src', 'main', 'resources', 'application.properties');
    const ymlPath = path.join(backendDir, 'src', 'main', 'resources', 'application.yml');
    const configContent = readFileSafe(propsPath) || readFileSafe(ymlPath);

    expect(configContent).toMatch(/datasource|jdbc:h2|h2\.driver/i);
  });

  // covers AC-4
  it('AC-4: README describes how to start the frontend and backend', () => {
    const readmePath = path.join(REPO_ROOT, 'README.md');
    expect(pathExists(readmePath), 'README.md should exist at repository root').toBe(true);

    const readme = readFileSafe(readmePath);
    expect(readme).toMatch(/spring-boot:run|mvn.*run|gradle.*bootRun/i);
    expect(readme).toMatch(/npm run dev|npm start|yarn dev|yarn start/i);
  });

  // covers AC-9
  it('AC-9: boilerplate runs without external database (H2 in-memory or file-based)', () => {
    const propsPath = path.join(REPO_ROOT, 'backend', 'src', 'main', 'resources', 'application.properties');
    const ymlPath = path.join(REPO_ROOT, 'backend', 'src', 'main', 'resources', 'application.yml');
    const configExists = pathExists(propsPath) || pathExists(ymlPath);
    expect(configExists, 'application.properties or application.yml should exist').toBe(true);

    const configContent = readFileSafe(propsPath) || readFileSafe(ymlPath);
    const usesH2Embedded = /jdbc:h2:(mem|file):|h2:mem|h2:file/i.test(configContent);
    expect(usesH2Embedded, 'H2 should be configured as embedded (mem or file)').toBe(true);
  });

  // covers AC-10
  it('AC-10: README documents that frontend and backend can be started independently', () => {
    const readmePath = path.join(REPO_ROOT, 'README.md');
    const readme = readFileSafe(readmePath);

    const hasBackendCmd = /cd backend|backend.*mvn|backend.*gradle/i.test(readme);
    const hasFrontendCmd = /cd frontend|frontend.*npm|frontend.*yarn/i.test(readme);
    expect(hasBackendCmd && hasFrontendCmd, 'README should document separate start commands for both projects').toBe(true);
  });
});
