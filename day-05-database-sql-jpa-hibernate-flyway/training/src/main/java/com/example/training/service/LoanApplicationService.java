package com.example.training.service;

import com.example.training.dto.*;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.repository.CustomerRepository;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {

    private final LoanApplicationRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    private static final Set<LoanStatus> TERMINAL_STATUSES = EnumSet.of(LoanStatus.REJECTED, LoanStatus.CLOSED);

    @Transactional
    public LoanApplicationResponse create(CreateLoanApplicationRequest request) {
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new CustomerNotFoundException("Customer not found with id: " + request.getCustomerId());
        }
        if(customerRepository.findUserByIdAndIsDeleted(request.getCustomerId())){
            throw new CustomerNotFoundException("Customer with id: " + request.getCustomerId() + " is deleted");
        }
        LoanApplicationEntity loan = LoanApplicationEntity.builder()
                .customerId(request.getCustomerId())
                .loanAmount(request.getLoanAmount())
                .tenorMonth(request.getTenorMonth())
                .purpose(request.getPurpose())
                .status(LoanStatus.SUBMITTED)
                .build();

        LoanApplicationEntity saved = loanRepository.save(loan);

        // Generate repayment schedules
        // generateRepaymentSchedules(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoanApplicationDetailResponse findById(Long id) {
        LoanApplicationEntity loan = loanRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException("Loan application not found with id: " + id));
        return toDetailResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findAll() {
        return loanRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findByCustomerId(Long customerId) {
        if(customerRepository.findUserByIdAndIsDeleted(customerId)){
            throw new CustomerNotFoundException("Customer with id: " + customerId + " is deleted");
        }
        return loanRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LoanApplicationResponse> getAllLoanPagination(LoanStatus status, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable){
        if(startDate == null){
            startDate = Instant.EPOCH.atZone(ZoneId.of("Asia/Jakarta"));
        }
        if(endDate == null){
            endDate = ZonedDateTime.now();
        }
        Page<LoanApplicationEntity> entityPage = loanRepository.findByStatusAndDateRangeWithPage(status, startDate, endDate, pageable);
        return entityPage.map(this::toResponse);
    }

    // @Transactional
    // public LoanApplicationResponse updateStatus(Long id, UpdateLoanStatusRequest request) {
    //     LoanApplicationEntity loan = loanRepository.findById(id)
    //             .orElseThrow(() -> new LoanApplicationNotFoundException("Loan application not found with id: " + id));
    //     loan.setStatus(request.getStatus());
    //     return toResponse(loanRepository.save(loan));
    // }

    @Transactional
    public LoanApplicationResponse updateStatus(Long id, UpdateLoanStatusRequest request) {
        LoanApplicationEntity loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException("Loan not found"));

        LoanStatus current = loan.getStatus();
        LoanStatus next = request.getStatus();

        // Rule 1: Terminal states cannot be changed
        if (TERMINAL_STATUSES.contains(current)) {
            throw new IllegalStateException(
                "Loan with status " + current + " cannot be changed. Terminal state.");
        }

        // Rule 2: Validate state transition
        validateStateTransition(current, next);

        // Rule 3: Create repayment schedule ONLY when transitioning to DISBURSED
        if (next == LoanStatus.DISBURSED) {
            if (repaymentScheduleRepository.findByLoanApplicationId(id).isEmpty()) {
                generateRepaymentSchedules(loan);
            }
        }

        // Rule 4: CLOSED only if all repayment schedules are PAID
        if (next == LoanStatus.CLOSED) {
            boolean hasUnpaid = repaymentScheduleRepository.existsUnpaidByLoanApplicationId(id);
            if (hasUnpaid) {
                throw new IllegalStateException(
                    "Cannot close loan. There are still unpaid repayment schedules.");
            }
        }
        
        loan.setStatus(next);
        return toResponse(loanRepository.save(loan));
    }

    private void validateStateTransition(LoanStatus current, LoanStatus next) {
        switch (current) {
            case SUBMITTED:
                if (next != LoanStatus.APPROVED && next != LoanStatus.REJECTED) {
                    throw new IllegalStateException(
                        "SUBMITTED can only become APPROVED or REJECTED");
                }
                break;
            case APPROVED:
                if (next != LoanStatus.DISBURSED) {
                    throw new IllegalStateException(
                        "APPROVED can only become DISBURSED");
                }
                break;
            case DISBURSED:
                if (next != LoanStatus.CLOSED) {
                    throw new IllegalStateException(
                        "DISBURSED can only become CLOSED");
                }
                break;
            default:
                throw new IllegalStateException("Invalid transition");
        }
    }


    @Transactional(readOnly = true)
    public List<LoanReportDto> getLoanSummaryByStatus() {
        return loanRepository.summarizeByStatus().stream()
                .map(p -> LoanReportDto.builder()
                        .status(p.getStatus())
                        .totalLoans(p.getTotalLoans())
                        .totalAmount(p.getTotalAmount())
                        .averageAmount(p.getAverageAmount())
                        .minAmount(p.getMinAmount())
                        .maxAmount(p.getMaxAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanReportDto> getLoanSummaryByStatusAndDateRange(
            ZonedDateTime startDate, ZonedDateTime endDate) {
            if(startDate == null){
            startDate = Instant.EPOCH.atZone(ZoneId.of("Asia/Jakarta"));
            }
            if(endDate == null){
                endDate = ZonedDateTime.now();
            }
        return loanRepository.summarizeByStatusAndDateRange(startDate, endDate).stream()
                .map(p -> LoanReportDto.builder()
                        .status(p.getStatus())
                        .totalLoans(p.getTotalLoans())
                        .totalAmount(p.getTotalAmount())
                        .averageAmount(p.getAverageAmount())
                        .minAmount(p.getMinAmount())
                        .maxAmount(p.getMaxAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerOutstandingDto> getCustomerOutstandingReport() {
        return loanRepository.findCustomerOutstandingReport().stream()
                .map(this::toOutstandingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerOutstandingDto getCustomerOutstandingById(Long customerId) {
        if(customerRepository.findUserByIdAndIsDeleted(customerId)){
            throw new CustomerNotFoundException("Customer with id: " + customerId + " is deleted");
        }
        return loanRepository.findCustomerOutstandingById(customerId)
                .map(this::toOutstandingDto)
                .orElseThrow(() -> new CustomerNotFoundException(
                    "Customer not found or has no loans: " + customerId));
    }

    private CustomerOutstandingDto toOutstandingDto(CustomerOutstandingProjection p) {
        BigDecimal percentage = BigDecimal.ZERO;
        if (p.getTotalLoanAmount() != null && p.getTotalLoanAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = p.getTotalPaid()
                    .multiply(new BigDecimal("100"))
                    .divide(p.getTotalLoanAmount(), 2, RoundingMode.HALF_UP);
        }
        return CustomerOutstandingDto.builder()
                .customerId(p.getCustomerId())
                .fullName(p.getFullName())
                .nik(p.getNik())
                .totalLoanAmount(p.getTotalLoanAmount())
                .totalPaid(p.getTotalPaid())
                .outstandingAmount(p.getOutstandingAmount())
                .paymentPercentage(percentage)
                .totalLoans(p.getTotalLoans())
                .activeLoans(p.getActiveLoans())
                .build();
    }

    private void generateRepaymentSchedules(LoanApplicationEntity loan) {
        BigDecimal monthlyPrincipal = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getTenorMonth()), 2, RoundingMode.HALF_UP);
        BigDecimal interestRate = new BigDecimal("0.01"); // 1% per month example
        BigDecimal monthlyInterest = loan.getLoanAmount().multiply(interestRate)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalMonthly = monthlyPrincipal.add(monthlyInterest);

        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= loan.getTenorMonth(); i++) {
            RepaymentScheduleEntity schedule = RepaymentScheduleEntity.builder()
                    .loanApplicationId(loan.getId())
                    .installmentNumber(i)
                    .dueDate(dueDate)
                    .principalAmount(monthlyPrincipal)
                    .interestAmount(monthlyInterest)
                    .totalAmount(totalMonthly)
                    .status(RepaymentStatus.UNPAID)
                    .build();
            repaymentScheduleRepository.save(schedule);
            dueDate = dueDate.plusMonths(1);
        }
    }

    private LoanApplicationResponse toResponse(LoanApplicationEntity entity) {
        return LoanApplicationResponse.builder()
                .id(entity.getId())
                .loanAmount(entity.getLoanAmount())
                .tenorMonth(entity.getTenorMonth())
                .purpose(entity.getPurpose())
                .status(entity.getStatus())
                .customerId(entity.getCustomerId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LoanApplicationDetailResponse toDetailResponse(LoanApplicationEntity entity) {
        return LoanApplicationDetailResponse.builder()
                .id(entity.getId())
                .loanAmount(entity.getLoanAmount())
                .tenorMonth(entity.getTenorMonth())
                .purpose(entity.getPurpose())
                .status(entity.getStatus())
                .customer(CustomerResponse.builder()
                        .id(entity.getCustomer().getId())
                        .fullName(entity.getCustomer().getFullName())
                        .nik(entity.getCustomer().getNik())
                        .email(entity.getCustomer().getEmail())
                        .phoneNumber(entity.getCustomer().getPhoneNumber())
                        .build())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
