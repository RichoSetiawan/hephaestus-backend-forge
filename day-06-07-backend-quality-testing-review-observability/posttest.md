# Posttest - Backend Quality: Testing, Peer Code Review & Observability

## Objective

Posttest ini digunakan untuk mengukur pemahaman peserta setelah mempelajari testing mindset, unit testing, peer code review, structured logging, correlation ID, dan PII safety.

## Instructions

- Jawab dengan singkat dan jelas.
- Total pertanyaan: 10.

1. Kenapa testing disebut risk reduction?

Jawaban:

```text
Tulis jawaban di sini.
mencegah terjadi kesalahan di production karena ditemukan duluan di QA ataupun dev environment
```

2. Apa perbedaan working code dan trusted code?

Jawaban:

```text
Tulis jawaban di sini.
working code hanyalah program yang bisa berjalan sementara trusted code terbukti keandalannya dan bisa handle edge case
```

3. Jelaskan pola Given-When-Then.

Jawaban:

```text
Tulis jawaban di sini.
Struktur penulisan test case berbasis Behavior-Driven Development (BDD):
Given: Kondisi awal atau persiapan konteks/data dummy.
When: Aksi atau eksekusi fungsi yang sedang diuji.
Then: Verifikasi atau assertion terhadap hasil akhir yang diharapkan.
```

4. Kenapa service layer cocok untuk unit test?

Jawaban:

```text
Tulis jawaban di sini.
Karena service layer adalah tempat berkumpulnya inti bisnis logika (business logic) dan aturan validasi aplikasi, yang tidak seharusnya bergantung pada bagaimana data disimpan (Database) atau bagaimana data diterima (Controller).
```

5. Apa peran JUnit 5 dan Mockito dalam unit test?

Jawaban:

```text
Tulis jawaban di sini.
Sebagai testing framework standar di ekosistem Java untuk menstrukturkan, mengelompokkan, memicu eksekusi test case, serta menyediakan fitur assertions (seperti assertEquals, assertThrows).
```

6. Sebutkan 3 test case penting untuk `LoanApplicationService`.

Jawaban:

```text
Tulis jawaban di sini.
Skenario Positif: Pengajuan pinjaman berhasil disetujui jika skor kredit kustomer memenuhi syarat.
Skenario Negatif: Pengajuan ditolak otomatis jika kustomer memiliki outstanding loan yang melebihi batas limit.
Skenario Validasi: Melempar exception jika nominal pinjaman yang dimasukkan bernilai minus atau nol.
```

7. Apa tujuan peer code review?

Jawaban:

```text
Tulis jawaban di sini.
Menjaga standar kualitas kode, mendeteksi bug atau celah keamanan yang terlewat oleh penulis kode, serta sarana berbagi pengetahuan (knowledge sharing) antar anggota tim.
```

8. Apa itu structured logging dan kenapa penting?

Jawaban:

```text
Tulis jawaban di sini.
Praktik menulis log aplikasi ke dalam format yang terstruktur dan konsisten (biasanya JSON) bukan teks biasa (plain text), agar log tersebut mudah diparsing, diindeks, dan dianalisis oleh log management tools seperti ELK Stack atau Grafana Loki.
```

9. Apa fungsi `correlation_id` pada log dan error response?

Jawaban:

```text
Tulis jawaban di sini.
Sebuah string unik yang dilekatkan pada satu request sejak awal masuk sistem untuk melacak alur request tersebut saat melewati berbagai fungsi, service, atau microservices. Berguna mempermudah debugging log yang saling terpisah.
```

10. Sebutkan minimal 5 data yang tidak boleh ditulis mentah di log.

Jawaban:

```text
Tulis jawaban di sini.
Data Pribadi/PII (Personally Identifiable Information) dan data kredensial, seperti: Nomor NIK/KTP, nomor kartu kredit/CVV, kata sandi (password), alamat email pribadi, nomor telepon kustomer, dan access token.
```

## Reflection

Apa 3 hal utama yang kamu pahami hari ini?

```text
1.
2.
3.
```

Apa 2 hal yang masih membingungkan?

```text
1.
2.
```

Apa 1 hal yang akan kamu cek saat melakukan code review?

```text
Tulis jawaban di sini.
```
