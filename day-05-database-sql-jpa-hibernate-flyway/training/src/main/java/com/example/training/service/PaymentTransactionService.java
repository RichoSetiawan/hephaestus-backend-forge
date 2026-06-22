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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    @Transactional
    public PaymentTransactionResponse create(CreatePaymentTransactionRequest request) {
        RepaymentScheduleEntity schedule = repaymentScheduleRepository.findById(request.getRepaymentScheduleId())
                .orElseThrow(() -> new RepaymentScheduleNotFoundException("Repayment schedule not found with id: " + request.getRepaymentScheduleId()));

        PaymentTransactionEntity transaction = PaymentTransactionEntity.builder()
                .repaymentScheduleId(request.getRepaymentScheduleId())
                .paymentReference(request.getPaymentReference())
                .paidAmount(request.getPaidAmount())
                .paidAt(request.getPaidAt())
                .status(PaymentStatus.SUCCESS)
                .build();

        PaymentTransactionEntity saved = paymentTransactionRepository.save(transaction);

        // Update schedule status if fully paid
        BigDecimal totalPaid = paymentTransactionRepository.sumPaidAmountByScheduleId(schedule.getId());
        if (totalPaid.compareTo(schedule.getTotalAmount()) >= 0) {
            schedule.setStatus(RepaymentStatus.PAID);
            repaymentScheduleRepository.save(schedule);
        } else if (totalPaid.compareTo(schedule.getTotalAmount()) < 0){
            schedule.setStatus(RepaymentStatus.PARTIAL);
            repaymentScheduleRepository.save(schedule);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> findByRepaymentScheduleId(Long repaymentScheduleId) {
        return paymentTransactionRepository.findByRepaymentScheduleId(repaymentScheduleId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
