/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Recursos;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author practicante
 */
public class Recursos {
    public static ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(Recursos.class.getResource(ruta));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imgEscalada);
    }
}
