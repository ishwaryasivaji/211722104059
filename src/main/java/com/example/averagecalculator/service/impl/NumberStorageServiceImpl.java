package com.example.averagecalculator.service.impl;


import com.example.averagecalculator.config.AppConfig;
import com.example.averagecalculator.dto.NumbersResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NumberStorageServiceImpl {

    private final Deque<Integer> numberWindow;
    private final int windowSize;

    @Autowired
    public NumberStorageServiceImpl(AppConfig appConfig) {
        this.windowSize = appConfig.windowSize();
        this.numberWindow = new ConcurrentLinkedDeque<>();
    }

  
    public NumbersResponse processNumbers(List<Integer> newNumbers) {
        if (newNumbers == null) {
            newNumbers = Collections.emptyList();
        }

        synchronized (numberWindow) {
            List<Integer> prevState = new ArrayList<>(numberWindow);
            
            Set<Integer> uniqueNewNumbers = new LinkedHashSet<>(newNumbers);
            uniqueNewNumbers.removeAll(numberWindow);
            
            for (int num : uniqueNewNumbers) {
                if (numberWindow.size() >= windowSize) {
                    numberWindow.removeFirst();
                }
                numberWindow.addLast(num);
            }
            
            List<Integer> currState = new ArrayList<>(numberWindow);
            double average = calculateAverage(currState);
            
            log.info("Processed numbers. Previous state: {}, Current state: {}, Average: {}", 
                    prevState, currState, average);
            
            return new NumbersResponse(
                prevState,
                currState,
                newNumbers,
                average
            );
        }
    }

   
    public List<Integer> getCurrentWindow() {
        return new ArrayList<>(numberWindow);
    }

    private double calculateAverage(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0.0;
        }
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}