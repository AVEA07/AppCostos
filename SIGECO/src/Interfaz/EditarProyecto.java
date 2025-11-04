/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author aeva2007
 */



public class EditarProyecto extends JDialog implements ActionListener {

    private Container contenedor;
    private JTextField campoNombre;
    private JTextArea campoDescripcion;
    private JComboBox<String> comboComplejidad;
    private JTextField campoHoras; // para agregar horas trabajadas hoy
    private JButton btnAgregarHoras, btnAgregarModulo, btnGuardar, btnCancelar;
    private DefaultListModel<String> modeloModulos;
    private JList<String> listaModulos;

    private Connection conexion;
    private int proyectoId;
    private int usuarioId;

    // mapa complejidadId -> display (y lista auxiliar)
    private Map<Integer, String> mapComplejidad = new LinkedHashMap<>();
    private Map<Integer, Integer> mapComplejidadDias = new LinkedHashMap<>();

    public EditarProyecto(JDialog padre, Connection conexion, int proyectoId, int usuarioId) {
        super(padre, "Editar Proyecto", true);
        this.conexion = conexion;
        this.proyectoId = proyectoId;
        this.usuarioId = usuarioId;

        setSize(700, 600);
        setLocationRelativeTo(padre);
        setResizable(false);

        inicio();
        cargarComplejidades();
        cargarDatosProyecto();
        cargarModulos();
    }

    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0; c.gridx = 0; c.gridwidth = 2;
        JLabel titulo = new JLabel("Editar Proyecto", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        contenedor.add(titulo, c);

        // Nombre
        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0;
        contenedor.add(new JLabel("Nombre proyecto:"), c);
        campoNombre = new JTextField(30);
        c.gridx = 1;
        contenedor.add(campoNombre, c);

        // Descripción
        c.gridy = 2; c.gridx = 0;
        contenedor.add(new JLabel("Descripción:"), c);
        campoDescripcion = new JTextArea(6, 30);
        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(campoDescripcion);
        c.gridx = 1;
        contenedor.add(scrollDesc, c);

        // Complejidad
        c.gridy = 3; c.gridx = 0;
        contenedor.add(new JLabel("Complejidad:"), c);
        comboComplejidad = new JComboBox<>();
        c.gridx = 1;
        contenedor.add(comboComplejidad, c);

        // Horas - registrar trabajo diario
        c.gridy = 4; c.gridx = 0;
        contenedor.add(new JLabel("Horas a agregar (h):"), c);
        campoHoras = new JTextField(10);
        c.gridx = 1;
        contenedor.add(campoHoras, c);

        c.gridy = 5; c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        btnAgregarHoras = new JButton("Registrar Horas");
        btnAgregarHoras.addActionListener(this);
        contenedor.add(btnAgregarHoras, c);

        // Modulos
        c.gridy = 6; c.gridx = 0;
        contenedor.add(new JLabel("Módulos:"), c);
        modeloModulos = new DefaultListModel<>();
        listaModulos = new JList<>(modeloModulos);
        listaModulos.setVisibleRowCount(6);
        JScrollPane scrollMod = new JScrollPane(listaModulos);
        c.gridx = 1;
        contenedor.add(scrollMod, c);

        c.gridy = 7; c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        btnAgregarModulo = new JButton("Agregar Módulo");
        btnAgregarModulo.addActionListener(this);
        contenedor.add(btnAgregarModulo, c);

        // Botones Guardar/Cancelar
        c.gridy = 8; c.gridx = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        JPanel panelBot = new JPanel();
        btnGuardar = new JButton("Guardar Cambios");
        btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(this);
        btnCancelar.addActionListener(this);
        panelBot.add(btnGuardar);
        panelBot.add(btnCancelar);
        contenedor.add(panelBot, c);
    }

