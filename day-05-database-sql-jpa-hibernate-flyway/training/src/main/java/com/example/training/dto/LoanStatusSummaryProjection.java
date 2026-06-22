package com.example.training.dto;

import java.math.BigDecimal;

public interface LoanStatusSummaryProjection {
    String getStatus();
    Long getTotalLoans();
    BigDecimal getTotalAmount();
    BigDecimal getAverageAmount();
    BigDecimal getMinAmount();
    BigDecimal getMaxAmount();
}
