package com.germantown.autocare.ui;

import com.germantown.autocare.config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

/**
 * Simple login screen; verifies DB connection and opens dashboard.
 */
public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Germantown AutoCare - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Germantown AutoCare Management System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        JLabel sub = new JLabel("Click Login to continue.", SwingConstants.CENTER);
        center.add(sub);
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 32));
        loginBtn.addActionListener(e -> onLogin());
        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        center.add(btnPanel);
        main.add(center, BorderLayout.CENTER);

        add(main);
    }

    private void onLogin() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                dispose();
                new DashboardFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
