package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Vehicle table.
 */
public class VehicleDAO {

    private static final String INSERT = "INSERT INTO vehicle (customer_id, vin, make, model, \"year\", license_plate) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE vehicle SET customer_id=?, vin=?, make=?, model=?, \"year\"=?, license_plate=? WHERE vehicle_id=?";
    private static final String DELETE = "DELETE FROM vehicle WHERE vehicle_id=?";
    private static final String FIND_BY_ID = "SELECT * FROM vehicle WHERE vehicle_id=?";
    private static final String FIND_ALL = "SELECT * FROM vehicle ORDER BY make, model, \"year\"";
    private static final String FIND_BY_CUSTOMER = "SELECT * FROM vehicle WHERE customer_id=? ORDER BY make, model";

    public int insert(Vehicle v) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setVehicleParams(ps, v);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setVehicleId(rs.getInt(1));
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(Vehicle v) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            setVehicleParams(ps, v);
            ps.setInt(7, v.getVehicleId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int vehicleId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // First get all appointments for this vehicle
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
            
            // Delete each appointment (which handles cascading deletes)
            for (Integer appointmentId : appointmentIds) {
                String deletePayments = "DELETE FROM Payment WHERE Invoice_ID IN (SELECT Invoice_ID FROM Invoice WHERE Appointment_ID=?)";
                try (PreparedStatement ps = conn.prepareStatement(deletePayments)) {
                    ps.setInt(1, appointmentId);
                    ps.executeUpdate();
                }
                
                String deleteInvoices = "DELETE FROM Invoice WHERE Appointment_ID=?";
                try (PreparedStatement ps = conn.prepareStatement(deleteInvoices)) {
                    ps.setInt(1, appointmentId);
                    ps.executeUpdate();
                }
                
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
            
            // Reset appointment auto-increment
            try (PreparedStatement ps = conn.prepareStatement("ALTER TABLE Appointment ALTER COLUMN Appointment_ID RESTART WITH 1")) {
                ps.executeUpdate();
            }
            
            // Finally delete the vehicle
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, vehicleId);
                boolean result = ps.executeUpdate() > 0;
                
                // Reset vehicle auto-increment
                if (result) {
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE vehicle ALTER COLUMN Vehicle_ID RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                }
                
                return result;
            }
        }
    }

    public Vehicle findById(int vehicleId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Vehicle> findByCustomerId(int customerId) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_CUSTOMER)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private static void setVehicleParams(PreparedStatement ps, Vehicle v) throws SQLException {
        ps.setInt(1, v.getCustomerId());
        ps.setString(2, v.getVin());
        ps.setString(3, v.getMake());
        ps.setString(4, v.getModel());
        ps.setInt(5, v.getYear());
        ps.setString(6, v.getLicensePlate());
    }

    private static Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setCustomerId(rs.getInt("customer_id"));
        v.setVin(rs.getString("vin"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setLicensePlate(rs.getString("license_plate"));
        Timestamp t = rs.getTimestamp("created_at");
        v.setCreatedAt(t != null ? t.toLocalDateTime() : null);
        t = rs.getTimestamp("updated_at");
        v.setUpdatedAt(t != null ? t.toLocalDateTime() : null);
        return v;
    }
}
