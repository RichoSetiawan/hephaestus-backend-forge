# Posttest - Java Fundamental

Jawab pertanyaan berikut setelah membaca materi dan mengerjakan exercise Java Fundamental.

### 1. Apa itu variable?

Jawaban:

```text
Tulis jawaban di sini.
Variabel adalah sebuah wadah atau deklarasi di dalam memori komputer untuk menyimpan data yang nilainya dapat berubah-ubah selama program berjalan. Data ini bersifat sementara dan akan hilang ketika aplikasi dimatikan. Di Java, deklarasi variabel wajib menyertakan tipe data, dengan format: TipeData namaVariabel = nilai; Contoh: String namaLengkap = "Budi";
```

### 2. Apa perbedaan String, int, Long, dan boolean?

Jawaban:

```text
Tulis jawaban di sini.
- String: Tipe data referensi untuk menyimpan teks atau sekumpulan karakter (ditulis di antara tanda kutip dua "").
- int: Tipe data primitif untuk menyimpan angka bulat berukuran 32-bit (rentang sekitar -2 miliar hingga 2 miliar).
- Long: Tipe data (bisa primitif 'long' atau wrapper class 'Long') untuk angka bulat berukuran besar 64-bit yang melebihi kapasitas int (diakhiri huruf 'L' di Java).
- boolean: Tipe data primitif yang hanya memiliki dua nilai logis: true (benar) atau false (salah).
```

### 3. Kenapa Java menggunakan camelCase untuk variable?

Jawaban:

```text
Tulis jawaban di sini.
Java menggunakan camelCase (kata pertama huruf kecil, kata berikutnya diawali huruf kapital) karena merupakan standar konvensi resmi (Java Naming Conventions). Hal ini bertujuan untuk meningkatkan keterbacaan kode (readability) agar sesama developer Java dapat memahami struktur kode dengan mudah dan seragam.
```

### 4. Apa perbedaan class dan object?

Jawaban:

```text
Tulis jawaban di sini.
- Class adalah cetak biru (blueprint) atau template yang mendefinisikan atribut (field) dan perilaku (method) dari sesuatu. Class belum nyata di memori.
- Object adalah perwujudan nyata (instance) dari sebuah class yang dibuat menggunakan kata kunci 'new' dan sudah menempati memori untuk menyimpan data spesifik.
```

### 5. Apa itu field?

Jawaban:

```text
Tulis jawaban di sini.
Field (sering disebut atribut atau variabel anggota) adalah variabel yang dideklarasikan langsung di dalam suatu class tetapi di luar method. Fungsinya untuk menyimpan data, status, atau karakteristik yang dimiliki oleh objek dari class tersebut.
```

### 6. Apa itu method?

Jawaban:

```text
Tulis jawaban di sini.
Method adalah blok kode di dalam class yang memiliki nama dan dirancang untuk menjalankan tugas, aksi, atau perilaku tertentu. Method digunakan untuk memanipulasi data field atau melakukan komputasi, dan bisa dipanggil berulang kali.
```

### 7. Apa itu parameter?

Jawaban:

```text
Tulis jawaban di sini.
Parameter adalah variabel yang dideklarasikan di dalam tanda kurung definisi method. Fungsinya sebagai saluran input untuk menerima nilai (argumen) dari luar saat method tersebut dipanggil, sehingga isi method bisa bersifat dinamis.
```

### 8. Apa itu return value?

Jawaban:

```text
Tulis jawaban di sini.
Return value adalah nilai kembalian atau hasil akhir yang dikirimkan oleh sebuah method setelah selesai mengeksekusi kodenya kepada pihak yang memanggil method tersebut. Tipe data nilainya harus dideklarasikan di awal fungsi dan dikirim menggunakan kata kunci 'return' (kecuali method 'void' yang tidak mengembalikan nilai).
```

### 9. Apa fungsi constructor?

Jawaban:

```text
Tulis jawaban di sini.
Constructor adalah method khusus yang namanya sama persis dengan nama class dan otomatis dieksekusi pertama kali saat objek dibuat ('new'). Fungsinya untuk melakukan inisialisasi awal nilai field objek atau mempersiapkan setup sistem yang dibutuhkan objek tersebut.
```

### 10. Apa fungsi `this`?

Jawaban:

```text
Tulis jawaban di sini.
Kata kunci 'this' di Java berfungsi untuk merujuk pada objek saat ini (current instance). Paling sering digunakan di dalam constructor atau setter untuk membedakan antara nama field milik class dengan nama parameter method yang kebetulan sama (shadowing).
```

