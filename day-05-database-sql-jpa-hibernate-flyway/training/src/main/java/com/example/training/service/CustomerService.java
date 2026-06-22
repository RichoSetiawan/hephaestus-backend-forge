package com.example.training.service;

import com.example.training.dto.*;
import com.example.training.entity.CustomerEntity;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.DuplicateCustomerException;
import com.example.training.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        return customerRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateStatus(Long id, DeleteCustomerDto request){
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(
                        "Customer with ID " + id + " not found"));
        customer.setDeleted(request.getIsDeleted());
        customerRepository.save(customer);
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        if (customerRepository.existsByNik(request.getNik())) {
            throw new DuplicateCustomerException("NIK already exists: " + request.getNik());
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Email already exists: " + request.getEmail());
        }

        CustomerEntity entity = CustomerEntity.builder()
                .fullName(request.getFullName())
                .nik(request.getNik())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        CustomerEntity saved = customerRepository.save(entity);
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
