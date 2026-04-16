package com.germantown.autocare.config;

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
     */
    private static void loadPropertiesFile() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                host = props.getProperty("db.host", "localhost");
                port = Integer.parseInt(props.getProperty("db.port", "3306"));
                database = props.getProperty("db.database", "autocare_db");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "root");
                System.out.println("Database config loaded from properties file");
            }
        } catch (IOException e) {
            System.out.println("config.properties not found, using default values: " + e.getMessage());
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