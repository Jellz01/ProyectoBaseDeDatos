import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class ControlerMenuPrincipal {

    private ArrayList<String> empleados;
    Operaciones op = new Operaciones();
    Properties properties;
    String usuarioU;

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
                try {
                    listarTiposAnimales lta = new listarTiposAnimales(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

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
                //FormularioFactura ff = new FormularioFactura(op);
                break;

            case 14:
                break;

            case 15:
                try {

                    ListarFactura lf = new ListarFactura(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 16:
                try {
                    formularioConsulta fc = new formularioConsulta(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 17:
                try {
                    ListarConsultas lcc = new ListarConsultas(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 18:
                cancelarCita ccc = new cancelarCita(op);
                break;

            case 19:
                try {
                    ListarConsultas lc = new ListarConsultas(op);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 20:
                FormularioServicio fs = new FormularioServicio(op);
                break;

            case 21:
                modificarServicio ms = new modificarServicio(op);
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

    public void factura(String usuarioU){
        this.usuarioU = usuarioU;
        FormularioFactura ff = new FormularioFactura(op,usuarioU);
    }
}
