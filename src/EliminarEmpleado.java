import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EliminarEmpleado extends JFrame implements ActionListener {

    private Operaciones op;
    private JTextField txtCedula;
    private JButton eliminar;
    private JButton cancelar;



    public EliminarEmpleado(Operaciones op){

        super.setLayout(null);
        this.op = op;
        op.conectar();



        JLabel labCedula = new JLabel("Cedula:");
        labCedula.setSize(100,25);
        labCedula.setLocation(60,20);
        this.add(labCedula);
        txtCedula = new JTextField();
        txtCedula.setSize(100,25);
        txtCedula.setLocation(120,20);
        this.add(txtCedula);


        eliminar = new JButton("Eliminar");
        eliminar.setSize(100,25);
        eliminar.setLocation(90,60);
        eliminar.addActionListener(this);
        this.add(eliminar);

        cancelar = new JButton("Cancelar");
        cancelar.setSize(100,25);
        cancelar.setLocation(200,60);
        cancelar.addActionListener(this);
        this.add(cancelar);



        super.setTitle("ELIMINAR");
        super.setSize(400, 250);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);



    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == eliminar){

            String cedula = txtCedula.getText();

            boolean estado = op.eliminarEmpleado(cedula);

            if(estado == true){
                this.setVisible(false);
            }
            else if(estado == false){
                this.setVisible(true);
            }

        }
        else if(e.getSource() == cancelar){
            this.setVisible(false);
        }

    }
}
