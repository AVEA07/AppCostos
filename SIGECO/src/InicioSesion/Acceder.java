/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package InicioSesion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Interfaz.Principal;
/**
 *
 * @author practicante
 */
public class Acceder extends JFrame implements ActionListener{
    Container contenedor ;
    JButton ingresar, cancelar;
    
    public Acceder(){
        setTitle("Inicio de Sesion");
        setSize(600,400);
        inicio();
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void inicio(){
        contenedor = getContentPane();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2; c.gridheight = 1;
        JPanel panelTitulo = new JPanel();
        JLabel titulo = new JLabel("Inicie sesión con su usuario");
        panelTitulo.add(titulo,c);
        contenedor.add(panelTitulo,c);
        
        c.gridy = 1;
        JPanel panelBotones = new JPanel();
        ingresar = new JButton("Ingresar");
        ingresar.addActionListener(this);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(ingresar);
        panelBotones.add(cancelar);
        contenedor.add(panelBotones,c);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelar){
            InicioSesion in = new InicioSesion();
            in.setVisible(true);
            this.dispose();
        }
        
        if(e.getSource() == ingresar){
            Principal pr = new Principal();
            pr.setVisible(true);
            this.dispose();
        }
    }
}
