package com.example.training.controller;

import com.example.training.dto.*;
import com.example.training.enums.LoanStatus;
import com.example.training.service.LoanApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanService;

    @PostMapping("/loan-applications")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> create(@Valid @RequestBody CreateLoanApplicationRequest request) {
        LoanApplicationResponse data = loanService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Loan application created successfully", data));
    }

    @GetMapping("/loan-applications/{id}")
    public ResponseEntity<ApiResponse<LoanApplicationDetailResponse>> getById(@PathVariable Long id) {
        LoanApplicationDetailResponse data = loanService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Loan application retrieved successfully", data));
    }

    @GetMapping("/loan-applications")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getAll(
            @RequestParam(value = "status", required = false) LoanStatus status) {
        List<LoanApplicationResponse> data = status != null ? loanService.findByStatus(status) : loanService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Loan applications retrieved successfully", data));
    }

    @GetMapping("/customers/{customerId}/loan-applications")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getByCustomerId(@PathVariable Long customerId) {
        List<LoanApplicationResponse> data = loanService.findByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer loan applications retrieved successfully", data));
    }

    @GetMapping("/pagination")
    public ResponseEntity<ApiResponse<Page<LoanApplicationResponse>>> getAllLoans(
        @RequestParam(required = false) LoanStatus status,
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
        @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
    Page<LoanApplicationResponse> data = loanService.getAllLoanPagination(status, startDate, endDate, pageable);
    return ResponseEntity.ok(ApiResponse.success("Successfully fetched paged loans", data));
    }

    @PatchMapping("/loan-applications/{id}/status")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLoanStatusRequest request) {
        LoanApplicationResponse data = loanService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Loan status updated successfully", data));
    }

    @GetMapping("/reports/loan-summary-by-status")
    @Operation(summary = "Summary total loan grouped by status")
    public ResponseEntity<ApiResponse<List<LoanReportDto>>> getLoanSummaryByStatus() {
        List<LoanReportDto> data = loanService.getLoanSummaryByStatus();
        return ResponseEntity.ok(ApiResponse.success("Loan summary by status retrieved successfully", data));
    }

    @GetMapping("/reports/loan-summary-by-status/date-range")
    @Operation(summary = "Summary total loan grouped by status with date range")
    public ResponseEntity<ApiResponse<List<LoanReportDto>>> getLoanSummaryByStatusAndDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        List<LoanReportDto> data = loanService.getLoanSummaryByStatusAndDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Loan summary by status and date range retrieved successfully", data));
    }

    @GetMapping("/reports/customer-outstanding")
    @Operation(summary = "Outstanding amount per customer (all customers)")
    public ResponseEntity<ApiResponse<List<CustomerOutstandingDto>>> getCustomerOutstandingReport() {
        List<CustomerOutstandingDto> data = loanService.getCustomerOutstandingReport();
        return ResponseEntity.ok(ApiResponse.success("Customer outstanding report retrieved successfully", data));
    }

    @GetMapping("/customers/{customerId}/outstanding")
    @Operation(summary = "Outstanding amount for specific customer")
    public ResponseEntity<ApiResponse<CustomerOutstandingDto>> getCustomerOutstandingById(
            @PathVariable Long customerId) {
        CustomerOutstandingDto data = loanService.getCustomerOutstandingById(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer outstanding retrieved successfully", data));
    }
}
