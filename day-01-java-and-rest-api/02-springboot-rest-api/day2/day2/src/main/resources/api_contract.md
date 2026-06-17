# Customer Management System API Documentation v2

Dokumentasi ini mencakup spesifikasi API Contract v2, pemetaan objek data (DTO), dan logikal bisnis pada service layer menggunakan Spring Boot, Spring Data JPA, dan database H2.

---

## 1. API Contract & Endpoints

### 1. Create Customer
* **Method**: `POST`
* **URL**: `/api/v1/customers`
* **Description**: Menambahkan data customer baru ke database H2 dengan validasi field wajib.
* **Request Body** (`application/json`):
```json
{
  "full_name": "Lala",
  "email": "lala@gmail.com",
  "phone_number": "088888888"
}
```
* **Success Response (`201 Created`)**:
```json
{
  "code": "201",
  "message": "Successfully created new customer",
  "data": {
    "id": 1,
    "full_name": "Lala",
    "email": "lala@gmail.com",
    "phone_number": "088888888",
    "created_at": "2026-06-17T14:52:19.123+07:00",
    "updated_at": "2026-06-17T14:52:19.123+07:00"
  },
  "errors": null,
  "timestamp": "2026-06-17T14:52:19.123"
}
```
* **Error Response (`400 Bad Request` - Gagal Validasi)**:
```json
{
  "code": "400",
  "message": "Validation failed",
  "data": null,
  "errors": [
    {
      "field": "email",
      "message": "Format email tidak valid"
    }
  ],
  "timestamp": "2026-06-17T14:53:01.456"
}
```

### 2. Get Customer List
* **Method**: `GET`
* **URL**: `/api/v1/customers`
* **Description**: Mendapatkan seluruh daftar customer tanpa filter/pagination dari database H2.
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully fetched all customers",
  "data": [
    {
      "id": 1,
      "full_name": "Lala",
      "email": "lala@gmail.com",
      "phone_number": "088888888",
      "created_at": "2026-06-17T14:52:19.123+07:00",
      "updated_at": "2026-06-17T14:52:19.123+07:00"
    }
  ],
  "errors": null,
  "timestamp": "2026-06-17T14:54:10.000"
}
```

### 3. Get Customer By ID
* **Method**: `GET`
* **URL**: `/api/v1/customers/{id}`
* **Description**: Mencari data satu customer yang sesuai dengan ID dari database.
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully fetched customer",
  "data": {
    "id": 1,
    "full_name": "Lala",
    "email": "lala@gmail.com",
    "phone_number": "088888888",
    "created_at": "2026-06-17T14:52:19.123+07:00",
    "updated_at": "2026-06-17T14:52:19.123+07:00"
  },
  "errors": null,
  "timestamp": "2026-06-17T14:55:00.111"
}
```
* **Error Response (`404 Not Found`)**:
```json
{
  "code": "404",
  "message": "Customer not found",
  "data": null,
  "errors": null,
  "timestamp": "2026-06-17T14:55:25.222"
}
```

### 4. Update Customer (PUT)
* **Method**: `PUT`
* **URL**: `/api/v1/customers/{id}`
* **Description**: Mengganti/menimpa seluruh data customer lama dengan data baru berdasarkan ID. Semua field di Request Body wajib diisi.
* **Request Body** (`application/json`):
```json
{
  "full_name": "Alexander Wright Jr",
  "email": "alex.jr@example.com",
  "phone_number": "0899999999"
}
```
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully updated customer",
  "data": {
    "id": 1,
    "full_name": "Alexander Wright Jr",
    "email": "alex.jr@example.com",
    "phone_number": "0899999999",
    "created_at": "2026-06-17T14:52:19.123+07:00",
    "updated_at": "2026-06-17T14:56:05.888+07:00"
  },
  "errors": null,
  "timestamp": "2026-06-17T14:56:05.888"
}
```

### 5. Patch Customer (PATCH)
* **Method**: `PATCH`
* **URL**: `/api/v1/customers/{id}`
* **Description**: Memperbarui sebagian data customer. Hanya field yang dikirim di request body yang berubah, sisanya tetap mempertahankan data lama di database.
* **Request Body** (`application/json`):
```json
{
  "phone_number": "0855555555"
}
```
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully updated customer",
  "data": {
    "id": 1,
    "full_name": "Alexander Wright Jr",
    "email": "alex.jr@example.com",
    "phone_number": "0855555555",
    "created_at": "2026-06-17T14:52:19.123+07:00",
    "updated_at": "2026-06-17T14:57:40.999+07:00"
  },
  "errors": null,
  "timestamp": "2026-06-17T14:57:40.999"
}
```

