package com.germantown.autocare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String host = "localhost";
    private static int port = 3306;
    private static String database = "autocare_db";
    private static String username = "root";
    private static String password = "root";

    static {
        loadPropertiesFile();
    }

    /**
     * Load database configuration from config.properties file
     * First tries to load from disk (working directory / same directory as JAR),
     * then falls back to classpath resource, then uses hardcoded defaults.
     */
    private static void loadPropertiesFile() {
        Properties props = new Properties();
        
        // First, try to load from disk (working directory / same directory as JAR)
        File externalFile = new File("config.properties");
        if (externalFile.exists()) {
            try (InputStream input = new FileInputStream(externalFile)) {
                props.load(input);
                applyProperties(props);
                System.out.println("Database config loaded from disk: " + externalFile.getAbsolutePath());
                return;
            } catch (IOException e) {
                System.out.println("Failed to load config.properties from disk: " + e.getMessage());
            }
        }
        
        // Fall back to classpath resource
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                applyProperties(props);
                System.out.println("Database config loaded from classpath resource");
                return;
            }
        } catch (IOException e) {
            System.out.println("config.properties not found in classpath: " + e.getMessage());
        }
        
        // Use hardcoded defaults
        System.out.println("Using default database configuration");
    }
    
    /**
     * Apply properties from Properties object, with safe parsing for port
     */
    private static void applyProperties(Properties props) {
        host = props.getProperty("db.host", "localhost");
        database = props.getProperty("db.database", "autocare_db");
        username = props.getProperty("db.username", "root");
        password = props.getProperty("db.password", "root");
        
        // Parse port safely, default to 3306 if invalid
        try {
            port = Integer.parseInt(props.getProperty("db.port", "3306"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number in config, using default 3306: " + e.getMessage());
            port = 3306;
        }
    }

    /**
     * Get connection with current settings
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get connection with custom credentials (used during login)
     */
    public static Connection getConnection(String customHost, int customPort, String customDb, String customUser, String customPassword) throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s", customHost, customPort, customDb);
        return DriverManager.getConnection(url, customUser, customPassword);
    }

    /**
     * Update connection settings (usually called after successful login)
     */
    public static void setConnectionSettings(String newHost, int newPort, String newDatabase, String newUsername, String newPassword) {
        host = newHost;
        port = newPort;
        database = newDatabase;
        username = newUsername;
        password = newPassword;
    }

    // Getters for current settings
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDatabase() { return database; }
    public static String getUsername() { return username; }
}