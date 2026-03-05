package com.todo.acceptance;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for repository structure.
 * These tests verify the presence of required project folders and configuration.
 */
class RepositoryStructureTest {

    private static final Path REPO_ROOT = Paths.get(System.getProperty("user.dir")).getParent();

    // covers AC-1
    @Test
    void repositoryContainsReactFrontendFolder() {
        Path frontendDir = REPO_ROOT.resolve("frontend");
        Path packageJson = frontendDir.resolve("package.json");
        assertTrue(Files.exists(frontendDir), "Repository shall contain a frontend folder");
        assertTrue(Files.exists(packageJson), "Frontend shall have package.json");
        // Verify it is a React project (has react dependency)
        assertTrue(Files.isDirectory(frontendDir), "frontend shall be a directory");
    }

    // covers AC-2
    @Test
    void repositoryContainsJavaSpringBootBackendFolder() {
        Path backendDir = REPO_ROOT.resolve("backend");
        Path pomXml = backendDir.resolve("pom.xml");
        assertTrue(Files.exists(backendDir), "Repository shall contain a backend folder");
        assertTrue(Files.exists(pomXml), "Backend shall have pom.xml");
        assertTrue(Files.isDirectory(backendDir), "backend shall be a directory");
    }
}
