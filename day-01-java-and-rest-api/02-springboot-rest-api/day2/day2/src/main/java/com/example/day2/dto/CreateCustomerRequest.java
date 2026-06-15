package com.example.day2.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCustomerRequest {
    
    // @NotNull(message = "Id tidak boleh kosong")
    private Long id;
    
    @NotBlank(message = "Name is required")
	@Size(min = 3,message = "Nama minimun 3 character")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Format email tidak valid") 
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 10, message = "Phone number maksimal 10 digit")
    private String phoneNumber;
}
