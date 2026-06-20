package pemrograman2;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormLaporan extends JPanel {

    private JTextField        txtTglMulai, txtTglSelesai;
    private JTable            tblLaporan;
    private DefaultTableModel tableModel;
    private JLabel            lblTotalPendapatan, lblJmlTransaksi;

    public FormLaporan() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("LAPORAN PENJUALAN", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Filter Panel ─────────────────────────────────────────────────────
        String today = Validasi.hariIni();
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBorder(UITheme.titledBorder("Filter Tanggal"));

        txtTglMulai   = new JTextField(today, 12);
        txtTglSelesai = new JTextField(today, 12);
        // Tanpa filter ketik: tanggal divalidasi saat klik "Tampilkan" via popup.

        JButton btnTampilkan = new JButton("Tampilkan");
        JButton btnSemua     = new JButton("Semua Data");
        JButton btnCetak     = new JButton("Export PDF");

        btnTampilkan.addActionListener(e -> {
            if (!Validasi.isTanggal(this, txtTglMulai,   "Tanggal mulai",   true)) return;
            if (!Validasi.isTanggal(this, txtTglSelesai, "Tanggal selesai", true)) return;
            loadData(txtTglMulai.getText().trim(), txtTglSelesai.getText().trim());
        });
        btnSemua.addActionListener(e -> {
            txtTglMulai.setText("");
            txtTglSelesai.setText("");
            loadData(null, null);
        });
        btnCetak.addActionListener(e -> exportPdf());
        UITheme.styleButton(btnTampilkan, UITheme.PRIMARY);
        UITheme.styleButton(btnSemua,     UITheme.NEUTRAL);
        UITheme.styleButton(btnCetak,     UITheme.SUCCESS);

        filterPanel.add(new JLabel("Dari:"));
        filterPanel.add(txtTglMulai);
        filterPanel.add(new JLabel("s/d:"));
        filterPanel.add(txtTglSelesai);
        filterPanel.add(btnTampilkan);
        filterPanel.add(btnSemua);
        filterPanel.add(btnCetak);

        // ── Table ────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
                new String[]{"ID Jual", "No. Faktur", "Tanggal", "ID Customer", "Nama Customer",
                             "Nama Barang", "Satuan", "Qty", "Harga Satuan (Rp)", "Subtotal (Rp)", "Kasir"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLaporan = new JTable(tableModel);
        tblLaporan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        UITheme.styleTable(tblLaporan);
        // Atur lebar kolom
        int[] widths = {60, 90, 90, 90, 140, 140, 65, 45, 130, 130, 110};
        for (int i = 0; i < widths.length; i++) {
            tblLaporan.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(tblLaporan);
        scroll.setBorder(UITheme.titledBorder("Data Penjualan"));

        // ── Summary Panel ────────────────────────────────────────────────────
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setBackground(UITheme.PRIMARY_LIGHT);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.PRIMARY),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        lblJmlTransaksi    = new JLabel("Jumlah Transaksi: 0");
        lblTotalPendapatan = new JLabel("Total Pendapatan: Rp 0");

        lblJmlTransaksi.setFont(UITheme.FONT_BOLD);
        lblJmlTransaksi.setForeground(UITheme.PRIMARY_DARK);
        lblTotalPendapatan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalPendapatan.setForeground(UITheme.SUCCESS);

        summaryPanel.add(lblJmlTransaksi);
        summaryPanel.add(lblTotalPendapatan);

        // ── Layout ───────────────────────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);

        add(topPanel,     BorderLayout.NORTH);
        add(scroll,       BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void refresh() {
        // Saat tab dibuka, tampilkan data hari ini
        String today = Validasi.hariIni();
        txtTglMulai.setText(today);
        txtTglSelesai.setText(today);
        loadData(today, today);
    }

    /** Ekspor isi tabel laporan langsung ke berkas PDF (tanpa dialog printer). */
    private void exportPdf() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data untuk diekspor. Tampilkan data terlebih dahulu.",
                    "Export PDF", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String mulai   = txtTglMulai.getText().trim();
        String selesai = txtTglSelesai.getText().trim();
        String periode = (mulai.isEmpty() && selesai.isEmpty())
                ? "Semua Periode"
                : mulai + " s/d " + selesai;

        // Dialog pilih lokasi simpan, dengan nama berkas default.
        String defName = "Laporan_Penjualan_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan PDF");
        chooser.setSelectedFile(new java.io.File(defName));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Berkas PDF (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".pdf");
        }

        // Kumpulkan kolom + data dari tabel.
        int cols = tableModel.getColumnCount();
        String[] columns = new String[cols];
        for (int i = 0; i < cols; i++) columns[i] = tableModel.getColumnName(i);
        // Lebar kolom (point) untuk A4 landscape; jumlah = 782.
        int[] colWidths = {40, 70, 70, 55, 110, 110, 45, 35, 85, 85, 77};

        java.util.List<String[]> rows = new java.util.ArrayList<>();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String[] row = new String[cols];
            for (int ccol = 0; ccol < cols; ccol++) {
                Object v = tableModel.getValueAt(r, ccol);
                row[ccol] = v == null ? "" : v.toString();
            }
            rows.add(row);
        }

        String title  = "Laporan Penjualan Toko Berkah Jaya";
        String footer = lblTotalPendapatan.getText() + "    |    " + lblJmlTransaksi.getText();

        try {
            PdfExporter.exportTable(file, title, periode, columns, colWidths, rows, footer);

            int pil = JOptionPane.showConfirmDialog(this,
                    "Laporan berhasil disimpan ke:\n" + file.getAbsolutePath()
                            + "\n\nBuka berkas sekarang?",
                    "Export PDF", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (pil == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengekspor PDF:\n" + ex.getMessage(),
                    "Error Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData(String tglMulai, String tglSelesai) {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_jual, p.no_faktur, p.tgl_transaksi, " +
                "c.id_customer, c.nama_customer, " +
                "b.nama_barang, b.satuan, " +
                "d.jumlah_beli, d.harga_satuan, d.subtotal, " +
                "u.nama_lengkap " +
                "FROM tb_penjualan p " +
                "JOIN tb_detail_penjualan d ON p.id_jual     = d.id_jual " +
                "LEFT JOIN tb_customer     c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang            b ON d.id_barang   = b.id_barang " +
                "JOIN tb_user              u ON p.id_user     = u.id_user ");

        boolean hasFilter = (tglMulai != null && !tglMulai.isEmpty())
                         || (tglSelesai != null && !tglSelesai.isEmpty());

        if (hasFilter) {
            if (tglMulai != null && !tglMulai.isEmpty() && tglSelesai != null && !tglSelesai.isEmpty()) {
                sql.append("WHERE p.tgl_transaksi BETWEEN ? AND ? ");
            } else if (tglMulai != null && !tglMulai.isEmpty()) {
                sql.append("WHERE p.tgl_transaksi >= ? ");
            } else {
                sql.append("WHERE p.tgl_transaksi <= ? ");
            }
        }
        sql.append("ORDER BY p.tgl_transaksi DESC, p.id_jual DESC, d.id_detail");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (hasFilter) {
                if (tglMulai != null && !tglMulai.isEmpty() && tglSelesai != null && !tglSelesai.isEmpty()) {
                    ps.setString(1, Validasi.tglKeSql(tglMulai));
                    ps.setString(2, Validasi.tglKeSql(tglSelesai));
                } else if (tglMulai != null && !tglMulai.isEmpty()) {
                    ps.setString(1, Validasi.tglKeSql(tglMulai));
                } else {
                    ps.setString(1, Validasi.tglKeSql(tglSelesai));
                }
            }

            double totalPendapatan = 0;
            int    jmlTransaksi    = 0;

            java.util.Set<Integer> fakturSet = new java.util.HashSet<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double subtotal = rs.getDouble("subtotal");
                    totalPendapatan += subtotal;
                    fakturSet.add(rs.getInt("id_jual"));
                    String idCust   = rs.getString("id_customer");
                    String namaCust = rs.getString("nama_customer");
                    tableModel.addRow(new Object[]{
                        rs.getInt("id_jual"),
                        rs.getString("no_faktur"),
                        Validasi.tglTampil(rs.getString("tgl_transaksi")),
                        idCust   != null ? idCust   : "-",
                        namaCust != null ? namaCust : "Non-Member",
                        rs.getString("nama_barang"),
                        rs.getString("satuan"),
                        rs.getInt("jumlah_beli"),
                        Validasi.rupiah(rs.getDouble("harga_satuan")),
                        Validasi.rupiah(subtotal),
                        rs.getString("nama_lengkap")
                    });
                }
            }
            jmlTransaksi = fakturSet.size();

            lblJmlTransaksi.setText("Jumlah Transaksi: " + jmlTransaksi);
            lblTotalPendapatan.setText("Total Pendapatan: Rp " + Validasi.rupiah(totalPendapatan));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
