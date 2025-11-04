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
 * Gestión de Programas - versión simplificada y segura.
 * Se eliminan botones peligrosos (Eliminar, Refrescar) y
 * se sustituye "Guardar Cambios" por "Modificar" que abre una ventana
 * dedicada (EditarProyecto). Solo el usuario asignado puede modificar.
 *
 * @author practicante
 */
public class GestionProgramas extends JDialog implements ActionListener {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton agregar, modificar;

    private Connection conexion;
    private int usuarioId;
    private JFrame principal;

    public GestionProgramas(JFrame principal, Connection conexion, int usuarioId) {
        super(principal, "Gestión de Programas", true);
        this.principal = principal;
        this.conexion = conexion;
        this.usuarioId = usuarioId;

        setSize(900, 400);
        setLocationRelativeTo(principal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        inicio();
        cargarDatos();
    }

    private void inicio() {
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new String[]{
                "ID", "Proyecto_ID", "Proyecto", "Descripción", "Complejidad", "Costo Total", "Usuarios"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) return Integer.class;
                if (columnIndex == 5) return Double.class;
                return String.class;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        TableColumnModel cm = tabla.getColumnModel();
        if (cm.getColumnCount() > 1) {
            TableColumn colProyectoId = cm.getColumn(1);
            colProyectoId.setMinWidth(0);
            colProyectoId.setMaxWidth(0);
            colProyectoId.setPreferredWidth(0);
        }

        try {
            if (cm.getColumnCount() > 6) cm.getColumn(6).setPreferredWidth(200);
            if (cm.getColumnCount() > 2) cm.getColumn(2).setPreferredWidth(200);
            if (cm.getColumnCount() > 3) cm.getColumn(3).setPreferredWidth(300);
        } catch (Exception ex) { /* no crítico */ }

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        agregar = new JButton("Agregar");
        modificar = new JButton("Modificar"); // antes "Guardar Cambios"

        agregar.addActionListener(this);
        modificar.addActionListener(this);

        panelBotones.add(agregar);
        panelBotones.add(modificar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga datos desde la base de datos y los muestra en la tabla.
     */
    public void cargarDatos() {
        modelo.setRowCount(0);

        if (conexion == null) {
            JOptionPane.showMessageDialog(this, "Sin conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cursor prev = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modificar.setEnabled(false);
        agregar.setEnabled(false);

        String sql =
                "SELECT c.id AS costo_id, " +
                "       p.id AS proyecto_id, " +
                "       p.nombre_proyecto, " +
                "       p.descripcion, " +
                "       COALESCE(co.nivel, '') AS complejidad, " +
                "       COALESCE(c.costo_proyecto, 0) AS costo_proyecto, " +
                "       COALESCE(GROUP_CONCAT(DISTINCT CONCAT(u.nombre, ' ', u.apellido) SEPARATOR ', '), '') AS usuarios " +
                "FROM costos c " +
                "LEFT JOIN proyecto p ON c.proyecto_id = p.id " +
                "LEFT JOIN complejidad co ON p.complejidad_id = co.id " +
                "LEFT JOIN asignacion_proyecto ap ON ap.proyecto_id = p.id " +
                "LEFT JOIN usuarios u ON ap.usuarios_id = u.id " +
                "GROUP BY c.id, p.id, p.nombre_proyecto, p.descripcion, co.nivel, c.costo_proyecto " +
                "ORDER BY p.nombre_proyecto ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("costo_id"),
                        rs.getInt("proyecto_id"),
                        rs.getString("nombre_proyecto"),
                        rs.getString("descripcion"),
                        rs.getString("complejidad"),
                        rs.getDouble("costo_proyecto"),
                        rs.getString("usuarios")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            modificar.setEnabled(true);
            agregar.setEnabled(true);
            setCursor(prev);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == agregar) {
            IngresoProyecto ip = new IngresoProyecto(principal, conexion, usuarioId, this);
            ip.setLocationRelativeTo(this);
            ip.setVisible(true);
        }

        if (e.getSource() == modificar) {
            abrirEditorProyecto();
        }
    }

    /**
     * Abre la ventana EditarProyecto si la fila está seleccionada y el usuario está asignado.
     */
    private void abrirEditorProyecto() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un proyecto en la tabla para modificarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int proyectoId = (int) modelo.getValueAt(filaModelo, 1);
        String nombreProyecto = String.valueOf(modelo.getValueAt(filaModelo, 2));

        // Verificar si el usuario actual está asignado al proyecto (es encargado)
        String checkSql = "SELECT COUNT(*) AS total FROM asignacion_proyecto WHERE proyecto_id = ? AND usuarios_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(checkSql)) {
            ps.setInt(1, proyectoId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    if (total == 0) {
                        JOptionPane.showMessageDialog(this, "No eres el usuario encargado de este proyecto. Solo el encargado puede modificarlo.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar permisos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Abrir la ventana de edición pasando conexion, proyectoId y usuarioId
        EditarProyecto ep = new EditarProyecto(this, conexion, proyectoId, usuarioId);
        ep.setLocationRelativeTo(this);
        ep.setVisible(true);

        // Al volver, recargamos datos
        cargarDatos();
    }
}

