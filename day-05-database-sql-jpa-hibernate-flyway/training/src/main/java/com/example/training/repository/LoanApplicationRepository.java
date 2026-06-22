package com.example.training.repository;

import com.example.training.dto.LoanStatusSummaryProjection;
import com.example.training.dto.CustomerOutstandingProjection;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.enums.LoanStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long> {

    List<LoanApplicationEntity> findByCustomerId(Long customerId);

    List<LoanApplicationEntity> findByStatus(LoanStatus status);

    @Query("SELECT l FROM LoanApplicationEntity l JOIN FETCH l.customer WHERE l.id = :id")
    Optional<LoanApplicationEntity> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT l FROM LoanApplicationEntity l JOIN l.customer c WHERE c.id = :customerId")
    List<LoanApplicationEntity> findLoansByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT l FROM LoanApplicationEntity l " +
    "WHERE (:status IS NULL OR l.status = :status) " +
    "AND l.createdAt >= :startDate " +
    "AND l.createdAt <= :endDate")
    Page<LoanApplicationEntity> findByStatusAndDateRangeWithPage(
        @Param("status") LoanStatus status,
        @Param("startDate") ZonedDateTime startDate,
        @Param("endDate") ZonedDateTime endDate,
        Pageable pageable);

        @Query("SELECT l.status as status, " +
        "COUNT(l) as totalLoans, " +
        "SUM(l.loanAmount) as totalAmount, " +
        "AVG(l.loanAmount) as averageAmount, " +
        "MIN(l.loanAmount) as minAmount, " +
        "MAX(l.loanAmount) as maxAmount " +
        "FROM LoanApplicationEntity l " +
        "GROUP BY l.status")
    List<LoanStatusSummaryProjection> summarizeByStatus();

    @Query("SELECT l.status as status, " +
        "COUNT(l) as totalLoans, " +
        "SUM(l.loanAmount) as totalAmount, " +
        "AVG(l.loanAmount) as averageAmount, " +
        "MIN(l.loanAmount) as minAmount, " +
        "MAX(l.loanAmount) as maxAmount " +
        "FROM LoanApplicationEntity l " +
        "WHERE l.createdAt >= :startDate AND l.createdAt <= :endDate " +
        "GROUP BY l.status")
    List<LoanStatusSummaryProjection> summarizeByStatusAndDateRange(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate);
    
        // Outstanding ALL customers (native query)
    @Query(value = "SELECT c.id as customerId, c.full_name as fullName, c.nik as nik, " +
            "COALESCE(SUM(l.loan_amount), 0) as totalLoanAmount, " +
            "COALESCE(SUM(pt.paid_amount), 0) as totalPaid, " +
            "COALESCE(SUM(l.loan_amount), 0) - COALESCE(SUM(pt.paid_amount), 0) as outstandingAmount, " +
            "COUNT(DISTINCT l.id) as totalLoans, " +
            "COUNT(DISTINCT CASE WHEN l.status IN ('SUBMITTED', 'APPROVED', 'DISBURSED') THEN l.id END) as activeLoans " +
            "FROM customers c " +
            "LEFT JOIN loan_applications l ON c.id = l.customer_id " +
            "LEFT JOIN repayment_schedules r ON l.id = r.loan_application_id " +
            "LEFT JOIN payment_transactions pt ON r.id = pt.repayment_schedule_id AND pt.status = 'SUCCESS' " +
            "WHERE c.is_deleted = false " +
            "GROUP BY c.id, c.full_name, c.nik " +
            "ORDER BY outstandingAmount DESC",
            nativeQuery = true)
    List<CustomerOutstandingProjection> findCustomerOutstandingReport();

    // Outstanding SINGLE customer (native query)
    @Query(value = "SELECT c.id as customerId, c.full_name as fullName, c.nik as nik, " +
            "COALESCE(SUM(l.loan_amount), 0) as totalLoanAmount, " +
            "COALESCE(SUM(pt.paid_amount), 0) as totalPaid, " +
            "COALESCE(SUM(l.loan_amount), 0) - COALESCE(SUM(pt.paid_amount), 0) as outstandingAmount, " +
            "COUNT(DISTINCT l.id) as totalLoans, " +
            "COUNT(DISTINCT CASE WHEN l.status IN ('SUBMITTED', 'APPROVED', 'DISBURSED') THEN l.id END) as activeLoans " +
            "FROM customers c " +
            "LEFT JOIN loan_applications l ON c.id = l.customer_id " +
            "LEFT JOIN repayment_schedules r ON l.id = r.loan_application_id " +
            "LEFT JOIN payment_transactions pt ON r.id = pt.repayment_schedule_id AND pt.status = 'SUCCESS' " +
            "WHERE c.id = :customerId AND c.is_deleted = false " +
            "GROUP BY c.id, c.full_name, c.nik",
            nativeQuery = true)
    Optional<CustomerOutstandingProjection> findCustomerOutstandingById(@Param("customerId") Long customerId);
}
