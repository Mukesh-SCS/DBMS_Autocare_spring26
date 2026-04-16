package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.util.UiTheme;

/**
 * Login screen with username and password.
 * Database host, port, and name are auto-configured (localhost:3306/autocare_db).
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Germantown AutoCare - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        UiTheme.paintPanelBackground(main);

        // Title
        JLabel title = new JLabel("Germantown AutoCare", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(30, 41, 59));
        main.add(title, BorderLayout.NORTH);

        // Center Panel with form
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        UiTheme.paintPanelBackground(center);

        JLabel subtitle = new JLabel("<html><div style='text-align:center;color:#64748b;'>Database-backed shop management.<br/>Enter your MySQL credentials.</div></html>", SwingConstants.CENTER);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);
        center.add(Box.createVerticalStrut(24));

        // Username
        center.add(createLabeledRow("Username:", usernameField = new JTextField(DBConnection.getUsername(), 20)));
        center.add(Box.createVerticalStrut(16));

        // Password
        center.add(createLabeledRow("Password:", passwordField = new JPasswordField(20)));
        center.add(Box.createVerticalStrut(24));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        UiTheme.paintPanelBackground(btnPanel);

        JButton loginBtn = new JButton("  Login  ");
        UiTheme.stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> onLogin());

        JButton resetBtn = new JButton("Reset");
        UiTheme.stylePrimaryButton(resetBtn);
        resetBtn.addActionListener(e -> resetFields());

        btnPanel.add(loginBtn);
        btnPanel.add(resetBtn);

        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(btnPanel);

        main.add(center, BorderLayout.CENTER);

        add(main);
    }

    private JPanel createLabeledRow(String label, JTextField component) {
        JPanel panel = new JPanel(new GridBagLayout());
        UiTheme.paintPanelBackground(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(90, 32));
        panel.add(lbl, gbc);

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Set proper size for text fields
        component.setPreferredSize(new Dimension(250, 32));
        component.setMinimumSize(new Dimension(150, 32));
        component.setMaximumSize(new Dimension(400, 32));
        
        panel.add(component, gbc);

        return panel;
    }

    private void resetFields() {
        usernameField.setText(DBConnection.getUsername());
        passwordField.setText("");
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Use default host, port, database with custom username/password
            try (Connection conn = DBConnection.getConnection(
                    DBConnection.getHost(),
                    DBConnection.getPort(),
                    DBConnection.getDatabase(),
                    username,
                    password
            )) {
                if (conn != null && !conn.isClosed()) {
                    // Update static settings for future connections
                    DBConnection.setConnectionSettings(
                            DBConnection.getHost(),
                            DBConnection.getPort(),
                            DBConnection.getDatabase(),
                            username,
                            password
                    );
                    dispose();
                    new com.germantown.autocare.ui.DashboardFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Authentication failed:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
