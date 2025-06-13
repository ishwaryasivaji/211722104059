package com.example.averagecalculator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumbersResponse {
    @JsonProperty("windowPrevState")
    private List<Integer> windowPrevState;
    
    @JsonProperty("windowCurrState")
    private List<Integer> windowCurrState;
    
    @JsonProperty("numbers")
    private List<Integer> numbers;
    
    @JsonProperty("avg")
    private double avg;
}