package com.example.training.exception;

import com.example.training.controller.PaymentTransactionController;
import com.example.training.dto.CreatePaymentTransactionRequest;
import com.example.training.dto.PaymentTransactionResponse;
import com.example.training.enums.PaymentStatus;
import com.example.training.service.PaymentTransactionService;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionControllerTest {

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @InjectMocks
    private PaymentTransactionController paymentTransactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PaymentTransactionResponse paymentResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentTransactionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        paymentResponse = PaymentTransactionResponse.builder()
                .id(1L)
                .repaymentScheduleId(1L)
                .paymentReference("PAY-001")
                .paidAmount(new BigDecimal("1120000"))
                .paidAt(ZonedDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .build();
    }

    @Test
    void create_shouldReturn201WithCreatedTransaction() throws Exception {
        CreatePaymentTransactionRequest request = new CreatePaymentTransactionRequest();
        request.setRepaymentScheduleId(1L);
        request.setPaymentReference("PAY-001");
        request.setPaidAmount(new BigDecimal("1120000"));
        request.setPaidAt(ZonedDateTime.now());

        when(paymentTransactionService.create(any(CreatePaymentTransactionRequest.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(post("/api/v1/payment-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.payment_reference").value("PAY-001"))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Payment transaction created successfully"));

        verify(paymentTransactionService).create(any(CreatePaymentTransactionRequest.class));
    }

    @Test
    void getByRepaymentScheduleId_shouldReturn200WithTransactionList() throws Exception {
        when(paymentTransactionService.findByRepaymentScheduleId(1L))
                .thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/api/v1/repayment-schedules/1/payment-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].payment_reference").value("PAY-001"))
                .andExpect(jsonPath("$.data[0].repayment_schedule_id").value(1));

        verify(paymentTransactionService).findByRepaymentScheduleId(1L);
    }
}
