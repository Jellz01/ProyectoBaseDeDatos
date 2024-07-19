import oracle.sql.CHAR;

import java.awt.List;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


public class Operaciones {

    private Connection conn;

    public void conectar() {
        try {
            // Connection URL Syntax for Oracle
            String url = "jdbc:oracle:thin:@localhost:1521:xe";
            String user = "c##jellz";
            String password = "Jjwm20020";

            // Establish the connection
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa");
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean agregarContrato(String idContrato, String descripcion) {
        boolean state = false;
        PreparedStatement sentencia = null;

        try {
            sentencia = conn.prepareStatement("INSERT INTO TipoContrato VALUES (?,?)");
            sentencia.setString(1, idContrato);
            sentencia.setString(2, descripcion);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contrato agregado exitosamente");
                state = true;
            } else {
                System.err.println("Error: No se pudo agregar el contrato");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeResources(sentencia);
        }

        return state;
    }

    public boolean agregarEmpleado(String cedula, String nombre, String apellido, String telefono, String direccion, String email) {
        boolean state = false;

        try {
            PreparedStatement sentencia = conn.prepareStatement("INSERT INTO VE_PERSONAS VALUES (SEQ_VE_PERSONAS.NEXTVAL,?,?,?,?,?,?)");

            sentencia.setString(1, nombre);
            sentencia.setString(2, apellido);
            sentencia.setString(3, direccion);
            sentencia.setString(4, telefono);
            sentencia.setString(5, email);
            sentencia.setString(6, cedula);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Se ingreso un Empleado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "NO Se ingreso un Empleado ERROR", "ERROR", JOptionPane.ERROR);
            }

            sentencia.close();
        } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
            JOptionPane.showMessageDialog(null, "ERROR: Se ha intentado insertar un valor duplicado para la clave primaria.", "ERROR", JOptionPane.ERROR_MESSAGE);
            // Handle the exception as needed, log, or perform additional actions
            duplicateKeyException.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        return state;
    }

