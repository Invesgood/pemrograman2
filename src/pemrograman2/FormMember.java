package pemrograman2;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class FormMember extends JPanel {

    private FormCustomer     panelCek;
    private FormDaftarMember panelDaftar;
    private CardLayout       cards;
    private JPanel           cardPanel;
    private JButton          btnCek, btnDaftar;
    private String           activeCard = "cek";

    public FormMember() {
        setLayout(new BorderLayout(0, 0));
        initComponents();
    }

    private void initComponents() {
        // ── Toggle Bar ───────────────────────────────────────────────────────
        JPanel toggleBar = new JPanel(new BorderLayout());
        toggleBar.setBackground(UITheme.PRIMARY_DARK);
        toggleBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));

        JLabel lblSection = new JLabel("MEMBER");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSection.setForeground(new Color(200, 225, 255));

        JPanel pillBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        pillBox.setOpaque(false);

        btnCek    = new JButton("  Cek Member  ");
        btnDaftar = new JButton("  Daftar Member  ");

        applyActive(btnCek);
        applyInactive(btnDaftar);

        btnCek.addActionListener(e -> switchTo("cek"));
        btnDaftar.addActionListener(e -> switchTo("daftar"));

        pillBox.add(btnCek);
        pillBox.add(btnDaftar);

        toggleBar.add(lblSection, BorderLayout.WEST);
        toggleBar.add(pillBox,    BorderLayout.CENTER);

        // ── Cards ────────────────────────────────────────────────────────────
        cards     = new CardLayout();
        cardPanel = new JPanel(cards);

        panelCek    = new FormCustomer();
        panelDaftar = new FormDaftarMember();

        cardPanel.add(panelCek,    "cek");
        cardPanel.add(panelDaftar, "daftar");

        add(toggleBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    private void switchTo(String name) {
        activeCard = name;
        cards.show(cardPanel, name);
        if ("cek".equals(name)) {
            applyActive(btnCek);
            applyInactive(btnDaftar);
            panelCek.refresh();
        } else {
            applyActive(btnDaftar);
            applyInactive(btnCek);
            panelDaftar.refresh();
        }
    }

    public void refresh() {
        if ("cek".equals(activeCard)) panelCek.refresh();
        else panelDaftar.refresh();
    }

    // ── Toggle button styling ────────────────────────────────────────────────

    private void applyActive(JButton btn) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(Color.WHITE);
        btn.setForeground(UITheme.PRIMARY_DARK);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void applyInactive(JButton btn) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(new Color(60, 100, 160));
        btn.setForeground(new Color(200, 225, 255));
        btn.setFont(UITheme.FONT_BODY);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
