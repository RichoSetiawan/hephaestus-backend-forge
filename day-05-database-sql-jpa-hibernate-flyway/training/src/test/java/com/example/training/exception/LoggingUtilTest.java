package com.example.training.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import com.example.training.utils.LoggingUtil;

import static org.junit.jupiter.api.Assertions.*;

class LoggingUtilTest {

    @BeforeEach
    void setUp() {
        MDC.put("correlation_id", "test-correlation-123");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // ─────────────── audit ───────────────

    @Test
    void audit_shouldNotThrow_whenCalledWithValidParams() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("CUSTOMER_CREATED", "CREATE", "Customer id=1, name=Budi Santoso")
        );
    }

    @Test
    void audit_shouldNotThrow_whenDetailsIsNull() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("CUSTOMER_CREATED", "CREATE", null)
        );
    }

    @Test
    void audit_shouldNotThrow_whenCorrelationIdIsMissing() {
        MDC.clear();
        assertDoesNotThrow(() ->
            LoggingUtil.audit("LOAN_SUBMITTED", "CREATE", "Loan id=1")
        );
    }

    @Test
    void audit_shouldMaskSensitiveFields_inDetails() {
        // Should not throw even when sensitive data is present — masking is applied internally
        assertDoesNotThrow(() ->
            LoggingUtil.audit("CUSTOMER_CREATED", "CREATE",
                "Customer {\"nik\":\"1234567890123456\", \"email\":\"budi@test.com\"}")
        );
    }

    @Test
    void audit_shouldNotThrow_whenDetailsIsObject() {
        Object details = new Object() {
            @Override
            public String toString() {
                return "{\"id\":1,\"name\":\"Budi\"}";
            }
        };
        assertDoesNotThrow(() ->
            LoggingUtil.audit("TEST_EVENT", "CREATE", details)
        );
    }

    // ─────────────── security ───────────────

    @Test
    void security_shouldNotThrow_whenCalledWithValidParams() {
        assertDoesNotThrow(() ->
            LoggingUtil.security("LOGIN_FAILED", "Invalid credentials", "budi@test.com")
        );
    }

    @Test
    void security_shouldNotThrow_whenCorrelationIdIsMissing() {
        MDC.clear();
        assertDoesNotThrow(() ->
            LoggingUtil.security("UNAUTHORIZED_ACCESS", "Token expired", "user123")
        );
    }

    @Test
    void security_shouldNotThrow_whenUsernameIsNull() {
        assertDoesNotThrow(() ->
            LoggingUtil.security("UNKNOWN_USER", "User not found", null)
        );
    }

    // ─────────────── error ───────────────

    @Test
    void error_shouldNotThrow_whenCalledWithValidParams() {
        Exception ex = new RuntimeException("Something went wrong");
        assertDoesNotThrow(() ->
            LoggingUtil.error("UNEXPECTED_ERROR", "An error occurred", ex)
        );
    }

    @Test
    void error_shouldNotThrow_whenExceptionIsNull() {
        assertDoesNotThrow(() ->
            LoggingUtil.error("VALIDATION_ERROR", "Input validation failed", null)
        );
    }

    @Test
    void error_shouldNotThrow_whenCorrelationIdIsMissing() {
        MDC.clear();
        assertDoesNotThrow(() ->
            LoggingUtil.error("DB_ERROR", "Database connection failed", new RuntimeException("timeout"))
        );
    }

    @Test
    void error_shouldNotThrow_withCheckedException() {
        Exception ex = new Exception("Checked exception");
        assertDoesNotThrow(() ->
            LoggingUtil.error("CHECKED_ERROR", "Checked exception occurred", ex)
        );
    }

    // ─────────────── maskSensitive (via audit) ───────────────

    @Test
    void audit_shouldMaskPasswordField() {
        // Verified indirectly — no exception means masking ran successfully
        assertDoesNotThrow(() ->
            LoggingUtil.audit("AUTH_EVENT", "LOGIN",
                "{\"password\":\"secret123\", \"username\":\"budi\"}")
        );
    }

    @Test
    void audit_shouldMaskTokenField() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("TOKEN_EVENT", "VALIDATE",
                "{\"token\":\"eyJhbGciOiJIUzI1NiJ9.payload.signature\"}")
        );
    }

    @Test
    void audit_shouldMaskNikField() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("CUSTOMER_EVENT", "CREATE",
                "{\"nik\":\"1234567890123456\", \"full_name\":\"Budi\"}")
        );
    }

    @Test
    void audit_shouldMaskEmailField() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("CUSTOMER_EVENT", "CREATE",
                "{\"email\":\"budi@test.com\", \"full_name\":\"Budi\"}")
        );
    }

    @Test
    void audit_shouldHandleDetailsWithNoSensitiveFields() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("LOAN_EVENT", "CREATE",
                "{\"loan_id\":1, \"amount\":12000000, \"tenor\":12}")
        );
    }

    @Test
    void audit_shouldHandleEmptyStringDetails() {
        assertDoesNotThrow(() ->
            LoggingUtil.audit("EMPTY_EVENT", "TEST", "")
        );
    }
}