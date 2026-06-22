package com.example.training.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCustomerDto {
    @NotNull(message = "Deleted status must not be null")
    private Boolean deleted;
}
