/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Start;
import InicioSesion.InicioSesion;
import javax.swing.SwingUtilities;

/**
 *
 * @author practicante
 */
public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InicioSesion is = new InicioSesion();
            is.setVisible(true);
        });
    }
}
