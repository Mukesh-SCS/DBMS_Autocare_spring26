package com.germantown.autocare.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/autocare_db",
                "root",
                "admin"
                 //change based on you password usually its root

        );
    }
}