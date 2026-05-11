package com.germantown.autocare.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Initializes the embedded H2 database with schema and sample data on first run.
 * This ensures the application is completely self-contained and works on any computer.
 */
public class DatabaseInitializer {
    
    private static final String SCHEMA_RESOURCE = "/schema.sql";
    private static final String SAMPLE_DATA_RESOURCE = "/sample_data.sql";
    private static volatile boolean initialized = false;
    
    /**
     * Initialize the database: create tables and insert sample data if not already done
     */
    public static synchronized void initializeDatabase() {
        if (initialized) {
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if tables already exist
            if (!tablesExist(conn)) {
                System.out.println("Creating database schema...");
                executeScript(conn, SCHEMA_RESOURCE);
                System.out.println("Database schema created successfully.");
                
                System.out.println("Inserting sample data...");
                executeScript(conn, SAMPLE_DATA_RESOURCE);
                System.out.println("Sample data inserted successfully.");
            } else {
                System.out.println("Database tables already exist. Skipping initialization.");
            }
            migrateAppointmentEmployeeColumn(conn);
            initialized = true;
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Check if the customer table exists (indicates database is initialized)
     */
    private static boolean tablesExist(Connection conn) {
        try {
            java.sql.ResultSet rs = conn.getMetaData().getTables(null, null, "CUSTOMER", null);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds Employee_ID to Appointment when upgrading an older embedded DB that was created before this column existed.
     */
    private static void migrateAppointmentEmployeeColumn(Connection conn) throws SQLException {
        if (appointmentHasEmployeeColumn(conn)) {
            return;
        }
        System.out.println("Migrating: adding Employee_ID to Appointment...");
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE Appointment ADD COLUMN Employee_ID INT NULL");
        }
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE Appointment ADD CONSTRAINT fk_appointment_employee "
                    + "FOREIGN KEY (Employee_ID) REFERENCES employee(employee_id) ON DELETE SET NULL");
        } catch (SQLException e) {
            System.err.println("Warning: could not add fk_appointment_employee (may already exist): " + e.getMessage());
        }
        System.out.println("Migration complete.");
    }

    private static boolean appointmentHasEmployeeColumn(Connection conn) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        String[] tableNames = { "APPOINTMENT", "Appointment" };
        for (String table : tableNames) {
            try (ResultSet rs = md.getColumns(null, null, table, null)) {
                while (rs.next()) {
                    if ("Employee_ID".equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Execute SQL script from resource file
     */
    private static void executeScript(Connection conn, String resourcePath) throws Exception {
        StringBuilder sqlScript = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(DatabaseInitializer.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip comments and empty lines
                if (!line.isEmpty() && !line.startsWith("--")) {
                    sqlScript.append(line).append(" ");
                }
            }
        }
        
        // Split by semicolons and execute each statement
        String[] statements = sqlScript.toString().split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                    } catch (Exception e) {
                        // Log but continue for some common non-critical errors
                        System.err.println("Warning executing statement: " + e.getMessage());
                    }
                }
            }
        }
    }
}
