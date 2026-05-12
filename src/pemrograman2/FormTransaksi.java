package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

public class FormTransaksi extends JPanel {

    // ── Barang data ───────────────────────────────────────────────────────────
    private final List<String>  barangIdList     = new ArrayList<>();
    private final List<Double>  barangHargaList  = new ArrayList<>();
    private final List<Integer> barangStokList   = new ArrayList<>();
    private final List<String>  barangSatuanList = new ArrayList<>();

    // ── Customer state ────────────────────────────────────────────────────────
    private String foundCustomerId   = null;  // null = Non-Member

    // ── Components ───────────────────────────────────────────────────────────
    private JRadioButton       rbNonMember, rbMember;
    private JPanel             memberSearchPanel;
    private JTextField         txtTelpCari;
    private JButton            btnCariMember;
    private JLabel             lblMemberInfo;

    private JComboBox<String>  cboBarang;
    private JTextField         txtTanggal, txtHarga, txtTotal, txtJumlah;
    private JLabel             lblStokInfo;
    private JButton            btnSimpan, btnBersihkan;

    private JTable             tblTransaksi;
    private DefaultTableModel  tableModel;

    public FormTransaksi() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("FORM TRANSAKSI PENJUALAN", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Input Panel ──────────────────────────────────────────────────────
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(UITheme.titledBorder("Detail Transaksi"));
        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;

        // ── Tanggal ──────────────────────────────────────────────────────────
        txtTanggal = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 12);
        txtTanggal.setEditable(false);
        txtTanggal.setBackground(new Color(235, 240, 250));

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        inputPanel.add(boldLabel("Tanggal:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtTanggal, g);

        // ── Customer Toggle (Non-Member / Member) ────────────────────────────
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        inputPanel.add(boldLabel("Customer:"), g);

        JPanel custTogglePanel = new JPanel(new BorderLayout(0, 6));
        custTogglePanel.setOpaque(false);

        // Radio row
        rbNonMember = new JRadioButton("Non-Member");
        rbMember    = new JRadioButton("Member (cari by No. Telepon)");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbNonMember);
        bg.add(rbMember);
        rbNonMember.setSelected(true);
        rbNonMember.setFont(UITheme.FONT_BODY);
        rbMember.setFont(UITheme.FONT_BODY);
        rbNonMember.setOpaque(false);
        rbMember.setOpaque(false);

        JPanel radioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        radioRow.setOpaque(false);
        radioRow.add(rbNonMember);
        radioRow.add(rbMember);

        // Member search panel (hidden by default)
        memberSearchPanel = buildMemberSearchPanel();
        memberSearchPanel.setVisible(false);

        custTogglePanel.add(radioRow,           BorderLayout.NORTH);
        custTogglePanel.add(memberSearchPanel,   BorderLayout.CENTER);

        rbNonMember.addActionListener(e -> onCustTypeChanged());
        rbMember.addActionListener(e -> onCustTypeChanged());

        g.gridx = 1; g.gridy = 1; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(custTogglePanel, g);

        // ── Barang ───────────────────────────────────────────────────────────
        g.gridwidth = 1;
        cboBarang = new JComboBox<>();
        lblStokInfo = new JLabel("Stok: -");
        lblStokInfo.setFont(UITheme.FONT_SMALL);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        inputPanel.add(boldLabel("Barang:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1; g.gridwidth = 2;
        inputPanel.add(cboBarang, g);
        g.gridwidth = 1;
        g.gridx = 3; g.gridy = 2; g.weightx = 0;
        inputPanel.add(lblStokInfo, g);

        // ── Harga & Jumlah ───────────────────────────────────────────────────
        txtHarga = new JTextField("0", 12);
        txtHarga.setEditable(false);
        txtHarga.setBackground(new Color(235, 240, 250));

        txtJumlah = new JTextField(6);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        inputPanel.add(boldLabel("Harga Satuan (Rp):"), g);
        g.gridx = 1; g.gridy = 3; g.weightx = 0.5;
        inputPanel.add(txtHarga, g);
        g.gridx = 2; g.gridy = 3; g.weightx = 0;
        inputPanel.add(boldLabel("Jumlah Beli:"), g);
        g.gridx = 3; g.gridy = 3; g.weightx = 0.5;
        inputPanel.add(txtJumlah, g);

        // ── Total ────────────────────────────────────────────────────────────
        txtTotal = new JTextField("0", 12);
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(220, 255, 220));
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblTotalLbl = new JLabel("TOTAL BAYAR (Rp):");
        lblTotalLbl.setFont(UITheme.FONT_BOLD);
        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        inputPanel.add(lblTotalLbl, g);
        g.gridx = 1; g.gridy = 4; g.weightx = 1; g.gridwidth = 3;
        inputPanel.add(txtTotal, g);

        // ── Events ───────────────────────────────────────────────────────────
        cboBarang.addActionListener(e -> onBarangSelected());

        txtJumlah.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { hitungTotal(); }
            public void removeUpdate(DocumentEvent e)  { hitungTotal(); }
            public void changedUpdate(DocumentEvent e) { hitungTotal(); }
        });

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnSimpan    = new JButton("Simpan Transaksi");
        btnBersihkan = new JButton("Bersihkan");
        btnSimpan.addActionListener(e -> simpanTransaksi());
        btnBersihkan.addActionListener(e -> bersihkan());
        UITheme.styleButton(btnSimpan,    UITheme.SUCCESS);
        UITheme.styleButton(btnBersihkan, UITheme.NEUTRAL);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBersihkan);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel,   BorderLayout.SOUTH);

        // ── Tabel Riwayat ─────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
                new String[]{"ID Jual", "Tanggal", "Customer", "Barang", "Jumlah", "Harga Satuan", "Total Bayar"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTransaksi = new JTable(tableModel);
        UITheme.styleTable(tblTransaksi);

        JScrollPane scroll = new JScrollPane(tblTransaksi);
        scroll.setBorder(UITheme.titledBorder("Riwayat Transaksi Hari Ini"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, scroll);
        split.setDividerLocation(280);
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);
    }

    // ── Member Search Panel ───────────────────────────────────────────────────

    private JPanel buildMemberSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        // Input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputRow.setOpaque(false);

        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setFont(UITheme.FONT_BOLD);

        txtTelpCari = new JTextField(16);
        txtTelpCari.setFont(UITheme.FONT_BODY);
        txtTelpCari.setToolTipText("Masukkan nomor telepon member, lalu tekan Enter atau klik Cari");

        btnCariMember = new JButton("Cari");
        UITheme.styleButton(btnCariMember, UITheme.PRIMARY);

        btnCariMember.addActionListener(e -> cariMember());
        txtTelpCari.addActionListener(e -> cariMember());  // Enter key

        inputRow.add(lblTelp);
        inputRow.add(txtTelpCari);
        inputRow.add(btnCariMember);

        // Info result
        lblMemberInfo = new JLabel("Masukkan nomor telepon member lalu tekan Cari");
        lblMemberInfo.setFont(UITheme.FONT_SMALL);
        lblMemberInfo.setForeground(Color.GRAY);
        lblMemberInfo.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        panel.add(inputRow,      BorderLayout.NORTH);
        panel.add(lblMemberInfo, BorderLayout.CENTER);
        return panel;
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    private void onCustTypeChanged() {
        boolean isMember = rbMember.isSelected();
        memberSearchPanel.setVisible(isMember);
        if (!isMember) {
            // Reset member state when switched to Non-Member
            foundCustomerId = null;
            txtTelpCari.setText("");
            lblMemberInfo.setText("Masukkan nomor telepon member lalu tekan Cari");
            lblMemberInfo.setForeground(Color.GRAY);
        } else {
            txtTelpCari.requestFocus();
        }
        revalidate();
        repaint();
    }

    private void cariMember() {
        String telp = txtTelpCari.getText().trim();
        if (telp.isEmpty()) {
            lblMemberInfo.setText("Nomor telepon tidak boleh kosong.");
            lblMemberInfo.setForeground(UITheme.DANGER);
            foundCustomerId = null;
            return;
        }

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_customer, nama_customer, telepon FROM tb_customer WHERE telepon = ?")) {
            ps.setString(1, telp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    foundCustomerId = rs.getString("id_customer");
                    String nama    = rs.getString("nama_customer");
                    String noTelp  = rs.getString("telepon");
                    lblMemberInfo.setText(
                        "<html><font color='green'>&#10003; <b>" + nama +
                        "</b>  |  ID: " + foundCustomerId +
                        "  |  Telp: " + noTelp + "</font></html>");
                    lblMemberInfo.setForeground(UITheme.SUCCESS);
                } else {
                    foundCustomerId = null;
                    lblMemberInfo.setText(
                        "<html><font color='red'>&#10007; Member dengan nomor telepon <b>\"" +
                        telp + "\"</b> tidak ditemukan.</font></html>");
                    lblMemberInfo.setForeground(UITheme.DANGER);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBarangSelected() {
        int idx = cboBarang.getSelectedIndex();
        if (idx < 0 || idx >= barangHargaList.size()) return;

        double harga  = barangHargaList.get(idx);
        int    stok   = barangStokList.get(idx);
        String satuan = barangSatuanList.get(idx);

        txtHarga.setText(String.format("%.0f", harga));

        String stokColor = stok > 10 ? "green" : (stok > 0 ? "orange" : "red");
        lblStokInfo.setText("<html>Stok: <b><font color='" + stokColor + "'>" +
                stok + " " + satuan + "</font></b></html>");

        hitungTotal();
    }

    private void hitungTotal() {
        try {
            double harga  = Double.parseDouble(txtHarga.getText().trim().replaceAll("[^0-9]", ""));
            int    jumlah = Integer.parseInt(txtJumlah.getText().trim());
            txtTotal.setText(String.format("%,.0f", harga * jumlah));
        } catch (NumberFormatException e) {
            txtTotal.setText("0");
        }
    }

    // ── Data Load ─────────────────────────────────────────────────────────────

    public void refresh() {
        loadBarang();
        loadRiwayat();
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private void loadBarang() {
        int prevIdx = cboBarang.getSelectedIndex();
        cboBarang.removeAllItems();
        barangIdList.clear();
        barangHargaList.clear();
        barangStokList.clear();
        barangSatuanList.clear();

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_barang, nama_barang, harga_jual, stok, satuan FROM tb_barang ORDER BY nama_barang");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                barangIdList.add(rs.getString("id_barang"));
                barangHargaList.add(rs.getDouble("harga_jual"));
                barangStokList.add(rs.getInt("stok"));
                barangSatuanList.add(rs.getString("satuan") != null ? rs.getString("satuan") : "");
                cboBarang.addItem(rs.getString("id_barang") + " — " + rs.getString("nama_barang")
                        + "  [Stok: " + rs.getInt("stok") + "]");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (prevIdx >= 0 && prevIdx < cboBarang.getItemCount()) cboBarang.setSelectedIndex(prevIdx);
    }

    private void loadRiwayat() {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT p.id_jual, p.tgl_transaksi, c.nama_customer, b.nama_barang, " +
                "p.jumlah_beli, b.harga_jual, p.total_bayar " +
                "FROM tb_penjualan p " +
                "LEFT JOIN tb_customer c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang        b ON p.id_barang   = b.id_barang " +
                "WHERE p.tgl_transaksi = CURDATE() " +
                "ORDER BY p.id_jual DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nama = rs.getString("nama_customer");
                tableModel.addRow(new Object[]{
                    rs.getInt("id_jual"),
                    rs.getString("tgl_transaksi"),
                    nama != null ? nama : "Non-Member",
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_beli"),
                    String.format("%,.0f", rs.getDouble("harga_jual")),
                    String.format("%,.0f", rs.getDouble("total_bayar"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Save Transaction ──────────────────────────────────────────────────────

    private void simpanTransaksi() {
        // Validasi: jika pilih Member tapi belum cari / tidak ditemukan
        if (rbMember.isSelected() && foundCustomerId == null) {
            JOptionPane.showMessageDialog(this,
                    "Silakan cari dan temukan member terlebih dahulu,\n" +
                    "atau pilih Non-Member jika pembeli bukan member.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtTelpCari.requestFocus();
            return;
        }

        if (cboBarang.getSelectedIndex() < 0 || barangIdList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih barang terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jumlahStr = txtJumlah.getText().trim();
        if (jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Masukkan jumlah beli!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus();
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah beli harus berupa angka bulat!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus();
            return;
        }
        if (jumlah <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah beli harus lebih dari 0!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    idxBarang  = cboBarang.getSelectedIndex();
        String idBarang   = barangIdList.get(idxBarang);
        double harga      = barangHargaList.get(idxBarang);
        double total      = harga * jumlah;
        String idCustomer = foundCustomerId;   // null = Non-Member

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try {
            con.setAutoCommit(false);

            // Cek & lock stok
            int stokTersedia;
            try (PreparedStatement psCheck = con.prepareStatement(
                    "SELECT stok FROM tb_barang WHERE id_barang = ? FOR UPDATE")) {
                psCheck.setString(1, idBarang);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        JOptionPane.showMessageDialog(this,
                                "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    stokTersedia = rs.getInt("stok");
                }
            }

            if (jumlah > stokTersedia) {
                con.rollback();
                JOptionPane.showMessageDialog(this,
                        "<html>Stok tidak mencukupi!<br>" +
                        "Diminta : <b>" + jumlah + "</b><br>" +
                        "Tersedia: <b>" + stokTersedia + "</b></html>",
                        "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // INSERT transaksi
            try (PreparedStatement psIns = con.prepareStatement(
                    "INSERT INTO tb_penjualan " +
                    "(tgl_transaksi, id_customer, id_barang, jumlah_beli, total_bayar, id_user) " +
                    "VALUES (CURDATE(), ?, ?, ?, ?, ?)")) {
                if (idCustomer != null) psIns.setString(1, idCustomer);
                else                    psIns.setNull(1, java.sql.Types.VARCHAR);
                psIns.setString(2, idBarang);
                psIns.setInt(3, jumlah);
                psIns.setDouble(4, total);
                psIns.setInt(5, Session.idUser);
                psIns.executeUpdate();
            }

            // Kurangi stok
            try (PreparedStatement psUpd = con.prepareStatement(
                    "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?")) {
                psUpd.setInt(1, jumlah);
                psUpd.setString(2, idBarang);
                psUpd.executeUpdate();
            }

            con.commit();

            String custLabel = idCustomer != null ? foundCustomerId + " — " +
                    lblMemberInfo.getText().replaceAll("<[^>]*>", "").replaceAll(".*✓ ", "").split("\\|")[0].trim()
                    : "Non-Member (Umum)";

            JOptionPane.showMessageDialog(this,
                    "<html>Transaksi berhasil!<br>" +
                    "Customer : <b>" + (idCustomer != null ? foundCustomerId : "Non-Member") + "</b><br>" +
                    "Total    : <b>Rp " + String.format("%,.0f", total) + "</b></html>",
                    "Transaksi Sukses", JOptionPane.INFORMATION_MESSAGE);

            bersihkan();

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { /* ignore */ }
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void bersihkan() {
        // Reset customer section
        rbNonMember.setSelected(true);
        memberSearchPanel.setVisible(false);
        txtTelpCari.setText("");
        lblMemberInfo.setText("Masukkan nomor telepon member lalu tekan Cari");
        lblMemberInfo.setForeground(Color.GRAY);
        foundCustomerId = null;

        // Reset barang & amount
        txtJumlah.setText("");
        txtHarga.setText("0");
        txtTotal.setText("0");
        lblStokInfo.setText("Stok: -");
        txtJumlah.requestFocus();

        loadBarang();
        loadRiwayat();
        if (cboBarang.getItemCount() > 0) {
            cboBarang.setSelectedIndex(0);
            onBarangSelected();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel boldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        return lbl;
    }
}
