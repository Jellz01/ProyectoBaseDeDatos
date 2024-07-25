import java.util.Properties;

public class ControladorUsuarios {

    private int opcion;
    public Properties properties;

    public ControladorUsuarios(int opcion, String usuarioU){

        this.opcion=opcion;

        if(opcion == 1){
            ViewMenuPrincipal vmp = new ViewMenuPrincipal();
            vmp.menu(vmp,usuarioU);
        }
    }
}
