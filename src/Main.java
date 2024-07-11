import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        //FormularioServicio fs= new FormularioServicio();


        Operaciones op = new Operaciones();
        VisualLogIn vli = new VisualLogIn(op);
        //borrarServicios bs = new borrarServicios(op);
    }

    }
    /*
    // Database URL
    static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/Conexcion_Proyecto";
    // Database credentials
    static final String USER = "c##jellz";
    static final String PASS = "Jjwm20020";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Register Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql = "SELECT CONTRATO_DESC FROM contratos";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name

                String descripcion = rs.getString("CONTRATO_DESC");

                // Display values

                System.out.println( descripcion);

            }
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // Finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }

     */
