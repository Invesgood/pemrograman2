package pemrograman2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Penulis PDF minimal tanpa library eksternal.
 * Khusus untuk mengekspor tabel laporan ke berkas .pdf (A4 landscape),
 * memakai font bawaan PDF (Helvetica) sehingga tidak perlu meng-embed font.
 */
public final class PdfExporter {

    private PdfExporter() {}

    // Ukuran A4 landscape dalam satuan point (1/72 inci).
    private static final float PAGE_W = 842f;
    private static final float PAGE_H = 595f;
    private static final float MARGIN = 30f;

    private static final float TITLE_SIZE  = 14f;
    private static final float PERIOD_SIZE = 9f;
    private static final float HEAD_SIZE   = 8f;
    private static final float CELL_SIZE   = 8f;
    private static final float HEAD_H      = 16f;
    private static final float ROW_H       = 14f;

    /**
     * Tulis laporan tabel ke berkas PDF.
     *
     * @param file       berkas tujuan
     * @param title      judul laporan
     * @param periode    keterangan periode
     * @param columns    nama kolom
     * @param colWidths  lebar tiap kolom (point); jumlahnya idealnya = PAGE_W - 2*MARGIN
     * @param rows       baris data (tiap baris panjangnya = columns.length)
     * @param footerText teks ringkasan di kaki halaman (mis. total pendapatan)
     */
    public static void exportTable(File file, String title, String periode,
                                   String[] columns, int[] colWidths,
                                   List<String[]> rows, String footerText) throws IOException {

        // Posisi x tepi kiri tiap kolom.
        float[] colX = new float[columns.length + 1];
        colX[0] = MARGIN;
        for (int i = 0; i < columns.length; i++) {
            colX[i + 1] = colX[i] + colWidths[i];
        }

        float tableTop   = PAGE_H - MARGIN - 40f;   // di bawah judul & periode
        float bottomLimit = MARGIN + 22f;            // sisakan ruang footer
        int rowsPerPage  = (int) ((tableTop - HEAD_H - bottomLimit) / ROW_H);
        if (rowsPerPage < 1) rowsPerPage = 1;

        int totalPages = Math.max(1, (int) Math.ceil(rows.size() / (double) rowsPerPage));

        List<String> contentStreams = new ArrayList<>();
        for (int page = 0; page < totalPages; page++) {
            int from = page * rowsPerPage;
            int to   = Math.min(from + rowsPerPage, rows.size());
            contentStreams.add(buildPageContent(
                    title, periode, columns, colWidths, colX,
                    rows.subList(from, to), footerText,
                    tableTop, page + 1, totalPages));
        }

        writePdf(file, contentStreams);
    }

    // ── Penyusun isi (content stream) per halaman ──────────────────────────────

    private static String buildPageContent(String title, String periode,
                                           String[] columns, int[] colWidths, float[] colX,
                                           List<String[]> rows, String footerText,
                                           float tableTop, int pageNo, int totalPages) {
        StringBuilder c = new StringBuilder();
        float right = colX[columns.length];

        // Judul.
        text(c, "F2", TITLE_SIZE, MARGIN, PAGE_H - MARGIN - 10f, title);
        // Periode.
        text(c, "F1", PERIOD_SIZE, MARGIN, PAGE_H - MARGIN - 26f, "Periode: " + periode);

        // Garis grid warna abu-abu.
        c.append("0.6 0.6 0.6 RG\n0.5 w\n");

        // Header kolom (latar diberi garis atas-bawah).
        float headTop    = tableTop;
        float headBottom = tableTop - HEAD_H;
        hLine(c, colX[0], right, headTop);
        hLine(c, colX[0], right, headBottom);
        for (int i = 0; i < columns.length; i++) {
            text(c, "F2", HEAD_SIZE, colX[i] + 2f, headBottom + 4f,
                 fit(columns[i], colWidths[i]));
        }

        // Baris data.
        float y = headBottom;
        for (String[] row : rows) {
            float rowBottom = y - ROW_H;
            for (int i = 0; i < columns.length && i < row.length; i++) {
                String val = row[i] == null ? "" : row[i];
                text(c, "F1", CELL_SIZE, colX[i] + 2f, rowBottom + 4f, fit(val, colWidths[i]));
            }
            hLine(c, colX[0], right, rowBottom);
            y = rowBottom;
        }

        // Garis vertikal kolom (dari header sampai baris terakhir).
        for (int i = 0; i <= columns.length; i++) {
            vLine(c, colX[i], headTop, y);
        }

        // Footer: ringkasan + nomor halaman.
        text(c, "F2", PERIOD_SIZE, MARGIN, MARGIN, footerText);
        String pg = "Halaman " + pageNo + " / " + totalPages;
        text(c, "F1", PERIOD_SIZE, right - 80f, MARGIN, pg);

        return c.toString();
    }

