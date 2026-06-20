package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormKategori extends JPanel {

    private JTextField         txtIdKategori, txtNamaKategori;
    private JButton            btnSimpan, btnUbah, btnHapus, btnBersihkan;
    private JTable             tblKategori;
    private DefaultTableModel  tableModel;
    private boolean            isEdit = false;

    public FormKategori() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("MANAJEMEN DATA KATEGORI", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Input Panel ──────────────────────────────────────────────────────
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Form Input Kategori"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 10, 7, 10);

        txtIdKategori    = new JTextField(8);
        txtNamaKategori  = new JTextField(20);
        txtIdKategori.setEditable(false);
        txtIdKategori.setBackground(Color.LIGHT_GRAY);

        // Tanpa filter ketik: divalidasi saat Simpan & ditampilkan via popup.

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("ID Kategori:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.3;
        inputPanel.add(txtIdKategori, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("Nama Kategori:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 1;
        inputPanel.add(txtNamaKategori, g);

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
        tableModel = new DefaultTableModel(new String[]{"ID Kategori", "Nama Kategori"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKategori = new JTable(tableModel);
        tblKategori.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(tblKategori);
        tblKategori.getColumnModel().getColumn(0).setPreferredWidth(80);

        tblKategori.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiForm(); }
        });

        JScrollPane scroll = new JScrollPane(tblKategori);
        scroll.setBorder(UITheme.titledBorder("Daftar Kategori"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(130);
        split.setResizeWeight(0.25);
        add(split, BorderLayout.CENTER);
    }

    // ── Data Operations ─────────────────────────────────────────────────────

    public void refresh() { loadData(); }

    private void loadData() {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_kategori, nama_kategori FROM tb_kategori ORDER BY id_kategori");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_kategori"),
                    rs.getString("nama_kategori")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void isiForm() {
        int row = tblKategori.getSelectedRow();
        if (row < 0) return;
        txtIdKategori.setText(tableModel.getValueAt(row, 0).toString());
        txtNamaKategori.setText(tableModel.getValueAt(row, 1).toString());
        isEdit = true;
        btnSimpan.setEnabled(false);
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    /** Cek nama kategori sudah ada (case-insensitive). exceptId = -1 untuk data baru. */
    private boolean namaKategoriDuplikat(String nama, int exceptId) {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return false;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_kategori FROM tb_kategori WHERE LOWER(nama_kategori) = LOWER(?) AND id_kategori <> ?")) {
            ps.setString(1, nama);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { showError(e); return false; }
    }

    private void simpan() {
        if (!Validasi.isNama(this, txtNamaKategori, "Nama kategori")) return;
        String nama = txtNamaKategori.getText().trim();
        if (namaKategoriDuplikat(nama, -1)) {
            JOptionPane.showMessageDialog(this, "Kategori \"" + nama + "\" sudah ada!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_kategori (nama_kategori) VALUES (?)")) {
            ps.setString(1, nama);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kategori berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void ubah() {
        if (!isEdit) return;
        if (!Validasi.isNama(this, txtNamaKategori, "Nama kategori")) return;
        String nama = txtNamaKategori.getText().trim();
        int idKat = Integer.parseInt(txtIdKategori.getText());
        if (namaKategoriDuplikat(nama, idKat)) {
            JOptionPane.showMessageDialog(this, "Kategori \"" + nama + "\" sudah ada!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE tb_kategori SET nama_kategori = ? WHERE id_kategori = ?")) {
            ps.setString(1, nama);
            ps.setInt(2, Integer.parseInt(txtIdKategori.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kategori berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void hapus() {
        if (!isEdit) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus kategori ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM tb_kategori WHERE id_kategori = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdKategori.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus! Kategori masih digunakan oleh data barang.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkan() {
        txtIdKategori.setText("");
        txtNamaKategori.setText("");
        txtNamaKategori.requestFocus();
        isEdit = false;
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        tblKategori.clearSelection();
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
