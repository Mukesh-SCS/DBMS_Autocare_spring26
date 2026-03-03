package com.germantown.autocare.dao;

import com.germantown.autocare.config.DBConnection;
import com.germantown.autocare.model.Part;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Part table (inventory).
 */
public class PartDAO {

    private static final String INSERT = "INSERT INTO Part (Part_Name, Description, Unit_Price, Quantity_In_Stock, Reorder_Level) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE Part SET Part_Name=?, Description=?, Unit_Price=?, Quantity_In_Stock=?, Reorder_Level=? WHERE Part_ID=?";
    private static final String DELETE = "DELETE FROM Part WHERE Part_ID=?";
    private static final String FIND_BY_ID = "SELECT * FROM Part WHERE Part_ID=?";
    private static final String FIND_ALL = "SELECT * FROM Part ORDER BY Part_Name";

    public int insert(Part p) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setPartId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(Part p) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            setParams(ps, p);
            ps.setInt(6, p.getPartId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int partId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, partId);
            return ps.executeUpdate() > 0;
        }
    }

    public Part findById(int partId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Part> findAll() throws SQLException {
        List<Part> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static void setParams(PreparedStatement ps, Part p) throws SQLException {
        ps.setString(1, p.getPartName());
        ps.setString(2, p.getDescription());
        ps.setDouble(3, p.getUnitPrice());
        ps.setInt(4, p.getQuantityInStock());
        ps.setInt(5, p.getReorderLevel());
    }

    private static Part mapRow(ResultSet rs) throws SQLException {
        Part p = new Part();
        p.setPartId(rs.getInt("Part_ID"));
        p.setPartName(rs.getString("Part_Name"));
        p.setDescription(rs.getString("Description"));
        p.setUnitPrice(rs.getDouble("Unit_Price"));
        p.setQuantityInStock(rs.getInt("Quantity_In_Stock"));
        p.setReorderLevel(rs.getInt("Reorder_Level"));
        return p;
    }
}

