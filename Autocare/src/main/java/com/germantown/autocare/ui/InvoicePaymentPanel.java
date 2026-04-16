package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.germantown.autocare.model.Invoice;
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
        String input = JOptionPane.showInputDialog(this, "Enter Invoice ID:", "Load Invoice", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        int id;
        try {
            id = Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Invoice ID must be a number.");
            return;
        }
        try {
            Invoice invoice = billingService.getInvoice(id);
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
        String apptInput = JOptionPane.showInputDialog(this, "Enter Appointment ID:", "Create Invoice", JOptionPane.QUESTION_MESSAGE);
        if (apptInput == null || apptInput.trim().isEmpty()) return;
        int appointmentId;
        try {
            appointmentId = Integer.parseInt(apptInput.trim());
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Appointment ID must be a number.");
            return;
        }

        String date = JOptionPane.showInputDialog(this, "Invoice Date (YYYY-MM-DD):", "Create Invoice", JOptionPane.QUESTION_MESSAGE);
        if (date == null || date.trim().isEmpty()) return;

        String totalStr = JOptionPane.showInputDialog(this, "Total Amount ($):", "Create Invoice", JOptionPane.QUESTION_MESSAGE);
        if (totalStr == null || totalStr.trim().isEmpty()) return;
        double total;
        try {
            total = Double.parseDouble(totalStr.trim());
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
}

