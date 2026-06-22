# Posttest - Database, SQL, JPA, Hibernate, Flyway & Query Relationship

## Objective

Posttest ini digunakan untuk mengukur pemahaman peserta setelah mempelajari Database, SQL, JPA, Hibernate, Flyway, dan Query Relationship.

1. Apa perbedaan in-memory Map dan database?

Jawaban:

```text
Tulis jawaban di sini.
data di map disimpan di memori dan akan hilang. di database disimpan dan persistent
```

2. Kenapa database diperlukan pada aplikasi backend production?

Jawaban:

```text
Tulis jawaban di sini.
Sebagai jembatan atau penerjemah bahasa pemograman aplikasi agar dapat berkomunikasi, mengirim perintah, dan memahami respons dari engine database spesifik (seperti PostgreSQL, MySQL).
```

3. Apa fungsi SELECT, INSERT, UPDATE, dan DELETE pada SQL?

Jawaban:

```text
Tulis jawaban di sini.
Select berfungsi Untuk mengambil atau menampilkan data dari satu atau beberapa tabel dalam database.
Insert berfungsi Untuk memasukkan atau menambahkan baris data baru ke dalam tabel.
Update berfunsgi Untuk mengubah atau memperbarui data yang sudah ada di dalam tabel berdasarkan kondisi tertentu.
Delete berfungsi Untuk menghapus satu atau lebih baris data dari dalam tabel.
```

4. Apa fungsi WHERE dan ILIKE di PostgreSQL?

Jawaban:

```text
Tulis jawaban di sini.
Where Untuk menyaring (filter) baris data yang akan diambil, diubah, atau dihapus berdasarkan kondisi yang ditentukan.
ILIKE bersifat case-insensitive (tidak membedakan huruf besar/kecil) dan mencari data yang contains atau mengandung kata kunci yang ingin dicari.
```

5. Apa itu primary key dan foreign key?

Jawaban:

```text
Tulis jawaban di sini.
Primary key adalah Kolom atau kombinasi kolom yang nilainya unik untuk setiap baris dalam tabel, digunakan sebagai identitas utama pembeda antar record.
Foreign key adalah Kolom dalam suatu tabel yang merujuk ke Primary Key di tabel lain, berfungsi untuk membangun dan menjaga hubungan (relationship) antar tabel.
```

6. Apa itu JPA dan Hibernate, serta apa perbedaannya?

Jawaban:

```text
Tulis jawaban di sini.
Jpa adalah Jakarta Persistence (dulu Java Persistence API), yaitu sebuah spesifikasi atau standar resmi di Java untuk mengelola data relasional menggunakan Object-Relational Mapping (ORM).
Hibernate adalah Salah satu framework ORM populer di Java yang bertindak sebagai implementasi konkret (provider) dari spesifikasi JPA.
Perbedaannya JPA adalah blueprint/antarmuka (interface & aturan), sedangkan Hibernate adalah mesin/realisasi fisik (class & code) yang menjalankan aturan dari JPA tersebut.
```

7. Apa itu Entity dan apa fungsi anotasi `@Entity`, `@Id`, serta `@GeneratedValue`?

Jawaban:

```text
Tulis jawaban di sini.
Entity adalah Sebuah class Java biasa (POJO) yang dipetakan secara langsung ke sebuah tabel di dalam database.
@Entity adalah Annotation untuk memberi tahu JPA/Hibernate bahwa class tersebut adalah sebuah entitas yang harus dipetakan ke tabel database.
@Id adalah Annotation untuk menandai sebuah field di dalam class entitas sebagai Primary Key dari tabel tersebut.
@GeneratedValue adalah Annotation untuk menentukan strategi pembuatan nilai Primary Key secara otomatis (misal: AUTO_INCREMENT atau UUID).
```

8. Apa fungsi `@Table` dan `@Column`?

Jawaban:

```text
Tulis jawaban di sini.
@Table adalah Annotation opsional untuk menentukan konfigurasi spesifik tabel di database, seperti mengubah nama tabel jika berbeda dengan nama class Java-nya.
@Column adalah Annotation yang digunakan untuk menentukan referensi terhadap entity untuk dipetakan kepada table yang ada di database dengan memberikan name dan mendefinisikan column atau kolom yang direpresentasikan oleh database.
```

