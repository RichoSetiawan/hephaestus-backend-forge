package com.example.training.exception;

import com.example.training.controller.LoanApplicationController;
import com.example.training.dto.*;
import com.example.training.enums.LoanStatus;
import com.example.training.service.LoanApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationControllerTest {

    @Mock
    private LoanApplicationService loanService;

    @InjectMocks
    private LoanApplicationController loanApplicationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoanApplicationResponse loanResponse;
    private LoanApplicationDetailResponse loanDetailResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(loanApplicationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        loanResponse = LoanApplicationResponse.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12)
                .purpose("Pembelian Motor")
                .status(LoanStatus.SUBMITTED)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(1L).fullName("Budi Santoso")
                .nik("1234567890123456").email("budi@test.com")
                .phoneNumber("08123456789").build();

        loanDetailResponse = LoanApplicationDetailResponse.builder()
                .id(1L)
                .loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12)
                .purpose("Pembelian Motor")
                .status(LoanStatus.SUBMITTED)
                .customer(customerResponse)
                .build();
    }

    @Test
    void create_shouldReturn201WithCreatedLoan() throws Exception {
        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest();
        request.setCustomerId(1L);
        request.setLoanAmount(new BigDecimal("12000000"));
        request.setTenorMonth(12);
        request.setPurpose("Pembelian Motor");

        when(loanService.create(any(CreateLoanApplicationRequest.class)))
                .thenReturn(loanResponse);

        mockMvc.perform(post("/api/v1/loan-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.message").value("Loan application created successfully"));

        verify(loanService).create(any(CreateLoanApplicationRequest.class));
    }

    @Test
    void getById_shouldReturn200WithLoanDetail() throws Exception {
        when(loanService.findById(1L)).thenReturn(loanDetailResponse);

        mockMvc.perform(get("/api/v1/loan-applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.customer.full_name").value("Budi Santoso"));

        verify(loanService).findById(1L);
    }

    @Test
    void getAll_shouldReturn200WithAllLoans_whenNoStatusParam() throws Exception {
        when(loanService.findAll()).thenReturn(List.of(loanResponse));

        mockMvc.perform(get("/api/v1/loan-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(loanService).findAll();
        verify(loanService, never()).findByStatus(any());
    }

    @Test
    void getAll_shouldReturn200FilteredByStatus_whenStatusParamProvided() throws Exception {
        when(loanService.findByStatus(LoanStatus.SUBMITTED)).thenReturn(List.of(loanResponse));

        mockMvc.perform(get("/api/v1/loan-applications")
                        .param("status", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("SUBMITTED"));

        verify(loanService).findByStatus(LoanStatus.SUBMITTED);
        verify(loanService, never()).findAll();
    }

    @Test
    void getByCustomerId_shouldReturn200WithCustomerLoans() throws Exception {
        when(loanService.findByCustomerId(1L)).thenReturn(List.of(loanResponse));

        mockMvc.perform(get("/api/v1/customers/1/loan-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].customer_id").value(1));

        verify(loanService).findByCustomerId(1L);
    }

    @Test
    void getAllLoans_shouldReturn200WithPagedLoans() throws Exception {
        Page<LoanApplicationResponse> page = new PageImpl<>(
                List.of(loanResponse), PageRequest.of(0, 10), 1);

        when(loanService.getAllLoanPagination(any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/pagination"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(loanService).getAllLoanPagination(any(), any(), any(), any());
    }

    @Test
    void updateStatus_shouldReturn200WithUpdatedLoan() throws Exception {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.APPROVED);

        LoanApplicationResponse approvedLoan = LoanApplicationResponse.builder()
                .id(1L).customerId(1L)
                .loanAmount(new BigDecimal("12000000")).tenorMonth(12)
                .purpose("Motor").status(LoanStatus.APPROVED).build();

        when(loanService.updateStatus(eq(1L), any(UpdateLoanStatusRequest.class)))
                .thenReturn(approvedLoan);

        mockMvc.perform(patch("/api/v1/loan-applications/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        verify(loanService).updateStatus(eq(1L), any(UpdateLoanStatusRequest.class));
    }

    @Test
    void getLoanSummaryByStatus_shouldReturn200WithReport() throws Exception {
        LoanReportDto report = LoanReportDto.builder()
                .status("SUBMITTED").totalLoans(5L)
                .totalAmount(new BigDecimal("50000000")).build();

        when(loanService.getLoanSummaryByStatus()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/v1/reports/loan-summary-by-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("SUBMITTED"));

        verify(loanService).getLoanSummaryByStatus();
    }

    @Test
    void getLoanSummaryByStatusAndDateRange_shouldReturn200() throws Exception {
        LoanReportDto report = LoanReportDto.builder()
                .status("APPROVED").totalLoans(3L)
                .totalAmount(new BigDecimal("30000000")).build();

        when(loanService.getLoanSummaryByStatusAndDateRange(any(), any()))
                .thenReturn(List.of(report));

        mockMvc.perform(get("/api/v1/reports/loan-summary-by-status/date-range"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("APPROVED"));

        verify(loanService).getLoanSummaryByStatusAndDateRange(any(), any());
    }

    @Test
    void getCustomerOutstandingReport_shouldReturn200WithReport() throws Exception {
        CustomerOutstandingDto outstanding = CustomerOutstandingDto.builder()
                .customerId(1L).fullName("Budi Santoso")
                .totalLoanAmount(new BigDecimal("12000000"))
                .totalPaid(new BigDecimal("6000000"))
                .outstandingAmount(new BigDecimal("6000000")).build();

        when(loanService.getCustomerOutstandingReport()).thenReturn(List.of(outstanding));

        mockMvc.perform(get("/api/v1/reports/customer-outstanding"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].customer_id").value(1));

        verify(loanService).getCustomerOutstandingReport();
    }

    @Test
    void getCustomerOutstandingById_shouldReturn200WithCustomerOutstanding() throws Exception {
        CustomerOutstandingDto outstanding = CustomerOutstandingDto.builder()
                .customerId(1L).fullName("Budi Santoso")
                .totalLoanAmount(new BigDecimal("12000000"))
                .totalPaid(new BigDecimal("6000000"))
                .outstandingAmount(new BigDecimal("6000000")).build();

        when(loanService.getCustomerOutstandingById(1L)).thenReturn(outstanding);

        mockMvc.perform(get("/api/v1/customers/1/outstanding"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.full_name").value("Budi Santoso"));

        verify(loanService).getCustomerOutstandingById(1L);
    }
}
