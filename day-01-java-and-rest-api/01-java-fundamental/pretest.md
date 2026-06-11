# Pretest - Java Fundamental

## Objective

Pretest ini bertujuan mengukur pemahaman awal tentang Java Fundamental sebelum membaca materi dan mengerjakan exercise.

## Instructions

- Jawab dengan bahasa sendiri.
- Tidak perlu takut salah.
- Jangan melihat jawaban teman terlebih dahulu.
- Jika belum tahu, tulis apa yang kamu pahami saat ini.

Estimasi waktu: 20-30 menit.

## Section A - Basic Syntax

### 1. Apa itu variable?

Jawaban:

```text
Tulis jawaban di sini.
Variabel adalah sebuah deklarasi untuk menyimpan sebuah data yang akan diassign dan akan berjalan diatas memory akan hilang bila aplikasi atau program dimatikan. contoh deklarasinya di java bisa dengan format TipeData namaVariabel = assignNilai. String namaLengkap = "";
```

### 2. Apa itu data type?

Jawaban:

```text
Tulis jawaban di sini.
Data type adalah tipe data yang akan disimpan atau dikelola. Ada tipe data primitive seperti int, char, float. Namun java sudah menerapkan implementasi class pada Tipe data misalkan Integer, Double, BigDecimal, String, Float, Loang dan lain-lain. Tipe data sendiri memiliki jenis data yang bisa mereka simpan pada char hanya bisa menyimpan 1 character, int hanya bisa menyimpan angka sampai 2,148 miliar, long tipe data yang mirip int namun memiliki byte lebih besar hingga bisa menerima angka yang lebih besar, String yang bisa menyimpan ada kumpulan huruf, float yang bisa menyimpan data desimal, serta Boolean yang menyimpan data berupa true/false.
```

### 3. Sebutkan contoh tipe data untuk text, number, dan true/false.

Jawaban:

```text
Tulis jawaban di sini.
Text biasa diberikan kepada String dan Char contohnya seperti String namaLengkap = "Richo Setiawan";
number biasa diberikan pada Integer, Float ataupun BigDecimal, serta Double contohnya Float gaji = 8_000_000f;
Sementara true/false adalah tipe data Boolean yang contohnya Boolean state = false;
```

### 4. Apa perbedaan variable declaration dan assignment?

Jawaban:

```text
Tulis jawaban di sini.
Variable declaration adalah sebuah proses Deklarasi untuk mendaftarkan sebuah tipe data yang akan disimpan saat program dijalankan kalau di Java akan disimpan di heap setelah dicompile menjadi bytecode. untuk deklarasi sendiri bisa digunakan String nama;
Kalau assignment adalah memasukkan atau set sebuah nilai bisa String nama = "";
```

### 5. Apa itu camelCase?

Jawaban:

```text
Tulis jawaban di sini.
camelCase adalah format penulisan yang biasa digunakan untuk deklarasi sebuah object atau variabel, dengan huruf dari kata pertama kecil dan kata keduan serta selanjutnya besar tanpa spasi. akuMauMakan ini adalah contoh camelCase
```

## Section B - Class and Object

### 6. Apa itu class?

Jawaban:

```text
Tulis jawaban di sini.
Class bisa digambarkan sebagai koper yang menyimpan data maupun method serta property tergantung dari kebutuhan. kita bisa menuliskan Class identitas {
    String namaLengkap;
    Scanner sc = new Scanner(System.in);
    String name = sc.nextLine();
    namaLengkap = name;
}
```

### 7. Apa itu object?

Jawaban:

```text
Tulis jawaban di sini.
Object adalah sebuah inisiasi yang dibuat untuk bisa dipanggil untuk bisa di declarasikan untuk mengakses sebuah property atau method yang terdapat dalam class.
```

### 8. Apa perbedaan class dan object?

Jawaban:

```text
Tulis jawaban di sini.
Class adalah sebuah koper atau blueprint sementara Object adalah isi dari inisiasi sebuah class. Misalkan Identitas identitas = new Identitas();
```

## Section C - Field, Method, Constructor

### 9. Apa itu field?

Jawaban:

```text
Tulis jawaban di sini.
field adalah sebuah line atau property
```

### 10. Apa itu method?

Jawaban:

```text
Tulis jawaban di sini.
Method adalah sebuah cara atau logikan untuk melakukan handle data ataupun melakukan sebuah perintah.
```

### 11. Apa itu parameter?

Jawaban:

```text
Tulis jawaban di sini.
parameter adalah sebuah data yang dilemparkan kedalam sebuah method untuk dilakukan proses data misalkan void namaLengkap(String nama){} string nama adalah paramternya
```

### 12. Apa itu return value?

Jawaban:

```text
Tulis jawaban di sini.
return value adalah sebuah balikkan data setelah method dijalankan bisa saja void tidak mengembalikkan apa-apa atau mengembalikan tipe data.
```

### 13. Apa itu constructor?

Jawaban:

