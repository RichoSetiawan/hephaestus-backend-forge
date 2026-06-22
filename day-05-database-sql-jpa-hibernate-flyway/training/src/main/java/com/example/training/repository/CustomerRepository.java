package com.example.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.training.entity.CustomerEntity;
import java.util.List;
import java.util.Optional;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>{
    List<CustomerEntity> findByFullNameContainingIgnoreCase (String fullName);

    boolean existsByEmail(String email);

    boolean existsByNik(String nik);

    Optional<CustomerEntity> findByNik(String nik);

    Optional<CustomerEntity> findByEmail(String email);

}
