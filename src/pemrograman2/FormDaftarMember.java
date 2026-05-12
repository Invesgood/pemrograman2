package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

public class FormDaftarMember extends JPanel {

    private JTextField        txtNama, txtAlamat, txtTelepon;
    private JTextField        txtCari;
    private JTable            tblMember;
    private DefaultTableModel tableModel;
    private JLabel            lblTotal;

    public FormDaftarMember() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadData("");
    }

    private void initComponents() {
        // ── Title ────────────────────────────────────────────────────────────
        JLabel lblTitle = new JLabel("PENDAFTARAN MEMBER BARU", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Form Pendaftaran ─────────────────────────────────────────────────
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Form Daftar Member Baru"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 8, 5, 8);

        txtNama    = new JTextField(20);
        txtAlamat  = new JTextField(25);
        txtTelepon = new JTextField(15);

        JLabel lNama    = new JLabel("Nama Lengkap:");
        JLabel lAlamat  = new JLabel("Alamat:");
        JLabel lTelepon = new JLabel("No. Telepon:");
        lNama.setFont(UITheme.FONT_BOLD);
        lAlamat.setFont(UITheme.FONT_BOLD);
        lTelepon.setFont(UITheme.FONT_BOLD);

        // Row 0: Nama
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(lNama, g);
        g.gridx = 1; g.gridy = 0; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtNama, g);

        // Row 1: Alamat
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        inputPanel.add(lAlamat, g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtAlamat, g);

        // Row 2: Telepon
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        inputPanel.add(lTelepon, g);
        g.gridx = 1; g.gridy = 2; g.weightx = 0.5;
        inputPanel.add(txtTelepon, g);

        // ── Tombol ──────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnDaftar    = new JButton("Daftarkan Member");
        JButton btnBersihkan = new JButton("Bersihkan");

        btnDaftar.addActionListener(e -> daftarMember());
        btnBersihkan.addActionListener(e -> bersihkan());
        UITheme.styleButton(btnDaftar,    UITheme.SUCCESS);
        UITheme.styleButton(btnBersihkan, UITheme.NEUTRAL);

        btnPanel.add(btnDaftar);
        btnPanel.add(btnBersihkan);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel,   BorderLayout.SOUTH);

        // ── Search + Tabel ───────────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));

        JLabel lblCari = new JLabel("Cari Member: ");
        lblCari.setFont(UITheme.FONT_BOLD);
        txtCari = new JTextField();
        txtCari.setFont(UITheme.FONT_BODY);
        txtCari.setToolTipText("Cari berdasarkan nama atau nomor telepon...");

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { loadData(txtCari.getText().trim()); }
            public void removeUpdate(DocumentEvent e)  { loadData(txtCari.getText().trim()); }
            public void changedUpdate(DocumentEvent e) { loadData(txtCari.getText().trim()); }
        });

        searchPanel.add(lblCari, BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(
                new String[]{"ID Customer", "Nama Customer", "Alamat", "No. Telepon"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMember = new JTable(tableModel);
        tblMember.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(tblMember);
        tblMember.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblMember.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblMember.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblMember.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(tblMember);
        scroll.setBorder(UITheme.titledBorder("Daftar Semua Member"));

        JPanel tableArea = new JPanel(new BorderLayout(0, 4));
        tableArea.add(searchPanel, BorderLayout.NORTH);
        tableArea.add(scroll,      BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────────────────
        lblTotal = new JLabel("Total member: 0");
        lblTotal.setFont(UITheme.FONT_BOLD);
        lblTotal.setForeground(UITheme.PRIMARY_DARK);
        lblTotal.setOpaque(true);
        lblTotal.setBackground(UITheme.PRIMARY_LIGHT);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.PRIMARY),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tableArea);
        split.setDividerLocation(175);
        split.setResizeWeight(0.35);

        add(split,    BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
    }

    public void refresh() {
        loadData(txtCari.getText().trim());
    }

    private void daftarMember() {
        String nama    = txtNama.getText().trim();
        String alamat  = txtAlamat.getText().trim();
        String telepon = txtTelepon.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama lengkap harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return;
        }
        if (telepon.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nomor telepon harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtTelepon.requestFocus();
            return;
        }

        String newId = generateNextId();
        if (newId == null) return;

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) VALUES (?,?,?,?)")) {
            ps.setString(1, newId);
            ps.setString(2, nama);
            ps.setString(3, alamat);
            ps.setString(4, telepon);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "<html>Member berhasil didaftarkan!<br>" +
                    "ID Member: <b>" + newId + "</b><br>" +
                    "Nama     : <b>" + nama + "</b></html>",
                    "Pendaftaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
            bersihkan();
            loadData("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateNextId() {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return null;
        try (PreparedStatement ps = con.prepareStatement("SELECT MAX(id_customer) FROM tb_customer");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).replaceAll("[^0-9]", "")) + 1;
                return "CST" + String.format("%03d", num);
            }
        } catch (Exception ignored) {}
        return "CST001";
    }

    private void bersihkan() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtNama.requestFocus();
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        String sql = keyword.isEmpty()
            ? "SELECT id_customer, nama_customer, alamat, telepon FROM tb_customer ORDER BY nama_customer"
            : "SELECT id_customer, nama_customer, alamat, telepon FROM tb_customer " +
              "WHERE nama_customer LIKE ? OR telepon LIKE ? ORDER BY nama_customer";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (!keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
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
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        int total = tableModel.getRowCount();
        lblTotal.setText("Total member terdaftar: " + total +
                (keyword.isEmpty() ? "" : "  (pencarian: \"" + keyword + "\")"));
    }
}
