
# Exercise - Authentication, Authorization & Loan Application API

## Objective

Peserta dapat membuat authentication sederhana, menerapkan authorization berbasis role, dan menambahkan service baru yaitu Loan Application API.

## Case

Lanjutkan Customer Management API dari exercise sebelumnya.

Pada exercise ini, sistem memiliki fitur:

1. Login sederhana.
2. Current user endpoint.
3. Customer API.
4. Loan Application API.
5. Authorization berdasarkan role.

Sistem memiliki 3 role:

| Role | Description |
|---|---|
| ADMIN | Bisa mengakses semua endpoint |
| STAFF | Bisa membuat customer dan loan application |
| APPROVER | Bisa melihat data dan approve/reject loan application |

## Endpoints

### Auth API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/login` | Login user |
| GET | `/api/v1/auth/me` | Get current logged-in user |

### Customer API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/customers` | Create customer |
| GET | `/api/v1/customers` | Get all customers |
| GET | `/api/v1/customers/{id}` | Get customer by ID |

### Loan Application API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/loan-applications` | Create loan application |
| GET | `/api/v1/loan-applications` | Get all loan applications |
| GET | `/api/v1/loan-applications/{id}` | Get loan application by ID |
| PATCH | `/api/v1/loan-applications/{id}/approve` | Approve loan application |
| PATCH | `/api/v1/loan-applications/{id}/reject` | Reject loan application |

## Authorization Rules

| Endpoint | ADMIN | STAFF | APPROVER |
|---|---|---|---|
| `POST /api/v1/auth/login` | Yes | Yes | Yes |
| `GET /api/v1/auth/me` | Yes | Yes | Yes |
| `POST /api/v1/customers` | Yes | Yes | No |
| `GET /api/v1/customers` | Yes | Yes | Yes |
| `GET /api/v1/customers/{id}` | Yes | Yes | Yes |
| `POST /api/v1/loan-applications` | Yes | Yes | No |
| `GET /api/v1/loan-applications` | Yes | Yes | Yes |
| `GET /api/v1/loan-applications/{id}` | Yes | Yes | Yes |
| `PATCH /api/v1/loan-applications/{id}/approve` | Yes | No | Yes |
| `PATCH /api/v1/loan-applications/{id}/reject` | Yes | No | Yes |

## Technical Requirements

- Java 8 compatible.
- Menggunakan Spring Boot.
- Menggunakan dependency Spring Web.
- Tidak menggunakan database.
- Data disimpan menggunakan in-memory `Map`.
- Menggunakan Controller, Service, DTO, dan Model.
- JSON menggunakan `snake_case`.
- Java menggunakan `camelCase`.
- Menggunakan `@JsonProperty` untuk mapping `snake_case`.
- Menggunakan `ResponseEntity` untuk status code.
- Menggunakan simple token authentication.
- Token boleh hardcoded.
- Tidak perlu JWT.
- Tidak perlu Spring Security.
- Semua protected endpoint wajib membaca header `Authorization`.
- Jika token kosong atau tidak valid, return `401 Unauthorized`.
- Jika token valid tetapi role tidak punya akses, return `403 Forbidden`.

## Dummy Users

Gunakan user berikut:

| Username | Password | Role | Token |
|---|---|---|---|
| admin | admin123 | ADMIN | token-admin |
| staff | staff123 | STAFF | token-staff |
| approver | approver123 | APPROVER | token-approver |

## Protected Request Header

Gunakan format berikut untuk protected endpoint:

```text
Authorization: Bearer token-admin
````

Contoh lain:

```text
Authorization: Bearer token-staff
```

```text
Authorization: Bearer token-approver
```

---

# Request & Response

## 1. Login

### Request

```text
POST /api/v1/auth/login
```

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "token": "token-admin",
  "username": "admin",
  "role": "ADMIN"
}
```

### Failed Response

Status:

```text
401 Unauthorized
```

Response:

```json
{
  "code": "UNAUTHORIZED",
  "message": "Invalid username or password",
  "errors": []
}
```

---

## 2. Get Current User

### Request

```text
GET /api/v1/auth/me
Authorization: Bearer token-admin
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "username": "admin",
  "role": "ADMIN"
}
```

---

