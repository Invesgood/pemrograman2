package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormBarang extends JPanel {

    private JTextField        txtId, txtNama, txtSatuan, txtHarga, txtStok;
    private JComboBox<String> cboKategori;
    private JButton           btnSimpan, btnUbah, btnHapus, btnBersihkan;
    private JTable            tblBarang;
    private DefaultTableModel tableModel;

    // Menyimpan id_kategori paralel dengan item di cboKategori
    private final List<Integer> kategoriIds = new ArrayList<>();
    // Menyimpan raw data per baris tabel agar bisa isi combo kategori
    private final List<Integer> rowKategoriIds = new ArrayList<>();

    private boolean isEdit = false;

    public FormBarang() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadKategori();
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
        g.insets = new Insets(4, 8, 4, 8);

        txtId     = new JTextField(10);
        txtNama   = new JTextField(20);
        txtSatuan = new JTextField(10);
        txtHarga  = new JTextField(12);
        txtStok   = new JTextField(6);
        cboKategori = new JComboBox<>();

        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);

        // Row 0: ID Barang | Kategori
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("ID Barang:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.25;
        inputPanel.add(txtId, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("Kategori:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.75;
        inputPanel.add(cboKategori, g);

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
        loadKategori();
        loadData();
    }

    private void loadKategori() {
        cboKategori.removeAllItems();
        kategoriIds.clear();
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_kategori, nama_kategori FROM tb_kategori ORDER BY nama_kategori");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                kategoriIds.add(rs.getInt("id_kategori"));
                cboKategori.addItem(rs.getInt("id_kategori") + " - " + rs.getString("nama_kategori"));
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        rowKategoriIds.clear();
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT b.id_barang, b.id_kategori, k.nama_kategori, b.nama_barang, " +
                "b.satuan, b.harga_jual, b.stok " +
                "FROM tb_barang b " +
                "LEFT JOIN tb_kategori k ON b.id_kategori = k.id_kategori " +
                "ORDER BY b.id_barang");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rowKategoriIds.add(rs.getInt("id_kategori"));
                tableModel.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_kategori"),
                    rs.getString("nama_barang"),
                    rs.getString("satuan"),
                    String.format("%,.0f", rs.getDouble("harga_jual")),
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
        txtNama.setText(tableModel.getValueAt(row, 2).toString());
        txtSatuan.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        // Hapus titik/koma format sebelum set ke field angka
        txtHarga.setText(tableModel.getValueAt(row, 4).toString().replaceAll("[^0-9]", ""));
        txtStok.setText(tableModel.getValueAt(row, 5).toString());

        // Pilih kategori yang sesuai di combo
        int idKat = (row < rowKategoriIds.size()) ? rowKategoriIds.get(row) : -1;
        for (int i = 0; i < kategoriIds.size(); i++) {
            if (kategoriIds.get(i) == idKat) {
                cboKategori.setSelectedIndex(i);
                break;
            }
        }

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
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama barang harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cboKategori.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kategori barang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try { Double.parseDouble(txtHarga.getText().trim()); }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga jual harus berupa angka!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try { Integer.parseInt(txtStok.getText().trim()); }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka bulat!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void simpan() {
        if (!validasiInput()) return;
        String newId    = generateNextId();
        int    idKat    = kategoriIds.get(cboKategori.getSelectedIndex());
        double harga    = Double.parseDouble(txtHarga.getText().trim());
        int    stok     = Integer.parseInt(txtStok.getText().trim());

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_barang (id_barang, id_kategori, nama_barang, satuan, harga_jual, stok) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, newId);
            ps.setInt(2, idKat);
            ps.setString(3, txtNama.getText().trim());
            ps.setString(4, txtSatuan.getText().trim());
            ps.setDouble(5, harga);
            ps.setInt(6, stok);
            ps.executeUpdate();
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
        int    idKat = kategoriIds.get(cboKategori.getSelectedIndex());
        double harga = Double.parseDouble(txtHarga.getText().trim());
        int    stok  = Integer.parseInt(txtStok.getText().trim());

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? WHERE id_barang=?")) {
            ps.setInt(1, idKat);
            ps.setString(2, txtNama.getText().trim());
            ps.setString(3, txtSatuan.getText().trim());
            ps.setDouble(4, harga);
            ps.setInt(5, stok);
            ps.setString(6, txtId.getText());
            ps.executeUpdate();
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
        if (cboKategori.getItemCount() > 0) cboKategori.setSelectedIndex(0);
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
