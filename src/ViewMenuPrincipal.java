import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.Random;

public class ViewMenuPrincipal extends JFrame implements ActionListener {

    private ControlerMenuPrincipal cmp;

    private JMenuBar menu;

    JMenuItem ModificarCitas;

    private JMenu servicios;
    private JMenu clientes;
    private JMenu empleados;
    private JMenu facturas;
    private JMenu citas;

    private JMenu animales;
    private JMenuItem ingresarAnimal;
    private JMenuItem borrarAnimal;
    private JMenuItem modificarAnimal;
    private JMenuItem listarAnimal;
    private JMenuItem ingresoCliente;
    private JMenuItem modificarCliente;
    private JMenuItem borrarCliente;
    private JMenuItem listarClientes;

    private JMenuItem ingresoEmpelado;
    private JMenuItem modificarEmpleado;
    private JMenuItem borrarEmpleado;
    private JMenuItem listarEmpleado;


    private JMenuItem ingresoFactura;
    private JMenuItem borrarFactura;
    private JMenuItem listarFacturas;

    private JMenuItem ingresoCita;
    private JMenuItem borrarCita;
    private JMenuItem ModificarCita;
    private JMenuItem listarFCita;

    private JMenuItem ingresoServicio;
    private JMenuItem modificarServicio;
    private JMenuItem borrarServicio;
    private JMenuItem listarServicio;

    private JLabel label;
    private JPanel panelIm;

    private JButton botCerrar;
    private ViewMenuPrincipal vmp;
    String usuarioU;

    Properties properties;

