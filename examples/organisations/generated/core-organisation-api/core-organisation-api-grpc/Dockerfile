# ---- Stage 1: Build the fat jar (optional if you use Gradle outside Docker)
# FROM gradle:8.7-jdk17-alpine AS build
# WORKDIR /home/app
# COPY . .
# RUN ./gradlew shadowJar --no-daemon

# ---- Stage 2: Run with minimal base image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy fat jar built by shadowJar
COPY build/libs/*-all.jar app.jar

EXPOSE 50051
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
