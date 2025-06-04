package com.example.dbtester;


import com.example.dbtester.config.DatabaseConfig;
import com.example.dbtester.service.DatabaseService;
import com.example.dbtester.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DatabaseConfig.class)
public class DBTesterApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DBTesterApplication.class);

    @Autowired
    private DatabaseConfig databaseConfig;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private MenuService menuService;

    public static void main(String[] args) {
        System.setProperty("spring.main.web-application-type", "none");
        SpringApplication.run(DBTesterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== 데이터베이스 연결 테스터 시작 ===");

        // 초기 연결 테스트
        // performInitialConnectionTest();

        // 메인 메뉴 시작
        menuService.showMainMenu();

        logger.info("=== 데이터베이스 연결 테스터 종료 ===");
    }

    private void performInitialConnectionTest() {
        logger.info("=== 초기 연결 테스트 시작 ===");

        System.out.println("\n프로그램 시작 - 초기 데이터베이스 연결 테스트를 수행합니다...\n");

        String defaultDbKey = databaseConfig.getDefaultDatabase();
        if (defaultDbKey != null) {
            logger.info("기본 데이터베이스 연결 테스트: {}", defaultDbKey);
            System.out.println("기본 데이터베이스 (" + defaultDbKey + ") 연결 테스트 중...");

            boolean success = databaseService.testConnection(defaultDbKey);
            if (success) {
                System.out.println("✓ 기본 데이터베이스 연결 성공!");
                databaseService.switchDatabase(defaultDbKey);
                logger.info("기본 데이터베이스 활성화 완료: {}", defaultDbKey);
            } else {
                System.out.println("✗ 기본 데이터베이스 연결 실패!");
                logger.warn("기본 데이터베이스 연결 실패: {}", defaultDbKey);
            }
        } else {
            logger.warn("기본 데이터베이스가 설정되지 않았습니다.");
            System.out.println("기본 데이터베이스가 설정되지 않았습니다.");
        }

        logger.info("=== 초기 연결 테스트 완료 ===");
    }
}