```text
Tulis jawaban di sini.
Constructor adalah sebuah method yang berjalan saat object diinisiasikan untuk mengisi data misalkan. Identitas identitas = new Identitas(Richo);
kalau di file lainnya akan
public Class Identitas{
    String nama;
    public Identitas(String nama){
        this.nama = nama;
    }
}
```

## Section D - Access Modifier

### 14. Apa fungsi private?

Jawaban:

```text
Tulis jawaban di sini.
private adalah encapsulation yang membuat dia hanya bisa diakses class dia sendiri dan harus membuat getter atau setter untuk diakses.
```

### 15. Apa fungsi public?

Jawaban:

```text
Tulis jawaban di sini.
public adalah deklarasi bahwa field ini bisa diakses oleh siapa saja bahkan di package yang berbeda.
```

### 16. Kenapa field biasanya dibuat private?

Jawaban:

```text
Tulis jawaban di sini.
agar lebih aman dan tidak bisa sembarangan diakses oleh fungsi yang tidak berwenang.
```

## Section E - Getter and Setter

### 17. Apa fungsi getter?

Jawaban:

```text
Tulis jawaban di sini.
getter untuk mengambil data dari field yang bersifat private
```

### 18. Apa fungsi setter?

Jawaban:

```text
Tulis jawaban di sini.
melakukan assign value pada field yang private
```

### 19. Kenapa tidak semua field dibuat public?

Jawaban:

```text
Tulis jawaban di sini.
karena tidak semua field perlu diakses oleh semua class atau package yang berbeda.
```

## Section F - Collection

### 20. Apa itu List?

Jawaban:

```text
Tulis jawaban di sini.
List adalah kumpulan serangkaian data yang isi tipe data sama List<String> akan berisikan {"nama", "richo", "aldi taher"}
```

### 21. Apa itu Map?

Jawaban:

```text
Tulis jawaban di sini.
Map adalah sebuah kumpulan data yang berbentuk key dan value. misal {'Hachi': "nama", 'Naruto': "richo", 'Mantap': "aldi taher"}
```

### 22. Kapan menggunakan List dan kapan menggunakan Map?

Jawaban:

```text
Tulis jawaban di sini.
Gunakanlah list bila mau menyimpan tipe data yang sama dan ingin melakukan akses data secara linear atau tidak ada identifier. Gunakanlah Map bila ingin melakukan assigning data serta memiliki identifier pada satu key yang nilainya bisa berubah misal customer key dan jumlah belanja adalah value.
```

## Section G - OOP Basic

### 23. Apa itu encapsulation?

Jawaban:

```text
Tulis jawaban di sini.
Sebuah cara untuk memberikan akses control pada sebuah field. Encapsulation ada 3 public, protected, dan private. bila tidak dideclare akan dianggap protected. Public bisa diakses siapa saja bahkan beda package. protected bisa diakses oleh seluruh packagenya sendiri. Sementara private hanya bisa diakses oleh classnya sendiri.
```

### 24. Apa itu interface?

Jawaban:

```text
Tulis jawaban di sini.
Interface adalah sebuah blueprint yang harus diimplementasikan seluruh field yang dimilikinya. Misal membuat Interface Animal{
    void gerak();
}
Class Anjing implements Animal {
    @Override
    void gerak(){
        System.out.println("Guk guk");
    }
}
Bila anjing tidak membuat method gerak maka akan error karena Interface bisa dibilang kontrak yang harus dijalankan sebuah class yang mengimplemtasikannya (Kecuali abstract class). Satu class bisa memiliki banyak interface seperti Class Anjing implements Animal, Lari {

}

setelah dibuat bisa diinisiasikan 
Animal anjing =  new Anjing(); 
ini adalah konsep polymorpishm karena Animal sekarang bentuknya Anjing. Bisa buat baru lagi
Animal burung = new Burung();
```

### 25. Apa itu abstract class?

Jawaban:

```text
Tulis jawaban di sini.
abstract class adalah sebuah class yang bentuknya tidak menentu namun bisa di extends ke class lain namun harus ada di abstract class mirip seperti kontrak juga.
implementasinya sekarang biasa Interface dibuat diimplementasikan ke Abstract Class baru di extends ke class
```

### 26. Apa perbedaan interface dan abstract class?

Jawaban:

```text
Tulis jawaban di sini.
Interface wajib dibuat karena contract menjadi blueprint yang strict, sementara abstract tidak perlu namun itu akan menjadi sebuah blueprint kepada sebuah class. Abstract class bisa memiliki method sendiri namun Interface secara best practice adalah method kosong untuk sebuah blueprint
```

## Self Assessment

Beri tanda sesuai pemahaman kamu saat ini.

| Topik | Belum paham | Mulai paham | Cukup paham |
| --- | --- | --- | --- |
| Basic syntax | [ ] | [ ] | [v ] |
| Class and object | [ ] | [ ] | [ v] |
| Field/method/constructor | [ ] | [ ] | [v ] |
| Access modifier | [ ] | [ ] | [ v] |
| Getter/setter | [ ] | [ ] | [ v] |
| List/Map | [ ] | [ ] | [ v] |
| Interface/abstract class | [ ] | [ ] | [v ] |
