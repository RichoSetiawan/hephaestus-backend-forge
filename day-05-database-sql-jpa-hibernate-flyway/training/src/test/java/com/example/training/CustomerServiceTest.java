package com.example.training;

import com.example.training.dto.CreateCustomerRequest;
import com.example.training.dto.CustomerResponse;
import com.example.training.dto.DeleteCustomerDto;
import com.example.training.entity.CustomerEntity;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.DuplicateCustomerException;
import com.example.training.repository.CustomerRepository;
import com.example.training.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity customerEntity;
    private CreateCustomerRequest createRequest;
    private DeleteCustomerDto deleteDto;

    @BeforeEach
    void setUp() {
        customerEntity = CustomerEntity.builder()
                .id(1L)
                .fullName("John Doe")
                .nik("1234567890123456")
                .email("john@example.com")
                .phoneNumber("081234567890")
                .deleted(false)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        createRequest = new CreateCustomerRequest();
        createRequest.setFullName("John Doe");
        createRequest.setNik("1234567890123456");
        createRequest.setEmail("john@example.com");
        createRequest.setPhoneNumber("081234567890");

        deleteDto = new DeleteCustomerDto();
        deleteDto.setDeleted(true);
    }

    @Test
    void findAll_ShouldReturnAllCustomers() {
        // given
        CustomerEntity customer2 = CustomerEntity.builder()
                .id(2L)
                .fullName("Jane Doe")
                .nik("6543210987654321")
                .email("jane@example.com")
                .phoneNumber("089876543210")
                .build();
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customerEntity, customer2));

        // when
        List<CustomerResponse> result = customerService.findAll();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        assertEquals("Jane Doe", result.get(1).getFullName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenCustomerExists_ShouldReturnCustomer() {
        // given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));

        // when
        CustomerResponse result = customerService.findById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getFullName());
        assertEquals("1234567890123456", result.getNik());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenCustomerNotExists_ShouldThrowException() {
        // given
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> customerService.findById(99L));
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    void searchByName_WithValidName_ShouldReturnMatchingCustomers() {
        // given
        when(customerRepository.findByFullNameContainingIgnoreCase("John"))
                .thenReturn(Arrays.asList(customerEntity));

        // when
        List<CustomerResponse> result = customerService.searchByName("John");

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        verify(customerRepository, times(1)).findByFullNameContainingIgnoreCase("John");
    }

    @Test
    void searchByName_WithEmptyName_ShouldReturnAllCustomers() {
        // given
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customerEntity));

        // when
        List<CustomerResponse> result = customerService.searchByName("");

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never()).findByFullNameContainingIgnoreCase(any());
    }

    @Test
    void searchByName_WithNullName_ShouldReturnAllCustomers() {
        // given
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customerEntity));

        // when
        List<CustomerResponse> result = customerService.searchByName(null);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void create_WithValidRequest_ShouldReturnCreatedCustomer() {
        // given
        when(customerRepository.existsByNik("1234567890123456")).thenReturn(false);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        // when
        CustomerResponse result = customerService.create(createRequest);

        // then
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository, times(1)).existsByNik("1234567890123456");
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void create_WithDuplicateNik_ShouldThrowException() {
        // given
        when(customerRepository.existsByNik("1234567890123456")).thenReturn(true);

        // when & then
        assertThrows(DuplicateCustomerException.class, () -> customerService.create(createRequest));
        verify(customerRepository, times(1)).existsByNik("1234567890123456");
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void create_WithDuplicateEmail_ShouldThrowException() {
        // given
        when(customerRepository.existsByNik("1234567890123456")).thenReturn(false);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // when & then
        assertThrows(DuplicateCustomerException.class, () -> customerService.create(createRequest));
        verify(customerRepository, times(1)).existsByNik("1234567890123456");
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void updateStatus_WhenCustomerExists_ShouldUpdateStatus() {
        // given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));

        // when
        CustomerResponse result = customerService.updateStatus(1L, deleteDto);

        // then
        assertNotNull(result);
        assertTrue(customerEntity.isDeleted());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void updateStatus_WhenCustomerNotExists_ShouldThrowException() {
        // given
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateStatus(99L, deleteDto));
        verify(customerRepository, times(1)).findById(99L);
    }
}