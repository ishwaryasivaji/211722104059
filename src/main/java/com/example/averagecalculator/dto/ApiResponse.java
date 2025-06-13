package com.example.averagecalculator.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ApiResponse {
    @JsonProperty("numbers")
    private List<Integer> numbers;

    
    public ApiResponse() {
        this.numbers = Collections.emptyList();
    }

   
    public ApiResponse(List<Integer> numbers) {
        this.numbers = numbers;
    }

    
    public static ApiResponse empty() {
        return new ApiResponse(Collections.emptyList());
    }

   
    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}