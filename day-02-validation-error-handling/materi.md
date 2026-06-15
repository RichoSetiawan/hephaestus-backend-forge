# Materi - Validation & Error Handling

## 1. Recap Day 1

Pada Day 1, kita sudah membuat Customer REST API sederhana.

API tersebut bisa:

- Create customer.
- Get customer by id.
- Get customer list.

Data masih disimpan di memory menggunakan `Map`.

Namun API Day 1 masih punya beberapa kelemahan:

- Menerima request kosong.
- Menerima email tidak valid.
- Error response belum konsisten.
- Customer not found belum ditangani dengan rapi.

Day 2 akan memperbaiki hal tersebut dengan validation dan error handling.

## 2. Kenapa Validation Penting?

Validation adalah proses memastikan data yang masuk sesuai aturan sebelum diproses oleh aplikasi.

Validation penting karena:

- Menjaga kualitas data.
- Mencegah proses bisnis berjalan dengan data salah.
- Mengurangi bug.
- Membuat API lebih aman.
- Membuat response lebih jelas untuk client.

Contoh request tidak valid:

```json
{
  "full_name": "",
  "email": "wrong-email",
  "phone_number": ""
}
```

Request tersebut harus ditolak karena:

- `full_name` kosong.
- `email` bukan format email yang valid.
- `phone_number` kosong.

Jika request seperti ini tetap diproses, data customer menjadi tidak lengkap dan bisa menyebabkan error di proses berikutnya.

## 3. Frontend Validation vs Backend Validation

Frontend validation membantu user experience. User bisa mendapat feedback lebih cepat sebelum request dikirim ke backend.

Namun frontend validation bisa dilewati oleh user teknis. Misalnya request dikirim langsung melalui Postman, curl, script, atau aplikasi lain.

Backend validation bertugas melindungi sistem. Backend adalah gerbang utama sebelum data diproses.

Kesimpulannya: backend tidak boleh percaya sepenuhnya pada frontend.

## 4. Jenis Validasi

Beberapa jenis validasi yang umum digunakan:

- Required Field Validation  
  Memastikan field wajib tidak kosong.

- Format Validation  
  Memastikan format value benar, misalnya email.

- Length Validation  
  Memastikan panjang text sesuai batas minimal atau maksimal.

- Range Validation  
  Memastikan angka berada di rentang yang benar.

- Business Validation  
  Memastikan data sesuai aturan bisnis.

Contoh:

- `full_name` tidak boleh kosong.
- `email` harus format email.
- `phone_number` minimal 10 karakter.
- `monthly_income` minimal 0.
- Customer id harus terdaftar.

## 5. Bean Validation di Spring Boot

Bean Validation membantu validasi DTO menggunakan annotation.

| Annotation | Fungsi | Contoh |
| --- | --- | --- |
| `@Valid` | Menjalankan validasi pada request object | `@Valid @RequestBody` |
| `@NotBlank` | String tidak boleh null, kosong, atau blank | `full_name` |
| `@NotNull` | Object tidak boleh null | `customer_id` |
| `@Email` | Format harus email | `email` |
| `@Size` | Panjang text/list harus sesuai | `phone_number` |
| `@Min` | Nilai minimal | `monthly_income` |
| `@Max` | Nilai maksimal | `age` |

Untuk String seperti `fullName`, biasanya gunakan `@NotBlank`, bukan `@NotNull`. `@NotNull` hanya memastikan value tidak null, tetapi string kosong `""` masih bisa lolos.

## 6. Dependency Validation

Untuk Spring Boot 2.x, tambahkan dependency berikut jika belum ada:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Dependency ini dibutuhkan agar annotation seperti `@NotBlank`, `@Email`, dan `@Valid` bisa bekerja.

## 7. Validasi di Request DTO

Contoh `CreateCustomerRequest`:

