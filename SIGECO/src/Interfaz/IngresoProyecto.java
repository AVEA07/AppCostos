/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

//import Recursos.Recursos;

/**
 *
 * @author practicante
 */

public class IngresoProyecto extends JDialog implements ActionListener {
    private JTextField campoProyecto;
    private JTextArea campoDescripcion;
    private JButton guardar, cancelar;
    private Container contenedor;

    private Connection conexion;
    private int usuarioId;
    private GestionProgramas gp;


    public IngresoProyecto(JFrame padre, Connection conexion, int usuarioId, GestionProgramas gp) {
        super(padre, "Ingreso de Proyecto", true);
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        this.gp = gp;

        setSize(500, 400);
        setLocationRelativeTo(padre);
        setResizable(false);

        inicio();
    }

    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        contenedor.add(new JLabel("Nombre del Proyecto:"), c);
        campoProyecto = new JTextField(15);
        c.gridx = 1;
        contenedor.add(campoProyecto, c);
        
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        campoDescripcion = new JTextArea(10, 15);
        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(campoDescripcion);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contenedor.add(scroll,c);

        JPanel panelBotones = new JPanel();
        cancelar = new JButton("Cancelar");
        guardar = new JButton("Guardar");
        cancelar.addActionListener(this);
        guardar.addActionListener(this);
        panelBotones.add(cancelar);
        panelBotones.add(guardar);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        contenedor.add(panelBotones, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelar){
            this.dispose();
        }

        if (e.getSource() == guardar) {
            try {
                String nombreProyecto = campoProyecto.getText().trim();
                String descripcionProyecto = campoDescripcion.getText().trim();
                
                if(nombreProyecto.isEmpty() || descripcionProyecto.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
                    return;
                }
                
                int proyectoId;
                String sqlProyecto = "SELECT id FROM proyecto WHERE nombre_proyecto = ?";
                PreparedStatement psProyecto = conexion.prepareStatement(sqlProyecto);
                psProyecto.setString(1, nombreProyecto);
                ResultSet rs = psProyecto.executeQuery();

                if (rs.next()) {
                    proyectoId = rs.getInt("id");
                } else {
                    String insertProyecto = "INSERT INTO proyecto (nombre_proyecto, descripcion) VALUES (?,?)";
                    PreparedStatement psInsert = conexion.prepareStatement(insertProyecto, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setString(1, nombreProyecto);
                    psInsert.setString(2, descripcionProyecto);
                    psInsert.executeUpdate();
                    ResultSet keys = psInsert.getGeneratedKeys();
                    keys.next();
                    proyectoId = keys.getInt(1);
                }
                String sqlCosto = "INSERT INTO costos (proyecto_id) VALUES (?)";
                PreparedStatement psCosto = conexion.prepareStatement(sqlCosto);
                psCosto.setInt(1, proyectoId);
                //psCosto.setDouble(2, costoProyecto);
                psCosto.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registro guardado exitosamente");
                if (gp != null) {
                    gp.cargarDatos();
                }
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error SQL: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "valores numéricos inválidos","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}