package com.germantown.autocare.app;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.config.DatabaseInitializer;
import com.germantown.autocare.ui.DashboardFrame;
import com.germantown.autocare.ui.LoginFrame;
import com.germantown.autocare.util.UiTheme;

import javax.swing.*;
import java.sql.Connection;

/**
 * Entry point: initializes embedded H2 database, shows login screen, then opens the dashboard.
 * No external database installation required.
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("Germantown AutoCare Management System (GAMS)");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            UiTheme.applyGlobal();

            try {
                // Initialize database (creates tables if needed)
                System.out.println("Initializing database...");
                DatabaseInitializer.initializeDatabase();
                
                // Test connection
                try (Connection conn = DBConnection.getConnection()) {
                    if (conn != null && !conn.isClosed()) {
                        System.out.println("Database connection successful. Connected to: " + DBConnection.getDatabase());
                        
                        // Show login frame, then open dashboard on successful login
                        LoginFrame loginFrame = new LoginFrame(() -> {
                            SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
                        });
                        loginFrame.setVisible(true);
                    } else {
                        System.err.println("Database connection failed: connection is null or closed.");
                        showDatabaseError("Connection is null or closed.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Database initialization or connection failed: " + e.getMessage());
                e.printStackTrace();
                showDatabaseError(e.getMessage());
            }
        });
    }

    private static void showDatabaseError(String detail) {
        JOptionPane.showMessageDialog(null,
                "Database initialization failed.\n\n"
                        + "The application uses an embedded H2 database which should work automatically.\n"
                        + "If the problem persists, check that the application has write permissions\n"
                        + "to the user home directory (" + System.getProperty("user.home") + ").\n\n"
                        + "Details: " + detail,
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
