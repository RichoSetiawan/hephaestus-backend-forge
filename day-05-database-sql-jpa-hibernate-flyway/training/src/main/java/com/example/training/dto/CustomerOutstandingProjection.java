package com.example.training.dto;

import java.math.BigDecimal;

public interface CustomerOutstandingProjection {
    Long getCustomerId();
    String getFullName();
    String getNik();
    BigDecimal getTotalLoanAmount();
    BigDecimal getTotalPaid();
    BigDecimal getOutstandingAmount();
    Long getTotalLoans();
    Long getActiveLoans();
}