package com.example.training.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "full_name is required")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "nik is required")
    @Size(min = 16, max = 16, message = "nik must be 16 characters")
    @JsonProperty("nik")
    private String nik;

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "phone_number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;
}
