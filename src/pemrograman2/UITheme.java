package pemrograman2;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;

public class UITheme {

    // ── Palette: Neobrutalism (Yellow + Neon Green) ─────────────────────────
    public static final Color PRIMARY       = new Color(255, 217,  61); // yellow
    public static final Color PRIMARY_DARK  = new Color(  0,   0,   0); // black
    public static final Color PRIMARY_LIGHT = new Color(180, 255,  57); // neon green
    public static final Color SUCCESS       = new Color(180, 255,  57); // neon green
    public static final Color DANGER        = new Color(255,  85, 119); // hot pink
    public static final Color WARNING       = new Color(255, 140,  50); // orange
    public static final Color NEUTRAL       = new Color(230, 230, 230); // light gray
    public static final Color BG_PANEL      = new Color(255, 255, 255); // pure white
    public static final Color BORDER_COLOR  = new Color(  0,   0,   0); // pure black
    public static final Color TEXT_MAIN     = new Color(  0,   0,   0);
    public static final Color TEXT_MUTED    = new Color( 80,  80,  80);
    public static final Color ROW_ALT       = new Color(245, 245, 245); // very light grey
    public static final Color ROW_SEL       = new Color(180, 255,  57); // neon green

    // Thickness constants
    public static final int   BORDER_THICK  = 3;
    public static final int   SHADOW_SIZE   = 5;

