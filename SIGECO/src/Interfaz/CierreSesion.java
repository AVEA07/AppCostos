/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import InicioSesion.InicioSesion;

/**
 *
 * @author practicante
 */
public class CierreSesion extends JFrame implements ActionListener{
    private Container contenedor;
    private JButton aceptar, cancelar;
    
    public CierreSesion(){
        setTitle("");
        setSize(350,250);
        setLocationRelativeTo(null);
        inicio();
    }
    
    private void inicio(){
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets (5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridy = 0; c.gridx = 0;
        JLabel titulo = new JLabel("¿Esta Seguro de querer cerrar sesión?");
        titulo.setFont(new Font("Arial",Font.BOLD,16));
        contenedor.add(titulo,c);
        
        c.gridy = 1;
        JPanel panelBotones = new JPanel();
        aceptar = new JButton("Aceptar");
        cancelar = new JButton("Cancelar");
        aceptar.addActionListener(this);
        cancelar.addActionListener(this);
        panelBotones.add(aceptar);
        panelBotones.add(cancelar);
        contenedor.add(panelBotones,c);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == aceptar){
            Principal pr = new Principal();
            InicioSesion in = new InicioSesion();
            pr.dispose();
            this.dispose();
            in.setVisible(true);
        }
        
        if(e.getSource() == cancelar){
            this.dispose();
        }
    }
}
