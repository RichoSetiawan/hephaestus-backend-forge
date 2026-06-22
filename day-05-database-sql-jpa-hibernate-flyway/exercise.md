# Exercise - Finance Database, SQL, JPA, Hibernate, Flyway & Query Relationship

## Objective

Peserta mampu membuat rancangan dan implementasi requirement backend API sederhana untuk case finance industry menggunakan Java 17, Spring Boot 3.x, PostgreSQL, Spring Data JPA, Hibernate, Flyway migration, relationship antar table, dan query join.

## Case

Buat sistem sederhana untuk proses pengajuan pinjaman nasabah.

Sistem memiliki 4 data utama:

1. Customer
2. Loan Application
3. Repayment Schedule
4. Payment Transaction

Flow bisnis sederhana:

- Customer bisa mengajukan pinjaman.
- Satu customer bisa memiliki banyak loan application.
- Satu loan application memiliki beberapa repayment schedule.
- Satu repayment schedule bisa memiliki payment transaction.
- Backend harus bisa mengambil data loan beserta customer.
- Backend harus bisa mengambil repayment schedule berdasarkan loan.
- Backend harus bisa mencatat payment transaction.
- Backend harus bisa filter loan berdasarkan status.

## Business Rules

- Customer wajib memiliki `full_name`, `nik`, `email`, dan `phone_number`.
- `nik` harus unique.
- `email` harus unique.
- Loan application wajib memiliki `customer_id`, `loan_amount`, `tenor_month`, dan `purpose`.
- Status awal loan application adalah `SUBMITTED`.
- Status loan application bisa: `SUBMITTED`, `APPROVED`, `REJECTED`, `DISBURSED`, `CLOSED`.
- Perubahan status harus mengikuti flow loan; status tidak boleh dilompati atau dikembalikan ke status sebelumnya.
- Repayment schedule dibuat berdasarkan tenor saat loan berstatus `DISBURSED`.
- Payment transaction mencatat pembayaran cicilan.
- Jika customer tidak ditemukan saat create loan, return 404.
- Jika loan tidak ditemukan, return 404.
- Jika repayment schedule tidak ditemukan, return 404.
- Jika request tidak valid, return 400.
- API response tidak boleh return Entity langsung.

## Repayment Schedule Rules

### Loan Status Flow

```text
SUBMITTED --> APPROVED --> DISBURSED --> CLOSED
     |
     +--> REJECTED
```

- Saat create loan, backend menetapkan status `SUBMITTED`.
- Loan `SUBMITTED` hanya dapat diubah menjadi `APPROVED` atau `REJECTED`.
- Loan `APPROVED` hanya dapat diubah menjadi `DISBURSED`.
- Saat status berubah ke `DISBURSED`, backend membuat repayment schedule sesuai `tenor_month`. Schedule hanya dibuat sekali.
- Loan `DISBURSED` hanya dapat diubah menjadi `CLOSED` setelah seluruh repayment schedule berstatus `PAID`.
- `REJECTED` dan `CLOSED` adalah status akhir; status tersebut tidak boleh diubah lagi.

Repayment schedule tidak dibuat saat loan masih `SUBMITTED` atau hanya `APPROVED`; schedule dibuat ketika loan benar-benar `DISBURSED`.

Untuk exercise ini, gunakan bunga **flat 12% per tahun**. Bunga wajib berasal dari konfigurasi backend, bukan dari input request customer dan bukan dari payment transaction.

```properties
loan.interest.annual-rate=0.12
```

Service membaca nilai config tersebut ketika membuat repayment schedule. Pada sistem nyata, rate biasanya berasal dari `loan_product`, lalu disimpan pada loan saat disetujui agar perubahan rate produk tidak mengubah cicilan loan yang sudah berjalan.

```text
monthly_interest_rate = annual_interest_rate / 12
principal_amount      = loan_amount / tenor_month
interest_amount       = loan_amount x monthly_interest_rate
total_amount          = principal_amount + interest_amount
```

Contoh: loan Rp12.000.000 dengan tenor 12 bulan dan bunga 12% per tahun menghasilkan:

```text
principal_amount = 12.000.000 / 12 = 1.000.000
interest_amount  = 12.000.000 x 1% = 120.000
total_amount     = 1.120.000
```

Backend membuat 12 repayment schedule dengan `installment_number` berurutan dan `due_date` setiap bulan dari tanggal pencairan. Gunakan `BigDecimal` untuk nominal uang; tetapkan aturan pembulatan secara eksplisit apabila pembagian menghasilkan pecahan.

