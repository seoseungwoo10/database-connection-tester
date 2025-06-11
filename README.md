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
bin/start.bat               # 윈도우 실행 스크립트
```

## 외부 라이브러리(JDBC 드라이버) 사용을 위한 spring-boot-maven-plugin 설정

이 프로젝트는 JDBC 드라이버 JAR 파일을 `libs` 폴더에 두고, 실행 시 동적으로 로딩합니다.  
이를 위해 `spring-boot-maven-plugin`의 `loader.path` 옵션을 사용합니다.

### 실행 시 외부 라이브러리 경로 지정

- `start.bat` 또는 직접 실행 시 아래와 같이 `-Dloader.path` 옵션을 지정합니다.
- 예시:
  ```bat
  java -Dloader.path="libs" -jar target/database-connection-tester-1.0.0.jar
  ```
- `start.bat`에서는 이미 해당 옵션이 포함되어 있습니다.

### pom.xml 예시

`pom.xml`의 `spring-boot-maven-plugin` 설정 예시:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <version>2.7.18</version>
      <configuration>
        <layout>ZIP</layout>
      </configuration>
    </plugin>
  </plugins>
</build>
```
- `layout: ZIP` 설정을 통해 fat jar가 외부 라이브러리 경로를 인식할 수 있습니다.
- JDBC 드라이버는 `libs` 폴더에 복사하고, jar 파일 내에 포함하지 않습니다.

## 실행 방법

1. `libs` 폴더에 사용할 DB 드라이버 JAR 파일을 복사합니다.
   - 예: `libs/postgresql-42.7.3.jar`, `libs/mysql-connector-java-8.0.21.jar`
2. `src/main/resources/application.yml`에서 DB 연결 정보를 설정합니다.
   - **중요:** 비밀번호는 환경변수(`MYSQL_DB_PASSWORD` 등)로 주입할 수 있습니다.
3. 프로젝트 빌드:
   ```bash
   mvn clean package -DskipTests=true
   ```
4. 실행(윈도우):
   ```bat
   set MYSQL_DB_PASSWORD=비밀번호
   bin\start.bat
   ```
   - 환경변수는 실행 전에 반드시 설정해야 하며, start.bat에서 자동으로 체크합니다.

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
      password: "${MYSQL_DB_PASSWORD}"   # 환경변수로 비밀번호 주입
      test-query: "SELECT VERSION() AS DBVersion"
    postgresql:
      name: "PostgreSQL Database"
      driver-class-name: "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
      original-class-name: org.postgresql.Driver
      jar-file: postgresql-42.7.3.jar
      jdbc-url: "jdbc:log4jdbc:postgresql://localhost:5432/mydatabase"
      username: "postgres"
      password: "${POSTGRES_DB_PASSWORD}" # 환경변수로 비밀번호 주입
      test-query: "SELECT version() AS DBVersion"
    # ... 추가 DB 설정 가능 ...
```
- `password` 항목에 `${MYSQL_DB_PASSWORD}` 또는 `${POSTGRES_DB_PASSWORD}`와 같이 환경변수명을 지정하면, 실행 시 해당 환경변수 값이 주입됩니다.
- 여러 DB를 동시에 설정할 수 있습니다.

## 환경변수로 비밀번호 전달하기

- **윈도우:**  
  ```bat
  set MYSQL_DB_PASSWORD=비밀번호
  bin\start.bat
  ```
- **리눅스/macOS:**  
  ```bash
  export MYSQL_DB_PASSWORD=비밀번호
  ./mvnw spring-boot:run
  ```

## 참고
- 드라이버 JAR을 직접 로딩하므로, pom.xml에 드라이버 dependency를 추가하지 않아도 됩니다.
- 커넥션 풀 및 드라이버 로딩 문제는 `DatabaseService.java`의 createDataSource 메서드에서 처리합니다.
- 로그 및 실행 결과는 `logs/` 폴더에 저장됩니다.

## 인스톨

```bash
mvn clean package -DskipTests=true
```