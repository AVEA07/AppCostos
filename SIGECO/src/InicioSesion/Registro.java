/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package InicioSesion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author practicante
 */
public class Registro extends JFrame implements ActionListener{
    Container contenedor;
    JButton registrar, cancelar;
    
    public Registro(){
        setTitle("Registro");
        setSize(600,400);
        inicio();
        setLocationRelativeTo(null);
    }
    
    public void inicio(){
        contenedor = getContentPane();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2; c.gridheight = 1;
        JPanel panelTitulo = new JPanel();
        JLabel titulo = new JLabel("Registro");
        panelTitulo.add(titulo);
        contenedor.add(panelTitulo,c);
        
        c.gridy = 1;
        JPanel panelBotones = new JPanel();
        registrar = new JButton("Registrar");
        registrar.addActionListener(this);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(registrar);
        panelBotones.add(cancelar);
        contenedor.add(panelBotones,c);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelar){
            InicioSesion is = new InicioSesion();
            is.setVisible(true);
            this.dispose();
        }
        if(e.getSource() == registrar){
            InicioSesion is = new InicioSesion();
            is.setVisible(true);
            this.dispose();
            
            JOptionPane.showMessageDialog(this, "Cuenta Registrada","Information",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