### 11. Kenapa field dibuat private?

Jawaban:

```text
Tulis jawaban di sini.
Field dibuat private untuk menerapkan prinsip enkapsulasi. Tujuannya adalah melindungi data sensitif di dalam objek agar tidak bisa diakses atau diubah secara langsung dan sembarangan dari luar class, sehingga validasi data tetap terjaga.
```

### 12. Apa fungsi getter dan setter?

Jawaban:

```text
Tulis jawaban di sini.
- Getter: Method publik yang berfungsi untuk mengambil/membaca nilai dari field yang bersifat private.
- Setter: Method publik yang berfungsi untuk mengisi, mengubah, atau memperbarui nilai field private, biasanya disertai validasi logika di dalamnya agar data yang masuk tetap valid.
```

### 13. Apa itu encapsulation?

Jawaban:

```text
Tulis jawaban di sini.
Encapsulation (pembungkusan) adalah konsep OOP untuk menyembunyikan data (field) dan detail implementasi suatu objek dari akses luar, serta hanya menyediakan akses terkontrol melalui method publik (seperti getter dan setter).
```

### 14. Apa perbedaan List dan Map?

Jawaban:

```text
Tulis jawaban di sini.
- List: Koleksi data berurutan (ordered) yang menyimpan elemen secara tunggal dan memperbolehkan adanya duplikasi data. Diakses menggunakan indeks angka (0, 1, 2...).
- Map: Koleksi data tidak berurutan yang menyimpan data dalam bentuk pasangan Key (Kunci) dan Value (Nilai). Setiap Key harus unik dan digunakan sebagai indeks pencari data Value-nya.
```

### 15. Kenapa CustomerService menggunakan Map<Long, Customer>?

Jawaban:

```text
Tulis jawaban di sini.
CustomerService menggunakan Map<Long, Customer> agar proses pencarian, pembaruan, atau penghapusan data customer berdasarkan ID (bertipe Long) dapat berjalan sangat cepat dan efisien (O(1)). Kita cukup memanggil Key ID-nya tanpa perlu melakukan perulangan (looping) satu per satu.
```

### 16. Kenapa getAllCustomers mengembalikan List<Customer>?

Jawaban:

```text
Tulis jawaban di sini.
Karena method getAllCustomers bertujuan untuk menampilkan seluruh data customer ke antarmuka pengguna (UI) atau sistem lain. Format List<Customer> sangat ideal karena datanya berurutan, mudah di-loop untuk ditampilkan, dan struktur urutannya konsisten.
```

### 17. Apa itu interface?

Jawaban:

```text
Tulis jawaban di sini.
Interface adalah sebuah kontrak atau wadah abstrak yang hanya berisi deklarasi nama-nama method tanpa implementasi (tidak ada bodi kodenya). Interface digunakan untuk mendefinisikan standar perilaku atau kemampuan yang wajib diimplementasikan oleh class lain yang mengadopsinya.
```

### 18. Apa perbedaan interface dan abstract class?

Jawaban:

```text
Tulis jawaban di sini.
- Interface: Hanya berisi deklarasi method kosong (sebelum Java 8) dan variabelnya otomatis bertipe 'public static final'. Sebuah class bisa mengimplementasikan banyak interface sekaligus (multiple inheritance).
- Abstract Class: Class setengah matang yang bisa memiliki method kosong (abstract method) sekaligus method biasa yang sudah ada kodenya (concrete method), serta bisa memiliki field biasa. Sebuah class hanya bisa mengekstend satu abstract class saja.
```

### 19. Dari exercise, jelaskan flow createCustomer.

Jawaban:

```text
Tulis jawaban di sini.
1. Method 'createCustomer' menerima input berupa objek Customer atau data inputan (seperti nama, email, dll).
2. Sistem melakukan validasi data (nama tidak kosong).
3. Jika valid, sistem memasukkan ID baru untuk customer tersebut (misal bertipe Long) dengan increment pada nilai sequence.
4. Objek Customer dimasukkan ke dalam penyimpanan data (Map) dengan ID tersebut sebagai Key-nya dan objek Customer sebagai Value-nya (`map.put(id, customer)`).
```

### 20. Bagian mana yang paling sulit?

Jawaban:

```text
Tulis jawaban di sini.
Bagian yang paling menantang adalah memahami visualisasi konsep OOP seperti perbedaan abstrak dari Interface vs Abstract Class, serta logika memanipulasi struktur data Map di dalam CustomerService saat menyinkronkan Key ID dengan objek aslinya.
```
