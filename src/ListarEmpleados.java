import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListarEmpleados extends JFrame implements ActionListener {

    // ATRIBUTOS PARA RADIO BUTTONS
    private JPanel panelNorte;
    private ButtonGroup tipoListado;
    private JRadioButton inicioFin;
    private JRadioButton finInicio;

    // ATRIBUTOS PARA LA TABLA
    private final String[] nombresColumnas = {"Id", "Nombre", "Apellido", "Cedula", "Direccion"};
    private JTable tabla;
    private JScrollPane scrollDatos;

    private Operaciones op;

    ListarEmpleados(Operaciones op) throws SQLException {
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

        // TABLA CON EMPLEADOS
        ArrayList<String> empleadoList = op.obtenerTodosLosEmpleados(); // Handle the exception as needed

        Object[][] datos = new Object[empleadoList.size()][5]; // Adjusted to 5 columns

        for (int i = 0; i < empleadoList.size(); i++) {
            String[] empleadoDetails = empleadoList.get(i).split(", ");
            System.out.println("Processing row: " + empleadoList.get(i)); // Debug print

            for (String detail : empleadoDetails) {
                System.out.println("Detail: " + detail); // Debug print
            }

            datos[i][0] = empleadoDetails[0].split(": ")[1]; // ID
            datos[i][1] = empleadoDetails[1].split(": ")[1]; // Nombre
            datos[i][2] = empleadoDetails[2].split(": ")[1]; // Apellido
            datos[i][3] = empleadoDetails[3].split(": ")[1]; // Cedula
            datos[i][4] = empleadoDetails[4].split(": ")[1]; // Direccion
        }

        TableModel modelo = new DefaultTableModel(datos, nombresColumnas); // MODELO
        tabla = new JTable(modelo); // TABLA
        TableRowSorter<TableModel> ordenamiento = new TableRowSorter<>(modelo); // ORDENAMIENTO
        tabla.setRowSorter(ordenamiento);
        scrollDatos = new JScrollPane(tabla); // PANEL CON SCROLL
        this.add(scrollDatos, BorderLayout.CENTER);

        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Empleados"); // TITULO VENTANA
        super.setSize(1000, 400); // TAMAÑO "MINIMIZADO"
        super.setLocationRelativeTo(null); // CENTRAR
        super.setResizable(false); // EVITA QUE SE PUEDA MAXIMIZAR
        super.setVisible(true); // VISIBILIZA
    }

    private ArrayList<String> obtenerListaEmpleados() throws SQLException {
        // Llama a tu método para obtener la lista de empleados de la base de datos
        return op.obtenerTodosLosEmpleados();
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
