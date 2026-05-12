package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;

public class UITheme {

    // ── Palette (Modern Slate / Blue) ────────────────────────────────────────
    public static final Color PRIMARY       = new Color(59,  130, 246);  // blue-500
    public static final Color PRIMARY_DARK  = new Color(15,   23,  42);  // slate-900
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);  // blue-100
    public static final Color SUCCESS       = new Color(34,  197,  94);  // green-500
    public static final Color DANGER        = new Color(239,  68,  68);  // red-500
    public static final Color WARNING       = new Color(249, 115,  22);  // orange-500
    public static final Color NEUTRAL       = new Color(100, 116, 139);  // slate-500
    public static final Color BG_PANEL      = new Color(248, 250, 252);  // slate-50
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);  // slate-200
    public static final Color TEXT_MAIN     = new Color( 15,  23,  42);  // slate-900
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);  // slate-500
    public static final Color ROW_ALT       = new Color(248, 250, 252);  // slate-50
    public static final Color ROW_SEL       = new Color(219, 234, 254);  // blue-100

    // ── Typography ───────────────────────────────────────────────────────────
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  22);

    // ── Look & Feel ──────────────────────────────────────────────────────────
    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    UIManager.put("control",                      BG_PANEL);
                    UIManager.put("nimbusBase",                   PRIMARY);
                    UIManager.put("nimbusBlueGrey",               new Color(100, 116, 139));
                    UIManager.put("nimbusSelectionBackground",    ROW_SEL);
                    UIManager.put("Table.alternateRowColor",      ROW_ALT);
                    UIManager.put("TabbedPane.tabAreaBackground", new Color(30, 41, 59));
                    UIManager.put("TabbedPane.selected",          PRIMARY);
                    UIManager.put("TabbedPane.contentAreaColor",  BG_PANEL);
                    UIManager.put("TabbedPane.selectedForeground",   Color.WHITE);
                    UIManager.put("TabbedPane.unselectedForeground", new Color(148, 163, 184));
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Button ───────────────────────────────────────────────────────────────
    public static void styleButton(JButton btn, Color bg) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final Color hover   = bg.darker();
        final Color pressed = hover.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(btn.isEnabled() ? bg : hover);
            }
            @Override public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(pressed);
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
        });
    }

    // ── TextField ────────────────────────────────────────────────────────────
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_MAIN);
        field.setBackground(Color.WHITE);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }

    // ── Titled Border ─────────────────────────────────────────────────────────
    public static TitledBorder titledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "  " + title + "  ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FONT_BOLD,
            PRIMARY);
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(new Color(30, 64, 175));
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                lbl.setBackground(new Color(30, 41, 59));
                lbl.setForeground(new Color(203, 213, 225));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                lbl.setOpaque(true);
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_BODY);
                if (sel) {
                    setBackground(ROW_SEL);
                    setForeground(new Color(30, 64, 175));
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                    setForeground(TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
}
