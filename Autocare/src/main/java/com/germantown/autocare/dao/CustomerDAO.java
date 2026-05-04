package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Customer table.
 */
public class CustomerDAO {

    private static final String INSERT = "INSERT INTO customer (customer_id, first_name, last_name, email, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_IDS = "SELECT customer_id FROM customer ORDER BY customer_id";
    private static final String UPDATE = "UPDATE customer SET first_name=?, last_name=?, email=?, phone=?, address=? WHERE customer_id=?";
    private static final String DELETE = "DELETE FROM customer WHERE customer_id=?";
    private static final String FIND_BY_ID = "SELECT * FROM customer WHERE customer_id=?";
    private static final String FIND_ALL = "SELECT * FROM customer ORDER BY last_name, first_name";

    public int insert(Customer c) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            int nextId = findLowestAvailableCustomerId(conn);
            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setInt(1, nextId);
                ps.setString(2, c.getFirstName());
                ps.setString(3, c.getLastName());
                ps.setString(4, c.getEmail());
                ps.setString(5, c.getPhone());
                ps.setString(6, c.getAddress());
                ps.executeUpdate();
            }
            c.setCustomerId(nextId);
            return nextId;
        }
    }

    /** Returns the lowest customer_id not yet used (reuses IDs after deletes). */
    private int findLowestAvailableCustomerId(Connection conn) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_IDS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("customer_id"));
            }
        }
        int next = 1;
        for (Integer id : ids) {
            if (id > next) return next;
            next = id + 1;
        }
        return next;
    }

    public boolean update(Customer c) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int customerId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // Get all vehicles for this customer
            String getVehicles = "SELECT Vehicle_ID FROM vehicle WHERE customer_id=?";
            List<Integer> vehicleIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(getVehicles)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        vehicleIds.add(rs.getInt("Vehicle_ID"));
                    }
                }
            }
            
            // For each vehicle, delete all dependent appointments and their data
            for (Integer vehicleId : vehicleIds) {
                // Get all appointments for this vehicle
                String getAppointments = "SELECT Appointment_ID FROM Appointment WHERE Vehicle_ID=?";
                List<Integer> appointmentIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(getAppointments)) {
                    ps.setInt(1, vehicleId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            appointmentIds.add(rs.getInt("Appointment_ID"));
                        }
                    }
                }
                
                // Delete all dependent records for each appointment
                for (Integer appointmentId : appointmentIds) {
                    // Delete payments for this appointment's invoices
                    String deletePayments = "DELETE FROM Payment WHERE Invoice_ID IN (SELECT Invoice_ID FROM Invoice WHERE Appointment_ID=?)";
                    try (PreparedStatement ps = conn.prepareStatement(deletePayments)) {
                        ps.setInt(1, appointmentId);
                        ps.executeUpdate();
                    }
                    
                    // Delete invoices for this appointment
                    String deleteInvoices = "DELETE FROM Invoice WHERE Appointment_ID=?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteInvoices)) {
                        ps.setInt(1, appointmentId);
                        ps.executeUpdate();
                    }
                    
                    // Delete appointment services for this appointment
                    String deleteAppointmentServices = "DELETE FROM Appointment_Service WHERE Appointment_ID=?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteAppointmentServices)) {
                        ps.setInt(1, appointmentId);
                        ps.executeUpdate();
                    }
                }
                
                // Delete all appointments for this vehicle
                String deleteAppointments = "DELETE FROM Appointment WHERE Vehicle_ID=?";
                try (PreparedStatement ps = conn.prepareStatement(deleteAppointments)) {
                    ps.setInt(1, vehicleId);
                    ps.executeUpdate();
                }
                
                // Reset vehicle auto-increment
                try (PreparedStatement ps = conn.prepareStatement("ALTER TABLE vehicle ALTER COLUMN Vehicle_ID RESTART WITH 1")) {
                    ps.executeUpdate();
                }
            }
            
            // Delete all vehicles for this customer
            String deleteVehicles = "DELETE FROM vehicle WHERE customer_id=?";
            try (PreparedStatement ps = conn.prepareStatement(deleteVehicles)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
            }
            
            // Reset vehicle auto-increment
            try (PreparedStatement ps = conn.prepareStatement("ALTER TABLE vehicle ALTER COLUMN Vehicle_ID RESTART WITH 1")) {
                ps.executeUpdate();
            }
            
            // Reset appointment auto-increment
            try (PreparedStatement ps = conn.prepareStatement("ALTER TABLE Appointment ALTER COLUMN Appointment_ID RESTART WITH 1")) {
                ps.executeUpdate();
            }
            
            // Finally delete the customer
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, customerId);
                boolean result = ps.executeUpdate() > 0;
                
                // Reset customer auto-increment
                if (result) {
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE customer ALTER COLUMN customer_id RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                }
                
                return result;
            }
        }
    }

    public Customer findById(int customerId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        Timestamp t = rs.getTimestamp("created_at");
        c.setCreatedAt(t != null ? t.toLocalDateTime() : null);
        t = rs.getTimestamp("updated_at");
        c.setUpdatedAt(t != null ? t.toLocalDateTime() : null);
        return c;
    }
}
