# ============================================
# STAGE 1: Build React Frontend (Node.js)
# ============================================
FROM node:18-alpine AS frontend-builder

WORKDIR /app
COPY deadline-scheduler-frontend/package.json deadline-scheduler-frontend/package-lock.json ./
RUN npm ci --silent

COPY deadline-scheduler-frontend .
RUN npm run build

# ============================================
# STAGE 2: Build Spring Boot (Maven)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# ============================================
# FINAL STAGE: Production Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
COPY --from=frontend-builder /app/build /static

# Optimized for production
ENV SPRING_PROFILES_ACTIVE=production
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-Xss512k", "-jar", "app.jar", "--spring.web.resources.static-locations=classpath:/static/"]