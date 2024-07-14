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




        JLabel labTipo = new JLabel("Tipo:");
        labTipo.setBounds(10, 35, 90, 25);
        this.add(labTipo);

       txtTipo = new JTextField();
       txtTipo.setBounds(70,35,90,25);
       this.add(txtTipo);




        botGrabar = new JButton("Grabar");
        botGrabar.setBounds(40, 70, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Cancelar");
        botCancelar.setBounds(150, 70, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);
        this.add(txtTipo);
        this.add(labTipo);
        this.add(botGrabar);




        super.setTitle("Ingresar Animales");
        super.setSize(300, 150);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botGrabar) {


            String tipo = txtTipo.getText();

            boolean estado = op.agregarTipoAnimal(tipo);

            if(estado == true ){
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
