package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceDAO {
    // Method to create a new invoice after an appointment is completed
    public void createInvoice(Invoice invoice) {
        String query = "INSERT INTO Invoice (Appointment_ID, Invoice_Date, Total_Amount, Payment_Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, invoice.getAppointmentID());
            pstmt.setString(2, invoice.getInvoiceDate());
            pstmt.setDouble(3, invoice.getTotalAmount());
            pstmt.setString(4, invoice.getPaymentStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch an invoice by ID for the UI summary screen
    public Invoice getInvoiceByID(int invoiceID) {
        String query = "SELECT * FROM Invoice WHERE Invoice_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, invoiceID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Invoice(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Invoice_Date"),
                        rs.getDouble("Total_Amount"),
                        rs.getString("Payment_Status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}