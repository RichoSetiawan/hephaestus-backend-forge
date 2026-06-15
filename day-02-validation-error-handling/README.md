# Day 2 - Validation & Error Handling

## Goal

Peserta belajar cara melakukan validasi request dan membuat error response yang konsisten pada REST API.

## Why This Day Is Important

API tidak boleh menerima data yang tidak valid. Walaupun frontend sudah melakukan validasi, backend tetap wajib melakukan validasi karena request bisa dikirim dari Postman, script, mobile app, atau sistem lain.

Error response juga harus mudah dipahami client. Detail internal error seperti stack trace tidak boleh bocor ke client karena bisa membingungkan pengguna dan berisiko dari sisi keamanan.

Error handling sebaiknya centralized, bukan ditulis berulang di setiap Controller. Dengan cara ini, Controller tetap clean dan format error response menjadi konsisten.

## Learning Objectives

Setelah menyelesaikan Day 2, peserta diharapkan mampu:

- Memahami request validation.
- Memahami Bean Validation.
- Menggunakan `@Valid`.
- Menggunakan `@NotBlank`, `@NotNull`, `@Email`, dan `@Size`.
- Memahami `MethodArgumentNotValidException`.
- Membuat standard error response.
- Membuat field-level error response.
- Membuat custom exception.
- Menggunakan `@ControllerAdvice`.
- Menggunakan `@ExceptionHandler`.
- Memahami perbedaan HTTP 400, 404, dan 500.
- Memahami validation error, business error, dan system error.

## What Will Be Improved From Day 1

Day 1 sudah membuat Customer REST API sederhana.

Day 2 akan memperbaiki API tersebut dengan:

- Validasi request create customer.
- Error response konsisten.
- Customer not found handling.
- Global exception handler.

## Files

| File | Description |
| --- | --- |
| `pretest.md` | Pertanyaan awal sebelum materi |
| `materi.md` | Materi utama Validation & Error Handling |
| `exercise.md` | Latihan praktik |
| `posttest.md` | Evaluasi setelah materi |

## Expected Output

Di akhir Day 2, peserta diharapkan memiliki:

- Customer API memiliki request validation.
- Invalid request menghasilkan `400 Bad Request`.
- Customer not found menghasilkan `404 Not Found`.
- Unexpected error menghasilkan `500 Internal Server Error`.
- Error response menggunakan format standar.
- Controller tetap clean.
- Error handling dilakukan di `GlobalExceptionHandler`.

## Not Covered Today

Day 2 belum membahas:

- Database validation.
- Authentication.
- Authorization.
- Logging detail.
- Monitoring.
- Distributed tracing.
