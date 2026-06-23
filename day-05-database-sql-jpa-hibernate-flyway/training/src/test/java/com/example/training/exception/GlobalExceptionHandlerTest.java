package com.example.training.exception;

import com.example.training.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        FieldError fieldError = new FieldError("createCustomerRequest", "fullName", "full_name is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Invalid request", response.getBody().getMessage());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("fullName", response.getBody().getErrors().get(0).getField());
        assertEquals("full_name is required", response.getBody().getErrors().get(0).getMessage());
    }

    @Test
    void handleValidation_shouldReturn400WithMultipleFieldErrors() {
        FieldError fieldError1 = new FieldError("req", "fullName", "full_name is required");
        FieldError fieldError2 = new FieldError("req", "nik", "nik is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().getErrors().size());
    }

    @Test
    void handleCustomerNotFound_shouldReturn404() {
        CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found with id: 1");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleCustomerNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("CUSTOMER_NOT_FOUND", response.getBody().getCode());
        assertEquals("Customer not found with id: 1", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleLoanNotFound_shouldReturn404() {
        LoanApplicationNotFoundException ex = new LoanApplicationNotFoundException("Loan not found with id: 1");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleLoanNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("LOAN_NOT_FOUND", response.getBody().getCode());
        assertEquals("Loan not found with id: 1", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleRepaymentNotFound_shouldReturn404() {
        RepaymentScheduleNotFoundException ex = new RepaymentScheduleNotFoundException("Repayment schedule not found with id: 5");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleRepaymentNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("REPAYMENT_NOT_FOUND", response.getBody().getCode());
        assertEquals("Repayment schedule not found with id: 5", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleDuplicateCustomer_shouldReturn409() {
        DuplicateCustomerException ex = new DuplicateCustomerException("NIK already exists: 1234567890123456");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleDuplicateCustomer(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("DUPLICATE_CUSTOMER", response.getBody().getCode());
        assertEquals("NIK already exists: 1234567890123456", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleIllegalState_shouldReturn422() {
        IllegalStateException ex = new IllegalStateException("SUBMITTED can only become APPROVED or REJECTED");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleIllegalState(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_STATE", response.getBody().getCode());
        assertEquals("SUBMITTED can only become APPROVED or REJECTED", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleGeneral_shouldReturn500() {
        Exception ex = new RuntimeException("Unexpected database error");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }
}
