# Materi - Backend Quality: Testing, Peer Code Review & Observability

## 1. Recap Previous Days

Pada day sebelumnya kita sudah membuat REST API, validation dan error handling, contract/manual testing melalui Swagger atau Postman, serta authentication dan authorization sederhana. Loan Application API memiliki flow submit, approve, dan reject. Sekarang fokusnya bergeser: bagaimana memastikan perubahan tetap aman dan bagaimana menelusuri masalah saat terjadi.

## 2. Why This Topic Matters

Dalam sistem finance, perubahan kecil dapat berdampak besar. Validasi eligibility yang salah dapat meloloskan loan tidak valid; role check yang salah dapat membuat `STAFF` melakukan approval; status transition yang salah dapat membuat loan disetujui dua kali. Error response yang tidak konsisten menyulitkan client, sementara log yang terlalu sedikit sulit ditelusuri dan log yang terlalu detail dapat membocorkan PII.

## 3. Working Code vs Trusted Code

Working code adalah code yang tampak berjalan saat dicoba manual. Trusted code adalah code dengan behavior penting yang sudah diverifikasi sehingga lebih aman diubah.

| Area | Working Code | Trusted Code |
|---|---|---|
| Manual test | Bisa berhasil saat dicoba | Behavior penting sudah dites |
| Edge case | Belum tentu aman | Edge case dipikirkan |
| Refactor | Berisiko | Lebih aman |
| Review | Sulit dibuktikan | Ada test dan reasoning |
| Production | Masih banyak asumsi | Lebih siap dikembangkan |

## 4. Testing Is Risk Reduction

Testing bukan formalitas dan bukan jaminan nol bug. Testing mengurangi risiko bug tersembunyi, menjaga business rule, memberi confidence saat refactor, membantu engineer baru memahami behavior, dan mengurangi regression. Coverage tinggi bukan tujuan utama; pertanyaan yang lebih baik adalah: *apakah test melindungi business rule yang penting?*

## 5. Testing Mindset

Sebelum menulis test, tanyakan:

- Behavior apa yang harus terjadi?
- Kondisi apa yang harus ditolak?
- Edge case apa yang mungkin muncul?
- Apa yang tidak boleh berubah?
- Bagaimana membuktikan logic ini benar?

## 6. Given-When-Then

`Given` menyiapkan kondisi awal, `When` menjalankan action, dan `Then` memverifikasi hasil. Pola ini membuat test mudah dibaca.

```java
@Test
void should_reject_loan_when_staff_tries_to_approve() {
    // given
    // when
    // then
}
```

## 7. Test Pyramid

Unit test menguji logic kecil, integration test menguji beberapa komponen bersama, dan E2E test menguji flow penuh dari sudut pandang user/system. Semakin tinggi level test, umumnya semakin lambat dan mahal.

| Test Type | Scope | Speed | Example |
|---|---|---|---|
| Unit Test | Method/service kecil | Cepat | approve rule di `LoanApplicationService` |
| Integration Test | Beberapa layer | Sedang | Controller + Service |
| E2E Test | Flow penuh | Lambat | Login sampai approve loan |

## 8. Unit Test dan Service Layer

Unit test cocok untuk business rule, branching logic, calculation, status transition, error throwing behavior, dan access decision. Biasanya tidak perlu untuk getter/setter sederhana atau behavior framework.

Service layer cocok diuji karena business rule ada di sana, tidak terlalu dekat dengan HTTP, dan repository/dependency dapat dimock. Controller fokus pada request/response; repository fokus pada akses data.

## 9. JUnit 5 Basics

JUnit 5 digunakan untuk membuat dan menjalankan test, assertion, pengelompokan test, serta menjadikan expected behavior eksplisit.

```java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanApplicationServiceTest {
    @Test
    void should_submit_loan_when_customer_exists() {
        // given, when, then
    }
}
```

Assertion harus cukup kuat untuk menangkap perubahan behavior.

```java
assertEquals("SUBMITTED", result.getStatus());
assertThrows(CustomerNotFoundException.class,
        () -> service.createLoanApplication(request));
```

## 10. Mockito Basics dan Verify

Mockito membuat mock dependency seperti repository, external client, atau service lain. Test tidak membutuhkan database/external service asli, respons dependency dapat dikontrol, dan hasilnya cepat serta predictable.

```java
when(customerRepository.findById(1L))
        .thenReturn(Optional.of(customer));
```

Gunakan `verify` hanya bila interaction itu penting bagi behavior.

```java
verify(loanApplicationRepository).save(any(LoanApplication.class));
verify(notificationClient, never()).send(any());
```

Jangan memverifikasi semua detail implementasi karena test menjadi rapuh.

## 11. Contoh Test Case Loan Application

| Scenario | Expected | Dependency | Focus |
|---|---|---|---|
| customer exists | `SUBMITTED` | customer repository | happy path |
| customer not found | `CUSTOMER_NOT_FOUND` | customer repository | resource check |
| staff approve loan | `FORBIDDEN` | role validator | access logic |
| approver approve loan | `APPROVED` | loan repository | status transition |
| loan not found | `LOAN_APPLICATION_NOT_FOUND` | loan repository | error mapping |
| duplicate active loan | `LOAN_ALREADY_EXISTS` | loan repository | business rule |

## 12. Testing Error Handling dan Access Logic

Test error handling memastikan exception type, error code, HTTP status mapping, dan format response konsisten. Jangan bocorkan internal error kepada client.

Untuk access logic, minimal test: request tanpa token menghasilkan `401`, token valid dengan role salah menghasilkan `403`, `STAFF` tidak dapat approve, `APPROVER` dapat approve, `ADMIN` dapat mengakses semua endpoint, dan `APPROVER` tidak dapat membuat customer bila itulah aturannya.

