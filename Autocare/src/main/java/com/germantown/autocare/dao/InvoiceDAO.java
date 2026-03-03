package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    // Method to create a new invoice after an appointment is completed.
    // Returns the generated Invoice_ID and sets it on the Invoice object.
    public int createInvoice(Invoice invoice) {
        String query = "INSERT INTO Invoice (Appointment_ID, Invoice_Date, Total_Amount, Payment_Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, invoice.getAppointmentID());
            pstmt.setString(2, invoice.getInvoiceDate());
            pstmt.setDouble(3, invoice.getTotalAmount());
            pstmt.setString(4, invoice.getPaymentStatus());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    invoice.setInvoiceID(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Method to fetch an invoice by ID for the UI summary screen
    public Invoice getInvoiceByID(int invoiceID) {
        String query = "SELECT * FROM Invoice WHERE Invoice_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, invoiceID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Returns all invoices that are not yet fully paid.
    public List<Invoice> findUnpaidInvoices() {
        String query = "SELECT * FROM Invoice WHERE Payment_Status <> 'Paid'";
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Updates the status of an invoice (e.g., Unpaid, Partial, Paid).
    public void updateStatus(int invoiceId, String status) {
        String query = "UPDATE Invoice SET Payment_Status = ? WHERE Invoice_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, invoiceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice(
                rs.getInt("Appointment_ID"),
                rs.getString("Invoice_Date"),
                rs.getDouble("Total_Amount"),
                rs.getString("Payment_Status")
        );
        invoice.setInvoiceID(rs.getInt("Invoice_ID"));
        return invoice;
    }
}