package com.germantown.autocare.service;

import java.util.List;

import com.germantown.autocare.dao.InvoiceDAO;
import com.germantown.autocare.dao.PaymentDAO;
import com.germantown.autocare.model.Invoice;
import com.germantown.autocare.model.Payment;

/**
 * Service layer for billing: invoices and payments.
 */
public class BillingService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public Invoice getInvoice(int invoiceId) {
        return invoiceDAO.getInvoiceByID(invoiceId);
    }

    public List<Invoice> getUnpaidInvoices() {
        return invoiceDAO.findUnpaidInvoices();
    }

    /**
     * Creates a new invoice for an appointment and returns it with the generated ID.
     */
    public Invoice createInvoice(int appointmentId, String invoiceDate, double totalAmount) {
        Invoice invoice = new Invoice(appointmentId, invoiceDate, totalAmount, "Unpaid");
        invoiceDAO.createInvoice(invoice);
        return invoice;
    }

    /**
     * Records a payment for the given invoice and updates the invoice status
     * to Paid or Partial depending on total paid so far.
     */
    public Invoice recordPayment(int invoiceId, String paymentDate, double amount, String method) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        Invoice invoice = invoiceDAO.getInvoiceByID(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found: " + invoiceId);
        }

        Payment payment = new Payment(invoiceId, paymentDate, amount, method);
        paymentDAO.recordPayment(payment);

        double totalPaid = paymentDAO.getTotalPaidForInvoice(invoiceId);
        String newStatus = totalPaid >= invoice.getTotalAmount() ? "Paid" : "Partial";
        invoiceDAO.updateStatus(invoiceId, newStatus);
        invoice.setPaymentStatus(newStatus);
        return invoice;
    }
}

