package com.oscars.vehiclemaintenancesystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@calvin.humber.ca:1521:grok"; // Adjust as needed
    private static final String USER = "oscars";
    private static final String PASSWORD = "Ite5220";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found.", e);
        }
    }
}