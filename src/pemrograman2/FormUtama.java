package pemrograman2;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class FormUtama extends JFrame {

    private FormBarang    panelBarang;
    private FormCustomer  panelCustomer;
    private FormKategori  panelKategori;
    private FormTransaksi panelTransaksi;
    private FormLaporan   panelLaporan;
    private FormMember    panelMember;

    private JPanel     cardPanel;
    private CardLayout cardLayout;
    private JButton    activeNavBtn;

    // ── Dark gold palette — sama dengan FormLogin ──────────────────────────
    private static final Color BG1      = new Color(22,  17,  2);
    private static final Color BG2      = new Color(36,  28,  4);
    private static final Color CARD     = new Color(42,  33,  7);
    private static final Color CARD_BDR = new Color(72,  58, 14);
    private static final Color GOLD     = new Color(210, 158, 22);
    private static final Color GOLD_HI  = new Color(238, 185, 45);
    private static final Color GOLD_LO  = new Color(168, 122, 12);
    private static final Color MUTED    = new Color(165, 150, 105);
    private static final Color NAV_FG   = new Color(140, 125, 85);

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
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG1, getWidth(), 0, BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle blob kiri
                g2.setColor(new Color(58, 47, 9, 80));
                g2.fillOval(-30, -40, 160, 160);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GOLD_LO),
            BorderFactory.createEmptyBorder(12, 24, 12, 20)));

        // Kiri: logo + judul
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel logoBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GOLD_HI, 0, getHeight(), GOLD_LO));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawRoundRect(cx - 10, cy - 2, 20, 15, 4, 4);
                g2.drawArc(cx - 6, cy - 11, 12, 11, 0, 180);
                g2.drawLine(cx - 6, cy - 6, cx - 6, cy - 2);
                g2.drawLine(cx + 6, cy - 6, cx + 6, cy - 2);
                g2.dispose();
            }
        };
        logoBox.setOpaque(false);
        logoBox.setPreferredSize(new Dimension(40, 40));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel lTitle = new JLabel("BERKAH JAYA");
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lTitle.setForeground(GOLD_HI);
        JLabel lSub = new JLabel("Sistem Informasi Penjualan");
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lSub.setForeground(MUTED);
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
                g2.setPaint(new GradientPaint(0, 0, GOLD_HI, 0, getHeight(), GOLD_LO));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                String init = (Session.namaLengkap != null && !Session.namaLengkap.isEmpty())
                        ? String.valueOf(Session.namaLengkap.charAt(0)).toUpperCase() : "?";
                g2.setColor(new Color(22, 17, 2));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init,
                        (getWidth() - fm.stringWidth(init)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel userStack = new JPanel();
        userStack.setLayout(new BoxLayout(userStack, BoxLayout.Y_AXIS));
        userStack.setOpaque(false);
        JLabel lName  = new JLabel(Session.namaLengkap != null ? Session.namaLengkap : "");
        lName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lName.setForeground(Color.WHITE);
        JLabel lLevel = new JLabel(Session.level != null ? Session.level : "");
        lLevel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lLevel.setForeground(MUTED);
        userStack.add(lName);
        userStack.add(lLevel);

        JButton btnLogout = goldButton("Logout");
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
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, CARD, 0, getHeight(), BG1));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(58, 47, 9, 60));
                g2.fillOval(-50, getHeight() - 200, 240, 240);
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CARD_BDR));

        // Section label
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel lMenu = new JLabel("NAVIGASI");
        lMenu.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lMenu.setForeground(new Color(90, 72, 20));
        lMenu.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        lMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        lMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        sidebar.add(lMenu);

        if ("Admin".equals(Session.level)) {
            activeNavBtn = navBtn(sidebar, "Barang");
                           navBtn(sidebar, "Customer");
                           navBtn(sidebar, "Kategori");
                           navBtn(sidebar, "Transaksi");
                           navBtn(sidebar, "Laporan");
        } else {
            activeNavBtn = navBtn(sidebar, "Member");
                           navBtn(sidebar, "Transaksi");
        }

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        foot.setOpaque(false);
        foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CARD_BDR));
        JLabel lVer = new JLabel("v1.0  •  Berkah Jaya");
        lVer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lVer.setForeground(new Color(72, 58, 20));
        foot.add(lVer);
        sidebar.add(foot);

        return sidebar;
    }

    private JButton navBtn(JPanel sidebar, String name) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeNavBtn) {
                    g2.setColor(new Color(210, 158, 22, 28));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setPaint(new GradientPaint(0, 0, GOLD_HI, 0, getHeight(), GOLD_LO));
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 2, 2);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(210, 158, 22, 12));
                    g2.fillRect(0, 0, getWidth(), getHeight());
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
        btn.setForeground(NAV_FG);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            activateNav(btn);
            cardLayout.show(cardPanel, name);
            refreshPanel(name);
        });
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        return btn;
    }

    /** Vector icon 24×24 untuk tiap menu item */
    private Icon navIcon(String name, JButton owner) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean active = (owner == activeNavBtn);
                Color   bgTop  = active ? GOLD_HI          : new Color(72, 58, 13);
                Color   bgBot  = active ? GOLD_LO          : new Color(48, 38,  8);
                Color   ic     = active ? new Color(22,17,2) : new Color(170,145,65);

                // Background pill
                g2.setPaint(new GradientPaint(x, y, bgTop, x, y + 24, bgBot));
                g2.fillRoundRect(x, y, 24, 24, 8, 8);

                // Vector icon (stroke)
                g2.setColor(ic);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (name) {
                    case "Barang" -> {
                        // kotak paket
                        g2.drawRoundRect(x+4, y+6, 16, 13, 3, 3);
                        g2.drawLine(x+4,  y+11, x+20, y+11);
                        g2.drawLine(x+12, y+6,  x+12, y+11);
                    }
                    case "Customer" -> {
                        // orang
                        g2.drawOval(x+8, y+3, 8, 8);
                        g2.drawArc( x+4, y+13, 16, 9, 0, 180);
                    }
                    case "Kategori" -> {
                        // 4 kotak grid
                        g2.drawRoundRect(x+4,  y+4,  6, 6, 2, 2);
                        g2.drawRoundRect(x+14, y+4,  6, 6, 2, 2);
                        g2.drawRoundRect(x+4,  y+14, 6, 6, 2, 2);
                        g2.drawRoundRect(x+14, y+14, 6, 6, 2, 2);
                    }
                    case "Transaksi" -> {
                        // dokumen / receipt
                        g2.drawRoundRect(x+5, y+3, 14, 18, 3, 3);
                        g2.drawLine(x+8, y+8,  x+16, y+8);
                        g2.drawLine(x+8, y+12, x+16, y+12);
                        g2.drawLine(x+8, y+16, x+13, y+16);
                    }
                    case "Laporan" -> {
                        // bar chart
                        g2.fillRect(x+4,  y+14, 4, 7);
                        g2.fillRect(x+10, y+9,  4, 12);
                        g2.fillRect(x+16, y+5,  4, 16);
                        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawLine(x+3, y+21, x+21, y+21);
                    }
                    case "Member" -> {
                        // dua orang
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
            activeNavBtn.setForeground(NAV_FG);
            activeNavBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            activeNavBtn.repaint();
        }
        activeNavBtn = btn;
        btn.setForeground(GOLD_HI);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.repaint();
    }

    // ── Content ────────────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(248, 244, 232)); // warm off-white

        if ("Admin".equals(Session.level)) {
            panelBarang    = new FormBarang();
            panelCustomer  = new FormCustomer();
            panelKategori  = new FormKategori();
            panelTransaksi = new FormTransaksi();
            panelLaporan   = new FormLaporan();
            cardPanel.add(panelBarang,    "Barang");
            cardPanel.add(panelCustomer,  "Customer");
            cardPanel.add(panelKategori,  "Kategori");
            cardPanel.add(panelTransaksi, "Transaksi");
            cardPanel.add(panelLaporan,   "Laporan");
        } else {
            panelMember    = new FormMember();
            panelTransaksi = new FormTransaksi();
            cardPanel.add(panelMember,    "Member");
            cardPanel.add(panelTransaksi, "Transaksi");
        }

        return cardPanel;
    }

    private void refreshPanel(String name) {
        switch (name) {
            case "Barang"    -> { if (panelBarang    != null) panelBarang.refresh(); }
            case "Customer"  -> { if (panelCustomer  != null) panelCustomer.refresh(); }
            case "Kategori"  -> { if (panelKategori  != null) panelKategori.refresh(); }
            case "Transaksi" -> { if (panelTransaksi != null) panelTransaksi.refresh(); }
            case "Laporan"   -> { if (panelLaporan   != null) panelLaporan.refresh(); }
            case "Member"    -> { if (panelMember    != null) panelMember.refresh(); }
        }
    }

    // ── Gold button (sama gaya dengan FormLogin) ───────────────────────────

    private JButton goldButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isPressed() ? GOLD_LO : GOLD_HI;
                Color bot = getModel().isPressed() ? GOLD_LO.darker() : GOLD_LO;
                if (getModel().isRollover() && !getModel().isPressed()) { top = GOLD_HI.brighter(); bot = GOLD; }
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setUI(new BasicButtonUI());
        btn.setForeground(new Color(22, 17, 2));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }
}