### 6. Get Customer By Email
* **Method**: `GET`
* **URL**: `/api/v1/customers/search?email=lala@gmail.com`
* **Description**: Mencari data satu customer pertama berdasarkan parameter query email (case-insensitive).
* **Request Query Param**: 
  * `email` (String, Required) - Alamat email yang dicari.
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully fetched customer by email",
  "data": {
    "id": 1,
    "full_name": "Lala",
    "email": "lala@gmail.com",
    "phone_number": "088888888",
    "created_at": "2026-06-17T14:52:19.123+07:00",
    "updated_at": "2026-06-17T14:52:19.123+07:00"
  },
  "errors": null,
  "timestamp": "2026-06-17T14:58:10.000"
}
```
* **Error Response (`404 Not Found`)**:
```json
{
  "code": "404",
  "message": "Customer dengan email tersebut tidak ditemukan",
  "data": null,
  "errors": null,
  "timestamp": "2026-06-17T14:58:25.222"
}
```

---

### 7. Get Customer List with Pagination
* **Method**: `GET`
* **URL**: `/api/v1/customers/paged?page=1&size=10&sort=id,asc`
* **Description**: Mendapatkan daftar customer dari database H2 yang dibagi per halaman menggunakan pagination data.
* **Request Query Params** (Optional):
  * `page` (Integer) - Nomor halaman yang ingin diambil (dimulai dari `0`). Default: `0`.
  * `size` (Integer) - Jumlah baris data per halaman. Default: `10`.
  * `sort` (String) - Kriteria pengurutan dengan format `property,asc|desc`. Default: `id,asc`.
* **Success Response (`200 OK`)**:
```json
{
  "code": "200",
  "message": "Successfully fetched paged customers",
  "data": {
    "content": [
      {
        "id": 41,
        "full_name": "string",
        "email": "usa@example.com",
        "phone_number": "string",
        "created_at": null,
        "updated_at": null
      },
      {
        "id": 42,
        "full_name": "string",
        "email": "usa@example.com",
        "phone_number": "string",
        "created_at": null,
        "updated_at": null
      },
      {
        "id": 66,
        "full_name": "anjay",
        "email": "user@example.com",
        "phone_number": "string",
        "created_at": null,
        "updated_at": null
      },
      {
        "id": 97,
        "full_name": "string",
        "email": "user@example.com",
        "phone_number": "string",
        "created_at": "2026-06-17T15:32:53.721911+07:00",
        "updated_at": "2026-06-17T15:33:46.040478+07:00"
      }
    ],
    "empty": false,
    "first": false,
    "last": false,
    "number": 1,
    "number_of_elements": 10,
    "pageable": {
      "offset": 10,
      "page_number": 1,
      "page_size": 10,
      "paged": true,
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "unpaged": false
    },
    "size": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "total_elements": 46,
    "total_pages": 5
  },
  "errors": null,
  "timestamp": "2026-06-17T17:01:42.990971"
}
```

## Panduan Integrasi Pagination untuk Frontend

API mendistribusikan metadata pagination menggunakan format standar Spring Data yang telah disesuaikan menjadi `snake_case`. Berikut adalah fungsi dari setiap properti untuk kebutuhan pengembangan UI/UX (seperti komponen Data Table, Pagination Bar, atau Infinite Scroll).

### 1. Objek Data Utama
* **`content`** *(Array)*: Array data utama berisi daftar objek customer. Gunakan properti ini untuk melakukan *looping* (misal: `.map()`) saat merender baris tabel atau kartu komponen.

### 2. Status Navigasi & Kondisi Tombol
Properti berikut bertipe *boolean* (`true`/`false`) dan sangat berguna untuk mengatur *state* aktif/nonaktif (*disabled*) pada tombol navigasi:
* **`number`** *(Integer)*: Indeks halaman yang sedang aktif saat ini. **Catatan penting:** Indeks dimulai dari `0` (Halaman 1 = `0`, Halaman 2 = `1`).
* **`first`** *(Boolean)*: Bernilai `true` jika user berada di halaman pertama. Gunakan untuk me-nonaktifkan tombol **"Kembali/Previous"**.
* **`last`** *(Boolean)*: Bernilai `true` jika user berada di halaman terakhir. Gunakan untuk me-nonaktifkan tombol **"Selanjutnya/Next"**.
* **`empty`** *(Boolean)*: Bernilai `true` jika halaman tidak memiliki data sama sekali. Gunakan untuk memicu tampilan komponen *Empty State* (misal: gambar ilustrasi *"Data Tidak Ditemukan"*).

### 3. Kalkulasi Komponen Angka & Informasi Paging
* **`total_pages`** *(Integer)*: Total keseluruhan halaman yang tersedia. Gunakan angka ini untuk membuat batas perulangan nomor halaman pada komponen pagination (misal membuat tombol angka: `1`, `2`, `3`, `4`, `5`).
* **`total_elements`** *(Integer)*: Total seluruh baris data yang ada di database tanpa filter halaman. Gunakan untuk menampilkan informasi teks statistik di pojok tabel, contoh: `Showing 10 of 46 entries`.
* **`size`** *(Integer)*: Batas maksimum baris data yang diminta per halaman. Gunakan untuk mengikat (*binding*) nilai drop-down pilihan jumlah baris per halaman (*Rows per Page*).
* **`number_of_elements`** *(Integer)*: Jumlah data aktual yang berhasil dimuat pada halaman ini saja. Nilainya bisa lebih kecil dari `size` apabila user sedang berada di halaman paling terakhir.

### 4. Detail Teknis Tambahan (Internal Metadata)
* **`pageable.offset`** *(Integer)*: Jumlah baris data yang dilewati sebelum halaman ini dimulai. Sangat berguna bagi Frontend jika ingin membuat penomoran urut tabel secara kontinu di setiap halaman menggunakan rumus: `offset + index + 1`.
* **`sort.sorted`** *(Boolean)*: Bernilai `true` jika data dikembalikan dalam kondisi terurut. Dapat digunakan Frontend untuk menyalakan indikator ikon panah aktif (▲/▼) pada *header* kolom tabel.

---

## 2. Data Transfer Objects (DTO) Specification

Semua objek DTO dikonfigurasi menggunakan strategi penamaan **Snake Case** (`snake_case`) untuk payload JSON menggunakan `@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)`.

### WebResponse<T>
Struktur response global aplikasi untuk membungkus data sukses maupun representasi error validation.
* `code` (String): HTTP Status Code atau kode error internal.
* `message` (String): Pesan informasi response.
* `data` (T): Payload data utama (bisa berupa object, list, atau page).
* `errors` (List<FieldErrorResponse>): Daftar error spesifik pada field input (jika terjadi validasi error).
* `timestamp` (LocalDateTime): Waktu pemicuan response.

### FieldErrorResponse
Digunakan di dalam `WebResponse` untuk melacak error validasi input.
* `field` (String): Nama properti input yang bermasalah.
* `message` (String): Pesan detail kegagalan validasi.

### CreateCustomerRequest
Digunakan untuk payload pendaftaran customer baru (`POST`).
* `full_name` (String): Wajib diisi, minimal 3 karakter.
* `email` (String): Wajib diisi, harus berformat email valid.
* `phone_number` (String): Wajib diisi, maksimal 10 digit.

### PutCustomerRequest
Digunakan untuk menimpa seluruh data customer lama (`PUT`).
* Properti dan aturan validasi sama persis dengan `CreateCustomerRequest`.

### PatchCustomerRequest
Digunakan untuk memperbarui sebagian data customer secara opsional (`PATCH`).
* `full_name` (String): Opsional, jika diisi minimal 3 karakter.
* `email` (String): Opsional, jika diisi harus berformat email valid.
* `phone_number` (String): Opsional, jika diisi maksimal 10 digit.

### CustomerResponse
Payload data yang dikembalikan ke client setelah proses manipulasi data atau pembacaan sukses.
* `id` (Long): Identifier unik customer.
* `full_name` (String): Nama lengkap customer.
* `email` (String): Alamat email.
* `phone_number` (String): Nomor telepon.
* `created_at` (ZonedDateTime): Waktu data pertama kali dibuat.
* `updated_at` (ZonedDateTime): Waktu terakhir kali data diperbarui.

---

## 3. Service Layer Logic (`CustomerServiceV2`)

Layer bisnis menyediakan method utama untuk memproses transaksi data ke `CustomerRepository`:

1. **`getAllCustomerWithPage(Pageable pageable)`**
   Mengambil data customer menggunakan pagination standar Spring Data.
2. **`getAllCustomer()`**
   Mengambil seluruh daftar data customer tanpa batasan filter halaman.
3. **`createCustomer(CreateCustomerRequest request)`**
   Membuat baris data baru dengan inisialisasi timestamp `createdAt` dan `updatedAt` secara otomatis menggunakan waktu lokal saat ini (`ZonedDateTime.now()`).
4. **`getCustomerById(Long id)`**
   Mengambil satu baris data berdasarkan ID. Melempar `ResponseStatusException(HttpStatus.NOT_FOUND)` jika ID tidak ditemukan.
5. **`getCustomerByEmail(String email)`**
   Mencari entitas pertama yang cocok menggunakan fungsionalitas query method JPA `findFirstByEmailContainingIgnoreCase`.
6. **`updateCustomer(Long id, PutCustomerRequest request)`**
   Melakukan penggantian secara menyeluruh terhadap properti entitas, serta memperbarui nilai properti `updatedAt`.
7. **`patchCustomer(Long id, PatchCustomerRequest request)`**
   Melakukan pemeriksaan kondisi null sebelum mengubah properti parsial entitas, memastikan field yang absen pada request body tetap mempertahankan nilai lama di database.
8. **`deleteCustomerById(Long id)`**
   Menghapus data entitas berdasarkan ID setelah melakukan pengecekan eksistensi data terlebih dahulu.
