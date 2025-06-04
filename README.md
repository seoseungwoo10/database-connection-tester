# Database Connection Tester

이 프로젝트는 다양한 데이터베이스(JDBC 지원) 연결을 테스트하고, 쿼리를 실행할 수 있는 Spring Boot 기반 콘솔 애플리케이션입니다.

## 주요 특징
- PostgreSQL, MySQL 등 다양한 DB 지원
- 외부 JAR 드라이버(libs 폴더) 동적 로딩 지원
- HikariCP 커넥션 풀 사용
- SQL 실행 및 결과 출력
- 로그 파일 기록

## 폴더 구조
```
libs/                       # JDBC 드라이버 JAR 파일 위치
src/main/java/              # Java 소스 코드
src/main/resources/         # 설정 파일 및 로그 설정
logs/                       # 실행 로그 파일
```

## 실행 방법
1. `libs` 폴더에 사용할 DB 드라이버 JAR 파일을 복사합니다.
   - 예: `libs/postgresql-42.7.3.jar`, `libs/mysql-connector-java-8.0.21.jar`
2. `application.yml` 및 `DatabaseConfig.java`에서 DB 연결 정보와 드라이버 경로를 설정합니다.
3. 프로젝트 빌드 및 실행:
   ```bash
   ./mvnw spring-boot:run
   ```

## application.yml의 connections 설정 방법
`src/main/resources/application.yml` 파일의 `database.connections` 항목에 각 데이터베이스별 연결 정보를 아래와 같이 추가합니다:

```yaml
database:
  driver-path: "./libs"           # 드라이버 JAR 파일이 위치한 폴더
  default-database: mysql         # 기본 선택 DB
  connections:
    mysql:
      name: "MySQL Database"
      driver-class-name: "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
      original-class-name: com.mysql.cj.jdbc.Driver
      jar-file: mysql-connector-java-8.0.21.jar
      jdbc-url: "jdbc:log4jdbc:mysql://localhost:3316/demodb?serverTimezone=UTC"
      username: "root"
      password: "비밀번호"
      test-query: "SELECT VERSION() AS DBVersion"
    postgresql:
      name: "PostgreSQL Database"
      driver-class-name: "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
      original-class-name: org.postgresql.Driver
      jar-file: postgresql-42.7.3.jar
      jdbc-url: "jdbc:log4jdbc:postgresql://localhost:5432/mydatabase"
      username: "postgres"
      password: "비밀번호"
      test-query: "SELECT version() AS DBVersion"
    # ... 추가 DB 설정 가능 ...
```
- `driver-class-name`: log4jdbc를 사용할 경우 "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"로 지정
- `original-class-name`: 실제 JDBC 드라이버 클래스명
- `jar-file`: 사용할 드라이버 JAR 파일명 (libs 폴더에 위치)
- `jdbc-url`, `username`, `password`, `test-query` 등 각 DB에 맞게 설정

## 참고
- 드라이버 JAR을 직접 로딩하므로, pom.xml에 드라이버 dependency를 추가하지 않아도 됩니다.
- 커넥션 풀 및 드라이버 로딩 문제는 `DatabaseService.java`의 createDataSource 메서드에서 처리합니다.

## 문의
- 개발: 서승우
- 문의: seoseungwoo10@gmail.com

