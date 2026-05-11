package com.germantown.autocare.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.germantown.autocare.util.UiTheme;

/**
 * Main dashboard with tabs for Customers, Employees, Vehicles, Appointments, Services/Parts, Billing, and History.
 */
public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Germantown AutoCare - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        tabs.addTab("Customers", new CustomerPanel());
        tabs.addTab("Employees", new EmployeePanel());
        VehiclePanel vehiclePanel = new VehiclePanel();
        tabs.addTab("Vehicles", vehiclePanel);

        tabs.addTab("Appointments", new AppointmentPanel());
        tabs.addTab("Services & Parts", new ServicePartPanel());

        // Invoice & payment
        tabs.addTab("Billing", new InvoicePaymentPanel());

        // Payment and Service History (NEW)
        tabs.addTab("History", new HistoryPanel());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == vehiclePanel) {
                vehiclePanel.refreshCustomerList();
            }
        });

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        getContentPane().setBackground(UiTheme.PANEL_BG);
        ((JComponent) getContentPane()).setOpaque(true);
    }
}
