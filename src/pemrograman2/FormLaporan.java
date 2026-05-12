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
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("LAPORAN PENJUALAN", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Filter Panel ─────────────────────────────────────────────────────
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBorder(UITheme.titledBorder("Filter Tanggal"));

        txtTglMulai   = new JTextField(today, 12);
        txtTglSelesai = new JTextField(today, 12);

        JButton btnTampilkan = new JButton("Tampilkan");
        JButton btnSemua     = new JButton("Semua Data");

        btnTampilkan.addActionListener(e -> loadData(txtTglMulai.getText().trim(), txtTglSelesai.getText().trim()));
        btnSemua.addActionListener(e -> {
            txtTglMulai.setText("");
            txtTglSelesai.setText("");
            loadData(null, null);
        });
        UITheme.styleButton(btnTampilkan, UITheme.PRIMARY);
        UITheme.styleButton(btnSemua,     UITheme.NEUTRAL);

        filterPanel.add(new JLabel("Dari:"));
        filterPanel.add(txtTglMulai);
        filterPanel.add(new JLabel("s/d:"));
        filterPanel.add(txtTglSelesai);
        filterPanel.add(btnTampilkan);
        filterPanel.add(btnSemua);

        // ── Table ────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
                new String[]{"ID Jual", "Tanggal", "ID Customer", "Nama Customer",
                             "Nama Barang", "Satuan", "Qty", "Harga Satuan (Rp)", "Total Bayar (Rp)", "Kasir"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLaporan = new JTable(tableModel);
        tblLaporan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        UITheme.styleTable(tblLaporan);
        // Atur lebar kolom
        int[] widths = {60, 90, 90, 140, 140, 65, 45, 130, 130, 110};
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
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        txtTglMulai.setText(today);
        txtTglSelesai.setText(today);
        loadData(today, today);
    }

    private void loadData(String tglMulai, String tglSelesai) {
        tableModel.setRowCount(0);
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_jual, p.tgl_transaksi, " +
                "c.id_customer, c.nama_customer, " +
                "b.nama_barang, b.satuan, " +
                "p.jumlah_beli, b.harga_jual, p.total_bayar, " +
                "u.nama_lengkap " +
                "FROM tb_penjualan p " +
                "LEFT JOIN tb_customer c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang        b ON p.id_barang   = b.id_barang " +
                "JOIN tb_user          u ON p.id_user     = u.id_user ");

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
        sql.append("ORDER BY p.tgl_transaksi DESC, p.id_jual DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (hasFilter) {
                if (tglMulai != null && !tglMulai.isEmpty() && tglSelesai != null && !tglSelesai.isEmpty()) {
                    ps.setString(1, tglMulai);
                    ps.setString(2, tglSelesai);
                } else if (tglMulai != null && !tglMulai.isEmpty()) {
                    ps.setString(1, tglMulai);
                } else {
                    ps.setString(1, tglSelesai);
                }
            }

            double totalPendapatan = 0;
            int    jmlTransaksi    = 0;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double totalBayar = rs.getDouble("total_bayar");
                    totalPendapatan += totalBayar;
                    jmlTransaksi++;
                    String idCust   = rs.getString("id_customer");
                    String namaCust = rs.getString("nama_customer");
                    tableModel.addRow(new Object[]{
                        rs.getInt("id_jual"),
                        rs.getString("tgl_transaksi"),
                        idCust   != null ? idCust   : "-",
                        namaCust != null ? namaCust : "Non-Member",
                        rs.getString("nama_barang"),
                        rs.getString("satuan"),
                        rs.getInt("jumlah_beli"),
                        String.format("%,.0f", rs.getDouble("harga_jual")),
                        String.format("%,.0f", totalBayar),
                        rs.getString("nama_lengkap")
                    });
                }
            }

            lblJmlTransaksi.setText("Jumlah Transaksi: " + jmlTransaksi);
            lblTotalPendapatan.setText("Total Pendapatan: Rp " + String.format("%,.0f", totalPendapatan));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
