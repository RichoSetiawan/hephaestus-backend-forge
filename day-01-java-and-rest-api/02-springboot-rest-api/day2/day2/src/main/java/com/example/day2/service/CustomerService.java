package com.example.day2.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong; // Tambahan untuk thread-safe

import com.example.day2.dto.CreateCustomerRequest;
import com.example.day2.dto.CustomerResponse;
import com.example.day2.model.Customer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

    private final Map<Long, Customer> customerMap = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    public List<CustomerResponse> getAllCustomer() {
        log.info("Fetching all customers");
        return customerMap.values()
                .stream()
                .map(customer -> {
                    CustomerResponse response = new CustomerResponse();
                    BeanUtils.copyProperties(customer, response); 
                    return response;
                })
                .toList();
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = buildCustomerValidated(request);
        
        customerMap.put(customer.getId(), customer);
        log.info("Successfully created customer with ID: {}", customer.getId());
        
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

    private Customer buildCustomerValidated(CreateCustomerRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Harus Diisi");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email Harus Diisi");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone Harus Diisi");
        }

        Customer customer = new Customer();
        
        if (request.getId() == null) {
            customer.setId(sequence.getAndIncrement());
        } else {
            customer.setId(request.getId());
        }

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber()); 

        return customer;
    }

    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        Customer data = customerMap.get(id);
        
        if (data == null) {
            throw new IllegalArgumentException("Customer dengan ID " + id + " tidak ditemukan");
        }
        
        return CustomerResponse.builder()
                .id(data.getId())
                .fullName(data.getFullName())
                .email(data.getEmail())
                .phoneNumber(data.getPhoneNumber())
                .build();
    }

    public void deleteCustomerById(Long id) {
        if (!customerMap.containsKey(id)) {
            throw new IllegalArgumentException("Customer dengan ID " + id + " tidak ditemukan");
        }
        log.info("Deleting customer with ID: {}", id);
        customerMap.remove(id);
    }

    public CustomerResponse updateCustomer(Long id, CreateCustomerRequest request) {
        if (!customerMap.containsKey(id)) {
            throw new IllegalArgumentException("Customer dengan ID " + id + " tidak ditemukan");
        }

        Customer updatedCustomer = buildCustomerValidated(request);
        updatedCustomer.setId(id); 
        customerMap.put(id, updatedCustomer);
        log.info("Successfully updated customer with ID: {}", id);

        return CustomerResponse.builder()
                .id(updatedCustomer.getId())
                .fullName(updatedCustomer.getFullName())
                .email(updatedCustomer.getEmail())
                .phoneNumber(updatedCustomer.getPhoneNumber())
                .build();
    }

    public List<CustomerResponse> searchCustomers(String keyword) {
        log.info("Searching customers with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomer();
        }

        String lowerCaseKeyword = keyword.toLowerCase();

        return customerMap.values()
                .stream()
                .filter(customer -> 
                    (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(lowerCaseKeyword)) ||
                    (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(lowerCaseKeyword)) ||
                    (customer.getPhoneNumber() != null && customer.getPhoneNumber().contains(lowerCaseKeyword))
                )
                .map(customer -> {
                    CustomerResponse response = new CustomerResponse();
                    BeanUtils.copyProperties(customer, response);
                    return response;
                })
                .toList();
    }
}