import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;




    public class cancelarCita extends JFrame implements ActionListener {

        public JComboBox empleados;

        public JButton cancelar;
        public JButton borrar;
        private JLabel labServicos;

        public Operaciones op;
        public cancelarCita(Operaciones op){
            this.op=op;
            op.conectar();

            this.setSize(450,190);
            this.setLayout(null);

            this.setTitle("Borrar Cita");
            this.setResizable(false);
            this.setLocationRelativeTo(null);




            empleados = new JComboBox();
            empleados.setSize(300,25);
            empleados.setLocation(100,40);
            empleados.addActionListener(this);

            ArrayList<String> listaTipos = op.obtenerCitas();
            System.out.print(listaTipos);
            for (String tipo : listaTipos) {
                empleados.addItem(tipo);
            }
            this.add(empleados);

            labServicos = new JLabel("Citas:");
            labServicos.setSize(100,25);
            labServicos.setLocation(20,40);
            this.add(labServicos);

            cancelar = new JButton("Cancelar");
            cancelar.setSize(120,25);
            cancelar.setLocation(80,100);
            cancelar.addActionListener(this);

            this.add(cancelar);

            borrar = new JButton("Borrar");
            borrar.setSize(120,25);
            borrar.setLocation(240,100);
            borrar.addActionListener(this);

            this.add(borrar);



            this.setVisible(true);



        }


        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == borrar){


                }


            }

        }




