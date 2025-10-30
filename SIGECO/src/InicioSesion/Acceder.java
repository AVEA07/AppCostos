/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package InicioSesion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

import Interfaz.Principal;
/**
 *
 * @author practicante
 */
public class Acceder extends JDialog implements ActionListener{
    private Container contenedor ;
    private JTextField campoUsuario;
    private JPasswordField campoContraseña;
    private JButton ingresar, cancelar;
    private Connection conexion;
    
    private JFrame inicioSesion;
    
    public Acceder(JFrame inicioSesion){
        super(inicioSesion, "Inicio de Sesión", true);
        this.inicioSesion = inicioSesion;
        //setTitle("Inicio de Sesión");
        setSize(400,250);
        setLocationRelativeTo(inicioSesion);
        setResizable(false);
        
        //Recursos.cargarIcono(this, 64, 64);
        conectarDB();
        inicio();
    }
    
    private void conectarDB(){
        try{
            String url = "jdbc:mysql://localhost:3306/SIGECO";
            String user = "practicante";
            String pass = "Angel2007";
            conexion = DriverManager.getConnection(url,user,pass);
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(inicioSesion, "Error al cocnectar con la base de datos" + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
            this.dispose();
        }
    }
    
    private void inicio(){
        contenedor = getContentPane();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2;
        JLabel titulo = new JLabel("Inicie sesión con su usuario",SwingConstants.CENTER);
        titulo.setFont(new Font("Arial",Font.BOLD,16));
        contenedor.add(titulo,c);
        c.gridwidth = 1;c.gridy = 1;
        contenedor.add(new JLabel ("Usuario"),c);
        c.gridx = 1;
        campoUsuario = new JTextField(15);
        contenedor.add(campoUsuario,c);
        
        c.gridy = 2; c.gridx = 0;
        contenedor.add(new JLabel("Contraseña"),c);
        c.gridx = 1;
        campoContraseña = new JPasswordField(15);
        contenedor.add(campoContraseña,c);
        
        c.gridy = 3; c.gridx = 0; c.gridwidth = 2;
        JPanel panelBotones = new JPanel();
        ingresar = new JButton("Ingresar");
        ingresar.addActionListener(this);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(cancelar);
        panelBotones.add(ingresar);
        contenedor.add(panelBotones,c);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelar){
            //InicioSesion is = new InicioSesion();
            this.dispose();
        }
        
        if(e.getSource() == ingresar){
            validarUsuario();
        }
    }
    
    private void validarUsuario(){
        String usuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContraseña.getPassword());
        
        if(usuario.isEmpty() || contrasena.isEmpty()){
            JOptionPane.showMessageDialog(inicioSesion, "Completar todos los campos");
            return;
        }
        
        try{
            String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE BINARY usuario = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                String hash = rs.getString("contrasena");
                if(BCrypt.checkpw(contrasena, hash)){
                    // Obtenemos el nombre del usuario
                    String nombreUsuario = rs.getString("usuario");
                    int idUsuarios = rs.getInt("id");
                    Principal pr = new Principal(nombreUsuario ,conexion, idUsuarios);
                    pr.setVisible(true);
                    
                    if(inicioSesion != null && inicioSesion.isDisplayable()){
                        inicioSesion.dispose();
                        this.dispose();
                    }else{
                        this.dispose();   
                    }
                } else {
                    JOptionPane.showMessageDialog(inicioSesion, "Contraseña incorrecta");
                }
            }else{
                JOptionPane.showMessageDialog(inicioSesion, "Usuario no encontrado");
            }
            
            rs.close();
            ps.close();
            this.dispose();
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(inicioSesion, "Error al validar usuario: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
