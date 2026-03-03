package DAO;

import Model.Payment;
import java.sql.*;

public class PaymentDAO {
    // Method to record a customer payment
    public void recordPayment(Payment payment) {
        String query = "INSERT INTO Payment (Invoice_ID, Payment_Date, Payment_Amount, Payment_Method) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, payment.getInvoiceID());
            pstmt.setString(2, payment.getPaymentDate());
            pstmt.setDouble(3, payment.getPaymentAmount());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.executeUpdate();

            // Logic to update Invoice status (e.g., to 'Paid') could be added here
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}