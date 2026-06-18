# Materi - Authentication, Authorization & RBAC

## 1. Recap Day 2 and Day 3

Pada Day 2, kita membahas validation dan standard error handling. API tidak hanya harus menerima request yang benar, tetapi juga harus mengembalikan error response yang konsisten saat request tidak valid atau data tidak ditemukan.

Pada Day 3, kita membahas API contract, API testing, dan Swagger. API contract membantu backend dan client sepakat tentang endpoint, request, response, status code, dan error response.

Day 4 menambahkan security requirement ke API. Security error seperti `401 Unauthorized` dan `403 Forbidden` harus tetap konsisten dengan error response dari Day 2. API contract dari Day 3 juga harus menjelaskan endpoint mana yang membutuhkan token dan role tertentu.

## 2. Security Mindset in Finance Backend

Dalam sistem finance, access control adalah bagian dari risk control.

Prinsip penting:

- Tidak semua user boleh akses semua data.
- Tidak semua action boleh dilakukan semua user.
- Kesalahan akses bisa menyebabkan fraud.
- Kesalahan akses bisa menyebabkan data leak.
- Unauthorized approval bisa merugikan bisnis.
- Backend harus melakukan check, bukan hanya frontend.

Contoh:

- Agent tidak boleh approve loan.
- Customer tidak boleh melihat loan customer lain.
- User tidak boleh mengubah status application tanpa hak.
- Admin tidak boleh over-privileged tanpa review.

Frontend bisa menyembunyikan tombol approve, tetapi request masih bisa dikirim dari Postman, script, atau browser devtools. Karena itu, backend wajib menjadi penjaga utama.

## 3. System Context - Loan System

Kita gunakan contoh Loan System.

Actors:

- `customer`: membuat dan melihat pengajuan loan miliknya sendiri.
- `agent`: membantu submit atau follow up customer tertentu.
- `credit_analyst`: melakukan review dan approval.
- `supervisor`: melihat scope tim atau branch.
- `admin`: mengelola konfigurasi dan user access.

| Actor | Typical Access | Risk | Control |
| --- | --- | --- | --- |
| customer | create/view own loan | lihat data orang lain | ownership check |
| agent | submit/follow up | akses branch lain | role + scope |
| credit_analyst | review/approve | approval tidak sah | permission check |
| supervisor | view team/branch | akses terlalu luas | scope check |
| admin | manage access | over-privileged | least privilege |

## 4. Authentication vs Authorization

Authentication menjawab: siapa user ini?

Authorization menjawab: apa yang boleh user lakukan?

| Topic | Authentication | Authorization |
| --- | --- | --- |
| Question | Siapa user? | Apa yang boleh dilakukan? |
| Example | login username/password | cek role dan permission |
| Example | validasi token | cek ownership resource |
| Output | user identity | allow atau deny |
| Failure status | 401 | 403 |

User yang sudah login belum tentu boleh melakukan semua action. Misalnya agent sudah login dengan token valid, tetapi tetap tidak boleh approve loan karena action tersebut hanya untuk `credit_analyst`.

## 5. Authentication

Authentication adalah proses mengenali siapa user.

Contoh authentication:

- Login dengan username/password.
- Login dengan token.
- API key untuk partner integration.

Tanpa authentication, backend tidak tahu siapa actor di balik request. Dalam sistem finance, ini berbahaya karena backend tidak bisa membuat keputusan akses, audit, atau tracing dengan benar.

## 6. Token-Based Authentication

Modern backend sering memakai token, bukan session server-side.

Cara kerjanya:

- Token dikirim pada setiap request API.
- Format umum: `Authorization: Bearer <token>`.
- Backend memvalidasi token sebelum membaca user context.
- Jika token valid, request lanjut ke authorization.
- Jika token invalid, backend return `401 Unauthorized`.

Contoh HTTP request:

