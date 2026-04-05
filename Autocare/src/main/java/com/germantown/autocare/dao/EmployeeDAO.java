package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for employee table.
 */
public class EmployeeDAO {

    private static final String INSERT =
            "INSERT INTO employee (first_name, last_name, job_title, email, phone, hire_date, hourly_rate) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE employee SET first_name=?, last_name=?, job_title=?, email=?, phone=?, hire_date=?, hourly_rate=? " +
            "WHERE employee_id=?";
    private static final String DELETE    = "DELETE FROM employee WHERE employee_id=?";
    private static final String FIND_BY_ID = "SELECT * FROM employee WHERE employee_id=?";
    private static final String FIND_ALL   = "SELECT * FROM employee ORDER BY last_name, first_name";

    public int insert(Employee e) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, e);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    e.setEmployeeId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(Employee e) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            setParams(ps, e);
            ps.setInt(8, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int employeeId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    public Employee findById(int employeeId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static void setParams(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getFirstName());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getJobTitle());
        ps.setString(4, e.getEmail());
        ps.setString(5, e.getPhone());
        ps.setDate(6, e.getHireDate() != null ? Date.valueOf(e.getHireDate()) : null);
        ps.setBigDecimal(7, e.getHourlyRate());
    }

    private static Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("employee_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setJobTitle(rs.getString("job_title"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        Date hireDate = rs.getDate("hire_date");
        e.setHireDate(hireDate != null ? hireDate.toLocalDate() : null);
        e.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        Timestamp t = rs.getTimestamp("created_at");
        e.setCreatedAt(t != null ? t.toLocalDateTime() : null);
        t = rs.getTimestamp("updated_at");
        e.setUpdatedAt(t != null ? t.toLocalDateTime() : null);
        return e;
    }
}
