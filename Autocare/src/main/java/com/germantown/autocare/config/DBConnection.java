package com.germantown.autocare.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection handler using embedded H2 database.
 * No external database installation required - fully self-contained and portable.
 * Database file is stored in the user's home directory.
 */
public class DBConnection {
    private static final String DB_NAME = "autocare_db";
    private static final String DB_PATH;
    private static final String H2_JDBC_URL;
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    static {
        // Store database file in user's home directory
        String userHome = System.getProperty("user.home");
        String dbDir = new File(userHome, ".autocare").getAbsolutePath();
        DB_PATH = dbDir + "/" + DB_NAME;
        
        // Ensure directory exists
        new File(dbDir).mkdirs();
        
        // H2 JDBC URL for file-based database with compatibility mode
        H2_JDBC_URL = "jdbc:h2:" + DB_PATH + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        
        System.out.println("Using embedded H2 database at: " + DB_PATH);
    }

    /**
     * Get connection to embedded H2 database
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(H2_JDBC_URL, H2_USERNAME, H2_PASSWORD);
    }

    // Getters for current settings
    public static String getDatabase() { return DB_NAME; }
    public static String getUsername() { return H2_USERNAME; }
    public static String getDatabasePath() { return DB_PATH; }
}