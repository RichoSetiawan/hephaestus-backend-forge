package com.example.training;

import com.example.training.dto.CreateLoanApplicationRequest;
import com.example.training.dto.LoanApplicationResponse;
import com.example.training.dto.UpdateLoanStatusRequest;
import com.example.training.entity.LoanApplicationEntity;
import com.example.training.enums.LoanStatus;
import com.example.training.exception.CustomerNotFoundException;
import com.example.training.exception.LoanApplicationNotFoundException;
import com.example.training.repository.CustomerRepository;
import com.example.training.repository.LoanApplicationRepository;
import com.example.training.repository.RepaymentScheduleRepository;

import com.example.training.service.LoanApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    private LoanApplicationEntity loan;

    @BeforeEach
    void setUp() {

        loan = LoanApplicationEntity.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(new BigDecimal("12000000"))
                .tenorMonth(12)
                .purpose("Motor")
                .status(LoanStatus.SUBMITTED)
                .build();
    }

    @Test
    void create_shouldCreateLoanSuccessfully() {

        CreateLoanApplicationRequest request =
                new CreateLoanApplicationRequest();

        request.setCustomerId(1L);
        request.setLoanAmount(new BigDecimal("12000000"));
        request.setTenorMonth(12);
        request.setPurpose("Motor");

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        when(customerRepository.findUserByIdAndIsDeleted(1L))
                .thenReturn(false);

        when(loanRepository.save(any(LoanApplicationEntity.class)))
                .thenReturn(loan);

        LoanApplicationResponse response =
                loanApplicationService.create(request);

        assertNotNull(response);
        assertEquals(LoanStatus.SUBMITTED, response.getStatus());
        assertEquals(new BigDecimal("12000000"),
                response.getLoanAmount());

        verify(loanRepository).save(any());
    }

    @Test
    void create_shouldThrowCustomerNotFoundException() {

        CreateLoanApplicationRequest request =
                new CreateLoanApplicationRequest();

        request.setCustomerId(1L);

        when(customerRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                CustomerNotFoundException.class,
                () -> loanApplicationService.create(request)
        );

        verify(loanRepository, never())
                .save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenCustomerDeleted() {

        CreateLoanApplicationRequest request =
                new CreateLoanApplicationRequest();

        request.setCustomerId(1L);

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        when(customerRepository.findUserByIdAndIsDeleted(1L))
                .thenReturn(true);

        assertThrows(
                CustomerNotFoundException.class,
                () -> loanApplicationService.create(request)
        );

        verify(loanRepository, never())
                .save(any());
    }

    @Test
    void findById_shouldReturnLoan() {

        when(loanRepository.findByIdWithCustomer(1L))
                .thenReturn(Optional.of(loan));

        assertNotNull(
                loanApplicationService.findById(1L)
        );

        verify(loanRepository)
                .findByIdWithCustomer(1L);
    }

    @Test
    void findById_shouldThrowException() {

        when(loanRepository.findByIdWithCustomer(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                LoanApplicationNotFoundException.class,
                () -> loanApplicationService.findById(1L)
        );
    }

    @Test
    void updateStatus_shouldApproveLoan() {

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        loan.setStatus(LoanStatus.APPROVED);

        when(loanRepository.save(any()))
                .thenReturn(loan);

        LoanApplicationResponse response =
                loanApplicationService.updateStatus(
                        1L,
                        request
                );

        assertEquals(
                LoanStatus.APPROVED,
                response.getStatus()
        );

        verify(loanRepository).save(any());
    }

    @Test
    void updateStatus_shouldRejectLoan() {

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.REJECTED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        loan.setStatus(LoanStatus.REJECTED);

        when(loanRepository.save(any()))
                .thenReturn(loan);

        LoanApplicationResponse response =
                loanApplicationService.updateStatus(
                        1L,
                        request
                );

        assertEquals(
                LoanStatus.REJECTED,
                response.getStatus()
        );
    }

    @Test
    void updateStatus_shouldThrowInvalidTransition() {

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.CLOSED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.updateStatus(
                        1L,
                        request
                )
        );
    }

    @Test
    void updateStatus_shouldThrowWhenLoanAlreadyRejected() {

        loan.setStatus(LoanStatus.REJECTED);

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.updateStatus(
                        1L,
                        request
                )
        );
    }

    @Test
    void updateStatus_shouldGenerateRepaymentScheduleWhenDisbursed() {

        loan.setStatus(LoanStatus.APPROVED);

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.DISBURSED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentScheduleRepository
                .findByLoanApplicationId(1L))
                .thenReturn(Collections.emptyList());

        loan.setStatus(LoanStatus.DISBURSED);

        when(loanRepository.save(any()))
                .thenReturn(loan);

        LoanApplicationResponse response =
                loanApplicationService.updateStatus(
                        1L,
                        request
                );

        assertEquals(
                LoanStatus.DISBURSED,
                response.getStatus()
        );

        verify(repaymentScheduleRepository,
                times(12))
                .save(any());
    }

    @Test
    void updateStatus_shouldCloseLoanWhenAllSchedulesPaid() {

        loan.setStatus(LoanStatus.DISBURSED);

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.CLOSED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentScheduleRepository
                .existsUnpaidByLoanApplicationId(1L))
                .thenReturn(false);

        loan.setStatus(LoanStatus.CLOSED);

        when(loanRepository.save(any()))
                .thenReturn(loan);

        LoanApplicationResponse response =
                loanApplicationService.updateStatus(
                        1L,
                        request
                );

        assertEquals(
                LoanStatus.CLOSED,
                response.getStatus()
        );
    }

    @Test
    void updateStatus_shouldThrowWhenClosingLoanWithUnpaidSchedules() {

        loan.setStatus(LoanStatus.DISBURSED);

        UpdateLoanStatusRequest request =
                new UpdateLoanStatusRequest();

        request.setStatus(LoanStatus.CLOSED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentScheduleRepository
                .existsUnpaidByLoanApplicationId(1L))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> loanApplicationService.updateStatus(
                        1L,
                        request
                )
        );
    }
}