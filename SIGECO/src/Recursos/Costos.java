/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Recursos;

/**
 *
 * @author practicante
 */
public class Costos {
    private int cantidad;
    private double horas;
    private double tarifa;
    private String tipo;
    private String tecnologia;

    public double calcularTotal() {
        double horasAjustadas = horas;
        if ("Senior".equals(tipo) && "Java".equals(tecnologia)) {
            horasAjustadas *= 1.10;
        }
        return cantidad * horasAjustadas * tarifa;
    }
}
