# MANUAL BOOK
# APLIKASI KASIR & MANAJEMEN TOKO "BERKAH JAYA"

> **Cara pakai dokumen ini:** Setiap tanda `[Screenshot: ...]` adalah tempat kamu menyisipkan
> tangkapan layar aplikasi. Ganti juga bagian yang ditulis dalam `<kurung siku>` dengan datamu.

---

## HALAMAN JUDUL

**APLIKASI KASIR & MANAJEMEN TOKO BERKAH JAYA**

Disusun untuk memenuhi tugas mata kuliah **Pemrograman 2**

- Nama        : `<Nama Lengkap>`
- NIM         : `<NIM>`
- Kelas       : `<Kelas>`
- Dosen       : `<Nama Dosen Pengampu>`
- Program Studi: `<Program Studi>`
- Tahun       : 2026

`[Screenshot: logo / tampilan utama aplikasi]`

---

## DAFTAR ISI

1. Pendahuluan
2. Spesifikasi & Kebutuhan Sistem
3. Instalasi & Persiapan
4. Memulai Aplikasi (Login)
5. Panduan Penggunaan
   - 5.1 Dashboard (Admin)
   - 5.2 Data Barang (Admin)
   - 5.3 Data Customer (Admin)
   - 5.4 Member (Petugas)
   - 5.5 Transaksi Penjualan
   - 5.6 Laporan Penjualan (Admin)
   - 5.7 Manajemen User (Admin)
6. Hak Akses Pengguna
7. Troubleshooting (Pemecahan Masalah)
8. Penutup

---

## 1. PENDAHULUAN

### 1.1 Deskripsi Aplikasi
**Berkah Jaya** adalah aplikasi *desktop* kasir dan manajemen toko yang dibangun
menggunakan bahasa pemrograman **Java (Swing)** dengan database **MySQL**.
Aplikasi ini membantu pemilik toko dalam mengelola data barang, data pelanggan,
proses transaksi penjualan, hingga pembuatan laporan penjualan.

### 1.2 Tujuan
- Mempermudah pencatatan transaksi penjualan secara digital.
- Mengelola stok barang dan data pelanggan/member secara terpusat.
- Menyajikan laporan penjualan yang cepat dan akurat.
- Memisahkan hak akses antara **Admin** dan **Petugas (Kasir)**.

### 1.3 Fitur Utama
- **Login multi-level** (Admin & Petugas) dengan hak akses berbeda.
- **Dashboard** ringkasan penjualan dan stok (khusus Admin).
- **Manajemen data barang** beserta kategori.
- **Manajemen data customer & pendaftaran member.**
- **Transaksi penjualan** dengan keranjang multi-item, perhitungan otomatis,
  pembayaran, kembalian, dan cetak struk.
- **Laporan penjualan** dengan filter rentang tanggal.
- **Manajemen user** (tambah/ubah/hapus akun) khusus Admin.
- **Validasi input terpusat** dan format tampilan ramah pengguna
  (harga otomatis bertitik ribuan, tanggal format `dd-MM-yyyy`).

---

## 2. SPESIFIKASI & KEBUTUHAN SISTEM

### 2.1 Perangkat Lunak
| Komponen | Keterangan |
|---|---|
| Sistem Operasi | Windows / macOS / Linux |
| JDK / JRE | Java 8 atau lebih baru |
| IDE | Apache NetBeans |
| Database | MySQL 8.0 (bisa via XAMPP / Laragon / Docker) |
| Library eksternal | **MySQL Connector/J** (driver JDBC) |

> **Catatan:** Satu-satunya library di luar Java standar pada aplikasi ini adalah
> **MySQL Connector/J**, yang berfungsi sebagai jembatan koneksi antara Java dan MySQL.
> Fitur lain (tampilan, validasi, format tanggal & angka) menggunakan pustaka bawaan Java
> (`javax.swing`, `java.sql`, `java.text`).

### 2.2 Spesifikasi Minimum Perangkat Keras
- Processor : Dual Core
- RAM       : 2 GB (disarankan 4 GB)
- Penyimpanan: 200 MB ruang kosong

---

## 3. INSTALASI & PERSIAPAN

### 3.1 Menyiapkan Database
1. Pastikan **MySQL aktif** (jalankan XAMPP/Laragon atau service MySQL).
2. Buka **phpMyAdmin** atau MySQL Command Line.
3. Buat database baru bernama **`db_berkah_jaya`** (jika belum ada).
4. **Import** file `db_berkah_jaya.sql` yang ada di folder proyek.

`[Screenshot: proses import database di phpMyAdmin]`

