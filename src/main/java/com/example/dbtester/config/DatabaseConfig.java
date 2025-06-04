package com.example.dbtester.config;


import com.example.dbtester.model.DatabaseConnection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    private String driverPath;
    private String defaultDatabase;
    private Map<String, DatabaseConnection> connections = new HashMap<>();

    public String getDriverPath() {
        return driverPath;
    }
    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    public Map<String, DatabaseConnection> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, DatabaseConnection> connections) {
        this.connections = connections;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    public DatabaseConnection getDefaultConnection() {
        return connections.get(defaultDatabase);
    }

    public DatabaseConnection getConnection(String key) {
        return connections.get(key);
    }
}