```text
GET /api/v1/loan_applications
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Bearer berarti siapa pun yang membawa token tersebut dianggap membawa credential. Karena itu token harus disimpan dan dikirim dengan aman.

## 7. Why Token-Based Authentication

Token-based authentication sering dipilih karena:

- Stateless dan lebih mudah diskalakan.
- Cocok untuk mobile app dan web frontend.
- Cocok untuk microservices jika validasi token konsisten.
- API tidak bergantung pada session memory di satu server.

Walaupun praktis, token tetap harus divalidasi dengan benar. Backend tidak boleh hanya membaca payload token lalu langsung percaya.

## 8. JWT - JSON Web Token

JWT adalah token yang membawa claim tentang user.

Common claims:

- `user_id`
- `role`
- `branch_id`
- `issuer`
- `expiry`

Contoh payload:

```json
{
  "user_id": "USR-001",
  "role": "credit_analyst",
  "branch_id": "BR-001",
  "iss": "loan-auth-service",
  "exp": 1710000000
}
```

Penjelasan:

- Payload membantu backend mengenali user.
- Jangan percaya payload sebelum signature divalidasi.
- Jangan membawa data sensitif di JWT.
- JWT harus minimal.

Catatan penting: contoh di materi ini bersifat konseptual. Hari ini kita belum membuat real JWT signing dan verification.

## 9. JWT Authentication Flow

Flow JWT secara konseptual:

```text
User login
↓
Backend validates credential
↓
Backend generates JWT
↓
Client stores token
↓
Client sends token on API request
↓
Backend validates token
↓
Backend builds authenticated user context
↓
Request continues to authorization check
```

Jika token valid, request lanjut. Jika token invalid atau expired, backend mengembalikan `401 Unauthorized`.

## 10. JWT Validation Checklist

Checklist validasi JWT:

- Signature valid.
- Token belum expired.
- Issuer sesuai.
- Audience sesuai jika digunakan.
- Algorithm aman.
- Claim penting tersedia.
- Token tidak dimodifikasi.

Jika salah satu check penting gagal, backend tidak boleh membangun user context dari token tersebut.

## 11. What JWT Should Not Contain

JWT tidak boleh berisi:

- Password.
- Sensitive customer data.
- Full customer profile.
- Internal secret.
- API key.
- Data yang sering berubah dan butuh validasi real-time.

Payload JWT mudah dibaca jika token di-decode. Signature melindungi integritas token, bukan menyembunyikan isi payload.

## 12. Token Expiry

JWT harus punya expiry.

Alasannya:

- Expiry mengurangi risiko jika token dicuri.
- Access token sebaiknya short-lived.
- Token yang tidak pernah expired adalah risiko security besar.

Jika token expired, backend harus menolak request dengan `401 Unauthorized`.

## 13. Access Token vs Refresh Token

| Item | Access Token | Refresh Token |
| --- | --- | --- |
| Purpose | Akses API | Meminta access token baru |
| Duration | Pendek | Lebih panjang |
| Sent to API | Ya | Tidak selalu |
| Storage | Aman | Lebih aman lagi |
| Risk | Bisa dipakai akses API | Bisa membuat token baru |
| Revocation | Biasanya sulit jika stateless | Perlu bisa direvoke |

Dalam production system, refresh token perlu desain khusus, penyimpanan aman, dan mekanisme revoke. Hari ini kita hanya memahami konsepnya.

## 14. Authorization

Authorization menentukan apa yang boleh dilakukan user.

Prinsipnya:

- User yang sudah login belum tentu boleh melakukan semua action.
- Authorization mengecek role, permission, resource scope, dan business policy.
- Finance backend harus deny by default untuk action berisiko.

Deny by default berarti request ditolak kecuali ada rule eksplisit yang memperbolehkan.

## 15. Authorization Layer

Authorization layer biasanya mengecek:

- Apakah user sudah login?
- Apakah role sesuai?
- Apakah permission tersedia?
- Apakah endpoint boleh diakses role tersebut?
- Apakah resource berada dalam scope user?
- Apakah action valid terhadap current state?

Contoh state check: loan hanya boleh di-approve jika statusnya `pending_review`, bukan jika sudah `rejected` atau `disbursed`.

## 16. RBAC - Role-Based Access Control

RBAC berarti akses ditentukan berdasarkan role.

Penjelasan:

- Role merepresentasikan tanggung jawab user.
- Permission menentukan action yang boleh dilakukan role.
- RBAC membuat access policy lebih jelas.
- RBAC membantu audit.

Contoh RBAC matrix:

| Role | Create Loan | Approve Loan | View Report |
| --- | --- | --- | --- |
| customer | own loan only | no | no |
| agent | assigned customer | no | limited |
| credit_analyst | no | yes | limited |
| supervisor | no | override policy | team scope |
| admin | config only | no by default | admin scope |

Admin tidak otomatis boleh melakukan semua action bisnis. Di finance backend, admin access juga harus dibatasi agar tidak menjadi over-privileged.

## 17. Endpoint Protection Example

Contoh policy endpoint:

```text
POST /api/v1/loan_applications      -> customer, agent
POST /api/v1/loan_approvals         -> credit_analyst
GET  /api/v1/loan_applications/{id} -> owner or authorized internal role
GET  /api/v1/reports/loan_summary   -> supervisor, admin
```

Setiap endpoint harus punya access policy. Policy harus terlihat di API contract atau documentation. Jangan hanya mengandalkan frontend untuk menyembunyikan tombol.

## 18. Resource-Level Authorization

Role saja tidak cukup.

Contoh:

- Customer hanya boleh melihat loan miliknya.
- Agent hanya boleh melihat customer yang di-assign.
- Supervisor hanya boleh melihat data dalam scope tim atau branch.
- Resource-level check mencegah data leakage.

Tanpa resource-level authorization, user bisa mengganti id di URL dan mencoba membaca data milik orang lain.

## 19. Role Check vs Resource Check

| Check | Question | Example |
| --- | --- | --- |
| Role check | Apakah role boleh akses endpoint? | credit_analyst boleh approve |
| Permission check | Apakah user punya action tertentu? | loan:approve |
| Resource check | Apakah data ini milik/scope user? | customer melihat loan sendiri |
| State check | Apakah action valid untuk status saat ini? | approve hanya jika pending_review |

Authorization yang baik biasanya menggabungkan beberapa check, bukan hanya satu.

## 20. Resource Ownership Example

Contoh data:

```json
{
  "authenticated_customer_id": "CUST-001",
  "requested_customer_id": "CUST-999"
}
```

Request harus ditolak meskipun user sudah login. Masalah seperti ini sering disebut IDOR atau Insecure Direct Object Reference.

Backend wajib cek ownership atau scope. Jika user valid tetapi resource bukan miliknya, gunakan `403 Forbidden`.

## 21. Common Authorization Mistakes

Kesalahan yang sering terjadi:

- Hanya cek login, tapi tidak cek role.
- Tidak cek ownership resource.
- Semua internal role dianggap boleh semua.
- Policy hanya ada di frontend.
- Endpoint baru dibuat tanpa authorization review.
- Role dikirim dari client dan langsung dipercaya.
- Skip authorization karena endpoint dianggap internal.

Role, permission, dan user context harus berasal dari sumber yang dipercaya, misalnya token yang sudah divalidasi atau data server-side.

## 22. Endpoint Security Flow

Flow security endpoint:

```text
Request masuk
↓
Check token exists
↓
Validate token
↓
Identify user
↓
Check role/permission
↓
Check resource scope
↓
Execute action
↓
Write access log
↓
Return response
```

Jika authentication gagal, return `401`. Jika authorization gagal, return `403`. Jika semua check lolos, action dijalankan.

## 23. 401 Unauthorized vs 403 Forbidden

| Status | Meaning | Example |
| --- | --- | --- |
| 401 Unauthorized | Backend belum bisa mengenali user | token tidak ada, invalid, expired |
| 403 Forbidden | User dikenali tapi tidak punya akses | role tidak cukup, resource bukan miliknya |

Gunakan `401` untuk authentication failure. Gunakan `403` untuk authorization failure.

## 24. 401 Error Response

Contoh response:

```json
{
  "status": 401,
  "error_code": "UNAUTHORIZED",
  "message": "Authentication required",
  "correlation_id": "REQ-20260424-001"
}
```

Gunakan saat token tidak ada, invalid, atau expired. Jangan bocorkan detail token ke client, misalnya signature mana yang gagal atau isi token yang bermasalah.

## 25. 403 Error Response

Contoh response:

```json
{
  "status": 403,
  "error_code": "FORBIDDEN",
  "message": "Access denied",
  "correlation_id": "REQ-20260424-002"
}
```

Gunakan saat user valid tetapi tidak punya akses. Jangan bocorkan detail policy internal di message.

## 26. Principle of Least Privilege

Principle of least privilege berarti:

- User hanya boleh punya akses minimal yang dibutuhkan.
- Jangan memberi akses berlebihan karena lebih mudah.
- Access harus sesuai tugas dan tanggung jawab.
- Privilege harus bisa direview dan dicabut.

Prinsip ini penting di finance karena akses berlebihan bisa membuka jalan ke fraud, data leak, atau approval tidak sah.

## 27. Audit Perspective

Dalam finance, sistem harus bisa menjawab:

- Siapa melakukan apa?
- Kapan action dilakukan?
- Endpoint apa yang diakses?
- Apakah access diizinkan atau ditolak?
- Correlation id apa yang terkait dengan request?

Audit log membantu investigasi, compliance, debugging, dan review access policy.

## 28. Access Log Fields

| Field | Purpose | Example | Notes |
| --- | --- | --- | --- |
| correlation_id | trace request | REQ-20260424-001 | useful for debugging |
| user_id | identify actor | USR-001 | from token |
| role | access context | credit_analyst | avoid sensitive data |
| endpoint | target API | POST /api/v1/loan_approvals | include method |
| result | allow/deny | 403 FORBIDDEN | useful for audit |
| reason | short reason | role_not_allowed | avoid leaking sensitive policy |

Field audit harus cukup untuk investigasi, tetapi tidak boleh menyimpan data sensitif secara berlebihan.

## 29. Logging Access

Prinsip logging access:

- Log request penting dengan `user_id` dan `role`.
- Log endpoint dan action.
- Log allow/deny result.
- Tambahkan `correlation_id`.
- Jangan log password.
- Jangan log token penuh.
- Jangan log sensitive customer data.

Token penuh tidak boleh masuk log karena log sering dibaca banyak pihak internal dan bisa tersimpan lama.

## 30. Integration with Day 2

Day 2 membahas standard error handling.

Day 4 menambahkan security error:

- `401 Unauthorized`
- `403 Forbidden`

Error response untuk security tetap harus konsisten. Security error juga perlu `correlation_id` untuk tracing.

## 31. Integration with Day 3

Day 3 membahas API contract dan Swagger. Day 4 menambahkan auth requirement ke contract.

Swagger/OpenAPI harus menjelaskan endpoint yang membutuhkan bearer token. Contract juga harus menjelaskan role requirement jika relevan.

Contoh API contract:

```text
Endpoint:
POST /api/v1/loan_approvals