### 3.2 Menambahkan Driver MySQL
1. Pastikan file **`mysql-connector.jar`** berada di folder **`lib/`** proyek.
2. Jika belum ada, ikuti panduan pada file **`lib/CARA_INSTALL_DRIVER.txt`**.

### 3.3 Menyesuaikan Koneksi Database
Buka file **`src/pemrograman2/Koneksi.java`** lalu sesuaikan dengan pengaturan MySQL di
komputermu:

| Pengaturan | Nilai default |
|---|---|
| Host | `localhost` |
| Port | `3306` |
| Database | `db_berkah_jaya` |
| Username | `root` |
| Password | *(sesuaikan dengan password MySQL-mu)* |

> Jika password MySQL kamu kosong, ubah nilai konstanta `PASS` menjadi `""`.

### 3.4 Menjalankan Aplikasi
1. Buka proyek di **NetBeans**.
2. Klik kanan proyek → **Clean and Build** (atau tekan `Shift + F11`).
3. Klik **Run** (atau tekan `F6`).
4. Jendela **Login** akan tampil.

`[Screenshot: jendela login pertama kali]`

---

## 4. MEMULAI APLIKASI (LOGIN)

1. Masukkan **Username** dan **Password**.
2. Klik tombol **Login**.
3. Jika berhasil, aplikasi akan masuk ke jendela utama sesuai hak akses.

**Akun bawaan (default):**
| Level | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Petugas | `petugas` | `petugas123` |

`[Screenshot: form login terisi]`

> Jika username/password salah, akan muncul peringatan. Pastikan huruf besar/kecil sesuai.

---

## 5. PANDUAN PENGGUNAAN

Setelah login, terdapat **menu navigasi di sisi kiri (sidebar)**. Menu yang muncul
berbeda tergantung hak akses (lihat **Bab 6**).

`[Screenshot: tampilan utama + sidebar menu]`

---

### 5.1 Dashboard (Admin)
Halaman ringkasan kondisi toko.

**Yang ditampilkan:**
- **Kartu KPI**: Penjualan Hari Ini, Penjualan Bulan Ini, Transaksi Hari Ini, Jumlah Stok Menipis.
- **Tabel Barang Terlaris (Top 5)**.
- **Tabel Stok Menipis** (barang dengan stok ≤ 10).

**Cara menggunakan:** cukup buka menu **Dashboard**; data tampil otomatis dan
ter-*update* sesuai transaksi terbaru.

`[Screenshot: halaman dashboard]`

---

### 5.2 Data Barang (Admin)
Mengelola daftar barang beserta kategorinya.

**Menambah barang baru:**
1. Buka menu **Barang**.
2. **ID Barang** terisi otomatis (mis. `BRG001`).
3. Isi **Nama Barang**, **Kategori**, **Satuan**, **Harga Jual**, dan **Stok**.
   - Kolom **Harga** otomatis memberi titik ribuan saat diketik (mis. `180.000`).
4. Klik **Simpan**.

**Mengubah barang:**
1. Klik salah satu **baris** pada tabel → data masuk ke form.
2. Ubah data yang diperlukan.
3. Klik **Ubah**.

**Menghapus barang:**
1. Pilih baris pada tabel.
2. Klik **Hapus**, lalu konfirmasi.

`[Screenshot: halaman data barang]`

---

### 5.3 Data Customer (Admin)
Mengelola data pelanggan/member toko (CRUD penuh).

**Langkah umum:**
1. Buka menu **Customer**.
2. **ID Customer** terisi otomatis (mis. `CST001`).
3. Isi **Nama**, **Alamat**, dan **No. Telepon**.
4. Gunakan tombol **Simpan / Ubah / Hapus** sesuai kebutuhan.
5. Klik baris tabel untuk memuat data ke form (saat mengubah/menghapus).

> Validasi: No. telepon harus angka (9–15 digit) dan tidak boleh sama dengan customer lain.

`[Screenshot: halaman data customer]`

---

### 5.4 Member (Petugas)
Khusus Petugas, terdapat dua sub-menu (tab):

**a. Cek Member**
1. Masukkan **No. Telepon** member.
2. Klik **Cari** → data member tampil bila terdaftar.

**b. Daftar Member**
1. Isi **Nama**, **Alamat**, dan **No. Telepon**.
2. Klik **Simpan** → member baru tersimpan.

`[Screenshot: tab cek member & tab daftar member]`

---

### 5.5 Transaksi Penjualan
Menu inti untuk melayani pembelian. Tersedia untuk **Admin maupun Petugas**.

