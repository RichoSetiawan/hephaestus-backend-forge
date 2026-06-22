package com.example.training.repository;

import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentScheduleEntity, Long> {

    List<RepaymentScheduleEntity> findByLoanApplicationId(Long loanApplicationId);

    List<RepaymentScheduleEntity> findByStatus(RepaymentStatus status);

    @Query("SELECT r FROM RepaymentScheduleEntity r JOIN FETCH r.loanApplication WHERE r.id = :id")
    Optional<RepaymentScheduleEntity> findByIdWithLoanApplication(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM RepaymentScheduleEntity r WHERE r.loanApplicationId = :loanId AND r.status != 'PAID'")
    Long countUnpaidByLoanApplicationId(@Param("loanId") Long loanId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RepaymentScheduleEntity r WHERE r.loanApplicationId = :loanId AND r.status != 'PAID'")
    boolean existsUnpaidByLoanApplicationId(@Param("loanId") Long loanId);
}
