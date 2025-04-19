# Backend-only Dockerfile

# ============================================
# STAGE 1: Build Spring Boot (Maven)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code including resources
COPY src ./src

# Create production properties file directly in resources
RUN mkdir -p ./src/main/resources
RUN echo "spring.application.name=deadlineScheduler\n\
\n\
# Mail configuration\n\
spring.mail.host=smtp.gmail.com\n\
spring.mail.port=587\n\
spring.mail.username=\${EMAIL_USERNAME}\n\
spring.mail.password=\${EMAIL_PASSWORD}\n\
spring.mail.properties.mail.smtp.auth=true\n\
spring.mail.properties.mail.smtp.starttls.enable=true\n\
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com\n\
\n\
# PostgreSQL Database Configuration\n\
spring.datasource.url=jdbc:postgresql://dpg-d01egtqdbo4c738mjvdg-a:5432/deadline_db\n\
spring.datasource.username=\${DB_USERNAME}\n\
spring.datasource.password=\${DB_PASSWORD}\n\
spring.datasource.driver-class-name=org.postgresql.Driver\n\
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n\
spring.jpa.hibernate.ddl-auto=update\n\
spring.jpa.show-sql=true\n\
spring.jpa.properties.hibernate.format_sql=true\n\
\n\
# Logging configuration\n\
logging.level.org.hibernate.SQL=WARN\n\
logging.level.org.hibernate.type.descriptor.sql=WARN\n\
logging.level.root=INFO\n\
\n\
# Server configuration\n\
server.port=\${PORT:8080}\n\
\n\
# Enable actuator endpoints for health checks\n\
management.endpoints.web.exposure.include=health,info\n\
management.endpoint.health.show-details=always" > ./src/main/resources/application-production.properties

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