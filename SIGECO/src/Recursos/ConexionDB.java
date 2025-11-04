/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Recursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author aeva2007
 */
public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/SIGECO?serverTimezone=UTC";
    private static final String USER = "practicante";
    private static final String PASS = "Angel2007";

    private ConexionDB() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
