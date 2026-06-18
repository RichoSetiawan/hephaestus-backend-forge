# Pretest - Authentication, Authorization & RBAC

## Objective

Pretest ini digunakan untuk mengukur pemahaman awal peserta tentang authentication, authorization, JWT, RBAC, dan access control pada backend finance.

## Instructions

- Jawab dengan singkat dan jelas.
- Tidak perlu membuka dokumentasi.
- Tidak dinilai hanya dari benar atau salah, tetapi dari cara berpikir.
- Estimasi waktu: 20-30 menit.

## Section A - Security Mindset

### 1. Kenapa access control penting dalam sistem finance?

Jawaban:

```text
Tulis jawaban di sini.
Melindungi dana nasabah, mencegah transaksi ilegal atau penyalahgunaan kewenangan, dan mematuhi regulasi hukum perbankan/OJK
```

### 2. Apa risiko jika semua user bisa mengakses semua data?

Jawaban:

```text
Tulis jawaban di sini.
Kebocoran data finansial, manipulasi saldo, fraud internal, dan hancurnya reputasi perusahaan
```

### 3. Apa contoh data atau action yang harus dibatasi pada sistem loan?

Jawaban:

```text
Tulis jawaban di sini.
Persetujuan nominal pinjaman (loan approval), pencairan dana (disbursement), dan pengubahan suku bunga
```

### 4. Kenapa backend tidak boleh hanya mengandalkan frontend untuk membatasi akses?

Jawaban:

```text
Tulis jawaban di sini.
Frontend berjalan di sisi klien dan kodenya bisa dimanipulasi, dilewati, atau ditembus menggunakan alat seperti Postman/cURL
```

### 5. Apa maksud deny by default?

Jawaban:

```text
Tulis jawaban di sini.
Konsep keamanan di mana semua akses ditolak secara otomatis, kecuali jika secara eksplisit diberikan izin
```

## Section B - Authentication

### 6. Apa itu authentication?

Jawaban:

```text
Tulis jawaban di sini.
Proses pembuktian atau verifikasi identitas pengguna untuk memastikan bahwa mereka benar-benar orang yang mereka klaim
```

### 7. Apa contoh proses authentication?

Jawaban:

```text
Tulis jawaban di sini.
Memasukkan kombinasi username dan password, validasi OTP, atau pemindaian sidik jari (biometrik)
```

### 8. Apa itu token-based authentication?

Jawaban:

```text
Tulis jawaban di sini.
Metode autentikasi di mana server menghasilkan string unik (token) setelah login sukses, yang digunakan klien untuk request berikutnya
```

### 9. Apa fungsi Authorization header?

Jawaban:

```text
Tulis jawaban di sini.
Bagian dari HTTP header untuk mengirimkan kredensial atau token autentikasi dari klien ke server
```

### 10. Apa arti format Bearer token?

Jawaban:

```text
Tulis jawaban di sini.
Format yang menyatakan bahwa pembawa (bearer) token tersebut berhak diberikan akses tanpa perlu verifikasi tambahan
```

### 11. Apa yang harus dilakukan backend saat menerima token?

Jawaban:

```text
Tulis jawaban di sini.
Memvalidasi integritas struktur token, memeriksa kecocokan tanda tangan digital (signature), dan memastikan masa berlakunya belum habis
```

### 12. Apa yang terjadi jika token tidak ada?

Jawaban:

```text
Tulis jawaban di sini.
Backend menolak request dan mengembalikan respons error Unauthorized
```

### 13. Apa yang terjadi jika token invalid?

Jawaban:

```text
Tulis jawaban di sini.
Backend mendeteksi adanya manipulasi atau kesalahan format, lalu langsung menolak request tersebut
```

### 14. Apa yang terjadi jika token expired?

Jawaban:

```text
Tulis jawaban di sini.
Backend menolak akses karena masa berlaku token telah habis dan meminta klien melakukan refresh token atau login ulang
```

## Section C - JWT

### 15. Apa itu JWT?

Jawaban:

```text
Tulis jawaban di sini.
JSON Web Token; standar terbuka berbentuk string ringkas untuk mengirimkan informasi terenkripsi/terproteksi antar pihak secara aman
```

### 16. Apa itu claim pada JWT?

Jawaban:

```text
Tulis jawaban di sini.
Potongan informasi atau data konstanta yang disimpan di dalam payload JWT, berbentuk pasangan key-value
```

### 17. Sebutkan contoh claim yang umum ada di JWT.

Jawaban:

```text
Tulis jawaban di sini.
sub (user ID), exp (waktu kedaluwarsa), iat (waktu pembuatan), dan role
```

### 18. Kenapa payload JWT tidak boleh dipercaya sebelum signature divalidasi?

Jawaban:

```text
Tulis jawaban di sini.
Karena payload JWT hanya di-encode (Base64) dan bisa dibaca atau diubah dengan mudah oleh siapa saja di sisi klien
```

### 19. Data apa saja yang tidak boleh disimpan di JWT?

Jawaban:

```text
Tulis jawaban di sini.
Data sensitif seperti password, nomor kartu kredit, PIN, atau saldo akun
```

### 20. Kenapa JWT harus punya expiry?

Jawaban:

```text
Tulis jawaban di sini.
Meminimalkan jendela waktu penyalahgunaan jika token tersebut sempat dicuri oleh pihak yang tidak bertanggung jawab
```

