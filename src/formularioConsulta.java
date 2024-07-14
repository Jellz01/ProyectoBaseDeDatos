import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class formularioConsulta extends JFrame implements ActionListener {

    private JButton botGrabar;
    private JButton botCancelar;
    private JComboBox<String> tipos;
    private JComboBox<String> veterinarios;
    private JComboBox<String> clientes;
    private JTextField txtNombre;
    private JTextField txtFecha;

    private JTextField txtHora;

    private Operaciones op;

    formularioConsulta(Operaciones op) throws SQLException {
        this.op = op;
        op.conectar();

        super.setLayout(null);

        JLabel labCLiente = new JLabel("Cliente:");
        labCLiente.setBounds(10, 5, 90, 25);
        this.add(labCLiente);

        clientes = new JComboBox<>();
        clientes.setSize(215, 25);
        clientes.setLocation(100, 5);
        this.add(clientes);

        // Populate the JComboBox with contract data from the database
        ArrayList<String> listaClientes = op.obtenerNombreClientes();
        System.out.print(listaClientes);
        for (String clientee : listaClientes) {
            clientes.addItem(clientee);
        }

        JLabel labTipo = new JLabel("Tipo:");
        labTipo.setBounds(10, 35, 90, 25);
        this.add(labTipo);

        tipos = new JComboBox<>();
        tipos.setSize(115, 25);
        tipos.setLocation(100, 35);
        this.add(tipos);

        // Populate the JComboBox with contract data from the database
        ArrayList<String> listaTipos = null;
        listaTipos = op.obtenerTiposAnimales();

        System.out.print(listaTipos);
        for (String tipo : listaTipos) {
            tipos.addItem(tipo);
        }

        JLabel labNombre = new JLabel("Nombre:");
        labNombre.setBounds(10, 70, 90, 25);
        this.add(labNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 70, 140, 25);
        this.add(txtNombre);

        JLabel labVeterinario = new JLabel("Veterinario:");
        labVeterinario.setBounds(10, 100, 140, 25);
        this.add(labVeterinario);

        veterinarios = new JComboBox<>();
        veterinarios.setSize(115, 25);
        veterinarios.setLocation(100, 100);
        this.add(veterinarios);

        // Populate the JComboBox with contract data from the database
        ArrayList<String> listaVeterinarios = op.obtenerEmpleados();
        System.out.print(listaVeterinarios);
        for (String veterinario : listaVeterinarios) {
            veterinarios.addItem(veterinario);
        }

        JLabel labFecha = new JLabel("Fecha:");
        labFecha.setBounds(10, 130, 140, 25);
        this.add(labFecha);

        txtFecha = new JTextField();
        txtFecha.setSize(120,25);
        txtFecha.setLocation(100,130);
        this.add(txtFecha);


        JLabel labHora = new JLabel("Hora:");
        labHora.setBounds(10, 160, 140, 25);
        this.add(labHora);

        txtHora = new JTextField();
        txtHora.setSize(120,25);
        txtHora.setLocation(100,160);
        this.add(txtHora);


        botGrabar = new JButton("Grabar");
        botGrabar.setBounds(40, 200, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Cancelar");
        botCancelar.setBounds(150, 200, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);

        this.add(tipos);
        this.add(labTipo);
        this.add(labCLiente);
        this.add(clientes);

        super.setTitle("Ingresar Animales");
        super.setSize(350, 300);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }



    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource() == botGrabar) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            try {

                String fechaTexto = txtFecha.getText();
                String horaTexto = txtHora.getText();


                String fechaHoraTexto = fechaTexto + " " + horaTexto;

                // Parsear el texto combinado en un objeto Date
                Date fechaHoraDate = dateTimeFormat.parse(fechaHoraTexto);

                String nombreMascota = txtNombre.getText();
                String fechaHora = dateTimeFormat.format(fechaHoraDate);
                String estadoo = "Scheduled";
                String empleado = (String) veterinarios.getSelectedItem();
                int empleadoI = (Integer) op.obtenerIDVeterinario(empleado);

                String cliente = (String) clientes.getSelectedItem();
                int clienteI = op.obtenerIDCliente(cliente);



                System.out.println("Fecha y Hora: " + fechaHora);

                boolean estado = op.agregarCita(nombreMascota, fechaHora, estadoo, 1, empleadoI, 1);

            } catch (ParseException ex) {
                System.err.println("Error en el formato de fecha o hora: " + ex.getMessage());
            }
        }
        if (e.getSource() == botCancelar) {
            this.setVisible(false);
        }

    }
}
