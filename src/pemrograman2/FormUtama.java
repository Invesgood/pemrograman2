package pemrograman2;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class FormUtama extends JFrame {

    private FormBarang    panelBarang;
    private FormCustomer  panelCustomer;
    private FormTransaksi panelTransaksi;
    private FormLaporan   panelLaporan;
    private FormMember    panelMember;

    private JPanel     cardPanel;
    private CardLayout cardLayout;
    private JButton    activeNavBtn;

    // ── Neobrutalism palette (Yellow + Neon, Mint header) ─────────────────
    private static final Color BG       = new Color(255, 255, 255); // pure white
    private static final Color BLACK    = new Color(  0,   0,   0);
    private static final Color MINT     = new Color(212, 241, 212); // pastel mint header
    private static final Color YELLOW   = new Color(255, 217,  61); // primary
    private static final Color LIME     = new Color(180, 255,  57); // neon green
    private static final Color PINK     = new Color(255,  85, 119); // hot pink accent
    private static final Color ORANGE   = new Color(255, 140,  50);
    private static final Color SHADOW   = new Color(  0,   0,   0);
    private static final int   THICK    = 3;
    private static final int   SHADOW_OFF = 5;

    public FormUtama() {
        setTitle("Toko Berkah Jaya — Sistem Informasi Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel sidebar = buildSidebar();
        JPanel content = buildContent();
        add(buildHeader(), BorderLayout.NORTH);
        add(sidebar,       BorderLayout.WEST);
        add(content,       BorderLayout.CENTER);
        if (activeNavBtn != null) activateNav(activeNavBtn);
    }

    // ── Header ─────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MINT);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, THICK, 0, BLACK),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        // Kiri: logo + judul
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel logoBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth(), h = getHeight();
                // shadow
                g2.setColor(SHADOW);
                g2.fillRect(SHADOW_OFF, SHADOW_OFF, w - SHADOW_OFF, h - SHADOW_OFF);
                // fill
                g2.setColor(YELLOW);
                g2.fillRect(0, 0, w - SHADOW_OFF, h - SHADOW_OFF);
                // border
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRect(t, t, w - SHADOW_OFF - THICK, h - SHADOW_OFF - THICK);
                // store icon (simple bag)
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = (w - SHADOW_OFF) / 2, cy = (h - SHADOW_OFF) / 2;
                g2.drawRoundRect(cx - 9, cy - 2, 18, 13, 3, 3);
                g2.drawArc(cx - 6, cy - 10, 12, 11, 0, 180);
                g2.dispose();
            }
        };
        logoBox.setOpaque(false);
        logoBox.setPreferredSize(new Dimension(46, 42));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel lTitle = new JLabel("BERKAH JAYA");
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lTitle.setForeground(BLACK);
        JLabel lSub = new JLabel("Sistem Informasi Penjualan");
        lSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lSub.setForeground(BLACK);
        titles.add(lTitle);
        titles.add(lSub);

        left.add(logoBox);
        left.add(titles);

        // Kanan: avatar + info user + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(SHADOW);
                g2.fillRect(SHADOW_OFF, SHADOW_OFF, w - SHADOW_OFF, h - SHADOW_OFF);
                g2.setColor(PINK);
                g2.fillRect(0, 0, w - SHADOW_OFF, h - SHADOW_OFF);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRect(t, t, w - SHADOW_OFF - THICK, h - SHADOW_OFF - THICK);
                String init = (Session.namaLengkap != null && !Session.namaLengkap.isEmpty())
                        ? String.valueOf(Session.namaLengkap.charAt(0)).toUpperCase() : "?";
                g2.setColor(BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - SHADOW_OFF - fm.stringWidth(init)) / 2;
                int ty = (h - SHADOW_OFF + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(init, tx, ty);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(42, 42));

        JPanel userStack = new JPanel();
        userStack.setLayout(new BoxLayout(userStack, BoxLayout.Y_AXIS));
        userStack.setOpaque(false);
        JLabel lName  = new JLabel(Session.namaLengkap != null ? Session.namaLengkap : "");
        lName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lName.setForeground(BLACK);
        JLabel lLevel = new JLabel(Session.level != null ? Session.level : "");
        lLevel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lLevel.setForeground(BLACK);
        userStack.add(lName);
        userStack.add(lLevel);

        JButton btnLogout = neoButton("Logout", PINK);
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                Session.clear();
                dispose();
                new FormLogin().setVisible(true);
            }
        });

        right.add(avatar);
        right.add(userStack);
        right.add(btnLogout);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, THICK, BLACK));

        // Section label
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel lMenu = new JLabel("  NAVIGASI");
        lMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lMenu.setForeground(BLACK);
        lMenu.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 0));
        lMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        lMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        sidebar.add(lMenu);

        if ("Admin".equals(Session.level)) {
            activeNavBtn = navBtn(sidebar, "Barang");
                           navBtn(sidebar, "Customer");
                           navBtn(sidebar, "Transaksi");
                           navBtn(sidebar, "Laporan");
        } else {
            activeNavBtn = navBtn(sidebar, "Member");
                           navBtn(sidebar, "Transaksi");
        }

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        foot.setOpaque(false);
        foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        foot.setBorder(BorderFactory.createMatteBorder(THICK, 0, 0, 0, BLACK));
        JLabel lVer = new JLabel("v1.0 • BERKAH JAYA");
        lVer.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lVer.setForeground(BLACK);
        foot.add(lVer);
        sidebar.add(foot);

        return sidebar;
    }

    /** Neobrutalism nav button: active = lime + shadow, inactive = white. */
    private JButton navBtn(JPanel sidebar, String name) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth(), h = getHeight();
                boolean active = (this == activeNavBtn);
                boolean hover  = getModel().isRollover();

                // Inner margin so the card doesn't touch edges
                int mx = 10, my = 6;
                int cardW = w - 2 * mx;
                int cardH = h - 2 * my;

                if (active) {
                    // shadow
                    g2.setColor(SHADOW);
                    g2.fillRect(mx + SHADOW_OFF, my + SHADOW_OFF, cardW - SHADOW_OFF, cardH - SHADOW_OFF);
                    // fill
                    g2.setColor(LIME);
                    g2.fillRect(mx, my, cardW - SHADOW_OFF, cardH - SHADOW_OFF);
                    // border
                    g2.setColor(BLACK);
                    g2.setStroke(new BasicStroke(THICK));
                    int t = THICK / 2;
                    g2.drawRect(mx + t, my + t, cardW - SHADOW_OFF - THICK, cardH - SHADOW_OFF - THICK);
                } else if (hover) {
                    g2.setColor(YELLOW);
                    g2.fillRect(mx, my, cardW - SHADOW_OFF, cardH - SHADOW_OFF);
                    g2.setColor(BLACK);
                    g2.setStroke(new BasicStroke(THICK));
                    int t = THICK / 2;
                    g2.drawRect(mx + t, my + t, cardW - SHADOW_OFF - THICK, cardH - SHADOW_OFF - THICK);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setIcon(navIcon(name, btn));
        btn.setText(name);
        btn.setUI(new BasicButtonUI());
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(11);
        btn.setForeground(BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(200, 54));
        btn.setMinimumSize(new Dimension(120, 54));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            activateNav(btn);
            cardLayout.show(cardPanel, name);
            refreshPanel(name);
        });
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        return btn;
    }

    /** Vector icon 24×24 untuk tiap menu item — flat & bold style */
    private Icon navIcon(String name, JButton owner) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (name) {
                    case "Barang" -> {
                        g2.drawRoundRect(x+3, y+5, 18, 14, 2, 2);
                        g2.drawLine(x+3,  y+11, x+21, y+11);
                        g2.drawLine(x+12, y+5,  x+12, y+11);
                    }
                    case "Customer" -> {
                        g2.drawOval(x+8, y+3, 8, 8);
                        g2.drawArc( x+4, y+13, 16, 9, 0, 180);
                    }
                    case "Transaksi" -> {
                        g2.drawRoundRect(x+5, y+3, 14, 18, 2, 2);
                        g2.drawLine(x+8, y+8,  x+16, y+8);
                        g2.drawLine(x+8, y+12, x+16, y+12);
                        g2.drawLine(x+8, y+16, x+13, y+16);
                    }
                    case "Laporan" -> {
                        g2.fillRect(x+4,  y+14, 4, 7);
                        g2.fillRect(x+10, y+9,  4, 12);
                        g2.fillRect(x+16, y+5,  4, 16);
                        g2.drawLine(x+3, y+21, x+21, y+21);
                    }
                    case "Member" -> {
                        g2.drawOval(x+2,  y+2,  8, 8);
                        g2.drawArc( x+0,  y+12, 12, 8, 0, 180);
                        g2.drawOval(x+13, y+3,  8, 8);
                        g2.drawArc( x+11, y+13, 12, 8, 0, 180);
                    }
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return 24; }
            @Override public int getIconHeight() { return 24; }
        };
    }

    private void activateNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.repaint();
        }
        activeNavBtn = btn;
        btn.repaint();
    }

    // ── Content ────────────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG);

        if ("Admin".equals(Session.level)) {
            panelBarang    = new FormBarang();
            panelCustomer  = new FormCustomer();
            panelTransaksi = new FormTransaksi();
            panelLaporan   = new FormLaporan();
            cardPanel.add(panelBarang,    "Barang");
            cardPanel.add(panelCustomer,  "Customer");
            cardPanel.add(panelTransaksi, "Transaksi");
            cardPanel.add(panelLaporan,   "Laporan");
        } else {
            panelMember    = new FormMember();
            panelTransaksi = new FormTransaksi();
            cardPanel.add(panelMember,    "Member");
            cardPanel.add(panelTransaksi, "Transaksi");
        }

        // Apply neobrutalism styling to all text fields / combo boxes inside each form
        UITheme.applyNeoToTree(cardPanel);

        return cardPanel;
    }

    private void refreshPanel(String name) {
        switch (name) {
            case "Barang"    -> { if (panelBarang    != null) panelBarang.refresh(); }
            case "Customer"  -> { if (panelCustomer  != null) panelCustomer.refresh(); }
            case "Transaksi" -> { if (panelTransaksi != null) panelTransaksi.refresh(); }
            case "Laporan"   -> { if (panelLaporan   != null) panelLaporan.refresh(); }
            case "Member"    -> { if (panelMember    != null) panelMember.refresh(); }
        }
    }

    // ── Neobrutalism button (flat color + hard shadow + thick border) ──────

    private JButton neoButton(String text, Color bg) {
        JButton btn = new JButton(text);
        UITheme.styleButton(btn, bg);
        return btn;
    }
}
