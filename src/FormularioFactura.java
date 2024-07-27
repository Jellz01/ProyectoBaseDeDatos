import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class FormularioFactura extends JFrame implements ActionListener {

    // Variables para la interfaz
    private JTextField txtNum, txtFecha, txtCliente, txtDireccion, txtEmail, txtUsuario, txtCedula, txtSubT, txtIva, txtTotal;
    private JButton btnSelectClient, botGrabar, botCancelar;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private Operaciones op;
    private String usuarioU;
    private String servicio;
    private int cantidadServiciol;
    private float precioUnitario;
    private String cliente;
    private float subtotal;
    private float iva;
    private float total;

    public FormularioFactura(Operaciones op, String usuarioU) {
        this.usuarioU = usuarioU;
        this.op = op;
        servicio = "cacaguate";
        cantidadServiciol = 0;
        precioUnitario = 0.00F;
        cliente = "caca";
        op.conectar();
        int cantidad = 0;
        subtotal = 0.00F;
        super.setLayout(null);

        // Add logo to top right corner
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\JWell\\Downloads\\logoV (1).png"); // Adjust the path as necessary
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(480, 5, 300, 100); // Adjust size and position as necessary
        this.add(logoLabel);

        JLabel labNum = new JLabel("Factura N°:");
        labNum.setBounds(10, 5, 90, 25);
        this.add(labNum);

        txtNum = new JTextField();
        txtNum.setBounds(100, 5, 80, 25);
        txtNum.setEnabled(false);
        int numFactura = op.obtenerNumFactura();
        txtNum.setText(String.valueOf(numFactura));
        this.add(txtNum);

        JLabel labFecha = new JLabel("Fecha:");
        labFecha.setBounds(10, 35, 90, 25);
        this.add(labFecha);
        LocalDate currentDate = LocalDate.now();
        txtFecha = new JTextField();
        txtFecha.setText(String.valueOf(currentDate));
        txtFecha.setEnabled(false);
        txtFecha.setBounds(100, 35, 150, 25);
        this.add(txtFecha);

        JLabel labCliente = new JLabel("Cliente:");
        labCliente.setBounds(10, 65, 90, 25);
        this.add(labCliente);

        txtCliente = new JTextField();
        txtCliente.setBounds(100, 65, 200, 25);
        txtCliente.setEnabled(false);
        this.add(txtCliente);

        btnSelectClient = new JButton("Seleccionar Cliente");
        btnSelectClient.setBounds(310, 65, 150, 25);
        btnSelectClient.addActionListener(e -> showClientDialog());
        this.add(btnSelectClient);

        // Add Cedula label and text field
        JLabel labCedula = new JLabel("Cédula:");
        labCedula.setBounds(10, 95, 90, 25);
        this.add(labCedula);

        txtCedula = new JTextField();
        txtCedula.setBounds(100, 95, 200, 25);
        txtCedula.setEnabled(false);
        this.add(txtCedula);

        JLabel labDireccion = new JLabel("Dirección:");
        labDireccion.setBounds(10, 125, 90, 25);
        this.add(labDireccion);
        txtDireccion = new JTextField();
        txtDireccion.setBounds(100, 125, 200, 25);
        txtDireccion.setEnabled(false);
        this.add(txtDireccion);

        // New JLabel and JTextField for Usuario
        JLabel labUsuario = new JLabel("Usuario:");
        labUsuario.setBounds(10, 155, 90, 25);
        this.add(labUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(100, 155, 200, 25);
        String usuario = op.obtenerNombreUsuario(usuarioU);
        txtUsuario.setText(usuario);
        txtUsuario.setEnabled(false);
        this.add(txtUsuario);

        JLabel labEmail = new JLabel("Email:");
        labEmail.setBounds(10, 185, 90, 25);
        this.add(labEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(100, 185, 200, 25);
        String email = op.obtenerEmailCliente(cliente);
        txtEmail.setText(email);
        txtEmail.setEnabled(false);
        this.add(txtEmail);

        JLabel labProductos = new JLabel("Producto:");
        labProductos.setBounds(10, 215, 90, 25);
        this.add(labProductos);

        productTable = new JTable();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Servicio");
        tableModel.addColumn("Cantidad");
        tableModel.addColumn("Precio Unitario");
        tableModel.addColumn("Total");

        productTable.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBounds(100, 215, 600, 150);
        this.add(scrollPane);

        JButton btnAddProduct = new JButton("Agregar Producto");
        btnAddProduct.setBounds(100, 375, 150, 25);
        btnAddProduct.addActionListener(e -> showServiceDialog());
        this.add(btnAddProduct);

        JLabel labSubT = new JLabel("Subtotal:");
        labSubT.setBounds(420, 405, 90, 25);
        this.add(labSubT);
        txtSubT = new JTextField();
        txtSubT.setBounds(505, 405, 200, 25);
        txtSubT.setText("0");
        txtSubT.setEditable(false);
        this.add(txtSubT);

        JLabel labIva = new JLabel("IVA 15%:");
        labIva.setBounds(420, 435, 90, 25);
        this.add(labIva);
        txtIva = new JTextField();
        txtIva.setEditable(false);
        txtIva.setBounds(505, 435, 200, 25);
        this.add(txtIva);

        JLabel labTotal = new JLabel("Total:");
        labTotal.setBounds(460, 465, 90, 25);
        this.add(labTotal);
        txtTotal = new JTextField();
        txtTotal.setSize(200, 25);
        txtTotal.setLocation(505, 465);
        txtTotal.setEnabled(false);
        this.add(txtTotal);

        botGrabar = new JButton("Registrar");
        botGrabar.setBounds(60, 495, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Salir");
        botCancelar.setBounds(180, 495, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);

        super.setTitle("Facturación");
        super.setSize(800, 570);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    private void showClientDialog() {
        JDialog clientDialog = new JDialog(this, "Seleccionar Cliente", true);
        clientDialog.setSize(500, 400);
        clientDialog.setLocationRelativeTo(this);
        clientDialog.setLayout(null);

        JTable clientTable = new JTable();
        DefaultTableModel clientTableModel = new DefaultTableModel();
        clientTableModel.addColumn("Cliente");

        clientTable.setModel(clientTableModel);
        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientScrollPane.setBounds(10, 10, 460, 300);
        clientDialog.add(clientScrollPane);

        // Fill client table with data from the database
        ArrayList<String> listaClientes = op.obtenerClientes();
        for (String cliente : listaClientes) {
            clientTableModel.addRow(new Object[]{cliente});
        }

        JButton selectButton = new JButton("Seleccionar");
        selectButton.setBounds(210, 320, 100, 25);
        selectButton.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow != -1) {
                String clienteSeleccionado = (String) clientTableModel.getValueAt(selectedRow, 0);
                txtCliente.setText(clienteSeleccionado);
                String cedula = op.obtenerCedulaCliente(clienteSeleccionado); // Obtain Cedula

                String direccion = op.obtenerDireccion(cedula);
                if (direccion == null || direccion.trim().isEmpty()) {
                    direccion = "Dirección no disponible";
                }

                String email = op.obtenerEmailCliente(clienteSeleccionado);


                txtDireccion.setText(direccion);
                txtEmail.setText(email);
                txtCedula.setText(cedula); // Set Cedula

                clientDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente.");
            }
        });
        clientDialog.add(selectButton);

        clientDialog.setVisible(true);
    }
    private void showServiceDialog() {
        JDialog serviceDialog = new JDialog(this, "Seleccionar Servicio", true);
        serviceDialog.setSize(500, 400);
        serviceDialog.setLocationRelativeTo(this);
        serviceDialog.setLayout(null);

        JTable serviceTable = new JTable();
        DefaultTableModel serviceTableModel = new DefaultTableModel();
        serviceTableModel.addColumn("Servicio");

        serviceTable.setModel(serviceTableModel);
        JScrollPane serviceScrollPane = new JScrollPane(serviceTable);
        serviceScrollPane.setBounds(10, 10, 460, 300);
        serviceDialog.add(serviceScrollPane);

        // Fill service table with data from the database
        ArrayList<String> listaServicios = op.obtenerServicios();
        for (String servicio : listaServicios) {
            serviceTableModel.addRow(new Object[]{servicio});
        }

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setBounds(10, 320, 80, 25);
        serviceDialog.add(lblCantidad);

        JTextField txtCantidad = new JTextField();
        txtCantidad.setBounds(100, 320, 100, 25);
        serviceDialog.add(txtCantidad);

        JButton selectButton = new JButton("Seleccionar");
        selectButton.setBounds(210, 320, 100, 25);
        selectButton.addActionListener(e -> {
            int selectedRow = serviceTable.getSelectedRow();
            if (selectedRow != -1) {
                servicio = (String) serviceTableModel.getValueAt(selectedRow, 0);
                String cantidadStr = txtCantidad.getText();

                try {
                    int cantidad = parseInt(cantidadStr);

                    precioUnitario = op.obtenerPrecioUnitario(servicio);
                    float importe = cantidad * precioUnitario;

                    // Check if the service is already in the table
                    boolean found = false;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String existingServicio = (String) tableModel.getValueAt(i, 0);
                        if (existingServicio.equals(servicio)) {
                            int existingCantidad = (int) tableModel.getValueAt(i, 1);
                            float existingImporte = (float) tableModel.getValueAt(i, 3);

                            // Update the existing row with the new quantity and updated importe
                            tableModel.setValueAt(existingCantidad + cantidad, i, 1);
                            tableModel.setValueAt(precioUnitario, i, 2);
                            tableModel.setValueAt((existingCantidad + cantidad) * precioUnitario, i, 3);
                            cantidadServiciol = existingCantidad + cantidad;
                            found = true;
                            break;
                        }
                    }

                    // If the service was not found, add a new row
                    if (!found) {
                        tableModel.addRow(new Object[]{servicio, cantidad, precioUnitario, importe});
                    }

                    updateSubtotalAndTotal();
                    serviceDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un servicio.");
            }
        });
        serviceDialog.add(selectButton);

        serviceDialog.setVisible(true);
    }

    public void updateSubtotalAndTotal() {
        float newSubtotal = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            newSubtotal += (float) tableModel.getValueAt(i, 3);
        }
        subtotal = newSubtotal;
        txtSubT.setText(String.format("%.2f", subtotal));
        updateTotal();
    }

    private void updateTotal() {
        try {
            float iva = subtotal * 0.15f;
            float total = subtotal + iva;
            txtIva.setText(String.format("%.2f", iva));
            txtTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            txtSubT.setText("0");
            txtIva.setText("0");
            txtTotal.setText("0");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botGrabar) {
            String email = op.obtenerEmailCliente(cliente);
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            String cliente = txtCliente.getText();
            String num = txtNum.getText();
            int numFactura = parseInt(num);
            String fecha = txtFecha.getText();
            String direccion = txtDireccion.getText();
            String subt = txtSubT.getText();
            float subtt = Float.parseFloat(subt);
            String iva = txtIva.getText();
            float ivaF = Float.parseFloat(iva);
            String totalF = txtTotal.getText();
            float totalFF = Float.parseFloat(totalF);
            int cliId = op.obtenerIDCliente(cliente);
            int usuarioId = op.obtenerIDUsuario(usuarioU);

            boolean estadoCabecera = op.ingresarFacturaCabecera(num, fecha, subtt, ivaF, totalFF, cliId, usuarioId);

            // Iterate through the rows in the table and insert details for each service
            boolean estadoDetalles = true;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String servicio = (String) tableModel.getValueAt(i, 0);
                int cantidad = (int) tableModel.getValueAt(i, 1);
                float precioU = (float) tableModel.getValueAt(i, 2);
                float total = (float) tableModel.getValueAt(i, 3);

                int idServicio = op.obtenerIDServicio(servicio);
                System.out.println(idServicio);


                boolean estadoDetalle = op.ingresarFacturaDetalles(cantidad, precioU, total, ivaF, totalFF, numFactura, idServicio);

                if (!estadoDetalle) {
                    estadoDetalles = false;
                    break; // Stop processing if any detail insertion fails
                }
            }

            if (estadoCabecera && estadoDetalles) {
                JOptionPane.showMessageDialog(this, "Factura registrada exitosamente.");
                ArrayList<String> contenidos = op.obtenerFacturas(numFactura);

                SendEmail se = new SendEmail(properties, email, contenidos);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la factura.");
            }
        } else if (e.getSource() == botCancelar) {
            this.setVisible(false);
        }
    }

    public String obtenerDatosparaEmail(int cabId) {
        StringBuilder emailContent = new StringBuilder();
        double grandTotal = 0.0;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##jellz", "Jjwm20020");
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String query = "SELECT S.SER_NOMBRE, D.DET_FAC_CANTIDAD, D.DET_FAC_PRECIO_UNITARIO, D.DET_FAC_SUBTOTAL, D.DET_FAC_IVA, D.DET_FAC_TOTAL " +
                "FROM VE_DETALLE_FACTURAS D " +
                "JOIN VE_SERVICIOS S ON D.SER_ID = S.SER_ID " +
                "WHERE D.CAB_FAC_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, cabId);

            try (ResultSet resultSet = statement.executeQuery()) {
                emailContent.append("<html>" +
                        "<head><style>" +
                        "table { border-collapse: collapse; width: 100%; margin-top: 20px; }" +
                        "th, td { border: 1px solid #dddddd; padding: 8px; text-align: left; }" +
                        "th { background-color: #f2f2f2; }" +
                        "tr:nth-child(even) { background-color: #f9f9f9; }" +
                        "h2 { color: #333; }" +
                        "p { font-family: Arial, sans-serif; color: #555; }" +
                        "</style></head>" +
                        "<body>" +
                        "<h2>Factura</h2>" +
                        "<p>Saludos,</p>" +
                        "<p>A continuación, se presentan los detalles de la factura:</p>" +
                        "<table>" +
                        "<thead>" +
                        "<tr><th>Servicio</th><th>Cantidad</th><th>Precio Unitario</th><th>Subtotal</th><th>IVA</th><th>Total</th></tr>" +
                        "</thead>" +
                        "<tbody>");

                while (resultSet.next()) {
                    String servicioNombre = resultSet.getString("SER_NOMBRE");
                    int cantidad = resultSet.getInt("DET_FAC_CANTIDAD");
                    double precioUnitario = resultSet.getDouble("DET_FAC_PRECIO_UNITARIO");
                    double subtotal = resultSet.getDouble("DET_FAC_SUBTOTAL");
                    double iva = resultSet.getDouble("DET_FAC_IVA");
                    double total = resultSet.getDouble("DET_FAC_TOTAL");

                    grandTotal += total;

                    emailContent.append(String.format("<tr>" +
                            "<td>%s</td>" +
                            "<td>%d</td>" +
                            "<td>%.2f</td>" +
                            "<td>%.2f</td>" +
                            "<td>%.2f</td>" +
                            "<td>%.2f</td>" +
                            "</tr>", servicioNombre, cantidad, precioUnitario, subtotal, iva, total));
                }

                emailContent.append("</tbody>" +
                        "<tfoot>" +
                        "<tr><td colspan='5'><strong>Total General:</strong></td><td><strong>%.2f</strong></td></tr>" +
                        "</tfoot>" +
                        "</table>" +
                        "<p>Gracias por su tiempo.</p>" +
                        "</body>" +
                        "</html>");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return emailContent.toString();
    }
}