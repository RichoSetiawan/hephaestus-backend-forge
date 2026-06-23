package com.example.training.service;

import com.example.training.dto.RepaymentScheduleResponse;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.exception.RepaymentScheduleNotFoundException;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentScheduleServiceTest {

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private RepaymentScheduleService repaymentScheduleService;

    private RepaymentScheduleEntity schedule;

    @BeforeEach
    void setUp() {
        schedule = RepaymentScheduleEntity.builder()
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

    // ─────────────── findByLoanApplicationId ───────────────

    @Test
    void findByLoanApplicationId_shouldReturnScheduleList() {
        when(loanApplicationRepository.existsById(1L)).thenReturn(true);
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(List.of(schedule));

        List<RepaymentScheduleResponse> result =
                repaymentScheduleService.findByLoanApplicationId(1L);

        assertThat(result).hasSize(1);
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, result.get(0).getInstallmentNumber());
        assertEquals(RepaymentStatus.UNPAID, result.get(0).getStatus());
        verify(repaymentScheduleRepository).findByLoanApplicationId(1L);
    }

    @Test
    void findByLoanApplicationId_shouldReturnEmptyList_whenNoSchedules() {
        when(loanApplicationRepository.existsById(1L)).thenReturn(true);
        when(repaymentScheduleRepository.findByLoanApplicationId(1L))
                .thenReturn(Collections.emptyList());

        List<RepaymentScheduleResponse> result =
                repaymentScheduleService.findByLoanApplicationId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByLoanApplicationId_shouldThrowLoanApplicationNotFoundException_whenLoanNotFound() {
        when(loanApplicationRepository.existsById(99L)).thenReturn(false);

        assertThrows(LoanApplicationNotFoundException.class,
                () -> repaymentScheduleService.findByLoanApplicationId(99L));

        verify(repaymentScheduleRepository, never()).findByLoanApplicationId(any());
    }

    // ─────────────── findByStatus ───────────────

    @Test
    void findByStatus_shouldReturnSchedulesByStatus() {
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.UNPAID))
                .thenReturn(List.of(schedule));

        List<RepaymentScheduleResponse> result =
                repaymentScheduleService.findByStatus(RepaymentStatus.UNPAID);

        assertThat(result).hasSize(1);
        assertEquals(RepaymentStatus.UNPAID, result.get(0).getStatus());
        verify(repaymentScheduleRepository).findByStatus(RepaymentStatus.UNPAID);
    }

    @Test
    void findByStatus_shouldReturnEmptyList_whenNoSchedulesForStatus() {
        when(repaymentScheduleRepository.findByStatus(RepaymentStatus.PAID))
                .thenReturn(Collections.emptyList());

        List<RepaymentScheduleResponse> result =
                repaymentScheduleService.findByStatus(RepaymentStatus.PAID);

        assertThat(result).isEmpty();
    }

    // ─────────────── findById ───────────────

    @Test
    void findById_shouldReturnScheduleResponse() {
        when(repaymentScheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        RepaymentScheduleResponse result = repaymentScheduleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("1120000"), result.getTotalAmount());
        assertEquals(RepaymentStatus.UNPAID, result.getStatus());
        assertEquals(1L, result.getLoanApplicationId());
        verify(repaymentScheduleRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowRepaymentScheduleNotFoundException_whenNotFound() {
        when(repaymentScheduleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RepaymentScheduleNotFoundException.class,
                () -> repaymentScheduleService.findById(99L));
    }
}
