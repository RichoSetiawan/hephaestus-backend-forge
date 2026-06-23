package com.example.training.exception;

import com.example.training.controller.RepaymentScheduleController;
import com.example.training.dto.ApiResponse;
import com.example.training.dto.RepaymentScheduleResponse;
import com.example.training.enums.RepaymentStatus;
import com.example.training.service.RepaymentScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RepaymentScheduleControllerTest {

    @Mock
    private RepaymentScheduleService repaymentScheduleService;

    @InjectMocks
    private RepaymentScheduleController repaymentScheduleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RepaymentScheduleResponse scheduleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(repaymentScheduleController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        scheduleResponse = RepaymentScheduleResponse.builder()
                .id(1L)
                .loanApplicationId(1L)
                .installmentNumber(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .principalAmount(new BigDecimal("1000000"))
                .interestAmount(new BigDecimal("120000"))
                .totalAmount(new BigDecimal("1120000"))
                .status(RepaymentStatus.UNPAID)
                .createdAt(ZonedDateTime.now())
                .build();
    }

    @Test
    void getByLoanApplicationId_shouldReturn200WithScheduleList() throws Exception {
        when(repaymentScheduleService.findByLoanApplicationId(1L))
                .thenReturn(List.of(scheduleResponse));

        mockMvc.perform(get("/api/v1/loan-applications/1/repayment-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].installment_number").value(1))
                .andExpect(jsonPath("$.data[0].status").value("UNPAID"));

        verify(repaymentScheduleService).findByLoanApplicationId(1L);
    }

    @Test
    void getById_shouldReturn200WithSchedule() throws Exception {
        when(repaymentScheduleService.findById(1L)).thenReturn(scheduleResponse);

        mockMvc.perform(get("/api/v1/repayment-schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.loan_application_id").value(1));

        verify(repaymentScheduleService).findById(1L);
    }

    @Test
    void getByStatus_shouldReturn200WithScheduleListByStatus() throws Exception {
        when(repaymentScheduleService.findByStatus(RepaymentStatus.UNPAID))
                .thenReturn(List.of(scheduleResponse));

        mockMvc.perform(get("/api/v1/repayment/status")
                        .param("param", "UNPAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("UNPAID"));

        verify(repaymentScheduleService).findByStatus(RepaymentStatus.UNPAID);
    }
}
