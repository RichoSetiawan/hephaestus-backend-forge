package com.example.day2.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerResponse {
    private Long id;
	private String fullName;
	private String email;
	private String phoneNumber;

	public CustomerResponse(Long id, String fullName, String email, String phoneNumber) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public CustomerResponse(){
		
	}
}
