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
public class CustomerOutstandingDto {

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("nik")
    private String nik;

    @JsonProperty("total_loan_amount")
    private BigDecimal totalLoanAmount;

    @JsonProperty("total_paid")
    private BigDecimal totalPaid;

    @JsonProperty("outstanding_amount")
    private BigDecimal outstandingAmount;

    @JsonProperty("payment_percentage")
    private BigDecimal paymentPercentage;

    @JsonProperty("total_loans")
    private Long totalLoans;

    @JsonProperty("active_loans")
    private Long activeLoans;
}

