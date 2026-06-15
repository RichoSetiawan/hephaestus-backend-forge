package com.example.day2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.day2.dto.CreateCustomerRequest;
import com.example.day2.dto.CustomerResponse;
import com.example.day2.dto.WebResponse;
import com.example.day2.service.CustomerServiceV2;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/customers")
public class CustomerControllerV2 {

    private final CustomerServiceV2 customerService;

    public CustomerControllerV2(CustomerServiceV2 customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> data = customerService.getAllCustomer();
        return ResponseEntity.ok(createWebResponse(HttpStatus.OK, "Successfully fetched all customers", data));
    }

    @PostMapping
    public ResponseEntity<WebResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {        
        CustomerResponse data = customerService.createCustomer(request);
        return new ResponseEntity<>(createWebResponse(HttpStatus.CREATED, "Successfully created new customer", data), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse data = customerService.getCustomerById(id);
        return ResponseEntity.ok(createWebResponse(HttpStatus.OK, "Successfully fetched customer", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse data = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(createWebResponse(HttpStatus.OK, "Successfully updated customer", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<Void>> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.ok(createWebResponse(HttpStatus.OK, "Successfully deleted customer", null));
    }

    @GetMapping("/search")
    public ResponseEntity<WebResponse<List<CustomerResponse>>> getAllOrSearchCustomers(@RequestParam(value = "name", required = false) String keyword) {
        List<CustomerResponse> data = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(createWebResponse(HttpStatus.OK, "Successfully filtered customers", data));
    }

    private <T> WebResponse<T> createWebResponse(HttpStatus status, String message, T data) {
        return WebResponse.<T>builder()
                .code(String.valueOf(status.value()))
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}