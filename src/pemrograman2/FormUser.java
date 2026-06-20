package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Manajemen Data User (khusus Admin).
 * Admin dapat menambah, mengubah, dan menghapus akun pengguna (Admin / Petugas).
 */
public class FormUser extends JPanel {

    private JTextField        txtId, txtUsername, txtNama;
    private JPasswordField    txtPassword;
    private JComboBox<String> cboLevel;
    private JButton           btnSimpan, btnUbah, btnHapus, btnBersihkan;
    private JLabel            lblPassHint;
    private JTable            tblUser;
    private DefaultTableModel tableModel;
    private boolean           isEdit = false;

    public FormUser() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("MANAJEMEN DATA USER", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Form Input User"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 10, 7, 10);

        txtId       = new JTextField(10);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtNama     = new JTextField(20);

        // Tanpa filter ketik: divalidasi saat Simpan & ditampilkan via popup.
        cboLevel    = new JComboBox<>(new String[]{"Petugas", "Admin"});
        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);

        // Baris 0: ID User | Level
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("ID User:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.3;
        inputPanel.add(txtId, g);
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        inputPanel.add(new JLabel("Level / Hak Akses:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.7;
        inputPanel.add(cboLevel, g);

        // Baris 1: Username | Nama Lengkap
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        inputPanel.add(new JLabel("Username:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 0.3;
        inputPanel.add(txtUsername, g);
        g.gridx = 2; g.gridy = 1; g.weightx = 0;
        inputPanel.add(new JLabel("Nama Lengkap:"), g);
        g.gridx = 3; g.gridy = 1; g.weightx = 0.7;
        inputPanel.add(txtNama, g);

        // Baris 2: Password
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        inputPanel.add(new JLabel("Password:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtPassword, g);

        // Baris 3: hint password
        lblPassHint = new JLabel(" ");
        lblPassHint.setFont(UITheme.FONT_SMALL);
        lblPassHint.setForeground(Color.GRAY);
        g.gridx = 1; g.gridy = 3; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(lblPassHint, g);
        g.gridwidth = 1;

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

        JScrollPane scroll = new JScrollPane(tblUser);
        scroll.setBorder(UITheme.titledBorder("Daftar User"));

        tblUser.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiForm(); }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(200);
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);
    }

    private void buatTabel() {
        tableModel = new DefaultTableModel(
                new String[]{"ID User", "Username", "Nama Lengkap", "Level"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUser = new JTable(tableModel);
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(tblUser);
        tblUser.getColumnModel().getColumn(0).setPreferredWidth(70);
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void refresh() { loadData(); }

    private void loadData() {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_user, username, nama_lengkap, level FROM tb_user ORDER BY id_user");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("level")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void isiForm() {
        int row = tblUser.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtUsername.setText(tableModel.getValueAt(row, 1).toString());
        txtNama.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        cboLevel.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtPassword.setText("");
        lblPassHint.setText("* Kosongkan password jika tidak ingin mengubahnya");

        isEdit = true;
        btnSimpan.setEnabled(false);
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    // ── CRUD ───────────────────────────────────────────────────────────────────

    private void simpan() {
        String username = txtUsername.getText().trim();
        String nama     = txtNama.getText().trim();
        String password = new String(txtPassword.getPassword());
        String level    = (String) cboLevel.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (username.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh mengandung spasi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (!Validasi.isNama(this, txtNama, "Nama lengkap")) return;
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        if (usernameSudahAda(username, -1)) {
            JOptionPane.showMessageDialog(this, "Username \"" + username + "\" sudah digunakan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_user (username, password, nama_lengkap, level) VALUES (?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, nama);
            ps.setString(4, level);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "User \"" + username + "\" berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) { showError(e); }
    }

    private void ubah() {
        if (!isEdit) return;
        int idUser      = Integer.parseInt(txtId.getText());
        String username = txtUsername.getText().trim();
        String nama     = txtNama.getText().trim();
        String password = new String(txtPassword.getPassword());
        String level    = (String) cboLevel.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (username.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh mengandung spasi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (!Validasi.isNama(this, txtNama, "Nama lengkap")) return;
        if (usernameSudahAda(username, idUser)) {
            JOptionPane.showMessageDialog(this, "Username \"" + username + "\" sudah digunakan user lain!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try {
            if (password.isEmpty()) {
                // Update tanpa mengubah password
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_user SET username=?, nama_lengkap=?, level=? WHERE id_user=?")) {
                    ps.setString(1, username);
                    ps.setString(2, nama);
                    ps.setString(3, level);
                    ps.setInt(4, idUser);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_user SET username=?, password=?, nama_lengkap=?, level=? WHERE id_user=?")) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, nama);
                    ps.setString(4, level);
                    ps.setInt(5, idUser);
                    ps.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Data user berhasil diubah.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) { showError(e); }
    }

    private void hapus() {
        if (!isEdit) return;
        int idUser = Integer.parseInt(txtId.getText());

        // Jangan biarkan admin menghapus akunnya sendiri (sedang login)
        if (idUser == Session.idUser) {
            JOptionPane.showMessageDialog(this,
                    "Anda tidak dapat menghapus akun yang sedang digunakan untuk login.",
                    "Tidak Diizinkan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cegah penghapusan admin terakhir
        if ("Admin".equals(cboLevel.getSelectedItem()) && jumlahAdmin() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "Tidak dapat menghapus admin terakhir.\nMinimal harus ada satu akun Admin.",
                    "Tidak Diizinkan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus user \"" + txtUsername.getText() + "\"?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM tb_user WHERE id_user = ?")) {
            ps.setInt(1, idUser);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus! User mungkin masih memiliki data transaksi.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkan() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtNama.setText("");
        cboLevel.setSelectedIndex(0);
        lblPassHint.setText(" ");
        txtUsername.requestFocus();
        isEdit = false;
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        tblUser.clearSelection();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /** Cek apakah username sudah dipakai. exceptId = -1 untuk insert baru. */
    private boolean usernameSudahAda(String username, int exceptId) {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return false;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_user FROM tb_user WHERE username = ? AND id_user <> ?")) {
            ps.setString(1, username);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            showError(e);
            return false;
        }
    }

    private int jumlahAdmin() {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return 0;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tb_user WHERE level = 'Admin'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { showError(e); }
        return 0;
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