**Langkah transaksi:**
1. Buka menu **Transaksi**. **Tanggal** terisi otomatis (hari ini, format `dd-MM-yyyy`).
2. **(Opsional) Pilih Member:** aktifkan opsi member lalu cari berdasarkan no. telepon.
3. Pilih **Barang** dari daftar → **Harga** terisi otomatis.
4. Masukkan **Jumlah** → **Total** per item terhitung otomatis.
5. Klik **Tambah ke Keranjang**.
6. Ulangi langkah 3–5 untuk barang lain (boleh banyak item).
7. Periksa **TOTAL BAYAR** di bagian bawah keranjang.
8. Klik **Bayar**:
   - Masukkan **Uang Dibayar** (otomatis bertitik ribuan), atau klik **Uang Pas**.
   - **Kembalian** terhitung otomatis.
9. Klik **Proses / Simpan** → muncul **struk** berisi No. Faktur, total, tunai, dan kembalian.

> Saat transaksi disimpan, **stok barang otomatis berkurang** dan **nomor faktur** dibuat
> otomatis (format `INV-0001`). Jika stok tidak mencukupi, akan muncul peringatan.

`[Screenshot: form transaksi + keranjang]`
`[Screenshot: dialog pembayaran & struk]`

---

### 5.6 Laporan Penjualan (Admin)
Menampilkan riwayat penjualan beserta ringkasannya.

**Langkah:**
1. Buka menu **Laporan**.
2. Atur **filter tanggal** (`Dari` dan `s/d`) dengan format **`dd-MM-yyyy`**.
   - Saat dibuka, otomatis menampilkan data **hari ini**.
3. Klik **Tampilkan** → tabel terisi sesuai rentang tanggal.
4. Klik **Semua Data** untuk menampilkan seluruh transaksi tanpa filter.
5. Lihat ringkasan di bawah: **Jumlah Transaksi** dan **Total Pendapatan**.

`[Screenshot: halaman laporan dengan filter tanggal]`

---

### 5.7 Manajemen User (Admin)
Mengelola akun pengguna aplikasi.

**Langkah:**
1. Buka menu **User**.
2. Isi **Username**, **Password**, **Nama Lengkap**, dan **Level/Hak Akses** (Admin / Petugas).
3. Gunakan tombol **Simpan / Ubah / Hapus**.

> **Aturan keamanan:**
> - Admin tidak dapat menghapus akun yang sedang dipakai login.
> - Sistem mencegah penghapusan **Admin terakhir** (minimal harus ada 1 Admin).

`[Screenshot: halaman manajemen user]`

---

## 6. HAK AKSES PENGGUNA

| Menu / Fitur | Admin | Petugas |
|---|:---:|:---:|
| Dashboard | ✅ | ❌ |
| Data Barang | ✅ | ❌ |
| Data Customer | ✅ | ❌ |
| Member (Cek & Daftar) | ❌ | ✅ |
| Transaksi Penjualan | ✅ | ✅ |
| Laporan Penjualan | ✅ | ❌ |
| Manajemen User | ✅ | ❌ |

> **Admin** berfokus pada pengelolaan data & laporan. **Petugas** berfokus pada
> pelayanan transaksi dan member di kasir.

---

## 7. TROUBLESHOOTING (PEMECAHAN MASALAH)

| Masalah | Kemungkinan Penyebab | Solusi |
|---|---|---|
| **"Gagal terhubung ke database"** | MySQL belum jalan / nama DB salah | Nyalakan MySQL, pastikan database `db_berkah_jaya` ada |
| Koneksi gagal walau MySQL nyala | Password di `Koneksi.java` tidak cocok | Samakan `PASS` dengan password MySQL-mu |
| **Login gagal terus** | Username/password salah | Gunakan akun default; perhatikan huruf besar/kecil |
| Driver tidak ditemukan | `mysql-connector.jar` belum ditambahkan | Cek folder `lib/` & ikuti `CARA_INSTALL_DRIVER.txt` |
| **"Stok tidak cukup"** saat transaksi | Jumlah beli melebihi stok | Kurangi jumlah atau tambah stok di menu Barang |
| Filter laporan tidak menampilkan data | Format tanggal salah | Gunakan format **`dd-MM-yyyy`** (mis. `07-06-2026`) |
| Menu tertentu tidak muncul | Hak akses berbeda | Cek tabel hak akses (Bab 6) — login sebagai Admin bila perlu |

---

## 8. PENUTUP

Manual book ini disusun sebagai panduan penggunaan aplikasi **Berkah Jaya**.
Dengan mengikuti langkah-langkah di atas, pengguna diharapkan dapat mengoperasikan
aplikasi dengan mudah, mulai dari login, pengelolaan data, transaksi, hingga laporan.

Apabila terdapat kendala teknis, silakan merujuk pada **Bab 7 (Troubleshooting)**
atau menghubungi pengembang aplikasi.

---

*Dokumen ini dibuat untuk keperluan tugas mata kuliah Pemrograman 2 — 2026.*
