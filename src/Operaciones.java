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
            sentenciaPersona.setString(6, telefono);
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
        // Ajustar el formato a 'yyyy-MM-dd HH:mm:ss'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // Convertir String a Date
            Date parsedDate = sdf.parse(fechaHoraStr);
            Timestamp fechaHora = new Timestamp(parsedDate.getTime());

            // Verificar si ya existe una cita en el mismo horario para el mismo veterinario con estado 'Scheduled'
            String checkQuery = "SELECT COUNT(*) FROM VE_CITAS WHERE EMP_ID = ? AND CIT_FECHA_HORA = ? AND CIT_ESTADO = 'Scheduled'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, empleadoId);
            checkStmt.setTimestamp(2, fechaHora);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

            // Solo mostrar mensaje de error si ya hay una cita programada en el mismo horario
            if (count > 0 && estado.equals("Scheduled")) {
                JOptionPane.showMessageDialog(null, "Ya existe una cita para este veterinario en el mismo horario.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Si no hay citas existentes o el estado es 'Cancelado', proceder con la inserción
            String insertQuery = "INSERT INTO VE_CITAS (CIT_ID, CIT_NOMBRE_MASCOTA, CIT_FECHA_HORA, CIT_ESTADO, CLI_ID, EMP_ID, MAS_ID) " +
                    "VALUES (SEQ_VE_CITAS.NEXTVAL, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

            insertStmt.setString(1, nombreMascota);
            insertStmt.setTimestamp(2, fechaHora);
            insertStmt.setString(3, estado);
            insertStmt.setInt(4, clienteId);
            insertStmt.setInt(5, empleadoId);
            insertStmt.setInt(6, mascotaId);

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Cita agregada correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo agregar la cita", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            insertStmt.close();

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

        // Convertir el tipo de animal a mayúsculas
        tipo = tipo.toUpperCase();

        try {
            // Verificar si el tipo de animal ya existe en mayúsculas
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM VE_TIPO_MASCOTAS WHERE UPPER(MAS_NOMBRE) = ?");
            checkStmt.setString(1, tipo);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "El tipo de animal ya existe.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return state; // Salir del método si el tipo de animal ya existe
            }
            rs.close();
            checkStmt.close();

            // Insertar el nuevo tipo de animal en mayúsculas
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO VE_TIPO_MASCOTAS (MAS_ID, MAS_NOMBRE) VALUES (SEQ_VE_PERSONAS.NEXTVAL, ?)");
            insertStmt.setString(1, tipo);

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Se ingresó un tipo de animal correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "No se ingresó un tipo de animal. ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            insertStmt.close();
        } catch (SQLIntegrityConstraintViolationException duplicateKeyException) {
            JOptionPane.showMessageDialog(null, "ERROR: Ya existe este tipo de animal.", "ERROR", JOptionPane.ERROR_MESSAGE);
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


    public int obtenerIDEmpleado(String cedula) {
        int idResultado = -1; // Default value or use another sentinel value

        try {
            // La consulta SQL correcta para obtener el EMP_ID usando PER_CEDULA
            String query = "SELECT VE_EMPLEADOS.EMP_ID FROM VE_EMPLEADOS JOIN VE_PERSONAS ON VE_EMPLEADOS.PER_ID = VE_PERSONAS.PER_ID WHERE VE_PERSONAS.PER_CEDULA = ?";
            try (PreparedStatement statement = this.conn.prepareStatement(query)) {
                statement.setString(1, cedula);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idResultado = resultSet.getInt("EMP_ID");
                        System.out.println("ID encontrado: " + idResultado);
                    } else {
                        System.out.println("No se encontró ningún registro con la cédula: " + cedula);
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
                        System.out.println("CACAC" + idResultado);
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


    public boolean agregarUsuario(int codigoEmp, String usuario, String contrasena, String permiso) {
        boolean state = false;
        PreparedStatement sentencia = null;
        ResultSet rs = null;

        try {


            // Preparar la inserción
            sentencia = conn.prepareStatement("INSERT INTO VE_USUARIOS(USU_ID,EMP_ID,USU_NOMBRE,USU_CONTRASENA,USU_PERMISO) VALUES (SEQ_VE_USUARIOS.NEXTVAL,?,?,?,?)");

            sentencia.setInt(1, codigoEmp);
            sentencia.setString(2, usuario);
            sentencia.setString(3, contrasena);
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
            String query = "SELECT SER_NOMBRE FROM VE_SERVICIOS WHERE SER_ESTADO = 'Activo' OR SER_ESTADO = 'NoUsado'";

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

    public ArrayList<String> obtenerServiciosModificar() {
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

    public ArrayList<String> obtenerTodsLosServicios() {
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

        // Query para verificar el estado del servicio
        String checkQuery = "SELECT SER_ESTADO FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";

        // Query para eliminar el servicio
        String deleteQuery = "DELETE FROM VE_SERVICIOS WHERE SER_NOMBRE = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, nombre);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("SER_ESTADO");
                if ("NoUsado".equals(estado)) {
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
                } else {
                    JOptionPane.showMessageDialog(null, "El servicio no se puede eliminar porque no está presente en una factura", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Servicio no encontrado", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar eliminar el servicio", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return state;
    }

    public boolean cancelarCita(int id) {
        boolean state = false;

        // Query para eliminar el servicio
        String deleteQuery = "UPDATE VE_CITAS SET CIT_ESTADO = 'Cancelado' WHERE CIT_ID = ?";

        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1,id);
            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Consulta cancelado", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                JOptionPane.showMessageDialog(null, "no se pudo cancelar", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Servicio no se pudo eliminar, modifquelo , presente en una factura", "ERROR", JOptionPane.ERROR_MESSAGE);

            // Manejar la excepción según sea necesario
        }

        return state;
    }


    public ArrayList<String> obtenerEmpleados() {
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


    public ArrayList<String> obtenerDatosparaEmail(int cabId) {
        ArrayList<String> listaServios = new ArrayList<>();

        String query = "SELECT DET_FAC_CANTIDAD, DET_FAC_PRECIO_UNITARIO, DET_FAC_SUBTOTAL, DET_FAC_IVA, DET_FAC_TOTAL " +
                "FROM VE_DETALLE_FACTURAS WHERE CAB_FAC_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Establecer el parámetro del PreparedStatement
            statement.setInt(1, cabId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Obtener los datos del ResultSet
                    int cantidad = resultSet.getInt("DET_FAC_CANTIDAD");
                    double precioUnitario = resultSet.getDouble("DET_FAC_PRECIO_UNITARIO");
                    double subtotal = resultSet.getDouble("DET_FAC_SUBTOTAL");
                    double iva = resultSet.getDouble("DET_FAC_IVA");
                    double total = resultSet.getDouble("DET_FAC_TOTAL");

                    // Crear una cadena de texto con los detalles del empleado
                    String datosServicio = String.format("Cantidad: %d, Precio Unitario: %.2f, Subtotal: %.2f, IVA: %.2f, Total: %.2f",
                            cantidad, precioUnitario, subtotal, iva, total);

                    listaServios.add(datosServicio);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejar la excepción según sea necesario
        }

        return listaServios;
    }

    public String obtenerNombreUsuario(String nombre) {
        String datosServico = null; // Initialize to store the result

        String query = "SELECT p.PER_NOMBRE || ' ' || p.PER_APELLIDO AS FULL_NAME " +
                "FROM VE_USUARIOS u " +
                "JOIN VE_EMPLEADOS e ON u.EMP_ID = e.EMP_ID " +
                "JOIN VE_PERSONAS p ON e.PER_ID = p.PER_ID " +
                "WHERE u.USU_NOMBRE = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the full name from the result set
                    datosServico = resultSet.getString("FULL_NAME");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return datosServico; // Return the full name
    }

    public String obtenerDireccion(String nombre) {
        String datosServico = null; // Initialize to store the result

        String query = "select PER_DIRECCION FROM VE_PERSONAS WHERE PER_CEDULA = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the full name from the result set
                    datosServico = resultSet.getString("PER_DIRECCION");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return datosServico; // Return the full name
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




    public ArrayList<String> obtenerFacturasListado() {
        ArrayList<String> listaFacturas = new ArrayList<>();

        try {
            String query = "SELECT \n" +
                    "    f.CAB_FAC_NUMERO AS \"Número de Factura\",\n" +
                    "    f.CAB_FAC_VALOR_TOTAL AS \"Precio Total\",\n" +
                    "    f.CAB_FAC_FECHA AS \"Fecha\",\n" +
                    "    p.PER_NOMBRE || ' ' || p.PER_APELLIDO AS \"Empleado\"\n" +
                    "FROM \n" +
                    "    VE_CABECERA_FACTURAS f\n" +
                    "JOIN \n" +
                    "    VE_PERSONAS p ON f.USU_ID = p.PER_ID\n" +
                    "WHERE \n" +
                    "    p.PER_ID = f.USU_ID";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Retrieve data from the result set
                    String numeroFactura = resultSet.getString("Número de Factura");
                    String precioTotal = resultSet.getString("Precio Total");
                    String fecha = resultSet.getString("Fecha");
                    String empleado = resultSet.getString("Empleado");

                    // Create a string representation of the invoice details
                    String datosFactura = "Número de Factura: " + numeroFactura +
                            ", Precio Total: " + precioTotal +
                            ", Fecha: " + fecha +
                            ", Empleado: " + empleado;

                    // Add the string representation to the list
                    listaFacturas.add(datosFactura);
                    System.out.println(datosFactura);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return listaFacturas;
    }

    public ArrayList<String> obtenerDetallesFactura(int facturaId) throws SQLException {
        ArrayList<String> listaDetalles = new ArrayList<>();

        // Consulta SQL corregida
        String query = "SELECT " +
                "cab.CAB_FAC_ID AS Factura_ID, " +
                "ser.SER_NOMBRE AS Servicio, " +
                "det.DET_FAC_CANTIDAD AS Cantidad, " +
                "det.DET_FAC_PRECIO_UNITARIO AS Precio_Unitario, " +
                "det.DET_FAC_SUBTOTAL AS Subtotal, " +
                "det.DET_FAC_IVA AS IVA, " +
                "det.DET_FAC_TOTAL AS Total_Detalle, " +
                "det.DET_FAC_TOTAL AS Total_Factura " +
                "FROM VE_CABECERA_FACTURAS cab " +
                "JOIN VE_DETALLE_FACTURAS det ON cab.CAB_FAC_ID = det.CAB_FAC_ID " +
                "JOIN VE_SERVICIOS ser ON det.SER_ID = ser.SER_ID " +
                "WHERE cab.CAB_FAC_ID = ?"; // Usar ? para parámetros

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, facturaId); // Establecer el parámetro de la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String facturaDetails = "Factura ID: " + resultSet.getString("Factura_ID") +
                            ", Servicio: " + resultSet.getString("Servicio") +
                            ", Precio Unitario: " + resultSet.getString("Precio_Unitario") +
                            ", IVA: " + resultSet.getString("IVA") +
                            ", Cantidad: " + resultSet.getString("Cantidad") +
                            ", Subtotal: " + resultSet.getString("Subtotal") +
                            ", Total Detalle: " + resultSet.getString("Total_Detalle") +
                            ", Total Factura: " + resultSet.getString("Total_Factura");

                    listaDetalles.add(facturaDetails);
                }
            }
        }

        return listaDetalles;
    }



    public ArrayList<String> obtenerFacturas(int cabeceraID) {
        ArrayList<String> listaFacturas = new ArrayList<>();

        String query = "SELECT \n" +
                "    cab.CAB_FAC_ID AS Factura_ID,\n" +
                "    per.PER_NOMBRE AS Cliente_Nombre,\n" +
                "    per.PER_DIRECCION AS Cliente_Direccion,\n" +
                "    per.PER_CORREO_ELECTRONICO AS Cliente_Email,\n" +
                "    ser.SER_NOMBRE AS Servicio,\n" +
                "    ser.SER_PRECIO AS Precio_Unitario,\n" +
                "    ser.SER_TIENE_IVA AS IVA_Indicado,\n" +
                "    det.DET_FAC_CANTIDAD AS Cantidad,\n" +
                "    det.DET_FAC_SUBTOTAL AS Subtotal,\n" +
                "    (det.DET_FAC_CANTIDAD * ser.SER_PRECIO) AS Total_Calculado,\n" +
                "    (SELECT SUM(det2.DET_FAC_CANTIDAD * ser2.SER_PRECIO)\n" +
                "     FROM VE_DETALLE_FACTURAS det2\n" +
                "     JOIN VE_SERVICIOS ser2 ON det2.SER_ID = ser2.SER_ID\n" +
                "     WHERE det2.CAB_FAC_ID = cab.CAB_FAC_ID) AS Total_Factura\n" +
                "FROM \n" +
                "    VE_CABECERA_FACTURAS cab\n" +
                "JOIN \n" +
                "    VE_DETALLE_FACTURAS det ON cab.CAB_FAC_ID = det.CAB_FAC_ID\n" +
                "JOIN \n" +
                "    VE_SERVICIOS ser ON det.SER_ID = ser.SER_ID\n" +
                "JOIN \n" +
                "    VE_PERSONAS per ON cab.CLI_ID = per.PER_ID\n" +
                "WHERE \n" +
                "    cab.CAB_FAC_ID = ?\n" +
                "ORDER BY \n" +
                "    ser.SER_NOMBRE";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Asigna el parámetro
            statement.setInt(1, cabeceraID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Recupera datos del ResultSet
                    String facturaId = resultSet.getString("Factura_ID");
                    String clienteNombre = resultSet.getString("Cliente_Nombre");
                    String clienteDireccion = resultSet.getString("Cliente_Direccion");
                    String clienteEmail = resultSet.getString("Cliente_Email");
                    String servicio = resultSet.getString("Servicio");
                    double precioUnitario = resultSet.getDouble("Precio_Unitario");
                    String ivaIndicado = resultSet.getString("IVA_Indicado");
                    int cantidad = resultSet.getInt("Cantidad");
                    double subtotal = resultSet.getDouble("Subtotal");
                    double totalCalculado = resultSet.getDouble("Total_Calculado");
                    double totalFactura = resultSet.getDouble("Total_Factura");

                    // Crea una representación en cadena de los detalles de la factura
                    String datosFactura = String.format(
                            "Factura ID: %s, Cliente: %s, Direccion: %s, Email: %s, Servicio: %s, Precio Unitario: %.2f, IVA: %s, Cantidad: %d, Subtotal: %.2f, Total Calculado: %.2f, Total Factura: %.2f",
                            facturaId, clienteNombre, clienteDireccion, clienteEmail, servicio, precioUnitario, ivaIndicado, cantidad, subtotal, totalCalculado, totalFactura
                    );

                    // Agrega la representación en cadena a la lista
                    listaFacturas.add(datosFactura);
                    System.out.println(datosFactura);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Maneja la excepción según sea necesario
        }

        return listaFacturas;
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
                    String datosCita = citaId + "|" + nombrePerro + "|" + fechaCita;

                    // Add the string representation to the list
                    listaCitas.add(datosCita);
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



    public boolean cambiarEstado(int serId) {
        String query = "UPDATE VE_SERVICIOS SET SER_ESTADO = CASE WHEN SER_ESTADO = 'Desactivado' THEN 'Activo' ELSE 'Desactivado' END WHERE SER_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, serId); // Establecer el valor del parámetro SER_ID

            int filasAfectadas = statement.executeUpdate(); // Ejecutar la actualización
            JOptionPane.showMessageDialog(null,"El servicio ha sido cambiado de estado correctamente ","INFO",JOptionPane.INFORMATION_MESSAGE);
            return filasAfectadas > 0; // Retornar verdadero si se actualizó al menos una fila

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Error al cambiar el estado ","ERROR",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Manejo de errores
            return false;
        }
    }

    public int obtenerNumServicio() {
        int count = 0;
        try {
            String query = "SELECT COUNT(SER_ID) AS total_Servicios FROM VE_SERVICIOS";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    count = resultSet.getInt("total_Servicios");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }
        return count + 1;
    }

    public boolean cambiarEstadoServicio(int servicio) {
        String query = "UPDATE VE_SERVICIOS SET SER_ESTADO = CASE WHEN SER_ESTADO = 'Desactivado' THEN 'Activo' ELSE 'Desactivado' END WHERE SER_ID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, servicio); // Establecer el valor del parámetro SER_ID

            int filasAfectadas = stmt.executeUpdate(); // Ejecutar la actualización
            System.out.println("verfadero");
            return true;


        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
            return false;
        }
    }


    public boolean ingresarFacturaCabecera(String num, String fecha, float subtotal, float iva, float total, int cliId, int usuId) {
        boolean state = false;
        Connection conn = null;
        PreparedStatement sentencia = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##jellz", "Jjwm20020");
            conn.setAutoCommit(false);

            // SQL query para insertar en VE_CABECERA_FACTURAS
            String sqlCabecera = "INSERT INTO VE_CABECERA_FACTURAS (CAB_FAC_ID, CAB_FAC_NUMERO, CAB_FAC_FECHA, CAB_FAC_SUBTOTAL, CAB_FAC_IVA, CAB_FAC_VALOR_TOTAL, CLI_ID, USU_ID) VALUES (SEQ_VE_CABECERA_FACTURAS.NEXTVAL, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?)";
            sentencia = conn.prepareStatement(sqlCabecera);
            sentencia.setString(1, num);
            sentencia.setString(2, fecha);
            sentencia.setFloat(3, subtotal);
            sentencia.setFloat(4, iva);
            sentencia.setFloat(5, total);
            sentencia.setInt(6, cliId);
            sentencia.setInt(7, usuId);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(null, "Se ha ingresado la factura exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "ERROR: No se ha ingresado la factura", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
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



    public boolean ingresarFacturaDetalles(float cant,float precioUn, float subtotal, float iva, float total, int cabId, int serId) {
        boolean state = false;
        Connection conn = null;
        PreparedStatement sentencia = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##jellz", "Jjwm20020");
            conn.setAutoCommit(false);

            // SQL query para insertar en VE_DETALLE_FACTURAS
            String sqlCabecera ="INSERT INTO VE_DETALLE_FACTURAS (det_fac_id, det_fac_cantidad, det_fac_precio_unitario, det_fac_subtotal, det_fac_iva, det_fac_total, cab_fac_id, ser_id) VALUES (SEQ_VE_DETALLE_FACTURAS.NEXTVAL, ?,?, ?, ?,?,?,?)";
            sentencia = conn.prepareStatement(sqlCabecera);
            // Establecer parámetros correctos
            sentencia.setFloat(1, cant); // Asumiendo que la cantidad se establece como 0
            sentencia.setFloat(2, precioUn); // Asumiendo que el precio unitario se establece como 0
            sentencia.setFloat(3, subtotal);
            sentencia.setFloat(4, iva);
            sentencia.setFloat(5, total);
            sentencia.setInt(6, cabId);
            sentencia.setInt(7, serId);

            int rowsAffected = sentencia.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
               // JOptionPane.showMessageDialog(null, "Se ha ingresado la factura exitosamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
                state = true;
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "ERROR: No se ha ingresado la factura", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, "Error durante el rollback", e);
                }
            }
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, "Error durante la inserción de la factura", ex);
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, "Error al cerrar la sentencia", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, "Error al cerrar la conexión", e);
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
                    String datosCliente = "\t" + perId + "\t " + perNombre +
                            "\t " + perApellido + "\t" + perDireccion +
                            "\t " + perTelefono + "\t" + perCorreo;

                    // Add the client details to the list
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


    public String obtenerCedulaCliente(String clienteSeleccionado) {
        String cedula = null;  // Initialize the email variable to avoid potential null pointer issues
        String query = "SELECT PER_CEDULA FROM VE_PERSONAS WHERE PER_NOMBRE = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, clienteSeleccionado);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the cédula from the result set
                    cedula = resultSet.getString("PER_CEDULA");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        System.out.println(cedula);
        return cedula;
    }


    public String[] obtenerDatosClientePorCedula(String cedula) {
        // Initialize the data string
        String[] datos = new String[3]; // Array to hold name, address, and email

        // SQL query to select data based on cedula
        String query = "SELECT PER_NOMBRE, PER_DIRECCION, PER_CORREO_ELECTRONICO FROM VE_PERSONAS WHERE PER_CEDULA = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Set the parameter for the PreparedStatement
            statement.setString(1, cedula);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the data from the result set
                    datos[0] = resultSet.getString("PER_NOMBRE");
                    datos[1] = resultSet.getString("PER_DIRECCION");
                    datos[2] = resultSet.getString("PER_CORREO_ELECTRONICO");
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        // Return the data array
        return datos;
    }

}






