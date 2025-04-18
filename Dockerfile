# ============================================
# STAGE 1: Build React Frontend (Node.js)
# ============================================
FROM node:18-alpine AS frontend-builder

WORKDIR /app
# If you have a frontend, uncomment these lines:
# COPY package.json package-lock.json ./
# RUN npm ci
# COPY public ./public
# COPY src ./src
# RUN npm run build

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

# Create a directory for static content
RUN mkdir -p /static
COPY --from=frontend-builder /app/build /static

# Expose the port
EXPOSE 8080

# Run with environment variables
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-Xss512k", "-jar", "app.jar"]