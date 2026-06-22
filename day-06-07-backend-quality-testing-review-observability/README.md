# Day 6-7 - Backend Quality: Testing, Peer Code Review & Observability

## Goal

Peserta belajar bagaimana menjaga kualitas backend setelah API mulai memiliki authentication, authorization, business logic, dan flow approval. Fokus utama adalah memastikan behavior tetap benar melalui testing, menjaga code quality melalui peer review, dan membuat sistem lebih mudah ditrace melalui structured logging dan correlation ID.

## Why This Day Is Important

- Code yang bisa jalan sekali belum tentu aman untuk berubah.
- Backend enterprise terus berubah karena requirement, bug fix, dan audit finding.
- Testing mengurangi risiko regression.
- Peer code review membantu menemukan bug tersembunyi dan menjaga maintainability.
- Logging membantu engineer memahami production issue.
- `correlation_id` membantu tracing request lintas layer.
- Log harus membantu debugging tanpa membocorkan PII, password, token, atau secret.

## Learning Objectives

- Memahami testing sebagai risk reduction dan perbedaan working code dengan trusted code.
- Memahami Given-When-Then, test pyramid, unit test, integration test, dan E2E test.
- Menggunakan JUnit 5, Mockito, mock, dan verify untuk service layer.
- Membuat test happy path, negative path, error handling, dan access logic.
- Memahami peer code review dan membuat checklist review.
- Memahami structured logging, `correlation_id`, serta level `info`, `warn`, dan `error`.
- Menjaga PII dan secret tetap aman dalam log untuk audit dan monitoring.

## Previous Context

Materi ini melanjutkan exercise **Authentication, Authorization & Loan Application API**. API sebelumnya mencakup:

- `POST /api/v1/auth/login` dan `GET /api/v1/auth/me`
- Customer API: create dan read
- Loan Application API: create, read, approve, dan reject
- Role `ADMIN`, `STAFF`, dan `APPROVER`
- Simple token authentication, response `401 Unauthorized`, serta `403 Forbidden`

## Files

| File | Description |
|---|---|
| `pretest.md` | 15 pertanyaan awal |
| `materi.md` | Materi utama testing, review, dan observability |
| `exercise.md` | Latihan praktik lanjutan dari Loan Application API |
| `posttest.md` | 10 pertanyaan evaluasi |

## Expected Output

- Peserta bisa menjelaskan testing mindset dan membuat unit test service layer.
- Peserta bisa menggunakan JUnit 5 dan Mockito untuk success/failure scenario.
- Peserta bisa membuat peer code review checklist.
- Peserta bisa menambahkan structured logging dan `correlation_id`.
- Peserta bisa membedakan `info`, `warn`, dan `error`, serta menjaga log dari raw PII dan secret.

## Not Covered Today

- Full integration test dengan Testcontainers dan full E2E automation.
- Advanced observability platform, OpenTelemetry deep dive, dan setup ELK/Loki.
- CI/CD quality gate, mutation testing, dan load testing.
