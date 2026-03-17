package com.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for AC-1: Spring Boot backend starts and connects to H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BackendStartupH2Test {

    @Autowired(required = false)
    private DataSource dataSource;

    // covers AC-1
    @Test
    void applicationContextLoads() {
        assertNotNull(dataSource, "DataSource must be configured - Spring Boot app runs and connects");
    }

    // covers AC-1
    @Test
    void h2DataSourceIsConfigured() {
        assertNotNull(dataSource, "H2 datasource must be configured - connection to H2 established");
        String url = dataSource.toString();
        assertTrue(url.contains("h2") || url.contains("H2"), "DataSource must be H2");
    }
}
