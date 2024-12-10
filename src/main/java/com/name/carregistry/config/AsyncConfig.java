package com.name.carregistry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;


@EnableAsync
public class AsyncConfig {

    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize (10);
        executor.setCorePoolSize(5);
        executor.setThreadNamePrefix ("CarRegistryThread-");

        return executor;
    }
}