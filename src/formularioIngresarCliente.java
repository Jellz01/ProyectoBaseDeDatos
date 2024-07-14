import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
public class formularioIngresarCliente extends JFrame implements ActionListener {



        private JButton botGrabar;
        private JButton botCancelar;



        JTextField txtCedula;
        JTextField txtNombre;
        JTextField txtApellido;
        JTextField txtDireccion;
        JTextField txtSalario;

        JTextField txtTelefono;

        private Operaciones op;
        private JComboBox contratos;

        private JComboBox<String> contratoss;


        formularioIngresarCliente(Operaciones op) throws SQLException {
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

            JLabel labApellido = new JLabel("Apellido:"); // Added
            labApellido.setBounds(10, 65, 90, 25);
            this.add(labApellido);
            txtApellido = new JTextField(); // Added
            txtApellido.setBounds(100, 65, 150, 25); // Adjusted width
            this.add(txtApellido);

            JLabel labDireccion = new JLabel("Direccion:");
            labDireccion.setBounds(10, 95, 90, 25);
            this.add(labDireccion);
            txtDireccion = new JTextField();
            txtDireccion.setBounds(100, 95, 200, 25);
            this.add(txtDireccion);

            JLabel labSalario = new JLabel("Email:");
            labSalario.setBounds(10,125,90,25);
            this.add(labSalario);
            txtSalario = new JTextField();
            txtSalario.setBounds(100, 125,200,25);
            this.add(txtSalario);

            JLabel labTel = new JLabel("Telefono:");
            labTel.setBounds(10,155,90,25);
            this.add(labTel);
            txtTelefono = new JTextField();
            txtTelefono.setBounds(100,155,200,25);
            this.add(txtTelefono);





            botGrabar = new JButton("Grabar");
            botGrabar.setBounds(40, 190, 100, 25);
            botGrabar.addActionListener(this);
            this.add(botGrabar);

            botCancelar = new JButton("Cancelar");
            botCancelar.setBounds(150, 190, 100, 25);
            botCancelar.addActionListener(this);
            this.add(botCancelar);



            super.setTitle("Empelados");
            super.setSize(400, 350);
            super.setLocationRelativeTo(null);
            super.setResizable(false);
            super.setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == botGrabar) {

                String cedula = txtCedula.getText();
                String nombre = txtNombre.getText();
                String apellido = txtApellido.getText();
                String direccion = txtDireccion.getText();
                String email = txtSalario.getText();
                String telefono = txtTelefono.getText();




               boolean estado = op.agregarCliente( nombre, apellido, direccion, telefono,email,cedula);

                if(estado == true){
                  this.setVisible(false);
                }
                else{
                    this.setVisible(true);
                }

            }
            if (e.getSource() == botCancelar) {
                this.setVisible(false);
            }
        }
    }
