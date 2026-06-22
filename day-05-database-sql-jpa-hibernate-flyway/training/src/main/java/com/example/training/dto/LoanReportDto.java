package com.example.training.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanReportDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_loans")
    private Long totalLoans;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("average_amount")
    private BigDecimal averageAmount;

    @JsonProperty("min_amount")
    private BigDecimal minAmount;

    @JsonProperty("max_amount")
    private BigDecimal maxAmount;
}