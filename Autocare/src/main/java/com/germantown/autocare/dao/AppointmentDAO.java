package com.germantown.autocare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Appointment;

/**
 * Data access for Appointment table.
 */
public class AppointmentDAO {

    private static final String INSERT = "INSERT INTO Appointment (Customer_ID, Vehicle_ID, Appointment_Date, Status, Notes) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE Appointment SET Status=? WHERE Appointment_ID=?";
    private static final String DELETE = "DELETE FROM Appointment WHERE Appointment_ID=?";
    private static final String FIND_BY_ID = "SELECT * FROM Appointment WHERE Appointment_ID=?";
    private static final String FIND_ALL = "SELECT * FROM Appointment ORDER BY Appointment_Date DESC";

    public int insert(Appointment a) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getCustomerId());
            ps.setInt(2, a.getVehicleId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setAppointmentId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean updateStatus(int appointmentId, String status) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int appointmentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // Delete related payments first
            String deletePayments = "DELETE FROM Payment WHERE Invoice_ID IN (SELECT Invoice_ID FROM Invoice WHERE Appointment_ID=?)";
            try (PreparedStatement ps = conn.prepareStatement(deletePayments)) {
                ps.setInt(1, appointmentId);
                ps.executeUpdate();
            }
            
            // Delete related invoices
            String deleteInvoices = "DELETE FROM Invoice WHERE Appointment_ID=?";
            try (PreparedStatement ps = conn.prepareStatement(deleteInvoices)) {
                ps.setInt(1, appointmentId);
                ps.executeUpdate();
            }
            
            // Delete appointment services
            String deleteAppointmentServices = "DELETE FROM Appointment_Service WHERE Appointment_ID=?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAppointmentServices)) {
                ps.setInt(1, appointmentId);
                ps.executeUpdate();
            }
            
            // Finally delete the appointment
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, appointmentId);
                boolean result = ps.executeUpdate() > 0;
                
                // Reset appointment auto-increment
                if (result) {
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE Appointment ALTER COLUMN Appointment_ID RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                    
                    // Reset invoice auto-increment
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE Invoice ALTER COLUMN Invoice_ID RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                    
                    // Reset payment auto-increment
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE Payment ALTER COLUMN Payment_ID RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                }
                
                return result;
            }
        }
    }

    public Appointment findById(int appointmentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Appointment> findAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("Appointment_ID"));
        a.setCustomerId(rs.getInt("Customer_ID"));
        a.setVehicleId(rs.getInt("Vehicle_ID"));
        Timestamp ts = rs.getTimestamp("Appointment_Date");
        LocalDateTime dt = ts != null ? ts.toLocalDateTime() : null;
        a.setAppointmentDate(dt);
        a.setStatus(rs.getString("Status"));
        a.setNotes(rs.getString("Notes"));
        return a;
    }

    /**
     * Check if an appointment has linked invoices
     * @return List of invoice IDs linked to this appointment, or empty list if none
     */
    public List<Integer> getLinkedInvoiceIds(int appointmentId) throws SQLException {
        List<Integer> invoiceIds = new ArrayList<>();
        String sql = "SELECT Invoice_ID FROM Invoice WHERE Appointment_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoiceIds.add(rs.getInt("Invoice_ID"));
                }
            }
        }
        return invoiceIds;
    }
}

