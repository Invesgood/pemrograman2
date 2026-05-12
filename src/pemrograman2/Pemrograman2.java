package pemrograman2;

import javax.swing.SwingUtilities;

public class Pemrograman2 {

    public static void main(String[] args) {
        UITheme.applyNimbus();
        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
