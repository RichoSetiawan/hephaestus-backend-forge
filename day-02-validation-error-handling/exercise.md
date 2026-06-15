# Exercise - Validation & Error Handling

## Objective

Peserta mampu meningkatkan Customer REST API dari Day 1 dengan menambahkan request validation dan centralized error handling.

## Case

Lanjutkan Customer REST API dari Day 1.

Endpoint existing:

- `POST /api/v1/customers`
- `GET /api/v1/customers/{id}`
- `GET /api/v1/customers`

Pada Day 2, API harus:

- Menolak request tidak valid.
- Mengembalikan error response standar.
- Mengembalikan 404 jika customer tidak ditemukan.
- Tidak membocorkan detail internal error.

## Task 1 - Add Validation Dependency

Tambahkan dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## Task 2 - Add Validation to CreateCustomerRequest

Rules:

- `full_name` wajib diisi.
- `full_name` maksimal 100 karakter.
- `email` wajib diisi.
- `email` harus format email valid.
- `phone_number` wajib diisi.
- `phone_number` minimal 10 karakter.

## Task 3 - Add @Valid in Controller

Update create customer endpoint agar request divalidasi.

## Task 4 - Create FieldErrorResponse

Fields:

- `field`
- `message`

## Task 5 - Create ErrorResponse

Fields:

- `code`
- `message`
- `errors`

## Task 6 - Create CustomerNotFoundException

Gunakan exception ini ketika customer id tidak ditemukan.

## Task 7 - Update CustomerService

Jika customer tidak ditemukan, throw `CustomerNotFoundException`.

## Task 8 - Create GlobalExceptionHandler

Handle:

- `MethodArgumentNotValidException` -> `400 Bad Request`
- `CustomerNotFoundException` -> `404 Not Found`
- `Exception` -> `500 Internal Server Error`

## Expected Error Response

Validation error:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid request",
  "errors": [
    {
      "field": "email",
      "message": "email format is invalid"
    }
  ]
}
```

Customer not found:

```json
{
  "code": "CUSTOMER_NOT_FOUND",
  "message": "Customer not found with id: 999",
  "errors": []
}
```

Internal server error:

```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "Unexpected error occurred",
  "errors": []
}
```

## Suggested Package Structure

```text
src/main/java/com/example/training/
├── controller/
│   └── CustomerController.java
├── service/
│   └── CustomerService.java
├── dto/
│   ├── CreateCustomerRequest.java
│   ├── CustomerResponse.java
│   ├── ErrorResponse.java
│   └── FieldErrorResponse.java
├── exception/
│   ├── CustomerNotFoundException.java
│   └── GlobalExceptionHandler.java
└── model/
    └── Customer.java
```

## Technical Rules

- Java 8 compatible.
- Spring Boot 2.x compatible.
- Jangan gunakan database.
- Gunakan in-memory `Map`.
- Gunakan `@Valid`.
- Gunakan Bean Validation annotations.
- Gunakan `@ControllerAdvice`.
- Gunakan `@ExceptionHandler`.
- Gunakan `ResponseEntity`.
- Gunakan `snake_case` JSON.
- Gunakan `camelCase` Java fields.
- Jangan taruh logic error handling di Controller.
- Jangan expose stack trace ke client.

## Postman Test Cases

### Test 1 - Valid Request

```text
POST /api/v1/customers
```

```json
{
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

Expected:

```text
201 Created
```

### Test 2 - Empty full_name

```json
{
  "full_name": "",
  "email": "budi@mail.com",
  "phone_number": "08123456789"
}
```

Expected:

```text
400 Bad Request
```

### Test 3 - Invalid email

```json
{
  "full_name": "Budi Santoso",
  "email": "wrong-email",
  "phone_number": "08123456789"
}
```

Expected:

```text
400 Bad Request
```

### Test 4 - Empty phone_number

```json
{
  "full_name": "Budi Santoso",
  "email": "budi@mail.com",
  "phone_number": ""
}
```

Expected:

```text
400 Bad Request
```

### Test 5 - Customer Not Found

```text
GET /api/v1/customers/999
```

Expected:

```text
404 Not Found
```

## Acceptance Criteria

- [ ] `POST /api/v1/customers` returns 201 for valid request.
- [ ] `POST /api/v1/customers` returns 400 for invalid request.
- [ ] Invalid request returns code `VALIDATION_ERROR`.
- [ ] Error response includes field-level errors.
- [ ] `GET /api/v1/customers/{id}` returns 404 if customer is not found.
- [ ] Customer not found returns code `CUSTOMER_NOT_FOUND`.
- [ ] Unexpected error returns 500 with generic message.
- [ ] Controller remains clean.
- [ ] Error handling is centralized in `GlobalExceptionHandler`.
- [ ] No database is used.
- [ ] Code can run locally.

## Optional Challenge

- Add validation for `full_name` minimum length 3.
- Add Update Customer endpoint with validation.
- Add Delete Customer endpoint with not found handling.
- Convert Java field names in validation error to `snake_case`.
