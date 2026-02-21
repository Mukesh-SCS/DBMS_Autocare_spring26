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
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
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
