import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListarFactura extends JFrame implements ActionListener {

    // ATRIBUTOS PARA RADIO BUTTONS
    private JPanel panelNorte;
    private ButtonGroup tipoListado;
    private JRadioButton inicioFin;
    private JRadioButton finInicio;

    // ATRIBUTOS PARA LAS TABLAS
    private final String[] nombresColumnasFacturas = {"Id Factura", "Nombre Cliente", "Fecha"};
    private final String[] nombresColumnasDetalles = {"Servicio", "Precio Unitario", "IVA", "Cantidad", "Subtotal"};
    private JTable tablaFacturas;
    private JTable tablaDetalles;
    private JScrollPane scrollFacturas;
    private JScrollPane scrollDetalles;

    private Operaciones op;

    ListarFactura(Operaciones op) throws SQLException {
        this.op = op;

        op.conectar();
        super.setLayout(new BorderLayout());

        // RADIO BUTTON PARTE SUPERIOR
        panelNorte = new JPanel();
        tipoListado = new ButtonGroup();
        inicioFin = new JRadioButton("De inicio a fin");
        inicioFin.addActionListener(this);
        finInicio = new JRadioButton("De fin a inicio");
        finInicio.addActionListener(this);
        tipoListado.add(inicioFin);
        tipoListado.add(finInicio);
        panelNorte.add(inicioFin);
        panelNorte.add(finInicio);
        this.add(panelNorte, BorderLayout.NORTH);

        // TABLA CON FACTURAS
        ArrayList<String> facturaList = op.obtenerFacturas(9); // Handle the exception as needed

        Object[][] datosFacturas = new Object[facturaList.size()][3]; // Ajustado a 3 columnas

        for (int i = 0; i < facturaList.size(); i++) {
            String[] facturaDetails = facturaList.get(i).split(", ");
            datosFacturas[i][0] = facturaDetails[0].split(": ")[1]; // ID Factura
            datosFacturas[i][1] = facturaDetails[1].split(": ")[1]; // Nombre Cliente
            datosFacturas[i][2] = facturaDetails[2].split(": ")[1]; // Fecha
        }

        TableModel modeloFacturas = new DefaultTableModel(datosFacturas, nombresColumnasFacturas); // MODELO
        tablaFacturas = new JTable(modeloFacturas); // TABLA
        tablaFacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFacturas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablaFacturas.getSelectedRow();
                if (selectedRow >= 0) {
                    String idFacturaSeleccionada = (String) tablaFacturas.getValueAt(selectedRow, 0);
                    mostrarDetallesFactura(idFacturaSeleccionada);
                }
            }
        });
        TableRowSorter<TableModel> ordenamientoFacturas = new TableRowSorter<>(modeloFacturas); // ORDENAMIENTO
        tablaFacturas.setRowSorter(ordenamientoFacturas);
        scrollFacturas = new JScrollPane(tablaFacturas); // PANEL CON SCROLL
        this.add(scrollFacturas, BorderLayout.WEST);

        // TABLA CON DETALLES DE FACTURA (VACIA INICIALMENTE)
        Object[][] datosDetalles = new Object[0][5]; // Inicialmente vacío

        TableModel modeloDetalles = new DefaultTableModel(datosDetalles, nombresColumnasDetalles); // MODELO
        tablaDetalles = new JTable(modeloDetalles); // TABLA
        TableRowSorter<TableModel> ordenamientoDetalles = new TableRowSorter<>(modeloDetalles); // ORDENAMIENTO
        tablaDetalles.setRowSorter(ordenamientoDetalles);
        scrollDetalles = new JScrollPane(tablaDetalles); // PANEL CON SCROLL
        this.add(scrollDetalles, BorderLayout.CENTER);

        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Facturas"); // TITULO VENTANA
        super.setSize(1200, 600); // TAMAÑO
        super.setLocationRelativeTo(null); // CENTRAR
        super.setResizable(true); // PERMITIR CAMBIAR TAMAÑO
        super.setVisible(true); // VISIBILIZA
    }

    private void mostrarDetallesFactura(String idFactura) {
        try {
            ArrayList<String> detalleList = op.obtenerFacturas(Integer.parseInt(idFactura));
            Object[][] datosDetalles = new Object[detalleList.size()][5];

            for (int i = 0; i < detalleList.size(); i++) {
                String detalle = detalleList.get(i);
                String[] detalles = detalle.split(", ");

                // Split each detail based on the expected format
                String servicio = detalles[4].split(": ")[1];
                String precioUnitario = detalles[5].split(": ")[1];
                String iva = detalles[6].split(": ")[1];
                String cantidad = detalles[7].split(": ")[1];
                String subtotal = detalles[8].split(": ")[1];

                datosDetalles[i][0] = servicio;
                datosDetalles[i][1] = precioUnitario;
                datosDetalles[i][2] = iva;
                datosDetalles[i][3] = cantidad;
                datosDetalles[i][4] = subtotal;
            }

            TableModel modeloDetalles = new DefaultTableModel(datosDetalles, nombresColumnasDetalles);
            tablaDetalles.setModel(modeloDetalles);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalles de la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inicioFin) {
            System.out.println("Pulso primer radio button");
            // Implement sorting or other functionality here
        }
        if (e.getSource() == finInicio) {
            System.out.println("Pulso segundo radio button");
            // Implement sorting or other functionality here
        }
    }


}
