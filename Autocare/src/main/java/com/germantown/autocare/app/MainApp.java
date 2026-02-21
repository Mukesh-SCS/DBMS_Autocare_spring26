package com.germantown.autocare.app;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.ui.LoginFrame;

import javax.swing.*;
import java.sql.Connection;

/**
 * Entry point: tests DB connection, then launches the login UI.
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("Germantown AutoCare Management System (GAMS)");
        System.out.println("Testing database connection...");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful. Connected to: " + conn.getCatalog());
            } else {
                System.out.println("Database connection failed: connection is null or closed.");
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
