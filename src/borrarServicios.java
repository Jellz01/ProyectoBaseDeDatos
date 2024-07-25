import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class borrarServicios extends JFrame implements ActionListener {

    private JTable serviciosTable;
    private JButton cancelar;
    private JButton borrar;
    private JLabel labServicios;

    private Operaciones op;
    private DefaultTableModel tableModel;

    public borrarServicios(Operaciones op) {
        this.op = op;
        op.conectar();

        this.setSize(600, 400);
        this.setLayout(null);

        this.setTitle("Borrar Servicios");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new String[]{"Servicio"}, 0);
        serviciosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(serviciosTable);
        scrollPane.setSize(500, 200);
        scrollPane.setLocation(20, 40);
        this.add(scrollPane);

        // Load data into table
        ArrayList<String> listaServicios = op.obtenerTodsLosServicios();
        for (String servicio : listaServicios) {
            tableModel.addRow(new Object[]{servicio});
        }

        labServicios = new JLabel("Servicios:");
        labServicios.setSize(100, 25);
        labServicios.setLocation(20, 10);
        this.add(labServicios);

        cancelar = new JButton("Cancelar");
        cancelar.setSize(120, 25);
        cancelar.setLocation(40, 250);
        cancelar.addActionListener(this);
        this.add(cancelar);

        borrar = new JButton("Borrar (Desactivar)");
        borrar.setSize(220, 25);
        borrar.setLocation(180, 250);
        borrar.addActionListener(this);
        this.add(borrar);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == borrar) {
            int selectedRow = serviciosTable.getSelectedRow();
            if (selectedRow >= 0) {
                String servicio = (String) tableModel.getValueAt(selectedRow, 0);
                System.out.println(servicio);
                boolean estado = op.eliminarServicio(servicio);
                if (estado) {
                    tableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un servicio para borrar.");
            }
        } else if (e.getSource() == cancelar) {
            this.setVisible(false);
        }
    }
}
