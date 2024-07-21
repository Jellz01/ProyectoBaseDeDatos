import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class formularioFactura extends JFrame implements ActionListener {

    private JButton botGrabar;
    private JButton botCancelar;

    private JTextField txtNum;
    private JTextField txtFecha;
    private JTextField txtDescripcion;
    public JTextField txtSubT;
    public JTextField txtIva;

    private JTextField txtCant;
    public JTextField txtPrecioU;
    public JTextField txtTotal;

    private Operaciones op;
    public JComboBox<String> clientes;
    public JComboBox<String> servicios;
    public JComboBox<String> usuarios;

    public formularioFactura(Operaciones op) {
        this.op = op;
        op.conectar();
        super.setLayout(null);

        JLabel labCedula = new JLabel("Factura N°:");
        labCedula.setBounds(10, 5, 90, 25);
        this.add(labCedula);

        txtNum = new JTextField();
        txtNum.setBounds(100, 5, 80, 25);
        txtNum.setEnabled(false);
        int numFactura = op.obtenerNumFactura();
        txtNum.setText(String.valueOf(numFactura));
        this.add(txtNum);

        JLabel labNombre = new JLabel("Fecha:");
        labNombre.setBounds(10, 35, 90, 25);
        this.add(labNombre);
        LocalDate currentDate = LocalDate.now();
        txtFecha = new JTextField();
        txtFecha.setText(String.valueOf(currentDate));
        txtFecha.setEnabled(false);
        txtFecha.setBounds(100, 35, 150, 25);
        this.add(txtFecha);

        JLabel labApellido = new JLabel("Cliente:");
        labApellido.setBounds(10, 65, 90, 25);
        this.add(labApellido);
        clientes = new JComboBox<>();
        clientes.setSize(120, 25);
        clientes.setLocation(100, 65);
        ArrayList<String> listaClientes = op.obtenerClientes();
        for (String tipo : listaClientes) {
            clientes.addItem(tipo);
        }
        this.add(clientes);

        JLabel labUsuarios = new JLabel("Usuario:");
        labUsuarios.setBounds(10, 95, 90, 25);
        this.add(labUsuarios);
        usuarios = new JComboBox<>();
        usuarios.setSize(120, 25);
        usuarios.setLocation(100, 95);
        ArrayList<String> listaUsuarios = op.obtenerUsuarios();
        for (String tipo : listaUsuarios) {
            usuarios.addItem(tipo);
        }
        this.add(usuarios);

        JLabel labDireccion = new JLabel("Subtotal:");
        labDireccion.setBounds(10, 185, 90, 25);
        this.add(labDireccion);
        txtSubT = new JTextField();
        txtSubT.setBounds(100, 185, 200, 25);
        txtSubT.setText("0");
        this.add(txtSubT);

        JLabel labSalario = new JLabel("IVA 15%:");
        labSalario.setBounds(10, 245, 90, 25);
        this.add(labSalario);
        txtIva = new JTextField();
        txtIva.setEditable(false);
        txtIva.setBounds(100, 245, 200, 25);
        this.add(txtIva);

        JLabel labTotal = new JLabel("Total:");
        labTotal.setBounds(10, 275, 90, 25);
        this.add(labTotal);
        txtTotal = new JTextField();
        txtTotal.setSize(200, 25);
        txtTotal.setLocation(100, 275);
        txtTotal.setEnabled(false);
        this.add(txtTotal);

        JLabel labDesc = new JLabel("Servicio:");
        labDesc.setBounds(10, 125, 90, 25);
        this.add(labDesc);

        servicios = new JComboBox<>();
        servicios.setSize(120, 25);
        servicios.setLocation(100, 125);
        servicios.addActionListener(this);
        ArrayList<String> listaServicios = op.obtenerServiciosCombo();
        for (String servicio : listaServicios) {
            servicios.addItem(servicio);
        }
        this.add(servicios);

        JLabel labCant = new JLabel("Cantidad");
        labCant.setBounds(10, 155, 90, 25);
        this.add(labCant);

        txtCant = new JTextField();
        txtCant.setSize(200, 25);
        txtCant.setLocation(100, 155);
        this.add(txtCant);


        String precioUni = String.valueOf(op.obtenerPrecioUnitario(servicios.getName()));
        System.out.println(precioUni);

        JLabel labPU = new JLabel("Precio por 1:");
        labPU.setBounds(10, 215, 90, 25);
        this.add(labPU);

        txtPrecioU = new JTextField();
        txtPrecioU.setText(precioUni);
        txtPrecioU.setSize(200, 25);
        txtPrecioU.setLocation(100, 215);

        txtPrecioU.setEnabled(false);
        this.add(txtPrecioU);

        botGrabar = new JButton("Grabar");
        botGrabar.setBounds(60, 305, 100, 25);
        botGrabar.addActionListener(this);
        this.add(botGrabar);

        botCancelar = new JButton("Cancelar");
        botCancelar.setBounds(180, 305, 100, 25);
        botCancelar.addActionListener(this);
        this.add(botCancelar);

        super.setTitle("Facturación");
        super.setSize(400, 400);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);

        txtSubT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSalario();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSalario();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSalario();
            }
        });
    }

    private void updateSalario() {
        try {
            float valorT = Float.parseFloat(txtSubT.getText());
            float valorCI = valorT * 0.15f;
            float valorTT = valorT * 1.15f;
            txtTotal.setText(String.valueOf(valorTT));
            txtIva.setText(String.valueOf(valorCI));
        } catch (NumberFormatException ex) {
            txtSubT.setText("0.0");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botGrabar) {
            String cliente = (String) clientes.getSelectedItem();
            String email = op.obtenerEmailCliente(cliente);
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            SendEmail sm = new SendEmail(properties,email);

            String num = txtNum.getText();
            int numm = Integer.parseInt(num);
            String fechaa = txtFecha.getText();
            String subt = txtSubT.getText();
            String iva = txtIva.getText();
            String totalF = txtTotal.getText();

            // Manejo del JComboBox usuarios
            String selectedUsuario = (String) usuarios.getSelectedItem();
            System.out.println(selectedUsuario);
            int usuarioId = op.obtenerIDUsuario(selectedUsuario);
            System.out.println(usuarioId);





            // Manejo del JComboBox clientes
            String clienteSelec = (String) clientes.getSelectedItem();
            int clienteIdSelec = op.obtenerIDCliente(clienteSelec);
            System.out.println(clienteIdSelec);




            float subTT = 0;
            float IVA = 0;
            float factT = 0;
            try {
                subTT = Float.parseFloat(subt);
                IVA = Float.parseFloat(iva);
                factT = Float.parseFloat(totalF);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }

            boolean estado =  op.ingresarFacturaCabecera(numm, num, fechaa, (int) subTT, (int) IVA, (int) factT,clienteIdSelec,usuarioId);
            if (estado) {
                JOptionPane.showMessageDialog(this, "Factura ingresada correctamente.");
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Error al ingresar la factura.");
            }
        } else if (e.getSource() == servicios) {
            String nombreServicio = (String) servicios.getSelectedItem();
            if (nombreServicio != null && !nombreServicio.isEmpty()) {
                int precioUnitario = op.obtenerPrecioUnitario(nombreServicio);
                String precioUnitarioStr = Integer.toString(precioUnitario);
                //txtPrecioU.setText(precioUnitarioStr);
            } else {
                //txtPrecioU.setText("0");
            }
        } else if (e.getSource() == botCancelar) {
            this.setVisible(false);
        }
    }
}
