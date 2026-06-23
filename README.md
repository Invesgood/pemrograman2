# Toko Berkah Jaya - Aplikasi Kasir dan Manajemen Penjualan

Aplikasi desktop untuk mengelola penjualan toko bernama "Toko Berkah Jaya".
Dibangun dengan Java Swing untuk antarmuka dan MySQL untuk basis data.
Aplikasi ini mencakup proses lengkap mulai dari login pengguna, pengelolaan
data master (barang, kategori, customer/member, user), transaksi penjualan
multi-item dengan pembayaran, sampai laporan penjualan yang bisa diekspor ke PDF.


## Identitas

Nama  : Muhamad Deraya Kautsar
NIM   : 231011400635
Kelas : 06TPLP014


## Teknologi yang Digunakan

- Bahasa: Java (JDK)
- Antarmuka (GUI): Java Swing dengan Look and Feel Nimbus dan tema visual
  Neobrutalism custom (UITheme.java)
- Basis data: MySQL, diakses lewat JDBC (mysql-connector.jar)
- Ekspor PDF: generator PDF custom tanpa library eksternal (PdfExporter.java)
- Build: Apache Ant (proyek NetBeans, lihat build.xml)


## Fitur Utama

- Login dan Hak Akses, autentikasi pengguna dengan dua level peran:
  Admin punya akses penuh termasuk manajemen User, sedangkan Petugas hanya
  akses operasional (transaksi, member) tanpa menu User.
- Dashboard, ringkasan informasi toko setelah login.
- Manajemen Barang, tambah/ubah/hapus barang beserta kategori, satuan,
  harga jual, dan stok. Input kategori tergabung di dalam form barang.
- Manajemen Customer/Member, kelola data pelanggan (nama, alamat, telepon).
- Transaksi Penjualan, keranjang multi-item, perhitungan subtotal dan total,
  proses pembayaran, serta penyimpanan faktur beserta detailnya.
- Laporan Penjualan, menampilkan riwayat transaksi dan ekspor ke PDF.
- Manajemen User (khusus Admin), kelola akun pengguna dan levelnya.
- Validasi terpusat, pengecekan input form dikumpulkan di Validasi.java.
- Format tampilan, harga memakai pemisah ribuan (titik), tanggal dd-MM-yyyy.


## Struktur Basis Data

Database: db_berkah_jaya (lihat db_berkah_jaya.sql).

- tb_user, akun pengguna (username, password, nama_lengkap, level Admin/Petugas)
- tb_kategori, kategori barang
- tb_barang, data barang (harga jual, stok, satuan, relasi ke kategori)
- tb_customer, data pelanggan/member
- tb_penjualan, header faktur penjualan (no faktur, tanggal, customer, total, user)
- tb_detail_penjualan, detail item per faktur (barang, harga satuan, jumlah, subtotal)

Relasi antar tabel menggunakan foreign key dengan ON DELETE CASCADE pada detail
penjualan, sehingga menghapus faktur otomatis menghapus detailnya.


## Struktur Kode (src/pemrograman2/)

- Pemrograman2.java, titik masuk aplikasi (main), memanggil FormLogin
- Koneksi.java, koneksi tunggal (singleton) ke database MySQL
- Session.java, menyimpan data user yang sedang login (id, username, nama, level)
- FormLogin.java, halaman login
- FormUtama.java, jendela utama dengan sidebar navigasi, mengatur tampilan panel
- FormDashboard.java, panel dashboard
- FormBarang.java dan FormKategori.java, manajemen barang dan kategori
- FormCustomer.java, FormMember.java, FormDaftarMember.java, manajemen pelanggan/member
- FormTransaksi.java, transaksi penjualan (keranjang dan pembayaran)
- FormLaporan.java, laporan penjualan
- FormUser.java, manajemen user (Admin)
- Validasi.java, fungsi validasi input terpusat
- UITheme.java, tema visual dan komponen UI (Neobrutalism)
- PdfExporter.java, pembuatan file PDF untuk laporan


## Cara Menjalankan

1. Siapkan database. Pastikan MySQL berjalan, lalu jalankan skrip:

   mysql -u root -p < db_berkah_jaya.sql

   Skrip ini membuat database db_berkah_jaya, seluruh tabel, dan data awal
   (termasuk akun login default).

2. Sesuaikan koneksi. Cek konfigurasi di src/pemrograman2/Koneksi.java dan
   sesuaikan bila perlu:

   HOST     = localhost
   PORT     = 3306
   DATABASE = db_berkah_jaya
   USER     = root
   PASS     = (password MySQL Anda)

3. Build dan jalankan. Lewat NetBeans: buka proyek lalu tekan Run.
   Atau lewat terminal dengan Ant:

   ant run


## Akun Login Default

- Username: admin, Password: admin123, Level: Admin
- Username: petugas, Password: petugas123, Level: Petugas


## Catatan

- Pastikan lib/mysql-connector.jar tersedia (sudah disertakan di proyek).
- File backup_db_berkah_jaya_*.sql adalah cadangan database.