Auth:
Required: Bearer token
Allowed role: credit_analyst
Resource check: application branch must be in analyst scope

Success:
201 Created

Errors:
401 Unauthorized - token missing/invalid/expired
403 Forbidden - role not allowed or out of scope
404 Not Found - loan application not found
```

## 32. Case Discussion - Loan Approval

Scenario:

- Endpoint: `POST /api/v1/loan_approvals`.
- Scenario: agent mencoba approve loan.
- Expected: ditolak dengan `403 Forbidden`.
- Reason: agent login valid, tetapi tidak punya permission approve.
- Audit log harus mencatat denied access.

| Scenario | Authentication | Authorization | Result |
| --- | --- | --- | --- |
| no token | invalid | - | 401 |
| agent approve | valid | not allowed | 403 |
| analyst approve | valid | allowed | 200/201 |
| wrong branch | valid | out of scope | 403 |

## 33. Simple Java Pseudo-Code

Contoh sederhana, bukan full Spring Security:

```java
public void checkCanApproveLoan(UserContext userContext, LoanApplication loanApplication) {
    if (userContext == null) {
        throw new UnauthorizedException("Authentication required");
    }

    if (!"credit_analyst".equals(userContext.getRole())) {
        throw new ForbiddenException("Access denied");
    }

    if (!userContext.getBranchId().equals(loanApplication.getBranchId())) {
        throw new ForbiddenException("Access denied");
    }
}
```

Penjelasan:

- `UserContext` berasal dari token yang sudah divalidasi.
- Role harus `credit_analyst`.
- Branch harus sesuai scope.
- Error message tidak membocorkan policy detail.

Contoh `UserContext` sederhana:

```java
public class UserContext {

