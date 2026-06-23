package com.example.training;

import com.example.training.dto.*;
import com.example.training.entity.CustomerEntity;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.repository.CustomerRepository;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;
import com.example.training.service.LoanApplicationService;
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
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private LoanApplicationService loanService;

    private LoanApplicationEntity loanEntity;
    private CustomerEntity customerEntity;
    private CreateLoanApplicationRequest createRequest;
    private UpdateLoanStatusRequest updateRequest;

    @BeforeEach
    void setUp() {
        customerEntity = CustomerEntity.builder()
                .id(1L)
                .fullName("John Doe")
                .nik("1234567890123456")
                .email("john@example.com")
                .build();

        loanEntity = LoanApplicationEntity.builder()
                .id(1L)
                .customerId(1L)
                .customer(customerEntity)
                .loanAmount(new BigDecimal("10000000"))
                .tenorMonth(12)
                .purpose("Business")
                .status(LoanStatus.SUBMITTED)
                .build();

        createRequest = new CreateLoanApplicationRequest();
        createRequest.setCustomerId(1L);
        createRequest.setLoanAmount(new BigDecimal("10000000"));
        createRequest.setTenorMonth(12);
        createRequest.setPurpose("Business");

        updateRequest = new UpdateLoanStatusRequest();
    }

    @Test
    void create_WhenCustomerExistsAndNotDeleted_ShouldReturnCreatedLoan() {
        // given
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.create(createRequest);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("10000000"), result.getLoanAmount());
        assertEquals(LoanStatus.SUBMITTED, result.getStatus());
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, times(1)).save(any(LoanApplicationEntity.class));
    }

    @Test
    void create_WhenCustomerNotExists_ShouldThrowException() {
        // given
        when(customerRepository.existsById(1L)).thenReturn(false);

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> loanService.create(createRequest));
        verify(customerRepository, times(1)).existsById(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void create_WhenCustomerIsDeleted_ShouldThrowException() {
        // given
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> loanService.create(createRequest));
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void findById_WhenLoanExists_ShouldReturnLoanDetail() {
        // given
        when(loanRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(loanEntity));

        // when
        LoanApplicationDetailResponse result = loanService.findById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Business", result.getPurpose());
        assertNotNull(result.getCustomer());
        assertEquals("John Doe", result.getCustomer().getFullName());
        verify(loanRepository, times(1)).findByIdWithCustomer(1L);
    }

    @Test
    void findById_WhenLoanNotExists_ShouldThrowException() {
        // given
        when(loanRepository.findByIdWithCustomer(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(LoanApplicationNotFoundException.class, () -> loanService.findById(99L));
        verify(loanRepository, times(1)).findByIdWithCustomer(99L);
    }

    @Test
    void findAll_ShouldReturnAllLoans() {
        // given
        LoanApplicationEntity loan2 = LoanApplicationEntity.builder()
                .id(2L)
                .customerId(2L)
                .loanAmount(new BigDecimal("5000000"))
                .tenorMonth(6)
                .purpose("Education")
                .status(LoanStatus.APPROVED)
                .build();
        when(loanRepository.findAll()).thenReturn(Arrays.asList(loanEntity, loan2));

        // when
        List<LoanApplicationResponse> result = loanService.findAll();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LoanStatus.SUBMITTED, result.get(0).getStatus());
        assertEquals(LoanStatus.APPROVED, result.get(1).getStatus());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void findByStatus_ShouldReturnLoansWithStatus() {
        // given
        when(loanRepository.findByStatus(LoanStatus.SUBMITTED))
                .thenReturn(Collections.singletonList(loanEntity));

        // when
        List<LoanApplicationResponse> result = loanService.findByStatus(LoanStatus.SUBMITTED);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LoanStatus.SUBMITTED, result.get(0).getStatus());
        verify(loanRepository, times(1)).findByStatus(LoanStatus.SUBMITTED);
    }

    @Test
    void findByCustomerId_WhenCustomerNotDeleted_ShouldReturnLoans() {
        // given
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(loanEntity));

        // when
        List<LoanApplicationResponse> result = loanService.findByCustomerId(1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void findByCustomerId_WhenCustomerIsDeleted_ShouldThrowException() {
        // given
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> loanService.findByCustomerId(1L));
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, never()).findByCustomerId(any());
    }

    @Test
    void getAllLoanPagination_WithNullDates_ShouldUseDefaultDates() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoanApplicationEntity> page = new PageImpl<>(Collections.singletonList(loanEntity));
        when(loanRepository.findByStatusAndDateRangeWithPage(
                eq(LoanStatus.SUBMITTED), any(ZonedDateTime.class), any(ZonedDateTime.class), eq(pageable)))
                .thenReturn(page);

        // when
        Page<LoanApplicationResponse> result = loanService.getAllLoanPagination(
                LoanStatus.SUBMITTED, null, null, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(loanRepository, times(1)).findByStatusAndDateRangeWithPage(
                eq(LoanStatus.SUBMITTED), any(ZonedDateTime.class), any(ZonedDateTime.class), eq(pageable));
    }

    @Test
    void updateStatus_SubmittedToApproved_ShouldUpdateStatus() {
        // given
        loanEntity.setStatus(LoanStatus.SUBMITTED);
        updateRequest.setStatus(LoanStatus.APPROVED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.updateStatus(1L, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED, result.getStatus());
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).save(any(LoanApplicationEntity.class));
    }

    @Test
    void updateStatus_SubmittedToRejected_ShouldUpdateStatus() {
        // given
        loanEntity.setStatus(LoanStatus.SUBMITTED);
        updateRequest.setStatus(LoanStatus.REJECTED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.updateStatus(1L, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.REJECTED, result.getStatus());
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).save(any(LoanApplicationEntity.class));
    }

    @Test
    void updateStatus_ApprovedToDisbursed_ShouldGenerateRepaymentSchedules() {
        // given
        loanEntity.setStatus(LoanStatus.APPROVED);
        loanEntity.setTenorMonth(3);
        loanEntity.setLoanAmount(new BigDecimal("3000000"));
        updateRequest.setStatus(LoanStatus.DISBURSED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(repaymentScheduleRepository.findByLoanApplicationId(1L)).thenReturn(Collections.emptyList());
        when(repaymentScheduleRepository.save(any(RepaymentScheduleEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.updateStatus(1L, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.DISBURSED, result.getStatus());
        verify(repaymentScheduleRepository, times(3)).save(any(RepaymentScheduleEntity.class));
        verify(loanRepository, times(1)).save(any(LoanApplicationEntity.class));
    }

    @Test
    void updateStatus_ApprovedToDisbursed_WhenSchedulesExist_ShouldNotGenerateAgain() {
        // given
        loanEntity.setStatus(LoanStatus.APPROVED);
        updateRequest.setStatus(LoanStatus.DISBURSED);
        RepaymentScheduleEntity existingSchedule = RepaymentScheduleEntity.builder().id(1L).build();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Collections.singletonList(existingSchedule));
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.updateStatus(1L, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.DISBURSED, result.getStatus());
        verify(repaymentScheduleRepository, never()).save(any(RepaymentScheduleEntity.class));
    }

    @Test
    void updateStatus_DisbursedToClosed_WhenNoUnpaidSchedules_ShouldUpdateStatus() {
        // given
        loanEntity.setStatus(LoanStatus.DISBURSED);
        updateRequest.setStatus(LoanStatus.CLOSED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(repaymentScheduleRepository.existsUnpaidByLoanApplicationId(1L)).thenReturn(false);
        when(loanRepository.save(any(LoanApplicationEntity.class))).thenReturn(loanEntity);

        // when
        LoanApplicationResponse result = loanService.updateStatus(1L, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.CLOSED, result.getStatus());
        verify(repaymentScheduleRepository, times(1)).existsUnpaidByLoanApplicationId(1L);
    }

    @Test
    void updateStatus_DisbursedToClosed_WhenHasUnpaidSchedules_ShouldThrowException() {
        // given
        loanEntity.setStatus(LoanStatus.DISBURSED);
        updateRequest.setStatus(LoanStatus.CLOSED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(repaymentScheduleRepository.existsUnpaidByLoanApplicationId(1L)).thenReturn(true);

        // when & then
        assertThrows(IllegalStateException.class, () -> loanService.updateStatus(1L, updateRequest));
        verify(repaymentScheduleRepository, times(1)).existsUnpaidByLoanApplicationId(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void updateStatus_WhenTerminalStatus_ShouldThrowException() {
        // given
        loanEntity.setStatus(LoanStatus.REJECTED);
        updateRequest.setStatus(LoanStatus.APPROVED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));

        // when & then
        assertThrows(IllegalStateException.class, () -> loanService.updateStatus(1L, updateRequest));
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void updateStatus_InvalidTransition_SubmittedToDisbursed_ShouldThrowException() {
        // given
        loanEntity.setStatus(LoanStatus.SUBMITTED);
        updateRequest.setStatus(LoanStatus.DISBURSED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));

        // when & then
        assertThrows(IllegalStateException.class, () -> loanService.updateStatus(1L, updateRequest));
    }

    @Test
    void updateStatus_InvalidTransition_ApprovedToClosed_ShouldThrowException() {
        // given
        loanEntity.setStatus(LoanStatus.APPROVED);
        updateRequest.setStatus(LoanStatus.CLOSED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));

        // when & then
        assertThrows(IllegalStateException.class, () -> loanService.updateStatus(1L, updateRequest));
    }

    @Test
    void updateStatus_WhenLoanNotFound_ShouldThrowException() {
        // given
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(LoanApplicationNotFoundException.class, () -> loanService.updateStatus(99L, updateRequest));
    }

    @Test
    void getLoanSummaryByStatus_ShouldReturnSummary() {
        // given
        LoanStatusSummaryProjection projection = mock(LoanStatusSummaryProjection.class);
        when(projection.getStatus()).thenReturn("SUBMITTED");
        when(projection.getTotalLoans()).thenReturn(5L);
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("50000000"));
        when(projection.getAverageAmount()).thenReturn(new BigDecimal("10000000"));
        when(projection.getMinAmount()).thenReturn(new BigDecimal("5000000"));
        when(projection.getMaxAmount()).thenReturn(new BigDecimal("15000000"));
        when(loanRepository.summarizeByStatus()).thenReturn(Collections.singletonList(projection));

        // when
        List<LoanReportDto> result = loanService.getLoanSummaryByStatus();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUBMITTED", result.get(0).getStatus());
        assertEquals(5L, result.get(0).getTotalLoans());
        verify(loanRepository, times(1)).summarizeByStatus();
    }

    @Test
    void getLoanSummaryByStatusAndDateRange_WithNullDates_ShouldUseDefaultDates() {
        // given
        LoanStatusSummaryProjection projection = mock(LoanStatusSummaryProjection.class);
        when(projection.getStatus()).thenReturn("APPROVED");
        when(projection.getTotalLoans()).thenReturn(3L);
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("30000000"));
        when(projection.getAverageAmount()).thenReturn(new BigDecimal("10000000"));
        when(projection.getMinAmount()).thenReturn(new BigDecimal("5000000"));
        when(projection.getMaxAmount()).thenReturn(new BigDecimal("15000000"));
        when(loanRepository.summarizeByStatusAndDateRange(any(ZonedDateTime.class), any(ZonedDateTime.class)))
                .thenReturn(Collections.singletonList(projection));

        // when
        List<LoanReportDto> result = loanService.getLoanSummaryByStatusAndDateRange(null, null);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("APPROVED", result.get(0).getStatus());
        verify(loanRepository, times(1)).summarizeByStatusAndDateRange(any(ZonedDateTime.class), any(ZonedDateTime.class));
    }

    @Test
    void getCustomerOutstandingReport_ShouldReturnReport() {
        // given
        CustomerOutstandingProjection projection = mock(CustomerOutstandingProjection.class);
        when(projection.getCustomerId()).thenReturn(1L);
        when(projection.getFullName()).thenReturn("John Doe");
        when(projection.getNik()).thenReturn("1234567890123456");
        when(projection.getTotalLoanAmount()).thenReturn(new BigDecimal("10000000"));
        when(projection.getTotalPaid()).thenReturn(new BigDecimal("3000000"));
        when(projection.getOutstandingAmount()).thenReturn(new BigDecimal("7000000"));
        when(projection.getTotalLoans()).thenReturn(2L);
        when(projection.getActiveLoans()).thenReturn(1L);
        when(loanRepository.findCustomerOutstandingReport()).thenReturn(Collections.singletonList(projection));

        // when
        List<CustomerOutstandingDto> result = loanService.getCustomerOutstandingReport();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        assertEquals(new BigDecimal("30.00"), result.get(0).getPaymentPercentage());
        verify(loanRepository, times(1)).findCustomerOutstandingReport();
    }

    @Test
    void getCustomerOutstandingById_WhenCustomerNotDeleted_ShouldReturnOutstanding() {
        // given
        CustomerOutstandingProjection projection = mock(CustomerOutstandingProjection.class);
        when(projection.getCustomerId()).thenReturn(1L);
        when(projection.getFullName()).thenReturn("John Doe");
        when(projection.getNik()).thenReturn("1234567890123456");
        when(projection.getTotalLoanAmount()).thenReturn(new BigDecimal("10000000"));
        when(projection.getTotalPaid()).thenReturn(new BigDecimal("5000000"));
        when(projection.getTotalLoans()).thenReturn(1L);
        when(projection.getActiveLoans()).thenReturn(1L);
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findCustomerOutstandingById(1L)).thenReturn(Optional.of(projection));

        // when
        CustomerOutstandingDto result = loanService.getCustomerOutstandingById(1L);

        // then
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals(new BigDecimal("50.00"), result.getPaymentPercentage());
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, times(1)).findCustomerOutstandingById(1L);
    }

    @Test
    void getCustomerOutstandingById_WhenCustomerIsDeleted_ShouldThrowException() {
        // given
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(true);

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> loanService.getCustomerOutstandingById(1L));
        verify(customerRepository, times(1)).findUserByIdAndIsDeleted(1L);
        verify(loanRepository, never()).findCustomerOutstandingById(any());
    }

    @Test
    void getCustomerOutstandingById_WhenCustomerHasNoLoans_ShouldThrowException() {
        // given
        when(customerRepository.findUserByIdAndIsDeleted(1L)).thenReturn(false);
        when(loanRepository.findCustomerOutstandingById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomerNotFoundException.class, () -> loanService.getCustomerOutstandingById(1L));
    }

    @Test
    void getCustomerOutstandingReport_WithZeroTotalLoanAmount_ShouldReturnZeroPercentage() {
        // given
        CustomerOutstandingProjection projection = mock(CustomerOutstandingProjection.class);
        when(projection.getCustomerId()).thenReturn(1L);
        when(projection.getFullName()).thenReturn("John Doe");
        when(projection.getNik()).thenReturn("1234567890123456");
        when(projection.getTotalLoanAmount()).thenReturn(BigDecimal.ZERO);
        when(projection.getTotalPaid()).thenReturn(BigDecimal.ZERO);
        when(projection.getTotalLoans()).thenReturn(0L);
        when(projection.getActiveLoans()).thenReturn(0L);
        when(loanRepository.findCustomerOutstandingReport()).thenReturn(Collections.singletonList(projection));

        // when
        List<CustomerOutstandingDto> result = loanService.getCustomerOutstandingReport();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.ZERO, result.get(0).getPaymentPercentage());
    }
}