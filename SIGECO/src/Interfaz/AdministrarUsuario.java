/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import Recursos.Recursos;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;




/**
 *
 * @author practicante
 */
public class AdministrarUsuario  extends JFrame implements ActionListener{
    private Container contenedor;
    private JComboBox box;
    private JButton regresar;
    
    public AdministrarUsuario(){
        setTitle("Inicio de Sesión");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        Recursos.cargarIcono(this, 64, 64);
        //conectarDB();
        inicio();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void inicio(){
        contenedor = getContentPane();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
    }
}
