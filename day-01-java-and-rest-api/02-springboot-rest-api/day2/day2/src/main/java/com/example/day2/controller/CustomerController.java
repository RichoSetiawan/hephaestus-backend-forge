package com.example.day2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

import com.example.day2.dto.CreateCustomerRequest;
import com.example.day2.dto.CustomerResponse;
import com.example.day2.dto.WebResponse;
import com.example.day2.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> data = customerService.getAllCustomer();
        
        WebResponse<List<CustomerResponse>> response = WebResponse.<List<CustomerResponse>>builder()
                .code("ERROR")
                .message("Successfully fetched all customers")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WebResponse<CustomerResponse>> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {        
        CustomerResponse data = customerService.createCustomer(createCustomerRequest);
        
        WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                .code("ERROR")
                .message("Successfully created new customer")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse data = customerService.getCustomerById(id);
        
        WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                .code("ERROR")
                .message("Successfully fetched customer with ID " + id)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @RequestBody CreateCustomerRequest request) {
        
        CustomerResponse data = customerService.updateCustomer(id, request);
        
        WebResponse<CustomerResponse> response = WebResponse.<CustomerResponse>builder()
                .code("ERROR")
                .message("Successfully updated customer with ID " + id)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<Void>> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        
        WebResponse<Void> response = WebResponse.<Void>builder()
                .code("ERROR") // Atau bisa pakai 204 NO_CONTENT, tapi kalau pakai body response, status 200 OK/200 Success lebih umum digunakan
                .message("Successfully deleted customer with ID " + id)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<WebResponse<List<CustomerResponse>>> getAllOrSearchCustomers(
            @RequestParam(value = "name", required = false) String keyword) {
        
        List<CustomerResponse> data = customerService.searchCustomers(keyword);
        
        WebResponse<List<CustomerResponse>> response = WebResponse.<List<CustomerResponse>>builder()
                .code("ERROR")
                .message("Successfully filtered customers")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }
}