package com.example.day2.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.day2.dto.CreateCustomerRequest;
import com.example.day2.dto.CustomerResponse;
import com.example.day2.model.CustomerEntity;
import com.example.day2.repository.CustomerRepository;
import com.example.day2.utils.CustomerNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerServiceV2 {

    private final CustomerRepository customerRepository;

    public CustomerServiceV2(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponse> getAllCustomer() {
        log.info("V2: Fetching all customers from H2 database");
        return customerRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("V2: Creating new customer into H2 database");
        

        CustomerEntity entity = new CustomerEntity();
        BeanUtils.copyProperties(request, entity);
        
        entity.setId(null);

        CustomerEntity savedEntity = customerRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    public CustomerResponse getCustomerById(Long id) {
        log.info("V2: Fetching customer with ID: {}", id);
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer dengan ID " + id + " tidak ditemukan"));
        return convertToResponse(entity);
    }

    public CustomerResponse updateCustomer(Long id, CreateCustomerRequest request) {
        log.info("V2: Updating customer with ID: {}", id);
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer dengan ID " + id + " tidak ditemukan"));

        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());

        CustomerEntity updatedEntity = customerRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    public void deleteCustomerById(Long id) {
        log.info("V2: Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer dengan ID " + id + " tidak ditemukan");
        }
        customerRepository.deleteById(id);
    }

    public List<CustomerResponse> searchCustomers(String keyword) {
        log.info("V2: Searching customers with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomer();
        }
        return customerRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContaining(
                keyword, keyword, keyword).stream()
                .map(this::convertToResponse)
                .toList();
    }

    private CustomerResponse convertToResponse(CustomerEntity entity) {
        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