`payment_transaction` mencatat uang yang dibayarkan, bukan menghitung ulang bunga dan pokok. Untuk pembayaran satu cicilan, buat payment transaction pada repayment schedule terkait dan ubah status schedule menjadi `PAID` jika total pembayaran memenuhi `total_amount`.

Jika customer membayar dua bulan sekaligus, pendekatan sederhana adalah membuat dua payment transaction, masing-masing untuk satu repayment schedule. Jika satu transaksi harus dapat dibagi ke beberapa schedule, tambahkan tabel `payment_allocations`:

```text
payment_transaction (id, paid_amount, paid_at, ...)
payment_allocation  (payment_transaction_id, repayment_schedule_id, amount)
```

## Tables

### customers

```text
id
full_name
nik
email
phone_number
created_at
updated_at
```

### loan_applications

```text
id
customer_id
loan_amount
tenor_month
purpose
status
created_at
updated_at
```

### repayment_schedules

```text
id
loan_application_id
installment_number
due_date
principal_amount
interest_amount
total_amount
status
created_at
updated_at
```

### payment_transactions

```text
id
repayment_schedule_id
payment_reference
paid_amount
paid_at
status
created_at
updated_at
```

## Relationship

- Customer `one-to-many` LoanApplication.
- LoanApplication `many-to-one` Customer.
- LoanApplication `one-to-many` RepaymentSchedule.
- RepaymentSchedule `many-to-one` LoanApplication.
- RepaymentSchedule `one-to-many` PaymentTransaction.
- PaymentTransaction `many-to-one` RepaymentSchedule.

## Technical Requirements

- Java 17 compatible.
- Spring Boot 3.x compatible.
- Menggunakan Spring Web.
- Menggunakan Spring Data JPA.
- Menggunakan Hibernate 6.
- Menggunakan PostgreSQL driver.
- Menggunakan Flyway.
- Menggunakan Jakarta Persistence.
- Menggunakan Jakarta Validation.
- Gunakan import `jakarta.persistence.*`, bukan `javax.persistence.*`.
- Gunakan import `jakarta.validation.*`, bukan `javax.validation.*`.
- Menggunakan validation.
- Menggunakan global exception handler.
- Menggunakan DTO untuk request dan response.
- Menggunakan Repository untuk akses database.
- Menggunakan Service untuk business logic.
- Menggunakan Controller hanya untuk routing API.
- Menggunakan `@Transactional` untuk write operation.
- Menggunakan `@Transactional(readOnly = true)` untuk read operation.
- Relationship default menggunakan `FetchType.LAZY`.
- Gunakan JPQL join untuk query relationship.
- Gunakan join fetch ketika response membutuhkan data relation.
- Gunakan native query untuk reporting sederhana.
- Jangan return Entity langsung dari Controller.
- JSON menggunakan `snake_case`.
- Java menggunakan `camelCase`.
- Gunakan `@JsonProperty` untuk mapping JSON.

## Dependencies

Tambahkan dependency berikut:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Jika dependency validation belum tersedia, tambahkan:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## Database Configuration

Gunakan contoh konfigurasi berikut:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_training
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Flyway Migration

Buat migration berikut:

```text
src/main/resources/db/migration/V1__create_customers_table.sql
src/main/resources/db/migration/V2__create_loan_applications_table.sql
src/main/resources/db/migration/V3__create_repayment_schedules_table.sql
src/main/resources/db/migration/V4__create_payment_transactions_table.sql
```

## Required Endpoints

### Customer API

```text
POST /api/v1/customers
GET  /api/v1/customers/{id}
GET  /api/v1/customers
GET  /api/v1/customers/search?name=budi
```

### Loan Application API

```text
POST  /api/v1/loan-applications
GET   /api/v1/loan-applications/{id}
GET   /api/v1/loan-applications
GET   /api/v1/customers/{customer_id}/loan-applications
GET   /api/v1/loan-applications?status=SUBMITTED
PATCH /api/v1/loan-applications/{id}/status
```

### Repayment Schedule API

```text
GET /api/v1/loan-applications/{loan_application_id}/repayment-schedules
GET /api/v1/repayment-schedules/{id}
```

### Payment Transaction API

```text
POST /api/v1/payment-transactions
GET  /api/v1/repayment-schedules/{repayment_schedule_id}/payment-transactions
```

## Request JSON

### Create Customer

