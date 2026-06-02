-- Migrasi db_berkah_jaya agar sesuai diagram ER (lanjutan, collation diperbaiki)
-- no_faktur sudah ditambahkan di run sebelumnya.

START TRANSACTION;

-- 1. Buat tabel tb_detail_penjualan (collation samakan dgn tb_barang.id_barang)
CREATE TABLE tb_detail_penjualan (
  id_detail    INT          NOT NULL AUTO_INCREMENT,
  id_jual      INT          NULL,
  id_barang    VARCHAR(10)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  harga_satuan DOUBLE       DEFAULT 0,
  jumlah_beli  INT          DEFAULT 0,
  subtotal     DOUBLE       DEFAULT 0,
  PRIMARY KEY (id_detail),
  KEY idx_detail_jual (id_jual),
  KEY idx_detail_barang (id_barang),
  CONSTRAINT fk_detail_jual   FOREIGN KEY (id_jual)   REFERENCES tb_penjualan(id_jual) ON DELETE CASCADE,
  CONSTRAINT fk_detail_barang FOREIGN KEY (id_barang) REFERENCES tb_barang(id_barang)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Pindahkan data lama ke tabel detail
INSERT INTO tb_detail_penjualan (id_jual, id_barang, harga_satuan, jumlah_beli, subtotal)
SELECT id_jual,
       id_barang,
       CASE WHEN jumlah_beli > 0 THEN total_bayar / jumlah_beli ELSE total_bayar END AS harga_satuan,
       jumlah_beli,
       total_bayar AS subtotal
FROM tb_penjualan;

-- 3. Generate no_faktur untuk transaksi lama (format INV-0001)
UPDATE tb_penjualan SET no_faktur = CONCAT('INV-', LPAD(id_jual, 4, '0')) WHERE no_faktur IS NULL;

COMMIT;

-- 4. Hapus FK + kolom id_barang & jumlah_beli dari tb_penjualan (DDL, di luar transaksi)
ALTER TABLE tb_penjualan DROP FOREIGN KEY tb_penjualan_ibfk_2;
ALTER TABLE tb_penjualan DROP COLUMN id_barang;
ALTER TABLE tb_penjualan DROP COLUMN jumlah_beli;