    private static void text(StringBuilder c, String font, float size, float x, float y, String s) {
        c.append("BT\n/").append(font).append(' ').append(num(size)).append(" Tf\n")
         .append(num(x)).append(' ').append(num(y)).append(" Td\n")
         .append('(').append(escape(s)).append(") Tj\nET\n");
    }

    private static void hLine(StringBuilder c, float x1, float x2, float y) {
        c.append(num(x1)).append(' ').append(num(y)).append(" m ")
         .append(num(x2)).append(' ').append(num(y)).append(" l S\n");
    }

    private static void vLine(StringBuilder c, float x, float y1, float y2) {
        c.append(num(x)).append(' ').append(num(y1)).append(" m ")
         .append(num(x)).append(' ').append(num(y2)).append(" l S\n");
    }

    // ── Penyusun struktur berkas PDF ───────────────────────────────────────────

    private static void writePdf(File file, List<String> contentStreams) throws IOException {
        // Penomoran objek:
        //  1 Catalog, 2 Pages, 3 Font Helvetica, 4 Font Helvetica-Bold,
        //  lalu tiap halaman: (content stream, page).
        int nPages = contentStreams.size();
        int firstPageObj = 5;
        int[] contentObj = new int[nPages];
        int[] pageObj    = new int[nPages];
        for (int i = 0; i < nPages; i++) {
            contentObj[i] = firstPageObj + i * 2;
            pageObj[i]    = firstPageObj + i * 2 + 1;
        }
        int totalObjs = 4 + nPages * 2;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i <= totalObjs; i++) offsets.add(0);

        writeAscii(out, "%PDF-1.4\n%âãÏÓ\n");

        // obj 1: Catalog
        offsets.set(1, out.size());
        writeAscii(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // obj 2: Pages
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < nPages; i++) kids.append(pageObj[i]).append(" 0 R ");
        offsets.set(2, out.size());
        writeAscii(out, "2 0 obj\n<< /Type /Pages /Kids [" + kids.toString().trim()
                + "] /Count " + nPages + " >>\nendobj\n");

        // obj 3 & 4: Fonts
        offsets.set(3, out.size());
        writeAscii(out, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");
        offsets.set(4, out.size());
        writeAscii(out, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n");

        // Halaman + content stream
        for (int i = 0; i < nPages; i++) {
            byte[] stream = contentStreams.get(i).getBytes(StandardCharsets.ISO_8859_1);
            offsets.set(contentObj[i], out.size());
            writeAscii(out, contentObj[i] + " 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
            out.write(stream, 0, stream.length);
            writeAscii(out, "\nendstream\nendobj\n");

            offsets.set(pageObj[i], out.size());
            writeAscii(out, pageObj[i] + " 0 obj\n<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 " + num(PAGE_W) + " " + num(PAGE_H) + "] "
                    + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> "
                    + "/Contents " + contentObj[i] + " 0 R >>\nendobj\n");
        }

        // xref
        int xrefStart = out.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(totalObjs + 1).append('\n');
        xref.append("0000000000 65535 f \n");
        for (int i = 1; i <= totalObjs; i++) {
            xref.append(String.format("%010d 00000 n \n", offsets.get(i)));
        }
        writeAscii(out, xref.toString());
        writeAscii(out, "trailer\n<< /Size " + (totalObjs + 1) + " /Root 1 0 R >>\n"
                + "startxref\n" + xrefStart + "\n%%EOF\n");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            out.writeTo(fos);
        }
    }

    // ── Utilitas ───────────────────────────────────────────────────────────────

    private static void writeAscii(ByteArrayOutputStream out, String s) {
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        out.write(b, 0, b.length);
    }

    /** Hilangkan karakter spesial PDF dan paksa ke Latin-1. */
    private static String escape(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(' || ch == ')' || ch == '\\') b.append('\\').append(ch);
            else if (ch == '\n' || ch == '\r' || ch == '\t') b.append(' ');
            else if (ch < 32 || ch > 255) b.append('?');
            else b.append(ch);
        }
        return b.toString();
    }

    /** Pangkas teks agar muat di lebar kolom (perkiraan kasar lebar karakter Helvetica). */
    private static String fit(String s, int colWidth) {
        if (s == null) return "";
        float avail = colWidth - 4f;
        float perChar = CELL_SIZE * 0.5f;
        int maxChars = (int) (avail / perChar);
        if (maxChars <= 0) return "";
        if (s.length() <= maxChars) return s;
        if (maxChars <= 2) return s.substring(0, maxChars);
        return s.substring(0, maxChars - 2) + "..";
    }

    private static String num(float v) {
        if (v == Math.rint(v)) return Integer.toString((int) v);
        return String.format(java.util.Locale.US, "%.2f", v);
    }
}
