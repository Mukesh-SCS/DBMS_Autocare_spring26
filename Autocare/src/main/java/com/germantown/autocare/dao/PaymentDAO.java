package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}