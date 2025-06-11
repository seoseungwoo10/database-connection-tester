@echo off
REM Spring Boot 애플리케이션 실행 스크립트
setlocal

REM --- 설정 변수 ---

REM JAR 파일명 (스크립트와 동일한 폴더에 위치)
set JAR_NAME=database-connection-tester-0.0.1.jar

REM 외부 라이브러리 폴더 (스크립트 위치 기준)
set LIBS_DIR=.\libs

REM Java 실행 옵션
REM 최소 힙 메모리 (-Xms), 최대 힙 메모리 (-Xmx)
set JAVA_OPTS=-Xms256m -Xmx1024m

REM 설정 파일 경로 (스크립트 위치 기준, bin\\config\\application.yml)
set CONFIG_FILE_PATH=./config/application.yml

REM 패스워드 설정
set MYSQL_DB_PASSWORD=비밀번호

REM --- 설정 변수 끝 ---

java -Dloader.path="%LIBS_DIR%" -Dspring.config.location="%CONFIG_FILE_PATH%" %JAVA_OPTS% -jar "%JAR_NAME%"
