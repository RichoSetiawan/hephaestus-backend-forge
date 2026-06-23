package com.example.training.exception;

import com.example.training.controller.CustomerController;
import com.example.training.dto.CreateCustomerRequest;
import com.example.training.dto.CustomerResponse;
import com.example.training.dto.DeleteCustomerDto;
import com.example.training.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        customerResponse = CustomerResponse.builder()
                .id(1L)
                .fullName("Budi Santoso")
                .nik("1234567890123456")
                .email("budi@test.com")
                .phoneNumber("08123456789")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    @Test
    void getAll_shouldReturn200WithCustomerList() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(customerResponse));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].full_name").value("Budi Santoso"))
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(customerService).findAll();
    }

    @Test
    void getById_shouldReturn200WithCustomer() throws Exception {
        when(customerService.findById(1L)).thenReturn(customerResponse);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.full_name").value("Budi Santoso"));

        verify(customerService).findById(1L);
    }

    @Test
    void search_shouldReturn200WithFilteredCustomers() throws Exception {
        when(customerService.searchByName("Budi")).thenReturn(List.of(customerResponse));

        mockMvc.perform(get("/api/v1/customers/search")
                        .param("name", "Budi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].full_name").value("Budi Santoso"));

        verify(customerService).searchByName("Budi");
    }

    @Test
    void search_shouldReturn200_whenNoNameParam() throws Exception {
        when(customerService.searchByName(null)).thenReturn(List.of(customerResponse));

        mockMvc.perform(get("/api/v1/customers/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(customerService).searchByName(null);
    }

    @Test
    void create_shouldReturn201WithCreatedCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Budi Santoso", "1234567890123456", "budi@test.com", "08123456789");

        when(customerService.create(any(CreateCustomerRequest.class)))
                .thenReturn(customerResponse);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.full_name").value("Budi Santoso"))
                .andExpect(jsonPath("$.message").value("Customer created successfully"));

        verify(customerService).create(any(CreateCustomerRequest.class));
    }

    @Test
    void updateStatus_shouldReturn200WithUpdatedCustomer() throws Exception {
        DeleteCustomerDto dto = new DeleteCustomerDto(true);

        CustomerResponse updated = CustomerResponse.builder()
                .id(1L).fullName("Budi Santoso")
                .nik("1234567890123456").email("budi@test.com")
                .phoneNumber("08123456789").build();

        when(customerService.updateStatus(eq(1L), any(DeleteCustomerDto.class)))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/v1/customers/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(customerService).updateStatus(eq(1L), any(DeleteCustomerDto.class));
    }
}
