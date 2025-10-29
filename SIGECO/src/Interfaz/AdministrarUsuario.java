/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

import Interfaz.ModificarAdministrarUsuario.ModificarUsuario;

/**
 *
 * @author practicante
 */
public class AdministrarUsuario  extends JDialog implements ActionListener{
    private Container contenedor;
    private JButton regresar, cambiarUsuario;
    
    private JLabel nombre, apellido, usuario, contraseña, rango, correo;
    private Connection conexion;
    private int usuarioId;
    
    private JFrame principal;
    
    public AdministrarUsuario(JFrame principal, Connection conexion, int usuarioId){
        super(principal,"Administrar Usuario");
        this.principal = principal;
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        //setTitle("Inicio de Sesión");
        setSize(700, 450);
        setLocationRelativeTo(principal);
        setResizable(false);
        
        //Recursos.cargarIcono(this, 64, 64);
        conectarDB();
        inicio();
    }
    
    private void conectarDB(){
        try{
            String sql = """
                         SELECT u.nombre, u.apellido, u.usuario, u.correo, r.nombre_rango, r.nivel 
                         FROM usuarios u INNER JOIN rangos r ON u.rango_id = r.id 
                         WHERE u.id = ?
                         """;
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                nombre = new JLabel("Nombre: " + rs.getString("nombre"));
                apellido = new JLabel("Apellido: " + rs.getString("apellido"));
                usuario = new JLabel("Nombre Usuario: " + rs.getString("usuario"));
                rango = new JLabel("Rango del Usuario: " + rs.getString("nombre_rango")+", Nivel "+rs.getInt("nivel"));
                correo = new JLabel("Correo Electronico: " + rs.getString("correo"));
            } else {
                JOptionPane.showMessageDialog(principal, "No se encontraron datos del usuario","Aviso",JOptionPane.WARNING_MESSAGE);
                this.dispose();
            }

            
            rs.close();
            ps.close();
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(principal, "Error al cargar datos","Error",JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
    
    private void inicio(){
        contenedor = getContentPane();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2;
        JLabel titulo = new JLabel("Datos del usuario",SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        contenedor.add(titulo,c);
        
        c.gridwidth = 1;
        c.gridy = 1; c.anchor = GridBagConstraints.WEST;
        contenedor.add(usuario,c);
        c.gridx = 1;;
        cambiarUsuario = new JButton("Modificar");
        cambiarUsuario.addActionListener(this);
        contenedor.add(cambiarUsuario,c);
        c.gridy = 2; c.gridx = 0;
        contenedor.add(nombre,c);
        c.gridy = 3;
        contenedor.add(apellido,c);
        c.gridy = 4;
        contenedor.add(rango,c);
        c.gridy = 5;
        contenedor.add(correo,c);
        
        c.gridy = 6; c.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel();
        regresar = new JButton("Regresar");
        regresar.addActionListener(this);
        panelBotones.add(regresar);
        contenedor.add(panelBotones,c);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == regresar){
            this.dispose();
        }
        
        if(e.getSource() == cambiarUsuario){
            
        }
    }
}
