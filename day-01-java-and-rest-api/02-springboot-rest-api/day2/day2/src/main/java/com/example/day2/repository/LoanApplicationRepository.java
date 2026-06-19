package com.example.day2.repository;

import com.example.day2.enum_auth.LoanStatus;
import com.example.day2.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByStatus(LoanStatus status);
    List<LoanApplication> findByCustomerId(Long customerId);
    List<LoanApplication> findByStatusAndCustomerId(LoanStatus status, Long customerId);
}
