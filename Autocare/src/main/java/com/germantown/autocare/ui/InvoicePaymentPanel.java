package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.germantown.autocare.dao.AppointmentDAO;
import com.germantown.autocare.dao.CustomerDAO;
import com.germantown.autocare.dao.InvoiceDAO;
import com.germantown.autocare.dao.ServiceDAO;
import com.germantown.autocare.model.Appointment;
import com.germantown.autocare.model.Customer;
import com.germantown.autocare.model.Invoice;
import com.germantown.autocare.model.Service;
import com.germantown.autocare.service.BillingService;
import com.germantown.autocare.util.UIHelper;
import com.germantown.autocare.util.UiTheme;

/**
 * Panel for viewing an invoice summary and entering a payment.
 * Uses BillingService + DAOs to talk to the database.
 */
public class InvoicePaymentPanel extends JPanel {

    private final BillingService billingService = new BillingService();

    private JLabel lblInvID, lblTotal, lblStatus;
    private JTextField txtPayDate, txtAmount;
    private JComboBox<String> cbMethod;
    private JButton btnSave, btnLoad, btnCreate;

    private Integer currentInvoiceId;

    public InvoicePaymentPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        UiTheme.paintPanelBackground(this);

        // --- INVOICE SUMMARY SECTION ---
        JPanel pnlInvoice = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlInvoice.setBorder(UiTheme.sectionBorder("Invoice summary"));
        UiTheme.paintPanelBackground(pnlInvoice);

        pnlInvoice.add(new JLabel("Invoice ID:"));
        lblInvID = new JLabel("---"); // Populated after loading an invoice
        lblInvID.setForeground(new Color(30, 41, 59));
        lblInvID.setFont(lblInvID.getFont().deriveFont(Font.BOLD, 14f));
        pnlInvoice.add(lblInvID);

        pnlInvoice.add(new JLabel("Total Amount:"));
        lblTotal = new JLabel("$0.00");
        lblTotal.setForeground(new Color(30, 41, 59));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        pnlInvoice.add(lblTotal);

        pnlInvoice.add(new JLabel("Current Status:"));
        lblStatus = new JLabel("UNLOADED");
        lblStatus.setForeground(new Color(100, 116, 139));
        pnlInvoice.add(lblStatus);

        // --- PAYMENT ENTRY SECTION ---
        JPanel pnlPayment = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlPayment.setBorder(UiTheme.sectionBorder("Record payment"));
        UiTheme.paintPanelBackground(pnlPayment);

        pnlPayment.add(new JLabel("Payment Date (YYYY-MM-DD):"));
        txtPayDate = new JTextField();
        pnlPayment.add(txtPayDate);

        pnlPayment.add(new JLabel("Amount ($):"));
        txtAmount = new JTextField();
        pnlPayment.add(txtAmount);

        pnlPayment.add(new JLabel("Payment Method:"));
        cbMethod = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Check"});
        pnlPayment.add(cbMethod);

        btnSave = new JButton("Submit Payment");
        btnLoad = new JButton("Load Invoice...");
        btnCreate = new JButton("Create Invoice...");

        btnLoad.addActionListener(e -> onLoadInvoice());
        btnSave.addActionListener(e -> onSubmitPayment());
        btnCreate.addActionListener(e -> onCreateInvoice());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UiTheme.paintPanelBackground(bottom);
        UiTheme.styleToolbarButton(btnCreate);
        UiTheme.styleToolbarButton(btnLoad);
        UiTheme.stylePrimaryButton(btnSave);
        bottom.add(btnCreate);
        bottom.add(btnLoad);
        bottom.add(btnSave);

