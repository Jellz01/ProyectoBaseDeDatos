import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class formulariosIngresarPersona extends JFrame implements ActionListener {
    private JButton botGrabar;
    private JButton botCancelar;

    public JTextField txtUsuario;
    public JTextField txtCon;
    public JTextField txtCon1;

    JTextField txtCedula;
    JTextField txtNombre;
    JTextField txtApellido;
    JTextField txtDireccion;
    JTextField txtSalario;

    private Operaciones op;
    private JComboBox<String> contratos;

    formulariosIngresarPersona(Operaciones op) throws SQLException {
        this.op = op;
        op.conectar();

        super.setLayout(null);

        JLabel labCedula = new JLabel("Cédula:");
        labCedula.setBounds(10, 5, 90, 25);
        this.add(labCedula);
        txtCedula = new JTextField();
        txtCedula.setBounds(100, 5, 80, 25);
        this.add(txtCedula);

        JLabel labNombre = new JLabel("Nombre:");
        labNombre.setBounds(10, 35, 90, 25);
        this.add(labNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(100, 35, 150, 25);
        this.add(txtNombre);

        JLabel labApellido = new JLabel("Apellido:");
        labApellido.setBounds(10, 65, 90, 25);
        this.add(labApellido);
        txtApellido = new JTextField();
        txtApellido.setBounds(100, 65, 150, 25);
        this.add(txtApellido);

        JLabel labDireccion = new JLabel("Telefono:");
        labDireccion.setBounds(10, 95, 90, 25);
        this.add(labDireccion);
        txtDireccion = new JTextField();
        txtDireccion.setBounds(100, 95, 200, 25);
        this.add(txtDireccion);

        JLabel labSalario = new JLabel("Email:");
        labSalario.setBounds(10, 125, 90, 25);
        this.add(labSalario);
        txtSalario = new JTextField();
        txtSalario.setBounds(100, 125, 200, 25);
        this.add(txtSalario);

        JLabel labContratos = new JLabel("Tipo:");
        labContratos.setBounds(10, 155, 90, 25);
        this.add(labContratos);

        contratos = new JComboBox<>();
        contratos.setSize(225, 30);
        contratos.setLocation(100, 155);
        contratos.addActionListener(this);

        // Populate the JComboBox with contract data from the database
        ArrayList<String> contratoList = op.obtenerContratos();
        for (String contrato : contratoList) {
            contratos.addItem(contrato);
        }
        this.add(contratos);

        JLabel labelUsu = new JLabel("Usuario:");
        labelUsu.setSize(90, 25);
        labelUsu.setLocation(10, 190);
        this.add(labelUsu);

        txtUsuario = new JTextField();
        txtUsuario.setSize(200, 25);
        txtUsuario.setLocation(100, 190);
        txtUsuario.setEnabled(false);
        this.add(txtUsuario);

        JLabel labCon = new JLabel("Contraseña:");
        labCon.setSize(90, 25);
        labCon.setLocation(10, 220);
        this.add(labCon);

        txtCon = new JTextField();
        txtCon.setSize(200, 25);
        txtCon.setLocation(100, 220);
        txtCon.setEnabled(false);
        this.add(txtCon);

        JLabel labCon1 = new JLabel("Contraseña nuevamente:");
        labCon1.setSize(190, 25);
        labCon1.setLocation(10, 250);
        this.add(labCon1);

        txtCon1 = new JTextField();
        txtCon1.setSize(200, 25);
        txtCon1.setLocation(155, 250);
        txtCon1.setEnabled(false);
        this.add(txtCon1);

        botGrabar = new JButton("Grabar");
        botGrabar.setBounds(40, 295, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Cancelar");
        botCancelar.setBounds(150, 295, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);

        super.setTitle("Empleados");
        super.setSize(400, 350);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == contratos) {
            int numeroElegido = contratos.getSelectedIndex();
            if (numeroElegido == 0) {
               /* txtUsuario.setEnabled(true);
                txtCon.setEnabled(true);
                txtCon1.setEnabled(true);
                */

            } else {
                /*
                txtUsuario.setEnabled(false);
                txtCon.setEnabled(false);
                txtCon1.setEnabled(false);
                */

            }
        }

        if (e.getSource() == botGrabar) {
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String telefono = txtDireccion.getText();
            String salario = txtSalario.getText();

            boolean estado = op.agregarEmpleado(1, nombre, apellido, telefono, salario, "caaca");

            if (estado) {
                this.setVisible(false);
            } else {
                this.setVisible(true);
            }
        }

        if (e.getSource() == botCancelar) {
            this.setVisible(false);
        }
    }


}
