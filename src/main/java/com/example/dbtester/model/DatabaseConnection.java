package com.example.dbtester.model;

public class DatabaseConnection {
    private String name;
    private String driverClassName;
    private String originalClassName;
    private String jarFile;// 원본 클래스 이름 (선택적)
    private String jdbcUrl;
    private String username;
    private String password;
    private String testQuery;

    // 기본 생성자
    public DatabaseConnection() {}

    // 전체 생성자
    public DatabaseConnection(String name, String driverClassName, String originalClassName, String jarFile,
                              String jdbcUrl, String username, String password, String testQuery) {
        this.name = name;
        this.driverClassName = driverClassName;
        this.originalClassName = originalClassName;
        this.jarFile = jarFile;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.testQuery = testQuery;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDriverClassName() { return driverClassName; }
    public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }

    public String getOriginalClassName() { return originalClassName; }
    public void setOriginalClassName(String originalClassName) { this.originalClassName = originalClassName; }

    public String getJarFile() { return jarFile; }
    public void setJarFile(String jarFile) { this.jarFile = jarFile; }

    public String getJdbcUrl() { return jdbcUrl; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTestQuery() { return testQuery; }
    public void setTestQuery(String testQuery) { this.testQuery = testQuery; }

    @Override
    public String toString() {
        return "DatabaseConnection{" +
                "name='" + name + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", originalClassName='" + originalClassName + '\'' +
                ", jarFile='" + jarFile + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", username='" + username + '\'' +
                ", testQuery='" + testQuery + '\'' +
                '}';
    }
}