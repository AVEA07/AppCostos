
package InicioSesion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author practicante
 */
public class InicioSesion extends JFrame implements ActionListener{
    private Container contenedor ;
    private JButton acceder, registrar;
    
    public InicioSesion(){
        setTitle("Inicio de Sesión");
        setSize(400,300);
        setLocationRelativeTo(null);
        inicio();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setVisible(true);
    }
    
    private void inicio(){
        contenedor = getContentPane();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2; c.gridheight = 1;
        
        JPanel panelTitulo = new JPanel();
        JLabel titulo = new JLabel("Inicio de sesion");
        panelTitulo.add(titulo);
        contenedor.add(panelTitulo,c);
        
        JPanel panelBotones = new JPanel();
        c.gridy = 1;
        acceder = new JButton("Igresar");
        acceder.addActionListener(this);
        registrar = new JButton("Registrar");
        registrar.addActionListener(this);
        panelBotones.add(acceder);
        panelBotones.add(registrar);
        contenedor.add(panelBotones,c);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == acceder){
            Acceder ac = new Acceder();
            ac.setVisible(true);
            this.dispose();
        }
        if(e.getSource() == registrar){
            Registro re = new Registro();
            re.setVisible(true);
            this.dispose();
        }
    }
}
