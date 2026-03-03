package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Simple panel for viewing an invoice summary and entering a payment.
 * (Data wiring to Invoice/Payment tables can be added later.)
 */
public class InvoicePaymentPanel extends JPanel {

    private JLabel lblInvID, lblTotal, lblStatus;
    private JTextField txtPayDate, txtAmount;
    private JComboBox<String> cbMethod;
    private JButton btnSave;

    public InvoicePaymentPanel() {
        setLayout(new BorderLayout(15, 15));

        // --- INVOICE SUMMARY SECTION --- [cite: 97, 197]
        JPanel pnlInvoice = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlInvoice.setBorder(BorderFactory.createTitledBorder("Invoice Summary"));

        pnlInvoice.add(new JLabel("Invoice ID:"));
        lblInvID = new JLabel("---"); // To be populated from DB
        pnlInvoice.add(lblInvID);

        pnlInvoice.add(new JLabel("Total Amount:"));
        lblTotal = new JLabel("$0.00");
        pnlInvoice.add(lblTotal);

        pnlInvoice.add(new JLabel("Current Status:"));
        lblStatus = new JLabel("PENDING");
        pnlInvoice.add(lblStatus);

        // --- PAYMENT ENTRY SECTION --- [cite: 93, 193]
        JPanel pnlPayment = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlPayment.setBorder(BorderFactory.createTitledBorder("Record New Payment"));

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

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSave);

        add(pnlInvoice, BorderLayout.NORTH);
        add(pnlPayment, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}

