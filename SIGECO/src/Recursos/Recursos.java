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
    public static void cargarIcono(JFrame frame, int ancho, int altura){
        try{
            ImageIcon iconoOriginal = new ImageIcon(Recursos.class.getResource("/Imagenes/SIGECO - BCG - SQ - 2D.png"));
            Image iconoEscalado = iconoOriginal.getImage().getScaledInstance(ancho, altura, Image.SCALE_SMOOTH);
            frame.setIconImage(iconoEscalado); 
        }catch(Exception e){
            frame.setIconImage(null);
        }
    }
}
