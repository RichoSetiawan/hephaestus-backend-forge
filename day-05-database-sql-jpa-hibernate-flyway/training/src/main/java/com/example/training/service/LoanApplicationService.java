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
import com.example.training.utils.LoggingUtil;

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
        log.info("Creating loan application: customerId={}, amount={}", request.getCustomerId(), request.getLoanAmount());
        if (!customerRepository.existsById(request.getCustomerId())) {
            log.warn("Customer not found: id={}", request.getCustomerId());
            throw new CustomerNotFoundException("Customer not found with id: " + request.getCustomerId());
        }
        if(customerRepository.findUserByIdAndIsDeleted(request.getCustomerId())){
            log.warn("Customer is deleted: id={}", request.getCustomerId());
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

        LoggingUtil.audit("LOAN_SUBMITTED", "CREATE", 
                "Loan id=" + saved.getId() + ", amount=" + request.getLoanAmount() + ", customer=" + request.getCustomerId());
        log.info("Loan application created successfully: id={}", saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoanApplicationDetailResponse findById(Long id) {
        log.info("Fetching loan application by id: {}", id);
        LoanApplicationEntity loan = loanRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> {
                    log.warn("Loan application not found: id={}", id);
                    return new LoanApplicationNotFoundException("Loan application not found with id: " + id);
                });
        log.debug("Loan application found: id={}, status={}", loan.getId(), loan.getStatus());
        return toDetailResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findAll() {
        log.info("Fetching all loan applications");
        List<LoanApplicationResponse> result = loanRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Retrieved {} loan applications", result.size());
        return result;

    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findByStatus(LoanStatus status) {
        log.info("Fetching loan applications by status: {}", status);
        List<LoanApplicationResponse> result = loanRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Found {} loan applications with status: {}", result.size(), status);
        return result;
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> findByCustomerId(Long customerId) {
        log.info("Fetching loan applications for customer: {}", customerId);
        if(customerRepository.findUserByIdAndIsDeleted(customerId)){
            log.warn("Customer is deleted: id={}", customerId);
            throw new CustomerNotFoundException("Customer with id: " + customerId + " is deleted");
        }
        List<LoanApplicationResponse> result = loanRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Found {} loan applications for customer: {}", result.size(), customerId);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<LoanApplicationResponse> getAllLoanPagination(LoanStatus status, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable){
        log.info("Fetching loan applications with pagination: status={}, startDate={}, endDate={}", status, startDate, endDate);
        if(startDate == null){
            startDate = Instant.EPOCH.atZone(ZoneId.of("Asia/Jakarta"));
        }
        if(endDate == null){
            endDate = ZonedDateTime.now();
        }
        Page<LoanApplicationEntity> entityPage = loanRepository.findByStatusAndDateRangeWithPage(status, startDate, endDate, pageable);
        log.debug("Retrieved {} loan applications in page {}/{}", 
                entityPage.getNumberOfElements(), entityPage.getNumber(), entityPage.getTotalPages());
        return entityPage.map(this::toResponse);
    }

    @Transactional
    public LoanApplicationResponse updateStatus(Long id, UpdateLoanStatusRequest request) {
        log.info("Updating loan status: id={}, newStatus={}", id, request.getStatus());
        
        LoanApplicationEntity loan = loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Loan not found: id={}", id);
                    return new LoanApplicationNotFoundException("Loan not found");
                });

        LoanStatus current = loan.getStatus();
        LoanStatus next = request.getStatus();
        log.debug("Current status={}, requested status={}", current, next);

        if (TERMINAL_STATUSES.contains(current)) {
            log.error("Cannot change terminal status: id={}, current={}", id, current);
            throw new IllegalStateException(
                "Loan with status " + current + " cannot be changed. Terminal state.");
        }

        validateStateTransition(current, next);

        if (next == LoanStatus.DISBURSED) {
            if (repaymentScheduleRepository.findByLoanApplicationId(id).isEmpty()) {
                log.info("Generating repayment schedules for loan: id={}", id);
                generateRepaymentSchedules(loan);
                log.info("Repayment schedules generated for loan: id={}", id);
            } else {
                log.warn("Repayment schedules already exist for loan: id={}", id);
            }
        }

        if (next == LoanStatus.CLOSED) {
            boolean hasUnpaid = repaymentScheduleRepository.existsUnpaidByLoanApplicationId(id);
            if (hasUnpaid) {
                log.error("Cannot close loan with unpaid schedules: id={}", id);
                throw new IllegalStateException(
                    "Cannot close loan. There are still unpaid repayment schedules.");
            }
            log.info("All repayment schedules paid for loan: id={}", id);
        }
        
        loan.setStatus(next);
        LoanApplicationEntity saved = loanRepository.save(loan);
        
        LoggingUtil.audit("LOAN_STATUS_CHANGED", next.name(), 
                "Loan id=" + id + ", from=" + current + ", to=" + next);
        log.info("Loan status updated successfully: id={}, from={} to={}", id, current, next);
        
        return toResponse(saved);
    }

    private void validateStateTransition(LoanStatus current, LoanStatus next) {
        log.debug("Validating state transition: {} -> {}", current, next);
        switch (current) {
            case SUBMITTED:
                if (next != LoanStatus.APPROVED && next != LoanStatus.REJECTED) {
                    log.error("Invalid transition: SUBMITTED -> {}", next);
                    throw new IllegalStateException(
                        "SUBMITTED can only become APPROVED or REJECTED");
                }
                break;
            case APPROVED:
                if (next != LoanStatus.DISBURSED) {
                    log.error("Invalid transition: APPROVED -> {}", next);
                    throw new IllegalStateException(
                        "APPROVED can only become DISBURSED");
                }
                break;
            case DISBURSED:
                if (next != LoanStatus.CLOSED) {
                    log.error("Invalid transition: DISBURSED -> {}", next);
                    throw new IllegalStateException(
                        "DISBURSED can only become CLOSED");
                }
                break;
            default:
                log.error("Invalid state transition: {} -> {}", current, next);
                throw new IllegalStateException("Invalid transition");
        }
    }


    @Transactional(readOnly = true)
    public List<LoanReportDto> getLoanSummaryByStatus() {
        log.info("Generating loan summary by status");
        List<LoanReportDto> result = loanRepository.summarizeByStatus().stream()
                .map(p -> LoanReportDto.builder()
                        .status(p.getStatus())
                        .totalLoans(p.getTotalLoans())
                        .totalAmount(p.getTotalAmount())
                        .averageAmount(p.getAverageAmount())
                        .minAmount(p.getMinAmount())
                        .maxAmount(p.getMaxAmount())
                        .build())
                .collect(Collectors.toList());
        log.debug("Generated summary for {} status groups", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<LoanReportDto> getLoanSummaryByStatusAndDateRange(
            ZonedDateTime startDate, ZonedDateTime endDate) {
            log.info("Generating loan summary by status and date range: startDate={}, endDate={}", startDate, endDate);
        if(startDate == null){
            startDate = Instant.EPOCH.atZone(ZoneId.of("Asia/Jakarta"));
        }
        if(endDate == null){
            endDate = ZonedDateTime.now();
        }
        List<LoanReportDto> result = loanRepository.summarizeByStatusAndDateRange(startDate, endDate).stream()
                .map(p -> LoanReportDto.builder()
                        .status(p.getStatus())
                        .totalLoans(p.getTotalLoans())
                        .totalAmount(p.getTotalAmount())
                        .averageAmount(p.getAverageAmount())
                        .minAmount(p.getMinAmount())
                        .maxAmount(p.getMaxAmount())
                        .build())
                .collect(Collectors.toList());
        log.debug("Generated summary for {} status groups in date range", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<CustomerOutstandingDto> getCustomerOutstandingReport() {
        log.info("Generating customer outstanding report");
        List<CustomerOutstandingDto> result = loanRepository.findCustomerOutstandingReport().stream()
                .map(this::toOutstandingDto)
                .collect(Collectors.toList());
        log.debug("Generated outstanding report for {} customers", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public CustomerOutstandingDto getCustomerOutstandingById(Long customerId) {
        log.info("Fetching customer outstanding by id: {}", customerId);
        if(customerRepository.findUserByIdAndIsDeleted(customerId)){
            log.warn("Customer is deleted: id={}", customerId);
            throw new CustomerNotFoundException("Customer with id: " + customerId + " is deleted");
        }
        CustomerOutstandingDto result = loanRepository.findCustomerOutstandingById(customerId)
                .map(this::toOutstandingDto)
                .orElseThrow(() -> {
                    log.warn("Customer not found or has no loans: id={}", customerId);
                    return new CustomerNotFoundException(
                        "Customer not found or has no loans: " + customerId);
                });
        log.debug("Customer outstanding retrieved: id={}", customerId);
        return result;
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
        log.info("Generating repayment schedules for loan: id={}, tenor={}", loan.getId(), loan.getTenorMonth());
        BigDecimal monthlyPrincipal = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getTenorMonth()), 2, RoundingMode.HALF_UP);
        BigDecimal interestRate = new BigDecimal("0.01");
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
        log.info("Generated {} repayment schedules for loan: id={}", loan.getTenorMonth(), loan.getId());
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
