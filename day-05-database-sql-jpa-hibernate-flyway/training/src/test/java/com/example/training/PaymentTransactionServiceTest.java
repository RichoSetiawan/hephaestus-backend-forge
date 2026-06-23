package com.example.training;

import com.example.training.dto.CreatePaymentTransactionRequest;
import com.example.training.dto.PaymentTransactionResponse;
import com.example.training.entity.PaymentTransactionEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.PaymentStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.RepaymentScheduleNotFoundException;
import com.example.training.repository.PaymentTransactionRepository;
import com.example.training.repository.RepaymentScheduleRepository;
import com.example.training.service.PaymentTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class PaymentTransactionServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @InjectMocks
    private PaymentTransactionService paymentTransactionService;

    private RepaymentScheduleEntity repaymentSchedule;
    private CreatePaymentTransactionRequest createRequest;
    private PaymentTransactionEntity paymentTransaction;

    @BeforeEach
    void setUp() {
        repaymentSchedule = RepaymentScheduleEntity.builder()
                .id(1L)
                .loanApplicationId(1L)
                .installmentNumber(1)
                .dueDate(java.time.LocalDate.now().plusMonths(1))
                .principalAmount(new BigDecimal("900000"))
                .interestAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("1000000"))
                .status(RepaymentStatus.UNPAID)
                .build();

        createRequest = new CreatePaymentTransactionRequest();
        createRequest.setRepaymentScheduleId(1L);
        createRequest.setPaymentReference("REF-001");
        createRequest.setPaidAmount(new BigDecimal("1000000"));
        createRequest.setPaidAt(ZonedDateTime.now());

        paymentTransaction = PaymentTransactionEntity.builder()
                .id(1L)
                .repaymentScheduleId(1L)
                .paymentReference("REF-001")
                .paidAmount(new BigDecimal("1000000"))
                .paidAt(ZonedDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    @Test
    void create_WhenScheduleExistsAndFullyPaid_ShouldMarkScheduleAsPaid() {
        // given
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repaymentSchedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class))).thenReturn(paymentTransaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L)).thenReturn(new BigDecimal("1000000"));
        when(repaymentScheduleRepository.save(any(RepaymentScheduleEntity.class))).thenReturn(repaymentSchedule);

        // when
        PaymentTransactionResponse result = paymentTransactionService.create(createRequest);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("REF-001", result.getPaymentReference());
        assertEquals(new BigDecimal("1000000"), result.getPaidAmount());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(RepaymentStatus.PAID, repaymentSchedule.getStatus());
        verify(repaymentScheduleRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransactionEntity.class));
        verify(paymentTransactionRepository, times(1)).sumPaidAmountByScheduleId(1L);
        verify(repaymentScheduleRepository, times(1)).save(repaymentSchedule);
    }

    @Test
    void create_WhenScheduleExistsAndPartiallyPaid_ShouldMarkScheduleAsPartial() {
        // given
        createRequest.setPaidAmount(new BigDecimal("500000"));
        paymentTransaction.setPaidAmount(new BigDecimal("500000"));
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repaymentSchedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class))).thenReturn(paymentTransaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L)).thenReturn(new BigDecimal("500000"));
        when(repaymentScheduleRepository.save(any(RepaymentScheduleEntity.class))).thenReturn(repaymentSchedule);

        // when
        PaymentTransactionResponse result = paymentTransactionService.create(createRequest);

        // then
        assertNotNull(result);
        assertEquals(new BigDecimal("500000"), result.getPaidAmount());
        assertEquals(RepaymentStatus.PARTIAL, repaymentSchedule.getStatus());
        verify(repaymentScheduleRepository, times(1)).save(repaymentSchedule);
    }

    @Test
    void create_WhenScheduleNotFound_ShouldThrowException() {
        // given
        when(repaymentScheduleRepository.findById(99L)).thenReturn(Optional.empty());
        createRequest.setRepaymentScheduleId(99L);

        // when & then
        assertThrows(RepaymentScheduleNotFoundException.class, () -> paymentTransactionService.create(createRequest));
        verify(repaymentScheduleRepository, times(1)).findById(99L);
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void findByRepaymentScheduleId_ShouldReturnTransactions() {
        // given
        PaymentTransactionEntity transaction2 = PaymentTransactionEntity.builder()
                .id(2L)
                .repaymentScheduleId(1L)
                .paymentReference("REF-002")
                .paidAmount(new BigDecimal("500000"))
                .paidAt(ZonedDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();
        when(paymentTransactionRepository.findByRepaymentScheduleId(1L))
                .thenReturn(Arrays.asList(paymentTransaction, transaction2));

        // when
        List<PaymentTransactionResponse> result = paymentTransactionService.findByRepaymentScheduleId(1L);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("REF-001", result.get(0).getPaymentReference());
        assertEquals("REF-002", result.get(1).getPaymentReference());
        verify(paymentTransactionRepository, times(1)).findByRepaymentScheduleId(1L);
    }

    @Test
    void findByRepaymentScheduleId_WhenNoTransactions_ShouldReturnEmptyList() {
        // given
        when(paymentTransactionRepository.findByRepaymentScheduleId(1L))
                .thenReturn(Collections.emptyList());

        // when
        List<PaymentTransactionResponse> result = paymentTransactionService.findByRepaymentScheduleId(1L);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentTransactionRepository, times(1)).findByRepaymentScheduleId(1L);
    }

    @Test
    void create_WhenTotalPaidEqualsTotalAmount_ShouldMarkAsPaid() {
        // given
        createRequest.setPaidAmount(new BigDecimal("1000000"));
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repaymentSchedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class))).thenReturn(paymentTransaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L)).thenReturn(new BigDecimal("1000000"));
        when(repaymentScheduleRepository.save(any(RepaymentScheduleEntity.class))).thenReturn(repaymentSchedule);

        // when
        paymentTransactionService.create(createRequest);

        // then
        assertEquals(RepaymentStatus.PAID, repaymentSchedule.getStatus());
    }

    @Test
    void create_WhenTotalPaidGreaterThanTotalAmount_ShouldMarkAsPaid() {
        // given
        createRequest.setPaidAmount(new BigDecimal("1100000"));
        paymentTransaction.setPaidAmount(new BigDecimal("1100000"));
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repaymentSchedule));
        when(paymentTransactionRepository.save(any(PaymentTransactionEntity.class))).thenReturn(paymentTransaction);
        when(paymentTransactionRepository.sumPaidAmountByScheduleId(1L)).thenReturn(new BigDecimal("1100000"));
        when(repaymentScheduleRepository.save(any(RepaymentScheduleEntity.class))).thenReturn(repaymentSchedule);

        // when
        paymentTransactionService.create(createRequest);

        // then
        assertEquals(RepaymentStatus.PAID, repaymentSchedule.getStatus());
    }
}