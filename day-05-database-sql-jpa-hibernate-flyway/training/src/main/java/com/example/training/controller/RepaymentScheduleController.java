package com.example.training.controller;

import com.example.training.dto.ApiResponse;
import com.example.training.dto.RepaymentScheduleResponse;
import com.example.training.entity.RepaymentScheduleEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.enums.RepaymentStatus;
import com.example.training.service.RepaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RepaymentScheduleController {

    private final RepaymentScheduleService repaymentScheduleService;

    @GetMapping("/loan-applications/{loanApplicationId}/repayment-schedules")
    public ResponseEntity<ApiResponse<List<RepaymentScheduleResponse>>> getByLoanApplicationId(
            @PathVariable Long loanApplicationId) {
        List<RepaymentScheduleResponse> data = repaymentScheduleService.findByLoanApplicationId(loanApplicationId);
        return ResponseEntity.ok(ApiResponse.success("Repayment schedules retrieved successfully", data));
    }

    @GetMapping("/repayment-schedules/{id}")
    public ResponseEntity<ApiResponse<RepaymentScheduleResponse>> getById(@PathVariable Long id) {
        RepaymentScheduleResponse data = repaymentScheduleService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Repayment schedule retrieved successfully", data));
    }

    @GetMapping("/repayment/status")
    public ResponseEntity<ApiResponse<List<RepaymentScheduleResponse>>> getMethodName(@RequestParam RepaymentStatus param) {
        List<RepaymentScheduleResponse> data = repaymentScheduleService.findByStatus(param);
        return ResponseEntity.ok(ApiResponse.success("Repayment schedule retrieved successfully", data));
    }
    
}
