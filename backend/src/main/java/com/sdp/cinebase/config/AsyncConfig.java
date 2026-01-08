package com.sdp.cinebase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration to enable asynchronous method execution.
 * This allows @Async annotated methods to run in separate threads.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring will automatically configure a default executor for async tasks
}
