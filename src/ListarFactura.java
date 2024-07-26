import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListarFactura extends JFrame implements ActionListener {

    // ATRIBUTOS PARA LAS TABLAS
    private DefaultTableModel tableModelFacturas;
    private JTable tablaFacturas;
    private JScrollPane scrollFacturas;
    private DefaultTableModel tableModelDetalles;
    private JTable tablaDetalles;
    private JScrollPane scrollDetalles;
    private JButton verDetalles;
    private JButton cancelar;

    private Operaciones op;

    public ListarFactura(Operaciones op) throws SQLException {
        this.op = op;
        op.conectar();
        super.setLayout(null);

        // Inicializar el modelo de la tabla y la JTable para facturas
        tableModelFacturas = new DefaultTableModel(
                new String[]{"Número de Factura", "Precio Total", "Fecha", "Empleado"},
                0);
        tablaFacturas = new JTable(tableModelFacturas);
        scrollFacturas = new JScrollPane(tablaFacturas);
        scrollFacturas.setSize(900, 150);
        scrollFacturas.setLocation(20, 40);
        this.add(scrollFacturas);

        // Inicializar el modelo de la tabla y la JTable para detalles de factura
        tableModelDetalles = new DefaultTableModel(
                new String[]{"Factura ID", "Servicio", "Precio Unitario", "IVA", "Cantidad",
                        "Subtotal", "Total Detalle", "Total Factura"},
                0);
        tablaDetalles = new JTable(tableModelDetalles);
        scrollDetalles = new JScrollPane(tablaDetalles);
        scrollDetalles.setSize(900, 150);
        scrollDetalles.setLocation(20, 200);
        this.add(scrollDetalles);

        // Cargar datos en la tabla de facturas
        ArrayList<String> facturaList = op.obtenerFacturasListado();
        for (String factura : facturaList) {
            String[] facturaDetails = factura.split(", ");
            if (facturaDetails.length >= 4) {
                tableModelFacturas.addRow(new Object[]{
                        facturaDetails[0].split(": ")[1], // Número de Factura
                        facturaDetails[1].split(": ")[1], // Precio Total
                        facturaDetails[2].split(": ")[1], // Fecha
                        facturaDetails[3].split(": ")[1]  // Empleado
                });
            }
        }

        // Botón para ver los detalles de la factura seleccionada
        verDetalles = new JButton("Ver Detalles");
        verDetalles.setSize(150, 25);
        verDetalles.setLocation(40, 370);
        verDetalles.addActionListener(this);
        this.add(verDetalles);

        // Botón para cancelar la selección y cerrar la ventana
        cancelar = new JButton("Cancelar");
        cancelar.setSize(120, 25);
        cancelar.setLocation(200, 370);
        cancelar.addActionListener(this);
        this.add(cancelar);

        this.setTitle("Listado de Facturas");
        this.setSize(950, 470);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == verDetalles) {
            int selectedRow = tablaFacturas.getSelectedRow();
            if (selectedRow >= 0) {
                // Obtener el ID de la factura seleccionada
                String numeroFactura = (String) tableModelFacturas.getValueAt(selectedRow, 0);
                System.out.println("Factura ID seleccionada: " + numeroFactura);

                // Limpiar la tabla de detalles
                tableModelDetalles.setRowCount(0);

                // Obtener los detalles de la factura seleccionada
                ArrayList<String> detallesFacturaList = null;
                try {
                    detallesFacturaList = op.obtenerDetallesFactura(Integer.parseInt(numeroFactura));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al obtener detalles de la factura.");
                    return;
                }

                for (String detalle : detallesFacturaList) {
                    System.out.println("Detalle: " + detalle); // Para depuración
                    String[] detalleDetails = detalle.split(", ");
                    if (detalleDetails.length >= 7) {
                        tableModelDetalles.addRow(new Object[]{
                                detalleDetails[0].split(": ")[1], // Factura ID
                                detalleDetails[1].split(": ")[1], // Servicio
                                detalleDetails[2].split(": ")[1], // Precio Unitario
                                detalleDetails[3].split(": ")[1], // IVA
                                detalleDetails[4].split(": ")[1], // Cantidad
                                detalleDetails[5].split(": ")[1], // Subtotal
                                detalleDetails[6].split(": ")[1], // Total Detalle
                                detalleDetails[7].split(": ")[1]  // Total Factura
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura para ver detalles.");
            }
        } else if (e.getSource() == cancelar) {
            this.setVisible(false);
        }
    }
}
