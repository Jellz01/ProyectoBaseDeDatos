import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormularioConsultas extends JFrame implements ActionListener {

    private JComboBox<String> comboClientes;
    private JComboBox<String> comboTiposMascota;
    private JComboBox<String> comboVeterinarios;
    private JTextField campoNombreMascota;
    private JTextField campoFecha;
    private JTextField campoHora;
    private JButton botonGuardar;
    private JButton botonCancelar;

    public FormularioConsultas() {
        this.setSize(450, 550);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Citas");

        // Datos de ejemplo para los combo boxes
        String[] clientes = {"Cliente 1", "Cliente 2", "Cliente 3"};
        String[] tiposMascota = {"Perro", "Gato", "Canario"};
        String[] veterinarios = {"Veterinario 1", "Veterinario 2", "Veterinario 3"};

        // Etiquetas
        JLabel labelCliente = new JLabel("Cliente:");
        JLabel labelTipoMascota = new JLabel("Tipo de Mascota:");
        JLabel labelVeterinario = new JLabel("Veterinario:");
        JLabel labelNombreMascota = new JLabel("Nombre de la Mascota:");
        JLabel labelFecha = new JLabel("Fecha (YYYY-MM-DD):");
        JLabel labelHora = new JLabel("Hora (HH:MM):");

        labelCliente.setBounds(50, 50, 150, 30);
        labelTipoMascota.setBounds(50, 100, 150, 30);
        labelVeterinario.setBounds(50, 150, 150, 30);
        labelNombreMascota.setBounds(50, 200, 150, 30);
        labelFecha.setBounds(50, 250, 150, 30);
        labelHora.setBounds(50, 300, 150, 30);

        // Inicialización de los JComboBox
        comboClientes = new JComboBox<>(clientes);
        comboTiposMascota = new JComboBox<>(tiposMascota);
        comboVeterinarios = new JComboBox<>(veterinarios);

        comboClientes.setBounds(200, 50, 200, 30);
        comboTiposMascota.setBounds(200, 100, 200, 30);
        comboVeterinarios.setBounds(200, 150, 200, 30);

        // Campos de texto
        campoNombreMascota = new JTextField();
        campoNombreMascota.setBounds(200, 200, 200, 30);
        campoFecha = new JTextField();
        campoFecha.setBounds(200, 250, 200, 30);
        campoHora = new JTextField();
        campoHora.setBounds(200, 300, 200, 30);

        // Botones
        botonGuardar = new JButton("Guardar");
        botonGuardar.setBounds(50, 400, 150, 30);
        botonGuardar.addActionListener(this);

        botonCancelar = new JButton("Cancelar");
        botonCancelar.setBounds(250, 400, 150, 30);
        botonCancelar.addActionListener(this);

        // Añadir componentes al JFrame
        this.add(labelCliente);
        this.add(comboClientes);
        this.add(labelTipoMascota);
        this.add(comboTiposMascota);
        this.add(labelVeterinario);
        this.add(comboVeterinarios);
        this.add(labelNombreMascota);
        this.add(campoNombreMascota);
        this.add(labelFecha);
        this.add(campoFecha);
        this.add(labelHora);
        this.add(campoHora);
        this.add(botonGuardar);
        this.add(botonCancelar);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonGuardar) {
            String cliente = (String) comboClientes.getSelectedItem();
            String tipoMascota = (String) comboTiposMascota.getSelectedItem();
            String veterinario = (String) comboVeterinarios.getSelectedItem();
            String nombreMascota = campoNombreMascota.getText();
            String fecha = campoFecha.getText();
            String hora = campoHora.getText();

            // Aquí puedes agregar la lógica para guardar la cita
            System.out.println("Cita guardada:");
            System.out.println("Cliente: " + cliente);
            System.out.println("Tipo de Mascota: " + tipoMascota);
            System.out.println("Veterinario: " + veterinario);
            System.out.println("Nombre de la Mascota: " + nombreMascota);
            System.out.println("Fecha: " + fecha);
            System.out.println("Hora: " + hora);

            // Validar que no se asignen más de una cita al mismo veterinario en la misma fecha y hora
            // Aquí debes agregar la lógica para esta validación
        } else if (e.getSource() == botonCancelar) {
            // Aquí puedes agregar la lógica para cancelar la cita y liberar el espacio del veterinario
            System.out.println("Cita cancelada");
        }
    }


}
