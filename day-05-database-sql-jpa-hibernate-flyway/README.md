# Day 5 - Database, SQL, JPA, Hibernate, Flyway & Query Relationship

## Goal

Peserta belajar cara menghubungkan Spring Boot REST API ke database PostgreSQL, memahami SQL dasar, menggunakan Spring Data JPA, memahami Hibernate, membuat database migration dengan Flyway, dan memahami relationship antar table seperti one-to-many, many-to-one, lazy loading, eager loading, join query, dan join fetch.

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Hibernate 6
- PostgreSQL
- PostgreSQL JDBC Driver
- Flyway
- Jakarta Persistence
- Jakarta Validation

## Why This Day Is Important

Data tidak boleh hanya disimpan di memory. Backend production biasanya menggunakan database agar data tetap tersimpan walaupun aplikasi restart.

SQL membantu mengambil dan memanipulasi data. JPA membantu developer bekerja dengan database menggunakan object Java. Hibernate adalah implementasi JPA yang umum digunakan di Spring Boot. Flyway membantu versioning perubahan struktur database.

Relationship antar table penting untuk membangun aplikasi nyata. Lazy loading, eager loading, join, dan join fetch perlu dipahami agar API tidak lambat, tidak menghasilkan query terlalu banyak, dan tidak error saat membaca data relasi.

## Learning Objectives

Setelah menyelesaikan Day 5, peserta diharapkan mampu:

- Memahami perbedaan in-memory storage dan database.
- Memahami table, row, column, primary key, foreign key.
- Memahami SQL basic: SELECT, INSERT, UPDATE, DELETE.
- Memahami WHERE, LIKE/ILIKE, ORDER BY, LIMIT.
- Memahami database driver PostgreSQL.
- Memahami konsep JPA.
- Memahami Hibernate sebagai JPA provider.
- Memahami perbedaan Jakarta Persistence dan Javax Persistence secara singkat.
- Membuat Entity.
- Membuat Repository.
- Menggunakan derived query method.
- Menggunakan `@Query` untuk JPQL.
- Menggunakan native query.
- Memahami Flyway migration.
- Membuat migration SQL.
- Memahami relationship one-to-many dan many-to-one.
- Memahami `@ManyToOne` dan `@OneToMany`.
- Memahami `FetchType.LAZY` dan `FetchType.EAGER`.
- Memahami join query.
- Memahami N+1 query problem secara sederhana.
- Memahami kapan memakai join fetch.
- Memahami transaction boundary secara basic.
- Memahami penerapan database pada case finance industry.

## What Will Be Improved From Previous Days

Previous days already covered:

- REST API basic.
- Validation and error handling.
- API contract, testing, and Swagger.
- Security, filter, authorization basic jika sudah tersedia.

Day 5 improves the API by:

- Replacing Map storage with PostgreSQL.
- Introducing database migration with Flyway.
- Using Spring Data JPA Repository.
- Using Hibernate ORM.
- Adding basic query and filter.
- Explaining relationship and join using finance case:
  - Customer
  - Loan Application
  - Repayment Schedule
  - Payment Transaction

## Files

| File | Description |
| --- | --- |
| `pretest.md` | Pertanyaan awal sebelum materi |
| `materi.md` | Materi utama Database, SQL, JPA, Hibernate, Flyway & Query Relationship |
| `exercise.md` | Latihan praktik berbasis finance industry |
| `posttest.md` | Evaluasi setelah materi |

## Expected Output

Di akhir Day 5:

- Peserta memahami kenapa database diperlukan.
- Peserta memahami cara Spring Boot terhubung ke PostgreSQL.
- Peserta memahami dependency JPA, PostgreSQL driver, dan Flyway.
- Peserta memahami Entity dan Repository.
- Peserta memahami basic query menggunakan Spring Data JPA.
- Peserta memahami Flyway migration.
- Peserta memahami relationship antar table.
- Peserta memahami lazy loading, eager loading, join, dan join fetch secara basic.
- Peserta memahami penggunaan query untuk case finance industry.

## Not Covered Today

Day 5 belum membahas:

- Database indexing advanced.
- Query optimization advanced.
- Redis caching.
- Database replication.
- Sharding.
- Advanced transaction isolation.
- Production-grade migration strategy.
- Complex reporting query.
- Full loan underwriting engine.