        add(pnlInvoice, BorderLayout.NORTH);
        add(pnlPayment, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void onLoadInvoice() {
        List<Invoice> invoices;
        List<Appointment> appointments;
        List<Customer> customers;
        try {
            invoices = new InvoiceDAO().findAll();
            appointments = new AppointmentDAO().findAll();
            customers = new CustomerDAO().findAll();
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load invoices: " + ex.getMessage());
            return;
        }
        if (invoices.isEmpty()) {
            UIHelper.showError(this, "No invoices found.");
            return;
        }

        JComboBox<InvoiceItem> invoiceCombo = new JComboBox<>();
        for (Invoice inv : invoices) {
            String custName = appointments.stream()
                .filter(a -> a.getAppointmentId() == inv.getAppointmentID())
                .findFirst()
                .map(a -> customers.stream()
                    .filter(c -> c.getCustomerId() == a.getCustomerId())
                    .findFirst()
                    .map(c -> c.getFirstName() + " " + c.getLastName())
                    .orElse("?"))
                .orElse("?");
            String label = String.format("ID: %d | %s | $%.2f | %s",
                inv.getInvoiceID(), custName, inv.getTotalAmount(), inv.getPaymentStatus());
            invoiceCombo.addItem(new InvoiceItem(inv.getInvoiceID(), label));
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Select Invoice:"), BorderLayout.NORTH);
        panel.add(invoiceCombo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Load Invoice",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        InvoiceItem selected = (InvoiceItem) invoiceCombo.getSelectedItem();
        if (selected == null) return;

        try {
            Invoice invoice = billingService.getInvoice(selected.id);
            if (invoice == null) {
                UIHelper.showError(this, "Invoice not found.");
                return;
            }
            currentInvoiceId = invoice.getInvoiceID();
            lblInvID.setText(String.valueOf(invoice.getInvoiceID()));
            lblTotal.setText(String.format("$%.2f", invoice.getTotalAmount()));
            lblStatus.setText(invoice.getPaymentStatus());
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load invoice: " + ex.getMessage());
        }
    }

    private void onSubmitPayment() {
        if (currentInvoiceId == null) {
            UIHelper.showError(this, "Load an invoice first.");
            return;
        }
        String date = txtPayDate.getText().trim();
        String amountStr = txtAmount.getText().trim();
        if (date.isEmpty() || amountStr.isEmpty()) {
            UIHelper.showError(this, "Payment date and amount are required.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Amount must be a valid number.");
            return;
        }
        String method = (String) cbMethod.getSelectedItem();
        try {
            Invoice updated = billingService.recordPayment(currentInvoiceId, date, amount, method);
            lblStatus.setText(updated.getPaymentStatus());
            
            // Get customer name for confirmation
            String customerName = billingService.getCustomerNameForInvoice(currentInvoiceId);
            
            // Show payment confirmation with customer info
            String message = String.format(
                "✓ Payment Confirmed!\n\n" +
                "Customer: %s\n" +
                "Invoice ID: %d\n" +
                "Amount: $%.2f\n" +
                "Payment Method: %s\n" +
                "Date: %s\n" +
                "Status: %s",
                customerName,
                currentInvoiceId,
                amount,
                method,
                date,
                updated.getPaymentStatus()
            );
            
            JOptionPane.showMessageDialog(this, message, "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE);
            
            txtAmount.setText("");
            txtPayDate.setText("");
        } catch (IllegalArgumentException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to record payment: " + ex.getMessage());
        }
    }

    private void onCreateInvoice() {
        List<Appointment> appointments;
        List<Customer> customers;
        List<Service> services;
        try {
            appointments = new AppointmentDAO().findAll();
            customers = new CustomerDAO().findAll();
            services = new ServiceDAO().findAll();
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load data: " + ex.getMessage());
            return;
        }
        if (appointments.isEmpty()) {
            UIHelper.showError(this, "No appointments found. Schedule an appointment first.");
            return;
        }

        // Step 1: Select appointment
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JComboBox<AppointmentItem> apptCombo = new JComboBox<>();
        for (Appointment a : appointments) {
            String custName = customers.stream()
                .filter(c -> c.getCustomerId() == a.getCustomerId())
                .findFirst()
                .map(c -> c.getFirstName() + " " + c.getLastName())
                .orElse("?");
            String label = String.format("ID: %d | %s | %s | %s",
                a.getAppointmentId(), custName,
                a.getAppointmentDate() != null ? a.getAppointmentDate().format(fmt) : "?",
                a.getStatus());
            apptCombo.addItem(new AppointmentItem(a.getAppointmentId(), label));
        }

        JPanel apptPanel = new JPanel(new BorderLayout(5, 5));
        apptPanel.add(new JLabel("Select Appointment:"), BorderLayout.NORTH);
        apptPanel.add(apptCombo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, apptPanel, "Create Invoice",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        AppointmentItem selected = (AppointmentItem) apptCombo.getSelectedItem();
        if (selected == null) return;
        int appointmentId = selected.id;

        // Step 2: Invoice date
        String date = JOptionPane.showInputDialog(this, "Invoice Date (YYYY-MM-DD):", "Create Invoice", JOptionPane.QUESTION_MESSAGE);
        if (date == null || date.trim().isEmpty()) return;

        // Step 3: Service dropdown + editable total amount
        JComboBox<ServiceItem> serviceCombo = new JComboBox<>();
        serviceCombo.addItem(new ServiceItem("-- Select service --", 0.0));
        for (Service s : services) {
            serviceCombo.addItem(new ServiceItem(
                s.getServiceName(),
                s.getBasePrice() != null ? s.getBasePrice().doubleValue() : 0.0));
        }
        JTextField totalField = new JTextField(10);
        serviceCombo.addActionListener(e -> {
            ServiceItem si = (ServiceItem) serviceCombo.getSelectedItem();
            if (si != null && si.price > 0) {
                totalField.setText(String.format("%.2f", si.price));
            } else {
                totalField.setText("");
            }
        });

        JPanel totalPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        totalPanel.add(new JLabel("Service:"));
        totalPanel.add(serviceCombo);
        totalPanel.add(new JLabel("Total Amount ($):"));
        totalPanel.add(totalField);

        int totalResult = JOptionPane.showConfirmDialog(this, totalPanel, "Create Invoice",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (totalResult != JOptionPane.OK_OPTION) return;

        String totalStr = totalField.getText().trim();
        if (totalStr.isEmpty()) {
            UIHelper.showError(this, "Total amount is required.");
            return;
        }
        double total;
        try {
            total = Double.parseDouble(totalStr);
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Total amount must be a valid number.");
            return;
        }

        try {
            Invoice invoice = billingService.createInvoice(appointmentId, date, total);
            currentInvoiceId = invoice.getInvoiceID();
            lblInvID.setText(String.valueOf(invoice.getInvoiceID()));
            lblTotal.setText(String.format("$%.2f", invoice.getTotalAmount()));
            lblStatus.setText(invoice.getPaymentStatus());
            UIHelper.showMessage(this, "Invoice created. ID = " + invoice.getInvoiceID());
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to create invoice: " + ex.getMessage());
        }
    }

    private static class AppointmentItem {
        final int id;
        private final String label;
        AppointmentItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class InvoiceItem {
        final int id;
        private final String label;
        InvoiceItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class ServiceItem {
        final String name;
        final double price;
        ServiceItem(String name, double price) { this.name = name; this.price = price; }
        @Override public String toString() {
            return price > 0 ? name + " ($" + String.format("%.2f", price) + ")" : name;
        }
    }
}

