package com.germantown.autocare.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard with tabs for Customers, Vehicles, and Billing.
 */
public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Germantown AutoCare - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customers", new CustomerPanel());
        VehiclePanel vehiclePanel = new VehiclePanel();
        tabs.addTab("Vehicles", vehiclePanel);

        // New tab for Invoice & Payment UI
        tabs.addTab("Billing", new InvoicePaymentPanel());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == vehiclePanel) {
                vehiclePanel.refreshCustomerList();
            }
        });

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
