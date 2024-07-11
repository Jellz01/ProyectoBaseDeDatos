import java.sql.SQLException;
import java.util.ArrayList;

public class ControlerMenuPrincipal {

    private ArrayList<String> empleados;
    Operaciones op = new Operaciones();

    public ControlerMenuPrincipal(int opcion, ViewMenuPrincipal vmp){
        empleados = new ArrayList<>();
        switch (opcion) {
            case 1:
                try {
                    formulariosIngresarPersona fip = new formulariosIngresarPersona(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                    op.conectar();
                 empleados = op.obtenerTodosLosEmpleados();
                System.out.print(empleados);
                break;

            case 3:

                break;

            case 4:
                try {
                    ListarEmpleados le = new ListarEmpleados(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 5:
                try {
                    formularioIngresarAnimal fia = new formularioIngresarAnimal(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                try {
                    formularioIngresarCliente fic = new formularioIngresarCliente(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 10:
                break;

            case 11:
                borrarClientes bc = new borrarClientes(op);
                break;

            case 12:
                try {
                    listarClientes lc = new listarClientes(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 13:
                formularioFactura ff = new formularioFactura(op);
                break;

            case 14:
                break;

            case 15:
                break;

            case 16:
                try {
                    formularioConsulta fc = new formularioConsulta(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 17:
                break;

            case 18:
                break;

            case 19:
                break;

            case 20:
                FormularioServicio fs = new FormularioServicio(op);
                break;

            case 21:
                break;
            case 22:
                borrarServicios bs = new borrarServicios(op);
                break;

            case 23:
                try {
                    listarServicios ls = new listarServicios(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            default:
                // Handle other cases if needed
                break;
        }
    }
}
