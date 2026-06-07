package pemrograman2;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;

public class UITheme {

    // ── Palette: Soft Neobrutalism (warm yellow + pastel, charcoal lines) ────
    public static final Color PRIMARY       = new Color(255, 214,  90); // softer warm yellow
    public static final Color PRIMARY_DARK  = new Color( 38,  38,  48); // charcoal (ex pure black)
    public static final Color PRIMARY_LIGHT = new Color(168, 230, 161); // pastel green
    public static final Color SUCCESS       = new Color(168, 230, 161); // pastel green
    public static final Color DANGER        = new Color(255, 122, 138); // soft coral
    public static final Color WARNING       = new Color(255, 184, 107); // soft orange
    public static final Color NEUTRAL       = new Color(236, 236, 240); // light gray
    public static final Color BG_PANEL      = new Color(252, 252, 253); // near white
    public static final Color BORDER_COLOR  = new Color( 38,  38,  48); // charcoal
    public static final Color TEXT_MAIN     = new Color( 38,  38,  48);
    public static final Color TEXT_MUTED    = new Color(120, 120, 132);
    public static final Color ROW_ALT       = new Color(248, 248, 250); // very light grey
    public static final Color ROW_SEL       = new Color(206, 240, 200); // soft pastel green
    public static final Color SHADOW_COLOR  = new Color( 38,  38,  48, 46); // soft translucent
    public static final Color GRID_COLOR    = new Color(224, 224, 230); // gentle grid line

    // Thickness / shape constants
    public static final int   BORDER_THICK  = 2;   // ex 3
    public static final int   SHADOW_SIZE   = 4;   // ex 5 (softer offset)
    public static final int   ARC           = 14;  // rounded corner radius

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
                    UIManager.put("nimbusBlueGrey",               new Color(228, 228, 232));
                    UIManager.put("nimbusSelectionBackground",    ROW_SEL);
                    UIManager.put("Table.alternateRowColor",      ROW_ALT);
                    UIManager.put("TabbedPane.tabAreaBackground", BG_PANEL);
                    UIManager.put("TabbedPane.selected",          PRIMARY);
                    UIManager.put("TabbedPane.contentAreaColor",  BG_PANEL);
                    UIManager.put("TabbedPane.selectedForeground",   BORDER_COLOR);
                    UIManager.put("TabbedPane.unselectedForeground", TEXT_MUTED);
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Button (soft neobrutalism: rounded + thin charcoal border + soft shadow)
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

    /** Custom UI: rounded flat color + soft shadow + thin border, antialiased. */
    public static class NeoButtonUI extends BasicButtonUI {
        @Override public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            int w = c.getWidth(), h = c.getHeight();
            int s = SHADOW_SIZE;
            boolean pressed = b.getModel().isPressed() && b.getModel().isArmed();
            boolean hover   = b.getModel().isRollover();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Soft shadow (only when not pressed)
            if (!pressed && b.isEnabled()) {
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(s, s, w - s, h - s, ARC, ARC);
            }

            int dx = pressed ? s : 0;
            int dy = pressed ? s : 0;

            // Fill
            Color fill = b.getBackground();
            if (!b.isEnabled()) {
                fill = new Color(228, 228, 232);
            } else if (hover && !pressed) {
                fill = brighten(fill, 14);
            }
            g2.setColor(fill);
            g2.fillRoundRect(dx, dy, w - s, h - s, ARC, ARC);

            // Thin charcoal border
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(BORDER_THICK));
            int t = BORDER_THICK / 2;
            g2.drawRoundRect(dx + t, dy + t, w - s - BORDER_THICK, h - s - BORDER_THICK, ARC, ARC);
            g2.dispose();