```json
{
  "full_name": "Budi Santoso",
  "nik": "3173010101900001",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

### Create Loan Application

```json
{
  "customer_id": 1,
  "loan_amount": 10000000,
  "tenor_month": 12,
  "purpose": "Working capital"
}
```

### Update Loan Status

```json
{
  "status": "APPROVED"
}
```

### Create Payment Transaction

```json
{
  "repayment_schedule_id": 1,
  "payment_reference": "PAY-20260619-001",
  "paid_amount": 950000,
  "paid_at": "2026-06-19T10:00:00"
}
```

## Response JSON

### Loan Detail Response

```json
{
  "success": true,
  "message": "Loan application retrieved successfully",
  "data": {
    "id": 1,
    "loan_amount": 10000000,
    "tenor_month": 12,
    "purpose": "Working capital",
    "status": "APPROVED",
    "customer": {
      "id": 1,
      "full_name": "Budi Santoso",
      "nik": "3173010101900001",
      "email": "budi@mail.com"
    }
  }
}
```

### Repayment Schedule Response

```json
{
  "success": true,
  "message": "Repayment schedules retrieved successfully",
  "data": [
    {
      "id": 1,
      "installment_number": 1,
      "due_date": "2026-07-19",
      "principal_amount": 833333,
      "interest_amount": 100000,
      "total_amount": 933333,
      "status": "UNPAID"
    }
  ]
}
```

## Error Response JSON

Customer not found:

```json
{
  "success": false,
  "code": "CUSTOMER_NOT_FOUND",
  "message": "Customer not found with id: 999",
  "errors": []
}
```

Loan not found:

```json
{
  "success": false,
  "code": "LOAN_APPLICATION_NOT_FOUND",
  "message": "Loan application not found with id: 999",
  "errors": []
}
```

Validation error:

```json
{
  "success": false,
  "code": "VALIDATION_ERROR",
  "message": "Invalid request",
  "errors": [
    {
      "field": "loan_amount",
      "message": "loan_amount must be greater than 0"
    }
  ]
}
```

## Suggested Structure

```text
src/main/java/com/example/training/
├── TrainingApplication.java
├── controller/
│   ├── CustomerController.java
│   ├── LoanApplicationController.java
│   ├── RepaymentScheduleController.java
│   └── PaymentTransactionController.java
├── service/
│   ├── CustomerService.java
│   ├── LoanApplicationService.java
│   ├── RepaymentScheduleService.java
│   └── PaymentTransactionService.java
├── repository/
│   ├── CustomerRepository.java
│   ├── LoanApplicationRepository.java
│   ├── RepaymentScheduleRepository.java
│   └── PaymentTransactionRepository.java
├── dto/
│   ├── ApiResponse.java
│   ├── CreateCustomerRequest.java
│   ├── CustomerResponse.java
│   ├── CustomerSummaryResponse.java
│   ├── CreateLoanApplicationRequest.java
│   ├── UpdateLoanStatusRequest.java
│   ├── LoanApplicationResponse.java
│   ├── CreatePaymentTransactionRequest.java
│   ├── PaymentTransactionResponse.java
│   ├── RepaymentScheduleResponse.java
│   ├── ErrorResponse.java
│   └── FieldErrorResponse.java
├── entity/
│   ├── CustomerEntity.java
│   ├── LoanApplicationEntity.java
│   ├── RepaymentScheduleEntity.java
│   └── PaymentTransactionEntity.java
└── exception/
    ├── CustomerNotFoundException.java
    ├── LoanApplicationNotFoundException.java
    ├── RepaymentScheduleNotFoundException.java
    ├── DuplicateCustomerException.java
    └── GlobalExceptionHandler.java
```

## Repository Requirements

### CustomerRepository

```java
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByNik(String nik);

    Optional<CustomerEntity> findByEmail(String email);

    boolean existsByNik(String nik);

    boolean existsByEmail(String email);

    List<CustomerEntity> findByFullNameContainingIgnoreCase(String fullName);
}
```

### LoanApplicationRepository

```java
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long> {

    List<LoanApplicationEntity> findByCustomerId(Long customerId);

    List<LoanApplicationEntity> findByStatus(String status);

    @Query("SELECT l FROM LoanApplicationEntity l JOIN FETCH l.customer WHERE l.id = :id")
    Optional<LoanApplicationEntity> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT l FROM LoanApplicationEntity l JOIN l.customer c WHERE c.id = :customerId")
    List<LoanApplicationEntity> findLoansByCustomerId(@Param("customerId") Long customerId);
}
```

### RepaymentScheduleRepository

```java
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentScheduleEntity, Long> {

    List<RepaymentScheduleEntity> findByLoanApplicationId(Long loanApplicationId);

    @Query("SELECT r FROM RepaymentScheduleEntity r JOIN FETCH r.loanApplication WHERE r.id = :id")
    Optional<RepaymentScheduleEntity> findByIdWithLoanApplication(@Param("id") Long id);
}
```

### PaymentTransactionRepository

```java
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {

    List<PaymentTransactionEntity> findByRepaymentScheduleId(Long repaymentScheduleId);

    @Query(value = "SELECT COALESCE(SUM(paid_amount), 0) FROM payment_transactions WHERE repayment_schedule_id = :scheduleId AND status = 'SUCCESS'", nativeQuery = true)
    BigDecimal sumPaidAmountByScheduleId(@Param("scheduleId") Long scheduleId);
}
```

## SQL Practice

Jalankan dan pahami SQL berikut:

```sql
SELECT * FROM customers;

