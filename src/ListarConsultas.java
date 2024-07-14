import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListarConsultas extends JFrame implements ActionListener {

    // ATRIBUTOS PARA RADIO BUTTONS
    private JPanel panelNorte;
    private ButtonGroup tipoListado;
    private JRadioButton inicioFin;
    private JRadioButton finInicio;

    // ATRIBUTOS PARA LA TABLA
    private final String[] nombresColumnas = {"ID", "Nombre Mascota", "Fecha-Hora", "Estado", "Cliente ID", "Empleado ID", "Mascota ID"};
    private JTable tabla;
    private JScrollPane scrollDatos;

    private Operaciones op;
    private ArrayList<String> listaContratos; // Asumiendo que necesitas esta lista para los contratos

    public ListarConsultas(Operaciones op) throws SQLException {
        this.op = op;
        this.listaContratos = listaContratos;
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

        // Obtener los datos de las citas
        ArrayList<String> citaList = op.obtenerTodosLasCitas();
        Object[][] datos = new Object[citaList.size()][7]; // Ajustar el tamaño según el número de columnas

        for (int i = 0; i < citaList.size(); i++) {
            String[] citaDetails = citaList.get(i).split(", ");
            if (citaDetails.length >= 7) { // Asegurarse de que hay suficientes detalles
                datos[i][0] = citaDetails[0].split(": ")[1]; // ID
                datos[i][1] = citaDetails[1].split(": ")[1]; // Nombre
                datos[i][2] = citaDetails[2].split(": ")[1]; // Fecha-Hora
                datos[i][3] = citaDetails[3].split(": ")[1]; // Estado
                datos[i][4] = citaDetails[4].split(": ")[1]; // Cliente ID
                datos[i][5] = citaDetails[5].split(": ")[1]; // Empleado ID
                datos[i][6] = citaDetails[6].split(": ")[1]; // Mascota ID

                // Verificar y asignar tipo de contrato
                String contrato = citaDetails[5].split(": ")[1];
                if (Integer.parseInt(contrato) >= 1 && Integer.parseInt(contrato) <= listaContratos.size()) {
                    datos[i][5] = listaContratos.get(Integer.parseInt(contrato) - 1);
                }
            }
        }

        // Crear el modelo de la tabla y la tabla
        TableModel modelo = new DefaultTableModel(datos, nombresColumnas);
        tabla = new JTable(modelo);
        TableRowSorter<TableModel> ordenamiento = new TableRowSorter<>(modelo);
        tabla.setRowSorter(ordenamiento);
        scrollDatos = new JScrollPane(tabla);
        this.add(scrollDatos, BorderLayout.CENTER);

        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Consultas");
        super.setSize(800, 400);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inicioFin) {
            System.out.println("Pulso primer radio button");
        }
        if (e.getSource() == finInicio) {
            System.out.println("Pulso segundo radio button");
        }
    }
}
