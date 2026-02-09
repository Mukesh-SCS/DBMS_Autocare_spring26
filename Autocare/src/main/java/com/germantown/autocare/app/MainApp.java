package com.germantown.autocare.app;

import com.germantown.autocare.config.DBConnection;

import java.sql.Connection;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful.");
                System.out.println("Connected to: " + conn.getCatalog());
            } else {
                System.out.println("Database connection failed: connection is null or closed.");
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
