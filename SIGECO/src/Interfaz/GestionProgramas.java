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

//import Recursos.Recursos;

/**
 * @author practicante
 */
public class GestionProgramas extends JDialog implements ActionListener {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton agregar, modificar, eliminar, refrescar;
    
    private Connection conexion;
    private int usuarioId;
    private JFrame principal;
    

    public GestionProgramas(JFrame principal, Connection conexion, int usuarioId) {
        super(principal,"Gestión de Programas",true);
        this.principal = principal;
        this.conexion = conexion;
        this.usuarioId = usuarioId;

        //Recursos.cargarIcono(this, 64, 64);
        
        //setTitle("Gestión de Costos");
        setSize(900, 400);
        setLocationRelativeTo(principal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Modelo de la tabla
        modelo = new DefaultTableModel(new String[]{
            "ID","Proyecto_ID", "Descripcion", "Complejidad","Costo Total", "Usuario"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo se pueden editar columnas específicas
                return false;
            }
        };

        // Configuración de la tabla
        tabla = new JTable(modelo);
        // Ocultar la columna Proyecto_ID
        tabla.getColumnModel().getColumn(1).setMinWidth(0);
        tabla.getColumnModel().getColumn(1).setMaxWidth(0);
        tabla.getColumnModel().getColumn(1).setWidth(0);
        // Usuario visible
        tabla.getColumnModel().getColumn(9).setPreferredWidth(150);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones
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

    
     //Carga los datos desde la base de datos en la tabla.
    public void cargarDatos() {
        modelo.setRowCount(0);
        
        String sql = """
                     SELECT  c.id, p.id AS proyecto_id, p.descripcion co.nivel,
                     c.costo_proyecto,
                     CONCAT(u.nombre, ' ', u.apellido) AS usuario
                     FROM costos c
                     LEFT JOIN proyecto p ON c.proyecto_id = p.id
                     LEFT JOIN usuarios u ON c.usuarios_id = u.id
                     LEFT JOIN complejidad co ON p.complejidad_id = co.id
            """;
        try (PreparedStatement ps = conexion.prepareStatement(sql);
            //ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery()){

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("proyecto_id"),
                    rs.getString("proyecto"),
                    //rs.getInt("cantidad_programadores"),
                    rs.getString("nombre_modulo"),
                    rs.getString("complejidad"),
                    rs.getDouble("horas_estimadas"),
                    rs.getDouble("costo_por_hora"),
                    rs.getDouble("costo_total"),
                    rs.getString("usuario")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refrescar) {
            cargarDatos();
        }

        if (e.getSource() == agregar) {
            IngresoProyecto ip = new IngresoProyecto(principal, conexion, usuarioId, this);
            //setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            //ip.setAlwaysOnTop(true);
            ip.setLocationRelativeTo(this);
            ip.setVisible(true);
        }

        if (e.getSource() == modificar) {
            guardarCambios();
        }

        if (e.getSource() == eliminar) {
            eliminarRegistro();
        }
    }

    private void guardarCambios() {
        try {
            for (int i = 0; i < modelo.getRowCount(); i++) {
                int id = (int) modelo.getValueAt(i, 0);
                int cantidad =  Integer.parseInt(modelo.getValueAt(i, 3).toString());
                String modulo = (String) modelo.getValueAt(i, 4);
                String complejidad = (String) modelo.getValueAt(i, 5);
                double horas = Double.parseDouble(modelo.getValueAt(i, 6).toString());
                double costoHora = Double.parseDouble(modelo.getValueAt(i, 7).toString());

                String sql = "UPDATE costos SET nombre_modulo=?, complejidad=?, cantidad_programadores=?, horas_estimadas=?, costo_por_hora=? WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, modulo);
                ps.setString(2, complejidad);
                ps.setInt(3, cantidad);
                ps.setDouble(4, horas);
                ps.setDouble(5, costoHora);
                ps.setInt(6, id);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Cambios guardados correctamente");
            cargarDatos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar cambios: " + ex.getMessage());
        }
    }

    private void eliminarRegistro() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int costoId = (int) modelo.getValueAt(fila, 0);
            int proyectoId = (int) modelo.getValueAt(fila, 1);
            try {
                // Eliminar el costo
                String sqlCosto = "DELETE FROM costos WHERE id=?";
                PreparedStatement psCosto = conexion.prepareStatement(sqlCosto);
                psCosto.setInt(1, costoId);
                psCosto.executeUpdate();

                // Verificar si el proyecto tiene otros costos
                String sqlVerificar = "SELECT COUNT(*) AS total FROM costos WHERE proyecto_id=?";
                PreparedStatement psVerificar = conexion.prepareStatement(sqlVerificar);
                psVerificar.setInt(1, proyectoId);
                ResultSet rs = psVerificar.executeQuery();
                if (rs.next() && rs.getInt("total") == 0) {
                    // Si no hay más costos, eliminar proyecto
                    String sqlEliminarProyecto = "DELETE FROM proyectos WHERE id=?";
                    PreparedStatement psEliminarProyecto = conexion.prepareStatement(sqlEliminarProyecto);
                    psEliminarProyecto.setInt(1, proyectoId);
                    psEliminarProyecto.executeUpdate();
                }

                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar");
        }
    }
}