```java
public class CreateCustomerRequest {

    @JsonProperty("full_name")
    @NotBlank(message = "full_name is required")
    @Size(max = 100, message = "full_name maximum length is 100")
    private String fullName;

    @JsonProperty("email")
    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "phone_number is required")
    @Size(min = 10, message = "phone_number minimum length is 10")
    private String phoneNumber;

    // getter and setter
}
```

Penjelasan:

- `@JsonProperty` menghubungkan nama JSON dengan nama field Java.
- `@NotBlank` memastikan String tidak null, tidak kosong, dan tidak hanya spasi.
- `@Size` membatasi panjang text.
- `@Email` memastikan format email valid.
- JSON menggunakan `snake_case`, misalnya `full_name`.
- Java menggunakan `camelCase`, misalnya `fullName`.

## 8. Mengaktifkan Validasi dengan @Valid

Validasi dijalankan dengan menambahkan `@Valid` di Controller.

```java
@PostMapping
public ResponseEntity<CustomerResponse> createCustomer(
        @Valid @RequestBody CreateCustomerRequest request
) {
    CustomerResponse response = customerService.createCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Penjelasan:

- `@RequestBody` membaca JSON request body.
- `@Valid` menjalankan validasi pada `CreateCustomerRequest`.
- Jika validasi gagal, method Controller tidak lanjut ke Service.
- Spring akan melempar `MethodArgumentNotValidException`.

## 9. Apa yang Terjadi Saat Validasi Gagal?

Flow saat validasi gagal:

```text
Client sends invalid request
  ↓
Controller receives request
  ↓
@Valid checks request DTO
  ↓
Validation fails
  ↓
Spring throws MethodArgumentNotValidException
  ↓
GlobalExceptionHandler handles exception
  ↓
API returns 400 Bad Request
```

## 10. Kenapa Error Response Perlu Standar?

Tanpa standar, tiap endpoint bisa punya format error berbeda. Ini membuat frontend atau mobile app sulit membaca error.

Contoh kurang baik:

```json
{
  "error": "Invalid email"
}
```

```json
{
  "message": "full_name required"
}
```

Contoh standard response yang lebih baik:

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

Penjelasan:

- `code` adalah kode error yang bisa dibaca sistem.
- `message` adalah ringkasan error.
- `errors` berisi detail error per field.
- `field` menunjukkan field yang bermasalah.
- `message` di dalam `errors` menjelaskan masalah field tersebut.

## 11. Field-Level Error

Field-level error membantu client tahu field mana yang salah.

Contoh:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid request",
  "errors": [
    {
      "field": "full_name",
      "message": "full_name is required"
    },
    {
      "field": "email",
      "message": "email format is invalid"
    }
  ]
}
```

Dengan format ini, frontend bisa menampilkan error tepat di field yang bermasalah.

## 12. ErrorResponse DTO

Contoh DTO untuk response error:

```java
public class ErrorResponse {

    private String code;
    private String message;
    private List<FieldErrorResponse> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message, List<FieldErrorResponse> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    // getter and setter
}
```

## 13. FieldErrorResponse DTO

Contoh DTO untuk error per field:

```java
public class FieldErrorResponse {

    private String field;
    private String message;

    public FieldErrorResponse() {
    }

    public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

    // getter and setter
}
```

## 14. Apa Itu Exception?

Exception adalah kondisi error yang terjadi saat program berjalan.

Contoh:

- Request tidak valid.
- Customer tidak ditemukan.
- Null pointer.
- Koneksi database gagal.

Exception perlu ditangani agar API tetap memberikan response yang rapi ke client.

## 15. Custom Exception

Custom exception adalah exception yang dibuat sendiri untuk kasus tertentu.

Contoh `CustomerNotFoundException`:

```java
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}
```

Custom exception berguna karena:

- Lebih jelas daripada `RuntimeException` biasa.
- Mudah di-handle secara spesifik.
- Membantu menentukan HTTP status yang tepat.

