package com.example.training.service;

import com.example.training.dto.CreatePaymentTransactionRequest;
import com.example.training.dto.PaymentTransactionResponse;
import com.example.training.entity.PaymentTransactionEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.PaymentStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.RepaymentScheduleNotFoundException;
import com.example.training.repository.PaymentTransactionRepository;
import com.example.training.repository.RepaymentScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @InjectMocks
    private PaymentTransactionService paymentTransactionService;

    private RepaymentScheduleEntity schedule;
    private PaymentTransactionEntity transaction;
    private CreatePaymentTransactionRequest request;

    @BeforeEach
    void setUp() {
        schedule = RepaymentScheduleEntity.builder()
                .id(1L)
                .loanApplicationId(1L)
                .installmentNumber(1)
                .principalAmount(new BigDecimal("1000000"))
                .interestAmount(new BigDecimal("120000"))
                .totalAmount(new BigDecimal("1120000"))
                .status(RepaymentStatus.UNPAID)
                .build();

        transaction = PaymentTransactionEntity.builder()
                .id(1L)
                .repaymentScheduleId(1L)
                .paymentReference("PAY-001")
                .paidAmount(new BigDecimal("1120000"))
                .paidAt(ZonedDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .build();

        request = new CreatePaymentTransactionRequest();
        request.setRepaymentScheduleId(1L);
        request.setPaymentReference("PAY-001");
        request.setPaidAmount(new BigDecimal("1120000"));
        request.setPaidAt(ZonedDateTime.now());
    }

    // ─────────────── create - fully paid ───────────────

    @Test
    void create_shouldCreateTransaction_andMarkScheduleAsPaid_whenFullyPaid() {
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class)))
                .thenReturn(transaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L))
                .thenReturn(new BigDecimal("1120000"));

        PaymentTransactionResponse result = paymentTransactionService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PAY-001", result.getPaymentReference());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());

        verify(repaymentScheduleRepository, times(1)).save(any(RepaymentScheduleEntity.class));
        assertEquals(RepaymentStatus.PAID, schedule.getStatus());
    }

    @Test
    void create_shouldMarkScheduleAsPartial_whenPartiallyPaid() {
        request.setPaidAmount(new BigDecimal("500000"));

        PaymentTransactionEntity partialTx = PaymentTransactionEntity.builder()
                .id(2L).repaymentScheduleId(1L)
                .paymentReference("PAY-001").paidAmount(new BigDecimal("500000"))
                .paidAt(ZonedDateTime.now()).status(PaymentStatus.SUCCESS).build();

        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class)))
                .thenReturn(partialTx);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L))
                .thenReturn(new BigDecimal("500000")); // less than totalAmount 1120000

        paymentTransactionService.create(request);

        assertEquals(RepaymentStatus.PARTIAL, schedule.getStatus());
        verify(repaymentScheduleRepository).save(schedule);
    }

    @Test
    void create_shouldMarkScheduleAsPaid_whenPaidAmountExceedsTotal() {
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class)))
                .thenReturn(transaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L))
                .thenReturn(new BigDecimal("2000000")); // over-payment

        paymentTransactionService.create(request);

        assertEquals(RepaymentStatus.PAID, schedule.getStatus());
    }

    @Test
    void create_shouldThrowRepaymentScheduleNotFoundException_whenScheduleNotFound() {
        when(repaymentScheduleRepository.findById(99L)).thenReturn(Optional.empty());

        request.setRepaymentScheduleId(99L);

        assertThrows(RepaymentScheduleNotFoundException.class,
                () -> paymentTransactionService.create(request));

        verify(paymentTransactionRepository, never()).save(any());
    }

    // ─────────────── findByRepaymentScheduleId ───────────────

    @Test
    void findByRepaymentScheduleId_shouldReturnTransactionList() {
        when(paymentTransactionRepository.findByRepaymentScheduleId(1L))
                .thenReturn(List.of(transaction));

        List<PaymentTransactionResponse> result =
                paymentTransactionService.findByRepaymentScheduleId(1L);

        assertThat(result).hasSize(1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("PAY-001", result.get(0).getPaymentReference());
        assertEquals(PaymentStatus.SUCCESS, result.get(0).getStatus());
        verify(paymentTransactionRepository).findByRepaymentScheduleId(1L);
    }

    @Test
    void findByRepaymentScheduleId_shouldReturnEmptyList_whenNoTransactions() {
        when(paymentTransactionRepository.findByRepaymentScheduleId(1L))
                .thenReturn(List.of());

        List<PaymentTransactionResponse> result =
                paymentTransactionService.findByRepaymentScheduleId(1L);

        assertThat(result).isEmpty();
    }
}
