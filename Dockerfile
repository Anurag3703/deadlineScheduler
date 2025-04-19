# Backend-only Dockerfile

# ============================================
# STAGE 1: Build Spring Boot (Maven)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY application-production.properties ./src/main/resources/
RUN mvn package -DskipTests

# ============================================
# STAGE 2: Production Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar

# Add Health Check
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Expose the port
EXPOSE 8080

# Run with environment variables
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-Xss512k", "-jar", "app.jar", "--spring.profiles.active=production"]