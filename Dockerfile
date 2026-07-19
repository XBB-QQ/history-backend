FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# 安全修复 N2：用 -D 设置 JVM 系统属性，JwtUtil.static 块的 System.getProperty("spring.profiles.active") 才能读到
# 原 --spring.profiles.active=prod 是 Spring Boot 命令行参数，只设置 Environment，不会调用 System.setProperty()
# 导致 JwtUtil 读不到 profile，走 dev 兜底密钥
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
