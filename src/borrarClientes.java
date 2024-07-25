import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class borrarClientes extends JFrame implements ActionListener {

    private JTable clientesTable;
    private JButton cancelar;
    private JButton borrar;
    private JLabel labClientes;

    private Operaciones op;
    private DefaultTableModel tableModel;

    public borrarClientes(Operaciones op) {
        this.op = op;
        op.conectar();

        this.setSize(600, 350);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Borrar Clientes");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new String[]{"Cliente"}, 0);
        clientesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        scrollPane.setSize(500, 200);
        scrollPane.setLocation(20, 40);
        this.add(scrollPane);

        // Load data into table
        ArrayList<String> listaClientes = op.obtenerClientes();
        for (String cliente : listaClientes) {
            tableModel.addRow(new Object[]{cliente});
        }

        labClientes = new JLabel("Clientes:");
        labClientes.setSize(100, 25);
        labClientes.setLocation(20, 10);
        this.add(labClientes);

        cancelar = new JButton("Cancelar");
        cancelar.setSize(120, 25);
        cancelar.setLocation(40, 250);
        cancelar.addActionListener(this);
        this.add(cancelar);

        borrar = new JButton("Borrar");
        borrar.setSize(120, 25);
        borrar.setLocation(180, 250);
        borrar.addActionListener(this);
        this.add(borrar);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == borrar) {
            int selectedRow = clientesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String cliente = (String) tableModel.getValueAt(selectedRow, 0);
                System.out.println(cliente);
                boolean estado = op.eliminarCliente(cliente);
                if (estado) {
                    tableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para borrar.");
            }
        } else if (e.getSource() == cancelar) {
            this.setVisible(false);
        }
    }
}