    // ── Typography ───────────────────────────────────────────────────────────
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  24);

    // ── Look & Feel ──────────────────────────────────────────────────────────
    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    UIManager.put("control",                      BG_PANEL);
                    UIManager.put("nimbusBase",                   PRIMARY);
                    UIManager.put("nimbusBlueGrey",               new Color(220, 220, 220));
                    UIManager.put("nimbusSelectionBackground",    ROW_SEL);
                    UIManager.put("Table.alternateRowColor",      ROW_ALT);
                    UIManager.put("TabbedPane.tabAreaBackground", BORDER_COLOR);
                    UIManager.put("TabbedPane.selected",          PRIMARY);
                    UIManager.put("TabbedPane.contentAreaColor",  BG_PANEL);
                    UIManager.put("TabbedPane.selectedForeground",   BORDER_COLOR);
                    UIManager.put("TabbedPane.unselectedForeground", TEXT_MUTED);
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Button (Neobrutalism: thick black border + hard offset shadow) ──────
    public static void styleButton(JButton btn, Color bg) {
        btn.setUI(new NeoButtonUI());
        btn.setBackground(bg);
        btn.setForeground(BORDER_COLOR);
        btn.setFont(FONT_BOLD);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8 + SHADOW_SIZE, 18 + SHADOW_SIZE));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setRolloverEnabled(true);
    }

    /** Custom UI: renders flat color + hard shadow + thick border. */
    public static class NeoButtonUI extends BasicButtonUI {
        @Override public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            int w = c.getWidth(), h = c.getHeight();
            int s = SHADOW_SIZE;
            boolean pressed = b.getModel().isPressed() && b.getModel().isArmed();
            boolean hover   = b.getModel().isRollover();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            // Shadow (only when not pressed)
            if (!pressed && b.isEnabled()) {
                g2.setColor(BORDER_COLOR);
                g2.fillRect(s, s, w - s, h - s);
            }

            int dx = pressed ? s : 0;
            int dy = pressed ? s : 0;

            // Fill
            Color fill = b.getBackground();
            if (!b.isEnabled()) {
                fill = new Color(220, 220, 220);
            } else if (hover && !pressed) {
                fill = brighten(fill, 18);
            }
            g2.setColor(fill);
            g2.fillRect(dx, dy, w - s, h - s);

            // Thick black border
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(BORDER_THICK));
            int t = BORDER_THICK / 2;
            g2.drawRect(dx + t, dy + t, w - s - BORDER_THICK, h - s - BORDER_THICK);
            g2.dispose();

            // Text — shift down-right when pressed so it looks "pushed in"
            Graphics2D gt = (Graphics2D) g.create();
            if (pressed) gt.translate(s, s);
            super.paint(gt, c);
            gt.dispose();
        }

        @Override protected void paintText(Graphics g, JComponent c, java.awt.Rectangle textRect, String text) {
            AbstractButton b = (AbstractButton) c;
            Font f = b.getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics(f);
            g.setColor(b.isEnabled() ? b.getForeground() : new Color(110, 110, 110));
            g.drawString(text, textRect.x, textRect.y + fm.getAscent());
        }
    }

    private static Color brighten(Color c, int amount) {
        return new Color(
            Math.min(255, c.getRed()   + amount),
            Math.min(255, c.getGreen() + amount),
            Math.min(255, c.getBlue()  + amount));
    }

    // ── TextField (thick black border) ──────────────────────────────────────
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BOLD);
        field.setForeground(TEXT_MAIN);
        field.setBackground(Color.WHITE);
        field.setCaretColor(BORDER_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICK),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }

    // ── ComboBox (thick black border) ───────────────────────────────────────
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_BOLD);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICK));
        // Custom renderer: highlight item dengan kuning tema + teks hitam,
        // supaya tidak silau seperti default neon-green/teks putih.
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(FONT_BOLD);
                setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
                if (isSelected) {
                    setBackground(PRIMARY);        // yellow
                    setForeground(BORDER_COLOR);   // black text
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_MAIN);
                }
                setOpaque(true);
                return this;
            }
        });
    }

    /** Walk container tree and apply neobrutalism styling to input components. */
    public static void applyNeoToTree(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JPasswordField) {
                styleTextField((JPasswordField) c);
            } else if (c instanceof JTextField) {
                // Skip non-editable fields (e.g., ID fields) so they keep their grey look.
                JTextField tf = (JTextField) c;
                if (tf.isEditable()) {
                    styleTextField(tf);
                } else {
                    tf.setFont(FONT_BOLD);
                    tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICK),
                        BorderFactory.createEmptyBorder(7, 10, 7, 10)));
                }
            } else if (c instanceof JComboBox<?>) {
                styleComboBox((JComboBox<?>) c);
            }
            if (c instanceof Container) applyNeoToTree((Container) c);
        }
    }

    // ── Titled Border (thick black line + bold title + inner padding) ─────
    public static Border titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICK),
            "  " + title.toUpperCase() + "  ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            BORDER_COLOR);
        return BorderFactory.createCompoundBorder(
            tb,
            BorderFactory.createEmptyBorder(10, 8, 8, 8));
    }

    /** Border that draws a card with hard offset black shadow. */
    public static Border neoCardBorder() {
        return new AbstractBorder() {
            @Override public Insets getBorderInsets(Component c) {
                return new Insets(BORDER_THICK, BORDER_THICK,
                                  BORDER_THICK + SHADOW_SIZE, BORDER_THICK + SHADOW_SIZE);
            }
            @Override public boolean isBorderOpaque() { return false; }
            @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BORDER_COLOR);
                // shadow at bottom-right
                g2.fillRect(x + SHADOW_SIZE, y + h - SHADOW_SIZE, w - SHADOW_SIZE, SHADOW_SIZE);
                g2.fillRect(x + w - SHADOW_SIZE, y + SHADOW_SIZE, SHADOW_SIZE, h - SHADOW_SIZE);
                // thick border
                g2.setStroke(new BasicStroke(BORDER_THICK));
                int t = BORDER_THICK / 2;
                g2.drawRect(x + t, y + t,
                            w - SHADOW_SIZE - BORDER_THICK, h - SHADOW_SIZE - BORDER_THICK);
                g2.dispose();
            }
        };
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(34);
        table.setGridColor(BORDER_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(BORDER_COLOR);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                lbl.setBackground(PRIMARY);
                lbl.setForeground(BORDER_COLOR);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, BORDER_THICK, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                lbl.setOpaque(true);
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_BODY);
                if (sel) {
                    setBackground(ROW_SEL);
                    setForeground(BORDER_COLOR);
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
