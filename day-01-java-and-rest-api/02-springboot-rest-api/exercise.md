# Exercise - Spring Boot REST API

## Objective

Peserta dapat membuat Customer REST API sederhana menggunakan Spring Boot.

## Case

Buat Customer Management API.

## Endpoints

1. `POST /api/v1/customers`
2. `GET /api/v1/customers/{id}`
3. `GET /api/v1/customers`

## Technical Requirements

- Java 8 compatible.
- Menggunakan Spring Boot.
- Menggunakan dependency Spring Web.
- Tidak menggunakan database.
- In-memory storage menggunakan `Map`.
- Menggunakan Controller, Service, DTO, dan Model.
- JSON menggunakan `snake_case`.
- Java menggunakan `camelCase`.
- Menggunakan `@JsonProperty` untuk mapping `snake_case`.
- Menggunakan `ResponseEntity` untuk status code.
- `POST` mengembalikan `201 Created`.
- `GET` mengembalikan `200 OK`.

## Request JSON

```json
{
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

## Response JSON

```json
{
  "id": 1,
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

## Suggested Structure

```text
src/main/java/com/example/training/
├── TrainingApplication.java
├── controller/
│   └── CustomerController.java
├── service/
│   └── CustomerService.java
├── dto/
│   ├── CreateCustomerRequest.java
│   └── CustomerResponse.java
└── model/
    └── Customer.java
```

## Tasks

1. Generate project dari Spring Initializr.
2. Tambahkan dependency Spring Web.
3. Jalankan project.
4. Buat package `controller`, `service`, `dto`, dan `model`.
5. Buat model `Customer`.
6. Buat DTO `CreateCustomerRequest` dan `CustomerResponse`.
7. Buat `CustomerService`.
8. Buat `CustomerController`.
9. Test menggunakan Postman.
10. Push ke fork dan buat Pull Request.

## Endpoint Details

### 1. Create Customer

```text
POST /api/v1/customers
```

Expected status:

```text
201 Created
```

### 2. Get Customer by ID

```text
GET /api/v1/customers/{id}
```

Expected status:

```text
200 OK
```

### 3. Get All Customers

```text
GET /api/v1/customers
```

Expected status:

```text
200 OK
```

## Acceptance Criteria

- [ ] Application bisa berjalan di `localhost:8080`.
- [ ] `POST /api/v1/customers` berjalan.
- [ ] `GET /api/v1/customers/{id}` berjalan.
- [ ] `GET /api/v1/customers` berjalan.
- [ ] Response JSON menggunakan `snake_case`.
- [ ] Controller tidak berisi business logic utama.
- [ ] Service berisi business logic.
- [ ] Data disimpan di memory.
- [ ] Tidak menggunakan database.
- [ ] Pull Request dibuat ke branch `master`.

## Optional Challenge

Jika tugas utama sudah selesai, coba tambahkan:

- `DELETE /api/v1/customers/{id}`
- `PUT /api/v1/customers/{id}`
- Search sederhana: `GET /api/v1/customers?name=budi`
