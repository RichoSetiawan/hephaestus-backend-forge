package com.example.day2.model;

import lombok.Data;

@Data
public class Customer {
    private Long id;
	private String fullName;
	private String email;
	private String phoneNumber;
}
