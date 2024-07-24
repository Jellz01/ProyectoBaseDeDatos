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

    public boolean agregarPersona(String cedula, String nombre, String apellido, String telefono, String direccion, String email) {
        boolean state = false;

        try {
            PreparedStatement sentencia = conn.prepareStatement("INSERT INTO VE_PERSONAS (per_id, per_nombre,per_apellido, per_direccion, per_telefono, per_correo_electronico,per_cedula) VALUES (SEQ_VE_PERSONAS.NEXTVAL,?,?,?,?,?,?)");

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

    public boolean agregarEmpleado(String cedula, String nombre, String apellido, String telefono, String direccion, String email, String tipConNombre) {
        boolean state = false;

        int tipConId = -1;
        String perId = null;

        // Obtener TIP_CON_ID basado en el nombre del contrato
        String queryTipoContrato = "SELECT TIP_CON_ID FROM VE_TIPOS_CONTRATOS WHERE TIP_CON_NOMBRE = ?";
        try (PreparedStatement sentenciaTipoContrato = conn.prepareStatement(queryTipoContrato)) {
            sentenciaTipoContrato.setString(1, tipConNombre);

            try (ResultSet rsTipoContrato = sentenciaTipoContrato.executeQuery()) {
                if (rsTipoContrato.next()) {
                    tipConId = rsTipoContrato.getInt("TIP_CON_ID");
                    System.out.println("TIP_CON_ID: " + tipConId);
                } else {
                    System.out.println("No se encontró el tipo de contrato con el nombre: " + tipConNombre);
                    return false; // Salir si no se encontró el tipo de contrato
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Salir en caso de error con la consulta del tipo de contrato
        }

        // Obtener el siguiente valor de la secuencia SEQ_VE_PERSONAS
        String querySecuencia = "SELECT SEQ_VE_PERSONAS.NEXTVAL AS NEXT_VAL FROM DUAL";
        try (PreparedStatement sentenciaSecuencia = conn.prepareStatement(querySecuencia);
             ResultSet rsSecuencia = sentenciaSecuencia.executeQuery()) {

            if (rsSecuencia.next()) {
                perId = rsSecuencia.getString("NEXT_VAL"); // Obtener el siguiente valor de la secuencia
                System.out.println("PER_ID (sec): " + perId);
            } else {
                System.out.println("No se pudo obtener el valor de la secuencia.");
                return false; // Salir si no se pudo obtener el valor de la secuencia
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Salir en caso de error al obtener el valor de la secuencia
        }

        // Insertar en VE_PERSONAS usando el valor obtenido de la secuencia
        String insertPersonaQuery = "INSERT INTO VE_PERSONAS (per_id, per_nombre, per_cedula, per_apellido, per_direccion, per_telefono, per_correo_electronico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentenciaPersona = conn.prepareStatement(insertPersonaQuery)) {
            sentenciaPersona.setString(1, perId); // Usar el valor de la secuencia
            sentenciaPersona.setString(2, nombre);
            sentenciaPersona.setString(3, cedula);
            sentenciaPersona.setString(4, apellido);
            sentenciaPersona.setString(5, direccion);
            sentenciaPersona.setString(6,telefono);
            sentenciaPersona.setString(7, email);

            int rowsAffected = sentenciaPersona.executeUpdate();

            if (rowsAffected > 0) {
                // Insertar en VE_EMPLEADOS usando el mismo PER_ID
                String insertEmpleadoQuery = "INSERT INTO VE_EMPLEADOS (EMP_ID, PER_ID, TIP_CON_ID) VALUES (SEQ_VE_EMPLEADOS.NEXTVAL, ?, ?)";
                try (PreparedStatement sentenciaEmpleado = conn.prepareStatement(insertEmpleadoQuery)) {
                    sentenciaEmpleado.setString(1, perId); // Usar el mismo valor de PER_ID
                    sentenciaEmpleado.setInt(2, tipConId);

                    int rowsAffectedEmpleado = sentenciaEmpleado.executeUpdate();

                    if (rowsAffectedEmpleado > 0) {
                        JOptionPane.showMessageDialog(null, "Se ingresó un empleado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                        state = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "No se ingresó un empleado en VE_EMPLEADOS. ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
                    JOptionPane.showMessageDialog(null, "ERROR: Se ha intentado insertar un valor duplicado para la clave primaria en VE_EMPLEADOS.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    duplicateKeyException.printStackTrace();
                } catch (SQLException ex) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se ingresó un empleado en VE_PERSONAS. ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
            JOptionPane.showMessageDialog(null, "ERROR: Se ha intentado insertar un valor duplicado para la clave primaria en VE_PERSONAS.", "ERROR", JOptionPane.ERROR_MESSAGE);
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

    public boolean verificarExistencia(int clienteId, int empleadoId, int mascotaId) throws SQLException {
        String query = "SELECT 1 FROM VE_CLIENTES WHERE CLI_ID = ? UNION ALL " +
                "SELECT 1 FROM EMPLEADOS WHERE EMP_ID = ? UNION ALL " +
                "SELECT 1 FROM MASCOTAS WHERE MAS_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, empleadoId);
            stmt.setInt(3, mascotaId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Verificar que al menos una fila existe en los resultados
                return rs.next();
            }
        }
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

    public boolean eliminarCita(int id) {
        boolean state = false;

        String query = "UPDATE VE_CITAS SET CIT_ESTADO = 'CANCELADO' WHERE CIT_ID = ?";
        try (PreparedStatement sentencia = conn.prepareStatement(query)) {
            sentencia.setInt(1, id);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Cita cancelada correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la cita para cancelar", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Consider logging the exception
            JOptionPane.showMessageDialog(null, "Error al intentar cancelar la cita", "ERROR", JOptionPane.ERROR_MESSAGE);
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

    public int obtenerIDCliente(String cliente) {
        // Assuming `cliente` has the format "Nombre Apellido"
        // Split the string by space and take the first part
        String nombre = cliente.split(" ")[0];

        int idResultado = 10; // Default value or use another sentinel value

        try {
            String query = "SELECT c.CLI_ID FROM VE_CLIENTES c JOIN VE_PERSONAS p ON c.PER_ID = p.PER_ID WHERE p.PER_NOMBRE = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idResultado = resultSet.getInt("CLI_ID");
                        System.out.println("ID encontrado: " + idResultado);
                    } else {
                        System.out.println("No se encontró ningún registro con el nombre: " + nombre);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }

    public int obtenerIDServicio(String servicio) {
        // Assuming `cliente` has the format "Nombre Apellido"
        // Split the string by space and take the first part
        String nombre = servicio.split(" ")[0];

        int idResultado = 10; // Default value or use another sentinel value

        try {
            String query = "SELECT SER_ID FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idResultado = resultSet.getInt("SER_ID");
                        System.out.println("ID encontrado: " + idResultado);
                    } else {
                        System.out.println("No se encontró ningún registro con el nombre: " + nombre);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }


    public int obtenerIDEmpleado(String empleado) {
        // Assuming `empleado` has the format "Nombre Apellido"
        // Split the string by space and take the first part
        String nombre = empleado.split(" ")[0];

        int idResultado = -1; // Default value or use another sentinel value

        try {
            String query = "SELECT VE_EMPLEADOS.EMP_ID FROM VE_EMPLEADOS JOIN VE_PERSONAS ON VE_EMPLEADOS.EMP_ID = VE_PERSONAS.PER_ID WHERE VE_PERSONAS.PER_NOMBRE LIKE ?";
            try (PreparedStatement statement = this.conn.prepareStatement(query)) {
                statement.setString(1, nombre + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idResultado = resultSet.getInt("EMP_ID");
                        System.out.println("ID encontrado: " + idResultado);
                    } else {
                        System.out.println("No se encontró ningún registro con el nombre: " + nombre);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }


    public int obtenerIDmascota(String nombre) {
        int idResultado = 0; // Initialize ID to 0 or any default value

        try {
            String query = "SELECT MAS_ID FROM VE_TIPO_MASCOTAS WHERE MAS_NOMBRE  = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {

                        idResultado = resultSet.getInt("MAS_ID");

                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return idResultado;
    }

    public int obtenerIDUsuario(String nombre) {
        int idResultado = 0; // Initialize ID to 0 or any default value

        try {
            String query = "SELECT USU_ID FROM VE_USUARIOS WHERE USU_NOMBRE LIKE ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, nombre);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {

                        idResultado = resultSet.getInt("USU_ID");
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
            // Updated query to include PER_DIRECCION
            String query = "SELECT e.EMP_ID, p.PER_NOMBRE, p.PER_APELLIDO, p.PER_CEDULA, p.PER_DIRECCION " +
                    "FROM VE_EMPLEADOS e " +
                    "JOIN VE_PERSONAS p ON e.PER_ID = p.PER_ID";

            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String empID = resultSet.getString("EMP_ID");
                    String nombreResult = resultSet.getString("PER_NOMBRE");
                    String apellidoResult = resultSet.getString("PER_APELLIDO");
                    String perCedula = resultSet.getString("PER_CEDULA");
                    String perDireccion = resultSet.getString("PER_DIRECCION");

                    // Create a string representation of the employee details
                    String empleadoDetails = "EMP_ID: " + empID +
                            ", Nombre: " + nombreResult +
                            ", Apellido: " + apellidoResult +
                            ", Cedula: " + perCedula +
                            ", Direccion: " + perDireccion;

                    empleadoList.add(empleadoDetails);
                }

                // Print the list after the loop
                System.out.println(empleadoList);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return empleadoList;
    }



    public ArrayList<String> obtenerTodosLasCitas() {
        ArrayList<String> listaCitas = new ArrayList<>();

        String query = "SELECT c.CIT_ID, c.CIT_NOMBRE_MASCOTA, c.CIT_FECHA_HORA, c.CIT_ESTADO, p.PER_NOMBRE AS EMPLEADO_NOMBRE " +
                "FROM VE_CITAS c " +
                "JOIN VE_EMPLEADOS e ON c.EMP_ID = e.EMP_ID " +
                "JOIN VE_PERSONAS p ON e.PER_ID = p.PER_ID";

        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String citID = resultSet.getString("CIT_ID");
                String nombreMascota = resultSet.getString("CIT_NOMBRE_MASCOTA");
                String fechaHora = resultSet.getString("CIT_FECHA_HORA");
                String citEstado = resultSet.getString("CIT_ESTADO");
                String empleadoNombre = resultSet.getString("EMPLEADO_NOMBRE");

                String citaDetails = "ID: " + citID +
                        ", Nombre Mascota: " + nombreMascota +
                        ", Fecha y Hora: " + fechaHora +
                        ", Estado: " + citEstado +
                        ", Empleado: " + empleadoNombre;

                listaCitas.add(citaDetails);
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
            String query = "SELECT vp.PER_NOMBRE, vp.PER_APELLIDO FROM VE_PERSONAS vp JOIN VE_CLIENTES ves ON vp.PER_ID = ves.PER_ID";
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


    public boolean modificarServicio(String nombre) {

        boolean state = false;


        try {
            PreparedStatement sentencia = conn.prepareStatement("UPDATE VE_SERVICIOS SET SER_ESTADO = 'ACTIVADO' WHERE SER_NOMBRE = ?");


            sentencia.setString(1, nombre);


            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                //JOptionPane.showMessageDialog(null, "Se ha MODIFICADO el SERIVICOS exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
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


    public boolean agregarUsuario( int codigoEmp, String usuario, String contrasena, String permiso) {
        boolean state = false;
        PreparedStatement sentencia = null;
        ResultSet rs = null;

        try {




            // Preparar la inserción
            sentencia = conn.prepareStatement( "INSERT INTO VE_USUARIOS(USU_ID,EMP_ID,USU_NOMBRE,USU_CONTRASENA,USU_PERMISO) VALUES (SEQ_VE_USUARIOS.NEXTVAL,?,?,?,?)");

            sentencia.setInt(1,codigoEmp);
            sentencia.setString(2,  usuario);
            sentencia.setString(3,contrasena);
            sentencia.setString(4, permiso);

            int rowsAffected = sentencia.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario agregado exitosamente");
                state = true;
            } else {
                System.err.println("Error: No se pudo agregar el usuario");
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

        // Queries
        String estadoQuery = "SELECT SER_ESTADO FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";
        String updateQuery = "UPDATE VE_SERVICIOS SET SER_ESTADO = 'Suspended' WHERE SER_NOMBRE = ?";
        String deleteQuery = "DELETE FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(estadoQuery)) {
            selectStmt.setString(1, nombre);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("SER_ESTADO");
                    if ("Activo".equalsIgnoreCase(estado)) {
                        // If the service is active, update its status to 'Suspended'
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, nombre);
                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Servicio presente en factura, desactivado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                                state = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "Servicio no se pudo desactivar", "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        return state; // Exit after updating the status
                    }
                }
            }

            // If not active, proceed to delete the service
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, nombre);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Servicio eliminado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    state = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Servicio no se pudo eliminar", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return state;
    }


    public ArrayList<String> obtenerEmpleados () {
            ArrayList<String> listaEmpleados = new ArrayList<>();

            try {
                String query = "SELECT per_nombre FROM VE_PERSONAS vp, VE_EMPLEADOS vee WHERE vp.per_id = vee.per_id AND TIP_CON_ID =1 ORDER BY 1";
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

        // SQL query to fetch client names
        String query = "SELECT VP.PER_NOMBRE " +
                "FROM VE_CLIENTES VC " +
                "JOIN VE_PERSONAS VP ON VP.PER_ID = VC.PER_ID " +
                "WHERE VC.CLI_ACTIVO = 'Y'"; // Assuming 'S' means active client

        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set
            while (resultSet.next()) {
                // Get the client name from the result set
                String nombreCliente = resultSet.getString("PER_NOMBRE");

                // Add the client name to the list
                listaClientes.add(nombreCliente);
            }

            // Optionally print the list after fetching all data
            System.out.println(listaClientes);

        } catch (SQLException ex) {
            // Print exception details
            ex.printStackTrace();
            // Handle exception as needed, maybe logging or rethrowing
        }

        return listaClientes;
    }

    public ArrayList<String> obtenerCitas() {
        ArrayList<String> listaCitas = new ArrayList<>();

        try {
            String query = "SELECT c.CIT_ID AS Cita_ID, c.CIT_NOMBRE_MASCOTA AS Nombre_Perro, TO_CHAR(c.CIT_FECHA_HORA, 'DD-MON-YY HH24:MI:SS') AS Fecha_Cita FROM VE_CITAS c";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Retrieve data from the result set
                    String citaId = resultSet.getString("Cita_ID");
                    String nombrePerro = resultSet.getString("Nombre_Perro");
                    String fechaCita = resultSet.getString("Fecha_Cita");

                    // Create a string representation of the appointment details
                    String datosCita = "ID: " + citaId + ", Perro: " + nombrePerro + ", Fecha: " + fechaCita;

                    // Add the string representation to the list
                    listaCitas.add(datosCita);
                    System.out.println(datosCita);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaCitas;
    }

    public ArrayList<String> obtenerCitass() {
        ArrayList<String> listaCitas = new ArrayList<>();

        try {
            String query = "SELECT c.CIT_ID AS Cita_ID, c.CIT_NOMBRE_MASCOTA AS Nombre_Perro, TO_CHAR(c.CIT_FECHA_HORA, 'DD-MON-YY HH24:MI:SS') AS Fecha_Cita FROM VE_CITAS c";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Retrieve data from the result set
                    String citaId = resultSet.getString("Cita_ID");
                    String nombrePerro = resultSet.getString("Nombre_Perro");
                    String fechaCita = resultSet.getString("Fecha_Cita");

                    // Create a string representation of the appointment details
                    String datosCita = citaId + " " + nombrePerro ;

                    // Add the string representation to the list
                    listaCitas.add(datosCita);
                    System.out.println(datosCita);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaCitas;
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


    public boolean ingresarFacturaCabecera(int id, String num, String fecha, float subtotal, float iva, float total, int cliId, int usuId, int cantidad, float precio_uni, float subtotal_detalle, int ser_id) {
        boolean state = false;

        Connection conn = null;
        PreparedStatement sentencia = null;

        try {
            // Suponiendo que 'conn' es tu conexión a la base de datos
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##jellz", "Jjwm20020");
            conn.setAutoCommit(false); // Inicia la transacción

            // SQL query para insertar en VE_CABECERA_FACTURAS
            String sqlCabecera = "INSERT INTO VE_CABECERA_FACTURAS (CAB_FAC_ID, CAB_FAC_NUMERO, CAB_FAC_FECHA, CAB_FAC_SUBTOTAL, CAB_FAC_IVA, CAB_FAC_VALOR_TOTAL, CLI_ID, USU_ID) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?)";
            sentencia = conn.prepareStatement(sqlCabecera);
            sentencia.setInt(1, id);
            sentencia.setString(2, num);
            sentencia.setString(3, fecha);
            sentencia.setFloat(4, subtotal);
            sentencia.setFloat(5, iva);
            sentencia.setFloat(6, total);
            sentencia.setInt(7, cliId);
            sentencia.setInt(8, usuId);

            int rowsAffected = sentencia.executeUpdate();

            // Comprobar si la inserción fue exitosa
            if (rowsAffected > 0) {
                // Inserción en VE_DETALLE_FACTURAS
                String sqlDetalle = "INSERT INTO VE_DETALLE_FACTURAS (DET_FAC_ID, DET_FAC_CANTIDAD, DET_FAC_PRECIO_UNITARIO, DET_FAC_SUBTOTAL, DET_FAC_IVA, DET_FAC_TOTAL, CAB_FAC_ID, SER_ID) VALUES (SEQ_VE_DETALLE_FACTURAS.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
                sentencia = conn.prepareStatement(sqlDetalle);
                sentencia.setInt(1, cantidad);
                sentencia.setFloat(2, precio_uni);
                sentencia.setFloat(3, subtotal_detalle);
                sentencia.setFloat(4, iva);
                sentencia.setFloat(5, total);
                sentencia.setInt(6, id); // Asegura que el CAB_FAC_ID coincida
                sentencia.setInt(7, ser_id);

                int rowsAffectedDetalle = sentencia.executeUpdate();

                if (rowsAffectedDetalle > 0) {
                    conn.commit(); // Confirma la transacción
                    JOptionPane.showMessageDialog(null, "Se ha ingresado la factura exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    state = true;
                } else {
                    conn.rollback(); // Deshace la transacción en caso de error
                    JOptionPane.showMessageDialog(null, "ERROR: No se ha ingresado el detalle de la factura", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                conn.rollback(); // Deshace la transacción en caso de error
                JOptionPane.showMessageDialog(null, "ERROR: No se ha ingresado la factura", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Deshace la transacción en caso de excepción
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, e);
                }
            }
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
            // Adjust the query to fetch only clients
            String query = "SELECT vp.per_id, vp.per_nombre, vp.per_apellido, vp.per_direccion, vp.per_telefono, vp.per_correo_electronico " +
                    "FROM VE_PERSONAS vp " +
                    "JOIN VE_CLIENTES vc ON vp.per_id = vc.per_id " +
                    "WHERE vc.cli_activo = 'Y' " + // Assuming 'S' means active client
                    "ORDER BY vp.per_nombre";

            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Retrieve the values from the result set
                    String perId = resultSet.getString("per_id");
                    String perNombre = resultSet.getString("per_nombre");
                    String perApellido = resultSet.getString("per_apellido");
                    String perDireccion = resultSet.getString("per_direccion");
                    String perTelefono = resultSet.getString("per_telefono");
                    String perCorreo = resultSet.getString("per_correo_electronico");

                    // Create a string representation of the client details
                    String datosCliente = "ID: " + perId + ", Nombre: " + perNombre +
                            ", Apellido: " + perApellido + ", Direccion: " + perDireccion +
                            ", Telefono: " + perTelefono + ", Correo: " + perCorreo;

                    listaClientes.add(datosCliente);
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
            String query = "SELECT SER_NOMBRE FROM VE_SERVICIOS WHERE SER_ESTADO LIKE 'Active'";

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






