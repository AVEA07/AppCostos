/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
/**
 *
 * @author practicante
 */
public class GestionCostos extends JFrame implements ActionListener{
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton agregar, modificar, eliminar, refrescar;
    private Connection conexion;
    private int usuarioId;
    
    public GestionCostos(Connection conexion, int usuarioId){
        this.conexion = conexion;
        this.usuarioId = usuarioId;
        setTitle("Gestion de Costos");
        setSize(800,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        inicio();
        cargarDatos();
    }
    
    private void inicio(){
        setLayout(new BorderLayout());
        
        modelo = new DefaultTableModel(new String[]{
            "ID", "Proyecto", "Modulo", "Complejidad", "Horas Estimadas", "Costo/Hora", "Total"
        },0){
            @Override
            public boolean isCellEditable(int now, int column){
                return column != 0 && column !=6;
            }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        agregar = new JButton("Agregar");
        modificar = new JButton("Guardar Cambios");
        eliminar = new JButton("Eliminar");
        refrescar = new JButton("Refrescar");

        agregar.addActionListener(this);
        modificar.addActionListener(this);
        eliminar.addActionListener(this);
        refrescar.addActionListener(this);

        panelBotones.add(agregar);
        panelBotones.add(modificar);
        panelBotones.add(eliminar);
        panelBotones.add(refrescar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        modelo.setRowCount(0);
        try {
            String sql = "SELECT c.id, p.nombre, c.nombre_modulo, c.complejidad, c.horas_estimadas, c.costo_por_hora, c.costo_total " +
                         "FROM costos c LEFT JOIN proyectos p ON c.proyecto_id = p.id WHERE c.usuario_id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("nombre_modulo"),
                    rs.getString("complejidad"),
                    rs.getDouble("horas_estimadas"),
                    rs.getDouble("costo_por_hora"),
                    rs.getDouble("costo_total")
                });
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == refrescar) {
            cargarDatos();
        }

        if(e.getSource() == agregar) {
            String proyecto = JOptionPane.showInputDialog(this, "Nombre del proyecto:");
            String modulo = JOptionPane.showInputDialog(this, "Nombre del módulo:");
            String complejidad = JOptionPane.showInputDialog(this, "Complejidad (baja, media, alta):");
            double horas = Double.parseDouble(JOptionPane.showInputDialog(this, "Horas estimadas:"));
            double costoHora = Double.parseDouble(JOptionPane.showInputDialog(this, "Costo por hora:"));

            try {
                String sqlProyecto = "SELECT id FROM proyectos WHERE nombre = ?";
                PreparedStatement ps = conexion.prepareStatement(sqlProyecto);
                ps.setString(1, proyecto);
                ResultSet rs = ps.executeQuery();
                int proyectoId;
                if(rs.next()) {
                    proyectoId = rs.getInt("id");
                } else {
                    String insertProyecto = "INSERT INTO proyectos(nombre, usuario_id) VALUES (?, ?)";
                    PreparedStatement psInsert = conexion.prepareStatement(insertProyecto, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setString(1, proyecto);
                    psInsert.setInt(2, usuarioId);
                    psInsert.executeUpdate();
                    ResultSet keys = psInsert.getGeneratedKeys();
                    keys.next();
                    proyectoId = keys.getInt(1);
                }

                String sql = "INSERT INTO costos(proyecto_id, nombre_modulo, complejidad, horas_estimadas, costo_por_hora, usuario_id) VALUES (?,?,?,?,?,?)";
                PreparedStatement psInsert = conexion.prepareStatement(sql);
                psInsert.setInt(1, proyectoId);
                psInsert.setString(2, modulo);
                psInsert.setString(3, complejidad);
                psInsert.setDouble(4, horas);
                psInsert.setDouble(5, costoHora);
                psInsert.setInt(6, usuarioId);
                psInsert.executeUpdate();
                JOptionPane.showMessageDialog(this, "Costo agregado correctamente");
                cargarDatos();
            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage());
            }
        }

        if(e.getSource() == modificar) {
            try {
                for(int i=0; i<modelo.getRowCount(); i++) {
                    int id = (int) modelo.getValueAt(i, 0);
                    String modulo = (String) modelo.getValueAt(i, 2);
                    String complejidad = (String) modelo.getValueAt(i, 3);
                    double horas = (double) modelo.getValueAt(i, 4);
                    double costoHora = (double) modelo.getValueAt(i, 5);

                    String sql = "UPDATE costos SET nombre_modulo=?, complejidad=?, horas_estimadas=?, costo_por_hora=? WHERE id=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, modulo);
                    ps.setString(2, complejidad);
                    ps.setDouble(3, horas);
                    ps.setDouble(4, costoHora);
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Cambios guardados correctamente");
                cargarDatos();
            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar cambios: " + ex.getMessage());
            }
        }

        if(e.getSource() == eliminar) {
            int fila = tabla.getSelectedRow();
            if(fila >= 0) {
                int id = (int) modelo.getValueAt(fila, 0);
                try {
                    String sql = "DELETE FROM costos WHERE id=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    cargarDatos();
                    JOptionPane.showMessageDialog(this, "Registro eliminado");
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar");
            }
        }
    }
}
    

