package com.germantown.autocare.ui;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.util.UiTheme;

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
        setSize(440, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));
        UiTheme.paintPanelBackground(main);

        JLabel title = new JLabel("Germantown AutoCare", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(new Color(30, 41, 59));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 12, 12));
        UiTheme.paintPanelBackground(center);
        JLabel sub = new JLabel("<html><div style='text-align:center;color:#64748b;'>Database-backed shop management.<br/>Connect to MySQL, then sign in.</div></html>", SwingConstants.CENTER);
        center.add(sub);
        JButton loginBtn = new JButton("  Sign in  ");
        UiTheme.stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> onLogin());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        UiTheme.paintPanelBackground(btnPanel);
        btnPanel.add(loginBtn);
        center.add(btnPanel);
        JLabel hint = new JLabel("<html><div style='text-align:center;font-size:11px;color:#94a3b8;'>Requires <code>autocare_db</code> and credentials in DBConnection</div></html>", SwingConstants.CENTER);
        center.add(hint);
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