SELECT * FROM loan_applications WHERE status = 'SUBMITTED';

SELECT * FROM loan_applications WHERE customer_id = 1;

SELECT
    l.id,
    l.loan_amount,
    l.tenor_month,
    l.status,
    c.full_name,
    c.nik
FROM loan_applications l
INNER JOIN customers c ON c.id = l.customer_id;

SELECT
    r.id,
    r.installment_number,
    r.due_date,
    r.total_amount,
    r.status,
    l.id AS loan_application_id,
    c.full_name
FROM repayment_schedules r
INNER JOIN loan_applications l ON l.id = r.loan_application_id
INNER JOIN customers c ON c.id = l.customer_id;

SELECT
    l.status,
    COUNT(*) AS total_loan,
    SUM(l.loan_amount) AS total_amount
FROM loan_applications l
GROUP BY l.status;
```

## Tasks

1. Pastikan project menggunakan Java 17.
2. Pastikan project menggunakan Spring Boot 3.x.
3. Tambahkan dependency Spring Data JPA.
4. Tambahkan dependency PostgreSQL driver.
5. Tambahkan dependency Flyway.
6. Tambahkan dependency Validation jika belum ada.
7. Buat database PostgreSQL `finance_training`.
8. Setup `application.properties`.
9. Buat migration untuk table `customers`.
10. Buat migration untuk table `loan_applications`.
11. Buat migration untuk table `repayment_schedules`.
12. Buat migration untuk table `payment_transactions`.
13. Buat Entity untuk semua table menggunakan `jakarta.persistence`.
14. Buat relationship antar Entity.
15. Gunakan `FetchType.LAZY`.
16. Buat Repository untuk semua Entity.
17. Buat derived query untuk search customer.
18. Buat JPQL query untuk loan with customer.
19. Buat native query untuk total payment atau summary loan.
20. Buat DTO request dan response menggunakan `jakarta.validation`.
21. Buat Service layer.
22. Buat Controller layer.
23. Buat validation.
24. Buat exception untuk not found dan duplicate data.
25. Buat clean API response.
26. Test semua endpoint.
27. Pastikan Entity tidak langsung dikembalikan sebagai response.
28. Push ke fork dan buat Pull Request.

## Acceptance Criteria

- [ ] Application menggunakan Java 17.
- [ ] Application menggunakan Spring Boot 3.x.
- [ ] Application bisa berjalan di `localhost:8080`.
- [ ] Application terkoneksi ke PostgreSQL.
- [ ] Flyway berhasil membuat semua table.
- [ ] Entity menggunakan `jakarta.persistence`.
- [ ] Validation menggunakan `jakarta.validation`.
- [ ] Customer bisa dibuat dan disimpan ke database.
- [ ] Loan application bisa dibuat untuk customer yang valid.
- [ ] Loan application gagal dibuat jika customer tidak ditemukan.
- [ ] Repayment schedule bisa diambil berdasarkan loan application.
- [ ] Payment transaction bisa dibuat untuk repayment schedule.
- [ ] Search customer by name berjalan.
- [ ] Filter loan by status berjalan.
- [ ] Query join loan dengan customer berjalan.
- [ ] Native query summary berjalan.
- [ ] Relationship menggunakan `FetchType.LAZY`.
- [ ] Response menggunakan DTO.
- [ ] Tidak return Entity langsung.
- [ ] Validation error menghasilkan 400.
- [ ] Not found error menghasilkan 404.
- [ ] Duplicate NIK/email menghasilkan error yang sesuai.
- [ ] Response JSON menggunakan `snake_case`.
- [ ] Pull Request dibuat ke branch `master`.

## Optional Challenge

Jika tugas utama selesai, tambahkan:

- Pagination untuk list loan application.
- Filter loan berdasarkan tanggal pengajuan.
- Filter repayment schedule berdasarkan status `PAID` / `UNPAID`.
- Endpoint summary total loan by status.
- Endpoint outstanding amount per customer.
- Index untuk `nik`, `email`, `customer_id`, dan `status`.
- Soft delete customer.
- DTO projection untuk query report.
