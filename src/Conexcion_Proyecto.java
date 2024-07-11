import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexcion_Proyecto {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "c##jellz";
    private static final String PASSWORD = "Jjwm20020";
    private static Connection conn = null;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa");
        }
        return conn;
    }

    public static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Conexión cerrada");
        }
    }
}