            // Text — shift down-right when pressed so it looks "pushed in"
            Graphics2D gt = (Graphics2D) g.create();
            if (pressed) gt.translate(s, s);
            super.paint(gt, c);
            gt.dispose();
        }

        @Override protected void paintText(Graphics g, JComponent c, java.awt.Rectangle textRect, String text) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font f = b.getFont();
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics(f);
            g2.setColor(b.isEnabled() ? b.getForeground() : new Color(150, 150, 158));
            g2.drawString(text, textRect.x, textRect.y + fm.getAscent());
            g2.dispose();
        }
    }

    private static Color brighten(Color c, int amount) {
        return new Color(
            Math.min(255, c.getRed()   + amount),
            Math.min(255, c.getGreen() + amount),
            Math.min(255, c.getBlue()  + amount));
    }

    // ── Rounded line border (charcoal) with inner padding ───────────────────
    public static Border roundedBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
            new RoundLineBorder(BORDER_COLOR, BORDER_THICK, ARC),
            BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    /** A line border with rounded corners, drawn antialiased. */
    public static class RoundLineBorder extends AbstractBorder {
        private final Color color;
        private final int thick, arc;
        public RoundLineBorder(Color color, int thick, int arc) {
            this.color = color; this.thick = thick; this.arc = arc;
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(thick, thick, thick, thick);
        }
        @Override public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(thick, thick, thick, thick);
            return insets;
        }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thick));
            int t = thick / 2;
            g2.drawRoundRect(x + t, y + t, w - thick, h - thick, arc, arc);
            g2.dispose();
        }
    }

    // ── TextField (rounded charcoal border) ─────────────────────────────────
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BOLD);
        field.setForeground(TEXT_MAIN);
        field.setBackground(Color.WHITE);
        field.setCaretColor(BORDER_COLOR);
        field.setBorder(roundedBorder(7, 12, 7, 12));
    }

    // ── ComboBox (rounded charcoal border) ──────────────────────────────────
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_BOLD);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(Color.WHITE);
        cb.setBorder(new RoundLineBorder(BORDER_COLOR, BORDER_THICK, ARC));
        // Custom renderer: highlight item dengan kuning tema + teks gelap,
        // supaya tidak silau seperti default.
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(FONT_BOLD);
                setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
                if (isSelected) {
                    setBackground(PRIMARY);        // yellow
                    setForeground(BORDER_COLOR);   // charcoal text
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_MAIN);
                }
                setOpaque(true);
                return this;
            }
        });
    }

    /** Walk container tree and apply styling to input components. */
    public static void applyNeoToTree(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JPasswordField) {
                styleTextField((JPasswordField) c);
            } else if (c instanceof JTextField) {
                // Skip non-editable fields (e.g., ID fields) so they keep a muted look.
                JTextField tf = (JTextField) c;
                if (tf.isEditable()) {
                    styleTextField(tf);
                } else {
                    tf.setFont(FONT_BOLD);
                    tf.setForeground(TEXT_MUTED);
                    tf.setBackground(NEUTRAL);
                    tf.setBorder(roundedBorder(7, 12, 7, 12));
                }
            } else if (c instanceof JComboBox<?>) {
                styleComboBox((JComboBox<?>) c);
            }
            if (c instanceof Container) applyNeoToTree((Container) c);
        }
    }

    // ── Titled Border (rounded charcoal line + bold title + inner padding) ──
    public static Border titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
            new RoundLineBorder(BORDER_COLOR, BORDER_THICK, ARC),
            "  " + title + "  ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            BORDER_COLOR);
        return BorderFactory.createCompoundBorder(
            tb,
            BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /** Border that draws a rounded card with soft offset shadow. */
    public static Border neoCardBorder() {
        return new AbstractBorder() {
            @Override public Insets getBorderInsets(Component c) {
                return new Insets(BORDER_THICK, BORDER_THICK,
                                  BORDER_THICK + SHADOW_SIZE, BORDER_THICK + SHADOW_SIZE);
            }
            @Override public boolean isBorderOpaque() { return false; }
            @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // soft shadow
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(x + SHADOW_SIZE, y + SHADOW_SIZE,
                                 w - SHADOW_SIZE, h - SHADOW_SIZE, ARC, ARC);
                // thin border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(BORDER_THICK));
                int t = BORDER_THICK / 2;
                g2.drawRoundRect(x + t, y + t,
                            w - SHADOW_SIZE - BORDER_THICK, h - SHADOW_SIZE - BORDER_THICK, ARC, ARC);
                g2.dispose();
            }
        };
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(36);
        table.setGridColor(GRID_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
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
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, BORDER_THICK, 0, BORDER_COLOR),
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