    private String userId;
    private String role;
    private String branchId;

    // getter and setter
}
```

Java field menggunakan `camelCase`, sedangkan JSON contract tetap menggunakan `snake_case`.

## 34. API Security Checklist

Checklist:

- Semua endpoint penting sudah protected?
- Token divalidasi setiap request?
- Role dan permission dicek?
- Resource ownership atau scope dicek?
- 401 dan 403 sudah dipakai dengan tepat?
- Access penting sudah dilog?
- API contract mencantumkan auth requirement?
- Swagger menjelaskan bearer token jika digunakan?

Checklist ini berguna saat review endpoint baru.

## 35. Anti-Patterns

Hindari:

- Hardcode role tanpa struktur jelas.
- Skip authorization karena endpoint internal.
- Semua endpoint terbuka saat development lalu lupa dikunci.
- Trust client untuk menentukan role.
- Expose sensitive data di response.
- Expose sensitive data di log.
- JWT berisi data sensitif.
- Token tidak punya expiry.
- Semua internal user dianggap admin.

## 36. Key Takeaways

- Authentication berarti mengenali siapa user.
- Authorization berarti menentukan apa yang boleh dilakukan user.
- JWT membantu membawa user context secara stateless.
- RBAC mengontrol akses berdasarkan role.
- Role check harus dilengkapi resource-level check.
- 401 digunakan untuk authentication failure.
- 403 digunakan untuk authorization failure.
- Access control mendukung security, audit, dan compliance.
- Jangan percaya client untuk menentukan akses.
- Dokumentasikan auth requirement di API contract.
