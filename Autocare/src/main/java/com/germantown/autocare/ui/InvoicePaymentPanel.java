package UI;

import javax.swing.*;
import java.awt.*;

public class PaymentInvoiceUI extends JFrame {
    // UI Components for Invoice Summary
    private JLabel lblInvID, lblTotal, lblStatus;

    // UI Components for Payment Entry [cite: 93, 193]
    private JTextField txtPayDate, txtAmount;
    private JComboBox<String> cbMethod;
    private JButton btnSave;

    public PaymentInvoiceUI() {
        setTitle("Germantown AutoCare - Billing & Payments"); [cite: 109]
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        cbMethod = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Check"}); [cite: 154]
        pnlPayment.add(cbMethod);

        btnSave = new JButton("Submit Payment");

        // Assemble Layout
        add(pnlInvoice, BorderLayout.NORTH);
        add(pnlPayment, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);

        setVisible(true);
    }
}