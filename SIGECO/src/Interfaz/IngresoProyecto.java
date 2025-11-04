/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/**
 * IngresoProyecto - crea un proyecto, registra costo inicial, asigna creador y deja un log.
 * Mejoras: UX (cursor/disable), manejo seguro de transacción y tolerancia si falta proyecto_log.
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

        getRootPane().setDefaultButton(guardar);
        campoProyecto.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelar){
            this.dispose();
            return;
        }

        if (e.getSource() == guardar) {
            // Comprobación de conexión
            if (conexion == null) {
                JOptionPane.showMessageDialog(this, "Sin conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nombreProyecto = campoProyecto.getText().trim();
            String descripcionProyecto = campoDescripcion.getText().trim();

            if (nombreProyecto.isEmpty() || descripcionProyecto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nombreProyecto.length() > 100) {
                JOptionPane.showMessageDialog(this, "El nombre del proyecto no debe exceder 100 caracteres", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // UX: deshabilitar botón y mostrar cursor de espera
            guardar.setEnabled(false);
            Cursor previousCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            boolean autoCommitPrev = true;
            try {
                autoCommitPrev = conexion.getAutoCommit();
                conexion.setAutoCommit(false);

                int proyectoId = -1;

                // 1) Buscar proyecto por nombre
                String sqlProyecto = "SELECT id FROM proyecto WHERE nombre_proyecto = ?";
                try (PreparedStatement psProyecto = conexion.prepareStatement(sqlProyecto)) {
                    psProyecto.setString(1, nombreProyecto);
                    try (ResultSet rs = psProyecto.executeQuery()) {
                        if (rs.next()) {
                            proyectoId = rs.getInt("id");
                        }
                    }
                }

                // Si existe, preguntar al usuario qué hacer
                if (proyectoId != -1) {
                    int resp = JOptionPane.showConfirmDialog(
                            this,
                            "Ya existe un proyecto con ese nombre. ¿Desea abrirlo para editarlo?",
                            "Proyecto existente",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    // Restaurar estado UI / autoCommit antes de salir
                    try { conexion.setAutoCommit(autoCommitPrev); } catch (SQLException ex) { /* ignorar */ }
                    guardar.setEnabled(true);
                    setCursor(previousCursor);

                    if (resp == JOptionPane.YES_OPTION) {
                        if (gp != null) gp.cargarDatos();
                        dispose();
                        return;
                    } else {
                        // No crea nada, simplemente vuelve a la UI
                        return;
                    }
                }

                // 2) Insertar proyecto nuevo
                String insertProyecto = "INSERT INTO proyecto (nombre_proyecto, descripcion, fecha_inicializacion) VALUES (?,?,CURRENT_DATE)";
                try (PreparedStatement psInsert = conexion.prepareStatement(insertProyecto, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, nombreProyecto);
                    psInsert.setString(2, descripcionProyecto);
                    psInsert.executeUpdate();
                    try (ResultSet keys = psInsert.getGeneratedKeys()) {
                        if (keys.next()) {
                            proyectoId = keys.getInt(1);
                        } else {
                            throw new SQLException("No se obtuvo ID del proyecto insertado");
                        }
                    }
                }

                // 3) Insertar registro en costos si no existe
                String checkCosto = "SELECT id FROM costos WHERE proyecto_id = ?";
                try (PreparedStatement psCheckCosto = conexion.prepareStatement(checkCosto)) {
                    psCheckCosto.setInt(1, proyectoId);
                    try (ResultSet rs = psCheckCosto.executeQuery()) {
                        if (!rs.next()) {
                            String sqlCosto = "INSERT INTO costos (proyecto_id, costo_proyecto) VALUES (?, 0)";
                            try (PreparedStatement psCosto = conexion.prepareStatement(sqlCosto)) {
                                psCosto.setInt(1, proyectoId);
                                psCosto.executeUpdate();
                            }
                        }
                    }
                }

                // 4) Insertar asignacion_proyecto para el creador (si no existe)
                String checkAsign = "SELECT id FROM asignacion_proyecto WHERE usuarios_id = ? AND proyecto_id = ?";
                try (PreparedStatement psCheckAssign = conexion.prepareStatement(checkAsign)) {
                    psCheckAssign.setInt(1, usuarioId);
                    psCheckAssign.setInt(2, proyectoId);
                    try (ResultSet rs = psCheckAssign.executeQuery()) {
                        if (!rs.next()) {
                            String insertAsign = "INSERT INTO asignacion_proyecto (usuarios_id, proyecto_id, horas_trabajadas) VALUES (?,?,0)";
                            try (PreparedStatement psAsign = conexion.prepareStatement(insertAsign)) {
                                psAsign.setInt(1, usuarioId);
                                psAsign.setInt(2, proyectoId);
                                psAsign.executeUpdate();
                            }
                        }
                    }
                }

                // 5) Intentar insertar log, pero no fallar si la tabla no existe
                String insertLog = "INSERT INTO proyecto_log (proyecto_id, usuario_id, accion, detalle) VALUES (?,?,?,?)";
                try (PreparedStatement psLog = conexion.prepareStatement(insertLog)) {
                    psLog.setInt(1, proyectoId);
                    psLog.setInt(2, usuarioId);
                    psLog.setString(3, "CREAR_PROYECTO");
                    psLog.setString(4, "Proyecto registrado: " + nombreProyecto);
                    psLog.executeUpdate();
                } catch (SQLException exLog) {
                    // Si falla por ausencia de tabla o permisos, no abortamos la transacción
                    System.err.println("Aviso: no se pudo insertar log (proyecto_log?): " + exLog.getMessage());
                }

                // Confirmar todas las operaciones
                conexion.commit();

                JOptionPane.showMessageDialog(this, "Registro guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (gp != null) gp.cargarDatos();
                dispose();

            } catch (SQLException ex) {
                try { conexion.rollback(); } catch (SQLException rbe) { System.err.println("Rollback failed: " + rbe.getMessage()); }
                JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Restaurar estado de la conexión y UI
                try { conexion.setAutoCommit(autoCommitPrev); } catch (SQLException ex) { /* ignorar */ }
                guardar.setEnabled(true);
                setCursor(previousCursor);
            }
        }
    }
}
