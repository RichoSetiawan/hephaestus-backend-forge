package com.example.training.service;

import com.example.training.dto.CreateCustomerRequest;
import com.example.training.dto.CustomerResponse;
import com.example.training.dto.DeleteCustomerDto;
import com.example.training.entity.CustomerEntity;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.DuplicateCustomerException;
import com.example.training.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder()
                .id(1L)
                .fullName("Budi Santoso")
                .nik("1234567890123456")
                .email("budi@test.com")
                .phoneNumber("08123456789")
                .deleted(false)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    // ─────────────── findAll ───────────────

    @Test
    void findAll_shouldReturnListOfCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Budi Santoso");
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerResponse> result = customerService.findAll();

        assertThat(result).isEmpty();
        verify(customerRepository).findAll();
    }

    // ─────────────── findById ───────────────

    @Test
    void findById_shouldReturnCustomerResponse() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Budi Santoso", result.getFullName());
        assertEquals("1234567890123456", result.getNik());
        assertEquals("budi@test.com", result.getEmail());
        assertEquals("08123456789", result.getPhoneNumber());
        verify(customerRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowCustomerNotFoundException_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.findById(99L));

        verify(customerRepository).findById(99L);
    }

    // ─────────────── searchByName ───────────────

    @Test
    void searchByName_shouldReturnMatchingCustomers() {
        when(customerRepository.findByFullNameContainingIgnoreCase("Budi"))
                .thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.searchByName("Budi");

        assertThat(result).hasSize(1);
        assertEquals("Budi Santoso", result.get(0).getFullName());
        verify(customerRepository).findByFullNameContainingIgnoreCase("Budi");
    }

    @Test
    void searchByName_whenNameIsNull_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.searchByName(null);

        assertThat(result).hasSize(1);
        verify(customerRepository).findAll();
        verify(customerRepository, never()).findByFullNameContainingIgnoreCase(any());
    }

    @Test
    void searchByName_whenNameIsEmpty_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.searchByName("");

        assertThat(result).hasSize(1);
        verify(customerRepository).findAll();
    }

    @Test
    void searchByName_whenNameIsBlank_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerService.searchByName("   ");

        assertThat(result).hasSize(1);
        verify(customerRepository).findAll();
    }

    @Test
    void searchByName_shouldReturnEmptyWhenNoMatch() {
        when(customerRepository.findByFullNameContainingIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

        List<CustomerResponse> result = customerService.searchByName("xyz");

        assertThat(result).isEmpty();
    }

    // ─────────────── create ───────────────

    @Test
    void create_shouldCreateAndReturnCustomerResponse() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Budi Santoso", "1234567890123456", "budi@test.com", "08123456789");

        when(customerRepository.existsByNik(request.getNik())).thenReturn(false);
        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        CustomerResponse result = customerService.create(request);

        assertNotNull(result);
        assertEquals("Budi Santoso", result.getFullName());
        assertEquals(1L, result.getId());
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void create_shouldThrowDuplicateCustomerException_whenNikExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Budi Santoso", "1234567890123456", "budi@test.com", "08123456789");

        when(customerRepository.existsByNik("1234567890123456")).thenReturn(true);

        assertThrows(DuplicateCustomerException.class,
                () -> customerService.create(request));

        verify(customerRepository, never()).save(any());
        verify(customerRepository, never()).existsByEmail(any());
    }

    @Test
    void create_shouldThrowDuplicateCustomerException_whenEmailExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Budi Santoso", "1234567890123456", "budi@test.com", "08123456789");

        when(customerRepository.existsByNik("1234567890123456")).thenReturn(false);
        when(customerRepository.existsByEmail("budi@test.com")).thenReturn(true);

        assertThrows(DuplicateCustomerException.class,
                () -> customerService.create(request));

        verify(customerRepository, never()).save(any());
    }

    // ─────────────── updateStatus ───────────────

    @Test
    void updateStatus_shouldSetDeletedTrue() {
        DeleteCustomerDto dto = new DeleteCustomerDto(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse result = customerService.updateStatus(1L, dto);

        assertNotNull(result);
        assertTrue(customer.isDeleted());
        verify(customerRepository).findById(1L);
    }

    @Test
    void updateStatus_shouldSetDeletedFalse() {
        customer.setDeleted(true);
        DeleteCustomerDto dto = new DeleteCustomerDto(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse result = customerService.updateStatus(1L, dto);

        assertNotNull(result);
        assertFalse(customer.isDeleted());
    }

    @Test
    void updateStatus_shouldThrowCustomerNotFoundException_whenNotFound() {
        DeleteCustomerDto dto = new DeleteCustomerDto(true);

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateStatus(99L, dto));

        verify(customerRepository).findById(99L);
    }
}
