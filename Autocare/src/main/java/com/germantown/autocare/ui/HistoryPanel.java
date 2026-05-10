package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.germantown.autocare.dao.AppointmentDAO;
import com.germantown.autocare.dao.PaymentDAO;
import com.germantown.autocare.model.Appointment;
import com.germantown.autocare.util.UiTheme;

/**
 * Panel for viewing payment history and service history.
 */
public class HistoryPanel extends JPanel {

    private JTable paymentTable;
    private JTable serviceTable;
    private DefaultTableModel paymentTableModel;
    private DefaultTableModel serviceTableModel;
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public HistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        UiTheme.paintPanelBackground(this);

        // Create tabbed pane for payment and service history
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Payment History", createPaymentHistoryPanel());
        tabs.addTab("Service History", createServiceHistoryPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPaymentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        UiTheme.paintPanelBackground(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Create payment table
        paymentTableModel = new DefaultTableModel(
            new String[]{"Payment ID", "Invoice ID", "Customer Name", "Payment Date", "Amount", "Method", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(paymentTableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setRowHeight(25);
        paymentTable.getTableHeader().setPreferredSize(new Dimension(0, 30));

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        UiTheme.paintPanelBackground(buttonPanel);

        JButton refreshBtn = new JButton("Refresh");
        UiTheme.stylePrimaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadPaymentHistory());

        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load data on startup
        loadPaymentHistory();

        return panel;
    }

    private JPanel createServiceHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        UiTheme.paintPanelBackground(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Create service table
        serviceTableModel = new DefaultTableModel(
            new String[]{"Appointment ID", "Customer ID", "Vehicle ID", "Appointment Date", "Status", "Notes"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable = new JTable(serviceTableModel);
        serviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serviceTable.setRowHeight(25);
        serviceTable.getTableHeader().setPreferredSize(new Dimension(0, 30));

        JScrollPane scrollPane = new JScrollPane(serviceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        UiTheme.paintPanelBackground(buttonPanel);

        JButton refreshBtn = new JButton("Refresh");
        UiTheme.stylePrimaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadServiceHistory());

        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load data on startup
        loadServiceHistory();

        return panel;
    }

    private void loadPaymentHistory() {
        paymentTableModel.setRowCount(0);

        try {
            List<Map<String, Object>> payments = paymentDAO.getPaymentHistory();
            for (Map<String, Object> payment : payments) {
                Object paymentId = payment.get("paymentId");
                Object amount = payment.get("paymentAmount");
                paymentTableModel.addRow(new Object[]{
                    paymentId != null ? paymentId : "—",
                    payment.get("invoiceId"),
                    payment.get("customerName"),
                    payment.get("paymentDate") != null ? payment.get("paymentDate") : "—",
                    amount != null ? String.format("$%.2f", ((Number) amount).doubleValue()) : "—",
                    payment.get("paymentMethod") != null ? payment.get("paymentMethod") : "—",
                    payment.get("paymentStatus")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading payment history: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadServiceHistory() {
        serviceTableModel.setRowCount(0);

        try {
            List<Appointment> appointments = appointmentDAO.findAll();
            for (Appointment appt : appointments) {
                serviceTableModel.addRow(new Object[]{
                    appt.getAppointmentId(),
                    appt.getCustomerId(),
                    appt.getVehicleId(),
                    appt.getAppointmentDate(),
                    appt.getStatus(),
                    appt.getNotes() != null ? appt.getNotes() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading service history: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
