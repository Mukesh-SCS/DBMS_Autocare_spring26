package com.germantown.autocare.app;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.ui.DashboardFrame;
import com.germantown.autocare.util.UiTheme;

import javax.swing.*;
import java.sql.Connection;

/**
 * Entry point: connects using {@link DBConnection} (defaults or {@code config.properties}), then opens the dashboard.
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

            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("Database connection successful. Connected to: " + conn.getCatalog());
                    new DashboardFrame().setVisible(true);
                } else {
                    System.err.println("Database connection failed: connection is null or closed.");
                    showDatabaseError("Connection is null or closed.");
                }
            } catch (Exception e) {
                System.err.println("Database connection failed: " + e.getMessage());
                e.printStackTrace();
                showDatabaseError(e.getMessage());
            }
        });
    }

    private static void showDatabaseError(String detail) {
        JOptionPane.showMessageDialog(null,
                "Could not connect to MySQL.\n\n"
                        + "• Start MySQL and create database autocare_db (run Autocare/sql/create_tables.sql).\n"
                        + "• Place config.properties next to the JAR with db.host, db.port, db.database, db.username, db.password.\n"
                        + "• Default credentials are root/root if you do not use a config file.\n\n"
                        + "Details: " + detail,
                "Database connection failed",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
