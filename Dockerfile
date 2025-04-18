# Backend-only Dockerfile

# ============================================
# STAGE 1: Build Spring Boot (Maven)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# ============================================
# STAGE 2: Production Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run with environment variables
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-Xss512k", "-jar", "app.jar"]