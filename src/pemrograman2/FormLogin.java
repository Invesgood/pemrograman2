package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class FormLogin extends JFrame {

    // ── Soft neobrutalism palette (warm yellow + pastel) ──────────────────
    private static final Color BG       = new Color(255, 255, 255); // card white
    private static final Color BLACK    = new Color( 38,  38,  48); // charcoal
    private static final Color YELLOW   = new Color(255, 214,  90); // softer warm yellow
    private static final Color LIME     = new Color(168, 230, 161); // pastel green
    private static final Color PINK     = new Color(255, 122, 138); // soft coral
    private static final Color PH_CLR   = new Color(150, 150, 158);
    private static final Color SHADOW   = new Color( 38,  38,  48, 46); // soft translucent
    private static final int   THICK    = 2;
    private static final int   SHADOW_OFF = 5;
    private static final int   ARC      = 16;

    private static final String PH_PASS = "Masukkan password...";

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private Point          dragOrigin;

    public FormLogin() {
        setTitle("Login — Berkah Jaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(480, 620);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // ── Background (cyan dengan polka-dot subtle) ──────────────────────
        JPanel outer = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(YELLOW);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // dotted pattern
                g2.setColor(new Color(0, 0, 0, 25));
                for (int y = 0; y < getHeight(); y += 28) {
                    for (int x = 0; x < getWidth(); x += 28) {
                        g2.fillOval(x, y, 4, 4);
                    }
                }
                g2.dispose();
            }
        };
        setContentPane(outer);
        makeDraggable(outer);

        // ── Card (white with thick black border + hard shadow) ─────────────
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int arc = ARC + 6;
                g2.setColor(SHADOW);
                g2.fillRoundRect(SHADOW_OFF, SHADOW_OFF, w - SHADOW_OFF, h - SHADOW_OFF, arc, arc);
                g2.setColor(BG);
                g2.fillRoundRect(0, 0, w - SHADOW_OFF, h - SHADOW_OFF, arc, arc);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRoundRect(t, t, w - SHADOW_OFF - THICK, h - SHADOW_OFF - THICK, arc, arc);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 580));

        // ── Close button (top-right) ───────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, SHADOW_OFF));
        JButton btnX = new JButton("×");
        btnX.setUI(new BasicButtonUI());
        btnX.setForeground(BLACK);
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnX.setOpaque(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setContentAreaFilled(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnX.setForeground(PINK); }
            @Override public void mouseExited(MouseEvent e)  { btnX.setForeground(BLACK); }
        });
        btnX.addActionListener(e -> System.exit(0));
        topBar.add(btnX);

        // ── Content area ───────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(4, 38, 36, 38 + SHADOW_OFF));

        // Lock icon — square block w/ shadow
        JPanel lockIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int s = SHADOW_OFF;
                g2.setColor(SHADOW);
                g2.fillRoundRect(s, s, w - s, h - s, ARC, ARC);
                g2.setColor(LIME);
                g2.fillRoundRect(0, 0, w - s, h - s, ARC, ARC);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRoundRect(t, t, w - s - THICK, h - s - THICK, ARC, ARC);
                // lock symbol
                int cx = (w - s) / 2, cy = (h - s) / 2;
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 9, cy - 16, 18, 16, 0, 180);
                g2.drawLine(cx - 9, cy - 8, cx - 9, cy - 3);
                g2.drawLine(cx + 9, cy - 8, cx + 9, cy - 3);
                g2.drawRoundRect(cx - 13, cy - 3, 26, 20, 4, 4);
                g2.fillOval(cx - 3, cy + 4, 6, 6);
                g2.dispose();
            }
        };
        lockIcon.setOpaque(false);
        lockIcon.setPreferredSize(new Dimension(70, 70));
        lockIcon.setMaximumSize(new Dimension(70, 70));
        lockIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("SELAMAT DATANG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(BLACK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk untuk melanjutkan");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSub.setForeground(BLACK);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel lUser = neoLabel("USERNAME");
        txtUsername = buildTextField();
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Password
        JLabel lPass = neoLabel("PASSWORD");
        txtPassword = buildPasswordField();
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtPassword.addActionListener(e -> doLogin());

        // Masuk button
        JButton btnLogin = buildNeoButton("MASUK", LIME);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btnLogin.addActionListener(e -> doLogin());

        // "Lupa password?" link
        JLabel lblForgot = new JLabel("Lupa password?", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblForgot.setForeground(BLACK);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { lblForgot.setForeground(PINK); }
            @Override public void mouseExited(MouseEvent e)  { lblForgot.setForeground(BLACK); }
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(FormLogin.this,
                    "Hubungi administrator untuk reset password.",
                    "Lupa Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        content.add(lockIcon);
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(lblTitle);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.add(lblSub);
        content.add(Box.createRigidArea(new Dimension(0, 26)));
        content.add(lUser);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(txtUsername);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(lPass);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(txtPassword);
        content.add(Box.createRigidArea(new Dimension(0, 24)));
        content.add(btnLogin);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(lblForgot);

        card.add(topBar,  BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        outer.add(card);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private JLabel neoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(BLACK);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return lbl;
    }

    private JTextField buildTextField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, ARC, ARC);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRoundRect(t, t, getWidth() - THICK, getHeight() - THICK, ARC, ARC);
                g2.dispose();
            }
        };
        f.setUI(new BasicTextFieldUI());
        f.setOpaque(false);
        f.setForeground(BLACK);
        f.setFont(new Font("Segoe UI", Font.BOLD, 14));
        f.setCaretColor(BLACK);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return f;
    }

    private JPasswordField buildPasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRoundRect(t, t, getWidth() - THICK, getHeight() - THICK, ARC, ARC);
                g2.dispose();
            }
        };
        f.setUI(new BasicPasswordFieldUI());
        f.setOpaque(false);
        f.setEchoChar((char) 0);
        f.setText(PH_PASS);
        f.setForeground(PH_CLR);
        f.setFont(new Font("Segoe UI", Font.BOLD, 14));
        f.setCaretColor(BLACK);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (new String(f.getPassword()).equals(PH_PASS)) {
                    f.setText("");
                    f.setForeground(BLACK);
                    f.setEchoChar('•');
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0);
                    f.setText(PH_PASS);
                    f.setForeground(PH_CLR);
                }
            }
        });
        return f;
    }

    private JButton buildNeoButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int s = SHADOW_OFF;
                boolean pressed = getModel().isPressed();
                boolean hover   = getModel().isRollover();

                if (!pressed) {
                    g2.setColor(SHADOW);
                    g2.fillRoundRect(s, s, w - s, h - s, ARC, ARC);
                }
                int dx = pressed ? s : 0;
                int dy = pressed ? s : 0;
                Color fill = bg;
                if (hover && !pressed) fill = brighten(bg, 14);
                g2.setColor(fill);
                g2.fillRoundRect(dx, dy, w - s, h - s, ARC, ARC);
                g2.setColor(BLACK);
                g2.setStroke(new BasicStroke(THICK));
                int t = THICK / 2;
                g2.drawRoundRect(dx + t, dy + t, w - s - THICK, h - s - THICK, ARC, ARC);
                g2.dispose();
                Graphics2D gt = (Graphics2D) g.create();
                if (pressed) gt.translate(s, s);
                super.paintComponent(gt);
                gt.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setUI(new BasicButtonUI());
        btn.setForeground(BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 22, 12 + SHADOW_OFF, 22 + SHADOW_OFF));
        return btn;
    }

    private Color brighten(Color c, int amount) {
        return new Color(
            Math.min(255, c.getRed()   + amount),
            Math.min(255, c.getGreen() + amount),
            Math.min(255, c.getBlue()  + amount));
    }

    private void makeDraggable(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { dragOrigin = e.getPoint(); }
            @Override public void mouseReleased(MouseEvent e) { dragOrigin = null; }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragOrigin != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragOrigin.x, loc.y + e.getY() - dragOrigin.y);
                }
            }
        });
    }

    // ── Login logic ─────────────────────────────────────────────────────────

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (pass.equals(PH_PASS)) pass = "";

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password harus diisi!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection con = Koneksi.getKoneksi();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM tb_user WHERE username = ? AND password = ?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session.setUser(rs.getInt("id_user"), rs.getString("username"),
                            rs.getString("nama_lengkap"), rs.getString("level"));
                    dispose();
                    new FormUtama().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setEchoChar((char) 0);
                    txtPassword.setText(PH_PASS);
                    txtPassword.setForeground(PH_CLR);
                    txtPassword.requestFocus();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
