package com.todo.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance tests for backend H2 database configuration.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BackendH2ConfigTest {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private Environment environment;

    // covers AC-3
    @Test
    void backendIsConfiguredToUseH2AsDatabase() {
        assertNotNull(dataSource, "Backend shall have a DataSource configured");
        assertNotNull(environment, "Spring context shall be loaded");
        String url = environment.getProperty("spring.datasource.url");
        assertNotNull(url, "spring.datasource.url shall be configured");
        assertTrue(url.contains("h2"), "Backend shall use H2 as the database (URL contains 'h2')");
    }

    // covers AC-12
    @Test
    void backendUsesH2AsSoleDataStore() {
        assertNotNull(environment, "Spring context shall be loaded");
        String url = environment.getProperty("spring.datasource.url");
        assertNotNull(url, "spring.datasource.url shall be configured");
        assertTrue(url.contains("h2"), "Backend shall use H2 for all Todo persistence; no other database");
    }
}