9. Apa itu Repository dan apa manfaat `JpaRepository`?

Jawaban:

```text
Tulis jawaban di sini.
Repository adalah Lapisan (layer) dalam arsitektur aplikasi yang bertugas menangani operasi akses data (CRUD) ke database, memisahkan logika bisnis dari logika database.
JpaRepository adalah Interface dari Spring Data JPA yang menyediakan method-method standar penanganan database (save, findAll, delete, dll.) siap pakai tanpa perlu menulis query manual.
```

10. Apa itu derived query method? Berikan contoh method untuk mencari customer berdasarkan email.

Jawaban:

```text
Tulis jawaban di sini.
Fitur di mana Spring Data JPA akan otomatis membuat query SQL secara dinamis hanya berdasarkan pola penamaan (naming convention) method di interface Repository.
contohnya adalah
CustomerEntity findByEmail(String email);
```

11. Apa fungsi `@Query`? Jelaskan perbedaan JPQL dan native query.

Jawaban:

```text
Tulis jawaban di sini.
Annotation untuk mendefinisikan query kustom (baik JPQL maupun Native SQL) secara eksplisit di atas sebuah method repository jika penamaan derived query sudah terlalu kompleks.
JPQL berorientasi pada objek (merujuk ke nama Class Entity dan Field Java), bersifat database-agnostic. Native Query menggunakan sintaks SQL murni yang merujuk langsung ke nama Tabel dan Kolom database fisik, terikat pada engine database tertentu.
```

12. Apa itu Flyway dan kenapa database migration penting?

Jawaban:

```text
Tulis jawaban di sini.
Tool open-source untuk otomatisasi database migration yang berbasis versi, memastikan schema database di semua environment (dev, staging, prod) selalu sinkron. Karena database migration adalah proses mengelola, melacak, dan menerapkan perubahan schema database (tambah tabel, ubah kolom) secara terarah dan tercatat sepanjang siklus pengembangan aplikasi.
```

13. Apa maksud penamaan file migration seperti `V1__create_customers_table.sql`? Kenapa migration lama sebaiknya tidak diubah setelah dijalankan?

Jawaban:

```text
Tulis jawaban di sini.
Penamaan terhadap V1__create_customer_table.sql adalah representasi dari versioning V1 akan dipetakan menjadi versi yang dijalankan oleh flyway dan kita bisa membuat subversi denagn V28.1 dan akan masuk ke kolom version di table flyway_schema_migration. Sementara setelah '__' dua kali underscore adalah description dari flyway yang dijalankan. Alasan mengapa migration lama tidak diubah setelah dijalankan karena untuk menghindari error saat pengecekan flyway yang melakukan checksum bila berbeda akan mengembalikan error dan gagal build. Selain itu sangat direkomendasikan bahwa flyway dijalankan sekali dan tidak diubah untuk menjaga konsistensi migrasi dan perubahan database bila ada perubahan baru best practicenya adalah membuat file baru.
```

14. Jelaskan relationship one-to-many dan many-to-one dengan contoh Customer dan Order.

Jawaban:

```text
Tulis jawaban di sini.
One to many adalah Hubungan di mana satu record di Tabel A dapat terhubung dengan banyak record di Tabel B (contoh: 1 Customer punya Banyak Loan Applications). Sementara Many to one adalah Sudut pandang kebalikan dari one-to-many, di mana banyak record di Tabel B merujuk pada satu record yang sama di Tabel A (contoh: Banyak Loan Applications milik 1 Customer). Contoh implementasinya pada kasus customer dan order adalah customer adalah one to many, customer sendiri unik dan hanya satu namun bisa memiliki banyak order atau melakukan order. sementara order adalah many to one karena order bisa banyak struk dan transaksi tapi hanya dimiliki atau dilakukan satu aktor customer.
```

15. Apa fungsi `@ManyToOne`, `@OneToMany`, dan `@JoinColumn`?

Jawaban:

