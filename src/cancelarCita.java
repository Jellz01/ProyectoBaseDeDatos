import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class cancelarCita extends JFrame implements ActionListener {

    private JTable clientesTable;
    private JButton cancelar;
    private JButton borrar;
    private JLabel labClientes;

    private Operaciones op;
    private DefaultTableModel tableModel;

    public cancelarCita(Operaciones op) {
        this.op = op;
        op.conectar();

        this.setSize(600, 350);
        this.setLayout(null);

        this.setTitle("Cancelar Citas");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Initialize table model and JTable with multiple columns
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre Mascota", "Fecha Cita"}, 0);
        clientesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        scrollPane.setSize(500, 200);
        scrollPane.setLocation(20, 40);
        this.add(scrollPane);

        // Load data into table
        ArrayList<String> listaCitas = op.obtenerCitass();
        for (String cita : listaCitas) {
            // Split the string into columns based on the delimiter
            String[] datosCita = cita.split("\\|");
            tableModel.addRow(datosCita);
        }

        labClientes = new JLabel("Citas:");
        labClientes.setSize(100, 25);
        labClientes.setLocation(20, 10);
        this.add(labClientes);

        cancelar = new JButton("Salir");
        cancelar.setSize(120, 25);
        cancelar.setLocation(40, 250);
        cancelar.addActionListener(this);
        this.add(cancelar);

        borrar = new JButton("Cancelar Cita");
        borrar.setSize(120, 25);
        borrar.setLocation(180, 250);
        borrar.addActionListener(this);
        this.add(borrar);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == borrar) {
            // Get the selected row index
            int selectedRow = clientesTable.getSelectedRow();

            if (selectedRow != -1) { // Ensure a row is selected
                // Get the Cita_ID from the selected row
                String citaId = (String) tableModel.getValueAt(selectedRow, 0); // Assuming ID is in the first column
                System.out.println("Cita ID selected: " + citaId);
                int idCita = Integer.parseInt(citaId);
                boolean estado = op.cancelarCita(idCita);
                if(estado == true){
                    this.setVisible(false);

                }
                else{
                    this.setVisible(true);
                }
                // Implement the logic to cancel the selected appointment
                // Example: op.cancelarCita(citaId);
            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna cita.", "Error", JOptionPane.ERROR_MESSAGE);
            }


        } else if (e.getSource() == cancelar) {
            this.setVisible(false);
        }
    }
}