    private void cargarComplejidades() {
        mapComplejidad.clear();
        mapComplejidadDias.clear();
        comboComplejidad.removeAllItems();
        String sql = "SELECT id, nivel, cantidad_dias FROM complejidad ORDER BY cantidad_dias ASC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nivel = rs.getString("nivel");
                int dias = rs.getInt("cantidad_dias");
                String label = nivel + " (" + dias + " días)";
                mapComplejidad.put(id, label);
                mapComplejidadDias.put(id, dias);
                comboComplejidad.addItem(label);
            }
        } catch (SQLException ex) {
            // Si no hay tabla o falla, colocamos los tres valores por defecto
            mapComplejidad.clear();
            mapComplejidad.put(1, "baja (30 días)");
            mapComplejidad.put(2, "media (90 días)");
            mapComplejidad.put(3, "alta (180 días)");
            mapComplejidadDias.put(1, 30);
            mapComplejidadDias.put(2, 90);
            mapComplejidadDias.put(3, 180);
            comboComplejidad.removeAllItems();
            comboComplejidad.addItem("baja (30 días)");
            comboComplejidad.addItem("media (90 días)");
            comboComplejidad.addItem("alta (180 días)");
        }
    }

    private void cargarDatosProyecto() {
        String sql = "SELECT p.nombre_proyecto, p.descripcion, p.complejidad_id FROM proyecto p WHERE p.id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    campoNombre.setText(rs.getString("nombre_proyecto"));
                    campoDescripcion.setText(rs.getString("descripcion"));
                    int compId = rs.getInt("complejidad_id");
                    // set selected index by matching compId in mapComplejidad
                    int idx = 0;
                    int i = 0;
                    for (Integer id : mapComplejidad.keySet()) {
                        if (id == compId) { idx = i; break; }
                        i++;
                    }
                    if (comboComplejidad.getItemCount() > 0) comboComplejidad.setSelectedIndex(Math.max(0, idx));
                } else {
                    JOptionPane.showMessageDialog(this, "Proyecto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar proyecto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void cargarModulos() {
        modeloModulos.clear();
        String sql = "SELECT nombre_modulo, fecha_creacion FROM modulo WHERE proyecto_id = ? ORDER BY fecha_creacion ASC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombre_modulo");
                    java.sql.Date sqlDate = rs.getDate("fecha_creacion"); // puede ser null
                    String fechaStr = "";
                    if (sqlDate != null) {
                        LocalDate ld = sqlDate.toLocalDate();
                        fechaStr = " (" + ld.toString() + ")"; // ISO yyyy-MM-dd
                    }
                    modeloModulos.addElement(nombre + fechaStr);
                }
            }
        } catch (SQLException ex) {
            // ignorar si no hay tabla todavía, pero muestra en consola para debug
            System.err.println("cargarModulos error: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAgregarHoras) {
            registrarHoras();
        } else if (e.getSource() == btnAgregarModulo) {
            agregarModuloDialog();
        } else if (e.getSource() == btnGuardar) {
            guardarCambios();
        } else if (e.getSource() == btnCancelar) {
            this.dispose();
        }
    }

    private void registrarHoras() {
        String sHoras = campoHoras.getText().trim();
        if (sHoras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa las horas a registrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double horas;
        try {
            horas = Double.parseDouble(sHoras);
            if (horas <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un número válido de horas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Actualizar asignacion_proyecto: sumar horas al registro del usuario; si no existe, insertar.
        boolean prevAuto = true;
        try {
            prevAuto = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            String select = "SELECT id, horas_trabajadas FROM asignacion_proyecto WHERE proyecto_id = ? AND usuarios_id = ?";
            try (PreparedStatement ps = conexion.prepareStatement(select)) {
                ps.setInt(1, proyectoId);
                ps.setInt(2, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int asignId = rs.getInt("id");
                        double actuales = rs.getDouble("horas_trabajadas");
                        double nuevas = actuales + horas;
                        try (PreparedStatement psUpd = conexion.prepareStatement("UPDATE asignacion_proyecto SET horas_trabajadas = ? WHERE id = ?")) {
                            psUpd.setDouble(1, nuevas);
                            psUpd.setInt(2, asignId);
                            psUpd.executeUpdate();
                        }
                    } else {
                        // insertar
                        try (PreparedStatement psIns = conexion.prepareStatement("INSERT INTO asignacion_proyecto (usuarios_id, proyecto_id, horas_trabajadas) VALUES (?,?,?)")) {
                            psIns.setInt(1, usuarioId);
                            psIns.setInt(2, proyectoId);
                            psIns.setDouble(3, horas);
                            psIns.executeUpdate();
                        }
                    }
                }
            }

            // Insertar log
            try (PreparedStatement psLog = conexion.prepareStatement(
                    "INSERT INTO proyecto_log (proyecto_id, usuario_id, accion, detalle) VALUES (?,?,?,?)")) {
                psLog.setInt(1, proyectoId);
                psLog.setInt(2, usuarioId);
                psLog.setString(3, "REGISTRAR_HORAS");
                psLog.setString(4, "Se agregaron " + horas + " horas por el usuario ID " + usuarioId);
                psLog.executeUpdate();
            } catch (SQLException exLog) {
                // no romper si no existe la tabla
            }

            conexion.commit();
            campoHoras.setText("");
            JOptionPane.showMessageDialog(this, "Horas registradas correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            try { conexion.rollback(); } catch (SQLException r) { /* ignorar */ }
            JOptionPane.showMessageDialog(this, "Error al registrar horas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conexion.setAutoCommit(prevAuto); } catch (SQLException ex) { /* ignorar */ }
        }

        // recargar módulos / datos por si afectan complejidad
        cargarModulos();
    }

    private void agregarModuloDialog() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del módulo:");
        if (nombre == null) return; // canceló
        nombre = nombre.trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // usamos CURRENT_DATE en SQL para no preocuparnos por tipos
            String sql = "INSERT INTO modulo (proyecto_id, nombre_modulo, fecha_creacion) VALUES (?,?,CURRENT_DATE)";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, proyectoId);
                ps.setString(2, nombre);
                ps.executeUpdate();
            }

            // mostrar en UI con LocalDate (más fiable)
            String fechaHoy = LocalDate.now().toString(); // yyyy-MM-dd
            modeloModulos.addElement(nombre + " (" + fechaHoy + ")");

            // log en BD (si la tabla existe)
            try (PreparedStatement psLog = conexion.prepareStatement(
                    "INSERT INTO proyecto_log (proyecto_id, usuario_id, accion, detalle) VALUES (?,?,?,?)")) {
                psLog.setInt(1, proyectoId);
                psLog.setInt(2, usuarioId);
                psLog.setString(3, "AGREGAR_MODULO");
                psLog.setString(4, "Se agregó módulo: " + nombre + " por usuario ID " + usuarioId);
                psLog.executeUpdate();
            } catch (SQLException exLog) { /* ignorar si no existe la tabla aún */ }

            JOptionPane.showMessageDialog(this, "Módulo agregado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar módulo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        String nuevoNombre = campoNombre.getText().trim();
        String nuevaDesc = campoDescripcion.getText().trim();
        int selCompIndex = comboComplejidad.getSelectedIndex();

        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proyecto no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // encontrar complejidad id seleccionado
        Integer complejidadId = null;
        int i = 0;
        for (Integer id : mapComplejidad.keySet()) {
            if (i == selCompIndex) { complejidadId = id; break; }
            i++;
        }
        if (complejidadId == null && !mapComplejidad.isEmpty()) {
            complejidadId = mapComplejidad.keySet().iterator().next();
        }

        // Guardar cambios en proyecto y registrar log; además escribir resumen en archivo TXT
        boolean prevAuto = true;
        try {
            prevAuto = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            // Actualizar proyecto
            String sqlUpd = "UPDATE proyecto SET nombre_proyecto = ?, descripcion = ?, complejidad_id = ? WHERE id = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sqlUpd)) {
                ps.setString(1, nuevoNombre);
                ps.setString(2, nuevaDesc);
                if (complejidadId != null) ps.setInt(3, complejidadId); else ps.setNull(3, Types.INTEGER);
                ps.setInt(4, proyectoId);
                ps.executeUpdate();
            }

            // Insertar registro en proyecto_log con detalle
            String detalle = "Modificado proyecto: nombre='" + nuevoNombre + "', descripcion='" + (nuevaDesc.length() > 100 ? nuevaDesc.substring(0, 100) + "..." : nuevaDesc) + "', complejidad_id=" + complejidadId;
            try (PreparedStatement psLog = conexion.prepareStatement(
                    "INSERT INTO proyecto_log (proyecto_id, usuario_id, accion, detalle) VALUES (?,?,?,?)")) {
                psLog.setInt(1, proyectoId);
                psLog.setInt(2, usuarioId);
                psLog.setString(3, "MODIFICAR_PROYECTO");
                psLog.setString(4, detalle);
                psLog.executeUpdate();
            } catch (SQLException exLog) {
                // ignora si falla
            }

            conexion.commit();

            // Escribir archivo TXT con resumen (en carpeta SIGECO_logs)
            escribirArchivoLog(detalle);

            JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (SQLException ex) {
            try { conexion.rollback(); } catch (SQLException r) { /* ignorar */ }
            JOptionPane.showMessageDialog(this, "Error al guardar cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conexion.setAutoCommit(prevAuto); } catch (SQLException ex) { /* ignorar */ }
        }
    }

    private void escribirArchivoLog(String detalle) {
        String dir = System.getProperty("user.dir") + File.separator + "SIGECO_logs";
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();

        DateTimeFormatter fileFmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        DateTimeFormatter humanFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(fileFmt);
        String filename = "proyecto_" + proyectoId + "_" + timestamp + ".txt";
        File out = new File(folder, filename);

        try (FileWriter fw = new FileWriter(out, true)) {
            fw.write("Proyecto ID: " + proyectoId + System.lineSeparator());
            fw.write("Usuario ID: " + usuarioId + System.lineSeparator());
            fw.write("Fecha: " + LocalDateTime.now().format(humanFmt) + System.lineSeparator());
            fw.write("Detalle: " + detalle + System.lineSeparator());
            fw.flush();
        } catch (IOException ex) {
            System.err.println("No se pudo escribir archivo log: " + ex.getMessage());
        }
    }
}