Tidak semua log harus dites. Test log event secara selektif untuk event penting seperti forbidden access (`warn`), unexpected error (`error`), dan loan dibuat (`info`) dengan `correlation_id`.

## 13. Common Unit Test Mistakes dan Test as Documentation

Kesalahan umum: test terlalu panjang, menguji banyak behavior sekaligus, nama test tidak jelas, assertion terlalu lemah, mock berlebihan, hanya mengejar coverage, atau terlalu dekat pada implementation detail.

Test yang baik juga menjadi dokumentasi: ia menunjukkan rule yang berlaku, kondisi yang ditolak, output yang diharapkan, dan memudahkan review maupun onboarding.

## 14. Peer Code Review

Peer code review adalah proses engineer lain membaca dan menilai perubahan code. Tujuannya menemukan bug tersembunyi, menjaga standard tim, meningkatkan readability, memastikan design decision masuk akal, dan menjaga maintainability. Review bukan serangan pada orang; fokusnya adalah code, risk, correctness, security, dan business behavior.

Area review yang penting: layer responsibility, validation, error handling, access control, logging safety, test relevance, naming, query efficiency, dan maintainability.

Contoh hidden bug: status transition salah, authorization hilang, duplicate logic, null handling hilang, error mapping tidak ada, concurrency issue, atau PII/token masuk log.

Komentar kurang membantu:

```text
Ini salah.
```

Komentar yang membantu:

```text
Business rule ini lebih cocok di service agar controller fokus pada HTTP flow.
Ini juga membuat logic lebih mudah di-unit-test. Bisa tambahkan test ketika STAFF mencoba approve loan?
```

## 15. Structured Logging

Structured logging menulis log dengan field yang jelas sehingga mudah dicari, difilter, dibuat dashboard, dan digunakan audit.

Log tidak terstruktur:

```text
failed submit loan for customer, something wrong
```

Log terstruktur:

```json
{
  "level": "warn",
  "event": "loan_application_rejected",
  "application_id": "APP-20260424-001",
  "customer_id": "CUST-001",
  "error_code": "CUSTOMER_NOT_ELIGIBLE",
  "correlation_id": "REQ-20260424-001"
}
```

Gunakan nama event konsisten, misalnya `auth_login_success`, `auth_login_failed`, `customer_created`, `loan_application_submitted`, `loan_application_approved`, `loan_application_rejected`, `access_denied`, `validation_error`, dan `unexpected_error`. Hindari nama seperti `error nih`, `failed`, atau `something wrong`.

## 16. Correlation ID

`correlation_id` adalah ID unik untuk menandai satu request flow.

```text
X-Correlation-Id: REQ-20260424-001
```

Jika client mengirim header, backend menggunakan nilainya. Jika tidak, backend membuat nilai baru. ID ini harus muncul pada semua log dalam request flow, error response, dan diteruskan ke downstream service bila ada.

```json
{
  "status": 500,
  "error_code": "INTERNAL_SERVER_ERROR",
  "message": "Unexpected error occurred",
  "correlation_id": "REQ-20260424-001"
}
```

Client tidak perlu melihat detail internal; engineer cukup menelusuri `correlation_id`.

## 17. Log Levels dan PII Safety

| Level | Meaning | Example | Action |
|---|---|---|---|
| `info` | event normal yang penting | loan submitted | biasanya tanpa alert |
| `warn` | kondisi tidak ideal | customer tidak eligible | monitor trend |
| `error` | failure perlu investigasi | database timeout | alert/investigate |

PII adalah personally identifiable information, misalnya NIK, nomor telepon, alamat, email, rekening, data keluarga, dan dokumen identitas. Jangan log password, access/refresh token, full request body, full customer profile, full NIK/phone number, secret/config, atau stack trace ke response client.

Unsafe:

```json
{"event":"loan_application_created","nik":"3173010101900001","phone_number":"08123456789"}
```

Lebih aman:

```json
{"event":"loan_application_created","application_id":"APP-001","customer_id":"CUST-001","correlation_id":"REQ-001"}
```

Jika benar-benar diperlukan, masking dapat digunakan seperti `0812****6789` atau `bu***@example.com`; password dan token harus `[REDACTED]`. Default yang aman: jangan log PII jika tidak diperlukan.

## 18. Logging untuk Audit dan Monitoring

Business event yang berguna: login success, customer created, loan submitted, approved, dan rejected. Failure event: login failed, validation error, customer/loan not found, forbidden access, unexpected error, dan timeout.

```json
{"level":"warn","event":"access_denied","user_id":"USR-001","role":"STAFF","endpoint":"/api/v1/loan-applications/1/approve","error_code":"FORBIDDEN","correlation_id":"REQ-001"}
```

Jangan log token. Role dan endpoint sudah cukup untuk investigasi awal.

| Signal | Possible Meaning | Action Owner |
|---|---|---|
| `INTERNAL_SERVER_ERROR` naik | system issue | backend/SRE |
| `FORBIDDEN` naik | misuse, UI, atau access issue | backend/security |
| `VALIDATION_ERROR` naik | client issue | FE/BE |
| timeout naik | dependency issue | BE/platform |

## 19. Key Takeaways

- Testing adalah risk reduction; service layer memudahkan unit test.
- JUnit 5 menyusun test, Mockito mengisolasi dependency.
- Peer review menjaga kualitas tim dan menemukan hidden bug.
- Structured logging dan `correlation_id` mempercepat tracing serta audit.
- Raw PII dan secret tidak boleh masuk log.
- Test, review, dan log bersama-sama adalah quality system.
