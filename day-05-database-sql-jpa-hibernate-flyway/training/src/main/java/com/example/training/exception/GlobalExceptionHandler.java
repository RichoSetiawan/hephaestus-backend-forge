package com.example.training.exception;

import com.example.training.dto.ApiResponse;
import com.example.training.dto.FieldErrorResponse;
import com.example.training.utils.LoggingUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> FieldErrorResponse.builder()
                        .field(e.getField())
                        .message(e.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        LoggingUtil.error("VALIDATION_ERROR", "Input validation failed", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", "Invalid request", errors));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomerNotFound(CustomerNotFoundException ex) {
        LoggingUtil.error("CUSTOMER_NOT_FOUND", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("CUSTOMER_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(LoanApplicationNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoanNotFound(LoanApplicationNotFoundException ex) {
        LoggingUtil.error("LOAN_NOT_FOUND", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LOAN_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(RepaymentScheduleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRepaymentNotFound(RepaymentScheduleNotFoundException ex) {
        LoggingUtil.error("REPAYMENT_NOT_FOUND", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("REPAYMENT_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateCustomer(DuplicateCustomerException ex) {
        LoggingUtil.error("DUPLICATE_CUSTOMER", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("DUPLICATE_CUSTOMER", ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        LoggingUtil.error("INVALID_STATE", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error("INVALID_STATE", ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        LoggingUtil.error("UNEXPECTED_ERROR", "An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred", null));
    }
}