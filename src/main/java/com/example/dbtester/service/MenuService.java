package com.example.dbtester.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private DatabaseService databaseService;

    private Scanner scanner = new Scanner(System.in);

    public void showMainMenu() {
        while (true) {
            System.out.println("\n========= 데이터베이스 연결 테스터 =========");
            System.out.println("1. 데이터베이스 연결 테스트");
            System.out.println("2. SQL 쿼리 실행 및 결과 확인");
            System.out.println("3. 데이터베이스 변경");
            System.out.println("4. 종료");
            System.out.print("선택: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        handleConnectionTest();
                        break;
                    case 2:
                        handleQueryExecution();
                        break;
                    case 3:
                        handleDatabaseChange();
                        break;
                    case 4:
                        System.out.println("프로그램을 종료합니다.");
                        logger.info("프로그램 종료");
                        return;
                    default:
                        System.out.println("잘못된 선택입니다. 1-4 사이의 숫자를 입력하세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }
    }

    private void handleConnectionTest() {
        System.out.println("\n=== 데이터베이스 연결 테스트 ===");

        Map<String, String> databases = databaseService.getAvailableDatabases();
        if (databases.isEmpty()) {
            System.out.println("설정된 데이터베이스가 없습니다.");
            return;
        }

        System.out.println("사용 가능한 데이터베이스:");
        int index = 1;
        String[] dbKeys = databases.keySet().toArray(new String[0]);

        for (String key : dbKeys) {
            System.out.printf("%d. %s (%s)\n", index++, databases.get(key), key);
        }
        System.out.print("테스트할 데이터베이스 번호를 선택하세요: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= dbKeys.length) {
                String selectedKey = dbKeys[choice - 1];
                boolean success = databaseService.testConnection(selectedKey);

                if (success) {
                    System.out.println("✓ 연결 테스트 성공!");
                } else {
                    System.out.println("✗ 연결 테스트 실패!");
                }
            } else {
                System.out.println("잘못된 선택입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력하세요.");
        }
    }

    private void handleQueryExecution() {
        System.out.println("\n=== SQL 쿼리 실행 및 결과 확인 ===");

        if (databaseService.getCurrentDatabaseKey() == null) {
            System.out.println("먼저 데이터베이스에 연결하세요. (메뉴 3번을 통해 데이터베이스 변경)");
            return;
        }

        System.out.println("현재 연결된 데이터베이스: " + databaseService.getCurrentDatabaseKey());
        System.out.println("\n샘플 쿼리:");
        System.out.println("- SELECT version() AS DBVersion;");
        System.out.println("- SELECT current_timestamp AS CurrentTime;");
        System.out.println("- SELECT 1 AS TestValue;");
        System.out.println();

        System.out.print("실행할 SQL 쿼리를 입력하세요 (종료: 'exit'): ");
        String sql = scanner.nextLine().trim();

        if ("exit".equalsIgnoreCase(sql)) {
            return;
        }

        if (sql.isEmpty()) {
            System.out.println("쿼리를 입력하세요.");
            return;
        }

        List<Map<String, Object>> results = databaseService.executeQuery(sql);

        if (results.isEmpty()) {
            System.out.println("결과가 없습니다.");
        } else {
            System.out.println("\n=== 쿼리 결과 ===");
            displayResults(results);
        }
    }

    private void handleDatabaseChange() {
        System.out.println("\n=== 데이터베이스 변경 ===");

        Map<String, String> databases = databaseService.getAvailableDatabases();
        if (databases.isEmpty()) {
            System.out.println("설정된 데이터베이스가 없습니다.");
            return;
        }

        System.out.println("사용 가능한 데이터베이스:");
        int index = 1;
        String[] dbKeys = databases.keySet().toArray(new String[0]);

        for (String key : dbKeys) {
            String status = key.equals(databaseService.getCurrentDatabaseKey()) ? " (현재)" : "";
            System.out.printf("%d. %s (%s)%s\n", index++, databases.get(key), key, status);
        }
        System.out.print("변경할 데이터베이스 번호를 선택하세요: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= dbKeys.length) {
                String selectedKey = dbKeys[choice - 1];
                boolean success = databaseService.switchDatabase(selectedKey);

                if (success) {
                    System.out.println("✓ 데이터베이스 변경 성공: " + databases.get(selectedKey));
                } else {
                    System.out.println("✗ 데이터베이스 변경 실패!");
                }
            } else {
                System.out.println("잘못된 선택입니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력하세요.");
        }
    }

    private void displayResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            return;
        }

        // 컬럼 이름 출력
        Map<String, Object> firstRow = results.get(0);
        System.out.print("| ");
        for (String column : firstRow.keySet()) {
            System.out.printf("%-20s | ", column);
        }
        System.out.println();

        // 구분선 출력
        System.out.print("|");
        for (int i = 0; i < firstRow.size(); i++) {
            System.out.print("----------------------|");
        }
        System.out.println();

        // 데이터 출력
        for (Map<String, Object> row : results) {
            System.out.print("| ");
            for (Object value : row.values()) {
                String displayValue = value != null ? value.toString() : "NULL";
                if (displayValue.length() > 20) {
                    displayValue = displayValue.substring(0, 17) + "...";
                }
                System.out.printf("%-20s | ", displayValue);
            }
            System.out.println();
        }

        System.out.println("\n총 " + results.size() + "개의 행이 조회되었습니다.");
    }
}