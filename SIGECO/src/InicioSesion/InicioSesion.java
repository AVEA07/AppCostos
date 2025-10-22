
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
        setTitle("Inicio");
        setSize(400,300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Imagenes/SIGECO - BCG.png"));
        Image iconoEscalado = iconoOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        setIconImage(iconoEscalado);
        
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
        JLabel titulo = new JLabel("Bienvenido a SIGECO");
        titulo.setFont(new Font("Arial",Font.BOLD,16));
        panelTitulo.add(titulo);
        contenedor.add(panelTitulo,c);
        
        c.gridy = 1;
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Imagenes/SIGECO - BCG.png"));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imagenEscalada);
        JLabel logo = new JLabel(icono);
        contenedor.add(logo, c);
        
        JPanel panelBotones = new JPanel();
        c.gridy = 2;
        acceder = new JButton("Acceder");
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