    public void menu(ViewMenuPrincipal vmp,String usuarioU){
        this.vmp = vmp;
        this.usuarioU = usuarioU;
        this.properties = properties;





        ImageIcon image = new ImageIcon("C:\\Users\\JWell\\Downloads\\hunting-dogs-5.jpg");
        ImageIcon image1 = new ImageIcon("C:\\Users\\JWell\\Downloads\\imagen2.jpg");
        ImageIcon image2 = new ImageIcon("C:\\Users\\JWell\\Downloads\\perro.jpg");
        ImageIcon image3 = new ImageIcon("C:\\Users\\JWell\\Downloads\\dalmatian-card-large.jpg");
        ImageIcon image4 = new ImageIcon("C:\\Users\\JWell\\Downloads\\chi.jpg");
        ImageIcon icono = new ImageIcon("C:\\Users\\JWell\\Downloads\\2324244_animals_bone_dog_dogs_pets_icon.png");

        Font font = new Font("Arial", Font.BOLD, 20);

        label = new JLabel();
        label.setText("Veterinaria Paws & Claws");
        label.setFont(font);

        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1;
        double result = (double) randomNumber / 10;
        if (result >= 0.01 && result <= 0.2) {
            label.setIcon(image);
        }
        else if (result>= 0.21 && result<= 0.4){
            label.setIcon(image1);
        }
        else if (result>= 0.41 && result<= 0.6){
            label.setIcon(image2);
        }
        else if(result >= 0.61 && result<= 0.8){
            label.setIcon(image3);
        }
        else if(result >= 0.81 && result<= 1){
            label.setIcon(image4);
        }
        else {
            label.setIcon(image);
        }


        this.setSize(700,450);
        this.setTitle("Clinica Veterinaria PERRAS");
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        panelIm = new JPanel();
        panelIm.setSize(900,400);
        panelIm.setLocation(0,0);
        panelIm.add(label);


        menu = new JMenuBar();
        menu.setBackground(Color.gray);
        menu.setLocation(0,0);
        menu.setSize(900,25);


        animales = new JMenu("Animales");
        ingresarAnimal = new JMenuItem("Ingresar");
        ingresarAnimal.addActionListener(this);
        borrarAnimal  = new JMenuItem("Borrar");
        borrarAnimal.addActionListener(this);
        modificarAnimal = new JMenuItem("Modificar");
        modificarAnimal.addActionListener(this);
        listarAnimal = new JMenuItem("Listar");
        listarAnimal.addActionListener(this);
        animales.add(ingresarAnimal);
        animales.add(borrarAnimal);
        animales.add(modificarAnimal);
        animales.add(listarAnimal);



        clientes = new JMenu("Clientes");

        ingresoCliente = new JMenuItem("Ingresar");
        ingresoCliente.addActionListener(this);
        modificarCliente = new JMenuItem("Modificar");
        modificarCliente.addActionListener(this);
        borrarCliente = new JMenuItem("Borrar");
        borrarCliente.addActionListener(this);
        listarClientes = new JMenuItem("Listar");
        listarClientes.addActionListener(this);
        clientes.add(ingresoCliente);
        clientes.add(modificarCliente);
        clientes.add(borrarCliente);
        clientes.add(listarClientes);


        empleados = new JMenu("Empleados");

        ingresoEmpelado = new JMenuItem("Ingresar");
        ingresoEmpelado.addActionListener(this);
        modificarEmpleado = new JMenuItem("Modificar");
        modificarEmpleado.addActionListener(this);
        borrarEmpleado = new JMenuItem("Borrar");
        borrarEmpleado.addActionListener(this);
        listarEmpleado = new JMenuItem("Listar");
        listarEmpleado.addActionListener(this);

        empleados.add(ingresoEmpelado);
        empleados.add(modificarEmpleado);
        empleados.add(borrarEmpleado);
        empleados.add(listarEmpleado);

        facturas = new JMenu("Facturas");
        ingresoFactura = new JMenuItem("Ingresar");
        ingresoFactura.addActionListener(this);
        borrarFactura = new JMenuItem("Anular");
        borrarFactura.addActionListener(this);
        listarFacturas = new JMenuItem("Listar");
        listarFacturas.addActionListener(this);
        facturas.add(ingresoFactura);
        facturas.add(borrarFactura);
        facturas.add(listarFacturas);

        citas = new JMenu("Citas");
        ingresoCita = new JMenuItem("Ingresar");
        ingresoCita.addActionListener(this);
        ModificarCita = new JMenuItem("listar");
        ModificarCita.addActionListener(this);
        ModificarCitas = new JMenuItem("Cancelar");
        ModificarCitas.addActionListener(this);




        citas.add(ingresoCita);

        citas.add(ModificarCita);
        citas.add(ModificarCitas);


        servicios = new JMenu("Servicios");
        ingresoServicio = new JMenuItem("Ingresar");
        ingresoServicio.addActionListener(this);
        modificarServicio = new JMenuItem("Modificar");
        modificarServicio.addActionListener(this);
        borrarServicio = new JMenuItem("Borrar");
        borrarServicio.addActionListener(this);
        listarServicio = new JMenuItem("Listar");
        listarServicio.addActionListener(this);
        servicios.add(ingresoServicio);
        servicios.add(modificarServicio);
        servicios.add(borrarServicio);
        servicios.add(listarServicio);

        menu.add(empleados);
        menu.add(animales);
        menu.add(clientes);
        menu.add(citas);
        menu.add(facturas);
        menu.add(servicios);

        botCerrar = new JButton("Salir");
        botCerrar.setSize(700,30);
        botCerrar.addActionListener(this);
        botCerrar.setLocation(0,385);

        this.add(menu);
        this.add(panelIm);
        this.add(botCerrar);
        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==botCerrar){
            this.setVisible(false);
        } else if(e.getSource() == ingresoEmpelado){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(1,vmp);

        }
        else if(e.getSource()== modificarEmpleado){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(2,vmp);

        }
        else if (e.getSource() == borrarEmpleado){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(3,vmp);

        }
        else if (e.getSource() == listarEmpleado){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(4,vmp);

        }
        else if (e.getSource() == ingresarAnimal){

            ControlerMenuPrincipal cmp =  new ControlerMenuPrincipal(5,vmp);

        }
        else if(e.getSource() == borrarAnimal){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(6,vmp);

        }
        else if (e.getSource() == modificarAnimal){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(7,vmp);

        }
        else if (e.getSource() == listarAnimal){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(8,vmp);

        }
        else if (e.getSource() == ingresoCliente){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(9,vmp);


        }
        else if (e.getSource() == modificarCliente){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(10,vmp);

        }
        else if(e.getSource() == borrarCliente){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(11,vmp);

        }
        else if(e.getSource() == listarClientes){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(12,vmp);

        }
        else if (e.getSource() == ingresoFactura){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(13,vmp);
            cmp.factura(usuarioU);
            System.out.println(usuarioU);

        }
        else if (e.getSource() == borrarFactura){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(14,vmp);

        }

        else if (e.getSource() == listarFacturas){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(15,vmp);

        }
        else if (e.getSource() == ingresoCita){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(16,vmp);

        }
        else if (e.getSource() == ModificarCita){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(17,vmp);

        }
        else if (e.getSource() == ModificarCitas){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(18,vmp);
            System.out.println("nkk");

        }
        else if (e.getSource() == listarFCita){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(19,vmp);
            System.out.println("Entro");

        }
        else if (e.getSource() == ingresoServicio){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(20,vmp);


        }
        else if (e.getSource() == modificarServicio){
            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(21,vmp);

        }
        else if (e.getSource() == borrarServicio){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(22,vmp);

        }
        else if (e.getSource() == listarServicio){

            ControlerMenuPrincipal cmp = new ControlerMenuPrincipal(23,vmp);

        }
        else if( e.getSource() == botCerrar){
            this.setVisible(false);
        }
    }
}
