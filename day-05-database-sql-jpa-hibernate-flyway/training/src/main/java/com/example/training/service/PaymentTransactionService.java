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
import com.example.training.utils.LoggingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    @Transactional
    public PaymentTransactionResponse create(CreatePaymentTransactionRequest request) {
        log.info("Creating payment transaction: scheduleId={}, amount={}", 
                request.getRepaymentScheduleId(), request.getPaidAmount());
        
        RepaymentScheduleEntity schedule = repaymentScheduleRepository.findById(request.getRepaymentScheduleId())
                .orElseThrow(() -> {
                    log.warn("Repayment schedule not found: id={}", request.getRepaymentScheduleId());
                    return new RepaymentScheduleNotFoundException("Repayment schedule not found with id: " + request.getRepaymentScheduleId());
                });

        PaymentTransactionEntity transaction = PaymentTransactionEntity.builder()
                .repaymentScheduleId(request.getRepaymentScheduleId())
                .paymentReference(request.getPaymentReference())
                .paidAmount(request.getPaidAmount())
                .paidAt(request.getPaidAt())
                .status(PaymentStatus.SUCCESS)
                .build();

        PaymentTransactionEntity saved = paymentTransactionRepository.save(transaction);
        log.info("Payment transaction saved: id={}, amount={}", saved.getId(), saved.getPaidAmount());

        // Update schedule status if fully paid
        BigDecimal totalPaid = paymentTransactionRepository.sumPaidAmountByScheduleId(schedule.getId());
        log.debug("Schedule {} total paid: {}, required: {}", schedule.getId(), totalPaid, schedule.getTotalAmount());
        
        if (totalPaid.compareTo(schedule.getTotalAmount()) >= 0) {
            schedule.setStatus(RepaymentStatus.PAID);
            repaymentScheduleRepository.save(schedule);
            log.info("Repayment schedule fully paid: id={}", schedule.getId());
        } else if (totalPaid.compareTo(schedule.getTotalAmount()) < 0){
            schedule.setStatus(RepaymentStatus.PARTIAL);
            repaymentScheduleRepository.save(schedule);
            log.info("Repayment schedule partially paid: id={}, paid={}", schedule.getId(), totalPaid);
        }

        LoggingUtil.audit("PAYMENT_CREATED", "CREATE", 
                "Payment id=" + saved.getId() + ", schedule=" + saved.getRepaymentScheduleId() + ", amount=" + saved.getPaidAmount());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> findByRepaymentScheduleId(Long repaymentScheduleId) {
        log.info("Fetching payment transactions for schedule: {}", repaymentScheduleId);
        List<PaymentTransactionResponse> result = paymentTransactionRepository.findByRepaymentScheduleId(repaymentScheduleId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Found {} payment transactions for schedule: {}", result.size(), repaymentScheduleId);
        return result;
    }

    private PaymentTransactionResponse toResponse(PaymentTransactionEntity entity) {
        return PaymentTransactionResponse.builder()
                .id(entity.getId())
                .repaymentScheduleId(entity.getRepaymentScheduleId())
                .paymentReference(entity.getPaymentReference())
                .paidAmount(entity.getPaidAmount())
                .paidAt(entity.getPaidAt())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
