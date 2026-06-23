package com.example.training.service;

import com.example.training.dto.*;
import com.example.training.entity.CustomerEntity;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.repository.CustomerRepository;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    private LoanApplicationEntity loan;
    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder()
                .id(1L)
                .fullName("Budi Santoso")
                .nik("1234567890123456")
                .email("budi@test.com")
                .phoneNumber("08123456789")
                .build();

        loan = LoanApplicationEntity.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12)
                .purpose("Pembelian Motor")
                .status(LoanStatus.SUBMITTED)
                .build();
    }

    // ─────────────── create ───────────────

    @Test
    void create_shouldCreateLoanSuccessfully() {
        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest();
        request.setCustomerId(1L);
        request.setLoanAmount(new BigDecimal("12000000"));
        request.setTenorMonth(12);
        request.setPurpose("Pembelian Motor");

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loan);

        LoanApplicationResponse result = loanApplicationService.create(request);

        assertNotNull(result);
        assertEquals(LoanStatus.SUBMITTED, result.getStatus());
        assertEquals(new BigDecimal("12000000"), result.getLoanAmount());
        assertEquals(1L, result.getCustomerId());
        verify(loanRepository).save(any());
    }

    @Test
    void create_shouldThrowCustomerNotFoundException_whenCustomerNotExists() {
        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest();
        request.setCustomerId(99L);

        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class,
                () -> loanApplicationService.create(request));

        verify(loanRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowCustomerNotFoundException_whenCustomerIsDeleted() {
        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest();
        request.setCustomerId(1L);

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        assertThrows(CustomerNotFoundException.class,
                () -> loanApplicationService.create(request));

        verify(loanRepository, never()).save(any());
    }

    // ─────────────── findById ───────────────

    @Test
    void findById_shouldReturnLoanDetailResponse() {
        loan.setCustomer(customer);
        when(loanRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(loan));

        LoanApplicationDetailResponse result = loanApplicationService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LoanStatus.SUBMITTED, result.getStatus());
        assertNotNull(result.getCustomer());
        assertEquals("Budi Santoso", result.getCustomer().getFullName());
        verify(loanRepository).findByIdWithCustomer(1L);
    }

    @Test
    void findById_shouldThrowLoanApplicationNotFoundException_whenNotFound() {
        when(loanRepository.findByIdWithCustomer(99L)).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class,
                () -> loanApplicationService.findById(99L));
    }

    // ─────────────── findAll ───────────────

    @Test
    void findAll_shouldReturnAllLoans() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<LoanApplicationResponse> result = loanApplicationService.findAll();

        assertThat(result).hasSize(1);
        assertEquals(1L, result.get(0).getId());
        verify(loanRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoLoans() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<LoanApplicationResponse> result = loanApplicationService.findAll();

        assertThat(result).isEmpty();
    }

    // ─────────────── findByStatus ───────────────

    @Test
    void findByStatus_shouldReturnLoansByStatus() {
        when(loanRepository.findByStatus(LoanStatus.SUBMITTED)).thenReturn(List.of(loan));

        List<LoanApplicationResponse> result = loanApplicationService.findByStatus(LoanStatus.SUBMITTED);

        assertThat(result).hasSize(1);
        assertEquals(LoanStatus.SUBMITTED, result.get(0).getStatus());
        verify(loanRepository).findByStatus(LoanStatus.SUBMITTED);
    }

    // ─────────────── findByCustomerId ───────────────

    @Test
    void findByCustomerId_shouldReturnLoansForCustomer() {
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of(loan));

        List<LoanApplicationResponse> result = loanApplicationService.findByCustomerId(1L);

        assertThat(result).hasSize(1);
        verify(loanRepository).findByCustomerId(1L);
    }

    @Test
    void findByCustomerId_shouldThrowException_whenCustomerDeleted() {
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        assertThrows(CustomerNotFoundException.class,
                () -> loanApplicationService.findByCustomerId(1L));

        verify(loanRepository, never()).findByCustomerId(any());
    }

    // ─────────────── getAllLoanPagination ───────────────

    @Test
    void getAllLoanPagination_shouldReturnPagedLoans() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoanApplicationEntity> page = new PageImpl<>(List.of(loan), pageable, 1);
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();

        when(loanRepository.findByStatusAndDateRangeWithPage(
                eq(LoanStatus.SUBMITTED), eq(start), eq(end), eq(pageable)))
                .thenReturn(page);

        Page<LoanApplicationResponse> result =
                loanApplicationService.getAllLoanPagination(LoanStatus.SUBMITTED, start, end, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertEquals(1L, result.getTotalElements());
    }

    @Test
    void getAllLoanPagination_shouldUseDefaultDates_whenDatesAreNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoanApplicationEntity> page = new PageImpl<>(List.of(loan), pageable, 1);

        when(loanRepository.findByStatusAndDateRangeWithPage(
                any(), any(), any(), eq(pageable)))
                .thenReturn(page);

        Page<LoanApplicationResponse> result =
                loanApplicationService.getAllLoanPagination(LoanStatus.SUBMITTED, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // ─────────────── updateStatus ───────────────

    @Test
    void updateStatus_shouldApproveSubmittedLoan() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.APPROVED);

        LoanApplicationEntity savedLoan = LoanApplicationEntity.builder()
                .id(1L).customerId(1L).loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12).purpose("Motor").status(LoanStatus.APPROVED).build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(savedLoan);

        LoanApplicationResponse result = loanApplicationService.updateStatus(1L, request);

        assertEquals(LoanStatus.APPROVED, result.getStatus());
        verify(loanRepository).save(any());
    }

    @Test
    void updateStatus_shouldRejectSubmittedLoan() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.REJECTED);

        LoanApplicationEntity savedLoan = LoanApplicationEntity.builder()
                .id(1L).customerId(1L).loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12).purpose("Motor").status(LoanStatus.REJECTED).build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(savedLoan);

        LoanApplicationResponse result = loanApplicationService.updateStatus(1L, request);

        assertEquals(LoanStatus.REJECTED, result.getStatus());
    }

    @Test
    void updateStatus_shouldDisburseApprovedLoan_andGenerateRepaymentSchedules() {
        loan.setStatus(LoanStatus.APPROVED);

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.DISBURSED);

        LoanApplicationEntity savedLoan = LoanApplicationEntity.builder()
                .id(1L).customerId(1L).loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12).purpose("Motor").status(LoanStatus.DISBURSED).build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Collections.emptyList());
        when(loanRepository.save(any())).thenReturn(savedLoan);

        LoanApplicationResponse result = loanApplicationService.updateStatus(1L, request);

        assertEquals(LoanStatus.DISBURSED, result.getStatus());
        verify(repaymentScheduleRepository, times(12)).save(any(RepaymentScheduleEntity.class));
    }

    @Test
    void updateStatus_shouldNotGenerateSchedules_whenSchedulesAlreadyExist() {
        loan.setStatus(LoanStatus.APPROVED);

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.DISBURSED);

        LoanApplicationEntity savedLoan = LoanApplicationEntity.builder()
                .id(1L).customerId(1L).loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12).purpose("Motor").status(LoanStatus.DISBURSED).build();

        RepaymentScheduleEntity existingSchedule = RepaymentScheduleEntity.builder()
                .id(1L).loanApplicationId(1L).build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(List.of(existingSchedule));
        when(loanRepository.save(any())).thenReturn(savedLoan);

        loanApplicationService.updateStatus(1L, request);

        verify(repaymentScheduleRepository, never()).save(any(RepaymentScheduleEntity.class));
    }

    @Test
    void updateStatus_shouldCloseDisbursedLoan_whenAllSchedulesPaid() {
        loan.setStatus(LoanStatus.DISBURSED);

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.CLOSED);

        LoanApplicationEntity savedLoan = LoanApplicationEntity.builder()
                .id(1L).customerId(1L).loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12).purpose("Motor").status(LoanStatus.CLOSED).build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(repaymentScheduleRepository.existsUnpaidByLoanApplicationId(1L)).thenReturn(false);
        when(loanRepository.save(any())).thenReturn(savedLoan);

        LoanApplicationResponse result = loanApplicationService.updateStatus(1L, request);

        assertEquals(LoanStatus.CLOSED, result.getStatus());
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenClosingWithUnpaidSchedules() {
        loan.setStatus(LoanStatus.DISBURSED);

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.CLOSED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(repaymentScheduleRepository.existsUnpaidByLoanApplicationId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_shouldThrowLoanNotFoundException_whenLoanNotFound() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class,
                () -> loanApplicationService.updateStatus(99L, request));
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenLoanIsTerminal_REJECTED() {
        loan.setStatus(LoanStatus.REJECTED);
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenLoanIsTerminal_CLOSED() {
        loan.setStatus(LoanStatus.CLOSED);
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.DISBURSED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenTransitionSubmittedToDisbursed() {
        // SUBMITTED -> DISBURSED is invalid (must go SUBMITTED -> APPROVED -> DISBURSED)
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.DISBURSED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenTransitionApprovedToRejected() {
        loan.setStatus(LoanStatus.APPROVED);
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.REJECTED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_shouldThrowIllegalState_whenTransitionDisbursedToApproved() {
        loan.setStatus(LoanStatus.DISBURSED);
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.updateStatus(1L, request));
    }

    // ─────────────── getLoanSummaryByStatus ───────────────

    @Test
    void getLoanSummaryByStatus_shouldReturnReportList() {
        LoanStatusSummaryProjection projection = mockProjection(
                "SUBMITTED", 5L, new BigDecimal("50000000"),
                new BigDecimal("10000000"), new BigDecimal("5000000"), new BigDecimal("20000000"));

        when(loanRepository.summarizeByStatus()).thenReturn(List.of(projection));

        List<LoanReportDto> result = loanApplicationService.getLoanSummaryByStatus();

        assertThat(result).hasSize(1);
        assertEquals("SUBMITTED", result.get(0).getStatus());
        assertEquals(5L, result.get(0).getTotalLoans());
        assertEquals(new BigDecimal("50000000"), result.get(0).getTotalAmount());
    }

    @Test
    void getLoanSummaryByStatus_shouldReturnEmptyList_whenNoData() {
        when(loanRepository.summarizeByStatus()).thenReturn(Collections.emptyList());

        List<LoanReportDto> result = loanApplicationService.getLoanSummaryByStatus();

        assertThat(result).isEmpty();
    }

    // ─────────────── getLoanSummaryByStatusAndDateRange ───────────────

    @Test
    void getLoanSummaryByStatusAndDateRange_shouldReturnReport_withExplicitDates() {
        ZonedDateTime start = ZonedDateTime.now().minusDays(30);
        ZonedDateTime end = ZonedDateTime.now();

        LoanStatusSummaryProjection projection = mockProjection(
                "APPROVED", 3L, new BigDecimal("30000000"),
                new BigDecimal("10000000"), new BigDecimal("5000000"), new BigDecimal("15000000"));

        when(loanRepository.summarizeByStatusAndDateRange(start, end))
                .thenReturn(List.of(projection));

        List<LoanReportDto> result =
                loanApplicationService.getLoanSummaryByStatusAndDateRange(start, end);

        assertThat(result).hasSize(1);
        assertEquals("APPROVED", result.get(0).getStatus());
    }

    @Test
    void getLoanSummaryByStatusAndDateRange_shouldUseDefaultDates_whenNull() {
        LoanStatusSummaryProjection projection = mockProjection(
                "SUBMITTED", 1L, new BigDecimal("10000000"),
                new BigDecimal("10000000"), new BigDecimal("10000000"), new BigDecimal("10000000"));

        when(loanRepository.summarizeByStatusAndDateRange(any(), any()))
                .thenReturn(List.of(projection));

        List<LoanReportDto> result =
                loanApplicationService.getLoanSummaryByStatusAndDateRange(null, null);

        assertThat(result).hasSize(1);
    }

    // ─────────────── getCustomerOutstandingReport ───────────────

    @Test
    void getCustomerOutstandingReport_shouldReturnOutstandingList() {
        CustomerOutstandingProjection projection = mockOutstandingProjection(
                1L, "Budi Santoso", "1234567890123456",
                new BigDecimal("12000000"), new BigDecimal("5000000"),
                new BigDecimal("7000000"), 2L, 1L);

        when(loanRepository.findCustomerOutstandingReport())
                .thenReturn(List.of(projection));

        List<CustomerOutstandingDto> result = loanApplicationService.getCustomerOutstandingReport();

        assertThat(result).hasSize(1);
        assertEquals(1L, result.get(0).getCustomerId());
        assertEquals("Budi Santoso", result.get(0).getFullName());
        assertNotNull(result.get(0).getPaymentPercentage());
    }

    @Test
    void getCustomerOutstandingReport_shouldHandleZeroLoanAmount() {
        CustomerOutstandingProjection projection = mockOutstandingProjection(
                1L, "Budi Santoso", "1234567890123456",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L);

        when(loanRepository.findCustomerOutstandingReport())
                .thenReturn(List.of(projection));

        List<CustomerOutstandingDto> result = loanApplicationService.getCustomerOutstandingReport();

        assertThat(result).hasSize(1);
        assertEquals(BigDecimal.ZERO, result.get(0).getPaymentPercentage());
    }

    // ─────────────── getCustomerOutstandingById ───────────────

    @Test
    void getCustomerOutstandingById_shouldReturnOutstanding() {
        CustomerOutstandingProjection projection = mockOutstandingProjection(
                1L, "Budi Santoso", "1234567890123456",
                new BigDecimal("12000000"), new BigDecimal("6000000"),
                new BigDecimal("6000000"), 1L, 1L);

        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findCustomerOutstandingById(1L))
                .thenReturn(Optional.of(projection));

        CustomerOutstandingDto result = loanApplicationService.getCustomerOutstandingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals(new BigDecimal("50.00"), result.getPaymentPercentage());
    }

    @Test
    void getCustomerOutstandingById_shouldThrowException_whenCustomerDeleted() {
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        assertThrows(CustomerNotFoundException.class,
                () -> loanApplicationService.getCustomerOutstandingById(1L));

        verify(loanRepository, never()).findCustomerOutstandingById(any());
    }

    @Test
    void getCustomerOutstandingById_shouldThrowException_whenCustomerHasNoLoans() {
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findCustomerOutstandingById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> loanApplicationService.getCustomerOutstandingById(1L));
    }

    // ─────────────── helpers ───────────────

    private LoanStatusSummaryProjection mockProjection(
            String status, Long totalLoans, BigDecimal totalAmount,
            BigDecimal avgAmount, BigDecimal minAmount, BigDecimal maxAmount) {

        return new LoanStatusSummaryProjection() {
            public String getStatus() { return status; }
            public Long getTotalLoans() { return totalLoans; }
            public BigDecimal getTotalAmount() { return totalAmount; }
            public BigDecimal getAverageAmount() { return avgAmount; }
            public BigDecimal getMinAmount() { return minAmount; }
            public BigDecimal getMaxAmount() { return maxAmount; }
        };
    }

    private CustomerOutstandingProjection mockOutstandingProjection(
            Long customerId, String fullName, String nik,
            BigDecimal totalLoan, BigDecimal totalPaid,
            BigDecimal outstanding, Long totalLoans, Long activeLoans) {

        return new CustomerOutstandingProjection() {
            public Long getCustomerId() { return customerId; }
            public String getFullName() { return fullName; }
            public String getNik() { return nik; }
            public BigDecimal getTotalLoanAmount() { return totalLoan; }
            public BigDecimal getTotalPaid() { return totalPaid; }
            public BigDecimal getOutstandingAmount() { return outstanding; }
            public Long getTotalLoans() { return totalLoans; }
            public Long getActiveLoans() { return activeLoans; }
        };
    }
}
