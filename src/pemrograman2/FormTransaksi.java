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
    private final List<String>  barangNamaList   = new ArrayList<>();
    private final List<Double>  barangHargaList  = new ArrayList<>();
    private final List<Integer> barangStokList   = new ArrayList<>();
    private final List<String>  barangSatuanList = new ArrayList<>();

    // ── Customer state ────────────────────────────────────────────────────────
    private String foundCustomerId   = null;  // null = Non-Member
    private String foundCustomerNama = null;

    // ── Keranjang (item-item dalam 1 transaksi) ───────────────────────────────
    private final List<CartItem> keranjang = new ArrayList<>();
    private boolean suppressComboEvent = false;

    private static class CartItem {
        String idBarang, namaBarang, satuan;
        double harga;
        int    jumlah;
        double subtotal() { return harga * jumlah; }
    }

    // ── Components ───────────────────────────────────────────────────────────
    private JRadioButton       rbNonMember, rbMember;
    private JPanel             memberSearchPanel;
    private JTextField         txtTelpCari;
    private JButton            btnCariMember;
    private JLabel             lblMemberInfo;

    private JComboBox<String>  cboBarang;
    private JTextField         txtTanggal, txtHarga, txtTotal, txtJumlah;
    private JLabel             lblStokInfo;
    private JButton            btnTambah, btnHapusItem, btnKosongkan;
    private JButton            btnSimpan, btnBersihkan;

    private JTable             tblKeranjang;
    private DefaultTableModel  keranjangModel;
    private JLabel             lblGrandTotal;

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

        // ── Subtotal item ────────────────────────────────────────────────────
        txtTotal = new JTextField("0", 12);
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(220, 255, 220));
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblTotalLbl = new JLabel("Subtotal Item (Rp):");
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
        txtJumlah.addActionListener(e -> tambahKeKeranjang());  // Enter = tambah

        // ── Button row input (Tambah ke Keranjang) ────────────────────────────
        JPanel inputBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnTambah    = new JButton("+ Tambah ke Keranjang");
        btnBersihkan = new JButton("Bersihkan Input");
        btnTambah.addActionListener(e -> tambahKeKeranjang());
        btnBersihkan.addActionListener(e -> bersihkanInput());
        UITheme.styleButton(btnTambah,    UITheme.PRIMARY);
        UITheme.styleButton(btnBersihkan, UITheme.NEUTRAL);
        inputBtnPanel.add(btnTambah);
        inputBtnPanel.add(btnBersihkan);

        JPanel inputArea = new JPanel(new BorderLayout());
        inputArea.add(inputPanel,    BorderLayout.CENTER);
        inputArea.add(inputBtnPanel, BorderLayout.SOUTH);

        // ── Panel Keranjang ────────────────────────────────────────────────────
        keranjangModel = new DefaultTableModel(
                new String[]{"ID Barang", "Nama Barang", "Harga Satuan", "Jumlah", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKeranjang = new JTable(keranjangModel);
        UITheme.styleTable(tblKeranjang);

        JScrollPane scrollKeranjang = new JScrollPane(tblKeranjang);

        // Footer keranjang: grand total + tombol aksi
        lblGrandTotal = new JLabel("TOTAL BAYAR: Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGrandTotal.setForeground(UITheme.PRIMARY_DARK);
        lblGrandTotal.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        btnHapusItem = new JButton("Hapus Item");
        btnKosongkan = new JButton("Kosongkan");
        btnSimpan    = new JButton("Pembayaran");
        btnHapusItem.addActionListener(e -> hapusItem());
        btnKosongkan.addActionListener(e -> kosongkanKeranjang());
        btnSimpan.addActionListener(e -> simpanTransaksi());
        UITheme.styleButton(btnHapusItem, UITheme.DANGER);
        UITheme.styleButton(btnKosongkan, UITheme.NEUTRAL);
        UITheme.styleButton(btnSimpan,    UITheme.SUCCESS);

        JPanel keranjangBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        keranjangBtns.add(btnHapusItem);
        keranjangBtns.add(btnKosongkan);
        keranjangBtns.add(btnSimpan);

        JPanel keranjangFooter = new JPanel(new BorderLayout());
        keranjangFooter.add(lblGrandTotal, BorderLayout.WEST);
        keranjangFooter.add(keranjangBtns, BorderLayout.EAST);

        JPanel keranjangPanel = new JPanel(new BorderLayout());
        keranjangPanel.setBorder(UITheme.titledBorder("Keranjang Belanja (Transaksi Saat Ini)"));
        keranjangPanel.add(scrollKeranjang, BorderLayout.CENTER);
        keranjangPanel.add(keranjangFooter, BorderLayout.SOUTH);

        // ── Tabel Riwayat ─────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
                new String[]{"ID Jual", "No. Faktur", "Tanggal", "Customer", "Barang", "Jumlah", "Harga Satuan", "Total Bayar"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTransaksi = new JTable(tableModel);
        UITheme.styleTable(tblTransaksi);

        JScrollPane scroll = new JScrollPane(tblTransaksi);
        scroll.setBorder(UITheme.titledBorder("Riwayat Transaksi Hari Ini"));

        // ── Susun layout ────────────────────────────────────────────────────────
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, keranjangPanel, scroll);
        bottomSplit.setResizeWeight(0.5);
        bottomSplit.setDividerLocation(200);

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.add(inputArea,   BorderLayout.NORTH);
        center.add(bottomSplit, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
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
            foundCustomerId   = null;
            foundCustomerNama = null;
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
            foundCustomerId   = null;
            foundCustomerNama = null;
            return;
        }

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_customer, nama_customer, telepon FROM tb_customer WHERE telepon = ?")) {
            ps.setString(1, telp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    foundCustomerId   = rs.getString("id_customer");
                    foundCustomerNama = rs.getString("nama_customer");
                    String noTelp     = rs.getString("telepon");
                    lblMemberInfo.setText(
                        "<html><font color='green'>&#10003; <b>" + foundCustomerNama +
                        "</b>  |  ID: " + foundCustomerId +
                        "  |  Telp: " + noTelp + "</font></html>");
                    lblMemberInfo.setForeground(UITheme.SUCCESS);
                } else {
                    foundCustomerId   = null;
                    foundCustomerNama = null;
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
        if (suppressComboEvent) return;
        int idx = cboBarang.getSelectedIndex();
        if (idx < 0 || idx >= barangHargaList.size()) return;

        double harga  = barangHargaList.get(idx);
        int    sisa   = sisaStok(idx);   // stok DB dikurangi yang sudah di keranjang
        String satuan = barangSatuanList.get(idx);

        txtHarga.setText(String.format("%.0f", harga));

        String stokColor = sisa > 10 ? "green" : (sisa > 0 ? "orange" : "red");
        lblStokInfo.setText("<html>Stok: <b><font color='" + stokColor + "'>" +
                sisa + " " + satuan + "</font></b></html>");

        hitungTotal();
    }

    /** Jumlah barang ini yang sudah ada di keranjang. */
    private int qtyDiKeranjang(String idBarang) {
        int q = 0;
        for (CartItem c : keranjang) if (c.idBarang.equals(idBarang)) q += c.jumlah;
        return q;
    }

    /** Sisa stok yang masih bisa dibeli = stok DB - yang sudah di keranjang. */
    private int sisaStok(int idx) {
        return barangStokList.get(idx) - qtyDiKeranjang(barangIdList.get(idx));
    }

    /** Perbarui teks dropdown barang agar menampilkan sisa stok terkini. */
    private void refreshComboStok() {
        if (barangIdList.isEmpty()) return;
        suppressComboEvent = true;
        int sel = cboBarang.getSelectedIndex();
        cboBarang.removeAllItems();
        for (int i = 0; i < barangIdList.size(); i++) {
            cboBarang.addItem(barangIdList.get(i) + " — " + barangNamaList.get(i)
                    + "  [Stok: " + sisaStok(i) + "]");
        }
        if (sel >= 0 && sel < cboBarang.getItemCount()) cboBarang.setSelectedIndex(sel);
        suppressComboEvent = false;
        onBarangSelected();
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

    // ── Keranjang ──────────────────────────────────────────────────────────────

    private void tambahKeKeranjang() {
        int idx = cboBarang.getSelectedIndex();
        if (idx < 0 || barangIdList.isEmpty()) {
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

        String idBarang = barangIdList.get(idx);
        int    stok     = barangStokList.get(idx);

        // Jumlah yang sudah ada di keranjang untuk barang yang sama
        int sudahDiKeranjang = 0;
        CartItem existing = null;
        for (CartItem c : keranjang) {
            if (c.idBarang.equals(idBarang)) { existing = c; sudahDiKeranjang = c.jumlah; break; }
        }

        if (sudahDiKeranjang + jumlah > stok) {
            JOptionPane.showMessageDialog(this,
                    "<html>Stok tidak mencukupi!<br>" +
                    "Sudah di keranjang : <b>" + sudahDiKeranjang + "</b><br>" +
                    "Ditambah           : <b>" + jumlah + "</b><br>" +
                    "Stok tersedia      : <b>" + stok + "</b></html>",
                    "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existing != null) {
            existing.jumlah += jumlah;   // gabungkan dengan item yang sama
        } else {
            CartItem item   = new CartItem();
            item.idBarang   = idBarang;
            item.namaBarang = barangNamaList.get(idx);
            item.satuan     = barangSatuanList.get(idx);
            item.harga      = barangHargaList.get(idx);
            item.jumlah     = jumlah;
            keranjang.add(item);
        }

        refreshKeranjangTable();

        // Reset input jumlah agar siap menambah barang berikutnya
        txtJumlah.setText("");
        txtTotal.setText("0");
        cboBarang.requestFocus();
    }

    private void hapusItem() {
        int row = tblKeranjang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih item di keranjang yang ingin dihapus terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        keranjang.remove(row);
        refreshKeranjangTable();
    }

    private void kosongkanKeranjang() {
        if (keranjang.isEmpty()) return;
        int ok = JOptionPane.showConfirmDialog(this,
                "Kosongkan seluruh keranjang?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            keranjang.clear();
            refreshKeranjangTable();
        }
    }

    private void refreshKeranjangTable() {
        keranjangModel.setRowCount(0);
        double grand = 0;
        for (CartItem c : keranjang) {
            grand += c.subtotal();
            keranjangModel.addRow(new Object[]{
                c.idBarang,
                c.namaBarang,
                String.format("%,.0f", c.harga),
                c.jumlah + (c.satuan != null && !c.satuan.isEmpty() ? " " + c.satuan : ""),
                String.format("%,.0f", c.subtotal())
            });
        }
        lblGrandTotal.setText("TOTAL BAYAR: Rp " + String.format("%,.0f", grand));
        refreshComboStok();   // tampilan sisa stok ikut menyesuaikan isi keranjang
    }

    // ── Data Load ─────────────────────────────────────────────────────────────

    public void refresh() {
        loadBarang();
        refreshComboStok();   // sesuaikan tampilan stok dengan isi keranjang (bila ada)
        loadRiwayat();
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private void loadBarang() {
        int prevIdx = cboBarang.getSelectedIndex();
        cboBarang.removeAllItems();
        barangIdList.clear();
        barangNamaList.clear();
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
                barangNamaList.add(rs.getString("nama_barang"));
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
                "SELECT p.id_jual, p.no_faktur, p.tgl_transaksi, c.nama_customer, b.nama_barang, " +
                "d.jumlah_beli, d.harga_satuan, d.subtotal " +
                "FROM tb_penjualan p " +
                "JOIN tb_detail_penjualan d ON p.id_jual    = d.id_jual " +
                "LEFT JOIN tb_customer     c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang            b ON d.id_barang   = b.id_barang " +
                "WHERE p.tgl_transaksi = CURDATE() " +
                "ORDER BY p.id_jual DESC, d.id_detail");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nama = rs.getString("nama_customer");
                tableModel.addRow(new Object[]{
                    rs.getInt("id_jual"),
                    rs.getString("no_faktur"),
                    rs.getString("tgl_transaksi"),
                    nama != null ? nama : "Non-Member",
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_beli"),
                    String.format("%,.0f", rs.getDouble("harga_satuan")),
                    String.format("%,.0f", rs.getDouble("subtotal"))
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

        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang masih kosong!\nTambahkan minimal satu barang terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0;
        for (CartItem c : keranjang) total += c.subtotal();

        // ── Pembayaran: input uang dibayar & hitung kembalian ─────────────────
        Double uangBayar = mintaPembayaran(total);
        if (uangBayar == null) return;            // dibatalkan oleh kasir
        double kembalian = uangBayar - total;

        String idCustomer = foundCustomerId;   // null = Non-Member

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try {
            con.setAutoCommit(false);

            // Cek & lock stok untuk setiap item di keranjang
            for (CartItem c : keranjang) {
                int stokTersedia;
                try (PreparedStatement psCheck = con.prepareStatement(
                        "SELECT stok FROM tb_barang WHERE id_barang = ? FOR UPDATE")) {
                    psCheck.setString(1, c.idBarang);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            JOptionPane.showMessageDialog(this,
                                    "Barang " + c.idBarang + " tidak ditemukan!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        stokTersedia = rs.getInt("stok");
                    }
                }
                if (c.jumlah > stokTersedia) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this,
                            "<html>Stok tidak mencukupi untuk <b>" + c.namaBarang + "</b>!<br>" +
                            "Diminta : <b>" + c.jumlah + "</b><br>" +
                            "Tersedia: <b>" + stokTersedia + "</b></html>",
                            "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // INSERT header penjualan -> ambil id_jual
            int idJual;
            try (PreparedStatement psIns = con.prepareStatement(
                    "INSERT INTO tb_penjualan " +
                    "(tgl_transaksi, id_customer, total_bayar, id_user) " +
                    "VALUES (CURDATE(), ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                if (idCustomer != null) psIns.setString(1, idCustomer);
                else                    psIns.setNull(1, java.sql.Types.VARCHAR);
                psIns.setDouble(2, total);
                psIns.setInt(3, Session.idUser);
                psIns.executeUpdate();
                try (ResultSet gk = psIns.getGeneratedKeys()) {
                    if (!gk.next()) {
                        con.rollback();
                        JOptionPane.showMessageDialog(this,
                                "Gagal membuat ID penjualan!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    idJual = gk.getInt(1);
                }
            }

            // Set no_faktur (format INV-0001)
            try (PreparedStatement psFak = con.prepareStatement(
                    "UPDATE tb_penjualan SET no_faktur = ? WHERE id_jual = ?")) {
                psFak.setString(1, String.format("INV-%04d", idJual));
                psFak.setInt(2, idJual);
                psFak.executeUpdate();
            }

            // INSERT detail penjualan (batch) + kurangi stok per item
            try (PreparedStatement psDet = con.prepareStatement(
                    "INSERT INTO tb_detail_penjualan " +
                    "(id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)");
                 PreparedStatement psUpd = con.prepareStatement(
                    "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?")) {
                for (CartItem c : keranjang) {
                    psDet.setInt(1, idJual);
                    psDet.setString(2, c.idBarang);
                    psDet.setDouble(3, c.harga);
                    psDet.setInt(4, c.jumlah);
                    psDet.setDouble(5, c.subtotal());
                    psDet.addBatch();

                    psUpd.setInt(1, c.jumlah);
                    psUpd.setString(2, c.idBarang);
                    psUpd.addBatch();
                }
                psDet.executeBatch();
                psUpd.executeBatch();
            }

            con.commit();

            JOptionPane.showMessageDialog(this,
                    "<html>Transaksi berhasil!<br>" +
                    "No. Faktur : <b>" + String.format("INV-%04d", idJual) + "</b><br>" +
                    "Customer   : <b>" + (idCustomer != null ? foundCustomerNama : "Non-Member") + "</b><br>" +
                    "Jumlah Item: <b>" + keranjang.size() + " jenis barang</b><br>" +
                    "Total      : <b>Rp " + String.format("%,.0f", total) + "</b><br>" +
                    "Tunai      : <b>Rp " + String.format("%,.0f", uangBayar) + "</b><br>" +
                    "Kembalian  : <b>Rp " + String.format("%,.0f", kembalian) + "</b></html>",
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

    /**
     * Dialog pembayaran: tampilkan total, minta uang dibayar, hitung kembalian.
     * Mengembalikan jumlah uang yang dibayar, atau null bila dibatalkan.
     */
    private Double mintaPembayaran(double total) {
        JTextField txtBayar = new JTextField(14);
        txtBayar.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblTotalVal = new JLabel("Rp " + String.format("%,.0f", total));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel lblKembalianVal = new JLabel("Rp 0");
        lblKembalianVal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Hitung ulang kembalian setiap kali nominal diketik
        Runnable hitung = () -> {
            try {
                double bayar   = Double.parseDouble(txtBayar.getText().trim().replaceAll("[^0-9]", ""));
                double kembali = bayar - total;
                lblKembalianVal.setText("Rp " + String.format("%,.0f", kembali));
                lblKembalianVal.setForeground(kembali < 0 ? UITheme.DANGER : new Color(0, 140, 0));
            } catch (NumberFormatException ex) {
                lblKembalianVal.setText("Rp 0");
                lblKembalianVal.setForeground(Color.BLACK);
            }
        };
        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { hitung.run(); }
            public void removeUpdate(DocumentEvent e)  { hitung.run(); }
            public void changedUpdate(DocumentEvent e) { hitung.run(); }
        });

        JButton btnPas = new JButton("Uang Pas");
        btnPas.setFocusPainted(false);
        btnPas.addActionListener(e -> txtBayar.setText(String.format("%.0f", total)));

        JPanel bayarRow = new JPanel(new BorderLayout(6, 0));
        bayarRow.add(txtBayar, BorderLayout.CENTER);
        bayarRow.add(btnPas,   BorderLayout.EAST);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        panel.add(boldLabel("Total Belanja:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 1;
        panel.add(lblTotalVal, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        panel.add(boldLabel("Uang Dibayar:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1;
        panel.add(bayarRow, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        panel.add(boldLabel("Kembalian:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1;
        panel.add(lblKembalianVal, g);

        while (true) {
            int res = JOptionPane.showConfirmDialog(this, panel, "Pembayaran",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return null;   // Batal

            double bayar;
            try {
                bayar = Double.parseDouble(txtBayar.getText().trim().replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Masukkan jumlah uang yang dibayar terlebih dahulu!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (bayar < total) {
                JOptionPane.showMessageDialog(this,
                        "<html>Uang yang dibayar kurang!<br>" +
                        "Total    : <b>Rp " + String.format("%,.0f", total) + "</b><br>" +
                        "Dibayar  : <b>Rp " + String.format("%,.0f", bayar) + "</b><br>" +
                        "Kurang   : <b>Rp " + String.format("%,.0f", total - bayar) + "</b></html>",
                        "Pembayaran Kurang", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            return bayar;
        }
    }

    /** Bersihkan hanya bagian input barang (keranjang tetap). */
    private void bersihkanInput() {
        txtJumlah.setText("");
        txtTotal.setText("0");
        if (cboBarang.getItemCount() > 0) {
            cboBarang.setSelectedIndex(0);
            onBarangSelected();
        }
        txtJumlah.requestFocus();
    }

    /** Reset seluruh form setelah transaksi tersimpan. */
    private void bersihkan() {
        // Reset customer section
        rbNonMember.setSelected(true);
        memberSearchPanel.setVisible(false);
        txtTelpCari.setText("");
        lblMemberInfo.setText("Masukkan nomor telepon member lalu tekan Cari");
        lblMemberInfo.setForeground(Color.GRAY);
        foundCustomerId   = null;
        foundCustomerNama = null;

        // Reset keranjang
        keranjang.clear();
        refreshKeranjangTable();

        // Reset barang & amount
        txtJumlah.setText("");
        txtHarga.setText("0");
        txtTotal.setText("0");
        lblStokInfo.setText("Stok: -");

        loadBarang();
        loadRiwayat();
        if (cboBarang.getItemCount() > 0) {
            cboBarang.setSelectedIndex(0);
            onBarangSelected();
        }
        txtJumlah.requestFocus();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel boldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        return lbl;
    }
}
