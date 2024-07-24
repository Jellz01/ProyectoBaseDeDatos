import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class borrarServicios extends JFrame implements ActionListener {

    public JComboBox servicios;

    public JButton cancelar;
    public JButton borrar;
    private JLabel labServicos;

    public Operaciones op;
    public borrarServicios(Operaciones op){
        this.op=op;
        op.conectar();

        this.setSize(450,190);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Borrar Servicios");
        this.setResizable(false);
        this.setLocationRelativeTo(null);




        servicios = new JComboBox();
        servicios.setSize(200,25);
        servicios.setLocation(100,40);
        servicios.addActionListener(this);

        ArrayList<String> listaTipos = op.obtenerServicios();
        System.out.print(listaTipos);
        for (String tipo : listaTipos) {
            servicios.addItem(tipo);
        }
        this.add(servicios);

        labServicos = new JLabel("Servicios:");
        labServicos.setSize(100,25);
        labServicos.setLocation(20,40);
        this.add(labServicos);

        cancelar = new JButton("Cancelar");
        cancelar.setSize(120,25);
        cancelar.setLocation(40,100);
        cancelar.addActionListener(this);

        this.add(cancelar);

        borrar = new JButton("Borrar (Desactivar)");
        borrar.setSize(220,25);
        borrar.setLocation(180,100);
        borrar.addActionListener(this);

        this.add(borrar);



        this.setVisible(true);



    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == borrar){

            String servicio = (String) servicios.getSelectedItem();
            System.out.println(servicio);
            boolean estado = op.eliminarServicio(servicio);
            if(estado=true){
                this.setVisible(false);
            }
        }

    }
}
