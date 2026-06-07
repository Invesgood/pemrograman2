package pemrograman2;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard ringkasan (khusus Admin).
 * Menampilkan KPI penjualan, barang terlaris, dan barang yang stoknya menipis.
 */
public class FormDashboard extends JPanel {

    private static final int STOK_MINIMUM = 10;

    private JLabel lblPenjualanHari, lblPenjualanBulan, lblTransaksiHari, lblStokMenipis;
    private DefaultTableModel modelTerlaris, modelMenipis;

    public FormDashboard() {
        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        initComponents();
        refresh();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("DASHBOARD", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ── Kartu KPI ───────────────────────────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);

        lblPenjualanHari  = new JLabel("Rp 0");
        lblPenjualanBulan = new JLabel("Rp 0");
        lblTransaksiHari  = new JLabel("0");
        lblStokMenipis    = new JLabel("0");

        cards.add(buildCard("PENJUALAN HARI INI",  lblPenjualanHari,  UITheme.PRIMARY));
        cards.add(buildCard("PENJUALAN BULAN INI", lblPenjualanBulan, UITheme.PRIMARY_LIGHT));
        cards.add(buildCard("TRANSAKSI HARI INI",  lblTransaksiHari,  UITheme.WARNING));
        cards.add(buildCard("STOK MENIPIS",        lblStokMenipis,    UITheme.DANGER));

        // ── Tabel Barang Terlaris ────────────────────────────────────────────────
        modelTerlaris = new DefaultTableModel(
                new String[]{"#", "Nama Barang", "Terjual", "Total Penjualan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblTerlaris = new JTable(modelTerlaris);
        UITheme.styleTable(tblTerlaris);
        tblTerlaris.getColumnModel().getColumn(0).setPreferredWidth(30);
        JScrollPane scrollTerlaris = new JScrollPane(tblTerlaris);
        scrollTerlaris.setBorder(UITheme.titledBorder("Barang Terlaris (Top 5)"));

        // ── Tabel Stok Menipis ───────────────────────────────────────────────────
        modelMenipis = new DefaultTableModel(
                new String[]{"ID", "Nama Barang", "Sisa Stok", "Satuan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblMenipis = new JTable(modelMenipis);
        UITheme.styleTable(tblMenipis);
        tblMenipis.getColumnModel().getColumn(0).setPreferredWidth(50);
        JScrollPane scrollMenipis = new JScrollPane(tblMenipis);
        scrollMenipis.setBorder(UITheme.titledBorder("Stok Menipis (≤ " + STOK_MINIMUM + ")"));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.add(scrollTerlaris);
        tablesPanel.add(scrollMenipis);

        // ── Susun ─────────────────────────────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setOpaque(false);
        cardWrap.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        cardWrap.add(cards, BorderLayout.CENTER);
        cardWrap.setPreferredSize(new Dimension(0, 130));
        center.add(cardWrap,    BorderLayout.NORTH);
        center.add(tablesPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    /** Kartu KPI neobrutalism: judul kecil + nilai besar di atas warna solid. */
    private JPanel buildCard(String title, JLabel valueLabel, Color bg) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                UITheme.neoCardBorder(),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(UITheme.BORDER_COLOR);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UITheme.BORDER_COLOR);

        card.add(lblTitle,    BorderLayout.NORTH);
        card.add(valueLabel,  BorderLayout.CENTER);
        return card;
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void refresh() {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        // KPI: penjualan & transaksi hari ini
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT IFNULL(SUM(total_bayar),0) AS total, COUNT(*) AS jml " +
                "FROM tb_penjualan WHERE tgl_transaksi = CURDATE()");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lblPenjualanHari.setText("Rp " + Validasi.rupiah(rs.getDouble("total")));
                lblTransaksiHari.setText(String.valueOf(rs.getInt("jml")));
            }
        } catch (SQLException e) { showError(e); }

        // KPI: penjualan bulan ini
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT IFNULL(SUM(total_bayar),0) AS total FROM tb_penjualan " +
                "WHERE MONTH(tgl_transaksi) = MONTH(CURDATE()) AND YEAR(tgl_transaksi) = YEAR(CURDATE())");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                lblPenjualanBulan.setText("Rp " + Validasi.rupiah(rs.getDouble("total")));
        } catch (SQLException e) { showError(e); }

        // KPI: jumlah barang stok menipis
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tb_barang WHERE stok <= ?")) {
            ps.setInt(1, STOK_MINIMUM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblStokMenipis.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) { showError(e); }

        // Tabel barang terlaris
        modelTerlaris.setRowCount(0);
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT b.nama_barang, SUM(d.jumlah_beli) AS qty, SUM(d.subtotal) AS total " +
                "FROM tb_detail_penjualan d JOIN tb_barang b ON d.id_barang = b.id_barang " +
                "GROUP BY d.id_barang, b.nama_barang ORDER BY qty DESC LIMIT 5");
             ResultSet rs = ps.executeQuery()) {
            int rank = 1;
            while (rs.next()) {
                modelTerlaris.addRow(new Object[]{
                    rank++,
                    rs.getString("nama_barang"),
                    rs.getInt("qty"),
                    "Rp " + Validasi.rupiah(rs.getDouble("total"))
                });
            }
        } catch (SQLException e) { showError(e); }

        // Tabel stok menipis
        modelMenipis.setRowCount(0);
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_barang, nama_barang, stok, satuan FROM tb_barang " +
                "WHERE stok <= ? ORDER BY stok ASC")) {
            ps.setInt(1, STOK_MINIMUM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelMenipis.addRow(new Object[]{
                        rs.getString("id_barang"),
                        rs.getString("nama_barang"),
                        rs.getInt("stok"),
                        rs.getString("satuan") != null ? rs.getString("satuan") : ""
                    });
                }
            }
        } catch (SQLException e) { showError(e); }
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
