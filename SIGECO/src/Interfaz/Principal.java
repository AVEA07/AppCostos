/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import InicioSesion.*;
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
    private JMenuBar barra; private JMenu menu; private JMenuItem cerrarSesion,calcularCostos;
    
    public Principal(){
        setTitle("Aplicacion de Costos");
        setSize(900,700);
        setLocationRelativeTo(null);
        inicio();
        //setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void inicio(){
        contenedor = getContentPane();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        barra= new JMenuBar();
        menu = new JMenu("Opciones");
        cerrarSesion = new JMenuItem("cerrarSesion");
        cerrarSesion.addActionListener(this);
        calcularCostos = new JMenuItem("Calcular Costos");
        calcularCostos.addActionListener(this);
        menu.add(cerrarSesion);
        menu.addSeparator();
        menu.add(calcularCostos);
        barra.add(Box.createHorizontalGlue());
        barra.add(menu);
        setJMenuBar(barra);
        
        
        c.insets = new Insets(5, 5, 5, 5);
        //c.insets = new Insets(5, 5, 5, 5);
        //c.gridy = 0; c.gridx = 0; c.gridwidth = 2; c.gridheight = 1;
        c.gridy = 0; c.gridx = 0; c.gridwidth = 2;
        JPanel panelTitulo = new JPanel();
        JLabel titulo = new JLabel("Aplicación Costos");
        panelTitulo.add(titulo);
        contenedor.add(panelTitulo,c);
        c.gridwidth = 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cerrarSesion){
            InicioSesion is = new InicioSesion();
            is.setVisible(true);
            this.dispose();
        }
        
        if(e.getSource() == calcularCostos){
            CalculadoraCostos cc = new CalculadoraCostos();
            cc.setVisible(true);
        }
        
    }
}
