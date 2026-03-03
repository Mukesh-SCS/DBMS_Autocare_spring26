package com.germantown.autocare.model;

public class Invoice {
    private int invoiceID;
    private int appointmentID;
    private String invoiceDate;
    private double totalAmount;
    private String paymentStatus;

    // Constructor for creating new invoices
    public Invoice(int appointmentID, String invoiceDate, double totalAmount, String paymentStatus) {
        this.appointmentID = appointmentID;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }
    public int getAppointmentID() { return appointmentID; }
    public String getInvoiceDate() { return invoiceDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}