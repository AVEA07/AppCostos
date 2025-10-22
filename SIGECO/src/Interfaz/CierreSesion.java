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
    private JFrame ventanaPrincipal;
    
    public CierreSesion(JFrame ventanaPrincipal){
        this.ventanaPrincipal = ventanaPrincipal;
        setTitle("");
        setSize(350,250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Imagenes/SIGECO - BCG.png"));
        Image iconoEscalado = iconoOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        setIconImage(iconoEscalado);
        
        inicio();
    }
    
    private void inicio(){
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets (5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridy = 0; c.gridx = 0; c.anchor = GridBagConstraints.CENTER;
        JTextArea titulo = new JTextArea("¿Esta Seguro de\nquerer cerrar sesión?");
        titulo.setEditable(false);
        titulo.setOpaque(false);
        titulo.setFont(new Font("Arial",Font.BOLD,16));
        titulo.setFocusable(false);
        titulo.setHighlighter(null);
        titulo.setLineWrap(true);
        titulo.setWrapStyleWord(true);  
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setAlignmentY(Component.CENTER_ALIGNMENT);
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
            InicioSesion in = new InicioSesion();
            ventanaPrincipal.dispose();
            this.dispose();
            in.setVisible(true);
        }
        
        if(e.getSource() == cancelar){
            this.dispose();
        }
    }
}
