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
    private final String nombresColumnas [] = {"ID", "Nombre", "Apellido", "Direccion", "Teléfono","Correo"};
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


        // TABLA CON EMPLEADOS
        ArrayList<String> empleadoList = op.obtenerTodosLosClientes();


        Object[][] datos = new Object[empleadoList.size()][6]; // Adjust the size based on the number of columns


        for (int i = 0; i < empleadoList.size(); i++) {
            String[] empleadoDetails = empleadoList.get(i).split(", ");
            datos[i][0] = empleadoDetails[0].split(": ")[1];
            datos[i][1] = empleadoDetails[1].split(": ")[1];
            datos[i][2] = empleadoDetails[2].split(": ")[1];
            datos[i][3] = empleadoDetails[3].split(": ")[1];
            datos[i][4] = empleadoDetails[4].split(": ")[1];




        }
        TableModel modelo = new DefaultTableModel(datos, nombresColumnas); // MODELO
        tabla = new JTable(modelo); // TABLA
        TableRowSorter<TableModel> ordenamiento = new TableRowSorter<>(modelo); // ORDENAMIENTO
        tabla.setRowSorter(ordenamiento);
        scrollDatos = new JScrollPane(tabla); // PANEL CON SCROLL
        this.add(scrollDatos, BorderLayout.CENTER);


        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Empleados"); // TITULO VENTANA
        super.setSize(800, 400); // TAMAÑO "MINIMIZADO"
        super.setLocationRelativeTo(null); // CENTRAR
        super.setResizable(false); // EVITA QUE SE PUEDA MAXIMIZAR
        super.setVisible(true); // VISIBILIZA
    }


    private ArrayList<String> obtenerListaEmpleados() throws SQLException {
        // Llama a tu método para obtener la lista de empleados de la base de datos
        // Por ejemplo: return Operaciones.obtenerListaEmpleados();
        return new ArrayList<>();
    }




    @Override
    public void actionPerformed(ActionEvent e) {

    }
}




