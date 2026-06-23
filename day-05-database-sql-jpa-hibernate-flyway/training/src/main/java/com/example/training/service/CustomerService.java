package com.example.training.service;

import com.example.training.dto.*;
import com.example.training.entity.CustomerEntity;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.DuplicateCustomerException;
import com.example.training.repository.CustomerRepository;
import com.example.training.utils.LoggingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        log.info("Fetching all customers");
        List<CustomerResponse> result = customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Retrieved {} customers", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        log.info("Fetching customer by id: {}", id);  // ← TAMBAH
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        log.info("Successfully get the customer by id={}", id);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchByName(String name) {
        log.info("Searching customers by name: {}", name);
        if (name == null || name.trim().isEmpty()) {
            log.debug("Empty keyword, returning all customers");
            return findAll();
        }
        List<CustomerResponse> result = customerRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Found {} customers matching name: {}", result.size(), name);
        return result;
    }

    @Transactional
    public CustomerResponse updateStatus(Long id, DeleteCustomerDto request) {
        log.info("Updating customer status: id={}, deleted={}", id, request.getDeleted());  // ← TAMBAH
        CustomerEntity customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
        
        customer.setDeleted(request.getDeleted());
        
        LoggingUtil.audit("CUSTOMER_STATUS_UPDATED", "UPDATE",
                "Customer id=" + id + ", deleted=" + request.getDeleted());
        log.info("Customer status updated: id={}, deleted={}", id, request.getDeleted());
        
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        log.info("Creating new customer: name={}", request.getFullName());
        if (customerRepository.existsByNik(request.getNik())) {
            log.warn("Duplicate NIK detected: nik=***MASKED***");
            throw new DuplicateCustomerException("NIK already exists: " + request.getNik());
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate email detected: email=***MASKED***");
            throw new DuplicateCustomerException("Email already exists: " + request.getEmail());
        }

        CustomerEntity entity = CustomerEntity.builder()
                .fullName(request.getFullName())
                .nik(request.getNik())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

                
        CustomerEntity saved = customerRepository.save(entity);
        LoggingUtil.audit("CUSTOMER_CREATED", "CREATE",
                "Customer id=" + saved.getId() + ", name=" + saved.getFullName());
        log.info("Customer created successfully: id={}", saved.getId());
        return toResponse(saved);
    }

    private CustomerResponse toResponse(CustomerEntity entity) {
        return CustomerResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .nik(entity.getNik())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
