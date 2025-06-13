package com.example.averagecalculator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Value("${app.window.size}")
    private int windowSize;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Value("${third-party.service.auth-token}")
    private String authToken;

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public int windowSize() {
        return windowSize;
    }
}