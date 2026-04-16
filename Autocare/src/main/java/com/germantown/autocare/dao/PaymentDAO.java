package com.germantown.autocare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Payment;

public class PaymentDAO {
    // Method to record a customer payment
    public void recordPayment(Payment payment) {
        String query = "INSERT INTO Payment (Invoice_ID, Payment_Date, Payment_Amount, Payment_Method) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, payment.getInvoiceID());
            pstmt.setString(2, payment.getPaymentDate());
            pstmt.setDouble(3, payment.getPaymentAmount());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Returns total amount paid so far for a given invoice.
    public double getTotalPaidForInvoice(int invoiceId) {
        String sql = "SELECT COALESCE(SUM(Payment_Amount), 0) AS total FROM Payment WHERE Invoice_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get all payments with customer and invoice information
     */
    public List<Map<String, Object>> getPaymentHistory() throws SQLException {
        List<Map<String, Object>> history = new ArrayList<>();
        String sql = "SELECT p.Payment_ID, p.Invoice_ID, p.Payment_Date, p.Payment_Amount, p.Payment_Method, " +
                     "i.Appointment_ID, c.first_name, c.last_name, i.Total_Amount, i.Payment_Status " +
                     "FROM Payment p " +
                     "JOIN Invoice i ON p.Invoice_ID = i.Invoice_ID " +
                     "JOIN Appointment a ON i.Appointment_ID = a.Appointment_ID " +
                     "JOIN customer c ON a.Customer_ID = c.customer_id " +
                     "ORDER BY p.Payment_Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> payment = new HashMap<>();
                payment.put("paymentId", rs.getInt("Payment_ID"));
                payment.put("invoiceId", rs.getInt("Invoice_ID"));
                payment.put("appointmentId", rs.getInt("Appointment_ID"));
                payment.put("paymentDate", rs.getString("Payment_Date"));
                payment.put("paymentAmount", rs.getDouble("Payment_Amount"));
                payment.put("paymentMethod", rs.getString("Payment_Method"));
                payment.put("customerName", rs.getString("first_name") + " " + rs.getString("last_name"));
                payment.put("totalAmount", rs.getDouble("Total_Amount"));
                payment.put("paymentStatus", rs.getString("Payment_Status"));
                history.add(payment);
            }
        }
        return history;
    }

    /**
     * Get all payments for a specific customer
     */
    public List<Map<String, Object>> getPaymentHistoryByCustomer(int customerId) {
        List<Map<String, Object>> history = new ArrayList<>();
        String sql = "SELECT p.Payment_ID, p.Invoice_ID, p.Payment_Date, p.Payment_Amount, p.Payment_Method, " +
                     "i.Appointment_ID, c.first_name, c.last_name, i.Total_Amount, i.Payment_Status " +
                     "FROM Payment p " +
                     "JOIN Invoice i ON p.Invoice_ID = i.Invoice_ID " +
                     "JOIN Appointment a ON i.Appointment_ID = a.Appointment_ID " +
                     "JOIN customer c ON a.Customer_ID = c.customer_id " +
                     "WHERE a.Customer_ID = ? " +
                     "ORDER BY p.Payment_Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> payment = new HashMap<>();
                    payment.put("paymentId", rs.getInt("Payment_ID"));
                    payment.put("invoiceId", rs.getInt("Invoice_ID"));
                    payment.put("appointmentId", rs.getInt("Appointment_ID"));
                    payment.put("paymentDate", rs.getString("Payment_Date"));
                    payment.put("paymentAmount", rs.getDouble("Payment_Amount"));
                    payment.put("paymentMethod", rs.getString("Payment_Method"));
                    payment.put("customerName", rs.getString("first_name") + " " + rs.getString("last_name"));
                    payment.put("totalAmount", rs.getDouble("Total_Amount"));
                    payment.put("paymentStatus", rs.getString("Payment_Status"));
                    history.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get customer name from invoice
     */
    public String getCustomerNameForInvoice(int invoiceId) {
        String sql = "SELECT c.first_name, c.last_name FROM customer c " +
                     "JOIN Appointment a ON c.customer_id = a.Customer_ID " +
                     "JOIN Invoice i ON a.Appointment_ID = i.Appointment_ID " +
                     "WHERE i.Invoice_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}