# Exercise - Add Unit Test, Peer Review Checklist & Structured Logging

## Objective

Peserta mampu meningkatkan kualitas Loan Application API dengan menambahkan unit test, peer code review checklist, structured logging, correlation ID, dan PII-safe logging.

## Case

Lanjutkan exercise sebelumnya: **Authentication, Authorization & Loan Application API**. Sistem sudah memiliki Auth API, Customer API, Loan Application API, role `ADMIN`, `STAFF`, dan `APPROVER`, simple token authentication, approve/reject flow, serta standard error response `401`, `403`, dan `404`.

Pada exercise ini peserta tidak membuat API baru. Peserta meningkatkan kualitas API yang sudah ada.

## Part 1 - Unit Testing

### Technical Requirements

- Gunakan Java 17, Spring Boot 3.x, JUnit 5, dan Mockito.
- Fokus test pada service layer dengan pola Given-When-Then dan nama test yang jelas.
- Test harus mencakup happy path dan negative path.
- Jangan test getter/setter sederhana dan jangan memakai database asli untuk unit test.
- Mock repository atau dependency yang diperlukan.

### Required Test Classes

```text
src/test/java/com/example/training/service/AuthServiceTest.java
src/test/java/com/example/training/service/CustomerServiceTest.java
src/test/java/com/example/training/service/LoanApplicationServiceTest.java
```

### AuthService Test Cases

1. `should_login_successfully_when_username_and_password_are_valid`
2. `should_throw_unauthorized_when_password_is_invalid`
3. `should_return_current_user_when_token_is_valid`
4. `should_throw_unauthorized_when_token_is_missing`
5. `should_throw_unauthorized_when_token_is_invalid`

### CustomerService Test Cases

1. `should_create_customer_successfully`
2. `should_get_customer_by_id_successfully`
3. `should_throw_not_found_when_customer_does_not_exist`
4. `should_return_all_customers`
5. `should_not_allow_approver_to_create_customer`

### LoanApplicationService Test Cases

1. `should_create_loan_application_successfully`
2. `should_throw_not_found_when_customer_does_not_exist`
3. `should_get_loan_application_by_id_successfully`
4. `should_throw_not_found_when_loan_application_does_not_exist`
5. `should_approve_loan_when_user_is_approver`
6. `should_reject_loan_when_user_is_approver`
7. `should_throw_forbidden_when_staff_tries_to_approve_loan`
8. `should_throw_forbidden_when_staff_tries_to_reject_loan`

### Example Unit Test Structure

```java
@Test
void should_throw_forbidden_when_staff_tries_to_approve_loan() {
    // given
    Long loanId = 1L;
    AuthContext staffContext = new AuthContext("staff", "STAFF");
    LoanApplication loan = new LoanApplication();
    loan.setId(loanId);
    loan.setStatus("SUBMITTED");

    when(loanApplicationRepository.findById(loanId))
            .thenReturn(Optional.of(loan));

    // when & then
    ForbiddenException exception = assertThrows(ForbiddenException.class,
            () -> loanApplicationService.approveLoan(loanId, staffContext));

    assertEquals("FORBIDDEN", exception.getCode());
    verify(loanApplicationRepository, never()).save(any());
}
```

`given` menyiapkan data, `when` menjalankan action, dan `then` memverifikasi hasil. `never()` memastikan `STAFF` tidak menyimpan perubahan approval.

## Part 2 - Peer Code Review Checklist

### Task

Buat file `CODE_REVIEW_CHECKLIST.md` pada project aplikasi peserta. Checklist minimalnya:

### 1. Correctness

- [ ] Logic sesuai requirement.
- [ ] Happy path berjalan.
- [ ] Negative path ditangani.
- [ ] Edge case dipikirkan.
- [ ] Status transition benar.
- [ ] Tidak ada business rule yang dilewati.

### 2. Layer Responsibility

- [ ] Controller hanya mengatur HTTP request/response.
- [ ] Service berisi business logic.
- [ ] Repository hanya akses data.
- [ ] DTO tidak berisi business logic berat.
- [ ] Security/auth helper tidak dicampur dengan controller.

### 3. Error Handling

- [ ] `400` untuk validation error, `401` untuk unauthenticated, `403` untuk forbidden, dan `404` untuk data tidak ditemukan.
- [ ] Error response konsisten dan tidak membocorkan stack trace ke client.

### 4. Security & Authorization

- [ ] Endpoint protected sudah cek token dan role di backend.
- [ ] `STAFF` tidak bisa approve/reject loan.
- [ ] `APPROVER` tidak bisa create customer bila rule melarang.
- [ ] Backend tidak percaya role dari request body.
- [ ] Token tidak ditulis ke log.

### 5. Testing

- [ ] Service layer punya unit test untuk happy path, negative path, access logic, dan error behavior.
- [ ] Nama test jelas dan assertion cukup kuat.

### 6. Logging & PII