```text
Tulis jawaban di sini.
@ManyToOne Annotation di JPA pada sisi entitas "Many" untuk mendefinisikan relasi objek bersangkutan ke entitas tunggal ("One"), bertindak sebagai pemilik foreign key pemilik hubungan.
@OneToMany Annotation di JPA untuk mendefinisikan relasi ke sebuah Collection objek (List/Set) dari entitas lain.
@JoinColumn berfungsi sebagai Annotation di JPA yang memiliki fungsi sebagai key identifier untuk pengenal atau foreign key di table tersebut yang menghubungkan tabel lain sebagai join.
```

16. Apa perbedaan lazy loading dan eager loading? Kenapa `FetchType.LAZY` sering lebih aman sebagai default?

Jawaban:

```text
Tulis jawaban di sini.
Lazy loading adalah Strategi fetch data di mana objek relasi tidak akan diambil dari database sampai objek tersebut benar-benar diakses secara eksplisit melalui kode (via getter).
Eager loading Strategi fetch data di mana objek yang berelasi akan langsung ikut diambil dari database secara bersamaan pada saat objek utama di-load.
Karena default dari annotation dari ManyToOne adalah Eager dan akan membuat memori aplikasi termakan sangat banyak yang membuat performa aplikasi menurun dan berpotensi membuat aplikasi crash karena problem (N+1) query.
```

17. Apa itu SQL join? Jelaskan perbedaan `INNER JOIN` dan `LEFT JOIN`.

Jawaban:

```text
Tulis jawaban di sini.
Operasi SQL untuk menggabungkan baris dari dua atau lebih tabel berdasarkan kolom yang saling berhubungan (biasanya PK dan FK).
INNER JOIN hanya mengembalikan baris yang memiliki kecocokan di kedua tabel. LEFT JOIN mengembalikan semua baris dari tabel kiri, plus data yang cocok dari tabel kanan (jika tidak ada yang cocok, kolom tabel kanan diisi NULL).
```

18. Apa itu N+1 query problem dan bagaimana cara sederhana menguranginya?

Jawaban:

```text
Tulis jawaban di sini.
Masalah performa di mana aplikasi mengeksekusi 1 query awal untuk mengambil list data utama, lalu menjalankan N buah query tambahan secara terpisah hanya untuk mengambil data relasi dari masing-masing baris utama tersebut. Dengan mendefinisikan Lazy Loading sesuai dengan jumlah penarikan data yang dilakukan
```

19. Kenapa Entity sebaiknya tidak langsung dikembalikan sebagai API response? Apa manfaat DTO?

Jawaban:

```text
Tulis jawaban di sini.
Karena entity adalah representasi dari tabel asli database yang dimiliki sebuah aplikasi bila dikembalikan atau langsung diakses akan membuat aplikasi menjadi lebih rentan dan membuat mengirimkan data yang tidak perlu ke user. DTO berfungsi sebagai jembatan yang melindungi atau layer diatas entity yang menerima data terlebih dahulu dan akan diteruskan ke service barulah akan disesuaikan dan di passing ke entity dan bisa melanjutkan proses bisnis dengan lebih aman serta membalikkan respon yang lebih fleksibel untuk diberikan ke customer menghindari memberikan field yang confidential atau rentan ke FE sebagai respon aplikasi.
```

20. Apa fungsi `@Transactional` dan kapan menggunakan `@Transactional(readOnly = true)`?

Jawaban:

```text
Tulis jawaban di sini.
@Transactional adalah annotation yang berfunsgi untuk membuat komunikasi dan proses logika aplikasi menjadi lebih terarah dan aman. karena Transactional menerapkan ACID dan akan membatalkan seluruh request atau proses bila terdapat sebuah proses yang gagal. Transactional readOnly = true membuat sebuah komunikasi atau proses logika service di Backend hanya bisa melakukan read dan tidak bisa melakukan write atau update untuk menghindari celah keamanan aplikasi dan menjaga konsistensi agar tidak terjadi kesalahan melakukan write pada fungsi yang hanya melakukan get
```

## Reflection

Apa 3 hal utama yang kamu pahami hari ini?

```text
1. Melakukan inisialisasi dan setup docker
2. ORM
3. Lazy Loading dan Eager Loading
```

Apa 2 hal yang masih membingungkan?

```text
1. Docker
2. Melakukan best practice
```

Apa 1 pertanyaan untuk mentor?

```text
Tulis pertanyaan di sini.
Bagaimana melakukan best practice untuk menerapkan lazy loading
```
