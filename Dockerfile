# 빌드 스테이지
FROM gradle:8.13-jdk17 AS build
WORKDIR /app

# 소스 코드 복사
COPY . .

# 애플리케이션 빌드
RUN ./gradlew build -x test

# 실행 스테이지
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 파일 업로드 디렉토리 생성
RUN mkdir -p /app/uploads/posts

# 포트 설정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]