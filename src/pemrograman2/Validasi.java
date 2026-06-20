package pemrograman2;

import java.awt.Component;
import java.sql.*;
import javax.swing.*;
import javax.swing.text.*;

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

    // ── Filter ketik (live) ─────────────────────────────────────────────────
    // Menolak karakter yang tidak sesuai langsung saat diketik / di-paste,
    // dengan menguji teks HASIL akhir terhadap pola yang diizinkan.

    private static void pasangFilter(JTextField f, java.util.function.Predicate<String> hasilValid) {
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void insertString(FilterBypass fb, int off, String s, AttributeSet a)
                    throws BadLocationException {
                String cur  = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = cur.substring(0, off) + (s == null ? "" : s) + cur.substring(off);
                if (hasilValid.test(next)) super.insertString(fb, off, s, a);
            }
            @Override public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a)
                    throws BadLocationException {
                String cur  = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = cur.substring(0, off) + (s == null ? "" : s) + cur.substring(off + len);
                if (hasilValid.test(next)) super.replace(fb, off, len, s, a);
            }
        });
    }

    /** Hanya digit angka 0-9 (mis. stok, jumlah, telepon, uang dibayar). */
    public static void hanyaAngka(JTextField f) {
        pasangFilter(f, t -> t.matches("\\d*"));
    }

    /**
     * Format ribuan otomatis dengan titik saat mengetik (mis. 180000 → 180.000).
     * Hanya menerima angka bulat. Untuk membaca nilainya: getText().replaceAll("[^0-9]", "").
     */
    public static void formatRibuan(JTextField f) {
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void insertString(FilterBypass fb, int off, String s, AttributeSet a)
                    throws BadLocationException {
                replace(fb, off, 0, s, a);
            }
            @Override public void remove(FilterBypass fb, int off, int len)
                    throws BadLocationException {
                replace(fb, off, len, "", null);
            }
            @Override public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a)
                    throws BadLocationException {
                String cur  = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ins  = (s == null ? "" : s).replaceAll("[^0-9]", "");
                String next = cur.substring(0, off) + ins + cur.substring(off + len);
                String digits = next.replaceAll("[^0-9]", "");
                // jumlah digit di kiri kursor, untuk menempatkan ulang kursor setelah diformat
                int digitKiri = (cur.substring(0, off) + ins).replaceAll("[^0-9]", "").length();

                String formatted = kelompokRibuan(digits);
                super.replace(fb, 0, fb.getDocument().getLength(), formatted, a);

                int pos = 0, count = 0;
                while (pos < formatted.length() && count < digitKiri) {
                    if (Character.isDigit(formatted.charAt(pos))) count++;
                    pos++;
                }
                final int caret = pos;
                SwingUtilities.invokeLater(() ->
                        f.setCaretPosition(Math.min(caret, f.getText().length())));
            }
        });
    }

    /** Format angka untuk ditampilkan dengan titik ribuan, mis. 180000 → "180.000". */
    public static String rupiah(double v) {
        long n = Math.round(v);
        String sign = n < 0 ? "-" : "";
        return sign + kelompokRibuan(Long.toString(Math.abs(n)));
    }

    /** Sisipkan titik setiap 3 digit dari kanan. "180000" → "180.000". */
    private static String kelompokRibuan(String digits) {
        if (digits.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for (int i = digits.length() - 1; i >= 0; i--) {
            sb.append(digits.charAt(i));
            if (++c % 3 == 0 && i > 0) sb.append('.');
        }
        return sb.reverse().toString();
    }

    /** Angka desimal: digit dengan paling banyak satu titik (mis. harga). */
    public static void hanyaDesimal(JTextField f) {
        pasangFilter(f, t -> t.matches("\\d*\\.?\\d*"));
    }

    /** Hanya huruf dan spasi (mis. nama orang, nama kategori). */
    public static void hanyaHuruf(JTextField f) {
        pasangFilter(f, t -> t.matches("[a-zA-Z ]*"));
    }

    /** Teks tanpa spasi/whitespace (mis. username). */
    public static void tanpaSpasi(JTextField f) {
        pasangFilter(f, t -> t.matches("\\S*"));
    }

    /** Pola tanggal progresif dd-MM-yyyy (hanya digit & tanda hubung). */
    public static void hanyaTanggal(JTextField f) {
        pasangFilter(f, t -> t.matches("\\d{0,2}(-\\d{0,2}(-\\d{0,4})?)?"));
    }

    /** Validasi format tanggal dd-MM-yyyy. Kosong = lolos bila required=false. */
    public static boolean isTanggal(Component p, JTextField f, String label, boolean required) {
        String v = f.getText().trim();
        if (v.isEmpty()) {
            if (required) { warn(p, label + " harus diisi!", f); return false; }
            return true;
        }
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd-MM-yyyy");
        fmt.setLenient(false);
        try {
            fmt.parse(v);
        } catch (java.text.ParseException e) {
            warn(p, label + " harus format tanggal dd-MM-yyyy yang valid!", f);
            return false;
        }
        return true;
    }

    /** Tanggal hari ini dalam format tampilan dd-MM-yyyy. */
    public static String hariIni() {
        return new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
    }

    /** Tampilan dd-MM-yyyy → format database yyyy-MM-dd. Gagal parse → kembalikan apa adanya. */
    public static String tglKeSql(String tampilan) {
        try {
            java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("dd-MM-yyyy");
            in.setLenient(false);
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(in.parse(tampilan.trim()));
        } catch (Exception e) { return tampilan; }
    }

    /** Tanggal database yyyy-MM-dd → tampilan dd-MM-yyyy. Null/gagal → aman. */
    public static String tglTampil(String dbDate) {
        if (dbDate == null) return "";
        try {
            java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM-dd");
            in.setLenient(false);
            return new java.text.SimpleDateFormat("dd-MM-yyyy").format(in.parse(dbDate.trim()));
        } catch (Exception e) { return dbDate; }
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

    /** Nama barang: wajib diisi & boleh alfanumerik, tapi tidak boleh angka saja. */
    public static boolean isNamaBarang(Component p, JTextField f, String label) {
        if (!notEmpty(p, f, label)) return false;
        if (!f.getText().trim().matches(".*[a-zA-Z].*")) {
            warn(p, label + " tidak boleh berisi angka saja, harus mengandung huruf!", f);
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
