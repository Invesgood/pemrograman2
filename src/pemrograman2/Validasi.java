package pemrograman2;

import java.awt.Component;
import java.sql.*;
import javax.swing.*;

/**
 * Validasi input terpusat agar aturannya seragam di seluruh form.
 * Setiap method menampilkan pesan peringatan & memindahkan fokus bila gagal,
 * lalu mengembalikan true bila valid.
 */
public class Validasi {

    // Nama orang: hanya huruf dan spasi.
    private static final String NAMA_REGEX    = "[a-zA-Z\\s]+";
    // Nomor telepon: 9–15 digit angka.
    private static final String TELEPON_REGEX = "\\d{9,15}";

    private static void warn(Component p, String msg, JComponent focus) {
        JOptionPane.showMessageDialog(p, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
        if (focus != null) focus.requestFocus();
    }

    /** Wajib diisi (tidak boleh kosong). */
    public static boolean notEmpty(Component p, JTextField f, String label) {
        if (f.getText().trim().isEmpty()) {
            warn(p, label + " harus diisi!", f);
            return false;
        }
        return true;
    }

    /** Nama orang: wajib diisi & hanya boleh huruf dan spasi. */
    public static boolean isNama(Component p, JTextField f, String label) {
        if (!notEmpty(p, f, label)) return false;
        if (!f.getText().trim().matches(NAMA_REGEX)) {
            warn(p, label + " hanya boleh berisi huruf dan spasi!", f);
            return false;
        }
        return true;
    }

    /** Nomor telepon: angka 9–15 digit. required=true berarti wajib diisi. */
    public static boolean isTelepon(Component p, JTextField f, String label, boolean required) {
        String v = f.getText().trim();
        if (v.isEmpty()) {
            if (required) { warn(p, label + " harus diisi!", f); return false; }
            return true; // opsional & kosong → lolos
        }
        if (!v.matches(TELEPON_REGEX)) {
            warn(p, label + " harus berupa angka (9–15 digit)!", f);
            return false;
        }
        return true;
    }

    /** Bilangan bulat tak negatif (mis. stok, jumlah). */
    public static boolean isIntTakNegatif(Component p, JTextField f, String label) {
        try {
            if (Integer.parseInt(f.getText().trim()) < 0) {
                warn(p, label + " tidak boleh negatif!", f);
                return false;
            }
        } catch (NumberFormatException e) {
            warn(p, label + " harus berupa angka bulat!", f);
            return false;
        }
        return true;
    }

    /** Bilangan desimal tak negatif (mis. harga). */
    public static boolean isDoubleTakNegatif(Component p, JTextField f, String label) {
        try {
            if (Double.parseDouble(f.getText().trim()) < 0) {
                warn(p, label + " tidak boleh negatif!", f);
                return false;
            }
        } catch (NumberFormatException e) {
            warn(p, label + " harus berupa angka!", f);
            return false;
        }
        return true;
    }

    /** Pastikan no. telepon belum dipakai customer lain. exceptId null = data baru. */
    public static boolean teleponBelumDipakai(Component p, String telepon, String exceptId) {
        Connection con = Koneksi.getKoneksi();
        if (con == null) return true;
        String sql = exceptId == null
                ? "SELECT id_customer FROM tb_customer WHERE telepon = ?"
                : "SELECT id_customer FROM tb_customer WHERE telepon = ? AND id_customer <> ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telepon);
            if (exceptId != null) ps.setString(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    warn(p, "No. telepon \"" + telepon + "\" sudah terdaftar untuk customer lain!", null);
                    return false;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(p, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }
}