    public boolean agregarCliente(String cedula, String nombre, String apellido, String telefono, String direccion, String correoElectronico) {
        boolean state = false;

        long personaId = -1; // Use long for handling larger IDs

        try {
            // Step 1: Obtain the next sequence value
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SEQ_VE_PERSONAS.NEXTVAL AS NEXT_ID FROM DUAL");
            if (rs.next()) {
                personaId = rs.getLong("NEXT_ID");
            }
            rs.close();
            stmt.close();

            // Step 2: Insert into VE_PERSONAS using the obtained ID
            PreparedStatement sentenciaPersona = conn.prepareStatement(
                    "INSERT INTO VE_PERSONAS (PER_ID, PER_NOMBRE, PER_APELLIDO, PER_DIRECCION, PER_TELEFONO, PER_CORREO_ELECTRONICO, PER_CEDULA) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            sentenciaPersona.setLong(1, personaId);
            sentenciaPersona.setString(2, nombre);
            sentenciaPersona.setString(3, apellido);
            sentenciaPersona.setString(4, direccion);
            sentenciaPersona.setString(5, telefono);
            sentenciaPersona.setString(6, correoElectronico);
            System.out.println(correoElectronico);
            sentenciaPersona.setString(7, cedula);

            int rowsAffectedPersona = sentenciaPersona.executeUpdate();

            if (rowsAffectedPersona > 0) {
                sentenciaPersona.close();

                // Step 3: Insert into VE_CLIENTES using the same ID
                PreparedStatement sentenciaCliente = conn.prepareStatement(
                        "INSERT INTO VE_CLIENTES (CLI_ID, PER_ID, CLI_ACTIVO) VALUES (SEQ_VE_CLIENTES.NEXTVAL, ?, 'Y')"
                );
                sentenciaCliente.setLong(1, personaId);

                int rowsAffectedCliente = sentenciaCliente.executeUpdate();
                if (rowsAffectedCliente > 0) {
                    JOptionPane.showMessageDialog(null, "Se ingresó un Cliente correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    state = true;
                } else {
                    JOptionPane.showMessageDialog(null, "No se ingresó el Cliente. ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                sentenciaCliente.close();
            } else {
                JOptionPane.showMessageDialog(null, "No se ingresó la Persona. ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
            JOptionPane.showMessageDialog(null, "ERROR: Se ha intentado insertar un valor duplicado para la clave primaria.", "ERROR", JOptionPane.ERROR_MESSAGE);
            duplicateKeyException.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Ensure you close the connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }

        return state;
    }


    public boolean agregarCita(String nombreMascota, String fechaHoraStr, String estado, int clienteId, int empleadoId, int mascotaId) {
        boolean state = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // Ajusta el formato aquí

        try {
            // Convertir String a Date
            Date parsedDate = sdf.parse(fechaHoraStr);
            Timestamp fechaHora = new Timestamp(parsedDate.getTime());

            PreparedStatement sentencia = conn.prepareStatement(
                    "INSERT INTO VE_CITAS (CIT_ID, CIT_NOMBRE_MASCOTA, CIT_FECHA_HORA, CIT_ESTADO, CLI_ID, EMP_ID, MAS_ID) " +
                            "VALUES (SEQ_VE_CITAS.NEXTVAL, ?, ?, ?, ?, ?, ?)"
            );

            sentencia.setString(1, nombreMascota);
            sentencia.setTimestamp(2, fechaHora);
            sentencia.setString(3, estado);
            sentencia.setInt(4, clienteId);
            sentencia.setInt(5, empleadoId);
            sentencia.setInt(6, mascotaId);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Cita agregada correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo agregar la cita", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            sentencia.close();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "ERROR: El formato de la fecha y hora es incorrecto.", "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        return state;
    }

    public boolean agregarTipoAnimal(String tipo) {
        boolean state = false;

        try {
            PreparedStatement sentencia = conn.prepareStatement("INSERT INTO VE_TIPO_MASCOTAS VALUES (SEQ_VE_PERSONAS.NEXTVAL,?)");

            sentencia.setString(1, tipo);


            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Se ingreso un tipo de animal correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "NO Se ingreso un tipo de animal ERROR", "ERROR", JOptionPane.ERROR);
            }

            sentencia.close();
        } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
            JOptionPane.showMessageDialog(null, "ERROR:Ya existe es TIPO de animal.", "ERROR", JOptionPane.ERROR_MESSAGE);
            // Handle the exception as needed, log, or perform additional actions
            duplicateKeyException.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }


        return state;
    }




    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            try {
                if (resource != null) {
                    resource.close();
                }
            } catch (Exception e) {
                // Handle or log the exception
            }
        }
    }

    /**
     * @return
     */

    public ArrayList<String> obtenerContratos() throws SQLException {
        ArrayList<String> contratoList = new ArrayList<>();

        try {
            String query = "SELECT * FROM VE_TIPOS_CONTRATOS";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String descripcion = resultSet.getString("TIP_CON_NOMBRE");
                contratoList.add(descripcion);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex; // Rethrow the exception to handle it at the calling point
        }

        System.out.println(contratoList); // Print the contratoList to verify the contents
        return contratoList;
    }


    public ArrayList<String> obtenerTiposAnimales() throws SQLException {
        ArrayList<String> tiposanimales = new ArrayList<>();

        try {
            String query = "SELECT MAS_NOMBRE FROM VE_TIPO_MASCOTAS";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String descripcion = resultSet.getString("MAS_NOMBRE");
                tiposanimales.add(descripcion);
                System.out.print(descripcion);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex; // Rethrow the exception to handle it at the calling point
        }

        System.out.println(tiposanimales); // Print the contratoList to verify the contents
        return tiposanimales;
    }

    public boolean eliminarEmpleado(String cedula) {
        boolean state = false;

        try {
            String query = "DELETE FROM VE_PERSONAS WHERE PER_ID = ?";
            try (PreparedStatement sentencia = conn.prepareStatement(query)) {
                sentencia.setString(1, cedula);

                int rowsAffected = sentencia.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Empleado eliminado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    state = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Empleado no se pudo eliminar REVISAR LA CEDULA", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return state;
    }

    public int obtenerIDVeterinario(String nombre) {
        int idResultado = 0; // Initialize ID to 0 or any default value

        try {
            String query = "SELECT VE.EMP_ID FROM VE_PERSONAS PER JOIN VE_EMPLEADOS VE ON VE.EMP_ID = PER.PER_ID WHERE PER.PER_NOMBRE = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Retrieve the EMP_ID from the ResultSet
                        idResultado = resultSet.getInt("EMP_ID");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }

    public int obtenerIDCliente(String nombre) {
        int idResultado = 0; // Initialize ID to 0 or any default value

        try {
            String query = "SELECT PER_ID FROM VE_PERSONAS WHERE PER_NOMBRE  = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {

                        idResultado = resultSet.getInt("PER_ID");
                        System.out.println("CACAC"+idResultado);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }



    public ArrayList<String> obtenerAnimalesTipos() {
        ArrayList<String> listaTipos = new ArrayList<>();

        try {
            String query = "SELECT MAS_ID,MAS_NOMBRE FROM VE_TIPO_MASCOTAS";
            try (PreparedStatement statement = conn.prepareStatement(query)) {


                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Assuming your Empleado table has columns like 'Cedula', 'Nombre', 'Apellido', etc.
                        String IDResult = resultSet.getString("MAS_ID");
                        String nombreResult = resultSet.getString("MAS_NOMBRE");



                        // Add more columns as needed

                        // Create a string representation of the employee details
                        String empleadoDetails = "ID: " + IDResult + ", Nombre: " + nombreResult;

                        listaTipos.add(empleadoDetails);
                    }


                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        if (listaTipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existen tipos de animales en nuestro sistema", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return listaTipos;
    }


    public ArrayList<String> obtenerTodosLosEmpleados() {
        ArrayList<String> empleadoList = new ArrayList<>();

        try {
            String query = "SELECT * FROM VE_PERSONAS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String perID = resultSet.getString("PER_ID");
                    String nombreResult = resultSet.getString("PER_NOMBRE");
                    String apellidoResult = resultSet.getString("PER_APELLIDO");
                    String perDireccion = resultSet.getString("PER_DIRECCION");
                    String perTelefono = resultSet.getString("PER_TELEFONO");
                    String perEmail = resultSet.getString("PER_CORREO_ELECTRONICO");


                    // Create a string representation of the employee details
                    String empleadoDetails = "Cedula: " + perID + ", Nombre: " + nombreResult +
                            ", Apellido: " + apellidoResult + ", Telefono: " + perTelefono +
                            ", Direccion: " + perDireccion + ", perEmail: " + perEmail;

                    empleadoList.add(empleadoDetails);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return empleadoList;

    }



    public ArrayList<String> obtenerTodosLasCitas() {
        ArrayList<String> listaCitas = new ArrayList<>();

        try {
            String query = "SELECT * FROM VE_PERSONAS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String citID = resultSet.getString("CIT_ID");
                    String nombreMascota = resultSet.getString("CIT_NOMBRE_MASCOTA");
                    String fechaHora = resultSet.getString("CIT_FECHA_HORA");
                    String citEstado = resultSet.getString("CIT_ESTADO");
                    String idCli = resultSet.getString("CLI_ID");
                    String idEmp = resultSet.getString("EMP_ID");
                    String idMas = resultSet.getString("MAS_ID");


                    // Suponiendo que citID, nombreMascota, fechaHora, citEstado, idCli, idEmp, idMas son variables correctamente definidas y asignadas

                    String empleadoDetails = "Cedula: " + citID +
                            ", Nombre: " + nombreMascota +
                            ", Apellido: " + fechaHora +
                            ", Telefono: " + citEstado +
                            ", Direccion: " + idCli +
                            ", perEmail: " + idEmp +
                            ", permail: " + idMas;

                    listaCitas.add(empleadoDetails);

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaCitas;

    }


    public ArrayList<String> obtenerNombreClientes() {
        ArrayList<String> clienteLista = new ArrayList<>();

        try {
            String query = "SELECT PER_NOMBRE,PER_APELLIDO FROM VE_PERSONAS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String nombreResult = resultSet.getString("PER_NOMBRE");
                    String apellidoResult = resultSet.getString("PER_APELLIDO");


                    // Create a string representation of the employee details
                    String empleadoDetails = nombreResult + " " + apellidoResult;

                    clienteLista.add(empleadoDetails);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return clienteLista;

    }


    public ArrayList<String> obtenerDescripcionesTipoContrato() {
        ArrayList<String> listaContratos = new ArrayList<>();

        try {
            String query = "SELECT Descripcion FROM TipoContrato";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Retrieve the "Descripcion" column from the result set
                    String contratoDescripcion = resultSet.getString("Descripcion");

                    // Add the description to the list
                    listaContratos.add(contratoDescripcion);
                    System.out.println(listaContratos);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaContratos;
    }


    public boolean modificarEmpleado(String cedula, String nombre, String apellido, String telefono, String salario, int idContrato) {

        boolean state = false;


        try {
            PreparedStatement sentencia = conn.prepareStatement("UPDATE Empleado SET Nombre = ?, Apellido = ?, Telefono = ?, Salario = ?, idTipoContrato = ? WHERE Cedula = ?");


            sentencia.setString(1, nombre);
            sentencia.setString(2, apellido);
            sentencia.setString(3, telefono);
            sentencia.setString(4, salario);
            sentencia.setInt(5, idContrato);
            sentencia.setString(6, cedula);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Se ha MODIFICADO el empleado exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "ERROR: NO Se ha MODIFICADO el empleado ", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            sentencia.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        return state;
    }

    public boolean obtenerUsuario(String usuario) {
        boolean state = false;
        PreparedStatement pstmt = null; // Declare PreparedStatement outside try block

        try {
            // Create the SQL query with a parameter placeholder
            String query = "SELECT usu_nombre FROM VE_USUARIOS WHERE usu_nombre LIKE ?";

            // Prepare the statement with the query
            pstmt = conn.prepareStatement(query);

            // Set the parameter value based on the usuario parameter
            pstmt.setString(1, usuario);

            // Execute the query and obtain the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the result set has any rows
                if (rs.next()) {
                    state = true; // Set state to true if a matching user is found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // Throw a RuntimeException if a SQL exception occurs
        } finally {
            // Close the PreparedStatement in the finally block to ensure it's always closed
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // Handle or log the exception as needed
                }
            }
        }

        return state; // Return the boolean state indicating if the user was found
    }

    public boolean obtenerContrasena(String contrasena) {
        boolean state = false;
        PreparedStatement pstmt = null; // Declare PreparedStatement outside try block

        try {
            // Create the SQL query with a parameter placeholder
            String query = "SELECT usu_contrasena FROM VE_USUARIOS WHERE usu_contrasena LIKE ?";

            // Prepare the statement with the query
            pstmt = conn.prepareStatement(query);

            // Set the parameter value based on the usuario parameter
            pstmt.setString(1, contrasena);

            // Execute the query and obtain the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the result set has any rows
                if (rs.next()) {
                    state = true; // Set state to true if a matching user is found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // Throw a RuntimeException if a SQL exception occurs
        } finally {
            // Close the PreparedStatement in the finally block to ensure it's always closed
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // Handle or log the exception as needed
                }
            }
        }

        return state; // Return the boolean state indicating if the user was found
    }


    public ArrayList<String> obtenerTodosLosServicios() {
        ArrayList<String> listaServicios = new ArrayList<>();

        try {
            String query = "SELECT * FROM VE_SERVICIOS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String serId = resultSet.getString("SER_ID");
                    String serCodigo = resultSet.getString("SER_CODIGO");
                    String serNombre = resultSet.getString("SER_NOMBRE");
                    int serPrecio = resultSet.getInt("SER_PRECIO");
                    String serTieneIva = resultSet.getString("SER_TIENE_IVA");
                    String serEstado = resultSet.getString("SER_ESTADO");


                    // Create a string representation of the employee details
                    String datosServico = "id: " + serId + ", codigo: " + serCodigo +
                            ", Nombre: " + serNombre + ", precio: " + serPrecio +
                            ", IVA: " + serTieneIva + ", estado: " + serEstado;

                    listaServicios.add(datosServico);
                    System.out.println(listaServicios);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaServicios;

    }


    public boolean agregarServicio(String ser_codigo, String ser_nombre, float ser_precio, String ser_tiene_iva, String ser_estado) {
        boolean state = false;
        PreparedStatement sentencia = null;
        ResultSet rs = null;

        try {
            // Verificar que ser_codigo no sea null o vacío
            if (ser_codigo == null || ser_codigo.isEmpty()) {
                System.err.println("Error: El código del servicio no puede ser nulo o vacío");
                return false;
            }

            // Comprobar si el código del servicio ya existe
            String checkQuery = "SELECT COUNT(*) FROM VE_SERVICIOS WHERE SER_CODIGO = ?";
            sentencia = conn.prepareStatement(checkQuery);
            sentencia.setString(1, ser_codigo);
            rs = sentencia.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Error: El código del servicio ya existe");
                return false;
            }

            // Preparar la inserción
            sentencia = conn.prepareStatement("INSERT INTO VE_SERVICIOS VALUES (SEQ_VE_SERVICIOS.NEXTVAL, ?, ?, ?, ?, ?)");
            sentencia.setString(1, ser_codigo);
            sentencia.setString(2, ser_nombre);
            sentencia.setFloat(3, ser_precio);
            sentencia.setString(4, ser_tiene_iva);
            sentencia.setString(5, ser_estado);

            int rowsAffected = sentencia.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Servicio agregado exitosamente");
                state = true;
            } else {
                System.err.println("Error: No se pudo agregar el servicio");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeResources(rs, sentencia);
        }

        return state;
    }


    public ArrayList<String> obtenerServicios() {
        ArrayList<String> listaServicios = new ArrayList<>();

        try {
            String query = "SELECT SER_NOMBRE FROM VE_SERVICIOS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String serNombre = resultSet.getString("SER_NOMBRE");


                    // Create a string representation of the employee details
                    String datosServico = serNombre;

                    listaServicios.add(datosServico);
                    System.out.println(listaServicios);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaServicios;

    }

    public boolean eliminarServicio(String nombre) {
        boolean state = false;

        try {
            String query = "DELETE  FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";
            try (PreparedStatement sentencia = conn.prepareStatement(query)) {
                sentencia.setString(1, nombre);

                int rowsAffected = sentencia.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Servicio eliminado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    state = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Servicio no se pudo eliminar ", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return state;
    }

    public ArrayList<String> obtenerEmpleados() {
        ArrayList<String> listaEmpleados = new ArrayList<>();

        try {
            String query = "SELECT per_nombre FROM VE_PERSONAS vp, VE_EMPLEADOS vee WHERE vp.per_id = vee.per_id ORDER BY 1";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String serNombre = resultSet.getString("per_nombre");


                    // Create a string representation of the employee details
                    String datosServico = serNombre;

                    listaEmpleados.add(datosServico);
                    System.out.println(listaEmpleados);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaEmpleados;

    }

    public boolean eliminarEmpleadoo(String nombre) {
        boolean state = false;

        try {
            // Primero obtenemos el PER_ID del empleado usando el nombre
            String queryGetId = "SELECT PER_ID FROM VE_PERSONAS WHERE PER_NOMBRE = ?";
            try (PreparedStatement getIdStmt = conn.prepareStatement(queryGetId)) {
                getIdStmt.setString(1, nombre);
                ResultSet rs = getIdStmt.executeQuery();

                // Verificamos si encontramos el empleado
                if (rs.next()) {
                    int perId = rs.getInt("PER_ID");

                    // Luego procedemos a eliminar el empleado usando PER_ID
                    String deleteQuery = "DELETE FROM VE_PERSONAS WHERE PER_ID = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, perId);

                        int rowsAffected = deleteStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Empleado eliminado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                            state = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "El Empleado no se pudo eliminar", "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Empleado no encontrado", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return state;
    }

    public ArrayList<String> obtenerClientes() {
        ArrayList<String> listaClientes = new ArrayList<>();

        try {
            String query = "SELECT VP.PER_NOMBRE FROM VE_CLIENTES VC JOIN VE_PERSONAS VP ON VP.PER_ID = VC.PER_ID ";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String serNombre = resultSet.getString("PER_NOMBRE");


                    // Create a string representation of the employee details
                    String datosServico = serNombre;

                    listaClientes.add(datosServico);
                    System.out.println(listaClientes);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaClientes;

    }


    public ArrayList<String> obtenerUsuarios() {
        ArrayList<String> listaUsuarios = new ArrayList<>();

        try {
            String query = "SELECT USU_NOMBRE FROM VE_USUARIOS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String serNombre = resultSet.getString("USU_NOMBRE");


                    // Create a string representation of the employee details
                    String datosServico = serNombre;

                    listaUsuarios.add(datosServico);
                    System.out.println(listaUsuarios);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaUsuarios;

    }

    public int obtenerNumFactura() {
        int count = 0;
        try {
            String query = "SELECT COUNT(CAB_FAC_ID) AS total_facturas FROM VE_CABECERA_FACTURAS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    count = resultSet.getInt("total_facturas");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }
        return count + 1;
    }


    public boolean ingresarFacturaCabecera(int num, String numfac, String fecha, int subtotal, int iva, int facTotal) {
        boolean state = false;

        try {
            PreparedStatement sentencia = conn.prepareStatement("INSERT INTO VE_CABECERA_FACTURAS VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?)");

            sentencia.setInt(1, num);
            sentencia.setString(2, numfac);
            sentencia.setString(3, fecha); // Assuming fecha is in format "DD-MM-YYYY"
            sentencia.setInt(4, subtotal);
            sentencia.setInt(5, iva);
            sentencia.setInt(6, facTotal);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Se ha ingresado la factura exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "ERROR: No se ha ingresado la factura", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            sentencia.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        return state;
    }

    public boolean eliminarCliente(String nombre) {
        boolean state = false;


        try {
            conn.setAutoCommit(false); // Iniciar una transacción

            // Buscar el PER_ID y CLI_ID del cliente por nombre
            String query = "SELECT p.PER_ID, c.CLI_ID " +
                    "FROM VE_PERSONAS p, VE_CLIENTES c " +
                    "WHERE p.PER_ID = c.CLI_ID AND p.PER_NOMBRE = ?";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int perId = resultSet.getInt("PER_ID");
                        int cliId = resultSet.getInt("CLI_ID");

                        // Eliminar de la tabla VE_CITAS
                        String deleteCitasQuery = "DELETE FROM VE_CITAS WHERE CLI_ID = ?";
                        try (PreparedStatement deleteCitasStmt = conn.prepareStatement(deleteCitasQuery)) {
                            deleteCitasStmt.setInt(1, cliId);
                            deleteCitasStmt.executeUpdate();
                        }

                        // Eliminar de la tabla VE_EMPLEADOS
                        String deleteEmpleadosQuery = "DELETE FROM VE_EMPLEADOS WHERE PER_ID = ?";
                        try (PreparedStatement deleteEmpleadosStmt = conn.prepareStatement(deleteEmpleadosQuery)) {
                            deleteEmpleadosStmt.setInt(1, perId);
                            deleteEmpleadosStmt.executeUpdate();
                        }

                        // Eliminar de la tabla VE_CLIENTES
                        String deleteClientesQuery = "DELETE FROM VE_CLIENTES WHERE CLI_ID = ?";
                        try (PreparedStatement deleteClientesStmt = conn.prepareStatement(deleteClientesQuery)) {
                            deleteClientesStmt.setInt(1, cliId);
                            deleteClientesStmt.executeUpdate();
                        }

                        // Eliminar de la tabla VE_PERSONAS
                        String deletePersonasQuery = "DELETE FROM VE_PERSONAS WHERE PER_ID = ?";
                        try (PreparedStatement deletePersonasStmt = conn.prepareStatement(deletePersonasQuery)) {
                            deletePersonasStmt.setInt(1, perId);

                            int rowsAffected = deletePersonasStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Cliente eliminado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                                state = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "El Cliente no se pudo eliminar", "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }

                conn.commit(); // Confirmar la transacción
            } catch (SQLException ex) {
                conn.rollback(); // Revertir la transacción en caso de error
                ex.printStackTrace();
                // Manejar la excepción según sea necesario
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejar la excepción según sea necesario
        } finally {
            try {
                conn.setAutoCommit(true); // Restaurar el modo de autocommit
                conn.close(); // Cerrar la conexión
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return state;
    }


    public ArrayList<String> obtenerTodosLosClientes() {
        ArrayList<String> listaClientes = new ArrayList<>();

        try {
            String query = "SELECT * FROM VE_PERSONAS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String serId = resultSet.getString("PER_ID");
                    String serNombre = resultSet.getString("PER_NOMBRE");
                    String serApellido = resultSet.getString("PER_APELLIDO");
                    String serDireccion = resultSet.getString("PER_DIRECCION");
                    String serTelefono = resultSet.getString("PER_TELEFONO");
                    String sercorreo = resultSet.getString("PER_CORREO_ELECTRONICO");


                    // Create a string representation of the employee details
                    String datosServico = "id: " + serId + ", nombre: " + serNombre +
                            ", Apellido: " + serApellido + ", Direccion: " + serDireccion +
                            ", telefono: " + serTelefono + ", correo: " + sercorreo;

                    listaClientes.add(datosServico);
                    System.out.println(listaClientes);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaClientes;

    }


    public ArrayList<String> obtenerServiciosCombo() {
        ArrayList<String> listaServicios = new ArrayList<>();

        try {
            String query = "SELECT SER_NOMBRE FROM VE_SERVICIOS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {


                    String serNombre = resultSet.getString("SER_NOMBRE");


                    // Create a string representation of the employee details
                    String datosServico = serNombre;

                    listaServicios.add(datosServico);
                    System.out.println(listaServicios);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaServicios;

    }



    public int obtenerPrecioUnitario(String serviceName) {
        int precio = 0; // Default to 0 in case no result is found
        String query = "SELECT SER_PRECIO FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, serviceName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the price from the result set
                    precio = resultSet.getInt("SER_PRECIO");
                }
            }
            System.out.println(precio);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return precio;
    }


    public String obtenerEmailCliente(String cliente) {
        String email = ""; // Default to 0 in case no result is found
        String query = "SELECT PER_CORREO_ELECTRONICO FROM VE_PERSONAS WHERE PER_NOMBRE=?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, cliente);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the price from the result set
                    email = resultSet.getString("PER_CORREO_ELECTRONICO");
                }
            }
            System.out.println(email);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return email;
    }


}






