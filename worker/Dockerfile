# Multi-stage build: build jar then produce small runtime image
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# multi-arch runtime image (Temurin jammy) instead of alpine variant that may not exist for arm64
FROM eclipse-temurin:17-jre-jammy
ARG JAR=target/beam-worker-0.1.0-SNAPSHOT.jar
COPY --from=build /app/${JAR} /app/beam-worker.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","/app/beam-worker.jar"]