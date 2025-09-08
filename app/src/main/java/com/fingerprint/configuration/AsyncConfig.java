package com.fingerprint.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Number of threads to keep in the pool
        executor.setMaxPoolSize(10); // Maximum number of threads
        executor.setQueueCapacity(25); // Queue capacity for waiting tasks
        executor.setThreadNamePrefix("async-"); // Thread name prefix
        executor.initialize();
        return executor;
    }
}
