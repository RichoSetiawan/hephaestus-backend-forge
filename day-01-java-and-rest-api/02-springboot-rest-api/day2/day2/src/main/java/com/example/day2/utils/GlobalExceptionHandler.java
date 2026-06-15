package com.example.day2.utils;

import com.example.day2.dto.FieldErrorResponse;
import com.example.day2.dto.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<FieldErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldErrorResponse.builder()
                        .field(error.getField().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        WebResponse<FieldErrorResponse> errorResponse = WebResponse.<FieldErrorResponse>builder()
                .code("VALIDATION_ERROR")
                .message("Input data tidak valid")
                .errors(fieldErrors) 
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<WebResponse<Void>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        WebResponse<Void> errorResponse = WebResponse.<Void>builder()
                .code("NOT_FOUND_CUSTOMER")
                .message(ex.getMessage())
                .errors(null) 
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Void>> handleGeneralException(Exception ex) {
        WebResponse<Void> errorResponse = WebResponse.<Void>builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("Terjadi kesalahan internal pada server: " + ex.getMessage())
                .errors(null)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
