package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}