package com.fingerprint.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary // This makes this bean take precedence during tests
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor(); // Synchronous for tests
    }

}
