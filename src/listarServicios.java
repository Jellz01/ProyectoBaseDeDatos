import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class listarServicios extends JFrame implements ActionListener {

    // ATRIBUTOS PARA RADIO BUTTONS
    private JPanel panelNorte;
    private ButtonGroup tipoListado;
    private JRadioButton inicioFin;
    private JRadioButton finInicio;

    // ATRIBUTOS PARA LA TABLA
    private final String[] nombresColumnas = {"ID", "Codigo", "Nombre", "Precio", "IVA", "Estado"};
    private JTable tabla;
    private JScrollPane scrollDatos;

    private Operaciones op;

    listarServicios(Operaciones op) throws SQLException {
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

        // TABLA CON SERVICIOS
        ArrayList<String> listaServicios = op.obtenerTodosLosServicios();

        Object[][] datos = new Object[listaServicios.size()][nombresColumnas.length];

        for (int i = 0; i < listaServicios.size(); i++) {
            String[] datosservicio = listaServicios.get(i).split(", ");
            for (int j = 0; j < datosservicio.length; j++) {
                datos[i][j] = datosservicio[j].split(": ")[1]; // Extract the value after ": "
            }
        }

        TableModel modelo = new DefaultTableModel(datos, nombresColumnas);
        tabla = new JTable(modelo);
        TableRowSorter<TableModel> ordenamiento = new TableRowSorter<>(modelo);
        tabla.setRowSorter(ordenamiento);
        scrollDatos = new JScrollPane(tabla);
        this.add(scrollDatos, BorderLayout.CENTER);

        // CONFIGURACIONES GENERALES DEL FORMULARIO
        super.setTitle("Listado de Servicios");
        super.setSize(800, 400);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inicioFin) {
            System.out.println("Pulso primer radio button");
        }
        if (e.getSource() == finInicio) {
            System.out.println("Pulso segundo radio button");
        }
    }
}