## 16. Update CustomerService untuk Customer Not Found

Contoh update method `getCustomerById`:

```java
public CustomerResponse getCustomerById(Long id) {
    Customer customer = customerStorage.get(id);

    if (customer == null) {
        throw new CustomerNotFoundException(id);
    }

    return toResponse(customer);
}
```

Penjelasan:

- Jika customer ada, return response.
- Jika tidak ada, throw `CustomerNotFoundException`.
- Controller tidak perlu mengecek null.

## 17. @ControllerAdvice

`@ControllerAdvice` digunakan untuk membuat global exception handler.

Dengan `@ControllerAdvice`:

- Error dari banyak Controller bisa ditangani di satu tempat.
- Controller tetap clean.
- Format error response lebih konsisten.

## 18. @ExceptionHandler

`@ExceptionHandler` digunakan untuk menentukan method mana yang menangani exception tertentu.

Contoh:

```java
@ExceptionHandler(CustomerNotFoundException.class)
public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
    ...
}
```

Method tersebut akan dipanggil ketika terjadi `CustomerNotFoundException`.

## 19. GlobalExceptionHandler

Contoh lengkap:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> errors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(new FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Invalid request",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                "CUSTOMER_NOT_FOUND",
                ex.getMessage(),
                Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred",
                Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

Handler di atas menangani:

- `MethodArgumentNotValidException` menjadi `400 Bad Request`.
- `CustomerNotFoundException` menjadi `404 Not Found`.
- `Exception` umum menjadi `500 Internal Server Error`.

Untuk menjalankan contoh ini, pastikan import yang digunakan sesuai:

```java
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
```

## 20. 400 vs 404 vs 500

| Status | Meaning | Example |
| --- | --- | --- |
| `400 Bad Request` | Request client salah | email invalid |
| `404 Not Found` | Resource tidak ditemukan | customer id tidak ada |
| `500 Internal Server Error` | Error tidak terduga di server | null pointer |

## 21. Validation Error vs Business Error vs System Error

| Error Type | Example | HTTP Status | Code |
| --- | --- | --- | --- |
| Validation Error | email invalid | 400 | `VALIDATION_ERROR` |
| Business Error | customer not found | 404 | `CUSTOMER_NOT_FOUND` |
| System Error | unexpected null pointer | 500 | `INTERNAL_SERVER_ERROR` |

Penjelasan:

- Validation error terjadi sebelum business logic utama diproses.
- Business error terjadi karena aturan bisnis.
- System error terjadi karena masalah teknis tidak terduga.

## 22. Step-by-step Implementation

Ikuti langkah berikut:

1. Add validation dependency.
2. Add validation annotation to `CreateCustomerRequest`.
3. Add `@Valid` in `CustomerController`.
4. Create `FieldErrorResponse`.
5. Create `ErrorResponse`.
6. Create `CustomerNotFoundException`.
7. Update `CustomerService`.
8. Create `GlobalExceptionHandler`.
9. Test valid request.
10. Test invalid request.
11. Test customer not found.

## 23. Testing with Postman

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

## 24. Common Beginner Errors

Beberapa error yang sering terjadi:

- Lupa menambahkan dependency validation.
- Lupa menambahkan `@Valid` di Controller.
- Menggunakan `@NotNull` untuk String padahal lebih cocok `@NotBlank`.
- Lupa getter/setter.
- Salah import `FieldError`.
- Error response tidak konsisten.
- Customer not found masih return null.
- Stack trace bocor ke client.
- Semua error dibuat 500.

## 25. Summary

- Validasi request menjaga API dari input tidak valid.
- Bean Validation membantu validasi DTO.
- `@Valid` menjalankan validasi.
- Error response harus standar.
- Custom exception membuat error lebih jelas.
- `@ControllerAdvice` membuat error handling centralized.
- Gunakan 400 untuk invalid request, 404 untuk data tidak ditemukan, 500 untuk error tidak terduga.