## 3. Create Customer

### Request

```text
POST /api/v1/customers
Authorization: Bearer token-staff
```

```json
{
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

### Success Response

Status:

```text
201 Created
```

Response:

```json
{
  "id": 1,
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

### Forbidden Response

Jika role `APPROVER` mencoba create customer.

Status:

```text
403 Forbidden
```

Response:

```json
{
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this resource",
  "errors": []
}
```

---

## 4. Get All Customers

### Request

```text
GET /api/v1/customers
Authorization: Bearer token-staff
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
[
  {
    "id": 1,
    "full_name": "Budi Santoso",
    "email": "budi@mail.com",
    "phone_number": "08123456789"
  }
]
```

---

## 5. Get Customer by ID

### Request

```text
GET /api/v1/customers/1
Authorization: Bearer token-staff
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "id": 1,
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

### Not Found Response

Status:

```text
404 Not Found
```

Response:

```json
{
  "code": "CUSTOMER_NOT_FOUND",
  "message": "Customer not found",
  "errors": []
}
```

---

## 6. Create Loan Application

### Request

```text
POST /api/v1/loan-applications
Authorization: Bearer token-staff
```

```json
{
  "customer_id": 1,
  "loan_amount": 5000000,
  "tenor_month": 12,
  "purpose": "Modal usaha"
}
```

### Success Response

Status:

```text
201 Created
```

Response:

```json
{
  "id": 1,
  "customer_id": 1,
  "loan_amount": 5000000,
  "tenor_month": 12,
  "purpose": "Modal usaha",
  "status": "SUBMITTED"
}
```

### Forbidden Response

Jika role `APPROVER` mencoba create loan application.

Status:

```text
403 Forbidden
```

Response:

```json
{
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this resource",
  "errors": []
}
```

---

## 7. Get All Loan Applications

### Request

```text
GET /api/v1/loan-applications
Authorization: Bearer token-approver
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
[
  {
    "id": 1,
    "customer_id": 1,
    "loan_amount": 5000000,
    "tenor_month": 12,
    "purpose": "Modal usaha",
    "status": "SUBMITTED"
  }
]
```

---

## 8. Get Loan Application by ID

### Request

```text
GET /api/v1/loan-applications/1
Authorization: Bearer token-approver
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "id": 1,
  "customer_id": 1,
  "loan_amount": 5000000,
  "tenor_month": 12,
  "purpose": "Modal usaha",
  "status": "SUBMITTED"
}
```

### Not Found Response

Status:

```text
404 Not Found
```

Response:

```json
{
  "code": "LOAN_APPLICATION_NOT_FOUND",
  "message": "Loan application not found",
  "errors": []
}
```

---

## 9. Approve Loan Application

### Request

```text
PATCH /api/v1/loan-applications/1/approve
Authorization: Bearer token-approver
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "id": 1,
  "customer_id": 1,
  "loan_amount": 5000000,
  "tenor_month": 12,
  "purpose": "Modal usaha",
  "status": "APPROVED"
}
```

### Forbidden Response

Jika role `STAFF` mencoba approve loan application.

Status:

```text
403 Forbidden
```

Response:

```json
{
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this resource",
  "errors": []
}
```

---

## 10. Reject Loan Application

### Request

```text
PATCH /api/v1/loan-applications/1/reject
Authorization: Bearer token-approver
```

### Success Response

Status:

```text
200 OK
```

Response:

```json
{
  "id": 1,
  "customer_id": 1,
  "loan_amount": 5000000,
  "tenor_month": 12,
  "purpose": "Modal usaha",
  "status": "REJECTED"
}
```

---

# Standard Error Responses

## 401 Unauthorized

Digunakan jika token kosong atau token tidak valid.

```json
{
  "code": "UNAUTHORIZED",
  "message": "Authentication is required",
  "errors": []
}
```

## 403 Forbidden

Digunakan jika user sudah login tetapi tidak punya akses.

```json
{
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this resource",
  "errors": []
}
```

## 404 Not Found

Digunakan jika data tidak ditemukan.

```json
{
  "code": "NOT_FOUND",
  "message": "Data not found",
  "errors": []
}
```

---

# Suggested Structure

```text
src/main/java/com/example/training/
├── TrainingApplication.java
├── controller/
│   ├── AuthController.java
│   ├── CustomerController.java
│   └── LoanApplicationController.java
├── service/
│   ├── AuthService.java
│   ├── CustomerService.java
│   └── LoanApplicationService.java
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── UserResponse.java
│   ├── CreateCustomerRequest.java
│   ├── CustomerResponse.java
│   ├── CreateLoanApplicationRequest.java
│   ├── LoanApplicationResponse.java
│   └── ErrorResponse.java
├── model/
│   ├── User.java
│   ├── Customer.java
│   └── LoanApplication.java
└── security/
    ├── AuthContext.java
    ├── AuthUtil.java
    └── RoleValidator.java
