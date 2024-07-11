import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VisualLogIn extends JFrame implements ActionListener {

    private JButton logIn;
    private JButton cancel;

    public JTextField usuario;
    private JPasswordField contrasena;
    private JLabel labelUsu;
    private JLabel labelCon;
    private Operaciones op;


    public VisualLogIn(Operaciones op){
        this.op = op;
        op.conectar();
        this.setSize(300,200);
        this.setTitle("LOG IN");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        logIn = new JButton("Log In");
        logIn.setSize(100,20);
        logIn.addActionListener(this);
        logIn.setLocation(25,100);

        cancel = new JButton("Cancelar");
        cancel.setSize(100,20);
        cancel.addActionListener(this);
        cancel.setLocation(175,100);

        usuario = new JTextField();
        usuario.setSize(160,25);
        usuario.setLocation(100,20);


        contrasena = new JPasswordField();
        contrasena.setSize(160,25);
        contrasena.setLocation(100,50);
        contrasena.setEchoChar('*');

        labelUsu = new JLabel("Usuario:");
        labelUsu.setSize(100,25);
        labelUsu.setLocation(20,20);

        labelCon = new JLabel("Contrasena:");
        labelCon.setSize(100,25);
        labelCon.setLocation(20,50);

        this.add(labelCon);
        this.add(labelUsu);
        this.add(logIn);
        this.add(cancel);
        this.add(usuario);
        this.add(contrasena);





        this.setVisible(true);
    }





    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == logIn) {
            String usuarioU = usuario.getText();
            String usuarioC = contrasena.getText();
            boolean status = op.obtenerUsuario(usuarioU);
            boolean statusContrasena = op.obtenerContrasena(usuarioC);

            if(status == true &&(statusContrasena == true)) {
                ControladorUsuarios cu = new ControladorUsuarios(1);
                this.setVisible(false);
            }
            else {
                JOptionPane.showMessageDialog(null,"Contrasena o Usuario Incorrecto","ERROR",JOptionPane.ERROR_MESSAGE);

            }

        }

        }
    }


