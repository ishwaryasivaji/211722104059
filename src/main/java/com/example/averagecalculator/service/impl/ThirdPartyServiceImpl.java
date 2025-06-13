package com.example.averagecalculator.service.impl;

import com.example.averagecalculator.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ThirdPartyServiceImpl {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

   
    @Value("${third-party.service.primes}")
    private String primesUrl;
    @Value("${third-party.service.fibo}")
    private String fiboUrl;
    @Value("${third-party.service.even}")
    private String evenUrl;
    @Value("${third-party.service.rand}")
    private String randUrl;

    
    @Value("${app.request.timeout}")
    private long timeout;
    @Value("${third-party.service.auth-token}")
    private String authToken;

    @Autowired
    public ThirdPartyServiceImpl(RestTemplate restTemplate, ExecutorService executorService) {
        this.restTemplate = restTemplate;
        this.executorService = executorService;
    }

    @Cacheable(value = "numbersCache", key = "#numberType")
    public CompletableFuture<ApiResponse> fetchNumbers(String numberType) {
        return CompletableFuture.supplyAsync(() -> {
            String url = getUrlForType(numberType);
            try {
               
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(authToken.trim()); 
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                
                log.debug("Making request to {} with headers: {}", url, headers);
                
           
                ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    ApiResponse.class
                );

    
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    log.info("Successful response from {}: {}", url, response.getBody());
                    return response.getBody();
                }
                log.warn("Unexpected response from {}: {}", url, response.getStatusCode());
                return new ApiResponse(List.of());
                
            } catch (HttpClientErrorException e) {
                
                log.error("HTTP Error {} from {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
                return new ApiResponse(List.of());
            } catch (Exception e) {
                log.error("Unexpected error fetching from {}: {}", url, e.getMessage());
                return new ApiResponse(List.of());
            }
        }, executorService).completeOnTimeout(new ApiResponse(List.of()), timeout, TimeUnit.MILLISECONDS);
    }

    private String getUrlForType(String numberType) {
        return switch (numberType.toLowerCase()) {
            case "p" -> primesUrl;
            case "f" -> fiboUrl;
            case "e" -> evenUrl;
            case "r" -> randUrl;
            default -> throw new IllegalArgumentException("Invalid number type: " + numberType);
        };
    }
}