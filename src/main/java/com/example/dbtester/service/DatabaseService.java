package com.example.dbtester.service;


import com.example.dbtester.config.DatabaseConfig;
import com.example.dbtester.model.DatabaseConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Autowired
    private DatabaseConfig databaseConfig;

    private HikariDataSource currentDataSource;
    private String currentDatabaseKey;

    public boolean testConnection(String databaseKey) {
        logger.info("=== 데이터베이스 연결 테스트 시작: {} ===", databaseKey);

        DatabaseConnection dbConfig = databaseConfig.getConnection(databaseKey);

        if (dbConfig == null) {
            logger.error("데이터베이스 설정을 찾을 수 없습니다: {}", databaseKey);
            return false;
        }

        try {
            // 새로운 데이터소스 생성
            HikariDataSource testDataSource = createDataSource(dbConfig);

            // 연결 테스트
            try (Connection connection = testDataSource.getConnection()) {
                logger.info("데이터베이스 연결 성공: {}", dbConfig.getName());

                // 테스트 쿼리 실행
                if (dbConfig.getTestQuery() != null && !dbConfig.getTestQuery().trim().isEmpty()) {
                    executeTestQuery(connection, dbConfig.getTestQuery());
                }

                logger.info("데이터베이스 연결 테스트 완료: {}", dbConfig.getName());
                return true;

            } finally {
                testDataSource.close();
            }

        } catch (Exception e) {
            logger.error("데이터베이스 연결 실패: {} - {}", dbConfig.getName(), e.getMessage(), e);
            return false;
        }
    }

    public boolean switchDatabase(String databaseKey) {
        logger.info("=== 데이터베이스 변경 시작: {} ===", databaseKey);

        DatabaseConnection dbConfig = databaseConfig.getConnection(databaseKey);
        if (dbConfig == null) {
            logger.error("데이터베이스 설정을 찾을 수 없습니다: {}", databaseKey);
            return false;
        }

        try {
            // 기존 연결 종료
            if (currentDataSource != null) {
                currentDataSource.close();
                logger.info("기존 데이터베이스 연결 종료: {}", currentDatabaseKey);
            }

            // 새로운 데이터소스 생성
            currentDataSource = createDataSource(dbConfig);
            currentDatabaseKey = databaseKey;

            // 연결 테스트
            try (Connection connection = currentDataSource.getConnection()) {
                logger.info("새로운 데이터베이스 연결 성공: {}", dbConfig.getName());
                return true;
            }

        } catch (Exception e) {
            logger.error("데이터베이스 변경 실패: {} - {}", dbConfig.getName(), e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        logger.info("=== SQL 쿼리 실행 시작 ===");
        logger.info("SQL: {}", sql);

        if (currentDataSource == null) {
            logger.error("활성 데이터베이스 연결이 없습니다. 먼저 데이터베이스를 연결하세요.");
            return new ArrayList<>();
        }

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = currentDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            logger.info("쿼리 실행 성공. 컬럼 수: {}", columnCount);

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            logger.info("쿼리 결과: {} 행", results.size());

        } catch (Exception e) {
            logger.error("SQL 쿼리 실행 실패: {}", e.getMessage());
        }

        return results;
    }

    private void executeTestQuery(Connection connection, String testQuery) {
        try (PreparedStatement statement = connection.prepareStatement(testQuery);
             ResultSet resultSet = statement.executeQuery()) {

            logger.info("테스트 쿼리 실행: {}", testQuery);

            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    logger.info("  {}: {}", columnName, value);
                }
            }

        } catch (Exception e) {
            logger.warn("테스트 쿼리 실행 실패: {}", e.getMessage());
        }
    }

    private HikariDataSource createDataSource(DatabaseConnection dbConfig) {
        String jarPath = databaseConfig.getDriverPath() + "/" + dbConfig.getJarFile();
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            File jarFile = new File(jarPath);
            if (!jarFile.exists() || !jarFile.isFile()) {
                logger.error("JDBC 드라이버 JAR 파일을 찾을 수 없습니다: {}", jarPath);
                throw new RuntimeException("JDBC 드라이버 JAR 파일을 찾을 수 없습니다: " + jarPath);
            }
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, originalClassLoader);
            Class.forName(dbConfig.getOriginalClassName(), true, classLoader);
            logger.info("JDBC 드라이버 로드 성공: {}", dbConfig.getOriginalClassName());
            Thread.currentThread().setContextClassLoader(classLoader);
            System.out.println("Thread Context ClassLoader를 customClassLoader로 설정했습니다.");
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(dbConfig.getDriverClassName());
            config.setJdbcUrl(dbConfig.getJdbcUrl());
            config.setUsername(dbConfig.getUsername());
            config.setPassword(dbConfig.getPassword());
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            HikariDataSource ds = new HikariDataSource(config);
            // HikariDataSource 생성 후에만 원래의 ClassLoader로 복원
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            return ds;
        } catch (ClassNotFoundException e) {
            logger.error("JDBC 드라이버 로드 실패: {}", e.getMessage());
            throw new RuntimeException("JDBC 드라이버를 찾을 수 없습니다: " + dbConfig.getDriverClassName(), e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentDatabaseKey() {
        return currentDatabaseKey;
    }

    public Map<String, String> getAvailableDatabases() {
        Map<String, String> databases = new HashMap<>();
        for (Map.Entry<String, DatabaseConnection> entry : databaseConfig.getConnections().entrySet()) {
            databases.put(entry.getKey(), entry.getValue().getName());
        }
        return databases;
    }

    @PreDestroy
    public void cleanup() {
        if (currentDataSource != null) {
            currentDataSource.close();
            logger.info("데이터베이스 연결 정리 완료");
        }
    }
}

