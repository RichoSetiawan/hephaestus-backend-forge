package com.example.day2.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCustomerRequest {
    private Long id;
	private String fullName;
	private String email;
	private String phoneNumber;
}
