/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

import Recursos.Recursos;

/**
 *
 * @author practicante
 */

public class CalculadoraCostos extends JFrame implements ActionListener {
    private JComboBox<String> tipoBox;
    private JComboBox<String> techBox;
    private JTextField campoProyecto, campoModulo, campoCantidad, campoHoras, campoTarifa;
    private JComboBox<String> complejidadBox;
    private JButton calcular, guardar;
    private JLabel resultadoLabel;
    private Container contenedor;
    private double total = 0.0;

    private Connection conexion;
    private int usuarioId;
    private GestionCostos padre;

    public CalculadoraCostos(Connection conexion, int usuarioId, GestionCostos padre) {
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        this.padre = padre;

        setTitle("Calculadora de Costos");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        Recursos.cargarIcono(this, 64, 64);
        inicio();
    }

    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        contenedor.add(new JLabel("Proyecto:"), c);
        campoProyecto = new JTextField(20);
        c.gridx = 1;
        contenedor.add(campoProyecto, c);

        c.gridx = 0; c.gridy = 1;
        contenedor.add(new JLabel("Módulo:"), c);
        campoModulo = new JTextField(20);
        c.gridx = 1;
        contenedor.add(campoModulo, c);

        c.gridx = 0; c.gridy = 2;
        contenedor.add(new JLabel("Complejidad:"), c);
        complejidadBox = new JComboBox<>(new String[]{"baja", "media", "alta"});
        c.gridx = 1;
        contenedor.add(complejidadBox, c);

        c.gridx = 0; c.gridy = 3;
        contenedor.add(new JLabel("Cantidad de programadores:"), c);
        campoCantidad = new JTextField(20);
        c.gridx = 1;
        contenedor.add(campoCantidad, c);

        c.gridx = 0; c.gridy = 4;
        contenedor.add(new JLabel("Horas estimadas por programador:"), c);
        campoHoras = new JTextField(20);
        c.gridx = 1;
        contenedor.add(campoHoras, c);

        c.gridx = 0; c.gridy = 5;
        contenedor.add(new JLabel("Costo por hora (USD):"), c);
        campoTarifa = new JTextField(20);
        c.gridx = 1;
        contenedor.add(campoTarifa, c);

        JPanel panelBotones = new JPanel();
        calcular = new JButton("Calcular");
        guardar = new JButton("Guardar");
        calcular.addActionListener(this);
        guardar.addActionListener(this);
        panelBotones.add(calcular);
        panelBotones.add(guardar);
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        contenedor.add(panelBotones, c);

        resultadoLabel = new JLabel("Total: 0.00 USD");
        c.gridy = 7;
        contenedor.add(resultadoLabel, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calcular) {
            try {
                int cantidad = Integer.parseInt(campoCantidad.getText().trim());
                double horas = Double.parseDouble(campoHoras.getText().trim());
                double tarifa = Double.parseDouble(campoTarifa.getText().trim());

                total = cantidad * horas * tarifa;
                resultadoLabel.setText("Total: " + String.format("%.2f USD", total));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Introduce números válidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == guardar) {
            try {
                String proyecto = campoProyecto.getText().trim();
                String modulo = campoModulo.getText().trim();
                String complejidad = (String) complejidadBox.getSelectedItem();
                double horas = Double.parseDouble(campoHoras.getText().trim());
                double costoHora = Double.parseDouble(campoTarifa.getText().trim());
                int cantidad = Integer.parseInt(campoCantidad.getText().trim());

                // Verificar si el proyecto ya existe o insertarlo
                int proyectoId;
                String sqlProyecto = "SELECT id FROM proyectos WHERE nombre = ? AND usuarios_id = ?";
                PreparedStatement psProyecto = conexion.prepareStatement(sqlProyecto);
                psProyecto.setString(1, proyecto);
                psProyecto.setInt(2, usuarioId);
                ResultSet rs = psProyecto.executeQuery();

                if (rs.next()) {
                    proyectoId = rs.getInt("id");
                } else {
                    String insertProyecto = "INSERT INTO proyectos (nombre, usuarios_id) VALUES (?, ?)";
                    PreparedStatement psInsert = conexion.prepareStatement(insertProyecto, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setString(1, proyecto);
                    psInsert.setInt(2, usuarioId);
                    psInsert.executeUpdate();
                    ResultSet keys = psInsert.getGeneratedKeys();
                    keys.next();
                    proyectoId = keys.getInt(1);
                }

                // Insertar el nuevo costo
                String sqlCosto = "INSERT INTO costos (proyecto_id, usuarios_id, nombre_modulo, complejidad, cantidad_programadores, horas_estimadas, costo_por_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psCosto = conexion.prepareStatement(sqlCosto);
                psCosto.setInt(1, proyectoId);
                psCosto.setInt(2, usuarioId);
                psCosto.setString(3, modulo);
                psCosto.setString(4, complejidad);
                psCosto.setInt(5, cantidad);
                psCosto.setDouble(6, horas);
                psCosto.setDouble(7, costoHora);
                psCosto.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registro guardado exitosamente");
                if (padre != null) {
                    padre.cargarDatos();
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