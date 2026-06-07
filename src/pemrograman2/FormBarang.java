package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormBarang extends JPanel {

    private JTextField        txtId, txtNama, txtSatuan, txtHarga, txtStok, txtKategori;
    private JButton           btnSimpan, btnUbah, btnHapus, btnBersihkan;
    private JTable            tblBarang;
    private DefaultTableModel tableModel;

    private boolean isEdit = false;

    public FormBarang() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("MANAJEMEN DATA BARANG", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Input Panel ──────────────────────────────────────────────────────
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Form Input Barang"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 10, 7, 10);

        txtId       = new JTextField(10);
        txtNama     = new JTextField(20);
        txtSatuan   = new JTextField(10);
        txtHarga    = new JTextField(12);
        txtStok     = new JTextField(6);
        txtKategori = new JTextField(15);

        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);

        // Filter ketik sesuai jenis data (nama barang dibiarkan alfanumerik bebas)
        Validasi.hanyaHuruf(txtKategori);   // kategori: huruf & spasi
        Validasi.hanyaHuruf(txtSatuan);     // satuan: huruf (pcs, kg, liter)
        Validasi.formatRibuan(txtHarga);    // harga: angka bulat + titik ribuan
        Validasi.hanyaAngka(txtStok);       // stok: bilangan bulat

        // Row 0: ID Barang | Kategori
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("ID Barang:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.25;
        inputPanel.add(txtId, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("Kategori:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.75;
        inputPanel.add(txtKategori, g);

        // Row 1: Nama Barang (span)
        g.gridx = 0; g.gridy = 1; g.weightx = 0; g.gridwidth = 1;
        inputPanel.add(new JLabel("Nama Barang:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtNama, g);

        // Row 2: Satuan | Harga | Stok
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        inputPanel.add(new JLabel("Satuan:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 0.25;
        inputPanel.add(txtSatuan, g);
        g.gridx = 2; g.gridy = 2; g.weightx = 0;
        inputPanel.add(new JLabel("Harga Jual (Rp):"), g);
        g.gridx = 3; g.gridy = 2; g.weightx = 0.5;
        inputPanel.add(txtHarga, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        inputPanel.add(new JLabel("Stok Awal:"), g);
        g.gridx = 1; g.gridy = 3; g.weightx = 0.25;
        inputPanel.add(txtStok, g);

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnSimpan    = new JButton("Simpan");
        btnUbah      = new JButton("Ubah");
        btnHapus     = new JButton("Hapus");
        btnBersihkan = new JButton("Bersihkan");

        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);

        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkan());

        UITheme.styleButton(btnSimpan,    UITheme.SUCCESS);
        UITheme.styleButton(btnUbah,      UITheme.WARNING);
        UITheme.styleButton(btnHapus,     UITheme.DANGER);
        UITheme.styleButton(btnBersihkan, UITheme.NEUTRAL);

        btnPanel.add(btnSimpan);
        btnPanel.add(btnUbah);
        btnPanel.add(btnHapus);
        btnPanel.add(btnBersihkan);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel,   BorderLayout.SOUTH);

        // ── Table ────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
                new String[]{"ID Barang", "Nama Kategori", "Nama Barang", "Satuan", "Harga Jual (Rp)", "Stok"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBarang = new JTable(tableModel);
        tblBarang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(tblBarang);
        tblBarang.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblBarang.getColumnModel().getColumn(4).setPreferredWidth(110);

        tblBarang.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiForm(); }
        });

        JScrollPane scroll = new JScrollPane(tblBarang);
        scroll.setBorder(UITheme.titledBorder("Daftar Barang"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(195);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);
    }

    // ── Data Operations ─────────────────────────────────────────────────────

    public void refresh() {
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT b.id_barang, k.nama_kategori, b.nama_barang, " +
                "b.satuan, b.harga_jual, b.stok " +
                "FROM tb_barang b " +
                "LEFT JOIN tb_kategori k ON b.id_kategori = k.id_kategori " +
                "ORDER BY b.id_barang");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_kategori"),
                    rs.getString("nama_barang"),
                    rs.getString("satuan"),
                    Validasi.rupiah(rs.getDouble("harga_jual")),
                    rs.getInt("stok")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void isiForm() {
        int row = tblBarang.getSelectedRow();
        if (row < 0) return;

        txtId.setText(tableModel.getValueAt(row, 0).toString());
        Object kat = tableModel.getValueAt(row, 1);
        txtKategori.setText(kat != null ? kat.toString() : "");
        txtNama.setText(tableModel.getValueAt(row, 2).toString());
        txtSatuan.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        // Hapus titik/koma format sebelum set ke field angka
        txtHarga.setText(tableModel.getValueAt(row, 4).toString().replaceAll("[^0-9]", ""));
        txtStok.setText(tableModel.getValueAt(row, 5).toString());

        isEdit = true;
        btnSimpan.setEnabled(false);
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    private String generateNextId() {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return "BRG001";
        try (PreparedStatement ps = con.prepareStatement("SELECT MAX(id_barang) FROM tb_barang");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).replaceAll("[^0-9]", "")) + 1;
                return "BRG" + String.format("%03d", num);
            }
        } catch (Exception e) { /* pakai default */ }
        return "BRG001";
    }

    private boolean validasiInput() {
        // Nama barang boleh alfanumerik (mis. "Air Mineral 600ml"), cukup wajib diisi.
        if (!Validasi.notEmpty(this, txtNama, "Nama barang")) return false;
        if (!Validasi.isNama(this, txtKategori, "Kategori")) return false;
        if (!Validasi.isNama(this, txtSatuan, "Satuan")) return false;
        if (!Validasi.isDoubleTakNegatif(this, txtHarga, "Harga jual")) return false;
        if (!Validasi.isIntTakNegatif(this, txtStok, "Stok")) return false;
        return true;
    }

    /** Cari id_kategori berdasarkan nama (case-insensitive). Kalau belum ada, insert baru. */
    private int getOrCreateKategoriId(Connection con, String nama) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_kategori FROM tb_kategori WHERE LOWER(nama_kategori) = LOWER(?)")) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_kategori (nama_kategori) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nama);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Gagal membuat kategori baru.");
    }

    private void simpan() {
        if (!validasiInput()) return;
        String newId    = generateNextId();
        double harga    = Double.parseDouble(txtHarga.getText().replaceAll("[^0-9]", ""));
        int    stok     = Integer.parseInt(txtStok.getText().trim());

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try {
            int idKat = getOrCreateKategoriId(con, txtKategori.getText().trim());
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tb_barang (id_barang, id_kategori, nama_barang, satuan, harga_jual, stok) VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, newId);
                ps.setInt(2, idKat);
                ps.setString(3, txtNama.getText().trim());
                ps.setString(4, txtSatuan.getText().trim());
                ps.setDouble(5, harga);
                ps.setInt(6, stok);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this,
                    "Barang berhasil disimpan dengan ID: " + newId, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void ubah() {
        if (!isEdit || !validasiInput()) return;
        double harga = Double.parseDouble(txtHarga.getText().replaceAll("[^0-9]", ""));
        int    stok  = Integer.parseInt(txtStok.getText().trim());

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try {
            int idKat = getOrCreateKategoriId(con, txtKategori.getText().trim());
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? WHERE id_barang=?")) {
                ps.setInt(1, idKat);
                ps.setString(2, txtNama.getText().trim());
                ps.setString(3, txtSatuan.getText().trim());
                ps.setDouble(4, harga);
                ps.setInt(5, stok);
                ps.setString(6, txtId.getText());
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Data barang berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void hapus() {
        if (!isEdit) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus barang ini?\nData transaksi terkait akan bermasalah.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM tb_barang WHERE id_barang = ?")) {
            ps.setString(1, txtId.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus! Barang masih ada di data transaksi.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkan() {
        txtId.setText("");
        txtNama.setText("");
        txtSatuan.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        txtKategori.setText("");
        txtNama.requestFocus();
        isEdit = false;
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        tblBarang.clearSelection();
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
