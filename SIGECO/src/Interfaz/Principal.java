/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

import InicioSesion.*;
import Recursos.Recursos;
/**
 *
 * @author practicante
 */
public class Principal extends JFrame implements ActionListener{
    /*
    Programador: programadores, freelancers, horas, tecnologias
    Almacenamiento: host, tamaño y volumen de datos, rendimiento, seguridad, datacenter
    Documentacion: desarrolladores, validacion, escritura tecnica
    Capacitacion: numero de usuarios, duracion, modalidad, seguimiento
    */

    private Container contenedor;
    private JTextField campoProg, campoHoras, campoTec;
    private JMenuBar barra; private JMenu menu; private JMenuItem cerrarSesion,ingresoProyecto, gestionarCostos,administrarUsuario;
    private JLabel titulo;
    private String nombreUsuario;
    private Connection conexion;
    private int usuarioId;
    
    public Principal(String nombreUsuario, Connection conexion, int usuarioId){
        this.nombreUsuario = nombreUsuario;
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        setTitle("SIGECO");
        setSize(900,700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        Recursos.cargarIcono(this, 64, 64);
        
        inicio();
        //setVisible(true);
        //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void inicio(){
        contenedor = getContentPane();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        barra= new JMenuBar();
        menu = new JMenu("Opciones");
        cerrarSesion = new JMenuItem("Cerrar Sesión");
        cerrarSesion.addActionListener(this);
        ingresoProyecto = new JMenuItem("IngresarProyecto");
        ingresoProyecto.addActionListener(this);
        gestionarCostos = new JMenuItem("Gestionar Programas");
        gestionarCostos.addActionListener(this);
        administrarUsuario = new JMenuItem("Administrar Usuario");
        administrarUsuario.addActionListener(this);
        menu.add(administrarUsuario);
        menu.addSeparator();
        menu.add(cerrarSesion);
        menu.addSeparator();
        menu.add(ingresoProyecto);
        menu.add(gestionarCostos);
        barra.add(Box.createHorizontalGlue());
        barra.add(menu);
        setJMenuBar(barra);
        
        c.insets = new Insets(5, 5, 5, 5);
        //c.insets = new Insets(5, 5, 5, 5);
        //c.gridy = 0; c.gridx = 0; c.gridwidth = 2; c.gridheight = 1;
        c.gridy = 0; c.gridx = 0; //c.gridwidth = 2;
        JLabel titulo = new JLabel("Bienenido "+nombreUsuario+" al Sistema de Gestión de Costos SIGECO");
        titulo.setFont(new Font("Arial",Font.BOLD,16));
        contenedor.add(titulo,c);
        
        c.gridy = 1;
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Imagenes/SIGECO - BCG - CQ - 2D.png"));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imagenEscalada);
        JLabel logo = new JLabel(icono);
        contenedor.add(logo, c);
        
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if(e.getSource() == administrarUsuario){
        AdministrarUsuario au = new AdministrarUsuario(this, conexion, usuarioId);
        au.setVisible(true);
    }
    
    if(e.getSource() == cerrarSesion){
        CierreSesion ci = new CierreSesion(this);
        ci.setVisible(true);
    }
    
    if(e.getSource() == ingresoProyecto){
        // Aquí pasamos null como padre porque no hay ventana GestionCostos abierta
        IngresoProyecto ip = new IngresoProyecto(this, conexion, usuarioId, null);
        //ip.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        //ip.setAlwaysOnTop(true);
        ip.setVisible(true);
    }
    
    if(e.getSource() == gestionarCostos){
        GestionProgramas gc = new GestionProgramas(this, conexion, usuarioId);
        //gc.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        //gc.setAlwaysOnTop(true);
        gc.setVisible(true);
    }
}
}