```

# Tasks

1. Lanjutkan project dari exercise sebelumnya.
2. Buat model `User`.
3. Buat dummy user `admin`, `staff`, dan `approver`.
4. Buat DTO `LoginRequest`.
5. Buat DTO `LoginResponse`.
6. Buat DTO `UserResponse`.
7. Buat `AuthService`.
8. Buat `AuthController`.
9. Buat endpoint `POST /api/v1/auth/login`.
10. Buat endpoint `GET /api/v1/auth/me`.
11. Buat helper untuk membaca token dari header `Authorization`.
12. Buat validasi token sederhana.
13. Buat validasi role sederhana.
14. Proteksi Customer API menggunakan token.
15. Buat model `LoanApplication`.
16. Buat DTO `CreateLoanApplicationRequest`.
17. Buat DTO `LoanApplicationResponse`.
18. Buat `LoanApplicationService`.
19. Buat `LoanApplicationController`.
20. Buat endpoint Loan Application API.
21. Terapkan authorization sesuai role.
22. Return `401 Unauthorized` jika token kosong atau invalid.
23. Return `403 Forbidden` jika role tidak punya akses.
24. Return `404 Not Found` jika data tidak ditemukan.
25. Test semua endpoint menggunakan Postman.
26. Push ke fork dan buat Pull Request.

# Acceptance Criteria

* [ ] Application bisa berjalan di `localhost:8080`.
* [ ] Login admin menghasilkan `token-admin`.
* [ ] Login staff menghasilkan `token-staff`.
* [ ] Login approver menghasilkan `token-approver`.
* [ ] Login salah menghasilkan `401 Unauthorized`.
* [ ] Request tanpa token menghasilkan `401 Unauthorized`.
* [ ] Request dengan token tidak valid menghasilkan `401 Unauthorized`.
* [ ] `GET /api/v1/auth/me` berjalan.
* [ ] Customer API tetap berjalan.
* [ ] `LoanApplicationService` berhasil dibuat.
* [ ] `POST /api/v1/loan-applications` berjalan.
* [ ] `GET /api/v1/loan-applications` berjalan.
* [ ] `GET /api/v1/loan-applications/{id}` berjalan.
* [ ] `PATCH /api/v1/loan-applications/{id}/approve` berjalan.
* [ ] `PATCH /api/v1/loan-applications/{id}/reject` berjalan.
* [ ] Staff bisa create loan application.
* [ ] Staff tidak bisa approve loan application.
* [ ] Approver bisa approve loan application.
* [ ] Approver bisa reject loan application.
* [ ] Approver tidak bisa create customer.
* [ ] Admin bisa mengakses semua endpoint.
* [ ] Akses tanpa permission menghasilkan `403 Forbidden`.
* [ ] Response JSON menggunakan `snake_case`.
* [ ] Controller tidak berisi business logic utama.
* [ ] Service berisi business logic.
* [ ] Data disimpan di memory.
* [ ] Tidak menggunakan database.
* [ ] Pull Request dibuat ke branch `master`.

# Optional Challenge

Jika tugas utama sudah selesai, coba tambahkan:

* `GET /api/v1/loan-applications?status=SUBMITTED`
* `GET /api/v1/loan-applications?customer_id=1`
* `PATCH /api/v1/loan-applications/{id}/cancel`
* Role baru: `MANAGER`
* Manager hanya boleh approve loan application di atas nominal tertentu.

```

Ini lebih clean karena request/response dipisah per endpoint, dan error response standar juga jelas.
```
