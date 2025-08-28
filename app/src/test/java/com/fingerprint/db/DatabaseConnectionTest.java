package com.fingerprint.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private Environment env;

    @Test
    void testThatCanConnectToDatabase() {
        try {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.url"))
                    .username(env.getProperty("spring.datasource.username"))
                    .password(env.getProperty("spring.datasource.password"))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build();

            try (Connection connection = dataSource.getConnection()) {
                assertNotNull(connection, "Connection should not be null");
                System.out.println("✅ Successfully connected to the PostgreSQL database.");
            }
        } catch (Exception e) {
            fail(" Database connection failed: " + e.getMessage());
        }
    }
}
