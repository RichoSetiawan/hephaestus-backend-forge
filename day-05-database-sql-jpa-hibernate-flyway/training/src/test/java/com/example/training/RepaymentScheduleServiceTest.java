package com.example.training;

import com.example.training.dto.RepaymentScheduleResponse;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.exception.RepaymentScheduleNotFoundException;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;
import com.example.training.service.RepaymentScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentScheduleServiceTest {

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private RepaymentScheduleService repaymentScheduleService;

    private RepaymentScheduleEntity schedule1;
    private RepaymentScheduleEntity schedule2;
    private LoanApplicationEntity loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplicationEntity.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(new BigDecimal("10000000"))
                .tenorMonth(12)
                .purpose("Business")
                .status(LoanStatus.DISBURSED)
                .build();

        schedule1 = RepaymentScheduleEntity.builder()
                .id(1L)
                .loanApplicationId(1L)
                .loanApplication(loanApplication)
                .installmentNumber(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .principalAmount(new BigDecimal("833333.33"))
                .interestAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("933333.33"))
                .status(RepaymentStatus.UNPAID)
                .createdAt(ZonedDateTime.now())
                .build();

        schedule2 = RepaymentScheduleEntity.builder()
                .id(2L)
                .loanApplicationId(1L)
                .loanApplication(loanApplication)
                .installmentNumber(2)
                .dueDate(LocalDate.now().plusMonths(2))
                .principalAmount(new BigDecimal("833333.33"))
                .interestAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("933333.33"))
                .status(RepaymentStatus.PAID)
                .createdAt(ZonedDateTime.now())
                .build();
    }

    @Test
    void findByLoanApplicationId_WhenLoanExists_ShouldReturnSchedules() {
        // given
        when(loanApplicationRepository.existsById(1L)).thenReturn(true);
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Arrays.asList(schedule1, schedule2));

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByLoanApplicationId(1L);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getInstallmentNumber());
        assertEquals(2, result.get(1).getInstallmentNumber());
        assertEquals(RepaymentStatus.UNPAID, result.get(0).getStatus());
        assertEquals(RepaymentStatus.PAID, result.get(1).getStatus());
        verify(loanApplicationRepository, times(1)).existsById(1L);
        verify(repaymentScheduleRepository, times(1)).findByLoanApplicationId(1L);
    }

    @Test
    void findByLoanApplicationId_WhenLoanNotExists_ShouldThrowException() {
        // given
        when(loanApplicationRepository.existsById(99L)).thenReturn(false);

        // when & then
        assertThrows(LoanApplicationNotFoundException.class,
                () -> repaymentScheduleService.findByLoanApplicationId(99L));
        verify(loanApplicationRepository, times(1)).existsById(99L);
        verify(repaymentScheduleRepository, never()).findByLoanApplicationId(any());
    }

    @Test
    void findByLoanApplicationId_WhenNoSchedules_ShouldReturnEmptyList() {
        // given
        when(loanApplicationRepository.existsById(1L)).thenReturn(true);
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Collections.emptyList());

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByLoanApplicationId(1L);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(loanApplicationRepository, times(1)).existsById(1L);
        verify(repaymentScheduleRepository, times(1)).findByLoanApplicationId(1L);
    }

    @Test
    void findByStatus_ShouldReturnSchedulesWithStatus() {
        // given
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.UNPAID))
                .thenReturn(Collections.singletonList(schedule1));

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByStatus(RepaymentStatus.UNPAID);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RepaymentStatus.UNPAID, result.get(0).getStatus());
        assertEquals(1, result.get(0).getInstallmentNumber());
        verify(repaymentScheduleRepository, times(1)).findByStatus(RepaymentStatus.UNPAID);
    }

    @Test
    void findByStatus_WhenNoSchedulesWithStatus_ShouldReturnEmptyList() {
        // given
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.OVERDUE))
                .thenReturn(Collections.emptyList());

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByStatus(RepaymentStatus.OVERDUE);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repaymentScheduleRepository, times(1)).findByStatus(RepaymentStatus.OVERDUE);
    }

    @Test
    void findById_WhenScheduleExists_ShouldReturnSchedule() {
        // given
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule1));

        // when
        RepaymentScheduleResponse result = repaymentScheduleService.findById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getInstallmentNumber());
        assertEquals(new BigDecimal("833333.33"), result.getPrincipalAmount());
        assertEquals(new BigDecimal("100000"), result.getInterestAmount());
        assertEquals(new BigDecimal("933333.33"), result.getTotalAmount());
        assertEquals(RepaymentStatus.UNPAID, result.getStatus());
        assertEquals(1L, result.getLoanApplicationId());
        verify(repaymentScheduleRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenScheduleNotExists_ShouldThrowException() {
        // given
        when(repaymentScheduleRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RepaymentScheduleNotFoundException.class, () -> repaymentScheduleService.findById(99L));
        verify(repaymentScheduleRepository, times(1)).findById(99L);
    }

    @Test
    void findByStatus_WithPaidStatus_ShouldReturnPaidSchedules() {
        // given
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.PAID))
                .thenReturn(Collections.singletonList(schedule2));

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByStatus(RepaymentStatus.PAID);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getInstallmentNumber());
        assertEquals(RepaymentStatus.PAID, result.get(0).getStatus());
        verify(repaymentScheduleRepository, times(1)).findByStatus(RepaymentStatus.PAID);
    }

    @Test
    void findByStatus_WithPartialStatus_ShouldReturnPartialSchedules() {
        // given
        RepaymentScheduleEntity partialSchedule = RepaymentScheduleEntity.builder()
                .id(3L)
                .loanApplicationId(1L)
                .installmentNumber(3)
                .dueDate(LocalDate.now().plusMonths(3))
                .principalAmount(new BigDecimal("833333.33"))
                .interestAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("933333.33"))
                .status(RepaymentStatus.PARTIAL)
                .build();
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.PARTIAL))
                .thenReturn(Collections.singletonList(partialSchedule));

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByStatus(RepaymentStatus.PARTIAL);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RepaymentStatus.PARTIAL, result.get(0).getStatus());
        assertEquals(3, result.get(0).getInstallmentNumber());
        verify(repaymentScheduleRepository, times(1)).findByStatus(RepaymentStatus.PARTIAL);
    }

    @Test
    void findByLoanApplicationId_ShouldMapAllFieldsCorrectly() {
        // given
        when(loanApplicationRepository.existsById(1L)).thenReturn(true);
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Collections.singletonList(schedule1));

        // when
        List<RepaymentScheduleResponse> result = repaymentScheduleService.findByLoanApplicationId(1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        RepaymentScheduleResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getInstallmentNumber());
        assertNotNull(response.getDueDate());
        assertEquals(new BigDecimal("833333.33"), response.getPrincipalAmount());
        assertEquals(new BigDecimal("100000"), response.getInterestAmount());
        assertEquals(new BigDecimal("933333.33"), response.getTotalAmount());
        assertEquals(RepaymentStatus.UNPAID, response.getStatus());
        assertEquals(1L, response.getLoanApplicationId());
        assertNotNull(response.getCreatedAt());
    }
}