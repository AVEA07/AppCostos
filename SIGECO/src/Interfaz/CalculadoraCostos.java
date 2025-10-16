/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author practicante
 */

public class CalculadoraCostos extends JFrame implements ActionListener {
    private JComboBox<String> tipoBox;
    private JComboBox<String> techBox;
    private JTextField campoCantidad, campoHoras, campoTarifa;
    private JButton calcularBtn;
    private JLabel resultadoLabel;
    private Container contenedor;

    public CalculadoraCostos() {
        setTitle("Calculadora de Costos");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        inicio();
    }

    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        contenedor.add(new JLabel("Tipo programador:"), c);
        tipoBox = new JComboBox<>(new String[] {"Senior","Semi-Senior","Junior"});
        c.gridx = 1; c.gridy = 0;
        contenedor.add(tipoBox, c);

        c.gridx = 0; c.gridy = 1;
        contenedor.add(new JLabel("Tecnología:"), c);
        techBox = new JComboBox<>(new String[] {"Java", "Python", "JavaScript", "Otro"});
        c.gridx = 1; c.gridy = 1;
        contenedor.add(techBox, c);

        c.gridx = 0; c.gridy = 2;
        contenedor.add(new JLabel("Cantidad de programadores:"), c);
        campoCantidad= new JTextField(50);
        c.gridx = 1; c.gridy = 2;
        contenedor.add(campoCantidad, c);

        c.gridx = 0; c.gridy = 3;
        contenedor.add(new JLabel("Horas estimadas por programador:"), c);
        campoHoras = new JTextField(50);
        c.gridx = 1; c.gridy = 3;
        contenedor.add(campoHoras, c);

        c.gridx = 0; c.gridy = 4;
        contenedor.add(new JLabel("Tarifa por hora (USD):"), c);
        campoTarifa = new JTextField(50);
        c.gridx = 1; c.gridy = 4;
        contenedor.add(campoTarifa, c);

        calcularBtn = new JButton("Calcular");
        calcularBtn.addActionListener(this);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        contenedor.add(calcularBtn, c);

        resultadoLabel = new JLabel("Total: ");
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        contenedor.add(resultadoLabel, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calcularBtn) {
            try {
                int cantidad = Integer.parseInt(campoCantidad.getText().trim());
                double horas = Double.parseDouble(campoHoras.getText().trim());
                double tarifa = Double.parseDouble(campoTarifa.getText().trim());

                // Aquí puedes añadir reglas por tipo/tecnología si quieres multiplicadores
                // por ejemplo: si Senior + Java -> tarifa *= 1.1; o ajustar horas.
                String tipo = (String) tipoBox.getSelectedItem();
                String tech = (String) techBox.getSelectedItem();

                // Ejemplo de ajuste simple (personalizable):
                if ("Senior".equals(tipo) && "Java".equals(tech)) {
                    // supongamos que para senior-java se estiman 10% más horas de integración
                    horas = horas * 1.10;
                }

                double total = cantidad * horas * tarifa;
                String detalle = String.format("Detalle: %d x %.2f h x %.2f USD = %.2f USD", cantidad, horas, tarifa, total);
                resultadoLabel.setText("Total: " + String.format("%.2f USD", total));
                JOptionPane.showMessageDialog(this, detalle, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce números válidos en cantidad/horas/tarifa", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
