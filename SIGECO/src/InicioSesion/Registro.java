/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package InicioSesion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author practicante
 */
public class Registro extends JFrame implements ActionListener{
    private Container contenedor;
    private JTextField campoNombre, campoApellido, campoUsuario, campoCorreo;
    private JPasswordField campoContraseña;
    private JComboBox<String> comboRango;
    private JButton registrar, cancelar;
    
    private Connection conexion;
    private ArrayList<Integer> rangoIds = new ArrayList<>();
    
    public Registro(){
        setTitle("Registro");
        setSize(600,400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Imagenes/SIGECO - BCG.png"));
        Image iconoEscalado = iconoOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        setIconImage(iconoEscalado);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        conectarDB();
        inicio();
    }
    
    private void conectarDB() {
        try{
            String url = "jdbc:mysql://localhost:3306/SIGECO";
            String user = "practicante";
            String pass = "Angel2007";
            conexion = DriverManager.getConnection(url, user, pass);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + ex.getMessage());
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
        JLabel titulo = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        contenedor.add(titulo, c);
        
        JPanel panelNA = new JPanel(new GridLayout(2,2,5,5));
        campoNombre = new JTextField(10);
        campoApellido = new JTextField(10);
        
        panelNA.add(new JLabel ("Nombres"));
        panelNA.add(new JLabel ("Apellidos"));
        panelNA.add(campoNombre);
        panelNA.add(campoApellido);
        
        c.gridy = 1;
        c.gridwidth = 2;
        contenedor.add(panelNA,c);
        
        c.gridwidth = 1;
        c.gridy=2; c.gridx=0;
        contenedor.add(new JLabel("Usuario"),c);
        c.gridx=1;
        campoUsuario = new JTextField(20);
        contenedor.add(campoUsuario,c);
        
        c.gridy=3; c.gridx = 0;
        contenedor.add(new JLabel("Contraseña"),c);
        c.gridx = 1;
        campoContraseña = new JPasswordField(20);
        contenedor.add(campoContraseña,c);
        
        c.gridy = 4; c.gridx = 0;
        contenedor.add(new JLabel("Correo"),c);
        c.gridx = 1;
        campoCorreo = new JTextField(20);
        contenedor.add(campoCorreo,c);
        
        c.gridy = 5; c.gridx = 0;
        contenedor.add(new JLabel("Rangos"),c);
        c.gridx = 1;
        comboRango = new JComboBox<>();
        cargarRangos();
        contenedor.add(comboRango,c);
        
        
        
        c.gridy = 6; c.gridx = 0; c.gridwidth = 2;
        JPanel panelBotones = new JPanel();
        registrar = new JButton("Registrar");
        registrar.addActionListener(this);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(registrar);
        panelBotones.add(cancelar);
        contenedor.add(panelBotones,c);
    }
    
    public void cargarRangos(){
        try{
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nombre_rango FROM rangos ORDER BY nivel");
            while (rs.next()){
                rangoIds.add(rs.getInt("id"));
                comboRango.addItem(rs.getString("nombre_rango"));
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(this,"Error al cargar Rangos: " + ex.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelar){
            InicioSesion is = new InicioSesion();
            is.setVisible(true);
            this.dispose();
        }
        
        if(e.getSource() == registrar){
            
            
            String nombre = campoNombre.getText();
            String apellido = campoApellido.getText();
            String usuario = campoUsuario.getText();
            String correo = campoCorreo.getText();
            String contrasena = new String (campoContraseña.getPassword());
            int rangoId = rangoIds.get(comboRango.getSelectedIndex());
            
            if(nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty()){
                JOptionPane.showMessageDialog(this, "Llenar todos los campos");
                return;
            }
            
            String hash = BCrypt.hashpw(contrasena,BCrypt.gensalt());
            
            try {
                String sql = "INSERT INTO usuarios(nombre, apellido, usuario, contrasena, rango_id, correo) VALUES (?,?,?,?,?,?)";
                PreparedStatement ps= conexion.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, apellido);
                ps.setString(3, usuario);
                ps.setString(4, hash);
                ps.setInt(5, rangoId);
                ps.setString(6, correo.isEmpty() ? null : correo);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Usuario Registrado");
                InicioSesion is = new InicioSesion();
                is.setVisible(true);
                this.dispose();
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(this, "Error al registrar usuario: "+ex.getMessage());
            }
            
            
            
            JOptionPane.showMessageDialog(this, "Cuenta Registrada","Information",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
