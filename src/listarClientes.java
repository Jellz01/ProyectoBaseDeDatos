import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class listarClientes extends JFrame implements ActionListener {

    // ATRIBUTOS PARA RADIO BUTTONS
    private JPanel panelNorte;
    private ButtonGroup tipoListado;
    private JRadioButton inicioFin;
    private JRadioButton finInicio;

    // ATRIBUTOS PARA LA TABLA
    private final String nombresColumnas[] = {"ID", "Nombre", "Apellido", "Direccion", "Teléfono", "Correo"};
    private JTable tabla;
    private JScrollPane scrollDatos;

    private Operaciones op;

    listarClientes(Operaciones op) throws SQLException {
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

        // TABLA CON CLIENTES
        ArrayList<String> clienteList = op.obtenerTodosLosClientes();

        Object[][] datos = new Object[clienteList.size()][6]; // Adjust the size based on the number of columns

        for (int i = 0; i < clienteList.size(); i++) {
            String clienteData = clienteList.get(i);
            // Assuming clienteData is a comma-separated string
            String[] clienteDetails = clienteData.split(", ");
            datos[i][0] = clienteDetails[0]; // ID
            datos[i][1] = clienteDetails[1]; // Nombre
            datos[i][2] = clienteDetails[2]; // Apellido
            datos[i][3] = clienteDetails[3]; // Direccion
            datos[i][4] = clienteDetails[4]; // Telefono
            datos[i][5] = clienteDetails[5]; // Correo
        }

        TableModel modelo = new DefaultTableModel(datos, nombresColumnas); // MODELO
        tabla = new JTable(modelo); // TABLA
        TableRowSorter<TableModel> ordenamiento = new TableRowSorter<>(modelo); // ORDENAMIENTO
        tabla.setRowSorter(ordenamiento);
        scrollDatos = new JScrollPane(tabla); // PANEL CON SCROLL
        this.add(scrollDatos, BorderLayout.CENTER);

        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Clientes"); // TITULO VENTANA
        super.setSize(800, 400); // TAMAÑO "MINIMIZADO"
        super.setLocationRelativeTo(null); // CENTRAR
        super.setResizable(false); // EVITA QUE SE PUEDA MAXIMIZAR
        super.setVisible(true); // VISIBILIZA
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Implementar el código para manejar los eventos de los radio buttons si es necesario
    }
}
