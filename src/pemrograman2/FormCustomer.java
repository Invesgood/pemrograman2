package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class FormCustomer extends JPanel {

    private JTextField        txtId, txtNama, txtAlamat, txtTelepon;
    private JButton           btnSimpan, btnUbah, btnHapus, btnBersihkan;
    private JTable            tblCustomer;
    private DefaultTableModel tableModel;
    private boolean           isEdit = false;

    // Khusus mode Petugas
    private JTextField txtCari;

    public FormCustomer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if ("Petugas".equals(Session.level)) {
            initComponentsPetugas();
        } else {
            initComponents();
        }
        loadData();
    }

    // ── Mode Admin: Full CRUD ────────────────────────────────────────────────

    private void initComponents() {
        JLabel lblTitle = new JLabel("MANAJEMEN DATA CUSTOMER", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Form Input Customer"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 8, 4, 8);

        txtId      = new JTextField(10);
        txtNama    = new JTextField(20);
        txtAlamat  = new JTextField(25);
        txtTelepon = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("ID Customer:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.3;
        inputPanel.add(txtId, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("No. Telepon:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.7;
        inputPanel.add(txtTelepon, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        inputPanel.add(new JLabel("Nama Customer:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 0.3; g.gridwidth = 3;
        inputPanel.add(txtNama, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        inputPanel.add(new JLabel("Alamat:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 0.3; g.gridwidth = 3;
        inputPanel.add(txtAlamat, g);

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

        buatTabel();

        JScrollPane scroll = new JScrollPane(tblCustomer);
        scroll.setBorder(UITheme.titledBorder("Daftar Customer"));

        tblCustomer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiFormAdmin(); }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(160);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);
    }

    // ── Mode Petugas: Input no. telepon → tampil data (seperti cek member) ──

    private void initComponentsPetugas() {
        JLabel lblTitle = new JLabel("CEK MEMBER CUSTOMER", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Kolom pencarian ──────────────────────────────────────────────────
        JPanel cariPanel = new JPanel(new BorderLayout(8, 4));
        cariPanel.setBorder(UITheme.titledBorder("Masukkan No. Telepon Member"));

        txtCari = new JTextField();
        txtCari.setFont(new Font("Arial", Font.BOLD, 14));
        txtCari.setToolTipText("Ketik nomor telepon customer...");

        JLabel lblCari = new JLabel("  No. Telepon: ");
        lblCari.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblHint = new JLabel("  * Ketik nomor telepon, data member akan muncul otomatis");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);

        cariPanel.add(lblCari,  BorderLayout.WEST);
        cariPanel.add(txtCari,  BorderLayout.CENTER);
        cariPanel.add(lblHint,  BorderLayout.SOUTH);

        // Filter tabel real-time saat mengetik
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTabel(); }
            public void removeUpdate(DocumentEvent e)  { filterTabel(); }
            public void changedUpdate(DocumentEvent e) { filterTabel(); }
        });

        // ── Detail (read-only) ───────────────────────────────────────────────
        txtId      = new JTextField(); txtId.setEditable(false); txtId.setBackground(Color.LIGHT_GRAY);
        txtNama    = new JTextField(); txtNama.setEditable(false); txtNama.setBackground(Color.LIGHT_GRAY);
        txtAlamat  = new JTextField(); txtAlamat.setEditable(false); txtAlamat.setBackground(Color.LIGHT_GRAY);
        txtTelepon = new JTextField(); txtTelepon.setEditable(false); txtTelepon.setBackground(Color.LIGHT_GRAY);

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBorder(UITheme.titledBorder("Detail Customer"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 8, 4, 8);

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        detailPanel.add(new JLabel("ID Customer:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.3;
        detailPanel.add(txtId, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        detailPanel.add(new JLabel("No. Telepon:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.7;
        detailPanel.add(txtTelepon, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        detailPanel.add(new JLabel("Nama Customer:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1; g.gridwidth = 3;
        detailPanel.add(txtNama, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        detailPanel.add(new JLabel("Alamat:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1; g.gridwidth = 3;
        detailPanel.add(txtAlamat, g);

        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        topPanel.add(cariPanel,   BorderLayout.NORTH);
        topPanel.add(detailPanel, BorderLayout.CENTER);

        // ── Tabel hasil ──────────────────────────────────────────────────────
        buatTabel();

        tblCustomer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiFormPetugas(); }
        });

        JScrollPane scroll = new JScrollPane(tblCustomer);
        scroll.setBorder(UITheme.titledBorder("Hasil Pencarian"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(185);
        split.setResizeWeight(0.35);
        add(split, BorderLayout.CENTER);
    }

    // ── Shared Table ─────────────────────────────────────────────────────────

    private void buatTabel() {
        tableModel = new DefaultTableModel(
                new String[]{"ID Customer", "Nama Customer", "Alamat", "No. Telepon"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustomer = new JTable(tableModel);
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(tblCustomer);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(200);
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void refresh() { loadData(); }

    private void loadData() {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM tb_customer ORDER BY nama_customer");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("alamat"),
                    rs.getString("telepon")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    // Cari customer berdasarkan no. telepon (mode Petugas)
    private void filterTabel() {
        String noTelp = txtCari.getText().trim();
        tableModel.setRowCount(0);
        txtId.setText(""); txtNama.setText(""); txtAlamat.setText(""); txtTelepon.setText("");

        if (noTelp.isEmpty()) return;

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM tb_customer WHERE telepon LIKE ? ORDER BY nama_customer")) {
            ps.setString(1, "%" + noTelp + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("id_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("alamat"),
                        rs.getString("telepon")
                    });
                }
            }
        } catch (SQLException e) {
            showError(e);
        }

        // Jika tepat 1 hasil ditemukan, langsung tampilkan semua data
        if (tableModel.getRowCount() == 1) {
            tblCustomer.setRowSelectionInterval(0, 0);
            isiFormPetugas();
        }
    }

    // Isi field detail saat baris tabel diklik (mode Petugas)
    private void isiFormPetugas() {
        int row = tblCustomer.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtNama.setText(tableModel.getValueAt(row, 1).toString());
        txtAlamat.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtTelepon.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
    }

    // Isi form CRUD saat baris tabel diklik (mode Admin)
    private void isiFormAdmin() {
        int row = tblCustomer.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtNama.setText(tableModel.getValueAt(row, 1).toString());
        txtAlamat.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtTelepon.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        isEdit = true;
        btnSimpan.setEnabled(false);
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    // ── CRUD (Admin only) ────────────────────────────────────────────────────

    private String generateNextId() {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return "CST001";
        try (PreparedStatement ps = con.prepareStatement("SELECT MAX(id_customer) FROM tb_customer");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).replaceAll("[^0-9]", "")) + 1;
                return "CST" + String.format("%03d", num);
            }
        } catch (Exception e) { /* pakai default */ }
        return "CST001";
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama customer harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String newId = generateNextId();
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) VALUES (?,?,?,?)")) {
            ps.setString(1, newId);
            ps.setString(2, nama);
            ps.setString(3, txtAlamat.getText().trim());
            ps.setString(4, txtTelepon.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Customer berhasil disimpan dengan ID: " + newId, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) { showError(e); }
    }

    private void ubah() {
        if (!isEdit) return;
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama customer harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=? WHERE id_customer=?")) {
            ps.setString(1, nama);
            ps.setString(2, txtAlamat.getText().trim());
            ps.setString(3, txtTelepon.getText().trim());
            ps.setString(4, txtId.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data customer berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) { showError(e); }
    }

    private void hapus() {
        if (!isEdit) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus customer ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM tb_customer WHERE id_customer = ?")) {
            ps.setString(1, txtId.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus! Customer masih memiliki data transaksi.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkan() {
        txtId.setText(""); txtNama.setText(""); txtAlamat.setText(""); txtTelepon.setText("");
        txtNama.requestFocus();
        isEdit = false;
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        tblCustomer.clearSelection();
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
