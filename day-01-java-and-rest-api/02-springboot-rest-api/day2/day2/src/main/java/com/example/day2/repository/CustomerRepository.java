package com.example.day2.repository;

import com.example.day2.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContaining(
            String fullName, String email, String phoneNumber);
}