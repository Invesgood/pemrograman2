package pemrograman2;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "db_berkah_jaya";
    private static final String USER = "root";
    private static final String PASS = "Root0211";

    private static Connection connection = null;

    public static Connection getKoneksi() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                connection = DriverManager.getConnection(url, USER, PASS);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database!\n\n" + e.getMessage()
                    + "\n\nPastikan MySQL berjalan dan database 'db_berkah_jaya' sudah dibuat.",
                    "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }
}
