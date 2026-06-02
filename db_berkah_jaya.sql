-- ============================================================
--  Toko Berkah Jaya - Database Setup Script
--  Jalankan script ini di MySQL sebelum menjalankan aplikasi
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_berkah_jaya
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_berkah_jaya;

-- Tabel Kategori
CREATE TABLE IF NOT EXISTS tb_kategori (
    id_kategori   INT          PRIMARY KEY AUTO_INCREMENT,
    nama_kategori VARCHAR(50)  NOT NULL
) ENGINE=InnoDB;

-- Tabel Barang
CREATE TABLE IF NOT EXISTS tb_barang (
    id_barang   VARCHAR(10)  PRIMARY KEY,
    id_kategori INT,
    nama_barang VARCHAR(100) NOT NULL,
    satuan      VARCHAR(20),
    harga_jual  DOUBLE       DEFAULT 0,
    stok        INT          DEFAULT 0,
    FOREIGN KEY (id_kategori) REFERENCES tb_kategori(id_kategori)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Tabel Customer
CREATE TABLE IF NOT EXISTS tb_customer (
    id_customer   VARCHAR(10)  PRIMARY KEY,
    nama_customer VARCHAR(100) NOT NULL,
    alamat        TEXT,
    telepon       VARCHAR(15)
) ENGINE=InnoDB;

-- Tabel User
CREATE TABLE IF NOT EXISTS tb_user (
    id_user     INT          PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100),
    level       ENUM('Admin','Petugas') DEFAULT 'Petugas'
) ENGINE=InnoDB;

-- Tabel Penjualan (header / faktur)
CREATE TABLE IF NOT EXISTS tb_penjualan (
    id_jual       INT    PRIMARY KEY AUTO_INCREMENT,
    no_faktur     VARCHAR(20),
    tgl_transaksi DATE,
    id_customer   VARCHAR(10),
    total_bayar   DOUBLE DEFAULT 0,
    id_user       INT,
    FOREIGN KEY (id_customer) REFERENCES tb_customer(id_customer),
    FOREIGN KEY (id_user)     REFERENCES tb_user(id_user)
) ENGINE=InnoDB;

-- Tabel Detail Penjualan (item per faktur)
CREATE TABLE IF NOT EXISTS tb_detail_penjualan (
    id_detail    INT          PRIMARY KEY AUTO_INCREMENT,
    id_jual      INT,
    id_barang    VARCHAR(10),
    harga_satuan DOUBLE       DEFAULT 0,
    jumlah_beli  INT          DEFAULT 0,
    subtotal     DOUBLE       DEFAULT 0,
    FOREIGN KEY (id_jual)   REFERENCES tb_penjualan(id_jual) ON DELETE CASCADE,
    FOREIGN KEY (id_barang) REFERENCES tb_barang(id_barang)
) ENGINE=InnoDB;

-- =============== Data Awal ===============

INSERT IGNORE INTO tb_user (username, password, nama_lengkap, level) VALUES
('admin',   'admin123',   'Administrator',  'Admin'),
('petugas', 'petugas123', 'Petugas Toko',   'Petugas');

INSERT IGNORE INTO tb_kategori (nama_kategori) VALUES
('Makanan'),
('Minuman'),
('Elektronik'),
('Pakaian'),
('Peralatan Rumah');

INSERT IGNORE INTO tb_customer VALUES
('CST001', 'Budi Santoso',  'Jl. Merdeka No. 10, Jakarta',    '08123456789'),
('CST002', 'Siti Rahayu',   'Jl. Sudirman No. 5, Bandung',    '08987654321'),
('CST003', 'Agus Prasetyo', 'Jl. Gatot Subroto No. 3, Depok', '08567890123');

INSERT IGNORE INTO tb_barang VALUES
('BRG001', 1, 'Beras Premium',      'Kg',   12000, 100),
('BRG002', 1, 'Minyak Goreng 1L',   'Botol', 18000,  50),
('BRG003', 2, 'Air Mineral 600ml',  'Botol',  3000, 200),
('BRG004', 2, 'Teh Botol Sosro',    'Botol',  5000,  80),
('BRG005', 3, 'Lampu LED 10W',      'Pcs',   25000,  30);
