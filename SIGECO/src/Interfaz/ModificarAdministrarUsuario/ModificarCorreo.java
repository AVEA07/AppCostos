/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz.ModificarAdministrarUsuario;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

import InicioSesion.Acceder;
/**
 *
 * @author practicante
 */
public class ModificarCorreo extends JDialog implements ActionListener{
    private Container contenedor;
    private JTextField campoCorreo;
    private JButton cancelar,cambiar;
    private int usuarioId;
    private Connection conexion;
    
    private JDialog administrarUsuario;
    private JFrame principal;
    
    public ModificarCorreo(JDialog administrarUsuario,JFrame principal,Connection conexion, int usuarioId){
        super(administrarUsuario,"Cambiar Nombre de Usuario", true);
        this.administrarUsuario = administrarUsuario;
        this.principal = principal;
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        setSize(350,250);
        setLocationRelativeTo(administrarUsuario);
        setResizable(false);
        
        
        inicio();
    }
    
    private void inicio(){
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.CENTER;
        
        c.gridy = 0; c.gridx = 0; //c.gridwidth = 1;
        JLabel titulo = new JLabel("Ingresar nuevo correo");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        contenedor.add(titulo,c);
        c.gridy = 1;
        campoCorreo = new JTextField(15);
        campoCorreo.setFont(new Font("Arial", Font.PLAIN, 14));
        contenedor.add(campoCorreo,c);
        
        
        c.gridy = 2;
        JPanel panelBotones = new JPanel();
        cancelar = new JButton("cancelar");
        cancelar.addActionListener(this);
        cambiar = new JButton("cambiar");
        cambiar.addActionListener(this);
        panelBotones.add(cancelar);
        panelBotones.add(cambiar);
        contenedor.add(panelBotones,c);
        
    }
    
    
    @Override
    public void actionPerformed (ActionEvent e){
            if(e.getSource() == cancelar){
                this.dispose();
            }
            
            if(e.getSource() == cambiar){
                String nuevoCorreo = campoCorreo.getText().trim();
                if(!nuevoCorreo.isEmpty()){
                    try{
                       PreparedStatement ps = conexion.prepareStatement("UPDATE usuarios SET correo = ? WHERE id = ?");
                       ps.setString(1, nuevoCorreo);
                       ps.setInt(2, usuarioId);
                       ps.executeUpdate();
                       ps.close();
                       JOptionPane.showMessageDialog(this, "Correo Actualizado");
                       dispose();
                       principal.dispose();
                       //principal.setVisible(true);
                       Acceder ac= new Acceder(null);
                       ac.setVisible(true);
                       
                    }catch(SQLException ex){
                       JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "Debe llenar el campo");
                }
            }
        
    }
}
