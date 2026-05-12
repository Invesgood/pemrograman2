package pemrograman2;

public class Session {

    public static int    idUser;
    public static String username;
    public static String namaLengkap;
    public static String level;

    public static void setUser(int id, String user, String nama, String lvl) {
        idUser      = id;
        username    = user;
        namaLengkap = nama;
        level       = lvl;
    }

    public static void clear() {
        idUser      = 0;
        username    = "";
        namaLengkap = "";
        level       = "";
    }
}
