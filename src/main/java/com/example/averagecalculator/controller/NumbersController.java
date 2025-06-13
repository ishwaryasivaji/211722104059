package com.example.averagecalculator.controller;

import com.example.averagecalculator.dto.NumbersResponse;
import com.example.averagecalculator.service.impl.NumberStorageServiceImpl;
import com.example.averagecalculator.service.impl.ThirdPartyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/numbers")
@Slf4j
public class NumbersController {

    private final ThirdPartyServiceImpl thirdPartyService;
    private final NumberStorageServiceImpl numberStorageService;

    @Autowired
    public NumbersController(ThirdPartyServiceImpl thirdPartyService, 
                           NumberStorageServiceImpl numberStorageService) {
        this.thirdPartyService = thirdPartyService;
        this.numberStorageService = numberStorageService;
    }

    @GetMapping("/{numberId}")
    public ResponseEntity<NumbersResponse> getNumbers(@PathVariable String numberId) {
        if (!isValidNumberId(numberId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            CompletableFuture<NumbersResponse> future = thirdPartyService.fetchNumbers(numberId)
                    .thenApply(apiResponse -> {
                        List<Integer> numbers = apiResponse.getNumbers();
                        return numberStorageService.processNumbers(numbers);
                    });

            NumbersResponse response = future.get(500, TimeUnit.MILLISECONDS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing request for numberId {}: {}", numberId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isValidNumberId(String numberId) {
        return numberId.matches("[pfer]");
    }
}