- [ ] Log menggunakan structured fields dan memiliki `correlation_id`.
- [ ] Log level tepat dan error log actionable.
- [ ] Password, token, dan raw PII tidak ditulis ke log.

### 7. Maintainability

- [ ] Naming jelas, method tidak terlalu panjang, dan duplicate logic tidak berlebihan.
- [ ] Code mudah dibaca dan comment hanya menjelaskan trade-off penting.

Komentar kurang membantu:

```text
Ini salah.
```

Komentar yang membantu:

```text
Business rule approve loan lebih aman tetap berada di service layer agar controller hanya fokus pada HTTP flow. Ini juga memudahkan unit test untuk role STAFF dan APPROVER.
```

## Part 3 - Structured Logging & Correlation ID

### Objective

Tambahkan logging yang aman dan traceable pada API.

### Technical Requirements

- Gunakan structured logging style dan support header `X-Correlation-Id`.
- Jika header tidak ada, backend membuat `correlation_id` baru.
- Semua log dalam request flow dan semua error response memiliki `correlation_id`.
- Gunakan `info` untuk successful business event, `warn` untuk business rejection/forbidden access, dan `error` untuk unexpected/system error.
- Jangan log password, token, raw NIK, raw phone number, full request body, atau stack trace ke client.

### Required Logging Events

| Scenario | Level | Event |
|---|---|---|
| Login success | `info` | `auth_login_success` |
| Login failed | `warn` | `auth_login_failed` |
| Customer created | `info` | `customer_created` |
| Loan application submitted | `info` | `loan_application_submitted` |
| Loan application approved | `info` | `loan_application_approved` |
| Loan application rejected | `info` | `loan_application_rejected` |
| Forbidden access | `warn` | `access_denied` |
| Validation error | `warn` | `validation_error` |
| Unexpected error | `error` | `unexpected_error` |

### Correlation ID Header dan Error Response

```text
X-Correlation-Id: REQ-20260424-001
```

Jika header ada, gunakan nilainya; jika tidak ada, generate nilai baru. Kembalikan nilai itu pada response error.

```json
{
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this resource",
  "correlation_id": "REQ-20260424-001",
  "errors": []
}
```

Untuk error tak terduga:

```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "Unexpected error occurred",
  "correlation_id": "REQ-20260424-001",
  "errors": []
}
```

### Safe Log Examples

```json
{"level":"info","event":"loan_application_submitted","application_id":"APP-001","customer_id":"CUST-001","correlation_id":"REQ-20260424-001"}
```

```json
{"level":"warn","event":"access_denied","user_id":"USR-001","role":"STAFF","endpoint":"/api/v1/loan-applications/1/approve","error_code":"FORBIDDEN","correlation_id":"REQ-20260424-001"}
```

```json
{"level":"error","event":"unexpected_error","error_code":"INTERNAL_SERVER_ERROR","correlation_id":"REQ-20260424-001"}
```

Hindari log yang berisi username bersama password, token, nama lengkap, NIK, dan nomor telepon. Jangan log full request body.

## Tasks

1. Tambahkan unit test untuk `AuthService`, `CustomerService`, dan `LoanApplicationService`.
2. Gunakan Given-When-Then dan Mockito untuk happy path, negative path, behavior `401`/`403`, serta approve/reject loan.
3. Buat `CODE_REVIEW_CHECKLIST.md` sesuai area checklist di atas.
4. Tambahkan support `X-Correlation-Id` dan `correlation_id` pada error response. (log)
5. Tambahkan structured log untuk login, customer created, loan submitted, approve/reject, forbidden access, validation error, dan unexpected error. (log)
6. Pastikan password, token, serta raw PII tidak masuk log. (log)
7. Test manual dengan Postman, baik dengan maupun tanpa header `X-Correlation-Id`.
8. Push ke fork dan buat Pull Request ke branch `master`.

## Acceptance Criteria

- [ ] Tiga service memiliki unit test dengan JUnit 5, Mockito, dan Given-When-Then.
- [ ] Happy path, negative path, `401`, `403`, approval APPROVER, dan penolakan STAFF dites.
- [ ] `CODE_REVIEW_CHECKLIST.md` mencakup correctness, error handling, authorization, testing, logging, dan PII.
- [ ] API mendukung/generate `X-Correlation-Id` dan error response memuat `correlation_id`.
- [ ] Structured fields serta level `info`/`warn`/`error` digunakan dengan benar.
- [ ] Forbidden access tercatat sebagai `warn`; unexpected error tercatat sebagai `error`.
- [ ] Password, token, dan raw PII tidak masuk log.
- [ ] Pull Request dibuat ke `master`.

## Optional Challenge

- Unit test untuk `GlobalExceptionHandler` dan `RoleValidator`.
- Test log event menggunakan appender test.
- Buat masking helper untuk email dan phone number.
- Tambahkan `correlation_id` ke success response, PR template, dan contoh review comment baik/kurang baik.
