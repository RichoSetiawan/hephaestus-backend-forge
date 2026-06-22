package com.example.training.controller;

import com.example.training.dto.*;
import com.example.training.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll() {
        List<CustomerResponse> data = customerService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable Long id) {
        CustomerResponse data = customerService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> search(@RequestParam(value = "name", required = false) String name) {
        List<CustomerResponse> data = customerService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success("Customers filtered successfully", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse data = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", data));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody DeleteCustomerDto request) {
        CustomerResponse data = customerService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Loan status updated successfully", data));
    }
}
