import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormularioServicio extends JFrame implements ActionListener {

    public JButton guardar;
    public JButton cancelar;

    private JLabel labNombre;
    private JLabel labPrecio;
    private JLabel estado;
    private JLabel iva;
    private JLabel id;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    public JRadioButton activo;
    public JRadioButton inactivo;
    public JRadioButton sIva;
    public JRadioButton nIva;
    public Operaciones op;

    public ButtonGroup grupoEstado;
    public ButtonGroup grupoIva;


    public FormularioServicio(Operaciones op){

        this.op =op;
        op.conectar();

        super.setSize(300,250);
        super.setLayout(null);
        super.setTitle("Ingreso de Servicios");
        super.setLocationRelativeTo(null);

        id = new JLabel("ID:");
        id.setSize(90,25);
        id.setLocation(10,20);
        this.add(id);




        txtId = new JTextField();
        txtId.setSize(120,25);
        txtId.setLocation(65,20);
        String id = String.valueOf(op.obtenerNumServicio());
        txtId.setText(id);
        txtId.setEnabled(false);
        this.add(txtId);

        labNombre = new JLabel("Nombre:");
        labNombre.setSize(90,25);
        labNombre.setLocation(10,50);
        this.add(labNombre);

        txtNombre = new JTextField();
        txtNombre.setSize(120,25);
        txtNombre.setLocation(65,50);
        this.add(txtNombre);

        labPrecio = new JLabel("Precio:");
        labPrecio.setSize(90,25);
        labPrecio.setLocation(10,80);
        this.add(labPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setSize(90,25);
        txtPrecio.setLocation(65,80);
        this.add(txtPrecio);




        iva = new JLabel("IVA:");
        iva.setSize(90,25);
        iva.setLocation(10,140);
        this.add(iva);

        sIva = new JRadioButton("Si");
        sIva.setSize(90,25);
        sIva.setLocation(65,140);
        this.add(sIva);

        nIva = new JRadioButton("NO");
        nIva.setSize(90,25);
        nIva.setLocation(165,140);
        this.add(nIva);

        grupoIva = new ButtonGroup();
        grupoIva.add(sIva);
        grupoIva.add(nIva);

        guardar = new JButton("Ingresar");
        guardar.setSize(90,25);
        guardar.setLocation(25,175);
        guardar.addActionListener(this);
        this.add(guardar);

        cancelar = new JButton("Cancelar");
        cancelar.setSize(90,25);
        cancelar.setLocation(150,175);
        cancelar.addActionListener(this);
        this.add(cancelar);



        super.setVisible(true);


    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == guardar){
            int id = 5;
            String codigo = txtId.getText();
            String nombre = txtNombre.getText();
            int precio = Integer.parseInt(txtPrecio.getText());
            String iva ;
            if(sIva.isSelected()){
                iva = "Y";
            }
            else{
                iva = "N";
            }

            String estadoo = "Desactivado";
            boolean cerrar = op.agregarServicio(codigo,nombre,precio,iva,estadoo);
            if(cerrar==true){
                this.setVisible(false);
            }

        }
        if(e.getSource() == cancelar){
            this.setVisible(false);
        }

    }
}
