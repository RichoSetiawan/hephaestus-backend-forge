package com.example.day2.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customers") 
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
}