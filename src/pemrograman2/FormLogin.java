package pemrograman2;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class FormLogin extends JFrame {

    // ── Dark gold palette ───────────────────────────────────────────────────
    private static final Color BG1      = new Color(22, 17, 2);
    private static final Color BG2      = new Color(36, 28, 4);
    private static final Color BLOB     = new Color(58, 47, 9, 150);
    private static final Color CARD     = new Color(42, 33, 7);
    private static final Color CARD_BDR = new Color(72, 58, 14);
    private static final Color GOLD     = new Color(210, 158, 22);
    private static final Color GOLD_HI  = new Color(238, 185, 45);
    private static final Color GOLD_LO  = new Color(168, 122, 12);
    private static final Color FIELD    = new Color(54, 43, 9);
    private static final Color FIELD_BD = new Color(160, 124, 28);
    private static final Color PH_CLR   = new Color(120, 105, 55);
    private static final Color MUTED    = new Color(165, 150, 105);

    private static final String PH_PASS = "Masukkan password...";

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private Point          dragOrigin;

    public FormLogin() {
        setTitle("Login — Berkah Jaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(480, 610);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // ── Background ─────────────────────────────────────────────────────
        JPanel outer = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG1, getWidth(), getHeight(), BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BLOB);
                g2.fillOval(-90, -90, 310, 310);
                g2.fillOval(getWidth() - 190, getHeight() - 190, 290, 290);
                g2.dispose();
            }
        };
        setContentPane(outer);
        makeDraggable(outer);

        // ── Card ───────────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
                g2.setColor(CARD_BDR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 570));

        // ── Close button (top-right) ───────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        topBar.setOpaque(false);
        JButton btnX = new JButton("×");
        btnX.setUI(new BasicButtonUI());
        btnX.setForeground(MUTED);
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        btnX.setOpaque(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnX.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { btnX.setForeground(MUTED); }
        });
        btnX.addActionListener(e -> System.exit(0));
        topBar.add(btnX);

        // ── Content area ───────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(4, 42, 36, 42));

        // Lock icon
        JPanel lockIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2;
                g2.setPaint(new GradientPaint(0, 0, GOLD_HI, 0, h, GOLD_LO));
                g2.fillOval(0, 0, w - 1, h - 1);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 9, cy - 18, 18, 16, 0, 180);
                g2.drawLine(cx - 9, cy - 10, cx - 9, cy - 5);
                g2.drawLine(cx + 9, cy - 10, cx + 9, cy - 5);
                g2.drawRoundRect(cx - 13, cy - 5, 26, 20, 5, 5);
                g2.setStroke(new BasicStroke(1f));
                g2.fillOval(cx - 3, cy + 2, 6, 6);
                g2.dispose();
            }
        };
        lockIcon.setOpaque(false);
        lockIcon.setPreferredSize(new Dimension(64, 64));
        lockIcon.setMaximumSize(new Dimension(64, 64));
        lockIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Selamat Datang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk untuk melanjutkan");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        JLabel lUser = goldLabel("Username");
        txtUsername = buildTextField();
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        // Password field
        JLabel lPass = goldLabel("Password");
        txtPassword = buildPasswordField();
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        txtPassword.addActionListener(e -> doLogin());

        // Masuk button
        JButton btnLogin = buildGoldButton("Masuk");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnLogin.addActionListener(e -> doLogin());

        // "Lupa password?" link
        JLabel lblForgot = new JLabel("Lupa password?", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblForgot.setForeground(GOLD);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { lblForgot.setForeground(GOLD_HI); }
            @Override public void mouseExited(MouseEvent e)  { lblForgot.setForeground(GOLD); }
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(FormLogin.this,
                    "Hubungi administrator untuk reset password.",
                    "Lupa Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        content.add(lockIcon);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(lblTitle);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(lblSub);
        content.add(Box.createRigidArea(new Dimension(0, 34)));
        content.add(lUser);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(txtUsername);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(lPass);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(txtPassword);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(btnLogin);
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(lblForgot);

        card.add(topBar,  BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        outer.add(card);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private JLabel goldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(GOLD);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return lbl;
    }

    private JTextField buildTextField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(isFocusOwner() ? GOLD : FIELD_BD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        f.setUI(new BasicTextFieldUI());
        f.setOpaque(false);
        f.setForeground(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return f;
    }

    private JPasswordField buildPasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(isFocusOwner() ? GOLD : FIELD_BD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        f.setUI(new BasicPasswordFieldUI());
        f.setOpaque(false);
        f.setEchoChar((char) 0);
        f.setText(PH_PASS);
        f.setForeground(PH_CLR);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (new String(f.getPassword()).equals(PH_PASS)) {
                    f.setText("");
                    f.setForeground(Color.WHITE);
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

    private JButton buildGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isPressed() ? GOLD_LO  : GOLD_HI;
                Color bot = getModel().isPressed() ? GOLD_LO.darker() : GOLD_LO;
                if (getModel().isRollover() && !getModel().isPressed()) {
                    top = GOLD_HI.brighter();
                    bot = GOLD;
                }
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setUI(new BasicButtonUI());
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        return btn;
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
