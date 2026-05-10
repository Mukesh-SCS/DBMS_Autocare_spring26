package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for service table.
 */
public class ServiceDAO {

    private static final String INSERT =
            "INSERT INTO service (service_name, estimated_duration_minutes, base_price) VALUES (?, ?, ?)";
    private static final String UPDATE =
            "UPDATE service SET service_name=?, estimated_duration_minutes=?, base_price=? WHERE service_id=?";
    private static final String DELETE     = "DELETE FROM service WHERE service_id=?";
    private static final String FIND_BY_ID = "SELECT * FROM service WHERE service_id=?";
    private static final String FIND_ALL   = "SELECT * FROM service ORDER BY service_name";

    public int insert(Service s) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, s);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    s.setServiceId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(Service s) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            setParams(ps, s);
            ps.setInt(4, s.getServiceId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int serviceId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // First delete all appointment_service entries that reference this service
            String deleteAppointmentServices = "DELETE FROM Appointment_Service WHERE Service_ID=?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAppointmentServices)) {
                ps.setInt(1, serviceId);
                ps.executeUpdate();
            }
            
            // Delete all service_part entries that reference this service
            String deleteServiceParts = "DELETE FROM service_part WHERE service_id=?";
            try (PreparedStatement ps = conn.prepareStatement(deleteServiceParts)) {
                ps.setInt(1, serviceId);
                ps.executeUpdate();
            }
            
            // Finally delete the service
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, serviceId);
                boolean result = ps.executeUpdate() > 0;
                
                // Reset service auto-increment
                if (result) {
                    try (PreparedStatement resetPs = conn.prepareStatement("ALTER TABLE service ALTER COLUMN service_id RESTART WITH 1")) {
                        resetPs.executeUpdate();
                    }
                }
                
                return result;
            }
        }
    }

    public Service findById(int serviceId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Service> findAll() throws SQLException {
        List<Service> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static void setParams(PreparedStatement ps, Service s) throws SQLException {
        ps.setString(1, s.getServiceName());
        if (s.getEstimatedDurationMinutes() == null) {
            ps.setNull(2, Types.INTEGER);
        } else {
            ps.setInt(2, s.getEstimatedDurationMinutes());
        }
        ps.setBigDecimal(3, s.getBasePrice());
    }

    private static Service mapRow(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setServiceId(rs.getInt("service_id"));
        s.setServiceName(rs.getString("service_name"));
        int duration = rs.getInt("estimated_duration_minutes");
        s.setEstimatedDurationMinutes(rs.wasNull() ? null : duration);
        s.setBasePrice(rs.getBigDecimal("base_price"));
        Timestamp t = rs.getTimestamp("created_at");
        s.setCreatedAt(t != null ? t.toLocalDateTime() : null);
        t = rs.getTimestamp("updated_at");
        s.setUpdatedAt(t != null ? t.toLocalDateTime() : null);
        return s;
    }
}
