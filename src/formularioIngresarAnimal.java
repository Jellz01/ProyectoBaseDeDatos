import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class formularioIngresarAnimal extends JFrame implements ActionListener {

    private JButton botGrabar;
    private JButton botCancelar;

    private JComboBox tipos;

    private JComboBox clientes;




    JTextField txtTipo;
    JTextField txtNombre;





    private Operaciones op;



    formularioIngresarAnimal(Operaciones op) throws SQLException {
        this.op = op;
        op.conectar();

        super.setLayout(null);



        JLabel labCLiente = new JLabel("Cliente:");
        labCLiente.setBounds(10,5,90,25);

        clientes = new JComboBox<>();
        clientes.setSize(215, 25);
        clientes.setLocation(100, 5);

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

        // Populate the JComboBox with contract data from the database

        ArrayList<String> listaTipos = op.obtenerTiposAnimales();
        System.out.print(listaTipos);
        for (String tipo : listaTipos) {
            tipos.addItem(tipo);
        }

        JLabel labNombre = new JLabel("Nombre:");
        labNombre.setBounds(10,70,90,25);

        txtNombre = new JTextField();
        txtNombre.setBounds(100,70,140,25);
        this.add(labNombre);
        this.add(txtNombre);

        botGrabar = new JButton("Grabar");
        botGrabar.setBounds(40, 110, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Cancelar");
        botCancelar.setBounds(150, 110, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);
        this.add(tipos);
        this.add(labTipo);
        this.add(labCLiente);
        this.add(clientes);


        super.setTitle("Ingresar Animales");
        super.setSize(350, 200);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botGrabar) {



        }
        if (e.getSource() == botCancelar) {
            this.setVisible(false);
        }
    }

}
