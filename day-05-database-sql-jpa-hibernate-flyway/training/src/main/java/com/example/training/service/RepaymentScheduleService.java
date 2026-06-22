package com.example.training.service;

import com.example.training.dto.RepaymentScheduleResponse;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.exception.RepaymentScheduleNotFoundException;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleService {

    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Transactional(readOnly = true)
    public List<RepaymentScheduleResponse> findByLoanApplicationId(Long loanApplicationId) {
        if (!loanApplicationRepository.existsById(loanApplicationId)) {
            throw new LoanApplicationNotFoundException("Loan application not found with id: " + loanApplicationId);
        }
        return repaymentScheduleRepository.findByLoanApplicationId(loanApplicationId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepaymentScheduleResponse> findByStatus(RepaymentStatus param){
        return repaymentScheduleRepository.findByStatus(param).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RepaymentScheduleResponse findById(Long id) {
        RepaymentScheduleEntity entity = repaymentScheduleRepository.findById(id)
                .orElseThrow(() -> new RepaymentScheduleNotFoundException("Repayment schedule not found with id: " + id));
        return toResponse(entity);
    }

    private RepaymentScheduleResponse toResponse(RepaymentScheduleEntity entity) {
        return RepaymentScheduleResponse.builder()
                .id(entity.getId())
                .installmentNumber(entity.getInstallmentNumber())
                .dueDate(entity.getDueDate())
                .principalAmount(entity.getPrincipalAmount())
                .interestAmount(entity.getInterestAmount())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .loanApplicationId(entity.getLoanApplicationId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
