package com.germantown.autocare.model;

public class Payment {
    private int paymentID;
    private int invoiceID;
    private String paymentDate;
    private double paymentAmount;
    private String paymentMethod;

    // Constructor for recording a payment
    public Payment(int invoiceID, String paymentDate, double paymentAmount, String paymentMethod) {
        this.invoiceID = invoiceID;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public int getInvoiceID() { return invoiceID; }
    public String getPaymentDate() { return paymentDate; }
    public double getPaymentAmount() { return paymentAmount; }
    public String getPaymentMethod() { return paymentMethod; }
}