### 21. Apa perbedaan access token dan refresh token?

Jawaban:

```text
Tulis jawaban di sini.
Access token berumur pendek untuk mengakses resource, sedangkan refresh token berumur panjang hanya untuk mendapatkan access token baru
```

## Section D - Authorization and RBAC

### 22. Apa itu authorization?

Jawaban:

```text
Tulis jawaban di sini.
Proses pengecekan apakah pengguna yang sudah terautentikasi memiliki hak akses untuk melakukan tindakan atau mengakses resource tertentu
```

### 23. Apa perbedaan authentication dan authorization?

Jawaban:

```text
Tulis jawaban di sini.
Authentication adalah validasi siapa Anda, sedangkan authorization adalah validasi apa yang boleh Anda lakukan
```

### 24. Apa itu RBAC?

Jawaban:

```text
Tulis jawaban di sini.
Role-Based Access Control; metode pembatasan akses sistem berdasarkan peran atau jabatan pengguna di dalam organisasi
```

### 25. Apa itu role?

Jawaban:

```text
Tulis jawaban di sini.
Kumpulan atau grup hak akses yang disematkan pada pengguna (contoh: Admin, Teller, Risk Analyst)
```

### 26. Apa itu permission?

Jawaban:

```text
Tulis jawaban di sini.
Izin spesifik untuk melakukan aksi tunggal terhadap suatu resource (contoh: loan:create, loan:approve)
```

### 27. Apa contoh role dalam loan system?

Jawaban:

```text
Tulis jawaban di sini.
Loan_Officer, Risk_Reviewer, dan Borrower (Nasabah)
```

### 28. Apa contoh permission dalam loan system?

Jawaban:

```text
Tulis jawaban di sini.
approve_loan_application, view_credit_score, dan disburse_funds
```

### 29. Kenapa user yang sudah login belum tentu boleh melakukan semua action?

Jawaban:

```text
Tulis jawaban di sini.
Karena setiap pengguna memiliki fungsi dan tanggung jawab berbeda yang dibatasi oleh hak authorization mereka
```

## Section E - Resource-Level Authorization

### 30. Apa itu resource-level authorization?

Jawaban:

```text
Tulis jawaban di sini.
Pengecekan hak akses yang lebih spesifik hingga ke level baris data atau kepemilikan objek tertentu, bukan cuma level fitur
```

### 31. Kenapa role check saja tidak cukup?

Jawaban:

```text
Tulis jawaban di sini.
Karena dua pengguna dengan role yang sama (misal: sesama nasabah) tidak boleh saling melihat atau mengubah data pribadi satu sama lain
```

### 32. Apa itu ownership check?

Jawaban:

```text
Tulis jawaban di sini.
Validasi backend untuk memastikan bahwa data/resource yang diminta memang benar-benar milik pengguna yang sedang login
```

### 33. Apa contoh kasus customer melihat loan milik customer lain?

Jawaban:

```text
Tulis jawaban di sini.
Nasabah A mengganti ID loan di URL dari /api/loan/101 menjadi /api/loan/102 dan berhasil melihat data milik nasabah B
```

### 34. Apa risiko IDOR?

Jawaban:

```text
Tulis jawaban di sini.
Insecure Direct Object Reference dapat mengakibatkan kebocoran data massal, manipulasi data ilegal, dan pelanggaran privasi berat
```

### 35. Bagaimana cara backend mencegah data leakage karena salah akses resource?

Jawaban:

```text
Tulis jawaban di sini.
Selalu sertakan parameter user ID dari token dalam klausa query database (misal: WHERE loan_id = :id AND user_id = :current_user_id)
```

## Section F - 401, 403, and Audit

### 36. Kapan menggunakan 401 Unauthorized?

Jawaban:

```text
Tulis jawaban di sini.
Saat pengguna belum login, token absen, atau token yang dikirimkan tidak valid/kedaluwarsa
```

### 37. Kapan menggunakan 403 Forbidden?

Jawaban:

```text
Tulis jawaban di sini.
Saat pengguna sudah login secara sah, tetapi tidak memiliki izin (permission) untuk mengakses resource atau fitur yang diminta
```

### 38. Apa bedanya 401 dan 403?

Jawaban:

```text
Tulis jawaban di sini.
Status 401 berarti identitas tidak dikenal, sedangkan status 403 berarti identitas dikenal tetapi akses ditolak
```

### 39. Kenapa access log penting?

Jawaban:

```text
Tulis jawaban di sini.
Untuk kebutuhan forensik keamanan, mendeteksi percobaan peretasan, serta melacak siapa yang melakukan perubahan data jika terjadi fraud
```

### 40. Field apa saja yang penting dalam access log?

Jawaban:

```text
Tulis jawaban di sini.
Timestamp, User ID, IP Address, HTTP Method, Request URL, Response Status Code, dan User Agent
```

## Self Assessment

| Area | Score 1-5 |
| --- | --- |
| Authentication | 5|
| Authorization | 5|
| JWT |5 |
| RBAC | 5|
| Resource ownership | 5|
| 401 vs 403 | 5|
| Audit log | 5|

## Notes

```text
Tulis bagian yang masih membingungkan.
OPENID Connect dan Authorization menggunakan resource third party seperti google
```
